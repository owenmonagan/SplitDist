package com.monagan.owen.splitdist;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.monagan.owen.splitdist.splitDist;

/**
 * Created by owen on 04/04/16.
 */
public class computeBestRoute{
    private static final String computeTag="Computaion";
    public poolLeader leader;
    public poolMember[] members;
    private String requestString;
    private requestFromServer request;
    public boolean routeComputed=false;

    computeBestRoute(poolMember[] members, poolLeader leader){
        this.leader=leader;
        this.members=members;
        prepareRequestString();
        request= new requestFromServer(requestString);
        request.execute();
        try {
            parseResponseString(request.getResponse());
            routeComputed=true;
        }
        catch (ArrayIndexOutOfBoundsException e){
            Log.e(computeTag, "got an error from the server");
            routeComputed=false;
        }

    }



    //protected Void doInBackground(Void... arg0) {
   //     prepareRequestString();
   //     request= new requestFromServer(requestString);
   //     request.execute();
   //     return null;
   // }

   // @Override
   // protected void onPostExecute(Void result) {
  //      while(!request.requestComplete){
            //try {
            //    wait(3000);
            //}
           // catch (InterruptedException e){
                //Log.e(computeTag, "Failed to wait!");
           // }
  //      }
   //     parseResponseString(request.getResponse());
  //      super.onPostExecute(result);
  //  }

    private void prepareRequestString(){
        requestString="PoolRequest\n";

        if(leader.destination==null){
            requestString+="PoolDestination:"+leader.destinationString+"\n";
        }
        else {
            requestString+="PoolDestination:("+leader.destination.latitude+","+leader.destination.longitude+")\n";
        }
        if(leader.location==null){
            requestString+="PoolLeader:"+leader.name+";"+leader.locationString+";"+leader.transport+"\n";
        }
        else {
            requestString+="PoolLeader:"+leader.name+";("+leader.location.latitude+","+leader.location.longitude+");"+leader.transport+"\n";
        }

        for (int increment=0;increment<members.length;increment++){
            requestString+="PoolMember:"+members[increment].name;
            if(members[increment].location==null){
                requestString+=";"+members[increment].locationString;
            }
            else {
                requestString+=";("+members[increment].location.latitude+","+members[increment].location.longitude+")";
            }
            requestString+=";"+members[increment].transport;
            if (increment+1!=members.length){
                requestString+="\n";
            }
        }
        Log.i(computeTag, requestString);
    }




    public void parseResponseString(String responseMessage){
        Log.i(computeTag, "Parsing Response"+responseMessage);
        Log.i(computeTag, responseMessage);
        final int leaderLine=1;
        final int memberStartingLine=2;

        String[] line=responseMessage.split("\n");
        Log.i(computeTag, "Number of Lines in the response: "+Integer.toString(line.length));
        Log.i(computeTag, "Parsing: "+line[leaderLine]);
        String[] poolLeaderInformation=line[leaderLine].split(":")[1].split(";");
        //name,location,origin,destination,polyroute,destinationName
        leader=new poolLeader(poolLeaderInformation[0],null,poolLeaderInformation[1],poolLeaderInformation[2],
                poolLeaderInformation[3],poolLeaderInformation[4],null,null);

        members= new poolMember[line.length-memberStartingLine];
        Log.i(computeTag, "Number of poolMembers: "+Integer.toString(members.length));

        for (int increment=memberStartingLine;increment<line.length;increment++){
            Log.i(computeTag, "Parsing: " + line[increment]);
            String[] poolMemberInformation=line[increment].split(":")[1].split(";");
            Log.i(computeTag, "MemberName: "+poolMemberInformation[0]);
            Log.i(computeTag, "MemberOrigin: "+poolMemberInformation[1]);
            Log.i(computeTag, "MemberMeet: "+poolMemberInformation[2]);
            Log.i(computeTag, "MemberETA: "+poolMemberInformation[3]);
            Log.i(computeTag, "MemberRoute: "+poolMemberInformation[4]);

            members[increment-memberStartingLine]=new poolMember(poolMemberInformation[0],
                    null,poolMemberInformation[1],poolMemberInformation[2]
                    ,poolMemberInformation[3],poolMemberInformation[4],null);
        }
        Log.i(computeTag, "Testing if stored: "+members[0].name);
    }
}