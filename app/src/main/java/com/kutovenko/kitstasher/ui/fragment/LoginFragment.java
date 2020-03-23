package com.kutovenko.kitstasher.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.databinding.FragmentLoginBinding;
import com.kutovenko.kitstasher.model.KsUser;
import com.kutovenko.kitstasher.network.AsyncApp42ServiceApi;
import com.kutovenko.kitstasher.util.Helper;
import com.kutovenko.kitstasher.util.MyConstants;
import com.kutovenko.kitstasher.util.ValueContainer;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.social.Social;
import com.shephertz.app42.paas.sdk.android.social.SocialService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

import static com.facebook.FacebookSdk.getApplicationContext;


public class LoginFragment extends Fragment {
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    public static AsyncApp42ServiceApi asyncService;
    private SharedPreferences sharedPreferences;
    private KsUser ksUser;
    private Collection<String> permissions;


    private FragmentLoginBinding binding;


    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);

        sharedPreferences = getApplicationContext().getSharedPreferences(MyConstants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        ksUser = new KsUser.KsUserBuilder().build();

        callbackManager = CallbackManager.Factory.create();
        binding.loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

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
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,cover,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                binding.tvInfo.setText(com.kutovenko.kitstasher.R.string.Facebook_dont_know_you);
            }

            @Override
            public void onError(FacebookException error) {
                binding.tvInfo.setText(com.kutovenko.kitstasher.R.string.Facebook_error);
            }
        });

//        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, null, new LogInCallback() {
//            @Override
//            public void done(final ParseUser user, ParseException e) {
//                if (user != null) {
//                    setSprefData(MyConstants.USER_ID_PARSE, user.getUsername());
//                }
//                if (user != null && user.isNew()) {
//
//                    ParseQuery<ParseObject> query = ParseQuery.getQuery(MyConstants.PARSE_C_TOPUSERS);
//                    query.whereContains(MyConstants.PARSE_TU_USERID, user.getUsername());
//                    query.getFirstInBackground(new GetCallback<ParseObject>() {
//                        public void done(ParseObject object, com.parse.ParseException e) {
//                            if (e != null) {
//                                ParseObject newParseObject = new ParseObject(MyConstants.PARSE_C_TOPUSERS);
//                                newParseObject.put(MyConstants.PARSE_TU_USERID, user.getUsername());
//                                newParseObject.put(MyConstants.PARSE_TU_OWNERNAME, ksUser.getName());
//                                newParseObject.put(MyConstants.PARSE_TU_STASH, 0);
//                                try {
//                                    newParseObject.save();
//                                } catch (ParseException ex) {
//                                    ex.printStackTrace();
//                                }
//                            }
//                        }
//                    });
//                }
//            }
//        });




        return binding.getRoot();
    }


    private void registerInAppHq() {
        asyncService = AsyncApp42ServiceApi.instance(getActivity());
        SocialService socialService = App42API.buildSocialService();
        socialService.linkUserFacebookAccount(accessToken.getUserId(),
                accessToken.toString(), new App42CallBack() {
                    public void onSuccess(Object response)
                    {
                        Social social  = (Social)response;
                        setSprefData(MyConstants.USER_ID_APPHQ, social.getUserName());
                    }
                    public void onException(Exception ex){
                        binding.tvInfo.setText(com.kutovenko.kitstasher.R.string.Kits_db_connection_error);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
//        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

        setSprefData("registered", "true");
        StatisticsFragment fragment = new StatisticsFragment();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, fragment);
        fragmentTransaction.commit();
    }


}
