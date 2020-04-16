package cdc.bintan.com.bintancdc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginODPActivity extends AppCompatActivity {
    Button btnLogin;

    String username;
    final String password = "123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_odp);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(LoginODPActivity.this, ProfilODPActivity.class);
                startActivity(in);
            }
        });

        // AMBIL TOKEN USER
        
    }
}
