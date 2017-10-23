package com.example.kitstasher.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.fragment.AddFragment;
import com.example.kitstasher.fragment.AftermarketFragment;
import com.example.kitstasher.fragment.HomeFragment;
import com.example.kitstasher.fragment.MyListsFragment;
import com.example.kitstasher.fragment.SettingsFragment;
import com.example.kitstasher.fragment.StatisticsFragment;
import com.example.kitstasher.fragment.ViewStashFragment;
import com.example.kitstasher.other.AsyncApp42ServiceApi;
import com.example.kitstasher.other.CircleTransform;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.parse.Parse;


/**
 * Created by Alexey on 10.04.2017.
 * Main Activity. Sets up Navigation Drawer and contains fragments for different tasks
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawer;
    private ImageView imgProfile;
    private TextView txtName, txtWebsite;
    private View navHeader;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private String title;

    // Index to identify current nav menu item
    public static int navItemIndex = 0;

    // Tags used to attach the fragments
    public static final String TAG_HOME = "home";
    public static final String TAG_ADDSTASH = "addstash";
    public static final String TAG_VIEWSTASH = "viewstash";
    public static final String TAG_SETTINGS = "settings";
    public static final String TAG_AFTERMARKET = "aftermarket";
    public static final String TAG_MYLISTS = "mylists";
    public static final String TAG_STATISTICS = "statistics";


    public static String CURRENT_TAG = TAG_HOME;

    // Toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    //Tag for replacing inner fragments in Pager
    public static final int REQUEST_CODE_POSITION = 1;
    public static final int REQUEST_CODE_CAMERA = 2;
    public static final int REQUEST_CODE_CROP = 3;

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 10;
    public static final int MY_PERMISSIONS_REQUEST_WRITE = 20;


    // Flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static AsyncApp42ServiceApi asyncService;
    private DbConnector dbConnector;
//    private int permissionCheck;

//    private Uri picUri;
//    private Bitmap bmBoxartPic;

//    public String getTabManualAdd(String t) {
//        return t;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            title = savedInstanceState.getString("title");
        }
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
        toolbar.setTitle(title);

        setSupportActionBar(toolbar);
        // Navigation view header
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
//        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
//        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
//        header = (RelativeLayout)navHeader.findViewById(R.id.rlAppBarHeader);
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
            loadHomeFragment(false);
        }

        checkPermissions();
        checkDeleted();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", title);
    }


    /***
     * Loads navigation menu header information. Image is Facebook profile picture, name is Facebook
     * profile name.
     */
    private void loadNavHeader() {
        // Setting Username
        txtName.setText(sharedPreferences.getString(Constants.USER_NAME_FACEBOOK, ""));
//        txtWebsite.setText("www.kitstashers.com");
        // Loading profile image
        String accountPictureUrl = sharedPreferences.getString(Constants.PROFILE_PICTURE_URL_FACEBOOK, null);
        Glide.with(this).load(accountPictureUrl)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);
    }

//    /**
//     * Sets image based on favorite stash category
//     *
//     * @return background image resource
//     */
//    public int setHeaderImage() {
//        //Default image
//        int result = R.drawable.default_texture;
//        //Choosing background image
//        int air = dbConnector.getByTag(Constants.CAT_AIR).getCount();
//        int ground = dbConnector.getByTag(Constants.CAT_GROUND).getCount();
//        int sea = dbConnector.getByTag(Constants.CAT_SEA).getCount();
//        int space = dbConnector.getByTag(Constants.CAT_SPACE).getCount();
//        int car = dbConnector.getByTag(Constants.CAT_AUTOMOTO).getCount();
//        int other = dbConnector.getByTag(Constants.CAT_OTHER).getCount();
//
//        int max = (int)Helper.findMax(air, ground, sea, space, car, other);
//        if (max == air)
//            result = R.drawable.texture_air;
//        if (max == ground)
//            result = R.drawable.texture_stone;
//        if (max == sea)
//            result = R.drawable.texture_sea;
//        if (max == space)
//            result = R.drawable.texture_space;
//        if (max == car)
//            result = R.drawable.texture_car;
//        if (max == other)
//            result = R.drawable.texture_other;
//
//
//        return result;
//    }
//
//    public int setHeaderBackground(){
//        int result = Helper.getColor(this, R.color.colorPrimary);
//        //Choosing background color
//        int air = dbConnector.getByTag(Constants.CAT_AIR).getCount();
//        int ground = dbConnector.getByTag(Constants.CAT_GROUND).getCount();
//        int sea = dbConnector.getByTag(Constants.CAT_SEA).getCount();
//        int space = dbConnector.getByTag(Constants.CAT_SPACE).getCount();
//        int car = dbConnector.getByTag(Constants.CAT_AUTOMOTO).getCount();
//        int other = dbConnector.getByTag(Constants.CAT_OTHER).getCount();
//
//        int max = (int)Helper.findMax(air, ground, sea, space, car, other);
//        if (max == air)
//            result = R.color.air;
//        if (max == ground)
//            result = R.color.ground;
//        if (max == sea)
//            result = R.color.sea;
//        if (max == space)
//            result = R.color.space;
//        if (max == car)
//            result = R.color.car;
//        if (max == other)
//            result = R.color.other;
//
//        return result;
//    }

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
//                        toolbar.setTitle(title);
                        break;
                    case R.id.nav_aftermarket:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_AFTERMARKET;
//                        toolbar.setTitle(title);
                        break;
                    case R.id.nav_mylists:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_MYLISTS;
                        break;
                    case R.id.nav_statistics:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_STATISTICS;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    default:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                }
                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment(false);

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
//            if (navItemIndex == 5) {
//                navItemIndex = 2;
//                CURRENT_TAG = TAG_VIEWSTASH;
//                loadHomeFragment();
////                return;
//            } else if (navItemIndex == 6 || navItemIndex == 7) {
//                navItemIndex = 5;
//                CURRENT_TAG = TAG_SETTINGS;
//                loadHomeFragment();
////                return;
//            } else {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
            loadHomeFragment(false);
//                return;

//            }

        } else {
            super.onBackPressed();
        }

    }

    public void setActionBarTitle(String t) {
        title = t;
        toolbar.setTitle(title);
    }

    /***
     * Returns fragment that was selected from navigation menu
     */
    public void loadHomeFragment(final boolean aftermarketMode) {
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
                if (aftermarketMode) {
                    bundle.putBoolean("afterMode", aftermarketMode);
                }
                // update the main content by replacing fragments
                android.support.v4.app.Fragment fragment = getHomeFragment();
                fragment.setArguments(bundle);//
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainactivityContainer, fragment, CURRENT_TAG);
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
                return new HomeFragment();
            case 1:
                return new AddFragment();
            case 2:
                return new ViewStashFragment();
            case 3:
                return new AftermarketFragment();
            case 4:
                return new MyListsFragment();
            case 5:
                return new StatisticsFragment();
            case 6:
                return new SettingsFragment();
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
            loadHomeFragment(false);
        } else if (id == R.id.nav_addstash) {
            navItemIndex = 1;
            loadHomeFragment(false);
        } else if (id == R.id.nav_viewstash) {
            navItemIndex = 2;
            loadHomeFragment(false);
        } else if (id == R.id.nav_aftermarket) {
            navItemIndex = 3;
            loadHomeFragment(true);
        } else if (id == R.id.nav_mylists) {
            navItemIndex = 4;
            loadHomeFragment(false);
        } else if (id == R.id.nav_statistics) {
            navItemIndex = 5;
            loadHomeFragment(false);
        } else if (id == R.id.nav_settings) {
            navItemIndex = 6;
            loadHomeFragment(false);
        }

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

            String scaleFilter = data.getExtras().getString(Constants.SCALE_FILTER);
            String brandFilter = data.getExtras().getString(Constants.BRAND_FILTER);
            String kitnameFilter = data.getExtras().getString(Constants.KITNAME_FILTER);

            String statusFilter = data.getExtras().getString(Constants.STATUS_FILTER);
            String mediaFilter = data.getExtras().getString(Constants.MEDIA_FILTER);


            Bundle bundle = new Bundle();
            bundle.putInt(Constants.LIST_POSITION, position);
            bundle.putInt(Constants.LIST_CATEGORY, categoryTab);

            bundle.putString(Constants.SCALE_FILTER, scaleFilter);
            bundle.putString(Constants.BRAND_FILTER, brandFilter);
            bundle.putString(Constants.KITNAME_FILTER, kitnameFilter);

            bundle.putString(Constants.STATUS_FILTER, statusFilter);
            bundle.putString(Constants.MEDIA_FILTER, mediaFilter);

            ViewStashFragment fragment = new ViewStashFragment();
            fragment.setArguments(bundle);
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainactivityContainer, fragment);
            fragmentTransaction.commit();
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerViewStash);
            viewPager.setCurrentItem(categoryTab);
        }
        if (resultCode != RESULT_OK){
        }
    }

    /////////////////////////

    private void checkPermissions() {
        //checking for permissions on Marshmallow+
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
        //Permissions for write
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE);
            }
        }
    }

    private void checkDeleted() {
        if (getIntent().hasExtra("was_deleted")
                && getIntent().getExtras().getBoolean("was_deleted")) {
            int position = getIntent().getExtras().getInt(Constants.LIST_POSITION);
            int categoryTab = getIntent().getExtras().getInt(Constants.LIST_CATEGORY);
            String scaleFilter = getIntent().getExtras().getString(Constants.SCALE_FILTER);
            String brandFilter = getIntent().getExtras().getString(Constants.BRAND_FILTER);
            String kitnameFilter = getIntent().getExtras().getString(Constants.KITNAME_FILTER);

            String statusFilter = getIntent().getExtras().getString(Constants.STATUS_FILTER);
            String mediaFilter = getIntent().getExtras().getString(Constants.MEDIA_FILTER);


            Bundle bundle = new Bundle();
            bundle.putInt(Constants.LIST_POSITION, position);
            bundle.putInt(Constants.LIST_CATEGORY, categoryTab);

            bundle.putString(Constants.SCALE_FILTER, scaleFilter);
            bundle.putString(Constants.BRAND_FILTER, brandFilter);
            bundle.putString(Constants.KITNAME_FILTER, kitnameFilter);

            bundle.putString(Constants.STATUS_FILTER, statusFilter);
            bundle.putString(Constants.MEDIA_FILTER, mediaFilter);

            ViewStashFragment fragment = new ViewStashFragment();
            fragment.setArguments(bundle);
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainactivityContainer, fragment);
            fragmentTransaction.commit();
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerViewStash);
            viewPager.setCurrentItem(categoryTab);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(MainActivity.this,
                            R.string.permission_denied_to_use_camera, Toast.LENGTH_SHORT).show();
                }
                return;
            case MY_PERMISSIONS_REQUEST_WRITE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(MainActivity.this,
                            R.string.permission_denied_to_write, Toast.LENGTH_SHORT).show();
                }
//                return;
        }
    }

}
