package com.monagan.owen.splitdist;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by owen on 04/04/16.
 */
public class requestFromServer extends AsyncTask<Void, Void, Void> {
    private String requestTag = "requestFromServer";
    private String dstAddress="192.168.0.27";
    private int dstPort=9734;
    private String requestString;
    private String responseString;
    public boolean requestComplete=false;
    private final int maxRetrys=5;
    private int retryCount=0;

    requestFromServer(String requestString){
        this.requestString=requestString;
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;
        Log.i(requestTag, "Starting socket connection");
        try {
            socket = new Socket(dstAddress, dstPort);
            Log.i(requestTag, "Connection Established");
            ByteArrayOutputStream byteArrayOutputStream =
                    new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int bytesRead;
            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());

            Log.i(requestTag, "Prepared to send: \n" + requestString);
            outToServer.writeBytes(requestString);
            Log.i(requestTag, "Message sent");

            InputStream inputStream = socket.getInputStream();

            responseString="";
            while((bytesRead = inputStream.read(buffer))!=-1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                String responseSoFar=byteArrayOutputStream.toString("UTF-8");
                Log.i(requestTag, "Response chunk:\n" + responseSoFar);
            }
            responseString += byteArrayOutputStream.toString("UTF-8");
            Log.i(requestTag, "Received final: \n" + responseString);
        }
        catch (ConnectException e){
            retryCount++;
            if (maxRetrys>retryCount){
                Log.d(requestTag, "'Retrying connection");
                doInBackground();

            }
            else{
                Log.e(requestTag, "couldnt connect!");
            }
        }

        catch (UnknownHostException e) {
         //TODO Auto-generated catch block
            e.printStackTrace();
            responseString = "UnknownHostException: " + e.toString();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            responseString = "IOException: " + e.toString();
        }finally{
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        requestComplete=true;
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.i(requestTag, "Post Execute");

        super.onPostExecute(result);
    }

    public String getResponse(){
        Log.i(requestTag, "Atemping to return responseString");
        while(!requestComplete){
        }
        Log.i(requestTag, "return responseString");
        return responseString;
    }
}
