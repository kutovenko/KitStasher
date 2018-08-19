package com.example.kitstasher.objects;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

import static com.example.kitstasher.other.DbConnector.COLUMN_BOXART_URI;
import static com.example.kitstasher.other.DbConnector.COLUMN_BOXART_URL;
import static com.example.kitstasher.other.DbConnector.COLUMN_BRAND;
import static com.example.kitstasher.other.DbConnector.COLUMN_BRAND_CATNO;
import static com.example.kitstasher.other.DbConnector.COLUMN_CATEGORY;
import static com.example.kitstasher.other.DbConnector.COLUMN_ITEMTYPE;
import static com.example.kitstasher.other.DbConnector.COLUMN_KIT_NAME;

public class PaintItem {
    private String brand;
    private String colorName;
    private String colorCode;
    private String photoUri;
    private String photoUrl;
    private String fsCode;
    private String itemType;
    private int paintType;
    private long localId;
    private String category;
    private Context context;

    public String getFsCode() {
        return fsCode;
    }

    public void setFsCode(String fsCode) {
        this.fsCode = fsCode;
    }

    public int getPaintType() {
        return paintType;
    }

    public void setPaintType(int paintType) {
        this.paintType = paintType;
    }

    private PaintItem(PaintItemBuilder builder){
        this.brand = builder.brand;
        this.colorName = builder.colorName;
        this.colorCode = builder.colorCode;
        this.photoUri = builder.photoUri;
        this.photoUrl = builder.photoUrl;
        this.fsCode = builder.fsCode;
        this.paintType = builder.paintType;
        this.context = builder.context;
        this.category = builder.category;
        this.itemType = builder.itemType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String name) {
        this.colorName = name;
    }

    public String getcolorCode() {
        return colorCode;
    }

    public void setCode(String code) {
        this.colorCode = code;
    }

    public String getUri() {
        return photoUri;
    }

    public void setUri(String uri) {
        this.photoUri = uri;
    }

    public String getUrl() {
        return photoUrl;
    }

    public void setUrl(String url) {
        this.photoUrl = url;
    }

    public void saveToLocalDb(Context context){
        DbConnector dbConnector = new DbConnector(context);
        dbConnector.open();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BRAND_CATNO, this.getcolorCode());
        cv.put(COLUMN_KIT_NAME, this.getColorName());
        cv.put(COLUMN_BRAND, this.getBrand());
        cv.put(COLUMN_BOXART_URL, this.getUrl());
        cv.put(COLUMN_BOXART_URI, this.getUri());
//        cv.put(COLUMN_CATEGORY, this.getCategory());
        cv.put(COLUMN_ITEMTYPE, MyConstants.TYPE_PAINT);
        dbConnector.addPaint(cv);
        dbConnector.close();
        // TODO: 17.08.2018 return long local_id?
    }

    public void saveWithBoxartToParse(String imagePath, String imageFileName) {
        try {
            saveThumbnail(imagePath, imageFileName,
                    MyConstants.SIZE_SMALL_HEIGHT,
                    MyConstants.SIZE_SMALL_WIDTH);
            saveThumbnail(imagePath, imageFileName,
                    MyConstants.SIZE_FULL_HEIGHT,
                    MyConstants.SIZE_FULL_WIDTH);
            saveToOnlineStash(context);
        } catch (Exception ex) {
        }
    }

    private String saveToOnlineStash(final Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        final String[] parseId = new String[1];
        final ParseObject kitTowrite = new ParseObject(MyConstants.PARSE_C_STASH);
        kitTowrite.put(MyConstants.PARSE_OWNERID, sharedPreferences.getString(MyConstants.USER_ID_PARSE, MyConstants.EMPTY));
        kitTowrite.put(MyConstants.PARSE_BRAND, this.getBrand());
        kitTowrite.put(MyConstants.PARSE_BRAND_CATNO, this.getcolorCode());
        kitTowrite.put(MyConstants.PARSE_KITNAME, this.getColorName());
        if (!TextUtils.isEmpty(this.getUrl())) {
            kitTowrite.put(MyConstants.BOXART_URL, this.getUrl());
        }
        kitTowrite.put(MyConstants.PARSE_LOCALID, this.getLocalId());
//        kitTowrite.put(MyConstants.PARSE_CATEGORY, this.getCategory());
        kitTowrite.put(MyConstants.PARSE_ITEMTYPE, MyConstants.TYPE_PAINT);
        kitTowrite.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                parseId[0] = kitTowrite.getObjectId();
            }
        });
        ContentValues cv = new ContentValues(1);
        cv.put(DbConnector.COLUMN_ID_ONLINE, parseId[0]);
        editPaint(cv);
        return parseId[0];
    }

    private void saveThumbnail(String imagePath, String imageFileName, final int height, final int width) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        getResizedBitmap(bmp, height, width).compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] data = stream.toByteArray();
        String nameBody;
        if (height != MyConstants.SIZE_FULL_HEIGHT) {
            nameBody = MyConstants.SIZE_SMALL;
        }else{
            nameBody = MyConstants.SIZE_FULL;
        }
        String fullName = imageFileName + nameBody + MyConstants.JPG;
        final ParseFile file = new ParseFile(fullName, data);
        ParseObject boxartToSave = new ParseObject(MyConstants.PARSE_C_BOXART);
        boxartToSave.put(MyConstants.PARSE_IMAGE, file);
        boxartToSave.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) {
                if (ex == null && height == MyConstants.SIZE_FULL_HEIGHT) {
                    PaintItem.this.setUrl(file.getUrl());
                    saveToOnlineStash(context);
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




    public void archivePaint(){

    }

    private void editPaint(ContentValues cv){
        DbConnector dbConnector = new DbConnector(context);
        dbConnector.open();
        dbConnector.editPaint(this.getLocalId(), cv);
        dbConnector.close();
    }

    public void deletePaint(){

    }

    public void editPaint(){

    }



    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public static class PaintItemBuilder{
        private String brand;
        private String colorName;
        private String colorCode;
        private String photoUri;
        private String photoUrl;
        private String fsCode; //
        private String category;
        private int paintType; //
        private Context context;
        private String itemType;

        public PaintItemBuilder(Context context){
            this.context = context;
            this.itemType = MyConstants.TYPE_PAINT;
        }

        public PaintItemBuilder hasCategory (String category){
            this.category = category;
            return this;
        }

        public PaintItemBuilder hasFsCode (String fsCode){
            this.fsCode = fsCode;
            return this;
        }

        public PaintItemBuilder hasPaintType(int paintType){
            this.paintType = paintType;
            return this;
        }

        public PaintItemBuilder hasBrand(String brand){
            this.brand = brand;
            return this;
        }

        public PaintItemBuilder hasName(String name){
            this.colorName = name;
            return this;
        }

        public PaintItemBuilder hasCode(String code){
            this.colorCode = code;
            return this;
        }

        public PaintItemBuilder hasUri(String uri){
            this.photoUri = uri;
            return this;
        }

        public PaintItemBuilder hasUrl(String url){
            this.photoUrl = url;
            return this;
        }

        public PaintItemBuilder hasItemType(String typePaint) {
            this.itemType = typePaint;
            return this;
        }

        public PaintItem build(){
            return new PaintItem(this);
        }


    }

}
