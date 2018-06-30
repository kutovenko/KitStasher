package com.example.kitstasher.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.ManualAddFragment;
import com.example.kitstasher.fragment.ScanFragment;
import com.example.kitstasher.other.MyConstants;

/**
 * Created by Алексей on 21.04.2017. Dispatch between scan and manual add
 */

public class FragmentAddAdapter extends FragmentPagerAdapter {
    private static final int FRAGMENT_COUNT = 2;
    private Context mContext;
    private boolean aftermarketMode;

    public FragmentAddAdapter(FragmentManager fm, Context context, boolean aftermarketMode) {
        super(fm);
        mContext = context;
        this.aftermarketMode = aftermarketMode;
    }
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(MyConstants.AFTERMARKET_MODE, aftermarketMode);

        switch (position){
            case 0:
                ScanFragment scanFragment = new ScanFragment();
                scanFragment.setArguments(bundle);
                return scanFragment;
            case 1:
                ManualAddFragment manualAddFragment = new ManualAddFragment();
                bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_KIT); // TODO: 02.04.2018 обработать вызов из афтермаркета 
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
                return mContext.getResources().getString(R.string.Scan_barcode);
            case 1:
                return mContext.getResources().getString(R.string.Manual_add);
        }
        return null;
    }

    public static Fragment openManualAdd(){
        return new ManualAddFragment();
    }
    public static Fragment openScan(){
        return new ScanFragment();
    }
}
