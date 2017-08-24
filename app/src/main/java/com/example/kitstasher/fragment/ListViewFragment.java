package com.example.kitstasher.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.ChooserActivity;
import com.example.kitstasher.activity.KitActivity;
import com.example.kitstasher.adapters.AdapterListGlide;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.SortKits;

import static com.example.kitstasher.activity.MainActivity.REQUEST_CODE_POSITION;

/**
 * Created by Алексей on 14.08.2017.
 */

public class ListViewFragment extends Fragment implements View.OnClickListener, SortKits {
    private ListView lvListAllItems;
    private Cursor cursor;
    private DbConnector dbConnector;
    private boolean sortDate, sortName, sortScale, sortBrand;
    private LinearLayout linLayoutListBrand, linLayoutListScale, linLayoutListDate,
            linLayoutListKitname, llListsContainer;
    private ImageView ivListSortDate, ivListSortBrand, ivListSortScale, ivListSortKitname;
    private Context context;
    private String listname;
    private View view;


    public ListViewFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_listview, container, false);
        context = getActivity();
        dbConnector = new DbConnector(context);
        dbConnector.open();
        initPortraitUi();
        Button btnAddListItem = (Button)view.findViewById(R.id.btnAddListItem);
        btnAddListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseAddMethodDialog();
            }
        });

        lvListAllItems = (ListView)view.findViewById(R.id.lvListAllItems);
        llListsContainer = (LinearLayout)view.findViewById(R.id.llListsContainer);
        Bundle bundle = getArguments();
        listname = bundle.getString("listname");


        cursor = dbConnector.getListItems(listname, "_id DESC");

        prepareListAndAdapter(cursor);
        returnToList();

        setActive(R.id.linLayoutListSortDate, ivListSortDate);
        sortDate = true;
        sortName = true;
        sortScale = true;
        sortBrand = true;

        return view;
    }

    public void returnToList(){
        Bundle bundle = getArguments();
        if (bundle != null) {
            String listname = bundle.getString("listname");
            long returnItemId = bundle.getLong("id");
            int returnItem = bundle.getInt("position");
            cursor = dbConnector.getListItems(listname, "_id DESC"); //влияет на сортировку списка после возврата
            prepareListAndAdapter(cursor);
            lvListAllItems.setSelectionFromTop(returnItem, 0);
        }
    }
    private void showChooseAddMethodDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.list_choosemode_alertdialog, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle(R.string.Choose_mode);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final Button getFromScan = (Button)dialogView.findViewById(R.id.btnListModeScan);
        getFromScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ScanFragment fragment = new ScanFragment();
                Bundle bundle = new Bundle(2);
                bundle.putChar("mode", 'l');
                bundle.putString("listname", listname);
                fragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.llListsContainer, fragment);
                fragmentTransaction.commit();
                alertDialog.dismiss();

            }
        });
        final Button getFromManualAdd = (Button)dialogView.findViewById(R.id.btnListModeManual);
        getFromManualAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManualAddFragment fragment = new ManualAddFragment();
                Bundle bundle = new Bundle(2);
                bundle.putChar("mode", 'l');
                bundle.putString("listname", listname);
                fragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.llListsContainer, fragment);
                fragmentTransaction.commit();
                alertDialog.dismiss();
            }
        });

        final Button getFromMyStash = (Button)dialogView.findViewById(R.id.btnListModeMyStash);
        getFromMyStash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChooserActivity.class);
                intent.putExtra("listname", listname);
                startActivityForResult(intent, 10);
                alertDialog.dismiss();
            }
        });
//        final Button getFromImport = (Button)dialogView.findViewById(R.id.btnListModeImport);
    }

    public void prepareListAndAdapter(Cursor cursor){

        AdapterListGlide adapterListGlide = new AdapterListGlide(context, cursor);
        lvListAllItems.setAdapter(adapterListGlide);

        lvListAllItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, KitActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("id", id);
                intent.putExtra("mode", 'l');
                startActivityForResult(intent, REQUEST_CODE_POSITION);
            }
        });
    }

    private void setActive(int  linLayout, ImageView arrow){
        linLayoutListScale.setBackgroundColor(Color.TRANSPARENT);
        linLayoutListBrand.setBackgroundColor(Color.TRANSPARENT);
        linLayoutListDate.setBackgroundColor(Color.TRANSPARENT);
        linLayoutListKitname.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout activeLayout = (LinearLayout)view.findViewById(linLayout);
        activeLayout.setBackgroundColor(Helper.getColor(context, R.color.colorAccent));

        ivListSortBrand.setVisibility(View.INVISIBLE);
        ivListSortKitname.setVisibility(View.INVISIBLE);
        ivListSortScale.setVisibility(View.INVISIBLE);
        ivListSortDate.setVisibility(View.INVISIBLE);
        arrow.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddListItem:
                showChooseAddMethodDialog();
                break;
            //Кнопки сортировки списка
            case R.id.linLayoutListSortBrand:
                setActive(R.id.linLayoutListSortBrand, ivListSortBrand);

                if (sortBrand){
                    SortByBrandAsc();
                    sortBrand = false;
                }else {
                    SortByBrandDesc();
                    sortBrand = true;
                }
                sortDate = true;
                sortScale = true;
                sortName = true;
                break;

            case R.id.linLayoutListSortScale:
                setActive(R.id.linLayoutListSortScale, ivListSortScale);
                if (sortScale){
                    SortByScaleAsc();
                    sortScale = false;
                }else {
                    SortByScaleDesc();
                    sortScale = true;
                }
                sortBrand = true;
                sortDate = true;
                sortName = true;
                break;

            case R.id.linLayoutListSortDate:
                setActive(R.id.linLayoutListSortDate, ivListSortDate);
                if (sortDate){
                    SortByDateAcs();
                    sortDate = false;
                }else {
                    SortByDateDesc();
                    sortDate = true;
                }
                sortBrand = true;
                sortScale = true;
                sortName = true;
                break;

            case R.id.linLayoutListSortKitname:
                setActive(R.id.linLayoutListSortKitname, ivListSortKitname);
                if (sortName){
                    SortByNameAsc();
                    sortName = false;
                }else {
                    SortByNameDesc();
                    sortName = true;
                }
                sortBrand = true;
                sortDate = true;
                sortScale = true;
                break;
        }
    }

    private void initPortraitUi(){
        linLayoutListBrand = (LinearLayout)view.findViewById(R.id.linLayoutListSortBrand);
        linLayoutListBrand.setOnClickListener(this);
        linLayoutListScale = (LinearLayout)view.findViewById(R.id.linLayoutListSortScale);
        linLayoutListScale.setOnClickListener(this);
        linLayoutListDate = (LinearLayout)view.findViewById(R.id.linLayoutListSortDate);
        linLayoutListDate.setOnClickListener(this);
        linLayoutListKitname = (LinearLayout)view.findViewById(R.id.linLayoutListSortKitname);
        linLayoutListKitname.setOnClickListener(this);

        ivListSortBrand = (ImageView)view.findViewById(R.id.ivListSortBrand);
        ivListSortBrand.setVisibility(View.INVISIBLE);
        ivListSortDate = (ImageView)view.findViewById(R.id.ivListSortDate);
        ivListSortDate.setVisibility(View.INVISIBLE);
        ivListSortScale = (ImageView)view.findViewById(R.id.ivListSortScale);
        ivListSortScale.setVisibility(View.INVISIBLE);
        ivListSortKitname = (ImageView)view.findViewById(R.id.ivListSortKitname);
        ivListSortKitname.setVisibility(View.INVISIBLE);
    }

    @Override
    public void SortByBrandAsc() {
        cursor = dbConnector.getListItems(listname, "brand");
        prepareListAndAdapter(cursor);
        ivListSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        sortBrand = true;
    }

    @Override
    public void SortByBrandDesc() {
        cursor = dbConnector.getListItems(listname, "brand DESC");
        prepareListAndAdapter(cursor);
        ivListSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        sortBrand = false;

    }

    @Override
    public void SortByScaleAsc() {
        cursor = dbConnector.getListItems(listname, "scale");
        prepareListAndAdapter(cursor);
        ivListSortScale.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        sortScale = true;
    }

    @Override
    public void SortByScaleDesc() {
        cursor = dbConnector.getListItems(listname, "scale DESC");
        prepareListAndAdapter(cursor);
        ivListSortScale.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        sortScale = false;
    }

    @Override
    public void SortByDateAcs() {
        cursor = dbConnector.getListItems(listname, "_id");
        prepareListAndAdapter(cursor);
        ivListSortDate.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        sortDate = true;
    }

    @Override
    public void SortByDateDesc() {
        cursor = dbConnector.getListItems(listname, "_id DESC");
        prepareListAndAdapter(cursor);
        ivListSortDate.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        sortDate = false;
    }

    @Override
    public void SortByNameAsc() {
        cursor = dbConnector.getListItems(listname, "kit_name");
        prepareListAndAdapter(cursor);
        ivListSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        sortName = true;
    }

    @Override
    public void SortByNameDesc() {
        cursor = dbConnector.getListItems(listname, "kit_name DESC");
        prepareListAndAdapter(cursor);
        ivListSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        sortName = false;
    }

    @Override
    public void onResume(){
        super.onResume();
        cursor = dbConnector.getListItems(listname, "_id DESC");
        prepareListAndAdapter(cursor);
    }
}
