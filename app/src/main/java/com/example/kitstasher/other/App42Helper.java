package com.example.kitstasher.other;

import android.os.Environment;
import android.util.Log;

import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Алексей on 18.04.2017.
 */

public class App42Helper {
    String json, bc, textQ, objectId;
    private final String dbName = "KitStasher";
    private final String collectionName = "Test";
    StorageService storageService;

    public void sendJson(String json){
        storageService.insertJSONDocument(dbName,collectionName,json,new App42CallBack() {
            public void onSuccess(Object response)
            {
                Storage storage  = (Storage)response;
                ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();
                for(int i = 0;i < jsonDocList.size(); i++)
                {

                    System.out.println("objectId is " + jsonDocList.get(i).getDocId());
                    //Above line will return object id of saved JSON object
                    System.out.println("CreatedAt is " + jsonDocList.get(i).getCreatedAt());
                    System.out.println("UpdatedAtis " + jsonDocList.get(i).getUpdatedAt());
                    System.out.println("Jsondoc is " + jsonDocList.get(i).getJsonDoc());
                }

            }
            public void onException(Exception ex)
            {
                System.out.println("Exception Message"+ex.getMessage());
            }
        });
    }

    private void readSendJson(){

        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + "Kitstasher");
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, "kits.csv");
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String str = "";
            // читаем содержимое

            while ((str = br.readLine()) != null) {
                Log.d("Читаем", str);
                String[] nextLine = str.split(";");
                // nextLine[] is an array of values from the line

                json = "{\"u\": \"" + nextLine[1] + "\"" + "," + //scalemates_url
                        "\"i\": \"" + nextLine[2] + "\"" + "," + //kit_boxart_url
                        "\"s\":" + nextLine[3] + "," + //scale
                        "\"m\":\"" + nextLine[5] + "\"" + "," + //brand
                        "\"k\":\"" + nextLine[6] + "\"" + "," + //kit_name
                        "\"n\":\"" + nextLine[8] + "\"" + "," + //brand_catno
                        "\"b\":\"" + nextLine[9] + "\"" + "," + //bc
                        "\"c\":\"" + nextLine[11] + "\"" + "," + //category
                        "\"e\":\"" + nextLine[13] + "\"" + "," + //kit_noengname
                        "\"t\":\"" + nextLine[14] + "\"" + //kit_type
                        "}";

//                        "{\"a\":" + "\"" + nextLine[0] + "\"" + "," + //Access id
//                        "\"scalemates_url\": \"" + nextLine[1] + "\"" + "," + //scalemates_url
//                        "\"kit_boxart_url\": \"" + nextLine[2] + "\"" + "," + //kit_boxart_url
//                        "\"scale\":" + nextLine[3] + "," + //scale
//                        "\"prototype\":\"" + nextLine[4] + "\"" + "," + //prototype
//                        "\"brand\":\"" + nextLine[5] + "\"" + "," + //brand
//                        "\"kit_name\":\"" + nextLine[6] + "\"" + "," + //kit_name
//                        "\"description\":\"" + nextLine[7] + "\"" + "," + //description
//                        "\"brand_catno\":\"" + nextLine[8] + "\"" + "," + //brand_catno
//                        "\"bc\":\"" + nextLine[9] + "\"" + "," + //bc
//                        "\"category_full\":\"" + nextLine[10] + "\"" + "," +
//                        "\"category\":\"" + nextLine[11] + "\"" + "," +
//                        "\"timeframe\":\"" + nextLine[12] + "\"" + "," +
//                        "\"kit_noengname\":\"" + nextLine[13] + "\"" + "," +
//                        "\"kit_type\":\"" + nextLine[14] + "\"" +
//                        "}";
                sendJson(json);

//                tv.setText(json);
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void writeToCloud() {
//        progressDialog = ProgressDialog.show(getActivity(), "Writing to Cloud", getString(R.string.Saving));
//        progressDialog.setCancelable(true);
//        JSONObject jsonToSave = new JSONObject();
//        try {
//            jsonToSave.put(Constants.TAG_KIT_NAME, kitname);
//            jsonToSave.put(Constants.TAG_SCALE, scale);
//            jsonToSave.put(Constants.TAG_BRAND, brand);
//            jsonToSave.put(Constants.TAG_BARCODE, barcode);
//            jsonToSave.put(Constants.TAG_BRAND_CATNO, brand_catno);
//            jsonToSave.put(Constants.TAG_NOENG_NAME, kit_noengname);
//            jsonToSave.put(Constants.TAG_CATEGORY, category);
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            Toast.makeText(context, R.string.Error_on_cloud_saving, Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//        asyncService.insertJSONDoc(MyConstants.App42DBName, MyConstants.CollectionName, jsonToSave, this);//
//
//    }
//
//    private void writeBoxartToCloud(String brand, String brand_catno){
//        String name = brand + brand_catno + ".jpg"; //todo добавить description
//        File exportDir = new File(Environment.getExternalStorageDirectory(), "Kitstasher");
//        String filePath = exportDir + File.separator + name;
//
//        asyncService.uploadImage(name, filePath, UploadFileType.IMAGE, brand + brand_catno + kitname,
//                new AsyncApp42ServiceApi.App42UploadServiceListener() {
//                    @Override
//                    public void onUploadImageSuccess(Upload response) {
//                        Toast.makeText(getActivity(), "Image S"
//                                + response.getFileList().get(0).getTinyUrl(), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onUploadImageFailed(App42Exception ex) {
//                        Toast.makeText(getActivity(), "Image F" + ex.toString(), Toast.LENGTH_LONG).show();
//
//                    }
//
//                    @Override
//                    public void onGetImageSuccess(Upload response) {
//
//                    }
//
//                    @Override
//                    public void onGetImageFailed(App42Exception ex) {
//
//                    }
//                });
//    }
}
