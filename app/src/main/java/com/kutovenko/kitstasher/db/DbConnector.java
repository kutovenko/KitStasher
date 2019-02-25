package com.kutovenko.kitstasher.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.kutovenko.kitstasher.model.BrandItem;
import com.kutovenko.kitstasher.model.CategoryItem;
import com.kutovenko.kitstasher.model.StashItem;
import com.kutovenko.kitstasher.model.ShopItem;
import com.kutovenko.kitstasher.util.MyConstants;

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

    /**
     * TABLE KITS
     * Содержит записи о всех объектах коллекции: наборах, афтермаркете, красках. Они отличаются
     * кодами COLUMN_ITEMTYPE. Имена полей унифицированы с именами полей в облачном хранилище.
     * Синхронизируется с облаком.
     */

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
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_CURRENCY = "currency";
    public static final String COLUMN_PURCHASE_PLACE = "purchasePlace";

    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_MEDIA = "media";
    public static final String COLUMN_SCALEMATES_URL = "scalemates";

    public static final String COLUMN_ITEMTYPE = "item_type";

    /**
     * TABLE BRANDS
     * Содержит записи о брэндах. Они используются в автозаполнении. Есть стартовый набор, который
     * может быть дополнен пользователем.
     * Не синхронизируется с облаком.
     */

    public static final String TABLE_BRANDS = "brands";

    public static final String BRANDS_COLUMN_ID = "_id";
    public static final String BRANDS_COLUMN_BRAND = "brand";

    /**
     * TABLE MYSHOPS
     * Список источников
     * Не синхронизируется с облаком.
     */
    public static final String TABLE_MYSHOPS = "myshops";
    public static final String MYSHOPS_COLUMN_SHOP_NAME = "shop_name";
    public static final String MYSHOPS_COLUMN_SHOP_DESCRIPTION = "shop_desc";
    public static final String MYSHOPS_COLUMN_SHOP_URL = "shop_url";
    public static final String MYSHOPS_COLUMN_SHOP_RATING = "shop_rating";
    public static final String MYSHOPS_COLUMN_SHOP_CONTACT = "shop_contact";

    /**
     * TABLE CURRENCIES
     * Перечень валют. Используется в настройках и формах редактирования.
     * Не синхронизируется с облаком.
     */
    public static final String TABLE_CURRENCIES = "currencies";
    public static final String CURRENCIES_COLUMN_CURRENCY = "currency";


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
                    COLUMN_CATEGORY + " text," + //категория (самолет, корабль, и тд - 8
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
                    COLUMN_ITEMTYPE + " text" + //тип объекта набор/афтермаркет/краска/декаль - 26
                    ");";

    private static final String CREATE_TABLE_BRANDS =
            "CREATE TABLE " + TABLE_BRANDS + "(" +
                    BRANDS_COLUMN_ID + " integer primary key autoincrement, " +
                    BRANDS_COLUMN_BRAND + " text" + //Список фирм-производителей для автодополнения
                    // в AddActivity
                    ");";

    //////////////////////////////////
    ////////// INIT SCRIPTS /////////
    /////////////////////////////////

    private static final String INIT_TABLE_BRANDS =
            "INSERT INTO " + TABLE_BRANDS + "(" + BRANDS_COLUMN_BRAND + ")" +
                    " SELECT 'Academy' AS " + BRANDS_COLUMN_BRAND +
                    " UNION SELECT 'Academy Minicraft'" +
                    " UNION SELECT 'Accu-Scale'" +
                    " UNION SELECT 'ACE'" +
                    " UNION SELECT 'AFV Club'" +
                    " UNION SELECT 'Airfix'" +
                    " UNION SELECT 'AJM Models'" +
                    " UNION SELECT 'AK Interactive'" +
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
                    " UNION SELECT 'Citadel'" +
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
                    " UNION SELECT 'MIG'" +
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
                    " UNION SELECT 'Vallejo'" +
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
                    " SELECT 'USD' AS " + CURRENCIES_COLUMN_CURRENCY +
                    " UNION SELECT 'EUR';";


    /////////UNIVERSAL//////////////

    /**
     * Method for clearing the given table.
     *
     * @param table table name.
     */
    public void clearTable(String table)   {
        mDB.delete(table, null,null);
    }

    /**
     * Method for editing record by _id in given table.
     *
     * @param tableName table name.
     * @param id record id.
     * @param cv new content for the record.
     */
    public void editItemById(String tableName, long id, ContentValues cv) {
        mDB.update(tableName, cv, "_id = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Method for counting records in given table.
     *
     * @param tableName table name.
     * @return quantity of the records. Used in statistics.
     */
    public int countAllRecords(String tableName) {
        return mDB.query(tableName, null,
                null, null, null, null, null).getCount();
    }

    /**
     * Method for counting records in given table.
     *
     * @param itemType type of items to count
     * @return quantity of the records. Used in categories bottom sheet.
     */
    public int countAllRecordsByType(String itemType) {
        return mDB.query(TABLE_KITS, null,
                "item_type = ?", new String[]{itemType}, null, null, null).getCount();
    }

    ////////////// KITS //////////////

    /**
     * Method for searching if the given barcode is already presented in DB.
     *
     * @param bcode full or partial barcode.
     * @return true if barcode is already presented in DB.
     */
    public int isItemDuplicate(String bcode){
        return mDB.query(TABLE_KITS, null,
                "barcode LIKE ? ", new String[]{bcode + "%"}, null, null, null)
                .getCount();
    }

    /**
     * Method for searching if the given combination of brand and brand cat. number
     * is already presented in DB.
     *
     * @param brand brand name.
     * @param cat_no catalogue No.
     * @return true if item is already exists in DB.
     */
    public boolean isItemDuplicate(String brand, String cat_no){
        boolean found = false;
        if (mDB.query(TABLE_KITS, new String[] {"brand", "brand_catno"},"brand = ? " +
                "AND brand_catno = ?", new String[] {brand, cat_no}, null, null, null)
                .getCount() != 0){
            found = true;
        }
        return found;
    }

    /**
     * Method for counting number of records for given date. Used in statistics.
     *
     * @param date date to search.
     * @return number of records for given date.
     */
    public int countForDate(String date) {
        return mDB.query(TABLE_KITS,null,"date = ?", new String[] {date}, null, null, null).getCount();
    }

    /**
     * Method for counting number of records for given category. Used in statistics.
     *
     * @param category category name.
     * @return number of records for given category.
     */
    public int countForTag(String category) {
        return mDB.query(TABLE_KITS,null,"category = ? and (is_deleted is null or is_deleted =?)",
                new String[] {category,""}, null, null, null).getCount();
    }


    /**
     * Method for editing record in TABLE_KITS with given CV
     *
     * @param id the record ID
     * @param cv the values to write.
     * @return true if update was successful.
     */
    public boolean editItemById(long id, ContentValues cv){
        return mDB.update(TABLE_KITS, cv, "_id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    /**
     * Method for updating item record in local database.
     *
     * @param stashItem the new StashItem object to write.
     * @return true if update was successful.
     */
    public boolean editItem(StashItem stashItem){
        ContentValues cv = new ContentValues();
        String id = String.valueOf(stashItem.getLocalId());
        cv.put(DbConnector.COLUMN_BRAND, stashItem.getBrand());
        cv.put(DbConnector.COLUMN_KIT_NAME, stashItem.getName());
        cv.put(DbConnector.COLUMN_BRAND_CATNO, stashItem.getBrandCatno());
        cv.put(DbConnector.COLUMN_SCALE, stashItem.getScale());
        cv.put(DbConnector.COLUMN_PURCHASE_PLACE, stashItem.getPlacePurchased());
        cv.put(DbConnector.COLUMN_PURCHASE_DATE, stashItem.getDatePurchased());
        cv.put(DbConnector.COLUMN_ORIGINAL_NAME, stashItem.getNoengName());
        cv.put(DbConnector.COLUMN_PRICE, stashItem.getPrice());
        cv.put(DbConnector.COLUMN_NOTES, stashItem.getNotes());
        cv.put(DbConnector.COLUMN_BOXART_URI, stashItem.getBoxartUri());
        cv.put(DbConnector.COLUMN_CATEGORY, stashItem.getCategory());
        cv.put(DbConnector.COLUMN_YEAR, stashItem.getYear());
        cv.put(DbConnector.COLUMN_DESCRIPTION, stashItem.getDescription());
        cv.put(DbConnector.COLUMN_QUANTITY, stashItem.getQuantity());
        cv.put(DbConnector.COLUMN_CURRENCY, stashItem.getCurrency());
        cv.put(DbConnector.COLUMN_PURCHASE_PLACE, stashItem.getPlacePurchased());
        cv.put (DbConnector.COLUMN_STATUS, stashItem.getStatus());
        cv.put(DbConnector.COLUMN_MEDIA, stashItem.getMedia());
        cv.put(DbConnector.COLUMN_BOXART_URL, stashItem.getBoxartUrl());
        return mDB.update(TABLE_KITS, cv, "_id = ?", new String[] { id }) > 0;
    }

    /**
     * Method for adding item record to local database.
     *
     * @param stashItem StashItem object to write.
     * @return local ID of new record.
     */
    public long addItem(StashItem stashItem) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BARCODE, stashItem.getBarcode());
        cv.put(COLUMN_BRAND, stashItem.getBrand());
        cv.put(COLUMN_BRAND_CATNO, stashItem.getBrandCatno());
        cv.put(COLUMN_SCALE, stashItem.getScale());
        cv.put(COLUMN_KIT_NAME, stashItem.getName());
        cv.put(COLUMN_DESCRIPTION, stashItem.getDescription());
        cv.put(COLUMN_ORIGINAL_NAME, stashItem.getNoengName());
        cv.put(COLUMN_CATEGORY, stashItem.getCategory());
        cv.put(COLUMN_ID_ONLINE, stashItem.getOnlineId());
        cv.put(COLUMN_BOXART_URI, stashItem.getBoxartUri());
        cv.put(COLUMN_BOXART_URL, stashItem.getBoxartUrl());
        cv.put(COLUMN_DATE, stashItem.getDate_added());
        cv.put(COLUMN_YEAR, stashItem.getYear());
        cv.put(COLUMN_SCALEMATES_URL, stashItem.getScalematesUrl());
        cv.put(COLUMN_PURCHASE_DATE, stashItem.getDatePurchased());
        cv.put(COLUMN_PRICE, stashItem.getPrice());
        cv.put(COLUMN_QUANTITY, stashItem.getQuantity());
        cv.put(COLUMN_NOTES, stashItem.getNotes());
        cv.put(COLUMN_CURRENCY, stashItem.getCurrency());
        cv.put(COLUMN_SEND_STATUS, stashItem.getSendStatus());
        cv.put(COLUMN_PURCHASE_PLACE, stashItem.getPlacePurchased());
        cv.put(COLUMN_STATUS, stashItem.getStatus());
        cv.put(COLUMN_ITEMTYPE, stashItem.getItemType());
        cv.put(COLUMN_MEDIA, stashItem.getMedia());
        return mDB.insert(TABLE_KITS, null, cv);
    }

    /**
     * Method for deleting item record.
     *
     * @param id ID of the record to delete.
     */
    public void deleteItem(long id) {
        mDB.delete(TABLE_KITS, COLUMN_ID + " = " + id, null);
    }

    /**
     * Method for getting the list of items from the database based on given filter.
     *
     * @param category category filter.
     * @param sortBy field to sort by.
     * @return list of selected items.
     */

    public ArrayList<StashItem> filteredKits(String itemType, String category, String sortBy) {
        ArrayList<StashItem> itemList;
        String groupBy = "_id";
        String having;
        switch (category) {
            case MyConstants.CODE_OTHER:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_OTHER + "'";
                break;
            // Kits categories
            case MyConstants.CODE_AIR:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_AIR + "'";
                break;
            case MyConstants.CODE_GROUND:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_GROUND + "'";
                break;
            case MyConstants.CODE_SEA:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_SEA + "'";
                break;
            case MyConstants.CODE_SPACE:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_SPACE + "'";
                break;
            case MyConstants.CODE_AUTOMOTO:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_AUTOMOTO + "'";
                break;
            case MyConstants.CODE_FIGURES:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_FIGURES + "'";
                break;
            case MyConstants.CODE_FANTASY:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_FANTASY + "'";
                break;
            //Supplies categories
            case MyConstants.CODE_P_ACRYLLIC:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_P_ACRYLLIC + "'";
                break;
            case MyConstants.CODE_P_ENAMEL:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_P_ENAMEL + "'";
                break;
            case MyConstants.CODE_P_OIL:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_P_OIL + "'";
                break;
            case MyConstants.CODE_P_LACQUER:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_P_LACQUER + "'";
                break;
            case MyConstants.CODE_P_THINNER:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_P_THINNER + "'";
                break;
            case MyConstants.CODE_P_GLUE:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_P_GLUE + "'";
                break;
            case MyConstants.CODE_P_DECAL_SET:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_P_DECAL_SET + "'";
                break;
            case MyConstants.CODE_P_DECAL_SOL:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_P_DECAL_SOL + "'";
                break;
            case MyConstants.CODE_P_PIGMENT:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_P_PIGMENT + "'";
                break;
            case MyConstants.CODE_P_COLORSTOP:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_P_COLORSTOP + "'";
                break;
            case MyConstants.CODE_P_FILLER:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_P_FILLER + "'";
                break;
            case MyConstants.CODE_P_PRIMER:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.CODE_P_PRIMER + "'";
                break;
            //Aftermarket categories
            case MyConstants.M_CODE_ADDON:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.M_CODE_ADDON + "'";
                break;
            case MyConstants.M_CODE_PHOTOETCH:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.M_CODE_PHOTOETCH + "'";
                break;
            case MyConstants.M_CODE_DECAL:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.M_CODE_DECAL + "'";
                break;
            case MyConstants.M_CODE_MASK:
                having = DbConnector.COLUMN_CATEGORY + " = '" + MyConstants.M_CODE_MASK + "'";
                break;

            default:
                having = null;
                break;
        }
        if (!itemType.equals(MyConstants.TYPE_ALL)){
        Cursor cursor = mDB.query(TABLE_KITS, null, "item_type = ? ",
                new String[]{itemType}, groupBy, having, sortBy);

        itemList = prepareKit(cursor);
        cursor.close();

        return itemList;
        } else {
            Cursor cursor = mDB.query(TABLE_KITS, null, null,
                    null, groupBy, having, sortBy);

            itemList = prepareKit(cursor);
            cursor.close();

            return itemList;
        }

    }


    public ArrayList<StashItem> allFilteredKits() {
        ArrayList<StashItem> itemList;
        String groupBy = "_id";

        Cursor cursor = mDB.query(TABLE_KITS, null, null,
                null, groupBy, null, "_id");
        itemList = prepareKit(cursor);
        cursor.close();
        return itemList;
    }

    /**
     * Method for getting the list of brands.
     *
     * @param sortBy field to sort by.
     * @return list of all brands sorted by given field.
     */
    public ArrayList<BrandItem> getAllBrands(String sortBy) {
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
        } finally {
            if(cursor != null) cursor.close();
        }
    }

    /**
     * Method for receiving array of brand names ordered by quantity of items of this brand in local DB.
     *
     * @return ordered array of brands names.
     */
    public String[] getSortedBrandsCounts(String itemType) {

        Cursor cursor = mDB.rawQuery("SELECT DISTINCT brand, COUNT(brand) as count FROM kits " +
                "WHERE item_type is " + itemType + " GROUP BY brand ORDER BY count", null);
        String[] result = new String[cursor.getCount()];
        try {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                result[i] = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_BRAND));
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return result;
    }

    /**
     * Method for getting all brands records.
     * @return list of all brands in DB.
     */
    public ArrayList<String> getBrandsNames() {
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
            if(cursor != null) cursor.close();
        }
    }

    /**
     * Method for adding new brand.
     * @param brand name of the new brand
     */
    public void addBrand(String brand) {
        ContentValues cv = new ContentValues();
        cv.put(BRANDS_COLUMN_BRAND, brand);
        mDB.insert(TABLE_BRANDS, null, cv);
    }

    /**
     * Method for counting kits of given brand in local DB.
     *
     * @param brand brand name.
     * @return number of kits.
     */
    public int countKitsOfBrand(String brand) {
        return mDB.query(TABLE_KITS, null, "brand = ? and (is_deleted is null or is_deleted =?)",
                new String[] {brand, ""}, null, null, null).getCount();
    }

    /**
     * Method for updating brand name in local DB.
     *
     * @param id ID of brand record.
     * @param newName new name for brand.
     */
    public void updateBrand(long id, String newName) {
        ContentValues cv = new ContentValues();
        cv.put(BRANDS_COLUMN_BRAND, newName);
        mDB.update(TABLE_BRANDS, cv, "_id=?", new String[]{String.valueOf(id)});
    }

    /**
     * Method for deleting brand record from local DB.
     *
     * @param id ID of brand record.
     */
    public void deleteBrand(long id) {
        mDB.delete(TABLE_BRANDS, COLUMN_ID + " =? ", new String[]{String.valueOf(id)});

    }


    //////////////// SHOPS ////////////////

    /**
     * Method for getting the list of all shop names from local DB.
     *
     * @return list of all shop names from local DB.
     */
    public ArrayList<String> getShopNames() {

        ArrayList<String> allShops = new ArrayList<>();
        String query = "select shop_name from myshops;";
        Cursor cursor = mDB.rawQuery(query, null);
        try {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                allShops.add(cursor.getString(cursor.getColumnIndex(MYSHOPS_COLUMN_SHOP_NAME)));
            }
            return allShops;
        } finally {
            if(cursor != null) cursor.close();
        }
    }

    /**
     * Method for getting the list of all shop items from local DB.
     *
     * @return list of all shop items from local DB.
     */
    public ArrayList<ShopItem> getShops(String sortBy){
        ArrayList<ShopItem> items = new ArrayList<>();
        Cursor cursor = mDB.query(TABLE_MYSHOPS, null, null, null, null, null, sortBy);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                ShopItem item = new ShopItem();
                item.setLocalId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(MYSHOPS_COLUMN_SHOP_NAME)));
                items.add(item);
                cursor.moveToNext();
            }
            return items;
        } finally {
            if(cursor != null) cursor.close();
        }
    }

    /**
     * Method for adding shop record to local DB.
     *
     * @param shopName name of the shop.
     */
    public void addShop(String shopName) {
        if (!isShopDuplicate(shopName)) {
            ContentValues cv = new ContentValues();
            cv.put(MYSHOPS_COLUMN_SHOP_NAME, shopName);
            cv.put(MYSHOPS_COLUMN_SHOP_RATING, 1);
            cv.put(MYSHOPS_COLUMN_SHOP_DESCRIPTION, "");
            cv.put(MYSHOPS_COLUMN_SHOP_CONTACT, "");
            cv.put(MYSHOPS_COLUMN_SHOP_URL, "");
            mDB.insert(TABLE_MYSHOPS, null, cv);
        }
    }

    /**
     * Method for deleting shop record.
     *
     * @param id ID of the record.
     */
    public void delShopById(long id) {
        mDB.delete(TABLE_MYSHOPS, COLUMN_ID + " = " + id, null);
    }

    /**
     * Method for updating shop record in local DB.
     *
     * @param id ID of the record.
     * @param newName new name for the shop.
     */
    public void editShop(long id, String newName) {
        ContentValues cv = new ContentValues(1);
        cv.put(MYSHOPS_COLUMN_SHOP_NAME, newName);
        mDB.update(TABLE_MYSHOPS, cv, "_id = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Method for finding duplicate shop entries.
     * True if shop name already exists in DB.
     */
    private boolean isShopDuplicate(String shopName){
        return mDB.query(TABLE_MYSHOPS, null,
                "shop_name = ?", new String[] { shopName },
                null, null, null).getCount() != 0;
    }

    /**
     * Method for preparing StashItem objects from Cursor.
     *
     * @param cursor data from DB.
     * @return list of StashItem objects.
     */
    private ArrayList<StashItem> prepareKit(Cursor cursor) {
        ArrayList<StashItem> itemList = new ArrayList<>();
        try {
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
                String media = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_MEDIA));
                String barcode = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BARCODE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));
                String itemType = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ITEMTYPE));
                StashItem stashItem = new StashItem.StashItemBuilder(itemType)
                        .hasLocalId(id)
                        .hasBrand(brand)
                        .hasBrand_catno(brandCatno)
                        .hasKitName(name)
                        .hasScale(scale)
                        .hasCategory(category)
                        .hasBarcode(barcode)
                        .hasKitNoengName(kitNoengname)
                        .hasDescription(description)
                        .hasPrototype(MyConstants.EMPTY)//not in use
                        .hasSendStatus(sendStatus)
                        .hasBoxartUrl(boxartUrl)
                        .hasBoxartUri(boxartUri)
                        .hasScalematesUrl(scalematesUrl)
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
                        .build();
                itemList.add(stashItem);
                cursor.moveToNext();
            }
            return itemList;
        } finally {
            if(cursor != null) cursor.close();
        }
    }


    /////////////CURRENCY//////////////////

    /**
     * Get list of currencies.
     * @param sortBy
     * Field to sort by
     * @return
     * String array with names of currencies.
     */
    public String[] getCurrencies(String tableName, String sortBy) {
        Cursor cursor = mDB.query(tableName, null, null, null, null, null, sortBy);
        String[] responce = new String[cursor.getCount()];
        try {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                responce[i] = cursor.getString(1);
                cursor.moveToNext();
            }
            return responce;
        } finally {
            if(cursor != null) cursor.close();
        }
    }

    public void addCurrency(String cur) {
        ContentValues cv = new ContentValues();
        cv.put(CURRENCIES_COLUMN_CURRENCY, cur);
        mDB.insert(TABLE_CURRENCIES, null, cv);
    }

    //////////// VIEW STASH BOTTOM SHEET //////////////

    /**
     * Method for preparing list of categories fof Bottom Sheet.
     *
     * @param workMode code of item type (kit, aftermarket, paint).
     * @return list of category items (name, icon, quantity).
     */
    public ArrayList<CategoryItem> getActiveCategories(String workMode) {
        ArrayList<CategoryItem> list = new ArrayList<>();
        CategoryItem firstItem = new CategoryItem();
        firstItem.setName("");
        firstItem.setLogoResource(com.kutovenko.kitstasher.R.drawable.ic_check_black_24dp);
        firstItem.setQuantity(countAllRecordsByType(workMode));
        list.add(firstItem);
        Cursor cursor = mDB.rawQuery("SELECT category, count(*) as count " +
                "FROM kits " +
                "WHERE item_type is " + workMode +
                " GROUP BY category " +
                "HAVING count(*) > 0 " +
                "ORDER BY count(*) DESC", null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                CategoryItem item = new CategoryItem();
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                item.setName(category);
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("count")));
                setCategoryImage(item, category);
                list.add(item);
                cursor.moveToNext();
            }
            return list;
        }catch (Exception ex){
            return list;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void setCategoryImage(CategoryItem item, String category) {
        switch (category){
            case "0":
                item.setLogoResource(com.kutovenko.kitstasher.R.drawable.ic_help_black_24dp);
                break;
            case "1":
                item.setLogoResource(com.kutovenko.kitstasher.R.drawable.ic_tag_air_black_24dp);
                break;
            case "2":
                item.setLogoResource(com.kutovenko.kitstasher.R.drawable.ic_tag_afv_black_24dp);
                break;
            case "3":
                item.setLogoResource(com.kutovenko.kitstasher.R.drawable.ic_tag_ship_black_24dp);
                break;
            case "4":
                item.setLogoResource(com.kutovenko.kitstasher.R.drawable.ic_tag_space_black_24dp);
                break;
            case "5":
                item.setLogoResource(com.kutovenko.kitstasher.R.drawable.ic_directions_car_black_24dp);
                break;
            case "6":
                item.setLogoResource(com.kutovenko.kitstasher.R.drawable.ic_wc_black_24dp);
                break;
            case "7":
                item.setLogoResource(com.kutovenko.kitstasher.R.drawable.ic_android_black_24dp);
                break;

            case "100":
                item.setLogoResource(com.kutovenko.kitstasher.R.drawable.ic_check_black_24dp);
                break;
            case "101":
                item.setLogoResource(com.kutovenko.kitstasher.R.drawable.ic_check_black_24dp);
                break;
            case "102":
                item.setLogoResource(com.kutovenko.kitstasher.R.drawable.ic_check_black_24dp);
                break;
            default:
                item.setLogoResource(com.kutovenko.kitstasher.R.drawable.ic_check_black_24dp);
                break;
        }
    }

    ////////// PAINTS //////////

    /**
     * Method for adding new paint to local DB.
     *
     * @param cv CV with data.
     */
    public long addSupply(ContentValues cv) {
        return mDB.insertOrThrow(TABLE_KITS, null, cv);
    }

    /**
     * Method for updating paint record in local DB.
     *
     * @param id ID of the record.
     * @param cv new data for the paint.
     */
    public void editSupply(long id, ContentValues cv) {
        mDB.update(TABLE_KITS, cv, "_id = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getPaintById(long id) {
        return mDB.query(TABLE_KITS, null,
                "_id = ?", new String[]{String.valueOf(id)}, null, null, null);
    }
}