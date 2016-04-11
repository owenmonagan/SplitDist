package com.monagan.owen.splitdist;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.monagan.owen.splitdist.place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by owen on 30/03/16.
 */
public class poolMember {
    private static final String memberTag="poolMember";
    public String name,locationString,originString;
    public LatLng location;
    public LatLng origin;
    public place meetPoint=null;
    public String transport;
    public LatLng[] polyRoute=null;
    public int colour;
    public float hue;
    public String[] types;
    public int userId;
    public int eta;


    protected poolMember(String memberName,String memberLocation, String memberOrigin,
               String memberMeetPoint, String etaString,String memberPolyRoute,String transportStr){
        name=memberName;
        try{
            location=stringToLatLng(memberLocation);
        }
        catch (Exception e){
            locationString=memberLocation;
            location=null;

        }
        try {
            origin=stringToLatLng(memberOrigin);

        }
        catch (Exception e){
            originString=memberLocation;
            origin=null;
            Log.i(memberTag, "Couldnt make orign!");

        }
        transport=transportStr;
        if(memberMeetPoint!=null) {
            meetPoint = new place(memberMeetPoint);
        }
        else {
            Log.i(memberTag, "Couldnt make place!");
            meetPoint=null;
        }
        polyRoute=decodePoly(memberPolyRoute);
        Log.i(memberTag, "Member " + name + " created.");
        generateColour();

        try {
            eta=Integer.parseInt(etaString);

        }
        catch (Exception e){
            originString=memberLocation;
            origin=null;
            Log.i(memberTag, "Couldnt make orign!");

        }
    }

    protected void generateColour(){
        int minRgb=0;
        int maxRgb=255;
        int blue=minRgb+(int)(Math.random()*maxRgb);
        int red=minRgb+(int)(Math.random()*maxRgb);
        int green=minRgb+(int)(Math.random()*maxRgb);
        float[] hsv = new float[3];
        Color.RGBToHSV(red, green, blue, hsv);
        hue=hsv[0];
        colour=Color.rgb(red,green,blue);
    }

    public void mergeMember(poolMember member){
        if(member.name!=null){
            this.name=member.name;
        }
        if(member.location!=null){
            this.location=member.location;
        }
        if(member.origin!=null){
            this.origin=member.origin;
        }
        if(member.originString!=null){
            this.originString=member.originString;
        }
        if(member.meetPoint!=null){
            this.meetPoint=member.meetPoint;
        }
        if(member.transport!=null){
            this.transport=member.transport;
        }
        if(member.polyRoute!=null){
            this.polyRoute=member.polyRoute;
            Log.i(memberTag, "PolyRoute Saved!");
            Log.i(memberTag, "PolyRoute: "+this.polyRoute.toString());

        }
        Log.i(memberTag, "Member " + this.name +"/"+member.name+ " Merged!.");
    }

    protected LatLng stringToLatLng(String latLangString){
        LatLng latLng;
        Log.i(memberTag, "Converting: "+latLangString+" to latLang");
        if(latLangString==null){
            Log.i(memberTag, "No latLng!");
            return null;
        }
        else {
            String[] splitString=latLangString.split(",");
            latLng= new LatLng ((Double.parseDouble(splitString[0].replaceAll("[\\\\[\\\\](){}]",""))),
                    (Double.parseDouble(splitString[1].replaceAll("[\\\\[\\\\](){}]",""))));
            Log.i(memberTag, "Converted: "+latLng.toString());
            return latLng;
        }
    }
    private LatLng[] decodePoly(String encoded) {
        if(encoded==null){
            Log.i(memberTag, name+" has no polyroute");
            return null;
        }
        Log.i(memberTag, "Decoding Polyline");
        //could be optimised
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly.toArray(new LatLng[poly.size()]);
    }
}
