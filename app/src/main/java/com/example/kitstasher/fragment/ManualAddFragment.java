package com.example.kitstasher.fragment;

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
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kitstasher.BuildConfig;
import com.example.kitstasher.R;
import com.example.kitstasher.activity.CropActivity;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.adapters.FragmentAddAdapter;
import com.example.kitstasher.adapters.UiAlertDialogAdapter;
import com.example.kitstasher.adapters.UiSpinnerAdapter;
import com.example.kitstasher.objects.Item;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.AsyncApp42ServiceApi;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;
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
import java.util.Locale;
import java.util.regex.Pattern;

import moe.feng.common.stepperview.VerticalStepperItemView;

import static android.app.Activity.RESULT_OK;
import static com.example.kitstasher.activity.MainActivity.REQUEST_CODE_CROP;
import static com.example.kitstasher.activity.MainActivity.asyncService;


/**
 * Created by Алексей on 21.04.2017. Manual add items to database
 */

public class ManualAddFragment extends Fragment implements View.OnClickListener, TextWatcher,
        AsyncApp42ServiceApi.App42StorageServiceListener, OnFragmentInteractionListener {
    public static String manualTag;
    private View view;
    private EditText etBrandCat_no,
            etScale,
            etKitName,
            etKitNoengName,
            etNotes,
            etPrice;
    private Button btnCheckOnlineDatabase;
    private AppCompatSpinner spYear,
            spDescription,
            spQuantity,
            spCurrency,
            spKitMedia,
            spCategory;
    private ImageView ivGetBoxart;
    private TextView tvPurchaseDate;
    private VerticalStepperItemView stepper_0,
            stepper_1,
            stepper_2,
            stepper_3,
            stepper_4;
    private ProgressBar progressBar;
    private String imageFileName,
            ownerId,
            barcode,
            brand,
            brandCatno,
            sendStatus,
            dateAdded,
            boxartUrl,
            category,
            boxartUri,
            onlineId,
            scalematesUrl,
            placePurchased,
            defCurrency,
            mCurrentPhotoPath; //path for use with ACTION_VIEW intents
    private long currentId;
    private char workMode;
    private boolean isFoundOnline,
            isReported,
            cloudModeOn,
            isBoxartTemporary,
            wasSearchedOnline;
    private Context context;
    private DbConnector dbConnector;
    private List<String> myBrands;
    private ArrayAdapter<String> acAdapterMybrands,
            acAdapterMyshops,
            descriptionAdapter,
            currencyAdapter,
            yearsAdapter;
    private AutoCompleteTextView acTvBrand,
            acPurchasedFrom;
    private List<String> myShops;
    private OnFragmentInteractionListener mListener;
    private Kit kit;
    private String datePurchased;
    private String currency;
    private String activeTable;

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
            btnCheckOnlineDatabase.setVisibility(View.GONE);
            stepper_0.setTitle(R.string.brand_and_name);
            setAllStepsState(1);
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imagePath", mCurrentPhotoPath);
        if (kit != null){
            outState.putString(MyConstants.BOXART_URL, kit.getBoxart_url());
        }
        outState.putString(MyConstants.CATEGORY, category);
        if (barcode != null) {
            outState.putString(MyConstants.BARCODE, barcode);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tabbed_manualadd, container, false);
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        manualTag = getTag();

        initUI();

        initVariables();

        prepareBrandsList();

        stepper_0.setState(1);
        stepper_0.setNormalColor(Color.GREEN);

        if (savedInstanceState != null){
            if (savedInstanceState.getString(MyConstants.BOXART_URL) != null) {
                mCurrentPhotoPath = savedInstanceState.getString("imagePath");
                Glide
                        .with(context)
                        .load(
                                savedInstanceState.getString(MyConstants.BOXART_URL)
                                        + MyConstants.BOXART_URL_LARGE
                                        + MyConstants.JPG)
                        .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera).error(R.drawable.ic_menu_camera))
                        .into(ivGetBoxart);
                if (mCurrentPhotoPath != null && !mCurrentPhotoPath.equals(MyConstants.EMPTY)) {
                    Glide
                            .with(context)
                            .load(new File(Uri.parse(mCurrentPhotoPath).getPath()))
                            .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera).error(R.drawable.ic_menu_camera))
                            .into(ivGetBoxart);
                }
            }
            barcode = savedInstanceState.getString(MyConstants.BARCODE);
        }

        acAdapterMybrands = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, myBrands);
        acTvBrand.addTextChangedListener(this);
        acTvBrand.setAdapter(acAdapterMybrands);

        acAdapterMyshops = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, myShops);
        acPurchasedFrom.addTextChangedListener(this);
        acPurchasedFrom.setAdapter(acAdapterMyshops);

        if (!isOnline()) {
            btnCheckOnlineDatabase.setClickable(false);
            stepper_0.setTitle(R.string.brand_and_name);
            setAllStepsState(1);
        }

        if (workMode == MyConstants.MODE_AFTERMARKET || workMode == MyConstants.MODE_AFTER_KIT) {
            setAftermarketUI();
        } else if (workMode == MyConstants.MODE_KIT || workMode == MyConstants.MODE_LIST) {
            setKitUI();
        }

        String[] descriptionItems = new String[]{getString(R.string.unknown),
                getString(R.string.newkit), getString(R.string.rebox)};
        descriptionAdapter = new ArrayAdapter<>(context,
                R.layout.simple_spinner_item, descriptionItems);
        spDescription.setAdapter(descriptionAdapter);

        ArrayList<String> years = new ArrayList<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add(getString(R.string.unknown));
        for (int i = thisYear; i >= 1930; i--) {
            years.add(Integer.toString(i));
        }
        yearsAdapter = new ArrayAdapter<>(context,
                R.layout.simple_spinner_item, years);
        spYear.setAdapter(yearsAdapter);

        Integer[] quants = new Integer[]{1,2,3,4,5,6,7,8,9,10};
        ArrayAdapter<Integer> quantityAdapter = new ArrayAdapter<>(context,
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
        currencyAdapter = new ArrayAdapter<>(context,
                R.layout.simple_spinner_item, currencies);
        spCurrency.setAdapter(currencyAdapter);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultCurrency = sharedPreferences.getString(MyConstants.DEFAULT_CURRENCY, MyConstants.EMPTY);
        int spCurrencyPosition = currencyAdapter.getPosition(defaultCurrency);
        spCurrency.setSelection(spCurrencyPosition);

        String[] mediaTypes = new String[]{
                getString(R.string.media_unknown),
                getString(R.string.media_injected),
                getString(R.string.media_shortrun),
                getString(R.string.media_resin),
                getString(R.string.media_vacu),
                getString(R.string.media_paper),
                getString(R.string.media_wood),
                getString(R.string.media_metal),
                getString(R.string.media_3dprint),
                getString(R.string.media_multimedia),
                getString(R.string.media_other),
                getString(R.string.media_decal),
                getString(R.string.media_mask)
        };
        ArrayAdapter<String> mediaAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item,
                mediaTypes);
        spKitMedia.setAdapter(mediaAdapter);
        spKitMedia.setSelection(1);

        String[] categories = new String[]{
                getString(R.string.other),
                getString(R.string.Air),
                getString(R.string.Ground),
                getString(R.string.Sea),
                getString(R.string.Space),
                getString(R.string.Auto_moto),
                getString(R.string.Figures),
                getString(R.string.Fantasy)
        };
        int[] icons = new int[]{
                R.drawable.ic_check_box_outline_blank_black_24dp,
                R.drawable.ic_tag_air_black_24dp,
                R.drawable.ic_tag_afv_black_24dp,
                R.drawable.ic_tag_ship_black_24dp,
                R.drawable.ic_tag_space_black_24dp,
                R.drawable.ic_directions_car_black_24dp,
                R.drawable.ic_wc_black_24dp,
                R.drawable.ic_android_black_24dp
        };
        UiSpinnerAdapter uiSpinnerAdapter = new UiSpinnerAdapter(context, icons, categories);
        spCategory.setAdapter(uiSpinnerAdapter);
        spCategory.setSelection(Integer.parseInt(category));

        return view;
    }

    private void initVariables() {
        activeTable = DbConnector.TABLE_KITS;//MODE_KIT
        context = getActivity();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        cloudModeOn = sharedPreferences.getBoolean(MyConstants.CLOUD_MODE, true);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        if (getArguments() != null) {
            workMode = getArguments().getChar(MyConstants.WORK_MODE);
            if (workMode == MyConstants.MODE_AFTERMARKET) {
                setAftermarketUI();
            } else if (workMode == MyConstants.MODE_KIT && isOnline()) {
                setKitUI();
            } else if (workMode == MyConstants.MODE_KIT && !isOnline()) {
                setAftermarketUI();
            }
        } else {
            workMode = MyConstants.MODE_KIT;
        }
        wasSearchedOnline = false;
        isFoundOnline = false;
        isReported = false;
        sendStatus = MyConstants.EMPTY;//Статус для последующей записи пропущенных в офлайне записей
        brand = MyConstants.EMPTY;
        brandCatno = MyConstants.EMPTY;
        boxartUrl = MyConstants.EMPTY;
        boxartUri = MyConstants.EMPTY;
        if (getArguments() != null && getArguments().getString(MyConstants.BOXART_URI) != null) {
            boxartUri = getArguments().getString(MyConstants.BOXART_URI);
        }
        dateAdded = df.format(c.getTime());
        category = MyConstants.CODE_OTHER;
        onlineId = MyConstants.EMPTY;
        datePurchased = MyConstants.EMPTY;
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        currency = sharedPref.getString(MyConstants.DEFAULT_CURRENCY, MyConstants.EMPTY);
        ownerId = sharedPref.getString(MyConstants.USER_ID_PARSE, MyConstants.EMPTY);
        defCurrency = sharedPref.getString(MyConstants.DEFAULT_CURRENCY, MyConstants.EMPTY);
        scalematesUrl = MyConstants.EMPTY;
        placePurchased = MyConstants.EMPTY;
    }

    private void initUI() {
        btnCheckOnlineDatabase = view.findViewById(R.id.btnCheckOnlineDb);
        btnCheckOnlineDatabase.setOnClickListener(this);
        progressBar = view.findViewById(R.id.pbManualAdd);
        progressBar.setVisibility(View.GONE);
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
    }

    private void prepareBrandsList() {
        myBrands = new ArrayList<>();
        myBrands = DbConnector.getAllBrands();
        myShops = new ArrayList<>();
        myShops = DbConnector.getAllShops();
    }

    private boolean checkSearchFields() {
        boolean check = true;
        if (TextUtils.isEmpty(acTvBrand.getText())) {
            Toast.makeText(context, R.string.please_fill_fields, Toast.LENGTH_SHORT).show();
            check = false;
        }
        if (TextUtils.isEmpty(etBrandCat_no.getText())) {
            Toast.makeText(context, R.string.please_fill_fields, Toast.LENGTH_SHORT).show();
            check = false;
        }
        return check;
    }

    private boolean checkAllFields() {
        boolean check = true;
        if (TextUtils.isEmpty(acTvBrand.getText())) {
            check = false;
        }
        if (TextUtils.isEmpty(etBrandCat_no.getText())) {
            check = false;
        }
        if (TextUtils.isEmpty(etScale.getText()) || etScale.getText().toString().equals("0")) {
            check = false;
        } else if (!Pattern.matches("[0-9]+", etScale.getText().toString())) {
            Toast.makeText(getActivity(), R.string.please_use_numbers, Toast.LENGTH_SHORT).show();
            check = false;
        }
        if (TextUtils.isEmpty(etKitName.getText())) {
            check = false;
        }
        return check;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.btnMCancel):
                clearFields();

            case (R.id.btnCheckOnlineDb):
                if (checkSearchFields()) {
                    if (isOnline()) {
                        if (isInLocalBase(acTvBrand.getText().toString().trim(),
                                etBrandCat_no.getText().toString().trim())) {
                            Toast.makeText(getActivity(), R.string.entry_already_exist,
                                    Toast.LENGTH_SHORT).show();
                            break;
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                            Query q1 = QueryBuilder.build(MyConstants.TAG_BRAND, acTvBrand.getText().toString().trim(),
                                    QueryBuilder.Operator.EQUALS);
                            Query q2 = QueryBuilder.build(MyConstants.TAG_BRAND_CATNO, etBrandCat_no.getText().toString().trim(),
                                    QueryBuilder.Operator.EQUALS);
                            Query query = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
                            asyncService.findDocByQuery(MyConstants.App42DBName, MyConstants.CollectionName, query, this);
                        }

                    } else {
                        if (isInLocalBase(kit.getBrand(), kit.getBrandCatno())) {
                            Toast.makeText(getActivity(), R.string.entry_already_exist,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.please_fill_fields,
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case (R.id.btnMAdd):
                String newBrand = acTvBrand.getText().toString().trim();
                if (!myBrands.contains(newBrand)) {
                    if (newBrand.length() > 1) {
                        myBrands.add(newBrand);
                        dbConnector.addBrand(newBrand);
                    }
                    acAdapterMybrands = new ArrayAdapter<>(
                            context,
                            android.R.layout.simple_dropdown_item_1line, myBrands);
                    acTvBrand.setAdapter(acAdapterMybrands);
                }
                String newShop = acPurchasedFrom.getText().toString().trim();

                if (!myShops.contains(newShop)) {
                    if (!Helper.isBlank(newShop) && newShop.length() > 1) {
                        myShops.add(newShop);
                        dbConnector.addShop(newShop);
                    }
                    acAdapterMyshops = new ArrayAdapter<>(
                            context,
                            android.R.layout.simple_dropdown_item_1line, myShops);
                    acPurchasedFrom.setAdapter(acAdapterMyshops);
                }

                if (checkAllFields()) {
                    getFieldsValues();
                    isBoxartTemporary = false;
                    switch (workMode) {
                        case 'm':
                            kit.setItemType(MyConstants.TYPE_KIT);
                            break;
                        case 'a':
                            kit.setItemType(MyConstants.TYPE_AFTERMARKET);
                            break;
                    }
                    if (!isInLocalBase(kit.getBrand(), kit.getBrandCatno())) {
                        currentId = dbConnector.addKitRec(kit, activeTable); //kit has been saved in local base
                        kit.setLocalId((int) currentId);
                        if (isOnline()) {

//                                    currentId = dbConnector.addKitRec(kit); //kit has been saved in local base
                            if (wasSearchedOnline && !isFoundOnline) { //todo проверка в mystash на дубли
                                if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
                                    saveWithBoxartToParse(mCurrentPhotoPath, kit);
                                }

                            }
                            if (cloudModeOn) {
//                                        saveToOnlineStash(kit);
                                kit.saveToOnlineStash(context);
                            } else {
                                Toast.makeText(context, R.string.online_backup_is_off, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String sendStatus = "n";//Надо потом записать в облако
                            kit.setSendStatus(sendStatus);

//                                    currentId = dbConnector.addKitRec(kit);

                        }
                        Toast.makeText(getActivity(), R.string.kit_added, Toast.LENGTH_SHORT).show();
                        clearFields();
                        break;
                    } else {
                        Toast.makeText(getActivity(), R.string.entry_already_exist,
                                Toast.LENGTH_SHORT).show();
//                                break;
                    }

//                            if (!isInLocalBase(kit.getBrand(), kit.getBrandCatno())) {
//                                //todo не работает поиск афтеркопий, посмотреть db
//                                kit.setItemType(MyConstants.TYPE_AFTERMARKET);
//                                dbConnector.addKitRec(kit, activeTable);//запись
//                                Toast.makeText(getActivity(), R.string.aftermarket_added,
//                                        Toast.LENGTH_SHORT).show();
//                                sendStatus = MyConstants.EMPTY;
//                                clearFields();
//                            } else {
//                                Toast.makeText(getActivity(), R.string.aftermarket_already_exist,
//                                        Toast.LENGTH_SHORT).show();
//                                break;
//                            }
//                            break;
//                    }
                } else {
                    Toast.makeText(getActivity(), R.string.Please_enter_data, Toast.LENGTH_SHORT).show();
                    setAllStepsState(1);
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
                bundle.putString("caller", "manualadd");
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "DatePicker");
                break;

            case R.id.btnClearDate:
                datePurchased = MyConstants.EMPTY;
                tvPurchaseDate.setText(R.string.Date_not_set);
                break;
        }
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
            Toast.makeText(context, "Нельзя создать файл", Toast.LENGTH_LONG).show();
        }
        if (image != null) {
            mCurrentPhotoPath = image.getAbsolutePath();
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(MyConstants.FILE_URI, mCurrentPhotoPath);
            editor.apply();
        } else {
            Toast.makeText(context, "Нельзя создать файл", Toast.LENGTH_LONG).show();
        }
        return image;
    }

    private String getTimestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    }

    private boolean isInLocalBase(String brand, String brand_catno) {
        return dbConnector.searchForDoubles(activeTable, brand, brand_catno);
    }

    private void setAllStepsState(int state) {
        stepper_0.setState(state);
        stepper_1.setState(state);
        stepper_2.setState(state);
        stepper_3.setState(state);
        stepper_4.setState(state);
    }

    private void getFieldsValues() {

        String
                brand,
                brandCatno,
                kitName,
                kitNoengname,
//                datePurchased,
                category,
                description,
                year,
                notes,
//                currency,
                placePurchased;

        int
                status = MyConstants.STATUS_NEW,
                media,
                scale,
                quantity = 1,
                price;

        if (barcode == null) {
            barcode = MyConstants.EMPTY;
        }
        year = MyConstants.EMPTY;
        category = String.valueOf(spCategory.getSelectedItemPosition());
        String y = spYear.getSelectedItem().toString();
        if (!y.equals(getResources().getString(R.string.unknown))
                || y.equals(MyConstants.EMPTY)) {
            year = y;
        }

        brand = acTvBrand.getText().toString().trim();
        brandCatno = etBrandCat_no.getText().toString().trim();
        scale = Integer.parseInt(etScale.getText().toString());
        kitName = etKitName.getText().toString().trim();
        kitNoengname = etKitNoengName.getText().toString().trim();
        media = spKitMedia.getSelectedItemPosition();
        String d = spDescription.getSelectedItem().toString(); //todo
        description = (descToCode(d));
        currency = spCurrency.getSelectedItem().toString();
        quantity = spQuantity.getSelectedItemPosition() + 1;
        notes = etNotes.getText().toString();
        if (!etPrice.getText().toString().equals(MyConstants.EMPTY)) {
            price = Integer.parseInt(etPrice.getText().toString()) * 100;
        } else {
            price = 0;
        }
        if (!tvPurchaseDate.getText().toString().equals(MyConstants.EMPTY)
                && tvPurchaseDate.getText().toString().equals(getString(R.string.Date_not_set))) {
            datePurchased = tvPurchaseDate.getText().toString();
        } else {
            datePurchased = MyConstants.EMPTY;
        }
        placePurchased = acPurchasedFrom.getText().toString().trim();

        kit = new Kit.KitBuilder()
                .hasBrand(brand)
                .hasBrand_catno(brandCatno)
                .hasKit_name(kitName)
                .hasScale(scale)
                .hasCategory(category)
                .hasBarcode(barcode)
                .hasKit_noeng_name(kitNoengname)
                .hasDescription(description)
                .hasPrototype(MyConstants.EMPTY)//not in use
                .hasSendStatus(sendStatus)
                .hasBoxart_url(boxartUrl)
                .hasBoxart_uri(boxartUri)
                .hasScalemates_url(scalematesUrl)
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

        if (workMode == MyConstants.MODE_AFTERMARKET) {
            kit.setItemType(MyConstants.TYPE_AFTERMARKET);
        } else {
            kit.setItemType(MyConstants.TYPE_KIT);
        }
    }

    private String descToCode(String d) {
        String desc = MyConstants.EMPTY;
        if (d.equals(getString(R.string.kittype))){
            desc = "0";
        }else if (d.equals(getString(R.string.newkit))){
            desc = "1";
        }else if (d.equals(getString(R.string.rebox))){
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
        acTvBrand.setText(MyConstants.EMPTY);
        etBrandCat_no.setText(MyConstants.EMPTY);
        etKitNoengName.setText(MyConstants.EMPTY);
        etScale.setText(MyConstants.EMPTY);
        etKitName.setText(MyConstants.EMPTY);
        isFoundOnline = false;
        wasSearchedOnline = false;
        ivGetBoxart.setImageResource(R.drawable.ic_menu_camera);
        ivGetBoxart.setBackgroundResource(R.drawable.button);
        spDescription.setSelection(0);
        spYear.setSelection(0);
        spQuantity.setSelection(0);
        etNotes.setText(MyConstants.EMPTY);
        etPrice.setText(MyConstants.EMPTY);
        tvPurchaseDate.setText(R.string.Date_not_set);
        acPurchasedFrom.setText(MyConstants.EMPTY);
        setAllStepsState(0);
        stepper_0.setState(1);
        if (!isOnline()) {
            setAllStepsState(1);
            btnCheckOnlineDatabase.setVisibility(View.GONE);
        }

        kit = new Kit.KitBuilder().build();

        wasSearchedOnline = false;
        isFoundOnline = false;
        isReported = false;
        sendStatus = MyConstants.EMPTY;//Статус для последующей записи пропущенных в офлайне записей
        workMode = MyConstants.MODE_KIT;
        boxartUrl = MyConstants.EMPTY;
        boxartUri = MyConstants.EMPTY;
        barcode = MyConstants.EMPTY;
        datePurchased = dateAdded;
        scalematesUrl = MyConstants.EMPTY;
        int spCurrencyPosition = currencyAdapter.getPosition(defCurrency);
        spCurrency.setSelection(spCurrencyPosition);
        placePurchased = MyConstants.EMPTY;
        acPurchasedFrom.setText(placePurchased);
        currentId = 0;
        sendStatus = MyConstants.EMPTY;

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(MyConstants.BARCODE).apply();
    }

    private void returnToScan() {
        FragmentAddAdapter.openScan();
    }

    private void setDescription(String description) {
        if (!description.equals(MyConstants.EMPTY)) {
            String desc = MyConstants.EMPTY;
            if (!description.equals(MyConstants.EMPTY)) {
                switch (description) {
                    case "0":
                        desc = getString(R.string.unknown);
                        break;
                    case "1":
                        desc = getString(R.string.newkit);
                        break;
                    case "2":
                        desc = getString(R.string.rebox);
                        break;
                }
            }else{
                desc = getString(R.string.unknown);
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
            spYear.setSelection(0);
        }
    }

    private void setKitCategory(String cat) {
        spCategory.setSelection(Integer.valueOf(cat));
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

    /* Passing data from scanFragment In the child fragment. */
    public void onAttachToParentFragment(Fragment fragment) {
        try {
            mListener = (OnFragmentInteractionListener) fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    fragment.toString());
        }
    }

    @Override
    public void onFragmentInteraction(String b, char mode) {
        barcode = b;
        wasSearchedOnline = true;
        isFoundOnline = false;
        workMode = mode;
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
        if (resultCode != RESULT_OK) {
            Toast.makeText(getActivity(), R.string.camera_failure, Toast.LENGTH_LONG).show();
        }

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CROP) {
            File image = new File(mCurrentPhotoPath);
            Glide
                    .with(context)
                    .load(image)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera).error(R.drawable.ic_menu_camera))
                    .into(ivGetBoxart);
            boxartUri = mCurrentPhotoPath;

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(getActivity(), R.string.crop_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveWithBoxartToParse(String imagePath, Kit kitSave) {
        try {
            saveThumbnail(kitSave, imagePath, MyConstants.SIZE_SMALL_HEIGHT, MyConstants.SIZE_SMALL_WIDTH);
            saveThumbnail(kitSave, imagePath, MyConstants.SIZE_FULL_HEIGHT, MyConstants.SIZE_FULL_WIDTH);
        } catch (Exception ex) {
            sendStatus = "n";
        }
    }

    private void saveThumbnail(final Kit kitSave, String imagePath, final int height, final int width) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        getResizedBitmap(bmp, height, width).compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] data = stream.toByteArray();
        String name = imageFileName;
        String fullName;
        String nameBody;
        if (height != MyConstants.SIZE_FULL_HEIGHT) {
            nameBody = MyConstants.SIZE_SMALL;
        }else{
            nameBody = MyConstants.SIZE_FULL;
        }
        fullName = name + nameBody + MyConstants.JPG;
        final ParseFile file = new ParseFile(fullName, data);
        ParseObject boxartToSave = new ParseObject(MyConstants.PARSE_C_BOXART);
        boxartToSave.put(MyConstants.PARSE_IMAGE, file);
        boxartToSave.put(MyConstants.PARSE_DESCRIPTION, getBoxartDescription());
        boxartToSave.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) {
                if (ex == null && height == MyConstants.SIZE_FULL_HEIGHT) {
                    kitSave.setBoxart_url(file.getUrl());
                    saveOnline(kitSave);
                } else {
                    sendStatus = "n";
                }
            }
        });
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createScaledBitmap(bm, newWidth, newHeight, false);
    }

    private String getBoxartDescription() {
        return spYear.getSelectedItem().toString() + "-" + spDescription.getSelectedItem().toString();
    }

    private void saveOnline(Kit kitSave) {
        kitSave.saveToNewKit(ownerId);
        kitSave.saveToOnlineStash(context);
    }

    private void setKitUI() {
        stepper_0.setTitle(R.string.search_by_code);
        btnCheckOnlineDatabase.setVisibility(View.VISIBLE);
        setAllStepsState(0);
        stepper_0.setState(1);
    }

    private void setAftermarketUI() {
        stepper_0.setTitle(R.string.brand_and_name);
        btnCheckOnlineDatabase.setVisibility(View.GONE);
        setAllStepsState(1);
    }

    /* CLOUD methods */
    @Override
    public void onFindDocSuccess(Storage response) {
        progressBar.setVisibility(View.GONE);
        String showKit;
        final List<Kit> itemsToShow = new ArrayList<>();
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
        int inScale = 0;
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
                urlContainer.setVal(reader.getString(MyConstants.TAG_BOXART_URL));
                categoryContainer.setVal(String.valueOf(reader.getInt(MyConstants.TAG_CATEGORY)));
                barcodeContainer.setVal(reader.getString(MyConstants.TAG_BARCODE));
                inBrand = reader.getString(MyConstants.TAG_BRAND);
                inBrandCatno = reader.getString(MyConstants.TAG_BRAND_CATNO);
                inScale = reader.getInt(MyConstants.TAG_SCALE);
                inKitName = reader.getString(MyConstants.TAG_KIT_NAME);
                inKitNoengname = reader.getString(MyConstants.TAG_NOENG_NAME);
//                inBarcode = reader.getString(MyConstants.TAG_BARCODE);
                inDescription = reader.getString(MyConstants.TAG_DESCRIPTION);
                inYear = reader.getString(MyConstants.TAG_YEAR);
                inScalematesUrl = reader.getString(MyConstants.TAG_SCALEMATES_PAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showKit = inKitName + ", " + inBrand
                    + ", " + inBrandCatno + ", "
                    + "1/" + String.valueOf(inScale);

            Item item = new Item(urlContainer.getVal(), showKit);
            itemList.add(item);

            Kit kitToShow = new Kit.KitBuilder()
//                    .hasBarcode(inBarcode) //?
                    .hasBrand(inBrand)
                    .hasBrand_catno(inBrandCatno)
                    .hasKit_name(inKitName)
                    .hasScale(inScale)
                    .hasDescription(inDescription)
                    .hasCategory(categoryContainer.getVal())
                    .hasKit_noeng_name(inKitNoengname)
                    .hasBoxart_url(urlContainer.getVal())
                    .hasBarcode(barcodeContainer.getVal())
                    .hasScalemates_url(inScalematesUrl)
                    .hasYear(inYear)
                    .build();
            itemsToShow.add(kitToShow);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.Found);
        UiAlertDialogAdapter uiAlertDialogAdapter = new UiAlertDialogAdapter(getActivity(), itemList);

        builder.setAdapter(uiAlertDialogAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                setAllStepsState(1);
                Kit kitToAdd = itemsToShow.get(item);
                etScale.setText(String.valueOf(kitToAdd.getScale()));
                etKitName.setText(kitToAdd.getKit_name());
                etKitNoengName.setText(kitToAdd.getKit_noeng_name());
                setKitYear(kitToAdd.getYear());
                setDescription(kitToAdd.getDescription());

                String vv = kitToAdd.getBarcode();
                if (!vv.isEmpty()) {
                    barcode = vv;
                }
                category = kitToAdd.getCategory();
                setKitCategory(category);
                if (wasSearchedOnline && isFoundOnline) {
                    boxartUrl = kitToAdd.getBoxart_url();
                } else {
                    boxartUrl = MyConstants.EMPTY;
                }
                Glide
                        .with(context)
                        .load(
                                kitToAdd.getBoxart_url()
                                        + MyConstants.BOXART_URL_LARGE
                                        + MyConstants.JPG)
                        .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera).error(R.drawable.ic_menu_camera))
                        .into(ivGetBoxart);
            }
        });

        ivGetBoxart.setBackgroundResource(0);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onFindDocFailed(App42Exception ex) {
        progressBar.setVisibility(View.GONE);
        setAllStepsState(1);
        Toast.makeText(getActivity(),
                R.string.nothing_found_online,
                Toast.LENGTH_SHORT).show();
        wasSearchedOnline = true;
        isFoundOnline = false;
    }

    @Override
    public void onDocumentInserted(Storage response) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onUpdateDocSuccess(Storage response) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onInsertionFailed(App42Exception ex) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), "Saving failed. Please, try again!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateDocFailed(App42Exception ex) {
        progressBar.setVisibility(View.GONE);
    }
}