package com.example.blooddonation.ui.Activities.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blooddonation.MainActivity;
import com.example.blooddonation.R;
import com.example.blooddonation.core.Helpers.AuthHelper;
import com.example.blooddonation.core.Interfaces.Auth.Authentication;
import com.example.blooddonation.core.Models.LoginData;
import com.example.blooddonation.core.Utils.Constants.SharedPref;
import com.google.android.material.textfield.TextInputLayout;

public class Login extends AppCompatActivity {

    private TextView skip, forgot_pass, btn_login, signup;
    private TextInputLayout email, password;

    SharedPreferences sf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();

        skip.setOnClickListener(v -> saveSkipToSF());

        signup.setOnClickListener(vi ->{
            startActivity(new Intent(Login.this, Signup.class));
            finish();
        });

        btn_login.setOnClickListener(v ->{
            if (email.getEditText() == null || email.getEditText().getText().toString().isEmpty()){
                email.getEditText().setError("Required!");
                email.requestFocus();
            }else if (password.getEditText() == null || password.getEditText().getText().toString().isEmpty()) {
                password.getEditText().setError("Required!");
                password.requestFocus();
            }else {
                LoginData loginData = new LoginData(email.getEditText().getText().toString(), password.getEditText().getText().toString());

                AuthHelper
                        .getInstance(Login.this)
                        .setLoginCredentials(loginData, new Authentication.LoginInterface() {
                            @Override
                            public void onSuccess(String data) {
                                Toast.makeText(Login.this, "MSG: "+data, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, MainActivity.class));
                                finish();//todo check for past activity page flow and send back to previous page
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(Login.this, "EXCEPTION: "+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        forgot_pass.setOnClickListener(v ->{
            //TODO implement forgot password
        });

    }

    private void saveSkipToSF() {
        sf = getSharedPreferences(SharedPref.SKIP,MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putBoolean(SharedPref.SKIP_VALUE,true);
        editor.apply();
        startActivity(new Intent(Login.this, MainActivity.class));
        finish();
    }

    private synchronized void initViews() {
        skip = findViewById(R.id.l_skip);
        email = findViewById(R.id.l_email);
        password = findViewById(R.id.l_password);
        forgot_pass = findViewById(R.id.l_forgot_password);
        btn_login = findViewById(R.id.l_login_btn);
        signup = findViewById(R.id.l_signup);
    }
}