package com.example.kitstasher.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.kitstasher.R;
import com.example.kitstasher.adapters.AdapterViewCards;
import com.example.kitstasher.fragment.ItemCardFragment;
import com.example.kitstasher.fragment.KitsFragment;
import com.example.kitstasher.objects.CustomKitsViewPager;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;

import java.util.ArrayList;
import java.util.List;

/*
* Universal Activity for displaying kit and aftermarket pages in pager based on cursor.
 */

public class ViewActivity extends AppCompatActivity {
    public static CustomKitsViewPager viewPager;
    private Cursor cursor;
    private DbConnector dbConnector;
    private final int EDIT_ACTIVITY_CODE = 21;
    private long kitId;
    //    private Long[] ids;
    private ArrayList<Integer> positions;
    private ArrayList<Long> listIds;
    private String[] filters;
    private String tableName,
            sortBy,
            listname,
            scaleFilter,
            brandFilter,
            kitnameFilter,
            statusFilter,
            mediaFilter,
            category;
    private int categoryTab,
            position;
    private char workMode;
    private boolean aftermarketMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit);

        dbConnector = new DbConnector(this);
        dbConnector.open();

        category = getIntent().getStringExtra(MyConstants.CATEGORY);
        categoryTab = getIntent().getIntExtra(MyConstants.CATEGORY_TAB, 0);

        listIds = (ArrayList<Long>) getIntent().getSerializableExtra(MyConstants.IDS);
        positions = (ArrayList<Integer>) getIntent().getSerializableExtra(MyConstants.POSITIONS);

        workMode = getIntent().getCharExtra(MyConstants.WORK_MODE, MyConstants.MODE_KIT);
        sortBy = getIntent().getStringExtra(MyConstants.SORT_BY);

        position = getIntent().getIntExtra(MyConstants.POSITION, 0);


        listname = getIntent().getStringExtra(MyConstants.LISTNAME);
        kitId = getIntent().getLongExtra(MyConstants.ID, 0);
        scaleFilter = getIntent().getExtras().getString(MyConstants.SCALE_FILTER);
        brandFilter = getIntent().getExtras().getString(MyConstants.BRAND_FILTER);
        kitnameFilter = getIntent().getExtras().getString(MyConstants.KITNAME_FILTER);
        statusFilter = getIntent().getExtras().getString(MyConstants.STATUS_FILTER);
        mediaFilter = getIntent().getExtras().getString(MyConstants.MEDIA_FILTER);

        filters = new String[5];
        filters[0] = scaleFilter;
        filters[1] = brandFilter;
        filters[2] = kitnameFilter;
        filters[3] = statusFilter;
        filters[4] = mediaFilter;

        List<Fragment> fragments = buildFragments();
        viewPager = findViewById(R.id.viewpagerViewKits);
        AdapterViewCards adapterViewCards = new AdapterViewCards(this, getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapterViewCards);
        viewPager.setCurrentItem(position);
    }

    private List<Fragment> buildFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        chooseCursor();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            Long id = listIds.get(i);
            int newPosition = positions.get(i);

            Bundle bundle = new Bundle();
            bundle.putLong(MyConstants.ID, id); //id записи, по которой кликнули в списке
            bundle.putSerializable(MyConstants.IDS, listIds);
            bundle.putSerializable(MyConstants.POSITIONS, positions);
            bundle.putString(MyConstants.TABLE, tableName);
            bundle.putChar(MyConstants.WORK_MODE, workMode);
            bundle.putString(MyConstants.SORT_BY, sortBy);
            bundle.putInt(MyConstants.CATEGORY_TAB, categoryTab);
            bundle.putInt(MyConstants.POSITION, newPosition);
            bundle.putString(MyConstants.CATEGORY, category);
            bundle.putString(MyConstants.SCALE_FILTER, scaleFilter);
            bundle.putString(MyConstants.BRAND_FILTER, brandFilter);
            bundle.putString(MyConstants.KITNAME_FILTER, kitnameFilter);
            bundle.putString(MyConstants.STATUS_FILTER, statusFilter);
            bundle.putString(MyConstants.MEDIA_FILTER, mediaFilter);

            fragments.add(Fragment.instantiate(this, ItemCardFragment.class.getName(), bundle));
            cursor.moveToNext();
        }
        return fragments;
    }

    private void chooseCursor() {
        if (workMode == MyConstants.MODE_KIT) {
            tableName = DbConnector.TABLE_KITS;
            cursor = dbConnector.filteredKits(tableName, filters, sortBy, category, listname);
            if (cursor.getCount() == 0) {
                cursor = dbConnector.filteredKits(tableName, filters, sortBy, "", listname);
            }
            rebuildIdsAndPositions(cursor);
            aftermarketMode = false;

        } else if (workMode == MyConstants.MODE_LIST) {
            tableName = DbConnector.TABLE_MYLISTSITEMS;
            cursor = dbConnector.filteredKits(tableName, filters, sortBy, category, listname);
            aftermarketMode = false;

        } else if (workMode == MyConstants.MODE_AFTERMARKET) {
            tableName = DbConnector.TABLE_AFTERMARKET;
            cursor = dbConnector.filteredKits(tableName, filters, sortBy, category, listname);
            if (cursor.getCount() == 0) {
                cursor = dbConnector.filteredKits(tableName, filters, sortBy, "", listname);
            }
            rebuildIdsAndPositions(cursor);
            aftermarketMode = true;

        } else if (workMode == MyConstants.MODE_AFTER_KIT) {
            cursor = dbConnector.getAftermarketForKit(kitId, MyConstants.EMPTY);
        }
        aftermarketMode = false; //todo ????????
    }

    private void rebuildIdsAndPositions(Cursor cursor) {
        int count = cursor.getCount();
        listIds = new ArrayList<>(count);
        positions = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            listIds.add(i, cursor.getLong(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_ID)));
            positions.add(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_ACTIVITY_CODE) {
            super.onActivityResult(requestCode, resultCode, data);

            category = data.getStringExtra(MyConstants.CATEGORY);
            chooseCursor();
            position = positions.get(listIds.indexOf(data.getLongExtra(MyConstants.ID, 0))); //work
            List<Fragment> fragments = buildFragments();
            AdapterViewCards adapterViewCards = new AdapterViewCards(this, getSupportFragmentManager(), fragments);
            viewPager.setAdapter(adapterViewCards);
            viewPager.setCurrentItem(position);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();


        //1. Возврат в майн активити во всех случаях, кроме MODE_LIST
        //нужно узнать категориттаб для текущей категори
//        Cursor catCursor;
//        if (aftermarketMode) {
//            catCursor = dbConnector.getAfterActiveCategories();
//        } else {
//            catCursor = dbConnector.getActiveCategories();
//        }
//        catCursor.moveToFirst();
//        while (!catCursor.isAfterLast()) {
//            if (catCursor.getString(catCursor.getColumnIndexOrThrow("category")).equals(category)) {
//                categoryTab = catCursor.getPosition();
//            }
//            catCursor.moveToNext();
//        }

//        if (workMode == MyConstants.MODE_KIT) {
            Intent intent = new Intent(ViewActivity.this, MainActivity.class);
            intent.putExtra(MyConstants.SORT_BY, sortBy);
            intent.putExtra(MyConstants.WORK_MODE, workMode);
        intent.putExtra(MyConstants.CATEGORY_TAB, categoryTab);
            intent.putExtra(MyConstants.LIST_POSITION, position);
            intent.putExtra(MyConstants.CATEGORY, category);
            intent.putExtra(MyConstants.SCALE_FILTER, scaleFilter);
            intent.putExtra(MyConstants.BRAND_FILTER, brandFilter);
            intent.putExtra(MyConstants.KITNAME_FILTER, kitnameFilter);
            intent.putExtra(MyConstants.STATUS_FILTER, statusFilter);
            intent.putExtra(MyConstants.MEDIA_FILTER, mediaFilter);
            intent.putExtra("was_deleted", true);
            setResult(RESULT_OK, intent);

            KitsFragment.refreshPages();

            finish();
//        } else if (workMode == MyConstants.MODE_LIST) {
//            super.onBackPressed(); //todo обработать LIST
//        } else if (workMode == MyConstants.MODE_AFTER_KIT) {
//            super.onBackPressed();
//        }
    }

    public static void refreshPages() {
        viewPager.refresh();
    }
}
