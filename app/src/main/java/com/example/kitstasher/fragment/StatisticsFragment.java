package com.example.kitstasher.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kitstasher.R;
import com.example.kitstasher.activity.MainActivity;
import com.example.kitstasher.other.CircleTransform;
import com.example.kitstasher.other.DbConnector;
import com.example.kitstasher.other.MyConstants;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Алексей on 21.04.2017.
 * Demonstrates personal statistics. Uses charts to show data. Shows user's world statistics
 * from Parse cloud service.
 */

public class StatisticsFragment extends Fragment implements View.OnClickListener {
    private TextView tvGlobalCounter,
            tvTotalStashCount,
            tvAddedToday,
            tvDailyRecord;
    private ProgressDialog progressDialog;
    private DbConnector dbConnector;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor statEditor;
    private int totalStash;
    private String cloudId;
    private PieChart categoryChart;
    private float[] cyData; //значения количества по категориям
    private String[] cxData; //подписи
    private HorizontalBarChart brandsChart;
    private float[] byData; //количество по производителям
    private String[] bxData; //названия производителей

    public StatisticsFragment(){

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();

        Button btnCheckNow = view.findViewById(R.id.btnCheckNow);
        btnCheckNow.setOnClickListener(this);
        if (!isOnline()) {
            btnCheckNow.setClickable(false);
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        }
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        ImageView ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvGlobalCounter = view.findViewById(R.id.tvScore);
        if (sharedPreferences != null) {
            tvGlobalCounter.setText(sharedPreferences.getString(MyConstants.WORLDRANK, ""));
        }
        tvTotalStashCount = view.findViewById(R.id.tvTotalStashCount);
        tvAddedToday = view.findViewById(R.id.tvAddedToday);
        tvDailyRecord = view.findViewById(R.id.tvDailyRecord);
        ((MainActivity) getActivity())
                .setActionBarTitle(getActivity().getResources().getString(R.string.nav_statistics));

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);


        cloudId = sharedPreferences.getString(MyConstants.USER_ID_PARSE, "");
        tvUserName.setText(sharedPreferences.getString(MyConstants.USER_NAME_FACEBOOK, null));

        getAndSetStats();

        String accountPictureUrl = sharedPreferences.getString(MyConstants.PROFILE_PICTURE_URL_FACEBOOK, null);
        Glide.with(getApplicationContext())
                .load(accountPictureUrl)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .into(ivProfilePic);

        if (dbConnector.getAllData("_id").getCount() > 0) {
            int brandsChartHeight = (dbConnector.getBrandsStat().getCount() * 40) + 16;
            int chartSize = dbConnector.getBrandsStat().getCount();
            bxData = new String[chartSize];
            byData = new float[chartSize];

            Cursor brandsList = dbConnector.getBrandsStat();
            brandsList.moveToFirst();
            for (int i = 0; i < chartSize; i++) {
                bxData[i] = brandsList.getString(brandsList.getColumnIndex(DbConnector.COLUMN_BRAND));
                brandsList.moveToNext();
            }

            for (int i = 0; i < chartSize; i++) {
                byData[i] = dbConnector.getBrandsForCount(bxData[i]).getCount();
            }

            brandsChart = new HorizontalBarChart(getActivity());
            LinearLayout brandsLayout = view.findViewById(R.id.linLayoutBrands);
            brandsLayout.addView(brandsChart);
            brandsChart.setMinimumHeight(brandsChartHeight);

            brandsChart.setDescription("");
            brandsChart.getAxisLeft().setDrawLabels(false);
            brandsChart.getAxisRight().setDrawLabels(false);
            brandsChart.getLegend().setEnabled(false);
            brandsChart.setDrawGridBackground(false);
            addBrandData();

            int countAir = dbConnector.getByTag(MyConstants.CODE_AIR).getCount();
            int countSea = dbConnector.getByTag(MyConstants.CODE_SEA).getCount();
            int countGround = dbConnector.getByTag(MyConstants.CODE_GROUND).getCount();
            int countSpace = dbConnector.getByTag(MyConstants.CODE_SPACE).getCount();
            int countCarBike = dbConnector.getByTag(MyConstants.CODE_AUTOMOTO).getCount();
            int countOther = dbConnector.getByTag(MyConstants.CODE_OTHER).getCount();

            cxData = new String[]{
                    getString(R.string.air) + String.valueOf(countAir),
                    getString(R.string.sea) + String.valueOf(countSea),
                    getString(R.string.ground) + String.valueOf(countGround),
                    getString(R.string.space) + String.valueOf(countSpace),
                    getString(R.string.cars_bikes) + String.valueOf(countCarBike),
                    getString(R.string.other) + String.valueOf(countOther)
            };

            cyData = new float[]{countAir, countSea, countGround, countSpace, countCarBike, countOther};

            categoryChart = new PieChart(getActivity());
            LinearLayout layoutCategory = view.findViewById(R.id.linLayoutChart);
            layoutCategory.addView(categoryChart);

            categoryChart.setUsePercentValues(true);
            categoryChart.setDrawHoleEnabled(true);
            categoryChart.setHoleRadius(16);
            categoryChart.setTransparentCircleRadius(16);
            categoryChart.setRotationAngle(0);
            categoryChart.setRotationEnabled(true);
            categoryChart.setMinimumHeight(400);

            addPieData();

            categoryChart.setDescription("");
            categoryChart.getLegend().setEnabled(false);
        } else {
            LinearLayout linLayoutChart = view.findViewById(R.id.linLayoutChart);
            TextView databaseEmpty = new TextView(getActivity());
            databaseEmpty.setText(R.string.Database_empty);
            linLayoutChart.addView(databaseEmpty);
        }

        return view;
    }

    public void getAndSetStats(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String date = df.format(c.getTime());
        int dailyMax = dbConnector.getDataDate(date).getCount();
        String recordDate = date;
        totalStash = dbConnector.getAllData(MyConstants._ID).getCount();

        if (sharedPreferences != null) {
            int savedMax = sharedPreferences.getInt(MyConstants.DAILYMAX, 0);

            if (savedMax < dailyMax) {
                statEditor.putInt(MyConstants.DAILYMAX, dailyMax);
                statEditor.putString(MyConstants.DAILYMAXDATE, date);
                statEditor.commit();
            }else{
                dailyMax = savedMax;
                recordDate = sharedPreferences.getString(MyConstants.DAILYMAXDATE, "");
            }

        }else {
//            SharedPreferences.Editor statEditor = sharedPreferences.edit();
            statEditor.putInt(MyConstants.DAILYMAX, dailyMax);
            statEditor.putString(MyConstants.DAILYMAXDATE, date);
            statEditor.commit();
        }

        tvTotalStashCount.setText(String.valueOf(totalStash));
        tvAddedToday.setText(String.valueOf(dbConnector.getDataDate(date).getCount()));
        tvDailyRecord.setText(String.valueOf(dailyMax + " (" + recordDate + ")"));

    }

    private void addBrandData() {
        ArrayList<BarEntry> byVals = new ArrayList<>();
        for (int i = 0; i < byData.length; i++)
            byVals.add(new BarEntry(byData[i], i));

        ArrayList<String> bxVals = new ArrayList<>();

        Collections.addAll(bxVals, bxData);

        BarDataSet bDataSet = new BarDataSet(byVals, "");
        bDataSet.setValueTextColor(Color.BLACK);
        bDataSet.setColors(ColorTemplate.COLORFUL_COLORS);


        BarData bData = new BarData(bxVals, bDataSet);
        bData.setValueFormatter(new MyValueFormatter());
        bData.setValueTextSize(12f);

        brandsChart.setData(bData);
        brandsChart.getXAxis().setTextSize(12f);
        brandsChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        brandsChart.getXAxis().setDrawGridLines(false);
        brandsChart.getXAxis().setDrawAxisLine(false);
        brandsChart.getXAxis().setDrawLimitLinesBehindData(false);
        brandsChart.highlightValues(null);

        brandsChart.invalidate();
    }

    private void addPieData() {
        ArrayList<Entry> yVals1 = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < cyData.length; i++)
            if (cyData[i] > 0){
                yVals1.add(new Entry(cyData[i], i));
                xVals.add(cxData[i]);
            }

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);

        categoryChart.setData(data);
        categoryChart.highlightValues(null);
        categoryChart.invalidate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private class MyValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        private MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,###");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCheckNow:
                sendStatisticsToParse();
                checkUserAchievments();
                break;
        }
    }

    private void sendStatisticsToParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(MyConstants.PARSE_C_TOPUSERS);
        query.whereEqualTo(MyConstants.PARSE_TU_USERID, cloudId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    object.put(MyConstants.PARSE_TU_STASH, totalStash);
                    try {
                        object.save();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    private void checkUserAchievments() {
        progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.Checking));
        progressDialog.setCancelable(true);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(MyConstants.PARSE_C_TOPUSERS);
        query.whereGreaterThan(MyConstants.PARSE_TU_STASH, totalStash);
        query.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    if(count == 0){
                        tvGlobalCounter.setText("1");
                        progressDialog.dismiss();
                        statEditor = sharedPreferences.edit();
                        statEditor.putString(MyConstants.WORLDRANK, String.valueOf(1));
                        statEditor.apply();
                    }else{
                        tvGlobalCounter.setText(String.valueOf(count + 1));
                        progressDialog.dismiss();
                        statEditor = sharedPreferences.edit();
                        statEditor.putString(MyConstants.WORLDRANK, String.valueOf(count + 1));
                        statEditor.apply();
                    }
                } else {
                    tvGlobalCounter.setText(R.string.Server_error);
                }
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}