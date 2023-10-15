package com.example.blooddonation.ui.Activities.Auth;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;

import com.example.blooddonation.MainActivity;
import com.example.blooddonation.R;
import com.example.blooddonation.core.Helpers.AuthHelper;
import com.example.blooddonation.core.Interfaces.Auth.Authentication;
import com.example.blooddonation.core.Models.LoginData;
import com.example.blooddonation.core.Models.UserDetail;
import com.example.blooddonation.core.Utils.Constants.SharedPref;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Signup extends AppCompatActivity {

    private CircleImageView image_view;
    private TextView select_img, verify_no, login, signup, skip, o_verify;
    private MaterialAutoCompleteTextView t_gender, t_blood;
    private TextInputLayout name, aadhaar, email, password, phone_no, location, dob, o_otp;
    private SwitchCompat donor;
    private CardView otp;
    private AuthHelper authHelper;
    private SharedPreferences sf;
    private DatePickerDialog datePickerDialog;
    private String img_url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initViews();
        skip.setOnClickListener(v -> saveSkipToSF());

        dob.getEditText().setOnClickListener(view -> {
            datePickerDialog.show();
        });

        t_gender.setAdapter(new ArrayAdapter<>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,getGenderList()));

        t_blood.setAdapter(new ArrayAdapter<>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,getBloodGroupList()));
        t_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(Signup.this, "Selected Gender is: "+getGenderList().get(i), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(Signup.this, "Selected Gender is: ", Toast.LENGTH_SHORT).show();

            }
        });

        verify_no.setOnClickListener(v->{
            if (phone_no.getEditText() == null || phone_no.getEditText().getText().toString().isEmpty()){
                phone_no.getEditText().setError("Required!");
                phone_no.requestFocus();
            }else {
                otp.setVisibility(View.VISIBLE);
                verifyPhoneAuth("+91"+phone_no.getEditText().getText().toString());
            }
        });

        o_verify.setOnClickListener(v->{
            if (o_otp.getEditText() == null || o_otp.getEditText().getText().toString().isEmpty()){
                o_otp.getEditText().setError("Required!");
                o_otp.requestFocus();
            }else {
                verifyOtp(o_otp.getEditText().getText().toString());
            }
        });

        signup.setOnClickListener(v ->{
            if (name.getEditText() == null || name.getEditText().getText().toString().isEmpty()){
                name.getEditText().setError("Required!");
                name.requestFocus();
            }else if (phone_no.getEditText() == null || phone_no.getEditText().getText().toString().isEmpty()){
                phone_no.getEditText().setError("Required!");
                phone_no.requestFocus();
            }else if (phone_no.isEnabled()){
                phone_no.getEditText().setError("Verify First!");
                phone_no.requestFocus();
            }else if (email.getEditText() == null || email.getEditText().getText().toString().isEmpty()){
                email.getEditText().setError("Required!");
                email.requestFocus();
            }else if (password.getEditText() == null || password.getEditText().getText().toString().isEmpty()) {
                password.getEditText().setError("Required!");
                password.requestFocus();
            }else if (location.getEditText() == null || location.getEditText().getText().toString().isEmpty()) {
                location.getEditText().setError("Required!");
                location.requestFocus();
            }else{
                LoginData loginData = new LoginData(email.getEditText().getText().toString(), password.getEditText().getText().toString());
                UserDetail userDetail = new UserDetail(name.getEditText().getText().toString(),
                        email.getEditText().getText().toString(),
                        phone_no.getEditText().getText().toString(),
                        "" + FirebaseAuth.getInstance().getCurrentUser().getUid(),
                        aadhaar.getEditText().getText().toString(),
                        t_blood.getText().toString(),
                        dob.getEditText().getText().toString(),
                        t_gender.getText().toString(),
                        location.getEditText().getText().toString(),
                        img_url,//todo image for storage
                        donor.isChecked());
                    backend(loginData, userDetail);

            }
        });

        login.setOnClickListener(v ->{
            startActivity(new Intent(Signup.this, Login.class));
            finish();
        });

        select_img.setOnClickListener(v->{
            Intent picker = new Intent();
            picker.setType("image/*");
            picker.setAction(Intent.ACTION_GET_CONTENT);

            //startActivityForResult(picker,1001);
            activityResultLauncher.launch(picker);
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    activityResult -> {
                if (activityResult.getResultCode() == Activity.RESULT_OK){
                    Intent data = activityResult.getData();
                    if (data != null && data.getData() != null){
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap;
                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            image_view.setImageBitmap(selectedImageBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //imageView.setImageBitmap(selectedImageBitmap);
                    }
                }
            });
    private void verifyOtp(String otp) {
        authHelper.authenticateOtp(otp, new Authentication.LoginInterface() {
            @Override
            public void onSuccess(String data) {
                if (data.equals("verified")){
                    phone_no.setEnabled(false);
                    Signup.this.otp.setVisibility(View.GONE);
                    //phone_no.getEditText().setEnabled(false);
                    Toast.makeText(Signup.this, "Phone number verified successfully!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("LOGIN OTP ME ERROR",e.getMessage());
                Toast.makeText(Signup.this, "EXP: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyPhoneAuth(String number) {
        authHelper
                .authenticateNumber(number, new Authentication.PhoneRegistration() {
                    @Override
                    public void onSuccess(@NonNull PhoneAuthCredential credential) {
                        Toast.makeText(Signup.this, "Success "+credential.getSmsCode(), Toast.LENGTH_SHORT).show();
                        otp.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCodeSent(@NonNull String code, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        Toast.makeText(Signup.this, "Sent Code is : "+ code, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        otp.setVisibility(View.GONE);
                        Log.e("LOGIN ME ERROR",e.getMessage());
                        Toast.makeText(Signup.this, "Exception : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void selectDateOfBirth() {
        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(Signup.this,
                (datePicker, year1, month1, day) -> {
            String dd = "",mm="";
            dd = (day < 10) ? "0"+day : ""+day;
            mm = (month1 +1 < 10) ? "0"+(month1 +1) : ""+(month1 +1);
            dob.getEditText().setText(dd+"-"+mm+"-"+ year1);
        },year,month,date);


    }

    private ArrayList<String> getBloodGroupList() {
        ArrayList<String> blood = new ArrayList<>();
        blood.add(0,"A+");
        blood.add(1,"B+");
        blood.add(2,"AB+");
        blood.add(3,"O+");
        blood.add(4,"A-");
        blood.add(5,"B-");
        blood.add(6,"AB-");
        blood.add(7,"O-");
        return blood;
    }

    private ArrayList<String> getGenderList() {
        ArrayList<String> ge = new ArrayList<>();
        ge.add(0,"Male");
        ge.add(1,"Female");
        ge.add(2,"Other");
        return ge;
    }

    private void backend(LoginData loginData, UserDetail userDetail) {
        authHelper
                .setLoginDetails(loginData)
                .setCallback(new Authentication.RegisterInterface() {
                    @Override
                    public void onSuccess(String data) {
                        startActivity(new Intent(Signup.this,MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(Signup.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).setUserData(userDetail);
    }

    private synchronized void initViews() {

        image_view = findViewById(R.id.s_profile_image);
        select_img = findViewById(R.id.s_btn_select_img);
        name = findViewById(R.id.s_name);
        aadhaar = findViewById(R.id.s_aadhaar);
        //blood_grp = findViewById(R.id.s_blood_group);
        email = findViewById(R.id.s_email);
        password = findViewById(R.id.s_password);
        phone_no = findViewById(R.id.s_phone_number);
        verify_no = findViewById(R.id.s_verify_number);
        location = findViewById(R.id.s_location);
        dob = findViewById(R.id.s_dob);
        //gender = findViewById(R.id.s_gender);
        login = findViewById(R.id.s_login);
        signup = findViewById(R.id.s_signup);
        skip = findViewById(R.id.s_skip);
        donor = findViewById(R.id.s_donor);
        t_gender = findViewById(R.id.s_t_gender);
        t_blood = findViewById(R.id.s_t_blood);
        otp = findViewById(R.id.s_otp);
        o_otp = findViewById(R.id.o_otp);
        o_verify = findViewById(R.id.o_verify);

        authHelper = AuthHelper.getInstance(Signup.this);
        selectDateOfBirth();
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