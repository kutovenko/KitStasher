package com.example.kitstasher.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.ListViewFragment;
import com.example.kitstasher.other.DbConnector;

import static com.example.kitstasher.activity.MainActivity.REQUEST_CODE_POSITION;

public class ListActivity extends AppCompatActivity{
    private LinearLayout llListsContainer;
    private Cursor cursor;
    private DbConnector dbConnector;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarList);
        setSupportActionBar(toolbar);
        llListsContainer = (LinearLayout) findViewById(R.id.llListsContainer);
        dbConnector = new DbConnector(this);
        dbConnector.open();
        cursor = dbConnector.getListById(getIntent().getExtras().getLong("list_name"));
        cursor.moveToFirst();
        title = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.MYLISTS_COLUMN_LIST_NAME));
        setTitle(title);

        //Loading fragment with kit list
        Bundle bundle = new Bundle(2);
        bundle.putChar("mode", 'l');
        bundle.putString("listname", title);
        ListViewFragment listViewFragment = new ListViewFragment();
        listViewFragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.llListsContainer, listViewFragment);

        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_CODE_POSITION) {
            if (resultCode == RESULT_OK) {
                    ListViewFragment listViewFragment = new ListViewFragment();
                    Bundle bundle = getIntent().getExtras();
                    listViewFragment.setArguments(bundle);
                    android.support.v4.app.FragmentTransaction fragmentTransaction =
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.llListsContainer, listViewFragment);
                    fragmentTransaction.commit();
            }
        }
    }
}
