package com.monagan.owen.splitdist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class AddMember extends FragmentActivity
        implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private String addMemberTag="Add member Menu";
    private String geoTag="Str->LatLang";
    final int errorColor= Color.RED;
    final int validColor= Color.GREEN;
    int normalColor=Color.BLACK;
    ViewAnimator showMenu,loadDirections;
    poolLeader leader;
    poolMember[] members=new poolMember[0];
    ImageButton marker, walk, drive, bike, confirmRoute;
    Button confirmMember, cancelMember;// confirmRoute, cancelRoute;
    RadioButton locationFinder, destinationFinder;
    AutoCompleteTextView name;
    EditText location, destination;
    TextView AddMemberHeader, SplitDistHeader;
    String transport;
    LatLng locationLL, destinationLL;
    ListView listOfMembers;
    RelativeLayout addDestinationLayout;
    Boolean currentMemberIsLeader;
    Intent intent;
    boolean canPlace=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        showMenu = (ViewAnimator) findViewById(R.id.viewAnimator);
        loadDirections = (ViewAnimator) findViewById(R.id.loadAnimation);

        Animation inAnim = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        Animation outAnim = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);
        showMenu.setOutAnimation(outAnim);
        showMenu.setInAnimation(inAnim);
        loadDirections.setOutAnimation(outAnim);
        loadDirections.setInAnimation(inAnim);

        AddMemberHeader = (TextView) findViewById(R.id.addMemberHeader);
        normalColor=AddMemberHeader.getCurrentTextColor();
        SplitDistHeader = (TextView) findViewById(R.id.appText);
        SplitDistHeader.setTextColor(Color.BLACK);
        Typeface spartan=Typeface.createFromAsset(getAssets(), "fonts/LeagueSpartan-Bold.otf");
        AddMemberHeader.setTypeface(spartan);
        SplitDistHeader.setTypeface(spartan);

        confirmRoute = (ImageButton) findViewById(R.id.startButton);
        confirmRoute.setOnClickListener(confirmCancelRouteClickListener);
        confirmRoute.setFocusable(true);
        confirmRoute.setFocusableInTouchMode(true);
        //cancelRoute = (Button) findViewById(R.id.cancelRouteButton);
        //cancelRoute.setOnClickListener(confirmCancelRouteClickListener);

        addDestinationLayout=(RelativeLayout)findViewById(R.id.selectDestination);
        currentMemberIsLeader=true;
        createAddMemberMenu();
        onCreateMap();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu.showNext();
                //Snackbar.make(view, "Navigation menu should show!", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();
            }
        });

    }



    private void createAddMemberMenu() {
        locationFinder= (RadioButton) findViewById(R.id.locationButton);
        destinationFinder= (RadioButton) findViewById(R.id.destinationButton);
        locationFinder.setOnClickListener(finderOnClick);
        destinationFinder.setOnClickListener(finderOnClick);
        AddMemberHeader = (TextView) findViewById(R.id.addMemberHeader);
        Typeface spartan=Typeface.createFromAsset(getAssets(), "fonts/LeagueSpartan-Bold.otf");
        AddMemberHeader.setTypeface(spartan);
        AddMemberHeader.setTextColor(normalColor);
        location = (EditText) findViewById(R.id.location);
        location.setOnFocusChangeListener(locationChange);

        name = (AutoCompleteTextView) findViewById(R.id.poolMemberName);
        //facebookFriend = (RadioButton) findViewById(R.id.facebookButton);
        cancelMember = (Button) findViewById(R.id.cancelButton);
        confirmMember = (Button) findViewById(R.id.confirmButton);
        destination=(EditText) findViewById(R.id.destination);
        destination.setOnFocusChangeListener(locationChange);
        confirmMember = (Button) findViewById(R.id.confirmButton);
        cancelMember = (Button) findViewById(R.id.cancelButton);
        if(members.length>0){
            confirmRoute.setColorFilter(validColor);
        }

        Log.i(addMemberTag, "Is it a leader:" + currentMemberIsLeader);
        if(!currentMemberIsLeader){
            addDestinationLayout.setVisibility(View.GONE);
            AddMemberHeader.setText("Add A Member to the Route");
            location.setHint("Member Location");
            name.setHint("Member Name");
            confirmMember.setText("Add Member");
            cancelMember.setText("Reset Member");
        }
        else{
            location.setHint("Leader Location");
            destination.setHint("Leader Destination");
            name.setHint("Name of Leader");
            destination.setText("");
            AddMemberHeader.setText("Add the Leader of the Route");
            confirmMember.setText("Add Leader");
            cancelMember.setText("Reset Leader");
        }

        name.setText("");
        location.setText("");
        destination.setText("");
        location.setHintTextColor(Color.BLACK);
        destination.setHintTextColor(Color.BLACK);
        //marker = (Button) findViewById(R.id.markerButton);
        transport="";
        createTransportSelection();
        confirmMember.setOnClickListener(confirmCancelMemberClickListener);
        cancelMember.setOnClickListener(confirmCancelMemberClickListener);
    }

    View.OnClickListener finderOnClick = new View.OnClickListener() {
        public void onClick(View selectedButton) {
            showMenu.showNext();
            canPlace=true;
            Thread updateThread= new Thread( new Runnable(){
                public void run() {
                    //while (showMenu.getId()==R.id.memberInput){
                    //}
                    canPlace=false;


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });


                }
            });


        }
    };
    private void createTransportSelection() {
        walk = (ImageButton) findViewById(R.id.walkingButton);
        walk.setFocusable(true);

        walk.setFocusableInTouchMode(true);
        walk.setBackgroundColor(Color.TRANSPARENT);

        drive = (ImageButton) findViewById(R.id.drivingButton);
        drive.setFocusable(true);
        drive.setFocusableInTouchMode(true);
        drive.setBackgroundColor(Color.TRANSPARENT);

        bike = (ImageButton) findViewById(R.id.bicyclingButton);
        bike.setFocusable(true);
        bike.setFocusableInTouchMode(true);
        bike.setBackgroundColor(Color.TRANSPARENT);

        //stationary = (ImageButton) findViewById(R.id.stationaryButton);

        float startingAplha=1.0f;
        walk.setOnClickListener(transportClickListener);
        walk.setAlpha(startingAplha);
        bike.setOnClickListener(transportClickListener);
        bike.setAlpha(startingAplha);

        drive.setOnClickListener(transportClickListener);
        drive.setAlpha(startingAplha);

        //stationary.setOnClickListener(transportClickListener);
        //stationary.setAlpha(startingAplha);

        Log.i(transportTag, "Transport Buttons Setup!");

    }

    View.OnFocusChangeListener locationChange=new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText current;
            geoCoding geoLocation=new geoCoding("");
            if(v.getId()==location.getId()){
                current=location;
            }
            else {
                current=destination;
            }
            if(!hasFocus){


                Log.i(geoTag, "Changed focus from edit text!");

                final String editTextField=current.getText().toString();
                geoLocation=new geoCoding(editTextField);
                geoLocation.execute();
                while(geoLocation.wait==true){

                }
                if (geoLocation.location!=null){
                    current.setTextColor(validColor);
                    current.setText(geoLocation.addressString);

                }
                else{
                    current.setTextColor(errorColor);
                }

            }
            else{
                current.setTextColor(Color.BLACK);
            }

            if(v.getId()==location.getId()){
                location=current;
                locationLL=geoLocation.location;
            }
            else {
                destination=current;
                destinationLL=geoLocation.location;
            }

        }
    };

    View.OnClickListener transportClickListener = new View.OnClickListener() {
        float notSelect = 0.3f;
        float selected = 1.0f;
        int textColour=Color.BLACK;

        public void onClick(View selectedButton) {

            drive.setAlpha(notSelect);
            drive.setBackgroundColor(Color.TRANSPARENT);
            walk.setAlpha(notSelect);
            walk.setBackgroundColor(Color.TRANSPARENT);
            bike.setAlpha(notSelect);
            bike.setBackgroundColor(Color.TRANSPARENT);
            //stationary.setAlpha(notSelect);
            //stationary.setBackgroundColor(Color.TRANSPARENT);


            if (selectedButton.getId() == R.id.walkingButton) {
                walk.setAlpha(selected);
                Log.i(transportTag, "Walking Selected");
                transport="walking";

            } else if (selectedButton.getId() == R.id.drivingButton) {
                drive.setAlpha(selected);
                Log.i(transportTag, "Driving Selected");
                transport="driving";

            } else if (selectedButton.getId() == R.id.bicyclingButton) {
                bike.setAlpha(selected);
                Log.i(transportTag, "Cycling Selected");
                transport="bicycling";
            } else {
                //stationary.setAlpha(selected);
                Log.i(transportTag, "Stationary Selected");
                transport="stationary";
            }

        }
    };

    View.OnClickListener confirmCancelMemberClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            if(v.getId()==R.id.confirmButton){

                boolean completed=trySaveNewMember();
                if(completed){
                    updateMembers();
                    createAddMemberMenu();
                }
            }
            else{
                createAddMemberMenu();

            }
        }

    };

    View.OnClickListener confirmCancelRouteClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            if(v.getId()==R.id.startButton){

                Log.i(addMemberTag, "Confirmed the route");
                //leader.name="John";
                //leader.locationString="pearse st dublin 2";
                //leader.destinationString="trinity college dublin";
                //members[0].locationString="ranelagh dublin 6";
                //members[0].name="Mitt";
                if(members.length>0) {
                    ((splitDist) getApplication()).setLeader(leader);
                    ((splitDist) getApplication()).setMembers(members);

                    showMenu.showNext();
                    loadDirections.showNext();
                    //Thread updateThread= new Thread( new Runnable(){
                      ////  public void run() {
                    ((splitDist) getApplication()).calculateRoute();

                    while(!((splitDist) getApplication()).readyToAccess){
                          }
                           // runOnUiThread(new Runnable() {
                             //   @Override
                               // public void run() {
                    updateMap();
                            //    }
                          // });


                    //    }
                    //});


                    loadDirections.showNext();
                } else{
                    AddMemberHeader.setTextColor(errorColor);
                }
                //try {
                //    ((splitDist) getApplication()).calculateRoute();
                //    Intent intent = new Intent(AddMember.this, MapsActivity.class);
                //    startActivity(intent);
                //}
                //catch (Exception e){
                //    Log.e(geoTag, "No response from the server!");
                //    Context context = getApplicationContext();
                //    CharSequence text = "Couldn't Connect get response from the Server!";
                //    int duration = Toast.LENGTH_SHORT;

                //    Toast toast = Toast.makeText(context, text, duration);
                //    toast.show();

                //}


            }
            else{
                createAddMemberMenu();

            }
        }

    };

    public Boolean trySaveNewMember(){
        Log.i(addMemberTag, "Attempting to save a new member/leader");
        if(fieldsFilled()){

            String memberName=name.getText().toString();


            Log.i(addMemberTag, "Tying to save: "+name.getText().toString());
            Log.i(addMemberTag, location.getText().toString());

            boolean vaildlocation=location.getCurrentTextColor()==validColor;
            if(!currentMemberIsLeader && vaildlocation) {
                String memberLocationString=location.getText().toString();
                saveMemberAndUpdateList(memberName, memberLocationString);
                showMenu.showNext();
                showMenu.showNext();
                return true;
            }
            else{
                boolean vaildDestination=destination.getCurrentTextColor()==validColor;
                if (vaildDestination && vaildlocation){
                    String memberLocationString=location.getText().toString();
                    String destString=destination.getText().toString();
                    leader= new poolLeader(memberName,memberLocationString,memberLocationString,destString,null,null,"church",transport);
                    leader.location=locationLL;
                    leader.destination=destinationLL;
                    mMap.addMarker(new MarkerOptions()
                            .position(leader.location)
                            .title(leader.name)
                            .snippet(leader.originString)
                            .alpha(0.9f)
                            .icon(BitmapDescriptorFactory.defaultMarker(leader.hue)));
                    mMap.addMarker(new MarkerOptions()
                            .position(leader.destination)
                            .title("Destination")
                            .snippet(leader.destinationString)
                            .alpha(1.0f)
                            .icon(BitmapDescriptorFactory.defaultMarker(leader.hue)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(leader.location));

                    currentMemberIsLeader=false;
                    return true;
                }

            }

        }
        return false;
    }

    public void saveMemberAndUpdateList(String memberName,String memberLocationString){
        poolMember member = new poolMember(memberName, memberLocationString, memberLocationString, null, null,null, transport);
        member.location = locationLL;
        mMap.addMarker(new MarkerOptions()
                .position(locationLL)
                .title(member.name)
                .snippet(member.originString)
                .alpha(0.9f)
                .icon(BitmapDescriptorFactory.defaultMarker(member.hue)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(locationLL));
        Log.i(addMemberTag, memberName + " was saved");
        poolMember[] updatedMembers = new poolMember[members.length + 1];
        for (int incriment = 0; incriment < updatedMembers.length; incriment++) {
            if ((incriment < members.length)) {
                updatedMembers[incriment] = members[incriment];
            } else {
                updatedMembers[incriment] = member;
                Log.i(addMemberTag, updatedMembers[incriment] + " was stored in the array");
            }
        }
        members=updatedMembers;
    }

    public boolean fieldsFilled(){
        boolean returnValue=true;
        if(name.getText().toString().length()==0){
            name.setHintTextColor(errorColor);
            returnValue= false;
            Log.w(addMemberTag, "No Name Provided");
        }
        if(location.getText().toString().length()==0){
            location.setHintTextColor(errorColor);
            returnValue= false;
            Log.w(addMemberTag, "No Location Provided");
        }
        if(destination.getText().toString().length()==0 && currentMemberIsLeader){
            destination.setHintTextColor(errorColor);
            returnValue= false;
            Log.w(addMemberTag, "No Destination Provided");
        }
        if(transport==""){
            //stationary.setBackgroundColor(errorColor);
            walk.setBackgroundColor(errorColor);
            drive.setBackgroundColor(errorColor);
            bike.setBackgroundColor(errorColor);
            returnValue=false;
            Log.w(addMemberTag, "No Transport selected");
        }
        if(returnValue==false){
            AddMemberHeader.setText("Missing field(s)!");
        }
        Log.w(addMemberTag, "Fields are filled out is:"+returnValue);
        return returnValue;
    }

    public boolean addressValid(LatLng latlngLocation,LatLng latlngDestination, Boolean isDestination){
        boolean returnvalue=true;
        if(isDestination && latlngDestination==null){
            destination.setText("");
            destination.setHintTextColor(errorColor);
            destination.setHint("Invalid destination address");
            returnvalue= false;
            Log.i(geoTag, "Invalid destination ");

        }
        if(latlngLocation==null){
            location.setText("");
            location.setHintTextColor(errorColor);
            location.setHint("Invalid location address");
            Log.i(geoTag, "Invalid location ");
            returnvalue= false;
        }
        return returnvalue;
    }

    public void updateMembers(){

    }








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
        //updateMap();
        Log.i(mapTag, "On create");
    }

    //Location services
   // protected void onStart() {
     //   mGoogleApiClient.connect();
       // super.onStart();
   // }

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

        mMap.clear();
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
                .title(leader.name+" - Origin")
                .snippet(leader.originString)
                .icon(BitmapDescriptorFactory.defaultMarker(leader.hue)));
        Log.i(mapTag, "Placing leader destination marker" + leader.destination.toString());
        mMap.addMarker(new MarkerOptions()
                .position(leader.destination)
                .title("Destination")
                .snippet(leader.destinationString)
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
                    .title(members[count].name + "- Origin")
                    .snippet(members[count].originString)
                    .icon(BitmapDescriptorFactory.defaultMarker(members[count].hue)));
            float [] hsv = new float[3];
            mMap.addMarker(new MarkerOptions()
                    .position(members[count].meetPoint.location)
                    .title(members[count].name+" meeting at "+members[count].meetPoint.name)
                    .snippet(members[count].meetPoint.location.toString())
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
