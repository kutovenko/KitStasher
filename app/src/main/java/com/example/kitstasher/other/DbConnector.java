package com.example.kitstasher.other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DbConnector {

    public static final String DB_NAME = "myscalestash";
    private static final int DB_VERSION = 1;

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
    public static final String COLUMN_ORIGINAL_KIT_NAME = "original_kit_name";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_COLLECTION = "collection";
    public static final String COLUMN_SEND_STATUS = "send_status";
    public static final String COLUMN_ID_ONLINE = "id_online";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_BOXART_URI = "boxart_uri";
    public static final String COLUMN_BOXART_URL = "boxart_url";
    public static final String COLUMN_IS_DELETED = "is_deleted";
    public static final String COLUMN_YEAR = "year";

    ///////// TABLE BRANDS /////////

    private static final String TABLE_BRANDS = "brands";
    public static final String BRANDS_COLUMN_ID = "_id";
    public static final String BRANDS_COLUMN_BRAND = "brand";

    private static final String TABLE_TAGS = "tags";
    private static final String TAGS_COLUMN_ID = "_id";
    private static final String TAGS_COLUMN_TAG = "tag";

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


    ///////// TABLE ACCOUNT /////////
    //Not in use now

    private static final String TABLE_ACCOUNT = "account";
    private static final String ACCOUNT_COLUMN_ID = "_id"; // Локальный ключ
    private static final String ACCOUNT_COLUMN_USER_NAME = "user_name";
    private static final String ACCOUNT_COLUMN_USER_EMAIL = "user_email";
    private static final String ACCOUNT_COLUMN_PROFILEPIC_URL = "profile_pic";
    private static final String ACCOUNT_COLUMN_FACEBOOK_PROFILE_URL = "fb_profile_url";
    private static final String ACCOUNT_COLUMN_NETWORK = "network";

    ///////// CREATE SCRIPTS /////////

    private static final String CREATE_TABLE_MYLISTSITEMS =
            "create table " + TABLE_MYLISTSITEMS + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " + // Локальный ключ -0
                    COLUMN_BARCODE + " text," + // штрихкод NOBARCODE по умолчанию для garage kit? - 1
                    COLUMN_BRAND + " text, " + // производитель - 2
                    COLUMN_BRAND_CATNO + " text," + //каталожный номер набора - 3
                    COLUMN_SCALE + " integer," + //масштаб - 4
                    COLUMN_KIT_NAME + " text," + //название набора - 5
                    COLUMN_DESCRIPTION + " text," + //описание, продолжение названия - 6
                    COLUMN_ORIGINAL_KIT_NAME + " text," + //название на оригинальном языке, - 7
                    // если отличается
                    COLUMN_CATEGORY + " text," + //тег (самолет, корабль, и тд - 8
                    COLUMN_COLLECTION  + " text," + //коллекция - для группировки и других функций - 9
                    COLUMN_SEND_STATUS + " text," + //для отслеживания офлайн отправок LOCAL - 10
                    COLUMN_ID_ONLINE + " text," + //номер в онлайновой базе, может пригодится - 11
                    //заметки? LOCAL?
                    COLUMN_BOXART_URI + " text," + //локальная ссылка на файл боксарта LOCAL - 12
                    COLUMN_BOXART_URL + " text," + //интернет-ссылка на боксарт для Glide LOCAL - 13
                    COLUMN_IS_DELETED + " int," + // - 14
                    COLUMN_DATE + " text," +// дата добавления? LOCAL? - 15
                    COLUMN_YEAR + " text," + // год выпуска набора - 16
                    MYLISTSITEMS_LISTNAME + " text" + // Локальный ключ -0
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
                    COLUMN_ORIGINAL_KIT_NAME + " text," + //название на оригинальном языке, - 7
                                                            // если отличается
                    COLUMN_CATEGORY + " text," + //тег (самолет, корабль, и тд - 8
                    COLUMN_COLLECTION  + " text," + //коллекция - для группировки и других функций - 9
                    COLUMN_SEND_STATUS + " text," + //для отслеживания офлайн отправок LOCAL - 10
                    COLUMN_ID_ONLINE + " text," + //номер в онлайновой базе, может пригодится - 11
                    //заметки? LOCAL?
                    COLUMN_BOXART_URI + " text," + //локальная ссылка на файл боксарта LOCAL - 12
                    COLUMN_BOXART_URL + " text," + //интернет-ссылка на боксарт для Glide LOCAL - 13
                    COLUMN_IS_DELETED + " int," + // - 14
                    COLUMN_DATE + " text," +// дата добавления? LOCAL? - 15
                    COLUMN_YEAR + " text" + // год выпуска набора - 16
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
                    ACCOUNT_COLUMN_USER_NAME + " text" +
                    ACCOUNT_COLUMN_USER_EMAIL + " text" +
                    ACCOUNT_COLUMN_PROFILEPIC_URL + " text" +
                    ACCOUNT_COLUMN_FACEBOOK_PROFILE_URL + " text" +
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


    private static final String INIT_TABLE_CATEGORY =
            "INSERT INTO " + TABLE_TAGS + "(" + TAGS_COLUMN_TAG + ")" +
                    " SELECT 'Airplane' AS " + TAGS_COLUMN_TAG +
                    " UNION SELECT 'Ship'" +
                    " UNION SELECT 'Armour';";

    //todo delete?
    private static final String INIT_TABLE_ACCOUNT =
            "INSERT INTO " + TABLE_ACCOUNT + "(" + ACCOUNT_COLUMN_ID + ")" +
                    " SELECT '1' AS " + ACCOUNT_COLUMN_ID +
                    " ;";


    final String LOG_TAG = "myLogs";
    private final Context mCtx;
    private static DBHelper mDBHelper;
    private static SQLiteDatabase mDB;


    public DbConnector(Context context) {
        // конструктор суперкласса
        //   super(context, "myDB", null, 1);
        mCtx = context;
    }


    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();

    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    //вакуум - удаление и перестройка базы
    public void vacuumDb(){
        mDB.execSQL("VACUUM");
    }

    public void clearTable(String table)   {
        mDB.delete(table, null,null);
    }

    //////////////KITS//////////////

    // получить все данные из таблицы TABLE_KITS
    public Cursor getAllData() {
        return mDB.query(TABLE_KITS, null, null, null, null, null, null);
    }

    //Все с сортировкой, без удаленных
    public Cursor getAllData(String sortBy) {
        return mDB.query(TABLE_KITS, null, null, null, null, null, sortBy);
    }

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

    //Получить запись по идентификатору
    public Cursor getRecById(long id){
        return mDB.query(TABLE_KITS, null, "_id = " + id, null, null, null, null);
        //todo написать перевод в редактор и перенос значений
    }

    //Редактирование записи
    public void editRecById(long id, ContentValues cv){
        //String clause = String.valueOf(id);
        mDB.update(TABLE_KITS, cv, "_id = ?", new String[] { String.valueOf(id) });
    }

    // Запись в базу, если есть баркод со сканера //todo CHANGE and DELETE
    public void addKitRec(String barcode, String brand, String brand_catno, int scale,
                          String kitname, String kit_noengname, String status, String date,
                          String boxart_url, String category, String boxart_uri,
//                          String online_id,
                          String description,
                          String year) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BARCODE, barcode);
        cv.put(COLUMN_BRAND, brand);
        cv.put(COLUMN_BRAND_CATNO, brand_catno);
        cv.put(COLUMN_SCALE, scale);
        cv.put(COLUMN_KIT_NAME, kitname);
        cv.put(COLUMN_ORIGINAL_KIT_NAME, kit_noengname);
        cv.put(COLUMN_SEND_STATUS, status);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_BOXART_URL, boxart_url);
        cv.put(COLUMN_CATEGORY, category);

        cv.put(COLUMN_BOXART_URI, boxart_uri);
//        cv.put(COLUMN_ID_ONLINE, online_id);
        cv.put(COLUMN_YEAR, year);
        cv.put(COLUMN_DESCRIPTION, description);


        mDB.insert(TABLE_KITS, null, cv);
    }

    //Добавление импортом
    public void addKitRec(ContentValues cv){
        mDB.insert(TABLE_KITS, null, cv);
    }

    // удалить запись из TABLE_KITS
    public void delRec(long id) {
        mDB.delete(TABLE_KITS, COLUMN_ID + " = " + id, null);
    }



    ////////////////BRANDS/////////////////

    //ТАБЛИЦА BRANDS Все с сортировкой
    public Cursor getBrands(String sortBy) {
        //return mDB.query(TABLE_KITS, null, "is_deleted = ?", new String[] {"0"}, null, null, sortBy);
        return mDB.query(TABLE_BRANDS, null, null, null, null, null, sortBy);
    }

    //НОВОЕ!!! Для автодополнения брэндов
    public void addKitRec(String brand) {
        ContentValues cv = new ContentValues();
        cv.put(BRANDS_COLUMN_BRAND, brand);
        mDB.insert(TABLE_BRANDS, null, cv);
    }

    //НОВОЕ!!! Для автодополнения брэндов
    public static ArrayList<String> getAllBrands() {
        ArrayList<String> allBrands = new ArrayList<>();
        //SQLiteDatabase sqLiteDatabase = null;
//        try {
        String query = "select brand from brands;";
        Cursor cursor = mDB.rawQuery(query, null);
        while (cursor.moveToNext()) {
            allBrands.add(cursor.getString(cursor.getColumnIndex(COLUMN_BRAND)));
        }
//        }catch(Exception ex){
//            Log.e(TAG,"Erro in geting friends "+ex.toString());
//        }
        cursor.close();
        return allBrands;
    }


    // удалить запись из TABLE_BRANDS
    public void delBrand(long id) {
        mDB.delete(TABLE_BRANDS, COLUMN_ID + " = " + id, null);
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
    public void addList (String listName, String date){
            ContentValues cv = new ContentValues();
            cv.put(MYLISTS_COLUMN_LIST_NAME, listName);
            cv.put(MYLISTS_COLUMN_DATE, date);
            mDB.insert(TABLE_MYLISTS, null, cv);
    }

    public Cursor getAllLists (){
        return mDB.query(TABLE_MYLISTS, null, null, null, null, null, null);
    }

    public Cursor getListById (long id){
        return mDB.query(TABLE_MYLISTS, null, "_id = " + id, null, null, null, null);
    }

    public Cursor getList (String listname){
        return mDB.query(TABLE_MYLISTS, null, "listname = ?", new String[] { listname }, null, null, null);
    }

    public void deleteList (String listId){
            mDB.delete(TABLE_MYLISTS, MYLISTS_COLUMN_ID + " = " + listId, null);
    }

    public void updateList (String listName, String newListname){
        ContentValues cv = new ContentValues();
        cv.put(MYLISTS_COLUMN_LIST_NAME, newListname);
        mDB.update(TABLE_MYLISTS, cv, "listname = ?", new String[] { listName });
        updateListItems(listName, newListname);
    }

    private void updateListItems(String listName, String newListname){
        ContentValues cv = new ContentValues();
        cv.put(MYLISTSITEMS_LISTNAME, newListname);
        mDB.update(TABLE_MYLISTSITEMS, cv, "listname = ?", new String[] { listName });
    }

    public boolean isListExists (String listname){
        if (getList(listname).getCount() != 0){
            return true;
        }else{
            return false;
        }
    }


    public void addListItem(String barcode, String brand, String brand_catno, int scale,
                          String kitname, String kit_noengname, String status, String date,
                          String boxart_url, String category, String boxart_uri,
//                          String online_id,
                          String description,
                          String year, String listname) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BARCODE, barcode);
        cv.put(COLUMN_BRAND, brand);
        cv.put(COLUMN_BRAND_CATNO, brand_catno);
        cv.put(COLUMN_SCALE, scale);
        cv.put(COLUMN_KIT_NAME, kitname);
        cv.put(COLUMN_ORIGINAL_KIT_NAME, kit_noengname);
        cv.put(COLUMN_SEND_STATUS, status);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_BOXART_URL, boxart_url);
        cv.put(COLUMN_CATEGORY, category);

        cv.put(COLUMN_BOXART_URI, boxart_uri);
//        cv.put(COLUMN_ID_ONLINE, online_id);
        cv.put(COLUMN_YEAR, year);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(MYLISTSITEMS_LISTNAME, listname);

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
    private String makePlaceholders(int len) {
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

    public void clearList (String listname){
        mDB.delete(TABLE_MYLISTSITEMS, MYLISTSITEMS_LISTNAME + " = '" + listname + "'", null);
    }

    public boolean searchListForDoubles(String listname, String bcode){
        boolean found = false;
            if (mDB.query(TABLE_MYLISTSITEMS, new String[] {"listname", "barcode"},"listname = ? " +
                    "AND barcode = ?", new String[] {listname, bcode}, null, null, null)
                    .getCount() != 0){
                found = true;
        }
        return found;
    }

public boolean checkListForDounles(long id){
    boolean found = false;
    if (mDB.query(TABLE_MYLISTSITEMS, new String[] {"_id"},"_id = ? ",
            new String[] {String.valueOf(id)}, null, null, null)
            .getCount() != 0){
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
    ////////////////ПРОВЕРКИ ПРИ ДОБАВЛЕНИИ////////////////////

    //Для проверки из сканирования ScanActivity
    public boolean searchForDoubles(String bcode){
        boolean found = false;
        if (mDB.query(TABLE_KITS,null,"barcode = ?", new String[] {bcode}, null, null, null)
                .getCount() != 0){
            found = true;
        }
            return found;
    }

    //Для проверки из ручного добавления AddActivity
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
            db.execSQL(CREATE_TABLE_CATEGORY);
            db.execSQL(INIT_TABLE_CATEGORY);
            db.execSQL(CREATE_TABLE_STATISTIC);
            db.execSQL(CREATE_TABLE_ACCOUNT);
            db.execSQL(CREATE_TABLE_MYLISTS);
            db.execSQL(CREATE_TABLE_MYLISTSITEMS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}