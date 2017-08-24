package com.example.kitstasher.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitstasher.R;
import com.example.kitstasher.adapters.AdapterAddFragment;
import com.example.kitstasher.other.OnFragmentInteractionListener;

/**
 * Created by Алексей on 21.04.2017.
 */

public class AddFragment extends Fragment implements OnFragmentInteractionListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public String passBarcode;
//    private OnFragmentInteractionListener mListener;
    public AddFragment(){

    }

    public static AddFragment newInstance() {
        return new AddFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);


        tabLayout = (TabLayout)view.findViewById(R.id.tabsAdd);
        viewPager = (ViewPager)view.findViewById(R.id.viewpagerAdd);
        viewPager.setAdapter(new AdapterAddFragment(getChildFragmentManager(), getActivity()));
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onFragmentInteraction(String barcode) {
        FragmentManager fragmentManager = getChildFragmentManager();
        ScanFragment scanFragment = (ScanFragment) fragmentManager.findFragmentByTag(ScanFragment.scanTag);
        ManualAddFragment manualAddFragment = (ManualAddFragment)fragmentManager.findFragmentByTag(ManualAddFragment.manualTag);
        //Tag of your fragment which you should use when you add

        if(scanFragment != null)
        {
            // your some other frag need to provide some data back based on views.
            passBarcode = scanFragment.getBarcode();
            manualAddFragment.onFragmentInteraction(passBarcode);
        }
    }
}
