package com.example.kitstasher.fragment;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import static com.example.kitstasher.other.DbConnector.COLUMN_AFTERMARKET_NAME;
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
import static com.example.kitstasher.other.DbConnector.COLUMN_MEDIA;
import static com.example.kitstasher.other.DbConnector.COLUMN_NOTES;
import static com.example.kitstasher.other.DbConnector.COLUMN_ORIGINAL_NAME;
import static com.example.kitstasher.other.DbConnector.COLUMN_PRICE;
import static com.example.kitstasher.other.DbConnector.COLUMN_PURCHASE_DATE;
import static com.example.kitstasher.other.DbConnector.COLUMN_PURCHASE_PLACE;
import static com.example.kitstasher.other.DbConnector.COLUMN_QUANTITY;
import static com.example.kitstasher.other.DbConnector.COLUMN_SCALE;
import static com.example.kitstasher.other.DbConnector.COLUMN_SCALEMATES_URL;
import static com.example.kitstasher.other.DbConnector.COLUMN_SEND_STATUS;
import static com.example.kitstasher.other.DbConnector.COLUMN_STATUS;
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
import static com.example.kitstasher.other.DbConnector.MYLISTSITEMS_LISTNAME;

/**
 * Created by Алексей on 03.05.2017.
 * Import and export database. Repair of boxart image links.
 */

public class SettingsOptionsFragment extends Fragment implements View.OnClickListener {
    private DbConnector dbConnector;
    private View view;
    private EditText etNewCurrency;
    private ProgressBar progressBarDb,
            progressBarExport;
    private Spinner spDefaultCurrency;

    public SettingsOptionsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_options, container, false);

        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        initUI();
        return view;
    }

    private void initUI() {
        progressBarDb = view.findViewById(R.id.pbOptionsDb);
        progressBarDb.setVisibility(View.GONE);
        progressBarExport = view.findViewById(R.id.pbOptionsExport);
        progressBarExport.setVisibility(View.GONE);
        Button btnBackup = view.findViewById(R.id.btnBackup);
        btnBackup.setOnClickListener(this);
        Button btnRestore = view.findViewById(R.id.btnRestore);
        btnRestore.setOnClickListener(this);
        Button btnRepairImages = view.findViewById(R.id.btnRepairImages);
        btnRepairImages.setOnClickListener(this);
        Button btnSetDefault = view.findViewById(R.id.btnSetDefault);
        btnSetDefault.setOnClickListener(this);
        Button btnAddNewCurrency = view.findViewById(R.id.btnAddCurrency);
        btnAddNewCurrency.setOnClickListener(this);
        etNewCurrency = view.findViewById(R.id.etNewCurrency);
        spDefaultCurrency = view.findViewById(R.id.spDefaultCurrency);
        loadCurrencySpinner();
        Button btnRestoreFromCloud = view.findViewById(R.id.btnRestoreFromCloud);
        btnRestoreFromCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarDb.setVisibility(View.VISIBLE);
                restoreFromCloud();
            }
        });
        CheckBox cbCloudMode = view.findViewById(R.id.cbCloudMode);
        cbCloudMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(MyConstants.CLOUD_MODE, b).apply();
            }
        });
    }

    private void restoreFromCloud() {

        dbConnector.clearTable(DbConnector.TABLE_KITS);
        dbConnector.clearTable(DbConnector.TABLE_KIT_AFTER_CONNECTIONS);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String ownerId = sharedPreferences.getString(MyConstants.USER_ID_PARSE, "");//todo
        ParseQuery<ParseObject> ownerIds = ParseQuery.getQuery("Stash");
        ownerIds.whereEqualTo(MyConstants.PARSE_OWNERID, ownerId);

        String idType = sharedPreferences.getString(MyConstants.USER_IDTYPE, "");//todo
        ParseQuery<ParseObject> idSelect = ParseQuery.getQuery("Stash");
        idSelect.whereEqualTo(MyConstants.PARSE_IDTYPE, idType);

        ParseQuery<ParseObject> notDeleted = ParseQuery.getQuery("Stash");
        notDeleted.whereNotEqualTo(MyConstants.PARSE_DELETED, true);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(ownerIds);
        queries.add(idSelect);
        queries.add(notDeleted);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                for (ParseObject object : results) {
                    Kit kit = new Kit.KitBuilder()
                            .hasOnlineId(object.getObjectId())
                            .hasBrand(object.getString(MyConstants.PARSE_BRAND))
                            .hasBrand_catno(object.getString(MyConstants.PARSE_BRAND_CATNO))
                            .hasKit_name(object.getString(MyConstants.PARSE_KITNAME))
                            .hasScale(object.getInt(MyConstants.PARSE_SCALE))
                            .hasCategory(object.getString(MyConstants.PARSE_CATEGORY))
                            .hasDescription(object.getString(MyConstants.PARSE_DESCRIPTION))
//                            .hasPrototype(object.getString(MyConstants.PARSE_PROTOTYPE))//not in use
                            .hasKit_noeng_name(object.getString(MyConstants.PARSE_NOENGNAME))
                            .hasBoxart_url(object.getString(MyConstants.PARSE_BOXART_URL))
                            .hasBarcode(object.getString(MyConstants.PARSE_BARCODE))
                            .hasScalemates_url(object.getString(MyConstants.PARSE_SCALEMATES))
                            .hasYear(object.getString(MyConstants.PARSE_YEAR))
                            .build();
                    dbConnector.addKitRec(kit);
                }
                progressBarDb.setVisibility(View.GONE);
                Toast.makeText(getActivity(), getString(R.string.Restored), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAddCurrency:
                String cur = etNewCurrency.getText().toString();
                if (!Helper.isBlank(cur)) {
                    dbConnector.addCurrency(cur);
                    loadCurrencySpinner();
                    etNewCurrency.setText(MyConstants.EMPTY);
                }
                break;
            case R.id.btnSetDefault:
                setDefaultCurrency(spDefaultCurrency.getSelectedItem().toString());
                break;
            case R.id.btnBackup:
                progressBarDb.setVisibility(View.VISIBLE);
                backupDb();
                progressBarDb.setVisibility(View.GONE);
                Toast.makeText(getActivity(), getString(R.string.Saved), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnRestore:
                progressBarDb.setVisibility(View.VISIBLE);
                restoreDb();
                progressBarDb.setVisibility(View.GONE);
                break;
            case R.id.btnRepairImages:
                String[] items = new String[]{"TXT"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.choose_output_format);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item){
                            case 0:
                                exportStashTo("TXT");
                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
        }
    }

    private void loadCurrencySpinner() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String defCurrency = sharedPreferences.getString(MyConstants.DEFAULT_CURRENCY, "USD");
        Cursor currCursor = dbConnector.getAllFromTable(DbConnector.TABLE_CURRENCIES,
                DbConnector.CURRENCIES_COLUMN_CURRENCY);
        currCursor.moveToFirst();
        String[] currencies = new String[currCursor.getCount()];
        for (int i = 0; i < currCursor.getCount(); i++) {
            currencies[i] = currCursor.getString(1);
            currCursor.moveToNext();
        }
        ArrayAdapter currencyAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, currencies);
        spDefaultCurrency.setAdapter(currencyAdapter);
        int spCurrencyPosition = currencyAdapter.getPosition(defCurrency);
        spDefaultCurrency.setSelection(spCurrencyPosition);
    }


    private void exportStashTo(String mode){
        progressBarExport.setVisibility(View.VISIBLE);
        final String m = mode;
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                File exportDir = getActivity().getExternalFilesDir("export");
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }
                switch (m){
                    case "TXT":
                        try {
                            File fileStash = new File(exportDir, "collection.txt");
                            fileStash.delete();
                            fileStash.createNewFile();
                            FileOutputStream fOut = null;
                            try {
                                fOut = new FileOutputStream(fileStash, true);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            OutputStreamWriter osw = new OutputStreamWriter(fOut);
                            osw.write(getString(R.string.mystash_header) + "\n");
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
                        progressBarExport.setVisibility(View.GONE);
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        Uri txtUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory()
                                + MyConstants.APP_FOLDER + "MyStash.txt"));

                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, txtUri);
                        startActivity(Intent.createChooser(sharingIntent, getString(R.string.save_file_using)));
                        break;
                }
                return null;
            }
        };
        asyncTask.execute();
    }

    private void backupDb() {

//        File exportDir = new File(Environment.getExternalStorageDirectory(),"Kitstasher");
        File exportDir = getActivity().getExternalFilesDir("backup");
        if (!(exportDir != null ? exportDir.exists() : false)) {
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
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getActivity(), R.string.sdcard_failed, Toast.LENGTH_SHORT).show();
        }
        File sdPath = getActivity().getExternalFilesDir("backup");

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
        File fileAfter = new File(exportDir, MyConstants.AFTER_FILE_NAME);
        try {
            fileAfter.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(fileAfter),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER);
            Cursor curCSV = dbConnector.getAllAftermarket("_id");
            curCSV.moveToFirst();
            while(!curCSV.isAfterLast()) {
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
                        curCSV.getString(23), //listname
                        curCSV.getString(24),
                        curCSV.getString(25),
                        curCSV.getString(26),
                        curCSV.getString(27)
                };
                csvWrite.writeNext(arrStr);
                curCSV.moveToNext();
            }
            csvWrite.close();
            curCSV.close();
            Helper.encrypt(fileAfter);
        } catch(Exception sqlEx) {
            Log.e("Aftermarket Backup", sqlEx.getMessage(), sqlEx);
        }
    }

    private void restoreAftermarket(File sdPath) {
        File aftermarketFile = new File(sdPath, MyConstants.AFTER_FILE_NAME);

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
                cv.put(COLUMN_ID, colums[0]); // Локальный ключ -0
                cv.put(COLUMN_BARCODE, colums[1]); // штрихкод NOBARCODE по умолчанию для garage kit? - 1
                cv.put(COLUMN_BRAND, colums[2]); // производитель - 2
                cv.put(COLUMN_BRAND_CATNO, colums[3]); //каталожный номер набора - 3
                cv.put(COLUMN_SCALE, colums[4]); //масштаб - 4
                cv.put(COLUMN_AFTERMARKET_NAME, colums[5]); //название набора - 5
                cv.put(COLUMN_DESCRIPTION, colums[6]); //описание, продолжение названия - 6
                cv.put(COLUMN_ORIGINAL_NAME, colums[7]); //название на оригинальном языке, - 7
                cv.put(COLUMN_CATEGORY, colums[8]); //тег (самолет, корабль, и тд - 8
                cv.put(COLUMN_COLLECTION, colums[9]); //коллекция - для группировки и других функций - 9
                cv.put(COLUMN_SEND_STATUS, colums[10]); //для отслеживания офлайн отправок LOCAL - 10
                cv.put(COLUMN_ID_ONLINE, colums[11]); //номер в онлайновой базе, может пригодится - 11
                cv.put(COLUMN_BOXART_URI, colums[12]); //локальная ссылка на файл боксарта LOCAL - 12
                cv.put(COLUMN_BOXART_URL, colums[13]); //интернет-ссылка на боксарт для Glide LOCAL - 13
                cv.put(COLUMN_IS_DELETED, colums[14]); // - 14
                cv.put(COLUMN_DATE, colums[15]);// дата добавления? LOCAL? - 15
                cv.put(COLUMN_YEAR, colums[16]); // год выпуска набора - 16
                cv.put(COLUMN_PURCHASE_DATE, colums[17]); //дата покупки -17
                cv.put(COLUMN_PRICE, colums[18]); //цена -18
                cv.put(COLUMN_QUANTITY, colums[19]); //количество - 19
                cv.put(COLUMN_NOTES, colums[20]); //заметки - 20
                cv.put(COLUMN_CURRENCY, colums[21]); //валюта - 21
                cv.put(MYLISTSITEMS_LISTNAME, colums[22]); //22
                cv.put(COLUMN_STATUS, colums[23]); //начат/использован //23
                cv.put(COLUMN_MEDIA, colums[24]); //материал - 24
                cv.put(COLUMN_SCALEMATES_URL, colums[25]); // - 25
                cv.put(KIT_AFTER_AFTERDESIGNEDFOR, colums[26]); //26
                cv.put(COLUMN_PURCHASE_PLACE, colums[27]); //место покупки - 27
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
        File fileAfter = new File(exportDir, MyConstants.KIT_AFTER_FILE_NAME);
        try {
            fileAfter.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(fileAfter),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER);
            Cursor curCSV = dbConnector.getKitAfterConnections("_id");
            curCSV.moveToFirst();
            while(!curCSV.isAfterLast()) {
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
        } catch(Exception sqlEx) {
            Log.e("Kit+After Backup failed", sqlEx.getMessage(), sqlEx);
        }
    }

    private void restoreKitAfter(File sdPath){
        File aftermarketFile = new File(sdPath, MyConstants.KIT_AFTER_FILE_NAME);

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

    ////////// SHOPS ////////////////
    private void backupShops(File exportDir) {
        File fileShops = new File(exportDir, MyConstants.MYSHOPS_FILE_NAME);
        try {
            fileShops.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(fileShops),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER);
            Cursor curCSV = dbConnector.getShops("_id");
            curCSV.moveToFirst();
            while(!curCSV.isAfterLast()) {
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
        } catch(Exception sqlEx) {
            Log.e("Shops backup failed", sqlEx.getMessage(), sqlEx);
        }
    }

    private void restoreShops(File sdPath) {
        File shopsFile = new File(sdPath, MyConstants.MYSHOPS_FILE_NAME);
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
        File fileKits = new File(exportDir, MyConstants.KITS_FILE_NAME);
        try {
            fileKits.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(fileKits),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER);
            Cursor curCSV = dbConnector.getAllData("_id");
            curCSV.moveToFirst();
            while(!curCSV.isAfterLast()) {
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
                        curCSV.getString(23),
                        curCSV.getString(24),
                        curCSV.getString(25)
                };
                csvWrite.writeNext(arrStr);
                curCSV.moveToNext();
            }
            csvWrite.close();
            curCSV.close();
            Helper.encrypt(fileKits);
        } catch(Exception sqlEx) {
            Log.e("Kits backup failed", sqlEx.getMessage(), sqlEx);
        }
    }

    private void restoreKits(File sdPath) {
        File sdFile = new File(sdPath, MyConstants.KITS_FILE_NAME);
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
                cv.put(COLUMN_ID, colums[0]); // Локальный ключ -0
                cv.put(COLUMN_BARCODE, colums[1]); // штрихкод NOBARCODE по умолчанию для garage kit? - 1
                cv.put(COLUMN_BRAND, colums[2]); // производитель - 2
                cv.put(COLUMN_BRAND_CATNO, colums[3]); //каталожный номер набора - 3
                cv.put(COLUMN_SCALE, colums[4]); //масштаб - 4
                cv.put(COLUMN_KIT_NAME, colums[5]); //название набора - 5
                cv.put(COLUMN_DESCRIPTION, colums[6]); //описание, продолжение названия - 6
                cv.put(COLUMN_ORIGINAL_NAME, colums[7]); //название на оригинальном языке, - 7
                cv.put(COLUMN_CATEGORY, colums[8]); //тег (самолет, корабль, и тд - 8
                cv.put(COLUMN_COLLECTION, colums[9]); //коллекция - для группировки и других функций - 9
                cv.put(COLUMN_SEND_STATUS, colums[10]); //для отслеживания офлайн отправок LOCAL - 10
                cv.put(COLUMN_ID_ONLINE, colums[11]); //номер в онлайновой базе, может пригодится - 11
                cv.put(COLUMN_BOXART_URI, colums[12]); //локальная ссылка на файл боксарта LOCAL - 12
                cv.put(COLUMN_BOXART_URL, colums[13]); //интернет-ссылка на боксарт для Glide LOCAL - 13
                cv.put(COLUMN_IS_DELETED, colums[14]); // - 14
                cv.put(COLUMN_DATE, colums[15]); // дата добавления? LOCAL? - 15
                cv.put(COLUMN_YEAR, colums[16]); // год выпуска набора - 16
                cv.put(COLUMN_SCALEMATES_URL, colums[17]); //материал - 17
                cv.put(COLUMN_PURCHASE_DATE, colums[18]); //дата покупки -18
                cv.put(COLUMN_PRICE, colums[19]); //цена -19
                cv.put(COLUMN_QUANTITY, colums[20]); //количество - 20
                cv.put(COLUMN_NOTES, colums[21]); //заметки - 21
                cv.put(COLUMN_CURRENCY, colums[22]); //валюта - 22
                cv.put(COLUMN_PURCHASE_PLACE, colums[23]); //место покупки - 23
                cv.put(COLUMN_STATUS, colums[24]); //начат/использован - 24
                cv.put(COLUMN_MEDIA, colums[25]); //материал - 25
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
        File fileBrands = new File(exportDir, MyConstants.BRANDS_FILE_NAME);
        try {
            fileBrands.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(fileBrands),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER);
            Cursor curCSV = dbConnector.getBrands("_id");
            curCSV.moveToFirst();
            while(curCSV.moveToNext()) {
                String arrStr[] = {
                        curCSV.getString(0),
                        curCSV.getString(1)
                };
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        } catch(Exception sqlEx) {
            Log.e("Brands backup failed", sqlEx.getMessage(), sqlEx);
        }
    }

    private void restoreBrands(File sdPath) {
        File sdBrandsFile = new File(sdPath, MyConstants.BRANDS_FILE_NAME);

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
        File fileLists = new File(exportDir, MyConstants.LISTS_FILE_NAME);
        try {
            fileLists.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(fileLists),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER);
            Cursor listCur = dbConnector.getLists("_id DESC");
            listCur.moveToFirst();
            while(!listCur.isAfterLast()) {
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
        } catch(Exception sqlEx) {
            Log.e("Lists backup failed", sqlEx.getMessage(), sqlEx);
        }
    }

    private void restoreLists(File sdPath) {
        File sdListsFile = new File(sdPath, MyConstants.LISTS_FILE_NAME);
        dbConnector.clearTable(DbConnector.TABLE_MYLISTS);
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdListsFile));
            String str;
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
        File fileListItems = new File(exportDir, MyConstants.LISTITEMS_FILE_NAME);
        try {
            fileListItems.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(fileListItems),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER);
            Cursor curCSV = dbConnector.getAllListsItems("_id");
            curCSV.moveToFirst();
            while(!curCSV.isAfterLast()) {
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
                        curCSV.getString(23),
                        curCSV.getString(24),
                        curCSV.getString(25),
                        curCSV.getString(26)
                };
                csvWrite.writeNext(arrStr);
                curCSV.moveToNext();
            }
            csvWrite.close();
            curCSV.close();
            Helper.encrypt(fileListItems);
        } catch(Exception sqlEx) {
            Log.e("ListItems backup failed", sqlEx.getMessage(), sqlEx);
        }
    }

    private void restoreListsItems(File sdPath) {
        File sdListItemsFile = new File(sdPath, MyConstants.LISTITEMS_FILE_NAME);
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
                cv.put(COLUMN_BRAND_CATNO, colums[3]);//каталожный номер набора - 3
                cv.put(COLUMN_SCALE, colums[4]);//масштаб - 4
                cv.put(COLUMN_KIT_NAME, colums[5]);//название набора - 5
                cv.put(COLUMN_DESCRIPTION, colums[6]);//описание, продолжение названия - 6
                cv.put(COLUMN_ORIGINAL_NAME, colums[7]);//название на оригинальном языке, - 7
                cv.put(COLUMN_CATEGORY, colums[8]);//тег (самолет, корабль, и тд - 8
                cv.put(COLUMN_COLLECTION, colums[9]);//коллекция - для группировки и других функций - 9
                cv.put(COLUMN_SEND_STATUS, colums[10]);//для отслеживания офлайн отправок LOCAL - 10
                cv.put(COLUMN_ID_ONLINE, colums[11]);//номер в онлайновой базе, может пригодится - 11
                cv.put(COLUMN_BOXART_URI, colums[12]);//локальная ссылка на файл боксарта LOCAL - 12
                cv.put(COLUMN_BOXART_URL, colums[13]);//интернет-ссылка на боксарт для Glide LOCAL - 13
                cv.put(COLUMN_IS_DELETED, colums[14]);// - 14
                cv.put(COLUMN_DATE, colums[15]);// дата добавления в базу LOCAL? - 15
                cv.put(COLUMN_YEAR, colums[16]); // год выпуска набора - 16
                cv.put(COLUMN_PURCHASE_DATE, colums[17]); //дата покупки -17
                cv.put(COLUMN_PRICE, colums[18]); //цена в копейках-18
                cv.put(COLUMN_QUANTITY, colums[19]); //количество - 19
                cv.put(COLUMN_NOTES, colums[20]); //заметки - 20
                cv.put(COLUMN_CURRENCY, colums[21]); //валюта - 21
                cv.put(COLUMN_PURCHASE_PLACE, colums[22]); //место покупки - 22
                cv.put(COLUMN_STATUS, colums[23]); //начат/использован - 23
                cv.put(COLUMN_MEDIA, colums[24]); //материал - 24
                cv.put(COLUMN_SCALEMATES_URL, colums[25]); //скейлмейтс -25
                cv.put(MYLISTSITEMS_LISTNAME, colums[26]); // Локальный ключ -26
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

    private void setDefaultCurrency(String currency){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MyConstants.DEFAULT_CURRENCY, currency).apply();
    }

    private void setCloudOn() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(MyConstants.CLOUD_MODE, true).apply();
    }

    private void setCloudOff() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(MyConstants.CLOUD_MODE, false).apply();
    }

    private void setCloudMode(boolean b) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(MyConstants.CLOUD_MODE, b).apply();
    }

    private void setAllOptions(String currency, boolean cloudMode) {
        setDefaultCurrency(currency);
        setCloudMode(cloudMode);
    }

}