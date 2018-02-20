package com.example.kitstasher.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import java.util.Locale;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Алексей on 21.04.2017.
 * Demonstrates personal statistics. Uses charts to show data. Shows user's world statistics
 * from Parse cloud service.
 */

public class StatisticsFragment extends Fragment implements View.OnClickListener {
    private TextView tvGlobalCounter;
    private TextView tvTotalStashCount;
    private TextView tvAddedToday;
    private TextView tvDailyRecord;
    private ImageView ivProfilePic;
    private int totalStash;
    private String cloudId;

    private DbConnector dbConnector;
    private Cursor brandsList;

    private OnFragmentInteractionListener mListener;

    private PieChart categoryChart;
    private float[] cyData; //значения количества по категориям
    private String[] cxData; //подписи

    private int[] tyData; //значения количества по дням
    private String[] txData; //подписи

    private HorizontalBarChart brandsChart;
    private float[] byData; //количество по производителям
    private String[] bxData; //названия производителей
    private int brandsChartHeight;
    private ProgressDialog progressDialog;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public StatisticsFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        // Preparing database connection
        dbConnector = new DbConnector(getActivity());
        dbConnector.open();

        // Setting UI elements
        Button btnCheckNow = (Button) view.findViewById(R.id.btnCheckNow);
        btnCheckNow.setOnClickListener(this);
        if (!isOnline()) {
            btnCheckNow.setClickable(false);
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        }
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        // Setting statistics counters
        tvGlobalCounter = view.findViewById(R.id.tvScore);
        tvTotalStashCount = view.findViewById(R.id.tvTotalStashCount);
        tvAddedToday = view.findViewById(R.id.tvAddedToday);
        tvDailyRecord = view.findViewById(R.id.tvDailyRecord);
        ((MainActivity) getActivity())
                .setActionBarTitle(getActivity().getResources().getString(R.string.nav_statistics));

        // Getting shared preferences.
        sharedPreferences = getApplicationContext().getSharedPreferences(MyConstants.ACCOUNT_PREFS, Context.MODE_PRIVATE);

        // Setting up basic statistics (total stash, max stash by day, etc.)
        getAndSetStats();

        cloudId = sharedPreferences.getString(MyConstants.USER_ID_FACEBOOK, "");
        // Getting Facebook userpic URL
        String accountPictureUrl = sharedPreferences.getString(MyConstants.PROFILE_PICTURE_URL_FACEBOOK, null);
        // Loading profile image
        Glide.with(getApplicationContext())
                .load(accountPictureUrl)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(getApplicationContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                .into(ivProfilePic);

        // Setting up username
        tvUserName.setText(sharedPreferences.getString(MyConstants.USER_NAME_FACEBOOK, null));

        //Checking if local database is empty
        if (dbConnector.getAllData("_id").getCount() > 0) {
            // Preparing Brands data
            brandsChartHeight = (dbConnector.getBrandsStat().getCount()* 40) + 16;

            int chartSize = dbConnector.getBrandsStat().getCount();
            bxData = new String[chartSize];
            byData = new float[chartSize];

            brandsList = dbConnector.getBrandsStat();
            brandsList.moveToFirst();
            for (int i = 0; i < chartSize; i++) {
                bxData[i] = brandsList.getString(brandsList.getColumnIndex(DbConnector.COLUMN_BRAND));
                brandsList.moveToNext();
            }

            for (int i = 0; i < chartSize; i++) {
                byData[i] = dbConnector.getBrandsForCount(bxData[i]).getCount();
            }
            // Creating Bar Chart
            brandsChart = new HorizontalBarChart(getActivity());
            LinearLayout brandsLayout = (LinearLayout) view.findViewById(R.id.linLayoutBrands);
            brandsLayout.addView(brandsChart);
            brandsChart.setMinimumHeight(brandsChartHeight);

            // Bar chart customization
            brandsChart.setDescription("");
            brandsChart.getAxisLeft().setDrawLabels(false);
            brandsChart.getAxisRight().setDrawLabels(false);
            brandsChart.getLegend().setEnabled(false);
            brandsChart.setDrawGridBackground(false);
            // Adding data to Bar Chart
            addBrandData();

            float countAir = dbConnector.getByTag(MyConstants.CODE_AIR).getCount();
            float countSea = dbConnector.getByTag(MyConstants.CODE_SEA).getCount();
            float countGround = dbConnector.getByTag(MyConstants.CODE_GROUND).getCount();
            float countSpace = dbConnector.getByTag(MyConstants.CODE_SPACE).getCount();
            float countCarBike = dbConnector.getByTag(MyConstants.CODE_AUTOMOTO).getCount();
            float countOther = dbConnector.getByTag(MyConstants.CODE_OTHER).getCount();

            cxData = new String[]{
                    getString(R.string.air) + String.valueOf((int) countAir),
                    getString(R.string.sea) + String.valueOf((int) countSea),
                    getString(R.string.ground) + String.valueOf((int) countGround),
                    getString(R.string.space) + String.valueOf((int) countSpace),
                    getString(R.string.cars_bikes) + String.valueOf((int) countCarBike),
                    getString(R.string.other) + String.valueOf((int) countOther)
            };

            cyData = new float[]{countAir, countSea, countGround, countSpace, countCarBike, countOther};

            // Creating Pie Chart
            categoryChart = new PieChart(getActivity());
            LinearLayout layoutCategory = (LinearLayout) view.findViewById(R.id.linLayoutChart);
            layoutCategory.addView(categoryChart);

            // Pie Chart customization
            categoryChart.setUsePercentValues(true);
            categoryChart.setDrawHoleEnabled(true);
            categoryChart.setHoleRadius(16);
            categoryChart.setTransparentCircleRadius(16);
            categoryChart.setRotationAngle(0);
            categoryChart.setRotationEnabled(true);
            categoryChart.setMinimumHeight(400);
            // todo Listener for taps in Pie Chart
//            categoryChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//
//                @Override
//                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
//            //todo Taps to category viewpager
//                }
//
//                @Override
//                public void onNothingSelected() {
//
//                }
//            });

            //Adding data to Pie Chart
            addPieData();
            // Pie Chart customization
            categoryChart.setDescription("");
            categoryChart.getLegend().setEnabled(false);
        } else {
            LinearLayout linLayoutChart = (LinearLayout)view.findViewById(R.id.linLayoutChart);
            TextView databaseEmpty = new TextView(getActivity());
            databaseEmpty.setText(R.string.Database_empty);
            linLayoutChart.addView(databaseEmpty);
        }

        return view;
    }

    /*
    * Checks if there are nulls in required fields (brand)*/
    private void checkDb() {
        dbConnector.checkDbForNulls();
    }

    public void getAndSetStats(){
        // Setting current date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String date = df.format(c.getTime());
        // Counting daily record
        int dailyMax = dbConnector.getDataDate(date).getCount();
        String recordDate = date;
        totalStash = dbConnector.getAllData("_id").getCount();

        SharedPreferences stat = getActivity().getPreferences(Context.MODE_PRIVATE);
        if (stat != null) {
            int savedMax = stat.getInt("dailyMax", 0);

            if (savedMax < dailyMax) {
                SharedPreferences.Editor statEditor = stat.edit();
                statEditor.putInt("dailyMax", dailyMax);
                statEditor.putString("dailyMaxDate", date);
                statEditor.commit();
            }else{
                dailyMax = savedMax;
                recordDate = stat.getString("dailyMaxDate","");
            }

        }else {
            SharedPreferences.Editor statEditor = stat.edit();
            statEditor.putInt("dailyMax", dailyMax);
            statEditor.putString("dailyMaxDate", date);
            statEditor.commit();
        }

        tvTotalStashCount.setText(String.valueOf(totalStash));
        tvAddedToday.setText(String.valueOf(dbConnector.getDataDate(date).getCount()));
        tvDailyRecord.setText(String.valueOf(dailyMax + " (" + recordDate + ")"));

    }

    /**
     * Prepares data for Bar Chart.
     */
    private void addBrandData() {
        ArrayList<BarEntry> byVals = new ArrayList<BarEntry>();
        for (int i = 0; i < byData.length; i++)
            byVals.add(new BarEntry(byData[i], i));

        ArrayList<String> bxVals = new ArrayList<String>();

        for (int i = 0; i < bxData.length; i++)
            bxVals.add(bxData[i]);

        // Creating bar data set
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

    /**
     * Prepares data for Pie Chart
     */
    private void addPieData() {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < cyData.length; i++)
            //Add only categories with kits
            if (cyData[i] > 0){
                yVals1.add(new Entry(cyData[i], i));
                xVals.add(cxData[i]);
            }

        // Creates pie dataset
        PieDataSet dataSet = new PieDataSet(yVals1, "");

        // Customizes pie chart data
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);
        dataSet.setValueTextColor(Color.BLACK);
        // Customizable colors list
        final int[] MY_COLORS = {
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark),
                ContextCompat.getColor(getActivity(), R.color.colorSecondary),
                ContextCompat.getColor(getActivity(), R.color.colorSecondaryDark),
                ContextCompat.getColor(getActivity(), R.color.colorPrimary)
        };

        // Sets color scheme
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        // Sets data
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);

        categoryChart.setData(data);
        categoryChart.highlightValues(null);

        categoryChart.invalidate();
    }



//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // DO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Puts date in convenient format
     */
    private class MyValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
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
                sendDataToParse();
                checkStatus();
                break;
        }

    }

    /**
     * Sends statistics to the cloud in Top_users table
     */
    private void sendDataToParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Top_users");
        query.whereEqualTo("ownerId", cloudId);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    //Writing total stash count to cloud
                    object.put("stash", totalStash);
                    try {
                        object.save();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
//                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Checks user achievements
     */
    private void checkStatus() {
        progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.Checking));
        progressDialog.setCancelable(true);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Top_users");
        query.whereGreaterThan("stash", totalStash);
        query.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    if(count == 0){
                        tvGlobalCounter.setText("1");
                        SharedPreferences stat = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor statEditor = stat.edit();
                        progressDialog.dismiss();
                        statEditor.putString("world_rank", String.valueOf(1));
                        statEditor.commit();
                    }else{
                        tvGlobalCounter.setText(String.valueOf(count + 1));
                        SharedPreferences stat = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor statEditor = stat.edit();
                        progressDialog.dismiss();
                        statEditor.putString("world_rank", String.valueOf(count + 1));
                        statEditor.commit();
                    }
                } else {
                    tvGlobalCounter.setText(R.string.Server_error);
                }
            }
        });
    }

    public boolean isOnline() {//// TODO: 06.09.2017 Helper
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}