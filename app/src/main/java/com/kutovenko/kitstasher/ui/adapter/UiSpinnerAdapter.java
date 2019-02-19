package com.kutovenko.kitstasher.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Алексей on 04.10.2017. Adapter for spinners in AddPaintFragment, ManualAddFragment.
 *
 * Адаптер для спиннеров в AddPaintFragment, ManualAddFragment.
 */

// TODO: 28.02.2018 RecyclerViewAdapter
public class UiSpinnerAdapter extends BaseAdapter {
    private int icons[];
    private String[] categories;
    private LayoutInflater inflater;

    public UiSpinnerAdapter(Context applicationContext, int[] icons, String[] categories) {
        this.icons = icons;
        this.categories = categories;
        inflater = (LayoutInflater.from(applicationContext));

    }

    @Override
    public int getCount() {
        return icons.length;
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
        view = inflater.inflate(com.kutovenko.kitstasher.R.layout.item_spinner, viewGroup, false);
        ImageView icon = view.findViewById(com.kutovenko.kitstasher.R.id.ivItemCat);
        TextView category = view.findViewById(com.kutovenko.kitstasher.R.id.tvItemName);
        icon.setImageResource(icons[i]);
        category.setText(categories[i]);
        return view;
    }
}
