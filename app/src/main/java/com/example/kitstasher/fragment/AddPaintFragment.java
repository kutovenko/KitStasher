package com.example.kitstasher.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kitstasher.BuildConfig;
import com.example.kitstasher.R;
import com.example.kitstasher.activity.CropActivity;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.objects.PaintItem;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.example.kitstasher.activity.MainActivity.REQUEST_CODE_CROP;

public class AddPaintFragment extends Fragment implements View.OnClickListener{
    private PaintItem paintItem;
    private ImageView ivPaintBox;
    private EditText etPaintName, etPaintCode, etPaintBrand;
    private Button btnSave, btnCancel;
    private String mCurrentPhotoPath = "";

    private Spinner spPaintType;
    private DbConnector dbConnector;
    private Context context;
    private ArrayAdapter<String> typesAdapter;
    private String imageFileName;
    private boolean isBoxartTemporary;
    private String sendStatus;
    private String ownerId;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_paint, container, false);
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        context = getActivity();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        ownerId = sharedPref.getString(MyConstants.USER_ID_PARSE, MyConstants.EMPTY);
        ivPaintBox = view.findViewById(R.id.ivPaintBox);
        ivPaintBox.setOnClickListener(this);
        etPaintCode = view.findViewById(R.id.etPaintCode);
        etPaintName = view.findViewById(R.id.etPaintName);
        etPaintBrand = view.findViewById(R.id.etPaintBrand);
        spPaintType = view.findViewById(R.id.spPaintType);
        btnSave = view.findViewById(R.id.btnPaintSave);
        btnSave.setOnClickListener(this);
        btnCancel = view.findViewById(R.id.btnCancelPaint);
        btnCancel.setOnClickListener(this);

        if (savedInstanceState != null){
            if (mCurrentPhotoPath != null && !mCurrentPhotoPath.equals(MyConstants.EMPTY)) {
                Glide
                        .with(context)
                        .load(new File(Uri.parse(mCurrentPhotoPath).getPath()))
                        .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera).error(R.drawable.ic_menu_camera))
                        .into(ivPaintBox);
            }
        }

        ArrayList<String> types = new ArrayList<>();
        types.add(getString(R.string.unknown));
        types.add("Acryllic");
        types.add("Enamel");
        typesAdapter = new ArrayAdapter<>(context,
                R.layout.simple_spinner_item, types);
        spPaintType.setAdapter(typesAdapter);

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
                String category;
                if (checkFields()) {
                    switch (spPaintType.getSelectedItemPosition()){
                        case 0:
                            category = MyConstants.CODE_P_OTHER;
                            break;
                        case 1:
                            category = MyConstants.CODE_P_ACRYLLIC;
                            break;
                        case 2:
                            category = MyConstants.CODE_P_ENAMEL;
                            break;
                        default:
                            category = MyConstants.CODE_P_OTHER;
                            break;

                    }

                    PaintItem paintItem = new PaintItem.PaintItemBuilder()
                            .hasBrand(etPaintBrand.getText().toString().trim())
                            .hasCode(etPaintCode.getText().toString().trim())
                            .hasName(etPaintName.getText().toString().trim())
                            .hasUri(mCurrentPhotoPath)
                            .hasCategory(category)
//                            .hasUrl()
                            .build();
                    dbConnector.addPaint(paintItem);
                } else {
                    Toast.makeText(context, getString(R.string.please_fill_fields), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnCancelPaint:

                // TODO: 01.06.2018 возврат в список красок

                break;
            case R.id.ivPaintBox:
                takePicture();
                break;
        }
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
//            boxartUri = mCurrentPhotoPath;

            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(getActivity(), R.string.crop_error, Toast.LENGTH_SHORT).show();
        }

    }

    private void saveWithBoxartToParse(String imagePath, PaintItem paintSave) {
        try {
            saveThumbnail(paintSave, imagePath, MyConstants.SIZE_SMALL_HEIGHT, MyConstants.SIZE_SMALL_WIDTH);
            saveThumbnail(paintSave, imagePath, MyConstants.SIZE_FULL_HEIGHT, MyConstants.SIZE_FULL_WIDTH);
        } catch (Exception ex) {
            sendStatus = "n";
        }
    }

    private void saveThumbnail(final PaintItem paintSave, String imagePath, final int height, final int width) {
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
//        boxartToSave.put(MyConstants.PARSE_DESCRIPTION, getBoxartDescription());
        boxartToSave.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) {
                if (ex == null && height == MyConstants.SIZE_FULL_HEIGHT) {
                    paintSave.setUrl(file.getUrl());
                    saveOnline(paintSave);
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

//    private String getBoxartDescription() {
//        return spYear.getSelectedItem().toString() + "-" + spDescription.getSelectedItem().toString();
//    }

    private void saveOnline(PaintItem paintSave) {
//        paintSave.saveToNewKit(ownerId);
        paintSave.saveToOnlineStash(context);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBoxartTemporary) {
            File file = new File(mCurrentPhotoPath);
            file.deleteOnExit();
        }
    }
}
