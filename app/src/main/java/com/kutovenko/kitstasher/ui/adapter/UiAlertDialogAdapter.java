package com.kutovenko.kitstasher.ui.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kutovenko.kitstasher.model.Item;
import com.kutovenko.kitstasher.util.Helper;
import com.kutovenko.kitstasher.util.MyConstants;

import java.util.List;

/**
 * Created by Алексей on 06.07.2017. Adapter for AlertDialog in ScanFragment and ManualAddFragment.
 *
 * Адаптер для AlertDialog в ScanFragment и ManualAddFragment.
 */

public class UiAlertDialogAdapter extends ArrayAdapter<Item> {
    private final List<Item> list;
    private final Activity context;

    private static class ViewHolder {
        private TextView name;
        private ImageView ivItemBoxart;
    }
    public UiAlertDialogAdapter(Activity context, List<Item> list) {
        super(context, com.kutovenko.kitstasher.R.layout.item_alertdialog, list);
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(com.kutovenko.kitstasher.R.layout.item_alertdialog, parent, false);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = view.findViewById(com.kutovenko.kitstasher.R.id.tvItemTitle);
            viewHolder.ivItemBoxart = view.findViewById(com.kutovenko.kitstasher.R.id.ivItemBoxart);
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
                .into(holder.ivItemBoxart);
        return view;
    }

    private String composeUrl(String url){
        if (!Helper.isBlank(url)) {
            return url
                    + MyConstants.BOXART_URL_SMALL
                    + MyConstants.JPG;
        }else{
            return MyConstants.EMPTY;
        }
    }
}
