package com.monagan.owen.splitdist;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by owen on 04/04/16.
 */

class geoCoding extends AsyncTask<String, Void, String[]> {
    public final String geoCodingTag="GeoCoding";
    private String apiKey="AIzaSyDkGc_FUywaeGxYaAAFqX-p5xNISE3dX1o";
    String address;
    LatLng location=null;
    boolean wait=true;
    String addressString="";
    geoCoding(String address){
        this.address=address.replace(" ", "+");
    }

    @Override
    protected String[] doInBackground(String... params) {
        URL url;
        StringBuilder resultString=new StringBuilder();
        HttpURLConnection urlConnection;

        try {
            url = new URL("https://maps.google.com/maps/api/geocode/json?address="
                    + address); //+ "&key=" + apiKey);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);



            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            //InputStream in = address.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            resultString = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                resultString.append(line);
            }
            Log.i(geoCodingTag, resultString.toString());
            urlConnection.disconnect();

        }
        catch (MalformedURLException e){
            Log.e(geoCodingTag, "Malformed String");
        }
        catch (IOException e){
            Log.e(geoCodingTag, "IO Exception");
        }
        finally {
            Log.i(geoCodingTag, "HttpComplete");

        }
        try{
            JSONObject jsonObj = new JSONObject(resultString.toString()).getJSONArray("results").getJSONObject(0);

            addressString=jsonObj.getString("formatted_address");
            JSONObject jsonLocation=jsonObj.getJSONObject("geometry").getJSONObject("location");
            location= new LatLng(jsonLocation.getDouble("lat"),jsonLocation.getDouble("lng"));
        }
        catch (JSONException e){
            Log.e(geoCodingTag, "Couldnt convert to JSON");
            e.printStackTrace();

        }
        catch (Exception e){

        }

        wait=false;
        return null;
    }

    @Override
    protected void onPostExecute(String... result) {

        /*
        Log.d("response", "RESULT: "+result);

        try {
            JSONObject jsonObject = new JSONObject(result[0]);

            double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            Log.d("latitude", "" + lat);
            Log.d("longitude", "" + lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    */
    }



    public JSONObject getLatLongByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            //conn.setRequestProperty("Content-Type",
              //      "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();
            Log.d("response", Integer.toString(responseCode));

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONObject myObj = new JSONObject(response);
            return myObj;
        }
        catch (JSONException e){
            Log.d("response", "couldnt convert to json");
            return null;
        }

    }
    protected LatLng stringToLatLng(String latLangString){
        LatLng latLng;
        if(latLangString==null){
            return null;
        }
        else {
            String[] splitString=latLangString.split(",");
            latLng= new LatLng ((Double.parseDouble(splitString[0].replaceAll("[\\\\[\\\\](){}]",""))),
                    (Double.parseDouble(splitString[1].replaceAll("[\\\\[\\\\](){}]",""))));
            return latLng;
        }
    }
}