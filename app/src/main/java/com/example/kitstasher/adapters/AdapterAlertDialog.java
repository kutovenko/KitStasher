package com.example.kitstasher.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.objects.Item;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.Helper;

import java.util.List;

/**
 * Created by Алексей on 06.07.2017.
 */

public class AdapterAlertDialog extends ArrayAdapter<Item> {
    private final List<Item> list;
    private final Activity context;

    static class ViewHolder {
        protected TextView name;
        protected ImageView ivItemBoxart;
    }
    public AdapterAlertDialog(Activity context, List<Item> list) {
        super(context, R.layout.item_alertdialog, list);
        this.context = context;
        this.list = list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.item_alertdialog, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.tvItemTitle);
            viewHolder.ivItemBoxart = (ImageView) view.findViewById(R.id.ivItemBoxart);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(list.get(position).getItemTitle()
        );
        Glide
                .with(context)
                .load(composeUrl(list.get(position).getItemBoxartUrl()))
//                .placeholder(ic_menu_camera)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.crossFade()
                .into(holder.ivItemBoxart);
        return view;
    }

    private String composeUrl(String url){
        if (!Helper.isBlank(url)) {
            return Constants.BOXART_URL_PREFIX
                    + url
                    + Constants.BOXART_URL_SMALL
                    + Constants.BOXART_URL_POSTFIX;
        }else{
            return "";
        }

    }
}
