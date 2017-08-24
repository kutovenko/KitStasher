package com.example.kitstasher.fragment;

import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.content.ContentValues;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.other.DbConnector;

/**
 * Created by Алексей on 21.04.2017.
 */

public class SettingsTrashFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, NavigationView.OnNavigationItemSelectedListener{
    private String sortBy;
    private static final int CM_DELETE_ID = 1;
    private static final int CM_RESTORE_ID = 2;
    private Switch swSortTrash;
    private Button btnDeleteAll;
    private String[] from;
    private int[] to;
    private Cursor cursor;
    private SimpleCursorAdapter scAdapter;
    private DbConnector dbConnector;

    public SettingsTrashFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trash, container, false);
        swSortTrash = (Switch)view.findViewById(R.id.swSortTrash);
        swSortTrash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    sortBy = DbConnector.COLUMN_KIT_NAME;
                    prepareListAndAdapter(sortBy);
                    swSortTrash.setText("By Name");
                }else{
                    sortBy = "_id DESC";
                    prepareListAndAdapter(sortBy);
                    swSortTrash.setText("Latest Deleted");
                }
            }
        });
        btnDeleteAll = (Button)view.findViewById(R.id.btnDeleteAll);
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        long id = cursor.getLong(cursor.getColumnIndex(DbConnector.COLUMN_ID));
                        dbConnector.delRec(id);
                        Toast.makeText(getActivity(), getString(R.string.item_deleted) + id, Toast.LENGTH_SHORT).show();
                        cursor.moveToNext();
                    }
                    dbConnector.vacuumDb();
                    prepareListAndAdapter(sortBy);
                }else{
                    Toast.makeText(getActivity(), R.string.nothing_to_delete, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // формируем столбцы сопоставления
        from = new String[]{DbConnector.COLUMN_BRAND, DbConnector.COLUMN_SCALE,
                DbConnector.COLUMN_KIT_NAME, DbConnector.COLUMN_BRAND_CATNO};

        to = new int[] {R.id.tvTrashBrand, R.id.tvTrashScale, R.id.tvTrashKitName, R.id.tvTrashCatNo};
        sortBy = "_id DESC";
        prepareListAndAdapter(sortBy);

        return view;
    }

    //Подготовка списка брэндов и адаптера
    public void prepareListAndAdapter(String sortBy){
        cursor = dbConnector.getAllTrash(sortBy);
        scAdapter = new SimpleCursorAdapter(getActivity(), R.layout.item_short, cursor, from, to, 0);
        setListAdapter(scAdapter);
        getLoaderManager().initLoader(0, null, this);

    }

    //Меню контекстное
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete);
        menu.add(0, CM_RESTORE_ID, 0, R.string.restore);
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            dbConnector.delRec(acmi.id);
            // получаем новый курсор с данными
            prepareListAndAdapter(sortBy);
            return true;
        }
        if (item.getItemId() == CM_RESTORE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();
            // извлекаем id записи и редактируем соответствующую запись в БД
            ContentValues cv = new ContentValues();
            cv.put ("is_deleted", "");
            dbConnector.editRecById(acmi.id, cv);
            // получаем новый курсор с данными
            prepareListAndAdapter(sortBy);
            //getLoaderManager().getLoader(0).forceLoad();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedState){
        super.onActivityCreated(savedState);
        registerForContextMenu(getListView());
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

//    @Override
//    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
//
//    }
//
//    @Override
//    public void onLoaderReset(android.content.Loader<Cursor> loader) {
//
//    }

//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }
}
