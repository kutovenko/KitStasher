package com.example.kitstasher.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.other.MyConstants;

/**
 * Created by Алексей on 03.09.2017.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {
//    Button btnAddAction, btnSearchAction, btnViewStashAction, btnStatisticsAction, btnMylistsAction;

    public HomeFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button btnAddAction = view.findViewById(R.id.btnAddKitAction);
        btnAddAction.setOnClickListener(this);
        Button btnViewStashAction = view.findViewById(R.id.btnViewStashAction);
        btnViewStashAction.setOnClickListener(this);
        Button btnMylistsAction = view.findViewById(R.id.btnMylistsAction);
        btnMylistsAction.setOnClickListener(this);
        Button btnSearchAction = view.findViewById(R.id.btnSearchAction);
        btnSearchAction.setOnClickListener(this);
        Button btnStatisticsAction = view.findViewById(R.id.btnStatisticsAction);
        btnStatisticsAction.setOnClickListener(this);
        Button btnAftermarketAction = view.findViewById(R.id.btnAftermarketAction);
        btnAftermarketAction.setOnClickListener(this);

        ((MainActivity) getActivity())
                .setActionBarTitle(getActivity().getResources().getString(R.string.nav_home));

        return view;
    }
    @Override
    public void onClick(View view) {
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();

        switch (view.getId()){
            case R.id.btnAddKitAction:
                AddFragment addFragment = new AddFragment();
                bundle.putBoolean(MyConstants.AFTERMARKET_MODE, false);
                bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_KIT);
                addFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.mainactivityContainer, addFragment);
                fragmentTransaction.commit();
                break;
            case R.id.btnSearchAction:
                SearchFragment searchFragment = new SearchFragment();
                fragmentTransaction.replace(R.id.mainactivityContainer, searchFragment);
                fragmentTransaction.commit();
                break;
            case R.id.btnViewStashAction:
                KitsFragment kitsFragment = new KitsFragment();
                bundle.putInt(MyConstants.LIST_CATEGORY, 0);
                bundle.putBoolean(MyConstants.AFTERMARKET_MODE, false);
                bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_KIT);
                kitsFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.mainactivityContainer, kitsFragment);
                fragmentTransaction.commit();
                break;
            case R.id.btnAftermarketAction:
                AftermarketFragment aftermarketFragment = new AftermarketFragment();
                bundle.putInt(MyConstants.LIST_CATEGORY, 0);
                bundle.putBoolean(MyConstants.AFTERMARKET_MODE, true);
                bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_AFTERMARKET);
                aftermarketFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.mainactivityContainer, aftermarketFragment);
                fragmentTransaction.commit();
                break;
            case R.id.btnMylistsAction:
                MyListsFragment myListsFragment = new MyListsFragment();
                fragmentTransaction.replace(R.id.mainactivityContainer, myListsFragment);
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