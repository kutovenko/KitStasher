package com.example.kitstasher.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.adapters.AdapterChooserList;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.Constants;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;

import java.util.ArrayList;
import java.util.List;


public class ChooserActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    public static List<String> choosedIds;
    private ListView stashList;
    private DbConnector dbConnector;
    private Cursor cursor;
    private String listname;
    private char editMode;
    private String tableMode;
    private long kitId;
    
    private boolean sortDate, sortName, sortScale, sortBrand;
    private ImageButton ibtnChooseFilter;
    private LinearLayout linLayoutBrand, linLayoutScale, linLayoutDate,
            linLayoutKitname;
    private ImageView ivSortBrand, ivSortScale, ivSortDate, ivSortKitname;
    private String[] filters;
    private int categoryTab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        listname = getIntent().getExtras().getString("listname");

        initVariables();
        initUI();

        if (editMode == Constants.MODE_AFTERMARKET) {
            cursor = dbConnector.getAllAftermarket("_id DESC");
        } else if (editMode == Constants.MODE_KIT || editMode == Constants.MODE_LIST) {
            cursor = dbConnector.getAllData("_id DESC");
        }

        stashList = findViewById(R.id.lvChooser);
//        AdapterChooserList stashListAdapter =
//                new AdapterChooserList(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, editMode);
//        stashList.setAdapter(stashListAdapter);
        prepareListAndAdapter(cursor);

        setActive(R.id.linLayoutChooseSortDate, ivSortDate);
        sortDate = true;
        sortName = true;
        sortScale = true;
        sortBrand = true;


        ibtnChooseFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });

        ibtnChooseFilter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                filters = new String[0];
                ibtnChooseFilter.setBackgroundColor(Color.TRANSPARENT);
                cursor = dbConnector.filteredKits(tableMode, filters, "_id DESC", categoryTab, listname);
                prepareListAndAdapter(cursor);
                Toast.makeText(ChooserActivity.this, R.string.Filters_disabled, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        Button doneButton = findViewById(R.id.btnChooseDone);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editMode == Constants.MODE_KIT) {
                    if (choosedIds != null && !choosedIds.isEmpty()) {
                        for (int i = 0; i < choosedIds.size(); i++) {
                            long l = Long.valueOf(choosedIds.get(i));
                            Cursor c = dbConnector.getRecById(l);
                            copyKit(c);
                        }
                    } else {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        if (choosedIds != null && !choosedIds.isEmpty()) {
                            choosedIds.clear();
                        }
                        finish();
                    }
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    if (choosedIds != null && !choosedIds.isEmpty()) {
                        choosedIds.clear();
                    }
                    finish();
                } else if (editMode == Constants.MODE_AFTER_KIT) {
                    if (choosedIds != null && !choosedIds.isEmpty()) {
                        for (int i = 0; i < choosedIds.size(); i++) {
                            long afterId = Long.valueOf(choosedIds.get(i));
                            dbConnector.addAfterToKit(kitId, afterId);
                        }
                    } else {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        if (choosedIds != null && !choosedIds.isEmpty()) {
                            choosedIds.clear();
                        }
                        finish();
                    }
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    if (choosedIds != null && !choosedIds.isEmpty()) {
                        choosedIds.clear();
                    }
                    finish();
                }
            }
        });

        Button cancelButton = findViewById(R.id.btnChooseCancelled);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                if (choosedIds != null && !choosedIds.isEmpty()) {
                    choosedIds.clear();
                }
                finish();
            }
        });
    }



    private void initUI() {
        ibtnChooseFilter = (ImageButton)findViewById(R.id.ibtnChooseFilter);

        linLayoutBrand = (LinearLayout)findViewById(R.id.linLayoutChooseSortBrand);
        linLayoutBrand.setOnClickListener(this);
        linLayoutScale = (LinearLayout)findViewById(R.id.linLayoutChooseSortScale);
        linLayoutScale.setOnClickListener(this);
        linLayoutDate = (LinearLayout)findViewById(R.id.linLayoutChooseSortDate);
        linLayoutDate.setOnClickListener(this);
        linLayoutKitname = (LinearLayout)findViewById(R.id.linLayoutChooseSortKitname);
        linLayoutKitname.setOnClickListener(this);
        ivSortBrand = (ImageView)findViewById(R.id.ivChooseSortBrand);
        ivSortScale = (ImageView)findViewById(R.id.ivChooseSortScale);
        ivSortDate = (ImageView)findViewById(R.id.ivChooseSortDate);
        ivSortKitname = (ImageView)findViewById(R.id.ivChooseSortKitname);
    }

    private void initVariables() {
        dbConnector = new DbConnector(this);
        dbConnector.open();
        editMode = getIntent().getExtras().getChar(Constants.EDIT_MODE);
        if (editMode == Constants.MODE_KIT) {
            tableMode = DbConnector.TABLE_KITS;
        } else if (editMode == Constants.MODE_AFTERMARKET) {
            tableMode = DbConnector.TABLE_AFTERMARKET;
            kitId = getIntent().getExtras().getLong("kitId");
        }
        filters = new String[]{"","","","",""}; //todo уточнить количество
    }

    private void setActive(int  linLayout, ImageView arrow){
        linLayoutScale.setBackgroundColor(Color.TRANSPARENT);
        linLayoutBrand.setBackgroundColor(Color.TRANSPARENT);
        linLayoutDate.setBackgroundColor(Color.TRANSPARENT);
        linLayoutKitname.setBackgroundColor(Color.TRANSPARENT);
        LinearLayout activeLayout = (LinearLayout)findViewById(linLayout);
        activeLayout.setBackgroundColor(Helper.getColor(ChooserActivity.this, R.color.colorAccent));

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
            case R.id.linLayoutChooseSortBrand:
                setActive(R.id.linLayoutChooseSortBrand, ivSortBrand);

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

            case R.id.linLayoutChooseSortScale:
                setActive(R.id.linLayoutChooseSortScale, ivSortScale);

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

            case R.id.linLayoutChooseSortDate:
                setActive(R.id.linLayoutChooseSortDate, ivSortDate);
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

            case R.id.linLayoutChooseSortKitname:
                setActive(R.id.linLayoutChooseSortKitname, ivSortKitname);
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

    public void prepareListAndAdapter(Cursor cursor){
        AdapterChooserList stashListAdapter =
                new AdapterChooserList(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, editMode);
        stashList.setAdapter(stashListAdapter);

    }


    private void copyKit(Cursor c) {
        c.moveToFirst();
            String boxart_url = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URL));
            String boxart_uri = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_BOXART_URI));
            String brand = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND));
            String cat_no = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_BRAND_CATNO));
            String name = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_KIT_NAME));
            int scale = c.getInt(c.getColumnIndexOrThrow(DbConnector.COLUMN_SCALE));
            String category = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_CATEGORY));
            String year = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_YEAR));
            String description = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_DESCRIPTION));
        String kit_noengname = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_ORIGINAL_NAME));
            String barcode = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_BARCODE));
            String status = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_SEND_STATUS));
            String date = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_DATE));

        String onlineId = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_ID_ONLINE));

        String notes = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_NOTES));
        String purchaseDate = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_PURCHASE_DATE));
        String currency = c.getString(c.getColumnIndexOrThrow(DbConnector.COLUMN_CURRENCY));
        int quantity = c.getInt(c.getColumnIndexOrThrow(DbConnector.COLUMN_QUANTITY));
        int price = c.getInt(c.getColumnIndexOrThrow(DbConnector.COLUMN_PRICE));


        Kit kitToAdd = new Kit.KitBuilder()
                .hasBrand(brand)
                .hasBrand_catno(cat_no)
                .hasKit_name(name)
                .hasScale(scale)
                .hasCategory(category)
                .hasBarcode(barcode)
                .hasKit_noeng_name(kit_noengname)
                .hasDescription(description)

                .hasPrototype("")//not in use

                .hasBoxart_url(boxart_url)
                .hasBoxart_uri(boxart_uri)
                .hasScalemates_url("")
                .hasYear(year)
                .hasOnlineId(onlineId)
                .hasDateAdded(date)
                .hasDatePurchased(purchaseDate)
                .hasQuantity(quantity)
                .hasNotes(notes)
                .hasPrice(price)
                .hasCurrency(currency)
                .build();

        if (!dbConnector.searchListForDoubles(listname, brand, cat_no)) {
            dbConnector.addListItem(kitToAdd, listname);
        }
    }

    public void SortByBrandAsc() {
        cursor = dbConnector.filteredKits(tableMode, filters, "brand", categoryTab, listname);
        prepareListAndAdapter(cursor);
        ivSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortBrand = true;
    }

    public void SortByBrandDesc() {
        cursor = dbConnector.filteredKits(tableMode, filters, "brand DESC", categoryTab, listname);
        prepareListAndAdapter(cursor);
        ivSortBrand.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortBrand = false;

    }

    public void SortByScaleAsc() {
        cursor = dbConnector.filteredKits(tableMode, filters, "scale", categoryTab, listname);
        prepareListAndAdapter(cursor);
        ivSortScale.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortScale = true;
    }

    public void SortByScaleDesc() {
        cursor = dbConnector.filteredKits(tableMode, filters, "scale DESC", categoryTab, listname);
        prepareListAndAdapter(cursor);
        ivSortScale.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortScale = false;
    }

    public void SortByDateAcs() {
        cursor = dbConnector.filteredKits(tableMode, filters, "_id", categoryTab, listname);
        prepareListAndAdapter(cursor);
        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortDate = true;
    }

    public void SortByDateDesc() {
        cursor = dbConnector.filteredKits(tableMode, filters, "_id DESC", categoryTab, listname);
        prepareListAndAdapter(cursor);
        ivSortDate.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortDate = false;
    }

    public void SortByNameAsc() {
        cursor = dbConnector.filteredKits(tableMode, filters, "kit_name", categoryTab, listname);
        prepareListAndAdapter(cursor);
        ivSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        sortName = true;
    }

    public void SortByNameDesc() {
        cursor = dbConnector.filteredKits(tableMode, filters, "kit_name DESC", categoryTab, listname);
        prepareListAndAdapter(cursor);
        ivSortKitname.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        sortName = false;
    }

    public void showFilterDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ChooserActivity.this);
        LayoutInflater inflater = LayoutInflater.from(ChooserActivity.this);
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
        ArrayList<String> statusArray = dbConnector.getFilterFromIntData(tableMode, DbConnector.COLUMN_STATUS);
        ArrayAdapter statusAdapter = new ArrayAdapter<>(ChooserActivity.this,
                android.R.layout.simple_spinner_item, statusArray);
        spFilterStatus.setAdapter(statusAdapter);

        final Spinner spFilterMedia = (Spinner) dialogView.findViewById(R.id.spMedia);
        ArrayList<String> mediaArray = dbConnector.getFilterFromIntData(tableMode, DbConnector.COLUMN_MEDIA);
        ArrayAdapter mediaAdapter = new ArrayAdapter<>(ChooserActivity.this,
                android.R.layout.simple_spinner_item, mediaArray);
        spFilterMedia.setAdapter(mediaAdapter);

        final Spinner spFilterScale = (Spinner)dialogView.findViewById(R.id.spFilterScale);
        ArrayList<String> scalesArray = dbConnector.getFilterData(tableMode, DbConnector.COLUMN_SCALE);
        ArrayAdapter scalesAdapter = new ArrayAdapter<>(ChooserActivity.this,
                android.R.layout.simple_spinner_item, scalesArray);
        spFilterScale.setAdapter(scalesAdapter);

        final Spinner spFilterBrand = (Spinner)dialogView.findViewById(R.id.spFilterBrands);
        ArrayList<String> brandsArray = dbConnector.getFilterData(tableMode, DbConnector.COLUMN_BRAND);
        ArrayAdapter brandsAdapter = new ArrayAdapter<>(ChooserActivity.this,
                android.R.layout.simple_spinner_item, brandsArray);
        spFilterBrand.setAdapter(brandsAdapter);


        ArrayList<String> afternamesArray = dbConnector.getFilterData(tableMode, DbConnector.COLUMN_KIT_NAME);

        final AutoCompleteTextView acFilterKitname = (AutoCompleteTextView)dialogView
                .findViewById(R.id.acFilterKitname);
        ArrayAdapter acFilterKitnameAdapter = new ArrayAdapter<>(ChooserActivity.this,
                android.R.layout.simple_dropdown_item_1line, afternamesArray);
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
                    statusFilter = String.valueOf(spFilterStatus.getSelectedItemPosition());
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

                    cursor = dbConnector.filteredKits(tableMode, filters, "_id DESC", categoryTab, listname);
                    prepareListAndAdapter(cursor);

                    ibtnChooseFilter.setBackgroundColor(Helper.getColor(ChooserActivity.this, R.color.colorAccent));
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
