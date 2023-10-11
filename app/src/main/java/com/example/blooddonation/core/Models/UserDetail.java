package com.example.blooddonation.core.Models;

public class UserDetail {
    private String name, email, phone_no, uid, aadhaar, blood_group, dob, gender, location;

    public UserDetail() {
    }

    public UserDetail(String name, String email,
                      String phone_no, String uid,
                      String aadhaar, String blood_group,
                      String dob, String gender, String location) {
        this.name = name;
        this.email = email;
        this.phone_no = phone_no;
        this.uid = uid;
        this.aadhaar = aadhaar;
        this.blood_group = blood_group;
        this.dob = dob;
        this.gender = gender;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAadhaar() {
        return aadhaar;
    }

    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
