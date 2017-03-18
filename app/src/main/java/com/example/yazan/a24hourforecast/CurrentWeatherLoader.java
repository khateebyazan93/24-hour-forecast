package com.example.yazan.a24hourforecast;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.yazan.a24hourforecast.data.CurrentWeather;
import com.example.yazan.a24hourforecast.utils.QueryUtils;

/**
 * Created by yazan on 2/18/17.
 */

public class CurrentWeatherLoader extends AsyncTaskLoader<CurrentWeather> {

    /** Tag for log messages */
    private static final String LOG_TAG = CurrentWeatherLoader.class.getName();

    /**
     * */
    String mUrl;
    public CurrentWeatherLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG , "TEST : onStartLoading() called ...");


        forceLoad();
    }

    @Override
    public CurrentWeather loadInBackground() {
        Log.i(LOG_TAG , "TEST : loadInBackground() called ...");

        if(mUrl ==  null){
            return null;
        }

        CurrentWeather  currentWeather = QueryUtils.fetchCurrentWeatherData(mUrl);
        return currentWeather;
    }
}
