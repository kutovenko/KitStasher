package com.example.kitstasher.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.fragment.AddFragment;
import com.example.kitstasher.fragment.HomeFragment;
import com.example.kitstasher.fragment.MyListsFragment;
import com.example.kitstasher.fragment.SettingsFragment;
import com.example.kitstasher.fragment.ViewStashFragment;
import com.example.kitstasher.other.AsyncApp42ServiceApi;
import com.example.kitstasher.other.CircleTransform;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.DbConnector;
import com.parse.Parse;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;


/**
 * Created by Alexey on 10.04.2017.
 * Main Activity. Sets up Navigation Drawer and contains fragments for different tasks
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawer;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private View navHeader;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RelativeLayout header;
    public static final String CAT_AIR = "air";
    public static final String CAT_GROUND = "ground";
    public static final String CAT_SEA = "sea";
    public static final String CAT_SPACE = "space";
    public static final String CAT_OTHER = "other";
    public static final String CAT_AUTOMOTO = "auto";

    // Index to identify current nav menu item
    public static int navItemIndex = 0;

    // Tags used to attach the fragments
    public static final String TAG_HOME = "home";
    public static final String TAG_ADDSTASH = "addstash";
    public static final String TAG_VIEWSTASH = "viewstash";
    public static final String TAG_SETTINGS = "settings";
    public static final String TAG_HELP = "help";
    public static final String TAG_MYLISTS = "mylists";


    public static String CURRENT_TAG = TAG_HOME;

    // Toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    //Tag for replacing inner fragments in Pager
    public static final int REQUEST_CODE_POSITION = 1;
    public static final int REQUEST_CODE_CAMERA = 2;
    public static final int REQUEST_CODE_CROP = 3;


    // Flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static AsyncApp42ServiceApi asyncService;
    private DbConnector dbConnector;
    private int permissionCheck;

    private Uri picUri;
    private Bitmap bmBoxartPic;

    public String getTabManualAdd(String t) {
        return t;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Setting up cloud connections
        asyncService = AsyncApp42ServiceApi.instance(this);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.parse_application_id))
                .clientKey(getString(R.string.parse_client_key))
                .server(getString(R.string.parse_server_url)).build());
        //Setting up Sqlite connection
        dbConnector = new DbConnector(this);
        dbConnector.open();



        //Loading SharedPreferences
        sharedPreferences = this.getSharedPreferences(Constants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);

        //Setting up UI
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Navigation view header
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        header = (RelativeLayout)navHeader.findViewById(R.id.rlAppBarHeader);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mHandler = new Handler();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setUpNavigationView();
        loadNavHeader();

        //Loading home fragment on start
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }



    /***
     * Loads navigation menu header information. Image is Facebook profile picture, name is Facebook
     * profile name.
     */
    private void loadNavHeader() {
        // Setting Username
        txtName.setText(sharedPreferences.getString(Constants.USER_NAME_FACEBOOK, ""));
//        txtWebsite.setText("www.kitstashers.com");

//        RelativeLayout header = (RelativeLayout)findViewById(R.id.rlAppBarHeader);
//        imgNavHeaderBg.setVisibility(View.GONE);
//        header.setBackgroundColor(setHeaderBackground());
        //Setting header image
//        imgNavHeaderBg.setImageResource(setHeaderImage());
        // Loading profile image
        String accountPictureUrl = sharedPreferences.getString(Constants.PROFILE_PICTURE_URL_FACEBOOK, null);
        Glide.with(this).load(accountPictureUrl)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);
    }

    /**
     * Sets image based on favorite stash category
     *
     * @return background image resource
     */
    public int setHeaderImage() {
        //Default image
        int result = R.drawable.default_texture;
        //Choosing background image
        int air = dbConnector.getByTag(Constants.CAT_AIR).getCount();
        int ground = dbConnector.getByTag(Constants.CAT_GROUND).getCount();
        int sea = dbConnector.getByTag(Constants.CAT_SEA).getCount();
        int space = dbConnector.getByTag(Constants.CAT_SPACE).getCount();
        int car = dbConnector.getByTag(Constants.CAT_AUTOMOTO).getCount();
        int other = dbConnector.getByTag(Constants.CAT_OTHER).getCount();

        int max = (int)Helper.findMax(air, ground, sea, space, car, other);
        if (max == air)
            result = R.drawable.texture_air;
        if (max == ground)
            result = R.drawable.texture_stone;
        if (max == sea)
            result = R.drawable.texture_sea;
        if (max == space)
            result = R.drawable.texture_space;
        if (max == car)
            result = R.drawable.texture_car;
        if (max == other)
            result = R.drawable.texture_other;


        return result;
    }

    public int setHeaderBackground(){
        int result = Helper.getColor(this, R.color.colorPrimary);
        //Choosing background color
        int air = dbConnector.getByTag(Constants.CAT_AIR).getCount();
        int ground = dbConnector.getByTag(Constants.CAT_GROUND).getCount();
        int sea = dbConnector.getByTag(Constants.CAT_SEA).getCount();
        int space = dbConnector.getByTag(Constants.CAT_SPACE).getCount();
        int car = dbConnector.getByTag(Constants.CAT_AUTOMOTO).getCount();
        int other = dbConnector.getByTag(Constants.CAT_OTHER).getCount();

        int max = (int)Helper.findMax(air, ground, sea, space, car, other);
        if (max == air)
            result = R.color.air;
        if (max == ground)
            result = R.color.ground;
        if (max == sea)
            result = R.color.sea;
        if (max == space)
            result = R.color.space;
        if (max == car)
            result = R.color.car;
        if (max == other)
            result = R.color.other;

        return result;
    }

    /**
     * Sets up Navigation View
     */
    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click
        // of the navigation menu
        navigationView.setNavigationItemSelectedListener
                (new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_addstash:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_ADDSTASH;
                        break;
                    case R.id.nav_viewstash:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_VIEWSTASH;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_mylists:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_MYLISTS;
                        break;
//                    case R.id.nav_help:
//                        navItemIndex = 4;
//                        CURRENT_TAG = TAG_HELP;
//                        break;

                    default:
                        navItemIndex = 0;
                }
                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything
                // to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything
                // to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessary or else hamburger icon won't show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
//            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            if (navItemIndex == 5) {
                navItemIndex = 2;
                CURRENT_TAG = TAG_VIEWSTASH;
                loadHomeFragment();
//                return;
            } else if (navItemIndex == 6 || navItemIndex == 7) {
                navItemIndex = 3;
                CURRENT_TAG = TAG_SETTINGS;
                loadHomeFragment();
//                return;
            } else {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
//                return;

            }

        } else {
            super.onBackPressed();
        }

    }


    /***
     * Returns fragment that was selected from navigation menu
     */
    public void loadHomeFragment() {
        // Selecting appropriate nav menu item
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }
        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                // update the main content by replacing fragments
                android.support.v4.app.Fragment fragment = getHomeFragment();
                fragment.setArguments(bundle);//
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        //Closing drawer on item click
        drawer.closeDrawers();
        // Refresh toolbar menu
        invalidateOptionsMenu();
    }

    private android.support.v4.app.Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // Home fragment
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // Add stash
                AddFragment addFragment = new AddFragment();
                return addFragment;
            case 2:
                // View stash fragment
                ViewStashFragment viewStashFragment = new ViewStashFragment();
                return viewStashFragment;

            case 3:
                // Settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;

            case 4:
                // My Lists fragment
                MyListsFragment myListsFragment = new MyListsFragment();
                return myListsFragment;

//            case 4:
//                //Help fragment
//                HelpFragment helpFragment = new HelpFragment();
//                return helpFragment;

            default:
                return new HomeFragment();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            navItemIndex = 0;
            loadHomeFragment();
        } else if (id == R.id.nav_addstash) {
            navItemIndex = 1;
            loadHomeFragment();
        } else if (id == R.id.nav_viewstash) {
            navItemIndex = 2;
            loadHomeFragment();
        } else if (id == R.id.nav_settings) {
            navItemIndex = 3;
            loadHomeFragment();
        } else if (id == R.id.nav_mylists){
            navItemIndex = 4;
            loadHomeFragment();
        }
//        } else if (id == R.id.nav_help) {
//            navItemIndex = 4;
//            loadHomeFragment();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


/////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Request to KitEdit Activity if user edit kit. Return user to proper position in kits list.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super call required for work with onActivityResult from nested fragments
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_POSITION){
            super.onActivityResult(requestCode, resultCode, data);
            int position = data.getExtras().getInt(Constants.LIST_POSITION);
            int categoryTab = data.getExtras().getInt(Constants.LIST_CATEGORY);

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.LIST_POSITION, position);
            bundle.putInt(Constants.LIST_CATEGORY, categoryTab);

            ViewStashFragment fragment = new ViewStashFragment();
            fragment.setArguments(bundle);
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.commit();
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerViewStash);
            viewPager.setCurrentItem(categoryTab);
        }
        if (resultCode != RESULT_OK){
        }
    }

    /////////////////////////




//    /**
//     * Checks if Internet access available
//     * @return true if Internet available
//     */
//    public boolean isOnline() {
//        ConnectivityManager cm =
//                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        return netInfo != null && netInfo.isConnectedOrConnecting();
//    }


}
