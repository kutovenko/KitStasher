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
import android.support.v7.widget.AppCompatSpinner;
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
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.SortKits;

import java.util.ArrayList;

import static com.example.kitstasher.activity.MainActivity.REQUEST_CODE_POSITION;

/**
 * Created by Алексей on 22.04.2017.
 * Loads and manages kits data
 */

public class SortAllFragment extends Fragment implements SortKits, View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, TextWatcher {
    DbConnector dbConnector;
    Cursor cursor;

    private ImageButton ibtnFilter;
    //Для списка сортировок
    private boolean sortBrand, sortDate, sortScale, sortName;
    final public int categoryTab = 0;
    private LinearLayout linLayoutBrand, linLayoutScale, linLayoutDate,
            linLayoutKitname;
    private ImageView ivSortBrand, ivSortScale, ivSortDate, ivSortKitname;
    View view;
    public static String allTag;


    ListView lvKits;
    AdapterListGlide lgAdapter;
    private Context mContext;
    String[] filters;

    public SortAllFragment() {
    }

    public static SortAllFragment newInstance() {
        return new SortAllFragment();
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
        cursor = dbConnector.filteredKits(DbConnector.TABLE_KITS, filters, "_id DESC", categoryTab);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
// открываем подключение к БД
//        dbConnector = new DbConnector(getActivity());
//        dbConnector.open();
        view = inflater.inflate(R.layout.fragment_sort_all, container, false);
        allTag = this.getTag();


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
                intent.putExtra("tag", allTag);
                if (filters.length < 1){
                    filters = new String[5];
                }
                intent.putExtra("scaleFilter", filters[0]);
                intent.putExtra("brandFilter", filters[1]);
                intent.putExtra("kitnameFilter", filters[2]);
                intent.putExtra("statusFilter", filters[3]);
                intent.putExtra("mediaFilter", filters[4]);
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
                cursor = dbConnector.filteredKits(DbConnector.TABLE_KITS, filters, "_id DESC", categoryTab);
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
            if (bundle.getString("scaleFilter") != null) {
                filters = new String[5];
                filters[0] = bundle.getString("scaleFilter");
                filters[1] = bundle.getString("brandFilter");
                filters[2] = bundle.getString("kitnameFilter");
                filters[3] = bundle.getString("statusFilter");
                filters[4] = bundle.getString("mediaFilter");
                ibtnFilter.setBackgroundColor(Helper.getColor(getActivity(), R.color.colorAccent));
            }
            cursor = dbConnector.filteredKits(DbConnector.TABLE_KITS, filters, "_id DESC", categoryTab);
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
        cursor = dbConnector.filteredKits(DbConnector.TABLE_KITS, filters, "brand", categoryTab);
        prepareListAndAdapter(cursor);
        ivSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        sortBrand = true;
    }

    @Override
    public void SortByBrandDesc() {
        cursor = dbConnector.filteredKits(DbConnector.TABLE_KITS, filters, "brand DESC", categoryTab);
        prepareListAndAdapter(cursor);
        ivSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        sortBrand = false;

    }

    @Override
    public void SortByScaleAsc() {
        cursor = dbConnector.filteredKits(DbConnector.TABLE_KITS, filters, "scale", categoryTab);
        prepareListAndAdapter(cursor);
        ivSortScale.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        sortScale = true;
    }

    @Override
    public void SortByScaleDesc() {
        cursor = dbConnector.filteredKits(DbConnector.TABLE_KITS, filters, "scale DESC", categoryTab);
        prepareListAndAdapter(cursor);
        ivSortScale.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        sortScale = false;
    }

    @Override
    public void SortByDateAcs() {
        cursor = dbConnector.filteredKits(DbConnector.TABLE_KITS, filters, "_id", categoryTab);
        prepareListAndAdapter(cursor);
        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        sortDate = true;
    }

    @Override
    public void SortByDateDesc() {
        cursor = dbConnector.filteredKits(DbConnector.TABLE_KITS, filters, "_id DESC", categoryTab);
        prepareListAndAdapter(cursor);
        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        sortDate = false;
    }

    @Override
    public void SortByNameAsc() {
        cursor = dbConnector.filteredKits(DbConnector.TABLE_KITS, filters, "kit_name", categoryTab);
        prepareListAndAdapter(cursor);
        ivSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        sortName = true;
    }

    @Override
    public void SortByNameDesc() {
        cursor = dbConnector.filteredKits(DbConnector.TABLE_KITS, filters, "kit_name DESC", categoryTab);
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

        final CheckBox cbFilterStatus = (CheckBox)dialogView.findViewById(R.id.cbStatus);
        cbFilterStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        final CheckBox cbFilterMedia = (CheckBox)dialogView.findViewById(R.id.cbMedia);
        cbFilterMedia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

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


        final Spinner spFilterStatus = (Spinner)dialogView.findViewById(R.id.spStatus);
        ArrayList<String> statusArray = dbConnector.getFilterFromIntData(DbConnector.TABLE_KITS, DbConnector.COLUMN_STATUS);
        ArrayAdapter statusAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, statusArray);
        spFilterStatus.setAdapter(statusAdapter);

        final Spinner spFilterMedia = (Spinner) dialogView.findViewById(R.id.spMedia);
        ArrayList<String> mediaArray = dbConnector.getFilterFromIntData(DbConnector.TABLE_KITS, DbConnector.COLUMN_MEDIA);
        ArrayAdapter mediaAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, mediaArray);
        spFilterMedia.setAdapter(mediaAdapter);

        final Spinner spFilterScale = (Spinner)dialogView.findViewById(R.id.spFilterScale);
        ArrayList<String> scalesArray = dbConnector.getFilterData(DbConnector.TABLE_KITS, DbConnector.COLUMN_SCALE);
        ArrayAdapter scalesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, scalesArray);
        spFilterScale.setAdapter(scalesAdapter);

        final Spinner spFilterBrand = (Spinner)dialogView.findViewById(R.id.spFilterBrands);
        ArrayList<String> brandsArray = dbConnector.getFilterData(DbConnector.TABLE_KITS, DbConnector.COLUMN_BRAND);
        ArrayAdapter brandsAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, brandsArray);
        spFilterBrand.setAdapter(brandsAdapter);


        ArrayList<String> kitnamesArray = dbConnector.getFilterData(DbConnector.TABLE_KITS, DbConnector.COLUMN_KIT_NAME);

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

                    cursor = dbConnector.filteredKits(DbConnector.TABLE_KITS, filters, "_id DESC", categoryTab);
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
