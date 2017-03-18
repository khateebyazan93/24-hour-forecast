package com.example.yazan.a24hourforecast.utils;

/**
 * Created by yazan on 2/12/17.
 */


import android.text.TextUtils;
import android.util.Log;

import com.example.yazan.a24hourforecast.data.CurrentWeather;
import com.example.yazan.a24hourforecast.data.DayForecast;
import com.example.yazan.a24hourforecast.data.HourForecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link QueryUtils} class contains static helper methods related to requesting and receiving weather data from openweathermap.org website
 */
public final class QueryUtils {

    /**
     * Tag for log message
     */
    private static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * private constructor no need to construct new {@link QueryUtils} object
     */
    private QueryUtils() {
    }


    /**
     * Query the openweathermap.org data and return {@link CurrentWeather} object.
     *
     * @param requestUrl is a openweathermap.org URL for requesting Current Weather Data
     * @return Current Weather data for given URL
     */
    public static CurrentWeather fetchCurrentWeatherData(String requestUrl) {
        //create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the given URL and receive JSON String response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem in making the HTTP request ...", e);

        }

        //extract the relevant fields from JSON response and create a {@link CurrentWeather} object.
        CurrentWeather currentWeather = extractCurrentWeatherFromJSON(jsonResponse);

        //Return {@link CurrentWeather} object.
        return currentWeather;
    }


    /**
     * Helper method for {@link #fetchCurrentWeatherData(String)}
     * <p>
     * create  {@link CurrentWeather} object that has been built up from
     * parsing given JSON response.
     *
     * @param jsonResponse is a String JSON response
     * @return {@link CurrentWeather} object
     */
    private static CurrentWeather extractCurrentWeatherFromJSON(String jsonResponse) {
        Log.i(LOG_TAG, "TEST : extractCurrentWeatherFromJSON() called ...");

        //check if the JSON response is empty or null, then exit.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        //create empty CurrentWeather object
        CurrentWeather currentWeather = null;

// Try to parse the JSON response
        try {
            //create JSON Object from JSON response string
            JSONObject baseJSONResponse = new JSONObject(jsonResponse); // throw JSONException

            JSONArray weatherJSONArray = baseJSONResponse.getJSONArray("weather");
            JSONObject weatherJSONObject = weatherJSONArray.getJSONObject(0);
            String weatherDescription = weatherJSONObject.getString("description");
            String weatherIcon = weatherJSONObject.getString("icon");

            JSONObject sysJSONObject = baseJSONResponse.getJSONObject("sys");
            String countryName = sysJSONObject.getString("country");
            String currentLocationNmae = baseJSONResponse.getString("name");
            String weatherLocation = countryName + "/" + currentLocationNmae;

            long currentTime = baseJSONResponse.getLong("dt");

            JSONObject mainJSONObject = baseJSONResponse.getJSONObject("main");
            int currentTemp = mainJSONObject.getInt("temp");
            double currentHumidity = mainJSONObject.getDouble("humidity");

            JSONObject windJSONObject = baseJSONResponse.getJSONObject("wind");
            double windSpeed = windJSONObject.getDouble("speed");

            //create CurrentWeather form json response
            currentWeather = new CurrentWeather(weatherLocation, weatherIcon, currentTime,
                    currentTemp, currentHumidity, windSpeed, weatherDescription);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the weather JSON results ...", e);
        }


        return currentWeather;

    }


    /**
     * Query the openweathermap.org  data and return a list of {@link DayForecast} objects.
     *
     * @param requestUrl is a  openweathermap.org URL for requesting  forecasted weather Days
     * @return a list of forecasted weather Days  for given URL
     */
    public static List<DayForecast> fetchDailyForecastsData(String requestUrl) {
        //create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the given URL and receive JSON String response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem in making the HTTP request ...", e);

        }

        //extract the relevant fields from JSON response and create a list of {@link DayForecast} objects
        List<DayForecast> dayForecastList = extractDailyForecastsFromJSON(jsonResponse);

        //Return the list of {@link DayForecast} objects
        return dayForecastList;
    }


    /**
     * Helper method for {@link #fetchDailyForecastsData(String)}
     * <p>
     * create list of {@link DayForecast} objects that has been built up from
     * parsing given JSON response.
     *
     * @param jsonResponse is a String JSON response
     * @return list of {@link DayForecast} objects
     */
    private static List<DayForecast> extractDailyForecastsFromJSON(String jsonResponse) {
        Log.i(LOG_TAG, "TEST : extractDailyForecastsFromJSON() called ...");

        //check if the JSON response is empty or null, then exit.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        //create empty Day Weather List
        List<DayForecast> dailyForecastsList = new ArrayList<>();

        try {
            //create JSON Object from JSON response string
            JSONObject baseJSONResponse = new JSONObject(jsonResponse); // throw JSONException

            JSONArray listJSONArray = baseJSONResponse.getJSONArray("list");


            for (int i = 0; i < listJSONArray.length(); i++) {
                JSONObject currentDay = listJSONArray.getJSONObject(i);

                long time = currentDay.getLong("dt");

                JSONObject tempJSONObject = currentDay.getJSONObject("temp");
                int dayTemp = tempJSONObject.getInt("day");

                JSONArray weatherJSONArray = currentDay.getJSONArray("weather");
                JSONObject currentWeatherDescription = weatherJSONArray.getJSONObject(0);
                String description = currentWeatherDescription.getString("description");
                String iconName = currentWeatherDescription.getString("icon");

                DayForecast dayForecastData = new DayForecast(dayTemp, iconName, description, time);
                // Add the new {@link DayForecast} to the list of dayForecastList.
                dailyForecastsList.add(dayForecastData);

            }


        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the day weather JSON results ...", e);
        }


        return dailyForecastsList;
    }


    /**
     * Query the openweathermap.org data and return a list of {@link HourForecast} objects.
     *
     * @param requestUrl is a  openweathermap.org URL for forecasted weather hours
     * @return a list of forecasted weather hours for given URL
     */
    public static List<HourForecast> fetchHourlyForecastsData(String requestUrl) {
        //create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the given URL and receive JSON String response
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem in making the HTTP request ...", e);

        }

        //extract the relevant fields from JSON response and create a list of {@link HourForecast}
        List<HourForecast> hoursForecastsList = extractHourlyForecastsFromJSON(jsonResponse);

        //Return the list of {@link HourForecast}s object
        return hoursForecastsList;
    }


    /**
     * Helper method for {@link #fetchHourlyForecastsData(String)}
     * <p>
     * create list of {@link HourForecast} objects that has been built up from
     * parsing given JSON response.
     *
     * @param jsonResponse is a String JSON response
     * @return list of {@link HourForecast} objects
     * @throws JSONException
     */
    private static List<HourForecast> extractHourlyForecastsFromJSON(String jsonResponse) {
        Log.i(LOG_TAG, "TEST : extractHourlyForecastsFromJSON() called ...");

        //check if the JSON response is empty or null, then exit.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        //create empty Hour Weather List
        List<HourForecast> hoursForecastsList = new ArrayList<>();

        try {
            //create JSON Object from JSON response string
            JSONObject baseJSONResponse = new JSONObject(jsonResponse); // throw JSONException


            JSONArray hoursDataJSONArray = baseJSONResponse.getJSONArray("list");

            //for each hour in hoursDataJSONArray , create an {@link HourForecast} object
            for (int i = 0; i < hoursDataJSONArray.length(); i++) {

                JSONObject currentHourData = hoursDataJSONArray.getJSONObject(i);
                long time = currentHourData.getLong("dt");

                JSONObject mainJSONObject = currentHourData.getJSONObject("main");
                int hourTemp = mainJSONObject.getInt("temp");

                JSONArray weatherJSONArray = currentHourData.getJSONArray("weather");

                JSONObject weatherJSONObject = weatherJSONArray.getJSONObject(0);
                String iconName = weatherJSONObject.getString("icon");
                String hourDescription = weatherJSONObject.getString("description");


                HourForecast hourForecastData = new HourForecast(hourTemp, iconName, hourDescription, time);

                // Add the new {@link HourForecast} to the list of hourForecastList.
                hoursForecastsList.add(hourForecastData);
            }


        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the hours weather JSON results ...", e);
        }


        return hoursForecastsList;
    }


    /**
     * Helper method for {@link #fetchCurrentWeatherData(String)} ,
     * {@link #fetchDailyForecastsData(String)} (String)},
     * and {@link #fetchHourlyForecastsData(String)} .
     * <p>
     * <p>
     * return new URL object from the given String URL
     *
     * @param stringUrl is the given url string
     * @return new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.v(LOG_TAG, "Problem with building the URL object", e);
        }
        return url;
    }

    /**
     * Helper method for  {@link #fetchCurrentWeatherData(String)},
     * {@link #fetchDailyForecastsData(String)} (String)},
     * and {@link #fetchHourlyForecastsData(String)}.
     * <p>
     * Make an HTTP request to the given URL and return a String as the response .
     *
     * @param url is the given URL object
     * @return JSON String as response
     * @throws IOException
     */
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = null;

        //if the URL is null , then exit
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            //setup HTTP request
            urlConnection = (HttpURLConnection) url.openConnection(); //throw IOException
            urlConnection.setConnectTimeout(15000 /*milliseconds*/);
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //check if request was successful , then read the input stream
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem in retrieving the earthquake JSON results ...", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close(); //throws IOException
            }
        }

        return jsonResponse;

    }

    /**
     * Helper method for {@link #makeHttpRequest(URL)}
     * <p>
     * Convert the {@link InputStream} object into String contain whole JSON
     * response from the server.
     *
     * @return JSON response String from InputStream object
     */
    private static String readFromInputStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();

        //check if the inputStream has the response data
        if (inputStream != null) {

            //create character streams from JSON InputStream response
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            //read line of JSON response from BufferedReader
            String line = bufferedReader.readLine(); //throws IOException

            //check if the line  not null , then append to StringBuilder
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();

            }

        }
        //return JSON response String
        return output.toString();
    }


}
