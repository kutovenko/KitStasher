package com.example.kitstasher.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kitstasher.R;
import com.example.kitstasher.objects.ShopItem;
import com.example.kitstasher.other.DbConnector;

import java.util.ArrayList;

/**
 * Adapter for list of shops (SettingsFragment).
 *
 * Адаптер для списка магазинов (SettingsFragment).
 */

public class FragmentShopAdapter extends RecyclerView.Adapter<FragmentShopAdapter.ViewHolder>{
    private ArrayList<ShopItem> items;
    private Context context;

    public FragmentShopAdapter(Context context, ArrayList<ShopItem> items) {
        this.context = context;
        this.items = items;

    }

    @NonNull
    @Override
    public FragmentShopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopitem, parent, false);
        return new FragmentShopAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ShopItem item = items.get(position);
        final String name = item.getName();
        final long id = item.getLocalId();
        holder.tvShopName.setText(item.getName());
        holder.tvShopDateAdded.setText(item.getDateAdded());
        holder.ibtnEditShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.list_alertdialog, null);
                dialogBuilder.setView(dialogView);
                final EditText etNewBrandName = dialogView.findViewById(R.id.etNewListName);
                etNewBrandName.setText(name);
                etNewBrandName.setHint(R.string.enter_brand);
                dialogBuilder.setTitle(context.getString(R.string.Rename_) + name + "\"");
                dialogBuilder.setPositiveButton(R.string.Done, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newName = etNewBrandName.getText().toString().trim();
                        DbConnector dbConnector = new DbConnector(context);
                        dbConnector.open();
                        try{
                            dbConnector.editShop(id, newName);
                        }finally {
                            dbConnector.close();
                            item.setName(newName);
                            items.set(holder.getAdapterPosition(), item);
                            notifyItemChanged(holder.getAdapterPosition());
                        }

                    }
                });
                dialogBuilder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.show();
            }
        });
        holder.ibtnDeleteShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbConnector dbConnector = new DbConnector(context);
                dbConnector.open();
                try {
                    dbConnector.delShopById(id);
                }finally {
                    dbConnector.close();
                    notifyItemRemoved(holder.getAdapterPosition());
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvShopName, tvShopDateAdded;
        ImageButton ibtnEditShop, ibtnDeleteShop;

        ViewHolder(View itemView) {
            super(itemView);
            tvShopName = itemView.findViewById(R.id.tvShopName);
            tvShopDateAdded = itemView.findViewById(R.id.tvDateShopAdded);
            ibtnDeleteShop = itemView.findViewById(R.id.ibtnDeleteShop);
            ibtnEditShop = itemView.findViewById(R.id.ibtnEditShop);
        }
    }
}
