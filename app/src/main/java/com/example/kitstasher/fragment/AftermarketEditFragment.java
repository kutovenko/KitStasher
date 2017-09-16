package com.example.kitstasher.fragment;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.SelectDateFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.R.drawable.ic_menu_camera;
import static android.app.Activity.RESULT_OK;
import static java.lang.Integer.parseInt;

/**
 * Created by Алексей on 15.09.2017.
 * Edit and update Aftermarket
 */

public class AftermarketEditFragment extends Fragment implements View.OnClickListener{
    private int position;
    private long afterId;
    private ImageView ivEditorBoxart;
    private EditText etDetFullKitname, etDetFullBrand, etDetFullBrandCatNo, etDetFullScale,
            etDetFullKitNoengname,
            etFullNotes, etFullPrice, etPurchasedFrom;
    private LinearLayout linLayoutAir, linLayoutGround, linLayoutSea, linLayoutSpace, linLayoutCar,
            linLayoutOther, linLayoutFigures, linLayoutFantasy;
    private Button btnSaveEdit, btnCancelEdit, btnDelete, btnRestoreImage, btnAddBoxart, btnClearDate,
            btnAddAftermarket;
    private AppCompatSpinner spKitDescription, spKitYear, spQuantity, spCurrency, spKitMedia, spKitStatus;
    private String categoryTab, category, listname; // для переключения к вкладке. при изменении совпадает с category, иначе то, что было (пришло или сохранено в записи)
    private String brand, catno, kitname;
    private int scale;
    private int quantity;
    private DbConnector dbConnector;
    private Cursor cursor;
    private Uri uri;
    private boolean isRbChanged, isDateChanged;
    private ArrayAdapter quantityAdapter;

    private TextView tvMPurchaseDate;
    private String purchaseDate;
    private String defaultCurrency;
    private View view;

    //работа с камерой
    final static int CAMERA_CAPTURE = 1;
    final static int PIC_CROP = 2;
    private Uri picUri;
    File photoFile;
    String pictureName;
    Context context;
    Bitmap bmBoxartPic;
    ByteArrayOutputStream bytes;
    String size;

    String scaleFilter, brandFilter, kitnameFilter, statusFilter, mediaFilter;

    private ArrayAdapter<String> descriptionAdapter, yearsAdapter, currencyAdapter, statusAdapter,
            mediaAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_after_edit, container, false);
        afterId = getArguments().getLong("after_id");
        context = getActivity();
        dbConnector = new DbConnector(context);
        dbConnector.open();

        initUI();
        receiveArguments();


        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        if (savedInstanceState != null) {
            bmBoxartPic = savedInstanceState.getParcelable("boxartImage");
            ivEditorBoxart.setImageBitmap(bmBoxartPic);
        }

        isRbChanged = false;
        isDateChanged = false;

        /////////////////Работа с камерой

        pictureName = "";

        /////////////// изображение - добавлено

            cursor = dbConnector.getAftermarketByID(afterId);
            cursor.moveToFirst();

        category = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY)); //беру категогию из записи
        /////////////////////// радиокнопки
        setTag(category);
        brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
        etDetFullBrand.setText(brand);
        catno = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO));
        etDetFullBrandCatNo.setText(catno);
        kitname = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME));
        etDetFullKitname.setText(kitname);
        scale = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE));
        etDetFullScale.setText(String.valueOf(scale));
        String pr = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PRICE)) / 100);
        etFullPrice.setText(pr);
        if (cursor.getColumnIndex(DbConnector.COLUMN_PURCHASE_PLACE) != -1) {
            etPurchasedFrom.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_PLACE)));
        }
        etPurchasedFrom.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_PLACE)));

        String[] descriptionItems = new String[]{getString(R.string.kittype),
                getString(R.string.newkit),
                getString(R.string.rebox),
//                getString(R.string.new_tool),
//                getString(R.string.reissue),
//                getString(R.string.changed_parts), getString(R.string.new_decal),
//                getString(R.string.changed_box), getString(R.string.repack)
        };
        descriptionAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, descriptionItems);
        spKitDescription.setAdapter(descriptionAdapter);
        spKitDescription.setSelection(2);

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add(getString(R.string.year));
        for (int i = thisYear; i >= 1930; i--) {
            years.add(Integer.toString(i));
        }
        yearsAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, years);
        spKitYear.setAdapter(yearsAdapter);

        String[] currencies = new String[]{"BYN", "EUR", "RUR", "UAH", "USD"};
        currencyAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, currencies);
        spCurrency.setAdapter(currencyAdapter);

        Integer[] quants = new Integer[]{1,2,3,4,5,6,7,8,9,10};
        quantityAdapter = new ArrayAdapter<Integer>(context,
                android.R.layout.simple_spinner_item, quants);
        spQuantity.setAdapter(quantityAdapter);

        String[] mediaTypes = new String[]{
                getString(R.string.media_other),
                getString(R.string.media_injected),
                getString(R.string.media_shortrun),
                getString(R.string.media_resin),
                getString(R.string.media_vacu),
                getString(R.string.media_paper),
                getString(R.string.media_wood),
                getString(R.string.media_metal),
                getString(R.string.media_3dprint),
                getString(R.string.media_multimedia)
        };
        mediaAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                mediaTypes);
        spKitMedia.setAdapter(mediaAdapter);
        spKitMedia.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_MEDIA)));

        String[] kitStatuses = new String[]{
                getString(R.string.status_new),
                getString(R.string.status_opened),
                getString(R.string.status_started),
                getString(R.string.status_inprogress),
                getString(R.string.status_finished),
                getString(R.string.status_lost_sold)
        };
        statusAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
                kitStatuses);
        spKitStatus.setAdapter(statusAdapter);
        spKitMedia.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_STATUS)));


        if (cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_AFTERMARKET_ORIGINAL_NAME)) != null){
            etDetFullKitNoengname.setText(cursor.getString(cursor.getColumnIndex
                    (DbConnector.COLUMN_AFTERMARKET_ORIGINAL_NAME)));
        }
        if (cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PRICE)) != 0){
            etFullPrice.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex
                    (DbConnector.COLUMN_PRICE))/100));
        }else{
            etFullPrice.setText("");
        }
        if (cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_NOTES)) != null){
            etFullNotes.setText(cursor.getString(cursor.getColumnIndex
                    (DbConnector.COLUMN_NOTES)));
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        defaultCurrency = sharedPreferences.getString(Constants.DEFAULT_CURRENCY, "");

        if (!cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CURRENCY)).equals("")){
            String cr = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CURRENCY));
            setKitCurrency(cr);
        }else{
            setKitCurrency(defaultCurrency);
        }

        if (cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_QUANTITY)) != 0){
            quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_QUANTITY));
            setKitQuantity(quantity);
        }else{
            quantity = 1;
            setKitQuantity(quantity);
        }

        String pd = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_DATE));

        if (!pd.equals("")){
            tvMPurchaseDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_DATE)));
        }else{
            tvMPurchaseDate.setText(R.string.Date_not_set);
        }

        etPurchasedFrom.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_PLACE)));

        String year = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_YEAR));
        setKitYear(year);


        String description = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_DESCRIPTION));
        setKitDescription(description);

        setBoxartImage();

        return view;
    }

    private void receiveArguments() {
        position = getArguments().getInt(Constants.LIST_POSITION);
        afterId = getArguments().getLong("after_id");
        listname = "";
        scaleFilter = getArguments().getString("scaleFilter");
        brandFilter = getArguments().getString("brandFilter");
        kitnameFilter = getArguments().getString("kitnameFilter");
        statusFilter = getArguments().getString("statusFilter");
        mediaFilter = getArguments().getString("mediaFilter");
//
//        mode = Constants.MODE_KIT;
//        if (getArguments().getChar(Constants.EDIT_MODE) != '\u0000'){
//            mode = getArguments().getChar(Constants.EDIT_MODE);
//        }
    }

    private void setKitDescription(String description) {
        if (!description.equals("")) {
            String desc = "0";
            if (!description.equals("")) {
                switch (description) {
                    case "0":
                        desc = "";
                        break;
                    case "1":
                        desc = getString(R.string.newkit);
                        break;
                    case "2":
//                        desc = getString(R.string.changed_parts);
                        desc = getString(R.string.rebox);

                        break;
                    case "3":
//                        desc = getString(R.string.new_decal);
                        desc = getString(R.string.rebox);

                        break;
                    case "4":
//                        desc = getString(R.string.changed_box);
                        desc = getString(R.string.rebox);

                        break;
                    case "5":
//                        desc = getString(R.string.repack);
                        desc = getString(R.string.rebox);

                        break;
                    case "6":
                        desc = "";
                }
            }else{
                desc = getString(R.string.kittype);
            }
            int spDescPosition = descriptionAdapter.getPosition(desc);
            spKitDescription.setSelection(spDescPosition);
        }else{
            spKitDescription.setSelection(0);
        }
    }

    private void setKitYear(String year) {
        if (year.length() == 4 && !year.contains("-")) {
            int spYearPosition = yearsAdapter.getPosition(year);
            spKitYear.setSelection(spYearPosition);
        }else{
            spKitYear.setSelection(0); //оставляем на первой
        }
    }

    private void setKitCurrency(String currency) {
        int spCurrencyPosition = currencyAdapter.getPosition(currency);
        spCurrency.setSelection(spCurrencyPosition);
    }

    private void setKitQuantity(int quantity) {
        if (quantity != 0) {
            spQuantity.setSelection(quantity - 1);
        }else{
            spQuantity.setSelection(0);
        }
    }

    private void setBoxartImage() {
            cursor = dbConnector.getAftermarketByID(afterId);
        cursor.moveToFirst();
        if (cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI)) != null
                && cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI)).length() > 1){

            File imgFile = new  File(Constants.FOLDER_SDCARD_KITSTASHER
                    + cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI)));

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivEditorBoxart.setBackgroundResource(0);
                ivEditorBoxart.setImageBitmap(myBitmap);
                pictureName = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI));
            }
        }else if (cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URL)) != null){
           String boxart_url = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URL));
            ivEditorBoxart.setBackgroundResource(0);
            Glide
                    .with(context)
                    .load(composeUrl(boxart_url))
                    .placeholder(ic_menu_camera)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivEditorBoxart);
        }
    }

    private String composeUrl(String url){
        if (!Helper.isBlank(url)) {
            return Constants.BOXART_URL_PREFIX
                    + url
                    + getSuffix()
                    + Constants.BOXART_URL_POSTFIX;
        }else{
            return "";
        }

    }

    private String getSuffix(){
        String suffix = Constants.BOXART_URL_SMALL;
        SharedPreferences preferences = context.getSharedPreferences(Constants.BOXART_SIZE,
                Context.MODE_PRIVATE);
        if (preferences != null) {
            String temp = preferences.getString("boxart_size","");
            switch (temp){
                case Constants.BOXART_URL_COMPANY_SUFFIX:
                    suffix = "";
                    break;
                case Constants.BOXART_URL_SMALL:
                    suffix = Constants.BOXART_URL_SMALL;
                    break;
                case Constants.BOXART_URL_MEDIUM:
                    suffix = Constants.BOXART_URL_MEDIUM;
                    break;
                case Constants.BOXART_URL_LARGE:
                    suffix = Constants.BOXART_URL_LARGE;
                    break;
                default:
                    break;
            }
        }

        return suffix;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Вернулись от приложения Камера
            if (requestCode == CAMERA_CAPTURE) {
                // Получим Uri снимка
                picUri = data.getData();
                // кадрируем его
                performCrop();
            } else if (requestCode == PIC_CROP) {             // Вернулись из операции кадрирования
                Bundle extras = data.getExtras();
                // Получим кадрированное изображение
                bmBoxartPic = extras.getParcelable("data");

                bmBoxartPic = Bitmap.createScaledBitmap(bmBoxartPic, 640, 395, false);
                bytes = new ByteArrayOutputStream();
                bmBoxartPic.compress(Bitmap.CompressFormat.JPEG, 70, bytes);

                ivEditorBoxart.setImageBitmap(bmBoxartPic);
            }
        }
    }

    private void getValues() {
        ContentValues cv = new ContentValues();
        cv.put(DbConnector.COLUMN_BRAND, etDetFullBrand.getText().toString().trim());
        cv.put(DbConnector.COLUMN_KIT_NAME, etDetFullKitname.getText().toString().trim());
        cv.put(DbConnector.COLUMN_BRAND_CATNO, etDetFullBrandCatNo.getText().toString().trim());
        cv.put(DbConnector.COLUMN_SCALE, parseInt(etDetFullScale.getText().toString().trim()));
        cv.put(DbConnector.COLUMN_PURCHASE_PLACE, etPurchasedFrom.getText().toString().trim());
        if (!tvMPurchaseDate.getText().toString().equals("")
                && !tvMPurchaseDate.getText().toString().equals(R.string.Date_not_set)) {
            purchaseDate = tvMPurchaseDate.getText().toString();
            cv.put(DbConnector.COLUMN_PURCHASE_DATE, purchaseDate);
        } else {
            cv.put(DbConnector.COLUMN_PURCHASE_DATE, "");
        }
        if (etDetFullKitNoengname.getText() != null) {
            cv.put(DbConnector.COLUMN_AFTERMARKET_ORIGINAL_NAME, etDetFullKitNoengname.getText().toString().trim());
        }
        if (etFullPrice.getText().toString().trim().equals("")) {
            cv.put(DbConnector.COLUMN_PRICE, 0);
        } else {
            int pr = Integer.parseInt(etFullPrice.getText().toString().trim()) * 100;
            cv.put(DbConnector.COLUMN_PRICE, pr);

        }
        cv.put(DbConnector.COLUMN_NOTES, etFullNotes.getText().toString().trim());
        cv.put(DbConnector.COLUMN_CATEGORY, category);

        if (pictureName != null || pictureName.length() > 0) {
            cv.put(DbConnector.COLUMN_BOXART_URI, pictureName);
        } else {
            cv.put(DbConnector.COLUMN_BOXART_URI, "");
        }

        String y = spKitYear.getSelectedItem().toString();
        cv.put(DbConnector.COLUMN_YEAR, getKitYear(y));

        String d = spKitDescription.getSelectedItem().toString();
        cv.put(DbConnector.COLUMN_DESCRIPTION, getAfterDescription(d));

        quantity = spQuantity.getSelectedItemPosition() + 1;
        cv.put(DbConnector.COLUMN_QUANTITY, quantity);

        String curr = spCurrency.getSelectedItem().toString();
        cv.put(DbConnector.COLUMN_CURRENCY, curr);

        String purchasedFrom = etPurchasedFrom.getText().toString();
        cv.put(DbConnector.COLUMN_PURCHASE_PLACE, purchasedFrom);

        int status = spKitStatus.getSelectedItemPosition();
        cv.put(DbConnector.COLUMN_STATUS, status);

        int media = spKitMedia.getSelectedItemPosition();
        cv.put(DbConnector.COLUMN_MEDIA, media);

        dbConnector.addShop(purchasedFrom);//todo check shops

        dbConnector.editAftermarketById(afterId, cv);
    }

    private String getKitYear(String y) {
        if (!y.equals(getString(R.string.year))){
            return y;
        }else{
            return "";
        }
    }


    private void takePicture() {
        try {
            // Намерение для запуска камеры
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // Выводим сообщение об ошибке
            String errorMessage = "Ваше устройство не поддерживает съемку";
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private String getAfterDescription(String d) {
        String desc = "";
        if (!d.equals(getString(R.string.kittype))){
            desc = descToCode(d);
        }
        return desc;
    }

    private String descToCode(String d) { //// TODO: 13.09.2017 Helper
        String desc = "";
        if (d.equals(getString(R.string.kittype))){
            desc = "0";
        }else if (d.equals(getString(R.string.newkit))){
            desc = "1";
        }else if (d.equals(getString(R.string.rebox))){
            desc = "2";
//        }else if (d.equals(getString(R.string.new_decal))){
//            desc = "3";
//        }else if (d.equals(getString(R.string.changed_box))){
//            desc = "4";
//        }else if (d.equals(getString(R.string.repack))){
//            desc = "5";
//        }else if (d.equals(getString(R.string.reissue))){
//            desc = "6";
        }
        return desc;
    }

    private void setTag(String category) {
        switch (category){
            case Constants.CAT_AIR:
                linLayoutAir.setBackgroundColor(Helper.getColor(context, R.color.colorAccent));
//                categoryToReturn = 1;
                break;
            case Constants.CAT_GROUND:
                linLayoutGround.setBackgroundColor(Helper.getColor(context, R.color.colorAccent));
//                categoryToReturn = 2;
                break;
            case Constants.CAT_SEA:
                linLayoutSea.setBackgroundColor(Helper.getColor(context, R.color.colorAccent));
//                categoryToReturn = 3;
                break;
            case Constants.CAT_SPACE:
                linLayoutSpace.setBackgroundColor(Helper.getColor(context, R.color.colorAccent));
//                categoryToReturn = 4;
                break;
            case Constants.CAT_AUTOMOTO:
                linLayoutCar.setBackgroundColor(Helper.getColor(context, R.color.colorAccent));
//                categoryToReturn = 5;
                break;
            case Constants.CAT_FANTASY:
                linLayoutFantasy.setBackgroundColor(Helper.getColor(context, R.color.colorAccent));
//                categoryToReturn = 7;
                break;
            case Constants.CAT_FIGURES:
                linLayoutFigures.setBackgroundColor(Helper.getColor(context, R.color.colorAccent));
//                categoryToReturn = 6;
                break;
            case Constants.CAT_OTHER:
                linLayoutOther.setBackgroundColor(Helper.getColor(context, R.color.colorAccent));
//                categoryToReturn = 8;
                break;
        }
    }

    private void clearTags() {
        linLayoutSpace.setBackgroundColor(Helper.getColor(context, R.color.colorItem));
        linLayoutAir.setBackgroundColor(Helper.getColor(context, R.color.colorItem));
        linLayoutSea.setBackgroundColor(Helper.getColor(context, R.color.colorItem));
        linLayoutGround.setBackgroundColor(Helper.getColor(context, R.color.colorItem));
        linLayoutCar.setBackgroundColor(Helper.getColor(context, R.color.colorItem));
        linLayoutOther.setBackgroundColor(Helper.getColor(context, R.color.colorItem));
        linLayoutFigures.setBackgroundColor(Helper.getColor(context, R.color.colorItem));
        linLayoutFantasy.setBackgroundColor(Helper.getColor(context, R.color.colorItem));
    }


    private void writeBoxartFile(File exportDir) {
        File boxartImageFile = new File(exportDir + File.separator + pictureName);
        try {
            boxartImageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(boxartImageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bytes == null){
            bytes = new ByteArrayOutputStream();
            bmBoxartPic.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
        }
        try {
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void performCrop(){
        try {
            // Намерение для кадрирования. Не все устройства поддерживают его
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 280);
            cropIntent.putExtra("aspectY", 172);
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 172);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PIC_CROP);
            // TODO: 25.08.2017 исправить размеры картинки
        }
        catch(ActivityNotFoundException anfe){
            String errorMessage = "Извините, но ваше устройство не поддерживает кадрирование";
            Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }




    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (bmBoxartPic != null){
            outState.putParcelable("boxartImage", bmBoxartPic);
        }

    }

    private void initUI(){
        ivEditorBoxart = (ImageView)view.findViewById(R.id.ivEditorBoxart);
        ivEditorBoxart.setOnClickListener(this);
        etDetFullKitname = (EditText)view.findViewById(R.id.etDetFullKitname);
        etDetFullBrand = (EditText)view.findViewById(R.id.etDetFullBrand);
        etDetFullBrandCatNo = (EditText)view.findViewById(R.id.etDetFullBrandCatNo);
        etDetFullScale = (EditText)view.findViewById(R.id.etDetFullScale);
        etDetFullKitNoengname = (EditText)view.findViewById(R.id.etDetKitOrigName);

        linLayoutAir = (LinearLayout)view.findViewById(R.id.linLayoutSelectAir);
        linLayoutAir.setOnClickListener(this);
        linLayoutCar = (LinearLayout)view.findViewById(R.id.linLayoutSelectCar);
        linLayoutCar.setOnClickListener(this);
        linLayoutGround = (LinearLayout)view.findViewById(R.id.linLayoutSelectGround);
        linLayoutGround.setOnClickListener(this);
        linLayoutOther = (LinearLayout)view.findViewById(R.id.linLayoutSelectOther);
        linLayoutOther.setOnClickListener(this);
        linLayoutSea = (LinearLayout)view.findViewById(R.id.linLayoutSelectSea);
        linLayoutSea.setOnClickListener(this);
        linLayoutSpace = (LinearLayout)view.findViewById(R.id.linLayoutSelectSpace);
        linLayoutSpace.setOnClickListener(this);
        linLayoutFantasy = (LinearLayout)view.findViewById(R.id.linLayoutSelectFantasy);
        linLayoutFantasy.setOnClickListener(this);
        linLayoutFigures = (LinearLayout)view.findViewById(R.id.linLayoutSelectFigures);
        linLayoutFigures.setOnClickListener(this);

        btnSaveEdit = (Button)view.findViewById(R.id.btnSaveEdit);
        btnSaveEdit.setOnClickListener(this);
        btnCancelEdit = (Button)view.findViewById(R.id.btnCancelEdit);
        btnCancelEdit.setOnClickListener(this);
        btnDelete = (Button)view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);
        btnAddBoxart = (Button)view.findViewById(R.id.btnAddBoxart);
        btnAddBoxart.setOnClickListener(this);
        btnRestoreImage = (Button)view.findViewById(R.id.btnRestoreImage);
        btnRestoreImage.setOnClickListener(this);

        spKitDescription = (AppCompatSpinner)view.findViewById(R.id.spKitDescription);
        spKitYear = (AppCompatSpinner)view.findViewById(R.id.spKitYear);

        spCurrency = (AppCompatSpinner)view.findViewById(R.id.spFullCurrency);
        spQuantity = (AppCompatSpinner)view.findViewById(R.id.spFullQuantity);
        spKitMedia = (AppCompatSpinner)view.findViewById(R.id.spKitMedia);
        spKitStatus = (AppCompatSpinner)view.findViewById(R.id.spKitStatus);


        etFullNotes = (EditText)view.findViewById(R.id.etFullNotes);
        etFullPrice = (EditText)view.findViewById(R.id.etFullPrice);
        etPurchasedFrom = (EditText)view.findViewById(R.id.etFullPurchasedFrom);

        tvMPurchaseDate = (TextView)view.findViewById(R.id.tvMSelectPurchaseDate);
        tvMPurchaseDate.setOnClickListener(this);

        btnClearDate = (Button)view.findViewById(R.id.btnMClearDate);
        btnClearDate.setOnClickListener(this);

//        btnAddAftermarket = (Button)view.findViewById(R.id.btnAddAftermarket);
//        btnAddAftermarket.setOnClickListener(this);
    }

    private boolean checkAllFields() {
        // TODO: 25.08.2017 проверки на пробелы
        boolean check = true;
        if (TextUtils.isEmpty(etDetFullBrand.getText())) {
            etDetFullBrand.setError(getString(R.string.enter_brand));
            check = false;
        }
        if (TextUtils.isEmpty(etDetFullBrandCatNo.getText())) {
            etDetFullBrandCatNo.setError(getString(R.string.enter_cat_no));
            check = false;
        }
        if (TextUtils.isEmpty(etDetFullScale.getText()) || etDetFullScale.getText().toString().equals("0")) {
            etDetFullScale.setError(getString(R.string.enter_scale));
            check = false;
        }
        if (TextUtils.isEmpty(etDetFullKitname.getText())) {
            etDetFullKitname.setError(getString(R.string.enter_kit_name));
            check = false;
        }
//        if (bmBoxartPic == null){
//            Toast.makeText(KitActivity.this, R.string.Please_add_boxart_photo, Toast.LENGTH_SHORT).show();
//            check = false;
//        }
        return check;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linLayoutSelectAir:
                category = Constants.CAT_AIR;
                isRbChanged = true;
                clearTags();
                setTag(category);
                break;
            case R.id.linLayoutSelectGround:
                category = Constants.CAT_GROUND;
                isRbChanged = true;
                clearTags();
                setTag(category);
                break;
            case R.id.linLayoutSelectSea:
                category = Constants.CAT_SEA;
                isRbChanged = true;
                clearTags();
                setTag(category);
                break;
            case R.id.linLayoutSelectSpace:
                category = Constants.CAT_SPACE;
                isRbChanged = true;
                clearTags();
                setTag(category);
                break;
            case R.id.linLayoutSelectOther:
                category = Constants.CAT_OTHER;
                isRbChanged = true;
                clearTags();
                setTag(category);
                break;
            case R.id.linLayoutSelectCar:
                category = Constants.CAT_AUTOMOTO;
                isRbChanged = true;
                clearTags();
                setTag(category);
                break;
            case R.id.linLayoutSelectFigures:
                category = Constants.CAT_FIGURES;
                isRbChanged = true;
                clearTags();
                setTag(category);
                break;
            case R.id.linLayoutSelectFantasy:
                category = Constants.CAT_FANTASY;
                isRbChanged = true;
                clearTags();
                setTag(category);
                break;

            case R.id.ivEditorBoxart:
//                viewPicture();
                break;


///////////////////////////////////////////////////////////////////////////////
            case R.id.btnSaveEdit:
                if (checkAllFields()) {
                    if (bmBoxartPic != null) {
                        size = Constants.SIZE_FULL;

                        File pictures = Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        pictureName = etDetFullBrand.getText().toString()
                                + etDetFullBrandCatNo.getText().toString()
                                + size
                                + ".jpg";
                        photoFile = new File(pictures, pictureName);

                        File exportDir = new File(Environment.getExternalStorageDirectory(), "Kitstasher");
                        if (!exportDir.exists()) {
                            exportDir.mkdirs();
                        }
                        writeBoxartFile(exportDir);
//                        writeBoxartToCloud();

                        bmBoxartPic = Bitmap.createScaledBitmap(bmBoxartPic, 280, 172, false);
                        size = Constants.SIZE_MEDIUM;
                        pictureName = etDetFullBrand.getText().toString()
                                + etDetFullBrandCatNo.getText().toString()
                                + size
                                + ".jpg";
                        writeBoxartFile(exportDir);

                        bmBoxartPic = Bitmap.createScaledBitmap(bmBoxartPic, 140, 86, false);
                        size = Constants.SIZE_SMALL;
                        pictureName = etDetFullBrand.getText().toString()
                                + etDetFullBrandCatNo.getText().toString()
                                + size
                                + ".jpg";
                        writeBoxartFile(exportDir);
                    }


                    getValues();

                    AftermarketCardFragment fragment = new AftermarketCardFragment();
                    Bundle bundle = new Bundle();
//                    bundle.putChar(Constants.EDIT_MODE, Constants.MODE_AFTERMARKET);
//                    bundle.putString("listname", listname);
//                    bundle.putString("brand", brand);
//                    bundle.putString("catno", catno);
//                    bundle.putInt("scale", scale);
//                    bundle.putString("kitname", kitname);
////                bundle.putLong("kitid", cursor.getLong(cursor.getColumnIndex(DbConnector.COLUMN_ID)));
//                    bundle.putInt("position", position);
//                    bundle.putInt("category", categoryToReturn);
                    bundle.putLong("after_id", afterId);
                    bundle.putString("boxart_uri", pictureName);
                    fragment.setArguments(bundle);
                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.linlayoutAftermarketContainer, fragment);
                    fragmentTransaction.commit();
                } else {
                    //Проверяем все поля - начальная проверка не пройдена!
                    Toast.makeText(context, R.string.Please_enter_data, Toast.LENGTH_SHORT).show();
                }
                break;

/////////////////////////////////////////////////////////////////////////////////////////////

            case R.id.btnCancelEdit:

                AftermarketCardFragment fragment = new AftermarketCardFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("after_id", afterId);
                bundle.putString("boxart_uri", pictureName);
                fragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.linlayoutAftermarketContainer, fragment);
                fragmentTransaction.commit();
                break;

            ///////////////////////////////////////////////////////////////////////////////////

            case R.id.btnDelete:
                dbConnector.deleteAftermarketById(afterId);
                break;

            case R.id.btnAddBoxart:
                takePicture();
                break;

            case R.id.btnRestoreImage:
                ContentValues cvUri = new ContentValues();
                cvUri.put("boxart_uri", "");
                dbConnector.editRecById(afterId, cvUri);
                setBoxartImage();
                break;

            case R.id.tvMSelectPurchaseDate:
                isDateChanged = true;
                DialogFragment newFragment = new SelectDateFragment();
                Bundle timeBundle = new Bundle(1);
                timeBundle.putString("caller", "KitActivity");
                newFragment.setArguments(timeBundle);
                newFragment.show(getFragmentManager(), "DatePicker");
                break;

            case R.id.btnMClearDate:
                purchaseDate = "";
                tvMPurchaseDate.setText(R.string.Date_not_set);
                break;
        }
    }
}