package com.example.kitstasher.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kitstasher.R;
import com.example.kitstasher.other.Constants;

/**
 * Created by Алексей on 03.09.2017.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {
    Button btnAddAction, btnSearchAction, btnViewStashAction, btnStatisticsAction;

    public HomeFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btnAddAction = (Button)view.findViewById(R.id.btnAddKitAction);
        btnAddAction.setOnClickListener(this);
        btnSearchAction = (Button)view.findViewById(R.id.btnSearchAction);
        btnSearchAction.setOnClickListener(this);
        btnViewStashAction = (Button)view.findViewById(R.id.btnViewStashAction);
        btnViewStashAction.setOnClickListener(this);
        btnStatisticsAction = (Button)view.findViewById(R.id.btnStatisticsAction);
        btnStatisticsAction.setOnClickListener(this);


        return view;
    }
    @Override
    public void onClick(View view) {
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();

        switch (view.getId()){
            case R.id.btnAddKitAction:
                AddFragment addFragment = new AddFragment();
                fragmentTransaction.replace(R.id.mainactivityContainer, addFragment);
                fragmentTransaction.commit();
                break;
            case R.id.btnSearchAction:
                SearchFragment searchFragment = new SearchFragment();
                fragmentTransaction.replace(R.id.mainactivityContainer, searchFragment);
                fragmentTransaction.commit();
                break;
            case R.id.btnViewStashAction:
                ViewStashFragment viewStashFragment = new ViewStashFragment();
                Bundle bundle = new Bundle(1);
                bundle.putInt(Constants.LIST_CATEGORY, 0);
                viewStashFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.mainactivityContainer, viewStashFragment);
                fragmentTransaction.commit();
                break;
            case R.id.btnStatisticsAction:
                StatisticsFragment statisticsFragment = new StatisticsFragment();
                fragmentTransaction.replace(R.id.mainactivityContainer, statisticsFragment);
                fragmentTransaction.commit();
                break;
        }
    }
}