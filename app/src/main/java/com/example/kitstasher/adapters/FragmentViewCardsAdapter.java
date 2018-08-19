package com.example.kitstasher.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Алексей on 20.09.2017. Viev cards adapter for ViewActivity.
 *
 * Просмотр списка карточек наборов. Используется ViewActivity.
 */

public class FragmentViewCardsAdapter extends FragmentStatePagerAdapter {
    private static int pos = 0;
    private List<Fragment> myFragments;

    public FragmentViewCardsAdapter(FragmentManager fm, List<Fragment> myFrags) {
        super(fm);
        this.myFragments = myFrags;
    }

    @Override
    public Fragment getItem(int position) {
        return myFragments.get(position);
    }

    @Override
    public int getCount() {
        return myFragments.size();
    }

    public static int getPos() {
        return pos;
    }

    public static void setPos(int pos) {
        FragmentViewCardsAdapter.pos = pos;
    }
}
