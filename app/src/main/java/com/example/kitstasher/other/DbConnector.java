package com.example.kitstasher.other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kitstasher.R;
import com.example.kitstasher.objects.Aftermarket;
import com.example.kitstasher.objects.ChooserItem;
import com.example.kitstasher.objects.Kit;

import java.util.ArrayList;

public class DbConnector {

    private static final String DB_NAME = "myscalestash";
    private static final int DB_VERSION = 1;

    private final Context context;
    private static DBHelper mDBHelper;
    private static SQLiteDatabase mDB;

    ///////// TABLE KITS /////////

    public static final String TABLE_KITS = "kits";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_BARCODE = "barcode";
    public static final String COLUMN_BARCODE_TYPE = "barcode_type";
    public static final String COLUMN_BRAND = "brand";
    public static final String COLUMN_BRAND_CATNO = "brand_catno";
    public static final String COLUMN_KIT_NAME = "kit_name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_SCALE = "scale";
    public static final String COLUMN_ORIGINAL_NAME = "original_name";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_COLLECTION = "collection";
    public static final String COLUMN_SEND_STATUS = "send_status";
    public static final String COLUMN_ID_ONLINE = "id_online";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_BOXART_URI = "boxart_uri";
    public static final String COLUMN_BOXART_URL = "boxart_url";
    public static final String COLUMN_IS_DELETED = "is_deleted";
    public static final String COLUMN_YEAR = "year";

    public static final String COLUMN_PURCHASE_DATE = "purchase_date";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_CURRENCY = "currency";
    public static final String COLUMN_PURCHASE_PLACE = "purchasePlace";

    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_MEDIA = "media";
    public static final String COLUMN_SCALEMATES_URL = "scalemates";

    ///////// TABLE BRANDS /////////

    public static final String TABLE_BRANDS = "brands";
    public static final String BRANDS_COLUMN_ID = "_id";
    public static final String BRANDS_COLUMN_BRAND = "brand";

    ///////// TABLE TAGS //////////
    private static final String TABLE_TAGS = "tags";
    private static final String TAGS_COLUMN_ID = "_id";
    private static final String TAGS_COLUMN_TAG = "tag";

    ///////// TABLE MYSHOPS ///////////

    public static final String TABLE_MYSHOPS = "myshops";
    public static final String MYSHOPS_COLUMN_SHOP_NAME = "shop_name";
    public static final String MYSHOPS_COLUMN_SHOP_DESCRIPTION = "shop_desc";
    public static final String MYSHOPS_COLUMN_SHOP_URL = "shop_url";
    public static final String MYSHOPS_COLUMN_SHOP_RATING = "shop_rating";
    public static final String MYSHOPS_COLUMN_SHOP_CONTACT = "shop_contact";

    ///////// TABLE STATISTIC /////////
    //Not in use now?

    public static final String TABLE_STATISTIC = "statistic";
    public static final String STATISTIC_COLUNN_ID = "_id";
    public static final String STATISTICS_STATNAME = "stat_name";
    public static final String STATISTICS_STATVALUE = "stat_value";

    ///////// TABLE MYLISTS /////////

    public static final String TABLE_MYLISTS = "mylists";
    public static final String MYLISTS_COLUMN_ID = "_id";
    public static final String MYLISTS_COLUMN_LIST_NAME = "listname";
    public static final String MYLISTS_COLUMN_DATE = "date";

    ///////// TABLE MYLISTS ITEMS /////////
    public static final String TABLE_MYLISTSITEMS = "mylists_items";
    public static final String MYLISTSITEMS_LISTNAME = "listname";


    ///////// TABLE CURRENCIES ///////
    public static final String TABLE_CURRENCIES = "currencies";
    public static final String CURRENCIES_COLUMN_CURRENCY = "currency";


    ///////// TABLE ACCOUNT /////////
    //Not in use now

    private static final String TABLE_ACCOUNT = "account";
    private static final String ACCOUNT_COLUMN_ID = "_id"; // Локальный ключ
    private static final String ACCOUNT_COLUMN_USER_NAME = "user_name";
    private static final String ACCOUNT_COLUMN_USER_EMAIL = "user_email";
    private static final String ACCOUNT_COLUMN_PROFILEPIC_URL = "profile_pic";
    private static final String ACCOUNT_COLUMN_FACEBOOK_PROFILE_URL = "fb_profile_url";
    private static final String ACCOUNT_COLUMN_NETWORK = "network";

    ////////// TABLE AFTEMARKET /////////
    public static final String TABLE_AFTERMARKET = "aftermarket";
    public static final String COLUMN_AFTERMARKET_NAME = "kit_name"; //after_name одинаково для фильтра
//    public static final String COLUMN_ORIGINAL_NAME = "after_orig_name";

    ///////// TABLE AFTERMARKET_MYLIST /////////
    public static final String TABLE_AFTERMARKET_MYLIST_ITEMS = "after_mylistitems";
    public static final String AFTERMARKET_MYLIST_NAME = "after_mylistname";

    //////// TABLE KIT_AFTER_CONNECTIONS /////////
    public static final String TABLE_KIT_AFTER_CONNECTIONS = "kit_after";
    public static final String KIT_AFTER_KITNAME = "kitname";
    public static final String KIT_AFTER_KITBRAND = "kitbrand";
    public static final String KIT_AFTER_KITCATNO = "kitcatno";
    public static final String KIT_AFTER_KITBARCODE = "kitbarcode";
    public static final String KIT_AFTER_KITPROTOTYPE = "kitprototype";
    public static final String KIT_AFTER_AFTERNAME = "aftername";
    public static final String KIT_AFTER_AFTERBRAND = "afterbrand";
    public static final String KIT_AFTER_AFTERCATNO = "aftercatno";
    public static final String KIT_AFTER_AFTERBARCODE = "afterbarcode";
    public static final String KIT_AFTER_AFTERDESIGNEDFOR = "afterdesfor";
    public static final String KIT_AFTER_KITID = "kitid";
    public static final String KIT_AFTER_AFTERID = "afterid";
    public static final String KIT_AFTER_LISTNAME = "after_listname";

    //////// TABLE BRAND_BARCODE ///////////////
    public static final String TABLE_BRAND_BARCODE = "brand_bc";
    public static final String BB_COLUMN_BRAND = "bb_brand";
    public static final String BB_COLUMN_BARCODE = "bb_barcode";
    public static final String BB_COLUMN_SOURCE = "bb_source";

    /////// TABLE CATEGORIES ///////////////
    private static final String TABLE_CATEGORIES = "categories";
    public static final String CAT_RESID = "res_id";
    public static final String CAT_COUNT = "count";
    public static final String CAT_NAME = "name";


    //////////////////////////////////
    ///////// CREATE SCRIPTS /////////
    /////////////////////////////////

    private static final String CREATE_TABLE_CATEGORIES =
            "create table " + TABLE_CATEGORIES + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " + // Локальный ключ -0
                    CAT_RESID + " integer," + //
                    CAT_NAME + " text, " + //
                    CAT_COUNT + " integer" + //
                    ");";

    private static final String CREATE_TABLE_BRAND_BARCODE =
            "create table " + TABLE_BRAND_BARCODE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " + // Локальный ключ -0
                    BB_COLUMN_BRAND + " text," + //
                    BB_COLUMN_BARCODE + " text," + //
                    BB_COLUMN_SOURCE + " text" + //место покупки - 22
                    ");";

    private static final String CREATE_TABLE_KIT_AFTER_CONNECTIONS =
            "create table " + TABLE_KIT_AFTER_CONNECTIONS + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " + // Локальный ключ -0
                    COLUMN_SCALE + " integer," + //
                    KIT_AFTER_KITID + " int," + //
                    KIT_AFTER_AFTERID + " int," + //
                    KIT_AFTER_LISTNAME + " text," + //
                    KIT_AFTER_KITNAME + " text," + //
                    KIT_AFTER_KITBRAND + " text," + //
                    KIT_AFTER_KITCATNO + " text," + //
                    KIT_AFTER_KITBARCODE + " text," + // штрихкод NOBARCODE по умолчанию для garage kit? - 1
                    KIT_AFTER_KITPROTOTYPE + " text," + // штрихкод NOBARCODE по умолчанию для garage kit? - 1
                    KIT_AFTER_AFTERNAME + " text," + // штрихкод NOBARCODE по умолчанию для garage kit? - 1
                    KIT_AFTER_AFTERBRAND + " text," + // штрихкод NOBARCODE по умолчанию для garage kit? - 1
                    KIT_AFTER_AFTERCATNO + " text," + // штрихкод NOBARCODE по умолчанию для garage kit? - 1
                    KIT_AFTER_AFTERBARCODE + " text," + // штрихкод NOBARCODE по умолчанию для garage kit? - 1
                    KIT_AFTER_AFTERDESIGNEDFOR + " text" + //место покупки - 22
                    ");";

    private static final String CREATE_TABLE_AFTERMARKET =
            "create table " + TABLE_AFTERMARKET + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " + // Локальный ключ -0
                    COLUMN_BARCODE + " text," + // штрихкод NOBARCODE по умолчанию для garage kit? - 1
                    COLUMN_BRAND + " text, " + // производитель - 2
                    COLUMN_BRAND_CATNO + " text," + //каталожный номер набора - 3
                    COLUMN_SCALE + " integer," + //масштаб - 4
                    COLUMN_AFTERMARKET_NAME + " text," + //название набора - 5
                    COLUMN_DESCRIPTION + " text," + //описание, продолжение названия - 6
                    COLUMN_ORIGINAL_NAME + " text," + //название на оригинальном языке, - 7
                    // если отличается
                    COLUMN_CATEGORY + " text," + //тег (самолет, корабль, и тд - 8
                    COLUMN_COLLECTION  + " text," + //коллекция - для группировки и других функций - 9
                    COLUMN_SEND_STATUS + " text," + //для отслеживания офлайн отправок LOCAL - 10
                    COLUMN_ID_ONLINE + " text," + //номер в онлайновой базе, может пригодится - 11
                    //заметки? LOCAL?
                    COLUMN_BOXART_URI + " text," + //локальная ссылка на файл боксарта LOCAL - 12
                    COLUMN_BOXART_URL + " text," + //интернет-ссылка на боксарт для Glide LOCAL - 13
                    COLUMN_IS_DELETED + " integer," + // - 14
                    COLUMN_DATE + " text," +// дата добавления? LOCAL? - 15
                    COLUMN_YEAR + " text," + // год выпуска набора - 16

                    COLUMN_PURCHASE_DATE + " text," + //дата покупки -17
                    COLUMN_PRICE + " text," + //цена -18
                    COLUMN_QUANTITY + " integer," + //количество - 19
                    COLUMN_NOTES + " text," + //заметки - 20
                    COLUMN_CURRENCY + " text," + //валюта - 21

                    MYLISTSITEMS_LISTNAME + " text," + //22
                    COLUMN_STATUS + " text," + //начат/использован //23
                    COLUMN_MEDIA  + " text," + //материал
                    COLUMN_SCALEMATES_URL + " text," + //материал - 24

                    KIT_AFTER_AFTERDESIGNEDFOR + " text," + //25
                    COLUMN_PURCHASE_PLACE + " text" + //место покупки - 26
                    ");";

    private static final String CREATE_TABLE_AFTERMARKET_MYLISTITEMS =
            "create table " + TABLE_AFTERMARKET_MYLIST_ITEMS + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " + // Локальный ключ -0
                    COLUMN_BARCODE + " text," + // штрихкод NOBARCODE по умолчанию для garage kit? - 1
                    COLUMN_BRAND + " text, " + // производитель - 2
                    COLUMN_BRAND_CATNO + " text," + //каталожный номер набора - 3
                    COLUMN_SCALE + " integer," + //масштаб - 4
                    COLUMN_AFTERMARKET_NAME + " text," + //название набора - 5
                    COLUMN_DESCRIPTION + " text," + //описание, продолжение названия - 6
                    COLUMN_ORIGINAL_NAME + " text," + //название на оригинальном языке, - 7
                    // если отличается
                    COLUMN_CATEGORY + " text," + //тег (самолет, корабль, и тд - 8
                    COLUMN_COLLECTION  + " text," + //коллекция - для группировки и других функций - 9
                    COLUMN_SEND_STATUS + " text," + //для отслеживания офлайн отправок LOCAL - 10
                    COLUMN_ID_ONLINE + " text," + //номер в онлайновой базе, может пригодится - 11
                    //заметки? LOCAL?
                    COLUMN_BOXART_URI + " text," + //локальная ссылка на файл боксарта LOCAL - 12
                    COLUMN_BOXART_URL + " text," + //интернет-ссылка на боксарт для Glide LOCAL - 13
                    COLUMN_IS_DELETED + " integer," + // - 14
                    COLUMN_DATE + " text," +// дата добавления? LOCAL? - 15
                    COLUMN_YEAR + " text," + // год выпуска набора - 16

                    COLUMN_PURCHASE_DATE + " text," + //дата покупки -17
                    COLUMN_PRICE + " text," + //цена -18
                    COLUMN_QUANTITY + " integer," + //количество - 19
                    COLUMN_NOTES + " text," + //заметки - 20
                    COLUMN_CURRENCY + " text," + //валюта - 21
                    COLUMN_SCALEMATES_URL + " text," + //материал


                    COLUMN_STATUS + " text," + //начат/использован
                    COLUMN_MEDIA  + " text," + //материал
                    COLUMN_PURCHASE_PLACE + " text," + //место покупки - 22
                    AFTERMARKET_MYLIST_NAME + " text" +
                    ");";


    private static final String CREATE_TABLE_CURRENCIES =
            "create table " + TABLE_CURRENCIES + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " + // Локальный ключ -0
                    CURRENCIES_COLUMN_CURRENCY + " text" +
                    ");";

    private static final String CREATE_TABLE_MYSHOPS =
            "create table " + TABLE_MYSHOPS + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " + // Локальный ключ -0
                    MYSHOPS_COLUMN_SHOP_NAME + " text," +
                    MYSHOPS_COLUMN_SHOP_DESCRIPTION + " text," +
                    MYSHOPS_COLUMN_SHOP_URL + " text," +
                    MYSHOPS_COLUMN_SHOP_CONTACT + " text," +
                    MYSHOPS_COLUMN_SHOP_RATING + " integer" +
                    ");";

    private static final String CREATE_TABLE_MYLISTSITEMS =
            "create table " + TABLE_MYLISTSITEMS + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " + // Локальный ключ -0
                    COLUMN_BARCODE + " text," + // штрихкод NOBARCODE по умолчанию для garage kit? - 1
                    COLUMN_BRAND + " text, " + // производитель - 2
                    COLUMN_BRAND_CATNO + " text," + //каталожный номер набора - 3
                    COLUMN_SCALE + " integer," + //масштаб - 4
                    COLUMN_KIT_NAME + " text," + //название набора - 5
                    COLUMN_DESCRIPTION + " text," + //описание, продолжение названия - 6
                    COLUMN_ORIGINAL_NAME + " text," + //название на оригинальном языке, - 7
                    // если отличается
                    COLUMN_CATEGORY + " text," + //тег (самолет, корабль, и тд - 8
                    COLUMN_COLLECTION  + " text," + //коллекция - для группировки и других функций - 9
                    COLUMN_SEND_STATUS + " text," + //для отслеживания офлайн отправок LOCAL - 10
                    COLUMN_ID_ONLINE + " text," + //номер в онлайновой базе, может пригодится - 11
                    //заметки? LOCAL?
                    COLUMN_BOXART_URI + " text," + //локальная ссылка на файл боксарта LOCAL - 12
                    COLUMN_BOXART_URL + " text," + //интернет-ссылка на боксарт для Glide LOCAL - 13
                    COLUMN_IS_DELETED + " int," + // - 14
                    COLUMN_DATE + " text," +// дата добавления в базу LOCAL? - 15
                    COLUMN_YEAR + " text," + // год выпуска набора - 16

                    COLUMN_PURCHASE_DATE + " text," + //дата покупки -17
                    COLUMN_PRICE + " int," + //цена в копейках-18
                    COLUMN_QUANTITY + " integer," + //количество - 19
                    COLUMN_NOTES + " text," + //заметки - 20
                    COLUMN_CURRENCY + " text," + //валюта - 21
                    COLUMN_PURCHASE_PLACE + " text," + //место покупки - 22

                    COLUMN_STATUS + " text," + //начат/использован - 23
                    COLUMN_MEDIA + " text," + //материал - 24
                    COLUMN_SCALEMATES_URL + " text," + //скейлмейтс -25

                    MYLISTSITEMS_LISTNAME + " text" + // Локальный ключ -26

                    ");";


    private static final String CREATE_TABLE_MYLISTS =
            "create table " + TABLE_MYLISTS + "(" +
                    MYLISTS_COLUMN_ID + " integer primary key autoincrement, " + // Локальный ключ
                    MYLISTS_COLUMN_LIST_NAME + " text," +// значение параметра
                    MYLISTS_COLUMN_DATE + " text" +// значение параметра
                    ");";


    private static final String CREATE_TABLE_STATISTIC =
            "create table " + TABLE_STATISTIC + "(" +
                    STATISTIC_COLUNN_ID + " integer primary key autoincrement, " + // Локальный ключ
                    STATISTICS_STATNAME + " text," + // имя параметра
                    STATISTICS_STATVALUE + " text" +// значение параметра
                    ");";


    private static final String CREATE_TABLE_KITS =
            "create table " + TABLE_KITS + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " + // Локальный ключ -0
                    COLUMN_BARCODE + " text," + // штрихкод NOBARCODE по умолчанию для garage kit? - 1
                    COLUMN_BRAND + " text, " + // производитель - 2
                    COLUMN_BRAND_CATNO + " text," + //каталожный номер набора - 3
                    COLUMN_SCALE + " integer," + //масштаб - 4
                    COLUMN_KIT_NAME + " text," + //название набора - 5
                    COLUMN_DESCRIPTION + " text," + //описание, продолжение названия - 6
                    COLUMN_ORIGINAL_NAME + " text," + //название на оригинальном языке, - 7
                    // если отличается
                    COLUMN_CATEGORY + " text," + //тег (самолет, корабль, и тд - 8
                    COLUMN_COLLECTION  + " text," + //коллекция - для группировки и других функций - 9
                    COLUMN_SEND_STATUS + " text," + //для отслеживания офлайн отправок LOCAL - 10
                    COLUMN_ID_ONLINE + " text," + //номер в онлайновой базе, может пригодится - 11
                    //заметки? LOCAL?
                    COLUMN_BOXART_URI + " text," + //локальная ссылка на файл боксарта LOCAL - 12
                    COLUMN_BOXART_URL + " text," + //интернет-ссылка на боксарт для Glide LOCAL - 13
                    COLUMN_IS_DELETED + " integer," + // - 14
                    COLUMN_DATE + " text," +// дата добавления? LOCAL? - 15
                    COLUMN_YEAR + " text," + // год выпуска набора - 16
                    COLUMN_SCALEMATES_URL + " text," + //материал - 17

                    COLUMN_PURCHASE_DATE + " text," + //дата покупки -18
                    COLUMN_PRICE + " integer," + //цена -19
                    COLUMN_QUANTITY + " integer," + //количество - 20
                    COLUMN_NOTES + " text," + //заметки - 21
                    COLUMN_CURRENCY + " text," + //валюта - 22
                    COLUMN_PURCHASE_PLACE + " text," + //место покупки - 23
                    COLUMN_STATUS + " integer," + //начат/использован - 24
                    COLUMN_MEDIA + " text" + //материал - 25
                    ");";

    private static final String CREATE_TABLE_BRANDS =
            "CREATE TABLE " + TABLE_BRANDS + "(" +
                    BRANDS_COLUMN_ID + " integer primary key autoincrement, " +
                    BRANDS_COLUMN_BRAND + " text" + //Список фирм-производителей для автодополнения
                    // в AddActivity
                    ");";

    private static final String CREATE_TABLE_CATEGORY =
            "create table " + TABLE_TAGS + "(" +
                    TAGS_COLUMN_ID + " integer primary key autoincrement, " + // Локальный ключ
                    TAGS_COLUMN_TAG + " text" +
                    ");";


    private static final String CREATE_TABLE_ACCOUNT =
            "create table " + TABLE_ACCOUNT + "(" +
                    ACCOUNT_COLUMN_ID + " integer primary key autoincrement, " + // Локальный ключ
                    ACCOUNT_COLUMN_USER_NAME + " text," +
                    ACCOUNT_COLUMN_USER_EMAIL + " text," +
                    ACCOUNT_COLUMN_PROFILEPIC_URL + " text," +
                    ACCOUNT_COLUMN_FACEBOOK_PROFILE_URL + " text," +
                    ACCOUNT_COLUMN_NETWORK + " text" +
                    ");";

    private static final String INIT_TABLE_BRANDS =
            "INSERT INTO " + TABLE_BRANDS + "(" + BRANDS_COLUMN_BRAND + ")" +
                    " SELECT 'Academy' AS " + BRANDS_COLUMN_BRAND +
                    " UNION SELECT 'Academy Minicraft'" +
                    " UNION SELECT 'Accu-Scale'" +
                    " UNION SELECT 'ACE'" +
                    " UNION SELECT 'AFV Club'" +
                    " UNION SELECT 'Airfix'" +
                    " UNION SELECT 'AJM Models'" +
                    " UNION SELECT 'Alanger'" +
                    " UNION SELECT 'Alley Cat'" +
                    " UNION SELECT 'Amodel'" +
                    " UNION SELECT 'AMP'" +
                    " UNION SELECT 'AMT/ERTL'" +
                    " UNION SELECT 'Aoshima'" +
                    " UNION SELECT 'ART model'" +
                    " UNION SELECT 'Atlantic Models'" +
                    " UNION SELECT 'Aurora Heller'" +
                    " UNION SELECT 'Bandai'" +
                    " UNION SELECT 'Blue Ridge Models'" +
                    " UNION SELECT 'Blue Water Navy'" +
                    " UNION SELECT 'Bronco'" +
                    " UNION SELECT 'Classic Plane'" +
                    " UNION SELECT 'Combat Models'" +
                    " UNION SELECT 'Combrig Models'" +
                    " UNION SELECT 'Cottage Industry Models'" +
                    " UNION SELECT 'Doyusha'" +
                    " UNION SELECT 'Dragon'" +
                    " UNION SELECT 'Eduard'" +
                    " UNION SELECT 'FineMolds'" +
                    " UNION SELECT 'Fisher Model Pattern'" +
                    " UNION SELECT 'Fly'" +
                    " UNION SELECT 'Fujimi'" +
                    " UNION SELECT 'Greenstrawberry'" +
                    " UNION SELECT 'Hasegawa'" +
                    " UNION SELECT 'Heller'" +
                    " UNION SELECT 'Heller HTC'" +
                    " UNION SELECT 'Herb Deeks Models'" +
                    " UNION SELECT 'Hobby Boss'" +
                    " UNION SELECT 'HpH models'" +
                    " UNION SELECT 'ICM'" +
                    " UNION SELECT 'ID Models'" +
                    " UNION SELECT 'Imai'" +
                    " UNION SELECT 'Iron Shipwrights'" +
                    " UNION SELECT 'Italeri'" +
                    " UNION SELECT 'Kinetic'" +
                    " UNION SELECT 'Kitty Hawk'" +
                    " UNION SELECT 'Kopro'" +
                    " UNION SELECT 'Kopro-MasterCraft'" +
                    " UNION SELECT 'KP'" +
                    " UNION SELECT 'LEM Kits'" +
                    " UNION SELECT 'Lindberg'" +
                    " UNION SELECT 'Lukgraph'" +
                    " UNION SELECT 'Marsh Models/Aerotech'" +
                    " UNION SELECT 'Meng Model'" +
                    " UNION SELECT 'Midwest Products Co.'" +
                    " UNION SELECT 'MikroMir'" +
                    " UNION SELECT 'Minicraft Hasegawa'" +
                    " UNION SELECT 'Mirage Hobby'" +
                    " UNION SELECT 'Moebius Models'" +
                    " UNION SELECT 'Monogram'" +
                    " UNION SELECT 'Montex'" +
                    " UNION SELECT 'Naval Models'" +
                    " UNION SELECT 'Niko Model'" +
                    " UNION SELECT 'Omega Models'" +
                    " UNION SELECT 'Orange Hobby'" +
                    " UNION SELECT 'Pacific Coast Models'" +
                    " UNION SELECT 'Pacific Crossroads'" +
                    " UNION SELECT 'Pegasus Hobbies'" +
                    " UNION SELECT 'Pit-Road'" +
                    " UNION SELECT 'Planet Models'" +
                    " UNION SELECT 'Polar Lights'" +
                    " UNION SELECT 'Prop & Jet'" +
                    " UNION SELECT 'Raccoon Models'" +
                    " UNION SELECT 'Revell'" +
                    " UNION SELECT 'Revell Congost'" +
                    " UNION SELECT 'Revell ESCI'" +
                    " UNION SELECT 'Revell Great Britain'" +
                    " UNION SELECT 'Revell Italaerei'" +
                    " UNION SELECT 'Revell Lodela'" +
                    " UNION SELECT 'Revell Monogram'" +
                    " UNION SELECT 'Revell Takara'" +
                    " UNION SELECT 'Roden'" +
                    " UNION SELECT 'ROP o.s. Samek Models'" +
                    " UNION SELECT 'Silver Wings'" +
                    " UNION SELECT 'South Front'" +
                    " UNION SELECT 'Special Hobby'" +
                    " UNION SELECT 'SSN-Modellbau'" +
                    " UNION SELECT 'Tamiya'" +
                    " UNION SELECT 'Tanmodel'" +
                    " UNION SELECT 'Testors'" +
                    " UNION SELECT 'Tigger Models'" +
                    " UNION SELECT 'Toko'" +
                    " UNION SELECT 'Toms Modelworks'" +
                    " UNION SELECT 'Trumpeter'" +
                    " UNION SELECT 'U-Boat-Laboratorium'" +
                    " UNION SELECT 'Viking Models'" +
                    " UNION SELECT 'Wake Models'" +
                    " UNION SELECT 'White Ensign Models'" +
                    " UNION SELECT 'Williams Brothers'" +
                    " UNION SELECT 'Wingnut Wings'" +
                    " UNION SELECT 'Yankee Modelworks'" +
                    " UNION SELECT 'Zoukei-Mura'" +
                    " UNION SELECT 'Zvezda';";

    private static final String INIT_TABLE_CURRENCIES =
            "INSERT INTO " + TABLE_CURRENCIES + "(" + CURRENCIES_COLUMN_CURRENCY + ")" +
                    " SELECT 'BYN' AS " + CURRENCIES_COLUMN_CURRENCY +
                    " UNION SELECT 'EUR'" +
                    " UNION SELECT 'RUR'" +
                    " UNION SELECT 'USD'" +
                    " UNION SELECT 'UAH';";

    private static final String INIT_TABLE_CATEGORIES =
            "INSERT INTO " + TABLE_CATEGORIES + "(name, res_id) VALUES (1," + R.string.Air + ");"
                    + "INSERT INTO " + TABLE_CATEGORIES + "(name, res_id, count) VALUES (2," + R.string.Ground + ");"
                    + "INSERT INTO " + TABLE_CATEGORIES + "(name, res_id, count) VALUES (3," + R.string.Sea + ");"
                    + "INSERT INTO " + TABLE_CATEGORIES + "(name, res_id, count) VALUES (4," + R.string.Space + ");"
                    + "INSERT INTO " + TABLE_CATEGORIES + "(name, res_id, count) VALUES (5," + R.string.Auto_moto + ");"
                    + "INSERT INTO " + TABLE_CATEGORIES + "(name, res_id, count) VALUES (6," + R.string.Figures + ");"
                    + "INSERT INTO " + TABLE_CATEGORIES + "(name, res_id, count) VALUES (7," + R.string.Fantasy + ");"
                    + "INSERT INTO " + TABLE_CATEGORIES + "(name, res_id, count) VALUES (8," + R.string.Other + ",0" + ");";

    //todo delete?
    private static final String INIT_TABLE_ACCOUNT =
            "INSERT INTO " + TABLE_ACCOUNT + "(" + ACCOUNT_COLUMN_ID + ")" +
                    " SELECT '1' AS " + ACCOUNT_COLUMN_ID +
                    " ;";





    public DbConnector(Context context) {
//           super(); //todo?
        this.context = context;
    }


    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();

    }

    // закрыть подключение
    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    //вакуум - удаление и перестройка базы
    public void vacuumDb(){
        mDB.execSQL("VACUUM");
    }

    public void clearTable(String table)   {
        mDB.delete(table, null,null);
    }

    //////////////UNIVERSAL//////////

    //    все из таблицы
    public Cursor getAllFromTable(String tableName, String sortBy) {
        return mDB.query(tableName, null, null, null, null, null, sortBy);
    }

    //    ++++++++++++++++++++++++++
    public ArrayList<Kit> getAllFromTableAsList(String tableName, String sortBy) {
        Cursor cursor = mDB.query(tableName, null, null, null, null, null, sortBy);
        ArrayList<Kit> kitList = new ArrayList<Kit>();
        cursor.moveToFirst();


        while (!cursor.isAfterLast()) {
            final long id = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ID));
            String url = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URL));
            final String uri = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
            String brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
            String cat_no = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME));
            String scale = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE));
            final String onlneId = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ID_ONLINE));
            Kit kit = new Kit.KitBuilder()
                    .hasLocalId(id)
                    .hasBoxart_url(url)
                    .hasBoxart_uri(uri)
                    .hasBrand(brand)
                    .hasBrand_catno(cat_no)
                    .hasKit_name(name)
                    .hasScale(Integer.valueOf(scale))
                    .hasOnlineId(onlneId)
                    .build();
            kitList.add(kit);
            cursor.moveToNext();
        }
//        cursor.close();
        return kitList;
    }

    public void addRec(Object item) {
        String tableName = TABLE_KITS;
        if (item.getClass().equals(Kit.class)) {
            tableName = TABLE_KITS;
//            item = (Kit)item;
        } else if (item.getClass().equals(Aftermarket.class)) {
            tableName = TABLE_AFTERMARKET;
        }

        ContentValues cv = new ContentValues();

////        if (item.getClass() == Kit.class){
//            cv.put(COLUMN_BARCODE, item.getBarcode());
////        String string = kit.getBarcode();
//            cv.put(COLUMN_BRAND, item.getBrand());
//            cv.put(COLUMN_BRAND_CATNO, item.getBrandCatno());
//            cv.put(COLUMN_SCALE, item.getScale());
//            cv.put(COLUMN_KIT_NAME, item.getKit_name());
//            cv.put(COLUMN_DESCRIPTION, item.getDescription());
//            cv.put(COLUMN_ORIGINAL_NAME, item.getKit_noeng_name());
//
//            cv.put(COLUMN_CATEGORY, item.getCategory());
////        cv.put(COLUMN_COLLECTION, kit.get  + " text," + //коллекция - для группировки и других функций - 9
////        cv.put(COLUMN_SEND_STATUS, kit.getSe + " text," + //для отслеживания офлайн отправок LOCAL - 10
//            cv.put(COLUMN_ID_ONLINE, item.getOnlineId());
//            //заметки? LOCAL?
//            cv.put(COLUMN_BOXART_URI, item.getBoxart_uri());
//            cv.put(COLUMN_BOXART_URL, item.getBoxart_url());
////        cv.put(COLUMN_IS_DELETED, kit.get + " int," + // - 14
//            cv.put(COLUMN_DATE, item.getDate_added());
//            cv.put(COLUMN_YEAR, item.getYear());
//
//            cv.put(COLUMN_PURCHASE_DATE, item.getDatePurchased());
//            cv.put(COLUMN_PRICE, item.getPrice());
//            cv.put(COLUMN_QUANTITY, item.getQuantity());
//            cv.put(COLUMN_NOTES, item.getNotes());
//            cv.put(COLUMN_CURRENCY, item.getCurrency());
//            cv.put(COLUMN_SEND_STATUS, item.getSendStatus());
//            cv.put(COLUMN_PURCHASE_PLACE, item.getPlacePurchased());
//

//                MYLISTSITEMS_LISTNAME +

        mDB.insert(tableName, null, cv);


    }

    public void editRec() {

    }

    //////////////////////////UNIVERSAL/////////////////////////////////////////////////

    public Cursor getAllItems(String tableName, String sortBy) {
        //return mDB.query(TABLE_KITS, null, "is_deleted = ?", new String[] {"0"}, null, null, sortBy);
        return mDB.query(tableName, null, null, null, null, null, sortBy);
    }

    //Получить запись по идентификатору
    public Cursor getItemById(String tableName, long id) {
        return mDB.query(tableName, null, "_id = " + id, null, null, null, null);
        //todo написать перевод в редактор и перенос значений
    }

//    public Cursor getFieldById(String tableName, long id, String fieldName){
//        return mDB.query(tableName, new String[]{fieldName}, "_id = " + id, null, null, null, null);
//    }

    //Редактирование записи
    public void editItemById(String tableName, long id, ContentValues cv) {
        //String clause = String.valueOf(id);
        mDB.update(tableName, cv, "_id = ?", new String[]{String.valueOf(id)});
    }

    // удалить запись из TABLE
    public void delItemById(String tableName, long id) {
        mDB.delete(tableName, COLUMN_ID + " = " + id, null);
    }

    public boolean searchForDoubles(char editMode, String brand, String cat_no) {
        boolean found = false;
        String having = "";
        String tableName = TABLE_KITS;
        if (editMode == MyConstants.MODE_KIT) {
            tableName = TABLE_KITS;
        } else if (editMode == MyConstants.MODE_AFTERMARKET) {
            tableName = TABLE_AFTERMARKET;
        } else if (editMode == MyConstants.MODE_LIST) {
            tableName = TABLE_MYLISTSITEMS;
//        } else if (editMode == MyConstants.MODE_AFTER_KIT){
//            tableName = TABLE_KIT_AFTER_CONNECTIONS;
//            having = KIT_AFTER_KITID + " = '" + String.valueOf(kitId) + "'";
        }

        if (mDB.query(tableName, new String[]{"brand", "brand_catno"}, "brand = ? " +
                "AND brand_catno = ?", new String[]{brand, cat_no}, null, having, null)
                .getCount() != 0) {
            found = true;
        }
        return found;
    }

    //////////////KITS//////////////

    // получить все данные из таблицы TABLE_KITS
    public Cursor getAllData() {
        return mDB.query(TABLE_KITS, null, null, null, null, null, null);
    }

    //Все с сортировкой!!!!!!!!!!!!!!!!!!!!!
    public Cursor getAllData(String sortBy) {
        return mDB.query(TABLE_KITS, null, null, null, null, null, sortBy);
    }

//    public ArrayList<Item> getAllData(String sortBy) {
//        return mDB.query(TABLE_KITS, null, null, null, null, null, sortBy);
//    }

    //Все без удаленных с сортировкой по категории
    public Cursor getByCategory(String category, String sortBy) {
        return mDB.query(TABLE_KITS, null, "category = ? and (is_deleted is null or is_deleted =?)",
                new String[] {category, ""}, null, null, sortBy);
    }

    //Все удаленные в корзину KITS
    public Cursor getAllTrash(String sortBy) {
        return mDB.query(TABLE_KITS, null, "is_deleted = ?", new String[] {String.valueOf(1)}, null,
                null, sortBy);
    }

    // Все по дате
    public Cursor getDataDate(String date) {
        return mDB.query(TABLE_KITS,null,"date = ?", new String[] {date}, null, null, null);
    }

    // Все по категории
    public Cursor getByTag(String tag) {
        return mDB.query(TABLE_KITS,null,"category = ? and (is_deleted is null or is_deleted =?)",
                new String[] {tag,""}, null, null, null);
    }

    //Все без боксартов
    public Cursor getAllWithoutBoxart() {
        return mDB.query(TABLE_KITS, null, "boxart_url is null or boxart_url = ?", new String[] {""},
                null, null, null);
    }

    //Все офлайновые
    public Cursor getAllOffline(){
        return mDB.query(TABLE_KITS, null, "send_status = ?", new String[] {"n"}, null, null, null);
    }

    //Для статистики - все использованные брэнды, отсотрированные по количеству вхождений
    public Cursor getBrandsStat(){
        return mDB.rawQuery("SELECT DISTINCT brand, COUNT(brand) as count FROM kits " +
                "WHERE is_deleted is null or is_deleted ='' GROUP BY brand ORDER BY count", null);
    }

    //Все брэнды без удаленных с сортировкой по категории
    public Cursor getBrandsForCount(String brand) {
        return mDB.query(TABLE_KITS, null, "brand = ? and (is_deleted is null or is_deleted =?)",
                new String[] {brand, ""}, null, null, null);
    }

    //Выборка по дате записи
    public Cursor getKitsByDate(){
        return mDB.rawQuery("SELECT DISTINCT date FROM kits " +
                "WHERE is_deleted is null or is_deleted ='' ORDER BY date", null);
    }

    //Подсчет количества по датам
    public Cursor countByDate(String date){
        return mDB.query(TABLE_KITS, null, "date = ? and (is_deleted is null or is_deleted =?)",
                new String[] {date, ""}, null, null, null);
    }

    //Получить запись по идентификатору!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public Cursor getKitById(long id) {
        return mDB.query(TABLE_KITS, null, "_id = " + id, null, null, null, null);
        //todo написать перевод в редактор и перенос значений
    }

    //Редактирование записи!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public void editRecById(long id, ContentValues cv){
        //String clause = String.valueOf(id);
        mDB.update(TABLE_KITS, cv, "_id = ?", new String[] { String.valueOf(id) });
    }

    // Добавление записи в KITS
    public long addKitRec(Kit kit) {

        ContentValues cv = new ContentValues();

        cv.put(COLUMN_BARCODE, kit.getBarcode());
        cv.put(COLUMN_BRAND, kit.getBrand());
        cv.put(COLUMN_BRAND_CATNO, kit.getBrandCatno());
        cv.put(COLUMN_SCALE, kit.getScale());
        cv.put(COLUMN_KIT_NAME, kit.getKit_name());
        cv.put(COLUMN_DESCRIPTION, kit.getDescription());
        cv.put(COLUMN_ORIGINAL_NAME, kit.getKit_noeng_name());

        cv.put(COLUMN_CATEGORY, kit.getCategory());
//        cv.put(COLUMN_COLLECTION, kit.get  + " text," + //коллекция - для группировки и других функций - 9
//        cv.put(COLUMN_SEND_STATUS, kit.getSe + " text," + //для отслеживания офлайн отправок LOCAL - 10
        cv.put(COLUMN_ID_ONLINE, kit.getOnlineId());
        //заметки? LOCAL?
        cv.put(COLUMN_BOXART_URI, kit.getBoxart_uri());
        cv.put(COLUMN_BOXART_URL, kit.getBoxart_url());
//        cv.put(COLUMN_IS_DELETED, kit.get + " int," + // - 14
        cv.put(COLUMN_DATE, kit.getDate_added());
        cv.put(COLUMN_YEAR, kit.getYear());
        cv.put(COLUMN_SCALEMATES_URL, kit.getScalemates_url());

        cv.put(COLUMN_PURCHASE_DATE, kit.getDatePurchased());
        cv.put(COLUMN_PRICE, kit.getPrice());
        cv.put(COLUMN_QUANTITY, kit.getQuantity());
        cv.put(COLUMN_NOTES, kit.getNotes());
        cv.put(COLUMN_CURRENCY, kit.getCurrency());
        cv.put(COLUMN_SEND_STATUS, kit.getSendStatus());
        cv.put(COLUMN_PURCHASE_PLACE, kit.getPlacePurchased());
        cv.put(COLUMN_STATUS, kit.getStatus());
        return mDB.insert(TABLE_KITS, null, cv);
    }

    //Добавление импортом
    public void addKitRec(ContentValues cv){
        mDB.insert(TABLE_KITS, null, cv);
    }

    // удалить запись из TABLE_KITS!!!!!!!!!!!!!!!
    public void delRec(String tableName, long id) {
        mDB.delete(tableName, COLUMN_ID + " = " + id, null);
    }

    //+++++++++++++++++++++++++
    public ArrayList<String> getFilterData(String tableName, String filter) {
        ArrayList<String> filterData = new ArrayList<>();
        String query = "select DISTINCT " + filter + " from "+ tableName +" ORDER BY " + filter + " ASC;";
        Cursor cursor = mDB.rawQuery(query, null);
        while (cursor.moveToNext()) {
            filterData.add(cursor.getString(cursor.getColumnIndex(filter)));
        }
        cursor.close();
        return filterData;
    }

    //+++++++++++++++++++++++++++
    public ArrayList<String> getFilterFromIntData(String tableName, String filter) {
        ArrayList<String> filterData = new ArrayList<>();
        String query = "select DISTINCT " + filter + " from " + tableName + " ORDER BY " + filter + " ASC;";
        Cursor cursor = mDB.rawQuery(query, null);
        while (cursor.moveToNext()) {
//            filterData.add(String.valueOf(cursor.getInt(cursor.getColumnIndex(filter))));
            if (filter.equals(COLUMN_STATUS)) {
                String s = codeToStatus(cursor.getInt(cursor.getColumnIndex(filter)));
                filterData.add(s);
            }else if (filter.equals(COLUMN_MEDIA)){
                String m = codeToMedia(cursor.getInt(cursor.getColumnIndex(filter)));
                filterData.add(m);
            }
        }
        cursor.close();
        return filterData;
    }

    private String codeToStatus(int code){
        String status;
        switch (code){
            case MyConstants.STATUS_NEW:
                status = context.getResources().getString(R.string.status_new);
                break;
            case MyConstants.STATUS_OPENED:
                status = context.getResources().getString(R.string.status_opened);
                break;
            case MyConstants.STATUS_STARTED:
                status = context.getResources().getString(R.string.status_started);
                break;
            case MyConstants.STATUS_INPROGRESS:
                status = context.getResources().getString(R.string.status_inprogress);
                break;
            case MyConstants.STATUS_FINISHED:
                status = context.getResources().getString(R.string.status_finished);
                break;
            case MyConstants.STATUS_LOST:
                status = context.getResources().getString(R.string.status_lost_sold);
                break;
            default:
                status = context.getResources().getString(R.string.status_new);
                break;
        }
        return status;
    }

    private String codeToMedia(int mediaCode){
        String media;
        switch (mediaCode){
            case MyConstants.M_CODE_OTHER:
                media = context.getResources().getString(R.string.media_other);
                break;
            case MyConstants.M_CODE_INJECTED:
                media = context.getResources().getString(R.string.media_injected);
                break;
            case MyConstants.M_CODE_SHORTRUN:
                media = context.getResources().getString(R.string.media_shortrun);
                break;
            case MyConstants.M_CODE_RESIN:
                media = context.getResources().getString(R.string.media_resin);
                break;
            case MyConstants.M_CODE_VACU:
                media = context.getResources().getString(R.string.media_vacu);
                break;
            case MyConstants.M_CODE_PAPER:
                media = context.getResources().getString(R.string.media_paper);
                break;
            case MyConstants.M_CODE_WOOD:
                media = context.getResources().getString(R.string.media_wood);
                break;
            case MyConstants.M_CODE_METAL:
                media = context.getResources().getString(R.string.media_metal);
                break;
            case MyConstants.M_CODE_3DPRINT:
                media = context.getResources().getString(R.string.media_3dprint);
                break;
            case MyConstants.M_CODE_MULTIMEDIA:
                media = context.getResources().getString(R.string.media_multimedia);
                break;
            default:
                media = context.getResources().getString(R.string.media_other);
                break;
        }
        return media;
    }

    // Список с фильтрацией и сортировкой
    public ArrayList<Kit> filteredKits(String tableName, String sortBy, String category) {
        ArrayList<Kit> itemList;
        String groupBy = "_id";
        String having;
        switch (category) {
            case "1":
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_AIR + "'"; //"category = 'air'"
                break;
            case "2":
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_GROUND + "'";
                break;
            case "3":
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_SEA + "'";
                break;
            case "4":
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_SPACE + "'";
                break;
            case "5":
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_AUTOMOTO + "'";
                break;
            case "6":
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_FIGURES + "'";
                break;
            case "7":
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_FANTASY + "'";
                break;
            case "8":
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_OTHER + "'";
                break;
            default:
                having = null;
                break;
        }
        Cursor cursor = mDB.query(tableName, null, null, null, groupBy, having, sortBy);
        itemList = prepareKit(cursor);
        cursor.close();
        return itemList;
    }

    public ArrayList<ChooserItem> filteredAftermarket(String tableName, String[] filters, String sortBy,
                                                      String category, String listname) {
        ArrayList<ChooserItem> itemList = new ArrayList<>();
        String groupBy = "_id";
        String having;
        if (listname.equals(MyConstants.EMPTY)) {
            switch (category) {
                case "1":
                    having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_AIR + "'"; //"category = 'air'"
                    break;
                case "2":
                    having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_GROUND + "'";
                    break;
                case "3":
                    having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_SEA + "'";
                    break;
                case "4":
                    having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_SPACE + "'";
                    break;
                case "5":
                    having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_AUTOMOTO + "'";
                    break;
                case "6":
                    having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_FIGURES + "'";
                    break;
                case "7":
                    having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_FANTASY + "'";
                    break;
                case "8":
                    having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_OTHER + "'";
                    break;
                default:
                    having = null;
                    break;
            }
        } else {
            having = DbConnector.MYLISTSITEMS_LISTNAME + " = '" + listname + "'";
        }


        if (filters.length == 0){
            Cursor cursor = mDB.query(tableName, null, null, null, groupBy, having, sortBy);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String url = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URL));
                ;
                String uri = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
                ;
//                long id = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ID));
                String brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME));
                int scale = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE));

                ChooserItem item = new ChooserItem();
                item.setUrl(url);
                item.setUri(uri);
                item.setBrand(brand);
                item.setName(name);
                item.setScale(scale);

                itemList.add(item);
                cursor.moveToNext();
            }
            cursor.close();
            return itemList;
        }else {

            ArrayList<String> clausesList = new ArrayList<>();
            ArrayList<String> argsList = new ArrayList<>();

            if (!filters[2].equals("")) {
                if (argsList.size() == 0) {
                    clausesList.add(COLUMN_KIT_NAME + " LIKE ?");
                    argsList.add("%" + filters[2] + "%");
                } else {
                    clausesList.add(" AND " + COLUMN_KIT_NAME + " LIKE ?");
                    argsList.add("%" + filters[2] + "%");
                }
            }
            if (!filters[0].equals("")) {
                if (argsList.size() == 0) {
                    clausesList.add(COLUMN_SCALE + " = ?");
                    argsList.add(filters[0]);
                }else {
                    clausesList.add(" AND " + COLUMN_SCALE + " = ?");
                    argsList.add(filters[0]);
                }
            }
            if (!filters[1].equals("")) {
                if (argsList.size() == 0) {
                    clausesList.add(COLUMN_BRAND + " = ?");
                    argsList.add(filters[1]);
                } else {
                    clausesList.add(" AND " + COLUMN_BRAND + " = ?");
                    argsList.add(filters[1]);
                }
            }
            if (!filters[3].equals("")) {
                if (argsList.size() == 0) {
                    clausesList.add(COLUMN_STATUS + " = ?");
                    argsList.add(filters[3]);
                } else {
                    clausesList.add(" AND " + COLUMN_STATUS + " = ?");
                    argsList.add(filters[3]);
                }
            }
            if (!filters[4].equals("")) {
                if (argsList.size() == 0) {
                    clausesList.add(COLUMN_MEDIA + " = ?");
                    argsList.add(filters[4]);
                } else {
                    clausesList.add(" AND " + COLUMN_MEDIA + " = ?");
                    argsList.add(filters[4]);
                }
            }

            String[] argsStringArray = new String[argsList.size()];
            argsStringArray = argsList.toArray(argsStringArray);

            String[] clausesStringArray = new String[clausesList.size()];
            clausesStringArray = clausesList.toArray(clausesStringArray);

            StringBuilder builder = new StringBuilder();
            for (String s : clausesStringArray) {
                builder.append(s);
            }
            String query = builder.toString();

            Cursor cursor = mDB.query(tableName, null, query, argsStringArray, groupBy, having, sortBy);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String url = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URL));
                ;
                String uri = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
                ;
//                long id = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ID));
                String brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME));
                int scale = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE));

                ChooserItem item = new ChooserItem();
                item.setUrl(url);
                item.setUri(uri);
                item.setBrand(brand);
                item.setName(name);
                item.setScale(scale);

                itemList.add(item);
                cursor.moveToNext();
            }
            cursor.close();
            return itemList;
        }
    }

// Список с фильтрацией и сортировкой
//public Cursor filteredKits(String tableName, String[] filters, String sortBy,
//                           String category, String listname) {
//        String groupBy = "_id";
//        String having;
//    if (listname.equals(MyConstants.EMPTY)) {
//        switch (category) {
//            case "1":
//                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_AIR + "'"; //"category = 'air'"
//                break;
//            case "2":
//                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_GROUND + "'";
//                break;
//            case "3":
//                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_SEA + "'";
//                break;
//            case "4":
//                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_SPACE + "'";
//                break;
//            case "5":
//                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_AUTOMOTO + "'";
//                break;
//            case "6":
//                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_FIGURES + "'";
//                break;
//            case "7":
//                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_FANTASY + "'";
//                break;
//            case "8":
//                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_OTHER + "'";
//                break;
//            default:
//                having = null;
//                break;
//        }
//    } else {
//        having = DbConnector.MYLISTSITEMS_LISTNAME + " = '" + listname + "'";
//        }
//
//
//        if (filters.length == 0){
//            return mDB.query(tableName, null, null, null, groupBy, having, sortBy);
//        }else {
//
//            ArrayList<String> clausesList = new ArrayList<>();
//            ArrayList<String> argsList = new ArrayList<>();
//
//            if (!filters[2].equals("")) {
//                if (argsList.size() == 0) {
//                    clausesList.add(COLUMN_KIT_NAME + " LIKE ?");
//                    argsList.add("%" + filters[2] + "%");
//                } else {
//                    clausesList.add(" AND " + COLUMN_KIT_NAME + " LIKE ?");
//                    argsList.add("%" + filters[2] + "%");
//                }
//            }
//            if (!filters[0].equals("")) {
//                if (argsList.size() == 0) {
//                    clausesList.add(COLUMN_SCALE + " = ?");
//                    argsList.add(filters[0]);
//                }else {
//                    clausesList.add(" AND " + COLUMN_SCALE + " = ?");
//                    argsList.add(filters[0]);
//                }
//            }
//            if (!filters[1].equals("")) {
//                if (argsList.size() == 0) {
//                    clausesList.add(COLUMN_BRAND + " = ?");
//                    argsList.add(filters[1]);
//                } else {
//                    clausesList.add(" AND " + COLUMN_BRAND + " = ?");
//                    argsList.add(filters[1]);
//                }
//            }
//            if (!filters[3].equals("")) {
//                if (argsList.size() == 0) {
//                    clausesList.add(COLUMN_STATUS + " = ?");
//                    argsList.add(filters[3]);
//                } else {
//                    clausesList.add(" AND " + COLUMN_STATUS + " = ?");
//                    argsList.add(filters[3]);
//                }
//            }
//            if (!filters[4].equals("")) {
//                if (argsList.size() == 0) {
//                    clausesList.add(COLUMN_MEDIA + " = ?");
//                    argsList.add(filters[4]);
//                } else {
//                    clausesList.add(" AND " + COLUMN_MEDIA + " = ?");
//                    argsList.add(filters[4]);
//                }
//            }
//
//            String[] argsStringArray = new String[argsList.size()];
//            argsStringArray = argsList.toArray(argsStringArray);
//
//            String[] clausesStringArray = new String[clausesList.size()];
//            clausesStringArray = clausesList.toArray(clausesStringArray);
//
//            StringBuilder builder = new StringBuilder();
//            for (String s : clausesStringArray) {
//                builder.append(s);
//            }
//            String query = builder.toString();
//
//            return mDB.query(tableName, null, query, argsStringArray, groupBy, having, sortBy);
//
//        }
//    }
    ////////////////BRANDS/////////////////

    //ТАБЛИЦА BRANDS Все с сортировкой!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public Cursor getBrands(String sortBy) {
        //return mDB.query(TABLE_KITS, null, "is_deleted = ?", new String[] {"0"}, null, null, sortBy);
        return mDB.query(TABLE_BRANDS, null, null, null, null, null, sortBy);
    }

    //НОВОЕ!!! Для автодополнения брэндов
    public void addBrand(String brand) {
        ContentValues cv = new ContentValues();
        cv.put(BRANDS_COLUMN_BRAND, brand);
        mDB.insert(TABLE_BRANDS, null, cv);
    }

    // Запись брэнда в таблицу
    public void addBrand(ContentValues cv) {
        mDB.insert(TABLE_BRANDS, null, cv);
    }


    //НОВОЕ!!! Для автодополнения брэндов
    public static ArrayList<String> getAllBrands() {
        ArrayList<String> allBrands = new ArrayList<>();
        String query = "select brand from brands;";
        Cursor cursor = mDB.rawQuery(query, null);
        while (cursor.moveToNext()) {
            allBrands.add(cursor.getString(cursor.getColumnIndex(COLUMN_BRAND)));
        }
        cursor.close();
        return allBrands;
    }

    public boolean isBrandExists(String brandname) {
        return getBrand(brandname).getCount() != 0;
    }

    public Cursor getBrand(String brandname) {
        return mDB.query(TABLE_BRANDS, null, "brand = ?", new String[]{brandname}, null, null, null);
    }

    public void updateBrand(long id, String newName) {
        ContentValues cv = new ContentValues();
        cv.put(BRANDS_COLUMN_BRAND, newName);
        mDB.update(TABLE_BRANDS, cv, "_id=?", new String[]{String.valueOf(id)});
    }


    // удалить запись из TABLE_BRANDS
    public void delBrand(long id) {
//        mDB.delete(TABLE_BRANDS, COLUMN_ID + " = " + id, null);
        mDB.delete(TABLE_BRANDS, COLUMN_ID + " =? ", new String[]{String.valueOf(id)});

    }

    /////////////////ACCOUNT//////////////////////
//добавить запись в ACCOUNTS
    public void addAccount(String network, String user_id, String userName, String user_email,
                           String userProfilePicUrl){
        ContentValues cv = new ContentValues();
        cv.put(ACCOUNT_COLUMN_ID, user_id);
        cv.put(ACCOUNT_COLUMN_USER_NAME, userName);
        cv.put(ACCOUNT_COLUMN_USER_EMAIL, user_email);
        cv.put(ACCOUNT_COLUMN_PROFILEPIC_URL, userProfilePicUrl);

        mDB.insert(TABLE_ACCOUNT, null, cv);
    }

    public void updateAccountData (String network, String field, String value){
        ContentValues cv = new ContentValues();
        cv.put(field, value);
        mDB.update(TABLE_KITS, cv, "network = ?", new String[] { network });
    }

    public Cursor getAccountData(String network) {
        //return mDB.query(TABLE_KITS, null, "is_deleted = ?", new String[] {"0"}, null, null, sortBy);
        return mDB.query(TABLE_ACCOUNT, null, "network = ?", new String[] { network }, null, null, null);
    }

    /////////////////MYLISTS//////////////////////
    // Добавить мой список
    public void addList (String listName, String date){
        ContentValues cv = new ContentValues();
        cv.put(MYLISTS_COLUMN_LIST_NAME, listName);
        cv.put(MYLISTS_COLUMN_DATE, date);
        mDB.insert(TABLE_MYLISTS, null, cv);
    }

    public void addList (ContentValues cv) {
        mDB.insert(TABLE_MYLISTS, null, cv);
    }

    public Cursor getLists(String sortBy) {
        return mDB.query(TABLE_MYLISTS, null, null, null, null, null, sortBy);
    }

    public Cursor getListById (long id){
        return mDB.query(TABLE_MYLISTS, null, "_id = " + id, null, null, null, null);
    }

    public Cursor getList (String listname){
        return mDB.query(TABLE_MYLISTS, null, "listname = ?", new String[] { listname }, null, null, null);
    }

    public void deleteList(long id, String listName) {
        mDB.delete(TABLE_MYLISTS, MYLISTS_COLUMN_ID + " =?", new String[]{String.valueOf(id)});
        clearList(listName);
//            delListItems(listName);
    }

    public void updateList(long id, String listName, String newListname) {
        ContentValues cv = new ContentValues();
        cv.put(MYLISTS_COLUMN_LIST_NAME, newListname);
        mDB.update(TABLE_MYLISTS, cv, "listname = ?", new String[] { listName });

        updateListItems(listName, newListname);
    }

    public boolean isListExists (String listname){
        if (getList(listname).getCount() != 0){
            return true;
        }else{
            return false;
        }
    }

    private void deleteListItems(String listName) {
        mDB.delete(TABLE_MYLISTSITEMS, MYLISTS_COLUMN_LIST_NAME + " =?", new String[]{listName});
    }

    ////////// LISTITEMS //////////

    private void updateListItems(String listName, String newListname){
        ContentValues cv = new ContentValues();
        cv.put(MYLISTSITEMS_LISTNAME, newListname);
        mDB.update(TABLE_MYLISTSITEMS, cv, "listname = ?", new String[] { listName });
    }

    public void addListItem(Kit kit, String listname){
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_BARCODE, kit.getBarcode());
        cv.put(COLUMN_BRAND, kit.getBrand());
        cv.put(COLUMN_BRAND_CATNO, kit.getBrandCatno());
        cv.put(COLUMN_SCALE, kit.getScale());
        cv.put(COLUMN_KIT_NAME, kit.getKit_name());
        cv.put(COLUMN_DESCRIPTION, kit.getDescription());
        cv.put(COLUMN_ORIGINAL_NAME, kit.getKit_noeng_name());

        cv.put(COLUMN_CATEGORY, kit.getCategory());
//        cv.put(COLUMN_COLLECTION, kit.get  + " text," + //коллекция - для группировки и других функций - 9
//        cv.put(COLUMN_SEND_STATUS, kit.getSe + " text," + //для отслеживания офлайн отправок LOCAL - 10
        cv.put(COLUMN_ID_ONLINE, kit.getOnlineId());
        //заметки? LOCAL?
        cv.put(COLUMN_BOXART_URI, kit.getBoxart_uri());
        cv.put(COLUMN_BOXART_URL, kit.getBoxart_url());
//        cv.put(COLUMN_IS_DELETED, kit.get + " int," + // - 14
        cv.put(COLUMN_DATE, kit.getDate_added());
        cv.put(COLUMN_YEAR, kit.getYear());

        cv.put(COLUMN_PURCHASE_DATE, kit.getDatePurchased());
        cv.put(COLUMN_PRICE, kit.getPrice());
        cv.put(COLUMN_QUANTITY, kit.getQuantity());
        cv.put(COLUMN_NOTES, kit.getNotes());
        cv.put(COLUMN_CURRENCY, kit.getCurrency());

        cv.put(COLUMN_PURCHASE_PLACE, kit.getPlacePurchased());

        cv.put(MYLISTSITEMS_LISTNAME, listname);

        mDB.insert(TABLE_MYLISTSITEMS, null, cv);
    }

    public void addListItem(ContentValues cv){
        mDB.insert(TABLE_MYLISTSITEMS, null, cv);
    }

    public Cursor getListItems (String listname, String sortBy){
        return mDB.query(TABLE_MYLISTSITEMS,null,"listname = ?", new String[] {listname},
                null, null, sortBy);
    }

    //Получить запись по идентификатору
    public Cursor getListItemById(long id){
        return mDB.query(TABLE_MYLISTSITEMS, null, "_id = " + id, null, null, null, null);
    }

    /////
    private String makePlaceholders(int len) {// TODO: 05.09.2017 Helper
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }
    //////
    public Cursor getListPackById(String[] id ){
        String query = "SELECT * FROM mylists_items"
                + " WHERE _id IN (" + makePlaceholders(id.length) + ")";
        return mDB.rawQuery(query, id);
    }

    public void editListItemById(long id, ContentValues cv){
        mDB.update(TABLE_MYLISTSITEMS, cv, "_id = ?", new String[] { String.valueOf(id) });
    }

    public void delListItem(long id) {
        mDB.delete(TABLE_MYLISTSITEMS, COLUMN_ID + " = " + id, null);
    }

    public Cursor getAllListsItems(String sortBy) {
        return mDB.query(TABLE_MYLISTSITEMS, null, null, null, null, null, sortBy);
    }

    public void clearList (String listname){
        mDB.delete(TABLE_MYLISTSITEMS, MYLISTSITEMS_LISTNAME + " =?", new String[]{listname});
//        mDB.delete(TABLE_MYLISTSITEMS, MYLISTSITEMS_LISTNAME + " = '" + listname + "'", null);

    }

    public boolean searchListForDoubles(String listname, String bcode){
        boolean found = false;
        if (mDB.query(TABLE_MYLISTSITEMS, new String[]{"listname", "barcode"}, "listname = ? " +
                "AND barcode LIKE ?", new String[]{listname, bcode}, null, null, null)
                .getCount() != 0) {
            found = true;
        }
        return found;
    }

    public boolean searchListForDoubles(String listname, String brand, String cat_no){
        boolean found = false;
        if (mDB.query(TABLE_MYLISTSITEMS, new String[] {"listname", "brand", "brand_catno"},
                "listname = ? "
                        + "AND brand = ? "
                        + "AND brand_catno = ?",
                new String[] {listname, brand, cat_no}, null, null, null)
                .getCount() != 0){
            found = true;
        }
        return found;
    }

    public boolean searchAllListsForFoubles(String barcode, String brand, String catno){
        if (!barcode.equals("")){
            if (mDB.query(TABLE_MYLISTSITEMS, new String[] {"barcode"},
                    "barcode = ?", new String[] {barcode}, null, null, null)
                    .getCount() != 0){
                return true;
            }
        }else{
            if (mDB.query(TABLE_MYLISTSITEMS, new String[] {"brand_catno"},
                    "brand = ? " + "AND brand_catno = ?",
                    new String[] {brand, catno}, null, null, null)
                    .getCount() != 0){
                return true;
            }
        }
        return false;
    }

    //////////////// MYSHOPS ////////////////
    public static ArrayList<String> getAllShops() {
        ArrayList<String> allShops = new ArrayList<>();
        String query = "select shop_name from myshops;";
        Cursor cursor = mDB.rawQuery(query, null);
        while (cursor.moveToNext()) {
            allShops.add(cursor.getString(cursor.getColumnIndex(MYSHOPS_COLUMN_SHOP_NAME)));
        }
        cursor.close();
        return allShops;
    }

    public Cursor getShops(String sortBy){
        return mDB.query(TABLE_MYSHOPS, null, null, null, null, null, sortBy);
    }

    public void addShop(String shopName) { //// TODO: 30.08.2017 Переделать с объектом Shop
        if (!isShopExist(shopName)) {
            ContentValues cv = new ContentValues();
            cv.put(MYSHOPS_COLUMN_SHOP_NAME, shopName);
            cv.put(MYSHOPS_COLUMN_SHOP_RATING, 1);
            cv.put(MYSHOPS_COLUMN_SHOP_DESCRIPTION, "");
            cv.put(MYSHOPS_COLUMN_SHOP_CONTACT, "");
            cv.put(MYSHOPS_COLUMN_SHOP_URL, "");
            mDB.insert(TABLE_MYSHOPS, null, cv);
        }
    }

    public void addShop(ContentValues cv) { //// TODO: 30.08.2017 Переделать с объектом Shop
        mDB.insert(TABLE_MYSHOPS, null, cv);
    }

    public void deleteShopByName(String shopName){
        mDB.delete(TABLE_MYSHOPS, MYSHOPS_COLUMN_SHOP_NAME + " = '" + shopName + "'", null);
    }

    public void delShopById(long id) {
        mDB.delete(TABLE_MYSHOPS, COLUMN_ID + " = " + id, null);
    }

    public void clearMyshops (String shopname){
        mDB.delete(TABLE_MYSHOPS, null, null);
    }

    public void updateShop(long id, String newName) {
        ContentValues cv = new ContentValues(1);
        cv.put(MYSHOPS_COLUMN_SHOP_NAME, newName);
        mDB.update(TABLE_MYSHOPS, cv, "_id = ?", new String[]{String.valueOf(id)});
    }

    private boolean isShopExist(String shopName){
        if (getShop(shopName).getCount() != 0){
            return true;
        }else{
            return false;
        }
    }

    private Cursor getShop(String shopName){
        return mDB.query(TABLE_MYSHOPS, null, "shop_name = ?", new String[] { shopName }, null, null, null);
    }

    private ArrayList<Kit> prepareKit(Cursor cursor) {
        ArrayList<Kit> itemList = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ID));
            String brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME));
            int scale = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE));
            String brandCatno = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO));
            String kitNoengname = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ORIGINAL_NAME));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_DESCRIPTION));
            String sendStatus = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SEND_STATUS));
            String boxartUrl = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URL));
            String boxartUri = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
            String scalematesUrl = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALEMATES_URL));
            String year = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_YEAR));
            String onlineId = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ID_ONLINE));
            String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_DATE));
            String datePurchased = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_DATE));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_QUANTITY));
            String notes = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_NOTES));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PRICE));
            String currency = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CURRENCY));
            String placePurchased = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_PLACE));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_STATUS));
            int media = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_MEDIA));
            String barcode = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BARCODE));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));
            Kit kit = new Kit.KitBuilder()
                    .hasLocalId(id)
                    .hasBrand(brand)
                    .hasBrand_catno(brandCatno)
                    .hasKit_name(name)
                    .hasScale(scale)
                    .hasCategory(category)
                    .hasBarcode(barcode)
                    .hasKit_noeng_name(kitNoengname)
                    .hasDescription(description)
                    .hasPrototype(MyConstants.EMPTY)//not in use
                    .hasSendStatus(sendStatus)
                    .hasBoxart_url(boxartUrl)
                    .hasBoxart_uri(boxartUri)
                    .hasScalemates_url(scalematesUrl)
                    .hasYear(year)
                    .hasOnlineId(onlineId)

                    .hasDateAdded(dateAdded)
                    .hasDatePurchased(datePurchased)
                    .hasQuantity(quantity)
                    .hasNotes(notes)
                    .hasPrice(price)
                    .hasCurrency(currency)
                    .hasPlacePurchased(placePurchased)
                    .hasStatus(status)
                    .hasMedia(media)
                    .hasItemType(MyConstants.TYPE_KIT)
                    .build();
            itemList.add(kit);
            cursor.moveToNext();
        }
        cursor.close();
        return itemList;

    }


    ////////// AFTERMARKET ////////////////
    public void addAfterToKit(long kitId, long aftermarketId){
        ContentValues cv = new ContentValues();
        cv.put(KIT_AFTER_KITID, kitId);
        cv.put(KIT_AFTER_AFTERID, aftermarketId);
        mDB.insert(TABLE_KIT_AFTER_CONNECTIONS, null, cv);
    }

    public void addAftersToKits(ContentValues cv){
        mDB.insert(TABLE_KIT_AFTER_CONNECTIONS, null, cv);
    }

    public ArrayList<Kit> getKitForAfterById(long id) {
        Cursor cursor = mDB.query(TABLE_KIT_AFTER_CONNECTIONS, null,
                "afterid = ?", new String[]{String.valueOf(id)}, null, null, null);
        return prepareKit(cursor);
    }
//    public Cursor getKitForAfterById(long id) {
//        return mDB.query(TABLE_KIT_AFTER_CONNECTIONS, null,
//                "afterid = ?", new String[]{String.valueOf(id)}, null, null, null);
//    }

    public ArrayList<Kit> getAftermarketForKit(long id, String listname) {
        ArrayList<Kit> itemList = new ArrayList<>();
        ArrayList <String> listAft = new ArrayList<>();
        Cursor cursor = mDB.query(TABLE_KIT_AFTER_CONNECTIONS, new String[]{KIT_AFTER_AFTERID},
                KIT_AFTER_KITID + "="+ id, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            listAft.add(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.KIT_AFTER_AFTERID))));
            cursor.moveToNext();
        }

        String[] aftid = new String[listAft.size()];
        if (listAft.size() > 0) {
            aftid = listAft.toArray(aftid);
            String query = "SELECT * FROM " + TABLE_AFTERMARKET + " WHERE listname = '"
                    + listname
                    + "' AND _id IN (" + makePlaceholders(aftid.length) + ")";
            Cursor rawQuery = mDB.rawQuery(query, aftid);
            return prepareKit(rawQuery);

        }else{
            String query = "SELECT * FROM " + TABLE_AFTERMARKET + " WHERE listname = '"
                    + listname
                    + "' AND _id IN (-1)";
            Cursor rawQuery = mDB.rawQuery(query, aftid);
            return prepareKit(rawQuery);
        }
    }

//    public Cursor getAftermarketForKit(long id, String listname){
//        ArrayList <String> listAft = new ArrayList<>();
//        Cursor cursor = mDB.query(TABLE_KIT_AFTER_CONNECTIONS, new String[]{KIT_AFTER_AFTERID},
//                KIT_AFTER_KITID + "="+ id, null, null, null, null);
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()){
//            listAft.add(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.KIT_AFTER_AFTERID))));
//            cursor.moveToNext();
//        }
//
//        String[] aftid = new String[listAft.size()];
//        if (listAft.size() > 0) {
//            aftid = listAft.toArray(aftid);
//            String query = "SELECT * FROM " + TABLE_AFTERMARKET + " WHERE listname = '"
//                    + listname
//                    + "' AND _id IN (" + makePlaceholders(aftid.length) + ")";
//            return mDB.rawQuery(query, aftid);
//        }else{
//            String query = "SELECT * FROM " + TABLE_AFTERMARKET + " WHERE listname = '"
//                    + listname
//                    + "' AND _id IN (-1)";
//            return mDB.rawQuery(query, aftid);
//        }
//    }

    public Cursor getKitAfterConnections(String sortBy){
        return mDB.query(TABLE_KIT_AFTER_CONNECTIONS, null, null, null, null, null, sortBy);
    }



    public long addAftermarket(Aftermarket aftermarket){
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_BARCODE, aftermarket.getBarcode());
        cv.put(COLUMN_BRAND, aftermarket.getBrand());
        cv.put(COLUMN_BRAND_CATNO, aftermarket.getBrandCatno());
        cv.put(COLUMN_SCALE, aftermarket.getScale());
        cv.put(COLUMN_AFTERMARKET_NAME, aftermarket.getAftermarketName());
        cv.put(COLUMN_DESCRIPTION, aftermarket.getDescription());
        cv.put(COLUMN_ORIGINAL_NAME, aftermarket.getAftemarketOriginalName());

        cv.put(COLUMN_CATEGORY, aftermarket.getCategory());
//        cv.put(COLUMN_COLLECTION, kit.get  + " text," + //коллекция - для группировки и других функций - 9
//        cv.put(COLUMN_SEND_STATUS, kit.getSe + " text," + //для отслеживания офлайн отправок LOCAL - 10
        cv.put(COLUMN_ID_ONLINE, aftermarket.getOnlineId());
        //заметки? LOCAL?
        cv.put(COLUMN_BOXART_URI, aftermarket.getBoxartUri());
        cv.put(COLUMN_BOXART_URL, aftermarket.getBoxartUrl());
//        cv.put(COLUMN_IS_DELETED, kit.get + " int," + // - 14
        cv.put(COLUMN_DATE, aftermarket.getDateAdded());
        cv.put(COLUMN_YEAR, aftermarket.getYear());

        cv.put(COLUMN_PURCHASE_DATE, aftermarket.getDatePurchased());
        cv.put(COLUMN_PRICE, aftermarket.getPrice());
        cv.put(COLUMN_QUANTITY, aftermarket.getQuantity());
        cv.put(COLUMN_NOTES, aftermarket.getNotes());
        cv.put(COLUMN_CURRENCY, aftermarket.getCurrency());

        cv.put(COLUMN_PURCHASE_PLACE, aftermarket.getPlacePurchased());
        cv.put(KIT_AFTER_AFTERDESIGNEDFOR, aftermarket.getCompilanceWith());
        cv.put(MYLISTSITEMS_LISTNAME, aftermarket.getListname());


        return mDB.insert(TABLE_AFTERMARKET, null, cv);
    }

    public void addAftermarket(ContentValues cv){
        mDB.insert(TABLE_AFTERMARKET, null, cv);
    }

    public void editAftermarketById (long id, ContentValues cv){
        mDB.update(TABLE_AFTERMARKET, cv, "_id = ?", new String[] { String.valueOf(id) });
    }


    public Cursor getAftermarketByID(long after_id){
        return mDB.query(TABLE_AFTERMARKET, null, "_id = " + after_id, null, null, null, null);
    }


    public void deleteAftermarketFromKit(long kitId, long afterId) {
        mDB.delete(TABLE_KIT_AFTER_CONNECTIONS, KIT_AFTER_KITID + " = ? " +
                        "AND " + KIT_AFTER_AFTERID + " = ? ",
                new String[]{String.valueOf(kitId), String.valueOf(afterId)});
    }

    public void deleteAllAftermarketForKit(long kitId) {
        mDB.delete(TABLE_KIT_AFTER_CONNECTIONS, KIT_AFTER_KITID + " = ? ",
                new String[]{String.valueOf(kitId)});
    }

    public void deleteAftermarketById(long afterId) {
        mDB.delete(TABLE_AFTERMARKET, COLUMN_ID + " = " + afterId, null);
    }

    //++++++++++++++++++++
    public ArrayList<ChooserItem> getAllAftermarket(String sortBy) {
        Cursor cursor = mDB.query(TABLE_AFTERMARKET, null, null, null, null, null, sortBy);
        ArrayList<ChooserItem> itemList = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ChooserItem item = new ChooserItem();
            item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME)));
            item.setBrand(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND)));
            item.setScale(cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE)));
            item.setUri(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI)));
            item.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URL)));
            itemList.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return itemList;
    }
//    public Cursor getAllAftermarket(String sortBy){
//        return mDB.query(TABLE_AFTERMARKET, null, null, null, null, null, sortBy);
//
//    }

    /////////////CURRENCY//////////////////

    public void addCurrency(String cur) {
        ContentValues cv = new ContentValues();
        cv.put(CURRENCIES_COLUMN_CURRENCY, cur);
        mDB.insert(TABLE_CURRENCIES, null, cv);
    }

//    public Cursor getAllCurrencies(){
//        return
//    }

    //////////// VIEW STASH PAGER //////////////
    public Cursor getActiveCategories() {
//        return mDB.query(TABLE_CATEGORIES, null, "count >? ", new String[]{"0"}, null, null, "count DESC");
//        return mDB.rawQuery("SELECT * FROM " + TABLE_CATEGORIES  + " WHERE count > 0 ORDER BY count DESC", null);
        return mDB.rawQuery("SELECT category, count(*) FROM kits GROUP BY category HAVING count(*) > 0 ORDER BY count(*) DESC", null);

//                return mDB.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);

    }

    public void updateCategories() {
        mDB.rawQuery(
                "UPDATE " + TABLE_CATEGORIES + " SET count = " + countCategory("1") + " WHERE name = '1';"
                        + "UPDATE " + TABLE_CATEGORIES + " SET count = " + countCategory("2") + " WHERE name = '2';"
                        + "UPDATE " + TABLE_CATEGORIES + " SET count = " + countCategory("3") + " WHERE name = '3';"
                        + "UPDATE " + TABLE_CATEGORIES + " SET count = " + countCategory("4") + " WHERE name = '4';"
                        + "UPDATE " + TABLE_CATEGORIES + " SET count = " + countCategory("5") + " WHERE name = '5';"
                        + "UPDATE " + TABLE_CATEGORIES + " SET count = " + countCategory("6") + " WHERE name = '6';"
                        + "UPDATE " + TABLE_CATEGORIES + " SET count = " + countCategory("7") + " WHERE name = '7';"
                        + "UPDATE " + TABLE_CATEGORIES + " SET count = " + countCategory("8") + " WHERE name = '8';"
                , null);
    }

    private int countCategory(String category) {
        return mDB.rawQuery("SELECT _id FROM " + TABLE_KITS + " WHERE category = "
                + category + ";", null).getCount();
    }

    public Cursor getAfterActiveCategories() {
        return mDB.rawQuery("SELECT category, count(*) FROM aftermarket GROUP BY category HAVING count(*) > 0 ORDER BY count(*)", null);
    }

    ////////////////ПРОВЕРКИ ПРИ ДОБАВЛЕНИИ////////////////////

    //Для проверки из сканирования ScanActivity
    public boolean searchForDoubles(String bcode){
        boolean found = false;
        if (mDB.query(TABLE_KITS, null, "barcode LIKE ?", new String[]{bcode}, null, null, null)
                .getCount() != 0){
            found = true;
        }
        return found;
    }

    //Для проверки из ручного добавления AddActivity //////!!!!!!!!!!!!!!!!!!!!!Univ
    public boolean searchForDoubles(String brand, String cat_no){
        boolean found = false;
        if (mDB.query(TABLE_KITS, new String[] {"brand", "brand_catno"},"brand = ? " +
                "AND brand_catno = ?", new String[] {brand, cat_no}, null, null, null)
                .getCount() != 0){
            found = true;
        }
        return found;
    }

    public void checkDbForNulls() {
        Cursor cursor = mDB.query(TABLE_KITS, null,"brand is null OR brand_catno is null",
                null, null, null, null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            while (cursor.moveToNext()){
                mDB.delete(TABLE_KITS, COLUMN_ID + " = " + cursor.getColumnIndex("_id"), null);
            }
            vacuumDb();
        }
        cursor.close();
    }


    //////////////////////////////////////////////////////

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {


            // создаем таблицу с полями
            db.execSQL(CREATE_TABLE_KITS);
            //Таблица брэндов и заполнение списка
            db.execSQL(CREATE_TABLE_BRANDS);
            db.execSQL(INIT_TABLE_BRANDS);
//            db.execSQL(CREATE_TABLE_CATEGORY);
//            db.execSQL(INIT_TABLE_CATEGORY);
            db.execSQL(CREATE_TABLE_CATEGORIES);
            db.execSQL(INIT_TABLE_CATEGORIES);
            db.execSQL(CREATE_TABLE_STATISTIC);
            db.execSQL(CREATE_TABLE_ACCOUNT);
            db.execSQL(CREATE_TABLE_MYLISTS);
            db.execSQL(CREATE_TABLE_MYLISTSITEMS);

            db.execSQL(CREATE_TABLE_CURRENCIES);
            db.execSQL(INIT_TABLE_CURRENCIES);
            db.execSQL(CREATE_TABLE_MYSHOPS);

            db.execSQL(CREATE_TABLE_AFTERMARKET);
            db.execSQL(CREATE_TABLE_AFTERMARKET_MYLISTITEMS);
            db.execSQL(CREATE_TABLE_KIT_AFTER_CONNECTIONS);
            db.execSQL(CREATE_TABLE_BRAND_BARCODE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}