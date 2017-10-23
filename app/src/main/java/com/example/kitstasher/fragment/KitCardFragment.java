package com.example.kitstasher.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.activity.EditActivity;
import com.example.kitstasher.adapters.AdapterAfterItemsList;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by Алексей on 03.09.2017.
 */

public class KitCardFragment extends Fragment {
    private Context context;
    private ImageView ivBoxart, ivCategory;
    private TextView tvKitname, tvBrand, tvBrandcatno, tvScale, tvScalematesUrl, tvGoogleUrl,
            tvStatus, tvMedia, tvYear, tvNotes, tvQuantity, tvShop, tvDatePurchased,
            tvPrice, tvCurrency;
    private ListView lvAftermarket;
    private Button btnBack, btnEdit;
    private String category;
    private final int EDIT_ACTIVITY_CODE = 21;
    private boolean demoMode;

    private ArrayList<Long> list;

    public KitCardFragment() {
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

        final Fragment thisFragment = this;


        final char mode = getArguments().getChar(Constants.EDIT_MODE);

        final int cursorPosition = getArguments().getInt(Constants.CURSOR_POSITION);
        final long id = getArguments().getLong(Constants.ID);
        list = (ArrayList<Long>) getArguments().getSerializable(Constants.IDS);
//        final String sortBy = getArguments().getString(Constants.SORT_BY);
//        final int categoryToReturn = getArguments().getInt(Constants.LIST_CATEGORY);
//        final String scaleFilter = getArguments().getString(Constants.SCALE_FILTER);
//        final String brandFilter = getArguments().getString(Constants.BRAND_FILTER);
//        final String kitnameFilter = getArguments().getString(Constants.KITNAME_FILTER);
//        final String statusFilter = getArguments().getString(Constants.STATUS_FILTER);
//        final String mediaFilter = getArguments().getString(Constants.MEDIA_FILTER);
//        final int position = getArguments().getInt(Constants.LIST_POSITION);


        final Cursor cursor = dbConnector.getRecById(id);
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
        final String origName = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ORIGINAL_KIT_NAME));
        final String notes = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_NOTES));
        final int media = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_MEDIA));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_QUANTITY));
        final int status = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_STATUS));
        final String shop = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_PLACE));
        final String purchaseDate = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_DATE));
        final int price = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PRICE));
        final String currency = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CURRENCY));
        String listname = "";


        btnEdit = (Button) view.findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra(Constants.CURSOR_POSITION, cursorPosition);
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
                demoMode = true;
                getActivity().startActivityForResult(intent, EDIT_ACTIVITY_CODE);
            }
        });

//        Button btnDelete = (Button)view.findViewById(R.id.btnDelete);
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mode == Constants.MODE_LIST){
//                    dbConnector.delListItem(id);
//                }else {
//                    dbConnector.delRec(id);
//                }
//
//
//
////                getFragmentManager().popBackStack();
////                if (cursorPosition != 0){
//                    list.remove(cursorPosition);
//                    Intent intent = new Intent(getActivity(), ViewActivity.class);
//                    intent.putExtra(Constants.IDS, list);
//
//                intent.putExtra(Constants.SORT_BY, sortBy);
//                intent.putExtra(Constants.EDIT_MODE, mode);
//                intent.putExtra(Constants.LIST_CATEGORY, categoryToReturn);
//                intent.putExtra(Constants.LIST_POSITION, position);
//                intent.putExtra(Constants.SCALE_FILTER, scaleFilter);
//                intent.putExtra(Constants.BRAND_FILTER, brandFilter);
//                intent.putExtra(Constants.KITNAME_FILTER, kitnameFilter);
//                intent.putExtra(Constants.STATUS_FILTER, statusFilter);
//                intent.putExtra(Constants.MEDIA_FILTER, mediaFilter);
//                intent.putExtra("was_deleted", true);
//
//
//                startActivity(intent);
//
////                FragmentManager manager = getActivity().getSupportFragmentManager();
////                FragmentTransaction trans = manager.beginTransaction();
////                trans.remove(thisFragment);
////                trans.commit();
////                manager.popBackStack();
////                }else{
////                    1;
////                }
//            }
//        });

        ivBoxart = (ImageView)view.findViewById(R.id.ivBoxart);
        tvKitname = (TextView)view.findViewById(R.id.tvKitname);
        tvKitname.setText(kitname);
        tvBrand = (TextView)view.findViewById(R.id.tvBrand);
        tvBrand.setText(brand);
        tvBrandcatno = (TextView)view.findViewById(R.id.tvCatno);
        tvBrandcatno.setText(catno);
        tvScale = (TextView)view.findViewById(R.id.tvScale);
        tvScale.setText("1/" + String.valueOf(scale));
        ivCategory = (ImageView) view.findViewById(R.id.ivCategory);

        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        tvStatus.setText(codeToStatus(status));
        tvMedia = (TextView) view.findViewById(R.id.tvMedia);
        tvMedia.setText(codeToMedia(media));
        TextView tvDesc = (TextView) view.findViewById(R.id.tvDesc);
        tvDesc.setText(codeToDescription(description));
        tvYear = (TextView) view.findViewById(R.id.tvYear);
        tvYear.setText(year);
        tvShop = (TextView) view.findViewById(R.id.tvShop);
        tvShop.setText(shop);

        tvNotes = (TextView) view.findViewById(R.id.tvNotes);
        if (!notes.equals("")) {
            tvNotes.setText(notes);
        }
        tvQuantity = (TextView) view.findViewById(R.id.tvQuantity);
        tvQuantity.setText(String.valueOf(quantity));
        tvShop = (TextView) view.findViewById(R.id.tvShop);
        tvShop.setText(shop);
        tvDatePurchased = (TextView) view.findViewById(R.id.tvDatePurchased);
        tvDatePurchased.setText(purchaseDate);
        tvPrice = (TextView) view.findViewById(R.id.tvPrice);
        tvPrice.setText(String.valueOf(price / 100));
        tvCurrency = (TextView) view.findViewById(R.id.tvCurrency);
        tvCurrency.setText(currency);
        lvAftermarket = (ListView) view.findViewById(R.id.lvAftermarket);
        tvScalematesUrl = (TextView)view.findViewById(R.id.tvScalemates);
        tvScalematesUrl.setClickable(true);
        tvScalematesUrl.setMovementMethod(LinkMovementMethod.getInstance());
        String scalematesText = "<a href='https://www.scalemates.com/"
                + scalematesUrl
                + "'> " + getString(R.string.Look_up_on_Scalemates) + "</a>";
        tvScalematesUrl.setText(Helper.fromHtml(scalematesText));

        tvGoogleUrl = (TextView)view.findViewById(R.id.tvGoogle);
        tvGoogleUrl.setClickable(true);
        tvGoogleUrl.setMovementMethod(LinkMovementMethod.getInstance());
        String googleText = "<a href='https://www.google.com/search?"
                + "q=" + brand + "+" + catno + "+" + kitname + "+" + scale
                + "'> " + getString(R.string.Search_with_Google) + "</a>";
        tvGoogleUrl.setText(Helper.fromHtml(googleText));


        if (!Helper.isBlank(uri)) {
            Glide
                    .with(context)
                    .load(new File(Uri.parse(Environment.getExternalStorageDirectory()
                            + Constants.APP_FOLDER + uri).getPath()))
//                    .placeholder(ic_menu_camera)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.crossFade()
                    .into(ivBoxart);
        } else {
            Glide
                    .with(context)
                    .load(composeUrl(url))
//                .placeholder(ic_menu_camera)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .crossFade()
                    .into(ivBoxart);
        }

        Cursor aCursor = dbConnector.getAftermarketForKit(id, listname);
        AdapterAfterItemsList afterAdapter = new AdapterAfterItemsList(context, aCursor, 0, id,
                listname, mode, demoMode);
        lvAftermarket.setAdapter(afterAdapter);
        lvAftermarket.setClickable(false);

        setListViewHeightBasedOnChildren(lvAftermarket);
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
        String ship = Constants.CAT_SEA;
        String air = Constants.CAT_AIR;
        String ground = Constants.CAT_GROUND;
        String space = Constants.CAT_SPACE;
        String other = Constants.CAT_OTHER;
        String car = Constants.CAT_AUTOMOTO;

        if (ship.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_ship_black_24dp);
        }
        if (air.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_air_black_24dp);
        }
        if (ground.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_afv_black_24dp);
        }
        if (space.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_tag_space_black_24dp);
        }
        if (other.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        }
        if (car.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_directions_car_black_24dp);
        }
        if (Constants.CAT_FIGURES.equals(category)) {
            ivCategory.setImageResource(R.drawable.ic_wc_black_24dp);
        }
        if (Constants.CAT_FANTASY.equals(category)) {
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
                    + "pristine"
                    + Constants.JPG;
        }else{
            return "";
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
