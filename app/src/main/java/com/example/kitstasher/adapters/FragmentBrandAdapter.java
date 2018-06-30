package com.example.kitstasher.adapters;

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
import com.example.kitstasher.objects.BrandItem;
import com.example.kitstasher.other.DbConnector;

import java.util.ArrayList;

public class FragmentBrandAdapter extends RecyclerView.Adapter<FragmentBrandAdapter.ViewHolder>{
    private Context context;
    private ArrayList<BrandItem> items;

    public FragmentBrandAdapter(Context context, ArrayList<BrandItem> incomeItems){
            items = incomeItems;
            this.context = context;
    }

    @NonNull
    @Override
    public FragmentBrandAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_branditem, parent, false);
        return new FragmentBrandAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FragmentBrandAdapter.ViewHolder holder, int position) {
        final BrandItem item = items.get(position);
        final String name = item.getName();
        final long id = item.getLocalId();
        holder.tvBrandName.setText(item.getName());
        holder.ibtnEditBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                final View dialogView = inflater.inflate(R.layout.list_alertdialog, null);
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
                            dbConnector.updateBrand(id, newName);
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
        holder.ibtnDeleteBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbConnector dbConnector = new DbConnector(context);
                dbConnector.open();
                try {
                    dbConnector.delBrand(id);
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
        TextView tvBrandName;
        ImageButton ibtnEditBrand, ibtnDeleteBrand;

        ViewHolder(View itemView) {
            super(itemView);
            tvBrandName = itemView.findViewById(R.id.tvBrandName);
            ibtnDeleteBrand = itemView.findViewById(R.id.ibtnDeleteBrand);
            ibtnEditBrand = itemView.findViewById(R.id.ibtnEditBrand);
        }
    }
}

