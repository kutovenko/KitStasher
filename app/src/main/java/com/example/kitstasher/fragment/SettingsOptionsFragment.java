package com.example.kitstasher.fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Алексей on 03.05.2017.
 * Import and export database. Repair of boxart image links.
 */

public class SettingsOptionsFragment extends Fragment implements View.OnClickListener {
    Button btnBackup, btnRestore, btnRepairImages;
    DbConnector dbConnector;
    Cursor cursor;
    View view;
    private ProgressDialog progressDialog;

    public SettingsOptionsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_options, container, false);

        initUI();
        return view;
    }

    private void initUI() {
        btnBackup = (Button)view.findViewById(R.id.btnBackup);
        btnBackup.setOnClickListener(this);
        btnRestore = (Button)view.findViewById(R.id.btnRestore);
        btnRestore.setOnClickListener(this);
        btnRepairImages = (Button)view.findViewById(R.id.btnRepairImages);
        btnRepairImages.setOnClickListener(this);
        progressDialog = new ProgressDialog(getActivity());
    }

    @Override
    public void onClick(View view) {
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();

        switch (view.getId()){
            case R.id.btnBackup:
//                progressDialog = ProgressDialog.show(getActivity(), "Saving Database", getString(R.string.Saving));
//                progressDialog.setCancelable(true);
                Toast.makeText(getActivity(), getString(R.string.Saving), Toast.LENGTH_SHORT).show();
                backupDb();
//                progressDialog.dismiss();
                dbConnector.close();
                Toast.makeText(getActivity(), getString(R.string.Saved), Toast.LENGTH_SHORT).show();

//                progressDialog.dismiss();
                break;
            case R.id.btnRestore:
//                progressDialog = ProgressDialog.show(getActivity(), "Restoring Database", getString(R.string.Saving));
//                progressDialog.setCancelable(true);
                Toast.makeText(getActivity(), getString(R.string.Saving), Toast.LENGTH_SHORT).show();

                restoreDb();
//                progressDialog.dismiss();
                Toast.makeText(getActivity(), getString(R.string.Saved), Toast.LENGTH_SHORT).show();

                dbConnector.close();
//                progressDialog.dismiss();
                break;
            case R.id.btnRepairImages:
                String[] items = new String[]{"TXT"
//                        , "PDF"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.choose_output_format);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
//                 Do something with the selection
                        switch (item){
                            case 0:
                                exportStashTo("TXT");
//                                progressDialog.dismiss();
                                break;
//                            case 1:
//
//                                exportStashTo("PDF (coming soon)");
////                                progressDialog.dismiss();
//                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
//                repairImages();
//                dbConnector.close();
                break;
        }
        progressDialog.dismiss();
    }

    private void exportStashTo(String mode){
//        Toast.makeText(getActivity(), "Saving", Toast.LENGTH_SHORT).show();
        progressDialog = ProgressDialog.show(getActivity(), "Export to File", getString(R.string.Saving));
        progressDialog.setCancelable(true);
        final String m = mode;
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {


                File exportDir = new File(Environment.getExternalStorageDirectory(),"Kitstasher");
                if (!exportDir.exists())
                {
                    exportDir.mkdirs();
                }
                switch (m){
                    case "TXT":
                        try
                        {
                            File fileStash = new File(exportDir, "MyStash.txt");
                            fileStash.delete();
                            fileStash.createNewFile();
                            FileOutputStream fOut = null;
                            try {
                                fOut = new FileOutputStream(fileStash, true);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            OutputStreamWriter osw = new OutputStreamWriter(fOut);
                            osw.write("-- MY STASH --" + "\n");
                            Cursor curTXT = dbConnector.getAllData("kit_name");
                            osw.write("Total: " + curTXT.getCount() + "\n\n");
                            while(curTXT.moveToNext()) {
                                try {

                                    osw.write(
                                            curTXT.getString(curTXT.getColumnIndex("kit_name")) + " - "
                                                    + curTXT.getString(curTXT.getColumnIndex("brand")) + " "
                                                    + curTXT.getString(curTXT.getColumnIndex("brand_catno")) + " - "
                                                    + "1/" + curTXT.getInt(curTXT.getColumnIndex("scale"))
                                                    + "\n");

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                            osw.flush();
                            osw.close();
                            curTXT.close();
                        }catch(Exception sqlEx){
                            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
                        }
//                Toast.makeText(getActivity(), "Done!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        Uri txtUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()
                                + Constants.APP_FOLDER + "MyStash.txt"));

                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, txtUri);
                        startActivity(Intent.createChooser(sharingIntent, getString(R.string.save_file_using)));
                        break;

//                    case "PDF (coming soon)":
//                        progressDialog.dismiss();
//                        Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_SHORT).show();
//                        break;





//            case "CSV":
//
//                Toast.makeText(getActivity(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
//
//                File fileKits = new File(exportDir, "kits.csv");
//                try
//                {
//                    fileKits.createNewFile();
//                    CSVWriter csvWrite = new CSVWriter(new FileWriter(fileKits),
//                            CSVWriter.DEFAULT_SEPARATOR,
//                            CSVWriter.NO_QUOTE_CHARACTER);
//                    Cursor curCSV = dbConnector.getAllData("_id");
//                    while(curCSV.moveToNext())
//                    {
//                        //Which column you want to export
//                        String arrStr[] = {
//                                curCSV.getString(1), //barcode
//                                curCSV.getString(2), //brand
//                                curCSV.getString(3), //brand_catno
//                                curCSV.getString(4), //scale
//                                curCSV.getString(5), //kitname
//                                curCSV.getString(6), //desc
//                                curCSV.getString(7), //original name
//                                curCSV.getString(8), //category - tag
//                                curCSV.getString(9), //collection
//                                curCSV.getString(10), //send status
//                                curCSV.getString(11), // online id
//                                curCSV.getString(12), // boxart uri
//                                curCSV.getString(13), //boxart url
//
//                                curCSV.getString(14), //is deleted !!
//                                curCSV.getString(15)}; //date !!
//
//
//                        csvWrite.writeNext(arrStr);
//                    }
//                    csvWrite.close();
//                    curCSV.close();
//                }
//                catch(Exception sqlEx)
//                {
//                }
////                Toast.makeText(getActivity(), "Done!", Toast.LENGTH_SHORT).show();
//
//                break;
                }
                return null;
            }
        };
        asyncTask.execute();
    }

    private void restoreDb() {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getActivity(), R.string.sdcard_failed, Toast.LENGTH_SHORT).show();
        }
//        DbConnector dbConnector = new DbConnector(mCtx);
//        dbConnector.open();
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + "Kitstasher");
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, "kits.csv");
        try {
            Helper.decrypt(sdFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Очищаем базу
        // TODO: 17.08.2017 Сделать бэкап на случай сбоя записи?
        dbConnector.clearTable(DbConnector.TABLE_KITS);
        try {

            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String str = "";
            // читаем содержимое

            while ((str = br.readLine()) != null) {
                Log.d("Читаем", str);
                String[] colums = str.split(",");
                ContentValues cv = new ContentValues();
                cv.put(DbConnector.COLUMN_BARCODE, colums[0].trim()); //barcode
                cv.put(DbConnector.COLUMN_BRAND, colums[1].trim());//brand
                cv.put(DbConnector.COLUMN_BRAND_CATNO, colums[2].trim());//brand_catno
                cv.put(DbConnector.COLUMN_SCALE, colums[3].trim());//scale
                cv.put(DbConnector.COLUMN_KIT_NAME, colums[4].trim());//kitname
                cv.put(DbConnector.COLUMN_DESCRIPTION, colums[5].trim());//desc
                cv.put(DbConnector.COLUMN_ORIGINAL_KIT_NAME, colums[6].trim());//original name
                cv.put(DbConnector.COLUMN_CATEGORY, colums[7].trim());//category - tag
                cv.put(DbConnector.COLUMN_COLLECTION, colums[8].trim());//collection
                cv.put(DbConnector.COLUMN_SEND_STATUS, colums[9].trim());//send status
                cv.put(DbConnector.COLUMN_ID_ONLINE, colums[10]); // cloud id
                cv.put(DbConnector.COLUMN_BOXART_URI, colums[11]); // local boxart image file
                cv.put(DbConnector.COLUMN_BOXART_URL, colums[12].trim());//boxart url
//                cv.put(DbConnector.COLUMN_IS_DELETED, colums[13].trim());//is deleted
                cv.put(DbConnector.COLUMN_YEAR, colums[14].trim());//date

                dbConnector.addKitRec(cv);
            }
            Helper.encrypt(sdFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
//            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
//            progressDialog.dismiss();
        }
    }


    private void backupDb() {
        //сохраняем таблицу наборов
//        DbConnector dbConnector = new DbConnector(mCtx);
//        dbConnector.open();
        File exportDir = new File(Environment.getExternalStorageDirectory(),"Kitstasher");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        File fileKits = new File(exportDir, "kits.csv");
        try
        {
            fileKits.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(fileKits),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER);
            Cursor curCSV = dbConnector.getAllData("_id");
            while(curCSV.moveToNext())
            {
                //Which column you want to export
                String arrStr[] = {
                        curCSV.getString(1), //barcode
                        curCSV.getString(2), //brand
                        curCSV.getString(3), //brand_catno
                        curCSV.getString(4), //scale
                        curCSV.getString(5), //kitname
                        curCSV.getString(6), //desc
                        curCSV.getString(7), //original name
                        curCSV.getString(8), //category - tag
                        curCSV.getString(9), //collection
                        curCSV.getString(10), //send status
                        curCSV.getString(11), // online id
                        curCSV.getString(12), // boxart uri
                        curCSV.getString(13), //boxart url

                        curCSV.getString(14), //is deleted !!
                        curCSV.getString(15)}; //date !!


                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            Helper.encrypt(fileKits);
        }
        catch(Exception sqlEx)
        {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }

        //сохраняем таблицу брэндов

        File fileBrands = new File(exportDir, "brands.csv");
        try
        {
            fileBrands.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(fileBrands),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER);
            Cursor curCSV = dbConnector.getBrands("_id");
            curCSV.moveToFirst();

            while(curCSV.moveToNext())
            {
                //Which column you want to exprort
                String arrStr[] ={curCSV.getString(1)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
//            progressDialog.dismiss();
        }
        catch(Exception sqlEx)
        {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
//            progressDialog.dismiss();
        }

    }
}
