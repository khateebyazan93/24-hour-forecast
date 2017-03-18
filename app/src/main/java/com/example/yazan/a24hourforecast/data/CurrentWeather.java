package com.example.yazan.a24hourforecast.data;

/**
 * Created by yazan on 2/12/17.
 */

public class CurrentWeather {

    /**
     * weather location
     * */
    private String mLocation;
    /**
     * icon name for current weather state
     * */
    private String mIconName;
    /**
     * weather time
     * */
    private long mTime;
    /**
     * weather temperature
     * */
    private int mTemperature;
    /**
     * weather humidity
     * */
    private double mHumidity;

    /**
     * weather wind Speed
     * */
    private double mWindSpeed;

    /**
     * weather description
     * */
    private String mDescription;


    /**
     * Construct new {@link CurrentWeather} object
     *
     * */
    public CurrentWeather(String location, String iconName, long time, int temperature, double humidity,
                          double windSpeed, String description){
     this.mLocation = location;
        this.mIconName = iconName;
        this.mTime = time;
        this.mTemperature = temperature;
        this.mHumidity = humidity;
        this.mWindSpeed = windSpeed;
        this.mDescription = description;
    }


    public double getWindSpeed() {
        return mWindSpeed;
    }

    public String getDescription() {
        return mDescription;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public int getTemperature() {
        return mTemperature;
    }

    public long getTime() {
        return mTime;
    }

    public String getIconName() {
        return mIconName;
    }

    public String getLocation() {
        return mLocation;
    }


}
