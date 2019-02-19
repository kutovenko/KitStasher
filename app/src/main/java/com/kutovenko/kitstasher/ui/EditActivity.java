package com.kutovenko.kitstasher.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.kutovenko.kitstasher.ui.fragment.ItemEditFragment;
import com.kutovenko.kitstasher.model.StashItem;
import com.kutovenko.kitstasher.util.MyConstants;

import androidx.fragment.app.FragmentTransaction;

/**
 * Edit kit record. Will be deprecated
 */

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.kutovenko.kitstasher.R.layout.activity_edit);

        StashItem stashItem = getIntent().getParcelableExtra(MyConstants.KIT);
        String category = getIntent().getStringExtra(MyConstants.CATEGORY);
        String workMode = getIntent().getStringExtra(MyConstants.ITEM_TYPE);

        ItemEditFragment editFragment = new ItemEditFragment();
        Bundle bundleKit = new Bundle();
        bundleKit.putString(MyConstants.CATEGORY, category);
        bundleKit.putParcelable(MyConstants.KIT, stashItem);
        bundleKit.putString(MyConstants.ITEM_TYPE, workMode);
        editFragment.setArguments(bundleKit);
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.frameLayoutEditContainer, editFragment);
        fragmentTransaction.commit();
    }
}
