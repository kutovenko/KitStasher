package com.example.kitstasher.other;

/**
 * Created by Алексей on 21.04.2017.
 * Constants for application.
 * Cloud constants.
 */

public class Constants {
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

    public static final String CAT_ALL = "all";
    public static final String CAT_AIR = "air";
    public static final String CODE_AIR = "1";
    public static final String CAT_GROUND = "ground";
    public static final String CODE_GROUND = "2";
    public static final String CAT_SEA = "sea";
    public static final String CODE_SEA = "3";
    public static final String CAT_SPACE = "space";
    public static final String CODE_SPACE = "4";
    public static final String CAT_OTHER = "other";
    public static final String CODE_OTHER = "0";
    public static final String CAT_AUTOMOTO = "auto";
    public static final String CODE_AUTOMOTO = "5";
    public static final String CAT_FIGURES = "figures";
    public static final String CODE_FIGURES = "6";
    public static final String CAT_FANTASY = "fantasy";
    public static final String CODE_FANTASY = "7";


    public static final String LIST_ID = "id";
    public static final String LIST_TAG = "tag";
    public static final String LIST_POSITION = "position";
    public static final String LIST_CATEGORY = "category";
    public static final String BOXART_PIC = "boxart_pic";

    public static final String BOXART_URL_PREFIX = "https://www.scalemates.com/";
    public static final String BOXART_SIZE = "boxart_size";

    //    public static final String BOXART_URL_POSTFIX = ".jpg";
    public static final String BOXART_URL_COMPANY_SUFFIX = "210";
//    public static final String BOXART_URL_SMALL = "t210";
    public static final String BOXART_URL_SMALL = "t";
    public static final String BOXART_URL_MEDIUM = "t280";
    public static final String BOXART_URL_LARGE = "pristine";
    public static final String BOXART_URL_POSTFIX = ".jpg";
    public static final String SCALEMATES_PREFIX = "https://www.scalemates.com/kits/";

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
    public static final String PARSE_STATUS = "b4app_status";
    public static final String PARSE_REGISTERED = "registered";

    ///
    public static final String SIZE_FULL = "-pristine";
    public static final String SIZE_MEDIUM = "-t280";
    public static final String SIZE_SMALL = "-t140";

    // Image Dimensions
    public static final int SIZE_FULL_HEIGHT = 86;
    public static final int SIZE_FULL_WIDTH = 140;
    public static final int SIZE_UP_MEDIUM_HEIGHT = 393;
    public static final int SIZE_UP_MEDIUM_WIDTH = 640;
    public static final int SIZE_MEDIUM_HEIGHT = 172;
    public static final int SIZE_MEDIUM_WIDTH = 280;
    public static final int SIZE_SMALL_HEIGHT = 86;
    public static final int SIZE_SMALL_WIDTH = 140;

    //ManualAdd
    public static final String BOXART_IMAGE = "boxartImage";
    public static final String CATEGORY = "category";
    public static final String BOXART_URL = "boxart_url";
}