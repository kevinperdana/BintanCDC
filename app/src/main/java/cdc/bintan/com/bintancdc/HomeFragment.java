package cdc.bintan.com.bintancdc;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.kosalgeek.android.json.JsonConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback,LocationListener,GoogleMap.OnMarkerClickListener {

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;

    private GoogleMap mMap;
    public static final String URL = "http://cdc-backend.yacanet.com/v1/pasien/lokasiterakhir";
    private JSONArray result;

    final String LOGLOKASITERAKHIR = "CekLokasiTerakhir";
    final String LOGLATLNG = "CekLatLng";

    SharedPreferences.Editor editor;
    SharedPreferences pref;

    TextView tvBanyakPasien, tvTotalGPS;
    String ambilTokenSekarang;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_home2, container, false);

        pref = this.getActivity().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.google_map);

        if(supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }

        // Initialize Fused Location
        /*
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        // Check Permission
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurentLocation();
        } else {
            // When Permission Denied, Request Permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }*/

        tvBanyakPasien = (TextView) rootView.findViewById(R.id.tvBanyakPasien);
        tvTotalGPS = (TextView) rootView.findViewById(R.id.tvTotalGPS);

        hitungTotalPasien();

        return rootView;
    }

    private void hitungTotalPasien() {

        // AMBIL TOKEN SEKARANG
        ambilTokenSekarang = MainActivity.tokenSekarang;

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String urlDaftarODP = "http://cdc-backend.yacanet.com/v1/setting/userspasien";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlDaftarODP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                // JSON USERPASIEN
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray userPasien = jsonObject.getJSONArray("userspasien");

                    // HITUNG TOTAL PASIEN
                    int totalPasien = jsonObject.length();
                    Log.d("CekTotalPasien", totalPasien+"");
                    tvBanyakPasien.setText(String.valueOf(totalPasien));
                    tvTotalGPS.setText(String.valueOf("Total GPS yang Aktif : "));

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
                String auth = "Bearer "+ambilTokenSekarang;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mMap.setMyLocationEnabled(true);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(LOGLOKASITERAKHIR, response);
                JSONObject j = null;
                try{
                    j =new JSONObject(response);
                    result = j.getJSONArray("lokasiterakhir");
                    Log.d(LOGLATLNG, result+"");
                    for(int i=0; i<result.length(); i++){
                        JSONObject jsonObject1=result.getJSONObject(i);
                        String namaPasien = jsonObject1.getString("name");
                        String lat_i = jsonObject1.getString("lat");
                        String long_i = jsonObject1.getString("lng");

                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(lat_i) , Double.parseDouble(long_i)))
                                .title(namaPasien)
                                //.title(Double.valueOf(lat_i).toString() + "," + Double.valueOf(long_i).toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                        );

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(1.028423,104.455267), 11));
                    }

                }catch (NullPointerException e){
                    e.printStackTrace();

                }

                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String,String>();
                params.put("mode", "all");
                params.put("id","all");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String auth = "Bearer "+MainActivity.tokenSekarang;
                headers.put("Authorization", auth);
                return headers;
            }
        };


        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    // DEFAULT GET CURRENT LOCATION
    /*
    private void getCurentLocation() {
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                // When Success
                if (location != null){
                    // Sync Map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            // Initialize Lat Long
                            LatLng latLng = new LatLng(location.getLatitude()
                                    ,location.getLongitude());
                            // Create Marker
                            MarkerOptions options = new MarkerOptions().position(latLng)
                                    .title("I am Here");
                            // Zoom Map
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 3));
                            // Add Marker on Map
                            googleMap.addMarker(options);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // When Permission Granted
                getCurentLocation();
            }
        }
    }*/

}
