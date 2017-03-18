package com.example.yazan.a24hourforecast;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.yazan.a24hourforecast.data.DayForecast;
import com.example.yazan.a24hourforecast.utils.QueryUtils;

import java.util.List;

/**
 * Created by yazan on 2/18/17.
 */

public class DailyForecastsLoader  extends AsyncTaskLoader<List<DayForecast>> {

    /** Tag for log messages */
    private static final String LOG_TAG = DailyForecastsLoader.class.getName();

    private String mUrl;

    public DailyForecastsLoader(Context context,String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG , "TEST : onStartLoading() called ...");


        forceLoad();
    }


    @Override
    public List<DayForecast> loadInBackground() {
        Log.i(LOG_TAG , "TEST : loadInBackground() called ...");

        if(mUrl ==  null){
            return null;
        }

        List<DayForecast>  dailyForecastsList = QueryUtils.fetchDailyForecastsData(mUrl);

        return dailyForecastsList;
    }
}
