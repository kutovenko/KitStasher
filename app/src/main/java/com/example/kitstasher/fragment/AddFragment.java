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
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.adapters.AdapterAddFragment;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.OnFragmentInteractionListener;

/**
 * Created by Алексей on 21.04.2017.
 */

public class AddFragment extends Fragment implements OnFragmentInteractionListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public String passBarcode;
    public char passWorkMode;
    private boolean aftermarketMode;
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


        tabLayout = view.findViewById(R.id.tabsAdd);
        viewPager = view.findViewById(R.id.viewpagerAdd);
        boolean aftermarketMode = getArguments().getBoolean(Constants.AFTERMARKET_MODE);
        viewPager.setAdapter(new AdapterAddFragment(getChildFragmentManager(), getActivity(), aftermarketMode));
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
        //Tag of your fragment which you should use when you add

        if(scanFragment != null)
        {
            // your some other frag need to provide some data back based on views.
            passBarcode = scanFragment.getBarcode();
            passWorkMode = scanFragment.getWorkMode();
            manualAddFragment.onFragmentInteraction(passBarcode, passWorkMode);
        }
    }
}
