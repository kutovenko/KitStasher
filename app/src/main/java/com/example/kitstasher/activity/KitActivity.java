package com.example.kitstasher.activity;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.fragment.ListViewFragment;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import static android.R.drawable.ic_menu_camera;
import static java.lang.Integer.parseInt;

public class KitActivity extends AppCompatActivity implements View.OnClickListener {
    private int position;
    private long id;
    private int categoryToReturn;
    private ImageView ivEditorBoxart;
    private EditText etDetFullKitname, etDetFullBrand, etDetFullBrandCatNo, etDetFullScale,
            etDetFullKitNoengname;
    private LinearLayout linLayoutAir, linLayoutGround, linLayoutSea, linLayoutSpace, linLayoutCar,
            linLayoutOther;
    private Button btnSaveEdit, btnCancelEdit, btnDelete, btnRestoreImage, btnAddBoxart;
    AppCompatSpinner spKitDescription, spKitYear;
//    private int PICK_IMAGE_REQUEST = 1;
    private String boxart_url, incomeCategory; //incomeCategory - исходная категория.
    private String categoryTab, category, listname; // для переключения к вкладке. при изменении совпадает с category, иначе то, что было (пришло или сохранено в записи)
    private char mode;
    private DbConnector dbConnector;
    private Cursor cursor;
//    private SharedPreferences mSettings;
    private Uri uri;
//    private View view;
    private boolean isRbChanged;

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

    private ArrayAdapter<String> descriptionAdapter, yearsAdapter;

//    private final String SIZE_FULL = "-pristine";
//    private final String SIZE_MEDIUM = "-t280";
//    private final String SIZE_SMALL = "-t140";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit);

        context = KitActivity.this;

        position = getIntent().getExtras().getInt(Constants.LIST_POSITION);
        id = getIntent().getExtras().getLong(Constants.LIST_ID);
        categoryToReturn = getIntent().getExtras().getInt(Constants.LIST_CATEGORY);
//        categoryToReturn = "_id";
        mode = getIntent().getExtras().getChar("mode");
        dbConnector = new DbConnector(this);
        dbConnector.open();

        initUI();

        if (savedInstanceState != null) {
            bmBoxartPic = savedInstanceState.getParcelable("boxartImage");
            ivEditorBoxart.setImageBitmap(bmBoxartPic);
        }

        isRbChanged = false;

        /////////////////Работа с камерой

        pictureName = "";

        /////////////// изображение - добавлено
        if (mode == 'l'){
            cursor = dbConnector.getListItemById(id);
        }else {
            cursor = dbConnector.getRecById(id);
        }
        cursor.moveToFirst();

        category = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY)); //беру категогию из записи
        /////////////////////// радиокнопки
        setTag(category);

        etDetFullBrand.setText(cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BRAND)));
        etDetFullBrandCatNo.setText(cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BRAND_CATNO)));
        etDetFullKitname.setText(cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_KIT_NAME)));
        etDetFullScale.setText(cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_SCALE)));
        if (cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_ORIGINAL_KIT_NAME)) != null){
            etDetFullKitNoengname.setText(cursor.getString(cursor.getColumnIndex
                    (DbConnector.COLUMN_ORIGINAL_KIT_NAME)));
        }

        String[] descriptionItems = new String[]{getString(R.string.kittype),
                getString(R.string.newkit),
                getString(R.string.rebox),
//                getString(R.string.new_tool),
//                getString(R.string.reissue),
//                getString(R.string.changed_parts), getString(R.string.new_decal),
//                getString(R.string.changed_box), getString(R.string.repack)
        };
        descriptionAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, descriptionItems);
        spKitDescription.setAdapter(descriptionAdapter);
        spKitDescription.setSelection(2);

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add(getString(R.string.year));
        for (int i = thisYear; i >= 1930; i--) {
            years.add(Integer.toString(i));
        }
        yearsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, years);
        spKitYear.setAdapter(yearsAdapter);

        ////....


//        String year = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_YEAR));
        String year = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_YEAR));
        setKitYear(year);


        String description = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_DESCRIPTION));
//        Toast.makeText(this, description, Toast.LENGTH_SHORT).show();
        setKitDescription(description);


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
//                        desc = getString(R.string.changed_parts);
                        desc = getString(R.string.rebox);

                        break;
                    case "3":
//                        desc = getString(R.string.new_decal);
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
//        Toast.makeText(this, year + "+", Toast.LENGTH_SHORT).show();
        if (year.length() == 4 && !year.contains("-")) {
            int spYearPosition = yearsAdapter.getPosition(year);
            spKitYear.setSelection(spYearPosition);
        }else{
            spKitYear.setSelection(0); //оставляем на первой
        }
    }

    private void setBoxartImage() {
        if (mode == 'l'){
            cursor = dbConnector.getListItemById(id);
        }else{
            cursor = dbConnector.getRecById(id);
        }

        cursor.moveToFirst();
        if (cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI)) != null
                && cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI)).length() > 1){

            File imgFile = new  File(Constants.FOLDER_SDCARD_KITSTASHER
                    + cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI)));

            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivEditorBoxart.setBackgroundResource(0);
                ivEditorBoxart.setImageBitmap(myBitmap);
                pictureName = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URI));
            }
        }else if (cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URL)) != null){
            boxart_url = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BOXART_URL));
            ivEditorBoxart.setBackgroundResource(0);
            Glide
                    .with(context)
                    .load(composeUrl(boxart_url))
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
                    + Constants.BOXART_URL_POSTFIX;
        }else{
            return "";
        }

    }

    private String getSuffix(){
        String suffix = Constants.BOXART_URL_SMALL;
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
        super.onActivityResult(requestCode, resultCode, data);
////Choose from gallery
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            uri = data.getData();
//            try {
//                bmBoxartPic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                ivEditorBoxart.setImageBitmap(bmBoxartPic);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        if (resultCode == RESULT_OK) {
            // Вернулись от приложения Камера
            if (requestCode == CAMERA_CAPTURE) {
                // Получим Uri снимка
                picUri = data.getData();
                // кадрируем его
                performCrop();
            }else if(requestCode == PIC_CROP){             // Вернулись из операции кадрирования
                Bundle extras = data.getExtras();
                // Получим кадрированное изображение
                bmBoxartPic = extras.getParcelable("data");

                bmBoxartPic = Bitmap.createScaledBitmap(bmBoxartPic, 640, 395, false);
                bytes = new ByteArrayOutputStream();
                bmBoxartPic.compress(Bitmap.CompressFormat.JPEG, 70, bytes);

                ivEditorBoxart.setImageBitmap(bmBoxartPic);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linLayoutSelectAir:
                category = Constants.CAT_AIR;
                isRbChanged = true;
                clearTags();
                setTag(category);
                break;
            case R.id.linLayoutSelectGround:
                category = Constants.CAT_GROUND;
                isRbChanged = true;
                clearTags();
                setTag(category);
                break;
            case R.id.linLayoutSelectSea:
                category = Constants.CAT_SEA;
                isRbChanged = true;
                clearTags();
                setTag(category);
                break;
            case R.id.linLayoutSelectSpace:
                category = Constants.CAT_SPACE;
                isRbChanged = true;
                clearTags();
                setTag(category);
                break;
            case R.id.linLayoutSelectOther:
                category = Constants.CAT_OTHER;
                isRbChanged = true;
                clearTags();
                setTag(category);
                break;
            case R.id.linLayoutSelectCar:
                category = Constants.CAT_AUTOMOTO;
                isRbChanged = true;
                clearTags();
                setTag(category);
                break;

            //////////////////////////////////////////////////////////////////////////////////

//            case R.id.btnGetFromFile:
//                Intent intent = new Intent();
//// Show only images, no videos or anything else
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//// Always show the chooser (if there are multiple options available)
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//                break;

////////////////////////////////////////////////////////////////////////////////


            case R.id.ivEditorBoxart:
                viewPicture();
                break;


///////////////////////////////////////////////////////////////////////////////
            case R.id.btnSaveEdit:
                if (bmBoxartPic != null) {
                    size = Constants.SIZE_FULL;

                    File pictures = Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    pictureName = etDetFullBrand.getText().toString()
                            + etDetFullBrandCatNo.getText().toString()
                            + size
                            + ".jpg";
                    photoFile = new File(pictures, pictureName);
                    context = this;

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
                            + ".jpg";
                    writeBoxartFile(exportDir);

                    bmBoxartPic = Bitmap.createScaledBitmap(bmBoxartPic, 140, 86, false);
                    size = Constants.SIZE_SMALL;
                    pictureName = etDetFullBrand.getText().toString()
                            + etDetFullBrandCatNo.getText().toString()
                            + size
                            + ".jpg";
                    writeBoxartFile(exportDir);
                }


                ContentValues cv = new ContentValues();
                cv.put("brand", etDetFullBrand.getText().toString().trim());
                cv.put("kit_name", etDetFullKitname.getText().toString().trim());
                cv.put("brand_catno", etDetFullBrandCatNo.getText().toString().trim());
                cv.put("scale", parseInt(etDetFullScale.getText().toString().trim()));
                cv.put("original_kit_name", etDetFullKitNoengname.getText().toString().trim());
//                cv.put("description", etDetFullDescription.getText().toString());
                cv.put("category", category);
                if (pictureName != null || pictureName.trim().length() > 0) {
                    cv.put("boxart_uri", pictureName);
                }else{
                    cv.put("boxart_uri", "");
                }
                String y = spKitYear.getSelectedItem().toString();
                cv.put("year", getKitYear(y));
                
                String d = spKitDescription.getSelectedItem().toString();
                cv.put(DbConnector.COLUMN_DESCRIPTION, getKitDescription(d));
                if (mode == 'l'){
                    dbConnector.editListItemById(id, cv);
                }else{
                    dbConnector.editRecById(id, cv);
                }

                if (isRbChanged){/////????????????????????????
                    categoryTab = category;
                }else{
                    categoryTab = incomeCategory;
                }
                if (mode == 'l'){
                    Intent intent3 = new Intent();
                    intent3.putExtra(Constants.LIST_POSITION, position);
                    intent3.putExtra(Constants.LIST_ID, id);
                    intent3.putExtra(Constants.LIST_CATEGORY, categoryToReturn);
                    setResult(RESULT_OK, intent3);
                    finish();
                }else {
                    Intent intent3 = new Intent();
                    intent3.putExtra(Constants.LIST_POSITION, position);
                    intent3.putExtra(Constants.LIST_ID, id);
                    intent3.putExtra(Constants.LIST_CATEGORY, categoryToReturn);
                    setResult(RESULT_OK, intent3);
                    finish();
                }
                break;

/////////////////////////////////////////////////////////////////////////////////////////////

            case R.id.btnCancelEdit:

                Intent intent1 = new Intent();
                intent1.putExtra("position", position);
                intent1.putExtra("id", id);
                intent1.putExtra("category", categoryToReturn);
                setResult(RESULT_OK, intent1);
                finish();
                break;

            ///////////////////////////////////////////////////////////////////////////////////

            case R.id.btnDelete:
                //Delete to Trash
//                ContentValues cv2 = new ContentValues();
//                cv2.put("is_deleted", 1);
//                dbConnector.editRecById(id, cv2);
                //Permanent delete
                if (mode =='l'){
                    dbConnector.delListItem(id);
                }else {
                    dbConnector.delRec(id);
                }
                Intent intent2 = new Intent();
                intent2.putExtra("position", position);
                intent2.putExtra("id", id);
                intent2.putExtra("category", categoryToReturn);
                setResult(RESULT_OK, intent2);
                finish();
                break;

            case R.id.btnAddBoxart:
                takePicture();
                break;

            case R.id.btnRestoreImage:
                ContentValues cvUri = new ContentValues();
                cvUri.put("boxart_uri", "");
                dbConnector.editRecById(id, cvUri);
                setBoxartImage();
                break;
        }

    }

    private String getKitYear(String y) {
        if (!y.equals(getString(R.string.year))){
            return y;
        }else{
            return "";
        }
    }

    private void viewPicture() {
        // TODO: 13.07.2017 открывать в просмотрщике 
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.setDataAndType(Uri.parse(ivEditorBoxart.getDrawable().get), "image/*");
//        startActivity(intent);
    }

    private void takePicture() {
        try {
            // Намерение для запуска камеры
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // Выводим сообщение об ошибке
            String errorMessage = "Ваше устройство не поддерживает съемку";
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private String getKitDescription(String d) {
        String desc = "";
//      String d = spDescription.getSelectedItem().toString();
        if (!d.equals(getString(R.string.kittype))){
            desc = descToCode(d);
        }
        return desc;
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

    private void setTag(String category) {
        switch (category){
            case Constants.CAT_AIR:
                linLayoutAir.setBackgroundColor(Helper.getColor(this, R.color.colorAccent));
//                categoryToReturn = 1;
                break;
            case Constants.CAT_GROUND:
                linLayoutGround.setBackgroundColor(Helper.getColor(this, R.color.colorAccent));
//                categoryToReturn = 2;
                break;
            case Constants.CAT_SEA:
                linLayoutSea.setBackgroundColor(Helper.getColor(this, R.color.colorAccent));
//                categoryToReturn = 3;
                break;
            case Constants.CAT_SPACE:
                linLayoutSpace.setBackgroundColor(Helper.getColor(this, R.color.colorAccent));
//                categoryToReturn = 4;
                break;
            case Constants.CAT_AUTOMOTO:
                linLayoutCar.setBackgroundColor(Helper.getColor(this, R.color.colorAccent));
//                categoryToReturn = 5;
                break;
            case Constants.CAT_OTHER:
                linLayoutOther.setBackgroundColor(Helper.getColor(this, R.color.colorAccent));
//                categoryToReturn = 6;
                break;
        }
    }

    private void clearTags() {
        linLayoutSpace.setBackgroundColor(Helper.getColor(this, R.color.colorItem));
        linLayoutAir.setBackgroundColor(Helper.getColor(this, R.color.colorItem));
        linLayoutSea.setBackgroundColor(Helper.getColor(this, R.color.colorItem));
        linLayoutGround.setBackgroundColor(Helper.getColor(this, R.color.colorItem));
        linLayoutCar.setBackgroundColor(Helper.getColor(this, R.color.colorItem));
        linLayoutOther.setBackgroundColor(Helper.getColor(this, R.color.colorItem));
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



    private void performCrop(){
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
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch(ActivityNotFoundException anfe){
            String errorMessage = "Извините, но ваше устройство не поддерживает кадрирование";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }




    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (bmBoxartPic != null){
            outState.putParcelable("boxartImage", bmBoxartPic);
        }

    }

    private void initUI(){
        ivEditorBoxart = (ImageView)findViewById(R.id.ivEditorBoxart);
        ivEditorBoxart.setOnClickListener(this);
        etDetFullKitname = (EditText)findViewById(R.id.etDetFullKitname);
        etDetFullBrand = (EditText)findViewById(R.id.etDetFullBrand);
        etDetFullBrandCatNo = (EditText)findViewById(R.id.etDetFullBrandCatNo);
        etDetFullScale = (EditText)findViewById(R.id.etDetFullScale);
        etDetFullKitNoengname = (EditText)findViewById(R.id.etDetKitOrigName);

        linLayoutAir = (LinearLayout)findViewById(R.id.linLayoutSelectAir);
        linLayoutAir.setOnClickListener(this);
        linLayoutCar = (LinearLayout)findViewById(R.id.linLayoutSelectCar);
        linLayoutCar.setOnClickListener(this);
        linLayoutGround = (LinearLayout)findViewById(R.id.linLayoutSelectGround);
        linLayoutGround.setOnClickListener(this);
        linLayoutOther = (LinearLayout)findViewById(R.id.linLayoutSelectOther);
        linLayoutOther.setOnClickListener(this);
        linLayoutSea = (LinearLayout)findViewById(R.id.linLayoutSelectSea);
        linLayoutSea.setOnClickListener(this);
        linLayoutSpace = (LinearLayout)findViewById(R.id.linLayoutSelectSpace);
        linLayoutSpace.setOnClickListener(this);

        btnSaveEdit = (Button)findViewById(R.id.btnSaveEdit);
        btnSaveEdit.setOnClickListener(this);
        btnCancelEdit = (Button)findViewById(R.id.btnCancelEdit);
        btnCancelEdit.setOnClickListener(this);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);
        btnAddBoxart = (Button)findViewById(R.id.btnAddBoxart);
        btnAddBoxart.setOnClickListener(this);
        btnRestoreImage = (Button)findViewById(R.id.btnRestoreImage);
        btnRestoreImage.setOnClickListener(this);

        spKitDescription = (AppCompatSpinner)findViewById(R.id.spKitDescription);
        spKitYear = (AppCompatSpinner)findViewById(R.id.spKitYear);
//        btnGetFromFile = (Button)findViewById(R.id.btnGetFromFile);
//        btnGetFromFile.setOnClickListener(this);
//        btnGetFromCamera = (Button)findViewById(R.id.btnGetFromCamera);
//        btnGetFromCamera.setOnClickListener(this);
    }


}
