package com.example.yazan.a24hourforecast.activities;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yazan.a24hourforecast.CurrentWeatherLoader;
import com.example.yazan.a24hourforecast.R;
import com.example.yazan.a24hourforecast.data.CurrentWeather;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<CurrentWeather>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, android.location.LocationListener {


    /** Tag for log messages */
    private static final String LOG_TAG = MainActivity.class.getName();

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    /**
     * Weather attribute
     */
    TextView mLocationTextView;
    ImageView mWeatherIconImageView;
    TextView mTimeTextView;
    TextView mTemperatureTextView;
    TextView mHumidityTextView;
    TextView mWindSpeedTextView;
    TextView mDescriptionTextView;

    /**
     * openweathermap URL website for Current Weather data
     */
    String mUrl = "http://api.openweathermap.org/data/2.5/weather";

    /**
     * Current Weather Data  from given URL
     */
    CurrentWeather mCurrentWeather;


    /**
     * Loader Manager TO Load Weather Data in background thread
     */
    LoaderManager mLoaderManager;
    /**
     * refresh icon to refresh weather data
     */
    ImageView mRefreshImageView;
    /**
     * Progress Bar show when there loading data
     */
    ProgressBar mProgressBar;

    /**
     * Latitude && Longitude of current Location
     */
    private double mLatitudeValue;
    private double mLongitudeValue;

    /**
     * Intent Filter for Network State
     */
    private IntentFilter mNetworkStateChangedFilter;
    /**
     * Broadcast Receiver for Network State
     */
    private BroadcastReceiver mNetworkStateIntentReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find and attach  Weather attribute
        mLocationTextView = (TextView) findViewById(R.id.location_text_view);
        mWeatherIconImageView = (ImageView) findViewById(R.id.weather_icon_Image_View);
        mTimeTextView = (TextView) findViewById(R.id.time_text_view);
        mTemperatureTextView = (TextView) findViewById(R.id.temperature_text_view);
        mHumidityTextView = (TextView) findViewById(R.id.humidity_text_view);
        mWindSpeedTextView = (TextView) findViewById(R.id.wind_speed_text_view);
        mDescriptionTextView = (TextView) findViewById(R.id.description_text_view);

        //find and attach  hourly && daily Buttons
        Button hourlyButton = (Button) findViewById(R.id.hourly_button);
        Button dailyButton = (Button) findViewById(R.id.daily_button);

        //find and attach refresh icon image
        mRefreshImageView = (ImageView) findViewById(R.id.refresh_image_view);

        //find and attach Progress Bar
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // set gone to  Refresh icon Image
        mRefreshImageView.setVisibility(View.GONE);


        // Create new Intent Filter and add action to this filter
        mNetworkStateChangedFilter = new IntentFilter();
        mNetworkStateChangedFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        // Receive the Broadcast
        mNetworkStateIntentReceiver = new BroadcastReceiver() {
            private boolean firstTime = true;

            @Override
            public void onReceive(Context context, Intent intent) {

                if (isWifiOn()) {
                    Toast.makeText(MainActivity.this,"WIFI ON, YOU CAN REFRESH NOW !",Toast.LENGTH_LONG).show();
                    mRefreshImageView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);

                } else {
                    if (firstTime) {
                        Toast.makeText(MainActivity.this, "NO INTERNET CONNECTION", Toast.LENGTH_LONG).show();
                        firstTime = false;
                    } else {
                        firstTime = true;
                    }

                }

            }
        };


        //set On ClickListener for  hourly Button
        hourlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HourlyForecastsActivity.class);

                String hourURL = "http://api.openweathermap.org/data/2.5/forecast";

                Uri baseUri = Uri.parse(hourURL);
                Uri.Builder uriBuilder = baseUri.buildUpon();

                uriBuilder.appendQueryParameter("lat", String.valueOf(mLatitudeValue));
                uriBuilder.appendQueryParameter("lon", String.valueOf(mLongitudeValue));
                uriBuilder.appendQueryParameter("cnt", "15");
                uriBuilder.appendQueryParameter("units", "metric");
                uriBuilder.appendQueryParameter("APPID", "e9debb96cbde748e9de4d4ad0e3c7923");
                intent.putExtra("hourUrl", uriBuilder.toString());

                startActivity(intent);

            }
        });

        //set On ClickListener for  daily Button
        dailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, DailyForecastsActivity.class);
                String dayURL = "http://api.openweathermap.org/data/2.5/forecast/daily";

                Uri baseUri = Uri.parse(dayURL);
                Uri.Builder uriBuilder = baseUri.buildUpon();

                uriBuilder.appendQueryParameter("lat", String.valueOf(mLatitudeValue));
                uriBuilder.appendQueryParameter("lon", String.valueOf(mLongitudeValue));
                uriBuilder.appendQueryParameter("cnt", "14");
                uriBuilder.appendQueryParameter("units", "metric");
                uriBuilder.appendQueryParameter("APPID", "e9debb96cbde748e9de4d4ad0e3c7923");
                intent.putExtra("dayUrl", uriBuilder.toString());

                startActivity(intent);
            }
        });


        //set On ClickListener for  Refresh Image View
        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isWifiOn()) {
                    Toast.makeText(MainActivity.this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();

                } else if (isGPSOn()) {

                    mRefreshImageView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            buildGoogleApiClient();
                            mGoogleApiClient.connect();


                        }
                    }, 4000/* 5 sec */);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mGoogleApiClient.isConnected()) {
                                reloadWeatherData();
                            }
                            shakeViews();

                        }
                    }, 6500/* 5 sec */);

                } else {
                    Toast.makeText(MainActivity.this, "Make Sure Your GPS is ON", Toast.LENGTH_LONG).show();

                }

            }
        });

        //get instance from LoaderManager
        mLoaderManager = getSupportLoaderManager();

        //ask for ACCESS_FINE_LOCATION && ACCESS_COARSE_LOCATION permissions
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        askForPermission(permissions, 1);





    }

    /**
     * Helper method for {@link #onCreate(Bundle)}  method
     * */
    private void askForPermission(String[] permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission[0]) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, permission[1]) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission[0]) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission[1])) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, permission, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, permission, requestCode);
            }
        } else {
            //check if there is an internet && GPS TO Load Data OR NOT
            if (isWifiOn() && isGPSOn()) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buildGoogleApiClient();
                        mGoogleApiClient.connect();
                    }


                }, 3000/* 3 sec */);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mGoogleApiClient.isConnected()) {
                            reloadWeatherData();
                        }

                    }
                }, 6000/* 6 sec */);


            } else if (!isWifiOn() || !isGPSOn()) {
                if (!isWifiOn()) {
                    Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
                    registerReceiver(mNetworkStateIntentReceiver, mNetworkStateChangedFilter);

                }
                if (!isGPSOn()) {
                    Toast.makeText(this, "Make Sure Your GPS is ON", Toast.LENGTH_LONG).show();
                }

            }

            // get LocationManager instance for listening to GPS enabled OR disable
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            //register GPS Listener callbacks
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        }
    }

    /**
     * Handle result of requesting Permissions for android marshmallow (API 23 AND ABOVE)
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //check if there is an internet && GPS TO Load Data OR NOT
                    if (isWifiOn() && isGPSOn()) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                buildGoogleApiClient();
                                mGoogleApiClient.connect();
                            }


                        }, 3000/* 3 sec */);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mGoogleApiClient.isConnected()) {
                                    reloadWeatherData();
                                }

                            }
                        }, 6000/* 6 sec */);


                    } else if (!isWifiOn() || !isGPSOn()) {
                        if (!isWifiOn()) {
                            registerReceiver(mNetworkStateIntentReceiver, mNetworkStateChangedFilter);

                        }
                        if (!isGPSOn()) {
                            Toast.makeText(this, "Make Sure Your GPS is ON", Toast.LENGTH_LONG).show();
                        }

                    }

                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


                } else {

                    // permission denied,
                }
                return;
            }


        }
    }

    @Override
    public Loader<CurrentWeather> onCreateLoader(int id, Bundle args) {
        Log.i("onCreateLoader", "onCreateLoader onCreateLoader onCreateLoader");

        return new CurrentWeatherLoader(this, mUrl);
    }

    @Override
    public void onLoadFinished(Loader<CurrentWeather> loader, CurrentWeather data) {
        Log.i("onLoadFinished", "onLoadFinished onLoadFinished onLoadFinished");
        mRefreshImageView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);

        if (data != null) {
            mCurrentWeather = data;

            mLocationTextView.setText(mCurrentWeather.getLocation());

            int iconImageResourceId = getProperIcon(mCurrentWeather.getIconName());
            mWeatherIconImageView.setImageResource(iconImageResourceId);


            Date currentDate = new Date(mCurrentWeather.getTime() /* in seconds */ * 1000);
            String currentDateString = formatTime(currentDate);
            mTimeTextView.setText(currentDateString);

            mTemperatureTextView.setText(mCurrentWeather.getTemperature() + "");
            mHumidityTextView.setText(mCurrentWeather.getHumidity() + "");
            mWindSpeedTextView.setText(mCurrentWeather.getWindSpeed() + "");
            mDescriptionTextView.setText(mCurrentWeather.getDescription());
        }

    }

    @Override
    public void onLoaderReset(Loader<CurrentWeather> loader) {
        Log.i("onLoaderReset", "onLoaderReset onLoaderReset onLoaderReset");

        mCurrentWeather = new CurrentWeather(null, null, 0, 0, 0, 0, null);
    }

    /**
     * Helper method for {@link #onLoadFinished(Loader, CurrentWeather)} method
     * <p>
     * Create formatted time form unix time
     *
     * @param currentDate is a Date object from unix time
     * @return formatted time
     */
    public String formatTime(Date currentDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd.MM.yyyy");
        return simpleDateFormat.format(currentDate);


    }

    /**
     * Helper method for {@link #onLoadFinished(Loader, CurrentWeather)} method
     * <p>
     * Choose correct icon from given weather state
     *
     * @param iconName is a name of icon represent weather state
     * @return Proper icon resource ID for given icon name
     */
    private int getProperIcon(String iconName) {
        int iconImageResourceId = 0;
        switch (iconName) {
            case "01d":
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



    /**
     * Helper method for {@link #onCreate(Bundle)} method
     * <p>
     * Reload Current Weather Data from TheGiven URL
     */
    private void reloadWeatherData() {
        buildWeatherUrl();
        mLoaderManager.initLoader(1, null, this);
    }


    /**
     * Helper method for {@link #reloadWeatherData()} method
     * <p>
     * Build Current Weather URL
     */
    private void buildWeatherUrl() {
        Log.d("buildWeatherUrl","buildWeatherUrl buildWeatherUrl");
        Uri baseUri = Uri.parse(mUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("lat", String.valueOf(mLatitudeValue));
        uriBuilder.appendQueryParameter("lon", String.valueOf(mLongitudeValue));
        uriBuilder.appendQueryParameter("units", "metric");
        uriBuilder.appendQueryParameter("APPID", "f9e95f74625fb06fca58b2fbfc384ebb");
        mUrl = uriBuilder.toString();

    }

    /**
     * Helper method for {@link #onCreate(Bundle)} method
     * <p>
     * Check if the GPS ON OR OFF
     *
     * @return true(GPS ON) OR false(GPS OFF)
     */
    private boolean isGPSOn() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    /**
     * Helper method for {@link #onCreate(Bundle)} method
     * <p>
     * Check if the internet ON OR OFF
     *
     * @return true(INTERNET ON) OR false(INTERNET OFF)
     */
    private boolean isWifiOn() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Helper method for {@link #onCreate(Bundle)} method
     * <p>
     * attach shaking animation to the Views
     */
    private void shakeViews() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        mLocationTextView.startAnimation(shake);
        mWeatherIconImageView.startAnimation(shake);
        mTemperatureTextView.startAnimation(shake);
        mWindSpeedTextView.startAnimation(shake);
        mHumidityTextView.startAnimation(shake);
        mDescriptionTextView.startAnimation(shake);

    }


    protected synchronized void buildGoogleApiClient() {
Log.d(LOG_TAG," TEST : buildGoogleApiClient()  Called... ");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        Log.d(LOG_TAG," TEST : createLocationRequest()  Called... ");

        mLocationRequest = new LocationRequest();

        //mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

       // mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }



    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    private void updateLocationCoordinates() {
        Log.d(LOG_TAG," TEST : updateLocationCoordinates()  Called... ");

        mLatitudeValue = mCurrentLocation.getLatitude() ;
        mLongitudeValue = mCurrentLocation.getLongitude() ;

    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG," TEST : onConnected()  Called... ");

        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            updateLocationCoordinates();
        }else {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            updateLocationCoordinates();
        }



    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(LOG_TAG," TEST : onProviderEnabled()  Called... ");

        mProgressBar.setVisibility(View.GONE);
        mRefreshImageView.setVisibility(View.VISIBLE);
        Toast.makeText(this,"GPS ON, YOU CAN REFRESH NOW !",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
