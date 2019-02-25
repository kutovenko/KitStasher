package com.kutovenko.kitstasher.ui.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.databinding.FragmentSettingsBinding;
import com.kutovenko.kitstasher.ui.MainActivity;
import com.kutovenko.kitstasher.ui.adapter.FragmentSettingsAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;


/**
 * Created by Алексей on 21.04.2017. Adapter for More section with 3 pages in ViewPager
 */

public class SettingsFragment extends Fragment{

    public SettingsFragment(){
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSettingsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        FragmentSettingsAdapter fragmentSettingsAdapter = new FragmentSettingsAdapter(getChildFragmentManager(), getActivity());
        binding.viewpagerSettings.setAdapter(fragmentSettingsAdapter);
        binding.tabsSettings.setupWithViewPager(binding.viewpagerSettings);

        ((MainActivity) getActivity())
                .setActionBarTitle(getActivity().getResources().getString(com.kutovenko.kitstasher.R.string.nav_more));

        return binding.getRoot();
    }
}
