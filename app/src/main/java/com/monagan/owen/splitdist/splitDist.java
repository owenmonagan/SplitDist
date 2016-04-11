package com.monagan.owen.splitdist;

import android.app.Application;
import android.util.Log;

/**
 * Created by owen on 04/04/16.
 */
public class splitDist extends Application {
    private static final String appTag="Application";
    private poolMember[] members=null;
    private poolLeader leader=null;
    private computeBestRoute route;
    public boolean readyToAccess=false;

    public poolLeader getLeader() {
        return leader;
    }



    public void updateLeader(poolLeader newLeader) {
        this.leader.mergeLeader(newLeader);
    }

    public void setLeader(poolLeader newLeader) {
        this.leader=newLeader;
    }


    public poolMember[] getMembers(){
        return members;
    }

    public void updateMembers(poolMember[] newMembers){
        for( int count=0; count<newMembers.length; count++){
            updateMember(newMembers[count], newMembers[count].name);
        }
    }

    public boolean updateMember(poolMember newMember, String memberName){
        if (members==null){
            return false;
        }

        for(int count=0; count<members.length;count++) {
            if (members[count].name.equals(memberName)) {
                Log.d(appTag, "To be merged name: "+newMember.name);

                Log.d(appTag, "To be merged orign: "+newMember.origin);
                Log.d(appTag, "To be merged location: "+newMember.location);
                Log.d(appTag, "To be merged meetpointname: "+newMember.meetPoint.name);

                Log.d(appTag, "To be merged polyroute: "+newMember.polyRoute);
                members[count].mergeMember(newMember);
                return true;
            }
        }
        return false;
    }

    public void setMembers(poolMember[] newMembers){
        this.members=newMembers;
    }

    public boolean setMember(poolMember newMember, String memberName){
        if (members==null){
            return false;
        }
        for(int count=0; count<members.length;count++) {
            if (members[count].name == memberName) {
                members[count]=newMember;
                return true;
            }
        }
        return false;
    }

    public void calculateRoute(){
        route=new computeBestRoute(members,leader);
        while (!route.routeComputed){
        }
        updateLeader(route.leader);

        updateMembers(route.members);
        readyToAccess=true;

    }
}