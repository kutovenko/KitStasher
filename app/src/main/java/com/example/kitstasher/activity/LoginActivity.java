package com.example.kitstasher.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.objects.KsUser;
import com.example.kitstasher.other.AsyncApp42ServiceApi;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;
import com.example.kitstasher.other.ValueContainer;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.social.Social;
import com.shephertz.app42.paas.sdk.android.social.SocialService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

//import com.parse.ParseFacebookUtils;

/**
 * Created by Alexey on 21.04.2017.
 * Log in Facebook account and then in cloud services for interacting with online kits database
 * and Top users service. Currently there are AppHQ for kits and Parse on Buddy for Top users.
 *
 */

public class LoginActivity extends AppCompatActivity {
    private TextView tvInfo;
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    public static AsyncApp42ServiceApi asyncService;
    private SharedPreferences sharedPreferences;
    private KsUser ksUser;
    Collection<String> permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        ParseFacebookUtils.initialize(this);

//        Parse.initialize(new Parse.Configuration.Builder(this)
//                .applicationId(getString(R.string.parse_application_id))
//                .clientKey(getString(R.string.parse_client_key))
//                .server(getString(R.string.parse_server_url))
//                .build());

        sharedPreferences = getApplicationContext().getSharedPreferences(MyConstants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        tvInfo = findViewById(R.id.textView);
        tvInfo.setText(R.string.Please_log_in);
        ksUser = new KsUser.KsUserBuilder().build();

        //Facebook LoginButton and Callback
        LoginButton loginButton = findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                final String fbID = loginResult.getAccessToken().getUserId();
                setSprefData(MyConstants.USER_ID_FACEBOOK, fbID);
                permissions = loginResult.getRecentlyGrantedPermissions();
                final ValueContainer<String> fbName;
                fbName = new ValueContainer<>();
                fbName.setVal("fbName");

                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (object != null) {
                            try {
                                if (object.has("name")) {
                                    String profileName = object.getString("name");

                                    setSprefData(MyConstants.USER_NAME_FACEBOOK, profileName);
                                    fbName.setVal(profileName);
                                    ksUser.setName(profileName);
                                }

                                if (object.has("picture")) {
                                    String profilePicUrl =
                                            object.getJSONObject("picture")
                                                    .getJSONObject("data")
                                                    .getString("url");
                                    setSprefData(MyConstants.PROFILE_PICTURE_URL_FACEBOOK, profilePicUrl);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            registerInClouds(fbID, fbName.getVal());



                        }
                        String greeting = getString(R.string.Welcome) + fbName.getVal();
                        tvInfo.setText(greeting);
                    }
                });
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

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, null, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {

                if (user == null) {
                    Toast.makeText(LoginActivity.this, "Uh oh. The user cancelled the Facebook login.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(LoginActivity.this, "User logged in through Facebook!", Toast.LENGTH_LONG).show();
                    final String userId = user.getUsername();
                    setSprefData(MyConstants.USER_ID_PARSE, userId);

                }
            }
        });
    }

    private void registerInClouds(String id, String name) {
        if (Helper.isBlank(sharedPreferences.getString(MyConstants.USER_ID_APPHQ, null))) {
            registerInAppHq();
        }


//        if (Helper.isBlank(sharedPreferences.getString(MyConstants.USER_ID_PARSE, null))) {
//            registerInParse(id, name);
//        }
    }

//    private void registerInParse(final String id, final String name) {
//        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
//            @Override
//            public void done(ParseUser user, ParseException e) {
//                if (user == null) {
//                    Toast.makeText(LoginActivity.this, "Uh oh. The user cancelled the Facebook login.", Toast.LENGTH_SHORT).show();
//                } else if (user.isNew()) {
//                    Toast.makeText(LoginActivity.this, "User signed up and logged in through Facebook!", Toast.LENGTH_SHORT).show();
//
//                } else {
//                    Toast.makeText(LoginActivity.this, "User logged in through Facebook!", Toast.LENGTH_SHORT).show();
//
//                }
//
//            }
//        });
//
////        ParseQuery<ParseObject> query = ParseQuery.getQuery(MyConstants.PARSE_C_TOPUSERS);
////        query.whereContains(MyConstants.PARSE_TU_OWNERID, id.trim());
////        query.getFirstInBackground(new GetCallback<ParseObject>() {
////            public void done(ParseObject object, ParseException e) {
////                if (e == null) {
////                    setSprefData(MyConstants.USER_ID_PARSE, object.getObjectId());
////                } else {
////                    ParseObject newParseObject = new ParseObject("Top_users");
////                    newParseObject.put("ownerId", id);
////                    newParseObject.put("ownerName", name);
////                    newParseObject.put("stash", 0);
////                    try {
////                        newParseObject.save();
////                    } catch (ParseException e1) {
////                        e1.printStackTrace();
////                    }
////                    setSprefData(MyConstants.USER_ID_PARSE, newParseObject.getObjectId());
////                }
////            }
////
////        });
//    }


    private void registerInAppHq() {
        asyncService = AsyncApp42ServiceApi.instance(LoginActivity.this);
        SocialService socialService = App42API.buildSocialService();
        socialService.linkUserFacebookAccount(accessToken.getUserId().toString(),
                accessToken.toString(), new App42CallBack() {
                    public void onSuccess(Object response)
                    {
                        Social social  = (Social)response;
                        setSprefData(MyConstants.USER_ID_APPHQ, social.getUserName());
                    }
                    public void onException(Exception ex){
                        tvInfo.setText(R.string.Kits_db_connection_error);
                    }
                });
    }

    private void setSprefData(String key, String value){
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = getApplicationContext().getSharedPreferences(MyConstants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);


    }
}

