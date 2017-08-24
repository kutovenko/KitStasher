package com.example.kitstasher.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.SettingsBrandsFragment;
import com.example.kitstasher.fragment.SettingsOptionsFragment;
import com.example.kitstasher.fragment.SettingsTrashFragment;

/**
 * Created by Алексей on 03.05.2017.
 */

public class AdapterSettings extends FragmentPagerAdapter {

    private static final int FRAGMENT_COUNT = 2;
    private Context mContext;

    public AdapterSettings(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new SettingsOptionsFragment();
            case 1:
                return new SettingsBrandsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return mContext.getResources().getString(R.string.options);
            case 1:
                return mContext.getResources().getString(R.string.Brands);
        }
        return null;
    }

    public static Fragment openOptions(){
        return new SettingsOptionsFragment();
    }
    public static Fragment openBrands(){
        return new SettingsBrandsFragment();
    }
//    public static Fragment openTrash(){
//        return new SettingsTrashFragment();
//    }

}
