package com.example.kitstasher.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**
 * Created by Алексей on 04.05.2017.
 */

public class Kit {

    //Required

    private String brand;
    private String brandCatno;
    private String kit_name;
    private int scale;
    private String category;
    //Optional
    private String barcode;
    private String kit_noeng_name;
    private String description;
    private String prototype;
    private String boxart_url;
    private String scalemates_url;
    private String boxart_uri;

    private String year;
    private String onlineId;

    private String date_added;


    private String datePurchased;
    private int quantity;
    private String notes;
    private int price;
    private String currency;
    private String sendStatus;
    private String placePurchased;

    private int media;
    private int status;


    public int getMedia() {
        return media;
    }

    public void setMedia(int media) {
        this.media = media;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public  String getBrand() {
        return brand;
    }
    public String getBrandCatno() {
        return brandCatno;
    }
    public String getKit_name() {
        return kit_name;
    }
    public int    getScale() {
        return scale;
    }
    public  String getCategory() {
        return category;
    }

    public String getBarcode() {
        return barcode;
    }
    public String getKit_noeng_name() {
        return kit_noeng_name;
    }
    public String getDescription() {
        return description;
    }
    public String getPrototype() {
        return prototype;
    }
    public String getBoxart_url() {
        return boxart_url;
    }
    public String getScalemates_url() {
        return scalemates_url;
    }
    public String getBoxart_uri() {
        return boxart_uri;
    }
    public String getYear() {
        return year;
    }

    public String getOnlineId() {
        return onlineId;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setBrandCatno(String brandCatno) {
        this.brandCatno = brandCatno;
    }

    public void setKit_name(String kit_name) {
        this.kit_name = kit_name;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setKit_noeng_name(String kit_noeng_name) {
        this.kit_noeng_name = kit_noeng_name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrototype(String prototype) {
        this.prototype = prototype;
    }

    public void setBoxart_url(String boxart_url) {
        this.boxart_url = boxart_url;
    }

    public void setScalemates_url(String scalemates_url) {
        this.scalemates_url = scalemates_url;
    }

    public void setBoxart_uri(String boxart_uri) {
        this.boxart_uri = boxart_uri;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setOnlineId(String onlineId) {
        this.onlineId = onlineId;
    }

    public void setDatePurchased(String datePurchased) {
        this.datePurchased = datePurchased;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getDatePurchased() {
        return datePurchased;
    }


    public String saveToOnlineStash(final Context context) {
        final String[] parseId = new String[1];
        final ParseObject kitTowrite = new ParseObject(MyConstants.PARSE_C_STASH);
        kitTowrite.put(MyConstants.PARSE_BARCODE, this.getBarcode());
        kitTowrite.put(MyConstants.PARSE_BRAND, this.getBrand());
        kitTowrite.put(MyConstants.PARSE_BRAND_CATNO, this.getBrandCatno());
        kitTowrite.put(MyConstants.PARSE_SCALE, this.getScale());
        kitTowrite.put(MyConstants.PARSE_KITNAME, this.getKit_name());
        kitTowrite.put(MyConstants.PARSE_NOENGNAME, this.getKit_noeng_name());
        kitTowrite.put(MyConstants.CATEGORY, this.getCategory());
        if (!TextUtils.isEmpty(this.getBoxart_url())) {
            kitTowrite.put(MyConstants.BOXART_URL, this.getBoxart_url());
        }
        kitTowrite.put(MyConstants.PARSE_DESCRIPTION, this.getDescription());
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        kitTowrite.put(MyConstants.PARSE_OWNERID, sharedPreferences.getString(MyConstants.USER_ID_FACEBOOK, MyConstants.EMPTY));
        kitTowrite.put(MyConstants.YEAR, this.getYear());
        kitTowrite.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                parseId[0] = kitTowrite.getObjectId();
            }
        });
        return parseId[0];
    }

    public void saveToNewKit(String parseId) {
        ParseObject kitTowrite = new ParseObject(MyConstants.PARSE_C_NEWKIT);
        kitTowrite.put(MyConstants.PARSE_BARCODE, this.getBarcode());
        kitTowrite.put(MyConstants.BRAND, this.getBrand());
        kitTowrite.put(MyConstants.PARSE_BRAND_CATNO, this.getBrandCatno());
        kitTowrite.put(MyConstants.SCALE, this.getScale());
        kitTowrite.put(MyConstants.PARSE_KITNAME, this.getKit_name());
        kitTowrite.put(MyConstants.PARSE_NOENGNAME, this.getKit_noeng_name());
        kitTowrite.put(MyConstants.CATEGORY, this.getCategory());
        if (!TextUtils.isEmpty(this.getBoxart_url())) {
            kitTowrite.put(MyConstants.BOXART_URL, Helper.trimUrl(this.getBoxart_url())); //Убираем обозначение размера картинки
        }
        kitTowrite.put(MyConstants.DESCRIPTION, this.getDescription());
        kitTowrite.put(MyConstants.PARSE_OWNERID, parseId);
        kitTowrite.put(MyConstants.YEAR, this.getYear());
        kitTowrite.saveInBackground();
    }

    public boolean saveToLocalDatabase(Context context, char workMode, Object itemSave,
                                       String listname, Long incomeKitId) {
        DbConnector dbConnector = new DbConnector(context);
        dbConnector.open();
        if (workMode == MyConstants.MODE_KIT) {
            dbConnector.addKitRec((Kit) itemSave);
        } else if (workMode == MyConstants.MODE_LIST) {
            dbConnector.addListItem((Kit) itemSave, listname);
        } else if (workMode == MyConstants.MODE_AFTERMARKET) {
            dbConnector.addAftermarket((Aftermarket) itemSave);
        } else if (workMode == MyConstants.MODE_AFTER_KIT) {
            long aftId = dbConnector.addAftermarket((Aftermarket) itemSave);
            dbConnector.addAfterToKit(incomeKitId, aftId);
        }
        dbConnector.close();
        return false;
    }





    private Kit(KitBuilder kitBuilder){
        this.brand = kitBuilder.brand;
        this.brandCatno = kitBuilder.brand_catno;
        this.kit_name = kitBuilder.kit_name;
        this.scale = kitBuilder.scale;
        this.category = kitBuilder.category;

        this.barcode = kitBuilder.barcode;
        this.kit_noeng_name = kitBuilder.kit_noeng_name;
        this.description = kitBuilder.description;
        this.prototype = kitBuilder.prototype;
        this.boxart_uri = kitBuilder.boxart_uri;
        this.boxart_url = kitBuilder.boxart_url;
        this.scalemates_url = kitBuilder.scalemates_url;
        this.year = kitBuilder.year;
        this.onlineId = kitBuilder.onlineId;


        this.date_added = kitBuilder.date_added;
        this.datePurchased = kitBuilder.date_purchased;
        this.quantity = kitBuilder.quantity;
        this.notes = kitBuilder.notes;
        this.price = kitBuilder.price;
        this.currency = kitBuilder.currency;
        this.sendStatus = kitBuilder.sendStatus;
        this.placePurchased = kitBuilder.placePurchased;
        this.status = kitBuilder.status;
        this.media = kitBuilder.media;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getPlacePurchased() {
        return placePurchased;
    }

    public void setPlacePurchased(String placePurchased) {
        this.placePurchased = placePurchased;
    }


    public static class KitBuilder{
        //Required
        private String brand;
        private String brand_catno;
        private String kit_name;
        private int scale;
        private String category;
        //Optional
        private String barcode;
        private String kit_noeng_name;
        private String description;
        private String prototype;
        private String boxart_url;
        private String scalemates_url;
        private String boxart_uri;
        private String year;
        private String onlineId;

        private String date_added;
        private String date_purchased;
        private int quantity;
        private String notes;
        private int price;
        private String currency;
        private String sendStatus;
        private String placePurchased;
        private int status;
        private int media;

        public KitBuilder(){
//            this.barcode = "";
//            this.brand = brand;
//            this.brandCatno = brandCatno;
//            this.kit_name = kit_name;
//            this.scale = scale;
//            this.category = category;
        }
        public KitBuilder hasBrand(String brand){
            this.brand = brand;
            return this;
        }

        public KitBuilder hasBrand_catno(String brand_catno){
            this.brand_catno = brand_catno;
            return this;
        }

        public KitBuilder hasKit_name(String kit_name){
            this.kit_name = kit_name;
            return this;
        }

        public KitBuilder hasScale(int scale){
            this.scale = scale;
            return this;
        }

        public KitBuilder hasCategory(String category){
            this.category = category;
            return this;
        }

        public KitBuilder hasBarcode(String barcode){
            this.barcode = barcode;
            return this;
        }
        public KitBuilder hasKit_noeng_name(String kit_noeng_name) {
            this.kit_noeng_name = kit_noeng_name;
            return this;
        }
        public KitBuilder hasDescription(String description) {
            this.description = description;
            return this;
        }
        public KitBuilder hasPrototype(String prototype) {
            this.prototype = prototype;
            return this;
        }
        public KitBuilder hasBoxart_url(String boxart_url) {
            this.boxart_url = boxart_url;
            return this;
        }
        public KitBuilder hasScalemates_url(String scalemates_url) {
            this.scalemates_url = scalemates_url;
            return this;
        }
        public KitBuilder hasBoxart_uri(String boxart_uri) {
            this.boxart_uri = boxart_uri;
            return this;
        }
        public KitBuilder hasYear(String year){
            this.year = year;
            return this;
        }

        public KitBuilder hasOnlineId(String onlineId){
            this.onlineId = onlineId;
            return this;
        }


        public KitBuilder hasDateAdded(String date_added){
            this.date_added = date_added;
            return this;
        }
        public KitBuilder hasDatePurchased(String date_purchased){
            this.date_purchased = date_purchased;
            return this;
        }
        public KitBuilder hasQuantity(int quantity){
            this.quantity = quantity;
            return this;
        }
        public KitBuilder hasNotes(String notes){
            this.notes = notes;
            return this;
        }
        public KitBuilder hasPrice(int price){
            this.price = price;
            return this;
        }
        public KitBuilder hasCurrency(String unit){
            this.currency = unit;
            return this;
        }

        public KitBuilder hasSendStatus(String sendStatus){
            this.sendStatus = sendStatus;
            return this;
        }

        public KitBuilder hasStatus(int status){
            this.status = status;
            return this;
        }

        public KitBuilder hasMedia(int media){
            this.media = media;
            return this;
        }

        public KitBuilder hasPlacePurchased(String placePurchased){
            this.placePurchased = placePurchased;
            return this;
        }

        public Kit build(){
            return new Kit(this);
        }
    }
}
