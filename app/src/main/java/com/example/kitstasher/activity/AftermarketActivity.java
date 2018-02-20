package com.example.kitstasher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.AftermarketCardFragment;
import com.example.kitstasher.other.MyConstants;

public class AftermarketActivity extends AppCompatActivity {
    private char mode;
    int position;
    long kitId;
    long afterId;
    int categoryToReturn;
    String scaleFilter;
    String brandFilter;
    String kitnameFilter;
    String sortBy;

    String statusFilter;
    String mediaFilter;
    public static int counter;
    Bundle bundle;
    private LinearLayout linLayoutAftermarketContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aftermarket);
        counter = 1;
        position = getIntent().getExtras().getInt(MyConstants.LIST_POSITION);
        kitId = getIntent().getExtras().getLong(MyConstants.ID);
        afterId = getIntent().getExtras().getLong(MyConstants.AFTER_ID);
        categoryToReturn = getIntent().getExtras().getInt(MyConstants.LIST_CATEGORY);
        sortBy = getIntent().getExtras().getString(MyConstants.SORT_BY);

        scaleFilter = getIntent().getExtras().getString(MyConstants.SCALE_FILTER);
        brandFilter = getIntent().getExtras().getString(MyConstants.BRAND_FILTER);
        kitnameFilter = getIntent().getExtras().getString(MyConstants.KITNAME_FILTER);

        statusFilter = getIntent().getExtras().getString(MyConstants.STATUS_FILTER);
        mediaFilter = getIntent().getExtras().getString(MyConstants.MEDIA_FILTER);


        mode = getIntent().getExtras().getChar(MyConstants.WORK_MODE);
        //запрос может прийти от SortAll,
        // таблица афтермаркет тогда MODE_AFTERMARKET
        //от KitCardFragment MODE_VIEW_FROM_KIT
        //от KitEditFragment MODE_EDIT_FROM_KIT


        linLayoutAftermarketContainer = findViewById(R.id.linlayoutAftermarketContainer);

        //Loading fragment with kit list
        //Может прийти запрос на просмотр всей таблицы афтера, MODE_AFTERMARKET
        // на просмотр афтера определенного кита, MODE_KIT

        bundle = new Bundle();
        bundle.putChar(MyConstants.WORK_MODE, mode);
        bundle.putInt(MyConstants.LIST_POSITION, position);
        bundle.putLong(MyConstants.KIT_ID, kitId);
        bundle.putLong(MyConstants.AFTER_ID, afterId);
        bundle.putInt(MyConstants.LIST_CATEGORY, categoryToReturn);

        bundle.putString(MyConstants.SCALE_FILTER, scaleFilter);
        bundle.putString(MyConstants.BRAND_FILTER, brandFilter);
        bundle.putString(MyConstants.KITNAME_FILTER, kitnameFilter);

        bundle.putString(MyConstants.STATUS_FILTER, statusFilter);
        bundle.putString(MyConstants.MEDIA_FILTER, mediaFilter);

        //если пришел знак режима просмотра, открываем карточки.
        AftermarketCardFragment aftermarketCardFragment = new AftermarketCardFragment();
        aftermarketCardFragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.linlayoutAftermarketContainer, aftermarketCardFragment);
        fragmentTransaction.commit();
//        counter++;
    }

    @Override
    public void onBackPressed() {

        //1. Открыть список афтера в майнАктивити
        if (counter == 1 || counter > 2) {
            Intent intent = new Intent(AftermarketActivity.this, MainActivity.class);
            intent.putExtra(MyConstants.SORT_BY, sortBy);
            intent.putExtra(MyConstants.WORK_MODE, mode);
            intent.putExtra(MyConstants.LIST_CATEGORY, categoryToReturn);
            intent.putExtra(MyConstants.LIST_POSITION, position);
            intent.putExtra(MyConstants.SCALE_FILTER, scaleFilter);
            intent.putExtra(MyConstants.BRAND_FILTER, brandFilter);
            intent.putExtra(MyConstants.KITNAME_FILTER, kitnameFilter);
            intent.putExtra(MyConstants.STATUS_FILTER, statusFilter);
            intent.putExtra(MyConstants.MEDIA_FILTER, mediaFilter);
            intent.putExtra("was_deleted", true);
            setResult(RESULT_OK, intent);
            finish();

        } else if (counter == 2) {
            //2. НАДО ОТКРЫТЬ активную карточку афтера
            AftermarketCardFragment aftermarketCardFragment = new AftermarketCardFragment();
            aftermarketCardFragment.setArguments(bundle);
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.linlayoutAftermarketContainer, aftermarketCardFragment);
            fragmentTransaction.commit();
            counter++;
        }


    }

}
