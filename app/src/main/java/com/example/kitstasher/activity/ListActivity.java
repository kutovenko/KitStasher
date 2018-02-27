package com.example.kitstasher.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.kitstasher.R;
import com.example.kitstasher.fragment.ListViewFragment;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;

import static com.example.kitstasher.activity.MainActivity.REQUEST_CODE_VIEW;

public class ListActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        DbConnector dbConnector = new DbConnector(this);
        dbConnector.open();
        long listId = getIntent().getExtras().getLong(MyConstants.LISTID);
        Cursor cursor = dbConnector.getListById(listId);
        cursor.moveToFirst();
        String listName = cursor.getString(cursor.getColumnIndexOrThrow(DbConnector.MYLISTS_COLUMN_LIST_NAME));
        setTitle(listName);
        Bundle bundle = new Bundle(2);
        bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_LIST);
        bundle.putString(MyConstants.LISTNAME, listName);
        bundle.putLong(MyConstants.LISTID, listId);
        ListViewFragment listViewFragment = new ListViewFragment();
        listViewFragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.llListsContainer, listViewFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_VIEW) {
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
