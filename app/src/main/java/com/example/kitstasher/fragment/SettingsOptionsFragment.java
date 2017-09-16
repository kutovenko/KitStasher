package com.example.kitstasher.fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.kitstasher.other.DbConnector.COLUMN_AFTERMARKET_NAME;
import static com.example.kitstasher.other.DbConnector.COLUMN_AFTERMARKET_ORIGINAL_NAME;
import static com.example.kitstasher.other.DbConnector.COLUMN_BARCODE;
import static com.example.kitstasher.other.DbConnector.COLUMN_BOXART_URI;
import static com.example.kitstasher.other.DbConnector.COLUMN_BOXART_URL;
import static com.example.kitstasher.other.DbConnector.COLUMN_BRAND;
import static com.example.kitstasher.other.DbConnector.COLUMN_BRAND_CATNO;
import static com.example.kitstasher.other.DbConnector.COLUMN_CATEGORY;
import static com.example.kitstasher.other.DbConnector.COLUMN_COLLECTION;
import static com.example.kitstasher.other.DbConnector.COLUMN_CURRENCY;
import static com.example.kitstasher.other.DbConnector.COLUMN_DATE;
import static com.example.kitstasher.other.DbConnector.COLUMN_DESCRIPTION;
import static com.example.kitstasher.other.DbConnector.COLUMN_ID;
import static com.example.kitstasher.other.DbConnector.COLUMN_ID_ONLINE;
import static com.example.kitstasher.other.DbConnector.COLUMN_IS_DELETED;
import static com.example.kitstasher.other.DbConnector.COLUMN_KIT_NAME;
import static com.example.kitstasher.other.DbConnector.COLUMN_NOTES;
import static com.example.kitstasher.other.DbConnector.COLUMN_ORIGINAL_KIT_NAME;
import static com.example.kitstasher.other.DbConnector.COLUMN_PRICE;
import static com.example.kitstasher.other.DbConnector.COLUMN_PURCHASE_DATE;
import static com.example.kitstasher.other.DbConnector.COLUMN_PURCHASE_PLACE;
import static com.example.kitstasher.other.DbConnector.COLUMN_QUANTITY;
import static com.example.kitstasher.other.DbConnector.COLUMN_SCALE;
import static com.example.kitstasher.other.DbConnector.COLUMN_SEND_STATUS;
import static com.example.kitstasher.other.DbConnector.COLUMN_YEAR;
import static com.example.kitstasher.other.DbConnector.KIT_AFTER_AFTERBARCODE;
import static com.example.kitstasher.other.DbConnector.KIT_AFTER_AFTERBRAND;
import static com.example.kitstasher.other.DbConnector.KIT_AFTER_AFTERCATNO;
import static com.example.kitstasher.other.DbConnector.KIT_AFTER_AFTERDESIGNEDFOR;
import static com.example.kitstasher.other.DbConnector.KIT_AFTER_AFTERID;
import static com.example.kitstasher.other.DbConnector.KIT_AFTER_AFTERNAME;
import static com.example.kitstasher.other.DbConnector.KIT_AFTER_KITBARCODE;
import static com.example.kitstasher.other.DbConnector.KIT_AFTER_KITBRAND;
import static com.example.kitstasher.other.DbConnector.KIT_AFTER_KITCATNO;
import static com.example.kitstasher.other.DbConnector.KIT_AFTER_KITID;
import static com.example.kitstasher.other.DbConnector.KIT_AFTER_KITNAME;
import static com.example.kitstasher.other.DbConnector.KIT_AFTER_KITPROTOTYPE;
import static com.example.kitstasher.other.DbConnector.KIT_AFTER_LISTNAME;
import static com.example.kitstasher.other.DbConnector.MYLISTS_COLUMN_LIST_NAME;

/**
 * Created by Алексей on 03.05.2017.
 * Import and export database. Repair of boxart image links.
 */

public class SettingsOptionsFragment extends Fragment implements View.OnClickListener {
    private Button btnBackup, btnRestore, btnRepairImages, btnSetDefault;
    DbConnector dbConnector;
    Cursor cursor;
    View view;
    private ProgressDialog progressDialog;
    private String todayDate;
    private Spinner spDefaultCurrency;

    public SettingsOptionsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_options, container, false);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        todayDate = df.format(c.getTime());

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
        btnSetDefault = (Button)view.findViewById(R.id.btnSetDefault);
        btnSetDefault.setOnClickListener(this);
        progressDialog = new ProgressDialog(getActivity());

        spDefaultCurrency = (Spinner)view.findViewById(R.id.spDefaultCurrency);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String defCurrency = sharedPreferences.getString(Constants.DEFAULT_CURRENCY, "BYN");
        String[] currencies = new String[]{"BYN", "EUR", "RUR", "UAH", "USD"};
        ArrayAdapter currencyAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, currencies);
        spDefaultCurrency.setAdapter(currencyAdapter);
        int spCurrencyPosition = currencyAdapter.getPosition(defCurrency);
        spDefaultCurrency.setSelection(spCurrencyPosition);

        Button imp = (Button)view.findViewById(R.id.button2);
        imp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreOldDb();
            }
        });


    }

    @Override
    public void onClick(View view) {
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();

        switch (view.getId()){
            case R.id.btnSetDefault:
                setDefaultCurrency(spDefaultCurrency.getSelectedItem().toString());
                break;
            case R.id.btnBackup:
                Toast.makeText(getActivity(), getString(R.string.Saving), Toast.LENGTH_SHORT).show();
                backupDb();
                Toast.makeText(getActivity(), getString(R.string.Saved), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnRestore:
                Toast.makeText(getActivity(), getString(R.string.Saving), Toast.LENGTH_SHORT).show();
                restoreDb();
                Toast.makeText(getActivity(), getString(R.string.Saved), Toast.LENGTH_SHORT).show();
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
                break;
        }
        progressDialog.dismiss();
    }

    private void exportStashTo(String mode){
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

    private void backupDb() {

        File exportDir = new File(Environment.getExternalStorageDirectory(),"Kitstasher");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }
        backupKits(exportDir);
        backupListItems(exportDir);
        backupBrands(exportDir);
        backupLists(exportDir);
        backupShops(exportDir);
        backupAftermarket(exportDir);
        backupKitAfter(exportDir);
    }

    private void restoreDb() {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getActivity(), R.string.sdcard_failed, Toast.LENGTH_SHORT).show();
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + "Kitstasher");

        restoreKits(sdPath);
        restoreListsItems(sdPath);
        restoreBrands(sdPath);
        restoreLists(sdPath);
        restoreShops(sdPath);
        restoreAftermarket(sdPath);
        restoreKitAfter(sdPath);
    }

    /////////// AFTERMARKET ////////////
    private void backupAftermarket(File exportDir) {
        //сохраняем таблицу афтемаркета
        File fileAfter = new File(exportDir, Constants.AFTER_FILE_NAME);
        try
        {
            fileAfter.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(fileAfter),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER);
            Cursor curCSV = dbConnector.getAllAftermarket("_id");
            curCSV.moveToFirst();
            while(!curCSV.isAfterLast())
            {
                String arrStr[] = {
                        curCSV.getString(0), //id
                        curCSV.getString(1), //barcode
                        curCSV.getString(2), //brand
                        curCSV.getString(3), //brand_catno
                        curCSV.getString(4), //scale
                        curCSV.getString(5), //after_name
                        curCSV.getString(6), //desc
                        curCSV.getString(7), //after_original name
                        curCSV.getString(8), //category - tag
                        curCSV.getString(9), //collection
                        curCSV.getString(10), //send status
                        curCSV.getString(11), // online id
                        curCSV.getString(12), // boxart uri
                        curCSV.getString(13), //boxart url

                        curCSV.getString(14), //is deleted !!
                        curCSV.getString(15), //date !!

                        curCSV.getString(16), //year !!
                        curCSV.getString(17), //date !!
                        curCSV.getString(18), //date !!
                        curCSV.getString(19), //date !!
                        curCSV.getString(20), //date !!
                        curCSV.getString(21), //date !!
                        curCSV.getString(22),
                        curCSV.getString(23) //listname
                };
                csvWrite.writeNext(arrStr);
                curCSV.moveToNext();
            }
            csvWrite.close();
            curCSV.close();
            Helper.encrypt(fileAfter);
        }
        catch(Exception sqlEx)
        {
            Log.e("Aftermarket Backup", sqlEx.getMessage(), sqlEx);
        }
    }

    private void restoreAftermarket(File sdPath) {
        File aftermarketFile = new File(sdPath, Constants.AFTER_FILE_NAME);

        try {
            Helper.decrypt(aftermarketFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Очищаем базу
        dbConnector.clearTable(DbConnector.TABLE_AFTERMARKET);
        try {
            CSVReader reader = new CSVReader(new FileReader(aftermarketFile));
            String[] colums;
            ContentValues cv = new ContentValues();
            while ((colums = reader.readNext()) != null) {
                cv.put(COLUMN_ID, colums[0]);
                cv.put(COLUMN_BARCODE, colums[1]);
                cv.put(COLUMN_BRAND, colums[2]);
                cv.put(COLUMN_BRAND_CATNO, colums[3]);
                cv.put(COLUMN_SCALE, colums[4]);
                cv.put(COLUMN_AFTERMARKET_NAME, colums[5]);
                cv.put(COLUMN_DESCRIPTION, colums[6]);
                cv.put(COLUMN_AFTERMARKET_ORIGINAL_NAME, colums[7]);//!
                // если отличается
                cv.put(COLUMN_CATEGORY, colums[8]);
                cv.put(COLUMN_COLLECTION, colums[9]);
                cv.put(COLUMN_SEND_STATUS, colums[10]);
                cv.put(COLUMN_ID_ONLINE, colums[11]);
                //заметки? LOCAL?
                cv.put(COLUMN_BOXART_URI, colums[12]);
                cv.put(COLUMN_BOXART_URL, colums[13]);
                cv.put(COLUMN_IS_DELETED, colums[14]);
                cv.put(COLUMN_DATE, colums[15]);
                cv.put(COLUMN_YEAR, colums[16]);
                cv.put(COLUMN_PURCHASE_DATE, colums[17]);
                cv.put(COLUMN_PRICE, colums[18]);
                cv.put(COLUMN_QUANTITY, colums[19]);
                cv.put(COLUMN_NOTES, colums[20]);
                cv.put(COLUMN_CURRENCY, colums[21]);
                cv.put(COLUMN_PURCHASE_PLACE, colums[22]);
                cv.put(MYLISTS_COLUMN_LIST_NAME, colums[23]);
                dbConnector.addAftermarket(cv);
            }
            Helper.encrypt(aftermarketFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backupKitAfter(File exportDir){
        File fileAfter = new File(exportDir, Constants.KIT_AFTER_FILE_NAME);
        try
        {
            fileAfter.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(fileAfter),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER);
            Cursor curCSV = dbConnector.getKitAfterConnections("_id");
            curCSV.moveToFirst();
            while(!curCSV.isAfterLast())
            {
                String arrStr[] = {
                        curCSV.getString(0), //id
                        curCSV.getString(1), //barcode
                        curCSV.getString(2), //brand
                        curCSV.getString(3), //brand_catno
                        curCSV.getString(4), //scale
                        curCSV.getString(5), //after_name
                        curCSV.getString(6), //desc
                        curCSV.getString(7), //after_original name
                        curCSV.getString(8), //category - tag
                        curCSV.getString(9), //collection
                        curCSV.getString(10), //send status
                        curCSV.getString(11), // online id
                        curCSV.getString(12), // boxart uri
                        curCSV.getString(13), //boxart url
                        curCSV.getString(14), //is deleted !!
                };
                csvWrite.writeNext(arrStr);
                curCSV.moveToNext();
            }
            csvWrite.close();
            curCSV.close();
            Helper.encrypt(fileAfter);
        }
        catch(Exception sqlEx)
        {
            Log.e("Kit+After Backup failed", sqlEx.getMessage(), sqlEx);
        }
    }

    private void restoreKitAfter(File sdPath){
        File aftermarketFile = new File(sdPath, Constants.KIT_AFTER_FILE_NAME);

        try {
            Helper.decrypt(aftermarketFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Очищаем базу
        dbConnector.clearTable(DbConnector.TABLE_KIT_AFTER_CONNECTIONS);
        try {
            CSVReader reader = new CSVReader(new FileReader(aftermarketFile));
            String[] colums;
            ContentValues cv = new ContentValues();
            while ((colums = reader.readNext()) != null) {
                cv.put(COLUMN_ID, colums[0]);
                cv.put(COLUMN_SCALE, colums[1]);
                cv.put(KIT_AFTER_KITID, colums[2]);
                cv.put(KIT_AFTER_AFTERID, colums[3]);
                cv.put(KIT_AFTER_LISTNAME, colums[4]);
                cv.put(KIT_AFTER_KITNAME, colums[5]);
                cv.put(KIT_AFTER_KITBRAND, colums[6]);
                cv.put(KIT_AFTER_KITCATNO, colums[7]);//!
                // если отличается
                cv.put(KIT_AFTER_KITBARCODE, colums[8]);
                cv.put(KIT_AFTER_KITPROTOTYPE, colums[9]);
                cv.put(KIT_AFTER_AFTERNAME, colums[10]);
                cv.put(KIT_AFTER_AFTERBRAND, colums[11]);
                //заметки? LOCAL?
                cv.put(KIT_AFTER_AFTERCATNO, colums[12]);
                cv.put(KIT_AFTER_AFTERBARCODE, colums[13]);
                cv.put(KIT_AFTER_AFTERDESIGNEDFOR, colums[14]);
                dbConnector.addAftersToKits(cv);
            }
            Helper.encrypt(aftermarketFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//////////// KITSTASHER 0.8 /////////////
    private void restoreOldDb() {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getActivity(), R.string.sdcard_failed, Toast.LENGTH_SHORT).show();
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + "Kitstasher");

        restoreOldKitsDb(sdPath);
        restoreOldBrandsDb(sdPath);
        Toast.makeText(getActivity(), "Импортировано " + dbConnector.getAllData().getCount() + " записей", Toast.LENGTH_SHORT).show();
    }

    private void restoreOldKitsDb(File sdPath) {// TODO: 28.08.2017 убрать в 0.8.2!!

        File sdFile = new File(sdPath, "kits.csv");

        try {
            Helper.decrypt(sdFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Очищаем базу
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        dbConnector.clearTable(DbConnector.TABLE_KITS);
        try {
            dbConnector = new DbConnector(getActivity());
            dbConnector.open();
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                String[] colums = str.split(",");
                ContentValues cv = new ContentValues();
                cv.put(COLUMN_BARCODE, colums[0].trim()); //barcode
                cv.put(COLUMN_BRAND, colums[1].trim());//brand
                cv.put(COLUMN_BRAND_CATNO, colums[2].trim());//brand_catno
                cv.put(COLUMN_SCALE, colums[3].trim());//scale INT
                cv.put(COLUMN_KIT_NAME, colums[4].trim());//kitname
                cv.put(COLUMN_DESCRIPTION, colums[5].trim());//desc
                cv.put(COLUMN_ORIGINAL_KIT_NAME, colums[6].trim());//original name
                cv.put(COLUMN_CATEGORY, colums[7].trim());//category - tag
                cv.put(COLUMN_COLLECTION, colums[8].trim());//collection
                cv.put(COLUMN_SEND_STATUS, colums[9].trim());//send status
                cv.put(COLUMN_ID_ONLINE, colums[10]); // cloud id
                cv.put(COLUMN_BOXART_URI, colums[11]); // local boxart image file
                cv.put(COLUMN_BOXART_URL, colums[12].trim());//boxart url
//                cv.put(DbConnector.COLUMN_IS_DELETED, colums[13].trim());//is deleted
                if(!colums[14].isEmpty()) {
                    cv.put(COLUMN_DATE, colums[14].trim());//date added
                }else{
                    cv.put(COLUMN_DATE, "");//date added
                }

                cv.put(COLUMN_YEAR, ""); //// TODO: 30.08.2017 проверить колонки
                cv.put(COLUMN_PURCHASE_DATE, "");
                cv.put(COLUMN_PRICE, 0);
                cv.put(COLUMN_QUANTITY, 1);
                cv.put(COLUMN_NOTES, "");
                cv.put(COLUMN_CURRENCY, "");//валюта
                cv.put(COLUMN_PURCHASE_PLACE, "");//валюта

                dbConnector = new DbConnector(getActivity());
                dbConnector.open();
                dbConnector.addKitRec(cv);
            }
            Helper.encrypt(sdFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restoreOldBrandsDb(File sdPath) {
        File sdBrandsFile = new File(sdPath, "brands.csv");
        try {
            Helper.decrypt(sdBrandsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Очищаем базу
        // TODO: 17.08.2017 Сделать бэкап на случай сбоя записи?
        dbConnector.clearTable(DbConnector.TABLE_BRANDS);
        try {

            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdBrandsFile));
            String str = "";
            // читаем содержимое

            while ((str = br.readLine()) != null) {
                String[] colums = str.split(",");
                ContentValues cv = new ContentValues();
                cv.put(DbConnector.BRANDS_COLUMN_BRAND, colums[0].trim());
                dbConnector.addBrand(cv);
            }
            Helper.encrypt(sdBrandsFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////// SHOPS ////////////////
    private void backupShops(File exportDir) {
        File fileShops = new File(exportDir, Constants.MYSHOPS_FILE_NAME);
        try
        {
            fileShops.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(fileShops),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER);
            Cursor curCSV = dbConnector.getShops("_id");
            curCSV.moveToFirst();
            while(!curCSV.isAfterLast())
            {
                String arrStr[] ={
                        curCSV.getString(0), //id
                        curCSV.getString(1), //name
                        curCSV.getString(2), //desc
                        curCSV.getString(3), //url
                        curCSV.getString(4), //contact
                        curCSV.getString(5)  //rating int
                };
                csvWrite.writeNext(arrStr);
                curCSV.moveToNext();
            }
            csvWrite.close();
            curCSV.close();
        }
        catch(Exception sqlEx)
        {
            Log.e("Shops backup failed", sqlEx.getMessage(), sqlEx);
        }
    }

    private void restoreShops(File sdPath) {
        File shopsFile = new File(sdPath, Constants.MYSHOPS_FILE_NAME);
        dbConnector.clearTable(DbConnector.TABLE_MYSHOPS);
        try {
            BufferedReader br = new BufferedReader(new FileReader(shopsFile));
            String str = "";
            while ((str = br.readLine()) != null) {
                String[] colums = str.split(",");
                ContentValues cv = new ContentValues();
                cv.put(DbConnector.COLUMN_ID, colums[0].trim());
                cv.put(DbConnector.MYSHOPS_COLUMN_SHOP_NAME, colums[1].trim());
                cv.put(DbConnector.MYSHOPS_COLUMN_SHOP_DESCRIPTION, colums[2].trim());
                cv.put(DbConnector.MYSHOPS_COLUMN_SHOP_URL, colums[3].trim());
                cv.put(DbConnector.MYSHOPS_COLUMN_SHOP_CONTACT, colums[4].trim());
                cv.put(DbConnector.MYSHOPS_COLUMN_SHOP_RATING, colums[5].trim());
                dbConnector.addShop(cv);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //////////////// KITS //////////////////

    private void backupKits(File exportDir) {
        File fileKits = new File(exportDir, Constants.KITS_FILE_NAME);
        try
        {
            fileKits.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(fileKits),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER);
            Cursor curCSV = dbConnector.getAllData("_id");
            curCSV.moveToFirst();
            while(!curCSV.isAfterLast())
            {
                String arrStr[] = {
                        curCSV.getString(0), //id
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
                        curCSV.getString(15), //date !!

                        curCSV.getString(16), //year !!
                        curCSV.getString(17), //date !!
                        curCSV.getString(18), //date !!
                        curCSV.getString(19), //date !!
                        curCSV.getString(20), //date !!
                        curCSV.getString(21), //date !!
                        curCSV.getString(22)
                };
                csvWrite.writeNext(arrStr);
                curCSV.moveToNext();
            }
            csvWrite.close();
            curCSV.close();
            Helper.encrypt(fileKits);
        }
        catch(Exception sqlEx)
        {
            Log.e("Kits backup failed", sqlEx.getMessage(), sqlEx);
        }
    }

    private void restoreKits(File sdPath) {
        File sdFile = new File(sdPath, Constants.KITS_FILE_NAME);
        try {
            Helper.decrypt(sdFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Очищаем базу
        dbConnector.clearTable(DbConnector.TABLE_KITS);
        try {
            CSVReader reader = new CSVReader(new FileReader(sdFile));
            String[] colums;
            ContentValues cv = new ContentValues();
            while ((colums = reader.readNext()) != null) {
                cv.put(COLUMN_ID, colums[0]);
                cv.put(COLUMN_BARCODE, colums[1]);
                cv.put(COLUMN_BRAND, colums[2]);
                cv.put(COLUMN_BRAND_CATNO, colums[3]);
                cv.put(COLUMN_SCALE, colums[4]);
                cv.put(COLUMN_KIT_NAME, colums[5]);
                cv.put(COLUMN_DESCRIPTION, colums[6]);
                cv.put(COLUMN_ORIGINAL_KIT_NAME, colums[7]);
                cv.put(COLUMN_CATEGORY, colums[8]);
                cv.put(COLUMN_COLLECTION, colums[9]);
                cv.put(COLUMN_SEND_STATUS, colums[10]);
                cv.put(COLUMN_ID_ONLINE, colums[11]);
                cv.put(COLUMN_BOXART_URI, colums[12]);
                cv.put(COLUMN_BOXART_URL, colums[13]);
                cv.put(COLUMN_IS_DELETED, colums[14]);
                cv.put(COLUMN_DATE, colums[15]);
                cv.put(COLUMN_YEAR, colums[16]);
                cv.put(COLUMN_PURCHASE_DATE, colums[17]);
                cv.put(COLUMN_PRICE, colums[18]);
                cv.put(COLUMN_QUANTITY, colums[19]);
                cv.put(COLUMN_NOTES, colums[20]);
                cv.put(COLUMN_CURRENCY, colums[21]);
                cv.put(COLUMN_PURCHASE_PLACE, colums[22]);
                dbConnector.addKitRec(cv);
            }
            Helper.encrypt(sdFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ///////////// BRANDS ////////////////
    private void backupBrands(File exportDir) {
        File fileBrands = new File(exportDir, Constants.BRANDS_FILE_NAME);
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
                String arrStr[] = {
                        curCSV.getString(0),
                        curCSV.getString(1)
                };
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        }
        catch(Exception sqlEx)
        {
            Log.e("Brands backup failed", sqlEx.getMessage(), sqlEx);
        }
    }

    private void restoreBrands(File sdPath) {
        File sdBrandsFile = new File(sdPath, Constants.BRANDS_FILE_NAME);

        dbConnector.clearTable(DbConnector.TABLE_BRANDS);

        try {
            BufferedReader br = new BufferedReader(new FileReader(sdBrandsFile));
            String str = "";
            while ((str = br.readLine()) != null) {
                String[] colums = str.split(",");
                ContentValues cv = new ContentValues();
                cv.put(DbConnector.BRANDS_COLUMN_ID, colums[0].trim());
                cv.put(DbConnector.BRANDS_COLUMN_BRAND, colums[1].trim());
                dbConnector.addBrand(cv);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ////////////// MY LISTS ///////////////

    private void backupLists(File exportDir) {
        File fileLists = new File(exportDir, Constants.LISTS_FILE_NAME);
        try
        {
            fileLists.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(fileLists),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER);
            Cursor listCur = dbConnector.getAllLists("_id DESC");
            listCur.moveToFirst();
            while(!listCur.isAfterLast())
            {
                //Which column you want to exprort
                String arrStr[] ={
                        listCur.getString(0), //id
                        listCur.getString(1), //listname
                        listCur.getString(2), //date created
                };
                csvWrite.writeNext(arrStr);
                listCur.moveToNext();
            }
            csvWrite.close();
            listCur.close();
        }
        catch(Exception sqlEx)
        {
            Log.e("Lists backup failed", sqlEx.getMessage(), sqlEx);
        }
    }

    private void restoreLists(File sdPath) {
        File sdListsFile = new File(sdPath, Constants.LISTS_FILE_NAME);
        //Очищаем базу
        dbConnector.clearTable(DbConnector.TABLE_MYLISTS);
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdListsFile));
            String str = "";
            // читаем содержимое
            while ((str = br.readLine()) != null) {
                String[] colums = str.split(",");
                ContentValues cv = new ContentValues();
                cv.put(DbConnector.MYLISTS_COLUMN_ID, colums[0].trim()); //id
                cv.put(DbConnector.MYLISTS_COLUMN_LIST_NAME, colums[1].trim()); //listname
                cv.put(DbConnector.MYLISTS_COLUMN_DATE, colums[2].trim()); //date
                dbConnector.addList(cv);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /////////// LISTSITEMS //////////////

    private void backupListItems(File exportDir) {
        File fileListItems = new File(exportDir, Constants.LISTITEMS_FILE_NAME);
        try
        {
            fileListItems.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(fileListItems),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER);
            Cursor curCSV = dbConnector.getAllListsItems("_id");
            curCSV.moveToFirst();
            while(!curCSV.isAfterLast())
            {
                String arrStr[] = {
                        curCSV.getString(0), //id
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
                        curCSV.getString(15), //date !!

                        curCSV.getString(16), //year !!
                        curCSV.getString(17), //date !!
                        curCSV.getString(18), //date !!
                        curCSV.getString(19), //date !!
                        curCSV.getString(20), //date !!
                        curCSV.getString(21), //date !!
                        curCSV.getString(22),
                        curCSV.getString(23)
                };
                csvWrite.writeNext(arrStr);
                curCSV.moveToNext();
            }
            csvWrite.close();
            curCSV.close();
            Helper.encrypt(fileListItems);
        }
        catch(Exception sqlEx)
        {
            Log.e("ListItems backup failed", sqlEx.getMessage(), sqlEx);
        }
    }

    private void restoreListsItems(File sdPath) {
        File sdListItemsFile = new File(sdPath, Constants.LISTITEMS_FILE_NAME);
        try {
            Helper.decrypt(sdListItemsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Очищаем базу
        dbConnector.clearTable(DbConnector.TABLE_MYLISTSITEMS);
        try {
            CSVReader reader = new CSVReader(new FileReader(sdListItemsFile));
            String[] colums;
            ContentValues cv = new ContentValues();
            while ((colums = reader.readNext()) != null) {
                cv.put(COLUMN_ID, colums[0]);
                cv.put(COLUMN_BARCODE, colums[1]);
                cv.put(COLUMN_BRAND, colums[2]);
                cv.put(COLUMN_BRAND_CATNO, colums[3]);
                cv.put(COLUMN_SCALE, colums[4]);
                cv.put(COLUMN_KIT_NAME, colums[5]);
                cv.put(COLUMN_DESCRIPTION, colums[6]);
                cv.put(COLUMN_ORIGINAL_KIT_NAME, colums[7]);
                cv.put(COLUMN_CATEGORY, colums[8]);
                cv.put(COLUMN_COLLECTION, colums[9]);
                cv.put(COLUMN_SEND_STATUS, colums[10]);
                cv.put(COLUMN_ID_ONLINE, colums[11]);
                cv.put(COLUMN_BOXART_URI, colums[12]);
                cv.put(COLUMN_BOXART_URL, colums[13]);
                cv.put(COLUMN_IS_DELETED, colums[14]);
                cv.put(COLUMN_DATE, colums[15]);
                cv.put(COLUMN_YEAR, colums[16]);
                cv.put(COLUMN_PURCHASE_DATE, colums[17]);
                cv.put(COLUMN_PRICE, colums[18]);
                cv.put(COLUMN_QUANTITY, colums[19]);
                cv.put(COLUMN_NOTES, colums[20]);
                cv.put(COLUMN_CURRENCY, colums[21]);
                cv.put(COLUMN_PURCHASE_PLACE, colums[22]);
                cv.put(MYLISTS_COLUMN_LIST_NAME, colums[23]);
                dbConnector.addListItem(cv);
            }
            Helper.encrypt(sdListItemsFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /////////////////////

    private void setDefaultCurrency(String currency){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.DEFAULT_CURRENCY, currency).apply();
    }
}
