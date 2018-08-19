package com.example.kitstasher.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.social.Social;
import com.shephertz.app42.paas.sdk.android.social.SocialService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;


/**
 * Created by Alexey on 21.04.2017.
 * Log in Facebook account and then in cloud services for interacting with online kits database
 * and Top users service. Currently there are AppHQ for kits and Parse on Buddy for Top users.
 *
 * Авторизация с помощью Facebook. Проверка наличия учетной записи Parse и регистрация,
 * если таковой не было. Регистрация на сервисе AppHq  для доступа к основной базе наборов.
 */

public class LoginActivity extends AppCompatActivity {
    private TextView tvInfo;
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    public static AsyncApp42ServiceApi asyncService;
    private SharedPreferences sharedPreferences;
    private KsUser ksUser;
    Collection<String> permissions;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 10;
    public static final int MY_PERMISSIONS_REQUEST_WRITE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getApplicationContext().getSharedPreferences(MyConstants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        tvInfo = findViewById(R.id.textView);
        tvInfo.setText(R.string.Please_log_in);
        ksUser = new KsUser.KsUserBuilder().build();

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

                            if (Helper.isBlank(sharedPreferences.getString(MyConstants.USER_ID_APPHQ, null))) {
                                registerInAppHq();
                            }
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
            public void done(final ParseUser user, ParseException e) {
                if (user != null) {
                    setSprefData(MyConstants.USER_ID_PARSE, user.getUsername());
                }

//                if (user != null && user.isNew()) {
                if (user != null && user.isNew()) {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery(MyConstants.PARSE_C_TOPUSERS);
                    query.whereContains(MyConstants.PARSE_TU_USERID, user.getUsername());
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (e != null) {
                                ParseObject newParseObject = new ParseObject(MyConstants.PARSE_C_TOPUSERS);
                                newParseObject.put(MyConstants.PARSE_TU_USERID, user.getUsername());
                                newParseObject.put(MyConstants.PARSE_TU_OWNERNAME, ksUser.getName());
                                newParseObject.put(MyConstants.PARSE_TU_STASH, 0);
                                try {
                                    newParseObject.save();
                                } catch (ParseException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void registerInAppHq() {
        asyncService = AsyncApp42ServiceApi.instance(LoginActivity.this);
        SocialService socialService = App42API.buildSocialService();
        socialService.linkUserFacebookAccount(accessToken.getUserId(),
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

