package com.kutovenko.kitstasher.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.kutovenko.kitstasher.ui.fragment.ManualAddFragment;
import com.kutovenko.kitstasher.ui.fragment.ScanFragment;
import com.kutovenko.kitstasher.util.MyConstants;

/**
 * Created by Алексей on 21.04.2017. Dispatch between scan and manual add
 *
 * Адаптер, обеспечивающий переход между ScanFragment и ManualAddFragment.
 */

public class FragmentAddAdapter extends FragmentPagerAdapter {
    private static final int FRAGMENT_COUNT = 2;
    private Context mContext;
    private String workMode;

    public FragmentAddAdapter(FragmentManager fm, Context context, String workMode) {
        super(fm);
        mContext = context;
        this.workMode = workMode;
    }
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(MyConstants.ITEM_TYPE, workMode);

        switch (position){
            case 0:
                ScanFragment scanFragment = new ScanFragment();
                scanFragment.setArguments(bundle);
                return scanFragment;
            case 1:
                ManualAddFragment manualAddFragment = new ManualAddFragment();
                bundle.putString(MyConstants.ITEM_TYPE, workMode);
                manualAddFragment.setArguments(bundle);
                return manualAddFragment;
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
                return mContext.getResources().getString(com.kutovenko.kitstasher.R.string.Scan_barcode);
            case 1:
                return mContext.getResources().getString(com.kutovenko.kitstasher.R.string.Manual_add);
        }
        return null;
    }
}
