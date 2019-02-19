package com.kutovenko.kitstasher.model;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.kutovenko.kitstasher.db.DbConnector;
import com.kutovenko.kitstasher.util.MyConstants;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

public class SupplyItem {
    private String brand;
    private String colorName;
    private String colorCode;
    private String photoUri;
    private String photoUrl;
    private String fsCode;
    private int paintType;
    private long localId;
    private String category;

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

    private SupplyItem(SupplyItemBuilder builder){
        this.brand = builder.brand;
        this.colorName = builder.colorName;
        this.colorCode = builder.colorCode;
        this.photoUri = builder.photoUri;
        this.photoUrl = builder.photoUrl;
        this.fsCode = builder.fsCode;
        this.paintType = builder.paintType;
        this.category = builder.category;
        String itemType = builder.itemType;
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


    public long saveToLocalDb(DbConnector dbConnector){
        ContentValues cv = new ContentValues();
        cv.put(DbConnector.COLUMN_BRAND_CATNO, this.getcolorCode());
        cv.put(DbConnector.COLUMN_KIT_NAME, this.getColorName());
        cv.put(DbConnector.COLUMN_BRAND, this.getBrand());
        cv.put(DbConnector.COLUMN_BOXART_URL, this.getUrl());
        cv.put(DbConnector.COLUMN_BOXART_URI, this.getUri());
        cv.put(DbConnector.COLUMN_ITEMTYPE, MyConstants.TYPE_SUPPLY);
        return dbConnector.addSupply(cv);
    }

    public void saveToParse(String imagePath, String imageFileName){

    }

    /**
     * Saves thumbnail, boxart to parse, put boxart url in kit, returns kit online id
     * @param imagePath
     * @param imageFileName
     */
    public String saveBoxartToParse(String imagePath, String imageFileName) {
        String boxartUrl;
        try {
            boxartUrl = prepareUrlFromThumbnail(getThumbnailUrl(imagePath, imageFileName,
                    MyConstants.SIZE_SMALL_HEIGHT,
                    MyConstants.SIZE_SMALL_WIDTH));
            getThumbnailUrl(imagePath, imageFileName,
                    MyConstants.SIZE_FULL_HEIGHT,
                    MyConstants.SIZE_FULL_WIDTH);
//            saveAndGetParseStashId(context);
        } catch (Exception ex) {
            boxartUrl = "";
        }
        return boxartUrl;
    }


    private int saveWithBoxartAndThumbnail(String imagePath, String imageFileName, final String ownerId){
        final int[] url = new int[1];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        getResizedBitmap(bmp, MyConstants.SIZE_FULL_HEIGHT, MyConstants.SIZE_FULL_WIDTH)
                .compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] data = stream.toByteArray();
        String fullName = imageFileName + MyConstants.SIZE_FULL + MyConstants.JPG;
        final ParseFile imageFile = new ParseFile(fullName, data);
        ParseObject boxartToSave = new ParseObject(MyConstants.PARSE_C_BOXART);
        boxartToSave.put(MyConstants.PARSE_IMAGE, imageFile);
        boxartToSave.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) {
                if (ex == null) {
                    saveSupplyToParse(imageFile.getUrl(), ownerId);
                    url[1] = 0;
                }else{
                    url[0] = 1;
                }
            }
        });
//        saveThumbnailToParse();
        return url[0];
    }

    private void saveSupplyToParse(String boxartUrl, String ownerId) {
        final String[] parseId = new String[1];
        final ParseObject kitTowrite = new ParseObject(MyConstants.PARSE_C_STASH);
        kitTowrite.put(MyConstants.PARSE_OWNERID, ownerId);
        kitTowrite.put(MyConstants.PARSE_BRAND, this.getBrand());
        kitTowrite.put(MyConstants.PARSE_BRAND_CATNO, this.getcolorCode());
        kitTowrite.put(MyConstants.PARSE_KITNAME, this.getColorName());
        if (!TextUtils.isEmpty(this.getUrl())) {
            kitTowrite.put(MyConstants.BOXART_URL, this.getUrl());
        }
        kitTowrite.put(MyConstants.PARSE_LOCALID, this.getLocalId());
        kitTowrite.put(MyConstants.PARSE_ITEMTYPE, MyConstants.TYPE_SUPPLY);
        kitTowrite.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
//                parseId[0] = kitTowrite.getObjectId();
//                SupplyItem.this.set
//                ContentValues cv = new ContentValues(1);
//                cv.put(DbConnector.COLUMN_ID_ONLINE, parseId[0]);
//                editSupply(dbConnector, cv);
            }
        });
    }

    /**
     * return PART url of file
     * @param imagePath
     * @param imageFileName
     * @param height
     * @param width
     */
    private String getThumbnailUrl(String imagePath, String imageFileName, final int height, final int width) {
        final String[] url = new String[1];
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
                    url[0] = file.getUrl();
                }else{
                    url[0] = "";
                }
            }
        });
        return url[0];
    }

    private String prepareUrlFromThumbnail(String url) {
        return url.substring(0, url.length() - 5);
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


    public void editSupply(DbConnector dbConnector, ContentValues cv){
        dbConnector.editSupply(this.getLocalId(), cv);
    }



    public static class SupplyItemBuilder {
        private String brand;
        private String colorName;
        private String colorCode;
        private String photoUri;
        private String photoUrl;
        private String fsCode;
        private String category;
        private int paintType;
        private String itemType;

        public SupplyItemBuilder hasCategory (String category){
            this.category = category;
            return this;
        }

        public SupplyItemBuilder hasFsCode (String fsCode){
            this.fsCode = fsCode;
            return this;
        }

        public SupplyItemBuilder hasPaintType(int paintType){
            this.paintType = paintType;
            return this;
        }

        public SupplyItemBuilder hasBrand(String brand){
            this.brand = brand;
            return this;
        }

        public SupplyItemBuilder hasName(String name){
            this.colorName = name;
            return this;
        }

        public SupplyItemBuilder hasCode(String code){
            this.colorCode = code;
            return this;
        }

        public SupplyItemBuilder hasUri(String uri){
            this.photoUri = uri;
            return this;
        }

        public SupplyItemBuilder hasUrl(String url){
            this.photoUrl = url;
            return this;
        }

        public SupplyItemBuilder hasItemType(String typePaint) {
            this.itemType = typePaint;
            return this;
        }

        public SupplyItem build(){
            return new SupplyItem(this);
        }
    }
}
