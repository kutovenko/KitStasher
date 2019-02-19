package com.kutovenko.kitstasher.ui.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Алексей on 26.08.2017.
 */

public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private int year;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
        populateSetDate(yy, mm+1, dd);
    }
    private void populateSetDate(int year, int month, int day) {
        this.year = year;
        String caller = getArguments().getString("caller");
        switch (caller) {
            case "ViewActivity": {
                TextView dob = getActivity().findViewById(com.kutovenko.kitstasher.R.id.tvEditPurchaseDate);
                dob.setText(day + "-" + month + "-" + this.year);
                break;
            }
            case "manualadd": {
                TextView dob = getActivity().findViewById(com.kutovenko.kitstasher.R.id.tvPurchaseDate);
                dob.setText(day + "-" + month + "-" + this.year);
                break;
            }
            case "MainActivity": {
                TextView dob = getActivity().findViewById(com.kutovenko.kitstasher.R.id.tvEditPurchaseDate);
                dob.setText(day + "-" + month + "-" + this.year);
                break;
            }
        }

    }

    public int getYear() {
        return year;
    }
}