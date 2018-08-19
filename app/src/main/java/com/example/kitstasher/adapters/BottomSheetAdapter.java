package com.example.kitstasher.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kitstasher.R;
import com.example.kitstasher.fragment.KitsFragment;
import com.example.kitstasher.objects.CategoryItem;

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
    private KitsFragment.CategoriesChangeListener categoriesChangeListener;

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
                .inflate(R.layout.item_rv_categories, parent, false);
        return new BottomSheetAdapter.ViewHolder(itemView);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivLogo;
        TextView tvName;
        TextView tvQuantity;
        LinearLayout llCategoryItem;

        private ViewHolder(View view){
            super(view);
            llCategoryItem = view.findViewById(R.id.llcategoriesListItem);
            ivLogo = view.findViewById(R.id.ivCategoriesListLogo);
            tvName = view.findViewById(R.id.tvCategoriesListName);
            tvQuantity = view.findViewById(R.id.tvCategoriesListQuantity);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetAdapter.ViewHolder holder, int position) {
        final CategoryItem item = activeCategories.get(position);
        Glide.with(context)
                .load(item.getLogoResource())
                .apply(new RequestOptions().placeholder(R.drawable.ic_help_black_24dp)
                        .error(R.drawable.ic_help_black_24dp))
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
            case "1":
                name = context.getResources().getString(R.string.air);
                break;
            case "2":
                name = context.getResources().getString(R.string.ground);
                break;
            case "3":
                name = context.getResources().getString(R.string.sea);
                break;
            case "4":
                name = context.getResources().getString(R.string.space);
                break;
            case "5":
                name = context.getResources().getString(R.string.cars_bikes);
                break;
            case "6":
                name = context.getResources().getString(R.string.Figures);
                break;
            case "7":
                name = context.getResources().getString(R.string.Fantasy);
                break;
            case "0":
                name = context.getResources().getString(R.string.other);
                break;
            default:
                name = context.getResources().getString(R.string.all);
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