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
import com.example.kitstasher.fragment.KitsFragment;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class NewAdapterKitList
        extends android.support.v7.widget.RecyclerView.Adapter<NewAdapterKitList.ViewHolder>
        implements Filterable {
    private ArrayList<Kit> itemList;
    private ArrayList<Kit> filteredItemList;
    private Context context;
    private String activeTable;
    private FilterListener listener;
    private int categoryTab;

    public NewAdapterKitList(ArrayList<Kit> itemList, Context context, String activeTable,
                             FilterListener listener, int categoryTab) {
        this.itemList = itemList;
        this.filteredItemList = itemList;
        this.context = context;
        this.activeTable = activeTable;
        this.listener = listener;
        this.categoryTab = categoryTab;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llKitItem, llKitText;
        ImageView ivBoxart;
        TextView tvFullKitname, tvFullBrand, tvFullScale;
        ImageButton ibDelete;

        public ViewHolder(View view) {
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
    public NewAdapterKitList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kit, parent, false);
        return new NewAdapterKitList.ViewHolder(itemView);
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
        String fullBrand = brand + " " + cat_no;

        holder.tvFullBrand.setText(fullBrand);
        holder.tvFullScale.setText(scale);
        holder.tvFullKitname.setText(name);

        if (!Helper.isBlank(uri)) {
            Glide
                    .with(context)
                    .load(new File(Uri.parse(uri).getPath()))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera).error(R.drawable.ic_menu_camera))
                    .into(holder.ivBoxart);
        } else {
            Glide
                    .with(context)
                    .load(composeUrl(url))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_menu_camera).error(R.drawable.ic_menu_camera))
                    .into(holder.ivBoxart);
        }

        holder.llKitItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemSelected(filteredItemList.get(holder.getAdapterPosition()),
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
                                ||  row.getBrandCatno().toLowerCase(Locale.getDefault()).contains(query))
                        {
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

    public void setSortedItemList(ArrayList<Kit> list){
        filteredItemList = list;
        notifyItemRangeChanged(0, filteredItemList.size());
    }

    public ArrayList<Kit> getItemsList(){
        return new ArrayList<>(filteredItemList);
    }

    public interface FilterListener {
        void onItemSelected(Kit kit, ArrayList<Kit> filteredItemList, int position);
    }

    private String composeUrl(String url) {
        return url + MyConstants.BOXART_URL_SMALL + MyConstants.JPG;
    }

    private void showDeleteDialog(final long itemId, final int currentPosition, final String uri,
                                  final String onlineId) {
        final DbConnector dbConnector = new DbConnector(context);
        dbConnector.open();
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(R.string.Do_you_wish_to_delete);
        dialogBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!Helper.isBlank(uri)) {
                    File file = new File(uri);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                dbConnector.deleteAllAftermarketForKit(itemId);
                try{
                deleteFromOnlineStash(onlineId);
                }finally {
                    dbConnector.delRec(activeTable, itemId);
                    filteredItemList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
//                    notifyItemRangeRemoved(currentPosition, 1);
                    if (getItemCount() == 0) {
                        KitsFragment.refreshPages();
                    }
//                    }else {
//                        notifyItemRemoved(currentPosition);
//                    }
                }
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog d = dialogBuilder.create();
        d.show();
    }

    private void deleteFromOnlineStash(String onlineId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(MyConstants.PARSE_C_STASH);
        query.getInBackground(onlineId, new GetCallback<ParseObject>() {
            public void done(ParseObject item, ParseException e) {
                if (e == null) {
                    item.put(MyConstants.PARSE_DELETED, true);
                    item.saveInBackground();
                }
            }
        });
    }
}