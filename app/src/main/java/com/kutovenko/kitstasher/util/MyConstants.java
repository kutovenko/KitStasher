package com.kutovenko.kitstasher.util;

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
    public static final String App42ApiKey = "6509200cf9e36a7c9c669a0080ef0729c96f037037940b749b4814839ebb509b";  // API_KEY received From AppHQ console, When You create your first app in AppHQ Console.
    public static final String App42ApiSecret = "d2756e1875e46b7c9572fa7277751c9c94aa7a5bc58ee8b8f565f893d36bd0d2"; // SECRET_KEY received From AppHQ console, When You create your first app in AppHQ Console.

    /*
     * For creating Database from AppHQ console, just go to (Technical Service Manager -> Storage Service -> click "Add DB".)
     */
    public static final String App42DBName = "KitStasher";  // Change it as your requirement. (Note that, only one DataBase can be created through same API_KEY & SECRET_KEY);
    public static final String CollectionName = "Kits"; // Change it as your requirement.

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
    public static final String TAG_MEDIA = "e";
    public static final String TAG_TIMEFRAME = "f";
    public static final String TAG_OWNERID = "o";
    public static final String TAG_KITTYPE = "t";
    //f //timeframe
    //o //owner_id
    //t kit type 1 - full kit

    //Prefixes and postfixes for links
    public static final String CAT_ALL = "all";
    public static final String CODE_AIR = "1";
    public static final String CODE_GROUND = "2";
    public static final String CODE_SEA = "3";
    public static final String CODE_SPACE = "4";
    public static final String CODE_AUTOMOTO = "5";
    public static final String CODE_FIGURES = "6";
    public static final String CODE_FANTASY = "7";
    public static final String CODE_OTHER = "0";

    public static final String CODE_P_OTHER = "300";
    public static final String CODE_P_ACRYLLIC = "301";
    public static final String CODE_P_ENAMEL = "302";
    public static final String CODE_P_OIL = "303";
    public static final String CODE_P_LACQUER = "304";
    public static final String CODE_P_THINNER = "305";
    public static final String CODE_P_GLUE = "306";
    public static final String CODE_P_DECAL_SET = "307";
    public static final String CODE_P_DECAL_SOL = "308";
    public static final String CODE_P_PIGMENT = "309";
    public static final String CODE_P_COLORSTOP = "310";
    public static final String CODE_P_FILLER = "311";
    public static final String CODE_P_PRIMER = "312";

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

    // Image Dimensions
    public static final int SIZE_FULL_SIDE = 720;
    public static final int SIZE_FULL_HEIGHT = 450; //исправить!
    public static final int SIZE_FULL_WIDTH = 720;
    public static final int SIZE_UP_MEDIUM_HEIGHT = 393;
    public static final int SIZE_UP_MEDIUM_WIDTH = 640;
    public static final int SIZE_MEDIUM_HEIGHT = 172;
    public static final int SIZE_MEDIUM_WIDTH = 280;
    public static final int SIZE_SMALL_HEIGHT = 86;
    public static final int SIZE_SMALL_WIDTH = 140;

    public static final String BOXART_URL_PREFIX = "";
    public static final String BOXART_URL_COMPANY_SUFFIX = "210";
    public static final String BOXART_URL_SMALL = "t160";
    public static final String BOXART_URL_MEDIUM = "t280";
    public static final String BOXART_URL_LARGE = "pristine";
    public static final String JPG = ".jpg";
    public static final String SIZE_FULL = "-pristine";
    //    public static final String SIZE_FULL = "-720";
    public static final String SIZE_SMALL = "-t"; //140

    //ManualAdd
    public static final String BOXART_IMAGE = "boxartImage";
    public static final String BOXART_URL = "boxart_url";

    //Defaults
    public static final String DEFAULT_CURRENCY = "def_curr";

    //Modes for editor
    public static final String ITEM_TYPE = "mode";
    public static final String MODE_SEARCH = "s";

    //Media Codes
    public static final String M_CODE_UNKNOWN = "0";
    public static final String M_CODE_INJECTED = "1";
    public static final String M_CODE_SHORTRUN = "2";
    public static final String M_CODE_RESIN = "3";
    public static final String M_CODE_VACU = "4";
    public static final String M_CODE_PAPER = "5";
    public static final String M_CODE_WOOD = "6";
    public static final String M_CODE_METAL = "7";
    public static final String M_CODE_3DPRINT = "8";
    public static final String M_CODE_MULTIMEDIA = "9";
    public static final String M_CODE_OTHER = "10";
    public static final String M_CODE_DECAL = "11";
    public static final String M_CODE_MASK = "12";
    public static final String M_CODE_ADDON = "13";
    public static final String M_CODE_PHOTOETCH = "14";


    //Status codes
    public static final int STATUS_NEW = 0;

    //Aftermarket bundles
    public static final String AFTER_ID = "after_id";
    public static final String ITEM_ID = "item_id";

    //other
    public static final String POSITION = "position";
    public static final String ID = "id";
    public static final String CATEGORY = "category";
    public static final String BARCODE = "barcode";
    public static final String KITNAME = "kitname";
    public static final String BRAND = "brand";
    public static final String CATNO = "catno";
    public static final String STATUS = "status";
    public static final String MEDIA = "media";
    public static final String QUANTITY = "quantity";
    public static final String NOTES = "notes";
    public static final String SCALE = "scale";
    public static final String YEAR = "year";
    public static final String DESCRIPTION = "description";
    public static final String ORIGINAL_NAME = "originalname";
    public static final String BOXART_URI = "boxart_uri";
    public static final String SCALEMATES = "scalemates";
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
    public static final String PARSE_IMAGE = "image";
    public static final String PARSE_TU_STASH = "stash";
    public static final String PARSE_TU_USERID = "userId";
    public static final String PARSE_TU_OWNERNAME = "ownerName";
    public static final String PARSE_MEDIA = "media";

    public static final String EMPTY = "";
    public static final String CLOUD_MODE = "cloud_mode";

    //Directories names
    public static final String BOXART_DIRECTORY_NAME = "boxart";


    public static final String CATEGORY_TAB = "category_tab";

    public static final String DAILYMAX = "dailyMax";
    public static final String DAILYMAXDATE = "dailyMaxDate";
    public static final String WORLDRANK = "world_rank";


    public static final String TYPE_ALL = "0"; // тип - все
    public static final String TYPE_KIT = "1"; // тип - набор
    public static final String TYPE_AFTERMARKET = "2"; // тип - афтермаркет
    public static final String TYPE_SUPPLY = "3"; // тип - краска/химия
    public static final String LIST = "list";
    public static final String PARSE_PURCHASE_PLACE = "purchasePlace";
    public static final String ITEMS_LIST = "itemsList";
    public static final String KIT = "kit";
    public static final float ASPECTRATIO_X = 16;
    public static final float ASPECTRATIO_Y = 10;
    public static final String PAINT_EDIT_MODE = "isEdited";
}