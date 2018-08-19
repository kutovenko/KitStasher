package com.example.kitstasher.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Alexey on 10.04.2017.
 * Main Activity. Sets up Navigation Drawer and contains fragments for different tasks
 *
 * Основной Activity-класс. Содержит навигационную панель и загружает фрагменты просмотра коллекции
 * наборов.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    private ImageView imgProfile;
    private TextView tvName;
    private Handler mHandler;

    private static final String TAG_HOME = "home";
    private static final String TAG_ADDSTASH = "addstash";
    private static final String TAG_VIEWSTASH = "viewstash";
    private static final String TAG_PAINTS = "paints";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_AFTERMARKET = "aftermarket";
    private static final String TAG_SEARCH = "search";
    private static final String TAG_STATISTICS = "statistics";
    private static String CURRENT_TAG = TAG_HOME;

    public static final int REQUEST_CODE_VIEW = 1;
    public static final int REQUEST_CODE_CAMERA = 2;
    public static final int REQUEST_CODE_CROP = 3;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 10;
    public static final int MY_PERMISSIONS_REQUEST_WRITE = 20;

    private static int navItemIndex = 0;
    private DbConnector dbConnector;
    private SharedPreferences sharedPreferences;
    public static AsyncApp42ServiceApi asyncService;
    private String title;
    private Unbinder unbinder;
    private String workMode;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", title);
        outState.putString("mode", workMode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbConnector.close();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            title = savedInstanceState.getString("title");
            workMode = savedInstanceState.getString("mode");
        }
        setContentView(R.layout.activity_main);
        asyncService = AsyncApp42ServiceApi.instance(this);
        unbinder = ButterKnife.bind(this);
        dbConnector = new DbConnector(this);
        dbConnector.open();

        workMode = MyConstants.TYPE_KIT;
        sharedPreferences = this.getSharedPreferences(MyConstants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        View navHeader = navigationView.getHeaderView(0);
        tvName = navHeader.findViewById(R.id.name);
        imgProfile = navHeader.findViewById(R.id.img_profile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mHandler = new Handler();
        navigationView.setNavigationItemSelectedListener(this);

        setUpNavigationView();

        loadNavHeader();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_VIEWSTASH;
            loadHomeFragment(workMode);
        }

//        checkCameraPermissions();

//        checkWritePermissions();
    }

    private void loadNavHeader() {
        tvName.setText(sharedPreferences.getString(MyConstants.USER_NAME_FACEBOOK, ""));
        String accountPictureUrl = sharedPreferences.getString(MyConstants.PROFILE_PICTURE_URL_FACEBOOK, "");
        Glide.with(this).load(accountPictureUrl)
                .thumbnail(0.5f)
                .apply(new RequestOptions().circleCrop())
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
                                workMode = MyConstants.TYPE_KIT;
                                break;
                            case R.id.nav_aftermarket:
                                navItemIndex = 1;
                                CURRENT_TAG = TAG_AFTERMARKET;
                                workMode = MyConstants.TYPE_AFTERMARKET;
                                break;
                            case R.id.nav_paints:
                                navItemIndex = 2;
                                CURRENT_TAG = TAG_PAINTS;
                                workMode = MyConstants.TYPE_PAINT;
                                break;
                            case R.id.nav_search:
                                navItemIndex = 3;
                                CURRENT_TAG = TAG_SEARCH;
                                workMode = MyConstants.TYPE_KIT;
                                break;
                            case R.id.nav_statistics:
                                navItemIndex = 4;
                                CURRENT_TAG = TAG_STATISTICS;
                                workMode = MyConstants.TYPE_KIT;
                                break;
                            case R.id.nav_settings:
                                navItemIndex = 5;
                                CURRENT_TAG = TAG_SETTINGS;
                                workMode = MyConstants.TYPE_KIT;
                                break;
                            default:
                                navItemIndex = 0;
                                CURRENT_TAG = TAG_VIEWSTASH;
                                workMode = MyConstants.TYPE_KIT;
                        }
                        if (menuItem.isChecked()) {
                            menuItem.setChecked(false);
                        } else {
                            menuItem.setChecked(true);
                        }
                        menuItem.setChecked(true);
                        loadHomeFragment(workMode);
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
        navItemIndex = 0;
        CURRENT_TAG = TAG_ADDSTASH;
        loadHomeFragment(workMode);
    }

    public void setActionBarTitle(String t) {
        title = t;
        toolbar.setTitle(title);
    }

    public void loadHomeFragment(final String workMode) {
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putString(MyConstants.WORK_MODE, workMode);
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
        Fragment kitsFragment = new KitsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MyConstants.WORK_MODE, workMode);
        kitsFragment.setArguments(bundle);

        switch (navItemIndex) {
            case 0:
                return kitsFragment;
            case 1:
                return kitsFragment;
            case 2:
                return kitsFragment;
            case 3:
                return new SearchFragment();
            case 4:
                return new StatisticsFragment();
            case 5:
                return new SettingsFragment();
            default:
                Fragment defFragment = new KitsFragment();
                Bundle defBundle = new Bundle();
                defBundle.putString(MyConstants.WORK_MODE, workMode);
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
            loadHomeFragment(workMode);
        } else if (id == R.id.nav_aftermarket) {
            navItemIndex = 1;
            loadHomeFragment(workMode);
        } else if (id == R.id.nav_paints) {
            navItemIndex = 2;
            loadHomeFragment(workMode);
        } else if (id == R.id.nav_search) {
            navItemIndex = 3;
            loadHomeFragment(workMode);
        } else if (id == R.id.nav_statistics) {
            navItemIndex = 4;
            loadHomeFragment(workMode);
        } else if (id == R.id.nav_settings) {
            navItemIndex = 5;
            loadHomeFragment(workMode);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_VIEW) {
//                super.onActivityResult(requestCode, resultCode, data);
//                boolean aftermarketMode = data.getBooleanExtra(MyConstants.WORK_MODE, false);
                String workMode = data.getStringExtra(MyConstants.WORK_MODE);
                int position = data.getIntExtra(MyConstants.LIST_POSITION, 0);
//                int categoryTab = data.getIntExtra(MyConstants.CATEGORY_TAB, 0);
                String category = data.getStringExtra(MyConstants.CATEGORY);

                Bundle bundle = new Bundle();
                bundle.putInt(MyConstants.POSITION, position);
//                bundle.putInt(MyConstants.CATEGORY_TAB, categoryTab);
                bundle.putString(MyConstants.CATEGORY, category);
                bundle.putString(MyConstants.WORK_MODE, workMode);
//                bundle.putBoolean(MyConstants.WORK_MODE, aftermarketMode);

                KitsFragment fragment = KitsFragment.newInstance();
                fragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainactivityContainer, fragment);
                fragmentTransaction.commit();
            }
        }
    }

//    private void checkCameraPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.CAMERA)) {
//                Toast.makeText(this, R.string.we_cant_read_barcodes,
//                        Toast.LENGTH_LONG).show();
//            } else {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.CAMERA},
//                        MY_PERMISSIONS_REQUEST_CAMERA);
//            }
//        }
//    }

//    private void checkWritePermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                Toast.makeText(this, R.string.we_cant_save_boxarts,
//                        Toast.LENGTH_LONG).show();
//            } else {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        MY_PERMISSIONS_REQUEST_WRITE);
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_CAMERA:
//                if (grantResults.length <= 0
//                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(MainActivity.this,
//                            R.string.permission_denied_to_use_camera, Toast.LENGTH_SHORT).show();
//                    android.support.v4.app.Fragment fragment = NoPermissionFragment.newInstance(Manifest.permission.CAMERA, workMode);
//                    android.support.v4.app.FragmentTransaction fragmentTransaction =
//                            getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.mainactivityContainer, fragment, CURRENT_TAG);
//                    fragmentTransaction.commitAllowingStateLoss();
//                }else{
//                    loadHomeFragment(CURRENT_TAG);
//                }
//
//            case MY_PERMISSIONS_REQUEST_WRITE:
//                if (grantResults.length <= 0
//                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(MainActivity.this,
//                            R.string.permission_denied_to_write, Toast.LENGTH_SHORT).show();
//                    android.support.v4.app.Fragment fragment = NoPermissionFragment.newInstance(Manifest.permission.WRITE_EXTERNAL_STORAGE, workMode);
//                    android.support.v4.app.FragmentTransaction fragmentTransaction =
//                            getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.mainactivityContainer, fragment, CURRENT_TAG);
//                    fragmentTransaction.commitAllowingStateLoss();
//                }else{
//                    loadHomeFragment(CURRENT_TAG);
//                }
//
//        }
    }
}