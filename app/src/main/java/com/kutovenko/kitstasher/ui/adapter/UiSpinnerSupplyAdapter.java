package com.kutovenko.kitstasher.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class UiSpinnerSupplyAdapter  extends BaseAdapter {
    private String[] categories;
    private LayoutInflater inflater;

    public UiSpinnerSupplyAdapter(Context applicationContext, String[] categories) {
        this.categories = categories;
        inflater = (LayoutInflater.from(applicationContext));

    }

    @Override
    public int getCount() {
        return categories.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(com.kutovenko.kitstasher.R.layout.item_spinner_supply, viewGroup, false);
        TextView category = view.findViewById(com.kutovenko.kitstasher.R.id.tvItemName);
        category.setText(categories[i]);
        return view;
    }
}
