package cdc.bintan.com.bintancdc;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class DaftarFragment extends Fragment {

    String ambilTokenSekarang;
    final String LOGATS = "CekATS";
    final String LOGDAFTARODP = "CekDaftarODP";
    final String LOGDAFTARODPGAGAL = "CekDaftarODPGagal";
    final String LOGUSERPASIEN = "CekUserPasien";

    // RECYCLER VIEW
    private ArrayList<DaftarFragmentSerialisasi> daftarFragmentSerialisasi;
    private ListView lvODP;


    public DaftarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_daftar, container, false);

        // AMBIL TOKEN SEKARANG
        ambilTokenSekarang = MainActivity.tokenSekarang;
        Log.d(LOGATS, ambilTokenSekarang);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String urlDaftarODP = "http://cdc-backend.yacanet.com/v1/setting/userspasien";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlDaftarODP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(LOGDAFTARODP, response);

                // JSON USERPASIEN
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray userPasien = jsonObject.getJSONArray("userspasien");
                    Log.d(LOGUSERPASIEN, userPasien+"");

                    // RECYCLER VIEW
                    daftarFragmentSerialisasi = new JsonConverter<DaftarFragmentSerialisasi>().toArrayList(String.valueOf(userPasien), DaftarFragmentSerialisasi.class);

                    BindDictionary<DaftarFragmentSerialisasi> dict = new BindDictionary<DaftarFragmentSerialisasi>();
                    // NAMA ODP
                    dict.addStringField(R.id.tvNamaODP, new StringExtractor<DaftarFragmentSerialisasi>() {
                        @Override
                        public String getStringValue(DaftarFragmentSerialisasi daftarFragmentSerialisasi, int position) {
                            return daftarFragmentSerialisasi.name;
                        }
                    });
                    // NO HP
                    dict.addStringField(R.id.tvNoHP, new StringExtractor<DaftarFragmentSerialisasi>() {
                        @Override
                        public String getStringValue(DaftarFragmentSerialisasi daftarFragmentSerialisasi, int position) {
                            return daftarFragmentSerialisasi.nomor_hp;
                        }
                    });
                    // ALAMAT
                    dict.addStringField(R.id.tvAlamat, new StringExtractor<DaftarFragmentSerialisasi>() {
                        @Override
                        public String getStringValue(DaftarFragmentSerialisasi daftarFragmentSerialisasi, int position) {
                            return daftarFragmentSerialisasi.alamat;
                        }
                    });

                    FunDapter<DaftarFragmentSerialisasi> adapter = new FunDapter<>(
                            getActivity(), daftarFragmentSerialisasi, R.layout.fragment_daftar_card, dict);

                    lvODP = (ListView) rootView.findViewById(R.id.lvODP);
                    lvODP.setAdapter(adapter);
                    //lvODP.setOnItemClickListener(this);

                    // END RECYCLER VIEW

                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    String status = jsonObject.getString("status");
                    String pid = jsonObject.getString("pid");
                    Log.d(LOGUSERPASIEN, jsonObject.names());

                    JSONArray jsonArrayUsersPasien = jsonObject.getJSONArray("userspasien");
                    for (int i=0; i<jsonArrayUsersPasien.length(); i++){
                        //JSONObject jsonObjectUsersPasien = jsonArrayUsersPasien.getJSONObject(i);

                        //String nik = jsonObjectUsersPasien.getString("username");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOGDAFTARODPGAGAL, error+"");
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

        return rootView;
    }

}
