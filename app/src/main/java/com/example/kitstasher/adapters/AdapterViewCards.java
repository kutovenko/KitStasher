package com.example.kitstasher.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Алексей on 20.09.2017.
 */

public class AdapterViewCards extends FragmentStatePagerAdapter {
    private Context context;
    private static int pos = 0;
    private List<Fragment> myFragments;

    public AdapterViewCards(Context context, FragmentManager fm, List<Fragment> myFrags) {
        super(fm);
        this.context = context;
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

//    public void add(Class<Fragment> c, String title, Bundle b) {
//        myFragments.add(Fragment.instantiate(context,c.getName(),b));
//        categories.add(title);
//    }

    public static void setPos(int pos) {
        AdapterViewCards.pos = pos;
    }
}
