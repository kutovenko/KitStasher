package com.kutovenko.kitstasher.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
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
import com.kutovenko.kitstasher.R;
import com.kutovenko.kitstasher.databinding.FragmentStatisticsBinding;
import com.kutovenko.kitstasher.db.DbConnector;
import com.kutovenko.kitstasher.ui.MainActivity;
import com.kutovenko.kitstasher.util.Helper;
import com.kutovenko.kitstasher.util.MyConstants;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Алексей on 21.04.2017.
 * Demonstrates personal statistics. Uses charts to show data. Shows user's world statistics
 * from Parse cloud service.
 */

public class StatisticsFragment extends Fragment implements View.OnClickListener {
    private FragmentStatisticsBinding binding;
    private ProgressDialog progressDialog;
    private DbConnector dbConnector;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor statEditor;
    private int totalKits;
    private String cloudId;
    public StatisticsFragment(){

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false);

        dbConnector = new DbConnector(getActivity());
        dbConnector.open();
        totalKits = dbConnector.countAllRecordsByType(MyConstants.TYPE_KIT);

//        binding.btnCheckNow.setOnClickListener(this);
//        if (!isOnline()) {
//            binding.btnCheckNow.setClickable(false);
//            Toast.makeText(getActivity(), com.kutovenko.kitstasher.R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
//        }

        sharedPreferences = getActivity().getSharedPreferences(MyConstants.ACCOUNT_PREFS,
                Context.MODE_PRIVATE);
        cloudId = sharedPreferences.getString(MyConstants.USER_ID_PARSE, "");

//        if (sharedPreferences != null) {
//            binding.tvScore.setText(sharedPreferences.getString(MyConstants.WORLDRANK, ""));
//        }
        ((MainActivity) getActivity())
                .setActionBarTitle(getActivity().getResources().getString(com.kutovenko.kitstasher.R.string.nav_statistics));



        getAndSetStats();
        setPieAndChart(binding.linLayoutKitsBrands, binding.linLayoutKitsChart, MyConstants.TYPE_KIT);
        setPieAndChart(binding.linLayoutAfterBrands, binding.linLayoutAfterChart, MyConstants.TYPE_AFTERMARKET);
        setPieAndChart(binding.linLayoutSupplyBrands, binding.linLayoutSupplyChart, MyConstants.TYPE_SUPPLY);

        return binding.getRoot();
    }



    private void setPieAndChart(LinearLayout brandsLayout, LinearLayout layoutCategory, String contentType) {
        HorizontalBarChart brandsChart = new HorizontalBarChart(getActivity());
        PieChart categoriesPie = new PieChart(getActivity());
        int chartSize;
        int brandsChartHeigh;
        String[] bxItemsData;
        float[] byItemsData;
        String[] cxItemsData;
        float[] cyItemsData;

        switch (contentType){
            case MyConstants.TYPE_KIT:
                bxItemsData = dbConnector.getSortedBrandsCounts(MyConstants.TYPE_KIT);
                chartSize = bxItemsData.length;
                brandsChartHeigh = (chartSize * 40) + 16;
                brandsChart.setMinimumHeight(brandsChartHeigh);


                int countAir = dbConnector.countForTag(MyConstants.CODE_AIR);
                int countSea = dbConnector.countForTag(MyConstants.CODE_SEA);
                int countGround = dbConnector.countForTag(MyConstants.CODE_GROUND);
                int countSpace = dbConnector.countForTag(MyConstants.CODE_SPACE);
                int countCarBike = dbConnector.countForTag(MyConstants.CODE_AUTOMOTO);
                int countOther = dbConnector.countForTag(MyConstants.CODE_OTHER);

                byItemsData = new float[chartSize];
                for (int i = 0; i < chartSize; i++){
                    byItemsData[i] = dbConnector.countKitsOfBrand(bxItemsData[i]);
                }

                cxItemsData = new String[]{
                        getString(com.kutovenko.kitstasher.R.string.air_) + String.valueOf(countAir),
                        getString(com.kutovenko.kitstasher.R.string.sea_) + String.valueOf(countSea),
                        getString(com.kutovenko.kitstasher.R.string.ground_) + String.valueOf(countGround),
                        getString(com.kutovenko.kitstasher.R.string.space_) + String.valueOf(countSpace),
                        getString(com.kutovenko.kitstasher.R.string.cars_bikes_) + String.valueOf(countCarBike),
                        getString(com.kutovenko.kitstasher.R.string.other_) + String.valueOf(countOther)
                };

                cyItemsData = new float[]{countAir, countSea, countGround, countSpace, countCarBike, countOther};
                addBrandData(byItemsData, bxItemsData, brandsChart);
                addPieData(cyItemsData, cxItemsData, categoriesPie);
                break;

            case MyConstants.TYPE_AFTERMARKET:
                bxItemsData = dbConnector.getSortedBrandsCounts(MyConstants.TYPE_AFTERMARKET);
                chartSize = bxItemsData.length;
                brandsChartHeigh = (chartSize * 40) + 16;
                brandsChart.setMinimumHeight(brandsChartHeigh);
                byItemsData = new float[chartSize];
                for (int i = 0; i < chartSize; i++) {
                    byItemsData[i] = dbConnector.countKitsOfBrand(bxItemsData[i]);
                }
                int countAddons = dbConnector.countForTag(MyConstants.M_CODE_ADDON);
                int countPhotoetch = dbConnector.countForTag(MyConstants.M_CODE_PHOTOETCH);
                int countDecal = dbConnector.countForTag(MyConstants.M_CODE_DECAL);
                int countMasks = dbConnector.countForTag(MyConstants.M_CODE_OTHER);
                int countOtherAddon = dbConnector.countForTag(MyConstants.M_CODE_OTHER);
                cxItemsData = new String[]{
                        getString(com.kutovenko.kitstasher.R.string.media_addon_) + String.valueOf(countAddons),
                        getString(com.kutovenko.kitstasher.R.string.media_photoetch_) + String.valueOf(countPhotoetch),
                        getString(com.kutovenko.kitstasher.R.string.media_decal_) + String.valueOf(countDecal),
                        getString(com.kutovenko.kitstasher.R.string.media_mask_) + String.valueOf(countMasks),
                        getString(com.kutovenko.kitstasher.R.string.media_other_) + String.valueOf(countOtherAddon)
                };

                cyItemsData = new float[]{countAddons, countPhotoetch, countDecal, countMasks, countOtherAddon};
                addBrandData(byItemsData, bxItemsData, brandsChart);
                addPieData(cyItemsData, cxItemsData, categoriesPie);
                break;

            case MyConstants.TYPE_SUPPLY:
                bxItemsData = dbConnector.getSortedBrandsCounts(MyConstants.TYPE_SUPPLY);
                chartSize = bxItemsData.length;
                brandsChartHeigh = (chartSize * 40) + 16;
                brandsChart.setMinimumHeight(brandsChartHeigh);
                byItemsData = new float[chartSize];
                for (int i = 0; i < chartSize; i++) byItemsData[i] = dbConnector.countKitsOfBrand(bxItemsData[i]);
                int countAcrylic = dbConnector.countForTag(MyConstants.CODE_P_ACRYLLIC);
                int countEnamel = dbConnector.countForTag(MyConstants.CODE_P_ENAMEL);
                int countOil = dbConnector.countForTag(MyConstants.CODE_P_OIL);
                int countLacquer = dbConnector.countForTag(MyConstants.CODE_P_LACQUER);
                int countThinner = dbConnector.countForTag(MyConstants.CODE_P_THINNER);
                int countGlue = dbConnector.countForTag(MyConstants.CODE_P_GLUE);
                int countDecalSet = dbConnector.countForTag(MyConstants.CODE_P_DECAL_SET);
                int countDecalSol = dbConnector.countForTag(MyConstants.CODE_P_DECAL_SOL);
                int countPigment = dbConnector.countForTag(MyConstants.CODE_P_PIGMENT);
                int countColorStop = dbConnector.countForTag(MyConstants.CODE_P_COLORSTOP);
                int countFiller = dbConnector.countForTag(MyConstants.CODE_P_FILLER);
                int countPrimer = dbConnector.countForTag(MyConstants.CODE_P_PRIMER);
                int countOtherSupply = dbConnector.countForTag(MyConstants.CODE_P_OTHER);


                cxItemsData = new String[]{
                        getString(com.kutovenko.kitstasher.R.string.acryllic_) + String.valueOf(countAcrylic),
                        getString(com.kutovenko.kitstasher.R.string.enamel_) + String.valueOf(countEnamel),
                        getString(com.kutovenko.kitstasher.R.string.media_oil_) + String.valueOf(countOil),
                        getString(com.kutovenko.kitstasher.R.string.media_lacquer_) + String.valueOf(countLacquer),
                        getString(com.kutovenko.kitstasher.R.string.media_thinner_) + String.valueOf(countThinner),
                        getString(com.kutovenko.kitstasher.R.string.media_glue_) + String.valueOf(countGlue),
                        getString(com.kutovenko.kitstasher.R.string.media_decalset_) + String.valueOf(countDecalSet),
                        getString(com.kutovenko.kitstasher.R.string.media_decalsol_) + String.valueOf(countDecalSol),
                        getString(com.kutovenko.kitstasher.R.string.media_pigment_) + String.valueOf(countPigment),
                        getString(com.kutovenko.kitstasher.R.string.media_colorstop_) + String.valueOf(countColorStop),
                        getString(com.kutovenko.kitstasher.R.string.media_filler_) + String.valueOf(countFiller),
                        getString(com.kutovenko.kitstasher.R.string.media_primer_) + String.valueOf(countPrimer),
                        getString(com.kutovenko.kitstasher.R.string.media_other_) + String.valueOf(countOtherSupply)
                };

                cyItemsData = new float[]{countAcrylic, countEnamel, countOil, countLacquer,
                        countThinner, countGlue, countDecalSet, countDecalSol, countPigment, countColorStop,
                        countFiller, countPrimer, countOtherSupply};
                addBrandData(byItemsData, bxItemsData, brandsChart);
                addPieData(cyItemsData, cxItemsData, categoriesPie);
                break;
        }


        if (dbConnector.countAllRecordsByType(contentType) > 0) {

            brandsLayout.addView(brandsChart);
            brandsChart.setDescription(MyConstants.EMPTY);
            brandsChart.getAxisLeft().setDrawLabels(false);
            brandsChart.getAxisRight().setDrawLabels(false);
            brandsChart.getLegend().setEnabled(false);
            brandsChart.setDrawGridBackground(false);

            layoutCategory.addView(categoriesPie);
            int size = 480;
            layoutCategory.setMinimumHeight(size);
            layoutCategory.setMinimumWidth(size);
            categoriesPie.setUsePercentValues(true);
            categoriesPie.setDrawHoleEnabled(true);
            categoriesPie.setHoleRadius(16);
            categoriesPie.setTransparentCircleRadius(16);
            categoriesPie.setRotationAngle(0);
            categoriesPie.setRotationEnabled(true);
            categoriesPie.setMinimumWidth(size);
            categoriesPie.setMinimumHeight(size);
            categoriesPie.setDescription("");
            categoriesPie.getLegend().setEnabled(false);

        } else {
            layoutCategory.setVisibility(View.GONE);
            brandsLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dbConnector.close();
    }

    private void getAndSetStats(){
        binding.tvTotalKitsCount.setText(String.valueOf(totalKits));
        int totalAfter = dbConnector.countAllRecordsByType(MyConstants.TYPE_AFTERMARKET);
        binding.tvTotalAfterCount.setText(String.valueOf(totalAfter));
        int totalSupplies = dbConnector.countAllRecordsByType(MyConstants.TYPE_SUPPLY);
        binding.tvTotalSupplyCount.setText(String.valueOf(totalSupplies));
    }

    private void addBrandData(float[] byData, String[] bxData, HorizontalBarChart horizontalBarChart) {
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

        horizontalBarChart.setData(bData);
        horizontalBarChart.getXAxis().setTextSize(12f);
        horizontalBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        horizontalBarChart.getXAxis().setDrawGridLines(false);
        horizontalBarChart.getXAxis().setDrawAxisLine(false);
        horizontalBarChart.getXAxis().setDrawLimitLinesBehindData(false);
        horizontalBarChart.highlightValues(null);

        horizontalBarChart.invalidate();
    }

    private void addPieData(float[] cyData, String[] cxData, PieChart pieChart) {
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

        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
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
//        switch (view.getId()){
//            case com.kutovenko.kitstasher.R.id.btnCheckNow:
//                checkFacebookConnection();
//                if (sharedPreferences.getString("registered", "").equals("true")){
//                    sendStatisticsToParse();
//                    checkUserAchievments();
//                }
//                break;
//        }
    }

//    private void sendStatisticsToParse() {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery(MyConstants.PARSE_C_TOPUSERS);
//        query.whereEqualTo(MyConstants.PARSE_TU_USERID, cloudId);
//        query.getFirstInBackground(new GetCallback<ParseObject>() {
//            public void done(ParseObject object, ParseException e) {
//                if (e == null) {
//                    object.put(MyConstants.PARSE_TU_STASH, totalKits);
//                    try {
//                        object.save();
//                    } catch (ParseException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            }
//        });
//    }
//
//    private void checkUserAchievments() {
//        progressDialog = ProgressDialog.show(getActivity(), "", getString(com.kutovenko.kitstasher.R.string.Checking));
//        progressDialog.setCancelable(true);
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery(MyConstants.PARSE_C_TOPUSERS);
//        query.whereGreaterThan(MyConstants.PARSE_TU_STASH, totalKits);
//        query.countInBackground(new CountCallback() {
//            public void done(int count, ParseException e) {
//                if (e == null) {
//                    if(count == 0){
//                        binding.tvScore.setText("1");
//                        progressDialog.dismiss();
//                        statEditor = sharedPreferences.edit();
//                        statEditor.putString(MyConstants.WORLDRANK, String.valueOf(1));
//                        statEditor.apply();
//                    }else{
//                        binding.tvScore.setText(String.valueOf(count + 1));
//                        progressDialog.dismiss();
//                        statEditor = sharedPreferences.edit();
//                        statEditor.putString(MyConstants.WORLDRANK, String.valueOf(count + 1));
//                        statEditor.apply();
//                    }
//                } else {
//                    binding.tvScore.setText(com.kutovenko.kitstasher.R.string.Server_error);
//                }
//            }
//        });
//    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private void checkFacebookConnection() {
        if (Helper.isOnline(getActivity())){
            AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                    updateWithToken(newAccessToken);
                }
            };
            accessTokenTracker.startTracking();
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            updateWithToken(accessToken);
        } else {
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        }
    }



    private void updateWithToken(AccessToken currentAccessToken) {
        if (currentAccessToken == null) {
            LoginFragment fragment = LoginFragment.newInstance();

            FragmentTransaction fragmentTransaction =
                    getFragmentManager().beginTransaction();
            fragmentTransaction.replace(com.kutovenko.kitstasher.R.id.mainactivityContainer, fragment);
            fragmentTransaction.commit();
        }
    }
}