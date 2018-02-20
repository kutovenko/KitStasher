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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.adapters.AdapterSpinner;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;
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
import static com.example.kitstasher.other.MyConstants.CODE_AUTOMOTO;
import static com.example.kitstasher.other.MyConstants.CODE_FANTASY;
import static com.example.kitstasher.other.MyConstants.CODE_FIGURES;
import static com.example.kitstasher.other.MyConstants.CODE_OTHER;
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
    private AppCompatSpinner spKitDescription, spKitYear, spQuantity,
            spCurrency, spKitMedia, spKitStatus, spCategory;
    private String category, listname; // для переключения к вкладке. при изменении совпадает с category, иначе то, что было (пришло или сохранено в записи)
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
        view = inflater.inflate(R.layout.fragment_item_edit, container, false);
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
//        setTag(category);
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

        String[] categories = new String[]{getString(R.string.other), getString(R.string.air), getString(R.string.ground),
                getString(R.string.sea), getString(R.string.space), getString(R.string.auto_moto),
                getString(R.string.Figures), getString(R.string.Fantasy)};
        int[] icons = new int[]{R.drawable.ic_check_box_outline_blank_black_24dp, R.drawable.ic_tag_air_black_24dp, R.drawable.ic_tag_afv_black_24dp,
                R.drawable.ic_tag_ship_black_24dp, R.drawable.ic_tag_space_black_24dp,
                R.drawable.ic_directions_car_black_24dp, R.drawable.ic_wc_black_24dp,
                R.drawable.ic_android_black_24dp};
        AdapterSpinner adapterSpinner = new AdapterSpinner(context, icons, categories);
        spCategory.setAdapter(adapterSpinner);
        spCategory.setSelection(Integer.parseInt(tagToCode(category)));


        String[] descriptionItems = new String[]{getString(R.string.kittype),
                getString(R.string.newkit),
                getString(R.string.rebox),
//                getString(R.string.new_tool),
//                getString(R.string.reissue),
//                getString(R.string.changed_parts), getString(R.string.new_decal),
//                getString(R.string.changed_box), getString(R.string.repack)
        };
        descriptionAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item, descriptionItems);
        spKitDescription.setAdapter(descriptionAdapter);
        spKitDescription.setSelection(2);

        ArrayList<String> years = new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add(getString(R.string.year));
        for (int i = thisYear; i >= 1930; i--) {
            years.add(Integer.toString(i));
        }
        yearsAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, years);
        spKitYear.setAdapter(yearsAdapter);

        String[] currencies = new String[]{"BYN", "EUR", "RUR", "UAH", "USD"};
        currencyAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, currencies);
        spCurrency.setAdapter(currencyAdapter);

        Integer[] quants = new Integer[]{1,2,3,4,5,6,7,8,9,10};
        quantityAdapter = new ArrayAdapter<>(context,
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


        if (cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ORIGINAL_NAME)) != null) {
            etDetFullKitNoengname.setText(cursor.getString(cursor.getColumnIndex
                    (DbConnector.COLUMN_ORIGINAL_NAME)));
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
        defaultCurrency = sharedPreferences.getString(MyConstants.DEFAULT_CURRENCY, "");

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
        position = getArguments().getInt(MyConstants.LIST_POSITION);
        afterId = getArguments().getLong("after_id");
        listname = "";
        scaleFilter = getArguments().getString("scaleFilter");
        brandFilter = getArguments().getString("brandFilter");
        kitnameFilter = getArguments().getString("kitnameFilter");
        statusFilter = getArguments().getString("statusFilter");
        mediaFilter = getArguments().getString("mediaFilter");

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

            File imgFile = new File(MyConstants.FOLDER_SDCARD_KITSTASHER
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
        } else if (cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI)) != null) {
            String boxart_uri = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI));
            Glide
                    .with(context)
                    .load(new File(Uri.parse(boxart_uri).getPath()))
                    .placeholder(ic_menu_camera)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivEditorBoxart);
            Toast.makeText(context, boxart_uri, Toast.LENGTH_SHORT).show();
        }
    }

    private String composeUrl(String url){
        if (!Helper.isBlank(url)) {
            return MyConstants.BOXART_URL_PREFIX
                    + url
                    + getSuffix()
                    + MyConstants.JPG;
        }else{
            return "";
        }

    }

    private String getSuffix(){
        String suffix = MyConstants.BOXART_URL_SMALL;
        SharedPreferences preferences = context.getSharedPreferences(MyConstants.BOXART_SIZE,
                Context.MODE_PRIVATE);
        if (preferences != null) {
            String temp = preferences.getString("boxart_size","");
            switch (temp){
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
            cv.put(DbConnector.COLUMN_ORIGINAL_NAME, etDetFullKitNoengname.getText().toString().trim());
        }
        if (etFullPrice.getText().toString().trim().equals("")) {
            cv.put(DbConnector.COLUMN_PRICE, 0);
        } else {
            int pr = Integer.parseInt(etFullPrice.getText().toString().trim()) * 100;
            cv.put(DbConnector.COLUMN_PRICE, pr);

        }
        cv.put(DbConnector.COLUMN_NOTES, etFullNotes.getText().toString().trim());
        String cat = String.valueOf(spCategory.getSelectedItemPosition());
//        cv.put(DbConnector.COLUMN_CATEGORY, Helper.codeToTag(cat));
        cv.put(DbConnector.COLUMN_CATEGORY, cat);

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


//    private void takePicture() {
//        try {
//            // Намерение для запуска камеры
//            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(captureIntent, CAMERA_CAPTURE);
//        } catch (ActivityNotFoundException e) {
//            // Выводим сообщение об ошибке
//            String errorMessage = "Ваше устройство не поддерживает съемку";
//            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
//        }
//    }

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
        }
        return desc;
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

        ivEditorBoxart = view.findViewById(R.id.ivEditBoxart);
//        ivEditorBoxart.setOnClickListener(this);

        etDetFullBrand = (AutoCompleteTextView) view.findViewById(R.id.acEditBrand); //ac
        etPurchasedFrom = view.findViewById(R.id.acEditShop); //ac

        etDetFullKitname = view.findViewById(R.id.etEditName);
        etDetFullBrandCatNo = view.findViewById(R.id.etEditCatno);
        etDetFullScale = view.findViewById(R.id.etEditScale);
        etDetFullKitNoengname = view.findViewById(R.id.etEditOrigName);
        etFullNotes = view.findViewById(R.id.etEditNotes);
        etFullPrice = view.findViewById(R.id.etEditPrice);


        Button btnSaveEdit = view.findViewById(R.id.btnEditSave);
        btnSaveEdit.setOnClickListener(this);
//        Button btnCancelEdit = view.findViewById(R.id.btnEditCancel);
//        btnCancelEdit.setOnClickListener(this);
//        ImageButton btnRestoreImage = (ImageButton) view.findViewById(R.id.ibtnRestoreImage);
//        btnRestoreImage.setOnClickListener(this);
        Button btnClearDate = view.findViewById(R.id.btnEditClearDate);
        btnClearDate.setOnClickListener(this);
        Button btnAddAftermarket = view.findViewById(R.id.btnAddAftermarket);
        btnAddAftermarket.setOnClickListener(this);

        spKitDescription = view.findViewById(R.id.spEditDescription);
        spKitYear = view.findViewById(R.id.spEditYear);
        spCurrency = view.findViewById(R.id.spEditCurrency);
        spQuantity = view.findViewById(R.id.spEditQuantity);
        spKitMedia = view.findViewById(R.id.spEditMedia);
        spKitStatus = view.findViewById(R.id.spEditStatus);
//        AppCompatSpinner spCategory = view.findViewById(R.id.spEditCategory);
        spCategory = view.findViewById(R.id.spEditCategory);

        tvMPurchaseDate = (TextView) view.findViewById(R.id.tvEditPurchaseDate);
        tvMPurchaseDate.setOnClickListener(this);

//        ListView lvAftermarket = (ListView) view.findViewById(R.id.lvEditAftermarket);
//        lvAftermarket.setVisibility(View.GONE);
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
//            Toast.makeText(ViewActivity.this, R.string.Please_add_boxart_photo, Toast.LENGTH_SHORT).show();
//            check = false;
//        }
        return check;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnEditSave:
                if (checkAllFields()) {
                    if (bmBoxartPic != null) {
                        size = MyConstants.SIZE_FULL;

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

                        bmBoxartPic = Bitmap.createScaledBitmap(bmBoxartPic, 280, 172, false);
                        size = MyConstants.SIZE_MEDIUM;
                        pictureName = etDetFullBrand.getText().toString()
                                + etDetFullBrandCatNo.getText().toString()
                                + size
                                + ".jpg";
                        writeBoxartFile(exportDir);

                        bmBoxartPic = Bitmap.createScaledBitmap(bmBoxartPic, 140, 86, false);
                        size = MyConstants.SIZE_SMALL;
                        pictureName = etDetFullBrand.getText().toString()
                                + etDetFullBrandCatNo.getText().toString()
                                + size
                                + ".jpg";
                        writeBoxartFile(exportDir);
                    }


                    getValues();

                    AftermarketCardFragment fragment = new AftermarketCardFragment();
                    Bundle bundle = new Bundle();
                    bundle.putLong(MyConstants.AFTER_ID, afterId);
                    bundle.putString(MyConstants.BOXART_URI, pictureName);
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

//            case R.id.btnEditCancel:
//
//                AftermarketCardFragment fragment = new AftermarketCardFragment();
//                Bundle bundle = new Bundle();
//                bundle.putLong("after_id", afterId);
//                bundle.putString("boxart_uri", pictureName);
//                fragment.setArguments(bundle);
//                android.support.v4.app.FragmentTransaction fragmentTransaction =
//                        getFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.linlayoutAftermarketContainer, fragment);
//                fragmentTransaction.commit();
//                break;

            ///////////////////////////////////////////////////////////////////////////////////

//            case R.id.ivEditBoxart:
//                takePicture();
//                break;

//            case R.id.ibtnRestoreImage:
//                ContentValues cvUri = new ContentValues();
//                cvUri.put("boxart_uri", "");
//                dbConnector.editRecById(afterId, cvUri);
//                setBoxartImage();
//                break;

            case R.id.tvEditPurchaseDate:
                isDateChanged = true;
                DialogFragment newFragment = new SelectDateFragment();
                Bundle timeBundle = new Bundle(1);
                timeBundle.putString("caller", "ViewActivity");
                newFragment.setArguments(timeBundle);
                newFragment.show(getFragmentManager(), "DatePicker");
                break;

            case R.id.btnEditClearDate:
                purchaseDate = "";
                tvMPurchaseDate.setText(R.string.Date_not_set);
                break;
        }
    }

    private String codeToDescription(String code) {
        String desc = "";
        switch (code) {
            case MyConstants.NEW_TOOL:
                desc = getResources().getString(R.string.new_tool);
                break;
            case MyConstants.REBOX:
                desc = getResources().getString(R.string.rebox);
                break;
        }
        return desc;
    }

    private String tagToCode(String tag) {
        String code = tag;
        switch (code) {
            case MyConstants.CAT_AIR:
                code = MyConstants.CODE_AIR;
                break;
            case MyConstants.CAT_GROUND:
                code = MyConstants.CODE_GROUND;
                break;
            case MyConstants.CAT_SEA:
                code = MyConstants.CODE_SEA;
                break;
            case MyConstants.CAT_SPACE:
                code = MyConstants.CODE_SPACE;
                break;
            case MyConstants.CAT_AUTOMOTO:
                code = CODE_AUTOMOTO;
                break;
            case MyConstants.CAT_OTHER:
                code = CODE_OTHER;
                break;
            case MyConstants.CAT_FIGURES:
                code = CODE_FIGURES;
                break;
            case MyConstants.CAT_FANTASY:
                code = CODE_FANTASY;
                break;

        }
        return code;
    }

    private String codeToMedia(int mediaCode) {
        String media;
        switch (mediaCode) {
            case MyConstants.M_CODE_UNKNOWN:
                media = getResources().getString(R.string.unknown);
                break;
            case MyConstants.M_CODE_INJECTED:
                media = getResources().getString(R.string.media_injected);
                break;
            case MyConstants.M_CODE_SHORTRUN:
                media = getResources().getString(R.string.media_shortrun);
                break;
            case MyConstants.M_CODE_RESIN:
                media = getResources().getString(R.string.media_resin);
                break;
            case MyConstants.M_CODE_VACU:
                media = getResources().getString(R.string.media_vacu);
                break;
            case MyConstants.M_CODE_PAPER:
                media = getResources().getString(R.string.media_paper);
                break;
            case MyConstants.M_CODE_WOOD:
                media = getResources().getString(R.string.media_wood);
                break;
            case MyConstants.M_CODE_METAL:
                media = getResources().getString(R.string.media_metal);
                break;
            case MyConstants.M_CODE_3DPRINT:
                media = getResources().getString(R.string.media_3dprint);
                break;
            case MyConstants.M_CODE_MULTIMEDIA:
                media = getResources().getString(R.string.media_multimedia);
                break;
            case MyConstants.M_CODE_OTHER:
                media = getResources().getString(R.string.media_other);
                break;
            case MyConstants.M_CODE_DECAL:
                media = getResources().getString(R.string.media_decal);
                break;
            case MyConstants.M_CODE_MASK:
                media = getResources().getString(R.string.media_mask);
                break;

            default:
                media = getResources().getString(R.string.unknown);
                break;
        }
        return media;
    }


    private String codeToStatus(int code) {
        String status;
        switch (code) {
            case MyConstants.STATUS_NEW:
                status = getResources().getString(R.string.status_new);
                break;
            case MyConstants.STATUS_OPENED:
                status = getResources().getString(R.string.status_opened);
                break;
            case MyConstants.STATUS_STARTED:
                status = getResources().getString(R.string.status_started);
                break;
            case MyConstants.STATUS_INPROGRESS:
                status = getResources().getString(R.string.status_inprogress);
                break;
            case MyConstants.STATUS_FINISHED:
                status = getResources().getString(R.string.status_finished);
                break;
            case MyConstants.STATUS_LOST:
                status = getResources().getString(R.string.status_lost_sold);
                break;
            default:
                status = getResources().getString(R.string.status_new);
                break;
        }
        return status;
    }

}