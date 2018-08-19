package com.example.kitstasher.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.ItemEditFragment;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.MyConstants;

/**
 * Edit kit record. Will be deprecated
 */

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Kit kit = getIntent().getParcelableExtra(MyConstants.KIT);
        String category = getIntent().getStringExtra(MyConstants.CATEGORY);
        String workMode = getIntent().getStringExtra(MyConstants.WORK_MODE);

//        int categoryTab = getIntent().getIntExtra(MyConstants.CATEGORY_TAB, 0);
        ItemEditFragment editFragment = new ItemEditFragment();
        Bundle bundleKit = new Bundle();
        bundleKit.putString(MyConstants.CATEGORY, category);
        bundleKit.putParcelable(MyConstants.KIT, kit);
        bundleKit.putString(MyConstants.WORK_MODE, workMode);
        editFragment.setArguments(bundleKit);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutEditContainer, editFragment);
        fragmentTransaction.commit();
    }
}
