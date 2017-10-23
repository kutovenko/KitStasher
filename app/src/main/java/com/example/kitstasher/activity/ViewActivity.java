package com.example.kitstasher.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.kitstasher.R;
import com.example.kitstasher.adapters.AdapterViewCards;
import com.example.kitstasher.fragment.KitCardFragment;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;

import java.util.ArrayList;
import java.util.List;

/*
* Universal Activity for displaying kit and aftermarket pages in pager based on cursor.
 */

public class ViewActivity extends AppCompatActivity {
    private Cursor cursor;
    //    private String[] filters;
    private Long[] ids;
    private ArrayList<Long> list;

    //    private String sortBy;
    private final int EDIT_ACTIVITY_CODE = 21;
    private int cursorPosition;
    private String tableName;
    private String sortBy;
    int categoryToReturn;

    private String scaleFilter;
    private String brandFilter;
    private String kitnameFilter;
    private String statusFilter;
    private String mediaFilter;
    private int position;
    private boolean fragmentWasDeleted;
    private char mode;
    public ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit);

        DbConnector dbConnector = new DbConnector(this);
        dbConnector.open();
//        String sortBy = Constants._ID;

        fragmentWasDeleted = false;

        fragmentWasDeleted = getIntent().getExtras().getBoolean("was_deleted");


        list = (ArrayList<Long>) getIntent().getSerializableExtra(Constants.IDS);
        mode = getIntent().getExtras().getChar(Constants.EDIT_MODE);


        ids = new Long[list.size()];
        list.toArray(ids);

        sortBy = getIntent().getExtras().getString(Constants.SORT_BY);

        position = getIntent().getExtras().getInt(Constants.LIST_POSITION);
//        long id = getIntent().getExtras().getLong(Constants.ID);
        categoryToReturn = getIntent().getExtras().getInt(Constants.LIST_CATEGORY);

        scaleFilter = getIntent().getExtras().getString(Constants.SCALE_FILTER);
        brandFilter = getIntent().getExtras().getString(Constants.BRAND_FILTER);
        kitnameFilter = getIntent().getExtras().getString(Constants.KITNAME_FILTER);
        statusFilter = getIntent().getExtras().getString(Constants.STATUS_FILTER);
        mediaFilter = getIntent().getExtras().getString(Constants.MEDIA_FILTER);

        String[] filters = new String[5];
        filters[0] = scaleFilter;
        filters[1] = brandFilter;
        filters[2] = kitnameFilter;
        filters[3] = statusFilter;
        filters[4] = mediaFilter;


        if (mode == Constants.MODE_KIT) {
            tableName = DbConnector.TABLE_KITS;
//            cursor = dbConnector.filteredKits(DbConnector.TABLE_KITS, filters, sortBy, categoryToReturn); //todo нужно по категориям
        } else if (mode == Constants.MODE_LIST) {
            tableName = DbConnector.TABLE_MYLISTSITEMS;
//            cursor = dbConnector.filteredKits(DbConnector.TABLE_MYLISTSITEMS, filters, sortBy, categoryToReturn); //todo нужно по категориям
        } else if (mode == Constants.MODE_AFTERMARKET) {
            tableName = DbConnector.TABLE_AFTERMARKET;
//            cursor = dbConnector.filteredKits(DbConnector.TABLE_AFTERMARKET, filters, sortBy, categoryToReturn); //todo нужно по категориям
        }
        cursor = dbConnector.filteredKits(tableName, filters, sortBy, categoryToReturn); //todo нужно по категориям


        List<Fragment> fragments = buildFragments();
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerViewKits);
        AdapterViewCards adapterViewCards = new AdapterViewCards(this, getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapterViewCards);
        viewPager.setCurrentItem(position);
    }

    private List<Fragment> buildFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            Long id = ids[i];
            cursorPosition = getIntent().getExtras().getInt(Constants.LIST_POSITION);
            int categoryToReturn = getIntent().getExtras().getInt(Constants.LIST_CATEGORY);
            char mode = getIntent().getExtras().getChar(Constants.EDIT_MODE);
//            String kitname = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME));
//            String brand = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
//            String catno = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO));
//
//            String url = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URL));
//            String uri = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
//            int scale = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE));
//            String category = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));
//            String year = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_YEAR));
//            String description = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_DESCRIPTION));
//            String origname = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ORIGINAL_KIT_NAME));
//
//            String notes = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_NOTES));
//            String media = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_MEDIA));
//            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_QUANTITY));
//            int price = cursor.getInt(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PRICE));
//            String status = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_STATUS));
//            String shop = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_PLACE));
//            String purchaseDate = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_DATE));
////            String scalematesUrl = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.));
//            String currency = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_CURRENCY));


//нужно ли что-то, кроме id?
            Bundle bundle = new Bundle();

            bundle.putLong(Constants.ID, id); //id записи, по которой кликнули в списке


            bundle.putSerializable(Constants.IDS, list);
            bundle.putString("table", tableName); //?
            bundle.putChar(Constants.EDIT_MODE, mode);
            bundle.putString(Constants.SORT_BY, sortBy);
            bundle.putInt(Constants.LIST_CATEGORY, categoryToReturn);
            bundle.putInt(Constants.LIST_POSITION, position);

            bundle.putString(Constants.SCALE_FILTER, scaleFilter);
            bundle.putString(Constants.BRAND_FILTER, brandFilter);
            bundle.putString(Constants.KITNAME_FILTER, kitnameFilter);
            bundle.putString(Constants.STATUS_FILTER, statusFilter);
            bundle.putString(Constants.MEDIA_FILTER, mediaFilter);


            fragments.add(Fragment.instantiate(this, KitCardFragment.class.getName(), bundle));

            cursor.moveToNext();
        }
        return fragments;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_ACTIVITY_CODE) {
            super.onActivityResult(requestCode, resultCode, data);

            List<Fragment> fragments = buildFragments();
            viewPager = (ViewPager) findViewById(R.id.viewpagerViewKits);
            AdapterViewCards adapterViewCards = new AdapterViewCards(this, getSupportFragmentManager(), fragments);
            viewPager.setAdapter(adapterViewCards);
            int position = data.getIntExtra(Constants.CURSOR_POSITION, cursorPosition);
            viewPager.setCurrentItem(position);
        }
        if (resultCode != RESULT_OK) {
        }
    }

    public void refreshPager(int position) {
        List<Fragment> fragments = buildFragments();
        viewPager = (ViewPager) findViewById(R.id.viewpagerViewKits);
        AdapterViewCards adapterViewCards = new AdapterViewCards(this, getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapterViewCards);
//        int position = data.getIntExtra(Constants.CURSOR_POSITION, cursorPosition);
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onBackPressed() {
        if (fragmentWasDeleted) {
            Intent intent = new Intent(ViewActivity.this, MainActivity.class);
            intent.putExtra(Constants.SORT_BY, sortBy);
            intent.putExtra(Constants.EDIT_MODE, mode);
            intent.putExtra(Constants.LIST_CATEGORY, categoryToReturn);
            intent.putExtra(Constants.LIST_POSITION, position);
            intent.putExtra(Constants.SCALE_FILTER, scaleFilter);
            intent.putExtra(Constants.BRAND_FILTER, brandFilter);
            intent.putExtra(Constants.KITNAME_FILTER, kitnameFilter);
            intent.putExtra(Constants.STATUS_FILTER, statusFilter);
            intent.putExtra(Constants.MEDIA_FILTER, mediaFilter);
            intent.putExtra("was_deleted", true);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }
}
