package com.kutovenko.kitstasher.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.kutovenko.kitstasher.ui.fragment.SettingsBrandsFragment;
import com.kutovenko.kitstasher.ui.fragment.SettingsMyShopsFragment;
import com.kutovenko.kitstasher.ui.fragment.SettingsOptionsFragment;

/**
 * Created by Алексей on 03.05.2017. Adapter for Settings Fragment with ViewPager
 *
 * Адаптер для фрагмента Settings c ViewPAger.
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
                return mContext.getResources().getString(com.kutovenko.kitstasher.R.string.options);
            case 1:
                return mContext.getResources().getString(com.kutovenko.kitstasher.R.string.Brands);
            case 2:
                return mContext.getResources().getString(com.kutovenko.kitstasher.R.string.shops);

        }
        return null;
    }
}