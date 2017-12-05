package com.example.kitstasher.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.kitstasher.R;
import com.example.kitstasher.adapters.AdapterViewCards;
import com.example.kitstasher.fragment.ItemCardFragment;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;

import java.util.ArrayList;
import java.util.List;

/*
* Universal Activity for displaying kit and aftermarket pages in pager based on cursor.
 */

public class ViewActivity extends AppCompatActivity {
    private Cursor cursor;
    private long kitId;
    private Long[] ids;
    private Integer[] positions;
    private ArrayList<Long> listIds;
    private ArrayList<Integer> listPositions;
    private DbConnector dbConnector;
    private String[] filters;
    private final int EDIT_ACTIVITY_CODE = 21;
    private int cursorPosition;
    private String tableName;
    private String sortBy;
    private String listname;
    int categoryToReturn;

    private String scaleFilter;
    private String brandFilter;
    private String kitnameFilter;
    private String statusFilter;
    private String mediaFilter;
    private int position;
    private char workMode;
    public ViewPager viewPager;
    private int newPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit);

        dbConnector = new DbConnector(this);
        dbConnector.open();

        listIds = (ArrayList<Long>) getIntent().getSerializableExtra(Constants.IDS);
        listPositions = (ArrayList<Integer>) getIntent().getSerializableExtra("positions");

        workMode = getIntent().getExtras().getChar(Constants.WORK_MODE);
        sortBy = getIntent().getExtras().getString(Constants.SORT_BY);

        position = getIntent().getExtras().getInt(Constants.POSITION);
        categoryToReturn = getIntent().getExtras().getInt(Constants.LIST_CATEGORY);
        listname = getIntent().getStringExtra(Constants.LISTNAME);
        kitId = getIntent().getExtras().getLong(Constants.ID);
        scaleFilter = getIntent().getExtras().getString(Constants.SCALE_FILTER);
        brandFilter = getIntent().getExtras().getString(Constants.BRAND_FILTER);
        kitnameFilter = getIntent().getExtras().getString(Constants.KITNAME_FILTER);
        statusFilter = getIntent().getExtras().getString(Constants.STATUS_FILTER);
        mediaFilter = getIntent().getExtras().getString(Constants.MEDIA_FILTER);

        filters = new String[5];
        filters[0] = scaleFilter;
        filters[1] = brandFilter;
        filters[2] = kitnameFilter;
        filters[3] = statusFilter;
        filters[4] = mediaFilter;

//        chooseCursor();

        ids = new Long[listIds.size()];
        listIds.toArray(ids);
        positions = new Integer[listPositions.size()];
        listPositions.toArray(positions);

        List<Fragment> fragments = buildFragments();
        ViewPager viewPager = findViewById(R.id.viewpagerViewKits);
        AdapterViewCards adapterViewCards = new AdapterViewCards(this, getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapterViewCards);
        viewPager.setCurrentItem(position);
    }

    private List<Fragment> buildFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        chooseCursor();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            Long id = ids[i];
            newPosition = positions[i];

//нужно ли что-то, кроме id?
            Bundle bundle = new Bundle();
            bundle.putLong(Constants.ID, id); //id записи, по которой кликнули в списке
            bundle.putSerializable(Constants.IDS, listIds);
            bundle.putSerializable(Constants.POSITIONS, positions);
            bundle.putString(Constants.TABLE, tableName);
            bundle.putChar(Constants.WORK_MODE, workMode);
            bundle.putString(Constants.SORT_BY, sortBy);
            bundle.putInt(Constants.LIST_CATEGORY, categoryToReturn);
            bundle.putInt(Constants.POSITION, newPosition);
            bundle.putString(Constants.SCALE_FILTER, scaleFilter);
            bundle.putString(Constants.BRAND_FILTER, brandFilter);
            bundle.putString(Constants.KITNAME_FILTER, kitnameFilter);
            bundle.putString(Constants.STATUS_FILTER, statusFilter);
            bundle.putString(Constants.MEDIA_FILTER, mediaFilter);

            fragments.add(Fragment.instantiate(this, ItemCardFragment.class.getName(), bundle));
            cursor.moveToNext();
        }
        return fragments;
    }

    private void chooseCursor() {
        if (workMode == Constants.MODE_KIT) {
            tableName = DbConnector.TABLE_KITS;
            cursor = dbConnector.filteredKits(tableName, filters, sortBy, categoryToReturn, listname); //todo нужно по категориям

        } else if (workMode == Constants.MODE_LIST) {
            tableName = DbConnector.TABLE_MYLISTSITEMS;
            cursor = dbConnector.filteredKits(tableName, filters, sortBy, categoryToReturn, listname); //todo нужно по категориям

        } else if (workMode == Constants.MODE_AFTERMARKET) {
            tableName = DbConnector.TABLE_AFTERMARKET;
            cursor = dbConnector.filteredKits(tableName, filters, sortBy, categoryToReturn, listname); //todo нужно по категориям

        } else if (workMode == Constants.MODE_AFTER_KIT) {
            cursor = dbConnector.getAftermarketForKit(kitId, Constants.EMPTY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_ACTIVITY_CODE) {
            super.onActivityResult(requestCode, resultCode, data);
            position = data.getExtras().getInt(Constants.POSITION);
            List<Fragment> fragments = buildFragments();
            viewPager = findViewById(R.id.viewpagerViewKits);
            AdapterViewCards adapterViewCards = new AdapterViewCards(this, getSupportFragmentManager(), fragments);
            viewPager.setAdapter(adapterViewCards);
            viewPager.setCurrentItem(position);
        }
        if (resultCode != RESULT_OK) {
        }
    }

    @Override
    public void onBackPressed() {
        //1. Возврат в майн активити во всех случаях, кроме MODE_LIST
        if (workMode != Constants.MODE_LIST) {
            Intent intent = new Intent(ViewActivity.this, MainActivity.class);
            intent.putExtra(Constants.SORT_BY, sortBy);
            intent.putExtra(Constants.WORK_MODE, workMode);
            intent.putExtra(Constants.LIST_CATEGORY, categoryToReturn);
            intent.putExtra(Constants.LIST_POSITION, position);
            intent.putExtra(Constants.SCALE_FILTER, scaleFilter);
            intent.putExtra(Constants.BRAND_FILTER, brandFilter);
            intent.putExtra(Constants.KITNAME_FILTER, kitnameFilter);
            intent.putExtra(Constants.STATUS_FILTER, statusFilter);
            intent.putExtra(Constants.MEDIA_FILTER, mediaFilter);
            intent.putExtra("was_deleted", true);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            super.onBackPressed(); //todo обработать LIST
        }
    }
}
