package com.example.kitstasher.other;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.kitstasher.MyApplication;
import com.example.kitstasher.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static com.example.kitstasher.other.Constants.CODE_AUTOMOTO;
import static com.example.kitstasher.other.Constants.CODE_FANTASY;
import static com.example.kitstasher.other.Constants.CODE_FIGURES;
import static com.example.kitstasher.other.Constants.CODE_OTHER;


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

    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }


    public static void encrypt(File inputFile)
            throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, inputFile);
    }

    public static void decrypt(File inputFile)
            throws Exception {
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

    public static String codeToTag(String code) {
        String tag = code;
        switch (code){
            case Constants.CODE_AIR:
                tag = Constants.CAT_AIR;
                break;
            case Constants.CODE_GROUND:
                tag = Constants.CAT_GROUND;
                break;
            case Constants.CODE_SEA:
                tag = Constants.CAT_SEA;
                break;
            case Constants.CODE_SPACE:
                tag = Constants.CAT_SPACE;
                break;
            case Constants.CODE_AUTOMOTO:
                tag = Constants.CAT_AUTOMOTO;
                break;
            case Constants.CODE_OTHER:
                tag = Constants.CAT_OTHER;
                break;
            case Constants.CODE_FIGURES:
                tag = Constants.CAT_FIGURES;
                break;
            case Constants.CODE_FANTASY:
                tag = Constants.CAT_FANTASY;
                break;
        }
        return tag;
    }
//
//    public static String codeToDescription(String code){
//        String desc = "";
//        switch (code){
//            case Constants.NEW_TOOL:
//                desc = MyApplication.getContext().getResources().getString(R.string.new_tool);
//                break;
//            case Constants.REBOX:
//                desc = MyApplication.getContext().getResources().getString(R.string.rebox);
//                break;
//        }
//        return desc;
//    }

    public static String tagToCode(String tag) {
        String code = tag;
        switch (code){
            case Constants.CAT_AIR:
                code = Constants.CODE_AIR;
                break;
            case Constants.CAT_GROUND:
                code = Constants.CODE_GROUND;
                break;
            case Constants.CAT_SEA:
                code = Constants.CODE_SEA;
                break;
            case Constants.CAT_SPACE:
                code = Constants.CODE_SPACE;
                break;
            case Constants.CAT_AUTOMOTO:
                code = CODE_AUTOMOTO;
                break;
            case Constants.CAT_OTHER:
                code = CODE_OTHER;
                break;
            case Constants.CAT_FIGURES:
                code = CODE_FIGURES;
                break;
            case Constants.CAT_FANTASY:
                code = CODE_FANTASY;
                break;

        }
        return code;
    }

    public static String codeToMedia(int mediaCode){
        String media;
        switch (mediaCode){
            case Constants.M_CODE_OTHER:
                media = MyApplication.getContext().getResources().getString(R.string.media_other);
                break;
            case Constants.M_CODE_INJECTED:
                media = MyApplication.getContext().getResources().getString(R.string.media_injected);
                break;
            case Constants.M_CODE_SHORTRUN:
                media = MyApplication.getContext().getResources().getString(R.string.media_shortrun);
                break;
            case Constants.M_CODE_RESIN:
                media = MyApplication.getContext().getResources().getString(R.string.media_resin);
                break;
            case Constants.M_CODE_VACU:
                media = MyApplication.getContext().getResources().getString(R.string.media_vacu);
                break;
            case Constants.M_CODE_PAPER:
                media = MyApplication.getContext().getResources().getString(R.string.media_paper);
                break;
            case Constants.M_CODE_WOOD:
                media = MyApplication.getContext().getResources().getString(R.string.media_wood);
                break;
            case Constants.M_CODE_METAL:
                media = MyApplication.getContext().getResources().getString(R.string.media_metal);
                break;
            case Constants.M_CODE_3DPRINT:
                media = MyApplication.getContext().getResources().getString(R.string.media_3dprint);
                break;
            case Constants.M_CODE_MULTIMEDIA:
                media = MyApplication.getContext().getResources().getString(R.string.media_multimedia);
                break;
            default:
                media = MyApplication.getContext().getResources().getString(R.string.media_other);
                break;
        }
        return media;
    }


    public static String codeToStatus(int code){
        String status;
        switch (code){
            case Constants.STATUS_NEW:
                status = MyApplication.getContext().getResources().getString(R.string.status_new);
                break;
            case Constants.STATUS_OPENED:
                status = MyApplication.getContext().getResources().getString(R.string.status_opened);
                break;
            case Constants.STATUS_STARTED:
                status = MyApplication.getContext().getResources().getString(R.string.status_started);
                break;
            case Constants.STATUS_INPROGRESS:
                status = MyApplication.getContext().getResources().getString(R.string.status_inprogress);
                break;
            case Constants.STATUS_FINISHED:
                status = MyApplication.getContext().getResources().getString(R.string.status_finished);
                break;
            case Constants.STATUS_LOST:
                status = MyApplication.getContext().getResources().getString(R.string.status_lost_sold);
                break;
            default:
                status = MyApplication.getContext().getResources().getString(R.string.status_new);
                break;
        }
        return status;
    }

//    public static int mediaToCode(String media){
//        int code;
//
//        return code;
//    }


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


//    public String getScreenOrientation(){
//        // TODO: 13.08.2017 move to Helper
//        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        int rotation = display.getRotation();
//        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270){
//            return "landscape";
//        }else{
//            return "portrait";
//        }
//    }
}
