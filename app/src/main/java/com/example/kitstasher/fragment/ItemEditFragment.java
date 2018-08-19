package com.example.kitstasher.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.bumptech.glide.request.RequestOptions;
import com.example.kitstasher.BuildConfig;
import com.example.kitstasher.R;
import com.example.kitstasher.activity.CropActivity;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.activity.ViewActivity;
import com.example.kitstasher.adapters.UiSpinnerAdapter;
import com.example.kitstasher.objects.Kit;
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

import static android.app.Activity.RESULT_OK;
import static com.example.kitstasher.activity.MainActivity.REQUEST_CODE_CROP;
import static java.lang.Integer.parseInt;

/**
 * Created by Алексей on 05.09.2017.
 * Universal item edit fragment for kits and aftermarket card.
 */

public class ItemEditFragment extends Fragment implements View.OnClickListener {
    private Context context;
    private DbConnector dbConnector;
    //    private Cursor aftermarketCursor;
    private View view;
//    @BindView(R.id.ivEditBoxart) ImageView ivEditorBoxart;
//    @BindView(R.id.acEditBrand) AutoCompleteTextView etDetFullBrand;
//    @BindView(R.id.acEditShop) AutoCompleteTextView etPurchasedFrom;
//    @BindView(R.id.etEditName) EditText etDetFullKitname;
//    @BindView(R.id.etEditCatno) EditText etDetFullBrandCatNo;
//    @BindView(R.id.etEditScale) EditText etDetFullScale;
//    @BindView(R.id.etEditOrigName) EditText etDetFullKitNoengname;
//    @BindView(R.id.etEditNotes) EditText etFullNotes;
//    @BindView(R.id.etEditPrice) EditText etFullPrice;

    private ImageView ivEditorBoxart;
    private EditText etDetFullBrand;
    private EditText etPurchasedFrom;

    private EditText etDetFullKitname;
    private EditText etDetFullBrandCatNo;
    private EditText etDetFullScale;
    private EditText etDetFullKitNoengname;
    private EditText  etFullNotes;
    private EditText etFullPrice;

    private AppCompatSpinner spKitDescription,
            spKitYear,
            spQuantity,
            spCurrency,
            spKitMedia,
//            spKitStatus,
            spCategory;
    private TextView tvMPurchaseDate;
    //    private ImageView ivEditorBoxart;
//    private RecyclerView rvAftermarket;
//    private LinearLayout linLayoutEditAftermarket;

    private long id;
    private final int REQEST_AFTER_KIT = 10;
    private int position,

    scale,
            quantity,
            aMode;
    private String listname, // для переключения к вкладке.
            brand,
            catno,
            kitname,
            purchaseDate,
            boxartUri,
            boxartUrl,
            mCurrentPhotoPath,
            category;
    private String workMode;
    private boolean isBoxartTemporary,
            aftermarketMode;
    private ArrayAdapter<String> descriptionAdapter, yearsAdapter, currencyAdapter;
    private Kit kit;
    private Kit editedKit;
    private int tabToreturn;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item_edit, container, false);
        context = getActivity();

        dbConnector = new DbConnector(context);
        dbConnector.open();

        initUI();

        kit = getArguments().getParcelable(MyConstants.KIT);

//        tabToreturn = getArguments().getInt(MyConstants.CATEGORY_TAB);
        workMode = getArguments().getString(MyConstants.WORK_MODE, MyConstants.TYPE_KIT);
        category = getArguments().getString(MyConstants.CATEGORY);
//        aMode = MyConstants.MODE_A_KIT;
//                aMode = MyConstants.MODE_A_EDIT;
//                aftermarketMode = false;
        showEditForm(kit);


        if (savedInstanceState != null) {
            if (savedInstanceState.getString(MyConstants.BOXART_URI) != null) {
                mCurrentPhotoPath = savedInstanceState.getString("imagePath");
                if (mCurrentPhotoPath != null && !mCurrentPhotoPath.equals(MyConstants.EMPTY)) {
                    Glide
                            .with(context)
                            .load(new File(Uri.parse(mCurrentPhotoPath).getPath()))
                            .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera).error(R.drawable.ic_menu_camera))
                            .into(ivEditorBoxart);
                }
            }
        } else {

            String path = kit.getBoxart_uri();
            if (path != null) {
                mCurrentPhotoPath = path;
            }
//            else{
//                mCurrentPhotoPath = Helper.composeUrl(kit.getBoxart_url());
//            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dbConnector == null) dbConnector = new DbConnector(getActivity());
        dbConnector.open();
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
                    buildEditedKit();
//                    if (workMode == MyConstants.MODE_KIT) {
//                    dbConnector.open();
                    dbConnector.editItem(editedKit);
//                        dbConnector.close();
//                    } else if (workMode == MyConstants.MODE_AFTERMARKET) {
//                        dbConnector.editItem(DbConnector.TABLE_AFTERMARKET, editedKit);
//                    }

//                    KitsFragment.refreshPages();

//                    Cursor catCursor;
//                    if (aftermarketMode) {
//                        catCursor = dbConnector.getAfterActiveCategories();
//                    } else {
//                        catCursor = dbConnector.getActiveCategories();
//                    }
//                    catCursor.moveToFirst();
//                    while (!catCursor.isAfterLast()) {
//                        if (catCursor.getString(catCursor.getColumnIndexOrThrow(MyConstants.CATEGORY)).equals(category)) {
//                            tabToreturn = catCursor.getPosition();
//                        }
//                        catCursor.moveToNext();
//                    }

//                    String ret = String.valueOf(spCategory.getSelectedItemPosition());
                    Intent intent3 = new Intent(context, ViewActivity.class);
                    intent3.putExtra(MyConstants.POSITION, position);
                    intent3.putExtra(MyConstants.CATEGORY_TAB, tabToreturn);
                    intent3.putExtra(MyConstants.KIT, editedKit);
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
//            case R.id.btnAddAftermarket:
//                addAftermarket();
//                break;
        }
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
                    .into(ivEditorBoxart);
            boxartUri = mCurrentPhotoPath;

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(getActivity(), R.string.crop_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imagePath", mCurrentPhotoPath);
        String outDate = tvMPurchaseDate.getText().toString();
        outState.putString("outDate", outDate);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBoxartTemporary && mCurrentPhotoPath != null && !mCurrentPhotoPath.isEmpty()) {
            File file = new File(mCurrentPhotoPath);
            file.deleteOnExit();
        }
        dbConnector.close();
    }

    private void showEditForm(Kit kit) {
        boxartUrl = kit.getBoxart_url();
        boxartUri = kit.getBoxart_uri();
        brand = kit.getBrand();
        etDetFullBrand.setText(brand);
        catno = kit.getBrandCatno();
        etDetFullBrandCatNo.setText(catno);
        kitname = kit.getKit_name();
        etDetFullKitname.setText(kitname);
        scale = kit.getScale();
        etDetFullScale.setText(String.valueOf(scale));
        String pr = String.valueOf(kit.getPrice() / 100);
        etFullPrice.setText(pr);
        int categoryToSet = Integer.valueOf(kit.getCategory());
        String[] categories = new String[]{
                getString(R.string.other),
                getString(R.string.air),
                getString(R.string.ground),
                getString(R.string.sea),
                getString(R.string.space),
                getString(R.string.auto_moto),
                getString(R.string.Figures),
                getString(R.string.Fantasy)};
        int[] icons = new int[]{
                R.drawable.ic_help_black_24dp,
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
        spCategory.setSelection(categoryToSet);

        String[] descriptionItems = new String[]{getString(R.string.unknown),
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

        String[] currencies = dbConnector.getCurrencies(DbConnector.TABLE_CURRENCIES,
                DbConnector.CURRENCIES_COLUMN_CURRENCY);

        currencyAdapter = new ArrayAdapter<>(context,
                R.layout.simple_spinner_item, currencies);
        spCurrency.setAdapter(currencyAdapter);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultCurrency = sharedPreferences.getString(MyConstants.DEFAULT_CURRENCY, MyConstants.EMPTY);

        if (kit.getCurrency() != null
                && !kit.getCurrency().equals(MyConstants.EMPTY)) {
            String cr = kit.getCurrency();
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
        spKitMedia.setSelection(kit.getMedia());



        if (workMode.equals(MyConstants.TYPE_KIT)) {
            String orName = kit.getKit_noeng_name();
            if (orName != null) {
                etDetFullKitNoengname.setText(orName);
            }
        }
        int prc = kit.getPrice();
        if (prc != 0) {
            etFullPrice.setText(String.valueOf(prc / 100));
        }else{
            etFullPrice.setText(MyConstants.EMPTY);
        }
        String notes = kit.getNotes();
        etFullNotes.setText(notes);

        String pd = kit.getDatePurchased();
        if (pd != null && !pd.equals("")) {
            tvMPurchaseDate.setText(pd);
        }else{
            tvMPurchaseDate.setText(R.string.Date_not_set);
        }

        String pPlace = kit.getPlacePurchased();
        if (pPlace != null) {
            etPurchasedFrom.setText(pPlace);
        }

        String year = kit.getYear();
        if (year != null) {
            setKitYear(year);
        }

        String description = kit.getDescription();
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
        if (!Helper.isBlank(boxartUri)) {
            Glide
                    .with(context)
                    .load(new File(Uri.parse(boxartUri).getPath()))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera).error(R.drawable.ic_menu_camera))
                    .into(ivEditorBoxart);
        } else {
            Glide
                    .with(context)
                    .load(Helper.composeUrl(boxartUrl))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera).error(R.drawable.ic_menu_camera))
                    .into(ivEditorBoxart);
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
                if (!Helper.isBlank(boxartUri)) {
                    File file = new File(boxartUri);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                boxartUri = MyConstants.EMPTY;
                mCurrentPhotoPath = MyConstants.EMPTY;
                ContentValues cvUri = new ContentValues(1);
                cvUri.put(DbConnector.COLUMN_BOXART_URI, MyConstants.EMPTY);
                dbConnector.editItemById(kit.getLocalId(), cvUri);

                setBoxartImage();

                alertDialog.dismiss();
            }
        });
    }

    private void buildEditedKit(){
        editedKit = kit;
        editedKit.setBrand(etDetFullBrand.getText().toString().trim());
        editedKit.setKit_name(etDetFullKitname.getText().toString().trim());
        editedKit.setBrandCatno(etDetFullBrandCatNo.getText().toString().trim());
        editedKit.setScale(parseInt(etDetFullScale.getText().toString().trim()));
        editedKit.setPlacePurchased(etPurchasedFrom.getText().toString().trim());
        String date = tvMPurchaseDate.getText().toString();
        if (!date.equals(getResources().getString(R.string.Date_not_set))) {
            purchaseDate = tvMPurchaseDate.getText().toString();
            editedKit.setDatePurchased(purchaseDate);
        }else{
            editedKit.setDatePurchased(MyConstants.EMPTY);
        }

        if (etDetFullKitNoengname.getText() != null) {
            editedKit.setKit_noeng_name(etDetFullKitNoengname.getText().toString().trim());
        }
        if (etFullPrice.getText().toString().trim().equals("")){
            editedKit.setPrice(0);
        }else{
            int pr = Integer.parseInt(etFullPrice.getText().toString().trim()) * 100;
            editedKit.setPrice(pr);
        }

        editedKit.setNotes(etFullNotes.getText().toString().trim());

        if (mCurrentPhotoPath != null && !Helper.isBlank(mCurrentPhotoPath)) {
            editedKit.setBoxart_uri(mCurrentPhotoPath);
        } else {
            editedKit.setBoxart_uri(MyConstants.EMPTY);
        }

        String cat = String.valueOf(spCategory.getSelectedItemPosition());
        editedKit.setCategory(cat);


        String y = spKitYear.getSelectedItem().toString();
        if (!(y).equals(getResources().getString(R.string.unknown))) {
            editedKit.setYear(y);
        } else {
            editedKit.setYear(MyConstants.EMPTY);
        }

        String d = spKitDescription.getSelectedItem().toString();
        editedKit.setDescription(descToCode(d));

        quantity = spQuantity.getSelectedItemPosition() + 1;
        editedKit.setQuantity(quantity);

        String curr = spCurrency.getSelectedItem().toString();
        editedKit.setCurrency(curr);

        String purchasedFrom = etPurchasedFrom.getText().toString().trim();
        editedKit.setPlacePurchased(purchasedFrom);
        if (!Helper.isBlank(purchasedFrom)){
            dbConnector.addShop(purchasedFrom);
        }

//        int status = spKitStatus.getSelectedItemPosition();
//        editedKit.setStatus(status);

        int media = spKitMedia.getSelectedItemPosition();
        editedKit.setMedia(media);

        editedKit.setBoxart_url(kit.getBoxart_url());
    }

    public String descToCode(String d) {
        String desc = "";
        if (d.equals(getString(R.string.unknown))) {
            desc = "0";
        }else if (d.equals(getString(R.string.newkit))){
            desc = "1";
        }else if (d.equals(getString(R.string.rebox))){
            desc = "2";
        }
        return desc;
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
//        Button btnAddAftermarket = view.findViewById(R.id.btnAddAftermarket);
//        btnAddAftermarket.setOnClickListener(this);

        spKitDescription = view.findViewById(R.id.spEditDescription);
        spKitYear = view.findViewById(R.id.spEditYear);
        spCurrency = view.findViewById(R.id.spEditCurrency);
        spQuantity = view.findViewById(R.id.spEditQuantity);
        spKitMedia = view.findViewById(R.id.spEditMedia);
//        spKitStatus = view.findViewById(R.id.spEditStatus);
        spCategory = view.findViewById(R.id.spEditCategory);

        tvMPurchaseDate = view.findViewById(R.id.tvEditPurchaseDate);
        tvMPurchaseDate.setOnClickListener(this);

//        rvAftermarket = view.findViewById(R.id.rvEditAftermarket);
//        LinearLayoutManager rvAftermarketManager = new LinearLayoutManager(getActivity());
//        rvAftermarket.setLayoutManager(rvAftermarketManager);
//        rvAftermarket.setItemAnimator(new DefaultItemAnimator());

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

//    private void addAftermarket(){
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
//        LayoutInflater inflater = LayoutInflater.from(context);
//        final View dialogView = inflater.inflate(R.layout.list_choosemode_alertdialog, null);
//        dialogBuilder.setView(dialogView);
//        dialogBuilder.setTitle(R.string.Choose_mode);
//        final AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.show();
//
//        final Button getFromMyStash = dialogView.findViewById(R.id.btnListModeMyStash);
//        getFromMyStash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent(context, ChooserActivity.class);
//////                intent.putExtra(MyConstants.LISTNAME, listname);
////                intent.putExtra(MyConstants.KIT_ID, id);
//////                intent.putExtra(MyConstants.WORK_MODE, MyConstants.MODE_AFTER_KIT);
////                startActivityForResult(intent, REQEST_AFTER_KIT);
//                alertDialog.dismiss();
//            }
//        });
//    }
}