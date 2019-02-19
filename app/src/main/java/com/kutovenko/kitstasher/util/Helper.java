package com.kutovenko.kitstasher.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.Display;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * Created by Алексей on 10.05.2017.
 */

public class Helper {

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((!Character.isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    public static String nullCheck(String str){
        if (str == null || str.length() == 0) {
            return MyConstants.EMPTY;
        }else{
            return str;
        }
    }

    public static String nullCheck(String str, String defaultValue){
        if (str == null || str.length() == 0) {
            return defaultValue;
        }else{
            return str;
        }
    }

    public static String prepareCatno(String str){
        return str.trim().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }

    public static double findMax(double... vals) {
        double max = Double.NEGATIVE_INFINITY;

        for (double d : vals) {
            if (d > max) max = d;
        }
        return max;
    }

    public static int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }


    public static Drawable getAPICompatVectorDrawable(Context callingContext, int resource_id) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            return ContextCompat.getDrawable(callingContext.getApplicationContext(), resource_id);
        } else {
            return VectorDrawableCompat.create(
                    callingContext.getResources(),
                    resource_id,
                    callingContext.getTheme());
        }
    }

    public static String composeUrl(String url, String size) {
        if (!Helper.isBlank(url)) {
            return MyConstants.BOXART_URL_PREFIX
                    + url
                    + size
                    + MyConstants.JPG;
        } else {
            return MyConstants.EMPTY;
        }
    }

    public static String trimUrl(String str) {
        if (str != null && str.length() > 0
                ) {
            str = str.substring(0, str.length() - 12);
        }
        return str;
    }


    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static String getTodaysDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        return df.format(c.getTime());
    }

    public enum StorageState {
        NOT_AVAILABLE, WRITEABLE, READ_ONLY
    }

    public static StorageState getExternalStorageState() {
        StorageState result = StorageState.NOT_AVAILABLE;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return StorageState.WRITEABLE;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return StorageState.READ_ONLY;
        }
        return result;
    }

    public static float getScreenWidth(Activity context){
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density  = context.getResources().getDisplayMetrics().density;
        return outMetrics.widthPixels / density;
    }


    public static boolean isOnline(Context context) {
        // Add a null check before you proceed. Need for Samsung devices.
        //https://stackoverflow.com/questions/46500571/nullpointerexception-when-checking-network-state-on-samsung-devices
        if (context == null) return false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
