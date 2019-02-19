package com.kutovenko.kitstasher.model;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.kutovenko.kitstasher.db.DbConnector;
import com.kutovenko.kitstasher.util.Helper;
import com.kutovenko.kitstasher.util.MyConstants;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

/**
 * Created by Алексей on 04.05.2017.
 */

public class StashItem implements Parcelable
{

    //Required

    private String brand;
    private String brandCatno;
    private String name;
    private String category;
    private int scale;

    //Optional
    private String barcode;
    private String noengName;
    private String description;
    private String prototype;
    private String boxartUrl;
    private String scalematesUrl;
    private String boxartUri;
    private String year;
    private String onlineId;
    private String date_added;
    private String datePurchased;
    private String notes;
    private String currency;
    private String sendStatus;
    private String placePurchased;
    private String itemType;
    private String listname;
    private String media;
    private int status;
    private int quantity;
    private int price;
    private long localId;
    private long parentId;

    public String getItemType() {
        return itemType;
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

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
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
    public String getName() {
        return name;
    }
    public int getScale() {
        return scale;
    }
    public  String getCategory() {
        return category;
    }

    public String getBarcode() {
        return barcode;
    }
    public String getNoengName() {
        return noengName;
    }
    public String getDescription() {
        return description;
    }
    public String getPrototype() {
        return prototype;
    }
    public String getBoxartUrl() {
        return boxartUrl;
    }
    public String getScalematesUrl() {
        return scalematesUrl;
    }
    public String getBoxartUri() {
        return boxartUri;
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

    public void setName(String name) {
        this.name = name;
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

    public void setNoengName(String noengName) {
        this.noengName = noengName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrototype(String prototype) {
        this.prototype = prototype;
    }

    public void setBoxartUrl(String boxartUrl) {
        this.boxartUrl = boxartUrl;
    }

    public void setScalematesUrl(String scalematesUrl) {
        this.scalematesUrl = scalematesUrl;
    }

    public void setBoxartUri(String boxartUri) {
        this.boxartUri = boxartUri;
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

    private StashItem(StashItemBuilder stashItemBuilder){
        this.brand = stashItemBuilder.brand;
        this.brandCatno = stashItemBuilder.brand_catno;
        this.name = stashItemBuilder.kit_name;
        this.scale = stashItemBuilder.scale;
        this.category = stashItemBuilder.category;
        this.barcode = stashItemBuilder.barcode;
        this.noengName = stashItemBuilder.kit_noeng_name;
        this.description = stashItemBuilder.description;
        this.prototype = stashItemBuilder.prototype;
        this.boxartUri = stashItemBuilder.boxart_uri;
        this.boxartUrl = stashItemBuilder.boxart_url;
        this.scalematesUrl = stashItemBuilder.scalemates_url;
        this.year = stashItemBuilder.year;
        this.onlineId = stashItemBuilder.onlineId;
        this.date_added = stashItemBuilder.date_added;
        this.datePurchased = stashItemBuilder.date_purchased;
        this.quantity = stashItemBuilder.quantity;
        this.notes = stashItemBuilder.notes;
        this.price = stashItemBuilder.price;
        this.currency = stashItemBuilder.currency;
        this.sendStatus = stashItemBuilder.sendStatus;
        this.placePurchased = stashItemBuilder.placePurchased;
        this.status = stashItemBuilder.status;
        this.media = stashItemBuilder.media;
        this.itemType = stashItemBuilder.itemType;
        this.localId = stashItemBuilder.localId;
        this.parentId = stashItemBuilder.parentId;
        this.listname = stashItemBuilder.listname;
    }

    /////BASIC METHODS/////

    public void saveToStashWhenOffline(DbConnector dbConnector){
        saveToLocalDb(dbConnector);
    }

    public void saveToStashWhenOnline(DbConnector dbConnector, String imagePath,
                                      String imageFileName, String ownerId, final boolean isKitNew){
        this.setLocalId(saveToLocalDb(dbConnector));
        saveWithBoxartAndThumbnail(dbConnector, imagePath, imageFileName, ownerId, isKitNew);
    }

    private void saveWithBoxartAndThumbnail(final DbConnector dbConnector, String imagePath,
                                            String imageFileName, final String ownerId, final boolean isKitNew){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        getResizedBitmap(bmp, MyConstants.SIZE_FULL_HEIGHT, MyConstants.SIZE_FULL_WIDTH)
                .compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] data = stream.toByteArray();
        String fullName = imageFileName + MyConstants.SIZE_FULL + MyConstants.JPG;
        final ParseFile imageFile = new ParseFile(fullName, data);
        final ParseObject boxartToSave = new ParseObject(MyConstants.PARSE_C_BOXART);
        boxartToSave.put(MyConstants.PARSE_IMAGE, imageFile);
        boxartToSave.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException ex) {
                if (ex == null) {
                    String preparedUrl = prepareUrlFromParseBoxart(imageFile.getUrl());
                    saveStashItemToParse(dbConnector, preparedUrl, ownerId);
                    StashItem.this.setOnlineId(preparedUrl);
                    ContentValues cv = new ContentValues(1);
                    cv.put(DbConnector.COLUMN_ID_ONLINE, preparedUrl);
                    StashItem.this.editStashItem(dbConnector, cv);
                    StashItem.this.setBoxartUrl(preparedUrl);
                    if(isKitNew){
                        StashItem.this.saveToNewKit(ownerId);
                    }
                }
            }
        });
    }

    private void saveStashItemToParse(final DbConnector dbConnector, final String boxartUrl,
                                      final String ownerId) {
        final String[] parseId = new String[1];

        final ParseObject kitTowrite = new ParseObject(MyConstants.PARSE_C_STASH);
        String media = this.getMedia();
        if (media != null) {
            kitTowrite.put(MyConstants.PARSE_MEDIA, media);
        }
        if (ownerId != null) {
            kitTowrite.put(MyConstants.PARSE_OWNERID, ownerId);
        }
        String itemType = this.getItemType();
        if (itemType != null){
            kitTowrite.put(MyConstants.PARSE_ITEMTYPE, itemType);
        }
        long localId = this.getLocalId();
        kitTowrite.put(MyConstants.PARSE_LOCALID, localId);
        String brand = this.getBrand();
        if (brand != null) {
            kitTowrite.put(MyConstants.PARSE_BRAND, brand);
        }
        String catno = this.getBrandCatno();
        if (catno != null){
            kitTowrite.put(MyConstants.PARSE_BRAND_CATNO, catno);
        }
        String name = this.getName();
        if (name != null) {
            kitTowrite.put(MyConstants.PARSE_KITNAME, name);
        }
        String category = this.getCategory();
        if (category != null) {
            kitTowrite.put(MyConstants.CATEGORY, category);
        }
        String barcode = this.getBarcode();
        if (barcode != null) {
            kitTowrite.put(MyConstants.PARSE_BARCODE, barcode);
        }
        int scale = this.getScale();
        kitTowrite.put(MyConstants.PARSE_SCALE, scale);
        String noengname = this.getNoengName();
        if(noengname != null){
            kitTowrite.put(MyConstants.PARSE_NOENGNAME, noengname);
        }
        String mates = this.getScalematesUrl();
        if (mates != null) {
            kitTowrite.put(MyConstants.PARSE_SCALEMATES, mates);
        }
        kitTowrite.put(MyConstants.BOXART_URL, boxartUrl);
        String desc = this.getDescription();
        if (desc != null) {
            kitTowrite.put(MyConstants.PARSE_DESCRIPTION, desc);
        }
        String year = this.getYear();
        if (year != null) {
            kitTowrite.put(MyConstants.YEAR, year);
        }

        kitTowrite.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    parseId[0] = kitTowrite.getObjectId();
                    StashItem.this.setOnlineId(parseId[0]);
                    ContentValues cv = new ContentValues(2);
                    cv.put(DbConnector.COLUMN_ID_ONLINE, parseId[0]);
                    cv.put(DbConnector.COLUMN_BOXART_URL, boxartUrl);
                    StashItem.this.editStashItem(dbConnector, cv);
                } else {
                    String a = e.toString();
                    String b = " ";
                }
            }
        });
    }

    private void saveToNewKit(String ownerId) {
        ParseObject kitTowrite = new ParseObject(MyConstants.PARSE_C_NEWKIT);
        kitTowrite.put(MyConstants.PARSE_BARCODE, this.getBarcode());
        kitTowrite.put(MyConstants.BRAND, this.getBrand());
        kitTowrite.put(MyConstants.PARSE_BRAND_CATNO, this.getBrandCatno());
        kitTowrite.put(MyConstants.SCALE, this.getScale());
        kitTowrite.put(MyConstants.PARSE_KITNAME, this.getName());
        kitTowrite.put(MyConstants.PARSE_NOENGNAME, this.getNoengName());
        kitTowrite.put(MyConstants.CATEGORY, this.getCategory());
        if (!TextUtils.isEmpty(this.getBoxartUrl())) {
            kitTowrite.put(MyConstants.BOXART_URL, Helper.trimUrl(this.getBoxartUrl())); //Убираем обозначение размера картинки
        }
        kitTowrite.put(MyConstants.DESCRIPTION, this.getDescription());
        kitTowrite.put(MyConstants.PARSE_OWNERID, ownerId);
        kitTowrite.put(MyConstants.YEAR, this.getYear());

        kitTowrite.put(MyConstants.PARSE_ITEMTYPE, this.getItemType());
        kitTowrite.put(MyConstants.PARSE_MEDIA, this.getMedia());

        kitTowrite.saveInBackground();

    }

    ////HELPERS//////

    private long saveToLocalDb(DbConnector dbConnector){
        return dbConnector.addItem(this);
    }

    public boolean editStashItem(DbConnector dbConnector, ContentValues cv){
        return dbConnector.editItemById(this.getLocalId(), cv);
    }

    private String prepareUrlFromParseBoxart(String url) {
        return url.substring(0, url.length() - 12);
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

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<StashItem> CREATOR = new Parcelable.Creator<StashItem>() {
        public StashItem createFromParcel(Parcel in) {
            return new StashItem(in);
        }

        public StashItem[] newArray(int size) {
            return new StashItem[size];
        }
    };

    private StashItem(Parcel parcel) {
        brand = parcel.readString();
        brandCatno = parcel.readString();
        name = parcel.readString();
        scale = parcel.readInt();
        category = parcel.readString();
        //Optional
        barcode = parcel.readString();
        noengName = parcel.readString();
        description = parcel.readString();
        prototype = parcel.readString();
        boxartUrl = parcel.readString();
        scalematesUrl = parcel.readString();
        boxartUri = parcel.readString();
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
        media = parcel.readString();
        status = parcel.readInt();
        itemType = parcel.readString();
        localId = parcel.readLong();
        parentId = parcel.readLong();
        listname = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.brand);
        parcel.writeString(this.brandCatno);
        parcel.writeString(this.name);
        parcel.writeInt(this.scale);
        parcel.writeString(this.category);
        //Optional
        parcel.writeString(barcode);
        parcel.writeString(noengName);
        parcel.writeString(description);
        parcel.writeString(prototype);
        parcel.writeString(boxartUrl);
        parcel.writeString(scalematesUrl);
        parcel.writeString(boxartUri);
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
        parcel.writeString(media);
        parcel.writeInt(status);
        parcel.writeString(itemType);
        parcel.writeLong(localId);
        parcel.writeLong(parentId);
        parcel.writeString(listname);
    }




    public static class StashItemBuilder {
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
        private String media;
        private String itemType;
        private long localId;
        private int parentId;
        private String listname;

        public StashItemBuilder(String itemType){
            this.itemType = itemType;
        }
        public StashItemBuilder hasBrand(String brand){
            this.brand = brand;
            return this;
        }

        public StashItemBuilder hasBrand_catno(String brand_catno){
            this.brand_catno = brand_catno;
            return this;
        }

        public StashItemBuilder hasKitName(String kit_name){
            this.kit_name = kit_name;
            return this;
        }

        public StashItemBuilder hasScale(int scale){
            this.scale = scale;
            return this;
        }

        public StashItemBuilder hasCategory(String category){
            this.category = category;
            return this;
        }

        public StashItemBuilder hasBarcode(String barcode){
            this.barcode = barcode;
            return this;
        }
        public StashItemBuilder hasKitNoengName(String kit_noeng_name) {
            this.kit_noeng_name = kit_noeng_name;
            return this;
        }
        public StashItemBuilder hasDescription(String description) {
            this.description = description;
            return this;
        }
        public StashItemBuilder hasPrototype(String prototype) {
            this.prototype = prototype;
            return this;
        }
        public StashItemBuilder hasBoxartUrl(String boxart_url) {
            this.boxart_url = boxart_url;
            return this;
        }
        public StashItemBuilder hasScalematesUrl(String scalemates_url) {
            this.scalemates_url = scalemates_url;
            return this;
        }
        public StashItemBuilder hasBoxartUri(String boxart_uri) {
            this.boxart_uri = boxart_uri;
            return this;
        }
        public StashItemBuilder hasYear(String year){
            this.year = year;
            return this;
        }

        public StashItemBuilder hasOnlineId(String onlineId){
            this.onlineId = onlineId;
            return this;
        }


        public StashItemBuilder hasDateAdded(String date_added){
            this.date_added = date_added;
            return this;
        }
        public StashItemBuilder hasDatePurchased(String date_purchased){
            this.date_purchased = date_purchased;
            return this;
        }
        public StashItemBuilder hasQuantity(int quantity){
            this.quantity = quantity;
            return this;
        }
        public StashItemBuilder hasNotes(String notes){
            this.notes = notes;
            return this;
        }
        public StashItemBuilder hasPrice(int price){
            this.price = price;
            return this;
        }
        public StashItemBuilder hasCurrency(String unit){
            this.currency = unit;
            return this;
        }

        public StashItemBuilder hasSendStatus(String sendStatus){
            this.sendStatus = sendStatus;
            return this;
        }

        public StashItemBuilder hasPlacePurchased(String placePurchased) {
            this.placePurchased = placePurchased;
            return this;
        }

        public StashItemBuilder hasStatus(int status){
            this.status = status;
            return this;
        }

        public StashItemBuilder hasMedia(String media){
            this.media = media;
            return this;
        }


        public StashItemBuilder hasLocalId(long localId) {
            this.localId = localId;
            return this;
        }

        public StashItemBuilder hasParentId(int parentId) {
            this.parentId = parentId;
            return this;
        }

        public StashItemBuilder hasListname(String listname) {
            this.listname = listname;
            return this;
        }

        public StashItem build(){
            return new StashItem(this);
        }
    }
}
