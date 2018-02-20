package com.example.kitstasher.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.ItemEditFragment;
import com.example.kitstasher.other.MyConstants;

public class EditActivity extends AppCompatActivity {
//    private Long[] ids;

    private final int REQUEST_AFTER_KIT = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

//        ArrayList<Long> aftermarketList = (ArrayList<Long>) getIntent().getSerializableExtra(MyConstants.IDS);
//
//        Long[] aftermarketIds = new Long[aftermarketList.size()];
//        aftermarketList.toArray(aftermarketIds);

        String sortBy = getIntent().getExtras().getString(MyConstants.SORT_BY);

        int position = getIntent().getExtras().getInt(MyConstants.POSITION);
        long id = getIntent().getExtras().getLong(MyConstants.ID);
        char workMode = getIntent().getExtras().getChar(MyConstants.WORK_MODE);
        Long afterId = 0L;
        if (workMode == MyConstants.MODE_AFTERMARKET) {
            afterId = getIntent().getExtras().getLong(MyConstants.AFTER_ID);
        }
        String category = getIntent().getExtras().getString(MyConstants.LIST_CATEGORY);
        String kitname = getIntent().getExtras().getString(MyConstants.KITNAME);
        String brand = getIntent().getExtras().getString(MyConstants.BRAND);
        String catno = getIntent().getExtras().getString(MyConstants.CATNO);
        String url = getIntent().getExtras().getString(MyConstants.URL);
        String uri = getIntent().getExtras().getString(MyConstants.URI);
        int scale = getIntent().getExtras().getInt(MyConstants.SCALE);
        String year = getIntent().getExtras().getString(MyConstants.YEAR);
        String description = getIntent().getExtras().getString(MyConstants.DESCRIPTION);
        String origName = getIntent().getExtras().getString(MyConstants.ORIGINAL_NAME);
        String notes = getIntent().getExtras().getString(MyConstants.NOTES);
        String media = getIntent().getExtras().getString(MyConstants.MEDIA);
        int quantity = getIntent().getExtras().getInt(MyConstants.QUANTITY);
        String status = getIntent().getExtras().getString(MyConstants.STATUS);

        String scaleFilter = getIntent().getExtras().getString(MyConstants.SCALE_FILTER);
        String brandFilter = getIntent().getExtras().getString(MyConstants.BRAND_FILTER);
        String kitnameFilter = getIntent().getExtras().getString(MyConstants.KITNAME_FILTER);

        String statusFilter = getIntent().getExtras().getString(MyConstants.STATUS_FILTER);
        String mediaFilter = getIntent().getExtras().getString(MyConstants.MEDIA_FILTER);

        String[] filters = new String[5];
        filters[0] = scaleFilter;
        filters[1] = brandFilter;
        filters[2] = kitnameFilter;
        filters[3] = statusFilter;
        filters[4] = mediaFilter;

        ItemEditFragment editFragment = new ItemEditFragment();
        Bundle bundleKit = new Bundle();
        bundleKit.putInt(MyConstants.POSITION, position);
        bundleKit.putChar(MyConstants.WORK_MODE, workMode);
        bundleKit.putLong(MyConstants.ID, id); //id записи, по которой кликнули в списке
        bundleKit.putString(MyConstants.KITNAME, kitname);
        bundleKit.putString(MyConstants.BRAND, brand);
        bundleKit.putString(MyConstants.CATNO, catno);
        bundleKit.putString(MyConstants.URL, url);
        bundleKit.putString(MyConstants.URI, uri);
        bundleKit.putInt(MyConstants.SCALE, scale);
        bundleKit.putString(MyConstants.CATEGORY, category);
        bundleKit.putString(MyConstants.YEAR, year);
        bundleKit.putString(MyConstants.DESCRIPTION, description);
        bundleKit.putString(MyConstants.ORIGINAL_NAME, origName);
        bundleKit.putString(MyConstants.NOTES, notes);
        bundleKit.putString(MyConstants.MEDIA, media);
        bundleKit.putInt(MyConstants.QUANTITY, quantity);
        bundleKit.putString(MyConstants.STATUS, status);
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
//                bundleKit.putInt(MyConstants.POSITION, position);
//                bundleKit.putChar(MyConstants.WORK_MODE, workMode);
//                bundleKit.putLong(MyConstants.ID, id); //id записи, по которой кликнули в списке
//                bundleKit.putString(MyConstants.KITNAME, kitname);
//                bundleKit.putString(MyConstants.BRAND, brand);
//                bundleKit.putString(MyConstants.CATNO, catno);
//                bundleKit.putString(MyConstants.URL, url);
//                bundleKit.putString(MyConstants.URI, uri);
//                bundleKit.putInt(MyConstants.SCALE, scale);
//                bundleKit.putString(MyConstants.CATEGORY, category);
//                bundleKit.putString(MyConstants.YEAR, year);
//                bundleKit.putString(MyConstants.DESCRIPTION, description);
//                bundleKit.putString(MyConstants.ORIGINAL_NAME, origName);
//                bundleKit.putString(MyConstants.NOTES, notes);
//                bundleKit.putString(MyConstants.MEDIA, media);
//                bundleKit.putInt(MyConstants.QUANTITY, quantity);
//                bundleKit.putString(MyConstants.STATUS, status);
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
//                bundleList.putInt(MyConstants.POSITION, position);
//                bundleList.putChar(MyConstants.WORK_MODE, workMode);
//                bundleList.putLong(MyConstants.ID, id); //id записи, по которой кликнули в списке
//                bundleList.putString(MyConstants.KITNAME, kitname);
//                bundleList.putString(MyConstants.BRAND, brand);
//                bundleList.putString(MyConstants.CATNO, catno);
//                bundleList.putString(MyConstants.URL, url);
//                bundleList.putString(MyConstants.URI, uri);
//                bundleList.putInt(MyConstants.SCALE, scale);
//                bundleList.putString(MyConstants.CATEGORY, category);
//                bundleList.putString(MyConstants.YEAR, year);
//                bundleList.putString(MyConstants.DESCRIPTION, description);
//                bundleList.putString(MyConstants.ORIGINAL_NAME, origName);
//                bundleList.putString(MyConstants.NOTES, notes);
//                bundleList.putString(MyConstants.MEDIA, media);
//                bundleList.putInt(MyConstants.QUANTITY, quantity);
//                bundleList.putString(MyConstants.STATUS, status);
////                bundleList.putString(MyConstants.LISTNAME, listname);
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
//                bundle.putChar(MyConstants.WORK_MODE, workMode);
//                bundle.putInt(MyConstants.POSITION, position);
//                bundle.putLong(MyConstants.KIT_ID, id);
//                bundle.putLong(MyConstants.ID, id);
//                bundle.putLong(MyConstants.AFTER_ID, afterId);
//                bundle.putString(MyConstants.LIST_CATEGORY, category);
//                bundle.putString(MyConstants.SCALE_FILTER, scaleFilter);
//                bundle.putString(MyConstants.BRAND_FILTER, brandFilter);
//                bundle.putString(MyConstants.KITNAME_FILTER, kitnameFilter);
//                bundle.putString(MyConstants.STATUS_FILTER, statusFilter);
//                bundle.putString(MyConstants.MEDIA_FILTER, mediaFilter);
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
