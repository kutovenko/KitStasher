package com.example.kitstasher.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    SearchView searchView;

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
        setHasOptionsMenu(true);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SortAllFragment fr = (SortAllFragment) adapter.getItem(viewPager.getCurrentItem());
                fr.search(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                SortAllFragment fr = (SortAllFragment) adapter.getItem(viewPager.getCurrentItem());
                fr.search(query);
                return false;
            }

        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                SortAllFragment fr = (SortAllFragment) adapter.getItem(viewPager.getCurrentItem());
                fr.reloadList();
                return false;
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_search || super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void refreshPages() {
       viewPager.refresh();
    }
}
