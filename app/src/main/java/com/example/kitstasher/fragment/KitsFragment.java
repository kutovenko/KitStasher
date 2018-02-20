package com.example.kitstasher.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitstasher.R;
import com.example.kitstasher.adapters.AdapterViewStash;
import com.example.kitstasher.objects.CustomKitsViewPager;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;

/**
 * Created by Алексей on 21.04.2017. ViewPager
 */

public class KitsFragment extends Fragment {
    private static CustomKitsViewPager viewPager;

    public KitsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewstash, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tabsViewStash);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        DbConnector dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        Cursor cursor;
        boolean aftermarketMode = getArguments().getBoolean(MyConstants.AFTERMARKET_MODE);
        if (aftermarketMode) {
            cursor = dbConnector.getAfterActiveCategories();
        } else {
            cursor = dbConnector.getActiveCategories();
        }
        viewPager = view.findViewById(R.id.viewpagerViewStash);
        AdapterViewStash adapter = new AdapterViewStash(getChildFragmentManager(), getActivity(), aftermarketMode, cursor);
        viewPager.setAdapter(adapter);
        Bundle bundle = getArguments();
        if (!bundle.isEmpty()) {
            int currentTab = getArguments().getInt(MyConstants.CATEGORY_TAB);
            if (currentTab != 0) {
                viewPager.setCurrentItem(currentTab);
            }
        }else{
            int currentTab = 0;
            viewPager.setCurrentItem(currentTab);
        }
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    public static void refreshPages() {
        viewPager.refresh();
    }
}
