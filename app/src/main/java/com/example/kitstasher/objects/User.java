package com.example.kitstasher.objects;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.kitstasher.other.Constants;

/**
 * Created by Алексей on 14.05.2017.
 */

public class User {
    private String name;
    private String fbId;
    private String fbUserpicUrl;
    private String appHqId;
    private String back4appId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getFbUserpicUrl() {
        return fbUserpicUrl;
    }

    public void setFbUserpicUrl(String fbUserpicUrl) {
        this.fbUserpicUrl = fbUserpicUrl;
    }

    public String getAppHqId() {
        return appHqId;
    }

    public void setAppHqId(String appHqId) {
        this.appHqId = appHqId;
    }

    public String getBack4appId() {
        return back4appId;
    }

    public void setBack4appId(String back4appId) {
        this.back4appId = back4appId;
    }


}
