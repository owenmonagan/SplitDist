package com.monagan.owen.splitdist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.wallet.wobs.TimeInterval;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,ConnectionCallbacks,OnConnectionFailedListener {
    private static final String mapTag="Map";
    private static final String transportTag="Transport";
    private static final String computeTag="Computaion";

    private GoogleMap mMap;
    private String transportMethod="";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String locationTag = "Location";
    private EditText portEdit;
    private ListView contactList;
    private String contactTag= "contact list";
    private int port=7955;
    private int zoomLevel;
    private LatLng cameraCenter;
    private computeBestRoute route;
    boolean updated=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        onCreateMap();


        //Log.i(mapTag, "On create");

    }

    public void onCreateMap(){
        Log.i(mapTag, "Starting Map Creation");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.i(mapTag, "Map Created");
        Log.i(locationTag, "Building GoogleApuClient");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        Log.i(locationTag, "GoogleApuClient Built");

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.i(mapTag,"Setting Up Map");

        // Add a marker in Sydney and move the camera
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }

        //route.execute();
    /*
        Thread updateThread= new Thread( new Runnable(){
            public void run() {
                poolLeader leader=((splitDist) getApplication()).getLeader();
                poolMember[] members=((splitDist) getApplication()).getMembers();
                route=new computeBestRoute(members,leader);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateLeaderAndMembers();
                        updateMap();
                    }
                });


            }
        });
        updateThread.start();
        */
        //updateLeaderAndMembers();
        updateMap();
        Log.i(mapTag, "On create");
    }

    //Location services
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(locationTag, "Staring onConnect method");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(locationTag, "onConnect permission success");

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            //if (mLastLocation != null) {
            //  mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            // mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            //}
            Log.i("Location", String.valueOf(mLastLocation.getLatitude()));
            //computeMeetPoint();
        }
        else {
            Log.i(locationTag, "onConnect permission fail");

        }
    }
    @Override
    public void onConnectionSuspended(int pie){
    }

    @Override
    public void onConnectionFailed(ConnectionResult pie){
    }

    public void navigationView(){

    }
    public void overView(){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cameraCenter));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(zoomLevel);
        mMap.animateCamera(zoom);
    }
    public void updateMap(){
        while(!((splitDist) getApplication()).readyToAccess){
        }
        poolLeader leader=((splitDist) getApplication()).getLeader();
        poolMember[] members=((splitDist) getApplication()).getMembers();
        Log.i(mapTag, "Placing leader origin marker" + leader.origin.toString());
        zoomLevel=getZoomLevel(leader, members);
        cameraCenter=leader.polyRoute[leader.polyRoute.length/2];
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cameraCenter));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(zoomLevel);
        mMap.animateCamera(zoom);
        mMap.addPolyline(new PolylineOptions().add(leader.polyRoute).width(10)
                .color(leader.colour));
        mMap.addMarker(new MarkerOptions()
                .position(leader.origin)
                .alpha(0.7f)
                .title(leader.name)
                .icon(BitmapDescriptorFactory.defaultMarker(leader.hue)));
        Log.i(mapTag, "Placing leader destination marker" + leader.destination.toString());
        mMap.addMarker(new MarkerOptions()
                .position(leader.destination)
                .title("Destination")
                .alpha(0.9f)
                .icon(BitmapDescriptorFactory.defaultMarker(leader.hue)));

        for(int count=0; count < members.length;count++){
            Log.i(mapTag, "Adding member " + count + " origin" + members[count].origin.toString());

            mMap.addPolyline(new PolylineOptions()
                    .add(members[count].polyRoute).width(5)
                    .color(members[count].colour));
            mMap.addMarker(new MarkerOptions()
                    .position(members[count].origin)
                    .alpha(0.7f)
                    .title(members[count].name)
                    .icon(BitmapDescriptorFactory.defaultMarker(members[count].hue)));
            float [] hsv = new float[3];
            mMap.addMarker(new MarkerOptions()
                    .position(members[count].meetPoint.location)
                    .title(members[count].meetPoint.name)
                    .alpha(0.9f)
                    .icon(BitmapDescriptorFactory.defaultMarker(members[count].hue)));
        }

        updated=false;
    }


    public int getZoomLevel(poolLeader leader, poolMember[] member){
        double latMax=leader.origin.latitude;
        double lngMax=leader.origin.longitude;
        double latMin=leader.origin.latitude;
        double lngMin=leader.origin.longitude;

        for(int count=0; count<member.length+1;count++){
            for(int originOrMeetPoint=0;originOrMeetPoint<2;originOrMeetPoint++) {
                LatLng originMeetpoint;

                if (originOrMeetPoint == 0) {
                    if(count<member.length) {
                        originMeetpoint = member[count].origin;
                    }
                    else{
                        originMeetpoint = leader.origin;
                    }
                } else {
                    if(count<member.length) {
                        originMeetpoint = member[count].meetPoint.location;
                    }
                    else {
                        originMeetpoint= leader.destination;
                    }
                }
                if (latMax < originMeetpoint.latitude) {
                    latMax = originMeetpoint.latitude;
                }
                if (latMin > originMeetpoint.latitude) {
                    latMin = originMeetpoint.latitude;
                }
                if (lngMax < originMeetpoint.longitude) {
                    lngMax = originMeetpoint.longitude;
                }
                if (lngMin > originMeetpoint.longitude) {
                    lngMin = originMeetpoint.longitude;
                }
            }

        }
        int zoomLevel;
        double latDiff = latMax - latMin;
        double lngDiff = lngMax - lngMin;

        double maxDiff = (lngDiff > latDiff) ? lngDiff : latDiff;
        if (maxDiff < 360 / Math.pow(2, 20)) {
            zoomLevel = 21;
        } else {
            zoomLevel = (int) (-1*( (Math.log(maxDiff)/Math.log(2)) - (Math.log(360)/Math.log(2))));
            if (zoomLevel < 1)
                zoomLevel = 1;
        }
        return zoomLevel;
    }

}
