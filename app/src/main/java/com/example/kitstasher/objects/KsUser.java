package com.example.kitstasher.objects;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kitstasher.other.MyConstants;
import com.facebook.AccessToken;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.social.Social;
import com.shephertz.app42.paas.sdk.android.social.SocialService;

/**
 * Created by Алексей on 14.05.2017.
 */

public class KsUser {
    private String name,
            accountType,
            loggedBy,
            socialNetwork,
            appHqId,
            parseId,
            userpicUrl;

    private KsUser(KsUserBuilder ksUserBuilder) {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getLoggedBy() {
        return loggedBy;
    }

    public void setLoggedBy(String loggedBy) {
        this.loggedBy = loggedBy;
    }

    public String getSocialNetwork() {
        return socialNetwork;
    }

    public void setSocialNetwork(String socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    public String getAppHqId() {
        return appHqId;
    }

    public void setAppHqId(String appHqId) {
        this.appHqId = appHqId;
    }

    public String getParseId() {
        return parseId;
    }

    public void setParseId(String parseId) {
        this.parseId = parseId;
    }

    public String getUserpicUrl() {
        return userpicUrl;
    }

    public void setUserpicUrl(String userpicUrl) {
        this.userpicUrl = userpicUrl;
    }

    public boolean saveToLocalDb() {
        return false;
    }

    public boolean registerInAppHq(AccessToken accessToken, final Context context) {
        SocialService socialService = App42API.buildSocialService();
        socialService.linkUserFacebookAccount(accessToken.getUserId().toString(),
                accessToken.toString(), new App42CallBack() {
                    public void onSuccess(Object response) {
                        Social social = (Social) response;
                        SharedPreferences settings;
                        SharedPreferences.Editor editor;
                        settings = context.getSharedPreferences(MyConstants.ACCOUNT_PREFS,
                                Context.MODE_PRIVATE);
                        editor = settings.edit();
                        editor.putString(MyConstants.USER_ID_APPHQ, social.getUserName());
                        editor.commit();
                    }

                    public void onException(Exception ex) {

                    }
                });
        return false;
    }

    public boolean registerInParse() {
        return false;
    }

    public void getLocalUserData() {

    }

    public void saveLocalUserData() {

    }

    public static class KsUserBuilder {
        private String name,
                accountType,
                loggedBy,
                socialNetwork,
                appHqId,
                parseId,
                userpicUrl;

        public KsUserBuilder hasName(String name) {
            this.name = name;
            return this;
        }

        public KsUserBuilder hasAccountType(String accountType) {
            this.accountType = accountType;
            return this;
        }

        public KsUserBuilder hasLoggedBy(String loggedBy) {
            this.loggedBy = loggedBy;
            return this;
        }

        public KsUserBuilder hasSocialId(String socialNetwork) {
            this.socialNetwork = socialNetwork;
            return this;
        }

        public KsUserBuilder hasAppHqId(String appHqId) {
            this.appHqId = appHqId;
            return this;
        }

        public KsUserBuilder hasPatseId(String parseId) {
            this.parseId = parseId;
            return this;
        }

        public KsUserBuilder hasUserpicUrl(String userpicUrl) {
            this.userpicUrl = userpicUrl;
            return this;
        }

        public KsUser build() {
            return new KsUser(this);
        }
    }
}
