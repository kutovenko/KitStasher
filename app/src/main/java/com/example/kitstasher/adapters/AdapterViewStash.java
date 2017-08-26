package com.example.kitstasher.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.SortAirFragment;
import com.example.kitstasher.fragment.SortAllFragment;
import com.example.kitstasher.fragment.SortCarBikeFragment;
import com.example.kitstasher.fragment.SortFantasyFragment;
import com.example.kitstasher.fragment.SortFiguresFragment;
import com.example.kitstasher.fragment.SortGroundFragment;
import com.example.kitstasher.fragment.SortOtherFragment;
import com.example.kitstasher.fragment.SortSeaFragment;
import com.example.kitstasher.fragment.SortSpaceFragment;
import com.example.kitstasher.fragment.ViewStashFragment;

/**
 * Created by Алексей on 22.04.2017.
 */

public class AdapterViewStash extends FragmentStatePagerAdapter {
    private static int FRAGMENT_COUNT = 9;
    private Context mContext;

    public AdapterViewStash(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new SortAllFragment();
            case 1:
                return new SortAirFragment();
            case 2:
                return new SortGroundFragment();
            case 3:
                return new SortSeaFragment();
            case 4:
                return new SortSpaceFragment();
            case 5:
                return new SortCarBikeFragment();
            case 6:
                return new SortFiguresFragment();
            case 7:
                return new SortFantasyFragment();
            case 8:
                return new SortOtherFragment();

        }
        return null;
    }

//            @Override
//            public Object instantiateItem(ViewGroup container, int position) {
//
//                ListView listView = new ListView(mContext);
////                listView.setTag();
//                return super.instantiateItem(container, position);
//            }

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

    public static Fragment openViewStash(){
        return new ViewStashFragment();
    }

}
