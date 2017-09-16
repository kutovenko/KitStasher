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
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;

import java.util.ArrayList;
import java.util.List;

import static android.R.drawable.ic_menu_camera;

/**
 * Created by Алексей on 14.08.2017.
 */

public class AdapterChooserList extends CursorAdapter {
    private final Context context;
    List<Integer> selectedItemsPositions;//to store all selected items position
    List<String> selectedIds;
    char mode;



    public AdapterChooserList(Context context, Cursor c, int flags, char mode) {
        super(context, c, flags);
        this.context = context;
        selectedItemsPositions = new ArrayList<Integer>();
        selectedIds = new ArrayList<String>();
        this.mode = mode;
    }

    static class ViewHolder {
        protected TextView tvChooseKitName, tvChooseKitBrand, tvScale;
        protected ImageView ivChooseKitBoxart, ivChooseKitCategory;
        protected CheckBox cbChoose;
        protected CheckableLinearLayout cllContainer;
    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_list_choose_item, parent, false);
        CheckBox box = (CheckBox)view.findViewById(R.id.cbChoose);
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

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = new ViewHolder();
        holder.ivChooseKitCategory = (ImageView)view.findViewById(R.id.ivChooseKitCategory);
        holder.tvChooseKitBrand = (TextView)view.findViewById(R.id.tvChooseKitBrand);
        holder.tvChooseKitName = (TextView)view.findViewById(R.id.tvChooseKitName);
        holder.tvScale = (TextView)view.findViewById(R.id.tvScale);
        holder.cllContainer = (CheckableLinearLayout)view.findViewById(R.id.llChooseItemContainer);

/////////////// TODO: 15.09.2017 разделить на кит и афтер
        String kitname = "";
        if (mode == Constants.MODE_KIT){
            kitname = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME));
        }else if (mode == Constants.MODE_AFTERMARKET){
            kitname = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_AFTERMARKET_NAME));
        }
        String brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
        String scale = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));

        holder.tvChooseKitName.setText(kitname);
        holder.tvChooseKitBrand.setText(brand);
        holder.tvScale.setText(scale);


        if (Constants.CAT_SEA.equals(category)) {
            holder.ivChooseKitCategory.setImageResource(R.drawable.ic_tag_ship_black_24dp);
        }
        if (Constants.CAT_AIR.equals(category)) {
            holder.ivChooseKitCategory.setImageResource(R.drawable.ic_tag_air_black_24dp);
        }
        if (Constants.CAT_GROUND.equals(category)) {
            holder.ivChooseKitCategory.setImageResource(R.drawable.ic_tag_afv_black_24dp);
        }
        if (Constants.CAT_SPACE.equals(category)) {
            holder.ivChooseKitCategory.setImageResource(R.drawable.ic_tag_space_black_24dp);
        }
        if (Constants.CAT_OTHER.equals(category)) {
            holder.ivChooseKitCategory.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        }
        if (Constants.CAT_AUTOMOTO.equals(category)){
            holder.ivChooseKitCategory.setImageResource(R.drawable.ic_directions_car_black_24dp);
        }
        if (Constants.CAT_FANTASY.equals(category)){
            Glide
                    .with(context)
                    .load(R.drawable.ic_android_black_24dp)
                    .placeholder(ic_menu_camera)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.crossFade()
                    .into(holder.ivChooseKitCategory);
//            holder.ivChooseKitCategory.setImageResource(R.drawable.ic_android_black_24dp);
        }
        if (Constants.CAT_FIGURES.equals(category)){
            Glide
                    .with(context)
                    .load(R.drawable.ic_wc_black_24dp)
                    .placeholder(ic_menu_camera)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.crossFade()
                    .into(holder.ivChooseKitCategory);
//            holder.ivChooseKitCategory.setImageResource(R.drawable.ic_wc_black_24dp);
        }


        holder.cbChoose = (CheckBox)view.findViewById(R.id.cbChoose);
        holder.cbChoose.setTag(cursor.getPosition());

        if (selectedItemsPositions.contains(cursor.getPosition())) {
            holder.cbChoose.setChecked(true);
        }else {
            holder.cbChoose.setChecked(false);
        }
    }
}
