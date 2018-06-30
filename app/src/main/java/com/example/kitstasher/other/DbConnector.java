package com.example.kitstasher.other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.kitstasher.R;
import com.example.kitstasher.objects.BrandItem;
import com.example.kitstasher.objects.CategoryItem;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.objects.PaintItem;
import com.example.kitstasher.objects.ShopItem;

import java.util.ArrayList;

public class DbConnector {

    private static final String DB_NAME = "myscalestash";
    private static final int DB_VERSION = 1;


    private final Context context;
    private static DBHelper mDBHelper;
    private static SQLiteDatabase mDB;


    public DbConnector(Context context) {
        super();
        this.context = context;
    }

    public void open() {
        mDBHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDB != null && mDB.isOpen())
            try {
                mDB.close();
                mDBHelper.close();
            } catch (NullPointerException e) {
                Log.e("Close", "Error: " + e + " " + e.getMessage());
            }
        else{
            Log.e("Close", "Error! db is null.");
            mDBHelper.close();
        }
    }


    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                 int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("PRAGMA auto_vacuum = 2;");

            db.execSQL(CREATE_TABLE_KITS);

            db.execSQL(CREATE_TABLE_BRANDS);
            db.execSQL(INIT_TABLE_BRANDS);

            db.execSQL(CREATE_TABLE_CURRENCIES);
            db.execSQL(INIT_TABLE_CURRENCIES);

            db.execSQL(CREATE_TABLE_MYSHOPS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

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

    public static final String COLUMN_ITEMTYPE = "item_type";

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

    ///////// TABLE CURRENCIES ///////
    public static final String TABLE_CURRENCIES = "currencies";
    public static final String CURRENCIES_COLUMN_CURRENCY = "currency";

    //////// TABLE PAINTS //////////
    public static final String TABLE_PAINTS = "paints";
    public static final String PAINTS_NAME = "name";
    public static final String PAINTS_CODE = "code";
    public static final String PAINTS_NOTES = "notes";

    //////////////////////////////////
    ///////// CREATE SCRIPTS /////////
    /////////////////////////////////




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
                    COLUMN_MEDIA + " text," + //материал - 25
                    COLUMN_ITEMTYPE + " text" + //тип набор/афтермаркет - 26
                    ");";

    private static final String CREATE_TABLE_BRANDS =
            "CREATE TABLE " + TABLE_BRANDS + "(" +
                    BRANDS_COLUMN_ID + " integer primary key autoincrement, " +
                    BRANDS_COLUMN_BRAND + " text" + //Список фирм-производителей для автодополнения
                    // в AddActivity
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


    //////////////KITS//////////////

    public void clearTable(String table)   {
        mDB.delete(table, null,null);
    }

    //    все из таблицы
    public Cursor getAllFromTable(String tableName, String sortBy) {
        return mDB.query(tableName, null, null, null, null, null, sortBy);
    }

    //Редактирование записи
    public void editItemById(String tableName, long id, ContentValues cv) {
        //String clause = String.valueOf(id);
        mDB.update(tableName, cv, "_id = ?", new String[]{String.valueOf(id)});
    }

    public boolean searchForDoubles(String tableName, String brand, String cat_no) {
        boolean found = false;
        String having = "";
        if (mDB.query(tableName, new String[]{"brand", "brand_catno"}, "brand = ? " +
                "AND brand_catno = ?", new String[]{brand, cat_no}, null, having, null)
                .getCount() != 0) {
            found = true;
        }
        return found;
    }

    //Все с сортировкой!!!!!!!!!!!!!!!!!!!!!
    //Used in statistics
    public int countAllKits(String sortBy) {
        return mDB.query(TABLE_KITS, null, null, null, null, null, sortBy).getCount();
    }

    // Все по дате
    public int getAllForDate(String date) {
        return mDB.query(TABLE_KITS,null,"date = ?", new String[] {date}, null, null, null).getCount();
    }

    // Все по категории
    public Cursor getByTag(String tag) {
        return mDB.query(TABLE_KITS,null,"category = ? and (is_deleted is null or is_deleted =?)",
                new String[] {tag,""}, null, null, null);
    }



    //Редактирование записи!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public void editRecById(long id, ContentValues cv){
        mDB.update(TABLE_KITS, cv, "_id = ?", new String[] { String.valueOf(id) });
    }

    public void editKit(String table, Kit kit){
        ContentValues cv = new ContentValues();
        String id = String.valueOf(kit.getLocalId());
        cv.put(DbConnector.COLUMN_BRAND, kit.getBrand());
        cv.put(DbConnector.COLUMN_KIT_NAME, kit.getKit_name());
        cv.put(DbConnector.COLUMN_BRAND_CATNO, kit.getBrandCatno());
        cv.put(DbConnector.COLUMN_SCALE, kit.getScale());
        cv.put(DbConnector.COLUMN_PURCHASE_PLACE, kit.getPlacePurchased());
        cv.put(DbConnector.COLUMN_PURCHASE_DATE, kit.getDatePurchased());
        cv.put(DbConnector.COLUMN_ORIGINAL_NAME, kit.getKit_noeng_name());
        cv.put(DbConnector.COLUMN_PRICE, kit.getPrice());
        cv.put(DbConnector.COLUMN_NOTES, kit.getNotes());
        cv.put(DbConnector.COLUMN_BOXART_URI, kit.getBoxart_uri());
        cv.put(DbConnector.COLUMN_CATEGORY, kit.getCategory());
        cv.put(DbConnector.COLUMN_YEAR, kit.getYear());
        cv.put(DbConnector.COLUMN_DESCRIPTION, kit.getDescription());
        cv.put(DbConnector.COLUMN_QUANTITY, kit.getQuantity());
        cv.put(DbConnector.COLUMN_CURRENCY, kit.getCurrency());
        cv.put(DbConnector.COLUMN_PURCHASE_PLACE, kit.getPlacePurchased());
        cv.put (DbConnector.COLUMN_STATUS, kit.getStatus());
        cv.put(DbConnector.COLUMN_MEDIA, kit.getMedia());
        cv.put(DbConnector.COLUMN_BOXART_URL, kit.getBoxart_url());

        mDB.update(table, cv, "_id = ?", new String[] { id });
    }

    // Добавление записи в KITS
    public long addKitRec(Kit kit, String tableName) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BARCODE, kit.getBarcode());
        cv.put(COLUMN_BRAND, kit.getBrand());
        cv.put(COLUMN_BRAND_CATNO, kit.getBrandCatno());
        cv.put(COLUMN_SCALE, kit.getScale());
        cv.put(COLUMN_KIT_NAME, kit.getKit_name());
        cv.put(COLUMN_DESCRIPTION, kit.getDescription());
        cv.put(COLUMN_ORIGINAL_NAME, kit.getKit_noeng_name());
        cv.put(COLUMN_CATEGORY, kit.getCategory());
        cv.put(COLUMN_ID_ONLINE, kit.getOnlineId());
        cv.put(COLUMN_BOXART_URI, kit.getBoxart_uri());
        cv.put(COLUMN_BOXART_URL, kit.getBoxart_url());
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
        cv.put(COLUMN_ITEMTYPE, kit.getItemType());
        return mDB.insert(tableName, null, cv);
    }

    public void delRec(String tableName, long id) {
        mDB.delete(tableName, COLUMN_ID + " = " + id, null);
    }


    public ArrayList<Kit> filteredKits(String tableName, String category, String sortBy) {
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
            case "0":
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_OTHER + "'";
                break;
            default:
                having = null;
                break;
        }
//        having = having + " AND item_type = "+tableName;
        Cursor cursor = mDB.query(TABLE_KITS, null, "item_type = ? ", new String[]{tableName}, groupBy, having, sortBy);
        itemList = prepareKit(cursor);
        return itemList;
    }



    //////////////ТАБЛИЦА BRANDS ////////////////

    public ArrayList<BrandItem> getBrands(String sortBy) {
        ArrayList<BrandItem> items = new ArrayList<>();
        Cursor cursor = mDB.query(TABLE_BRANDS, null, null, null, null, null, sortBy);
        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                BrandItem item = new BrandItem();
                item.setLocalId(cursor.getLong(cursor.getColumnIndexOrThrow(BRANDS_COLUMN_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(BRANDS_COLUMN_BRAND)));
                items.add(item);
                cursor.moveToNext();
            }
            return items;
        }finally{
            if(cursor != null) {
                cursor.close();
            }
        }
    }

    public void addBrand(String brand) {
        ContentValues cv = new ContentValues();
        cv.put(BRANDS_COLUMN_BRAND, brand);
        mDB.insert(TABLE_BRANDS, null, cv);
    }

    public static ArrayList<String> getAllBrands() {
        ArrayList<String> allBrands = new ArrayList<>();
        String query = "select brand from brands;";
        Cursor cursor = mDB.rawQuery(query, null);
        try{
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                allBrands.add(cursor.getString(cursor.getColumnIndex(COLUMN_BRAND)));
            }
            return allBrands;
        }finally{
            if(cursor != null) {
                cursor.close();
            }
        }
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

//    public boolean isBrandExists(String brandname) {
//        return getBrand(brandname).getCount() != 0;
//    }

//    public Cursor getBrand(String brandname) {
//        return mDB.query(TABLE_BRANDS, null, "brand = ?", new String[]{brandname}, null, null, null);
//    }

    public void updateBrand(long id, String newName) {
        ContentValues cv = new ContentValues();
        cv.put(BRANDS_COLUMN_BRAND, newName);
        mDB.update(TABLE_BRANDS, cv, "_id=?", new String[]{String.valueOf(id)});
    }


    // удалить запись из TABLE_BRANDS
    public void delBrand(long id) {
        mDB.delete(TABLE_BRANDS, COLUMN_ID + " =? ", new String[]{String.valueOf(id)});

    }


    //////////////// SHOPS ////////////////

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

    public ArrayList<ShopItem> getShops(String sortBy){
        ArrayList<ShopItem> items = new ArrayList<>();
        Cursor cursor = mDB.query(TABLE_MYSHOPS, null, null, null, null, null, sortBy);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            ShopItem item = new ShopItem();
            item.setLocalId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            item.setName(cursor.getString(cursor.getColumnIndexOrThrow(MYSHOPS_COLUMN_SHOP_NAME)));
            items.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return items;
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
        return getShop(shopName).getCount() != 0;
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


    /////////////CURRENCY//////////////////

    public void addCurrency(String cur) {
        ContentValues cv = new ContentValues();
        cv.put(CURRENCIES_COLUMN_CURRENCY, cur);
        mDB.insert(TABLE_CURRENCIES, null, cv);
    }

    //////////// VIEW STASH BOTTOM SHEET //////////////
    public ArrayList<CategoryItem> getActiveCategories(String workMode) {
        ArrayList<CategoryItem> list = new ArrayList<>();
        CategoryItem firstItem = new CategoryItem();
        firstItem.setName("");
        firstItem.setLogoResource(R.drawable.ic_check_black_24dp);
        firstItem.setQuantity(countAllKits(MyConstants._ID));
        list.add(firstItem);
        Cursor cursor = mDB.rawQuery("SELECT category, count(*) as count FROM kits WHERE item_type is " + workMode + " GROUP BY category HAVING count(*) > 0 ORDER BY count(*) DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            CategoryItem item = new CategoryItem();
            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            item.setName(category);
            item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("count")));
            switch (category){
                case "0":
                    item.setLogoResource(R.drawable.ic_help_black_24dp);
                    break;
                case "1":
                    item.setLogoResource(R.drawable.ic_tag_air_black_24dp);
                    break;
                case "2":
                    item.setLogoResource(R.drawable.ic_tag_afv_black_24dp);
                    break;
                case "3":
                    item.setLogoResource(R.drawable.ic_tag_ship_black_24dp);
                    break;
                case "4":
                    item.setLogoResource(R.drawable.ic_tag_space_black_24dp);
                    break;
                case "5":
                    item.setLogoResource(R.drawable.ic_directions_car_black_24dp);
                    break;
                case "6":
                    item.setLogoResource(R.drawable.ic_wc_black_24dp);
                    break;
                case "7":
                    item.setLogoResource(R.drawable.ic_android_black_24dp);
                    break;

                case "100":
                    break;
                case "101":
                    break;
                case "102":
                    break;
                    
//                case "8":
//                    item.setLogoResource(R.drawable.ic_help_black_24dp);
//                    break;
                default:
                    item.setLogoResource(R.drawable.ic_check_black_24dp);
                    break;

            }
            list.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

//    private int countCategory(String category) {
//        return mDB.rawQuery("SELECT _id FROM " + TABLE_KITS + " WHERE category = "
//                + category + ";", null).getCount();
//    }

    public ArrayList<CategoryItem> getAfterActiveCategories(String workMode) {
        ArrayList<CategoryItem> list = new ArrayList<>();
        Cursor cursor = mDB.rawQuery("SELECT category, count(*) as count FROM kits WHERE item_type is " + workMode + " GROUP BY category HAVING count(*) > 0 ORDER BY count(*)", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            CategoryItem item = new CategoryItem();
            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            item.setName(category);
            item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("count")));
            switch (category){
                case "1":
                    item.setLogoResource(R.drawable.ic_tag_air_black_24dp);
                    break;
                case "2":
                    item.setLogoResource(R.drawable.ic_tag_afv_black_24dp);
                    break;
                case "3":
                    item.setLogoResource(R.drawable.ic_tag_ship_black_24dp);
                    break;
                case "4":
                    item.setLogoResource(R.drawable.ic_tag_space_black_24dp);
                    break;
                case "5":
                    item.setLogoResource(R.drawable.ic_directions_car_black_24dp);
                    break;
                case "6":
                    item.setLogoResource(R.drawable.ic_wc_black_24dp);
                    break;
                case "7":
                    item.setLogoResource(R.drawable.ic_android_black_24dp);
                    break;
                case "0":
                    item.setLogoResource(R.drawable.ic_help_black_24dp);
                    break;
                default:
                    break;
            }
            list.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }


    ////////// PAINTS //////////

    public void addPaint(PaintItem paintItem) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BRAND_CATNO, paintItem.getcolorCode());
        cv.put(COLUMN_KIT_NAME, paintItem.getColorName());
        cv.put(COLUMN_BRAND, paintItem.getBrand());
        cv.put(COLUMN_BOXART_URL, paintItem.getUrl());
        cv.put(COLUMN_BOXART_URI, paintItem.getUri());
        cv.put(COLUMN_ITEMTYPE, MyConstants.TYPE_PAINT);

        mDB.insertOrThrow(TABLE_KITS, null, cv);
    }

    /////////////// HELPER METHODS /////////////////////////////

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
}