package com.example.kitstasher.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.kitstasher.R;
import com.example.kitstasher.other.AsyncApp42ServiceApi;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.ValueContainer;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.social.Social;
import com.shephertz.app42.paas.sdk.android.social.SocialService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alexey on 21.04.2017.
 * Log in Facebook account and then in cloud services for interacting with online kits database
 * and Top users service. Currently there are AppHQ for kits and Parse on Buddy for Top users.
 * @param
 */

public class LoginActivity extends AppCompatActivity {
    LoginButton loginButton;
    TextView tvInfo;

    CallbackManager callbackManager;
    AccessToken accessToken;

    public static AsyncApp42ServiceApi asyncService;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.parse_application_id))
                .clientKey(getString(R.string.parse_client_key))
                .server(getString(R.string.parse_server_url))
                .build());

        sharedPreferences = getApplicationContext().getSharedPreferences(Constants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        tvInfo = (TextView) findViewById(R.id.textView);
        tvInfo.setText(R.string.Please_log_in);

        //Facebook LoginButton and Callback
        loginButton = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                //Retrieving and saving of Facebook user ID
                final String fbID = loginResult.getAccessToken().getUserId();
                setSprefData(Constants.USER_ID_FACEBOOK, fbID);
//                editor.putString(Constants.USER_ID_FACEBOOK, fbID);
//                editor.commit(); //todo setPref()

                //Container for data from inner object
                final ValueContainer<String> fbName;
                fbName = new ValueContainer<>();
                fbName.setVal("fbName");

                //Retrieving Facebook username and profile picture
                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (object != null) {
                            try {
                                if (object.has("name")) {
                                    String profileName = object.getString("name");

                                    setSprefData(Constants.USER_NAME_FACEBOOK, profileName);
                                    fbName.setVal(profileName);
                                }

                                if (object.has("picture")) {
                                    String profilePicUrl =
                                            object.getJSONObject("picture")
                                                    .getJSONObject("data")
                                                    .getString("url");

                                    setSprefData(Constants.PROFILE_PICTURE_URL_FACEBOOK, profilePicUrl);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace(); //TODO catch
                            }
                            //If there are Facebook credentials, register in clouds
                            registerInClouds(fbID, fbName.getVal()); //todo try-catch
                        }
                        tvInfo.setText(getString(R.string.Welcome) + fbName.getVal());
                    }
                });
                //Requested fields to be returned from the JSONObject
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,cover,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                tvInfo.setText(R.string.Facebook_dont_know_you);
            }

            @Override
            public void onError(FacebookException error) {
                tvInfo.setText(R.string.Facebook_error);
            }
        });
    }

    /**
     * Register in cloud services if there are no data in sharedPreferences
     * @param id Facebook ID
     * @param name Facebook name
     */
    private void registerInClouds(String id, String name) {
        if (Helper.isBlank(sharedPreferences.getString(Constants.USER_ID_APPHQ, null))){
            registerInAppHq();
        }
        if (Helper.isBlank(sharedPreferences.getString(Constants.USER_ID_PARSE, null))){
            registerInParse(id, name);
        }
    }

    /**
     * Register in any Parse-based cloud service. Checks Top_users by Facebook ID
     * @param id Facebook ID
     * @param name Facebook name
     */
    private void registerInParse(final String id, final String name) {
        // Checking for doubles
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Top_users");
        query.whereContains("ownerId", id.trim());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    //Saving Parse user ID
                    setSprefData(Constants.USER_ID_PARSE,object.getObjectId());
                }

                else {
                    //Creating new ParseUser
                    ParseObject newParseObject = new ParseObject("Top_users");
                    newParseObject.put("ownerId", id);
                    newParseObject.put("ownerName", name);
                    newParseObject.put("stash", 0);
                    try {
                        newParseObject.save();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    //Saving Parse user ID
                    setSprefData(Constants.USER_ID_PARSE,newParseObject.getObjectId());
                }
            }

        });
    }

    /**
     * Register in AppHQ service. No need to check for doubles, it is done by cloud service.
     */
    private void registerInAppHq() {
        //Register in AppHq
        asyncService = AsyncApp42ServiceApi.instance(LoginActivity.this);
        SocialService socialService = App42API.buildSocialService();
        socialService.linkUserFacebookAccount(accessToken.getUserId().toString(),
                accessToken.toString(), new App42CallBack() {
                    public void onSuccess(Object response)
                    {
                        //Saving AppHq user ID in Shared Preferences
                        Social social  = (Social)response;
                        setSprefData(Constants.USER_ID_APPHQ, social.getUserName());

//                        tvInfo.setText(R.string.Kits_db_connection_ok);
                    }
                    public void onException(Exception ex){
                        tvInfo.setText(R.string.Kits_db_connection_error);
                    }
                });
    }

    /**
     * Writing data to sharedPreferences
     * @param key name of the preference
     * @param value value of the preference
     */
    private void setSprefData(String key, String value){
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = getApplicationContext().getSharedPreferences(Constants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Method for LoginButton
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

