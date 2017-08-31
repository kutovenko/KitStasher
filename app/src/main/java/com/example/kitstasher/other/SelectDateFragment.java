package com.example.kitstasher.other;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.kitstasher.R;

import java.util.Calendar;

/**
 * Created by Алексей on 26.08.2017.
 */

public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
private int year, month, day;

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
    public void populateSetDate(int y, int m, int d) {
        year = y;
        month = m;
        day = d;
        String caller = getArguments().getString("caller");
        if (caller.equals("KitActivity")) {
            TextView dob = (TextView) getActivity().findViewById(R.id.tvMSelectPurchaseDate);
            dob.setText(day+"-"+month+"-"+year);
        }else if (caller.equals("manualadd")){
            TextView dob = (TextView) getActivity().findViewById(R.id.tvPurchaseDate);
            dob.setText(day+"-"+month+"-"+year);
        }

    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }


    public int getYear() {
        return year;
    }
}