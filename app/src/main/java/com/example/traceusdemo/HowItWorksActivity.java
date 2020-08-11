package com.example.traceusdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class HowItWorksActivity extends AppCompatActivity {

    //Declare Auth object
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_it_works);

        //Initialize Auth object
        mFirebaseAuth = FirebaseAuth.getInstance();
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