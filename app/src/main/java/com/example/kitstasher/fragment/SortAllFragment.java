package com.example.kitstasher.fragment;

import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.activity.ViewActivity;
import com.example.kitstasher.adapters.AdapterListGlide;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import static com.example.kitstasher.activity.MainActivity.REQUEST_CODE_VIEW;

/**
 * Created by Алексей on 22.04.2017.
 * Loads and manages kits data
 * Может быть открыт в двух ситуациях: при просмотре таблицы китов и при просмотре таблицы афтера.
 */

public class SortAllFragment extends Fragment implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, TextWatcher {

    DbConnector dbConnector;
    Cursor cursor;
    private Context context;
    private View view;
    private ImageButton ibtnFilter;
    private LinearLayout linLayoutViewAllContainer, linLayoutBrand, linLayoutScale, linLayoutDate,
            linLayoutKitname;
    private ImageView ivSortBrand, ivSortScale, ivSortDate, ivSortKitname;
    private ListView lvKits;
    private char workMode;
    public static String allTag;
    private String sortBy, activeTable, listname;
    public int categoryTab;
    private boolean sortBrand, sortDate, sortScale, sortName,
            aftermarketMode; //отвечает за просмотр таблицы афтемаркета, переключает курсор
    private ArrayList<Long> ids;
    private ArrayList<Integer> positions;
    String[] filters;
    AdapterListGlide lgAdapter;

    public SortAllFragment() {
    }

    public static SortAllFragment newInstance() {
        return new SortAllFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            aftermarketMode = savedInstanceState.getBoolean(Constants.AFTERMARKET_MODE);
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
        view = inflater.inflate(R.layout.fragment_sort_all, container, false);
        allTag = this.getTag();
        sortBy = "_id DESC";
        categoryTab = getArguments().getInt(Constants.CATEGORY);
        aftermarketMode = getArguments().getBoolean(Constants.AFTERMARKET_MODE);
        lgAdapter = new AdapterListGlide(context, cursor);

        initPortraitUi();

        if (aftermarketMode) {
            workMode = Constants.MODE_AFTERMARKET;
            activeTable = DbConnector.TABLE_AFTERMARKET;
            ((MainActivity) getActivity())
                    .setActionBarTitle(getActivity().getResources().getString(R.string.aftermarket));
        } else {
            workMode = Constants.MODE_KIT;
            activeTable = DbConnector.TABLE_KITS;
            ((MainActivity) getActivity())
                    .setActionBarTitle(getActivity().getResources().getString(R.string.kits));
        }
//        workMode = getArguments().getChar(Constants.WORK_MODE);
//        if (workMode == Constants.MODE_AFTERMARKET){
//            activeTable = DbConnector.TABLE_AFTERMARKET;
//            ((MainActivity) getActivity())
//                    .setActionBarTitle(getActivity().getResources().getString(R.string.aftermarket));
//        } else if (workMode == Constants.MODE_KIT){
//            activeTable = DbConnector.TABLE_KITS;
//            ((MainActivity) getActivity())
//                    .setActionBarTitle(getActivity().getResources().getString(R.string.kits));
//        }else{
//            activeTable = DbConnector.TABLE_KITS;
//        }
//Где КУРСОР?
        cursor = dbConnector.filteredKits(activeTable, filters, "_id DESC", categoryTab,
                Constants.EMPTY);
        prepareListAndAdapter(cursor);
        returnToList();

//        if (aftermarketMode) {
//
//        } else if (workMode != Constants.MODE_LIST){
//
//        }

//        if (aftermarketMode) {
//            workMode = Constants.MODE_AFTERMARKET;
//            activeTable = DbConnector.TABLE_AFTERMARKET;
//            ((MainActivity) getActivity())
//                    .setActionBarTitle(getActivity().getResources().getString(R.string.aftermarket));
//        } else if (workMode != Constants.MODE_LIST){
//            workMode = Constants.MODE_KIT;
//            activeTable = DbConnector.TABLE_KITS;
//            ((MainActivity) getActivity())
//                    .setActionBarTitle(getActivity().getResources().getString(R.string.kits));
//        }

        lvKits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                intent = new Intent(getParentFragment().getActivity(), ViewActivity.class);
                intent.putExtra(Constants.AFTER_ID, id);//ид афтемаркета, на котором должен открыться пейджер!
                intent.putExtra(Constants.ID, id);//ид кита, на котором должен открыться пейджер
                // отправляем режим редактирования - кит (таблица кита), афтер, если смотрим таблицу афтера - излишне, мы в афтермоде
                intent.putExtra(Constants.WORK_MODE, workMode);

//                if (aftermarketMode) {
//                    //переходим в активити афтера, там просмотр
////                    intent = new Intent(getParentFragment().getActivity(), AftermarketActivity.class);
//                    //переход к универсальному просмотру
//
//
//
////                    intent = new Intent(getParentFragment().getActivity(), AftermarketActivity.class);
////                    intent.putExtra(Constants.AFTER_ID, id);
//                } else {
//                    //переход в активити просмотра китов
//                    intent = new Intent(getParentFragment().getActivity(), ViewActivity.class);
//                    intent.putExtra(Constants.ID, id);//ид, на котором откротся пейджер
//                    intent.putExtra(Constants.WORK_MODE, Constants.MODE_KIT); //надо ли????????????
//
//                }

//                intent.putExtra(Constants.WORK_MODE, workMode); //мы смотрим киты или афтер? -

                //общие параметры для передачи
                intent.putExtra(Constants.POSITION, position);//ид открытия пейджера
                intent.putExtra(Constants.SORT_BY, sortBy);
                intent.putExtra(Constants.CATEGORY, categoryTab);
                intent.putExtra(Constants.TAG, allTag);
                if (filters.length < 1){
                    filters = new String[]{"", "", "", "", ""};
                }
                intent.putExtra(Constants.SCALE_FILTER, filters[0]);
                intent.putExtra(Constants.BRAND_FILTER, filters[1]);
                intent.putExtra(Constants.KITNAME_FILTER, filters[2]);
                intent.putExtra(Constants.STATUS_FILTER, filters[3]);
                intent.putExtra(Constants.MEDIA_FILTER, filters[4]);
                intent.putExtra(Constants.IDS, ids);
                intent.putExtra(Constants.POSITIONS, positions);
                intent.putExtra(Constants.SORT_BY, sortBy);
                intent.putExtra(Constants.FILTERS, (Serializable) filters);
                if (workMode == Constants.MODE_LIST) {
                    listname = getArguments().getString(Constants.LISTNAME);
                    intent.putExtra(Constants.LISTNAME, listname);
                } else {
                    intent.putExtra(Constants.LISTNAME, Constants.EMPTY); //мы идем из карточки кита, не из списка
                }
                getActivity().startActivityForResult(intent, REQUEST_CODE_VIEW); //интент на просмотр в пейджере
                // пойдет или в китакт или в афтерактивити
            }
        });
        lvKits.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDeleteDialog(l);
                return false;
            }
        });

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
                cursor = dbConnector.filteredKits(activeTable, filters, "_id DESC", categoryTab,
                        Constants.EMPTY);
                prepareListAndAdapter(cursor);
                Toast.makeText(context, R.string.Filters_disabled, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constants.AFTERMARKET_MODE, aftermarketMode);
    }


    public void returnToList(){
        Bundle bundle = getParentFragment().getArguments();
        if (bundle != null) {
            if (bundle.getString(Constants.SCALE_FILTER) != null) {
                filters = new String[5];
                filters[0] = bundle.getString(Constants.SCALE_FILTER);
                filters[1] = bundle.getString(Constants.BRAND_FILTER);
                filters[2] = bundle.getString(Constants.KITNAME_FILTER);
                filters[3] = bundle.getString(Constants.STATUS_FILTER);
                filters[4] = bundle.getString(Constants.MEDIA_FILTER);
                ibtnFilter.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorAccent));
            }

            cursor = dbConnector.filteredKits(activeTable, filters, "_id DESC", categoryTab,
                    Constants.EMPTY);
            int returnItem = bundle.getInt(Constants.POSITION);
            prepareListAndAdapter(cursor);
            lvKits.setSelectionFromTop(returnItem, 0);
        }
    }

    public void prepareListAndAdapter(Cursor cursor){
        lgAdapter = new AdapterListGlide(context, cursor);
        ids = new ArrayList<>();
        positions = new ArrayList<>();
        for (int i = 0; i < lgAdapter.getCount(); i++) {
            ids.add(lgAdapter.getItemId(i)); //заполняем список идентификаторов
            positions.add(i); ///////////// TODO
        }
        lvKits.setAdapter(lgAdapter);
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

        lvKits = view.findViewById(R.id.lvKits);

    }

    public void SortByBrandAsc() {
        cursor = dbConnector.filteredKits(activeTable, filters, "brand", categoryTab, Constants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortBrand = true;
        sortBy = "brand";
    }

    public void SortByBrandDesc() {
        cursor = dbConnector.filteredKits(activeTable, filters, "brand DESC", categoryTab, Constants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortBrand = false;
        sortBy = "brand DESC";
    }

    public void SortByScaleAsc() {
        cursor = dbConnector.filteredKits(activeTable, filters, "scale", categoryTab, Constants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortScale.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortScale = true;
        sortBy = "scale";
    }

    public void SortByScaleDesc() {
        cursor = dbConnector.filteredKits(activeTable, filters, "scale DESC", categoryTab, Constants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortScale.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortScale = false;
        sortBy = "scale DESC";
    }

    public void SortByDateAcs() {
        cursor = dbConnector.filteredKits(activeTable, filters, "_id", categoryTab, Constants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortDate = true;
        sortBy = "_id";
    }

    public void SortByDateDesc() {
        cursor = dbConnector.filteredKits(activeTable, filters, "_id DESC", categoryTab, Constants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortDate = false;
        sortBy = "_id DESC";
    }

    public void SortByNameAsc() {
        cursor = dbConnector.filteredKits(activeTable, filters, "kit_name", categoryTab, Constants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortName = true;
        sortBy = "kit_name";
    }

    public void SortByNameDesc() {
        cursor = dbConnector.filteredKits(activeTable, filters, "kit_name DESC", categoryTab, Constants.EMPTY);
        prepareListAndAdapter(cursor);
        ivSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortName = false;
        sortBy = "kit_name DESC";
    }

    private void showDeleteDialog(final long l) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(R.string.Do_you_wish_to_delete);
        dialogBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                File file = new File(getImagePath(l));
                file.delete();
                dbConnector.delRec(activeTable, l);
                cursor = dbConnector.filteredKits(activeTable, filters, "_id DESC", categoryTab, Constants.EMPTY);
                if (workMode == Constants.MODE_KIT) {
                    ((KitsFragment) getParentFragment()).refreshPages();
                } else if (workMode == Constants.MODE_AFTERMARKET || workMode == Constants.MODE_AFTER_KIT) {
                    ((AftermarketFragment) getParentFragment()).refreshPages();
                }
                prepareListAndAdapter(cursor);
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog d = dialogBuilder.create();
        d.show();
    }

    private String getImagePath(Long l) {
        Cursor imageCursor = dbConnector.getItemById(activeTable, l);
        imageCursor.moveToFirst();
        return imageCursor.getString(cursor.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
    }

    public void showFilterDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
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
        ArrayAdapter statusAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.simple_spinner_item, statusArray);
        spFilterStatus.setAdapter(statusAdapter);

        final Spinner spFilterMedia = dialogView.findViewById(R.id.spMedia);
        ArrayList<String> mediaArray = dbConnector.getFilterFromIntData(activeTable, DbConnector.COLUMN_MEDIA);
        ArrayAdapter mediaAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.simple_spinner_item, mediaArray);
        spFilterMedia.setAdapter(mediaAdapter);

        final Spinner spFilterScale = dialogView.findViewById(R.id.spFilterScale);
        ArrayList<String> scalesArray = dbConnector.getFilterData(activeTable, DbConnector.COLUMN_SCALE);
        ArrayAdapter scalesAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.simple_spinner_item, scalesArray);
        spFilterScale.setAdapter(scalesAdapter);

        final Spinner spFilterBrand = dialogView.findViewById(R.id.spFilterBrands);
        ArrayList<String> brandsArray = dbConnector.getFilterData(activeTable, DbConnector.COLUMN_BRAND);
        ArrayAdapter brandsAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.simple_spinner_item, brandsArray);
        spFilterBrand.setAdapter(brandsAdapter);


        ArrayList<String> kitnamesArray = dbConnector.getFilterData(activeTable, DbConnector.COLUMN_KIT_NAME);

        final AutoCompleteTextView acFilterKitname = dialogView
                .findViewById(R.id.acFilterKitname);
        ArrayAdapter acFilterKitnameAdapter = new ArrayAdapter<>(getActivity(),
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

                    cursor = dbConnector.filteredKits(activeTable, filters, sortBy, categoryTab, Constants.EMPTY);
                    prepareListAndAdapter(cursor);

                    ibtnFilter.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorAccent));
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
