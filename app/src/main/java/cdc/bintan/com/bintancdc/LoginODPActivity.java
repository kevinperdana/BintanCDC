package cdc.bintan.com.bintancdc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginODPActivity extends AppCompatActivity implements AsyncResponse {
    Button btnLogin;
    EditText etNIK;

    String username;
    final String password = "123";
    final String LOGTOKENUSER = "CekTokenUser";
    final String LOGTOKENUSERSEKARANG = "CekTokenUserSekarang";
    public static String tokenUserSekarang;
    final String LOGDATAUSER = "CekDataUser";
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_odp);

        etNIK = (EditText) findViewById(R.id.etNIK);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent in = new Intent(LoginODPActivity.this, ProfilODPActivity.class);
                //startActivity(in);
                username = etNIK.getText().toString();

                ambilTokenUser();
            }
        });
    }

    private void ambilTokenUser() {
        // AMBIL TOKEN USER
        final HashMap postData = new HashMap();
        postData.put("username", username);
        postData.put("password", password);

        PostResponseAsyncTask ambilToken = new PostResponseAsyncTask(LoginODPActivity.this, postData, this);
        ambilToken.execute("http://cdc-backend.yacanet.com/v1/auth/login");
    }

    @Override
    public void processFinish(String s) {
        Log.d(LOGTOKENUSER, s);

        // AMBIL TOKEN USER SEKARANG
        try {
            JSONObject jsonObject = new JSONObject(s);
            tokenUserSekarang = jsonObject.getString("access_token");
            Log.d(LOGTOKENUSERSEKARANG, tokenUserSekarang);

            // AMBIL DATA USER
            RequestQueue requestQueue = Volley.newRequestQueue(LoginODPActivity.this);
            String urlDataUser = "http://cdc-backend.yacanet.com/v1/auth/me";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, urlDataUser, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // SIMPAN DATA USER KE SHARED PREFERENCES
                    Log.d(LOGDATAUSER, response);

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String id = jsonResponse.getString("id");
                        String nik = jsonResponse.getString("username");
                        String nama = jsonResponse.getString("name");
                        String tempatLahir = jsonResponse.getString("tempat_lahir");
                        String tanggalLahir = jsonResponse.getString("tanggal_lahir");
                        String nomorHp = jsonResponse.getString("nomor_hp");
                        String alamat = jsonResponse.getString("alamat");
                        String idKecamatan = jsonResponse.getString("PmKecamatanID");
                        String namaKecamatan = jsonResponse.getString("Nm_Kecamatan");
                        String idKelurahan = jsonResponse.getString("PmDesaID");
                        String namaKelurahan = jsonResponse.getString("Nm_Desa");
                        String foto = jsonResponse.getString("foto");
                        String statusPasien = jsonResponse.getString("status_pasien");
                        String namaStatus = jsonResponse.getString("nama_status");

                        // INISIASI SHARED PREF
                        SharedPreferences sharedPreferences = LoginODPActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                        // EDIT ISI SHARED PREF
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        // TAMBAH NILAI SHARED PREF
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                        editor.putString(Config.ID_ODP, id);
                        editor.putString(Config.NIK, nik);
                        editor.putString(Config.NAMA, nama);
                        editor.putString(Config.TEMPAT_LAHIR, tempatLahir);
                        editor.putString(Config.TANGGAL_LAHIR, tanggalLahir);
                        editor.putString(Config.NOMOR_HP, nomorHp);
                        editor.putString(Config.ALAMAT, alamat);
                        editor.putString(Config.ID_KECAMATAN, idKecamatan);
                        editor.putString(Config.NAMA_KECAMATAN, namaKecamatan);
                        editor.putString(Config.ID_KELURAHAN, idKelurahan);
                        editor.putString(Config.NAMA_KELURAHAN, namaKelurahan);
                        editor.putString(Config.FOTO, foto);
                        editor.putString(Config.STATUS_PASIEN, statusPasien);
                        editor.putString(Config.NAMA_STATUS, namaStatus);
                        editor.putString(Config.TOKEN_USER, tokenUserSekarang);

                        // SIMPAN NILAI
                        editor.commit();

                        // BUKA PROFIL ODP
                        Intent intent = new Intent(LoginODPActivity.this, ProfilODPActivity.class);
                        finish();
                        startActivity(intent);

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
                    String auth = "Bearer "+tokenUserSekarang;
                    headers.put("Authorization", auth);
                    return headers;
                }
            };
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME,Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

        //If we will get true
        if(loggedIn){
            //We will start the Profile Activity
            Intent intent = new Intent(LoginODPActivity.this, ProfilODPActivity.class);
            startActivity(intent);
        }
    }
}
