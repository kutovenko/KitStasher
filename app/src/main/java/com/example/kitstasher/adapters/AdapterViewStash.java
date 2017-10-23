package com.example.kitstasher.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.SortAllFragment;
import com.example.kitstasher.other.Constants;

/**
 * Created by Алексей on 22.04.2017.
 */

public class AdapterViewStash extends FragmentStatePagerAdapter {
    private static int FRAGMENT_COUNT = 9;
    private Context mContext;
    private boolean aftermarketMode;

    public AdapterViewStash(FragmentManager fm, Context context, boolean aftermarketMode) {
        super(fm);
        mContext = context;
        this.aftermarketMode = aftermarketMode;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        SortAllFragment fragment = new SortAllFragment();
        switch (position){
            case 0:
                bundle.putInt("categoryTab", 0);
                bundle.putBoolean(Constants.AFTERMARKET_MODE, aftermarketMode);
                fragment.setArguments(bundle);
                return fragment;
            case 1:
                bundle.putInt("categoryTab", 1);
                bundle.putBoolean(Constants.AFTERMARKET_MODE, aftermarketMode);
                fragment.setArguments(bundle);
                return fragment;
            case 2:
                bundle.putInt("categoryTab", 2);
                bundle.putBoolean(Constants.AFTERMARKET_MODE, aftermarketMode);
                fragment.setArguments(bundle);
                return fragment;
            case 3:
                bundle.putInt("categoryTab", 3);
                bundle.putBoolean(Constants.AFTERMARKET_MODE, aftermarketMode);
                fragment.setArguments(bundle);
                return fragment;
            case 4:
                bundle.putInt("categoryTab", 4);
                bundle.putBoolean(Constants.AFTERMARKET_MODE, aftermarketMode);
                fragment.setArguments(bundle);
                return fragment;
            case 5:
                bundle.putInt("categoryTab", 5);
                bundle.putBoolean(Constants.AFTERMARKET_MODE, aftermarketMode);
                fragment.setArguments(bundle);
                return fragment;
            case 6:
                bundle.putInt("categoryTab", 6);
                bundle.putBoolean(Constants.AFTERMARKET_MODE, aftermarketMode);
                fragment.setArguments(bundle);
                return fragment;
            case 7:
                bundle.putInt("categoryTab", 7);
                bundle.putBoolean(Constants.AFTERMARKET_MODE, aftermarketMode);
                fragment.setArguments(bundle);
                return fragment;
            case 8:
                bundle.putInt("categoryTab", 8);
                bundle.putBoolean(Constants.AFTERMARKET_MODE, aftermarketMode);
                fragment.setArguments(bundle);
                return fragment;
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
                return mContext.getResources().getString(R.string.all);
            case 1:
                return mContext.getResources().getString(R.string.Air);
            case 2:
                return mContext.getResources().getString(R.string.Ground);
            case 3:
                return mContext.getResources().getString(R.string.Sea);
            case 4:
                return mContext.getResources().getString(R.string.Space);
            case 5:
                return mContext.getResources().getString(R.string.Auto_moto);
            case 6:
                return mContext.getResources().getString(R.string.Figures);
            case 7:
                return mContext.getResources().getString(R.string.Fantasy);
            case 8:
                return mContext.getResources().getString(R.string.Other);
        }
        return null;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

//    public static Fragment openViewStash(){
//        return new ViewStashFragment();
//    }

}
