package com.monagan.owen.splitdist;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by owen on 30/03/16.
 */
public class place {
    private static final String placeTag="place";
    public String name;
    public LatLng location;
    public String[] types;

    protected place(String placeInformation){
        String[] splitInfo=placeInformation.split("<>");
        name=splitInfo[0];
        location= stringToLatLng(splitInfo[1]);
        types=splitInfo[2].split(",");
        Log.i(placeTag, name+" created");
    }

    protected LatLng stringToLatLng(String latLangString){
        LatLng latLng;
        Log.i(placeTag, "Converting: "+latLangString+" to latLang");
        if(latLangString==null){
            Log.i(placeTag, "No latLng!");
            return null;
        }
        else {
            String[] splitString=latLangString.split(",");
            latLng= new LatLng ((Double.parseDouble(splitString[0].replaceAll("[\\\\[\\\\](){}]",""))),
                    (Double.parseDouble(splitString[1].replaceAll("[\\\\[\\\\](){}]",""))));
            Log.i(placeTag, "Converted: "+latLng.toString());
            return latLng;
        }
    }
}
