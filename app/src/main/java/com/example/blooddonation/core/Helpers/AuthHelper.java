package com.example.blooddonation.core.Helpers;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.blooddonation.core.Interfaces.Auth.Authentication.*;
import com.example.blooddonation.core.Models.LoginData;
import com.example.blooddonation.core.Models.UserDetail;
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

import java.util.concurrent.TimeUnit;

public class AuthHelper {
    private LoginInterface loginHelper;
    private Activity context;
    private RegisterInterface registerHelper;
    private PhoneRegistration phoneRegistration;
    private String phone_number;
    private PhoneAuthCredential phoneAuthCredential = null;

    private static AuthHelper authHelper;
    private UserDetail userDetail;

    public AuthHelper() {
    }

    public static AuthHelper getInstance(Activity context){
        if (authHelper == null) authHelper = new AuthHelper();
        authHelper.context = context;
        return authHelper;
    }

    public void setLoginCredentials(@NonNull LoginData data,@NonNull LoginInterface loginHelper){
        this.loginHelper = loginHelper;
        signIn(data);
    }

    //TODO add check for phone number and email for same account

    public AuthHelper authenticateNumber(@NonNull String number, PhoneRegistration phoneRegistration) {
        this.phoneRegistration = phoneRegistration;
        verifyPhoneNumber(number);
        return authHelper;
    }

    private void verifyPhoneNumber(String number) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions
                .newBuilder(auth)
                .setPhoneNumber(number)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setActivity(context)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        authHelper.phoneAuthCredential = phoneAuthCredential;
                        phoneRegistration.onSuccess(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        phoneRegistration.onFailure(e);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        //super.onCodeSent(s, forceResendingToken);
                        phoneRegistration.onCodeSent(s,forceResendingToken);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    public void setCallback(RegisterInterface registerHelper){
        authHelper.registerHelper = registerHelper;
    }

    public AuthHelper setLoginDetails(LoginData loginData) {
        createUser(loginData);
        return authHelper;
    }
    public AuthHelper setUserData(@NonNull UserDetail userDetail){
        if (phoneAuthCredential == null){
            registerHelper.onFailure(new Exception("Kindly verify your phone number first !\nThen only you can register."));
        }else {
            authHelper.userDetail = userDetail;
            //createUser(userDetail);
            //registerHelper.onSuccess();
        }
        return authHelper;
    }

    private void createUser(LoginData detail) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        AuthCredential authCredential = EmailAuthProvider.getCredential(detail.getEmail(),detail.getPassword());//TODO check for
        auth
                .getCurrentUser()
                .linkWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       // UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName("name").setPhotoUri("url").build();

                    }
                });
        /*auth
                .createUserWithEmailAndPassword(detail.getEmail(), detail.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                    }else {
                        registerHelper.onFailure(task.getException());
                    }
                });*/
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
