package com.example.kitstasher.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.ItemEditFragment;
import com.example.kitstasher.other.Constants;

public class EditActivity extends AppCompatActivity {
//    private Long[] ids;

    private final int REQUEST_AFTER_KIT = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

//        ArrayList<Long> aftermarketList = (ArrayList<Long>) getIntent().getSerializableExtra(Constants.IDS);
//
//        Long[] aftermarketIds = new Long[aftermarketList.size()];
//        aftermarketList.toArray(aftermarketIds);

        String sortBy = getIntent().getExtras().getString(Constants.SORT_BY);

        int position = getIntent().getExtras().getInt(Constants.POSITION);
        long id = getIntent().getExtras().getLong(Constants.ID);
        char workMode = getIntent().getExtras().getChar(Constants.WORK_MODE);
        Long afterId = 0L;
        if (workMode == Constants.MODE_AFTERMARKET) {
            afterId = getIntent().getExtras().getLong(Constants.AFTER_ID);
        }
        String category = getIntent().getExtras().getString(Constants.LIST_CATEGORY);
        String kitname = getIntent().getExtras().getString(Constants.KITNAME);
        String brand = getIntent().getExtras().getString(Constants.BRAND);
        String catno = getIntent().getExtras().getString(Constants.CATNO);
        String url = getIntent().getExtras().getString(Constants.URL);
        String uri = getIntent().getExtras().getString(Constants.URI);
        int scale = getIntent().getExtras().getInt(Constants.SCALE);
        String year = getIntent().getExtras().getString(Constants.YEAR);
        String description = getIntent().getExtras().getString(Constants.DESCRIPTION);
        String origName = getIntent().getExtras().getString(Constants.ORIGINAL_NAME);
        String notes = getIntent().getExtras().getString(Constants.NOTES);
        String media = getIntent().getExtras().getString(Constants.MEDIA);
        int quantity = getIntent().getExtras().getInt(Constants.QUANTITY);
        String status = getIntent().getExtras().getString(Constants.STATUS);

        String scaleFilter = getIntent().getExtras().getString(Constants.SCALE_FILTER);
        String brandFilter = getIntent().getExtras().getString(Constants.BRAND_FILTER);
        String kitnameFilter = getIntent().getExtras().getString(Constants.KITNAME_FILTER);

        String statusFilter = getIntent().getExtras().getString(Constants.STATUS_FILTER);
        String mediaFilter = getIntent().getExtras().getString(Constants.MEDIA_FILTER);

        String[] filters = new String[5];
        filters[0] = scaleFilter;
        filters[1] = brandFilter;
        filters[2] = kitnameFilter;
        filters[3] = statusFilter;
        filters[4] = mediaFilter;

        ItemEditFragment editFragment = new ItemEditFragment();
        Bundle bundleKit = new Bundle();
        bundleKit.putInt(Constants.POSITION, position);
        bundleKit.putChar(Constants.WORK_MODE, workMode);
        bundleKit.putLong(Constants.ID, id); //id записи, по которой кликнули в списке
        bundleKit.putString(Constants.KITNAME, kitname);
        bundleKit.putString(Constants.BRAND, brand);
        bundleKit.putString(Constants.CATNO, catno);
        bundleKit.putString(Constants.URL, url);
        bundleKit.putString(Constants.URI, uri);
        bundleKit.putInt(Constants.SCALE, scale);
        bundleKit.putString(Constants.CATEGORY, category);
        bundleKit.putString(Constants.YEAR, year);
        bundleKit.putString(Constants.DESCRIPTION, description);
        bundleKit.putString(Constants.ORIGINAL_NAME, origName);
        bundleKit.putString(Constants.NOTES, notes);
        bundleKit.putString(Constants.MEDIA, media);
        bundleKit.putInt(Constants.QUANTITY, quantity);
        bundleKit.putString(Constants.STATUS, status);
        editFragment.setArguments(bundleKit);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutEditContainer, editFragment);
        fragmentTransaction.commit();

//
//        switch (workMode) {
//            case 'm': //MODE_KIT
//                ItemEditFragment editFragment = new ItemEditFragment();
//                Bundle bundleKit = new Bundle();
//                bundleKit.putInt(Constants.POSITION, position);
//                bundleKit.putChar(Constants.WORK_MODE, workMode);
//                bundleKit.putLong(Constants.ID, id); //id записи, по которой кликнули в списке
//                bundleKit.putString(Constants.KITNAME, kitname);
//                bundleKit.putString(Constants.BRAND, brand);
//                bundleKit.putString(Constants.CATNO, catno);
//                bundleKit.putString(Constants.URL, url);
//                bundleKit.putString(Constants.URI, uri);
//                bundleKit.putInt(Constants.SCALE, scale);
//                bundleKit.putString(Constants.CATEGORY, category);
//                bundleKit.putString(Constants.YEAR, year);
//                bundleKit.putString(Constants.DESCRIPTION, description);
//                bundleKit.putString(Constants.ORIGINAL_NAME, origName);
//                bundleKit.putString(Constants.NOTES, notes);
//                bundleKit.putString(Constants.MEDIA, media);
//                bundleKit.putInt(Constants.QUANTITY, quantity);
//                bundleKit.putString(Constants.STATUS, status);
//                editFragment.setArguments(bundleKit);
//                android.support.v4.app.FragmentTransaction fragmentTransaction =
//                        getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.frameLayoutEditContainer, editFragment);
//                fragmentTransaction.commit();
//                break;
//
//            case 'l': //MODE_LIST
//                ItemEditFragment listFragment = new ItemEditFragment();
//                Bundle bundleList = new Bundle();
//                bundleList.putInt(Constants.POSITION, position);
//                bundleList.putChar(Constants.WORK_MODE, workMode);
//                bundleList.putLong(Constants.ID, id); //id записи, по которой кликнули в списке
//                bundleList.putString(Constants.KITNAME, kitname);
//                bundleList.putString(Constants.BRAND, brand);
//                bundleList.putString(Constants.CATNO, catno);
//                bundleList.putString(Constants.URL, url);
//                bundleList.putString(Constants.URI, uri);
//                bundleList.putInt(Constants.SCALE, scale);
//                bundleList.putString(Constants.CATEGORY, category);
//                bundleList.putString(Constants.YEAR, year);
//                bundleList.putString(Constants.DESCRIPTION, description);
//                bundleList.putString(Constants.ORIGINAL_NAME, origName);
//                bundleList.putString(Constants.NOTES, notes);
//                bundleList.putString(Constants.MEDIA, media);
//                bundleList.putInt(Constants.QUANTITY, quantity);
//                bundleList.putString(Constants.STATUS, status);
////                bundleList.putString(Constants.LISTNAME, listname);
//                listFragment.setArguments(bundleList);
//                android.support.v4.app.FragmentTransaction fragmentTransactionList =
//                        getSupportFragmentManager().beginTransaction();
//                fragmentTransactionList.replace(R.id.frameLayoutEditContainer, listFragment);
//                fragmentTransactionList.commit();
//                break;
//
//            case 'a': //MODE_AFTERMARKET
//                ItemEditFragment itemEditFragment = new ItemEditFragment();
//                //Loading fragment with kit list
//                Bundle bundle = new Bundle();
//                bundle.putChar(Constants.WORK_MODE, workMode);
//                bundle.putInt(Constants.POSITION, position);
//                bundle.putLong(Constants.KIT_ID, id);
//                bundle.putLong(Constants.ID, id);
//                bundle.putLong(Constants.AFTER_ID, afterId);
//                bundle.putString(Constants.LIST_CATEGORY, category);
//                bundle.putString(Constants.SCALE_FILTER, scaleFilter);
//                bundle.putString(Constants.BRAND_FILTER, brandFilter);
//                bundle.putString(Constants.KITNAME_FILTER, kitnameFilter);
//                bundle.putString(Constants.STATUS_FILTER, statusFilter);
//                bundle.putString(Constants.MEDIA_FILTER, mediaFilter);
//
//                itemEditFragment.setArguments(bundle);
//                android.support.v4.app.FragmentTransaction fragmentTransactionAfter =
//                        getSupportFragmentManager().beginTransaction();
//                fragmentTransactionAfter.replace(R.id.frameLayoutEditContainer, itemEditFragment);
//                fragmentTransactionAfter.commit();
//                break;
//
//            case 'k': //MODE_AFTER_KIT
//                break;
//        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && requestCode == REQUEST_AFTER_KIT) {
//
//        }
//    }

}
