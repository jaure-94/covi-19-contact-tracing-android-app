package com.example.traceusdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    //Declare Views
    private EditText mLoginEmail;
    private EditText mLoginPassword;
    private Button mLoginButton;
    private Button mToRegBtn;
    private ProgressBar mLoginProgBar;

    //Declare FirebaseAuth object
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize views
        mLoginEmail = (EditText) findViewById(R.id.loginEmail);
        mLoginPassword = (EditText) findViewById(R.id.loginPassword);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mToRegBtn = (Button) findViewById(R.id.toRegBtn);
        mLoginProgBar = (ProgressBar) findViewById(R.id.loginProgBar);

        //Initialize Auth object
        mFirebaseAuth = FirebaseAuth.getInstance();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
                finish();
            }
        });

        mToRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        String email = mLoginEmail.getText().toString();
        String password = mLoginPassword.getText().toString();

        //Validate email, phone and password input fields
        if(TextUtils.isEmpty(email)) {
            mLoginEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mLoginPassword.setError("Password is required");
            return;
        }

        mLoginProgBar.setVisibility(View.VISIBLE);

        //authenticate the user
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    showToast("User created successfully.");

                    //proceed to app
                    startActivity(new Intent(LoginActivity.this, PermissionsActivity.class));
                } else {
                    showToast("Error! Could not sign in.");
                    mLoginProgBar.setVisibility(View.GONE);
                }
            }
        });

    }

    private void showToast(String message) {
        //Show a simple toast message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}