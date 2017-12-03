package com.example.kitstasher.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.BuildConfig;
import com.example.kitstasher.R;
import com.example.kitstasher.activity.AftermarketActivity;
import com.example.kitstasher.activity.ChooserActivity;
import com.example.kitstasher.activity.CropActivity;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.adapters.AdapterAfterItemsList;
import com.example.kitstasher.adapters.AdapterSpinner;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.SelectDateFragment;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.R.drawable.ic_menu_camera;
import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static com.example.kitstasher.activity.MainActivity.REQUEST_CODE_CROP;
import static java.lang.Integer.parseInt;

/**
 * Created by Алексей on 05.09.2017.
 * Universal item edit fragment for kits and aftermarket card.
 */

public class ItemEditFragment extends Fragment implements View.OnClickListener {
    private View view;
    private EditText etDetFullKitname, etDetFullBrand, etDetFullBrandCatNo, etDetFullScale,
            etDetFullKitNoengname,
            etFullNotes, etFullPrice, etPurchasedFrom;
    private AppCompatSpinner spKitDescription, spKitYear, spQuantity, spCurrency, spKitMedia,
            spKitStatus, spCategory;
    private TextView tvMPurchaseDate;
    private ListView lvAftermarket;

    private int position;
    private long id;
    private int categoryToReturn;
    private ImageView ivEditorBoxart;
    private String listname; // для переключения к вкладке. при изменении совпадает с category, иначе то, что было (пришло или сохранено в записи)
    private String brand, catno, kitname;
    private String purchaseDate;
    private int scale;
    private int quantity;
    private char editMode;
    private DbConnector dbConnector;
    private Cursor cursor;
    private Cursor aCursor;

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
    final private boolean demoMode = false;
    private Uri photoPath;
    private String imageFileName;
    private String mCurrentPhotoPath;

    private final int REQEST_AFTER_KIT = 10;

    String scaleFilter, brandFilter, kitnameFilter, statusFilter, mediaFilter;

    private ArrayAdapter<String> descriptionAdapter, yearsAdapter, currencyAdapter;
    private AdapterAfterItemsList aAdapter;

    @Override
    public void onResume(){
        super.onResume();
        aCursor = dbConnector.getAftermarketForKit(id, listname);
        aAdapter = new AdapterAfterItemsList(context, aCursor, 0, id, listname, editMode, demoMode);
        lvAftermarket.setAdapter(aAdapter);
        Helper.setListViewHeight(lvAftermarket);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item_edit, container, false);
        context = getActivity();
        dbConnector = new DbConnector(context);
        dbConnector.open();

        initUI();
        receiveArguments();


        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String dateToday = df.format(c.getTime());

        if (savedInstanceState != null) {
            bmBoxartPic = savedInstanceState.getParcelable("boxartImage");
            Drawable drawable = new BitmapDrawable(getResources(), bmBoxartPic);
            ivEditorBoxart.setImageDrawable(drawable);
            tvMPurchaseDate.setText(savedInstanceState.getString("outDate"));
        }

        /////////////////Работа с камерой

        pictureName = "";

        /////////////// изображение - добавлено
        if (editMode == Constants.MODE_LIST) {
            cursor = dbConnector.getListItemById(id);
            cursor.moveToFirst();
            listname = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.MYLISTSITEMS_LISTNAME));
///////Aftermarket editMode
        } else if (editMode == Constants.MODE_AFTERMARKET) {
            cursor = dbConnector.getAftermarketByID(id);
            cursor.moveToFirst();
        } else if (editMode == Constants.MODE_KIT) {
            cursor = dbConnector.getRecById(id);
            cursor.moveToFirst();
        }
//        cursor.moveToFirst();

        aCursor = dbConnector.getAftermarketForKit(id, listname);

        String category = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY)); //беру категогию из записи
        /////////////////////// радиокнопки
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


///////////////////////////////////////////////////////////////////////////////////////
        String[] categories = new String[]{getString(R.string.other), getString(R.string.air), getString(R.string.ground),
                getString(R.string.sea), getString(R.string.space), getString(R.string.auto_moto),
                getString(R.string.Figures), getString(R.string.Fantasy)};
        int[] icons = new int[]{R.drawable.ic_check_box_outline_blank_black_24dp, R.drawable.ic_tag_air_black_24dp, R.drawable.ic_tag_afv_black_24dp,
                R.drawable.ic_tag_ship_black_24dp, R.drawable.ic_tag_space_black_24dp,
                R.drawable.ic_directions_car_black_24dp, R.drawable.ic_wc_black_24dp,
                R.drawable.ic_android_black_24dp};
        AdapterSpinner adapterSpinner = new AdapterSpinner(context, icons, categories);
        spCategory.setAdapter(adapterSpinner);
        spCategory.setSelection(Integer.parseInt(Helper.tagToCode(category)));


//        String[]from = new String[]{DbConnector.COLUMN_AFTERMARKET_NAME}; //chek
//        int[] to = new int[] { R.id.tvEditBrandListItem};
        aAdapter = new AdapterAfterItemsList(context, aCursor, 0, id, listname, editMode, demoMode);
        lvAftermarket.setAdapter(aAdapter);
        lvAftermarket.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, AftermarketActivity.class);
                intent.putExtra(Constants.AFTER_ID, l);
                intent.putExtra(Constants.ID, id);
                startActivity(intent);
            }
        });

        String[] descriptionItems = new String[]{getString(R.string.kittype),
                getString(R.string.newkit),
                getString(R.string.rebox),
        };
        descriptionAdapter = new ArrayAdapter<String>(context,
                R.layout.simple_spinner_item, descriptionItems);
        spKitDescription.setAdapter(descriptionAdapter);
        spKitDescription.setSelection(2); //// TODO: 04.10.2017 проверить и дописать

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add(getString(R.string.unknown));
        for (int i = thisYear; i >= 1930; i--) {
            years.add(Integer.toString(i));
        }
        yearsAdapter = new ArrayAdapter<String>(context,
                R.layout.simple_spinner_item, years);
        spKitYear.setAdapter(yearsAdapter);


        Cursor currCursor = dbConnector.getAllFromTable(DbConnector.TABLE_CURRENCIES,
                DbConnector.CURRENCIES_COLUMN_CURRENCY);
        currCursor.moveToFirst();
        String[] currencies = new String[currCursor.getCount()];
        for (int i = 0; i < currCursor.getCount(); i++) {
            currencies[i] = currCursor.getString(1);
            currCursor.moveToNext();
        }
        currencyAdapter = new ArrayAdapter<String>(context,
                R.layout.simple_spinner_item, currencies);
        spCurrency.setAdapter(currencyAdapter);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultCurrency = sharedPreferences.getString(Constants.DEFAULT_CURRENCY, "");

        if (!cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CURRENCY)).equals("")) {
            String cr = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_CURRENCY));
            setKitCurrency(cr);
        } else {
            setKitCurrency(defaultCurrency);
        }


        Integer[] quants = new Integer[]{1,2,3,4,5,6,7,8,9,10};
        ArrayAdapter quantityAdapter = new ArrayAdapter<Integer>(context,
                R.layout.simple_spinner_item, quants);
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
        ArrayAdapter mediaAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item,
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
        ArrayAdapter statusAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item,
                kitStatuses);
        spKitStatus.setAdapter(statusAdapter);
        spKitMedia.setSelection(cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_STATUS)));

        if (editMode == Constants.MODE_KIT) {
            if (cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ORIGINAL_NAME)) != null) {
            etDetFullKitNoengname.setText(cursor.getString(cursor.getColumnIndex
                    (DbConnector.COLUMN_ORIGINAL_NAME)));
        }
        }
        if (cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PRICE)) != 0) {
            etFullPrice.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex
                    (DbConnector.COLUMN_PRICE))/100));
        }else{
            etFullPrice.setText("");
        }
        if (cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_NOTES)) != null) {
            etFullNotes.setText(cursor.getString(cursor.getColumnIndex
                    (DbConnector.COLUMN_NOTES)));
        }



        if (cursor.getInt(cursor.getColumnIndex(DbConnector.COLUMN_QUANTITY)) != 0){
            quantity = cursor.getInt(cursor.getColumnIndex(DbConnector.COLUMN_QUANTITY));
            setKitQuantity(quantity);
        }else{
            quantity = 1;
            setKitQuantity(quantity);
        }

        String pd = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_PURCHASE_DATE));

        if (!pd.equals("")){
            tvMPurchaseDate.setText(cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_PURCHASE_DATE)));
        }else{
            tvMPurchaseDate.setText(R.string.Date_not_set);
        }

        etPurchasedFrom.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_PLACE)));

        String year = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_YEAR));
        setKitYear(year);


        String description = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_DESCRIPTION));
        setKitDescription(description);

        setBoxartImage();

        return view;
    }

    private void receiveArguments() {
        position = getArguments().getInt(Constants.POSITION);
        id = getArguments().getLong(Constants.ID);
        listname = "";
        scaleFilter = getArguments().getString(Constants.SCALE_FILTER);
        brandFilter = getArguments().getString(Constants.BRAND_FILTER);
        kitnameFilter = getArguments().getString(Constants.KITNAME_FILTER);
        statusFilter = getArguments().getString(Constants.STATUS_FILTER);
        mediaFilter = getArguments().getString(Constants.MEDIA_FILTER);

        categoryToReturn = getArguments().getInt(Constants.LIST_CATEGORY);
//        editMode = Constants.MODE_KIT;
//        if (getArguments().getChar(Constants.EDIT_MODE) != '\u0000'){
        editMode = getArguments().getChar(Constants.EDIT_MODE);
//        if (editMode == Constants.MODE_AFTERMARKET){
//            AftermarketActivity.counter++;
//        }
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
                        desc = getString(R.string.rebox);
                        break;
                    case "3":
                        desc = getString(R.string.rebox);
                        break;
                    case "4":
                        desc = getString(R.string.rebox);
                        break;
                    case "5":
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
        if (editMode == Constants.MODE_LIST) {
            cursor = dbConnector.getListItemById(id);
        } else if (editMode == Constants.MODE_KIT) {
            cursor = dbConnector.getRecById(id);
        } else if (editMode == Constants.MODE_AFTERMARKET) {
            cursor = dbConnector.getAftermarketByID(id);
        }

        cursor.moveToFirst();
        if (cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI)) != null
                && cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI)).length() > 1){
            File imgFile = new File(Uri.parse(cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI)))
                    .getPath());
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath()); //todo GLIDE!!!!!!!!!!!!!!
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
        }
    }

    private String composeUrl(String url){
        if (!Helper.isBlank(url)) {
            return Constants.BOXART_URL_PREFIX
                    + url
                    + getSuffix()
                    + Constants.JPG;
        }else{
            return "";
        }

    }

    private String getSuffix(){
        String suffix = Constants.BOXART_URL_LARGE;
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
        super.onActivityResult(requestCode, resultCode, data);//?

        if (resultCode == RESULT_OK && requestCode == MainActivity.REQUEST_CODE_CAMERA
                ) {
            Intent cropIntent = new Intent(getActivity(), CropActivity.class);
            cropIntent.putExtra(Constants.FILE_URI, mCurrentPhotoPath);
            startActivityForResult(cropIntent, REQUEST_CODE_CROP);
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(getActivity(), "Fail", Toast.LENGTH_LONG).show();
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
//            String s = String.valueOf(resultUri);
//            Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
            bmBoxartPic = BitmapFactory.decodeFile(mCurrentPhotoPath);
            pictureName = mCurrentPhotoPath;
            ivEditorBoxart.setImageBitmap(bmBoxartPic);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);

            }else if (requestCode == 10){
                aCursor = dbConnector.getAftermarketForKit(id, listname);
            aAdapter = new AdapterAfterItemsList(context, aCursor, 0, id, listname, editMode, demoMode);
                lvAftermarket.setAdapter(aAdapter);
            }
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivEditBoxart:
                chooseImageAction();

                break;

            case R.id.btnEditSave:
                if (checkAllFields()) {
                    if (bmBoxartPic != null) {
                        // TODO: 17.11.2017 убрать и переделать с новой камерой
                        size = Constants.SIZE_FULL;

                        File pictures = Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        pictureName = etDetFullBrand.getText().toString()
                                + etDetFullBrandCatNo.getText().toString()
                                + size
                                + Constants.JPG;
                        photoFile = new File(pictures, pictureName);

                        File exportDir = new File(Environment.getExternalStorageDirectory(), "Kitstasher");
                        if (!exportDir.exists()) {
                            exportDir.mkdirs();
                        }
                        writeBoxartFile(exportDir);
                        writeBoxartToCloud();

                        bmBoxartPic = Bitmap.createScaledBitmap(bmBoxartPic, 280, 172, false);
                        size = Constants.SIZE_MEDIUM;
                        pictureName = etDetFullBrand.getText().toString()
                                + etDetFullBrandCatNo.getText().toString()
                                + size
                                + Constants.JPG;
                        writeBoxartFile(exportDir);

                        bmBoxartPic = Bitmap.createScaledBitmap(bmBoxartPic, 140, 86, false);
                        size = Constants.SIZE_SMALL;
                        pictureName = etDetFullBrand.getText().toString()
                                + etDetFullBrandCatNo.getText().toString()
                                + size
                                + Constants.JPG;
                        writeBoxartFile(exportDir);
                    }

                    getValues();

                    Intent intent3 = new Intent();
                    intent3.putExtra(Constants.POSITION, position);
//                        intent3.putExtra(Constants.LIST_POSITION, position);
                    intent3.putExtra(Constants.LIST_ID, id);
                    intent3.putExtra(Constants.LIST_CATEGORY, categoryToReturn);
                    //for filters
                    intent3.putExtra(Constants.SCALE_FILTER, scaleFilter);
                    intent3.putExtra(Constants.BRAND_FILTER, brandFilter);
                    intent3.putExtra(Constants.KITNAME_FILTER, kitnameFilter);

                    intent3.putExtra(Constants.STATUS_FILTER, statusFilter);
                    intent3.putExtra(Constants.MEDIA_FILTER, mediaFilter);
                    intent3.putExtra(Constants.EDIT_MODE, editMode);

                    getActivity().setResult(RESULT_OK, intent3);
                    getActivity().finish();
                } else {
                    //Проверяем все поля - начальная проверка не пройдена!
                    Toast.makeText(context, R.string.Please_enter_data, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tvEditPurchaseDate:
                DialogFragment newFragment = new SelectDateFragment();
                Bundle bundle = new Bundle(1);
                bundle.putString("caller", "ViewActivity");
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "DatePicker");
                break;

            case R.id.btnEditClearDate:
                purchaseDate = "";
                tvMPurchaseDate.setText(R.string.Date_not_set);
                break;
            case R.id.btnAddAftermarket:
                addAftermarket();
                break;
        }
    }

    private void chooseImageAction() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.alertdialog_imagemode, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(R.string.change_boxart);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final Button btnTakePicture = dialogView.findViewById(R.id.btnTakePicture);
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
                alertDialog.dismiss();
            }
        });

        final Button btnClearPicture = dialogView.findViewById(R.id.btnClearPicture);
        btnClearPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues cvUri = new ContentValues();
                cvUri.put(Constants.BOXART_URI, "");
                dbConnector.editRecById(id, cvUri);
                setBoxartImage();
                alertDialog.dismiss();
            }
        });
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
        }else{
            cv.put(DbConnector.COLUMN_PURCHASE_DATE, "");
        }

//        if (editMode == Constants.MODE_KIT) {
        if (etDetFullKitNoengname.getText() != null) {
            cv.put(DbConnector.COLUMN_ORIGINAL_NAME, etDetFullKitNoengname.getText().toString().trim());
        }
//        }
        if (etFullPrice.getText().toString().trim().equals("")){
            cv.put(DbConnector.COLUMN_PRICE, 0);
        }else{
            int pr = Integer.parseInt(etFullPrice.getText().toString().trim()) * 100;
            cv.put(DbConnector.COLUMN_PRICE, pr);

        }

        cv.put(DbConnector.COLUMN_NOTES, etFullNotes.getText().toString().trim());

        if (pictureName != null || pictureName.length() > 0) {

            cv.put(DbConnector.COLUMN_BOXART_URI, pictureName);
        } else {
            cv.put(DbConnector.COLUMN_BOXART_URI, "");
        }

        String cat = String.valueOf(spCategory.getSelectedItemPosition());
//        cv.put(DbConnector.COLUMN_CATEGORY, Helper.codeToTag(cat));
        cv.put(DbConnector.COLUMN_CATEGORY, cat);

        //!!!!!!!!!!!!!!!!!!!!!////////
        categoryToReturn = spCategory.getSelectedItemPosition();
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!///////////////////

        String y = spKitYear.getSelectedItem().toString();
        cv.put(DbConnector.COLUMN_YEAR, getKitYear(y));

        String d = spKitDescription.getSelectedItem().toString();
        cv.put(DbConnector.COLUMN_DESCRIPTION, getKitDescription(d));

        quantity = spQuantity.getSelectedItemPosition() + 1;
        cv.put(DbConnector.COLUMN_QUANTITY, quantity);

        String curr = spCurrency.getSelectedItem().toString();
        cv.put(DbConnector.COLUMN_CURRENCY, curr);

        String purchasedFrom = etPurchasedFrom.getText().toString();
        cv.put(DbConnector.COLUMN_PURCHASE_PLACE, purchasedFrom);

        int status = spKitStatus.getSelectedItemPosition();
        cv.put (DbConnector.COLUMN_STATUS, status);

        int media = spKitMedia.getSelectedItemPosition();
        cv.put(DbConnector.COLUMN_MEDIA, media);

        dbConnector.addShop(purchasedFrom);

        if (editMode == Constants.MODE_LIST) {
            dbConnector.editListItemById(id, cv);
        } else if (editMode == Constants.MODE_KIT) {
            dbConnector.editRecById(id, cv);
        } else if (editMode == Constants.MODE_AFTERMARKET) {
            dbConnector.editAftermarketById(id, cv);
        }

    }

    private String getKitYear(String y) {
        if (!y.equals(getString(R.string.year))){
            return y;
        }else{
            return "";
        }
    }

    private void takePicture() {
        dispatchTakePictureIntent();
    }

    private String getKitDescription(String d) {
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

    private void writeBoxartToCloud() {
        // TODO: 02.05.2017 отправка картинок в облако
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


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            if (Helper.getExternalStorageState() == Helper.StorageState.WRITEABLE) {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        photoPath = FileProvider.getUriForFile(context,
                                BuildConfig.APPLICATION_ID + ".provider",
                                photoFile);
                    } else {
                        photoPath = Uri.fromFile(photoFile);
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);
                    startActivityForResult(takePictureIntent, MainActivity.REQUEST_CODE_CAMERA);
                }
            }
        }
    }

    private File createImageFile() {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = getTimestamp();
        File storageDir = getActivity().getExternalFilesDir("boxart");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    Constants.JPG,         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            Toast.makeText(context, "Нельзя создать файл", Toast.LENGTH_LONG).show();
        }
        mCurrentPhotoPath = image.getAbsolutePath();
        Toast.makeText(context, mCurrentPhotoPath, Toast.LENGTH_LONG).show();
        return image;
    }

    private String getTimestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }


    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (bmBoxartPic != null){
            outState.putParcelable(Constants.BOXART_IMAGE, bmBoxartPic);
            String outDate = tvMPurchaseDate.getText().toString();
            outState.putString("outDate", outDate);
        }
    }

    private void initUI(){
        ivEditorBoxart = view.findViewById(R.id.ivEditBoxart);
        ivEditorBoxart.setOnClickListener(this);

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
        spCategory = view.findViewById(R.id.spEditCategory);

        tvMPurchaseDate = view.findViewById(R.id.tvEditPurchaseDate);
        tvMPurchaseDate.setOnClickListener(this);

        LinearLayout linLayoutEditAftermarket = view.findViewById(R.id.linLayoutEditAftermarket);
        lvAftermarket = view.findViewById(R.id.lvEditAftermarket);

        if (editMode != Constants.MODE_KIT) {
            linLayoutEditAftermarket.setVisibility(GONE);
//            lvAftermarket.setVisibility(GONE);
        }
//        else{
//
//        }
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
        return check;
    }


    private void addAftermarket(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.list_choosemode_alertdialog, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle(R.string.Choose_mode);
        Button scanButton = dialogView.findViewById(R.id.btnListModeScan);
        scanButton.setVisibility(GONE);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();


        final Button getFromManualAdd = dialogView.findViewById(R.id.btnListModeManual);
        getFromManualAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManualAddFragment fragment = new ManualAddFragment();

                Bundle bundle = new Bundle();
                bundle.putChar(Constants.EDIT_MODE, Constants.MODE_AFTERMARKET);
                bundle.putString(Constants.LISTNAME, listname);
                bundle.putString(Constants.BRAND, brand);
                bundle.putString(Constants.CATNO, catno);
                bundle.putInt(Constants.SCALE, scale);
                bundle.putString(Constants.KITNAME, kitname);
//                bundle.putInt(Constants.POSITION, position);

                bundle.putInt(Constants.POSITION, position);
                bundle.putInt(Constants.CATEGORY, categoryToReturn);
                bundle.putLong(Constants.ID, id);
                bundle.putString(Constants.BOXART_URI, pictureName);
                fragment.setArguments(bundle);

                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayoutEditContainer, fragment);
                fragmentTransaction.commit();
                alertDialog.dismiss();
            }
        });

        final Button getFromMyStash = (Button)dialogView.findViewById(R.id.btnListModeMyStash);
        getFromMyStash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChooserActivity.class);
                intent.putExtra("listname", listname);
                intent.putExtra("kitId", id);
                intent.putExtra(Constants.EDIT_MODE, Constants.MODE_AFTER_KIT);
                startActivityForResult(intent, REQEST_AFTER_KIT);
                alertDialog.dismiss();
            }
        });
    }

}