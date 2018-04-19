package com.example.kitstasher.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kitstasher.R;
import com.example.kitstasher.fragment.KitsFragment;
import com.example.kitstasher.fragment.SearchFragment;
import com.example.kitstasher.fragment.SettingsFragment;
import com.example.kitstasher.fragment.StatisticsFragment;
import com.example.kitstasher.other.AsyncApp42ServiceApi;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;

/**
 * Created by Alexey on 10.04.2017.
 * Main Activity. Sets up Navigation Drawer and contains fragments for different tasks
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG_HOME = "home";
    public static final String TAG_ADDSTASH = "addstash";
    public static final String TAG_VIEWSTASH = "viewstash";
    public static final String TAG_SETTINGS = "settings";
    public static final String TAG_AFTERMARKET = "aftermarket";
    public static final String TAG_MYLISTS = "mylists";
    public static final String TAG_SEARCH = "search";
    public static final String TAG_STATISTICS = "statistics";
    public static String CURRENT_TAG = TAG_HOME;
    public static final int REQUEST_CODE_VIEW = 1;
    public static final int REQUEST_CODE_CAMERA = 2;
    public static final int REQUEST_CODE_CROP = 3;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 10;
    public static final int MY_PERMISSIONS_REQUEST_WRITE = 20;
    public static int navItemIndex = 0;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ImageView imgProfile;
    private TextView txtName;
    private Handler mHandler;
    private SharedPreferences sharedPreferences;
    public static AsyncApp42ServiceApi asyncService;
    private String title;
    private boolean aftermarketMode;
    // Flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            title = savedInstanceState.getString("title");
        }
        setContentView(R.layout.activity_main);
        asyncService = AsyncApp42ServiceApi.instance(this);

        DbConnector dbConnector = new DbConnector(this);
        dbConnector.open();

        aftermarketMode = false;
        sharedPreferences = this.getSharedPreferences(MyConstants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.name);
        imgProfile = navHeader.findViewById(R.id.img_profile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mHandler = new Handler();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setUpNavigationView();

        loadNavHeader();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_VIEWSTASH;
            loadHomeFragment(aftermarketMode);
        }

        checkCameraPermissions();

        checkWritePermissions();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", title);
    }

    private void loadNavHeader() {
        txtName.setText(sharedPreferences.getString(MyConstants.USER_NAME_FACEBOOK, ""));
        String accountPictureUrl = sharedPreferences.getString(MyConstants.PROFILE_PICTURE_URL_FACEBOOK, null);
        Glide.with(this).load(accountPictureUrl)
                .thumbnail(0.5f)
                .apply(new RequestOptions().centerCrop())
                .into(imgProfile);

    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener
                (new NavigationView.OnNavigationItemSelectedListener() {

                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.nav_viewstash:
                                navItemIndex = 0;
                                CURRENT_TAG = TAG_VIEWSTASH;
                                aftermarketMode = false;
                                break;
                            case R.id.nav_aftermarket:
                                navItemIndex = 1;
                                CURRENT_TAG = TAG_AFTERMARKET;
                                aftermarketMode = true;
                                break;
                            case R.id.nav_search:
                                navItemIndex = 2;
                                CURRENT_TAG = TAG_SEARCH;
                                aftermarketMode = false;
                                break;
                            case R.id.nav_statistics:
                                navItemIndex = 3;
                                CURRENT_TAG = TAG_STATISTICS;
                                aftermarketMode = false;
                                break;
                            case R.id.nav_settings:
                                navItemIndex = 4;
                                CURRENT_TAG = TAG_SETTINGS;
                                aftermarketMode = false;
                                break;
                            default:
                                navItemIndex = 0;
                                CURRENT_TAG = TAG_VIEWSTASH;
                                aftermarketMode = false;
                        }
                        if (menuItem.isChecked()) {
                            menuItem.setChecked(false);
                        } else {
                            menuItem.setChecked(true);
                        }
                        menuItem.setChecked(true);

                        loadHomeFragment(aftermarketMode);

                        return true;
                    }
                });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        if (shouldLoadHomeFragOnBackPress) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_ADDSTASH;
            loadHomeFragment(aftermarketMode);
        } else {
            super.onBackPressed();
        }
    }

    private void promptExit() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.Do_you_wish_to_exit);

        dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        });
        dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog d = dialogBuilder.create();
        d.show();
    }

    public void setActionBarTitle(String t) {
        title = t;
        toolbar.setTitle(title);
    }

    public void loadHomeFragment(final boolean aftermarketMode) {
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putBoolean(MyConstants.AFTERMARKET_MODE, aftermarketMode);
                android.support.v4.app.Fragment fragment = getHomeFragment();
                fragment.setArguments(bundle);//
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainactivityContainer, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        mHandler.post(mPendingRunnable);
        drawer.closeDrawers();
        invalidateOptionsMenu();
    }

    private android.support.v4.app.Fragment getHomeFragment() {

        switch (navItemIndex) {
            case 0:
                Fragment kitFragment = new KitsFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean(MyConstants.AFTERMARKET_MODE, false);
                kitFragment.setArguments(bundle);
                return kitFragment;
            case 1:
                Fragment fragment = new KitsFragment();
                Bundle aBundle = new Bundle();
                aBundle.putBoolean(MyConstants.AFTERMARKET_MODE, true);
                fragment.setArguments(aBundle);
                return fragment;
            case 2:
                return new SearchFragment();
            case 3:
                return new StatisticsFragment();
            case 4:
                return new SettingsFragment();
            default:
                Fragment defFragment = new KitsFragment();
                Bundle defBundle = new Bundle();
                defBundle.putBoolean(MyConstants.AFTERMARKET_MODE, false);
                defFragment.setArguments(defBundle);
                return defFragment;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_viewstash) {
            navItemIndex = 0;
            loadHomeFragment(aftermarketMode);
        } else if (id == R.id.nav_aftermarket) {
            navItemIndex = 1;
            loadHomeFragment(aftermarketMode);
        } else if (id == R.id.nav_search) {
            navItemIndex = 2;
            loadHomeFragment(aftermarketMode);
        } else if (id == R.id.nav_statistics) {
            navItemIndex = 3;
            loadHomeFragment(aftermarketMode);
        } else if (id == R.id.nav_settings) {
            navItemIndex = 4;
            loadHomeFragment(aftermarketMode);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Request to KitEdit Activity if user edit kit. Return user to proper position in kits list.
     *             //Если вернулись напрямую из афтермаркетЭдит, workMode будет MODE_AFTERMARKET
     //Если из карточки KitCard - MODE_KIT
     //Если из KitEdit - MODE_KIT AFTER
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_VIEW) {
                super.onActivityResult(requestCode, resultCode, data);
                char workMode = data.getCharExtra(MyConstants.WORK_MODE, MyConstants.MODE_KIT);
                int position = data.getIntExtra(MyConstants.LIST_POSITION, 0);
                int categoryTab = data.getIntExtra(MyConstants.CATEGORY_TAB, 0);
                String category = data.getStringExtra(MyConstants.CATEGORY);
//                String scaleFilter = data.getStringExtra(MyConstants.SCALE_FILTER);
//                String brandFilter = data.getStringExtra(MyConstants.BRAND_FILTER);
//                String kitnameFilter = data.getStringExtra(MyConstants.KITNAME_FILTER);
//                String statusFilter = data.getStringExtra(MyConstants.STATUS_FILTER);
//                String mediaFilter = data.getStringExtra(MyConstants.MEDIA_FILTER);

                Bundle bundle = new Bundle();
                bundle.putInt(MyConstants.POSITION, position);
                bundle.putInt(MyConstants.CATEGORY_TAB, categoryTab);
                bundle.putString(MyConstants.CATEGORY, category);
                bundle.putChar(MyConstants.WORK_MODE, workMode);
//                bundle.putString(MyConstants.SCALE_FILTER, scaleFilter);
//                bundle.putString(MyConstants.BRAND_FILTER, brandFilter);
//                bundle.putString(MyConstants.KITNAME_FILTER, kitnameFilter);
//                bundle.putString(MyConstants.STATUS_FILTER, statusFilter);
//                bundle.putString(MyConstants.MEDIA_FILTER, mediaFilter);

                KitsFragment fragment = new KitsFragment();
                if (workMode == MyConstants.MODE_KIT) {
                    bundle.putBoolean(MyConstants.AFTERMARKET_MODE, false);
                    fragment.setArguments(bundle);
                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.mainactivityContainer, fragment);
                    fragmentTransaction.commit();

                    KitsFragment.refreshPages(); //???

                    ViewPager viewPager = findViewById(R.id.viewpagerViewStash);
                    viewPager.setCurrentItem(categoryTab);
                } else if (workMode == MyConstants.MODE_AFTERMARKET) {
                    bundle.putBoolean(MyConstants.AFTERMARKET_MODE, true);
                    fragment.setArguments(bundle);
                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.mainactivityContainer, fragment);
                    fragmentTransaction.commit();

                    KitsFragment.refreshPages();

                    ViewPager viewPager = findViewById(R.id.viewpagerViewStash);
                    viewPager.setCurrentItem(categoryTab);

                } else if (workMode == MyConstants.MODE_VIEW_FROM_KIT) {
                    //Возвращаемся в просмотр кита

                } else if (workMode == MyConstants.MODE_EDIT_FROM_KIT) {
                    //Возвращаемся в КитЕдит
                }
            }
        }
    }

    private void checkCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Without permission to use camera we can't read barcodes",
                        Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
    }

    private void checkWritePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Without permission to write file we can't save boxart",
                        Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,
                            R.string.permission_denied_to_use_camera, Toast.LENGTH_SHORT).show();
                }
                return;
            case MY_PERMISSIONS_REQUEST_WRITE:
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,
                            R.string.permission_denied_to_write, Toast.LENGTH_SHORT).show();
                }
        }
    }

}