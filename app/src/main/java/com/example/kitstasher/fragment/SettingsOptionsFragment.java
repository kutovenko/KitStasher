package com.example.kitstasher.fragment;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kitstasher.R;
import com.example.kitstasher.objects.Kit;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.Helper;
import com.example.kitstasher.other.MyConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Алексей on 03.05.2017.
 * Import and export database. Repair of boxart image links.
 */

public class SettingsOptionsFragment extends Fragment implements View.OnClickListener {
    private DbConnector dbConnector;
    private View view;
    private EditText etNewCurrency;
    private ProgressBar progressBarDb;
    private Spinner spDefaultCurrency;

    public SettingsOptionsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_options, container, false);

        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        initUI();
        return view;
    }

    private void initUI() {
        progressBarDb = view.findViewById(R.id.pbOptionsDb);
        progressBarDb.setVisibility(View.GONE);
        Button btnSetDefault = view.findViewById(R.id.btnSetDefault);
        btnSetDefault.setOnClickListener(this);
        Button btnAddNewCurrency = view.findViewById(R.id.btnAddCurrency);
        btnAddNewCurrency.setOnClickListener(this);
        etNewCurrency = view.findViewById(R.id.etNewCurrency);
        spDefaultCurrency = view.findViewById(R.id.spDefaultCurrency);
        loadCurrencySpinner();
        Button btnRestoreFromCloud = view.findViewById(R.id.btnRestoreFromCloud);
        btnRestoreFromCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarDb.setVisibility(View.VISIBLE);
                restoreFromCloud();
            }
        });
        CheckBox cbCloudMode = view.findViewById(R.id.cbCloudMode);
        cbCloudMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(MyConstants.CLOUD_MODE, b).apply();
            }
        });
    }

    private void restoreFromCloud() {

        dbConnector.clearTable(DbConnector.TABLE_KITS);
        dbConnector.clearTable(DbConnector.TABLE_KIT_AFTER_CONNECTIONS);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String ownerId = sharedPreferences.getString(MyConstants.USER_ID_PARSE, "");
        ParseQuery<ParseObject> ownerIds = ParseQuery.getQuery("Stash");
        ownerIds.whereEqualTo(MyConstants.PARSE_OWNERID, ownerId);

        String idType = sharedPreferences.getString(MyConstants.USER_IDTYPE, "");//todo
        ParseQuery<ParseObject> idSelect = ParseQuery.getQuery("Stash");
        idSelect.whereEqualTo(MyConstants.PARSE_IDTYPE, idType);

        ParseQuery<ParseObject> notDeleted = ParseQuery.getQuery("Stash");
        notDeleted.whereNotEqualTo(MyConstants.PARSE_DELETED, true);

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(ownerIds);
        queries.add(idSelect);
        queries.add(notDeleted);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> results, ParseException e) {
                for (ParseObject object : results) {

                    Kit kit = new Kit.KitBuilder()
                            .hasLocalId(object.getLong(MyConstants.PARSE_LOCALID))
//                            .hasParentId()
                            .hasOnlineId(object.getObjectId())
                            .hasItemType(object.getString(MyConstants.PARSE_ITEMTYPE))

                            .hasBrand(object.getString(MyConstants.PARSE_BRAND))
                            .hasBrand_catno(object.getString(MyConstants.PARSE_BRAND_CATNO))
                            .hasKit_name(object.getString(MyConstants.PARSE_KITNAME))
                            .hasScale(object.getInt(MyConstants.PARSE_SCALE))
                            .hasCategory(object.getString(MyConstants.PARSE_CATEGORY))
                            .hasBarcode(object.getString(MyConstants.PARSE_BARCODE))
                            .hasKit_noeng_name(object.getString(MyConstants.PARSE_NOENGNAME))
                            .hasDescription(object.getString(MyConstants.PARSE_DESCRIPTION))
                            .hasYear(object.getString(MyConstants.PARSE_YEAR))
                            .hasPrototype(MyConstants.EMPTY)//not in use

                            .hasBoxart_url(object.getString(MyConstants.PARSE_BOXART_URL))
                            .hasScalemates_url(object.getString(MyConstants.PARSE_SCALEMATES))

                            .hasDateAdded(object.getString("createdAt"))
                            .hasDatePurchased(object.getString(MyConstants.PURCHASE_DATE))
                            .hasQuantity(object.getInt(MyConstants.QUANTITY))
                            .hasNotes(object.getString(MyConstants.NOTES))
                            .hasPrice(object.getInt(MyConstants.PRICE))
                            .hasCurrency(object.getString(MyConstants.CURRENCY))
                            .hasPlacePurchased(object.getString(MyConstants.PARSE_PURCHASE_PLACE))
                            .hasStatus(object.getInt(MyConstants.STATUS))
                            .hasMedia(object.getInt(MyConstants.MEDIA))



                            //                            .hasSendStatus(sendStatus)
                            //                            .hasBoxart_uri(boxartUri)
                            .build();

                    dbConnector.addKitRec(kit);
                }
                progressBarDb.setVisibility(View.GONE);
                Toast.makeText(getActivity(), getString(R.string.Restored), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAddCurrency:
                String cur = etNewCurrency.getText().toString();
                if (!Helper.isBlank(cur)) {
                    dbConnector.addCurrency(cur);
                    loadCurrencySpinner();
                    etNewCurrency.setText(MyConstants.EMPTY);
                }
                break;
            case R.id.btnSetDefault:
                setDefaultCurrency(spDefaultCurrency.getSelectedItem().toString());
                break;
        }
    }

    private void loadCurrencySpinner() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String defCurrency = sharedPreferences.getString(MyConstants.DEFAULT_CURRENCY, "USD");
        Cursor currCursor = dbConnector.getAllFromTable(DbConnector.TABLE_CURRENCIES,
                DbConnector.CURRENCIES_COLUMN_CURRENCY);
        currCursor.moveToFirst();
        String[] currencies = new String[currCursor.getCount()];
        for (int i = 0; i < currCursor.getCount(); i++) {
            currencies[i] = currCursor.getString(1);
            currCursor.moveToNext();
        }
        ArrayAdapter currencyAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, currencies);
        spDefaultCurrency.setAdapter(currencyAdapter);
        int spCurrencyPosition = currencyAdapter.getPosition(defCurrency);
        spDefaultCurrency.setSelection(spCurrencyPosition);
    }

    private void restoreShopsFromKits(){

    }

    private void restoreBrandsFromKit(){

    }

    private void setDefaultCurrency(String currency){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MyConstants.DEFAULT_CURRENCY, currency).apply();
    }

    private void setCloudMode(boolean b) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(MyConstants.CLOUD_MODE, b).apply();
    }

    private void setAllOptions(String currency, boolean cloudMode) {
        setDefaultCurrency(currency);
        setCloudMode(cloudMode);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbConnector.close();
    }
}