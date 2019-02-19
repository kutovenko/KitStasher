package com.kutovenko.kitstasher.ui.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Алексей on 20.09.2017. Viev cards adapter for ViewActivity.
 *
 * Просмотр списка карточек наборов. Используется ViewActivity.
 */

public class FragmentViewCardsAdapter extends FragmentStatePagerAdapter {
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
}
