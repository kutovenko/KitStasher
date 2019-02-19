package com.kutovenko.kitstasher.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.databinding.ActivityMainBinding;
import com.kutovenko.kitstasher.ui.fragment.KitsFragment;
import com.kutovenko.kitstasher.ui.fragment.SettingsAboutFragment;
import com.kutovenko.kitstasher.ui.fragment.SettingsFragment;
import com.kutovenko.kitstasher.ui.fragment.StatisticsFragment;
import com.kutovenko.kitstasher.network.AsyncApp42ServiceApi;
import com.kutovenko.kitstasher.db.DbConnector;
import com.kutovenko.kitstasher.util.MyConstants;
import com.kutovenko.kitstasher.ui.fragment.LoginFragment;
import com.parse.ParseFacebookUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by Alexey on 10.04.2017.
 * Main Activity. Sets up Navigation Drawer and contains fragments for different tasks
 *
 * Основной Activity-класс. Содержит навигационную панель и загружает фрагменты просмотра коллекции
 * наборов.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ImageView imgProfile;
    private TextView tvName;
    private Handler mHandler;

    private static final String TAG_HOME = "home";
    private static final String TAG_ADDSTASH = "addstash";
    private static final String TAG_VIEWSTASH = "viewstash";
    private static final String TAG_PAINTS = "paints";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_AFTERMARKET = "aftermarket";
    private static final String TAG_STATISTICS = "statistics";
    private static final String TAG_ABOUT = "about";
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
    private String workMode;
    private ActivityMainBinding binding;
    //private CallbackManager callbackManager;

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
//        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        if (savedInstanceState != null) {
            title = savedInstanceState.getString("title");
            workMode = savedInstanceState.getString("mode");
        }
        binding = DataBindingUtil.setContentView(this, com.kutovenko.kitstasher.R.layout.activity_main);
        asyncService = AsyncApp42ServiceApi.instance(this);
        sharedPreferences = this.getSharedPreferences(MyConstants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);

        dbConnector = new DbConnector(this);
        dbConnector.open();

        workMode = MyConstants.TYPE_KIT;

        binding.appbarmain.toolbar.setTitle(title);
        setSupportActionBar(binding.appbarmain.toolbar);

        View navHeader = binding.navView.getHeaderView(0);
        tvName = navHeader.findViewById(com.kutovenko.kitstasher.R.id.name);
        imgProfile = navHeader.findViewById(com.kutovenko.kitstasher.R.id.img_profile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);

                                        LoginFragment fragment = new LoginFragment();
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, fragment);
                fragmentTransaction.commit();


            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.appbarmain.toolbar, com.kutovenko.kitstasher.R.string.navigation_drawer_open, com.kutovenko.kitstasher.R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        mHandler = new Handler();
        binding.navView.setNavigationItemSelectedListener(this);

        setUpNavigationView();

        loadNavHeader();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_VIEWSTASH;
            loadHomeFragment(workMode);
        }
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
        binding.navView.setNavigationItemSelectedListener
                (new NavigationView.OnNavigationItemSelectedListener() {

                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case com.kutovenko.kitstasher.R.id.nav_viewstash:
                                navItemIndex = 0;
                                CURRENT_TAG = TAG_VIEWSTASH;
                                workMode = MyConstants.TYPE_KIT;
                                break;
                            case com.kutovenko.kitstasher.R.id.nav_aftermarket:
                                navItemIndex = 1;
                                CURRENT_TAG = TAG_AFTERMARKET;
                                workMode = MyConstants.TYPE_AFTERMARKET;
                                break;
                            case com.kutovenko.kitstasher.R.id.nav_paints:
                                navItemIndex = 2;
                                CURRENT_TAG = TAG_PAINTS;
                                workMode = MyConstants.TYPE_SUPPLY;
                                break;
//                            case com.kutovenko.kitstasher.R.id.nav_search:
//                                navItemIndex = 3;
//                                CURRENT_TAG = TAG_SEARCH;
//                                workMode = MyConstants.TYPE_KIT;
//                                break;
                            case com.kutovenko.kitstasher.R.id.nav_statistics:
                                navItemIndex = 3;
                                CURRENT_TAG = TAG_STATISTICS;
                                workMode = MyConstants.TYPE_KIT;
                                break;
                            case com.kutovenko.kitstasher.R.id.nav_settings:
                                navItemIndex = 4;
                                CURRENT_TAG = TAG_SETTINGS;
                                workMode = MyConstants.TYPE_KIT;
                                break;
                            case com.kutovenko.kitstasher.R.id.nav_about:
                                navItemIndex = 5;
                                CURRENT_TAG = TAG_ABOUT;
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

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                binding.appbarmain.toolbar, com.kutovenko.kitstasher.R.string.openDrawer, com.kutovenko.kitstasher.R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers();
            return;
        }
        navItemIndex = 0;
        CURRENT_TAG = TAG_ADDSTASH;
        loadHomeFragment(workMode);
    }

    public void setActionBarTitle(String t) {
        title = t;
        binding.appbarmain.toolbar.setTitle(title);
    }

    public void loadHomeFragment(final String workMode) {
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            binding.drawerLayout.closeDrawers();
            return;
        }
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putString(MyConstants.ITEM_TYPE, workMode);
                Fragment fragment = getHomeFragment();
                fragment.setArguments(bundle);//
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        mHandler.post(mPendingRunnable);
        binding.drawerLayout.closeDrawers();
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        Fragment kitsFragment = new KitsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MyConstants.ITEM_TYPE, workMode);
        kitsFragment.setArguments(bundle);

        switch (navItemIndex) {
            case 0:
                return kitsFragment;
            case 1:
                return kitsFragment;
            case 2:
                return kitsFragment;
            case 3:
                return new StatisticsFragment();
            case 4:
                return new SettingsFragment();
            case 5:
                return SettingsAboutFragment.newInstance();
            default:
                Fragment defFragment = new KitsFragment();
                Bundle defBundle = new Bundle();
                defBundle.putString(MyConstants.ITEM_TYPE, workMode);
                defFragment.setArguments(defBundle);
                return defFragment;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == com.kutovenko.kitstasher.R.id.nav_viewstash) {
            navItemIndex = 0;
            loadHomeFragment(workMode);
        } else if (id == com.kutovenko.kitstasher.R.id.nav_aftermarket) {
            navItemIndex = 1;
            loadHomeFragment(workMode);
        } else if (id == com.kutovenko.kitstasher.R.id.nav_paints) {
            navItemIndex = 2;
            loadHomeFragment(workMode);
        } else if (id == com.kutovenko.kitstasher.R.id.nav_statistics) {
            navItemIndex = 3;
            loadHomeFragment(workMode);
        } else if (id == com.kutovenko.kitstasher.R.id.nav_settings) {
            navItemIndex = 4;
            loadHomeFragment(workMode);
        } else if (id == com.kutovenko.kitstasher.R.id.nav_about) {
            navItemIndex = 5;
            loadHomeFragment(workMode);
        }

        DrawerLayout drawer = findViewById(com.kutovenko.kitstasher.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_VIEW) {
//                String workMode = data.getStringExtra(MyConstants.ITEM_TYPE);
//                int position = data.getIntExtra(MyConstants.POSITION, 0);
//                String category = data.getStringExtra(MyConstants.CATEGORY);
//                Bundle bundle = new Bundle();
//                bundle.putInt(MyConstants.POSITION, position);
//                bundle.putString(MyConstants.CATEGORY, category);
//                bundle.putString(MyConstants.ITEM_TYPE, workMode);
//                KitsFragment fragment = KitsFragment.newInstance();
//                fragment.setArguments(bundle);
//                FragmentTransaction fragmentTransaction =
//                        getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, fragment);
//                fragmentTransaction.commit();
            }
        }

        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.mainactivityContainer);
        f.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}