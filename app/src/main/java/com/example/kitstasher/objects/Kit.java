package com.example.kitstasher.objects;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import static com.example.kitstasher.other.DbConnector.COLUMN_BOXART_URI;
import static com.example.kitstasher.other.DbConnector.COLUMN_BOXART_URL;
import static com.example.kitstasher.other.DbConnector.COLUMN_BRAND;
import static com.example.kitstasher.other.DbConnector.COLUMN_BRAND_CATNO;
import static com.example.kitstasher.other.DbConnector.COLUMN_ITEMTYPE;
import static com.example.kitstasher.other.DbConnector.COLUMN_KIT_NAME;

/**
 * Created by Алексей on 04.05.2017.
 */

public class Kit implements Parcelable
{

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

    private String itemType; //1 - kit, 2 - aftermarket
    private long localId;
    private long parentId;

    private String listname;


    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }



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

    public String getListname() {
        return listname;
    }

    public void setListname(String listname) {
        this.listname = listname;
    }

    public boolean saveToLocalDb(Context context){
        DbConnector dbConnector = new DbConnector(context);
        dbConnector.open();
        long id = dbConnector.addItem(this, DbConnector.TABLE_KITS);
        dbConnector.close();
        return id != -1;
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
        kitTowrite.put(MyConstants.PARSE_SCALEMATES, this.getScalemates_url());
        if (!TextUtils.isEmpty(this.getBoxart_url())) {
            kitTowrite.put(MyConstants.BOXART_URL, this.getBoxart_url());
        }
        kitTowrite.put(MyConstants.PARSE_DESCRIPTION, this.getDescription());
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyConstants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        kitTowrite.put(MyConstants.PARSE_OWNERID, sharedPreferences.getString(MyConstants.USER_ID_PARSE, MyConstants.EMPTY));
        kitTowrite.put(MyConstants.YEAR, this.getYear());

        kitTowrite.put(MyConstants.PARSE_LOCALID, this.getLocalId());
//        kitTowrite.put(MyConstants.PARSE_PARENTID, this.getParentId());
        kitTowrite.put(MyConstants.PARSE_ITEMTYPE, this.getItemType());
        kitTowrite.put(MyConstants.PARSE_MEDIA, this.getMedia());


        kitTowrite.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                parseId[0] = kitTowrite.getObjectId();

            }
        });
        return parseId[0];
    }

    public void saveToNewKit(String ownerId) {
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
        kitTowrite.put(MyConstants.PARSE_OWNERID, ownerId);
        kitTowrite.put(MyConstants.YEAR, this.getYear());

        kitTowrite.put(MyConstants.PARSE_ITEMTYPE, this.getItemType());
        kitTowrite.put(MyConstants.PARSE_MEDIA, this.getMedia());

        kitTowrite.saveInBackground();
    }

//    public boolean saveToLocalDatabase(Context context, char workMode, Object itemSave,
//                                       String listname, Long incomeKitId) {
//        DbConnector dbConnector = new DbConnector(context);
//        dbConnector.open();
//        if (workMode == MyConstants.MODE_KIT) {
//            dbConnector.addItem((Kit) itemSave, DbConnector.TABLE_KITS);
////        } else if (workMode == MyConstants.MODE_LIST) {
////            dbConnector.addListItem((Kit) itemSave, listname);
//        } else if (workMode == MyConstants.MODE_AFTERMARKET) {
//            dbConnector.addItem((Kit) itemSave, DbConnector.TABLE_AFTERMARKET);
//        }
////        else if (workMode == MyConstants.MODE_AFTER_KIT) {
////            long aftId = dbConnector.addAftermarket((Aftermarket) itemSave);
////            dbConnector.addAfterToKit(incomeKitId, aftId);
////        }
//        dbConnector.close();
//        return false;
//    }
//
//    public boolean makeLocalBackup() {
//        return false;
//    }
//
//    public boolean addToKit(Kit kit, Kit aftermarket) {
//        return false;
//    }



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

        this.itemType = kitBuilder.itemType;
        this.localId = kitBuilder.localId;
        this.parentId = kitBuilder.parentId;

        this.listname = kitBuilder.listname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(brand);
        parcel.writeString(brandCatno);
        parcel.writeString(kit_name);
        parcel.writeInt(scale);
        parcel.writeString(category);
        //Optional
        parcel.writeString(barcode);
        parcel.writeString(kit_noeng_name);
        parcel.writeString(description);
        parcel.writeString(prototype);
        parcel.writeString(boxart_url);
        parcel.writeString(scalemates_url);
        parcel.writeString(boxart_uri);

        parcel.writeString(year);
        parcel.writeString(onlineId);

        parcel.writeString(date_added);


        parcel.writeString(datePurchased);
        parcel.writeInt(quantity);
        parcel.writeString(notes);
        parcel.writeInt(price);
        parcel.writeString(currency);
        parcel.writeString(sendStatus);
        parcel.writeString(placePurchased);

        parcel.writeInt(media);
        parcel.writeInt(status);

        parcel.writeString(itemType); //1 - kit, 2 - aftermarket, 3 - paint
        parcel.writeLong(localId);
        parcel.writeLong(parentId);

        parcel.writeString(listname);
    }

    public static final Parcelable.Creator<Kit> CREATOR = new Parcelable.Creator<Kit>() {
        public Kit createFromParcel(Parcel in) {
            return new Kit(in);
        }

        public Kit[] newArray(int size) {
            return new Kit[size];
        }
    };

    private Kit(Parcel parcel) {
        brand = parcel.readString();
        brandCatno = parcel.readString();
        kit_name = parcel.readString();
        scale = parcel.readInt();
        category = parcel.readString();
        //Optional
        barcode = parcel.readString();
        kit_noeng_name = parcel.readString();
        description = parcel.readString();
        prototype = parcel.readString();
        boxart_url = parcel.readString();
        scalemates_url = parcel.readString();
        boxart_uri = parcel.readString();

        year = parcel.readString();
        onlineId = parcel.readString();

        date_added = parcel.readString();


        datePurchased = parcel.readString();
        quantity = parcel.readInt();
        notes = parcel.readString();
        price = parcel.readInt();
        currency = parcel.readString();
        sendStatus = parcel.readString();
        placePurchased = parcel.readString();

        media = parcel.readInt();
        status = parcel.readInt();

        itemType = parcel.readString(); //1 - kit, 2 - aftermarket, 3 - paint
        localId = parcel.readLong();
        parentId = parcel.readLong();
        listname = parcel.readString();
    }

//    @Override
//    public int compareTo(@NonNull Kit kit) {
//        return 0;
//    }


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

        private String itemType; //kit or aftermarket
        private long localId; //id in local db
        private int parentId; // id of parent kit for aftermarket

        private String listname;

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

        public KitBuilder hasKitName(String kit_name){
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
        public KitBuilder hasKitNoengName(String kit_noeng_name) {
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
        public KitBuilder hasBoxartUrl(String boxart_url) {
            this.boxart_url = boxart_url;
            return this;
        }
        public KitBuilder hasScalematesUrl(String scalemates_url) {
            this.scalemates_url = scalemates_url;
            return this;
        }
        public KitBuilder hasBoxartUri(String boxart_uri) {
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

        public KitBuilder hasPlacePurchased(String placePurchased) {
            this.placePurchased = placePurchased;
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


        public KitBuilder hasItemType(String itemType) {
            this.itemType = itemType;
            return this;
        }

        public KitBuilder hasLocalId(long localId) {
            this.localId = localId;
            return this;
        }

        public KitBuilder hasParentId(int parentId) {
            this.parentId = parentId;
            return this;
        }

        public KitBuilder hasListname(String listname) {
            this.listname = listname;
            return this;
        }

        public Kit build(){
            return new Kit(this);
        }
    }
}
