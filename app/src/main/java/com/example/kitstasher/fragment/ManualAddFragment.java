package com.example.kitstasher.fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.example.kitstasher.BuildConfig;
import com.example.kitstasher.R;
import com.example.kitstasher.activity.CropActivity;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.adapters.AdapterAddFragment;
import com.example.kitstasher.adapters.AdapterAlertDialog;
import com.example.kitstasher.adapters.AdapterSpinner;
import com.example.kitstasher.objects.Aftermarket;
import com.example.kitstasher.objects.Item;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.AsyncApp42ServiceApi;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.OnFragmentInteractionListener;
import com.example.kitstasher.other.SelectDateFragment;
import com.example.kitstasher.other.ValueContainer;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import moe.feng.common.stepperview.VerticalStepperItemView;

import static android.R.drawable.ic_menu_camera;
import static android.app.Activity.RESULT_OK;
import static com.example.kitstasher.activity.MainActivity.REQUEST_CODE_CROP;
import static com.example.kitstasher.activity.MainActivity.asyncService;

//import com.example.kitstasher.activity.UCropActivity;

/**
 * Created by Алексей on 21.04.2017. Manual add items to database
 */

public class ManualAddFragment extends Fragment implements View.OnClickListener, TextWatcher,
        AsyncApp42ServiceApi.App42StorageServiceListener, OnFragmentInteractionListener
//        ,AdapterView.OnItemSelectedListener
{
    private View view;
    private EditText etBrandCat_no, etScale, etKitName, etKitNoengName, etNotes, etPrice;
    private Button btnCheckOnlineDatabase, btnContinueStep;
    //    private Button btnNextStepper1, btnNextStepper2, btnNextStepper3,
//    btnStepper2Back, btnStepper3Back, btnStepper4Back;
    private AppCompatSpinner spYear, spDescription, spQuantity, spCurrency, spKitMedia, spCategory;
    private ImageView ivGetBoxart;
    private TextView tvPurchaseDate, tvModeKit, tvModeAftermarket;
    private ProgressDialog progressDialog;
    private String imageFileName;

    private String aftermarketName, aftemarketOriginalName, compilanceWith;

    private String barcode, brand, brandCatno, kitName, sendStatus, kitNoengname, dateAdded, datePurchased,
            boxartUrl, category, boxartUri, fbId, description, year, onlineId, listname, notes,
    currency, prototype, scalematesUrl, placePurchased;
    private int status, media;

    private String defCurrency;
    private char editMode;
    private int scale, quantity, price;
    private int spCurrencyPosition;
    private boolean isFoundOnline, isReported, wasSearchedOnline;

    private Context context;
    private DbConnector dbConnector;

    ArrayAdapter currencyAdapter;

    List<String> myBrands;
    ArrayAdapter<String> acAdapterMybrands;
    AutoCompleteTextView acTvBrand;

    List<String> myShops;
    ArrayAdapter<String> acAdapterMyshops;
    AutoCompleteTextView acPurchasedFrom;

    private OnFragmentInteractionListener mListener;
    public static String manualTag;

    private Bitmap boxartPic;
    private ByteArrayOutputStream bytes;

    private Kit kit;
    private Aftermarket aftermarket;
    private ArrayAdapter<String> descriptionAdapter, yearsAdapter, mediaAdapter;

    static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath; //path for use with ACTION_VIEW intents

    private VerticalStepperItemView stepper_0, stepper_1, stepper_2, stepper_3, stepper_4;

    private String parseImageUrl, parseThumbnailUrl;
    private Uri photoPath;

    public ManualAddFragment() {

    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
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

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (boxartPic != null){
            outState.putParcelable(Constants.BOXART_IMAGE, boxartPic);
            outState.putString("imagePath", mCurrentPhotoPath);
        }
        if (kit != null){
            outState.putString(Constants.BOXART_URL, kit.getBoxart_url());
        }
        outState.putString(Constants.CATEGORY, category);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tabbed_manualadd, container, false);
//Соединение с локальной БД
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        manualTag = getTag(); //Receiving the tag for fragment (for passing data)

        initVariables();
        prepareMyList();
        checkMode();
        initUI();
        stepper_0.setState(1);
        stepper_0.setNormalColor(Color.GREEN);


        if (savedInstanceState != null){
            if (savedInstanceState.getString(Constants.BOXART_URL) != null) {
                mCurrentPhotoPath = savedInstanceState.getString("imagePath");
                Glide
                        .with(context)
                        .load(Constants.BOXART_URL_PREFIX
                                + savedInstanceState.getString(Constants.BOXART_URL)
                                + Constants.BOXART_URL_LARGE
                                + Constants.JPG)
                        .placeholder(ic_menu_camera)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivGetBoxart);
            }else if (savedInstanceState.getParcelable(Constants.BOXART_IMAGE) != null) {
                    boxartPic = savedInstanceState.getParcelable(Constants.BOXART_IMAGE);
                    ivGetBoxart.setImageBitmap(boxartPic);
            }
        }

        //Обрабатываем список автодополнения брэндов
        acAdapterMybrands = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, myBrands);
        acTvBrand.addTextChangedListener(this);
        acTvBrand.setAdapter(acAdapterMybrands);

        //Обрабатываем список автодополнения магазинов
        acAdapterMyshops = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, myShops);
        acPurchasedFrom.addTextChangedListener(this);
        acPurchasedFrom.setAdapter(acAdapterMyshops);


        if (!isOnline()) {
            btnCheckOnlineDatabase.setClickable(false);
//            btnCheckOnlineDatabase.setVisibility(View.GONE);
//            btnContinueStep.setVisibility(View.VISIBLE);
            stepper_0.setTitle(R.string.brand_and_name);
            setAllSteps(1);
        }
        if (editMode == Constants.MODE_AFTERMARKET) {
            setAftermarketMode();
        } else if (editMode == Constants.MODE_KIT) {
            setKitMode();
        }
//        else {
//            btnCheckOnlineDatabase.setVisibility(View.VISIBLE);
//            btnContinueStep.setVisibility(View.GONE);
//        }


        String[] descriptionItems = new String[]{getString(R.string.unknown),
                getString(R.string.newkit), getString(R.string.rebox)};
        descriptionAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.simple_spinner_item, descriptionItems);
        spDescription.setAdapter(descriptionAdapter);

        ArrayList<String> years = new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add(getString(R.string.unknown));
        for (int i = thisYear; i >= 1930; i--) {
            years.add(Integer.toString(i));
        }
        yearsAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.simple_spinner_item, years);
        spYear.setAdapter(yearsAdapter);

        Integer[] quants = new Integer[]{1,2,3,4,5,6,7,8,9,10};
        ArrayAdapter quantityAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.simple_spinner_item, quants);
        spQuantity.setAdapter(quantityAdapter);
        spQuantity.setSelection(0, true);

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
        int spCurrencyPosition = currencyAdapter.getPosition(defaultCurrency);
        spCurrency.setSelection(spCurrencyPosition);
//
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        defCurrency = sharedPreferences.getString(Constants.DEFAULT_CURRENCY, "");
//        spCurrencyPosition = currencyAdapter.getPosition(defCurrency);
//        spCurrency.setSelection(spCurrencyPosition);


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
        mediaAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item,
                mediaTypes);
        spKitMedia.setAdapter(mediaAdapter);
        spKitMedia.setSelection(1);


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

        return view;
    }
    // Проверяем, откуда обратились к редактору
    private void checkMode() {
        if (getArguments() != null){
            listname = getArguments().getString(Constants.LISTNAME);
            switch (getArguments().getChar(Constants.EDIT_MODE)){
                case 'l':
                    editMode = Constants.MODE_LIST;
                    break;
                case 'm':
                    editMode = Constants.MODE_KIT;
                    break;
                case 'a':
                    editMode = Constants.MODE_AFTERMARKET;
                    break;
                default:
                    editMode = Constants.MODE_KIT;
                    break;
            }
        }
    }

    private void initVariables() {
        context = getActivity();
        //Установка текущей даты для записи в локальную базу
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        //экономим обращения к онлайновой базе - если уже искали, не искать.
        wasSearchedOnline = false;
//        isRbChanged = false;
        isFoundOnline = false;
        isReported = false;
        sendStatus = "";//Статус для последующей записи пропущенных в офлайне записей
        editMode = Constants.MODE_KIT; //// TODO: 06.09.2017 Check

        brand = "";
        brandCatno = "";
        scale = 0;
        if (editMode == Constants.MODE_AFTERMARKET) {
            scale = getArguments().getInt(Constants.SCALE);
            etScale.setText(String.valueOf(scale));
        }
        kitName = "";
        kitNoengname = "";
        boxartUrl = "";
        boxartUri = "";

        if (getArguments() != null && getArguments().getChar(Constants.EDIT_MODE) == Constants.MODE_AFTERMARKET//???
                && getArguments().getString(Constants.BOXART_URI) != null) {
            boxartUri = getArguments().getString(Constants.BOXART_URI);

        }

        dateAdded = df.format(c.getTime());
        barcode = "";

        if (getArguments() != null && getArguments().getChar(Constants.EDIT_MODE) == Constants.MODE_LIST
                && getArguments().getString("barcode") != null){
            barcode = getArguments().getString("barcode");
        }
        description = "";
        year = "0";
//        category = Constants.CAT_OTHER;
        category = Constants.CODE_OTHER;
        onlineId = "";
        price = 0;
        notes = "";
        datePurchased = "";
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        currency = sharedPref.getString(Constants.DEFAULT_CURRENCY,"");
        quantity = 1;
        prototype = "";
        scalematesUrl = "";
        placePurchased = "";
        status = Constants.STATUS_NEW;
        media = Constants.M_CODE_INJECTED;


        //New kit with empty fields
        kit = new Kit.KitBuilder()
                .hasBrand(brand)
                .hasBrand_catno(brandCatno)
                .hasKit_name(kitName)
                .hasScale(scale)
                .hasCategory(category)
                .hasBarcode(barcode)
                .hasKit_noeng_name(kitNoengname)
                .hasDescription(description)

                .hasPrototype("")//not in use

                .hasSendStatus(sendStatus)

                .hasBoxart_url(boxartUrl)
                .hasBoxart_uri(boxartUri)
                .hasScalemates_url("")
                .hasYear(year)
                .hasOnlineId(onlineId)
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

//        if (editMode == Constants.MODE_AFTERMARKET) {
            aftermarketName = "";
            aftemarketOriginalName = "";
            compilanceWith = "";
            aftermarket = new Aftermarket.AftermarketBuilder()
                    .hasBrand(brand)
                    .hasBrandCatno(brandCatno)
                    .hasAftermarketName(aftermarketName)
                    .hasScale(scale)
                    .hasCategory(category)
                    .hasBarcode(barcode)
                    .hasAftermarketOriginalName(aftemarketOriginalName)
                    .hasDescription(description)
                    .hasCompilance(compilanceWith)
                    .hasBoxartUrl(boxartUrl)
                    .hasScalematesUrl(scalematesUrl)
                    .hasBoxartUri(boxartUri)
                    .hasYear(year)
                    .hasOnlineId(onlineId)
                    .hasDateAdded(dateAdded)
                    .hasDatePurchased(datePurchased)
                    .hasQuantity(quantity)
                    .hasNotes(notes)
                    .hasPrice(price)
                    .hasCurrency(currency)
                    .hasSendStatus(sendStatus)
                    .hasPlacePurchased(placePurchased)
                    .hasStatus(status)
                    .hasMedia(media)
                    .hasListname(listname)
                    .build();
    }

    private void initUI() {
        btnCheckOnlineDatabase = view.findViewById(R.id.btnCheckOnlineDb);
        btnCheckOnlineDatabase.setOnClickListener(this);
//        btnContinueStep = view.findViewById(R.id.btnContinueStep);
//        btnContinueStep.setOnClickListener(this);
        Button btnAdd = view.findViewById(R.id.btnMAdd);
        btnAdd.setOnClickListener(this);
        Button btnCancel = view.findViewById(R.id.btnMCancel);
        btnCancel.setOnClickListener(this);
        ivGetBoxart = view.findViewById(R.id.ivGetBoxart);
        ivGetBoxart.setOnClickListener(this);
        etBrandCat_no = view.findViewById(R.id.etBrandCat_no);
        etScale = view.findViewById(R.id.etScale);
        etKitName = view.findViewById(R.id.etKitName);
        etKitNoengName = view.findViewById(R.id.etKitNoengName);
        spDescription = view.findViewById(R.id.spDescription);
        spYear = view.findViewById(R.id.spYear);
        spCategory = view.findViewById(R.id.spCategory);


        acTvBrand = view.findViewById(R.id.acTvBrand);
        acPurchasedFrom = view.findViewById(R.id.acPlacePurchased);
        spCurrency = view.findViewById(R.id.spCurrency);
        spQuantity = view.findViewById(R.id.spQuantity);
        spKitMedia = view.findViewById(R.id.spAfterMedia);

        etNotes = view.findViewById(R.id.etNotes);
        tvPurchaseDate = view.findViewById(R.id.tvPurchaseDate);
        tvPurchaseDate.setText(R.string.Date_not_set);
        tvPurchaseDate.setOnClickListener(this);
        etPrice = view.findViewById(R.id.etPrice);
        Button btnClearDate = view.findViewById(R.id.btnClearDate);
        btnClearDate.setOnClickListener(this);

        stepper_0 = view.findViewById(R.id.stepper_0);
        stepper_0.setPadding(0, 16, 0, 0);
        stepper_1 = view.findViewById(R.id.stepper_1);
        stepper_2 = view.findViewById(R.id.stepper_2);
        stepper_3 = view.findViewById(R.id.stepper_3);
        stepper_4 = view.findViewById(R.id.stepper_4);
        stepper_4.setIsLastStep(true);

//        btnNextStepper1 = view.findViewById(R.id.btnNextStepper1);
//        btnNextStepper1.setOnClickListener(this);
//        btnNextStepper2 = view.findViewById(R.id.btnNextStepper2);
//        btnNextStepper2.setOnClickListener(this);
//        btnNextStepper3 = view.findViewById(R.id.btnNextStepper3);
//        btnNextStepper3.setOnClickListener(this);
//
//        btnStepper2Back = view.findViewById(R.id.btnStepper2Back);
//        btnStepper2Back.setOnClickListener(this);
//        btnStepper3Back = view.findViewById(R.id.btnStepper3Back);
//        btnStepper3Back.setOnClickListener(this);
//        btnStepper4Back = view.findViewById(R.id.btnStepper4Back);
//        btnStepper4Back.setOnClickListener(this);

        tvModeKit = view.findViewById(R.id.tvModeKit);
        tvModeKit.setOnClickListener(this);
        tvModeAftermarket = view.findViewById(R.id.tvModeAftermarket);
        tvModeAftermarket.setOnClickListener(this);
    }


    //Подготовка автодополняемого списка брэндов и магазинов
    private void prepareMyList() {
        myBrands = new ArrayList<>();
        myBrands = DbConnector.getAllBrands();
        myShops = new ArrayList<>();
        myShops = DbConnector.getAllShops();
    }

    //Проверки заполнения полей
    private boolean checkSearchFields() {
        boolean check = true; //Если true, проверка пройдена, можно записывать

        if (TextUtils.isEmpty(acTvBrand.getText())) {
            acTvBrand.setError(getString(R.string.enter_brand));
//            cancel = true;
            check = false;
        }
        if (TextUtils.isEmpty(etBrandCat_no.getText())) {
            etBrandCat_no.setError(getString(R.string.enter_cat_no));
//            cancel = true;
            check = false;
        }
        return check;
    }

    private void removeWarnings() {
        acTvBrand.setError(null);
        etBrandCat_no.setError(null);
        etScale.setError(null);
        etKitName.setError(null);
    }

    private boolean checkAllFields() {
        boolean check = true;
        if (TextUtils.isEmpty(acTvBrand.getText())) {
            acTvBrand.setError(getString(R.string.enter_brand));
            check = false;
        }
        if (TextUtils.isEmpty(etBrandCat_no.getText())) {
            etBrandCat_no.setError(getString(R.string.enter_cat_no));
            check = false;
        }
        if (TextUtils.isEmpty(etScale.getText()) || etScale.getText().toString().equals("0")) {
            etScale.setError(getString(R.string.enter_scale));
            check = false;
        }
        if (TextUtils.isEmpty(etKitName.getText())) {
            etKitName.setError(getString(R.string.enter_kit_name));
            check = false;
        }
        if (boxartPic == null && !wasSearchedOnline){
            Toast.makeText(getActivity(), "Please add boxart photo", Toast.LENGTH_SHORT).show();
            check = false;
        }
        return check;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.btnMCancel):
                clearFields();
                removeWarnings();
                returnToScan();

            case (R.id.btnCheckOnlineDb):
                //todo Первый вариант - баркода нет, интернет есть.
                if (checkSearchFields()) {
                    if (isOnline()) {
                        if (isInLocalBase(acTvBrand.getText().toString().trim(),
                                etBrandCat_no.getText().toString().trim())) {
                            Toast.makeText(getActivity(), R.string.entry_already_exist,
                                    Toast.LENGTH_SHORT).show();
                            break;
                        } else {//ищем онлайн
                            Query q1 = QueryBuilder.build(Constants.TAG_BRAND, acTvBrand.getText().toString().trim(),
                                    QueryBuilder.Operator.EQUALS);
                            Query q2 = QueryBuilder.build(Constants.TAG_BRAND_CATNO, etBrandCat_no.getText().toString().trim(),
                                    QueryBuilder.Operator.EQUALS);
                            Query query = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
                            asyncService.findDocByQuery(Constants.App42DBName, Constants.CollectionName, query, this);
 //проведен поиск в онлайне //todo вернуть фолс если изменяли текст
                        }

                    } else {
                        //нет интернета, кнопка и так не активна
                        if (isInLocalBase(brand, brandCatno)) {
                            Toast.makeText(getActivity(), R.string.entry_already_exist,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Please enter data",
                            Toast.LENGTH_SHORT).show();
                }

                break;

            case (R.id.btnMAdd):
                //Обработка пополнения списка автодополнения брэндов
                String newBrand = acTvBrand.getText().toString().trim();

                if (!myBrands.contains(newBrand)) {
                    //Срабатывает немедленно
                    if (newBrand.length() > 1) {
                        myBrands.add(newBrand);
                        //Срабатывает при перезапуске, записывает в базу
                        // и при следующем запуске уже берет из нее
                        dbConnector.addBrand(newBrand);
                    }
                    acAdapterMybrands = new ArrayAdapter<>(
                            getActivity(),
                            android.R.layout.simple_dropdown_item_1line, myBrands);
                    acTvBrand.setAdapter(acAdapterMybrands);
                }

                //Обработка пополнения списка магазинов
                String newShop = acPurchasedFrom.getText().toString().trim();

                if (!myShops.contains(newShop)) {
                    //Срабатывает немедленно
                    if (newShop.length() > 1) {
                        myShops.add(newShop);
                        //Срабатывает при перезапуске, записывает в базу
                        // и при следующем запуске уже берет из нее
                        dbConnector.addShop(newShop);
                    }
                    acAdapterMyshops = new ArrayAdapter<>(
                            getActivity(),
                            android.R.layout.simple_dropdown_item_1line, myShops);
                    acPurchasedFrom.setAdapter(acAdapterMyshops);
                }

                //******************************************************************
                if (checkAllFields()) {//Проверяем все поля - начальная проверка
                    getFieldsValues(); //getting data from form fields and prepare kit object
                    ////////////////////////
                    if (editMode == Constants.MODE_KIT) {//из ручного добавления
                        if (!isInLocalBase(kit.getBrand(), kit.getBrandCatno())) {
                            if (isOnline()) {
                                writeToLocalDatabase(kit); //writes kit to database
                                if (wasSearchedOnline && !isFoundOnline) {
                                    if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
                                        saveWithBoxartToParse(mCurrentPhotoPath, kit);
                                    }
                                    clearFields();
                                    returnToScan();
                                    Toast.makeText(getActivity(), R.string.kit_added, Toast.LENGTH_SHORT).show();
                                } else {
                                    saveToOnlineStash(kit); //save online in personal list
                                    clearFields();
                                    returnToScan();
                                    Toast.makeText(getActivity(), R.string.kit_added, Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                sendStatus = "n";//Надо потом записать в облако
                                writeToLocalDatabase(kit);
                                Toast.makeText(getActivity(), R.string.kit_added, Toast.LENGTH_SHORT).show();
                                sendStatus = "";
                                clearFields();
                                returnToScan();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.entry_already_exist,
                                    Toast.LENGTH_SHORT).show();
                            break;
                        }
                    } else if (editMode == Constants.MODE_LIST) { //из списков
                        //todo проверка на наличие в списке?
                        writeToLocalDatabase(kit);
                        Toast.makeText(getActivity(), R.string.Kit_added_to_list, Toast.LENGTH_SHORT).show();
                        sendStatus = "";
                        clearFields();
                        returnToScan();

                        ListViewFragment listViewFragment = new ListViewFragment();
                        Bundle bundle = new Bundle(1);
                        bundle.putString(Constants.LISTNAME, listname);
                        listViewFragment.setArguments(bundle);
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.llListsContainer, listViewFragment);
                        fragmentTransaction.commit();

                    } else if (editMode == Constants.MODE_AFTERMARKET) {
                        if (!isInLocalBase(kit.getBrand(), kit.getBrandCatno())) {


                            //запись в афтемаркет
                            writeToLocalDatabase(aftermarket);
                            Toast.makeText(getActivity(), R.string.Kit_added_to_list, Toast.LENGTH_SHORT).show();
                            sendStatus = "";
                            clearFields();

                            if (getArguments() != null) {
                                int position = getArguments().getInt(Constants.LIST_POSITION);
                                int categoryToReturn = getArguments().getInt(Constants.LIST_CATEGORY);
                                long after_id = getArguments().getLong(Constants.AFTER_ID);
                                long kit_id = getArguments().getLong(Constants.ID);


                                ItemEditFragment itemEditFragment = new ItemEditFragment();

                                Bundle bundle = new Bundle(5);
                                bundle.putInt(Constants.POSITION, position);
                                bundle.putInt(Constants.CATEGORY, categoryToReturn);
                                bundle.putLong(Constants.ID, kit_id);
                                bundle.putLong(Constants.AFTER_ID, after_id);
//                                bundle.putChar(Constants.EDIT_MODE, Constants.MODE_KIT);
                                bundle.putChar(Constants.EDIT_MODE, Constants.MODE_AFTERMARKET);
                                itemEditFragment.setArguments(bundle);

                                android.support.v4.app.FragmentTransaction fragmentTransaction =
                                        getFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.frameLayoutEditContainer, itemEditFragment);
                                fragmentTransaction.commit();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.entry_already_exist,
                                Toast.LENGTH_SHORT).show();
                        break;
                    }

                } else {
                    //Проверяем все поля - начальная проверка не пройдена!
                    Toast.makeText(getActivity(), R.string.Please_enter_data, Toast.LENGTH_SHORT).show();
                    setAllSteps(1);
                }
                break;

            case R.id.ivGetBoxart:

                try {
                    dispatchTakePictureIntent();
                } catch (ActivityNotFoundException e) {
                    // Выводим сообщение об ошибке
                    String errorMessage = getString(R.string.camera_failure);
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tvPurchaseDate:
                DialogFragment newFragment = new SelectDateFragment();
                Bundle bundle = new Bundle(1);
                bundle.putString("caller", "manualadd"); //// TODO: 30.08.2017 поменять на тэг
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "DatePicker");
                break;

            case R.id.btnClearDate:
                datePurchased = "";
                tvPurchaseDate.setText(R.string.Date_not_set);
                break;

//            case R.id.btnNextStepper1:
//                if(isBoxartSet()) {
//                    setAllSteps(0);
//                    stepper_0.setState(2);
//                    stepper_1.setState(2);
//                    stepper_2.setState(1);
//                }
//                break;
//            case R.id.btnNextStepper2:
//                setAllSteps(0);
//                stepper_0.setState(2);
//                stepper_1.setState(2);
//                stepper_2.setState(2);
//                stepper_3.setState(1);
//                break;
//            case R.id.btnNextStepper3:
//                setAllSteps(0);
//                stepper_0.setState(2);
//                stepper_1.setState(2);
//                stepper_2.setState(2);
//                stepper_3.setState(2);
//                stepper_4.setState(1);
//                break;
//
//            case R.id.btnContinueStep:
//                if (checkSearchFields()) {
//                    setAllSteps(0);
//                    stepper_0.setState(2);
//                    stepper_1.setState(1);
//                }
//                break;
//
//            case R.id.btnStepper2Back://todo доделать
//                stepper_1.setState(1);
//                break;
//            case R.id.btnStepper3Back:
//                stepper_2.setState(1);
//                break;
//            case R.id.btnStepper4Back:
//                stepper_3.setState(1);
//                break;
//
            case R.id.tvModeKit:
                setKitMode();
                break;
            case R.id.tvModeAftermarket:
                setAftermarketMode();
                break;
        }
    }

    private boolean isBoxartSet() {
        return (boxartPic != null);
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
//        Toast.makeText(context, mCurrentPhotoPath, Toast.LENGTH_LONG).show();
        return image;
    }

    private String getTimestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }


    /*
* Writes kit object to local Sqlite database*/
    private void writeToLocalDatabase(Object itemSave) {
        if (editMode == Constants.MODE_KIT) {
            dbConnector.addKitRec((Kit) itemSave);
        } else if (editMode == Constants.MODE_LIST) {
            dbConnector.addListItem((Kit)itemSave, listname);
        } else if (editMode == Constants.MODE_AFTERMARKET) {
            long aftId = dbConnector.addAftermarket((Aftermarket)itemSave);
            if (getArguments() != null && getArguments().containsKey(Constants.ID)) {
                Long incomeKitId = getArguments().getLong(Constants.ID);
                dbConnector.addAfterToKit(incomeKitId, aftId);
            }
        }
    }

    /*Checks if local database already includes this kit*/
    private boolean isInLocalBase(String brand, String brand_catno) {
        if (editMode == Constants.MODE_LIST) {
            if (dbConnector.searchListForDoubles(listname, brand, brand_catno)) {
                return true;
            }
        } else if (editMode == Constants.MODE_KIT) {
            if (dbConnector.searchForDoubles(editMode, brand, brand_catno)) {
                return true;
            }
        } else if (editMode == Constants.MODE_AFTERMARKET) {
            if (dbConnector.searchForDoubles(editMode, brand, brand_catno)) {
                return true;
            }
        }
        return false;
    }

    private void setAllSteps(int state) {
        stepper_0.setState(state);
        stepper_1.setState(state);
        stepper_2.setState(state);
        stepper_3.setState(state);
        stepper_4.setState(state);
    }


    private void getFieldsValues() {
        if (editMode == Constants.MODE_AFTERMARKET) {
            aftermarket.setBrand(acTvBrand.getText().toString().trim());
            aftermarket.setBrandCatno(etBrandCat_no.getText().toString().trim());
            aftermarket.setScale(Integer.parseInt(etScale.getText().toString()));
            aftermarket.setAftermarketName(etKitName.getText().toString().trim());
            aftermarket.setAftemarketOriginalName(etKitNoengName.getText().toString().trim());
            aftermarket.setBarcode(barcode);

            String cat = String.valueOf(spCategory.getSelectedItemPosition());
            aftermarket.setCategory(cat);
            String y = spYear.getSelectedItem().toString();
            if (!y.equals(getString(R.string.unknown))) {
                aftermarket.setYear(y);
            }else{
                aftermarket.setYear("");
            }

            aftermarket.setMedia(spKitMedia.getSelectedItemPosition());

            String d = spDescription.getSelectedItem().toString();
            if (!d.equals(getString(R.string.kittype))){
                aftermarket.setDescription(descToCode(d));
            }else{
                aftermarket.setDescription(Constants.CODE_OTHER);
            }
            currency = spCurrency.getSelectedItem().toString();
            aftermarket.setCurrency(currency);
            quantity = spQuantity.getSelectedItemPosition() + 1;
            aftermarket.setQuantity(quantity);
            notes = etNotes.getText().toString();
            aftermarket.setNotes(notes);
            if (!etPrice.getText().toString().equals("")){
                price = Integer.parseInt(etPrice.getText().toString()) * 100;
            }else{
                price = 0;
            }
            aftermarket.setPrice(price);
            if (!tvPurchaseDate.getText().toString().equals("")
                    && tvPurchaseDate.getText().toString().equals(R.string.Date_not_set)) {
                datePurchased = tvPurchaseDate.getText().toString();
            }else{
                datePurchased = "";
            }

            placePurchased = acPurchasedFrom.getText().toString().trim();
            aftermarket.setPlacePurchased(placePurchased);

            aftermarket.setDatePurchased(datePurchased);
            aftermarket.setBoxartUri(boxartUri);
            aftermarket.setBoxartUrl(boxartUrl);
            aftermarket.setListname(listname);

        }else{
            kit.setBrand(acTvBrand.getText().toString().trim());
            kit.setBrandCatno(etBrandCat_no.getText().toString().trim());
            kit.setScale(Integer.parseInt(etScale.getText().toString()));
            kit.setKit_name(etKitName.getText().toString().trim());
            kit.setKit_noeng_name(etKitNoengName.getText().toString().trim());
            kit.setBarcode(barcode);
            kit.setCategory(category);
            String y = spYear.getSelectedItem().toString();
            if (!y.equals(getString(R.string.unknown))) {
                kit.setYear(y);
            }else{
                kit.setYear("");
            }
            kit.setScalemates_url(scalematesUrl);

            kit.setMedia(spKitMedia.getSelectedItemPosition());

            String d = spDescription.getSelectedItem().toString();
            if (!d.equals(getString(R.string.kittype))){
                kit.setDescription(descToCode(d));
            }else{
                kit.setDescription(Constants.CODE_OTHER);
            }
            currency = spCurrency.getSelectedItem().toString();
            kit.setCurrency(currency);
            quantity = spQuantity.getSelectedItemPosition() + 1;
            kit.setQuantity(quantity);
            notes = etNotes.getText().toString();
            kit.setNotes(notes);
            if (!etPrice.getText().toString().equals("")){
                price = Integer.parseInt(etPrice.getText().toString()) * 100;
            }else{
                price = 0;
            }
            kit.setPrice(price);
            //SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            if (!tvPurchaseDate.getText().toString().equals("")
                    && tvPurchaseDate.getText().toString().equals(R.string.Date_not_set)) {
                datePurchased = tvPurchaseDate.getText().toString();
            }else{
                datePurchased = "";
            }

            placePurchased = acPurchasedFrom.getText().toString().trim();
            kit.setPlacePurchased(placePurchased);

            kit.setDatePurchased(datePurchased);
            kit.setBoxart_uri(boxartUri);
            kit.setBoxart_url(boxartUrl);
        }
    }

    private String descToCode(String d) {
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

    public boolean isOnline() {//// TODO: 06.09.2017 Helper
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    //Очистка полей и переменных
    private void clearFields() {

        //Очищаем поля
        acTvBrand.setText("");
        acTvBrand.setError(null);
        etBrandCat_no.setText("");
        etBrandCat_no.setError(null);
        etKitNoengName.setText("");
        etKitNoengName.setError(null);
        etScale.setText("");
        etKitName.setText("");
        isFoundOnline = false;
        wasSearchedOnline = false;
        ivGetBoxart.setImageResource(R.drawable.ic_menu_camera);
        ivGetBoxart.setBackgroundResource(R.drawable.button);
        spDescription.setSelection(0);
        spYear.setSelection(0);

        spQuantity.setSelection(0);
        etNotes.setText("");
        etPrice.setText("");

        tvPurchaseDate.setText(R.string.Date_not_set);
        acPurchasedFrom.setText("");

        setAllSteps(0);
        stepper_0.setState(1);
        kit = new Kit.KitBuilder().build();
        //экономим обращения к онлайновой базе - если уже искали, не искать.
        wasSearchedOnline = false;
//        isRbChanged = false;
        isFoundOnline = false;
        isReported = false;
        sendStatus = "";//Статус для последующей записи пропущенных в офлайне записей
        editMode = 'm';

        brand = "";
        brandCatno = "";
        scale = 0;
        kitName = "";
        kitNoengname = "";
        boxartUrl = "";
        boxartUri = "";
        barcode = "";
        description = "";
        year = "0";
        category = Constants.CODE_OTHER;
//        category = Constants.CAT_OTHER;
        onlineId = "";
        price = 0;
        notes = "";
        datePurchased = dateAdded;
        currency = "";
        quantity = 1;
        prototype = "";
        sendStatus = "";//Статус для последующей записи пропущенных в офлайне записей
        scalematesUrl = "";

        spCurrencyPosition = currencyAdapter.getPosition(defCurrency);
        spCurrency.setSelection(spCurrencyPosition);

        placePurchased = "";
        acPurchasedFrom.setText(placePurchased);

        status = Constants.STATUS_NEW;
        media = Constants.M_CODE_INJECTED;


//обнуляем значения кита
        kit.setBrand(brand);
        kit.setBrandCatno(brandCatno);
        kit.setKit_name(kitName);
        kit.setScale(scale);
        kit.setCategory(category);
        //Optional
        kit.setBarcode(barcode);
        kit.setKit_noeng_name(kitNoengname);
        kit.setDescription(description);
        kit.setPrototype(prototype);
        kit.setBoxart_url(boxartUrl);
        kit.setScalemates_url(scalematesUrl);
        kit.setBoxart_uri(boxartUri);
        kit.setYear(year);
        kit.setOnlineId(onlineId);

        kit.setDate_added(dateAdded);
        kit.setDatePurchased(datePurchased);
        kit.setQuantity(quantity);
        kit.setNotes(notes);
        kit.setPrice(price);
        kit.setCurrency(currency);
        kit.setSendStatus(sendStatus);
        kit.setPlacePurchased(placePurchased);
        kit.setStatus(status);
        kit.setMedia(media);

        aftermarketName = "";
        aftemarketOriginalName = "";
        compilanceWith = "";
        listname = "";

        aftermarket.setBrand(brand);
        aftermarket.setBrandCatno(brandCatno);
        aftermarket.setAftermarketName(aftermarketName);
        aftermarket.setScale(scale);
        aftermarket.setCategory(category);
        aftermarket.setBarcode(barcode);
        aftermarket.setAftemarketOriginalName(aftemarketOriginalName);
        aftermarket.setDescription(description);
        aftermarket.setCompilanceWith(compilanceWith);
        aftermarket.setBoxartUrl(boxartUrl);
        aftermarket.setScalematesUrl(scalematesUrl);
        aftermarket.setBoxartUri(boxartUri);
        aftermarket.setYear(year);
        aftermarket.setOnlineId(onlineId);
        aftermarket.setDateAdded(dateAdded);
        aftermarket.setDatePurchased(datePurchased);
        aftermarket.setQuantity(quantity);
        aftermarket.setNotes(notes);
        aftermarket.setPrice(price);
        aftermarket.setCurrency(currency);
        aftermarket.setSendStatus(sendStatus);
        aftermarket.setPlacePurchased(placePurchased);
        aftermarket.setListname(listname);
        aftermarket.setStatus(status);
        aftermarket.setMedia(media);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("barcode").commit();
    }


    private void returnToScan() {
        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        AdapterAddFragment adapterAddFragment = new AdapterAddFragment(fragmentManager, getActivity());
        adapterAddFragment.openScan();
    }

    private void checkBrandBcPrefix(String bcPrefix) {
//// TODO: 04.05.2017 вынести в БД
        //подстановка брэнда по префиксу баркода
        switch (bcPrefix) {
            default:
//                acTvBrand.setText(bundle.getString("brand"));
                acTvBrand.setText("");
                break;
//AOSHIMA+
            case "905083":
                acTvBrand.setText("Aoshima");
                break;
//ACADEMY+
            case "035501":
                acTvBrand.setText("Academy");
                break;
            case "809258":
                acTvBrand.setText("Academy");
                break;
//AIRFIX+
            case "924918":
                acTvBrand.setText("Airfix");
                break;
            case "816243":
                acTvBrand.setText("Airfix");
                break;
            case "542957":
                acTvBrand.setText("Airfix");
                break;
            case "431001":
                acTvBrand.setText("Airfix");
                break;
            case "345483":
                acTvBrand.setText("Airfix");
                break;
            case "0553891":
                acTvBrand.setText("Airfix");
                break;
            case "055297":
                acTvBrand.setText("Airfix");
                break;
            case "055288":
                acTvBrand.setText("Airfix");
                break;
            case "014429":
                acTvBrand.setText("Airfix");
                break;
            case "335993":
                acTvBrand.setText("Airfix");
                break;
            case "885922":
                acTvBrand.setText("Airfix");
                break;
            case "793625":
                acTvBrand.setText("Airfix");
                break;
            case "785924":
                acTvBrand.setText("Airfix");
                break;
            case "754295":
                acTvBrand.setText("Airfix");
                break;
            case "505395":
                acTvBrand.setText("Airfix");
                break;
            case "505375":
                acTvBrand.setText("Airfix");
                break;
            case "501442":
                acTvBrand.setText("Airfix");
                break;
            case "088567":
                acTvBrand.setText("Airfix");
                break;
            case "088533":
                acTvBrand.setText("Airfix");
                break;
            case "079795":
                acTvBrand.setText("Airfix");
                break;
            case "079249":
                acTvBrand.setText("Airfix");
                break;
            case "078592":
                acTvBrand.setText("Airfix");
                break;
            case "078523":
                acTvBrand.setText("Airfix");
                break;
            case "078371":
                acTvBrand.setText("Airfix");
                break;
            case "078332":
                acTvBrand.setText("Airfix");
                break;
            case "078236":
                acTvBrand.setText("Airfix");
                break;
            case "078162":
                acTvBrand.setText("Airfix");
                break;
            case "076748":
                acTvBrand.setText("Airfix");
                break;
            case "075429":
                acTvBrand.setText("Airfix");
                break;
            case "071608":
                acTvBrand.setText("Airfix");
                break;
            case "071593":
                acTvBrand.setText("Airfix");
                break;
            case "0433599":
                acTvBrand.setText("Airfix");
                break;
            case "020254":
                acTvBrand.setText("Airfix");
                break;
            case "052615":
                acTvBrand.setText("Airfix");
                break;

//HASEGAWA+
            case "853171":
                acTvBrand.setText("Hasegawa");
                break;
            case "852618":
                acTvBrand.setText("Hasegawa");
                break;
            case "982771":
                acTvBrand.setText("Hasegawa");
                break;
            case "982568":
                acTvBrand.setText("Hasegawa");
                break;
            case "958649":
                acTvBrand.setText("Hasegawa");
                break;

            case "877997":
                acTvBrand.setText("Hasegawa");
                break;
            case "875517":
                acTvBrand.setText("Hasegawa");
                break;
            case "431000":
                acTvBrand.setText("Hasegawa");
                break;
            case "384990"://?Trumpeter
                acTvBrand.setText("Hasegawa");
                break;
            case "967834":
                acTvBrand.setText("Hasegawa");
                break;
            case "200000":
                acTvBrand.setText("Hasegawa");
                break;

//RODEN
            case "482301":
                acTvBrand.setText("Roden");
                break;
            case "742655":
                acTvBrand.setText("Roden");
                break;
            case "823017":
                acTvBrand.setText("Roden");
                break;
//TRUMPETER+
            case "580208":
                acTvBrand.setText("Trumpeter");
                break;
            case "939319":
                acTvBrand.setText("Trumpeter");
                break;
            case "875519":
                acTvBrand.setText("Trumpeter");
                break;
            case "922803":
                acTvBrand.setText("Trumpeter");
                break;


//ITALERI+
            case "001283":
                acTvBrand.setText("Italeri");
                break;

//REVELL+
            case "009803":
                acTvBrand.setText("Revell");
                break;
            case "765130"://?
                acTvBrand.setText("Revell");
                break;
            case "3144505"://?
                acTvBrand.setText("Revell");
                break;

        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    /////////////////////CLOUD

/*
* As user can change text in fields manually, we don't add data to Kit object directly, just show
* variants in dialog. Only boxart url is added at once.*/
    @Override
    public void onFindDocSuccess(Storage response){
        String showKit;
        final List<Kit> itemsToShow = new ArrayList<>();
        final List<Item> itemList = new ArrayList<Item>();

        wasSearchedOnline = true;
        isFoundOnline = true;
        final ValueContainer <String> urlContainer = new ValueContainer<>();
        urlContainer.setVal("");
        final ValueContainer<String> categoryContainer;
        categoryContainer = new ValueContainer<>();
        final ValueContainer<String> barcodeContainer = new ValueContainer<>();

        //Нашли по коду в базе
        ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
        for (int i = 0; i < jsonDocList.size(); i++) {
            String in = jsonDocList.get(i).getJsonDoc();
            try {
                JSONObject reader = new JSONObject(in);
                //Containers should be first in list, not working otherwise
                urlContainer.setVal(reader.getString(Constants.TAG_BOXART_URL));
//                categoryContainer.setVal(Helper.codeToTag(reader.getString(Constants.TAG_CATEGORY)));
                categoryContainer.setVal(reader.getString(Constants.TAG_CATEGORY));

                barcodeContainer.setVal(reader.getString(Constants.TAG_BARCODE));

                brand = reader.getString(Constants.TAG_BRAND);
                brandCatno = reader.getString(Constants.TAG_BRAND_CATNO);
                scale = reader.getInt(Constants.TAG_SCALE);
                kitName = reader.getString(Constants.TAG_KIT_NAME);
                kitNoengname = reader.getString(Constants.TAG_NOENG_NAME);
                barcode = reader.getString(Constants.TAG_BARCODE);
                description = reader.getString(Constants.TAG_DESCRIPTION);
                year = reader.getString(Constants.TAG_YEAR);
                scalematesUrl = reader.getString(Constants.TAG_SCALEMATES_PAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showKit = kitName + ", " + brand
                    + ", " + brandCatno + ", "
                    + "1/" + String.valueOf(scale);

            Item item = new Item(urlContainer.getVal(), showKit);
            itemList.add(item);

            Kit kitToShow = new Kit.KitBuilder()
                    .hasBrand(brand)
                    .hasBrand_catno(brandCatno)
                    .hasKit_name(kitName)
                    .hasScale(scale)
                    .hasDescription(description)
                    .hasCategory(categoryContainer.getVal())
                    .hasKit_noeng_name(kitNoengname)
                    .hasBoxart_url(urlContainer.getVal())
                    .hasBarcode(barcodeContainer.getVal())
                    .hasScalemates_url(scalematesUrl)

                    .hasYear(year)
                    .build();
            itemsToShow.add(kitToShow);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.Found);
        AdapterAlertDialog adapterAlertDialog = new AdapterAlertDialog(getActivity(), itemList);

        builder.setAdapter(adapterAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                setAllSteps(1);
                    Kit kitToAdd = itemsToShow.get(item);
                    etScale.setText(String.valueOf(kitToAdd.getScale()));
                    etKitName.setText(kitToAdd.getKit_name());
                    etKitNoengName.setText(kitToAdd.getKit_noeng_name());
                setKitYear(kitToAdd.getYear());
                setDescription(kitToAdd.getDescription());

                    barcode = kitToAdd.getBarcode();//todo???

                if (wasSearchedOnline && isFoundOnline) {
                    boxartUrl = kitToAdd.getBoxart_url();
                    kit.setBoxart_url(kitToAdd.getBoxart_url());
                }else{
                    kit.setBoxart_url("");
                }
                    Glide
                            .with(context)
                            .load(Constants.BOXART_URL_PREFIX
                                    + kitToAdd.getBoxart_url()
                                    + Constants.BOXART_URL_LARGE
                                    + Constants.JPG)
                            .placeholder(ic_menu_camera)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(ivGetBoxart);
                }
        });

        ivGetBoxart.setBackgroundResource(0);
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void setDescription(String description) {
        if (!description.equals("")) {
            String desc = "";
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
//                        desc = getString(R.string.new_decal);
                        desc = getString(R.string.rebox);
//                        desc = getString(R.string.rebox);
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
//                        desc = getString(R.string.reissue);
                        desc = getString(R.string.rebox);

                }
            }else{
                desc = getString(R.string.kittype);
            }
            int spDescPosition = descriptionAdapter.getPosition(desc);
            spDescription.setSelection(spDescPosition);
        }else{
            spDescription.setSelection(0);
        }
    }

    private void setKitYear(String year) {
        if (year.length() == 4 && !year.contains("-")) {
            int spYearPosition = yearsAdapter.getPosition(year);
            spYear.setSelection(spYearPosition);
        }else{
            spYear.setSelection(0); //оставляем на первой
        }
    }


    @Override
    public void onFindDocFailed(App42Exception ex) {
        setAllSteps(1);
//        stepper_1.setState(1);
        Toast.makeText(getActivity(),
                R.string.nothing_found_online,
                Toast.LENGTH_SHORT).show();
        wasSearchedOnline = true;
        isFoundOnline = false;
    }

    @Override
    public void onDocumentInserted(Storage response) {

    }

    @Override
    public void onUpdateDocSuccess(Storage response) {

    }

    @Override
    public void onInsertionFailed(App42Exception ex) {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), "Saving failed. Please, try again!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {

    }

    // Passing data from scanFragment In the child fragment.
    public void onAttachToParentFragment(Fragment fragment) {
        try {
            mListener = (OnFragmentInteractionListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    fragment.toString() + " must implement OnPlayerSelectionSetListener");
        }
    }

    @Override
    public void onFragmentInteraction(String b) {
        checkBrandBcPrefix(b.substring(1, 7));
        barcode = b;
        wasSearchedOnline = true;
        isFoundOnline = false;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == MainActivity.REQUEST_CODE_CAMERA
                ) {
            Intent cropIntent = new Intent(getActivity(), CropActivity.class);
            cropIntent.putExtra(Constants.FILE_URI, mCurrentPhotoPath);
            startActivityForResult(cropIntent, REQUEST_CODE_CROP);
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(getActivity(), R.string.camera_failure, Toast.LENGTH_LONG).show();
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            boxartPic = BitmapFactory.decodeFile(mCurrentPhotoPath);
            boxartUri = mCurrentPhotoPath;
            ivGetBoxart.setImageBitmap(boxartPic);
        } else if (resultCode == UCrop.RESULT_ERROR) {
//            final Throwable cropError = UCrop.getError(data);
//            String s = String.valueOf(resultUri);
            Toast.makeText(getActivity(), R.string.crop_error, Toast.LENGTH_SHORT).show();
        }
    }


    private void saveWithBoxartToParse(String imagePath, Kit kitSave) {
//        String name = kitSave.getBrand() + kitSave.getBrandCatno();
//        saveFullImage(String imagePath, Kit kitSave);
        try {
            saveThumbnail(kitSave, imagePath, Constants.SIZE_SMALL_HEIGHT, Constants.SIZE_SMALL_WIDTH);
            saveThumbnail(kitSave, imagePath, Constants.SIZE_FULL_HEIGHT, Constants.SIZE_FULL_WIDTH);
        } catch (Exception ex) {
            sendStatus = "n";
        }
    }

    private void saveThumbnail(final Kit kitSave, String imagePath, int height, final int width) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        getResizedBitmap(bmp, height, width).compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();
        String name = imageFileName;
        String fullName;
        String nameBody;
        if (height != Constants.SIZE_FULL_HEIGHT) {
            nameBody = "-t";
        }else{
            nameBody = "-720";
        }
        fullName = name + nameBody + Constants.JPG;
        final ParseFile file = new ParseFile(fullName, data);
        ParseObject boxartToSave = new ParseObject("Boxart");
        boxartToSave.put("image", file);
        boxartToSave.put("description", getBoxartDescription());

        boxartToSave.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) {
                if (ex == null) {
                        kitSave.setBoxart_url(file.getUrl());
                    parseThumbnailUrl = file.getUrl();//?
                        saveOnline(kitSave);
                } else {
                    sendStatus = "n";
//                    Toast.makeText(context, "parse error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // Create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // Resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // Recreate the new Bitmap
        return Bitmap.createScaledBitmap(bm, newWidth, newHeight, false);
    }
    private String getBoxartDescription() {
//        String desc = "";
        return spYear.getSelectedItem().toString() + "-" + spDescription.getSelectedItem().toString();
//        return desc;
    }

    private void saveOnline(Kit kitSave) {
        saveToNewKit(kitSave); //save to intermediate online database with moderation
        saveToOnlineStash(kitSave);
    }

    /**
    * Writes new kit entry to Stash class of the Parse database. Then will be used in statistics**/
    private void saveToOnlineStash(Kit kitSave) {
        ParseObject kitTowrite = new ParseObject("Stash");
        kitTowrite.put("barcode", kitSave.getBarcode());
        kitTowrite.put("brand", kitSave.getBrand());
        kitTowrite.put("brandCatno", kitSave.getBrandCatno());
        kitTowrite.put("scale", kitSave.getScale());
        kitTowrite.put("kit_name", kitSave.getKit_name());
        kitTowrite.put("kitNoengname", kitSave.getKit_noeng_name());
        kitTowrite.put("category", Helper.tagToCode(kitSave.getCategory()));
        if (!TextUtils.isEmpty(kitSave.getBoxart_url())) {
            kitTowrite.put(Constants.BOXART_URL, kitSave.getBoxart_url());
        }
        kitTowrite.put("description", kitSave.getDescription());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        kitTowrite.put("owner_id", sharedPreferences.getString(Constants.USER_ID_FACEBOOK, ""));
        kitTowrite.put("year", kitSave.getYear());
        kitTowrite.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) {
                if (ex == null) {
//                    Toast.makeText(context, "parse dobe", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(context, "parse error" + ex.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    /*
    * Writes new entry to NewKit class of Parse database. After moderation it will be added
    * to the main kits database*/
    private void saveToNewKit(Kit kitSave){
        ParseObject kitTowrite = new ParseObject("NewKits");
        kitTowrite.put("barcode", kitSave.getBarcode());
        kitTowrite.put(Constants.BRAND, kitSave.getBrand());
        kitTowrite.put("brandCatno", kitSave.getBrandCatno());
        kitTowrite.put(Constants.SCALE, kitSave.getScale());
        kitTowrite.put("kit_name", kitSave.getKit_name());
        kitTowrite.put("kitNoengname", kitSave.getKit_noeng_name());
        kitTowrite.put(Constants.CATEGORY, Helper.tagToCode(kitSave.getCategory()));
        if (!TextUtils.isEmpty(kitSave.getBoxart_url())) {
            kitTowrite.put(Constants.BOXART_URL, kitSave.getBoxart_url());
        }
        kitTowrite.put(Constants.DESCRIPTION, kitSave.getDescription());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE); //todo Здесь ошибка сохранения онлайн!!!!!!!!!!!!!!!!!!!!!!!!
        kitTowrite.put("owner_id", sharedPreferences.getString(Constants.USER_ID_FACEBOOK, ""));
        kitTowrite.put(Constants.YEAR, kitSave.getYear());
        kitTowrite.saveInBackground();
    }

    private void setKitMode() {
        editMode = Constants.MODE_KIT;
        stepper_0.setTitle(R.string.search_by_code);
        tvModeKit.setBackgroundColor(Helper.getColor(context, R.color.colorAccent));
        tvModeKit.setTextColor(Helper.getColor(context, R.color.colorItem));
        tvModeAftermarket.setBackgroundColor(Helper.getColor(context, R.color.colorPassive));
        tvModeAftermarket.setTextColor(Helper.getColor(context, R.color.colorText));
        btnCheckOnlineDatabase.setVisibility(View.VISIBLE);
        setAllSteps(0);
        stepper_0.setState(1);

    }

    private void setAftermarketMode() {
        editMode = Constants.MODE_AFTERMARKET;
        stepper_0.setTitle(R.string.brand_and_name);
        tvModeAftermarket.setBackgroundColor(Helper.getColor(context, R.color.colorAccent));
        tvModeAftermarket.setTextColor(Helper.getColor(context, R.color.colorItem));
        tvModeKit.setBackgroundColor(Helper.getColor(context, R.color.colorPassive));
        tvModeKit.setTextColor(Helper.getColor(context, R.color.colorText));
        btnCheckOnlineDatabase.setVisibility(View.GONE);
        setAllSteps(1);
    }

//    @Override
//    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//        if(adapterView.getId() == R.id.spDescription){
//            description = adapterView.getItemAtPosition(position).toString();
//        }
//        if(adapterView.getId() == R.id.spYear){
//            if ((adapterView.getItemAtPosition(position)).toString().equals (getString(R.string.unknown))){
//                year = "0";
//            }else {
//                year = adapterView.getItemAtPosition(position).toString();
//            }
//        }
//        if(adapterView.getId() == R.id.spCategory){
//            category = String.valueOf(spCategory.getSelectedItemPosition());
//        }
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> adapterView) {
//
//    }
}