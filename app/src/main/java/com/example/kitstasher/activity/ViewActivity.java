package com.example.kitstasher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.kitstasher.R;
import com.example.kitstasher.adapters.FragmentViewCardsAdapter;
import com.example.kitstasher.fragment.ItemCardFragment;
import com.example.kitstasher.objects.CustomKitsViewPager;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.MyConstants;

import java.util.ArrayList;
import java.util.List;

/*
 * Universal Activity for displaying kit and aftermarket pages in pager based on ArrayList.
 */

public class ViewActivity extends AppCompatActivity {
    public static CustomKitsViewPager viewPager;
    private final int EDIT_ACTIVITY_CODE = 21;
    private long kitId;
    private ArrayList<Kit> itemList;
    private String
//            sortBy,
            category;
    private int categoryTab,
            position;
    private char workMode;
    private List<Fragment> fragments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit);

        category = getIntent().getStringExtra(MyConstants.CATEGORY);
        categoryTab = getIntent().getIntExtra(MyConstants.CATEGORY_TAB, 0);
        itemList = getIntent().getParcelableArrayListExtra(MyConstants.LIST);
        workMode = getIntent().getCharExtra(MyConstants.WORK_MODE, MyConstants.MODE_KIT);
//        sortBy = getIntent().getStringExtra(MyConstants.SORT_BY);
        position = getIntent().getIntExtra(MyConstants.POSITION, 0);
        kitId = getIntent().getLongExtra(MyConstants.ID, 0);
        fragments = buildFragments();
        viewPager = findViewById(R.id.viewpagerViewKits);
        FragmentViewCardsAdapter fragmentViewCardsAdapter = new FragmentViewCardsAdapter(this, getSupportFragmentManager(), fragments);
        viewPager.setAdapter(fragmentViewCardsAdapter);
        viewPager.setCurrentItem(position);
    }

    private List<Fragment> buildFragments() {
        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putLong(MyConstants.ID, kitId); //id записи, по которой кликнули в списке
            bundle.putInt(MyConstants.CATEGORY_TAB, categoryTab);
            bundle.putInt(MyConstants.POSITION, position);
            bundle.putString(MyConstants.CATEGORY, category);
            bundle.putParcelable("kit", itemList.get(i));
            fragments.add(Fragment.instantiate(this, ItemCardFragment.class.getName(), bundle));
        }
        return fragments;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_ACTIVITY_CODE) {
            super.onActivityResult(requestCode, resultCode, data);
            category = data.getStringExtra(MyConstants.CATEGORY);
            Kit editedKit = data.getParcelableExtra("kit");
            itemList.set(data.getIntExtra("position", 0), editedKit);
            List<Fragment> fragments = buildFragments();
            FragmentViewCardsAdapter fragmentViewCardsAdapter = new FragmentViewCardsAdapter(this, getSupportFragmentManager(), fragments);
            viewPager.setAdapter(fragmentViewCardsAdapter);
            viewPager.setCurrentItem(position);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewActivity.this, MainActivity.class);
//        intent.putExtra(MyConstants.SORT_BY, sortBy);
        intent.putExtra(MyConstants.WORK_MODE, workMode);
        intent.putExtra(MyConstants.CATEGORY_TAB, categoryTab);
        intent.putExtra(MyConstants.LIST_POSITION, position);
        setResult(RESULT_OK, intent);
//        KitsFragment.refreshPages();
        finish();
    }

    public static void refreshPages() {
        viewPager.refresh();
    }
}
