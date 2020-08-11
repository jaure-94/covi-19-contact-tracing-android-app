package com.example.traceusdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    //Declare Auth object
    private FirebaseAuth mFirebaseAuth;

    private Button mHealthStatus;
    private Button mHowItWorks;
    private Button mViewDevices;
    private Button mStartTracing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Initialize Auth object
        mFirebaseAuth = FirebaseAuth.getInstance();

        //assign views
        mHealthStatus = findViewById(R.id.healthStatus);
        mHowItWorks = findViewById(R.id.howItWorks);
        mViewDevices = findViewById(R.id.viewDevices);
        mStartTracing = findViewById(R.id.startTracing);

        //Health status button onClickListener
        mHealthStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, HealthStatusActivity.class);
                startActivity(intent);
            }
        });

        //How it works button onClickListener
        mHowItWorks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, HowItWorksActivity.class);
                startActivity(intent);
            }
        });

        //View Devices button onClickListener
        mViewDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });

        //start tracing button click listener
        mStartTracing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, NearbyTracingActivity.class);
                startActivity(intent);
            }
        });

    }

    //Toolbar menu configuration
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.dashboard_menu: {
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.update_user_menu: {
                Intent intent = new Intent(getApplicationContext(), UpdateUserActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.logout_menu: {
                mFirebaseAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}