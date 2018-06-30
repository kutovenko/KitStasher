package com.example.kitstasher.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

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

    public void addPaint(){
        DbConnector dbConnector = new DbConnector(context);
        dbConnector.open();
        dbConnector.addPaint(this);
        dbConnector.close();
    }

    public void archivePaint(){
        DbConnector dbConnector = new DbConnector(context);
        dbConnector.open();

        dbConnector.close();
    }

    public void editPaint(){
        DbConnector dbConnector = new DbConnector(context);
        dbConnector.open();

        dbConnector.close();
    }

    public void deletePaint(){
        DbConnector dbConnector = new DbConnector(context);
        dbConnector.open();

        dbConnector.close();
    }

    public String saveToOnlineStash(final Context context) {
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
        kitTowrite.put(MyConstants.PARSE_ITEMTYPE, MyConstants.TYPE_PAINT);
        kitTowrite.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                parseId[0] = kitTowrite.getObjectId();
            }
        });
        return parseId[0];
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
        private String fsCode;
        private String category;
        private int paintType;

        public PaintItemBuilder(){

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

        public PaintItem build(){
            return new PaintItem(this);
        }
    }

}
