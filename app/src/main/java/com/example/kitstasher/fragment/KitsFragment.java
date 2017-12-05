package com.example.kitstasher.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitstasher.R;
import com.example.kitstasher.adapters.AdapterViewStash;
import com.example.kitstasher.other.Constants;

/**
 * Created by Алексей on 21.04.2017. ViewPager
 */

public class KitsFragment extends Fragment {
    AdapterViewStash adapterViewStash;

    public KitsFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewstash, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tabsViewStash);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        boolean aftermarketMode = getArguments().getBoolean(Constants.AFTERMARKET_MODE);
//        char workMode = getArguments().getChar(Constants.WORK_MODE);

        ViewPager viewPager = view.findViewById(R.id.viewpagerViewStash);
        adapterViewStash = new AdapterViewStash(getChildFragmentManager(), getActivity(), aftermarketMode);
        viewPager.setAdapter(adapterViewStash);

        Bundle bundle = getArguments();
        if (!bundle.isEmpty()) {

            int currentTab = getArguments().getInt(Constants.LIST_CATEGORY);
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

    public void refreshPages() {
        adapterViewStash.notifyDataSetChanged();
    }
}
