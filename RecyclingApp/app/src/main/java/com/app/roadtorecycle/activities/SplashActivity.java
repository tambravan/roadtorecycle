package com.app.roadtorecycle.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.app.roadtorecycle.appmanager.PermissionHandlerActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.app.roadtorecycle.R;

public class SplashActivity extends PermissionHandlerActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    private GoogleApiClient googleApiClient;
    ImageButton nextIb;
    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        nextIb = findViewById(R.id.nextIb);
        nextBtn = findViewById(R.id.nextBtn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();

            }
        });

        if (Build.VERSION.SDK_INT > 22) {
            boolean stickyMode = true;

            askForPermission(permissions, true, new PermissionHandlerActivity.PermissionCallBack() {

                @Override
                public void onPermissionsGranted() {
                    locationDialog();
                }

                @Override
                public void onPermissionsDenied(String[] permissions) {
                    Log.i("mahdi", "onPermissionsDenied: ");
                }
            });

        } else {
            locationDialog();


        }
    }

    private void locationDialog() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(SplashActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            setUpPane();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        SplashActivity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                        case LocationSettingsStatusCodes.CANCELED:
                            locationDialog();
                            break;
                    }
                }
            });
        }

    }

    private void setUpPane() {

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case 1000:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        setUpPane();
                        break;
                    case Activity.RESULT_CANCELED:
                        locationDialog();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }
}
