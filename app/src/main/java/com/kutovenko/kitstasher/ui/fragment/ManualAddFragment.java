package com.kutovenko.kitstasher.ui.fragment;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kutovenko.kitstasher.BuildConfig;
import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.databinding.FragmentTabbedManualaddBinding;
import com.kutovenko.kitstasher.db.DbConnector;
import com.kutovenko.kitstasher.model.Item;
import com.kutovenko.kitstasher.model.StashItem;
import com.kutovenko.kitstasher.network.AsyncApp42ServiceApi;
import com.kutovenko.kitstasher.ui.CropActivity;
import com.kutovenko.kitstasher.ui.MainActivity;
import com.kutovenko.kitstasher.ui.adapter.UiAlertDialogAdapter;
import com.kutovenko.kitstasher.ui.adapter.UiSpinnerAdapter;
import com.kutovenko.kitstasher.ui.adapter.UiSpinnerSupplyAdapter;
import com.kutovenko.kitstasher.ui.listener.OnFragmentInteractionListener;
import com.kutovenko.kitstasher.util.Helper;
import com.kutovenko.kitstasher.util.MyConstants;
import com.kutovenko.kitstasher.util.PathUtil;
import com.kutovenko.kitstasher.util.ValueContainer;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.util.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static android.view.View.SCREEN_STATE_ON;
import static com.kutovenko.kitstasher.ui.MainActivity.MY_PERMISSIONS_REQUEST_CAMERA;
import static com.kutovenko.kitstasher.ui.MainActivity.MY_PERMISSIONS_REQUEST_WRITE;
import static com.kutovenko.kitstasher.ui.MainActivity.REQUEST_CODE_CROP;
import static com.kutovenko.kitstasher.ui.MainActivity.asyncService;


/**
 * Created by Алексей on 21.04.2017. Manual add items to database
 */

public class ManualAddFragment extends Fragment implements View.OnClickListener, TextWatcher,
        AsyncApp42ServiceApi.App42StorageServiceListener, OnFragmentInteractionListener {

    private FragmentTabbedManualaddBinding binding;
    public static String manualTag;
    private String imageFileName;
    private String ownerId;
    private String barcode;
    private String sendStatus;
    private String dateAdded;
    private String boxartUrl;
    private String category;
    private String boxartUri;
    private String onlineId;
    private String scalematesUrl;
    private String placePurchased;
    private String defCurrency;
    private String mCurrentPhotoPath; //path for use with ACTION_VIEW intents
    private String itemType;

    private long currentId;
    private boolean isFoundOnline;
    private boolean isBoxartTemporary;
    private boolean wasSearchedOnline;
    private Context context;
    private DbConnector dbConnector;
    private List<String> myBrands;
    private ArrayAdapter<String> acAdapterMybrands;
    private ArrayAdapter<String> acAdapterMyshops;
    private ArrayAdapter<String> descriptionAdapter;
    private ArrayAdapter<String> currencyAdapter;
    private ArrayAdapter<String> yearsAdapter;
    private List<String> myShops;
    private OnFragmentInteractionListener mListener;
    private StashItem stashItem;
    private String datePurchased;
    private String currency;

    public ManualAddFragment() {

    }

    @NonNull
    public static ManualAddFragment newInstance() {
        return new ManualAddFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAttachToParentFragment(getParentFragment());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isOnline()) {
            binding.btnCheckOnlineDb.setVisibility(View.GONE);
            binding.stepper0.setTitle(com.kutovenko.kitstasher.R.string.brand_and_name);
            setAllStepsState(1);
        }
        binding.pbManualAdd.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imagePath", mCurrentPhotoPath);
        if (stashItem != null){
            outState.putString(MyConstants.BOXART_URL, stashItem.getBoxartUrl());
        }
        outState.putString(MyConstants.CATEGORY, category);
        if (barcode != null) {
            outState.putString(MyConstants.BARCODE, barcode);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tabbed_manualadd, container, false);
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        manualTag = getTag();

        initUI();

        checkCameraPermissions();
        checkWritePermissions();

        initVariables();

        prepareBrandsList();

        binding.stepper0.setState(SCREEN_STATE_ON);
        binding.stepper0.setNormalColor(Color.GREEN);

        if (savedInstanceState != null){
            if (savedInstanceState.getString(MyConstants.BOXART_URL) != null) {
                mCurrentPhotoPath = savedInstanceState.getString("imagePath");
                Glide
                        .with(context)
                        .load(
                                savedInstanceState.getString(MyConstants.BOXART_URL)
                                        + MyConstants.BOXART_URL_LARGE
                                        + MyConstants.JPG)
                        .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera).error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                        .into(binding.ivGetBoxart);
                if (mCurrentPhotoPath != null && !mCurrentPhotoPath.equals(MyConstants.EMPTY)) {
                    Glide
                            .with(context)
                            .load(new File(Uri.parse(mCurrentPhotoPath).getPath()))
                            .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera).error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                            .into(binding.ivGetBoxart);
                }
            }
            barcode = savedInstanceState.getString(MyConstants.BARCODE);
        }

        acAdapterMybrands = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, myBrands);
        binding.acTvBrand.addTextChangedListener(this);
        binding.acTvBrand.setAdapter(acAdapterMybrands);

        acAdapterMyshops = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, myShops);
        binding.acPlacePurchased.setAdapter(acAdapterMyshops);

        if (!isOnline()) {
            binding.btnCheckOnlineDb.setClickable(false);
            binding.stepper0.setTitle(com.kutovenko.kitstasher.R.string.brand_and_name);
            setAllStepsState(1);
        }

        if (itemType.equals(MyConstants.TYPE_AFTERMARKET)) {
            setAftermarketUI();
        } else if (itemType.equals(MyConstants.TYPE_KIT) || itemType.equals(MyConstants.MODE_SEARCH)) {
            setKitUI();
        }

        String[] descriptionItems = new String[]{
                getString(com.kutovenko.kitstasher.R.string.unknown),
                getString(com.kutovenko.kitstasher.R.string.newkit),
                getString(com.kutovenko.kitstasher.R.string.rebox)};
        descriptionAdapter = new ArrayAdapter<>(context,
                com.kutovenko.kitstasher.R.layout.simple_spinner_item, descriptionItems);
        binding.spDescription.setAdapter(descriptionAdapter);

        ArrayList<String> years = new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add(getString(com.kutovenko.kitstasher.R.string.unknown));
        for (int i = thisYear; i >= 1930; i--) {
            years.add(Integer.toString(i));
        }
        yearsAdapter = new ArrayAdapter<>(context,
                com.kutovenko.kitstasher.R.layout.simple_spinner_item, years);
        binding.spYear.setAdapter(yearsAdapter);

        Integer[] quants = new Integer[]{1,2,3,4,5,6,7,8,9,10};
        ArrayAdapter<Integer> quantityAdapter = new ArrayAdapter<>(context,
                com.kutovenko.kitstasher.R.layout.simple_spinner_item, quants);
        binding.spQuantity.setAdapter(quantityAdapter);
        binding.spQuantity.setSelection(0, true);

        String[] currencies = dbConnector.getCurrencies(DbConnector.TABLE_CURRENCIES,
                DbConnector.CURRENCIES_COLUMN_CURRENCY);

        currencyAdapter = new ArrayAdapter<>(context,
                com.kutovenko.kitstasher.R.layout.simple_spinner_item, currencies);
        binding.spCurrency.setAdapter(currencyAdapter);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultCurrency = sharedPreferences.getString(MyConstants.DEFAULT_CURRENCY, MyConstants.EMPTY);
        int spCurrencyPosition = currencyAdapter.getPosition(defaultCurrency);
        binding.spCurrency.setSelection(spCurrencyPosition);

        String[] mediaTypes = new String[]{
                getString(com.kutovenko.kitstasher.R.string.media_unknown),
                getString(com.kutovenko.kitstasher.R.string.media_injected),
                getString(com.kutovenko.kitstasher.R.string.media_shortrun),
                getString(com.kutovenko.kitstasher.R.string.media_resin),
                getString(com.kutovenko.kitstasher.R.string.media_vacu),
                getString(com.kutovenko.kitstasher.R.string.media_paper),
                getString(com.kutovenko.kitstasher.R.string.media_wood),
                getString(com.kutovenko.kitstasher.R.string.media_metal),
                getString(com.kutovenko.kitstasher.R.string.media_3dprint),
                getString(com.kutovenko.kitstasher.R.string.media_multimedia),
                getString(com.kutovenko.kitstasher.R.string.media_other)
        };
        ArrayAdapter<String> mediaAdapter = new ArrayAdapter<>(context, com.kutovenko.kitstasher.R.layout.simple_spinner_item,
                mediaTypes);
        binding.spKitMedia.setAdapter(mediaAdapter);
        binding.spKitMedia.setSelection(0);
        String[] categories;
        int[] icons;
        if (itemType.equals(MyConstants.TYPE_KIT)) {
            categories = new String[]{
                    getString(com.kutovenko.kitstasher.R.string.other),
                    getString(com.kutovenko.kitstasher.R.string.Air),
                    getString(com.kutovenko.kitstasher.R.string.Ground),
                    getString(com.kutovenko.kitstasher.R.string.Sea),
                    getString(com.kutovenko.kitstasher.R.string.Space),
                    getString(com.kutovenko.kitstasher.R.string.Auto_moto),
                    getString(com.kutovenko.kitstasher.R.string.Figures),
                    getString(com.kutovenko.kitstasher.R.string.Fantasy)
            };
            icons = new int[]{
                    com.kutovenko.kitstasher.R.drawable.ic_check_box_outline_blank_black_24dp,
                    com.kutovenko.kitstasher.R.drawable.ic_tag_air_black_24dp,
                    com.kutovenko.kitstasher.R.drawable.ic_tag_afv_black_24dp,
                    com.kutovenko.kitstasher.R.drawable.ic_tag_ship_black_24dp,
                    com.kutovenko.kitstasher.R.drawable.ic_tag_space_black_24dp,
                    com.kutovenko.kitstasher.R.drawable.ic_directions_car_black_24dp,
                    com.kutovenko.kitstasher.R.drawable.ic_wc_black_24dp,
                    com.kutovenko.kitstasher.R.drawable.ic_android_black_24dp
            };
            UiSpinnerAdapter uiSpinnerAdapter = new UiSpinnerAdapter(context, icons, categories);
            binding.spCategory.setAdapter(uiSpinnerAdapter);
            binding.spCategory.setSelection(Integer.parseInt(category));
        }else if (itemType.equals(MyConstants.TYPE_AFTERMARKET)) {
            categories = new String[]{
                    getString(com.kutovenko.kitstasher.R.string.media_addon),
                    getString(com.kutovenko.kitstasher.R.string.media_photoetch),
                    getString(com.kutovenko.kitstasher.R.string.media_decal),
                    getString(com.kutovenko.kitstasher.R.string.media_mask),
                    getString(com.kutovenko.kitstasher.R.string.media_other),
            };
            UiSpinnerSupplyAdapter uiSpinnerAdapter = new UiSpinnerSupplyAdapter(context, categories);
            binding.spCategory.setAdapter(uiSpinnerAdapter);
            binding.spCategory.setSelection(setAddonCategoryToPosition(category));
        }

        return binding.getRoot();
    }

    private int setAddonCategoryToPosition(String category) {
        switch (category){
            case MyConstants.M_CODE_ADDON:
                return 0;
            case MyConstants.M_CODE_PHOTOETCH:
                return 1;
            case MyConstants.M_CODE_DECAL:
                return 2;
            case MyConstants.M_CODE_MASK:
                return 3;
            case MyConstants.M_CODE_OTHER:
                return 4;
            default:
                return 4;
        }
    }

    private String getAddonCategoryFromPosition(int position) {
        switch (position) {
            case 0:
                return MyConstants.M_CODE_ADDON;
            case 1:
                return MyConstants.M_CODE_PHOTOETCH;
            case 2:
                return MyConstants.M_CODE_DECAL;
            case 3:
                return MyConstants.M_CODE_MASK;
            case 4:
                return MyConstants.M_CODE_OTHER;
            default:
                return MyConstants.M_CODE_OTHER;
        }
    }

    private void initVariables() {
        String activeTable = DbConnector.TABLE_KITS;
        context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        if (getArguments() != null) {
            itemType = getArguments().getString(MyConstants.ITEM_TYPE);
            if (itemType.equals(MyConstants.TYPE_KIT) && isOnline()) {
                setKitUI();
            } else {
                setAftermarketUI();
            }
        } else {
            itemType = MyConstants.TYPE_KIT;
        }
        wasSearchedOnline = false;
        isFoundOnline = false;
        sendStatus = MyConstants.EMPTY;
        boxartUrl = MyConstants.EMPTY;
        boxartUri = MyConstants.EMPTY;
        if (getArguments() != null && getArguments().getString(MyConstants.BOXART_URI) != null) {
            boxartUri = getArguments().getString(MyConstants.BOXART_URI);
        }
        dateAdded = df.format(c.getTime());
        category = MyConstants.CODE_OTHER;

        if (itemType.equals(MyConstants.TYPE_SUPPLY))
            onlineId = MyConstants.EMPTY;
        datePurchased = MyConstants.EMPTY;
        currency = sharedPreferences.getString(MyConstants.DEFAULT_CURRENCY, MyConstants.EMPTY);
        ownerId = sharedPreferences.getString(MyConstants.USER_ID_PARSE, MyConstants.EMPTY);
        defCurrency = sharedPreferences.getString(MyConstants.DEFAULT_CURRENCY, MyConstants.EMPTY);
        scalematesUrl = MyConstants.EMPTY;
        placePurchased = MyConstants.EMPTY;
    }

    private void initUI() {
        binding.btnCheckOnlineDb.setOnClickListener(this);
        binding.pbManualAdd.setVisibility(View.GONE);
        binding.btnMAdd.setOnClickListener(this);
        binding.btnMCancel.setOnClickListener(this);
        binding.ivGetBoxart.setOnClickListener(this);
        binding.ivGetImageFile.setOnClickListener(this);
        binding.tvPurchaseDate.setText(com.kutovenko.kitstasher.R.string.Date_not_set);
        binding.tvPurchaseDate.setOnClickListener(this);
        binding.btnClearDate.setOnClickListener(this);
        binding.stepper0.setPadding(0, 16, 0, 0);
        binding.stepper4.setIsLastStep(true);
    }

    private void prepareBrandsList() {
        myBrands = new ArrayList<>();
        myBrands = dbConnector.getBrandsNames();
        myShops = new ArrayList<>();
        myShops = dbConnector.getShopNames();
    }

    private boolean checkSearchFields() {
        boolean check = true;
        if (TextUtils.isEmpty(binding.acTvBrand.getText())) {
            Toast.makeText(context, com.kutovenko.kitstasher.R.string.please_fill_fields, Toast.LENGTH_SHORT).show();
            check = false;
        }
        if (TextUtils.isEmpty(binding.etBrandCatNo.getText())) {
            Toast.makeText(context, com.kutovenko.kitstasher.R.string.please_fill_fields, Toast.LENGTH_SHORT).show();
            check = false;
        }
        return check;
    }

    private boolean checkAllFields() {
        boolean check = true;
        if (TextUtils.isEmpty(binding.acTvBrand.getText())) {
            check = false;
        }
        if (TextUtils.isEmpty(binding.etBrandCatNo.getText())) {
            check = false;
        }
        if (TextUtils.isEmpty(binding.etScale.getText()) || binding.etScale.getText().toString().equals("0")) {
            check = false;
        } else if (!Pattern.matches("[0-9]+", binding.etScale.getText().toString())) {
            Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.please_use_numbers, Toast.LENGTH_SHORT).show();
            check = false;
        }
        if (TextUtils.isEmpty(binding.etKitName.getText())) {
            check = false;
        }
        if (!isFoundOnline && TextUtils.isEmpty(mCurrentPhotoPath)) {
            check = false;
        }
        return check;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (com.kutovenko.kitstasher.R.id.btnMCancel):
                clearFields();

            case (com.kutovenko.kitstasher.R.id.btnCheckOnlineDb):
                if (checkSearchFields()) {
                    String sBrand = binding.acTvBrand.getText().toString().trim();
                    String sBrandCatno = Helper.prepareCatno(binding.etBrandCatNo.getText().toString());
                    if (!isInLocalBase(sBrand, sBrandCatno)) {
                        if (isOnline()) {
                            binding.pbManualAdd.setVisibility(View.VISIBLE);
                            Query q1 = QueryBuilder.build(MyConstants.TAG_BRAND, sBrand,
                                    QueryBuilder.Operator.EQUALS);
                            Query q2 = QueryBuilder.build(MyConstants.TAG_BRAND_CATNO, sBrandCatno,
                                    QueryBuilder.Operator.EQUALS);
                            Query query = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
                            asyncService.findDocByQuery(MyConstants.App42DBName, MyConstants.CollectionName, query, this);
                        } else {
                            Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.offline_mode,
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.entry_already_exist,
                            Toast.LENGTH_SHORT).show();
                        break;
                    }
                } else {
                    Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.please_fill_fields,
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case (com.kutovenko.kitstasher.R.id.btnMAdd):
                String newBrand = binding.acTvBrand.getText().toString().trim();
                if (!Helper.isBlank(newBrand)
                        && !myBrands.contains(newBrand)
                        && newBrand.length() > 1) {
                    myBrands.add(newBrand);
                    dbConnector.addBrand(newBrand);
                    acAdapterMybrands = new ArrayAdapter<>(
                            context,
                            android.R.layout.simple_dropdown_item_1line, myBrands);
                    binding.acTvBrand.setAdapter(acAdapterMybrands);
                }

                String newShop = binding.acPlacePurchased.getText().toString().trim();
                if (!myShops.contains(newShop)) {
                    if (!Helper.isBlank(newShop) && newShop.length() > 1) {
                        myShops.add(newShop);
                        dbConnector.addShop(newShop);
                    }
                    acAdapterMyshops = new ArrayAdapter<>(
                            context,
                            android.R.layout.simple_dropdown_item_1line, myShops);
                    binding.acPlacePurchased.setAdapter(acAdapterMyshops);
                }

                if (checkAllFields()) {
                    getFieldsValues();
                    isBoxartTemporary = false;
                    if (!isInLocalBase(stashItem.getBrand(), stashItem.getBrandCatno())) {
                        stashItem.saveToLocalStash(dbConnector);
                        if (Helper.isOnline(context) && !stashItem.getBarcode().isEmpty()){
                            StorageService storageService = App42API.buildStorageService();
                            stashItem.saveToNewKit(storageService);
                        }
//                        } else {
//                            Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
//                        }
                        Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.kit_added, Toast.LENGTH_SHORT).show();
                        clearFields();
                        break;
                    } else {
                        Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.entry_already_exist,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.Please_enter_data, Toast.LENGTH_SHORT).show();
                    setAllStepsState(1);
                }
                break;

            case com.kutovenko.kitstasher.R.id.ivGetBoxart:
                try {
                    dispatchTakePictureIntent();
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getActivity(), getString(com.kutovenko.kitstasher.R.string.camera_failure), Toast.LENGTH_SHORT).show();
                }
                break;

            case (R.id.ivGetImageFile):
                    dispatchFileImageIntent();
                    break;

            case com.kutovenko.kitstasher.R.id.tvPurchaseDate:
                // TODO: 06.03.2019 datepicker
//                DatePickerFragment newFragment = new DatePickerFragment();
//                newFragment.show(getSupportFragmentManager(), "datePicker");
//
//                newFragment.setOnDateClickListener(new onDateClickListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//
//                        TextView tv1= (TextView) findViewById(R.id.txtDate);
//                        tv1.setText(datePicker.getYear()+"/"+datePicker.getMonth()+"/"+datePicker.getDayOfMonth());
//                    }
//
//                });
//                DialogFragment newFragment = new SelectDateFragment();
//                Bundle bundle = new Bundle(1);
//                bundle.putString("caller", "manualadd");
//                newFragment.setArguments(bundle);
//                newFragment.show(getFragmentManager(), "DatePicker");
                break;

            case com.kutovenko.kitstasher.R.id.btnClearDate:
                datePurchased = MyConstants.EMPTY;
                binding.tvPurchaseDate.setText(com.kutovenko.kitstasher.R.string.Date_not_set);
                break;
        }
    }

    private void dispatchFileImageIntent(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            if (Helper.getExternalStorageState() == Helper.StorageState.WRITEABLE) {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        mCurrentPhotoPath = FileProvider.getUriForFile(context,
                                BuildConfig.APPLICATION_ID + ".provider",
                                photoFile).toString();
                    } else {
                        mCurrentPhotoPath = Uri.fromFile(photoFile).toString();
                    }
                    startActivityForResult(intent, MainActivity.OPEN_DOCUMENT_CODE);
                }
            }
        }




        startActivityForResult(intent, MainActivity.OPEN_DOCUMENT_CODE);
    }

    private void dispatchTakePictureIntent() {
        Uri photoPath;
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
                    takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, 90);
                    startActivityForResult(takePictureIntent, MainActivity.REQUEST_CODE_CAMERA);
                }
            }
        }
    }

    private File createImageFile() {
        isBoxartTemporary = true;
        imageFileName = getTimestamp();
        File storageDir = getActivity().getExternalFilesDir("boxart");
        if (!(storageDir != null && storageDir.exists())) {
            storageDir.mkdirs();
        }
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    MyConstants.JPG,         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            Toast.makeText(context, getString(R.string.cant_create_file), Toast.LENGTH_LONG).show();
        }
        if (image != null) {
            mCurrentPhotoPath = image.getAbsolutePath();
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(MyConstants.FILE_URI, mCurrentPhotoPath);
            editor.apply();
        } else {
            Toast.makeText(context, getString(R.string.cant_create_file), Toast.LENGTH_LONG).show();
        }
        return image;
    }

    private String getTimestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    }

    private boolean isInLocalBase(String brand, String brand_catno) {
        return dbConnector.isItemDuplicate(brand, brand_catno);
    }

    private void setAllStepsState(int state) {
        binding.stepper0.setState(state);
        binding.stepper1.setState(state);
        binding.stepper2.setState(state);
        binding.stepper3.setState(state);
        binding.stepper4.setState(state);
    }

    private void getFieldsValues() {
        if (barcode == null) {
            barcode = MyConstants.EMPTY;
        }
        String year = MyConstants.EMPTY;
        if (itemType.equals(MyConstants.TYPE_KIT)){
            category = String.valueOf(binding.spCategory.getSelectedItemPosition());
        }else{
            int position = binding.spCategory.getSelectedItemPosition();
            category = getAddonCategoryFromPosition(position);
        }
        String y = binding.spYear.getSelectedItem().toString();
        if (!y.equals(getResources().getString(com.kutovenko.kitstasher.R.string.unknown))
                || y.equals(MyConstants.EMPTY)) {
            year = y;
        }
        int     status = MyConstants.STATUS_NEW;
        String brand = binding.acTvBrand.getText().toString().trim();
        String brandCatno = binding.etBrandCatNo.getText().toString().trim();
        int scale = Integer.parseInt(binding.etScale.getText().toString());
        String kitName = binding.etKitName.getText().toString().trim();
        String kitNoengname = binding.etKitNoengName.getText().toString().trim();
        String media = String.valueOf(binding.spKitMedia.getSelectedItemPosition());
        String d = binding.spDescription.getSelectedItem().toString();
        String description = (descToCode(d));
        currency = binding.spCurrency.getSelectedItem().toString();
        int quantity = binding.spQuantity.getSelectedItemPosition() + 1;
        String notes = binding.etNotes.getText().toString();
        int price;
        if (!binding.etPrice.getText().toString().equals(MyConstants.EMPTY)) {
            price = Integer.parseInt(binding.etPrice.getText().toString()) * 100;
        }
        else {
            price = 0;
        }
        if (!binding.tvPurchaseDate.getText().toString().equals(MyConstants.EMPTY)
                && binding.tvPurchaseDate.getText().toString().equals(getString(com.kutovenko.kitstasher.R.string.Date_not_set))) {
            datePurchased = binding.tvPurchaseDate.getText().toString();
        } else {
            datePurchased = MyConstants.EMPTY;
        }
        placePurchased = binding.acPlacePurchased.getText().toString().trim();

        stashItem = new StashItem.StashItemBuilder(itemType)
                .hasBrand(brand)
                .hasBrand_catno(Helper.prepareCatno(brandCatno))
                .hasKitName(kitName)
                .hasScale(scale)
                .hasCategory(category)
                .hasBarcode(barcode)
                .hasKitNoengName(kitNoengname)
                .hasDescription(description)
                .hasPrototype(MyConstants.EMPTY)
                .hasSendStatus(sendStatus)
                .hasBoxartUrl(boxartUrl)
                .hasBoxartUri(boxartUri)
                .hasScalematesUrl(scalematesUrl)
                .hasYear(year)
                .hasOnlineId(onlineId)
                .hasStatus(status)
                .hasDateAdded(dateAdded)
                .hasDatePurchased(datePurchased)
                .hasQuantity(quantity)
                .hasNotes(notes)
                .hasPrice(price)
                .hasCurrency(currency)
                .hasPlacePurchased(placePurchased)
                .hasStatus(status)
                .hasMedia(media)
                .build();
    }

    private String descToCode(String d) {
        String desc = MyConstants.EMPTY;
        if (d.equals(getString(com.kutovenko.kitstasher.R.string.kittype))){
            desc = "0";
        }else if (d.equals(getString(com.kutovenko.kitstasher.R.string.newkit))){
            desc = "1";
        }else if (d.equals(getString(com.kutovenko.kitstasher.R.string.rebox))){
            desc = "2";
        }
        return desc;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void clearFields() {
        binding.acTvBrand.setText(MyConstants.EMPTY);
        binding.etBrandCatNo.setText(MyConstants.EMPTY);
        binding.etKitNoengName.setText(MyConstants.EMPTY);
        binding.etScale.setText(MyConstants.EMPTY);
        binding.etKitName.setText(MyConstants.EMPTY);
        isFoundOnline = false;
        wasSearchedOnline = false;
        binding.ivGetBoxart.setImageResource(com.kutovenko.kitstasher.R.drawable.ic_menu_camera);
        binding.ivGetBoxart.setBackgroundResource(com.kutovenko.kitstasher.R.drawable.button);
        binding.spDescription.setSelection(0);
        binding.spYear.setSelection(0);
        binding.spQuantity.setSelection(0);
        binding.etNotes.setText(MyConstants.EMPTY);
        binding.etPrice.setText(MyConstants.EMPTY);
        binding.tvPurchaseDate.setText(com.kutovenko.kitstasher.R.string.Date_not_set);
        binding.acPlacePurchased.setText(MyConstants.EMPTY);
        setAllStepsState(0);
        binding.stepper0.setState(1);
        if (!isOnline()) {
            setAllStepsState(1);
            binding.btnCheckOnlineDb.setVisibility(View.GONE);
        }

        stashItem = new StashItem.StashItemBuilder(itemType).build();

        wasSearchedOnline = false;
        sendStatus = MyConstants.EMPTY;
        itemType = MyConstants.TYPE_KIT;
        boxartUrl = MyConstants.EMPTY;
        boxartUri = MyConstants.EMPTY;
        barcode = MyConstants.EMPTY;
        datePurchased = dateAdded;
        scalematesUrl = MyConstants.EMPTY;
        int spCurrencyPosition = currencyAdapter.getPosition(defCurrency);
        binding.spCurrency.setSelection(spCurrencyPosition);
        placePurchased = MyConstants.EMPTY;
        binding.acPlacePurchased.setText(placePurchased);
        currentId = 0;
        sendStatus = MyConstants.EMPTY;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(MyConstants.BARCODE).apply();
    }

    private void setDescription(String description) {
        if (!description.equals(MyConstants.EMPTY)) {
            String desc = MyConstants.EMPTY;
            if (!description.equals(MyConstants.EMPTY)) {
                switch (description) {
                    case "0":
                        desc = getString(com.kutovenko.kitstasher.R.string.unknown);
                        break;
                    case "1":
                        desc = getString(com.kutovenko.kitstasher.R.string.newkit);
                        break;
                    case "2":
                        desc = getString(com.kutovenko.kitstasher.R.string.rebox);
                        break;
                }
            }else{
                desc = getString(com.kutovenko.kitstasher.R.string.unknown);
            }
            int spDescPosition = descriptionAdapter.getPosition(desc);
            binding.spDescription.setSelection(spDescPosition);
        }else{
            binding.spDescription.setSelection(0);
        }
    }

    private void setKitYear(String year) {
        if (year.length() == 4 && !year.contains("-")) {
            int spYearPosition = yearsAdapter.getPosition(year);
            binding.spYear.setSelection(spYearPosition);
        }else{
            binding.spYear.setSelection(0);
        }
    }

    private void setKitCategory(String cat) {
        binding.spCategory.setSelection(Integer.valueOf(cat));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBoxartTemporary) {
            File file = new File(mCurrentPhotoPath);
            file.deleteOnExit();
            dbConnector.close();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * Passing data from scanFragment In the child fragment.
     * @param fragment fragment with interface
     */
    public void onAttachToParentFragment(Fragment fragment) {
        try {
            mListener = (OnFragmentInteractionListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    fragment.toString());
        }
    }

    @Override
    public void onFragmentInteraction(String b, String mode) {
        barcode = b;
        wasSearchedOnline = true;
        isFoundOnline = false;
        itemType = mode;
        setKitUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mCurrentPhotoPath = sharedPref.getString(MyConstants.FILE_URI, "");
        if (resultCode == RESULT_OK && requestCode == MainActivity.REQUEST_CODE_CAMERA) {
            Intent cropIntent = new Intent(getActivity(), CropActivity.class);
            cropIntent.putExtra(MyConstants.FILE_URI, mCurrentPhotoPath);
            startActivityForResult(cropIntent, REQUEST_CODE_CROP);
        }

        if (resultCode == RESULT_OK && requestCode == MainActivity.OPEN_DOCUMENT_CODE){
            if (data != null) {
                Uri imageUri = data.getData();
                Glide
                        .with(context)
                        .load(imageUri)
                        .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera).error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                        .into(binding.ivCurrentBoxart);

                String input = null;
                try {
                    input = PathUtil.getPath(context, imageUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                String newFilePath = createImageFile().getAbsolutePath();
                try {
                    FileUtils.copyFile(input, newFilePath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCurrentPhotoPath = newFilePath;
                boxartUri = mCurrentPhotoPath;
            }
        }

        if (resultCode != RESULT_OK) {
            Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.camera_failure, Toast.LENGTH_LONG).show();
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CROP) {
            File image = new File(mCurrentPhotoPath);
            Glide
                    .with(context)
                    .load(image)
                    .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera).error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                    .into(binding.ivCurrentBoxart);
            boxartUri = mCurrentPhotoPath;

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.crop_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void setKitUI() {
        binding.stepper0.setTitle(com.kutovenko.kitstasher.R.string.search_by_code);
        binding.btnCheckOnlineDb.setVisibility(View.VISIBLE);
        setAllStepsState(0);
        binding.stepper0.setState(1);
    }

    private void setAftermarketUI() {
        binding.stepper0.setTitle(com.kutovenko.kitstasher.R.string.brand_and_name);
        binding.btnCheckOnlineDb.setVisibility(View.GONE);
        setAllStepsState(1);
    }

    @Override
    public void onFindDocSuccess(Storage response) {
        binding.pbManualAdd.setVisibility(View.GONE);
        String showKit;
        final List<StashItem> itemsToShow = new ArrayList<>();
        final List<Item> itemList = new ArrayList<Item>();

        wasSearchedOnline = true;
        isFoundOnline = true;
        final ValueContainer<String> urlContainer = new ValueContainer<>();
        urlContainer.setVal(MyConstants.EMPTY);
        final ValueContainer<String> categoryContainer;
        categoryContainer = new ValueContainer<>();
        final ValueContainer<String> barcodeContainer = new ValueContainer<>();

        String inBrand = "";
        String inBrandCatno = "";
        String inScale = "";
        String inMedia = MyConstants.M_CODE_UNKNOWN;
        String inKitName = "";
        String inKitNoengname = "";
        String inDescription = "";
        String inYear = "";
        String inScalematesUrl = "";

        ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
        for (int i = 0; i < jsonDocList.size(); i++) {
            String in = jsonDocList.get(i).getJsonDoc();
            try {
                JSONObject reader = new JSONObject(in);
                urlContainer.setVal(Helper.nullCheck(reader.getString(MyConstants.TAG_BOXART_URL)));
                categoryContainer.setVal(Helper.nullCheck(reader.getString(MyConstants.TAG_CATEGORY)));
                barcodeContainer.setVal(Helper.nullCheck(reader.getString(MyConstants.TAG_BARCODE)));
                inBrand = Helper.nullCheck(reader.getString(MyConstants.TAG_BRAND));
                inBrandCatno = Helper.nullCheck(reader.getString(MyConstants.TAG_BRAND_CATNO));
                inScale = Helper.nullCheck(reader.getString(MyConstants.TAG_SCALE), "0");
                inKitName = Helper.nullCheck(reader.getString(MyConstants.TAG_KIT_NAME));
                inDescription = Helper.nullCheck(reader.getString(MyConstants.TAG_DESCRIPTION));
                inYear = Helper.nullCheck(reader.getString(MyConstants.TAG_YEAR));
                inScalematesUrl = Helper.nullCheck(reader.getString(MyConstants.TAG_SCALEMATES_PAGE));
                inMedia = Helper.nullCheck(reader.getString(MyConstants.TAG_MEDIA), MyConstants.M_CODE_UNKNOWN);
                inKitNoengname = MyConstants.EMPTY;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showKit = inKitName + ", " + inBrand
                    + ", " + inBrandCatno + ", "
                    + "1/" + inScale;

            Item item = new Item(urlContainer.getVal(), showKit);
            itemList.add(item);

            StashItem stashItemToShow = new StashItem.StashItemBuilder(itemType)
                    .hasBrand(inBrand)
                    .hasBrand_catno(inBrandCatno)
                    .hasKitName(inKitName)
                    .hasScale(Integer.valueOf(inScale))
                    .hasDescription(inDescription)
                    .hasCategory(categoryContainer.getVal())
                    .hasKitNoengName(inKitNoengname)
                    .hasBoxartUrl(urlContainer.getVal())
                    .hasBarcode(barcodeContainer.getVal())
                    .hasScalematesUrl(inScalematesUrl)
                    .hasYear(inYear)
                    .hasMedia(inMedia)
                    .build();
            itemsToShow.add(stashItemToShow);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(com.kutovenko.kitstasher.R.string.Found);
        UiAlertDialogAdapter uiAlertDialogAdapter = new UiAlertDialogAdapter(getActivity(), itemList);

        builder.setAdapter(uiAlertDialogAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                setAllStepsState(1);
                StashItem stashItemToAdd = itemsToShow.get(item);
                binding.etScale.setText(String.valueOf(stashItemToAdd.getScale()));
                binding.etKitName.setText(stashItemToAdd.getName());
                binding.etKitNoengName.setText(stashItemToAdd.getNoengName());
                setKitYear(stashItemToAdd.getYear());
                setDescription(stashItemToAdd.getDescription());

                String barcode = stashItemToAdd.getBarcode();
                if (!Helper.isBlank(barcode)) {
                    ManualAddFragment.this.barcode = barcode;
                }
                category = stashItemToAdd.getCategory();
                setKitCategory(category);
                boxartUrl = stashItemToAdd.getBoxartUrl();
                scalematesUrl = stashItemToAdd.getScalematesUrl();
                Glide
                        .with(context)
                        .load(
                                stashItemToAdd.getBoxartUrl()
                                        + MyConstants.BOXART_URL_LARGE
                                        + MyConstants.JPG)
                        .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera).error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                        .into(binding.ivGetBoxart);
            }
        });

        binding.ivGetBoxart.setBackgroundResource(0);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onFindDocFailed(App42Exception ex) {
        binding.pbManualAdd.setVisibility(View.GONE);
        setAllStepsState(1);
        Toast.makeText(getActivity(),
                com.kutovenko.kitstasher.R.string.nothing_found_online,
                Toast.LENGTH_SHORT).show();
        wasSearchedOnline = true;
        isFoundOnline = false;
    }

    @Override
    public void onDocumentInserted(Storage response) {
        binding.pbManualAdd.setVisibility(View.GONE);
    }

    @Override
    public void onUpdateDocSuccess(Storage response) {
        binding.pbManualAdd.setVisibility(View.GONE);
    }

    @Override
    public void onInsertionFailed(App42Exception ex) {
        binding.pbManualAdd.setVisibility(View.GONE);
        Toast.makeText(getActivity(), getString(R.string.saving_failed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {
        binding.pbManualAdd.setVisibility(View.GONE);
    }

//    private void copyImageFile(Uri sourceUri, String pathToNewFile){
//        String sourcePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TongueTwister/tt_temp.3gp";
//
//        String source = Uri.parse(sourceUri);
//        String destination = pathToNewFile;
////        File destination = new File(destinationPath);
//        try
//        {
//            FileUtils.copyFile(source, destination);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }

    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
                Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.we_cant_read_barcodes,
                        Toast.LENGTH_LONG).show();
                Fragment fragment = NoPermissionFragment.newInstance(Manifest.permission.CAMERA, MyConstants.TYPE_SUPPLY);
                FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, fragment);
                fragmentTransaction.commitAllowingStateLoss();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
    }

    private void checkWritePermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE);
            }
        }
    }
}