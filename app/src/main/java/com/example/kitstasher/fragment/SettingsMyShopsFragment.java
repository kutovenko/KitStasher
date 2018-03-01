package com.example.kitstasher.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitstasher.R;
import com.example.kitstasher.adapters.MyListCursorAdapter;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;

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
        View view = inflater.inflate(R.layout.fragment_list_with_edit, container, false);
        Context context = getActivity();
        RecyclerView rvShops = view.findViewById(R.id.rvBrands);
        LinearLayoutManager rvBrandsManager = new LinearLayoutManager(context);
        rvShops.setHasFixedSize(true);
        rvShops.setLayoutManager(rvBrandsManager);
        rvShops.setItemAnimator(new DefaultItemAnimator());
        String sortBy = DbConnector.MYSHOPS_COLUMN_SHOP_NAME;
        DbConnector db = new DbConnector(context);
        db.open();
        Cursor cursor = db.getShops(sortBy);

        MyListCursorAdapter shopsAdapter = new MyListCursorAdapter(cursor, context, MyConstants.MODE_A_SHOP);
        shopsAdapter.setHasStableIds(true); //for animation on delete
        rvShops.setAdapter(shopsAdapter);

        return view;
    }
}