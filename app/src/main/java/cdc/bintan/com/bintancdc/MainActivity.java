package cdc.bintan.com.bintancdc;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AsyncResponse {

    Button btnLogin, btnYa;
    final String LOGTOKEN = "CekToken";
    final String LOGTOKENSEKARANG = "CekTokenSekarang";
    final String username = "admin";
    final String password = "1234";
    public static String tokenSekarang;

    private boolean loggedIn = false;
    EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

        // LOGIN PETUGAS
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent in = new Intent(MainActivity.this, HomeActivity.class);
                //startActivity(in);
                String usernameSementara = etUsername.getText().toString();
                String passwordSementara = etPassword.getText().toString();
                if ((usernameSementara.equals("admin")) && (passwordSementara.equals("1234"))){
                    Intent in = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(in);
                }
            }
        });

        // LOGIN PASIEN
        btnYa = (Button) findViewById(R.id.btnYa);
        btnYa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this, LoginODPActivity.class);
                //finish();
                startActivity(in);
            }
        });

        final HashMap postData = new HashMap();
        postData.put("username", username);
        postData.put("password", password);

        PostResponseAsyncTask ambilToken = new PostResponseAsyncTask(MainActivity.this, postData, this);
        ambilToken.execute("http://cdc-backend.yacanet.com/v1/auth/login");
    }

    @Override
    public void processFinish(String s) {
        Log.d(LOGTOKEN, s);

        // AMBIL TOKEN SEKARANG
        try {
            JSONObject jsonObject = new JSONObject(s);
            tokenSekarang = jsonObject.getString("access_token");
            Log.d(LOGTOKENSEKARANG, tokenSekarang);

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
            Intent intent = new Intent(MainActivity.this, ProfilODPActivity.class);
            startActivity(intent);
        }
    }
}
