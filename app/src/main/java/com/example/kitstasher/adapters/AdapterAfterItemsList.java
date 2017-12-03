package com.example.kitstasher.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kitstasher.R;
import com.example.kitstasher.other.DbConnector;

/**
 * Created by Алексей on 13.09.2017.
 */

public class AdapterAfterItemsList extends CursorAdapter {
    private Context context;
    private long kitId;
    private String listname;
    private char editMode;
    private boolean demoMode;

    public AdapterAfterItemsList(Context context, Cursor c, int flags, long kitId, String listname,
                                 char editMode, boolean demoMode) {
        super(context, c, 0);
        this.context = context;
        this.kitId = kitId;
        this.listname = listname;
        this.editMode = editMode;
        this.demoMode = demoMode;
    }

    private static class ViewHolder {
        private TextView tvItemAfterName, tvItemAfterBrand, tvItemAfterCatno;
        //        private ImageView ivItemAfterBoxart;
        private ImageButton ibRemoveAftermarket;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_aft_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final DbConnector dbConnector = new DbConnector(context);
        dbConnector.open();
        ViewHolder holder = new ViewHolder();
        holder.tvItemAfterName = view.findViewById(R.id.tvItemAfterName);
        holder.tvItemAfterBrand = view.findViewById(R.id.tvItemAfterBrand);
        holder.tvItemAfterCatno = view.findViewById(R.id.tvItemAfterCatno);
//        holder.ivItemAfterBoxart = (ImageView)view.findViewById(R.id.ivItemAfterBoxart);
        holder.ibRemoveAftermarket = view.findViewById(R.id.ibRemoveAftermarket);
        if (demoMode) {
            holder.ibRemoveAftermarket.setVisibility(View.GONE);
        }

        Object obj = cursor.getString(cursor.getColumnIndex(DbConnector.COLUMN_ID));
        holder.ibRemoveAftermarket.setTag(obj);

        holder.ibRemoveAftermarket.setFocusable(false);
        holder.ibRemoveAftermarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object obj = view.getTag();
                String st = obj.toString();
                long aftId = Long.valueOf(st);
                dbConnector.removeAftermarketFromKit(aftId);
                Cursor newcursor = dbConnector.getAftermarketForKit(kitId, listname);
                changeCursor(newcursor);
                notifyDataSetChanged();
            }
        });

        String name = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_AFTERMARKET_NAME));
        String brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
        String catno = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO));
//        String uri =  cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));

        holder.tvItemAfterName.setText(name);
        holder.tvItemAfterBrand.setText(brand);
        holder.tvItemAfterCatno.setText(catno);

//        Glide
//                .with(context)
//                .load(new File(Uri.parse(Environment.getExternalStorageDirectory()
//                        + Constants.APP_FOLDER + uri).getPath()))
//                .placeholder(ic_menu_camera)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                //.crossFade()
//                .into(holder.ivItemAfterBoxart);
    }
}
