package com.monagan.owen.splitdist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FacebookLoginActivity extends AppCompatActivity {
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private String facebookTag="Facebook";
    public final static String EXTRA_NAME = "com.monagan.owen.splitdist.NAME";
    public final static String EXTRA_ID = "com.monagan.owen.splitdist.ID";
    public final static String EXTRA_FRIENDS = "com.monagan.owen.splitdist.FRIENDS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        final Intent intent = new Intent(FacebookLoginActivity.this, MenuActivity.class);
        setContentView(R.layout.activity_facebook_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);



        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()

                );
                GraphRequestBatch batch = new GraphRequestBatch(
                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject jsonObject,
                                            GraphResponse response) {
                                        // Application code for user
                                        Log.i(facebookTag, "User");
                                        Log.i(facebookTag, jsonObject.toString());
                                        Log.i(facebookTag, "User");
                                        Log.i(facebookTag, response.toString());
                                        try {
                                            String name = jsonObject.getString("name");
                                            int id = jsonObject.getInt("id");

                                            intent.putExtra(EXTRA_NAME, name);
                                            intent.putExtra(EXTRA_ID, Integer.toString(id));
                                            Log.i(facebookTag, "Succesfully got name: " + name);
                                        } catch (JSONException fail) {
                                            Log.e(facebookTag, "failed to get name: ");

                                        }
                                    }
                                }),
                        GraphRequest.newMyFriendsRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONArrayCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONArray jsonArray,
                                            GraphResponse response) {
                                        // Application code for users friends
                                        Log.i("Facebook", "UserFriends");
                                        Log.i(facebookTag, jsonArray.toString());

                                    }
                                })
                );
                batch.addCallback(new GraphRequestBatch.Callback() {
                    @Override
                    public void onBatchCompleted(GraphRequestBatch graphRequests) {
                        // Application code for when the batch finishes
                        Log.i(facebookTag, "batch");
                        Log.i(facebookTag, graphRequests.toString());
                        startActivity(intent);

                    }
                });
                batch.executeAsync();
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
