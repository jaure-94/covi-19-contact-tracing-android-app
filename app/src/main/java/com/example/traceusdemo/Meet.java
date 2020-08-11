package com.example.traceusdemo;


import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Objects;

public class Meet implements Serializable {

    //Declare Database object
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    //Declare FirebaseAuth object
    private FirebaseAuth mFirebaseAuth;

    //declare variables
    private String met_user_uid;
    private String meet_date_time;
    private String meet_latitude;
    private String meet_longitude;
    private String meet_status;

    public Meet() {}

    public Meet(String met_user_uid, String meet_date_time, String meet_latitude, String meet_longitude) {
        this.setMet_user_uid(met_user_uid);
        this.setMeet_date_time(meet_date_time);
        this.setMeet_latitude(meet_latitude);
        this.setMeet_longitude(meet_longitude);
    }

    public Meet(String met_user_uid, String meet_date_time, String meet_latitude, String meet_longitude, String meet_status) {
        this.setMet_user_uid(met_user_uid);
        this.setMeet_date_time(meet_date_time);
        this.setMeet_latitude(meet_latitude);
        this.setMeet_longitude(meet_longitude);
        this.setMeet_status(meet_status);
    }

    public String getMet_user_uid() {
        return met_user_uid;
    }

    public void setMet_user_uid(String met_user_uid) {
        this.met_user_uid = met_user_uid;
    }

    public String getMeet_date_time() {
        return meet_date_time;
    }

    public void setMeet_date_time(String meet_date_time) {
        this.meet_date_time = meet_date_time;
    }

    public String getMeet_latitude() {
        return meet_latitude;
    }

    public void setMeet_latitude(String meet_latitude) {
        this.meet_latitude = meet_latitude;
    }

    public String getMeet_longitude() {
        return meet_longitude;
    }

    public void setMeet_longitude(String meet_longitude) {
        this.meet_longitude = meet_longitude;
    }

    public String getMeet_status() {
        return meet_status;
    }

    public void setMeet_status(String meet_status) {
        this.meet_status = meet_status;
    }

}
