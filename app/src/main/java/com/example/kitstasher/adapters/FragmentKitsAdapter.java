package com.example.kitstasher.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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
import com.example.kitstasher.R;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;
import com.example.kitstasher.other.OnPagerItemInteractionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class FragmentKitsAdapter extends android.support.v7.widget.RecyclerView.Adapter<FragmentKitsAdapter.ViewHolder>
        implements Filterable {
    private ArrayList<Kit> itemList;
    private Context context;
    private ArrayList<Kit> filteredItemList;
    private FilterListener listener;
    private OnPagerItemInteractionListener onPagerItemInteractionListener;

    public FragmentKitsAdapter(ArrayList<Kit> itemList, Context context, FilterListener listener,
                               OnPagerItemInteractionListener onPagerItemInteractionListener) {
        this.itemList = itemList;
        this.context = context;
        this.listener = listener;
        this.filteredItemList = itemList;
        this.onPagerItemInteractionListener =onPagerItemInteractionListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llKitItem, llKitText;
        ImageView ivBoxart;
        TextView tvFullKitname, tvFullBrand, tvFullScale;
        ImageButton ibDelete;

        ViewHolder(View view) {
            super(view);
            llKitItem = view.findViewById(R.id.llKitItem);
            llKitText = view.findViewById(R.id.llKitText);
            tvFullBrand = view.findViewById(R.id.tvBrandItem);
            tvFullKitname = view.findViewById(R.id.tvKit_nameItem);
            tvFullScale = view.findViewById(R.id.tvScaleItem);
            ivBoxart = view.findViewById(R.id.ivBoxartItem);
            ibDelete = view.findViewById(R.id.ibDelete);
        }
    }

    @NonNull
    @Override
    public FragmentKitsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kit, parent, false);
        return new FragmentKitsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Kit kit = filteredItemList.get(position);
        final long id = kit.getLocalId();
        String url = kit.getBoxart_url();
        final String uri = kit.getBoxart_uri();
        String brand = kit.getBrand();
        String cat_no = kit.getBrandCatno();
        String name = kit.getKit_name();
        String scale = String.valueOf(kit.getScale());
        final String onlneId = kit.getOnlineId();
        String category = kit.getCategory();
        String item_type = kit.getItemType();
        String fullBrand = brand + " " + cat_no + "-" + category + item_type;

        holder.tvFullBrand.setText(fullBrand);
        holder.tvFullScale.setText(scale);
        holder.tvFullKitname.setText(name);

        if (!Helper.isBlank(uri)) {
            Glide
                    .with(context)
                    .load(new File(Uri.parse(uri).getPath()))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera)
                            .error(R.drawable.ic_menu_camera))
                    .into(holder.ivBoxart);
        } else {
            Glide
                    .with(context)
                    .load(composeUrl(url))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera)
                            .error(R.drawable.ic_menu_camera))
                    .into(holder.ivBoxart);
        }

        holder.llKitItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemSelected(itemList.get(holder.getAdapterPosition()),
                        filteredItemList, holder.getAdapterPosition());
            }
        });

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

    private String composeUrl(String url) {
        return url + MyConstants.BOXART_URL_SMALL + MyConstants.JPG;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString().toLowerCase(Locale.getDefault());
                if (query.isEmpty()) {
                    filteredItemList = itemList;
                } else {
                    ArrayList<Kit> filteredList = new ArrayList<>();
                    for (Kit row : itemList) {
                        if (row.getBrand().toLowerCase(Locale.getDefault()).contains(query)
                                || row.getKit_name().toLowerCase(Locale.getDefault()).contains(query)
                                || row.getBrandCatno().toLowerCase(Locale.getDefault()).contains(query)
                                || row.getKit_noeng_name().toLowerCase(Locale.getDefault()).contains(query)
                                ) {
                            filteredList.add(row);
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
                filteredItemList = (ArrayList<Kit>) filterResults.values;
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
        dialogBuilder.setTitle(R.string.Do_you_wish_to_delete);
        dialogBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                onPagerItemInteractionListener.onPagerItemDelete(itemId,
                        currentPosition, uri, onlineId);
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

    public void setSortedItemList(ArrayList<Kit> list) {
        filteredItemList = list;
        notifyItemRangeChanged(0, filteredItemList.size());
    }

    public ArrayList<Kit> getItemsList() {
        return new ArrayList<>(filteredItemList);
    }

    public interface FilterListener {
        void onItemSelected(Kit kit, ArrayList<Kit> filteredItemList, int position);
    }
}
