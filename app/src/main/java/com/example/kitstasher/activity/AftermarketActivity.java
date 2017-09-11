package com.example.kitstasher.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.widget.LinearLayout;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.AftermarketCardFragment;
import com.example.kitstasher.fragment.KitEditFragment;
import com.example.kitstasher.other.Constants;

public class AftermarketActivity extends AppCompatActivity {

    private LinearLayout linLayoutAftermarketContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aftermarket);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarList);
//        setSupportActionBar(toolbar);
        int position = getIntent().getExtras().getInt(Constants.LIST_POSITION);
        long id = getIntent().getExtras().getLong("after_id");
        int categoryToReturn = getIntent().getExtras().getInt(Constants.LIST_CATEGORY);

        String scaleFilter = getIntent().getExtras().getString("scaleFilter");
        String brandFilter = getIntent().getExtras().getString("brandFilter");
        String kitnameFilter = getIntent().getExtras().getString("kitnameFilter");

        String statusFilter = getIntent().getExtras().getString("statusFilter");
        String mediaFilter = getIntent().getExtras().getString("mediaFilter");

        char mode = getIntent().getExtras().getChar(Constants.EDIT_MODE);



        linLayoutAftermarketContainer = (LinearLayout)findViewById(R.id.linlayoutAftermarketContainer);

        //Loading fragment with kit list
        Bundle bundle = new Bundle();
        bundle.putChar(Constants.EDIT_MODE, mode);
        bundle.putInt(Constants.LIST_POSITION, position);
        bundle.putLong("id", id);
        bundle.putInt(Constants.LIST_CATEGORY, categoryToReturn);

        bundle.putString("scaleFilter", scaleFilter);
        bundle.putString("brandFilter", brandFilter);
        bundle.putString("kitnameFilter", kitnameFilter);

        bundle.putString("statusFilter", statusFilter);
        bundle.putString("mediaFilter", mediaFilter);


//        bundle.putString("listname", title);
        AftermarketCardFragment aftermarketCardFragment = new AftermarketCardFragment();
        aftermarketCardFragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.linlayoutAftermarketContainer, aftermarketCardFragment);

        fragmentTransaction.commit();
    }

}
