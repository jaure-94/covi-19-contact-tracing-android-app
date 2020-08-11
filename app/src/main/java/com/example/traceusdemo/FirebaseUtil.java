package com.example.traceusdemo;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

    /*--CONNECT TO FIREBASE THROUGH THIS FIREBASE UTIL CLASS--*/

    //Initialize Database Object
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    public static FirebaseUtil mFirebaseUtil;
    public static ArrayList<User> mUsers;
    public static ArrayList<Meet> mMeetings;

    //Initialize authentication variables
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;

    //Initialize private activity
    private static Activity caller;
    private static final int RC_SIGN_IN = 123;

    private FirebaseUtil() {} //avoid this class being instantiated from outside this class

    public static void openFbReference(String ref, final Activity callerActivity) {
        if (mFirebaseUtil == null) {
            mFirebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();

            caller = callerActivity;

            //Initialize FirebaseAuth object
            mFirebaseAuth = FirebaseAuth.getInstance();

            //Initialize AuthListener
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    //check whether user is logged in or not
                    if (mFirebaseAuth.getCurrentUser() == null) {
                        FirebaseUtil.signIn();
                    }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
                }
            };
        }
        mUsers = new ArrayList<User>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    public static void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build());


        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attachListener () {
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    public static void detachListener () {
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }


}
