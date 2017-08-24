package com.example.kitstasher;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Алексей on 23.02.2017.
 * Нужен для корректной работы Parse во фрагментах
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        //This will only be called once in your app's entire lifecycle.
//        Parse.initialize(this,
//                getResources().getString(R.string.parse_application_id),
//                getResources().getString(R.string.parse_client_key));

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.parse_application_id))
                .clientKey(getString(R.string.parse_client_key))
                .server(getString(R.string.parse_server_url)).build());
    }

}
