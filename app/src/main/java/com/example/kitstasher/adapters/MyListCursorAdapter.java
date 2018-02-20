package com.example.kitstasher.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.ListActivity;
import com.example.kitstasher.activity.ViewActivity;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Алексей on 24.01.2018. For brands, shops and aftermarket for a kit
 * ItemCardFragment,
 * ItemEditFragment,
 * SettingsBrandsFragment
 * SettingsMyShopsFragment
 * MyListsFragment
 */

public class MyListCursorAdapter extends CursorRecyclerViewAdapter<MyListCursorAdapter.ViewHolder> {
    private int mode;
    private Context context;
    private int category;
    private long kitId;
    private Cursor afterCursor;

    public MyListCursorAdapter(Cursor cursor, Context context, int mode) {
        super(context, cursor);
        this.mode = mode;
        this.context = context;

//        this.cursor = cursor;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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

//    public static class ViewHolderAftermarket extends RecyclerView.ViewHolder {
//        TextView tvEditBrandListItem;
//        TextView tvListItemDate;
//        ImageButton ibListItemEdit;
//        ImageButton ibListItemDelete;
//        LinearLayoutCompat llitemBody;
//
//        ViewHolderAftermarket(View view) {
//            super(view);
//            tvEditBrandListItem = view.findViewById(R.id.tvMylistName);
//            tvListItemDate = view.findViewById(R.id.tvListDateAdded);
//            ibListItemEdit = view.findViewById(R.id.ibtnEditMyList);
//            ibListItemDelete = view.findViewById(R.id.ibtnDeleteMyList);
//            llitemBody = view.findViewById(R.id.llitemBody);
//        }
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_with_edit, parent, false);

        //размет

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        cursor.moveToPosition(holder.getAdapterPosition());
        String itemName = "";
        final long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ID));

        if (mode == MyConstants.MODE_A_SHOP) { //сокращенная карточка
            holder.tvListItemDate.setVisibility(View.GONE);
            itemName = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.MYSHOPS_COLUMN_SHOP_NAME));
        } else if (mode == MyConstants.MODE_A_BRAND) { //вокращенная карточка
            holder.tvListItemDate.setVisibility(View.GONE);
            itemName = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.BRANDS_COLUMN_BRAND));
        } else if (mode == MyConstants.MODE_A_LIST) { //полная карточка с датой
            itemName = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.MYLISTS_COLUMN_LIST_NAME));

        } else if (mode == MyConstants.MODE_A_KIT) { //сокращенная
//            category = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));
            holder.tvListItemDate.setVisibility(View.GONE); //works
            holder.ibListItemDelete.setVisibility(View.GONE);
            holder.ibListItemEdit.setVisibility(View.GONE);
            itemName = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_AFTERMARKET_NAME)); //works
            DbConnector dbConnector = new DbConnector(context);
            int currentPosition = holder.getAdapterPosition();
            long currentId = getItemId(currentPosition);
            afterCursor = dbConnector.getKitForAfterById(currentId); // нужно, чтобы получить ид кита, к которому добавлен афтер
            afterCursor.moveToFirst();
            kitId = afterCursor.getInt(afterCursor.getColumnIndexOrThrow(DbConnector.KIT_AFTER_KITID));
        } else if (mode == MyConstants.MODE_A_EDIT) {
            holder.ibListItemEdit.setVisibility(View.GONE);
            holder.tvListItemDate.setVisibility(View.GONE);
            itemName = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_AFTERMARKET_NAME));
            DbConnector dbConnector = new DbConnector(context);
            int currentPosition = holder.getAdapterPosition();
            long currentId = getItemId(currentPosition);
            afterCursor = dbConnector.getKitForAfterById(currentId);
            afterCursor.moveToFirst();
            kitId = afterCursor.getInt(afterCursor.getColumnIndexOrThrow(DbConnector.KIT_AFTER_KITID));
            category = afterCursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));
        }

        if (mode == MyConstants.MODE_A_LIST) {
            holder.tvListItemDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.MYLISTS_COLUMN_DATE)));
        }
        holder.tvEditBrandListItem.setText(itemName);
        final String finalName = itemName;

        holder.ibListItemEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String oldName = holder.tvEditBrandListItem.getText().toString();

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                final View dialogView = inflater.inflate(R.layout.list_alertdialog, null);
                dialogBuilder.setView(dialogView);
                final EditText etNewListName = dialogView.findViewById(R.id.etNewListName);
                etNewListName.setText(finalName);
                if (mode == MyConstants.MODE_A_BRAND) {
                    etNewListName.setHint(R.string.enter_brand);
                } else if (mode == MyConstants.MODE_A_SHOP) {
                    etNewListName.setHint(R.string.shop_name);
                } else if (mode == MyConstants.MODE_A_LIST) {
                    etNewListName.setHint(R.string.new_name_for_the_list);
                }

                dialogBuilder.setTitle(context.getString(R.string.Rename_) + oldName + "\"");
                dialogBuilder.setPositiveButton(R.string.Done, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newName = etNewListName.getText().toString().trim();
                        DbConnector dbConnector = new DbConnector(context);
                        dbConnector.open();
                        int currentPosition = holder.getAdapterPosition();
                        long currentId = getItemId(currentPosition);
                        if (mode == MyConstants.MODE_A_BRAND) {
                            dbConnector.updateBrand(currentId, newName);
                            notifyItemChanged(currentPosition);
                            Cursor newcursor = dbConnector.getBrands(DbConnector.COLUMN_ID);
                            changeCursor(newcursor);
                        } else if (mode == MyConstants.MODE_A_SHOP) {
                            dbConnector.updateShop(currentId, newName);
                            Cursor newcursor = dbConnector.getShops(DbConnector.COLUMN_ID);
                            changeCursor(newcursor);
                            notifyItemChanged(currentPosition);
                        } else if (mode == MyConstants.MODE_A_LIST) {
                            dbConnector.updateList(currentId, finalName, newName);
                            Cursor newcursor = dbConnector.getLists(DbConnector.COLUMN_ID);
                            changeCursor(newcursor);
                            notifyItemChanged(currentPosition);
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
        holder.ibListItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DbConnector dbConnector = new DbConnector(context);
                dbConnector.open();
                int currentPosition = holder.getAdapterPosition();
//                long currentId = holder.getItemId();
                long currentId = getItemId(currentPosition);

                if (mode == MyConstants.MODE_A_BRAND) {
                    dbConnector.delBrand(currentId);
                    Cursor newcursor = dbConnector.getBrands(DbConnector.COLUMN_ID);
                    notifyItemRemoved(currentPosition);
                    changeCursor(newcursor);
                } else if (mode == MyConstants.MODE_A_SHOP) {
                    dbConnector.delShopById(currentId);
                    Cursor newcursor = dbConnector.getShops(DbConnector.COLUMN_ID);
                    notifyItemRemoved(currentPosition);
                    changeCursor(newcursor);
                } else if (mode == MyConstants.MODE_A_LIST) {
                    dbConnector.deleteList(currentId, finalName);
                    Cursor newcursor = dbConnector.getLists(DbConnector.COLUMN_ID);
                    notifyItemRemoved(currentPosition);
                    changeCursor(newcursor);
                } else if (mode == MyConstants.MODE_A_KIT) {
                    dbConnector.deleteAftermarketFromKit(kitId, currentId);
                    Cursor newcursor = dbConnector.getAftermarketForKit(kitId, MyConstants.EMPTY);
                    notifyItemRemoved(currentPosition);
                    changeCursor(newcursor);
                    ViewActivity.refreshPages();
                } else if (mode == MyConstants.MODE_A_EDIT) {
                    dbConnector.deleteAftermarketFromKit(kitId, currentId);
                    Cursor newcursor = dbConnector.getAftermarketForKit(kitId, MyConstants.EMPTY);
                    notifyItemRemoved(currentPosition);
                    changeCursor(newcursor);
                    ViewActivity.refreshPages();
                }
            }
        });
        holder.llitemBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
//                long currentId = getItemId(currentPosition);

                if (mode == MyConstants.MODE_A_LIST) {
                    Intent intent = new Intent(context, ListActivity.class);
                    intent.putExtra(MyConstants.LISTNAME, itemId);
                    context.startActivity(intent);
                } else if (mode == MyConstants.MODE_A_KIT) {
                    int size = cursor.getCount();
                    final ArrayList<Long> ids = new ArrayList<>(size);
                    final ArrayList<Integer> positions = new ArrayList<>(size);
                    cursor.moveToFirst();
                    for (int y = 0; y < size; y++) {
                        ids.add(getItemId(y));
                        positions.add(y);
                        cursor.moveToNext();
                    }
                    Intent intent;
                    intent = new Intent(context, ViewActivity.class);
                    intent.putExtra(MyConstants.ID, kitId);//ид кита, к которому выводим афтер
                    intent.putExtra(MyConstants.WORK_MODE, MyConstants.MODE_AFTER_KIT);
                    intent.putExtra(MyConstants.POSITION, currentPosition);//ид открытия пейджера
                    intent.putExtra(MyConstants.SORT_BY, MyConstants._ID);
                    intent.putExtra(MyConstants.CATEGORY, category);
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


    //After for Kit MODE_AFTER_KIT

//    rvAftermarket.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            int size = aCursor.getCount();
//            final ArrayList<Long> ids = new ArrayList<>(size);
//            final ArrayList<Integer> positions = new ArrayList<>(size);
//            aCursor.moveToFirst();
//            for (int y = 0; y < aCursor.getCount(); y++) {
//                ids.add(afterAdapter.getItemId(y));
//                positions.add(y);
//                aCursor.moveToNext();
//            }
//            Intent intent;
//            intent = new Intent(context, ViewActivity.class);
//            intent.putExtra(MyConstants.ID, id);//ид кита, на котором должен открыться пейджер
//            intent.putExtra(MyConstants.WORK_MODE, MyConstants.MODE_AFTER_KIT);
//            intent.putExtra(MyConstants.POSITION, i);//ид открытия пейджера
//            intent.putExtra(MyConstants.SORT_BY, MyConstants._ID);
//            intent.putExtra(MyConstants.CATEGORY, category);
//            String[] filters = new String[5];
//            filters[0] = MyConstants.EMPTY;
//            filters[1] = MyConstants.EMPTY;
//            filters[2] = MyConstants.EMPTY;
//            filters[3] = MyConstants.EMPTY;
//            filters[4] = MyConstants.EMPTY;
//            intent.putExtra(MyConstants.SCALE_FILTER, filters[0]);
//            intent.putExtra(MyConstants.BRAND_FILTER, filters[1]);
//            intent.putExtra(MyConstants.KITNAME_FILTER, filters[2]);
//            intent.putExtra(MyConstants.STATUS_FILTER, filters[3]);
//            intent.putExtra(MyConstants.MEDIA_FILTER, filters[4]);
//            intent.putExtra(MyConstants.IDS, ids);
//            intent.putExtra(MyConstants.POSITIONS, positions);
//            intent.putExtra(MyConstants.SORT_BY, MyConstants._ID);
//            intent.putExtra(MyConstants.FILTERS, (Serializable) filters);
//            intent.putExtra(MyConstants.WORK_MODE, MyConstants.MODE_AFTER_KIT);
//            intent.putExtra(MyConstants.LISTNAME, MyConstants.EMPTY); //мы идем из карточки кита, не из списка
//            intent.putExtra(MyConstants.CATEGORY, category);
//            startActivity(intent);
//        }
//    });
//
//
//    TextView tvAftermarketTitle = view.findViewById(R.id.tvAftermarketTitle);
//        btnEdit.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent intent = new Intent(getActivity(), EditActivity.class);
//            intent.putExtra(MyConstants.POSITION, position);
//            intent.putExtra(MyConstants.WORK_MODE, workMode);
//            intent.putExtra(MyConstants.ID, id);
//            intent.putExtra(MyConstants.LIST_CATEGORY, category);
//            intent.putExtra(MyConstants.KITNAME, kitname);
//            intent.putExtra(MyConstants.BRAND, brand);
//            intent.putExtra(MyConstants.CATNO, catno);
//            intent.putExtra(MyConstants.URL, url);
//            intent.putExtra(MyConstants.URI, uri);
//            intent.putExtra(MyConstants.SCALE, scale);
//            intent.putExtra(MyConstants.CATEGORY, category);
//            intent.putExtra(MyConstants.YEAR, year);
//            intent.putExtra(MyConstants.DESCRIPTION, description);
//            intent.putExtra(MyConstants.ORIGINAL_NAME, origName);
//            intent.putExtra(MyConstants.NOTES, notes);
//            intent.putExtra(MyConstants.MEDIA, media);
//            intent.putExtra(MyConstants.QUANTITY, quantity);
//            intent.putExtra(MyConstants.STATUS, status);
//            intent.putExtra(MyConstants.SHOP, shop);
//            intent.putExtra(MyConstants.PURCHASE_DATE, purchaseDate);
//            intent.putExtra(MyConstants.PRICE, price);
//            intent.putExtra(MyConstants.CURRENCY, currency);
//            getActivity().startActivityForResult(intent, EDIT_ACTIVITY_CODE);
//        }
//    });
//
//    //Если пришли сюда из карточек кита, отключаем редактирование
//        if (workMode == MyConstants.MODE_AFTER_KIT) {
//        tvAftermarketTitle.setVisibility(View.GONE);
//        btnEdit.setVisibility(View.GONE);
//        rvAftermarket.setVisibility(View.GONE);
//    }
//    //Если из афтемаркета, вложенный афтер отключаем
//        if (workMode == MyConstants.MODE_AFTERMARKET) {
//        tvAftermarketTitle.setVisibility(View.GONE);
//        rvAftermarket.setVisibility(View.GONE);
//    }

}