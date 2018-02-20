package com.example.kitstasher.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.SortAllFragment;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Алексей on 22.04.2017. Adapter for viewPager with kits and aftermarket
 */

public class AdapterViewStash extends FragmentStatePagerAdapter {
    private Context context;
    private Cursor cursor;
    private int activeCategoriesCount;
    private List<Fragment> fragments;

    public AdapterViewStash(FragmentManager fm, Context context, boolean aftermarketMode, Cursor cursor) {
        super(fm);
        this.context = context;
        this.cursor = cursor;
        this.activeCategoriesCount = cursor.getCount();
        fragments = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putInt(MyConstants.CATEGORY_TAB, 0);
        bundle.putString(MyConstants.CATEGORY, MyConstants.EMPTY);
        bundle.putBoolean(MyConstants.AFTERMARKET_MODE, aftermarketMode);
        fragments.add(Fragment.instantiate(context, SortAllFragment.class.getName(), bundle));
        cursor.moveToFirst();
        for (int i = 0; i < activeCategoriesCount; i++) {
            String cat = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));
            bundle = new Bundle();
            bundle.putInt(MyConstants.CATEGORY_TAB, i + 1);
            bundle.putString(MyConstants.CATEGORY, cat);
            bundle.putBoolean(MyConstants.AFTERMARKET_MODE, aftermarketMode);
            fragments.add(Fragment.instantiate(context, SortAllFragment.class.getName(), bundle));
            cursor.moveToNext();
        }
    }

    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);

    }

    @Override
    public int getCount() {
        if (activeCategoriesCount == 0) {
            return 1;
        } else {
            return activeCategoriesCount + 1;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        ArrayList<Integer> names = new ArrayList<>();
        names.add(R.string.all);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String cat = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));
            names.add(getTitleName(cat));
            cursor.moveToNext();
        }
        return context.getResources().getString(names.get(position));
    }

    private int getTitleName(String cat) {
        switch (cat) {
            case "0":
                return R.string.unknown;
            case "1":
                return R.string.Air;
            case "2":
                return R.string.Ground;
            case "3":
                return R.string.Sea;
            case "4":
                return R.string.Space;
            case "5":
                return R.string.Auto_moto;
            case "6":
                return R.string.Figures;
            case "7":
                return R.string.Fantasy;
            case "8":
                return R.string.Other;
        }
        return 0;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
