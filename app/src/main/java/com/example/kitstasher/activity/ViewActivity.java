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

import static com.example.kitstasher.fragment.ItemCardFragment.EDIT_ACTIVITY_CODE;

/*
 * Will be deprecated
 *  Universal Activity for displaying kit and aftermarket pages in pager based on ArrayList.
 *
 * Класс для демонстрации карточек наборов и афтермаркета. Использует Pager, данные получает в виде
 * ArrayList<Kit>.
 */

public class ViewActivity extends AppCompatActivity {
    public static CustomKitsViewPager viewPager;
    private long kitId;
    private ArrayList<Kit> itemList;
    private String
            category;
    private int categoryTab,
            position;
    private String workMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit);

        category = getIntent().getStringExtra(MyConstants.CATEGORY);
//        categoryTab = getIntent().getIntExtra(MyConstants.CATEGORY_TAB, 0);
        itemList = getIntent().getParcelableArrayListExtra(MyConstants.LIST);
        workMode = getIntent().getStringExtra(MyConstants.WORK_MODE);
        position = getIntent().getIntExtra(MyConstants.POSITION, 0);
        kitId = getIntent().getLongExtra(MyConstants.ID, 0);
        List<Fragment> fragments = buildFragments();
        viewPager = findViewById(R.id.viewpagerViewKits);
        FragmentViewCardsAdapter fragmentViewCardsAdapter = new FragmentViewCardsAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(fragmentViewCardsAdapter);
        viewPager.setCurrentItem(position);
    }

    private List<Fragment> buildFragments() {
        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putLong(MyConstants.ID, kitId); //id записи, по которой кликнули в списке
//            bundle.putInt(MyConstants.CATEGORY_TAB, categoryTab);
            bundle.putInt(MyConstants.POSITION, position);
            bundle.putString(MyConstants.CATEGORY, category);
            bundle.putString(MyConstants.WORK_MODE, workMode);
            bundle.putParcelable(MyConstants.KIT, itemList.get(i));
            fragments.add(Fragment.instantiate(this, ItemCardFragment.class.getName(), bundle));
        }
        return fragments;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        int EDIT_ACTIVITY_CODE = 21;
        if (resultCode == RESULT_OK && requestCode == EDIT_ACTIVITY_CODE) {
position = data.getIntExtra(MyConstants.POSITION, 0);
            category = data.getStringExtra(MyConstants.CATEGORY);
            workMode = data.getStringExtra(MyConstants.WORK_MODE);
            Kit editedKit = data.getParcelableExtra(MyConstants.KIT);
            itemList.set(position, editedKit);
            List<Fragment> fragments = buildFragments();
            FragmentViewCardsAdapter fragmentViewCardsAdapter
                    = new FragmentViewCardsAdapter(getSupportFragmentManager(), fragments);
            viewPager.setAdapter(fragmentViewCardsAdapter);
//            viewPager.refresh();
            viewPager.setCurrentItem(position);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewActivity.this, MainActivity.class);
        intent.putExtra(MyConstants.WORK_MODE, workMode);
        intent.putExtra(MyConstants.CATEGORY, category);
        intent.putExtra(MyConstants.LIST_POSITION, position);
        intent.putExtra(MyConstants.WORK_MODE, workMode);
        setResult(RESULT_OK, intent);
        finish();
    }
}
