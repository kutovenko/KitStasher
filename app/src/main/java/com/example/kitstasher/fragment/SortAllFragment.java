package com.example.kitstasher.fragment;

import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.adapters.AdapterKitList;
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
        LoaderManager.LoaderCallbacks<Cursor>, TextWatcher {
    private DbConnector dbConnector;
    private Cursor cursor;
    private Context context;
    private View view;
    private ImageButton ibtnFilter;
    private LinearLayout linLayoutViewAllContainer,
            linLayoutBrand,
            linLayoutScale,
            linLayoutDate,
            linLayoutKitname;
    private ImageView ivSortBrand,
            ivSortScale,
            ivSortDate,
            ivSortKitname;
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
    private ArrayList<Long> ids;
    private ArrayList<Integer> positions;
    String[] filters;

    public SortAllFragment() {
    }

    public static SortAllFragment newInstance() {
        return new SortAllFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            aftermarketMode = savedInstanceState.getBoolean(MyConstants.AFTERMARKET_MODE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        filters = new String[0];
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_sort_all2, container, false);
        allTag = this.getTag();
        sortBy = "_id DESC";
        categoryTab = getArguments().getInt(MyConstants.CATEGORY_TAB);
        category = getArguments().getString(MyConstants.CATEGORY);
        aftermarketMode = getArguments().getBoolean(MyConstants.AFTERMARKET_MODE);

        initPortraitUi();

        if (aftermarketMode) {
            workMode = MyConstants.MODE_AFTERMARKET;
            activeTable = DbConnector.TABLE_AFTERMARKET;
            ((MainActivity) getActivity())
                    .setActionBarTitle(getActivity().getResources().getString(R.string.aftermarket));
        } else {
            workMode = MyConstants.MODE_KIT;
            activeTable = DbConnector.TABLE_KITS;
            ((MainActivity) getActivity())
                    .setActionBarTitle(getActivity().getResources().getString(R.string.kits));
        }

        if (workMode == MyConstants.MODE_LIST) {
            listname = getArguments().getString(MyConstants.LISTNAME);
        } else {
            listname = MyConstants.EMPTY; //мы идем из карточки кита, не из списка
        }

        cursor = dbConnector.filteredKits(activeTable, filters, "_id DESC", category,
                MyConstants.EMPTY);

        prepareListAndAdapter(cursor);
        returnToList();

        setActive(R.id.linLayoutSortDate, ivSortDate);
        sortDate = true;
        sortName = true;
        sortScale = true;
        sortBrand = true;

        ibtnFilter.setBackgroundColor(Color.TRANSPARENT);
        ibtnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });
        ibtnFilter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                filters = new String[0];
                ibtnFilter.setBackgroundColor(Color.TRANSPARENT);
                cursor = dbConnector.filteredKits(activeTable, filters, "_id DESC", category,
                        MyConstants.EMPTY);
                prepareListAndAdapter(cursor);
                Toast.makeText(context, R.string.Filters_disabled, Toast.LENGTH_SHORT).show();
                ibtnFilter.setColorFilter(R.color.colorAccent);
                return true;
            }
        });

        final FloatingActionButton fab = getParentFragment().getView().findViewById(R.id.fab);


        rvKits.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
//                    fab.show();
                    fab.show();
                }
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(MyConstants.AFTERMARKET_MODE, aftermarketMode);
    }

    public void returnToList(){
        Bundle bundle = getParentFragment().getArguments();
        if (bundle != null) {
            if (bundle.getString(MyConstants.SCALE_FILTER) != null) {
                filters = new String[5];
                filters[0] = bundle.getString(MyConstants.SCALE_FILTER);
                filters[1] = bundle.getString(MyConstants.BRAND_FILTER);
                filters[2] = bundle.getString(MyConstants.KITNAME_FILTER);
                filters[3] = bundle.getString(MyConstants.STATUS_FILTER);
                filters[4] = bundle.getString(MyConstants.MEDIA_FILTER);
                ibtnFilter.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorAccent));
            }

            cursor = dbConnector.filteredKits(activeTable, filters, "_id DESC", category,
                    MyConstants.EMPTY);
            int returnItem = bundle.getInt(MyConstants.POSITION);
            prepareListAndAdapter(cursor);
            rvKits.getLayoutManager().scrollToPosition(returnItem);
        }
    }

    private void prepareListAndAdapter(Cursor cursor) {
        AdapterKitList rvAdapter = new AdapterKitList(cursor, context, filters, activeTable,
                categoryTab, workMode, sortBy, allTag, listname, category);
        rvAdapter.setHasStableIds(true);
        rvKits.setAdapter(rvAdapter);

    }

    private void setActive(int  linLayout, ImageView arrow){
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

            case R.id.linLayoutSortScale:
                setActive(R.id.linLayoutSortScale, ivSortScale);

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

            case R.id.linLayoutSortDate:
                setActive(R.id.linLayoutSortDate, ivSortDate);
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

            case R.id.linLayoutSortKitname:
                setActive(R.id.linLayoutSortKitname, ivSortKitname);
                if (sortName){
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

    private void initPortraitUi(){
        linLayoutViewAllContainer = view.findViewById(R.id.linLayoutViewAllContainer);
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
        ibtnFilter = view.findViewById(R.id.ibtnFilter);
        ibtnFilter.setBackgroundColor(Color.TRANSPARENT);
        ibtnFilter.setColorFilter(R.color.colorAccent);
        rvKits = view.findViewById(R.id.rvKits);
        rvKitsManager = new LinearLayoutManager(getActivity());
        rvKits.setHasFixedSize(true);
        rvKits.setLayoutManager(rvKitsManager);
        rvKits.setItemAnimator(new DefaultItemAnimator());
    }

    private void SortByBrandAsc() {
        cursor = dbConnector.filteredKits(activeTable, filters, "brand", category, MyConstants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortBrand = true;
        sortBy = "brand";
    }

    private void SortByBrandDesc() {
        cursor = dbConnector.filteredKits(activeTable, filters, "brand DESC", category, MyConstants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortBrand = false;
        sortBy = "brand DESC";
    }

    private void SortByScaleAsc() {
        cursor = dbConnector.filteredKits(activeTable, filters, "scale", category, MyConstants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortScale.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortScale = true;
        sortBy = "scale";
    }

    private void SortByScaleDesc() {
        cursor = dbConnector.filteredKits(activeTable, filters, "scale DESC", category, MyConstants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortScale.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortScale = false;
        sortBy = "scale DESC";
    }

    private void SortByDateAcs() {
        cursor = dbConnector.filteredKits(activeTable, filters, "_id", category, MyConstants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortDate = true;
        sortBy = "_id";
    }

    private void SortByDateDesc() {
        cursor = dbConnector.filteredKits(activeTable, filters, "_id DESC", category, MyConstants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortDate = false;
        sortBy = "_id DESC";
    }

    private void SortByNameAsc() {
        cursor = dbConnector.filteredKits(activeTable, filters, "kit_name", category, MyConstants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortName = true;
        sortBy = "kit_name";
    }

    private void SortByNameDesc() {
        cursor = dbConnector.filteredKits(activeTable, filters, "kit_name DESC", category, MyConstants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortName = false;
        sortBy = "kit_name DESC";
    }

    private void showFilterDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialogView = inflater.inflate(R.layout.alertdialog_filter, null);
        dialogBuilder.setView(dialogView);

        final CheckBox cbFilterStatus = dialogView.findViewById(R.id.cbStatus);
        cbFilterStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        final CheckBox cbFilterMedia = dialogView.findViewById(R.id.cbMedia);
        cbFilterMedia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        final CheckBox cbFilterScale = dialogView.findViewById(R.id.cbScale);
        cbFilterScale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        final CheckBox cbFilterBrand = dialogView.findViewById(R.id.cbBrand);
        cbFilterBrand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        final CheckBox cbFilterKitname = dialogView.findViewById(R.id.cbKitname);
        cbFilterKitname.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });


        final Spinner spFilterStatus = dialogView.findViewById(R.id.spStatus);
        ArrayList<String> statusArray = dbConnector.getFilterFromIntData(activeTable, DbConnector.COLUMN_STATUS);
        ArrayAdapter statusAdapter = new ArrayAdapter<>(context,
                R.layout.simple_spinner_item, statusArray);
        spFilterStatus.setAdapter(statusAdapter);

        final Spinner spFilterMedia = dialogView.findViewById(R.id.spMedia);
        ArrayList<String> mediaArray = dbConnector.getFilterFromIntData(activeTable, DbConnector.COLUMN_MEDIA);
        ArrayAdapter mediaAdapter = new ArrayAdapter<>(context,
                R.layout.simple_spinner_item, mediaArray);
        spFilterMedia.setAdapter(mediaAdapter);

        final Spinner spFilterScale = dialogView.findViewById(R.id.spFilterScale);
        ArrayList<String> scalesArray = dbConnector.getFilterData(activeTable, DbConnector.COLUMN_SCALE);
        ArrayAdapter scalesAdapter = new ArrayAdapter<>(context,
                R.layout.simple_spinner_item, scalesArray);
        spFilterScale.setAdapter(scalesAdapter);

        final Spinner spFilterBrand = dialogView.findViewById(R.id.spFilterBrands);
        ArrayList<String> brandsArray = dbConnector.getFilterData(activeTable, DbConnector.COLUMN_BRAND);
        ArrayAdapter brandsAdapter = new ArrayAdapter<>(context,
                R.layout.simple_spinner_item, brandsArray);
        spFilterBrand.setAdapter(brandsAdapter);


        ArrayList<String> kitnamesArray = dbConnector.getFilterData(activeTable, DbConnector.COLUMN_KIT_NAME);

        final AutoCompleteTextView acFilterKitname = dialogView
                .findViewById(R.id.acFilterKitname);
        ArrayAdapter acFilterKitnameAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, kitnamesArray);
        acFilterKitname.addTextChangedListener(this);
        acFilterKitname.setAdapter(acFilterKitnameAdapter);

        dialogBuilder.setTitle(R.string.Filter_by);
        dialogBuilder.setPositiveButton(R.string.Done, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String scaleFilter = "";
                String brandFilter = "";
                String kitnameFilter = "";
                String statusFilter = "";
                String mediaFilter = "";

                if (cbFilterStatus.isChecked()){
                    statusFilter = String.valueOf(spFilterStatus.getSelectedItemPosition()); //преобразовать в код
                }
                if (cbFilterMedia.isChecked()){
                    mediaFilter = String.valueOf(spFilterMedia.getSelectedItemPosition());
                }

                if (cbFilterScale.isChecked()){
                    scaleFilter = spFilterScale.getSelectedItem().toString();
                }
                if (cbFilterBrand.isChecked()){
                    brandFilter = spFilterBrand.getSelectedItem().toString();
                }
                if (cbFilterKitname.isChecked()){
                    kitnameFilter = acFilterKitname.getText().toString().trim();
                }

                if (!(scaleFilter.equals("") && brandFilter.equals("") && kitnameFilter.equals("")
                        && statusFilter.equals("") && mediaFilter.equals(""))){
                    filters = new String[5];
                    filters[0] = scaleFilter;
                    filters[1] = brandFilter;
                    filters[2] = kitnameFilter;
                    filters[3] = statusFilter;
                    filters[4] = mediaFilter;

                    cursor = dbConnector.filteredKits(activeTable, filters, sortBy, category, MyConstants.EMPTY);
                    prepareListAndAdapter(cursor);

                    ibtnFilter.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorAccent));
                    ibtnFilter.setColorFilter(Color.WHITE);
                }
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
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
}
