package com.example.kitstasher.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.ItemEditFragment;
import com.example.kitstasher.other.MyConstants;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        int position = getIntent().getExtras().getInt(MyConstants.POSITION);
        long id = getIntent().getExtras().getLong(MyConstants.ID);
        char workMode = getIntent().getExtras().getChar(MyConstants.WORK_MODE);
        String category = getIntent().getExtras().getString(MyConstants.CATEGORY);
        int categoryTab = getIntent().getExtras().getInt(MyConstants.CATEGORY_TAB);
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
        bundleKit.putInt(MyConstants.CATEGORY_TAB, categoryTab);
        bundleKit.putString(MyConstants.YEAR, year);
        bundleKit.putString(MyConstants.DESCRIPTION, description);
        bundleKit.putString(MyConstants.ORIGINAL_NAME, origName);
        bundleKit.putString(MyConstants.NOTES, notes);
        bundleKit.putString(MyConstants.MEDIA, media);
        bundleKit.putInt(MyConstants.QUANTITY, quantity);
        bundleKit.putString(MyConstants.STATUS, status);

        bundleKit.putString(MyConstants.SCALE_FILTER, scaleFilter);
        bundleKit.putString(MyConstants.BRAND_FILTER, brandFilter);
        bundleKit.putString(MyConstants.KITNAME_FILTER, kitnameFilter);
        bundleKit.putString(MyConstants.STATUS_FILTER, statusFilter);
        bundleKit.putString(MyConstants.MEDIA_FILTER, mediaFilter);

        editFragment.setArguments(bundleKit);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutEditContainer, editFragment);
        fragmentTransaction.commit();
    }
}
