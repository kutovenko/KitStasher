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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.activity.KitActivity;
import com.example.kitstasher.adapters.AdapterListGlide;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.SortKits;

import java.util.ArrayList;

import static com.example.kitstasher.activity.MainActivity.REQUEST_CODE_POSITION;

/**
 * Created by Алексей on 22.04.2017.
 * Loads and manages kits data
 */

public class SortFantasyFragment extends Fragment implements SortKits, View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, TextWatcher {
    DbConnector dbConnector;
    Cursor cursor;

    private ImageButton ibtnFilter;

    //Для списка сортировок
    private boolean sortBrand, sortDate, sortScale, sortName;
    final public int categoryTab = 7;
    private LinearLayout linLayoutBrand, linLayoutScale, linLayoutDate,
            linLayoutKitname;
    private ImageView ivSortBrand, ivSortScale, ivSortDate, ivSortKitname;
    View view;
    public static String fantasyTag;


    ListView lvKits;
    AdapterListGlide lgAdapter;
    private Context mContext;
    String[] filters;

    public SortFantasyFragment() {
    }

    public static SortFantasyFragment newInstance() {
        return new SortFantasyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        onAttachToParentFragment(getParentFragment());
//        setRetainInstance(false);//if set, won't be updated
//        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        filters = new String[0];
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        cursor = dbConnector.filteredKits(filters, "_id DESC", categoryTab);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
// открываем подключение к БД
//        dbConnector = new DbConnector(getActivity());
//        dbConnector.open();
        view = inflater.inflate(R.layout.fragment_sort_all, container, false);
        fantasyTag = this.getTag();


//        cursor = dbConnector.filteredKits(filters, "_id DESC");
        lgAdapter = new AdapterListGlide(mContext, cursor);

        //переключение лэйаутов

        if (getScreenOrientation().equals("portrait")){
            initPortraitUi();
        }else {
            initLandscapeUi();
        }

        ibtnFilter.setBackgroundColor(Color.TRANSPARENT);

        lvKits = (ListView)view.findViewById(R.id.lvKits);
        lvKits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getParentFragment().getActivity(), KitActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("id", id);
                intent.putExtra("category", categoryTab);
                intent.putExtra("tag", fantasyTag);
//                intent.putExtra("description", description);
//                intent.putExtra("year", year);
                getActivity().startActivityForResult(intent, REQUEST_CODE_POSITION);
            }
        });
        prepareListAndAdapter(cursor);
        returnToList();

        setActive(R.id.linLayoutSortDate, ivSortDate);
        sortDate = true;
        sortName = true;
        sortScale = true;
        sortBrand = true;


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
                cursor = dbConnector.filteredKits(filters, "_id DESC", categoryTab);
                prepareListAndAdapter(cursor);
                Toast.makeText(mContext, R.string.Filters_disabled, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return view;
    }

    public void returnToList(){
        Bundle bundle = getParentFragment().getArguments();
        if (bundle != null) {
            cursor = dbConnector.filteredKits(filters, "_id DESC", categoryTab);
//            long returnItemId = bundle.getLong("id");
            int returnItem = bundle.getInt("position");
            prepareListAndAdapter(cursor);
            lvKits.setSelectionFromTop(returnItem, 0); //todo нужно ди возвращаться на позицию?
        }
    }

    //Подготовка списка брэндов и адаптера
    public void prepareListAndAdapter(Cursor cursor){
        lgAdapter = new AdapterListGlide(mContext, cursor);
        lvKits.setAdapter(lgAdapter);

    }

    private void setActive(int  linLayout, ImageView arrow){
        linLayoutScale.setBackgroundColor(Color.TRANSPARENT);
        linLayoutBrand.setBackgroundColor(Color.TRANSPARENT);
        linLayoutDate.setBackgroundColor(Color.TRANSPARENT);
        linLayoutKitname.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout activeLayout = (LinearLayout)view.findViewById(linLayout);
        activeLayout.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorAccent));

        ivSortBrand.setVisibility(View.INVISIBLE);
        ivSortKitname.setVisibility(View.INVISIBLE);
        ivSortScale.setVisibility(View.INVISIBLE);
        ivSortDate.setVisibility(View.INVISIBLE);
        arrow.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Кнопки сортировки списка
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
        linLayoutBrand = (LinearLayout)view.findViewById(R.id.linLayoutSortBrand);
        linLayoutBrand.setOnClickListener(this);
        linLayoutScale = (LinearLayout)view.findViewById(R.id.linLayoutSortScale);
        linLayoutScale.setOnClickListener(this);
        linLayoutDate = (LinearLayout)view.findViewById(R.id.linLayoutSortDate);
        linLayoutDate.setOnClickListener(this);
        linLayoutKitname = (LinearLayout)view.findViewById(R.id.linLayoutSortKitname);
        linLayoutKitname.setOnClickListener(this);

        ivSortBrand = (ImageView)view.findViewById(R.id.ivSortBrand);
        ivSortBrand.setVisibility(View.INVISIBLE);
        ivSortDate = (ImageView)view.findViewById(R.id.ivSortDate);
        ivSortDate.setVisibility(View.INVISIBLE);
        ivSortScale = (ImageView)view.findViewById(R.id.ivSortScale);
        ivSortScale.setVisibility(View.INVISIBLE);
        ivSortKitname = (ImageView)view.findViewById(R.id.ivSortKitname);
        ivSortKitname.setVisibility(View.INVISIBLE);

        ibtnFilter = (ImageButton)view.findViewById(R.id.ibtnFilter);
        ibtnFilter.setBackgroundColor(Color.TRANSPARENT);

    }

    private void initLandscapeUi(){
        linLayoutBrand = (LinearLayout)view.findViewById(R.id.linLayoutSortBrand);
        linLayoutBrand.setOnClickListener(this);
        linLayoutScale = (LinearLayout)view.findViewById(R.id.linLayoutSortScale);
        linLayoutScale.setOnClickListener(this);
        linLayoutDate = (LinearLayout)view.findViewById(R.id.linLayoutSortDate);
        linLayoutDate.setOnClickListener(this);
        linLayoutKitname = (LinearLayout)view.findViewById(R.id.linLayoutSortKitname);
        linLayoutKitname.setOnClickListener(this);

        ivSortBrand = (ImageView)view.findViewById(R.id.ivSortBrand);
        ivSortBrand.setVisibility(View.INVISIBLE);
        ivSortDate = (ImageView)view.findViewById(R.id.ivSortDate);
        ivSortDate.setVisibility(View.INVISIBLE);
        ivSortScale = (ImageView)view.findViewById(R.id.ivSortScale);
        ivSortScale.setVisibility(View.INVISIBLE);
        ivSortKitname = (ImageView)view.findViewById(R.id.ivSortKitname);
        ivSortKitname.setVisibility(View.INVISIBLE);

        ibtnFilter = (ImageButton)view.findViewById(R.id.ibtnFilter);
        ibtnFilter.setBackgroundColor(Color.TRANSPARENT);
    }

    private String getScreenOrientation(){
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270){
            return "landscape";
        }else{
            return "portrait";
        }
    }

    @Override
    public void SortByBrandAsc() {
        cursor = dbConnector.filteredKits(filters, "brand", categoryTab);
        prepareListAndAdapter(cursor);
        ivSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        sortBrand = true;
    }

    @Override
    public void SortByBrandDesc() {
        cursor = dbConnector.filteredKits(filters, "brand DESC", categoryTab);
        prepareListAndAdapter(cursor);
        ivSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        sortBrand = false;

    }

    @Override
    public void SortByScaleAsc() {
        cursor = dbConnector.filteredKits(filters, "scale", categoryTab);
        prepareListAndAdapter(cursor);
        ivSortScale.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        sortScale = true;
    }

    @Override
    public void SortByScaleDesc() {
        cursor = dbConnector.filteredKits(filters, "scale DESC", categoryTab);
        prepareListAndAdapter(cursor);
        ivSortScale.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        sortScale = false;
    }

    @Override
    public void SortByDateAcs() {
        cursor = dbConnector.filteredKits(filters, "_id", categoryTab);
        prepareListAndAdapter(cursor);
        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        sortDate = true;
    }

    @Override
    public void SortByDateDesc() {
        cursor = dbConnector.filteredKits(filters, "_id DESC", categoryTab);
        prepareListAndAdapter(cursor);
        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        sortDate = false;
    }

    @Override
    public void SortByNameAsc() {
        cursor = dbConnector.filteredKits(filters, "kit_name", categoryTab);
        prepareListAndAdapter(cursor);
        ivSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        sortName = true;
    }

    @Override
    public void SortByNameDesc() {
        cursor = dbConnector.filteredKits(filters, "kit_name DESC", categoryTab);
        prepareListAndAdapter(cursor);
        ivSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        sortName = false;
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

    public void showFilterDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View dialogView = inflater.inflate(R.layout.alertdialog_filter, null);
        dialogBuilder.setView(dialogView);

        final CheckBox cbFilterScale = (CheckBox)dialogView.findViewById(R.id.cbScale);
        cbFilterScale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        final CheckBox cbFilterBrand = (CheckBox)dialogView.findViewById(R.id.cbBrand);
        cbFilterBrand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        final CheckBox cbFilterKitname = (CheckBox)dialogView.findViewById(R.id.cbKitname);
        cbFilterKitname.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        final Spinner spFilterScale = (Spinner)dialogView.findViewById(R.id.spFilterScale);
        ArrayList<String> scalesArray = dbConnector.getFilterData(DbConnector.COLUMN_SCALE);
        ArrayAdapter scalesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, scalesArray);
        spFilterScale.setAdapter(scalesAdapter);

        final Spinner spFilterBrand = (Spinner)dialogView.findViewById(R.id.spFilterBrands);
        ArrayList<String> brandsArray = dbConnector.getFilterData(DbConnector.COLUMN_BRAND);
        ArrayAdapter brandsAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, brandsArray);
        spFilterBrand.setAdapter(brandsAdapter);

        ArrayList<String> kitnamesArray = dbConnector.getFilterData(DbConnector.COLUMN_KIT_NAME);

        final AutoCompleteTextView acFilterKitname = (AutoCompleteTextView)dialogView
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

                if (cbFilterScale.isChecked()){
                    scaleFilter = spFilterScale.getSelectedItem().toString();
                }
                if (cbFilterBrand.isChecked()){
                    brandFilter = spFilterBrand.getSelectedItem().toString();
                }
                if (cbFilterKitname.isChecked()){
                    kitnameFilter = acFilterKitname.getText().toString().trim();
                }

                if (!(scaleFilter.equals("") && brandFilter.equals("") && kitnameFilter.equals(""))){
                    filters = new String[3];
                    filters[0] = scaleFilter;
                    filters[1] = brandFilter;
                    filters[2] = kitnameFilter;

                    cursor = dbConnector.filteredKits(filters, "_id DESC", categoryTab);
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
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}

//package com.example.kitstasher.fragment;
//
//import android.app.LoaderManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.Loader;
//import android.database.Cursor;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.Display;
//import android.view.LayoutInflater;
//import android.view.Surface;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//
//import com.example.kitstasher.R;
//import com.example.kitstasher.activity.KitActivity;
//import com.example.kitstasher.activity.MainActivity;
//import com.example.kitstasher.other.Constants;
//import com.example.kitstasher.adapters.AdapterListGlide;
//import com.example.kitstasher.other.DbConnector;
//import com.example.kitstasher.other.Helper;
//import com.example.kitstasher.other.SortKits;
//
//import static com.example.kitstasher.activity.MainActivity.REQUEST_CODE_POSITION;
//
///**
// * Created by Алексей on 26.08.2017.
// * View stash of AIR category
// */
//
//public class SortFantasyFragment extends Fragment implements SortKits, View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
//    DbConnector dbConnector;
//    Cursor cursor;
//
//    //Для списка сортировок
//    private boolean sortBrand, sortDate, sortScale, sortName;
//    final public int categoryTab = 7;
//    private LinearLayout linLayoutBrand, linLayoutScale, linLayoutDate,
//            linLayoutKitname;
//    private ImageView ivSortBrand, ivSortScale, ivSortDate, ivSortKitname;
//    View view;
//    public static String airTag;
//
//    ListView lvKits;
//    AdapterListGlide lgAdapter;
//
//    public SortFantasyFragment() {
//    }
//
//    public static SortFantasyFragment newInstance() {
//        SortFantasyFragment fragment = new SortFantasyFragment();
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//
//
//    @Override
//    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // открываем подключение к БД
//        dbConnector = new DbConnector(getActivity());
//        dbConnector.open();
//        view = inflater.inflate(R.layout.fragment_sort_all, container, false);
//        airTag = this.getTag();
//        cursor = dbConnector.getByCategory(Constants.CAT_FANTASY, "_id DESC");
//
//        //переключение лэйаутов
//
//        if (getScreenOrientation() == "portrait"){
//            initPortraitUi();
//        }else {
//            initLandscapeUi();
//        }
//
//        lvKits = (ListView)view.findViewById(R.id.lvKits);
//        lvKits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getParentFragment().getActivity(), KitActivity.class);
//                intent.putExtra(Constants.LIST_POSITION, position);
//                intent.putExtra(Constants.LIST_ID, id);
//                intent.putExtra(Constants.LIST_CATEGORY, categoryTab);
//                intent.putExtra(Constants.LIST_TAG, airTag);
//                getActivity().startActivityForResult(intent, REQUEST_CODE_POSITION);
//            }
//        });
//        prepareListAndAdapter(cursor);
//        returnToList();
//
//        setActive(R.id.linLayoutSortDate, ivSortDate);
//        sortDate = true;
//        sortName = true;
//        sortScale = true;
//        sortBrand = true;
//
//        return view;
//    }
//
//
//    public void returnToList(){
//        Bundle bundle = this.getArguments();
//        if (bundle != null) {
//            long returnItemId = bundle.getLong(Constants.LIST_ID);
//            int returnItem = bundle.getInt(Constants.LIST_POSITION);
//            prepareListAndAdapter(cursor);
//            lvKits.setSelectionFromTop(returnItem, 0); //todo нужно ди возвращаться на позицию?
//        }
//    }
//
////    @Override
////    public void onAttach(Context context) {
////        super.onAttach(context);
////        dbConnector = new DbConnector(getActivity());
////        dbConnector.open();
////        cursor = dbConnector.getByCategory(Constants.CAT_AIR, "_id");
////        initPortraitUi();
////        prepareListAndAdapter(cursor);
////    }
//
//
////    @Override
////    public void onResume() {
////        super.onResume();
////        prepareListAndAdapter(cursor);
////    }
//
//    //Подготовка списка брэндов и адаптера
//    public void prepareListAndAdapter(Cursor cursor){
//        lgAdapter = new AdapterListGlide(getActivity(), cursor);
//        lvKits.setAdapter(lgAdapter);
//    }
//
//    private void setActive(int  linLayout, ImageView arrow){
//        linLayoutScale.setBackgroundColor(Color.TRANSPARENT);
//        linLayoutBrand.setBackgroundColor(Color.TRANSPARENT);
//        linLayoutDate.setBackgroundColor(Color.TRANSPARENT);
//        linLayoutKitname.setBackgroundColor(Color.TRANSPARENT);
//        LinearLayout activeLayout = (LinearLayout)view.findViewById(linLayout);
//        activeLayout.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorAccent));
//
//        ivSortBrand.setVisibility(View.INVISIBLE);
//        ivSortKitname.setVisibility(View.INVISIBLE);
//        ivSortScale.setVisibility(View.INVISIBLE);
//        ivSortDate.setVisibility(View.INVISIBLE);
//        arrow.setVisibility(View.VISIBLE);
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            //Кнопки сортировки списка
//            case R.id.linLayoutSortBrand:
//                setActive(R.id.linLayoutSortBrand, ivSortBrand);
//
//                if (sortBrand){
//                    SortByBrandAsc();
//                    sortBrand = false;
//                }else {
//                    SortByBrandDesc();
//                    sortBrand = true;
//                }
//                sortDate = true;
//                sortScale = true;
//                sortName = true;
//                break;
//
//            case R.id.linLayoutSortScale:
//                setActive(R.id.linLayoutSortScale, ivSortScale);
////                linLayoutScale.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorAccent));
////                linLayoutBrand.setBackgroundColor(Color.TRANSPARENT);
////                linLayoutDate.setBackgroundColor(Color.TRANSPARENT);
////                linLayoutKitname.setBackgroundColor(Color.TRANSPARENT);
////
////                ivSortBrand.setVisibility(View.INVISIBLE);
////                ivSortKitname.setVisibility(View.INVISIBLE);
////                ivSortScale.setVisibility(View.VISIBLE);
////                ivSortDate.setVisibility(View.INVISIBLE);
//                if (sortScale){
//                    SortByScaleAsc();
//                    sortScale = false;
//                }else {
//                    SortByScaleDesc();
//                    sortScale = true;
//                }
//                sortBrand = true;
//                sortDate = true;
//                sortName = true;
//                break;
//
//            case R.id.linLayoutSortDate:
//                setActive(R.id.linLayoutSortDate, ivSortDate);
////                linLayoutScale.setBackgroundColor(Color.TRANSPARENT);
////                linLayoutBrand.setBackgroundColor(Color.TRANSPARENT);
////                linLayoutDate.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorAccent));
////                linLayoutKitname.setBackgroundColor(Color.TRANSPARENT);
////
////                ivSortBrand.setVisibility(View.INVISIBLE);
////                ivSortKitname.setVisibility(View.INVISIBLE);
////                ivSortScale.setVisibility(View.INVISIBLE);
////                ivSortDate.setVisibility(View.VISIBLE);
//
//
//                if (sortDate){
//                    SortByDateAcs();
//                    sortDate = false;
//                }else {
//                    SortByDateDesc();
//                    sortDate = true;
//                }
//                sortBrand = true;
//                sortScale = true;
//                sortName = true;
//
//                break;
//
//            case R.id.linLayoutSortKitname:
//                setActive(R.id.linLayoutSortKitname, ivSortKitname);
////                linLayoutScale.setBackgroundColor(Color.TRANSPARENT);
////                linLayoutBrand.setBackgroundColor(Color.TRANSPARENT);
////                linLayoutDate.setBackgroundColor(Color.TRANSPARENT);
////                linLayoutKitname.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorAccent));
////
////                ivSortBrand.setVisibility(View.INVISIBLE);
////                ivSortKitname.setVisibility(View.VISIBLE);
////                ivSortScale.setVisibility(View.INVISIBLE);
////                ivSortDate.setVisibility(View.INVISIBLE);
//
//
//
//                if (sortName){
//                    SortByNameAsc();
//                    sortName = false;
//                }else {
//                    SortByNameDesc();
//                    sortName = true;
//                }
//                sortBrand = true;
//                sortDate = true;
//                sortScale = true;
//                break;
//        }
//    }
//
//    private void initPortraitUi(){
//        linLayoutBrand = (LinearLayout)view.findViewById(R.id.linLayoutSortBrand);
//        linLayoutBrand.setOnClickListener(this);
//        linLayoutScale = (LinearLayout)view.findViewById(R.id.linLayoutSortScale);
//        linLayoutScale.setOnClickListener(this);
//        linLayoutDate = (LinearLayout)view.findViewById(R.id.linLayoutSortDate);
//        linLayoutDate.setOnClickListener(this);
//        linLayoutKitname = (LinearLayout)view.findViewById(R.id.linLayoutSortKitname);
//        linLayoutKitname.setOnClickListener(this);
//
//        ivSortBrand = (ImageView)view.findViewById(R.id.ivSortBrand);
//        ivSortBrand.setVisibility(View.INVISIBLE);
//        ivSortDate = (ImageView)view.findViewById(R.id.ivSortDate);
//        ivSortDate.setVisibility(View.INVISIBLE);
//        ivSortScale = (ImageView)view.findViewById(R.id.ivSortScale);
//        ivSortScale.setVisibility(View.INVISIBLE);
//        ivSortKitname = (ImageView)view.findViewById(R.id.ivSortKitname);
//        ivSortKitname.setVisibility(View.INVISIBLE);
//    }
//
//    private void initLandscapeUi(){
//        linLayoutBrand = (LinearLayout)view.findViewById(R.id.linLayoutSortBrand);
//        linLayoutBrand.setOnClickListener(this);
//        linLayoutScale = (LinearLayout)view.findViewById(R.id.linLayoutSortScale);
//        linLayoutScale.setOnClickListener(this);
//        linLayoutDate = (LinearLayout)view.findViewById(R.id.linLayoutSortDate);
//        linLayoutDate.setOnClickListener(this);
//        linLayoutKitname = (LinearLayout)view.findViewById(R.id.linLayoutSortKitname);
//        linLayoutKitname.setOnClickListener(this);
//
//        ivSortBrand = (ImageView)view.findViewById(R.id.ivSortBrand);
//        ivSortBrand.setVisibility(View.INVISIBLE);
//        ivSortDate = (ImageView)view.findViewById(R.id.ivSortDate);
//        ivSortDate.setVisibility(View.INVISIBLE);
//        ivSortScale = (ImageView)view.findViewById(R.id.ivSortScale);
//        ivSortScale.setVisibility(View.INVISIBLE);
//        ivSortKitname = (ImageView)view.findViewById(R.id.ivSortKitname);
//        ivSortKitname.setVisibility(View.INVISIBLE);
//    }
//
//    private String getScreenOrientation(){
//        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        int rotation = display.getRotation();
//        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270){
//            return "landscape";
//        }else{
//            return "portrait";
//        }
//    }
//
//    @Override
//    public void SortByBrandAsc() {
//        cursor = dbConnector.getByCategory(MainActivity.CAT_AIR, "brand");
//        prepareListAndAdapter(cursor);
//        ivSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
//        sortBrand = true;
//    }
//
//    @Override
//    public void SortByBrandDesc() {
//        cursor = dbConnector.getByCategory(MainActivity.CAT_AIR, "brand DESC");
//        prepareListAndAdapter(cursor);
//        ivSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
//        sortBrand = false;
//
//    }
//
//    @Override
//    public void SortByScaleAsc() {
//        cursor = dbConnector.getByCategory(MainActivity.CAT_AIR, "scale");
//        prepareListAndAdapter(cursor);
//        ivSortScale.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
//        sortScale = true;
//    }
//
//    @Override
//    public void SortByScaleDesc() {
//        cursor = dbConnector.getByCategory(MainActivity.CAT_AIR, "scale DESC");
//        prepareListAndAdapter(cursor);
//        ivSortScale.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
//        sortScale = false;
//    }
//
//    @Override
//    public void SortByDateAcs() {
//        cursor = dbConnector.getByCategory(MainActivity.CAT_AIR, "_id");
//        prepareListAndAdapter(cursor);
//        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
//        sortDate = true;
//    }
//
//    @Override
//    public void SortByDateDesc() {
//        cursor = dbConnector.getByCategory(MainActivity.CAT_AIR, "_id DESC");
//        prepareListAndAdapter(cursor);
//        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
//        sortDate = false;
//    }
//
//    @Override
//    public void SortByNameAsc() {
//        cursor = dbConnector.getByCategory(MainActivity.CAT_AIR, "kit_name");
//        prepareListAndAdapter(cursor);
//        ivSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
//        sortName = true;
//    }
//
//    @Override
//    public void SortByNameDesc() {
//        cursor = dbConnector.getByCategory(MainActivity.CAT_AIR, "kit_name DESC");
//        prepareListAndAdapter(cursor);
//        ivSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
//        sortName = false;
//    }
//
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        return null;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }
//}