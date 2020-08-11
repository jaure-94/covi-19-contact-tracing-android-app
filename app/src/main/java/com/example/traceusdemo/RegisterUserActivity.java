package com.example.traceusdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RegisterUserActivity extends AppCompatActivity {

    //Declare database object
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    //Declare FirebaseAuth object
    private FirebaseAuth mFirebaseAuth;

    //Declare views
    private EditText mEmailEditText;
    private EditText mPhoneEditText;
    private EditText mNewRegPassword;
    private RadioButton mRegRadioPos;
    private RadioButton mRegRadioNeg;
    private Button mRegisterUserBtn;
    private Button mToLoginButton;
    private ProgressBar mProgressBar;

    //Declare User Object
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        //Initialize Auth object
        mFirebaseAuth = FirebaseAuth.getInstance();

        //create an instance of the database, pass child reference to path
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("users");

        //Alternatively:
        //Call getFbReference from FirebaseUtil class
        //FirebaseUtil.openFbReference("userAccounts", this);

        //Initialize an instance of the database through FirebaseUtil class
        //mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        //mDatabaseReference = FirebaseUtil.mDatabaseReference;

        //Initialize views
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mPhoneEditText = (EditText) findViewById(R.id.phoneEditText);
        mNewRegPassword = (EditText) findViewById(R.id.newRegPassword);
        mRegisterUserBtn = (Button) findViewById(R.id.registerUserBtn);
        mToLoginButton = (Button) findViewById(R.id.toLoginButton);
        mRegRadioPos = (RadioButton) findViewById(R.id.regRadioPos);
        mRegRadioNeg = (RadioButton) findViewById(R.id.regRadioNeg);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //If user already logged in, proceed to app
        if (mFirebaseAuth.getCurrentUser() != null) {
            //proceed to app
            startActivity(new Intent(RegisterUserActivity.this, PermissionsActivity.class));
        }

        //Register user onClickListener
        mRegisterUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserDetails();
                showToast("Account created successfully! Restart the app and login.");
                //clean();
            }
        });

        //ToLogin onClickListener
        mToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //Get intent extra from ListActivity
//        Intent intent = getIntent();
//        User user = (User) intent.getSerializableExtra("User");
//        if (user == null) { //i.e CreateAccount activity was reached through menu click
//            user = new User();
//        }
//        this.user = user;

//        //populate input fields with object data
//        mEmailEditText.setText(user.getEmail());
//        mPhoneEditText.setText(user.getPhoneNumber());


    }

    private void saveUserDetails() {

        //Retrieve text entered in editText views
        String email = mEmailEditText.getText().toString();
        String phone = mPhoneEditText.getText().toString();
        String password = mNewRegPassword.getText().toString();

        //Validate email, phone and password input fields
        if(TextUtils.isEmpty(email)) {
            mEmailEditText.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            mPhoneEditText.setError("Phone number is required");
            return;
        } else if (phone.length() < 10) {
            mPhoneEditText.setError("Enter valid phone");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mNewRegPassword.setError("Password is required");
            return;
        } else if (mNewRegPassword.length() < 6) {
            mNewRegPassword.setError("Password must be at least 6 characters");
        }

        mProgressBar.setVisibility(View.VISIBLE);

        //register user in firebase
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    //determine whether account already exists or not, insert object into database
//                    if (user.getEmail() == null) {
//                        mDatabaseReference.push().setValue(user);
//                    } else {
//                        mDatabaseReference.child(user.getPhone()).setValue(user);
//                    }

                    //Create User object, pass saved input values
                    User user = new User();
                    user.setEmail(mEmailEditText.getText().toString());
                    user.setPhone(mPhoneEditText.getText().toString());
                    if (mRegRadioNeg.isChecked()) {
                        user.setStatus(mRegRadioNeg.getText().toString());
                    } else {
                        user.setStatus(mRegRadioPos.getText().toString());
                    }

                    mDatabaseReference.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(user);

                    //upload user details to database
                    //mDatabaseReference.push().setValue(user);

                    showToast("User created successfully");

                    //proceed to app
                    startActivity(new Intent(RegisterUserActivity.this, PermissionsActivity.class));
                    finish();
                } else {
                    showToast("Error: unable to register user");
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    private void deleteAccount() {
        if (user == null) {
            showToast("Please save the deal before deleting");
        } else {
            mDatabaseReference.child(user.getEmail()).removeValue();
        }
    }

    private void clean() {
        //Reset input fields to empty, focus set on first name field
        mEmailEditText.setText("");
        mPhoneEditText.setText("");
    }

    private void showToast(String message) {
        //Show a simple toast message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}