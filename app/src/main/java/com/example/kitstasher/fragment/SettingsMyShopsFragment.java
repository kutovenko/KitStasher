package com.example.kitstasher.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;

import com.example.kitstasher.R;
import com.example.kitstasher.other.DbConnector;

/**
 * Created by Алексей on 30.08.2017.
 */

public class SettingsMyShopsFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor>, NavigationView.OnNavigationItemSelectedListener
{

    private String sortBy;
    private static final int CM_DELETE_ID = 22;
    private String[] from;
    private int[] to;
    private Cursor cursor;
    private SimpleCursorAdapter scAdapter;
    private DbConnector db;

    public SettingsMyShopsFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DbConnector(getActivity());
        db.open();
        setListAdapter(scAdapter);
        getLoaderManager().initLoader(0, null, this);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_brands_list, container, false);


        // формируем столбцы сопоставления
        from = new String[]{DbConnector.MYSHOPS_COLUMN_SHOP_NAME};
        to = new int[] { R.id.tvEditBrandListItem};
        sortBy = "shop_name";
        prepareListAndAdapter(sortBy);

        return view;
    }

    //Подготовка списка брэндов и адаптера
    public void prepareListAndAdapter(String sortBy){
        cursor = db.getShops(sortBy);
        scAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_edit_brands, cursor, from, to, 0);
        setListAdapter(scAdapter);

    }

    //Меню контекстное
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Delete");
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            db.delShopById(acmi.id);
            // получаем новый курсор с данными
            prepareListAndAdapter(sortBy);
            return true;
        }
        return super.onContextItemSelected(item);
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

    @Override
    public void onActivityCreated(Bundle savedState){
        super.onActivityCreated(savedState);
        registerForContextMenu(getListView());
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    static class MyCursorLoader extends CursorLoader {

        DbConnector db;

        public MyCursorLoader(Context context, DbConnector db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getShops("_id DESC");
            return cursor;
        }

    }
}