package com.example.traceusdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ArrayList<User> mUsers;

    //Declare Auth object
    private FirebaseAuth mFirebaseAuth;

    //Initialize Database object
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    //Initialize Event Listener
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //Initialize Auth object
        mFirebaseAuth = FirebaseAuth.getInstance();

        FirebaseUtil.openFbReference("users", this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        //Call reference to RecyclerView
        RecyclerView rvUsers = (RecyclerView) findViewById(R.id.rvUsers);

        //Declare new account adapter, set it on RecyclerView
        final MeetingAdapter adapter = new MeetingAdapter(this);
        rvUsers.setAdapter(adapter);

        //Declare LinearLayoutManager, set it on RecyclerView
        LinearLayoutManager usersLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvUsers.setLayoutManager(usersLayoutManager);


    }

//    //Inflate toolbar menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.toolbar_menu, menu);
//        return true;
//    }
//
//    //handle menu item click event
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.create_user_menu:
//                Intent intent = new Intent(this, UpdateUserActivity.class);
//                startActivity(intent);
//                return true;
//            case R.id.logout_menu:
//                AuthUI.getInstance()
//                        .signOut(this)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            public void onComplete(@NonNull Task<Void> task) {
//                            FirebaseUtil.attachListener();
//                            }
//                        });
//                FirebaseUtil.detachListener();
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUtil.openFbReference("users", this);

        //Call reference to RecyclerView
        RecyclerView rvUsers = (RecyclerView) findViewById(R.id.rvUsers);

        //Declare new user adapter, set it on RecyclerView
        final MeetingAdapter adapter = new MeetingAdapter(this);
        rvUsers.setAdapter(adapter);

        //Declare LinearLayoutManager, set it on RecyclerView
        LinearLayoutManager usersLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvUsers.setLayoutManager(usersLayoutManager);


    }
}