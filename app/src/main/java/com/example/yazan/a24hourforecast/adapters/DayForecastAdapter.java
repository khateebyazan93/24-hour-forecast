package com.example.yazan.a24hourforecast.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yazan.a24hourforecast.R;
import com.example.yazan.a24hourforecast.data.DayForecast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by yazan on 2/13/17.
 */

public class DayForecastAdapter extends ArrayAdapter<DayForecast> {

    /**
     * Construct new {@link DayForecastAdapter} object
     * @param context is a context of the APP
     * @param dayForecastArrayList a list of {@link DayForecast} objects
     * */
    public DayForecastAdapter(Context context, ArrayList<DayForecast> dayForecastArrayList) {
        super(context,0, dayForecastArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //get list item
        View listItemView= convertView;

        //check if  listItemView null to inflate list item otherwise reuse listItemView
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_day_forecast,
                    parent,false);
        }

        //get {@link DayForecast} object from dayForecastArrayList at the current position
        DayForecast currentDayForecast = getItem(position);

       //set weather temperature
        TextView temperatureTextView =(TextView) listItemView.findViewById(R.id.temperature_text_view);
        temperatureTextView.setText(currentDayForecast.getTemperature() + "");

       //set weather icon
        ImageView iconImageView = (ImageView) listItemView.findViewById(R.id.icon_image_view);
        int iconImageResourceId = getProperIcon(currentDayForecast.getIconName());
        iconImageView.setImageResource(iconImageResourceId);

         // set weather descriprtion
        TextView descriprtionTexyView = (TextView) listItemView.findViewById(R.id.description_text_view);
        descriprtionTexyView.setText(currentDayForecast.getDescription());

        //set weather Date in two TextView (day&&date)
        TextView dayTextView = (TextView) listItemView.findViewById(R.id.day_text_view);
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_text_view);

        Date date = new Date(currentDayForecast.getTime()/* in seconds */ * 1000);

        String currentDay = formatDay(date);
        String currentDate = formatDate(date);

        dayTextView.setText(currentDay);
        dateTextView.setText(currentDate);


        return listItemView;
    }
/**
 * Helper method for {@link #getView(int, View, ViewGroup)} method
 *
 * Format the current Date to Day format
 *
 * @param date is a current date in unix time from current {@link DayForecast} object
 * @return formatted time in String
 * */
    public String formatDay(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        return simpleDateFormat.format(date);

    }

    /**
     * Helper method for {@link #getView(int, View, ViewGroup)} method
     *
     * Format the current Date to Date format
     *
     * @param date is a current date in unix time from current {@link DayForecast} object
     * @return formatted time in String
     * */
    public String formatDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return simpleDateFormat.format(date);

    }

    /**
     * Helper method for {@link #getView(int, View, ViewGroup)} method
     *
     * Get image resource ID for current icon name
     *
     * @param iconName is a name of icon from current {@link DayForecast} object
     * @return image resource ID for current icon name
     * */
    private int getProperIcon(String iconName) {
        int iconImageResourceId = 0;
        switch (iconName){
            case  "01d":
                iconImageResourceId = R.drawable.ic_01d;
                break;

            case "02d":
                iconImageResourceId = R.drawable.ic_02d;
                break;

            case "03d":
            case "03n":
                iconImageResourceId = R.drawable.ic_03d;
                break;

            case "04d":
            case "04n":
                iconImageResourceId = R.drawable.ic_04d;
                break;

            case "09d":
            case "09n":
                iconImageResourceId = R.drawable.ic_09d;
                break;

            case "10d":
            case "10n":

                iconImageResourceId = R.drawable.ic_10d;
                break;

            case "11d":
            case "11n":
                iconImageResourceId = R.drawable.ic_11d;
                break;

            case "13d":
            case "13n":
                iconImageResourceId = R.drawable.ic_13d;
                break;

            case "50d":
            case "50n":
                iconImageResourceId = R.drawable.ic_50d;
                break;

            case "01n":
                iconImageResourceId = R.drawable.ic_01n;
                break;

            case "02n":
                iconImageResourceId = R.drawable.ic_02n;
                break;
        }

        return iconImageResourceId;

    }


}
