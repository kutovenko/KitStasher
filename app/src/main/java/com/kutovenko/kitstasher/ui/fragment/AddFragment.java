package com.kutovenko.kitstasher.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.databinding.FragmentAddBinding;
import com.kutovenko.kitstasher.ui.MainActivity;
import com.kutovenko.kitstasher.ui.adapter.FragmentAddAdapter;
import com.kutovenko.kitstasher.util.MyConstants;
import com.kutovenko.kitstasher.ui.listener.OnFragmentInteractionListener;

/**
 * Created by Алексей on 21.04.2017. Fragment with scan and manual add for kits.
 *
 * Фрагмент с пейджером для фрагментов сканера и ручного добавления набора и афтермаркета.
 */

public class AddFragment extends Fragment implements OnFragmentInteractionListener {

    public AddFragment(){

    }

    public static AddFragment newInstance() {
        return new AddFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAddBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_add, container, false);
        assert getArguments() != null;
        String workMode = getArguments().getString(MyConstants.ITEM_TYPE, MyConstants.TYPE_KIT);
        binding.viewpagerAdd.setAdapter(new FragmentAddAdapter(getChildFragmentManager(), getActivity(), workMode));
        binding.tabsAdd.setupWithViewPager(binding.viewpagerAdd);

        ((MainActivity) getActivity())
                .setActionBarTitle(getActivity().getResources().getString(com.kutovenko.kitstasher.R.string.nav_manual));

        return binding.getRoot();
    }

    @Override
    public void onFragmentInteraction(String barcode, String mode) {
        FragmentManager fragmentManager = getChildFragmentManager();
        ScanFragment scanFragment = (ScanFragment) fragmentManager.findFragmentByTag(ScanFragment.scanTag);
        ManualAddFragment manualAddFragment = (ManualAddFragment)fragmentManager.findFragmentByTag(ManualAddFragment.manualTag);
        if(scanFragment != null)
        {
            String passBarcode = scanFragment.getBarcode();
            String passWorkMode = scanFragment.getWorkMode();
            manualAddFragment.onFragmentInteraction(passBarcode, passWorkMode);
        }
    }
}
