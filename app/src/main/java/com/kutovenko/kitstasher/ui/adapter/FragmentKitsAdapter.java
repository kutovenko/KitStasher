package com.kutovenko.kitstasher.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kutovenko.kitstasher.ui.listener.OnPagerItemInteractionListener;
import com.kutovenko.kitstasher.model.StashItem;
import com.kutovenko.kitstasher.util.Helper;
import com.kutovenko.kitstasher.util.MyConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Adapter for kits and aftermarket list.
 *
 * Адаптер для списков наборов и афтермаркета.
 */

public class FragmentKitsAdapter extends RecyclerView.Adapter<FragmentKitsAdapter.ViewHolder>
        implements Filterable {
    private ArrayList<StashItem> itemList;
    private Context context;
    private ArrayList<StashItem> filteredItemList;
    private FilterListener listener;
    private OnPagerItemInteractionListener onPagerItemInteractionListener;
    private String workMode;

    public FragmentKitsAdapter(ArrayList<StashItem> itemList, String workMode, Context context, FilterListener listener,
                               OnPagerItemInteractionListener onPagerItemInteractionListener) {
        this.itemList = itemList;
        this.workMode = workMode;
        this.context = context;
        this.listener = listener;
        this.filteredItemList = itemList;
        this.onPagerItemInteractionListener = onPagerItemInteractionListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llKitItem, llKitText;
        ImageView ivBoxart;
        TextView tvFullKitname, tvFullBrand, tvFullScale, tvItemCardScaleText;
        ImageButton ibDelete;

        ViewHolder(View view) {
            super(view);
            llKitItem = view.findViewById(com.kutovenko.kitstasher.R.id.llKitItem);
            llKitText = view.findViewById(com.kutovenko.kitstasher.R.id.llKitText);
            tvFullBrand = view.findViewById(com.kutovenko.kitstasher.R.id.tvBrandItem);
            tvFullKitname = view.findViewById(com.kutovenko.kitstasher.R.id.tvKit_nameItem);
            tvItemCardScaleText = view.findViewById(com.kutovenko.kitstasher.R.id.tvItemCardScaleText);
            tvFullScale = view.findViewById(com.kutovenko.kitstasher.R.id.tvScaleItem);
            ivBoxart = view.findViewById(com.kutovenko.kitstasher.R.id.ivBoxartItem);
            ibDelete = view.findViewById(com.kutovenko.kitstasher.R.id.ibDelete);
        }
    }

    @NonNull
    @Override
    public FragmentKitsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(com.kutovenko.kitstasher.R.layout.item_kit, parent, false);
        return new FragmentKitsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        StashItem stashItem = filteredItemList.get(position);
        final long id = stashItem.getLocalId();
        String url = stashItem.getBoxartUrl();
        final String uri = stashItem.getBoxartUri();
        String brand = stashItem.getBrand();
        String cat_no = stashItem.getBrandCatno();
        String name = stashItem.getName();
        switch (workMode){
            case "1":
                break;
            case "2":
                break;
            case "3":

                break;

        }
        String scale = String.valueOf(stashItem.getScale());
        final String onlneId = stashItem.getOnlineId();
        String fullBrand = brand + " " + cat_no;
        holder.tvFullBrand.setText(fullBrand);
        if (workMode.equals(MyConstants.TYPE_SUPPLY)){
            holder.tvItemCardScaleText.setVisibility(View.GONE);
            holder.tvFullScale.setVisibility(View.GONE);
        }else {
            holder.tvFullScale.setText(scale);
        }
        holder.tvFullKitname.setText(name);

        if (!Helper.isBlank(uri)) {
            Glide
                    .with(context)
                    .load(uri)
                    .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera)
                            .error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                    .into(holder.ivBoxart);
        } else {
            Glide
                    .with(context)
                    .load(Helper.composeUrl(url, MyConstants.BOXART_URL_LARGE))
                    .apply(new RequestOptions().placeholder(com.kutovenko.kitstasher.R.drawable.ic_menu_camera)
                            .error(com.kutovenko.kitstasher.R.drawable.ic_menu_camera))
                    .into(holder.ivBoxart);
        }

        if (workMode.equals(MyConstants.TYPE_KIT) || workMode.equals(MyConstants.TYPE_AFTERMARKET)) {

            holder.llKitItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemSelected(itemList.get(holder.getAdapterPosition()),
                            filteredItemList, holder.getAdapterPosition());
                }
            });
        } else if (workMode.equals(MyConstants.TYPE_SUPPLY)){
            holder.llKitItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemSelected(itemList.get(holder.getAdapterPosition()),
                            filteredItemList, holder.getAdapterPosition());
                }
            });
        }
        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(id, holder.getAdapterPosition(), uri, onlneId);
            }
        });
    }


    @Override
    public int getItemCount() {
        return filteredItemList.size();
    }

//    private String composeUrl(String url) {
//        return url + MyConstants.BOXART_URL_SMALL + MyConstants.JPG;
//    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString().toLowerCase(Locale.getDefault());
                if (query.isEmpty()) {
                    filteredItemList = itemList;
                } else {
                    ArrayList<StashItem> filteredList = new ArrayList<>();
                    if (workMode.equals(MyConstants.TYPE_KIT) || workMode.equals(MyConstants.TYPE_AFTERMARKET)){
                    for (StashItem row : itemList) {
                        if (row.getBrand().toLowerCase(Locale.getDefault()).contains(query)
                                || row.getName().toLowerCase(Locale.getDefault()).contains(query)
                                || row.getBrandCatno().toLowerCase(Locale.getDefault()).contains(query)
                                || row.getNoengName().toLowerCase(Locale.getDefault()).contains(query)
                                ) {
                            filteredList.add(row);
                        }
                    }
                    }else{
                        for (StashItem row : itemList) {
                            if (row.getBrand().toLowerCase(Locale.getDefault()).contains(query)
                                    || row.getName().toLowerCase(Locale.getDefault()).contains(query)
                                    || row.getBrandCatno().toLowerCase(Locale.getDefault()).contains(query)
                                    ) {
                                filteredList.add(row);
                            }
                        }
                    }
                    filteredItemList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredItemList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredItemList = (ArrayList<StashItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public long getItemId(int position) {
        int itemID;
        if (itemList == null){
            itemID = position;
        }else{
            itemID = itemList.indexOf(filteredItemList.get(position));
        }
        return itemID;
    }

    private void showDeleteDialog(final long itemId, final int currentPosition, final String uri,
                                  final String onlineId) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(com.kutovenko.kitstasher.R.string.Do_you_wish_to_delete);
        dialogBuilder.setPositiveButton(com.kutovenko.kitstasher.R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                onPagerItemInteractionListener.onPagerItemDelete(itemId,
                        currentPosition, uri, onlineId);
            }
        });
        dialogBuilder.setNegativeButton(com.kutovenko.kitstasher.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog d = dialogBuilder.create();
        d.show();
    }

    public void delete(int position) {
        filteredItemList.remove(position);
        notifyItemRemoved(position);
    }

    public void setSortedItemList(ArrayList<StashItem> list) {
        filteredItemList = list;
        itemList = list;
        notifyItemRangeChanged(0, filteredItemList.size());
    }

    public ArrayList<StashItem> getItemsList() {
        return new ArrayList<>(filteredItemList);
    }

    public interface FilterListener {
        void onItemSelected(StashItem stashItem, ArrayList<StashItem> filteredItemList, int position);
    }
}
