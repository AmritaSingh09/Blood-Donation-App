package com.example.blooddonation.core.Helpers;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.blooddonation.core.Interfaces.Auth.Authentication.*;
import com.example.blooddonation.core.Interfaces.Database.RealtimeDatabase;
import com.example.blooddonation.core.Models.LoginData;
import com.example.blooddonation.core.Models.UserDetail;
import com.example.blooddonation.core.Utils.Constants.Database;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AuthHelper {
    private LoginInterface loginHelper;
    private Activity context;
    private RegisterInterface registerHelper;
    private PhoneRegistration phoneRegistration;
    private String verificationCode, number;

    private static AuthHelper authHelper;
    private LoginData loginData;
    private FirebaseAuth auth;

    public AuthHelper() {
    }

    public static AuthHelper getInstance(Activity context){
        if (authHelper == null) authHelper = new AuthHelper();
        authHelper.context = context;
        authHelper.auth = FirebaseAuth.getInstance();
        return authHelper;
    }

    public void setLoginCredentials(@NonNull LoginData data,@NonNull LoginInterface loginHelper){
        this.loginHelper = loginHelper;
        signIn(data);
    }

    public AuthHelper authenticateNumber(@NonNull String number, PhoneRegistration phoneRegistration) {
        this.phoneRegistration = phoneRegistration;
        verifyPhoneNumber(number);
        return authHelper;
    }

    public AuthHelper authenticateOtp(@NonNull String otp, LoginInterface loginHelper) {
        authHelper.loginHelper = loginHelper;
        PhoneAuthCredential authCredential = PhoneAuthProvider.getCredential(verificationCode,otp);
        signInUsingCredential(authCredential);
        return authHelper;
    }

    private void signInUsingCredential(PhoneAuthCredential authCredential) {
        auth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loginHelper.onSuccess("verified");
                Toast.makeText(context, "User created", Toast.LENGTH_SHORT).show();
            }else {
                loginHelper.onFailure(task.getException());
            }
        });
    }

    private void verifyPhoneNumber(String number) {
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions
                .newBuilder(auth)
                .setPhoneNumber(number)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setActivity(context)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // authHelper.phoneAuthCredential = phoneAuthCredential;
                        String code = phoneAuthCredential.getSmsCode();
                        phoneRegistration.onSuccess(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        phoneRegistration.onFailure(e);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        phoneRegistration.onCodeSent(s,forceResendingToken);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    public AuthHelper setCallback(RegisterInterface registerHelper){
        authHelper.registerHelper = registerHelper;
        return authHelper;
    }

    public AuthHelper setLoginDetails(LoginData loginData) {
        //createUser(loginData);
        authHelper.loginData = loginData;
        return authHelper;
    }
    public AuthHelper setUserData(@NonNull UserDetail userDetail){
        if (auth.getCurrentUser().getUid() == null){
            registerHelper.onFailure(new Exception("Kindly verify your phone number first !\nThen only you can register."));
        }else {
            createUser(userDetail);
        }
        return authHelper;
    }

    private void createUser(UserDetail userDetail) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthCredential authCredential = EmailAuthProvider.getCredential(loginData.getEmail(),loginData.getPassword());
        try {
            auth
                    .getCurrentUser()
                    .linkWithCredential(authCredential)
                    .addOnCompleteListener(task -> {

                        // UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName("name").setPhotoUri("url").build();
                        HashMap<String, Object> data = new HashMap<>();
                        data.put(Database.USER + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + Database.PERSONAL_DATA, userDetail);

                        if (task.isSuccessful()) {
                                DatabaseHelper
                                    .getInstance()
                                    .setData(data)
                                    .onCompleteListener(new RealtimeDatabase() {
                                        @Override
                                        public void onSuccess(String data) {
                                            registerHelper.onSuccess(data);
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            registerHelper.onFailure(e);
                                        }
                                    });
                        } else {
                            registerHelper.onFailure(task.getException());
                        }

                    });
        }catch (Exception e) {
            registerHelper.onFailure(e);
        }
    }

    private void signIn(LoginData data) {
        FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(data.getEmail(), data.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        loginHelper.onSuccess(task.getResult().toString());
                    }else {
                        loginHelper.onFailure(task.getException());
                    }
                });
    }

}
