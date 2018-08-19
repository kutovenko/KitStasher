package com.example.kitstasher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.other.Helper;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;

/**
 * Created by Alexey on 21.04.2017.
 * Splash screen of the app. Checks Facebook login status and redirects to Login Activity
 * if not logged on.
 *
 * Легкий сплэш-скрин без xml. Проверяет статус регистрации через Facebook. Если регистрации нет,
 * перенаправляет на LoginActivity.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Helper.isOnline(this)){
            Toast.makeText(this, R.string.We_badly_need_internet, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            finish();
        }else {
            AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                    updateWithToken(newAccessToken);
                }
            };
            accessTokenTracker.startTracking();
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            updateWithToken(accessToken);
        }
    }

    private void updateWithToken(AccessToken currentAccessToken) {
        if (currentAccessToken != null) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}