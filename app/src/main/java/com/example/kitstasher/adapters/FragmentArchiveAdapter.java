package com.example.kitstasher.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.view.ViewGroup;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.ArchiveFragment;
import com.example.kitstasher.fragment.ArchiveItemsFragment;
import com.example.kitstasher.fragment.KitsFragment;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.objects.PaintItem;

import java.util.ArrayList;

/**
 * Created by Алексей on 22.04.2017. Adapter for viewPager with kits and aftermarket
 */

public class FragmentArchiveAdapter extends FragmentStatePagerAdapter {
    private final int PAGER_PAGES = 3;
    private Context context;
    private ArrayList<Bundle> archivedItems;
//    private ArrayList<Kit> archivedAftermarket;
//    private ArrayList<PaintItem> archivedPaints;
//    private SparseArrayCompat<String> mFragmentTags; //tags
//    private FragmentManager mFragmentManager;
//    private long baseId = 0;

    public FragmentArchiveAdapter(Context context,
                                  ArrayList<Bundle> archivedItems, FragmentManager mFragmentManager) {
        super(mFragmentManager);
        this.context = context;
        this.archivedItems = archivedItems;
    }


    @Override
    public int getCount(){
        return PAGER_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = archivedItems.get(position);
        return ArchiveItemsFragment.newInstance(bundle);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return context.getResources().getString(R.string.kits);
            case 1:
                return context.getResources().getString(R.string.aftermarket);
            case 2:
                return context.getResources().getString(R.string.paints);
            default:
                return context.getResources().getString(R.string.kits);
        }
    }
}
