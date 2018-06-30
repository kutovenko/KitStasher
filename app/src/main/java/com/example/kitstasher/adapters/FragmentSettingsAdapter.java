package com.example.kitstasher.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.SettingsBrandsFragment;
import com.example.kitstasher.fragment.SettingsMyShopsFragment;
import com.example.kitstasher.fragment.SettingsOptionsFragment;

/**
 * Created by Алексей on 03.05.2017. Adapter for Settings Fragment
 */

public class FragmentSettingsAdapter extends FragmentPagerAdapter {

    private static final int FRAGMENT_COUNT = 3;
    private Context mContext;

    public FragmentSettingsAdapter(FragmentManager fm, Context context) {
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
            case 2:
                return new SettingsMyShopsFragment();
        }
        return null;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
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
            case 2:
                return mContext.getResources().getString(R.string.shops);

        }
        return null;
    }
}