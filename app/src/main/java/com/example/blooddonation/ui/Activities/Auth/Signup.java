package com.example.blooddonation.ui.Activities.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blooddonation.MainActivity;
import com.example.blooddonation.R;
import com.example.blooddonation.core.Helpers.AuthHelper;
import com.example.blooddonation.core.Interfaces.Auth.Authentication;
import com.example.blooddonation.core.Models.LoginData;
import com.example.blooddonation.core.Models.UserDetail;
import com.example.blooddonation.core.Utils.Constants.SharedPref;

public class Signup extends AppCompatActivity {

    SharedPreferences sf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initViews();

        /*
            A positive (A+).
            A negative (A-).
            B positive (B+).
            B negative (B-).
            AB positive (AB+).
            AB negative (AB-).
            O positive (O+).
            O negative (O-).
         */

        LoginData loginData = new LoginData();
        UserDetail userDetail = new UserDetail();
        AuthHelper authHelper = AuthHelper.getInstance(Signup.this);
        authHelper
                .setLoginDetails(loginData)
                .setUserData(userDetail)
                .setCallback(new Authentication.RegisterInterface() {
                    @Override
                    public void onSuccess(String data) {

                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });

    }

    private synchronized void initViews() {

    }

    private void saveSkipToSF() {
        sf = getSharedPreferences(SharedPref.SKIP,MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putBoolean(SharedPref.SKIP_VALUE,true);
        editor.apply();
        startActivity(new Intent(Signup.this, MainActivity.class));
        finish();
    }

}