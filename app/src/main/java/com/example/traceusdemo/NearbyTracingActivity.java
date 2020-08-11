package com.example.traceusdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NearbyTracingActivity extends AppCompatActivity {

    private static final String TAG = "Nearby";
    //Declare Database object
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference sDatabaseReference;

    //Declare FirebaseAuth object
    private FirebaseAuth mFirebaseAuth;

    //Declare variables
    private String user_phone;
    private String user_status;
    private String my_user_uid;
    private MessageListener mMessageListener;
    private Message mPubMessage, mActiveMessage;
    private long onFoundStart = -1;
    private String current_date;

    //Declare views
    private TextView mScanningStatusTv;
    private Button mStartScanningBtn;
    private Button mStopScanningBtn;

    //Declare location object and variables
    FusedLocationProviderClient mFusedLocationProviderClient;
    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_tracing);

        //Initialize views
        mScanningStatusTv = (TextView) findViewById(R.id.scanningStatusTv);
        mStartScanningBtn = (Button) findViewById(R.id.startScanningBtn);
        mStopScanningBtn = (Button) findViewById(R.id.stopScanningBtn);

        //Initialize an instance of the database
        mFirebaseDatabase = mFirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("users");
        sDatabaseReference = mFirebaseDatabase.getReference().child("meet");

        //Initialize FirebaseAuth object
        mFirebaseAuth = FirebaseAuth.getInstance();
        my_user_uid = mFirebaseAuth.getCurrentUser().getUid();

        //initialize location object
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Nearby Messages API messageListener
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {

                //get metUserUID
                String met_user_uid = new String(message.getContent()); //UID of found device
                Log.d(TAG, "Found user: " + met_user_uid);

                //get current location
                getLocation();

                //record the date and time of meet
                Calendar calendar = Calendar.getInstance();
                current_date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

                //write recorded info to "meet" node in database
                Meet meet = new Meet(met_user_uid, current_date, latitude, longitude);
                sDatabaseReference.push().setValue(meet);

            }

            @Override
            public void onLost(Message message) {
                Log.d(TAG, "Lost sight of user: " + new String(message.getContent()));
            }
        };

        //retrieve my user data from db to be published
        getMyUserData();
        mPubMessage = new Message(my_user_uid.getBytes());


        //Start scanning onClickListener
        mStartScanningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish(my_user_uid);
                subscribe();
                mScanningStatusTv.setText("Scanning for nearby devices...");
            }
        });

        //Stop scanning onClickListener
        mStopScanningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unpublish();
                unsubscribe();
                mScanningStatusTv.setText("Stopped scanning");
            }
        });

    }

    //register met_user_uid in database


    //send intentExtra to display found users

    //start broadcasting message to other devices
    private void publish(String message) {
        Log.i(TAG, "Publishing message: " + message);
        mActiveMessage = new Message(message.getBytes());
        Nearby.getMessagesClient(this).publish(mActiveMessage);
    }

    //stop broadcasting message to other devices
    private void unpublish() {
        Log.i(TAG, "Unpublishing.");
        if (mPubMessage != null) {
            Nearby.getMessagesClient(this).unpublish(mPubMessage);
            mPubMessage = null;
        }
    }

    // Subscribe to receive messages from other devices
    private void subscribe() {
        Log.i(TAG, "Subscribing.");
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
    }

    //stop receiving messages from other devices
    private void unsubscribe() {
        Log.i(TAG, "Unsubscribing.");
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);
    }

    @Override
    public void onStart() {
        super.onStart();

        Nearby.getMessagesClient(this).publish(mPubMessage);
        Nearby.getMessagesClient(this).subscribe(mMessageListener);
    }

    @Override
    public void onStop() {
        Nearby.getMessagesClient(this).unpublish(mPubMessage);
        Nearby.getMessagesClient(this).unsubscribe(mMessageListener);

        super.onStop();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //initialize location
                    Location location = task.getResult();
                    if (location != null) {
                        try {
                            //Initialize Geocoder
                            Geocoder geocoder = new Geocoder(NearbyTracingActivity.this, Locale.getDefault());
                            //Initialize address list
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1);
                            //store lat and long in variables
                            double doubleLatitute = addresses.get(0).getLatitude();
                            double doubleLongitude = addresses.get(0).getLongitude();
                            //convert doubles to strings for database storage
                            latitude = Double.toString(doubleLatitute);
                            longitude = Double.toString(doubleLongitude);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }

    //retrieve my user information from database
    private void getMyUserData() {
        //Query the database to get current user's UID
        Query query = mDatabaseReference.orderByKey().equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        //Perform the actual database query for my phone and status info
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                    User user = singleSnapshot.getValue(User.class);

                    //store data in strings
                    assert user != null;
                    user_phone = user.getPhone().toString();
                    user_status = user.getStatus().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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