package com.example.kitstasher.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.activity.ViewActivity;
import com.example.kitstasher.fragment.KitsFragment;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import static android.R.drawable.ic_menu_camera;

//import com.example.kitstasher.fragment.AftermarketFragment;

/**
 * Created by Алексей on 18.01.2018.
 * Adapter for kits, aftermarket recycler view.
 */

public class AdapterKitList extends CursorRecyclerViewAdapter<AdapterKitList.ViewHolder> {
    private Context context;
    private String[] filters;
    private String activeTable,
            sortBy,
            allTag,
            listname,
            category;
    private int tabToReturn;
    private char workMode;
    private ArrayList<Long> ids;
    private ArrayList<Integer> positions;

    public AdapterKitList(Cursor cursor, Context context, String[] filters, String activeTable,
                          int tabToReturn, char workMode, String sortBy,
                          String allTag, String listname
    ) {
        super(context, cursor);
        this.context = context;
        this.filters = filters;
        this.activeTable = activeTable;
        this.tabToReturn = tabToReturn;
        this.workMode = workMode;
        this.sortBy = sortBy;
        this.allTag = allTag;
        this.listname = listname;
        ids = new ArrayList<>();
        positions = new ArrayList<>();

        for (int i = 0; i < getItemCount(); i++) {
            ids.add(getItemId(i)); //заполняем список идентификаторов
            positions.add(i);
        }
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

    @Override
    public AdapterKitList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kit, parent, false);
        return new AdapterKitList.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        cursor.moveToPosition(holder.getAdapterPosition());

        final long id = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ID));
        String url = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URL));
        final String uri = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
        String brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
        String cat_no = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME));
        String scale = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE));
        final String onlneId = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ID_ONLINE));
        category = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));

//        String b = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BARCODE));

        String fullBrand = brand + " " + cat_no;
        holder.tvFullBrand.setText(fullBrand);
        holder.tvFullScale.setText(scale);
        holder.tvFullKitname.setText(name);

        if (!Helper.isBlank(uri)) {
            Glide
                    .with(context)
                    .load(new File(Uri.parse(uri).getPath()))
                    .placeholder(ic_menu_camera)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivBoxart);
        } else {
            Glide
                    .with(context)
                    .load(composeUrl(url))
                    .placeholder(ic_menu_camera)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivBoxart);
        }

        holder.llKitItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(context, ViewActivity.class);
                int idd = holder.getAdapterPosition();
                intent.putExtra(MyConstants.AFTER_ID, id);//ид афтемаркета, на котором должен открыться пейджер!
                intent.putExtra(MyConstants.ID, id);//ид кита, на котором должен открыться пейджер
                // отправляем режим редактирования - кит (таблица кита), афтер, если смотрим таблицу афтера - излишне, мы в афтермоде
                intent.putExtra(MyConstants.WORK_MODE, workMode);
                intent.putExtra(MyConstants.LISTNAME, listname);

                //общие параметры для передачи
                intent.putExtra(MyConstants.POSITION, idd);//ид открытия пейджера
                intent.putExtra(MyConstants.SORT_BY, sortBy);
                intent.putExtra(MyConstants.CATEGORY_TAB, tabToReturn);
                intent.putExtra(MyConstants.CATEGORY, category);
                intent.putExtra(MyConstants.TAG, allTag);
                if (filters.length < 1) {
                    filters = new String[]{"", "", "", "", ""};
                }
                intent.putExtra(MyConstants.SCALE_FILTER, filters[0]);
                intent.putExtra(MyConstants.BRAND_FILTER, filters[1]);
                intent.putExtra(MyConstants.KITNAME_FILTER, filters[2]);
                intent.putExtra(MyConstants.STATUS_FILTER, filters[3]);
                intent.putExtra(MyConstants.MEDIA_FILTER, filters[4]);
                intent.putExtra(MyConstants.IDS, ids);
                intent.putExtra(MyConstants.POSITIONS, positions);
                intent.putExtra(MyConstants.SORT_BY, sortBy);
                intent.putExtra(MyConstants.FILTERS, (Serializable) filters);
                context.startActivity(intent); //интент на просмотр в пейджере
                // пойдет или в китакт или в афтерактивити
            }
        });

        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(id, holder.getAdapterPosition(), uri, onlneId);
            }
        });
    }

    private String composeUrl(String url) {
        return url + MyConstants.BOXART_URL_SMALL + MyConstants.JPG;
    }

    private void showDeleteDialog(final long itemId, final int currentPosition, final String uri,
                                  final String onlineId) {
        final DbConnector dbConnector = new DbConnector(context);
        dbConnector.open();

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        if (workMode == MyConstants.MODE_LIST) {
            dialogBuilder.setTitle(R.string.Do_you_wish_to_delete_from_list);
        } else {
            dialogBuilder.setTitle(R.string.Do_you_wish_to_delete);
        }
        dialogBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!Helper.isBlank(uri)) {
                    File file = new File(uri);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                dbConnector.deleteAllAftermarketForKit(itemId);
                deleteFromOnlineStash(onlineId);
                dbConnector.delRec(activeTable, itemId);
                Cursor newcursor = dbConnector.filteredKits(activeTable, filters, sortBy, category, MyConstants.EMPTY);
                notifyItemRemoved(currentPosition);
                changeCursor(newcursor);
                KitsFragment.refreshPages();
//                if (workMode == MyConstants.MODE_KIT) {
//                    KitsFragment.refreshPages();
//                } else if (workMode == MyConstants.MODE_AFTERMARKET) {
//                    AftermarketFragment.refreshPages();
//                }
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