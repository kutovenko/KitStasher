package com.example.kitstasher.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.activity.AftermarketActivity;
import com.example.kitstasher.activity.EditActivity;
import com.example.kitstasher.adapters.AdapterAfterItemsList;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;

import java.io.File;


/**
 * Created by Алексей on 03.09.2017. Main display
 */

public class ItemCardFragment extends Fragment {
    private Context context;
    private ImageView ivCategory;
    private String category, tableName;
    private final int EDIT_ACTIVITY_CODE = 21;
    private boolean demoMode;

    public ItemCardFragment() {
        super();
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        //todo REFACTOR
        DbConnector dbConnector = new DbConnector(context);
        dbConnector.open();
        final long id = getArguments().getLong(Constants.ID);
        Cursor cursor = dbConnector.getRecById(id);
        cursor.moveToFirst();

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_card, container, false);
        context = getActivity();
        final DbConnector dbConnector = new DbConnector(context);
        dbConnector.open();

        final char mode = getArguments().getChar(Constants.EDIT_MODE);
        switch (mode) {
            case 'a':
                tableName = DbConnector.TABLE_AFTERMARKET;
                break;
            case 'm':
                tableName = DbConnector.TABLE_KITS;
                break;
            case 'l':
                tableName = DbConnector.TABLE_MYLISTSITEMS;
                break;
        }

        final int position = getArguments().getInt(Constants.POSITION);
        final long id = getArguments().getLong(Constants.ID);
        final Cursor cursor = dbConnector.getItemById(tableName, id);
        cursor.moveToFirst();
        final String kitname = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME));

        final String brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
        final String catno = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO));
        final int scale = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE));
        final String url = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URL));
        final String uri = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
        String scalematesUrl = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALEMATES_URL));
        category = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));
        final String year = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_YEAR));
        final String description = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_DESCRIPTION));
//        String origName = Constants.EMPTY;
//        if (mode != Constants.MODE_AFTERMARKET){
        final String origName = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ORIGINAL_NAME));
//        }
        final String notes = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_NOTES));
        final int media = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_MEDIA));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_QUANTITY));
        final int status = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_STATUS));
        final String shop = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_PLACE));
        final String purchaseDate = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_DATE));
        final int price = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PRICE));
        final String currency = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CURRENCY));
        String listname = "";
        demoMode = true;

        Button btnEdit = view.findViewById(R.id.btnEdit);
//        final String finalOrigName = origName;
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra(Constants.POSITION, position);
                intent.putExtra(Constants.EDIT_MODE, mode);
                intent.putExtra(Constants.ID, id);
                intent.putExtra(Constants.LIST_CATEGORY, category);
                intent.putExtra(Constants.KITNAME, kitname);
                intent.putExtra(Constants.BRAND, brand);
                intent.putExtra(Constants.CATNO, catno);
                intent.putExtra(Constants.URL, url);
                intent.putExtra(Constants.URI, uri);
                intent.putExtra(Constants.SCALE, scale);
                intent.putExtra(Constants.CATEGORY, category);
                intent.putExtra(Constants.YEAR, year);
                intent.putExtra(Constants.DESCRIPTION, description);
                intent.putExtra(Constants.ORIGINAL_NAME, origName);
                intent.putExtra(Constants.NOTES, notes);
                intent.putExtra(Constants.MEDIA, media);
                intent.putExtra(Constants.QUANTITY, quantity);
                intent.putExtra(Constants.STATUS, status);
                intent.putExtra(Constants.SHOP, shop);
                intent.putExtra(Constants.PURCHASE_DATE, purchaseDate);
                intent.putExtra(Constants.PRICE, price);
                intent.putExtra(Constants.CURRENCY, currency);

                getActivity().startActivityForResult(intent, EDIT_ACTIVITY_CODE);
            }
        });


        ImageView ivBoxart = view.findViewById(R.id.ivBoxart);
        TextView tvKitname = view.findViewById(R.id.tvKitname);
        tvKitname.setText(kitname);
        TextView tvOriginalKitName = view.findViewById(R.id.tvOriginalKitName);
        tvOriginalKitName.setText(origName);
        TextView tvBrand = view.findViewById(R.id.tvBrand);
        tvBrand.setText(brand); //todo ????
        TextView tvBrandcatno = view.findViewById(R.id.tvCatno);
        tvBrandcatno.setText(catno);
        TextView tvScale = view.findViewById(R.id.tvScale);
        tvScale.setText("1/" + String.valueOf(scale));
        ivCategory = view.findViewById(R.id.ivCategory);

        TextView tvStatus = view.findViewById(R.id.tvStatus);
        tvStatus.setText(codeToStatus(status));
        TextView tvMedia = view.findViewById(R.id.tvMedia);
        tvMedia.setText(codeToMedia(media));
        TextView tvDesc = view.findViewById(R.id.tvDesc);
        tvDesc.setText(codeToDescription(description));
        TextView tvYear = view.findViewById(R.id.tvYear);
        tvYear.setText(year);
        TextView tvShop = view.findViewById(R.id.tvShop);
        tvShop.setText(shop);

        TextView tvNotes = view.findViewById(R.id.tvNotes);
        if (!notes.equals("")) {
            tvNotes.setText(notes);
        }
        TextView tvQuantity = view.findViewById(R.id.tvQuantity);
        tvQuantity.setText(String.valueOf(quantity));
        TextView tvDatePurchased = view.findViewById(R.id.tvDatePurchased);
        tvDatePurchased.setText(purchaseDate);
        TextView tvPrice = view.findViewById(R.id.tvPrice);
        tvPrice.setText(String.valueOf(price / 100));
        TextView tvCurrency = view.findViewById(R.id.tvCurrency);
        tvCurrency.setText(currency);
        ListView lvAftermarket = view.findViewById(R.id.lvAftermarket);
        TextView tvScalematesUrl = view.findViewById(R.id.tvScalemates);
        tvScalematesUrl.setClickable(true);
        tvScalematesUrl.setMovementMethod(LinkMovementMethod.getInstance());

        String scalematesText = "<a href='https://www.scalemates.com/"
                + scalematesUrl
                + "'> " + getString(R.string.Look_up_on_Scalemates) + "</a>";
        if (scalematesUrl != null) {
            tvScalematesUrl.setText(Helper.fromHtml(scalematesText));
        } else {
            tvScalematesUrl.setText("");
        }

        TextView tvGoogleUrl = view.findViewById(R.id.tvGoogle);
        tvGoogleUrl.setClickable(true);
        tvGoogleUrl.setMovementMethod(LinkMovementMethod.getInstance());
        String googleText = "<a href='https://www.google.com/search?"
                + "q=" + brand + "+" + catno + "+" + kitname + "+" + scale
                + "'> " + getString(R.string.Search_with_Google) + "</a>";
        tvGoogleUrl.setText(Helper.fromHtml(googleText));


        if (!Helper.isBlank(uri)) {
            Glide
                    .with(context)
                    .load(new File(Uri.parse(uri).getPath()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivBoxart);
        } else {
            Glide
                    .with(context)
                    .load(composeUrl(url))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivBoxart);
        }

        Cursor aCursor = dbConnector.getAftermarketForKit(id, listname);
        AdapterAfterItemsList afterAdapter = new AdapterAfterItemsList(context, aCursor, 0, id,
                listname, mode, demoMode);
        lvAftermarket.setAdapter(afterAdapter);
//        lvAftermarket.setClickable(true);

        setListViewHeightBasedOnChildren(lvAftermarket);

        lvAftermarket.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, AftermarketActivity.class);
                intent.putExtra(Constants.AFTER_ID, l);
                intent.putExtra(Constants.ID, id);
                startActivity(intent);
            }
        });

        setCategoryImage();
        return view;
    }

    private String codeToDescription(String code) {
        String desc = "";
        switch (code) {
            case Constants.NEW_TOOL:
                desc = context.getResources().getString(R.string.new_tool);
                break;
            case Constants.REBOX:
                desc = context.getResources().getString(R.string.rebox);
                break;
        }
        return desc;
    }

    private String codeToStatus(int code) {
        String status;
        switch (code) {
            case Constants.STATUS_NEW:
                status = context.getResources().getString(R.string.status_new);
                break;
            case Constants.STATUS_OPENED:
                status = context.getResources().getString(R.string.status_opened);
                break;
            case Constants.STATUS_STARTED:
                status = context.getResources().getString(R.string.status_started);
                break;
            case Constants.STATUS_INPROGRESS:
                status = context.getResources().getString(R.string.status_inprogress);
                break;
            case Constants.STATUS_FINISHED:
                status = context.getResources().getString(R.string.status_finished);
                break;
            case Constants.STATUS_LOST:
                status = context.getResources().getString(R.string.status_lost_sold);
                break;
            default:
                status = context.getResources().getString(R.string.status_new);
                break;
        }
        return status;
    }

    private String codeToMedia(int mediaCode) {
        String media;
        switch (mediaCode) {
            case Constants.M_CODE_OTHER:
                media = context.getResources().getString(R.string.media_other);
                break;
            case Constants.M_CODE_INJECTED:
                media = context.getResources().getString(R.string.media_injected);
                break;
            case Constants.M_CODE_SHORTRUN:
                media = context.getResources().getString(R.string.media_shortrun);
                break;
            case Constants.M_CODE_RESIN:
                media = context.getResources().getString(R.string.media_resin);
                break;
            case Constants.M_CODE_VACU:
                media = context.getResources().getString(R.string.media_vacu);
                break;
            case Constants.M_CODE_PAPER:
                media = context.getResources().getString(R.string.media_paper);
                break;
            case Constants.M_CODE_WOOD:
                media = context.getResources().getString(R.string.media_wood);
                break;
            case Constants.M_CODE_METAL:
                media = context.getResources().getString(R.string.media_metal);
                break;
            case Constants.M_CODE_3DPRINT:
                media = context.getResources().getString(R.string.media_3dprint);
                break;
            case Constants.M_CODE_MULTIMEDIA:
                media = context.getResources().getString(R.string.media_multimedia);
                break;
            default:
                media = context.getResources().getString(R.string.media_other);
                break;
        }
        return media;
    }

    private void setCategoryImage() {
        if (Constants.CODE_SEA.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_ship_black_24dp);
        }
        if (Constants.CODE_AIR.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_air_black_24dp);
        }
        if (Constants.CODE_GROUND.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_afv_black_24dp);
        }
        if (Constants.CODE_SPACE.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_space_black_24dp);
        }
        if (Constants.CODE_OTHER.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        }
        if (Constants.CODE_AUTOMOTO.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_directions_car_black_24dp);
        }
        if (Constants.CODE_FIGURES.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_wc_black_24dp);
        }
        if (Constants.CODE_FANTASY.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_android_black_24dp);
        }
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    private void setListViewHeightBasedOnChildren(ListView listView) { //todo helper
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    private String composeUrl(String url){//// TODO: 04.09.2017 Helper
        if (!Helper.isBlank(url)) {
            return Constants.BOXART_URL_PREFIX
                    + url
                    + Constants.BOXART_URL_LARGE
                    + Constants.JPG;
        }else{
            return ""; //TODO проверить!!!
        }
    }

    private String getSuffix(){
        String suffix = Constants.BOXART_URL_SMALL;
        SharedPreferences preferences = context.getSharedPreferences(Constants.BOXART_SIZE,
                Context.MODE_PRIVATE);
        if (preferences != null) {
            String temp = preferences.getString(Constants.BOXART_SIZE, "");
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
}
