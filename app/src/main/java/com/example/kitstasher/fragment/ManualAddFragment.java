package com.example.kitstasher.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.objects.Item;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.adapters.AdapterAddFragment;
import com.example.kitstasher.adapters.AdapterAlertDialog;
import com.example.kitstasher.other.AsyncApp42ServiceApi;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.OnFragmentInteractionListener;
import com.example.kitstasher.other.ValueContainer;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.storage.Query;
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;
import com.shephertz.app42.paas.sdk.android.storage.Storage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.R.drawable.ic_menu_camera;
import static android.app.Activity.RESULT_OK;
import static com.example.kitstasher.activity.MainActivity.CAT_OTHER;
import static com.example.kitstasher.activity.MainActivity.REQUEST_CODE_CAMERA;
import static com.example.kitstasher.activity.MainActivity.REQUEST_CODE_CROP;
import static com.example.kitstasher.activity.MainActivity.asyncService;

/**
 * Created by Алексей on 21.04.2017.
 */

public class ManualAddFragment extends Fragment implements View.OnClickListener, TextWatcher,
        AsyncApp42ServiceApi.App42StorageServiceListener, OnFragmentInteractionListener, AdapterView.OnItemSelectedListener {
    private View view;
    private EditText etBrandCat_no, etScale, etKitName, etKitNoengName;
    private Button btnAdd, btnCancel, btnCheckOnlineDatabase;
    private AppCompatSpinner spYear, spDescription;
    private LinearLayout linLayoutMAir, linLayoutMCar, linLayoutMGround, linLayoutMOther,
            linLayoutMSea, linLayoutMSpace;
    private ImageView ivGetBoxart;
    private ProgressDialog progressDialog;

    private String barcode, brand, brand_catno, kitname, status, kit_noengname, date,
            boxart_url, category, boxart_uri, fbId, description, year, onlineId, listname;
    private char mode;
    private int scale;
    private boolean isFoundOnline, isReported, wasSearchedOnline, isRbChanged;

    private Context context;
    private DbConnector dbConnector;


    List<String> myList;
    ArrayAdapter<String> myAutoCompleteAdapter;
    AutoCompleteTextView acTvBrand;

    private OnFragmentInteractionListener mListener;
    public static String manualTag;

    private Bitmap boxartPic;
    private ByteArrayOutputStream bytes;
    private String currentDocId;

    private Kit kit;
    private ArrayAdapter<String> descriptionAdapter, yearsAdapter;

    private final int MY_PERMISSIONS_REQUEST_CAMERA = 11;


    public ManualAddFragment() {
        // Required empty public constructor
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

        prepareVariables();
        prepareMyList();
        checkMode();
        initUI();

        setTag(category);

        if (savedInstanceState != null){
            if (savedInstanceState.getString(Constants.BOXART_URL) != null) {
                Glide
                        .with(context)
                        .load(Constants.BOXART_URL_PREFIX
                                + savedInstanceState.getString(Constants.BOXART_URL)
                                + Constants.BOXART_URL_LARGE
                                + Constants.BOXART_URL_POSTFIX)
                        .placeholder(ic_menu_camera)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivGetBoxart);
            }else if (savedInstanceState.getParcelable(Constants.BOXART_IMAGE) != null) {
                    boxartPic = savedInstanceState.getParcelable(Constants.BOXART_IMAGE);
                    ivGetBoxart.setImageBitmap(boxartPic);
            }
            setCategory(savedInstanceState.getString(Constants.CATEGORY));
        }

        checkPermissions();


        //Обрабатываем список автодополнения
        myAutoCompleteAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, myList);
        acTvBrand = (AutoCompleteTextView) view.findViewById(R.id.acTvBrand);
        acTvBrand.addTextChangedListener(this);
        acTvBrand.setAdapter(myAutoCompleteAdapter);

        if (!isOnline()) {
            btnCheckOnlineDatabase.setEnabled(false);
        } else {
            btnCheckOnlineDatabase.setEnabled(true);
        }

//        String[] descriptionItems = new String[]{getString(R.string.kittype),
//                getString(R.string.new_tool), getString(R.string.reissue),
//                getString(R.string.changed_parts), getString(R.string.new_decal),
//                getString(R.string.changed_box), getString(R.string.repack)};
        String[] descriptionItems = new String[]{getString(R.string.kittype),
                getString(R.string.newkit), getString(R.string.rebox)};
        descriptionAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, descriptionItems);
        spDescription.setAdapter(descriptionAdapter);

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
            years.add(getString(R.string.year));
        for (int i = thisYear; i >= 1930; i--) {
            years.add(Integer.toString(i));
        }
        yearsAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, years);
        spYear.setAdapter(yearsAdapter);


        return view;
    }
// Проверяем, откуда обратились к редактору
    private void checkMode() {
        if (getArguments() != null){
            listname = getArguments().getString("listname");
        if (getArguments().getChar("mode") == 'l'){
            mode = 'l';
        }else{
            mode = 'm';
        }
        }else{
            mode = 'm';
        }
    }

    private void prepareVariables() {
        context = getActivity();
        //Установка текущей даты для записи в локальную базу
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

        //экономим обращения к онлайновой базе - если уже искали, не искать.
        wasSearchedOnline = false;


        //Статус для последующей записи пропущенных в офлайне записей
        status = null;
        brand = null;
        brand_catno = null;
        scale = 0;
        kitname = null;
        isFoundOnline = false;
        boxart_url = "";
        boxart_uri = "";
        isReported = false;
        date = df.format(c.getTime());
        barcode = "";
        mode = 'm';
        if (getArguments() != null && getArguments().getChar("mode") == 'l'
                && getArguments().getString("barcode") != null){
            barcode = getArguments().getString("barcode");
        }
        description = "";
        year = "0";

        category = Constants.CAT_OTHER;
        onlineId = "";

        isRbChanged = false;



        //New kit with empty fields
        kit = new Kit.KitBuilder().build();
        kit.setBarcode(barcode);
        kit.setBoxart_uri("");
        kit.setKit_noeng_name("");
        kit.setDescription("");
        kit.setYear("0");
        kit.setOnlineId("");
    }

    private void initUI() {
        btnCheckOnlineDatabase = (Button) view.findViewById(R.id.btnCheckOnlineDb);
        btnCheckOnlineDatabase.setOnClickListener(this);
        btnAdd = (Button) view.findViewById(R.id.btnMAdd);
        btnAdd.setOnClickListener(this);
        btnCancel = (Button) view.findViewById(R.id.btnMCancel);
        btnCancel.setOnClickListener(this);
        ivGetBoxart = (ImageView) view.findViewById(R.id.ivGetBoxart);
//        ivGetBoxart.setOnClickListener(this);
        etBrandCat_no = (EditText) view.findViewById(R.id.etBrandCat_no);
        etScale = (EditText) view.findViewById(R.id.etScale);
        etKitName = (EditText) view.findViewById(R.id.etKitName);
        etKitNoengName = (EditText) view.findViewById(R.id.etKitNoengName);
        spDescription = (AppCompatSpinner) view.findViewById(R.id.spDescription);
        spYear = (AppCompatSpinner) view.findViewById(R.id.spYear);

        linLayoutMAir = (LinearLayout) view.findViewById(R.id.linLayoutMAir);
        linLayoutMAir.setOnClickListener(this);
        linLayoutMCar = (LinearLayout) view.findViewById(R.id.linLayoutMCar);
        linLayoutMCar.setOnClickListener(this);
        linLayoutMGround = (LinearLayout) view.findViewById(R.id.linLayoutMGround);
        linLayoutMGround.setOnClickListener(this);
        linLayoutMSea = (LinearLayout) view.findViewById(R.id.linLayoutMSea);
        linLayoutMSea.setOnClickListener(this);
        linLayoutMSpace = (LinearLayout) view.findViewById(R.id.linLayoutMSpace);
        linLayoutMSpace.setOnClickListener(this);
        linLayoutMOther = (LinearLayout) view.findViewById(R.id.linLayoutMOther);
        linLayoutMOther.setOnClickListener(this);
    }


    //Подготовка автодополняемого списка брэндов
    private void prepareMyList() {
        myList = new ArrayList<String>();
        myList = DbConnector.getAllBrands();
    }

    private void checkPermissions() {
        //checking for permissions on Marshmallow+
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }else{
            ivGetBoxart.setOnClickListener(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ivGetBoxart.setOnClickListener(this);
                } else {
                    Toast.makeText(getActivity(),
                            R.string.permission_denied_to_use_camera, Toast.LENGTH_SHORT).show();
                    ivGetBoxart.setImageResource(R.drawable.ic_cancel_black_24dp);
                }
                return;
            }
        }
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
        if (TextUtils.isEmpty(etScale.getText()) || etScale.getText().equals("0")) {
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
                        if (isInLocalBase(brand, brand_catno)) {
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
                String newAdd = acTvBrand.getText().toString().trim();

                if (!myList.contains(newAdd)) {
                    //Срабатывает немедленно
                    if (newAdd.length() > 1) {
                        myList.add(newAdd);
                        //Срабатывает при перезапуске, записывает в базу
                        // и при следующем запуске уже берет из нее
                        dbConnector.addKitRec(newAdd);
                    }
                    myAutoCompleteAdapter = new ArrayAdapter<String>(
                            getActivity(),
                            android.R.layout.simple_dropdown_item_1line, myList);
                    acTvBrand.setAdapter(myAutoCompleteAdapter);
                }
                //******************************************************************
                if (checkAllFields()) {//Проверяем все поля - начальная проверка
                    getFieldsValues(); //getting data from form fields and prepare kit object
                    if (!isInLocalBase(kit.getBrand(), kit.getBrand_catno())) {
                        if (bytes != null && boxartPic != null) {
                            writeBoxartFile(bytes, boxartPic); //now kit has boxart_uri
                        }
                        saveOnline(kit);
                        ////////////////////////
                        if (mode !='l') {
                            if (isOnline()) {
                                writeToLocalDatabase(kit); //writes kit to database
                                if (wasSearchedOnline && !isFoundOnline) {
                                    if (boxartPic != null) {
                                        saveWithBoxartToParse(boxartPic, kit);
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
                                status = "n";//Надо потом записать в облако
                                writeToLocalDatabase(kit);
                                Toast.makeText(getActivity(), R.string.kit_added, Toast.LENGTH_SHORT).show();
                                status = null;
                                clearFields();
                                returnToScan();
                            }
                        }else{
                            writeToLocalDatabase(kit);
                            Toast.makeText(getActivity(), R.string.Kit_added_to_list, Toast.LENGTH_SHORT).show();
                            status = null;
                            clearFields();
                            returnToScan();

                            ListViewFragment listViewFragment = new ListViewFragment();
                            Bundle bundle = new Bundle(1);
                            bundle.putString("listname", listname);
                            listViewFragment.setArguments(bundle);
                            android.support.v4.app.FragmentTransaction fragmentTransaction =
                                    getFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.llListsContainer, listViewFragment);
                            fragmentTransaction.commit();
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.entry_already_exist,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Проверяем все поля - начальная проверка не пройдена!
                    Toast.makeText(getActivity(), "Please enter data", Toast.LENGTH_SHORT).show();
                }
                break;

            ////////RadioButtons

            case R.id.linLayoutMAir:
                isRbChanged = true;
                clearTags();
                setTag(Constants.CAT_AIR);
                break;
            case R.id.linLayoutMGround:
                isRbChanged = true;
                clearTags();
                setTag(Constants.CAT_GROUND);
                break;
            case R.id.linLayoutMSea:
                isRbChanged = true;
                clearTags();
                setTag(Constants.CAT_SEA);
                break;
            case R.id.linLayoutMSpace:
                isRbChanged = true;
                clearTags();
                setTag(Constants.CAT_SPACE);
                break;
            case R.id.linLayoutMCar:
                clearTags();
                setTag(Constants.CAT_AUTOMOTO);
                break;
            case R.id.linLayoutMOther:
                isRbChanged = true;
                clearTags();
                setTag(Constants.CAT_OTHER);
                break;

            case R.id.ivGetBoxart:
                try {
                    // Намерение для запуска камеры
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(captureIntent, REQUEST_CODE_CAMERA);
                } catch (ActivityNotFoundException e) {
                    // Выводим сообщение об ошибке
                    String errorMessage = getString(R.string.camera_failure);
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }



    private void searchKitOnline(Kit kitToSearch) {


        Query q1 = QueryBuilder.build(Constants.TAG_BRAND, kitToSearch.getBrand().trim(),
                QueryBuilder.Operator.EQUALS);
        Query q2 = QueryBuilder.build(Constants.TAG_BRAND_CATNO, kitToSearch.getBrand_catno().trim(),
                QueryBuilder.Operator.EQUALS);
        Query query = QueryBuilder.compoundOperator(q1, QueryBuilder.Operator.AND, q2);
        asyncService.findDocByQuery(Constants.App42DBName, Constants.CollectionName, query, this);

        wasSearchedOnline = true;
    }


    ///////////RadioButtons
    private void setCategory(String cat) {
        switch (cat) {
            case Constants.CAT_AIR:
                isRbChanged = true;
                clearTags();
                setTag(cat);
                break;
            case Constants.CAT_GROUND:
                isRbChanged = true;
                clearTags();
                setTag(cat);
                break;
            case Constants.CAT_SEA:
                isRbChanged = true;
                clearTags();
                setTag(cat);
                break;
            case Constants.CAT_SPACE:
                isRbChanged = true;
                clearTags();
                setTag(cat);
                break;
            case Constants.CAT_OTHER:
                isRbChanged = true;
                clearTags();
                setTag(cat);
                break;
            case Constants.CAT_AUTOMOTO:
                isRbChanged = true;
                clearTags();
                setTag(cat);
                break;
        }
    }

    private void setTag(String cat) {

        switch (cat) {
            case MainActivity.CAT_AIR:
                linLayoutMAir.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                category = Constants.CAT_AIR;
                break;
            case MainActivity.CAT_GROUND:
                linLayoutMGround.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                category = Constants.CAT_GROUND;
                break;
            case MainActivity.CAT_SEA:
                linLayoutMSea.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                category = Constants.CAT_SEA;
                break;
            case MainActivity.CAT_SPACE:
                linLayoutMSpace.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                category = Constants.CAT_SPACE;
                break;
            case MainActivity.CAT_AUTOMOTO:
                linLayoutMCar.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                category = Constants.CAT_AUTOMOTO;
                break;
            case MainActivity.CAT_OTHER:
                linLayoutMOther.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                category = Constants.CAT_OTHER;
                break;
        }
    }

    private void clearTags() {
        linLayoutMSpace.setBackgroundColor(ContextCompat.getColor(context, R.color.colorItem));
        linLayoutMAir.setBackgroundColor(ContextCompat.getColor(context, R.color.colorItem));
        linLayoutMSea.setBackgroundColor(ContextCompat.getColor(context, R.color.colorItem));
        linLayoutMGround.setBackgroundColor(ContextCompat.getColor(context, R.color.colorItem));
        linLayoutMCar.setBackgroundColor(ContextCompat.getColor(context, R.color.colorItem));
        linLayoutMOther.setBackgroundColor(ContextCompat.getColor(context, R.color.colorItem));
    }



/*
* Writes kit object to local Sqlite database*/
    private void writeToLocalDatabase(Kit kitSave) {
        if (mode == 'm') {
            dbConnector.addKitRec(kitSave.getBarcode(), kitSave.getBrand(), kitSave.getBrand_catno(),
                    kitSave.getScale(), kitSave.getKit_name(), kitSave.getKit_noeng_name(), status, date,
                    kitSave.getBoxart_url(), kitSave.getCategory(), kitSave.getBoxart_uri(),
//                kit.getOnlineId(),
                    kitSave.getDescription(),
                    kitSave.getYear());
        }else if (mode == 'l'){
            dbConnector.addListItem(kitSave.getBarcode(), kitSave.getBrand(), kitSave.getBrand_catno(),
                    kitSave.getScale(), kitSave.getKit_name(), kitSave.getKit_noeng_name(), status, date,
                    kitSave.getBoxart_url(), kitSave.getCategory(), kitSave.getBoxart_uri(),
//                kit.getOnlineId(),
                    kitSave.getDescription(),
                    kitSave.getYear(),
                    listname);
        }

    }



    /*Checks if local database already includes this kit*/
    private boolean isInLocalBase(String brand, String brand_catno) {
        if (mode == 'l'){
            if (dbConnector.searchListForDoubles(listname, brand, brand_catno)) {
                return true;
            }
        }else {
            if (dbConnector.searchForDoubles(brand, brand_catno)) {
                return true;
            }
        }
        return false;
    }
/*
* Gets data from form fields, trim strings and add them to kit object fields*/
    private void getFieldsValues() {
        kit.setBrand(acTvBrand.getText().toString().trim());
        kit.setBrand_catno(etBrandCat_no.getText().toString().trim());
        kit.setScale(Integer.parseInt(etScale.getText().toString()));
        kit.setKit_name(etKitName.getText().toString().trim());
        kit.setKit_noeng_name(etKitNoengName.getText().toString().trim());
        kit.setBarcode(barcode);
        kit.setCategory(category);
        String y = spYear.getSelectedItem().toString();
        if (!y.equals(getString(R.string.year))){
            kit.setYear(y);
        }else{
            kit.setYear("");
        }
        String d = spDescription.getSelectedItem().toString();
        if (!d.equals(getString(R.string.kittype))){
            kit.setDescription(descToCode(d));
        }else{
            kit.setDescription("0");
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    //Очистка полей и переменных
    private void clearFields() {
        //Обнуляем переменные
        barcode = "";
        brand = null;
        brand_catno = null;
        scale = 0;
        kitname = null;
        category = CAT_OTHER;
        description = "";
        year = "0";
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
        ivGetBoxart.setBackgroundResource(R.drawable.button_inversed);
        spDescription.setSelection(0);
        spYear.setSelection(0);
        onlineId = "";
        setCategory(Constants.CAT_OTHER);

        clearTags();
        kit = new Kit.KitBuilder().build();

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
        String showKit = "";
        final List<Kit> itemsToShow = new ArrayList<Kit>();
        final List<Item> itemList = new ArrayList<Item>();


        wasSearchedOnline = true;
        isFoundOnline = true;
        final ValueContainer <String> urlContainer = new ValueContainer<>();
        urlContainer.setVal("");//todo???
        final ValueContainer<String> categoryContainer;
        categoryContainer = new ValueContainer<>();
        final ValueContainer<String> barcodeContainer = new ValueContainer<>();

        //Нашли по коду в базе
        ArrayList<Storage.JSONDocument> jsonDocList = response.getJsonDocList();
        for (int i = 0; i < jsonDocList.size(); i++) {
//            onlineId = jsonDocList.get(i).getDocId();
            String in = jsonDocList.get(i).getJsonDoc();
            try {
                JSONObject reader = new JSONObject(in);
                //Containers should be first in list, not working otherwise
                urlContainer.setVal(reader.getString(Constants.TAG_BOXART_URL));
                categoryContainer.setVal(Helper.codeToTag(reader.getString(Constants.TAG_CATEGORY)));
                barcodeContainer.setVal(reader.getString(Constants.TAG_BARCODE));

                brand = reader.getString(Constants.TAG_BRAND);
                brand_catno = reader.getString(Constants.TAG_BRAND_CATNO);
                scale = reader.getInt(Constants.TAG_SCALE);
                kitname = reader.getString(Constants.TAG_KIT_NAME);
                kit_noengname = reader.getString(Constants.TAG_NOENG_NAME);
                barcode = reader.getString(Constants.TAG_BARCODE);
//                boxart_url = reader.getString(Constants.TAG_BOXART_URL);
                description = reader.getString(Constants.TAG_DESCRIPTION);

//                description = getKitDescription(reader.getString(Constants.TAG_DESCRIPTION));
                year = reader.getString(Constants.TAG_YEAR);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showKit = kitname + ", " + brand
                    + ", " + brand_catno + ", "
                    + "1/" + String.valueOf(scale);

            Item item = new Item(urlContainer.getVal(), showKit);
            itemList.add(item);

            Kit kitToShow = new Kit.KitBuilder()
                    .hasBrand(brand)
                    .hasBrand_catno(brand_catno)
                    .hasKit_name(kitname)
                    .hasScale(scale)
                    .hasDescription(description)
                    .hasCategory(categoryContainer.getVal())
                    .hasKit_noeng_name(kit_noengname)
                    .hasBoxart_url(urlContainer.getVal())
                    .hasBarcode(barcodeContainer.getVal())

                    .hasYear(year)
//                    .hasOnlineId(onlineId)

                    .build();
            itemsToShow.add(kitToShow);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.Found);
        AdapterAlertDialog adapterAlertDialog = new AdapterAlertDialog(getActivity(), itemList);

        builder.setAdapter(adapterAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                    Kit kitToAdd = itemsToShow.get(item);
                    etScale.setText(String.valueOf(kitToAdd.getScale()));
                    etKitName.setText(kitToAdd.getKit_name());
                    etKitNoengName.setText(kitToAdd.getKit_noeng_name());
                setKitYear(kitToAdd.getYear());
                setDescription(kitToAdd.getDescription());


                    barcode = kitToAdd.getBarcode();//todo???
                if (wasSearchedOnline && isFoundOnline) {
                    kit.setBoxart_url(kitToAdd.getBoxart_url());
                    setCategory(kitToAdd.getCategory());
                }else{
                    kit.setBoxart_url("");
                }
                    Glide
                            .with(context)
                            .load(Constants.BOXART_URL_PREFIX
                                    + kitToAdd.getBoxart_url()
                                    + Constants.BOXART_URL_LARGE
                                    + Constants.BOXART_URL_POSTFIX)
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

    private String getKitYear(String y) {
        if (y.equals(getString(R.string.year))){
            return "";
        }else{
            return y;
        }
    }


    @Override
    public void onFindDocFailed(App42Exception ex) {
        Toast.makeText(getActivity(),
                R.string.nothing_found_online,
                Toast.LENGTH_SHORT).show();
        wasSearchedOnline = true;
        isFoundOnline = false;
    }

    @Override
    public void onDocumentInserted(Storage response) {

        //save image to App42 - no need
//        progressDialog.dismiss();
//        currentDocId = response.getJsonDocList().get(0).getDocId();
//        Toast.makeText(getActivity(), currentDocId, Toast.LENGTH_SHORT).show();
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

    // In the child fragment.
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
        if (resultCode == getActivity().RESULT_OK && requestCode == MainActivity.REQUEST_CODE_CAMERA
                && data != null && data.getData() != null) {
            performCrop(data.getData());
        }
        if (resultCode == RESULT_OK && requestCode == MainActivity.REQUEST_CODE_CROP) {
            Bundle extras = data.getExtras();
            // Получим кадрированное изображение
            boxartPic = extras.getParcelable("data");

            boxartPic = Bitmap.createScaledBitmap(boxartPic, 640, 395, false);
            bytes = new ByteArrayOutputStream();
            boxartPic.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
            ivGetBoxart.setImageBitmap(boxartPic);
            ivGetBoxart.setBackgroundResource(0);
        }
//        if (resultCode != getActivity().RESULT_OK){
//        }
    }

    private void performCrop(Uri picUri){
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
            startActivityForResult(cropIntent, REQUEST_CODE_CROP);
        }
        catch(ActivityNotFoundException anfe){
            String errorMessage = getString(R.string.Sorry_no_crop);
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**Writes boxart image file to local directory**/
    private void writeBoxartFile(ByteArrayOutputStream baos, Bitmap bitmap) {
        String pictureName = kit.getBrand() + kit.getBrand_catno() + Constants.BOXART_URL_POSTFIX; //todo добавить description
        File exportDir = new File(Environment.getExternalStorageDirectory(), "Kitstasher");
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
        if (baos == null){
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
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
        kit.setBoxart_uri(pictureName);
    }

    private void saveWithBoxartToParse(Bitmap bmp, Kit kitSave){

        String name = kitSave.getBrand() + kitSave.getBrand_catno();
        saveWithResize(kitSave, name, bmp, Constants.SIZE_SMALL_HEIGHT, Constants.SIZE_SMALL_WIDTH);
        saveWithResize(kitSave, name, bmp, Constants.SIZE_MEDIUM_HEIGHT, Constants.SIZE_MEDIUM_WIDTH);
//        saveWithResize(kitSave, name, bmp, 393, 640);
        saveWithResize(kitSave, name, bmp, Constants.SIZE_FULL_HEIGHT, Constants.SIZE_FULL_WIDTH);

    }

    private void saveWithResize(final Kit kitSave, String name, Bitmap bmp, int height, final int width) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        getResizedBitmap(bmp, height, width).compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();
        String nameBody;
        if (width == Constants.SIZE_FULL_WIDTH){
            nameBody = "-pristine";
        }else{
            nameBody = "-t" + String.valueOf(width);
        }
        final ParseFile file = new ParseFile(name + nameBody + Constants.BOXART_URL_POSTFIX, data);
        ParseObject boxartToSave = new ParseObject("Boxart");
        boxartToSave.put("image", file);
        boxartToSave.put("description", getBoxartDescription());

        boxartToSave.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) {
                if (ex == null) {
                    if (width == Constants.SIZE_FULL_WIDTH){
                    boxart_url = file.getUrl();
                    kitSave.setBoxart_url(boxart_url);
                        saveOnline(kitSave);
                    }
                } else {
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
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, newWidth, newHeight,false);
        return resizedBitmap;

    }

    private String getBoxartDescription() {
        String desc = "";
//        String y = spYear.getSelectedItem().toString();
//        if (!y.equals(getString(R.string.year))){
//            kit.setYear(y);
//        }else{
//            kit.setYear("");
//        }
//        String d = spDescription.getSelectedItem().toString();
//        if (!d.equals(getString(R.string.kittype))){
//            kit.setDescription(descToCode(d));
//        }

        desc = spYear.getSelectedItem().toString() + "-" +  spDescription.getSelectedItem().toString();
        return desc;
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
        kitTowrite.put("brand_catno", kitSave.getBrand_catno());
        kitTowrite.put("scale", kitSave.getScale());
        kitTowrite.put("kit_name", kitSave.getKit_name());
        kitTowrite.put("kit_noengname", kitSave.getKit_noeng_name());
        kitTowrite.put("category", Helper.tagToCode(kitSave.getCategory()));
        if (kitSave.getBoxart_url() != null
                ) {
            kitTowrite.put("boxart_url", kitSave.getBoxart_url());
        }
        kitTowrite.put("description", kitSave.getDescription());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        kitTowrite.put("owner_id", sharedPreferences.getString(Constants.USER_ID_FACEBOOK, ""));
        kitTowrite.put("year", kitSave.getYear());
        kitTowrite.saveInBackground();
    }
    /*
    * Writes new entry to NewKit class of Parse database. After moderation it will be added
    * to the main kits database*/
    private void saveToNewKit(Kit kitSave){
        ParseObject kitTowrite = new ParseObject("NewKits");
        kitTowrite.put("barcode", kitSave.getBarcode());
        kitTowrite.put("brand", kitSave.getBrand());
        kitTowrite.put("brand_catno", kitSave.getBrand_catno());
        kitTowrite.put("scale", kitSave.getScale());
        kitTowrite.put("kit_name", kitSave.getKit_name());
        kitTowrite.put("kit_noengname", kitSave.getKit_noeng_name());
        kitTowrite.put("category", Helper.tagToCode(kitSave.getCategory()));
        if (kitSave.getBoxart_url() != null
                ) {
            kitTowrite.put("boxart_url", kitSave.getBoxart_url());
        }
        kitTowrite.put("description", kitSave.getDescription());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        kitTowrite.put("owner_id", sharedPreferences.getString(Constants.USER_ID_FACEBOOK, ""));
        kitTowrite.put("year", kitSave.getYear());
        kitTowrite.saveInBackground();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        // On selecting a spinner item
        if(adapterView.getId() == R.id.spDescription)
        {
            description = adapterView.getItemAtPosition(position).toString();        }
        else if(adapterView.getId() == R.id.spYear)
        {
            if ((adapterView.getItemAtPosition(position)).toString().equals
                    (getString(R.string.year))){
                year = "0";
            }else {
                year = adapterView.getItemAtPosition(position).toString();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
//Связь с интерфейсом. Перевод кодов в надписи с транслэйтом
//    private String getKitDescription(String description) {
//        String desc = "";
//        if (!description.equals("")) {
//            switch (description) {
//                case "0":
//                    desc = getString(R.string.new_tool);
//                    break;
//                case "1":
//                    desc = getString(R.string.reissue);
//                    break;
//                case "2":
//                    desc = getString(R.string.changed_parts);
//                    break;
//                case "3":
//                    desc = getString(R.string.new_decal);
//                    break;
//                case "4":
//                    desc = getString(R.string.changed_box);
//                    break;
//                case "5":
//                    desc = getString(R.string.repack);
//            }
//        }else{
//            desc = "";
//        }
//        return desc;
//    }
}