package com.example.kitstasher.fragment;

import android.content.Context;
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
import com.example.kitstasher.adapters.FragmentBrandAdapter;
import com.example.kitstasher.objects.BrandItem;
import com.example.kitstasher.other.DbConnector;

import java.util.ArrayList;

/**
 * Created by Алексей on 21.04.2017. Shows brands list in More section
 */

public class SettingsBrandsFragment extends Fragment {

    public SettingsBrandsFragment(){
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
        RecyclerView rvBrands = view.findViewById(R.id.rvBrands);
        LinearLayoutManager rvBrandsManager = new LinearLayoutManager(context);
        rvBrands.setHasFixedSize(true);
        rvBrands.setLayoutManager(rvBrandsManager);
        DefaultItemAnimator animator = new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
                return true;
            }
        };
        rvBrands.setItemAnimator(animator);
        String sortBy = DbConnector.BRANDS_COLUMN_BRAND;
        DbConnector db = new DbConnector(context);
        db.open();
        ArrayList<BrandItem> items = db.getBrands(sortBy);

        FragmentBrandAdapter brandAdapter = new FragmentBrandAdapter(context, items);
//        brandAdapter.setHasStableIds(true); //for animation on delete
        rvBrands.setAdapter(brandAdapter);

        return view;
    }

}
