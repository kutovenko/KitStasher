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
 * Created by Алексей on 21.04.2017. Adapter for More section with 3 pages in ViewPager
 */

public class SettingsFragment extends Fragment{

    public SettingsFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabsSettings);
        ViewPager viewPager = view.findViewById(R.id.viewpagerSettings);
        AdapterSettings adapterSettings = new AdapterSettings(getChildFragmentManager(), getActivity());
        viewPager.setAdapter(adapterSettings);
        tabLayout.setupWithViewPager(viewPager);

        ((MainActivity) getActivity())
                .setActionBarTitle(getActivity().getResources().getString(R.string.nav_more));

        return view;
    }
}
