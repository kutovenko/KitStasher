package com.example.kitstasher.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.BuildConfig;
import com.example.kitstasher.R;
import com.example.kitstasher.activity.ChooserActivity;
import com.example.kitstasher.activity.CropActivity;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.adapters.AdapterSpinner;
import com.example.kitstasher.adapters.MyListCursorAdapter;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;
import com.example.kitstasher.other.SelectDateFragment;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private Context context;
    private DbConnector dbConnector;
    private Cursor cursor;
    private Cursor aftermarketCursor;
    private View view;
    private EditText etDetFullKitname,
            etDetFullBrand,
            etDetFullBrandCatNo,
            etDetFullScale,
            etDetFullKitNoengname,
            etFullNotes,
            etFullPrice,
            etPurchasedFrom;
    private AppCompatSpinner spKitDescription,
            spKitYear,
            spQuantity,
            spCurrency,
            spKitMedia,
            spKitStatus,
            spCategory;
    private TextView tvMPurchaseDate;
    private ImageView ivEditorBoxart;
    private RecyclerView rvAftermarket;
    private LinearLayout linLayoutEditAftermarket;

    private long id;
    private final int REQEST_AFTER_KIT = 10;
    private int position,
            tabToReturn,
            scale,
            quantity,
            aMode;
    private String listname, // для переключения к вкладке.
            brand,
            catno,
            kitname,
            purchaseDate,
            boxartUri,
            mCurrentPhotoPath,
            scaleFilter,
            brandFilter,
            kitnameFilter,
            statusFilter,
            mediaFilter,
            category;
    private char workMode;
    private boolean isBoxartTemporary;
    private Bitmap bmBoxartPic;
    private Uri photoPath;
    private ArrayAdapter<String> descriptionAdapter, yearsAdapter, currencyAdapter;
    private MyListCursorAdapter aftermarketAdapter;


    @Override
    public void onResume(){
        super.onResume();
        aftermarketCursor = dbConnector.getAftermarketForKit(id, listname);
        if (aftermarketAdapter == null) {
            aftermarketAdapter = new MyListCursorAdapter(aftermarketCursor, context, aMode);
            rvAftermarket.setAdapter(aftermarketAdapter);
        } else {
            aftermarketAdapter.changeCursor(aftermarketCursor);
            aftermarketAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item_edit, container, false);
        context = getActivity();
        dbConnector = new DbConnector(context);
        dbConnector.open();

        initUI();

        position = getArguments().getInt(MyConstants.POSITION);
        id = getArguments().getLong(MyConstants.ID);
        listname = MyConstants.EMPTY;

        scaleFilter = getArguments().getString(MyConstants.SCALE_FILTER);
        brandFilter = getArguments().getString(MyConstants.BRAND_FILTER);
        kitnameFilter = getArguments().getString(MyConstants.KITNAME_FILTER);
        statusFilter = getArguments().getString(MyConstants.STATUS_FILTER);
        mediaFilter = getArguments().getString(MyConstants.MEDIA_FILTER);
        tabToReturn = getArguments().getInt(MyConstants.CATEGORY_TAB);
        workMode = getArguments().getChar(MyConstants.WORK_MODE);
        aMode = MyConstants.MODE_A_KIT;
        switch (workMode) {
            case 'a': //MyConstants.MODE_AFTERMARKET
                linLayoutEditAftermarket.setVisibility(GONE);
                cursor = dbConnector.getAftermarketByID(id);
                showEditForm(cursor);
                break;
            case 'k': //MyConstants.MODE_AFTER_KIT
                cursor = dbConnector.getKitById(id);
                aftermarketCursor = dbConnector.getAftermarketForKit(id, listname);
                aMode = MyConstants.MODE_A_EDIT;
                break;
            case 'm': //MyConstants.MODE_KIT
                cursor = dbConnector.getKitById(id);
                aftermarketCursor = dbConnector.getAftermarketForKit(id, listname);
                aMode = MyConstants.MODE_A_EDIT;
                break;
            case 'l': //MyConstants.MODE_LIST
                cursor = dbConnector.getListItemById(id);
                listname = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.MYLISTSITEMS_LISTNAME));
                aftermarketCursor = dbConnector.getAftermarketForKit(id, listname);
                aMode = MyConstants.MODE_A_KIT;
                break;
        }
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
        if (path != null) {
            mCurrentPhotoPath = path;
        }
        aftermarketAdapter = new MyListCursorAdapter(aftermarketCursor, context, aMode);
        rvAftermarket.setAdapter(aftermarketAdapter);

        showEditForm(cursor);

        if (savedInstanceState != null) {//todo glide
            bmBoxartPic = savedInstanceState.getParcelable(MyConstants.BOXART_IMAGE);
            Drawable drawable = new BitmapDrawable(getResources(), bmBoxartPic);
            ivEditorBoxart.setImageDrawable(drawable);
            tvMPurchaseDate.setText(savedInstanceState.getString("outDate"));
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivEditBoxart:
                chooseImageAction();
                break;

            case R.id.btnEditSave:
                if (checkAllFields()) {
                    isBoxartTemporary = false;
                    ContentValues cv = getValues();
                    if (workMode == MyConstants.MODE_LIST) {
                        dbConnector.editListItemById(id, cv);
                    } else if (workMode == MyConstants.MODE_KIT) {
                        dbConnector.editRecById(id, cv);
                    } else if (workMode == MyConstants.MODE_AFTERMARKET) {
                        dbConnector.editAftermarketById(id, cv);
                    }
                    String ret = String.valueOf(spCategory.getSelectedItemPosition());
                    Intent intent3 = new Intent();
                    intent3.putExtra(MyConstants.POSITION, position);
                    intent3.putExtra(MyConstants.LIST_ID, id);
                    intent3.putExtra(MyConstants.CATEGORY, ret);
                    intent3.putExtra(MyConstants.CATEGORY_TAB, tabToReturn);
                    intent3.putExtra(MyConstants.SCALE_FILTER, scaleFilter);
                    intent3.putExtra(MyConstants.BRAND_FILTER, brandFilter);
                    intent3.putExtra(MyConstants.KITNAME_FILTER, kitnameFilter);
                    intent3.putExtra(MyConstants.STATUS_FILTER, statusFilter);
                    intent3.putExtra(MyConstants.MEDIA_FILTER, mediaFilter);
                    intent3.putExtra(MyConstants.WORK_MODE, workMode);
                    getActivity().setResult(RESULT_OK, intent3);
                    getActivity().finish();
                } else {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == MainActivity.REQUEST_CODE_CAMERA) {
            Intent cropIntent = new Intent(getActivity(), CropActivity.class);
            cropIntent.putExtra(MyConstants.FILE_URI, mCurrentPhotoPath);
            startActivityForResult(cropIntent, REQUEST_CODE_CROP);
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(getActivity(), R.string.camera_failure, Toast.LENGTH_LONG).show();
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CROP) {
            File image = new File(mCurrentPhotoPath);
            Glide
                    .with(context)
                    .load(image)
                    .placeholder(ic_menu_camera)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivEditorBoxart);
            boxartUri = mCurrentPhotoPath;
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(getActivity(), R.string.crop_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (bmBoxartPic != null) {
            outState.putParcelable(MyConstants.BOXART_IMAGE, bmBoxartPic);
            String outDate = tvMPurchaseDate.getText().toString();
            outState.putString("outDate", outDate);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBoxartTemporary) {
            File file = new File(mCurrentPhotoPath);
            file.deleteOnExit();
        }
    }

    private void showEditForm(Cursor cursor) {
        cursor.moveToFirst();
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

        int categoryToSet = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY))); //беру категогию из записи
        String[] categories = new String[]{getString(R.string.other), getString(R.string.air), getString(R.string.ground),
                getString(R.string.sea), getString(R.string.space), getString(R.string.auto_moto),
                getString(R.string.Figures), getString(R.string.Fantasy)};
        int[] icons = new int[]{
                R.drawable.ic_check_box_outline_blank_black_24dp,
                R.drawable.ic_tag_air_black_24dp, R.drawable.ic_tag_afv_black_24dp,
                R.drawable.ic_tag_ship_black_24dp, R.drawable.ic_tag_space_black_24dp,
                R.drawable.ic_directions_car_black_24dp, R.drawable.ic_wc_black_24dp,
                R.drawable.ic_android_black_24dp
        };
        AdapterSpinner adapterSpinner = new AdapterSpinner(context, icons, categories);
        spCategory.setAdapter(adapterSpinner);
        spCategory.setSelection(categoryToSet);

        String[] descriptionItems = new String[]{getString(R.string.kittype),
                getString(R.string.newkit),
                getString(R.string.rebox),
        };
        descriptionAdapter = new ArrayAdapter<>(context,
                R.layout.simple_spinner_item, descriptionItems);
        spKitDescription.setAdapter(descriptionAdapter);
        spKitDescription.setSelection(2);

        ArrayList<String> years = new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add(getString(R.string.unknown));
        for (int i = thisYear; i >= 1930; i--) {
            years.add(Integer.toString(i));
        }
        yearsAdapter = new ArrayAdapter<>(context,
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
        currencyAdapter = new ArrayAdapter<>(context,
                R.layout.simple_spinner_item, currencies);
        spCurrency.setAdapter(currencyAdapter);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultCurrency = sharedPreferences.getString(MyConstants.DEFAULT_CURRENCY, MyConstants.EMPTY);

        if (cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CURRENCY)) != null
                && !cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CURRENCY)).equals(MyConstants.EMPTY)) {
            String cr = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_CURRENCY));
            setKitCurrency(cr);
        } else {
            setKitCurrency(defaultCurrency);
        }

        Integer[] quants = new Integer[]{1,2,3,4,5,6,7,8,9,10};
        ArrayAdapter quantityAdapter = new ArrayAdapter<>(context,
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
                getString(R.string.media_multimedia),
                getString(R.string.media_decal),
                getString(R.string.media_mask)
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

        if (workMode == MyConstants.MODE_KIT) {
            String orName = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ORIGINAL_NAME));
            if (orName != null) {
                etDetFullKitNoengname.setText(orName);
            }
        }
        int prc = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PRICE));
        if (prc != 0) {
            etFullPrice.setText(String.valueOf(prc / 100));
        }else{
            etFullPrice.setText(MyConstants.EMPTY);
        }
        if (cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_NOTES)) != null) {
            etFullNotes.setText(cursor.getString(cursor.getColumnIndex
                    (DbConnector.COLUMN_NOTES)));
        }

        int q = cursor.getInt(cursor.getColumnIndex(DbConnector.COLUMN_QUANTITY));
        if (q != 0) {
            quantity = q;
            setKitQuantity(quantity);
        }else{
            quantity = 1;
            setKitQuantity(quantity);
        }

        String pd = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_PURCHASE_DATE));
        if (pd != null && !pd.equals("")) {
            tvMPurchaseDate.setText(cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_PURCHASE_DATE)));
        }else{
            tvMPurchaseDate.setText(R.string.Date_not_set);
        }

        String pPlace = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_PLACE));
        if (pPlace != null) {
            etPurchasedFrom.setText(pPlace);
        }

        String year = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_YEAR));
        if (year != null) {
            setKitYear(year);
        }

        String description = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_DESCRIPTION));
        if (description != null) {
            setKitDescription(description);
        }
        setBoxartImage();
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
        if (workMode == MyConstants.MODE_LIST) {
            cursor = dbConnector.getListItemById(id);
        } else if (workMode == MyConstants.MODE_KIT) {
            cursor = dbConnector.getKitById(id);
        } else if (workMode == MyConstants.MODE_AFTERMARKET) {
            cursor = dbConnector.getAftermarketByID(id);
        }

        cursor.moveToFirst();
        if (cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI)) != null
                && cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI)).length() > 1){
            boxartUri = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI));
            File imgFile = new File(String.valueOf(Uri.parse(boxartUri)));
            if(imgFile.exists()){
                Glide
                        .with(context)
                        .load(imgFile)
                        .placeholder(ic_menu_camera)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivEditorBoxart);

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
            String boxartUri = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI));
            Glide
                    .with(context)
                    .load(new File(Uri.parse(boxartUri).getPath()))
                    .placeholder(ic_menu_camera)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivEditorBoxart);
        }
    }

    private String composeUrl(String url){
        if (!Helper.isBlank(url)) {
            return MyConstants.BOXART_URL_PREFIX
                    + url
                    + getSuffix()
                    + MyConstants.JPG;
        }else{
            return MyConstants.EMPTY;
        }

    }

    private String getSuffix(){
        String suffix = MyConstants.BOXART_URL_LARGE;
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
                cvUri.put(MyConstants.BOXART_URI, MyConstants.EMPTY);
                dbConnector.editRecById(id, cvUri);
                setBoxartImage();
                alertDialog.dismiss();
            }
        });
    }

    private ContentValues getValues() {
        ContentValues cv = new ContentValues();
        cv.put(DbConnector.COLUMN_BRAND, etDetFullBrand.getText().toString().trim());
        cv.put(DbConnector.COLUMN_KIT_NAME, etDetFullKitname.getText().toString().trim());
        cv.put(DbConnector.COLUMN_BRAND_CATNO, etDetFullBrandCatNo.getText().toString().trim());
        cv.put(DbConnector.COLUMN_SCALE, parseInt(etDetFullScale.getText().toString().trim()));
        cv.put(DbConnector.COLUMN_PURCHASE_PLACE, etPurchasedFrom.getText().toString().trim());
        String date = tvMPurchaseDate.getText().toString();
        if (!date.equals(getResources().getString(R.string.Date_not_set))) {
            purchaseDate = tvMPurchaseDate.getText().toString();
            cv.put(DbConnector.COLUMN_PURCHASE_DATE, purchaseDate);
        }else{
            cv.put(DbConnector.COLUMN_PURCHASE_DATE, MyConstants.EMPTY);
        }

        if (etDetFullKitNoengname.getText() != null) {
            cv.put(DbConnector.COLUMN_ORIGINAL_NAME, etDetFullKitNoengname.getText().toString().trim());
        }
        if (etFullPrice.getText().toString().trim().equals("")){
            cv.put(DbConnector.COLUMN_PRICE, 0);
        }else{
            int pr = Integer.parseInt(etFullPrice.getText().toString().trim()) * 100;
            cv.put(DbConnector.COLUMN_PRICE, pr);
        }

        cv.put(DbConnector.COLUMN_NOTES, etFullNotes.getText().toString().trim());

        if (mCurrentPhotoPath != null) {
            cv.put(DbConnector.COLUMN_BOXART_URI, mCurrentPhotoPath);
        } else {
            cv.put(DbConnector.COLUMN_BOXART_URI, MyConstants.EMPTY);
        }

        String cat = String.valueOf(spCategory.getSelectedItemPosition());
        cv.put(DbConnector.COLUMN_CATEGORY, cat);


        String y = spKitYear.getSelectedItem().toString();
        if (!(y).equals(getResources().getString(R.string.unknown))) {
            cv.put(DbConnector.COLUMN_YEAR, y);
        } else {
            cv.put(DbConnector.COLUMN_YEAR, MyConstants.EMPTY);
        }

        String d = spKitDescription.getSelectedItem().toString();
        cv.put(DbConnector.COLUMN_DESCRIPTION, descToCode(d));

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

        return cv;
    }

    public String descToCode(String d) {
        String desc = "";
        if (d.equals(getString(R.string.unknown))) {
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
        isBoxartTemporary = true;
        String imageFileName = getTimestamp();
        File storageDir = getActivity().getExternalFilesDir(MyConstants.BOXART_DIRECTORY_NAME);
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    MyConstants.JPG,/* suffix */
                    storageDir      /* directory */
            );

        } catch (IOException e) {
            Toast.makeText(context, R.string.cannot_create_file, Toast.LENGTH_SHORT).show();
        }
        mCurrentPhotoPath = image != null ? image.getAbsolutePath() : MyConstants.EMPTY;
        return image;
    }

    private String getTimestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    }

    private void initUI(){
        ivEditorBoxart = view.findViewById(R.id.ivEditBoxart);
        ivEditorBoxart.setOnClickListener(this);

        etDetFullBrand = (AutoCompleteTextView) view.findViewById(R.id.acEditBrand);
        etPurchasedFrom = view.findViewById(R.id.acEditShop);

        etDetFullKitname = view.findViewById(R.id.etEditName);
        etDetFullBrandCatNo = view.findViewById(R.id.etEditCatno);
        etDetFullScale = view.findViewById(R.id.etEditScale);
        etDetFullKitNoengname = view.findViewById(R.id.etEditOrigName);
        etFullNotes = view.findViewById(R.id.etEditNotes);
        etFullPrice = view.findViewById(R.id.etEditPrice);

        Button btnSaveEdit = view.findViewById(R.id.btnEditSave);
        btnSaveEdit.setOnClickListener(this);
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

        linLayoutEditAftermarket = view.findViewById(R.id.linLayoutEditAftermarket);
        rvAftermarket = view.findViewById(R.id.rvEditAftermarket);
        RecyclerView.LayoutManager afterManager = new LinearLayoutManager(context);
        rvAftermarket.setHasFixedSize(true);
        rvAftermarket.setLayoutManager(afterManager);
        DefaultItemAnimator animator = new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
                return true;
            }
        };
        rvAftermarket.setItemAnimator(animator);
        if (workMode == MyConstants.MODE_AFTER_KIT || workMode == MyConstants.MODE_AFTERMARKET) {
            linLayoutEditAftermarket.setVisibility(GONE);
        }
    }

    private boolean checkAllFields() {
        boolean check = true;
        if (TextUtils.isEmpty(etDetFullBrand.getText())) {
            check = false;
        }
        if (TextUtils.isEmpty(etDetFullBrandCatNo.getText())) {
            check = false;
        }
        if (TextUtils.isEmpty(etDetFullScale.getText())
                || etDetFullScale.getText().toString().equals("0")) {
            check = false;
        }
        if (TextUtils.isEmpty(etDetFullKitname.getText())) {
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
        //добавляем афтермаркет к киту
        getFromManualAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManualAddFragment fragment = new ManualAddFragment();

                Bundle bundle = new Bundle();
                bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_AFTER_KIT);
                bundle.putString(MyConstants.LISTNAME, listname);
                bundle.putString(MyConstants.BRAND, brand);
                bundle.putString(MyConstants.CATNO, catno);
                bundle.putInt(MyConstants.SCALE, scale);
                bundle.putString(MyConstants.KITNAME, kitname);
                bundle.putInt(MyConstants.POSITION, position);
                bundle.putString(MyConstants.CATEGORY, category);
                bundle.putLong(MyConstants.ID, id);
                bundle.putString(MyConstants.BOXART_URI, boxartUri);
                fragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayoutEditContainer, fragment);
                fragmentTransaction.commit();
                alertDialog.dismiss();
            }
        });

        final Button getFromMyStash = dialogView.findViewById(R.id.btnListModeMyStash);
        getFromMyStash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChooserActivity.class);
                intent.putExtra(MyConstants.LISTNAME, listname);
                intent.putExtra(MyConstants.KIT_ID, id);
                intent.putExtra(MyConstants.WORK_MODE, MyConstants.MODE_AFTER_KIT);
                startActivityForResult(intent, REQEST_AFTER_KIT);
                alertDialog.dismiss();
            }
        });
    }

}