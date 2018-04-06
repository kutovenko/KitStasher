package com.example.kitstasher.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitstasher.R;
import com.example.kitstasher.adapters.AdapterViewStash;
import com.example.kitstasher.objects.CustomKitsViewPager;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;

/**
 * Created by Алексей on 21.04.2017. ViewPager
 */

public class KitsFragment extends Fragment {
    private static CustomKitsViewPager viewPager;
    private DbConnector dbConnector;
    private Cursor cursor;
    private AdapterViewStash adapter;

    public KitsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewstash, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tabsViewStash);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        final boolean aftermarketMode = getArguments().getBoolean(MyConstants.AFTERMARKET_MODE);
        if (aftermarketMode) {
            cursor = dbConnector.getAfterActiveCategories();
        } else {
            cursor = dbConnector.getActiveCategories();
        }
        viewPager = view.findViewById(R.id.viewpagerViewStash);
        adapter = new AdapterViewStash(getChildFragmentManager(), getActivity(), aftermarketMode, cursor);
        viewPager.setAdapter(adapter);
        Bundle bundle = getArguments();
        if (!bundle.isEmpty()) {
            int currentTab = getArguments().getInt(MyConstants.CATEGORY_TAB);
            if (currentTab != 0) {
                viewPager.setCurrentItem(currentTab);
            }
        } else {
            viewPager.setCurrentItem(0);
        }
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                if (!aftermarketMode) {
                    Bundle bundle = new Bundle();
                    AddFragment addFragment = new AddFragment();
                    bundle.putBoolean(MyConstants.AFTERMARKET_MODE, false);
                    bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_KIT);
                    addFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.mainactivityContainer, addFragment);
                    fragmentTransaction.commit();
                } else {
                    Bundle bundle = new Bundle();
                    ManualAddFragment manualAddFragment = ManualAddFragment.newInstance();
                    bundle.putBoolean(MyConstants.AFTERMARKET_MODE, true);
                    bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_AFTERMARKET);
                    manualAddFragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.mainactivityContainer, manualAddFragment);
                    fragmentTransaction.commit();
                }
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        boolean aftermarketMode = getArguments().getBoolean(MyConstants.AFTERMARKET_MODE);
        if (aftermarketMode) {
            cursor = dbConnector.getAfterActiveCategories();
        } else {
            cursor = dbConnector.getActiveCategories();
        }
        adapter = new AdapterViewStash(getChildFragmentManager(), getActivity(), aftermarketMode, cursor);
        viewPager.setAdapter(adapter);
        Bundle bundle = getArguments(); //todo тут проблема возврата все время в прошлую категорию?
        if (!bundle.isEmpty()) {
            int currentTab = getArguments().getInt(MyConstants.CATEGORY_TAB);
            if (currentTab != 0) {
                viewPager.setCurrentItem(currentTab);
            }
        } else {
            int currentTab = 0;
            viewPager.setCurrentItem(currentTab);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void refreshPages() {
        viewPager.refresh();
    }

//    public static void showFab(){
//        fab.show();
//    }
//
//    public static void hideFab(){
//        fab.hide();
//    }

//    public static int getCategoryTab(String category){
//
//        return 0;
//    }
}
