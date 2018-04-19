package com.example.kitstasher.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.ListActivity;
import com.example.kitstasher.activity.ViewActivity;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;

import java.io.Serializable;
import java.util.ArrayList;

public class NewMyListAdapter extends RecyclerView.Adapter<NewMyListAdapter.ViewHolder> {
    private ArrayList<Kit> itemList;
    private int mode;
    private DbConnector dbConnector;
    private Context context;

    public NewMyListAdapter(ArrayList<Kit> itemList, Context context, int mode){
        super();
        this.itemList = itemList;
        this.mode = mode;
        this.context = context;
        dbConnector = new DbConnector(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_with_edit, parent, false);

        return new NewMyListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Kit item = itemList.get(position);
        String itemName = "";
        final long itemId = item.getLocalId();
        final long kitId;

        if (mode == MyConstants.MODE_A_KIT) { //сокращенная
            holder.tvListItemDate.setVisibility(View.GONE); //works
            holder.ibListItemDelete.setVisibility(View.GONE);
            holder.ibListItemEdit.setVisibility(View.GONE);
            itemName = item.getKit_name();

            int currentPosition = holder.getAdapterPosition();
            long currentId = getItemId(currentPosition);
            Kit parent = dbConnector.getKitForAfterById(currentId).get(0); // нужно, чтобы получить ид кита, к которому добавлен афтер
            kitId = parent.getLocalId();
        } else {
//            if (mode == MyConstants.MODE_A_EDIT) {
            holder.ibListItemEdit.setVisibility(View.GONE);
            holder.tvListItemDate.setVisibility(View.GONE);
            itemName = item.getKit_name();
            int currentPosition = holder.getAdapterPosition();
            long currentId = getItemId(currentPosition);
            Kit parent = dbConnector.getKitForAfterById(currentId).get(0); // нужно, чтобы получить ид кита, к которому добавлен афтер
            kitId = parent.getLocalId();
            String category = parent.getCategory();
        }

        if (mode == MyConstants.MODE_A_LIST) {
            holder.tvListItemDate.setText(item.getDate_added());
        }
        holder.tvEditBrandListItem.setText(itemName);
        final String finalName = itemName;

        final long finalKitId = kitId;
        holder.ibListItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                long currentId = getItemId(currentPosition);

                if (mode == MyConstants.MODE_A_BRAND) {
                    dbConnector.delBrand(currentId);
                    Cursor newcursor = dbConnector.getBrands(DbConnector.COLUMN_ID);
                    notifyItemRemoved(currentPosition);
                } else if (mode == MyConstants.MODE_A_SHOP) {
                    dbConnector.delShopById(currentId);
                    Cursor newcursor = dbConnector.getShops(DbConnector.COLUMN_ID);
                    notifyItemRemoved(currentPosition);
                } else if (mode == MyConstants.MODE_A_LIST) {
                    dbConnector.deleteList(currentId, finalName);
                    Cursor newcursor = dbConnector.getLists(DbConnector.COLUMN_ID);
                    notifyItemRemoved(currentPosition);
                } else if (mode == MyConstants.MODE_A_KIT) {
                    dbConnector.deleteAftermarketFromKit(finalKitId, currentId);
//                    Cursor newcursor = dbConnector.getAftermarketForKit(kitId, MyConstants.EMPTY);
//                    notifyItemRemoved(currentPosition);
//                    changeCursor(newcursor);
                    ViewActivity.refreshPages();
                } else if (mode == MyConstants.MODE_A_EDIT) {
                    dbConnector.deleteAftermarketFromKit(finalKitId, currentId);
//                    Cursor newcursor = dbConnector.getAftermarketForKit(kitId, MyConstants.EMPTY);
//                    notifyItemRemoved(currentPosition);
//                    changeCursor(newcursor);
                    ViewActivity.refreshPages();
                }
            }
        });
        holder.llitemBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (mode == MyConstants.MODE_A_LIST) {
                    Intent intent = new Intent(context, ListActivity.class);
                    intent.putExtra(MyConstants.LISTID, itemId);
                    context.startActivity(intent);
                } else if (mode == MyConstants.MODE_A_KIT) {
                    int size = itemList.size();
                    final ArrayList<Long> ids = new ArrayList<>(size);
                    final ArrayList<Integer> positions = new ArrayList<>(size);
                    for (int y = 0; y < size; y++) {
                        ids.add(itemList.get(y).getLocalId());
                        positions.add(y);
                    }
                    Intent intent;
                    intent = new Intent(context, ViewActivity.class);
                    intent.putExtra(MyConstants.ID, kitId);//ид кита, к которому выводим афтер
                    intent.putExtra(MyConstants.WORK_MODE, MyConstants.MODE_AFTER_KIT);
                    intent.putExtra(MyConstants.POSITION, currentPosition);//ид открытия пейджера
                    intent.putExtra(MyConstants.SORT_BY, MyConstants._ID);
                    intent.putExtra(MyConstants.CATEGORY, MyConstants.EMPTY);
                    String[] filters = new String[5];
                    filters[0] = MyConstants.EMPTY;
                    filters[1] = MyConstants.EMPTY;
                    filters[2] = MyConstants.EMPTY;
                    filters[3] = MyConstants.EMPTY;
                    filters[4] = MyConstants.EMPTY;
                    intent.putExtra(MyConstants.SCALE_FILTER, filters[0]);
                    intent.putExtra(MyConstants.BRAND_FILTER, filters[1]);
                    intent.putExtra(MyConstants.KITNAME_FILTER, filters[2]);
                    intent.putExtra(MyConstants.STATUS_FILTER, filters[3]);
                    intent.putExtra(MyConstants.MEDIA_FILTER, filters[4]);
                    intent.putExtra(MyConstants.IDS, ids);
                    intent.putExtra(MyConstants.POSITIONS, positions);
                    intent.putExtra(MyConstants.SORT_BY, MyConstants._ID);
                    intent.putExtra(MyConstants.FILTERS, (Serializable) filters);
                    intent.putExtra(MyConstants.WORK_MODE, MyConstants.MODE_AFTER_KIT);
                    intent.putExtra(MyConstants.LISTNAME, MyConstants.EMPTY); //мы идем из карточки кита, не из списка
                    context.startActivity(intent);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEditBrandListItem;
        TextView tvListItemDate;
        ImageButton ibListItemEdit;
        ImageButton ibListItemDelete;
        LinearLayoutCompat llitemBody;

        ViewHolder(View view) {
            super(view);
            tvEditBrandListItem = view.findViewById(R.id.tvMylistName);
            tvListItemDate = view.findViewById(R.id.tvListDateAdded);
            ibListItemEdit = view.findViewById(R.id.ibtnEditMyList);
            ibListItemDelete = view.findViewById(R.id.ibtnDeleteMyList);
            llitemBody = view.findViewById(R.id.llitemBody);
        }
    }
}
