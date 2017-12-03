package com.example.kitstasher.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.adapters.AdapterSettings;

/**
 * Created by Алексей on 21.04.2017.
 */

public class SettingsFragment extends Fragment{
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public SettingsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        tabLayout = (TabLayout)view.findViewById(R.id.tabsSettings);
        viewPager = (ViewPager)view.findViewById(R.id.viewpagerSettings);
        viewPager.setAdapter(new AdapterSettings(getChildFragmentManager(), getActivity()));
        tabLayout.setupWithViewPager(viewPager);

        ((MainActivity) getActivity())
                .setActionBarTitle(getActivity().getResources().getString(R.string.nav_more));

        return view;
    }
}
