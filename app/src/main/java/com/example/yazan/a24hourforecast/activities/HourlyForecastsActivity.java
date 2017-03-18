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

import com.example.yazan.a24hourforecast.HourlyForecastsLoader;
import com.example.yazan.a24hourforecast.R;
import com.example.yazan.a24hourforecast.adapters.HourForecastAdapter;
import com.example.yazan.a24hourforecast.data.HourForecast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yazan on 2/16/17.
 */

public class HourlyForecastsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<HourForecast>> {

ProgressBar mProgressBar ;

    /**
     * Progress Bar 
     * */
   String mHourlyUrl;
    /**
     *
     * Custom adapter for adapt hour Weather data  to list item view
     * */
    HourForecastAdapter mHourForecastAdapter;
    /**
     * List View for showing Hourly forecasts
     */
    ListView mHourlyForecastsListView;

    /**
     * TextView show only if no internet
     * */
    TextView mNoInternetTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecasts);

        //find and attach ProgressBar
        mProgressBar = (ProgressBar) findViewById(R.id.hourly_progress_bar);

        //find and attach NoInternet TextView
        mNoInternetTextView= (TextView) findViewById(R.id.no_internet_text_view);

        //find and attach ListView
        mHourlyForecastsListView = (ListView) findViewById(R.id.hourly_forecasts_list_view);

        //create and setup new HourForecastAdapter
        mHourForecastAdapter = new HourForecastAdapter(this, new ArrayList<HourForecast>());
        mHourlyForecastsListView.setAdapter(mHourForecastAdapter);

        //get hourUrl from intent that launch HourlyForecastsActivity
        mHourlyUrl = getIntent().getStringExtra("hourUrl");

        //get instance of ConnectivityManager
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //check if there is internet connection
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(2, null, this);

        }else {
            mHourlyForecastsListView.setEmptyView(mNoInternetTextView);

            mNoInternetTextView.setText("NO INTERNET");

        }



    }


    @Override
    public Loader<List<HourForecast>> onCreateLoader(int id, Bundle args) {
        return new HourlyForecastsLoader(this, mHourlyUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<HourForecast>> loader, List<HourForecast> data) {
        mHourForecastAdapter.clear();

        if(data != null) {
            mHourForecastAdapter.addAll(data);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<HourForecast>> loader) {
        mHourForecastAdapter.clear();

    }



}
