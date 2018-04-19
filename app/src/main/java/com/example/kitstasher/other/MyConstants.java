package com.example.kitstasher.other;

/**
 * Created by Алексей on 21.04.2017.
 * MyConstants for application.
 * Cloud constants.
 */

public class MyConstants {
    /*
     * Cloud constants for App42 service
     * For getting API_KEY & SECRET_KEY, just login or register to AppHQ Console (http://apphq.shephertz.com/).
     */
    static final String App42ApiKey = "6509200cf9e36a7c9c669a0080ef0729c96f037037940b749b4814839ebb509b";  // API_KEY received From AppHQ console, When You create your first app in AppHQ Console.
    static final String App42ApiSecret = "d2756e1875e46b7c9572fa7277751c9c94aa7a5bc58ee8b8f565f893d36bd0d2"; // SECRET_KEY received From AppHQ console, When You create your first app in AppHQ Console.

    /*
     * For creating Database from AppHQ console, just go to (Technical Service Manager -> Storage Service -> click "Add DB".)
     */
    public static final String App42DBName = "KitStasher";  // Change it as your requirement. (Note that, only one DataBase can be created through same API_KEY & SECRET_KEY);

//    public static final String CollectionName = "Test"; // Change it as your requirement.
    public static final String CollectionName = "Kits"; // Change it as your requirement.

    /*
     * For Creating Game, just go to (Business Service Manager -> Game Service -> select Game -> click "Add Game".)
     */

    //// TODO: 21.04.2017 поменять на реальные данные пользователей
    static final String App42GameName = "GameName"; // Change it as your requirement. (You have to create game through AppHQ console.);

    static final String IntentUserName = "intentUserName";
    static final String App42AndroidPref="App42AndroidPreferences";
    static final String UserName = "TestUser";

//
//        <string name="parse_application_id" translatable="false">btaakgy7Ch1juOYWd34B09ir2Nyt1MUiFqfA9BRU</string>
//    <string name="parse_client_key" translatable="false">bwcdVqjKNxrmmowaIPcala5ss9blO6KTr8s3Tn2U</string>
//    <string name="parse_server_url" translatable="false">https://parseapi.back4app.com/</string>

/*
Category tags
 */


    public static final String TAG_KIT_NAME = "k";
    public static final String TAG_BRAND = "b";
    public static final String TAG_BRAND_CATNO = "c";
    public static final String TAG_DESCRIPTION = "d";
    public static final String TAG_SCALE = "s";
    public static final String TAG_PROTOTYPE = "p";
    public static final String TAG_NOENG_NAME = "n";
    public static final String TAG_BOXART_URL = "u";
    public static final String TAG_SCALEMATES_PAGE = "m";
    public static final String TAG_BARCODE = "a";
    public static final String TAG_CATEGORY = "g";
    public static final String TAG_YEAR = "y";


/*
Prefixes and postfixes for links
 */
// TODO: 19.02.2018 проверить категории!!! 0 - неправильно уже
    public static final String CAT_ALL = "all";
    public static final String CAT_AIR = "air";
    public static final String CODE_AIR = "1";
    public static final String CAT_GROUND = "ground";
    public static final String CODE_GROUND = "2";
    public static final String CAT_SEA = "sea";
    public static final String CODE_SEA = "3";
    public static final String CAT_SPACE = "space";
    public static final String CODE_SPACE = "4";
    public static final String CAT_AUTOMOTO = "auto";
    public static final String CODE_AUTOMOTO = "5";
    public static final String CAT_FIGURES = "figures";
    public static final String CODE_FIGURES = "6";
    public static final String CAT_FANTASY = "fantasy";
    public static final String CODE_FANTASY = "7";
    public static final String CAT_OTHER = "other";
    public static final String CODE_OTHER = "0";


    public static final String LIST_ID = "id";
    public static final String LIST_TAG = "tag";
    public static final String LIST_POSITION = "position";
    public static final String LIST_CATEGORY = "category";
    public static final String BOXART_PIC = "boxart_pic";

    public static final String BOXART_URL_PREFIX = "";
    public static final String BOXART_SIZE = "boxart_size";

    //    public static final String JPG = ".jpg";
    public static final String BOXART_URL_COMPANY_SUFFIX = "210";
//    public static final String BOXART_URL_SMALL = "t210";
    public static final String BOXART_URL_SMALL = "t";
    public static final String BOXART_URL_MEDIUM = "t280";
    public static final String BOXART_URL_LARGE = "pristine";
    public static final String JPG = ".jpg";
//    public static final String SCALEMATES_PREFIX = "https://www.scalemates.com/kits/";

    public static final String APP_FOLDER = "/Kitstasher/";
    public static final String FOLDER_SDCARD_KITSTASHER = "/sdcard/Kitstasher/";

    public static final String BACKENDLESS_USER_ID = "6E0C94E5-E081-CF48-FF6E-33600FDCBC00";

    //Preferences
    public static final String ACCOUNT_PREFS = "account_prefs";
    public static final String USER_ID_APPHQ = "apphq_user_id";
    public static final String USER_ID_FACEBOOK = "fb_user_id";
    public static final String USER_NAME_FACEBOOK = "fb_user_name";
    public static final String PROFILE_PICTURE_URL_FACEBOOK = "fb_profile_picture_url";
    public static final String USER_ID_PARSE = "parse_user_id";
    public static final String USER_IDTYPE = "idtype";
    public static final String PARSE_STATUS = "b4app_status";
    public static final String PARSE_REGISTERED = "registered";

    ///
    public static final String SIZE_FULL = "-pristine";
    //    public static final String SIZE_FULL = "-720";
    public static final String SIZE_MEDIUM = "-t280";
    public static final String SIZE_SMALL = "-t"; //140

    // Image Dimensions
    public static final int SIZE_FULL_HEIGHT = 450; //исправить!
    public static final int SIZE_FULL_WIDTH = 720;
    public static final int SIZE_UP_MEDIUM_HEIGHT = 393;
    public static final int SIZE_UP_MEDIUM_WIDTH = 640;
    public static final int SIZE_MEDIUM_HEIGHT = 172;
    public static final int SIZE_MEDIUM_WIDTH = 280;
    public static final int SIZE_SMALL_HEIGHT = 86;
    public static final int SIZE_SMALL_WIDTH = 140;

    //ManualAdd
    public static final String BOXART_IMAGE = "boxartImage";
    //    public static final String CATEGORY = "category";
    public static final String BOXART_URL = "boxart_url";

    //Backup and Restore
    public static final String KITS_FILE_NAME = "kits";
    public static final String BRANDS_FILE_NAME = "brands";
    public static final String LISTS_FILE_NAME = "lists";
    public static final String LISTITEMS_FILE_NAME = "listitems";
    public static final String MYSHOPS_FILE_NAME = "myshops";
    public static final String AFTER_FILE_NAME = "aftermarket";
    public static final String KIT_AFTER_FILE_NAME = "kitandafter";

    //Defaults
    public static final String DEFAULT_CURRENCY = "def_curr";

    //Modes for editor
    public static final String LISTNAME = "listname";
    public static final String WORK_MODE = "mode";
    public static final Character MODE_KIT = 'm';
    public static final Character MODE_LIST = 'l';
    public static final Character MODE_AFTERMARKET = 'a';
    public static final Character MODE_AFTER_KIT = 'k';
    public static final Character MODE_VIEW_FROM_KIT = 'v';
    public static final Character MODE_EDIT_FROM_KIT = 'e';
    public static final Character MODE_SEARCH = 's';

    public static final String PASS_ID = "passid";

    //Media Codes
    public static final int M_CODE_UNKNOWN = 0;
    public static final int M_CODE_INJECTED = 1;
    public static final int M_CODE_SHORTRUN = 2;
    public static final int M_CODE_RESIN = 3;
    public static final int M_CODE_VACU = 4;
    public static final int M_CODE_PAPER = 5;
    public static final int M_CODE_WOOD = 6;
    public static final int M_CODE_METAL = 7;
    public static final int M_CODE_3DPRINT = 8;
    public static final int M_CODE_MULTIMEDIA = 9;
    public static final int M_CODE_OTHER = 10;
    public static final int M_CODE_DECAL = 11;
    public static final int M_CODE_MASK = 12;


    //Status codes
    public static final int STATUS_NEW = 0;
    public static final int STATUS_OPENED = 1;
    public static final int STATUS_STARTED = 2;
    public static final int STATUS_INPROGRESS = 3;
    public static final int STATUS_FINISHED = 4;
    public static final int STATUS_LOST = 5;

    //Aftermarket bundles
    public static final String AFTER_ID = "after_id";
    public static final String ITEM_ID = "item_id";

    //other
    public static final String POSITION = "position";
    public static final String ID = "id";
    public static final String CATEGORY = "category";
    public static final String TAG = "tag";
    public static final String SCALE_FILTER = "scaleFilter";
    public static final String BRAND_FILTER = "brandFilter";
    public static final String KITNAME_FILTER = "kitnameFilter";
    public static final String STATUS_FILTER = "statusFilter";
    public static final String MEDIA_FILTER = "mediaFilter";
    public static final String FILTERS = "filters";
    public static final String TABLE = "table";

    public static final String SORT_BY = "sortBy";
    public static final String BARCODE = "barcode";
    public static final String KITNAME = "kitname";
    public static final String CURSOR_POSITION = "cursorPosition";
    public static final String BRAND = "brand";
    public static final String CATNO = "catno";
    public static final String IDS = "ids";
    public static final String STATUS = "status";
    public static final String MEDIA = "media";
    public static final String QUANTITY = "quantity";
    public static final String NOTES = "notes";
    public static final String URL = "url";
    public static final String URI = "uri";
    public static final String SCALE = "scale";
    public static final String YEAR = "year";
    public static final String DESCRIPTION = "description";
    public static final String ORIGINAL_NAME = "originalname";

    public static final String KIT_ID = "kit_id";

    public static final String BOXART_URI = "boxart_uri";
    public static final String SCALEMATES = "scalemates";
    public static final String SHOP = "shop";
    public static final String PURCHASE_DATE = "purchaseDate";
    public static final String PRICE = "price";
    public static final String CURRENCY = "currency";

    public static final String NEW_TOOL = "1";
    public static final String REBOX = "2";
    //Sorting
    public static final String _ID = "_id";
    public static final String AFTERMARKET_MODE = "afterMode";

    //Cropping
    public static final String FILE_URI = "fileUri";
    public static final String CROPPED_URI = "croppedUri";

    //Parse
    public static final String PARSE_C_STASH = "Stash";
    public static final String PARSE_C_NEWKIT = "NewKits";
    public static final String PARSE_C_BOXART = "Boxart";
    public static final String PARSE_C_TOPUSERS = "Top_users";
    public static final String PARSE_TU_OWNERID = "ownerId";
    public static final String PARSE_BARCODE = "barcode";
    public static final String PARSE_OWNERID = "owner_id";
    public static final String PARSE_IDTYPE = "owner_id_type";
    public static final String PARSE_SOCIALTYPE = "socialType";
    public static final String PARSE_BRAND = "brand";
    public static final String PARSE_BOXART_URL = "boxart_url";
    public static final String PARSE_SCALE = "scale";
    public static final String PARSE_BRAND_CATNO = "brandCatno";
    public static final String PARSE_NOENGNAME = "kit_noengname";
    public static final String PARSE_KITNAME = "kit_name";
    public static final String PARSE_DESCRIPTION = "description";
    public static final String PARSE_YEAR = "year";
    public static final String PARSE_CATEGORY = "category";
    public static final String PARSE_SCALEMATES = "scalemates_url";
    public static final String PARSE_DELETED = "isDeleted";
    public static final String PARSE_THUMBNAIL_URL = "thumbnail_url";
    public static final String PARSE_ITEMTYPE = "itemType";
    public static final String PARSE_LOCALID = "localId";
    //    public static final String PARSE_PARENTID = "parentId";
    public static final String PARSE_IMAGE = "image";
    public static final String PARSE_TU_STASH = "stash";
    public static final String PARSE_TU_USERID = "userId";
    public static final String PARSE_TU_OWNERNAME = "ownerName";


    public static final String POSITIONS = "positions";
    public static final String EMPTY = "";

    //ItemEdit
    public static final int MODE_A_BRAND = 1; //карточка брэнда
    public static final int MODE_A_SHOP = 2; // карточка магазина
    public static final int MODE_A_LIST = 3; // карточка пункта вишлиста

    public static final int MODE_A_KIT = 4; // карточка афтера без редактирования
    public static final int MODE_A_EDIT = 5; // карточка афтера с редактированием

    //Options
    public static final int CLOUD_OFF = 0;
    public static final int CLOUD_ON = 1;
    public static final String CLOUD_MODE = "cloud_mode";

    //Directories names
    public static final String BOXART_DIRECTORY_NAME = "boxart";


    public static final String CATEGORY_TAB = "category_tab";
    public static final java.lang.String LISTID = "list_id";



    public static final String DAILYMAX = "dailyMax";
    public static final String DAILYMAXDATE = "dailyMaxDate";
    public static final String WORLDRANK = "world_rank";


    public static final String TYPE_KIT = "1"; // тип - набор
    public static final String TYPE_AFTERMARKET = "2"; // тип - афтермаркет
    public static final String LIST = "list";
    public static final String PARSE_PURCHASE_PLACE = "purchasePlace";
}