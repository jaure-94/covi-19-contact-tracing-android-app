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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateUserActivity extends AppCompatActivity {

    //Declare Database object
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    //Declare Auth object
    private FirebaseAuth mFirebaseAuth;

    //Declare views
    private EditText mEditTextPhone;
    private Button mToDashboardBtn;
    private Button mSaveRegBtn;
    private RadioGroup mStatusRegisterRg;
    private RadioButton mStatusNegRadio;
    private RadioButton mStatusPosRadio;
    private ProgressBar mUpdateProgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        //Initialize Auth object
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Initialize an instance of the database
        mFirebaseDatabase = mFirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("users");

        //Initialize views
        mEditTextPhone = (EditText) findViewById(R.id.editTextPhone);
        mToDashboardBtn = (Button) findViewById(R.id.toDashboardBtn);
        mSaveRegBtn = (Button) findViewById(R.id.saveRegBtn);
        mStatusRegisterRg = (RadioGroup) findViewById(R.id.statusRegisterRg);
        mStatusNegRadio = (RadioButton) findViewById(R.id.statusNegRadio);
        mStatusPosRadio = (RadioButton) findViewById(R.id.statusPosRadio);
        mUpdateProgBar = (ProgressBar) findViewById(R.id.updateProgBar);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //save button click listener
        mSaveRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditTextPhone.getText().toString().equals("")) { //check if edit text is not empty
                    mEditTextPhone.setError("Phone number required");
                } else if (mEditTextPhone.getText().toString().length() < 10) {
                   mEditTextPhone.setError("Enter valid phone number");
                } else {
                    saveUser();
                    Toast.makeText(UpdateUserActivity.this, "User details updated", Toast.LENGTH_LONG).show();
                    clean();
                    mUpdateProgBar.setVisibility(View.GONE);
                }
            }
        });

        //to dashboard onClickListener
        mToDashboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateUserActivity.this, DashboardActivity.class);
                startActivity(intent);
            }
        });
    }

    public void buttonCheck(View v) {
//        //retrieve radio button by ID
//        int radioId = mStatusRegisterRg.getCheckedRadioButtonId();
//        mRadioButton = (RadioButton) findViewById(radioId);
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
                clean();
                return true;
            }
            case R.id.update_user_menu: {
                Intent intent = new Intent(getApplicationContext(), UpdateUserActivity.class);
                startActivity(intent);
                clean();
                return true;
            }
            case R.id.logout_menu: {
                mFirebaseAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                clean();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveUser() {
        mUpdateProgBar.setVisibility(View.VISIBLE);

        //Retrieve Input text
        String phone = mEditTextPhone.getText().toString();
        String covidStatusPos = mStatusPosRadio.getText().toString();
        String covidStatusNeg = mStatusNegRadio.getText().toString();

        //update current user's phone number
        mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("phone").setValue(phone);

        //update current user's covid status
        if (mStatusPosRadio.isChecked()) {
            mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("status").setValue(covidStatusPos);
        } else {
            mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).child("status").setValue(covidStatusNeg);
        }

    }

    private void clean() {
        mEditTextPhone.setText("");
    }
}