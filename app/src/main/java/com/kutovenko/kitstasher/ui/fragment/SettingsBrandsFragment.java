package com.kutovenko.kitstasher.ui.fragment;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.databinding.FragmentListWithEditBinding;
import com.kutovenko.kitstasher.ui.adapter.FragmentBrandAdapter;
import com.kutovenko.kitstasher.model.BrandItem;
import com.kutovenko.kitstasher.db.DbConnector;

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
        FragmentListWithEditBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_list_with_edit, container, false);

        Context context = getActivity();
        LinearLayoutManager rvBrandsManager = new LinearLayoutManager(context);
        binding.rvBrands.setHasFixedSize(true);
        binding.rvBrands.setLayoutManager(rvBrandsManager);
        DefaultItemAnimator animator = new DefaultItemAnimator() {
            @Override
            public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
                return true;
            }
        };
        binding.rvBrands.setItemAnimator(animator);
        String sortBy = DbConnector.BRANDS_COLUMN_BRAND;
        DbConnector db = new DbConnector(context);
        db.open();
        ArrayList<BrandItem> items = db.getAllBrands(sortBy);

        FragmentBrandAdapter brandAdapter = new FragmentBrandAdapter(context, items);
        binding.rvBrands.setAdapter(brandAdapter);

        return binding.getRoot();
    }
}
