package com.kutovenko.kitstasher.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.kutovenko.kitstasher.ui.adapter.FragmentViewCardsAdapter;
import com.kutovenko.kitstasher.ui.fragment.ItemCardFragment;
import com.kutovenko.kitstasher.model.StashItem;
import com.kutovenko.kitstasher.util.MyConstants;

import java.util.ArrayList;
import java.util.List;

/*
 * Will be deprecated
 *  Universal Activity for displaying kit and aftermarket pages in pager based on ArrayList.
 *
 * Класс для демонстрации карточек наборов и афтермаркета. Использует Pager, данные получает в виде
 * ArrayList<StashItem>.
 */

public class ViewActivity extends AppCompatActivity {
    public ViewPager viewPager;
    private long kitId;
    private ArrayList<StashItem> itemList;
    private String category,
            workMode;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.kutovenko.kitstasher.R.layout.activity_kit);

        category = getIntent().getStringExtra(MyConstants.CATEGORY);
        itemList = getIntent().getParcelableArrayListExtra(MyConstants.LIST);
        workMode = getIntent().getStringExtra(MyConstants.ITEM_TYPE);
        position = getIntent().getIntExtra(MyConstants.POSITION, 0);
        kitId = getIntent().getLongExtra(MyConstants.ID, 0);
        List<Fragment> fragments = buildFragments();
        viewPager = findViewById(com.kutovenko.kitstasher.R.id.viewpagerViewKits);
        FragmentViewCardsAdapter fragmentViewCardsAdapter = new FragmentViewCardsAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(fragmentViewCardsAdapter);
        viewPager.setCurrentItem(position);
    }

    private List<Fragment> buildFragments() {
        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putLong(MyConstants.ID, kitId); //id записи, по которой кликнули в списке
            bundle.putInt(MyConstants.POSITION, position);
            bundle.putString(MyConstants.CATEGORY, category);
            bundle.putString(MyConstants.ITEM_TYPE, workMode);
            bundle.putParcelable(MyConstants.KIT, itemList.get(i));
            fragments.add(Fragment.instantiate(this, ItemCardFragment.class.getName(), bundle));
        }
        return fragments;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO: 15.02.2019  navigation
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ItemCardFragment.EDIT_ACTIVITY_CODE) {
            position = data.getIntExtra(MyConstants.POSITION, 0);
            category = data.getStringExtra(MyConstants.CATEGORY);
            workMode = data.getStringExtra(MyConstants.ITEM_TYPE);
            StashItem editedStashItem = data.getParcelableExtra(MyConstants.KIT);
            itemList.set(position, editedStashItem);
            List<Fragment> fragments = buildFragments();
            FragmentViewCardsAdapter fragmentViewCardsAdapter
                    = new FragmentViewCardsAdapter(getSupportFragmentManager(), fragments);
            viewPager.setAdapter(fragmentViewCardsAdapter);
            viewPager.setCurrentItem(position);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewActivity.this, MainActivity.class);
        intent.putExtra(MyConstants.ITEM_TYPE, workMode);
        intent.putExtra(MyConstants.CATEGORY, category);
        intent.putExtra(MyConstants.POSITION, viewPager.getCurrentItem());
        intent.putExtra(MyConstants.ITEM_TYPE, workMode);
        setResult(RESULT_OK, intent);
        finish();
    }
}