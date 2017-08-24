package com.example.kitstasher.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.ManualAddFragment;
import com.example.kitstasher.fragment.ScanFragment;

/**
 * Created by Алексей on 21.04.2017.
 */

public class AdapterAddFragment extends FragmentPagerAdapter {
//    private static final String TAG = AdapterAddFragment.class.getSimpleName();
    private static final int FRAGMENT_COUNT = 2;
    private Context mContext;


    public AdapterAddFragment(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ScanFragment();
            case 1:
                return new ManualAddFragment();
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
