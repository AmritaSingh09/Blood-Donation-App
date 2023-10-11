package com.example.blooddonation.core.Interfaces.Auth;

import androidx.annotation.NonNull;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public interface Authentication {
    interface CheckAuthentication {
        void authProvider(boolean isAuthenticated);
    }

    interface LoginInterface {
        void onSuccess(String data);
        void onFailure(Exception e);
    }

    interface RegisterInterface {
        void onSuccess(String data);
        void onFailure(Exception e);
    }

    interface PhoneRegistration {
        void onSuccess(@NonNull PhoneAuthCredential credential);
        void onCodeSent(@NonNull String code, @NonNull PhoneAuthProvider.ForceResendingToken token);
        void onFailure(Exception e);
    }

}
