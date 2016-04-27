package com.locationsender;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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

public class LocationActivity
        extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    GoogleApiClient googleApiClient = null;

    LocationRequest mLocationRequest = null;
    Location currentLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LocationActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });

        //
        pref = getSharedPreferences(Globals.KEY_PREF, Context.MODE_PRIVATE);
        Globals.MOBILE_NO = pref.getString(Globals.KEY_CONTACT, "nothing");
        if(Globals.MOBILE_NO.equals("nothing")){
            finish();
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
        }
        //

        //Conencting Google play services API
        if(googleApiClient == null){
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        //-- Conencting Google play services API

        ((TextView)findViewById(R.id.tvContactNo)).setText(Globals.MOBILE_NO);
        //-- Main Code
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    @Override
    public void onConnected(Bundle bundle) {
        //Toast.makeText(LocationActivity.this, "Connected with API", Toast.LENGTH_SHORT).show();

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(lastLocation != null){
            //Toast.makeText(LocationActivity.this, "Lat: "+lastLocation.getLatitude()+" Lon:"+lastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        }
        else{
            //Toast.makeText(LocationActivity.this, "No Last Location Saved", Toast.LENGTH_SHORT).show();
        }

        createLocationRequest();

        startLocationUpdates();

    }

    protected  void startLocationUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(LocationActivity.this, "Connection Suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(LocationActivity.this, "Can't Connect", Toast.LENGTH_SHORT).show();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {

                final Status status = result.getStatus();
                final LocationSettingsStates states = result.getLocationSettingsStates();
                switch (status.getStatusCode()){

                    case LocationSettingsStatusCodes.SUCCESS:
                        //Operation
                        Toast.makeText(LocationActivity.this, "Location is On", Toast.LENGTH_SHORT).show();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Toast.makeText(LocationActivity.this, "Turn On Location First", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;

                }
            }
        });
    }

    SharedPreferences pref;
    @Override
    public void onLocationChanged(Location location) {

        currentLocation = location;
        pref = getSharedPreferences(Globals.KEY_PREF, Context.MODE_PRIVATE);
        Globals.MOBILE_NO = pref.getString(Globals.KEY_CONTACT, "nothing");


        String result;

            result = SmsSender.send( Globals.MOBILE_NO, "EMFS#," + location.getLatitude() + "," + location.getLongitude());
            if(result.equals(SmsSender.RESULT_OK)){
                Toast.makeText(LocationActivity.this, "Location Sent Lat:"+location.getLatitude()+" Lon:"+location.getLongitude(), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(LocationActivity.this, result, Toast.LENGTH_SHORT).show();
            }
    }
}
