package com.example.kitstasher.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.objects.ChooserItem;
import com.example.kitstasher.other.CheckableLinearLayout;
import com.example.kitstasher.other.Helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.R.drawable.ic_menu_camera;
import static com.example.kitstasher.other.Helper.composeUrl;

public class NewAdapterChooserList extends RecyclerView.Adapter<NewAdapterChooserList.ViewHolder> {
    private List<Integer> selectedItemsPositions;//to store all selected items position
    private List<String> selectedIds;
    private ArrayList<ChooserItem> itemList;
    private char workMode;
    private Context context;

    public NewAdapterChooserList(ArrayList<ChooserItem> itemList, char workMode, Context context) {
        super();
        selectedItemsPositions = new ArrayList<>();
        selectedIds = new ArrayList<>();
        this.workMode = workMode;
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_choose_item, parent, false);
        return new NewAdapterChooserList.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChooserItem item = itemList.get(position);
        String kitname = item.getName();
        String brand = item.getBrand();
        int scale = item.getScale();
        holder.tvChooseKitName.setText(kitname);
        holder.tvChooseKitBrand.setText(brand);
        holder.tvScale.setText(String.valueOf(scale));
        String url = item.getUrl();
        String uri = item.getUri();
        if (!Helper.isBlank(uri)) {
            Glide
                    .with(context)
                    .load(new File(Uri.parse(uri).getPath()))
                    .placeholder(ic_menu_camera)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivChooseKitCategory);
        } else {
            Glide
                    .with(context)
                    .load(composeUrl(url))
                    .placeholder(ic_menu_camera)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivChooseKitCategory);
        }

        holder.cbChoose.setTag(position);

        if (selectedItemsPositions.contains(position)) {
            holder.cbChoose.setChecked(true);
        } else {
            holder.cbChoose.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChooseKitName, tvChooseKitBrand, tvScale;
        private ImageView ivChooseKitBoxart, ivChooseKitCategory;
        private CheckBox cbChoose;
        private CheckableLinearLayout cllContainer;

        ViewHolder(View view) {
            super(view);
            ivChooseKitCategory = view.findViewById(R.id.ivChooseKitCategory);
            tvChooseKitBrand = view.findViewById(R.id.tvChooseKitBrand);
            tvChooseKitName = view.findViewById(R.id.tvChooseKitName);
            tvScale = view.findViewById(R.id.tvScale);
            cllContainer = view.findViewById(R.id.llChooseItemContainer);
            cbChoose = view.findViewById(R.id.cbChoose);
        }
    }
}
