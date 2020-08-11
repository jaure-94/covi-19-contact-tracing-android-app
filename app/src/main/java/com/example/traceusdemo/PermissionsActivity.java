package com.example.traceusdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class PermissionsActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 2;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private static final String TAG = "Permission";
    private static final int ERROR_DIALOG_REQUEST = 1;

    //Initialize views
    private Button mAgreeProceedBtn;
    private TextView mStatusBlueTv;

    //Initialize bluetooth adapter
    private BluetoothAdapter mBlueAdapter;

    //Location permission boolean
    private boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        //Instantiate views
        mAgreeProceedBtn = (Button) findViewById(R.id.agreeProceedBtn);
        mStatusBlueTv = (TextView) findViewById(R.id.statusBlueTv);

        //Instantiate bluetooth adapter
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();

        //check if bluetooth is available
        if (mBlueAdapter == null) {
            mStatusBlueTv.setText("Bluetooth is not available");
        } else {
            mStatusBlueTv.setText("Bluetooth is available");
        }

        //agreeProceedBtn click
        mAgreeProceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBlueAdapter == null) {
                    startDashboardActivity();
                } else {
                    if (!mBlueAdapter.isEnabled()) {
                        showToast("Switching on Bluetooth...");
                        //Intent to switch on bluetooth
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, REQUEST_ENABLE_BT);
                        //Proceed to dashboard
                        startDashboardActivity();
                    } else {
                        showToast("Bluetooth is already switched on");
                        startDashboardActivity();
                    }
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mLocationPermissionGranted = false;

        switch (requestCode) {
            case REQUEST_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Access Coarse Location granted. You can scan for bluetooth devices.");
                } else {
                    showToast("Access Coarse Location denied. You can't scan for bluetooth devices.");
                }
            break;
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    //Toast message function
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    /*-- THE CODE BELOW IS FOR LOCATION, GPS AND GOOGLE SERVICES PERMISSIONS --*/

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            showToast("Permissions granted. Proceed");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PermissionsActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(PermissionsActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            showToast("You can't make map requests");
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    showToast("Permissions granted. Proceed");
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUtil.openFbReference("userAccounts", this);

        if (checkMapServices()) {
            if(mLocationPermissionGranted) {
                showToast("Permissions granted. Proceed");
            } else {
                getLocationPermission();
            }
        }

        //FirebaseUtil.attachListener();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //FirebaseUtil.detachListener();
    }

    private final void startDashboardActivity() {
        Intent intent = new Intent(PermissionsActivity.this, DashboardActivity.class);
        startActivity(intent);
    }

}