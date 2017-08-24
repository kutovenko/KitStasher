package com.example.kitstasher.other;

import android.content.Context;
import android.media.MediaCodec;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

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

import static com.example.kitstasher.other.Constants.CAT_AUTOMOTO;
import static com.example.kitstasher.other.Constants.CAT_OTHER;
import static com.example.kitstasher.other.Constants.CODE_AUTOMOTO;
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
            case CODE_AUTOMOTO:
                tag = CAT_AUTOMOTO;
                break;
            case CODE_OTHER:
                tag = CAT_OTHER;
                break;
        }
        return tag;
    }

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
        }
        return code;
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
