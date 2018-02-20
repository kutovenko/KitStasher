package com.example.kitstasher.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kitstasher.R;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;

/**
 * Created by Алексей on 23.01.2018.
 */

public class AdapterListWithEdit extends RecyclerView.Adapter<AdapterListWithEdit.ViewHolder> {
    private Cursor cursor;
    private int mode;
    private Context context;
    private DataSetObserver mDataSetObserver;
    private boolean mDataValid;
    private int mRowIdColumn;

    public AdapterListWithEdit(Cursor c, Context con, int m) {
        cursor = c;
        mode = m;
        context = con;
//        mRowIdColumn = mDataValid ? cursor.getColumnIndex("_id") : -1;
//        mDataValid = cursor != null;
//        mDataSetObserver = new DataSetObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//            }
//        };
//        if (cursor != null) {
//            cursor.registerDataSetObserver(mDataSetObserver);
//        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_with_edit, parent, false);
        return new AdapterListWithEdit.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        super.onBindViewHolder(holder, position);

        cursor.moveToPosition(position);
        final long id = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ID));
        String name = "";
        if (mode == MyConstants.MODE_A_SHOP) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.MYSHOPS_COLUMN_SHOP_NAME));
        } else if (mode == MyConstants.MODE_A_BRAND) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.BRANDS_COLUMN_BRAND));
        } else if (mode == MyConstants.MODE_A_LIST) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.MYLISTS_COLUMN_LIST_NAME));
        }
        holder.tvEditBrandListItem.setText(name);
        final String finalName = name;
        final Context context = holder.ibListItemEdit.getContext();
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
                        if (mode == MyConstants.MODE_A_BRAND) {
                            dbConnector.updateBrand(holder.getItemId(), newName);
//                            SettingsBrandsFragment.reloadEdit(holder.getAdapterPosition());
//                            SettingsFragment.refreshPages(holder.getAdapterPosition());

                        } else if (mode == MyConstants.MODE_A_SHOP) {
//                            dbConnector.updateShop(finalName, newName);
//                            SettingsFragment.refreshPages(holder.getAdapterPosition());

                        } else if (mode == MyConstants.MODE_A_LIST) {
//                            dbConnector.updateList(finalName, newName);
//
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
                if (mode == MyConstants.MODE_A_BRAND) {
                    dbConnector.delBrand(holder.getItemId());
//                    SettingsBrandsFragment.reloadDelete(holder.getAdapterPosition());

                } else if (mode == MyConstants.MODE_A_SHOP) {
                    dbConnector.deleteShopByName(finalName);
//                    SettingsFragment.refreshPages(holder.getAdapterPosition());

                } else if (mode == MyConstants.MODE_A_LIST) {
//                    dbConnector.deleteList();
//
                }

            }
        });


    }

//
//    /**
//     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
//     * closed.
//     */
//    public void changeCursor(Cursor cursor) {
//        Cursor old = swapCursor(cursor);
//        if (old != null) {
//            old.close();
//        }
//    }
//
//    /**
//     * Swap in a new Cursor, returning the old Cursor.  Unlike
//     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
//     * closed.
//     */
//    public Cursor swapCursor(Cursor newCursor) {
//        if (newCursor == cursor) {
//            return null;
//        }
//        final Cursor oldCursor = cursor;
//        if (oldCursor != null && mDataSetObserver != null) {
//            oldCursor.unregisterDataSetObserver(mDataSetObserver);
//        }
//        cursor = newCursor;
//        if (cursor != null) {
//            if (mDataSetObserver != null) {
//                cursor.registerDataSetObserver(mDataSetObserver);
//            }
//            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
//            mDataValid = true;
//            notifyDataSetChanged();
//        } else {
//            mRowIdColumn = -1;
//            mDataValid = false;
//            notifyDataSetChanged();
//            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
//        }
//        return oldCursor;
//    }


    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            return cursor.getLong(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ID));
        }
        return 0;
    }


    public Cursor getCursor() {
        return cursor;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEditBrandListItem;
        TextView tvListItemDate;
        ImageButton ibListItemEdit;
        ImageButton ibListItemDelete;

        ViewHolder(View view) {
            super(view);
            tvEditBrandListItem = view.findViewById(R.id.tvMylistName);
            tvListItemDate = view.findViewById(R.id.tvListDateAdded);
            ibListItemEdit = view.findViewById(R.id.ibtnEditMyList);
            ibListItemDelete = view.findViewById(R.id.ibtnDeleteMyList);
        }
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }
    }
}
