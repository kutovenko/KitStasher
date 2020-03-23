package com.kutovenko.kitstasher.ui.fragment;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.databinding.FragmentAddSupplyBinding;
import com.kutovenko.kitstasher.ui.MainActivity;
import com.kutovenko.kitstasher.ui.adapter.UiSpinnerSupplyAdapter;
import com.kutovenko.kitstasher.BuildConfig;
import com.kutovenko.kitstasher.ui.CropActivity;
import com.kutovenko.kitstasher.model.StashItem;
import com.kutovenko.kitstasher.db.DbConnector;
import com.kutovenko.kitstasher.util.Helper;
import com.kutovenko.kitstasher.util.MyConstants;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.kutovenko.kitstasher.ui.MainActivity.MY_PERMISSIONS_REQUEST_CAMERA;
import static com.kutovenko.kitstasher.ui.MainActivity.MY_PERMISSIONS_REQUEST_WRITE;
import static com.kutovenko.kitstasher.ui.MainActivity.REQUEST_CODE_CROP;

public class AddSupplyFragment extends Fragment implements View.OnClickListener, TextWatcher {
    private StashItem supplyItem;
    private FragmentAddSupplyBinding binding;
//    private ImageView ivPaintBox;
//    private EditText etPaintName, etPaintCode;
//    private AutoCompleteTextView acTvPaintBrand;
//    private Spinner spCategory;
    private String mCurrentPhotoPath = MyConstants.EMPTY;

    private ArrayList<String> myBrands;
    private ArrayAdapter<String> acAdapterMybrands;

    private DbConnector dbConnector;
    private Context context;
    private String imageFileName;
    private boolean isBoxartTemporary;
    private boolean isExisting;
    private long id;

    public static AddSupplyFragment newInstance() {
        return new AddSupplyFragment();
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("imagePath", mCurrentPhotoPath);
//        if (supplyItem != null){
//            outState.putString(MyConstants.BOXART_URL, supplyItem.getBoxartUri());
//        }
//    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        checkCameraPermissions();
        checkWritePermissions();

        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_add_supply, container, false);
//        View view = inflater.inflate(com.kutovenko.kitstasher.R.layout.fragment_add_supply, container, false);
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        context = getActivity();
        binding.ivPaintBox.setOnClickListener(this);
//        etPaintCode = view.findViewById(com.kutovenko.kitstasher.R.id.etPaintCode);
//        etPaintName = view.findViewById(com.kutovenko.kitstasher.R.id.etPaintName);
//        acTvPaintBrand = view.findViewById(com.kutovenko.kitstasher.R.id.acPaintBrand);
//        spCategory = view.findViewById(com.kutovenko.kitstasher.R.id.spSupplyCategory);

//        Button btnSave = view.findViewById(com.kutovenko.kitstasher.R.id.btnPaintSave);
        binding.btnPaintSave.setOnClickListener(this);
//        Button btnCancel = view.findViewById(com.kutovenko.kitstasher.R.id.btnCancelPaint);
        binding.btnCancelPaint.setOnClickListener(this);

        if (savedInstanceState != null){
            mCurrentPhotoPath = savedInstanceState.getString("imagePath");
            if (mCurrentPhotoPath != null && !mCurrentPhotoPath.equals(MyConstants.EMPTY)) {
                Glide
                        .with(context)
                        .load(new File(Uri.parse(mCurrentPhotoPath).getPath()))
                        .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera).error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                        .into(binding.ivPaintBox);
            }
        }
        myBrands = dbConnector.getBrandsNames();
        acAdapterMybrands = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, myBrands);
        binding.acPaintBrand.addTextChangedListener(this);
        binding.acPaintBrand.setAdapter(acAdapterMybrands);
        if (getArguments() != null && getArguments().getBoolean(MyConstants.PAINT_EDIT_MODE)){
            isExisting = true;
            id = getArguments().getLong(MyConstants.ID);
            Cursor cursor = dbConnector.getPaintById(id);
            cursor.moveToFirst();
            binding.acPaintBrand.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND)));
            binding.etPaintCode.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO)));
            binding.etPaintName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME)));
            mCurrentPhotoPath = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
            String url = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URL));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));
            if (Helper.isBlank(category)){
                category = MyConstants.CODE_P_OTHER;
            }
            cursor.close();
            if (!Helper.isBlank(mCurrentPhotoPath)) {
                Glide
                        .with(context)
                        .load(new File(Uri.parse(mCurrentPhotoPath).getPath()))
                        .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera).error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                        .into(binding.ivPaintBox);
            } else {
                Glide
                        .with(context)
                        .load(Helper.composeUrl(url, MyConstants.BOXART_URL_LARGE))
                        .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera).error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                        .into(binding.ivPaintBox);
            }
            String[] categories;
            categories = new String[]{
                    getString(com.kutovenko.kitstasher.R.string.acryllic),
                    getString(com.kutovenko.kitstasher.R.string.enamel),
                    getString(com.kutovenko.kitstasher.R.string.media_oil),
                    getString(com.kutovenko.kitstasher.R.string.media_lacquer),
                    getString(com.kutovenko.kitstasher.R.string.media_thinner),
                    getString(com.kutovenko.kitstasher.R.string.media_glue),
                    getString(com.kutovenko.kitstasher.R.string.media_decalset),
                    getString(com.kutovenko.kitstasher.R.string.media_decalsol),
                    getString(com.kutovenko.kitstasher.R.string.media_pigment),
                    getString(com.kutovenko.kitstasher.R.string.media_colorstop),
                    getString(com.kutovenko.kitstasher.R.string.media_filler),
                    getString(com.kutovenko.kitstasher.R.string.media_primer),
                    getString(com.kutovenko.kitstasher.R.string.media_other)
            };
            UiSpinnerSupplyAdapter uiSpinnerAdapter = new UiSpinnerSupplyAdapter(context, categories);
            binding.spSupplyCategory.setAdapter(uiSpinnerAdapter);
            binding.spSupplyCategory.setSelection(setSupplyCategoryToPosition(category));
        }else{
            String[] categories;
            categories = new String[]{
                    getString(com.kutovenko.kitstasher.R.string.acryllic),
                    getString(com.kutovenko.kitstasher.R.string.enamel),
                    getString(com.kutovenko.kitstasher.R.string.media_oil),
                    getString(com.kutovenko.kitstasher.R.string.media_lacquer),
                    getString(com.kutovenko.kitstasher.R.string.media_thinner),
                    getString(com.kutovenko.kitstasher.R.string.media_glue),
                    getString(com.kutovenko.kitstasher.R.string.media_decalset),
                    getString(com.kutovenko.kitstasher.R.string.media_decalsol),
                    getString(com.kutovenko.kitstasher.R.string.media_pigment),
                    getString(com.kutovenko.kitstasher.R.string.media_colorstop),
                    getString(com.kutovenko.kitstasher.R.string.media_filler),
                    getString(com.kutovenko.kitstasher.R.string.media_primer),
                    getString(com.kutovenko.kitstasher.R.string.media_other)
            };
            UiSpinnerSupplyAdapter uiSpinnerAdapter = new UiSpinnerSupplyAdapter(context, categories);
            binding.spSupplyCategory.setAdapter(uiSpinnerAdapter);
        }
        return binding.getRoot();
    }

    private int setSupplyCategoryToPosition(String category) {
        switch (category){
            case MyConstants.CODE_P_ACRYLLIC:
                return 0;
            case MyConstants.CODE_P_ENAMEL:
                return 1;
            case MyConstants.CODE_P_OIL:
                return 2;
            case MyConstants.CODE_P_LACQUER:
                return 3;
            case MyConstants.CODE_P_THINNER:
                return 4;
            case MyConstants.CODE_P_GLUE:
                return 5;
            case MyConstants.CODE_P_DECAL_SET:
                return 6;
            case MyConstants.CODE_P_DECAL_SOL:
                return 7;
            case MyConstants.CODE_P_PIGMENT:
                return 8;
            case MyConstants.CODE_P_COLORSTOP:
                return 9;
            case MyConstants.CODE_P_FILLER:
                return 10;
            case MyConstants.CODE_P_PRIMER:
                return 11;
            case MyConstants.CODE_P_OTHER:
                return 12;
            default:
                return 12;
        }
    }

    private String getSupplyCategoryFromPosition(int position) {
        switch (position) {
            case 0:
                return MyConstants.CODE_P_ACRYLLIC;
            case 1:
                return MyConstants.CODE_P_ENAMEL;
            case 2:
                return MyConstants.CODE_P_OIL;
            case 3:
                return MyConstants.CODE_P_LACQUER;
            case 4:
                return MyConstants.CODE_P_THINNER;
            case 5:
                return MyConstants.CODE_P_GLUE;
            case 6:
                return MyConstants.CODE_P_DECAL_SET;
            case 7:
                return MyConstants.CODE_P_DECAL_SOL;
            case 8:
                return MyConstants.CODE_P_PIGMENT;
            case 9:
                return MyConstants.CODE_P_COLORSTOP;
            case 10:
                return MyConstants.CODE_P_FILLER;
            case 11:
                return MyConstants.CODE_P_PRIMER;
            case 12:
                return MyConstants.M_CODE_OTHER;
            default:
                return MyConstants.M_CODE_OTHER;
        }
    }

    private boolean checkFields(){
        return !binding.acPaintBrand.getText().toString().trim().isEmpty()
                && !binding.etPaintCode.getText().toString().trim().isEmpty()
                && !mCurrentPhotoPath.isEmpty();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case com.kutovenko.kitstasher.R.id.btnPaintSave:
                String newBrand = binding.acPaintBrand.getText().toString().trim();
                if (!myBrands.contains(newBrand) && newBrand.length() > 1) {
                    myBrands.add(newBrand);
                    dbConnector.addBrand(newBrand);
                    acAdapterMybrands = new ArrayAdapter<>(context,
                            android.R.layout.simple_dropdown_item_1line, myBrands);
                    binding.acPaintBrand.setAdapter(acAdapterMybrands);
                }
                if (checkFields()) {
                    String brand = binding.acPaintBrand.getText().toString().trim();
                    String code = binding.etPaintCode.getText().toString().trim();
                    String name = binding.etPaintName.getText().toString().trim();
                    String category = getSupplyCategoryFromPosition(binding.spSupplyCategory.getSelectedItemPosition());
                    SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.ACCOUNT_PREFS,
                            Context.MODE_PRIVATE);
                    String ownerId = sharedPreferences.getString(MyConstants.USER_ID_PARSE, MyConstants.EMPTY);
                    StashItem supplyItem = new StashItem.StashItemBuilder(MyConstants.TYPE_SUPPLY)
                            .hasBrand(brand)
                            .hasBrand_catno(code)
                            .hasKitName(name)
                            .hasBoxartUri(mCurrentPhotoPath)
                            .hasCategory(category)
                            .build();
                    if (isExisting){
                        supplyItem.setLocalId(id);
                        ContentValues cv = new ContentValues(5);
                        cv.put(DbConnector.COLUMN_BRAND, brand);
                        cv.put(DbConnector.COLUMN_BRAND_CATNO, code);
                        cv.put(DbConnector.COLUMN_KIT_NAME, name);
                        cv.put(DbConnector.COLUMN_BOXART_URI, mCurrentPhotoPath);
                        cv.put(DbConnector.COLUMN_CATEGORY, category);
                        if (!supplyItem.editStashItem(dbConnector, cv)){
                            Toast.makeText(context, "Database Error. Can't write to local database", Toast.LENGTH_SHORT).show();
                        }
                    }else{
//                        if(Helper.isOnline(context)){
//                            supplyItem.saveToStashWhenOnline(dbConnector, mCurrentPhotoPath, imageFileName, ownerId);
//                        }else{
                            supplyItem.saveToLocalStash(dbConnector);
//                        }
                    }
                    returnToKits();

                } else {
                    Toast.makeText(context, getString(com.kutovenko.kitstasher.R.string.please_fill_fields), Toast.LENGTH_SHORT).show();
                }
                break;

            case com.kutovenko.kitstasher.R.id.btnCancelPaint:
                returnToKits();
                break;

            case com.kutovenko.kitstasher.R.id.ivPaintBox:
                takePicture();
                break;
        }
    }

    private void returnToKits() {
        KitsFragment fragment = KitsFragment.newInstance();
        Bundle bundle = new Bundle(1);
        bundle.putString(MyConstants.ITEM_TYPE, MyConstants.TYPE_SUPPLY);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, fragment);
        fragmentTransaction.commit();
    }

    private void takePicture() {
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
            Toast.makeText(context, com.kutovenko.kitstasher.R.string.cant_make_file, Toast.LENGTH_LONG).show();
        }
        if (image != null) {
            mCurrentPhotoPath = image.getAbsolutePath();
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(MyConstants.FILE_URI, mCurrentPhotoPath);
            editor.apply();
        } else {
            Toast.makeText(context, com.kutovenko.kitstasher.R.string.cant_make_file, Toast.LENGTH_LONG).show();
        }
        return image;
    }

    private String getTimestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.camera_failure, Toast.LENGTH_LONG).show();
        }else {
            if (requestCode == MainActivity.REQUEST_CODE_CAMERA) {
                Intent cropIntent = new Intent(getActivity(), CropActivity.class);
                cropIntent.putExtra(MyConstants.FILE_URI, mCurrentPhotoPath);
                startActivityForResult(cropIntent, REQUEST_CODE_CROP);
            } else if (requestCode == REQUEST_CODE_CROP) {
                File image = new File(mCurrentPhotoPath);
                Glide
                        .with(context)
                        .load(image)
                        .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera).error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                        .into(binding.ivPaintBox);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.crop_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBoxartTemporary) {
            File file = new File(mCurrentPhotoPath);
            file.deleteOnExit();
        }
        dbConnector.close();
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
