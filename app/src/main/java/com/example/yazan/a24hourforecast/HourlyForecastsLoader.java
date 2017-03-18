package com.example.yazan.a24hourforecast;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.yazan.a24hourforecast.data.HourForecast;
import com.example.yazan.a24hourforecast.utils.QueryUtils;

import java.util.List;

/**
 * Created by yazan on 2/14/17.
 */

public class HourlyForecastsLoader extends AsyncTaskLoader<List<HourForecast>> {

    /** Tag for log messages */
    private static final String LOG_TAG = HourlyForecastsLoader.class.getName();

    private String mUrl;

    public HourlyForecastsLoader(Context context, String url ) {
        super(context);
        mUrl = url;

    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG , "TEST : onStartLoading() called ...");


        forceLoad();
    }

    @Override
    public List<HourForecast> loadInBackground() {
        Log.i(LOG_TAG , "TEST : loadInBackground() called ...");

        if(mUrl ==  null){
            return null;
        }

            List<HourForecast> hourlyForecastsList = QueryUtils.fetchHourlyForecastsData(mUrl);
            return hourlyForecastsList;

    }
}
