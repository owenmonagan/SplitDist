package com.monagan.owen.splitdist;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.maps.model.LatLng;


import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    private String transportTag = "Transport";
    private String addMemberTag="Add member Menu";
    private String geoTag="Str->LatLang";
    private String contactTag= "contact list";
    final int errorColor= Color.RED;
    final int validColor= Color.GREEN;


    poolLeader leader;
    poolMember[] members=new poolMember[0];
    Button marker, walk, drive, bike, stationary, confirmMember, cancelMember, confirmRoute, cancelRoute, markerDestination;
    RadioButton facebookFriend;
    AutoCompleteTextView name;
    EditText location, destination;
    TextView AddMemberHeader;
    String transport;
    LatLng locationLL, destinationLL;
    ListView listOfMembers;
    RelativeLayout addDestinationLayout;
    Boolean currentMemberIsLeader;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = new Intent(MenuActivity.this, MapsActivity.class);

        confirmRoute = (Button) findViewById(R.id.confirmRouteButton);
        confirmRoute.setOnClickListener(confirmCancelRouteClickListener);
        cancelRoute = (Button) findViewById(R.id.cancelRouteButton);
        cancelRoute.setOnClickListener(confirmCancelRouteClickListener);

        addDestinationLayout=(RelativeLayout)findViewById(R.id.selectDestination);
        currentMemberIsLeader=true;
        createAddMemberMenu();

    }

    private void createAddMemberMenu() {

        AddMemberHeader = (TextView) findViewById(R.id.addMemberHeader);
        location = (EditText) findViewById(R.id.location);
        location.setOnFocusChangeListener(locationChange);

        name = (AutoCompleteTextView) findViewById(R.id.poolMemberName);
        facebookFriend = (RadioButton) findViewById(R.id.facebookButton);
        cancelMember = (Button) findViewById(R.id.cancelButton);
        confirmMember = (Button) findViewById(R.id.confirmButton);
        destination=(EditText) findViewById(R.id.destination);
        destination.setOnFocusChangeListener(locationChange);
        markerDestination = (Button) findViewById(R.id.markerDestinationButton);
        confirmMember = (Button) findViewById(R.id.confirmButton);
        cancelMember = (Button) findViewById(R.id.cancelButton);

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
        marker = (Button) findViewById(R.id.markerButton);
        transport="";
        createTransportSelection();
        confirmMember.setOnClickListener(confirmCancelMemberClickListener);
        cancelMember.setOnClickListener(confirmCancelMemberClickListener);
    }



    private void createTransportSelection() {
        walk = (Button) findViewById(R.id.walkingButton);
        drive = (Button) findViewById(R.id.drivingButton);
        bike = (Button) findViewById(R.id.bicyclingButton);
        stationary = (Button) findViewById(R.id.stationaryButton);

        float startingAplha=1.0f;
        walk.setOnClickListener(transportClickListener);
        walk.setAlpha(startingAplha);
        bike.setOnClickListener(transportClickListener);
        bike.setAlpha(startingAplha);

        drive.setOnClickListener(transportClickListener);
        drive.setAlpha(startingAplha);

        stationary.setOnClickListener(transportClickListener);
        stationary.setAlpha(startingAplha);

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

    OnClickListener transportClickListener = new OnClickListener() {
        float notSelect = 0.5f;
        float selected = 1.0f;
        int textColour=Color.BLACK;

        public void onClick(View selectedButton) {

            drive.setAlpha(notSelect);
            drive.setTextColor(textColour);
            walk.setAlpha(notSelect);
            walk.setTextColor(textColour);
            bike.setAlpha(notSelect);
            bike.setTextColor(textColour);
            stationary.setAlpha(notSelect);
            stationary.setTextColor(textColour);



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
                stationary.setAlpha(selected);
                Log.i(transportTag, "Stationary Selected");
                transport="stationary";
            }

        }
    };

    OnClickListener confirmCancelMemberClickListener = new OnClickListener() {

        public void onClick(View v) {
            if(v.getId()==R.id.confirmButton){

                boolean completed=trySaveNewMember();
                if(completed){
                    setupPoolMemberList();
                    createAddMemberMenu();
                }
            }
            else{
                createAddMemberMenu();

            }
        }

    };

    OnClickListener confirmCancelRouteClickListener = new OnClickListener() {

        public void onClick(View v) {
            if(v.getId()==R.id.confirmRouteButton){
                Log.i(addMemberTag, "Confirmed the route");
                leader.name="John";
                leader.locationString="pearse st dublin 2";
                leader.destinationString="trinity college dublin";
                members[0].locationString="ranelagh dublin 6";
                members[0].name="Mitt";
                ((splitDist) getApplication()).setLeader(leader);
                ((splitDist) getApplication()).setMembers(members);
                try {
                    ((splitDist) getApplication()).calculateRoute();
                    Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
                catch (Exception e){
                    Log.e(geoTag, "No response from the server!");
                    Context context = getApplicationContext();
                    CharSequence text = "Couldn't Connect get response from the Server!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

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
                    currentMemberIsLeader=false;
                    return true;
                }

            }

        }
        return false;
    }

    public void saveMemberAndUpdateList(String memberName,String memberLocationString){
        poolMember member = new poolMember(memberName, memberLocationString, memberLocationString, null, null,null, transport);
        member.location=locationLL;
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


    public LatLng getLocationFromAddress(String strAddress){
        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Log.i(geoTag, "trying to get latLng from "+strAddress);
        strAddress="300 West Martin Luther King Junior Boulevard, Austin, TX";
        try {
            int maxRetry=20;
            int tryCount=0;
            address = coder.getFromLocationName(strAddress, 5);
            while(address.size()==0 && tryCount<maxRetry) {
                address = coder.getFromLocationName(strAddress, 5);
                tryCount++;
            }
            if (address == null) {
                return null;
            }
            Log.e(geoTag, "Expection whilst trying to get latLng from " + address.toString());



            Address addressLocation = address.get(0);
            Log.e(geoTag, "Expection whilst trying to get latLng from " + addressLocation);


            //location.getLatitude();
            //location.getLongitude();
            LatLng latLngLocation = new LatLng(addressLocation.getLatitude(), addressLocation.getLongitude());


            return latLngLocation;
        }
        catch (IOException e){
            Log.e(geoTag, "Expection whilst trying to get latLng from address");
            return null;
        }
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
            stationary.setTextColor(errorColor);
            walk.setTextColor(errorColor);
            drive.setTextColor(errorColor);
            bike.setTextColor(errorColor);
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

    public void setupPoolMemberList(){

        listOfMembers=(ListView) findViewById(R.id.listOfMembers);

        int firstMemberPosition=1;
        String[] names= new String[members.length+firstMemberPosition];
        names[0]="Leader: \n"+leader.name+" - "+leader.transport;
        Log.i(contactTag, names[0]+" added to the poolMember list");
        Log.i(contactTag, names.length+" spaces in array names");

        for(int increment=0; increment<members.length;increment++){
            names[increment+1]=members[increment].name+" - "+members[increment].transport;
            if(increment==firstMemberPosition){
                names[increment]="Members: \n"+names[increment];
            }
            Log.i(contactTag, names[increment]+" added to the poolMember list");
        }
        // Defined Array values to show in ListView
        String[] values = new String[] { "Gibbs",
                "Jam",
                "Oisin",
                "Enda",
                "Hugo",
                "Rich",
                "Ollie",
                "Alex"
        };

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, names);


        // Assign adapter to ListView
        listOfMembers.setAdapter(adapter);
        // ListView Item Click Listener
        listOfMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listOfMembers.getItemAtPosition(position);

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();
                Log.i(contactTag, "contact: " + itemValue);
            }

        });
    }
}