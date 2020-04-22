package cdc.bintan.com.bintancdc;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.LoginFilter;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.util.Base64.NO_WRAP;


/**
 * A simple {@link Fragment} subclass.
 */
public class TambahFragment extends Fragment {

    Spinner spKecamatan, spKelurahan;
    Button btnSimpanODP;
    EditText etNIK, etNamaODP, etNoHP, etAlamat, etTempatLahir;
    String nik, namaODP, noHP, alamat, idKecamatan, namaKecamatan, idKelurahan, namaKelurahan, tempatLahir;

    String ambilTokenSekarang;
    final String LOGATS = "CekATS";
    final String LOGSIMPAN = "CekSimpan";
    final String LOGSIMPANGAGAL = "CekSimpanGagal";

    Button btnTentukanTglLahir;
    String tanggalLahir;
    final String LOGTGL = "CekTgl";
    final String LOGFORMATTGL = "CekFormatTgl";
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public TambahFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tambah, container, false);

        // TOMBOL TANGGAL LAHIR
        btnTentukanTglLahir = (Button) rootView.findViewById(R.id.btnTentukanTglLahir);
        btnTentukanTglLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int tahun = cal.get(Calendar.YEAR);
                int bulan = cal.get(Calendar.MONTH);
                int hari= cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener,
                        tahun,bulan,hari);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int tahun, int bulan, int hari) {
                bulan = bulan + 1;
                Log.d(LOGTGL, "DataSet Tanggal : "+ tahun + "-" + bulan + "-" + hari);

                String formatHari = String.format("%02d", hari);
                String formatBulan = String.format("%02d", bulan);
                tanggalLahir = tahun + "-" + formatBulan + "-" + formatHari;
                Log.d(LOGFORMATTGL, tanggalLahir);

                btnTentukanTglLahir.setText(tanggalLahir);
            }
        };


        // SPINNER KECAMATAN
        spKecamatan = (Spinner) rootView.findViewById(R.id.spinnerKecamatan);

        List<StringWithTag> listKecamatan = new ArrayList<StringWithTag>();
        listKecamatan.add(new StringWithTag("Bintan Pesisir", "uid1"));
        listKecamatan.add(new StringWithTag("Bintan Timur", "uid2"));
        listKecamatan.add(new StringWithTag("Bintan Utara", "uid3"));
        listKecamatan.add(new StringWithTag("Gunung Kijang", "uid4"));
        listKecamatan.add(new StringWithTag("Mantang", "uid5"));
        listKecamatan.add(new StringWithTag("Seri Kuala Lobam", "uid6"));
        listKecamatan.add(new StringWithTag("Tambelan", "uid7"));
        listKecamatan.add(new StringWithTag("Telok Sebong", "uid8"));
        listKecamatan.add(new StringWithTag("Teluk Bintan", "uid9"));
        listKecamatan.add(new StringWithTag("Toapaya", "uid10"));

        ArrayAdapter<StringWithTag> kecamatanAdapter = new ArrayAdapter<StringWithTag>(getActivity(), android.R.layout.simple_spinner_item, listKecamatan);
        kecamatanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKecamatan.setAdapter(kecamatanAdapter);

        spKecamatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                StringWithTag stringKecamatan = (StringWithTag) parent.getItemAtPosition(pos);
                Object tagKecamatan = stringKecamatan.tag;
                // CEK ID KECAMATAN
                //Toast.makeText(getActivity(), String.valueOf(tagKecamatan), Toast.LENGTH_SHORT).show();

                // CEK STRING KECAMATAN
                //Toast.makeText(getActivity(), String.valueOf(stringKecamatan), Toast.LENGTH_SHORT).show();

                idKecamatan = String.valueOf(tagKecamatan);
                namaKecamatan = String.valueOf(stringKecamatan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // SPINNER KELURAHAN
        spKelurahan = (Spinner) rootView.findViewById(R.id.spinnerKelurahan);

        List<StringWithTag> listKelurahan = new ArrayList<StringWithTag>();
        listKelurahan.add(new StringWithTag("Air Gelubi", "uid1"));
        listKelurahan.add(new StringWithTag("Kelong", "uid2"));
        listKelurahan.add(new StringWithTag("Mapur", "uid3"));
        listKelurahan.add(new StringWithTag("Numbing", "uid4"));
        listKelurahan.add(new StringWithTag("Gunung Lengkuas", "uid5"));
        listKelurahan.add(new StringWithTag("Kijang Kota", "uid6"));
        listKelurahan.add(new StringWithTag("Sungai Enam", "uid7"));
        listKelurahan.add(new StringWithTag("Sungai Lekop", "uid8"));
        listKelurahan.add(new StringWithTag("Lancang Kuning", "uid9"));
        listKelurahan.add(new StringWithTag("Tanjung Uban Kota", "uid10"));
        listKelurahan.add(new StringWithTag("Tanjung Uban Selatan", "uid11"));
        listKelurahan.add(new StringWithTag("Tanjung Uban Timur", "uid12"));
        listKelurahan.add(new StringWithTag("Gunung Kijang", "uid13"));
        listKelurahan.add(new StringWithTag("Malang Rapat", "uid14"));
        listKelurahan.add(new StringWithTag("Teluk Bakau", "uid15"));
        listKelurahan.add(new StringWithTag("Kawal", "uid16"));
        listKelurahan.add(new StringWithTag("Dendun", "uid17"));
        listKelurahan.add(new StringWithTag("Mantang Baru", "uid18"));
        listKelurahan.add(new StringWithTag("Mantang Besar", "uid19"));
        listKelurahan.add(new StringWithTag("Mantang Lama", "uid20"));
        listKelurahan.add(new StringWithTag("Busung", "uid21"));
        listKelurahan.add(new StringWithTag("Kuala Sempang", "uid22"));
        listKelurahan.add(new StringWithTag("Teluk Sasah", "uid23"));
        listKelurahan.add(new StringWithTag("Tanjung Permai", "uid24"));
        listKelurahan.add(new StringWithTag("Teluk Lobam", "uid25"));
        listKelurahan.add(new StringWithTag("Kelong", "uid26"));
        listKelurahan.add(new StringWithTag("Batu Lepuk", "uid27"));
        listKelurahan.add(new StringWithTag("Kampung Hilir", "uid28"));
        listKelurahan.add(new StringWithTag("Kampung Melayu", "uid29"));
        listKelurahan.add(new StringWithTag("Kukup", "uid30"));
        listKelurahan.add(new StringWithTag("Pengikik", "uid31"));
        listKelurahan.add(new StringWithTag("Pulau Mentebung", "uid32"));
        listKelurahan.add(new StringWithTag("Pulau Pinang", "uid33"));
        listKelurahan.add(new StringWithTag("Teluk Sekuni", "uid34"));
        listKelurahan.add(new StringWithTag("Berakit", "uid35"));
        listKelurahan.add(new StringWithTag("Ekang Anculai", "uid36"));
        listKelurahan.add(new StringWithTag("Pengudang", "uid37"));
        listKelurahan.add(new StringWithTag("Sebong Lagoi", "uid38"));
        listKelurahan.add(new StringWithTag("Sebong Pereh", "uid39"));
        listKelurahan.add(new StringWithTag("Sri Bintan", "uid40"));
        listKelurahan.add(new StringWithTag("Kota Baru", "uid41"));
        listKelurahan.add(new StringWithTag("Bintan Buyu", "uid42"));
        listKelurahan.add(new StringWithTag("Pangkil", "uid43"));
        listKelurahan.add(new StringWithTag("Penaga", "uid44"));
        listKelurahan.add(new StringWithTag("Pengujan", "uid45"));
        listKelurahan.add(new StringWithTag("Tembeling", "uid46"));
        listKelurahan.add(new StringWithTag("Tembeling Tanjung", "uid47"));
        listKelurahan.add(new StringWithTag("Toapaya", "uid48"));
        listKelurahan.add(new StringWithTag("Toapaya Utara", "uid49"));
        listKelurahan.add(new StringWithTag("Toapaya Selatan", "uid50"));

        ArrayAdapter<StringWithTag> kelurahanAdapter = new ArrayAdapter<StringWithTag>(getActivity(), android.R.layout.simple_spinner_item, listKelurahan);
        kelurahanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKelurahan.setAdapter(kelurahanAdapter);

        spKelurahan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                StringWithTag stringKelurahan = (StringWithTag) parent.getItemAtPosition(pos);
                Object tagKelurahan = stringKelurahan.tag;
                // CEK ID KELURAHAN
                //Toast.makeText(getActivity(), String.valueOf(tagKelurahan), Toast.LENGTH_SHORT).show();

                idKelurahan = String.valueOf(tagKelurahan);
                namaKelurahan = String.valueOf(stringKelurahan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // AMBIL TOKEN SEKARANG
        ambilTokenSekarang = MainActivity.tokenSekarang;
        Log.d(LOGATS, ambilTokenSekarang);

        // SIMPAN ODP
        btnSimpanODP = (Button) rootView.findViewById(R.id.btnSimpan);
        etNIK = (EditText) rootView.findViewById(R.id.etNIK);
        etNamaODP = (EditText) rootView.findViewById(R.id.etNamaODP);
        etNoHP = (EditText) rootView.findViewById(R.id.etNoHP);
        etAlamat = (EditText) rootView.findViewById(R.id.etAlamat);
        etTempatLahir = (EditText) rootView.findViewById(R.id.etTempatLahir);

        btnSimpanODP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nik = etNIK.getText().toString();
                namaODP = etNamaODP.getText().toString();
                noHP = etNoHP.getText().toString();
                alamat = etAlamat.getText().toString();
                tempatLahir = etTempatLahir.getText().toString();

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                String urlSimpanODP = "http://cdc-backend.yacanet.com/v1/setting/userspasien/store";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlSimpanODP, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LOGSIMPAN, response);
                        // NOTIFIKASI SIMPAN BERHASIL
                        Toast.makeText(getActivity(), "Data Disimpan", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOGSIMPANGAGAL, error+"");
                    }
                }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<String,String>();
                        params.put("username",nik);
                        params.put("password","123");
                        params.put("name",namaODP);
                        params.put("nomor_hp",noHP);
                        params.put("alamat",alamat);
                        params.put("PmKecamatanID",idKecamatan);
                        params.put("Nm_Kecamatan",namaKecamatan);
                        params.put("PmDesaID",idKelurahan);
                        params.put("Nm_Desa",namaKelurahan);
                        params.put("foto","storage/images/users/no_photo.png");
                        params.put("tempat_lahir",tempatLahir);
                        params.put("tanggal_lahir",tanggalLahir);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        String auth = "Bearer "+ambilTokenSekarang;
                        //String auth = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vY2RjLWJhY2tlbmQueWFjYW5ldC5jb20vdjEvYXV0aC9sb2dpbiIsImlhdCI6MTU4NzMyMTIzOSwiZXhwIjoxNTg3MzI0ODM5LCJuYmYiOjE1ODczMjEyMzksImp0aSI6Ik5pdEJ3cU8yeDhQWDRaYnAiLCJzdWIiOjEsInBydiI6IjIzYmQ1Yzg5NDlmNjAwYWRiMzllNzAxYzQwMDg3MmRiN2E1OTc2ZjcifQ.1b0ARevi42u6iCAjbliJbJ2rvFYYH71B874caMMfscM";
                        headers.put("Authorization", auth);
                        return headers;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }
}
