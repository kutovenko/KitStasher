package com.example.kitstasher.fragment;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.activity.ViewActivity;
import com.example.kitstasher.adapters.NewAdapterKitList;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;

import java.util.ArrayList;

/**
 * Created by Алексей on 22.04.2017.
 * Loads and manages kits data
 * Может быть открыт в двух ситуациях: при просмотре таблицы китов и при просмотре таблицы афтера.
 */

public class SortAllFragment extends Fragment implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, TextWatcher, NewAdapterKitList.FilterListener {
    private DbConnector dbConnector;
    //    private Cursor cursor;
    private Context context;
    private View view;
    private ImageButton ibtnFilter;
    private LinearLayout linLayoutViewAllContainer,
            llSortToolbar,
            linLayoutBrand,
            linLayoutScale,
            linLayoutDate,
            linLayoutKitname;
    private ImageView ivSortBrand,
            ivSortScale,
            ivSortDate,
            ivSortKitname;
    private SearchView searchView;
    private RecyclerView rvKits;
    private LinearLayoutManager rvKitsManager;
    private char workMode;
    public static String allTag;
    private String category,
            sortBy,
            activeTable,
            listname;
    public int categoryTab;
    private boolean sortBrand,
            sortDate,
            sortScale,
            sortName,
            aftermarketMode; //отвечает за просмотр таблицы афтемаркета, переключает курсор
    private ArrayList<Kit> itemList; //from DB
    private ArrayList<Kit> filteredList; //return, filter, etc. used for adapter
    String[] filters;
    NewAdapterKitList rvAdapter;


    public SortAllFragment() {
    }

    public static SortAllFragment newInstance() {
        return new SortAllFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();
        sortBy = "_id DESC";
        categoryTab = getArguments().getInt(MyConstants.CATEGORY_TAB);
        category = getArguments().getString(MyConstants.CATEGORY);
        aftermarketMode = getArguments().getBoolean(MyConstants.AFTERMARKET_MODE);

        dbConnector = new DbConnector(context);
        dbConnector.open();

        if (aftermarketMode) {
            workMode = MyConstants.MODE_AFTERMARKET;
            activeTable = DbConnector.TABLE_AFTERMARKET;
        } else {
            workMode = MyConstants.MODE_KIT;
            activeTable = DbConnector.TABLE_KITS;
        }

        if (savedInstanceState != null) {
            aftermarketMode = savedInstanceState.getBoolean(MyConstants.AFTERMARKET_MODE);
            itemList = savedInstanceState.getParcelableArrayList("itemList");
        } else {
            itemList = dbConnector.filteredKits(activeTable, sortBy, category);
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sort_all, container, false);
        allTag = this.getTag();
//setRetainInstance(true);
        if (aftermarketMode) {
            ((MainActivity) getActivity())
                    .setActionBarTitle(getActivity().getResources().getString(R.string.aftermarket));
        } else {
            ((MainActivity) getActivity())
                    .setActionBarTitle(getActivity().getResources().getString(R.string.kits));
        }

        initPortraitUi();

        rvAdapter = new NewAdapterKitList(itemList, context, activeTable, this);
        rvKits.setAdapter(rvAdapter);
        returnToList();

        setActive(R.id.linLayoutSortDate, ivSortDate);
        sortDate = true;
        sortName = true;
        sortScale = true;
        sortBrand = true;

        final FloatingActionButton fab = getParentFragment().getView().findViewById(R.id.fab);

        rvKits.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        return view;
    }

    public void search(String query) {
        rvAdapter.getFilter().filter(query);
    }


    public void returnToList() {
        Bundle bundle = getParentFragment().getArguments();
        int returnItem = bundle != null ? bundle.getInt(MyConstants.POSITION) : 0;
        rvKits.getLayoutManager().scrollToPosition(returnItem);
    }

    private void setActive(int linLayout, ImageView arrow) {
        linLayoutScale.setBackgroundColor(Color.TRANSPARENT);
        setTextColor(linLayoutScale, 0);
        linLayoutBrand.setBackgroundColor(Color.TRANSPARENT);
        setTextColor(linLayoutBrand, 0);
        linLayoutDate.setBackgroundColor(Color.TRANSPARENT);
        setTextColor(linLayoutDate, 0);
        linLayoutKitname.setBackgroundColor(Color.TRANSPARENT);
        setTextColor(linLayoutKitname, 0);
        LinearLayout activeLayout = view.findViewById(linLayout);
        activeLayout.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorAccent));
        setTextColor(activeLayout, 1);

        ivSortBrand.setVisibility(View.INVISIBLE);
        ivSortKitname.setVisibility(View.INVISIBLE);
        ivSortScale.setVisibility(View.INVISIBLE);
        ivSortDate.setVisibility(View.INVISIBLE);
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
            case R.id.linLayoutSortBrand:
                setActive(R.id.linLayoutSortBrand, ivSortBrand);

                if (sortBrand) {
                    SortByBrandAsc();
                    sortBrand = false;
                } else {
                    SortByBrandDesc();
                    sortBrand = true;
                }
                sortDate = true;
                sortScale = true;
                sortName = true;
                break;

            case R.id.linLayoutSortScale:
                setActive(R.id.linLayoutSortScale, ivSortScale);

                if (sortScale) {
                    SortByScaleAsc();
                    sortScale = false;
                } else {
                    SortByScaleDesc();
                    sortScale = true;
                }
                sortBrand = true;
                sortDate = true;
                sortName = true;
                break;

            case R.id.linLayoutSortDate:
                setActive(R.id.linLayoutSortDate, ivSortDate);
                if (sortDate) {
                    SortByDateAcs();
                    sortDate = false;
                } else {
                    SortByDateDesc();
                    sortDate = true;
                }
                sortBrand = true;
                sortScale = true;
                sortName = true;
                break;

            case R.id.linLayoutSortKitname:
                setActive(R.id.linLayoutSortKitname, ivSortKitname);
                if (sortName) {
                    SortByNameAsc();
                    sortName = false;
                } else {
                    SortByNameDesc();
                    sortName = true;
                }
                sortBrand = true;
                sortDate = true;
                sortScale = true;
                break;
        }
    }

    private void initPortraitUi() {
        linLayoutViewAllContainer = view.findViewById(R.id.linLayoutViewAllContainer);
        llSortToolbar = view.findViewById(R.id.llSortToolbar);
        linLayoutBrand = view.findViewById(R.id.linLayoutSortBrand);
        linLayoutBrand.setOnClickListener(this);
        linLayoutScale = view.findViewById(R.id.linLayoutSortScale);
        linLayoutScale.setOnClickListener(this);
        linLayoutDate = view.findViewById(R.id.linLayoutSortDate);
        linLayoutDate.setOnClickListener(this);
        linLayoutKitname = view.findViewById(R.id.linLayoutSortKitname);
        linLayoutKitname.setOnClickListener(this);
        ivSortBrand = view.findViewById(R.id.ivSortBrand);
        ivSortBrand.setVisibility(View.INVISIBLE);
        ivSortDate = view.findViewById(R.id.ivSortDate);
        ivSortDate.setVisibility(View.INVISIBLE);
        ivSortScale = view.findViewById(R.id.ivSortScale);
        ivSortScale.setVisibility(View.INVISIBLE);
        ivSortKitname = view.findViewById(R.id.ivSortKitname);
        ivSortKitname.setVisibility(View.INVISIBLE);
        rvKits = view.findViewById(R.id.rvKits);
        rvKitsManager = new LinearLayoutManager(getActivity());
        rvKits.setHasFixedSize(true);
        rvKits.setLayoutManager(rvKitsManager);
        rvKits.setItemAnimator(new DefaultItemAnimator());
    }

    private void SortByBrandAsc() {
        itemList.clear();
        itemList.addAll(dbConnector.filteredKits(activeTable, "brand", category));
        rvAdapter.notifyItemRangeChanged(0, itemList.size());

        ivSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortBrand = true;
        sortBy = "brand";

    }

    private void SortByBrandDesc() {
        itemList.clear();
        itemList.addAll(dbConnector.filteredKits(activeTable, "brand DESC", category));
        rvAdapter.notifyItemRangeChanged(0, itemList.size());
        ivSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortBrand = false;
        sortBy = "brand DESC";

    }

    private void SortByScaleAsc() {
        itemList.clear();
        itemList.addAll(dbConnector.filteredKits(activeTable, "scale", category));
        rvAdapter.notifyItemRangeChanged(0, itemList.size());
        ivSortScale.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortScale = true;
        sortBy = "scale";
    }

    private void SortByScaleDesc() {
        itemList.clear();
        itemList.addAll(dbConnector.filteredKits(activeTable, "scale DESC", category));
        rvAdapter.notifyItemRangeChanged(0, itemList.size());
        ivSortScale.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortScale = false;
        sortBy = "scale DESC";
    }

    private void SortByDateAcs() {
        itemList.clear();
        itemList.addAll(dbConnector.filteredKits(activeTable, "_id", category));
        rvAdapter.notifyItemRangeChanged(0, itemList.size());
        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortDate = true;
        sortBy = "_id";
    }

    private void SortByDateDesc() {
        itemList.clear();
        itemList.addAll(dbConnector.filteredKits(activeTable, "_id DESC", category));
        rvAdapter.notifyItemRangeChanged(0, itemList.size());
        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortDate = false;
        sortBy = "_id DESC";
    }

    private void SortByNameAsc() {
        itemList.clear();
        itemList.addAll(dbConnector.filteredKits(activeTable, "kit_name", category));
        rvAdapter.notifyItemRangeChanged(0, itemList.size());
        ivSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortName = true;
        sortBy = "kit_name";
    }

    private void SortByNameDesc() {
        itemList.clear();
        itemList.addAll(dbConnector.filteredKits(activeTable, "kit_name DESC", category));
        rvAdapter.notifyItemRangeChanged(0, itemList.size());
        ivSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortName = false;
        sortBy = "kit_name DESC";
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
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    @Override
    public void onItemSelected(Kit kit, ArrayList<Kit> filteredItemList, int position) {

        Intent intent;
        intent = new Intent(context, ViewActivity.class);
        intent.putExtra(MyConstants.WORK_MODE, workMode);
        //общие параметры для передачи
        intent.putExtra(MyConstants.POSITION, position);//ид открытия пейджера
        intent.putExtra(MyConstants.SORT_BY, sortBy);
        intent.putExtra(MyConstants.CATEGORY_TAB, activeTable);
        intent.putParcelableArrayListExtra(MyConstants.LIST, filteredItemList);
        ((Activity) context).startActivityForResult(intent, MainActivity.REQUEST_CODE_VIEW);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(MyConstants.AFTERMARKET_MODE, aftermarketMode);
        outState.putParcelableArrayList("itemList", itemList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dbConnector.close();
    }
}