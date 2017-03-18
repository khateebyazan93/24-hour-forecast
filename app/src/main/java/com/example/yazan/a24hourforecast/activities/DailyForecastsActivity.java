package com.example.yazan.a24hourforecast.activities;


import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.yazan.a24hourforecast.DailyForecastsLoader;
import com.example.yazan.a24hourforecast.R;
import com.example.yazan.a24hourforecast.adapters.DayForecastAdapter;
import com.example.yazan.a24hourforecast.data.DayForecast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yazan on 2/18/17.
 */

public class DailyForecastsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<DayForecast>> {

/**
 * Progress Bar
 * */
    ProgressBar mProgressBar ;
/**
 * openweathermap URL website for daily Weather data
 * */
   String mDailyUrl;
    /**
     * Custom adapter for show daily Weather data
     * */
    DayForecastAdapter mDayForecastAdapter;
    /**
     * TextView show only if no internet
     * */
    TextView mNoInternetTextView;
    /**
     * List View for showing Daily Forecasts
     * */
    ListView mDailyForecastsListView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecasts);

         //find and attach ProgressBar
        mProgressBar = (ProgressBar) findViewById(R.id.daily_progress_bar);
        //find and attach NoInternet TextView
        mNoInternetTextView = (TextView) findViewById(R.id.no_internet_text_view);

        //find and attach ListView
        mDailyForecastsListView = (ListView) findViewById(R.id.daily_forecasts_list_view);

        //create and setup new DayForecastAdapter
        mDayForecastAdapter = new DayForecastAdapter(this, new ArrayList<DayForecast>());
        mDailyForecastsListView.setAdapter(mDayForecastAdapter);

      //get dayUrl from intent that launch Daily Forecasts Activity
        mDailyUrl = getIntent().getStringExtra("dayUrl");

        //get instance of ConnectivityManager
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //check if there is internet connection
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(3, null, this);

        }else {
            mDailyForecastsListView.setEmptyView(mNoInternetTextView);

            mNoInternetTextView.setText("NO INTERNET");

        }

    }



    @Override
    public Loader<List<DayForecast>> onCreateLoader(int id, Bundle args) {
        return new DailyForecastsLoader(this, mDailyUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<DayForecast>> loader, List<DayForecast> data) {

        //clear the adapter
        mDayForecastAdapter.clear();

        if(data != null) {
            mDayForecastAdapter.addAll(data);
            mProgressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<DayForecast>> loader) {

        mDayForecastAdapter.clear();
    }



}
