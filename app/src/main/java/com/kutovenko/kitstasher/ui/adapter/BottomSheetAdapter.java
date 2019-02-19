package com.kutovenko.kitstasher.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kutovenko.kitstasher.model.CategoryItem;
import com.kutovenko.kitstasher.util.MyConstants;

import java.util.ArrayList;

/*
Adapter for BottomSheet in KitsFragment. Returns list of all active categories and
number of kits in each category.

Адаптер для BottomSheet в KitsFragment. Возвращает перечень всех активных категорий
и количество наборов для каждой из них.
 */

public class BottomSheetAdapter extends RecyclerView.Adapter<BottomSheetAdapter.ViewHolder>{
    private ArrayList<CategoryItem> activeCategories;
    private Context context;
    private ActiveCategoriesListener activeCategoriesListener;

    public BottomSheetAdapter(Context context, ArrayList<CategoryItem> objects,
                              ActiveCategoriesListener activeCategoriesListener){
        this.context = context;
        this.activeCategories = objects;
        this.activeCategoriesListener = activeCategoriesListener;
    }

    @NonNull
    @Override
    public BottomSheetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(com.kutovenko.kitstasher.R.layout.item_rv_categories, parent, false);
        return new BottomSheetAdapter.ViewHolder(itemView);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivLogo;
        TextView tvName;
        TextView tvQuantity;
        LinearLayout llCategoryItem;

        private ViewHolder(View view){
            super(view);
            llCategoryItem = view.findViewById(com.kutovenko.kitstasher.R.id.llcategoriesListItem);
            ivLogo = view.findViewById(com.kutovenko.kitstasher.R.id.ivCategoriesListLogo);
            tvName = view.findViewById(com.kutovenko.kitstasher.R.id.tvCategoriesListName);
            tvQuantity = view.findViewById(com.kutovenko.kitstasher.R.id.tvCategoriesListQuantity);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetAdapter.ViewHolder holder, int position) {
        final CategoryItem item = activeCategories.get(position);
        Glide.with(context)
                .load(item.getLogoResource())
                .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_help_black_24dp)
                        .error(com.kutovenko.kitstasher.R.drawable.ic_help_black_24dp))
                .into(holder.ivLogo);
        holder.tvName.setText(tagToCategoryName(item.getName()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.llCategoryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeCategoriesListener.onCategorySelected(item.getName());
            }
        });
    }

    private String tagToCategoryName(String tag) {
        String name;
        switch (tag){
            case MyConstants.CODE_AIR:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.air);
                break;
            case MyConstants.CODE_GROUND:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.ground);
                break;
            case MyConstants.CODE_SEA:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.sea);
                break;
            case MyConstants.CODE_SPACE:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.space);
                break;
            case MyConstants.CODE_AUTOMOTO:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.cars_bikes);
                break;
            case MyConstants.CODE_FIGURES:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.Figures);
                break;
            case MyConstants.CODE_FANTASY:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.Fantasy);
                break;
            case MyConstants.M_CODE_OTHER:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.other);
                break;

            case MyConstants.M_CODE_ADDON:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.media_addon);
                break;
            case MyConstants.M_CODE_PHOTOETCH:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.media_photoetch);
                break;
            case MyConstants.M_CODE_DECAL:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.media_decal);
                break;
            case MyConstants.M_CODE_MASK:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.media_mask);
                break;

            case MyConstants.CODE_P_ACRYLLIC:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.acryllic);
                break;
            case MyConstants.CODE_P_ENAMEL:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.enamel);
                break;
            case MyConstants.CODE_P_OIL:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.media_oil);
                break;
            case MyConstants.CODE_P_LACQUER:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.media_lacquer);
                break;
            case MyConstants.CODE_P_THINNER:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.media_thinner);
                break;
            case MyConstants.CODE_P_GLUE:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.media_glue);
                break;
            case MyConstants.CODE_P_DECAL_SET:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.media_decalset);
                break;
            case MyConstants.CODE_P_DECAL_SOL:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.media_decalsol);
                break;
            case MyConstants.CODE_P_PIGMENT:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.media_pigment);
                break;
            case MyConstants.CODE_P_COLORSTOP:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.media_colorstop);
                break;
            case MyConstants.CODE_P_FILLER:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.media_filler);
                break;
            case MyConstants.CODE_P_PRIMER:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.media_primer);
                break;

            default:
                name = context.getResources().getString(com.kutovenko.kitstasher.R.string.all);
                break;
        }
        return name;
    }

    @Override
    public int getItemCount() {
        return activeCategories.size();
    }

    public void updateCategories(ArrayList<CategoryItem> activeCategories) {
        this.activeCategories = activeCategories;
        this.notifyDataSetChanged();
    }

    public interface ActiveCategoriesListener {
        void onCategorySelected(String category);
    }
}