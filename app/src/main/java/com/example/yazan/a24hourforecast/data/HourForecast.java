package com.example.yazan.a24hourforecast.data;

/**
 * Created by yazan on 2/12/17.
 */

public class HourForecast {
    /**
     * weather temperature
     * */
    private int mTemperature;
    /**
     * icon name for current weather state
     * */
    private String mIconName;
    /**
     * weather description
     * */
    private String mDescription;
    /**
     * weather time
     * */
    private long mTime;


/**
 * Construct new {@link HourForecast} object
 * */
    public HourForecast(int temperature, String iconName, String description, long time){
        this.mTemperature = temperature;
        this.mIconName = iconName;
        this.mDescription = description;
        this.mTime = time;

    }


    public long getTime() {
        return mTime;
    }

    public String getDescription() {
        return mDescription;
    }


    public int getTemperature() {
        return mTemperature;
    }

    public String getIconName() {
        return mIconName;
    }
}
