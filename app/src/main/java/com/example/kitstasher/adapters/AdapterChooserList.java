package com.example.kitstasher.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.activity.ChooserActivity;
import com.example.kitstasher.other.CheckableLinearLayout;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Алексей on 14.08.2017.
 */

// TODO: 28.02.2018 RecyclerViewAdapter

public class AdapterChooserList extends CursorAdapter {
    private final Context context;
    private List<Integer> selectedItemsPositions;//to store all selected items position
    private List<String> selectedIds;
    private char workMode;

    public AdapterChooserList(Context context, Cursor c, int flags, char workMode) {
        super(context, c, flags);
        this.context = context;
        selectedItemsPositions = new ArrayList<>();
        selectedIds = new ArrayList<>();
        this.workMode = workMode;
    }

    private static class ViewHolder {
        private TextView tvChooseKitName, tvChooseKitBrand, tvScale;
        private ImageView ivChooseKitBoxart, ivChooseKitCategory;
        private CheckBox cbChoose;
        private CheckableLinearLayout cllContainer;
    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_list_choose_item, parent, false);
        CheckBox box = view.findViewById(R.id.cbChoose);
        Object obj = cursor.getString(cursor.getColumnIndex(DbConnector.MYLISTS_COLUMN_ID));
        view.setTag(obj);
        box.setTag(cursor.getPosition());
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int position = (int) compoundButton.getTag();
                cursor.moveToPosition(position);
                String id = cursor.getString(cursor.getColumnIndex(DbConnector.MYLISTS_COLUMN_ID));

                if (b) {
                    //check whether its already selected or not
                    if (!selectedItemsPositions.contains(position)){
                        selectedItemsPositions.add(position);
                        selectedIds.add(id);
                        ChooserActivity.choosedIds = selectedIds;
                    }
                } else {
                    if (selectedItemsPositions.contains(position)) {
                        selectedItemsPositions.remove(selectedItemsPositions.indexOf(position));
                        selectedIds.remove(selectedIds.indexOf(id));
                        ChooserActivity.choosedIds = selectedIds;
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = new ViewHolder();
        holder.ivChooseKitCategory = view.findViewById(R.id.ivChooseKitCategory);
        holder.tvChooseKitBrand = view.findViewById(R.id.tvChooseKitBrand);
        holder.tvChooseKitName = view.findViewById(R.id.tvChooseKitName);
        holder.tvScale = view.findViewById(R.id.tvScale);
        holder.cllContainer = view.findViewById(R.id.llChooseItemContainer);

        String kitname = "";
        if (workMode == MyConstants.MODE_LIST || workMode == MyConstants.MODE_KIT) {
            kitname = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME));
        } else if (workMode == MyConstants.MODE_AFTER_KIT || workMode == MyConstants.MODE_AFTERMARKET) {
            kitname = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_AFTERMARKET_NAME));
        }
        String brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
        String scale = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));

        holder.tvChooseKitName.setText(kitname);
        holder.tvChooseKitBrand.setText(brand);
        holder.tvScale.setText(scale);


        if (MyConstants.CODE_SEA.equals(category)) {
            holder.ivChooseKitCategory.setImageResource(R.drawable.ic_tag_ship_black_24dp);
        }
        if (MyConstants.CODE_AIR.equals(category)) {
            holder.ivChooseKitCategory.setImageResource(R.drawable.ic_tag_air_black_24dp);
        }
        if (MyConstants.CODE_GROUND.equals(category)) {
            holder.ivChooseKitCategory.setImageResource(R.drawable.ic_tag_afv_black_24dp);
        }
        if (MyConstants.CODE_SPACE.equals(category)) {
            holder.ivChooseKitCategory.setImageResource(R.drawable.ic_tag_space_black_24dp);
        }
        if (MyConstants.CODE_OTHER.equals(category)) {
            holder.ivChooseKitCategory.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        }
        if (MyConstants.CODE_AUTOMOTO.equals(category)) {
            holder.ivChooseKitCategory.setImageResource(R.drawable.ic_directions_car_black_24dp);
        }
        if (MyConstants.CODE_FANTASY.equals(category)) {
            Glide
                    .with(context)
                    .load(R.drawable.ic_android_black_24dp)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivChooseKitCategory);
        }
        if (MyConstants.CODE_FIGURES.equals(category)) {
            Glide
                    .with(context)
                    .load(R.drawable.ic_wc_black_24dp)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivChooseKitCategory);
        }

        holder.cbChoose = view.findViewById(R.id.cbChoose);
        holder.cbChoose.setTag(cursor.getPosition());

        if (selectedItemsPositions.contains(cursor.getPosition())) {
            holder.cbChoose.setChecked(true);
        }else {
            holder.cbChoose.setChecked(false);
        }
    }
}
