package com.kutovenko.kitstasher.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.databinding.FragmentListWithEditBinding;
import com.kutovenko.kitstasher.model.ShopItem;
import com.kutovenko.kitstasher.db.DbConnector;
import com.kutovenko.kitstasher.ui.adapter.FragmentShopAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Created by Алексей on 30.08.2017. Shows list of shops in More section
 */

public class SettingsMyShopsFragment extends Fragment {

    public SettingsMyShopsFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentListWithEditBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_list_with_edit, container, false);
        Context context = getActivity();
        LinearLayoutManager rvBrandsManager = new LinearLayoutManager(context);
        binding.rvBrands.setHasFixedSize(true);
        binding.rvBrands.setLayoutManager(rvBrandsManager);
        binding.rvBrands.setItemAnimator(new DefaultItemAnimator());
        String sortBy = DbConnector.MYSHOPS_COLUMN_SHOP_NAME;
        DbConnector db = new DbConnector(context);
        db.open();
        ArrayList<ShopItem> items = db.getShops(sortBy);

        FragmentShopAdapter shopsAdapter = new FragmentShopAdapter(context, items);
        shopsAdapter.setHasStableIds(true); //for animation on delete
        binding.rvBrands.setAdapter(shopsAdapter);

        return binding.getRoot();
    }
}