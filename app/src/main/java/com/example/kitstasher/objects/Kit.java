package com.example.kitstasher.objects;

import java.math.BigDecimal;

/**
 * Created by Алексей on 04.05.2017.
 */

public class Kit {

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
    private String status;




    public  String getBrand() {
        return brand;
    }
    public String getBrand_catno() {
        return brand_catno;
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

    public void setBrand_catno(String brand_catno) {
        this.brand_catno = brand_catno;
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

    public void setDate_purchased(String date_purchased) {
        this.date_purchased = date_purchased;
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

    public String getDate_purchased() {
        return date_purchased;
    }


    private Kit(KitBuilder kitBuilder){
        this.brand = kitBuilder.brand;
        this.brand_catno = kitBuilder.brand_catno;
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
        this.date_purchased = kitBuilder.date_purchased;
        this.quantity = kitBuilder.quantity;
        this.notes = kitBuilder.notes;
        this.price = kitBuilder.price;
        this.currency = kitBuilder.currency;
        this.status = kitBuilder.status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        private String status;

        public KitBuilder(){
            this.barcode = ""; //todo ??
//            this.brand = brand;
//            this.brand_catno = brand_catno;
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

        public KitBuilder hasStatus(String status){
            this.status = status;
            return this;
        }

        public Kit build(){
            return new Kit(this);
        }
    }
}