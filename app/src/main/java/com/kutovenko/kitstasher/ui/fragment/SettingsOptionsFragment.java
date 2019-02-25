package com.kutovenko.kitstasher.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.databinding.FragmentOptionsBinding;
import com.kutovenko.kitstasher.db.DbConnector;
import com.kutovenko.kitstasher.util.Helper;
import com.kutovenko.kitstasher.util.MyConstants;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

/**
 * Created by Алексей on 03.05.2017.
 * Import and export database. Repair of boxart image links.
 */

public class SettingsOptionsFragment extends Fragment implements View.OnClickListener {
    private DbConnector dbConnector;
    private FragmentOptionsBinding binding;
    private Context context;

    public SettingsOptionsFragment(){
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_options, container, false);

        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        context = getActivity();
        binding.btnSetDefault.setOnClickListener(this);
        binding.btnAddCurrency.setOnClickListener(this);
        loadCurrencySpinner();
        return binding.getRoot();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case com.kutovenko.kitstasher.R.id.btnAddCurrency:
                String cur = binding.etNewCurrency.getText().toString();
                if (!Helper.isBlank(cur)) {
                    dbConnector.addCurrency(cur);
                    loadCurrencySpinner();
                    binding.etNewCurrency.setText(MyConstants.EMPTY);
                }
                break;
            case com.kutovenko.kitstasher.R.id.btnSetDefault:
                setDefaultCurrency(binding.spDefaultCurrency.getSelectedItem().toString());
                break;
        }
    }

    private void loadCurrencySpinner() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String defCurrency = sharedPreferences.getString(MyConstants.DEFAULT_CURRENCY, "USD");
        String[] currencies = dbConnector.getCurrencies(DbConnector.TABLE_CURRENCIES,
                DbConnector.CURRENCIES_COLUMN_CURRENCY);
        ArrayAdapter currencyAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, currencies);
        binding.spDefaultCurrency.setAdapter(currencyAdapter);
        int spCurrencyPosition = currencyAdapter.getPosition(defCurrency);
        binding.spDefaultCurrency.setSelection(spCurrencyPosition);
    }

    private void setDefaultCurrency(String currency){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MyConstants.DEFAULT_CURRENCY, currency).apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbConnector.close();
    }
}