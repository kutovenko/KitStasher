package com.kutovenko.kitstasher.util


/**
 * Created by Алексей on 21.04.2017.
 * MyConstants for application.
 * Cloud constants.
 */

//object MyConstants {
    /*
     * Cloud constants for App42 service
     * For getting API_KEY & SECRET_KEY, just login or register to AppHQ Console (http://apphq.shephertz.com/).
     */
    val App42ApiKey = "6509200cf9e36a7c9c669a0080ef0729c96f037037940b749b4814839ebb509b"  // API_KEY received From AppHQ console, When You create your first app in AppHQ Console.
    val App42ApiSecret = "d2756e1875e46b7c9572fa7277751c9c94aa7a5bc58ee8b8f565f893d36bd0d2" // SECRET_KEY received From AppHQ console, When You create your first app in AppHQ Console.

    /*
     * For creating Database from AppHQ console, just go to (Technical Service Manager -> Storage Service -> click "Add DB".)
     */
    val App42DBName = "KitStasher"  // Change it as your requirement. (Note that, only one DataBase can be created through same API_KEY & SECRET_KEY);
    val CollectionName = "Currencies" // Change it as your requirement.

    val DATABASE_NAME = "myscalestash"

    /*
    Category tags
     */
    val TAG_KIT_NAME = "k"
    val TAG_BRAND = "b"
    val TAG_BRAND_CATNO = "c"
    val TAG_DESCRIPTION = "d"
    val TAG_SCALE = "s"
    val TAG_PROTOTYPE = "p"
    val TAG_NOENG_NAME = "n"
    val TAG_BOXART_URL = "u"
    val TAG_SCALEMATES_PAGE = "m"
    val TAG_BARCODE = "a"
    val TAG_CATEGORY = "g"
    val TAG_YEAR = "y"
    val TAG_MEDIA = "e"
    val TAG_TIMEFRAME = "f"
    val TAG_OWNERID = "o"
    val TAG_KITTYPE = "t"


    //Prefixes and postfixes for links
    val CAT_ALL = "all"
    val CODE_AIR = "1"
    val CODE_GROUND = "2"
    val CODE_SEA = "3"
    val CODE_SPACE = "4"
    val CODE_AUTOMOTO = "5"
    val CODE_FIGURES = "6"
    val CODE_FANTASY = "7"
    val CODE_OTHER = "0"

    val CODE_P_OTHER = "300"
    val CODE_P_ACRYLLIC = "301"
    val CODE_P_ENAMEL = "302"
    val CODE_P_OIL = "303"
    val CODE_P_LACQUER = "304"
    val CODE_P_THINNER = "305"
    val CODE_P_GLUE = "306"
    val CODE_P_DECAL_SET = "307"
    val CODE_P_DECAL_SOL = "308"
    val CODE_P_PIGMENT = "309"
    val CODE_P_COLORSTOP = "310"
    val CODE_P_FILLER = "311"
    val CODE_P_PRIMER = "312"

    //Preferences
    val ACCOUNT_PREFS = "account_prefs"
    val USER_ID_APPHQ = "apphq_user_id"
    val USER_ID_FACEBOOK = "fb_user_id"
    val USER_NAME_FACEBOOK = "fb_user_name"
    val PROFILE_PICTURE_URL_FACEBOOK = "fb_profile_picture_url"
    val USER_ID_PARSE = "parse_user_id"
    val USER_IDTYPE = "idtype"
    val PARSE_STATUS = "b4app_status"
    val PARSE_REGISTERED = "registered"

    // Image Dimensions
    val SIZE_FULL_SIDE = 720
    val SIZE_FULL_HEIGHT = 450
    val SIZE_FULL_WIDTH = 720
    val SIZE_UP_MEDIUM_HEIGHT = 393
    val SIZE_UP_MEDIUM_WIDTH = 640
    val SIZE_MEDIUM_HEIGHT = 172
    val SIZE_MEDIUM_WIDTH = 280
    val SIZE_SMALL_HEIGHT = 86
    val SIZE_SMALL_WIDTH = 140

    val BOXART_URL_PREFIX = ""
    val BOXART_URL_COMPANY_SUFFIX = "210"
    val BOXART_URL_SMALL = "t160"
    val BOXART_URL_MEDIUM = "t280"
    val BOXART_URL_LARGE = "pristine"
    val JPG = ".jpg"
    val SIZE_FULL = "-pristine"
    //    public static final String SIZE_FULL = "-720";
    val SIZE_SMALL = "-t" //140

    //ManualAdd
    val BOXART_IMAGE = "boxartImage"
    val BOXART_URL = "boxart_url"

    //Defaults
    val DEFAULT_CURRENCY = "def_curr"

    //Modes for editor
    val ITEM_TYPE = "mode"
    val MODE_SEARCH = "s"

    //Media Codes
    val M_CODE_UNKNOWN = "0"
    val M_CODE_INJECTED = "1"
    val M_CODE_SHORTRUN = "2"
    val M_CODE_RESIN = "3"
    val M_CODE_VACU = "4"
    val M_CODE_PAPER = "5"
    val M_CODE_WOOD = "6"
    val M_CODE_METAL = "7"
    val M_CODE_3DPRINT = "8"
    val M_CODE_MULTIMEDIA = "9"
    val M_CODE_OTHER = "10"
    val M_CODE_DECAL = "11"
    val M_CODE_MASK = "12"
    val M_CODE_ADDON = "13"
    val M_CODE_PHOTOETCH = "14"


    //Status codes
    val STATUS_NEW = 0

    //Aftermarket bundles
    val AFTER_ID = "after_id"
    val ITEM_ID = "item_id"

    //other
    val POSITION = "position"
    val ID = "id"
    val CATEGORY = "category"
    val BARCODE = "barcode"
    val KITNAME = "kitname"
    val BRAND = "brand"
    val CATNO = "catno"
    val STATUS = "status"
    val MEDIA = "media"
    val QUANTITY = "quantity"
    val NOTES = "notes"
    val SCALE = "scale"
    val YEAR = "year"
    val DESCRIPTION = "description"
    val ORIGINAL_NAME = "originalname"
    val BOXART_URI = "boxart_uri"
    val SCALEMATES = "scalemates"
    val PURCHASE_DATE = "purchaseDate"
    val PRICE = "price"
    val CURRENCY = "currency"
    val NEW_TOOL = "1"
    val REBOX = "2"

    //Sorting
    val _ID = "_id"
    val AFTERMARKET_MODE = "afterMode"

    //Cropping
    val FILE_URI = "fileUri"
    val CROPPED_URI = "croppedUri"

    //Parse
    val PARSE_C_STASH = "Stash"
    val PARSE_C_NEWKIT = "NewKits"
    val PARSE_C_BOXART = "Boxart"
    val PARSE_C_TOPUSERS = "Top_users"
    val PARSE_TU_OWNERID = "ownerId"
    val PARSE_BARCODE = "barcode"
    val PARSE_OWNERID = "owner_id"
    val PARSE_IDTYPE = "owner_id_type"
    val PARSE_SOCIALTYPE = "socialType"
    val PARSE_BRAND = "brand"
    val PARSE_BOXART_URL = "boxart_url"
    val PARSE_SCALE = "scale"
    val PARSE_BRAND_CATNO = "brandCatno"
    val PARSE_NOENGNAME = "kit_noengname"
    val PARSE_KITNAME = "kit_name"
    val PARSE_DESCRIPTION = "description"
    val PARSE_YEAR = "year"
    val PARSE_CATEGORY = "category"
    val PARSE_SCALEMATES = "scalemates_url"
    val PARSE_DELETED = "isDeleted"
    val PARSE_THUMBNAIL_URL = "thumbnail_url"
    val PARSE_ITEMTYPE = "itemType"
    val PARSE_LOCALID = "localId"
    val PARSE_IMAGE = "image"
    val PARSE_TU_STASH = "stash"
    val PARSE_TU_USERID = "userId"
    val PARSE_TU_OWNERNAME = "ownerName"
    val PARSE_MEDIA = "media"

    val EMPTY = ""
    val CLOUD_MODE = "cloud_mode"

    //Directories names
    val BOXART_DIRECTORY_NAME = "boxart"


    val CATEGORY_TAB = "category_tab"

    val DAILYMAX = "dailyMax"
    val DAILYMAXDATE = "dailyMaxDate"
    val WORLDRANK = "world_rank"


    val TYPE_ALL = "0" // тип - все
    val TYPE_KIT = "1" // тип - набор
    val TYPE_AFTERMARKET = "2" // тип - афтермаркет
    val TYPE_SUPPLY = "3" // тип - краска/химия
    val LIST = "list"
    val PARSE_PURCHASE_PLACE = "purchasePlace"
    val ITEMS_LIST = "itemsList"
    val KIT = "kit"
    val ASPECTRATIO_X = 16f
    val ASPECTRATIO_Y = 10f
    val PAINT_EDIT_MODE = "isEdited"
    val NEW_FILE_URI = "newFilePath"
//}