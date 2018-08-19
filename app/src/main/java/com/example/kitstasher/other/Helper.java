package com.example.kitstasher.other;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kitstasher.MyApplication;
import com.example.kitstasher.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static com.example.kitstasher.activity.MainActivity.MY_PERMISSIONS_REQUEST_WRITE;


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


    public static void encrypt(File inputFile) {
        doCrypto(Cipher.ENCRYPT_MODE, inputFile);
    }

    public static void decrypt(File inputFile) {
        doCrypto(Cipher.DECRYPT_MODE, inputFile);
    }

    private static void doCrypto(int cipherMode, File inputFile){
        try {
            String key = "An ode to no one";

            // Создаем ключ
            Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
            // Создаем объект Cipher
            Cipher cipher = Cipher.getInstance("AES");
            // Инициализируем его
            cipher.init(cipherMode, secretKey);
            // Читаем входной файл
            FileInputStream inputStream = new FileInputStream(inputFile);
            // Помещаем его содержимое в байтовый массив
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            // Вызываем метод doFinal()
            byte[] outputBytes = cipher.doFinal(inputBytes);
            // Создаем и записываем выходной файл
            FileOutputStream outputStream = new FileOutputStream(inputFile);
            outputStream.write(outputBytes);
            // Смываем за собой воду.
            inputStream.close();
            outputStream.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
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


    public static String codeToTag(String code) {
        String tag = code;
        switch (code){
            case MyConstants.CODE_AIR:
                tag = MyConstants.CAT_AIR;
                break;
            case MyConstants.CODE_GROUND:
                tag = MyConstants.CAT_GROUND;
                break;
            case MyConstants.CODE_SEA:
                tag = MyConstants.CAT_SEA;
                break;
            case MyConstants.CODE_SPACE:
                tag = MyConstants.CAT_SPACE;
                break;
            case MyConstants.CODE_AUTOMOTO:
                tag = MyConstants.CAT_AUTOMOTO;
                break;
            case MyConstants.CODE_OTHER:
                tag = MyConstants.CAT_OTHER;
                break;
            case MyConstants.CODE_FIGURES:
                tag = MyConstants.CAT_FIGURES;
                break;
            case MyConstants.CODE_FANTASY:
                tag = MyConstants.CAT_FANTASY;
                break;
        }
        return tag;
    }
//
//    public static String codeToDescription(String code){
//        String desc = "";
//        switch (code){
//            case MyConstants.NEW_TOOL:
//                desc = MyApplication.getContext().getResources().getString(R.string.new_tool);
//                break;
//            case MyConstants.REBOX:
//                desc = MyApplication.getContext().getResources().getString(R.string.rebox);
//                break;
//        }
//        return desc;
//    }

    public static String composeUrl(String url) {
        if (!Helper.isBlank(url)) {
            return MyConstants.BOXART_URL_PREFIX
                    + url
                    + MyConstants.BOXART_URL_LARGE
                    + MyConstants.JPG;
        } else {
            return ""; //TODO проверить!!!
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

    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static void clearArray(String[] filters) {
        for (int i = 0; i < filters.length; i++) {
            filters[i] = MyConstants.EMPTY;
        }
    }

    public static String getTodaysDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        return df.format(c.getTime());
    }

    public static String unifyValue(Object object) {
        if (object.getClass() == String.class) {
            return object.toString();
        } else {
        return String.valueOf(object);
        }
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

    private static String getScreenOrientation(Context context) {
        Display display = null;
        if ((context.getSystemService(Context.WINDOW_SERVICE)) != null) {
            display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        }
        int rotation = display.getRotation();
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            return "landscape";
        } else {
            return "portrait";
        }
    }

    public static String composeUrl(String url, Context context) {
        if (!Helper.isBlank(url)) {
            return MyConstants.BOXART_URL_PREFIX
                    + url
                    + getSuffix(context)
                    + MyConstants.JPG;
        } else {
            return "";
        }
    }

    private static String getSuffix(Context context) {
        String suffix = MyConstants.BOXART_URL_LARGE;
        SharedPreferences preferences = context.getSharedPreferences(MyConstants.BOXART_SIZE,
                Context.MODE_PRIVATE);
        if (preferences != null) {
            String temp = preferences.getString("boxart_size", "");
            switch (temp) {
                case MyConstants.BOXART_URL_COMPANY_SUFFIX:
                    suffix = "";
                    break;
                case MyConstants.BOXART_URL_SMALL:
                    suffix = MyConstants.BOXART_URL_SMALL;
                    break;
                case MyConstants.BOXART_URL_MEDIUM:
                    suffix = MyConstants.BOXART_URL_MEDIUM;
                    break;
                case MyConstants.BOXART_URL_LARGE:
                    suffix = MyConstants.BOXART_URL_LARGE;
                    break;
                default:
                    break;
            }
        }
        return suffix;
    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

//    public static String descToCode(String d) { //// TODO: 13.09.2017 Helper
//        String desc = "";
//        if (d.equals(MyApplication.getContext().getString(R.string.unknown))){
//            desc = "0";
//        }else if (d.equals(MyApplication.getContext().getString(R.string.newkit))){
//            desc = "1";
//        }else if (d.equals(MyApplication.getContext().getString(R.string.rebox))){
//            desc = "2";
////        }else if (d.equals(getString(R.string.new_decal))){
////            desc = "3";
////        }else if (d.equals(getString(R.string.changed_box))){
////            desc = "4";
////        }else if (d.equals(getString(R.string.repack))){
////            desc = "5";
////        }else if (d.equals(getString(R.string.reissue))){
////            desc = "6";
//        }
//        return desc;
//    }

    public static String getKitYear(String y) {
        if (!y.equals(MyApplication.getContext().getString(R.string.year))) {
            return y;
        } else {
            return "";
        }
    }


    public static boolean isOnline(Context context) {
        // Add a null check before you proceed. Need for Samsung devices.
        //https://stackoverflow.com/questions/46500571/nullpointerexception-when-checking-network-state-on-samsung-devices
        if (context == null) return false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }


//    public static String setDescription(String description) { // TODO: 03.09.2017 Helper
//        String desc = MyConstants.EMPTY;
//        final int version = Build.VERSION.SDK_INT;
//        if (version >= 23) {
//            return ContextCompat.getColor(context, id);
//        } else {
//            return context.getResources().getColor(id);
//        }
//        if (!description.equals(MyConstants.EMPTY)) {
//            switch (description) {
//                case "0":
//                    desc = MyConstants.EMPTY;
//                    break;
//                case "1":
//                    desc = getString(R.string.new_tool);
//                    break;
//
//                case "2":
//                    desc = getString(R.string.changed_parts);
//                    break;
//                case "3":
//                    desc = getString(R.string.new_decal);
//                    break;
//                case "4":
//                    desc = getString(R.string.changed_box);
//                    break;
//                case "5":
//                    desc = getString(R.string.repack);
//                    break;
//                case "6":
//                    desc = MyConstants.EMPTY;
//            }
//        }
//        return desc;
//    }



//    public static boolean checkPermissions(Context context, String... permissions) {
//        for (String permission : permissions) {
//            if (!checkPermission(context, permission)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public static boolean checkPermission(Context context, String permission) {
//        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    public static boolean isDeviceInfoGranted(Context context) {
//        return checkPermission(context, Manifest.permission.READ_PHONE_STATE);
//    }
//
//    public static void requestPermissions(Object o, int permissionId, String... permissions) {
//        if (o instanceof Fragment) {
//            Fragment.requestPermissions((Fragment) o, permissions, permissionId);
//        } else if (o instanceof Activity) {
//            ActivityCompat.requestPermissions((AppCompatActivity) o, permissions, permissionId);
//        }
//    }
}
