package com.example.kitstasher.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.ChooserActivity;
import com.example.kitstasher.adapters.AdapterKitList;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;

/**
 * Created by Алексей on 14.08.2017. Просмотр списка
 */

public class ListViewFragment extends Fragment implements View.OnClickListener {
    private RecyclerView rvListAllItems;
    private LinearLayoutManager rvKitsManager;
    private Cursor cursor;
    private DbConnector dbConnector;
    private boolean sortDate, sortName, sortScale, sortBrand;
    private LinearLayout linLayoutListBrand, linLayoutListScale, linLayoutListDate,
            linLayoutListKitname;
    private ImageView ivListSortDate, ivListSortBrand, ivListSortScale, ivListSortKitname;
    private Context context;
    private String listname;
    private View view;
    private AdapterKitList rvAdapter;


    public ListViewFragment() {
        super();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_listview, container, false);
        context = getActivity();
        dbConnector = new DbConnector(context);
        dbConnector.open();
        initPortraitUi();
        Button btnAddListItem = view.findViewById(R.id.btnAddListItem);
        btnAddListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseAddMethodDialog();
            }
        });

        Bundle bundle = getArguments();
        listname = bundle != null ? bundle.getString(MyConstants.LISTNAME) : "";


        cursor = dbConnector.getListItems(listname, "_id DESC");


        rvAdapter = new AdapterKitList(cursor, context, new String[]{},
                DbConnector.TABLE_MYLISTSITEMS,
                0, MyConstants.MODE_LIST, DbConnector.COLUMN_ID, MyConstants.EMPTY, listname, ""); //todo!!!!!!!!!!!!!!!!!!!
        rvAdapter.setHasStableIds(true);

        rvListAllItems.setAdapter(rvAdapter);

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
            int returnItem = bundle.getInt("position");
            cursor = dbConnector.getListItems(listname, "_id DESC"); //влияет на сортировку списка после возврата
            prepareListAndAdapter(cursor);
            rvListAllItems.getLayoutManager().scrollToPosition(returnItem);
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

        final Button getFromScan = dialogView.findViewById(R.id.btnListModeScan);
        getFromScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ScanFragment fragment = new ScanFragment();
                Bundle bundle = new Bundle(2);
                bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_LIST);
                bundle.putString(MyConstants.LISTNAME, listname);
                fragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.llListsContainer, fragment);
                fragmentTransaction.commit();
                alertDialog.dismiss();

            }
        });
        final Button getFromManualAdd = dialogView.findViewById(R.id.btnListModeManual);
        getFromManualAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManualAddFragment fragment = new ManualAddFragment();
                Bundle bundle = new Bundle(2);
                bundle.putChar(MyConstants.WORK_MODE, MyConstants.MODE_LIST);
                bundle.putString(MyConstants.LISTNAME, listname);
                fragment.setArguments(bundle);
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.llListsContainer, fragment);
                fragmentTransaction.commit();
                alertDialog.dismiss();
            }
        });

        final Button getFromMyStash = dialogView.findViewById(R.id.btnListModeMyStash);
        getFromMyStash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChooserActivity.class);
                intent.putExtra(MyConstants.LISTNAME, listname);
                intent.putExtra(MyConstants.WORK_MODE, MyConstants.MODE_LIST);
                startActivityForResult(intent, 10);
                alertDialog.dismiss();
            }
        });
    }

    public void prepareListAndAdapter(Cursor cursor){
        rvAdapter.changeCursor(cursor);
        rvAdapter.notifyDataSetChanged();
    }


    private void setActive(int  linLayout, ImageView arrow){
        linLayoutListScale.setBackgroundColor(Color.TRANSPARENT);
        setTextColor(linLayoutListScale, 0);
        linLayoutListBrand.setBackgroundColor(Color.TRANSPARENT);
        setTextColor(linLayoutListBrand, 0);
        linLayoutListDate.setBackgroundColor(Color.TRANSPARENT);
        setTextColor(linLayoutListDate, 0);
        linLayoutListKitname.setBackgroundColor(Color.TRANSPARENT);
        setTextColor(linLayoutListKitname, 0);
        LinearLayout activeLayout = view.findViewById(linLayout);
        activeLayout.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorAccent));
        setTextColor(activeLayout, 1);

        ivListSortBrand.setVisibility(View.INVISIBLE);
        ivListSortKitname.setVisibility(View.INVISIBLE);
        ivListSortScale.setVisibility(View.INVISIBLE);
        ivListSortDate.setVisibility(View.INVISIBLE);
        arrow.setVisibility(View.VISIBLE);

    }

    private void setTextColor(LinearLayout linearLayout, int mode) { //todo helper
        View view = linearLayout.getChildAt(0);
        int color;
        if (mode == 0) {
            color = Helper.getColor(getActivity(), R.color.colorPassive);
        } else {
            color = Helper.getColor(getActivity(), R.color.colorItem);
        }
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setTextColor(color);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddListItem:
                showChooseAddMethodDialog();
                break;
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
        rvListAllItems = view.findViewById(R.id.rvListAllItems);
        rvKitsManager = new LinearLayoutManager(getActivity());
        rvListAllItems.setHasFixedSize(true);
        rvListAllItems.setLayoutManager(rvKitsManager);
        rvListAllItems.setItemAnimator(new DefaultItemAnimator());

        linLayoutListBrand = view.findViewById(R.id.linLayoutListSortBrand);
        linLayoutListBrand.setOnClickListener(this);
        linLayoutListScale = view.findViewById(R.id.linLayoutListSortScale);
        linLayoutListScale.setOnClickListener(this);
        linLayoutListDate = view.findViewById(R.id.linLayoutListSortDate);
        linLayoutListDate.setOnClickListener(this);
        linLayoutListKitname = view.findViewById(R.id.linLayoutListSortKitname);
        linLayoutListKitname.setOnClickListener(this);

        ivListSortBrand = view.findViewById(R.id.ivListSortBrand);
        ivListSortBrand.setVisibility(View.INVISIBLE);
        ivListSortDate = view.findViewById(R.id.ivListSortDate);
        ivListSortDate.setVisibility(View.INVISIBLE);
        ivListSortScale = view.findViewById(R.id.ivListSortScale);
        ivListSortScale.setVisibility(View.INVISIBLE);
        ivListSortKitname = view.findViewById(R.id.ivListSortKitname);
        ivListSortKitname.setVisibility(View.INVISIBLE);
    }

    private void SortByBrandAsc() {
        cursor = dbConnector.getListItems(listname, "brand");
        prepareListAndAdapter(cursor);
        ivListSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortBrand = true;
    }

    private void SortByBrandDesc() {
        cursor = dbConnector.getListItems(listname, "brand DESC");
        prepareListAndAdapter(cursor);
        ivListSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortBrand = false;

    }

    private void SortByScaleAsc() {
        cursor = dbConnector.getListItems(listname, "scale");
        prepareListAndAdapter(cursor);
        ivListSortScale.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortScale = true;
    }

    private void SortByScaleDesc() {
        cursor = dbConnector.getListItems(listname, "scale DESC");
        prepareListAndAdapter(cursor);
        ivListSortScale.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortScale = false;
    }

    private void SortByDateAcs() {
        cursor = dbConnector.getListItems(listname, "_id");
        prepareListAndAdapter(cursor);
        ivListSortDate.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortDate = true;
    }

    private void SortByDateDesc() {
        cursor = dbConnector.getListItems(listname, "_id DESC");
        prepareListAndAdapter(cursor);
        ivListSortDate.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortDate = false;
    }

    private void SortByNameAsc() {
        cursor = dbConnector.getListItems(listname, "kit_name");
        prepareListAndAdapter(cursor);
        ivListSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortName = true;
    }

    private void SortByNameDesc() {
        cursor = dbConnector.getListItems(listname, "kit_name DESC");
        prepareListAndAdapter(cursor);
        ivListSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortName = false;
    }

    @Override
    public void onResume(){
        super.onResume();
        cursor = dbConnector.getListItems(listname, "_id DESC");
        prepareListAndAdapter(cursor);
    }
}
