package cdc.bintan.com.bintancdc;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class ProfilODPActivity extends AppCompatActivity {
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    TextView tvNamaODP, tvNoHP, tvMasaKarantina, status, statusgps, tvStatus;

    String idUser, namaODP, noHP, namaStatus, tokenUser, tokenBaru;

    Button btnLogout;

    final String LOGAMBILKOORDINAT = "CekKoordinat";
    final String LOGTOKENUSER = "CekTU";
    final String LOGTAMBAHLOKASI = "CekTL";
    final String LOGTOKENBARU = "CekTokenBaru";
    final String LOGTOKENBARUFINAL = "CekTokenBaruFinal";

    // LOKASI
    static ProfilODPActivity instance;
    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    public static ProfilODPActivity getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_odp);

        instance = this;

        tvNamaODP = (TextView) findViewById(R.id.tvNamaODP);
        tvNoHP = (TextView) findViewById(R.id.tvNoHP);
        tvMasaKarantina = (TextView) findViewById(R.id.tvMasaKarantina);
        status = (TextView) findViewById(R.id.status);
        statusgps = (TextView) findViewById(R.id.statusgps);
        tvStatus = (TextView) findViewById(R.id.tvStatus);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/PS.ttf");
        tvNamaODP.setTypeface(custom_font);
        tvNoHP.setTypeface(custom_font);
        tvMasaKarantina.setTypeface(custom_font);
        status.setTypeface(custom_font);
        statusgps.setTypeface(custom_font);

        pref = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        idUser = pref.getString(Config.ID_ODP, "Not Available");
        namaODP = pref.getString(Config.NAMA, "Not Available");
        noHP = pref.getString(Config.NOMOR_HP, "Not Available");
        namaStatus = pref.getString(Config.NAMA_STATUS, "Not Available");
        tokenUser = pref.getString(Config.TOKEN_USER, "Not Available");
        Log.d(LOGTOKENUSER, tokenUser);

        tvNamaODP.setText(namaODP);
        tvNoHP.setText(noHP);
        tvStatus.setText(namaStatus);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = pref.edit();
                editor.clear();
                editor.commit();
                Intent in = new Intent(ProfilODPActivity.this, NotificationService.class);
                finish();
                startActivity(in);
            }
        });

        // LOGOUT


        // REFRESH TOKEN USER
        final Handler handler = new Handler();

        final Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                //updateToken();
                RequestQueue requestQueue = Volley.newRequestQueue(ProfilODPActivity.this);
                String urlRefreshToken = "http://cdc-backend.yacanet.com/v1/auth/refresh";
                StringRequest stringRequest = new StringRequest(Request.Method.GET, urlRefreshToken, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // RESPONSE UPDATE TOKEN
                        Log.d(LOGTOKENBARU, response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            tokenBaru = jsonResponse.getString("access_token");
                            Log.d(LOGTOKENBARUFINAL, tokenBaru);
                            tokenUser = tokenBaru;
                            editor = pref.edit();
                            editor.putString(Config.TOKEN_USER, tokenUser);
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        String auth = "Bearer "+tokenUser;
                        headers.put("Authorization", auth);
                        return headers;
                    }
                };
                requestQueue.add(stringRequest);
                handler.postDelayed(this,10000);
            }
        };

        handler.postDelayed(updateTask,10000);


        // RUNTIME PERMISSION
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        updateLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(ProfilODPActivity.this, "Anda harus mengaktifkan izin ini", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

        // FOREGROUND NOTIFICATION
        String notifikasiKoordinat = "GPS Sedang Aktif";

        Intent serviceIntent = new Intent(this, NotificationService.class);
        serviceIntent.putExtra("inputExtra", notifikasiKoordinat);

        ContextCompat.startForegroundService(this, serviceIntent);
    }


    // FOREGROUND NOTIFICATION DENGAN BUTTON
    /*public void startService(View v) {
        String inputKoordinat = MyLocationService.location_string;

        Intent serviceIntent = new Intent(this, ExampleService.class);
        serviceIntent.putExtra("inputExtra", inputKoordinat);

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService(View v) {
        Intent serviceIntent = new Intent(this, ExampleService.class);
        stopService(serviceIntent);
    }*/

    // LOKASI
    private void updateLocation() {
        buildLocationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);

    }

    public void updateTextView(final String valueLat, final String valueLng){
        ProfilODPActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(LOGAMBILKOORDINAT, valueLat+" "+valueLng);

                    // TAMBAH LOKASI
                    RequestQueue requestQueue = Volley.newRequestQueue(ProfilODPActivity.this);
                    String urlTambahLokasi = "http://cdc-backend.yacanet.com/v1/setting/userspasien/tambahlokasi/"+idUser;
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, urlTambahLokasi, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // RESPONSE TAMBAH LOKASI
                            Log.d(LOGTAMBAHLOKASI, response);

                            try {

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }){
                        @Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<String,String>();
                            params.put("lat", valueLat);
                            params.put("lng",valueLng);
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            String auth = "Bearer "+tokenUser;
                            headers.put("Authorization", auth);
                            return headers;
                        }
                    };
                    requestQueue.add(stringRequest);
            }
        });
    }
    // END LOKASI

    /*public void tombolLogout(View v) {
        editor = pref.edit();
        editor.clear();
        editor.commit();
        Intent serviceIntent = new Intent(ProfilODPActivity.this, NotificationService.class);
        finish();
        tombolLogout(serviceIntent);
    }

    private void tombolLogout(Intent serviceIntent) {
        Intent in = new Intent(ProfilODPActivity.this, NotificationService.class);
        finish();
        startActivity(in);
    }*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
