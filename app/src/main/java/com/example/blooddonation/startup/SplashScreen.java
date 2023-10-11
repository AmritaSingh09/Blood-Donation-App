package com.example.blooddonation.startup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.blooddonation.MainActivity;
import com.example.blooddonation.R;
import com.example.blooddonation.core.Utils.Constants.SharedPref;
import com.example.blooddonation.ui.Activities.Auth.Login;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences sf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sf = getSharedPreferences(SharedPref.SKIP, Context.MODE_PRIVATE);

        new Handler().postDelayed(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            } else {
                if (sf != null && sf.getBoolean(SharedPref.SKIP_VALUE,false)){
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                }else startActivity(new Intent(SplashScreen.this, Login.class));
            }
            finish();
        },2000);
    }
}