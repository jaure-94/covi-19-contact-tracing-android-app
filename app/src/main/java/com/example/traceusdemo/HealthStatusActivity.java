package com.example.traceusdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HealthStatusActivity extends AppCompatActivity {

    //Declare Database object
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    //Declare Auth object
    private FirebaseAuth mFirebaseAuth;

    //Declare views
    private Button mToDashboard;
    private Button mToUpdateStatusBtn;
    private TextView mCovidStatusTv;

    //Declare variables
    private String currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_status);

        //Initialize an instance of the database
        mFirebaseDatabase = mFirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("users");

        //Initialize Auth object
        mFirebaseAuth = FirebaseAuth.getInstance();

       //Initialize views
        mToDashboard = (Button) findViewById(R.id.toDashboard);
        mToUpdateStatusBtn = (Button) findViewById(R.id.toUpdateStatusBtn);
        mCovidStatusTv = (TextView) findViewById(R.id.covidStatusTv);

        displayUserStatus();

        //save button onClickListener
        mToUpdateStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateUserActivity.class);
                startActivity(intent);
            }
        });

        //to dashboard onClickListener
        mToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HealthStatusActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });

    }

    //Display user's current Health Status in text view
    private void displayUserStatus() {
        //Get status from database
        Query query = mDatabaseReference.orderByKey().equalTo(mFirebaseAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                    User user = singleSnapshot.getValue(User.class);

                    mCovidStatusTv.setText(user.getStatus());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //display status in text view
        mCovidStatusTv.setText(currentStatus);
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
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}