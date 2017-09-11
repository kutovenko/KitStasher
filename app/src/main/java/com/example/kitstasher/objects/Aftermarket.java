package com.example.kitstasher.objects;

/**
 * Created by Алексей on 04.09.2017.
 * Aftermarket items. Uses builder pattern.
 */

public class Aftermarket {
    //Required

    private String brand;
    private String brandCatno;
    private String aftermarketName;
    private int scale;
    private String category;
    //Optional
    private String barcode;
    private String aftemarketOriginalName;
    private String description;
    private String compilanceWith;
    private String boxartUrl;
    private String scalematesUrl;
    private String boxartUri;
    private String year;
    private String onlineId;
    private String dateAdded;
    private String datePurchased;
    private int quantity;
    private String notes;
    private int price;
    private String currency;
    private String sendStatus;
    private String placePurchased;

    private int status;
    private int media;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMedia() {
        return media;
    }

    public void setMedia(int media) {
        this.media = media;
    }

    public String getListname() {
        return listname;
    }

    public void setListname(String listname) {
        this.listname = listname;
    }

    private String listname;

    private Aftermarket(AftermarketBuilder aftermarketBuilder){
        this.brand = aftermarketBuilder.brand;
        this.brandCatno = aftermarketBuilder.brandCatno;
        this.aftermarketName = aftermarketBuilder.aftermarketName;
        this.scale = aftermarketBuilder.scale;
        this.category = aftermarketBuilder.category;
        //Optional
        this.barcode = aftermarketBuilder.barcode;
        this.aftemarketOriginalName = aftermarketBuilder.aftermarketOriginalName;
        this.description = aftermarketBuilder.description;
        this.compilanceWith = aftermarketBuilder.compilanceWith;
        this.boxartUrl = aftermarketBuilder.boxartUrl;
        this.scalematesUrl = aftermarketBuilder.scalematesUrl;
        this.boxartUri = aftermarketBuilder.boxartUri;
        this.year = aftermarketBuilder.year;
        this.onlineId = aftermarketBuilder.onlineId;
        this.dateAdded = aftermarketBuilder.dateAdded;
        this.datePurchased = aftermarketBuilder.datePurchased;
        this.quantity = aftermarketBuilder.quantity;
        this.notes = aftermarketBuilder.notes;
        this.price = aftermarketBuilder.price;
        this.currency = aftermarketBuilder.currency;
        this.sendStatus = aftermarketBuilder.sendStatus;
        this.placePurchased = aftermarketBuilder.placePurchased;
        this.listname = aftermarketBuilder.listname;
        this.status = aftermarketBuilder.status;
        this.media = aftermarketBuilder.media;
    }

    public String getMylistName() {
        return mylistName;
    }

    public void setMylistName(String mylistName) {
        this.mylistName = mylistName;
    }

    private String mylistName;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getBrandCatno() {
        return brandCatno;
    }

    public void setBrandCatno(String brandCatno) {
        this.brandCatno = brandCatno;
    }

    public String getAftermarketName() {
        return aftermarketName;
    }

    public void setAftermarketName(String aftermarketName) {
        this.aftermarketName = aftermarketName;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getAftemarketOriginalName() {
        return aftemarketOriginalName;
    }

    public void setAftemarketOriginalName(String aftemarketOriginalName) {
        this.aftemarketOriginalName = aftemarketOriginalName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompilanceWith() {
        return compilanceWith;
    }

    public void setCompilanceWith(String compilanceWith) {
        this.compilanceWith = compilanceWith;
    }

    public String getBoxartUrl() {
        return boxartUrl;
    }

    public void setBoxartUrl(String boxartUrl) {
        this.boxartUrl = boxartUrl;
    }

    public String getScalematesUrl() {
        return scalematesUrl;
    }

    public void setScalematesUrl(String scalematesUrl) {
        this.scalematesUrl = scalematesUrl;
    }

    public String getBoxartUri() {
        return boxartUri;
    }

    public void setBoxartUri(String boxartUri) {
        this.boxartUri = boxartUri;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getOnlineId() {
        return onlineId;
    }

    public void setOnlineId(String onlineId) {
        this.onlineId = onlineId;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getDatePurchased() {
        return datePurchased;
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



    public static class AftermarketBuilder {
        private String brand;
        private String brandCatno;
        private String aftermarketName;
        private int scale;
        private String category;
        private String barcode;
        private String aftermarketOriginalName;
        private String description;
        private String compilanceWith;
        private String boxartUrl;
        private String scalematesUrl;
        private String boxartUri;
        private String year;
        private String onlineId;
        private String dateAdded;
        private String datePurchased;
        private int quantity;
        private String notes;
        private int price;
        private String currency;
        private String sendStatus;
        private String placePurchased;
        private String listname;
        private int status;
        private int media;


        public AftermarketBuilder() {
            this.barcode = "";
        }


        public AftermarketBuilder hasBoxartUri(String boxartUri) {
            this.boxartUri = boxartUri;
            return this;
        }

        public AftermarketBuilder hasScalematesUrl(String scalematesUrl) {
            this.scalematesUrl = scalematesUrl;
            return this;
        }

        public AftermarketBuilder hasBrand(String brand) {
            this.brand = brand;
            return this;
        }

        public AftermarketBuilder hasBrandCatno(String brandCatno) {
            this.brandCatno = brandCatno;
            return this;
        }

        public AftermarketBuilder hasAftermarketName(String aftemarketName) {
            this.aftermarketName = aftemarketName;
            return this;
        }

        public AftermarketBuilder hasScale(int scale) {
            this.scale = scale;
            return this;
        }

        public AftermarketBuilder hasCategory(String category) {
            this.category = category;
            return this;
        }

        public AftermarketBuilder hasBarcode(String barcode) {
            this.barcode = barcode;
            return this;
        }

        public AftermarketBuilder hasAftermarketOriginalName(String aftemarketOriginalName) {
            this.aftermarketOriginalName = aftemarketOriginalName;
            return this;
        }

        public AftermarketBuilder hasDescription(String description) {
            this.description = description;
            return this;
        }

        public AftermarketBuilder hasCompilance(String compilanceWith) {
            this.compilanceWith = compilanceWith;
            return this;
        }

        public AftermarketBuilder hasBoxartUrl(String boxartUrl) {
            this.boxartUrl = boxartUrl;
            return this;
        }

        public AftermarketBuilder hasYear(String year) {
            this.year = year;
            return this;
        }

        public AftermarketBuilder hasOnlineId(String onlineId) {
            this.onlineId = onlineId;
            return this;
        }

        public AftermarketBuilder hasDateAdded(String dateAdded) {
            this.dateAdded = brand;
            return this;
        }

        public AftermarketBuilder hasDatePurchased(String datePurchased) {
            this.datePurchased = datePurchased;
            return this;
        }

        public AftermarketBuilder hasQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public AftermarketBuilder hasNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public AftermarketBuilder hasPrice(int price) {
            this.price = price;
            return this;
        }

        public AftermarketBuilder hasCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public AftermarketBuilder hasSendStatus(String sendStatus) {
            this.sendStatus = sendStatus;
            return this;
        }

        public AftermarketBuilder hasPlacePurchased(String placePurchased) {
            this.placePurchased = placePurchased;
            return this;
        }

        public AftermarketBuilder hasListname(String listname) {
            this.listname = listname;
            return this;
        }

        public AftermarketBuilder hasStatus(int status){
            this.status = status;
            return this;
        }

        public AftermarketBuilder hasMedia(int media){
            this.media = media;
            return this;
        }

        public Aftermarket build(){
            return new Aftermarket(this);
        }

    }
}
