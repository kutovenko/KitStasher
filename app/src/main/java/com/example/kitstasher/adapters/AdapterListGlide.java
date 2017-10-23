package com.example.kitstasher.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;

import java.io.File;

import static android.R.drawable.ic_menu_camera;

//import com.example.kitstasher.activity.MainActivity;
//import static android.support.v4.content.res.TypedArrayUtils.getString;

/**
 * Created by Алексей on 15.11.2016.
 */

public class AdapterListGlide extends CursorAdapter {
    private String ship, air, ground, space, car, other;
    private final Context context;

    public AdapterListGlide(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_kit2, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = new ViewHolder();
        holder.tvFullBrand = (TextView) view.findViewById(R.id.tvBrandItem);
        holder.tvFullKitname = (TextView) view.findViewById(R.id.tvKit_nameItem);
        holder.tvFullScale = (TextView) view.findViewById(R.id.tvScaleItem);
        holder.ivBoxart = (ImageView) view.findViewById(R.id.ivBoxartItem);
//        holder.ivTagItem = (ImageView) view.findViewById(R.id.ivTagItem);
//        holder.tvDescription = (TextView)view.findViewById(R.id.tvDescription);
//        holder.tvYear = (TextView)view.findViewById(R.id.tvYear);
//        holder.tvNoEngName = (TextView)view.findViewById(R.id.tvNoEngKitname);

        // Extract properties from cursor
        String url = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URL));
        String uri = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
        String brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
        String cat_no = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME));
        String scale = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE));
//        String category = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));
//        String year = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_YEAR));
//        String description = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_DESCRIPTION));
//        String noengname = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ORIGINAL_KIT_NAME));

//        String id = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ID)));
//        String status = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_STATUS)));
//        String media = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_MEDIA)));


//        String noengname = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ORIGINAL_KIT_NAME)));
//        ship = Constants.CAT_SEA;
//        air = Constants.CAT_AIR;
//        ground = Constants.CAT_GROUND;
//        space = Constants.CAT_SPACE;
//        other = Constants.CAT_OTHER;
//        car = Constants.CAT_AUTOMOTO;
//
//        if (ship.equals(category)) {
//            holder.ivTagItem.setImageResource(R.drawable.ic_tag_ship_black_24dp);
//        }
//        if (air.equals(category)) {
//            holder.ivTagItem.setImageResource(R.drawable.ic_tag_air_black_24dp);
//        }
//        if (ground.equals(category)) {
//            holder.ivTagItem.setImageResource(R.drawable.ic_tag_afv_black_24dp);
//        }
//        if (space.equals(category)) {
//            holder.ivTagItem.setImageResource(R.drawable.ic_tag_space_black_24dp);
//        }
//        if (other.equals(category)) {
//            holder.ivTagItem.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
//        }
//        if (car.equals(category)){
//            holder.ivTagItem.setImageResource(R.drawable.ic_directions_car_black_24dp);
//        }
//        if (Constants.CAT_FIGURES.equals(category)){
//            holder.ivTagItem.setImageResource(R.drawable.ic_wc_black_24dp);
//        }
//        if (Constants.CAT_FANTASY.equals(category)){
//            holder.ivTagItem.setImageResource(R.drawable.ic_android_black_24dp);
//        }

        // Populate fields with extracted properties
        holder.tvFullBrand.setText(brand + " " + cat_no);
        holder.tvFullScale.setText(scale);
        holder.tvFullKitname.setText(name);
//        holder.tvDescription.setText(getKitDescription(description));
//        holder.tvYear.setText(year);
//        holder.tvNoEngName.setText(noengname);

        //Check - if URI - load local image, else - load from cloud
        if (!Helper.isBlank(uri)) {
            Glide
                    .with(context)
                    .load(new File(Uri.parse(Environment.getExternalStorageDirectory()
                            + Constants.APP_FOLDER + uri).getPath()))
                    .placeholder(ic_menu_camera)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.crossFade()
                    .into(holder.ivBoxart);
        } else {
            Glide
                    .with(context)
                    .load(composeUrl(url))
                    .placeholder(ic_menu_camera)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.crossFade()
                    .into(holder.ivBoxart);
        }
    }

    private String composeUrl(String url){  //// TODO: 04.09.2017 Helper
        if (!Helper.isBlank(url)) {
            return Constants.BOXART_URL_PREFIX
                    + url
                    + getSuffix()
                    + Constants.JPG;
        }else{
            return "";
        }
    }


    //    private String getKitDescription(String description) {
//        String desc = "";
//        switch (description) {
//            case "0":
//                desc = "";
//                break;
//            case "1":
//                desc = context.getResources().getString(R.string.new_tool);
//                break;
//            case "2":
//                desc = context.getResources().getString(R.string.changed_parts);
//                break;
//            case "3":
//                desc = context.getResources().getString(R.string.new_decal);
//                break;
//            case "4":
//                desc = context.getResources().getString(R.string.changed_box);
//                break;
//            case "5":
//                desc = context.getResources().getString(R.string.repack);
//                break;
//            case "6":
//                desc = "";
//                break;
//            default:
//                desc = "";
//                break;
//        }
//        return desc;
//    }
    private String getKitDescription(String description) {
        String desc = "";
        switch (description) {
            case "0":
                desc = "";
                break;
            case "1":
                desc = context.getResources().getString(R.string.newkit);
                break;
            case "2":
                desc = context.getResources().getString(R.string.rebox);
                break;
            case "3":
                desc = context.getResources().getString(R.string.rebox);
                break;
            case "4":
                desc = context.getResources().getString(R.string.rebox);
                break;
            case "5":
                desc = context.getResources().getString(R.string.rebox);
                break;
            case "6":
                desc = "";
                break;
            default:
                desc = "";
                break;
        }
        return desc;
    }

    private String getSuffix(){ //// TODO: 04.09.2017 Helper
        String suffix = Constants.BOXART_URL_SMALL;
        SharedPreferences preferences = context.getSharedPreferences(Constants.BOXART_SIZE,
                Context.MODE_PRIVATE);
        if (preferences != null) {
            String temp = preferences.getString("boxart_size","");
            switch (temp){
                case Constants.BOXART_URL_COMPANY_SUFFIX:
                    suffix = "";
                    break;
                case Constants.BOXART_URL_SMALL:
                    suffix = Constants.BOXART_URL_SMALL;
                    break;
                case Constants.BOXART_URL_MEDIUM:
                    suffix = Constants.BOXART_URL_MEDIUM;
                    break;
                case Constants.BOXART_URL_LARGE:
                    suffix = Constants.BOXART_URL_LARGE;
                    break;
                default:
                    break;
            }
        }

        return suffix;
    }


    static class ViewHolder {
        ImageView ivBoxart, ivTagItem;
        TextView tvFullKitname, tvFullBrand, tvFullScale, tvYear, tvDescription, tvNoEngName;
    }

}