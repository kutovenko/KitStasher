package com.example.kitstasher.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.adapters.FragmentAddAdapter;
import com.example.kitstasher.other.MyConstants;
import com.example.kitstasher.other.OnFragmentInteractionListener;

/**
 * Created by Алексей on 21.04.2017. Fragment with scan and manual add
 */

public class AddFragment extends Fragment implements OnFragmentInteractionListener {
    public String passBarcode;
    public char passWorkMode;
    public AddFragment(){

    }

    public static AddFragment newInstance() {
        return new AddFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tabsAdd);
        ViewPager viewPager = view.findViewById(R.id.viewpagerAdd);
        boolean aftermarketMode = getArguments().getBoolean(MyConstants.AFTERMARKET_MODE);
        viewPager.setAdapter(new FragmentAddAdapter(getChildFragmentManager(), getActivity(), aftermarketMode));
        tabLayout.setupWithViewPager(viewPager);

        ((MainActivity) getActivity())
                .setActionBarTitle(getActivity().getResources().getString(R.string.nav_manual));

        return view;
    }

    @Override
    public void onFragmentInteraction(String barcode, char mode) {
        FragmentManager fragmentManager = getChildFragmentManager();
        ScanFragment scanFragment = (ScanFragment) fragmentManager.findFragmentByTag(ScanFragment.scanTag);
        ManualAddFragment manualAddFragment = (ManualAddFragment)fragmentManager.findFragmentByTag(ManualAddFragment.manualTag);
        if(scanFragment != null)
        {
            passBarcode = scanFragment.getBarcode();
            passWorkMode = scanFragment.getWorkMode();
            manualAddFragment.onFragmentInteraction(passBarcode, passWorkMode);
        }
    }
}
