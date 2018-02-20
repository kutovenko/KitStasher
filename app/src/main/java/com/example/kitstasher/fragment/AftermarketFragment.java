package com.example.kitstasher.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitstasher.R;
import com.example.kitstasher.objects.CustomKitsViewPager;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;

/**
 * Created by Алексей on 29.09.2017. Fragment to display pager with aftermarket
 */

public class AftermarketFragment extends Fragment {
    private static CustomKitsViewPager viewPager;

    public AftermarketFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewstash, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tabsViewStash);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        DbConnector dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        Cursor cursor = dbConnector.getAfterActiveCategories();
        viewPager = view.findViewById(R.id.viewpagerViewStash);
//        AdapterViewStash adapter = new AdapterViewStash(getChildFragmentManager(), getActivity(), true, cursor);
//        viewPager.setAdapter(adapter);
        Bundle bundle = getArguments();
        if (!bundle.isEmpty()) {
            int currentTab = getArguments().getInt(MyConstants.CATEGORY_TAB);
            if (currentTab != 0) {
                viewPager.setCurrentItem(currentTab);
            }
        } else {
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
