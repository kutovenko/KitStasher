package com.example.kitstasher.fragment;

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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kitstasher.BuildConfig;
import com.example.kitstasher.R;
import com.example.kitstasher.activity.CropActivity;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.objects.PaintItem;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.example.kitstasher.activity.MainActivity.MY_PERMISSIONS_REQUEST_CAMERA;
import static com.example.kitstasher.activity.MainActivity.MY_PERMISSIONS_REQUEST_WRITE;
import static com.example.kitstasher.activity.MainActivity.REQUEST_CODE_CROP;

public class AddPaintFragment extends Fragment implements View.OnClickListener, TextWatcher {
    private PaintItem paintItem;
    private ImageView ivPaintBox;
    private EditText etPaintName, etPaintCode;
    private AutoCompleteTextView acTvPaintBrand;
    private String mCurrentPhotoPath = MyConstants.EMPTY;

    private ArrayList<String> myBrands;
    private ArrayAdapter<String> acAdapterMybrands;

    private DbConnector dbConnector;
    private Context context;
    private String imageFileName;
    private boolean isBoxartTemporary;
    private String sendStatus;
    private String ownerId;
    private String category;
    private long id;
    private boolean isEdited;

    public static AddPaintFragment newInstance() {
        return new AddPaintFragment();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imagePath", mCurrentPhotoPath);
        if (paintItem != null){
            outState.putString(MyConstants.BOXART_URL, paintItem.getUri());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dbConnector.close();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        checkCameraPermissions();
        checkWritePermissions();

        View view = inflater.inflate(R.layout.add_paint, container, false);
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        context = getActivity();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        ownerId = sharedPref.getString(MyConstants.USER_ID_PARSE, MyConstants.EMPTY);
        ivPaintBox = view.findViewById(R.id.ivPaintBox);
        ivPaintBox.setOnClickListener(this);
        etPaintCode = view.findViewById(R.id.etPaintCode);
        etPaintName = view.findViewById(R.id.etPaintName);
        acTvPaintBrand = view.findViewById(R.id.acPaintBrand);
        Button btnSave = view.findViewById(R.id.btnPaintSave);
        btnSave.setOnClickListener(this);
        Button btnCancel = view.findViewById(R.id.btnCancelPaint);
        btnCancel.setOnClickListener(this);

        if (savedInstanceState != null){
            mCurrentPhotoPath = savedInstanceState.getString("imagePath");
            if (mCurrentPhotoPath != null && !mCurrentPhotoPath.equals(MyConstants.EMPTY)) {
                Glide
                        .with(context)
                        .load(new File(Uri.parse(mCurrentPhotoPath).getPath()))
                        .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera).error(R.drawable.ic_menu_camera))
                        .into(ivPaintBox);
            }
        }

        myBrands = dbConnector.getBrandsNames();
        acAdapterMybrands = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, myBrands);
        acTvPaintBrand.addTextChangedListener(this);
        acTvPaintBrand.setAdapter(acAdapterMybrands);

        if (getArguments() != null){
            isEdited = true;
            id = getArguments().getLong(MyConstants.ID);
            Cursor cursor = dbConnector.getPaintById(id);
            cursor.moveToFirst();
            acTvPaintBrand.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND)));
            etPaintCode.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO)));
            etPaintName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME)));
            mCurrentPhotoPath = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
            String url = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URL));
            cursor.close();
            if (!Helper.isBlank(mCurrentPhotoPath)) {
                Glide
                        .with(context)
                        .load(new File(Uri.parse(mCurrentPhotoPath).getPath()))
                        .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera).error(R.drawable.ic_menu_camera))
                        .into(ivPaintBox);
            } else {
                Glide
                        .with(context)
                        .load(Helper.composeUrl(url))
                        .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera).error(R.drawable.ic_menu_camera))
                        .into(ivPaintBox);
            }
        }


        return view;
    }

    private boolean checkFields(){
        return !etPaintName.getText().toString().trim().isEmpty()
                && !etPaintCode.getText().toString().trim().isEmpty()
                && !mCurrentPhotoPath.isEmpty();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPaintSave:

                String newBrand = acTvPaintBrand.getText().toString().trim();
                if (!myBrands.contains(newBrand) && newBrand.length() > 1) {
                    myBrands.add(newBrand);
                    dbConnector.addBrand(newBrand);
                    acAdapterMybrands = new ArrayAdapter<>(context,
                            android.R.layout.simple_dropdown_item_1line, myBrands);
                    acTvPaintBrand.setAdapter(acAdapterMybrands);
                }



                if (checkFields()) {
                    String brand = acTvPaintBrand.getText().toString().trim();
                    String code = etPaintCode.getText().toString().trim();
                    String name = etPaintName.getText().toString().trim();
                    PaintItem paintItem = new PaintItem.PaintItemBuilder(context)
                            .hasBrand(brand)
                            .hasCode(code)
                            .hasName(name)
                            .hasUri(mCurrentPhotoPath)
//                            .hasItemType(MyConstants.TYPE_PAINT)
                            .build();
                    if (!isEdited){
                        paintItem.saveToLocalDb(context);
                        if(Helper.isOnline(context)){
                            paintItem.saveWithBoxartToParse(mCurrentPhotoPath, imageFileName);
                        }
                    }else{
                        ContentValues cv = new ContentValues(4);
                        cv.put(DbConnector.COLUMN_BRAND, brand);
                        cv.put(DbConnector.COLUMN_BRAND_CATNO, code);
                        cv.put(DbConnector.COLUMN_KIT_NAME, name);
                        cv.put(DbConnector.COLUMN_BOXART_URI, mCurrentPhotoPath);

                        dbConnector.editPaint(paintItem.getLocalId(), cv);

                    }
                    returnToKits();

                } else {
                    Toast.makeText(context, getString(R.string.please_fill_fields), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnCancelPaint:
                returnToKits();
                break;

            case R.id.ivPaintBox:
                takePicture();
                break;
        }
    }

    private void returnToKits() {
        KitsFragment fragment = KitsFragment.newInstance();
        Bundle bundle = new Bundle(1);
        bundle.putString(MyConstants.WORK_MODE, MyConstants.TYPE_PAINT);
        fragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainactivityContainer, fragment);
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
            Toast.makeText(context, R.string.cant_make_file, Toast.LENGTH_LONG).show();
        }
        if (image != null) {
            mCurrentPhotoPath = image.getAbsolutePath();
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(MyConstants.FILE_URI, mCurrentPhotoPath);
            editor.apply();
        } else {
            Toast.makeText(context, R.string.cant_make_file, Toast.LENGTH_LONG).show();
        }
        return image;
    }

    private String getTimestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(getActivity(), R.string.camera_failure, Toast.LENGTH_LONG).show();
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
                        .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera).error(R.drawable.ic_menu_camera))
                        .into(ivPaintBox);

            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(getActivity(), R.string.crop_error, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), R.string.we_cant_read_barcodes,
                        Toast.LENGTH_LONG).show();
                android.support.v4.app.Fragment fragment = NoPermissionFragment.newInstance(Manifest.permission.CAMERA, MyConstants.TYPE_PAINT);
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainactivityContainer, fragment);
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
