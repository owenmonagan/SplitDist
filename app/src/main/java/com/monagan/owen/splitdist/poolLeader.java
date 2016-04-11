package com.monagan.owen.splitdist;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.monagan.owen.splitdist.poolMember;
/**
 * Created by owen on 30/03/16.
 */
public class poolLeader extends poolMember {
    private static final String leaderTag="leaderMember";

    public LatLng destination;
    public String destinationName;
    public String destinationString;

    protected poolLeader(String leaderName,String leaderLocation, String leaderOrigin,
                         String leaderDestination, String etaString, String leaderPolyRoute,
                         String leaderDestinationName,String transport) {
        super(leaderName,leaderLocation,leaderOrigin,null, etaString,leaderPolyRoute,transport);

        destinationName=leaderDestinationName;
        Log.i(leaderTag, "leader "+name+" created.");

        try{
            destination=stringToLatLng(leaderDestination);
        }
        catch (Exception e){
            destination=null;
            destinationString=leaderDestination;
        }
    }
    public void mergeLeader(poolLeader member){
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
        }
        if(member.destination!=null){
            this.destination=member.destination;
        }
        if(member.destinationName!=null){
            this.destinationName=member.destinationName;
        }
        if(member.destinationString!=null){
            this.destinationString=member.destinationString;
        }
        Log.i(leaderTag, "Leader " + this.name +"/"+member.name+ " Merged!.");
    }
}
