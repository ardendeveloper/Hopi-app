package com.dev.hopi_app.Activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.dev.hopi_app.R;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private GeoFire geoFire;
    GeoQuery geoQuery;
    String tempKey;
    SharedPreferences sharedPref;
    Firebase myFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        tempKey = sharedPref.getString("firstName","")+"_"+sharedPref.getString("lastName","");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://hopiiapp.firebaseio.com/users");
        geoFire = new GeoFire(new Firebase("https://hopiiapp.firebaseio.com/location"));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        //JRU Reference
        LatLng Reference = new LatLng(14.592571, 121.028441);

        //greenwoods
//        LatLng Reference = new LatLng(14.552989, 121.102596);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(Reference));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(18);
        mMap.animateCamera(zoom);
        mMap.getUiSettings().setMapToolbarEnabled(true);
//        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();

        Firebase tempRef = myFirebaseRef.child(sharedPref.getString("pushID",""));
        tempRef.child("status").setValue("online");
        Toast.makeText(MapsActivity.this, "Start!!", Toast.LENGTH_SHORT).show();
        //greenwoods
//        geoQuery = geoFire.queryAtLocation(new GeoLocation(14.552926, 121.102595), 0.5);

        //jru
        geoQuery = geoFire.queryAtLocation(new GeoLocation(14.592571, 121.028441), 0.5);

        geoFire.getLocation(tempKey, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location != null) {

                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));

                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext());
                            mBuilder.setSmallIcon(R.drawable.hopi_icon);
                            mBuilder.setContentTitle("Alert!");
                            mBuilder.setContentText(key+ " entered the vicinity!");

                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            mBuilder.setSound(alarmSound);
                            mNotificationManager.notify(0, mBuilder.build());
                        }

                        @Override
                        public void onKeyExited(String key) {
                            System.out.println(String.format("Key %s is no longer in the search area", key));
                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {
                            System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
                            LatLng currentPosition = new LatLng(location.latitude, location.longitude);
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(currentPosition).title("Nandito si "+ key));
                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
//                Toast.makeText(MapsActivity.this, "hahahahahahaha", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getBaseContext(),ProfileActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }

                        @Override
                        public void onGeoQueryReady() {
                            System.out.println("All initial data has been loaded and events have been fired!");
                        }

                        @Override
                        public void onGeoQueryError(FirebaseError error) {
                            System.err.println("There was an error with this query: " + error);
                        }
                    });

                } else {
                    System.out.println(String.format("There is no location for key %s in GeoFire", key));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.err.println("There was an error getting the GeoFire location: " + firebaseError);
            }
        });
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    public void onLocationChanged(Location location) {
        Double mLat = location.getLatitude();
        Double mLong = location.getLongitude();
        geoFire.setLocation(tempKey, new GeoLocation(mLat, mLong));
    }

    public void onConnectionSuspended(int i){

    }

    public void onConnectionFailed(ConnectionResult connectionResult){

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Firebase tempRef = myFirebaseRef.child(sharedPref.getString("pushID",""));
        tempRef.child("status").setValue("offline");
        Toast.makeText(MapsActivity.this, "Destroy!!", Toast.LENGTH_SHORT).show();

        geoFire.removeLocation(tempKey);
    }

    @Override
    public void onPause(){
        super.onPause();
        Firebase tempRef = myFirebaseRef.child(sharedPref.getString("pushID",""));
        tempRef.child("status").setValue("offline");
        Toast.makeText(MapsActivity.this, "Pause!!", Toast.LENGTH_SHORT).show();
    }
}
