package com.kutovenko.kitstasher;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

/**
 * Created by Алексей on 23.02.2017.
 * Нужен для корректной работы Parse во фрагментах
 */
public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        //This will only be called once in app's entire lifecycle.
//        Parse.initialize(new Parse.Configuration.Builder(this)
//                .applicationId(getString(R.string.parse_application_id))
//                .clientKey(getString(R.string.parse_client_key))
//                .server(getString(R.string.parse_server_url)).build());
//        ParseFacebookUtils.initialize(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getContext() {
        return mContext;
    }

}
