package com.example.kitstasher.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;

import java.io.File;

import static android.R.drawable.ic_menu_camera;

//import com.example.kitstasher.activity.MainActivity;
//import static android.support.v4.content.res.TypedArrayUtils.getString;

/**
 * Created by Алексей on 15.11.2016. Universal adapter
 */

public class AdapterListGlide extends CursorAdapter {
    private final Context context;

    public AdapterListGlide(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_kit, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = new ViewHolder();
        holder.tvFullBrand = view.findViewById(R.id.tvBrandItem);
        holder.tvFullKitname = view.findViewById(R.id.tvKit_nameItem);
        holder.tvFullScale = view.findViewById(R.id.tvScaleItem);
        holder.ivBoxart = view.findViewById(R.id.ivBoxartItem);

        // Extract properties from cursor
        String url = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URL));
        String uri = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
        String brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
        String cat_no = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME));
        String scale = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE));

//        String sm = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALEMATES_URL));

//        String cat = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_STATUS));

        // Populate fields with extracted properties
        holder.tvFullBrand.setText(brand + " " + cat_no);
        holder.tvFullScale.setText(scale);
        holder.tvFullKitname.setText(name);

        //Check - if URI - load local image, else - load from cloud
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
    }

    private String composeUrl(String url){  //// TODO: 04.09.2017 Helper
        if (!Helper.isBlank(url)) {
            return MyConstants.BOXART_URL_PREFIX
                    + url
                    + getSuffix()
                    + MyConstants.JPG;
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
//                desc = context.getResources().getString(R.string.newkit);
//                break;
//            case "2":
//                desc = context.getResources().getString(R.string.rebox);
//                break;
//            case "3":
//                desc = context.getResources().getString(R.string.rebox);
//                break;
//            case "4":
//                desc = context.getResources().getString(R.string.rebox);
//                break;
//            case "5":
//                desc = context.getResources().getString(R.string.rebox);
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

    private String getSuffix(){ //// TODO: 04.09.2017 Helper
        String suffix = MyConstants.BOXART_URL_SMALL;
        SharedPreferences preferences = context.getSharedPreferences(MyConstants.BOXART_SIZE,
                Context.MODE_PRIVATE);
        if (preferences != null) {
            String temp = preferences.getString("boxart_size","");
            switch (temp){
                case MyConstants.BOXART_URL_COMPANY_SUFFIX:
                    suffix = "";
                    break;
                case MyConstants.BOXART_URL_SMALL:
                    suffix = MyConstants.BOXART_URL_SMALL;
                    break;
                case MyConstants.BOXART_URL_MEDIUM:
                    suffix = MyConstants.BOXART_URL_MEDIUM;
                    break;
                case MyConstants.BOXART_URL_LARGE:
                    suffix = MyConstants.BOXART_URL_LARGE;
                    break;
                default:
                    break;
            }
        }

        return suffix;
    }


    static class ViewHolder {
        ImageView ivBoxart;
        TextView tvFullKitname, tvFullBrand, tvFullScale;
    }

}