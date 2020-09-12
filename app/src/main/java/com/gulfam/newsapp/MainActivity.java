package com.gulfam.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    final String APP_ID = "d051645fc7fa6e0cdb07cc76a8660b75";
    final String API_NEWS = "54829ef79d3f4616a8e619bed308761d";

    String NEWS_URL = "http://newsapi.org/v2/top-headlines";
    String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";

    String COUNTRY = "in";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;
    final int REQUEST_CODE = 123;

    LocationManager mLocationManager;
    LocationListener mLocationListener;

    ImageView mWeatherImage;
    TextView mTemperature;
    TextView mCity,mNewsView,mNewsView1,mNewsView2,mAuthor,mTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCity = (TextView) findViewById(R.id.cityTextView);
        mWeatherImage = (ImageView) findViewById(R.id.weatherIcon);
        mTemperature = (TextView) findViewById(R.id.tempView);
        mAuthor = (TextView) findViewById(R.id.authorName);
        mTime = (TextView) findViewById(R.id.time);
        mNewsView = (TextView) findViewById(R.id.newsView);
        mNewsView1 = (TextView) findViewById(R.id.newsView1);
        mNewsView2 = (TextView) findViewById(R.id.newsView2);

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Weather", "onResume() called");
        Log.d("Weather,", "getting weather for current location");
        getWeatherForCurrentLocation();
    }

    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("weather", "onLocationChanged() callback received");
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                Log.d("Weather", "longitude is  " + longitude);
                Log.d("Weather", "latitude is  " + latitude);

                RequestParams params = new RequestParams();
                params.put("appid", APP_ID);

                params.put("lon", longitude);

                params.put("lat", latitude);


                RequestParams paramsNews = new RequestParams();
                paramsNews.put("country",COUNTRY);
                paramsNews.put("apiKey",API_NEWS);
                Log.d("Weather",""+NEWS_URL+"?country="+COUNTRY+"&apiKey="+API_NEWS);
                Log.d("Weather",""+WEATHER_URL+"?&lat="+latitude+"&lon="+longitude+"&appid="+APP_ID);
                letsDoSomeNetworking(params,paramsNews);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                Log.d("Weather", "onProviderDisabled() callback received");
            }
        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE );
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    private void letsDoSomeNetworking(final RequestParams params,RequestParams paramsNews){
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(NEWS_URL,paramsNews,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){

                Log.d("Weather","Success JSON :"+response.toString());

                NewsDataModel newsData =NewsDataModel.fromJSON(response);
                updateNewsUI(newsData);
            }

            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response){
                Log.e("Weather","Fail "+e.toString());
                Log.d("Weather","Status code "+statusCode);
                Toast.makeText(MainActivity.this,"Request Failed",Toast.LENGTH_SHORT).show();
            }
        });

        client.get(WEATHER_URL, params ,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){

                Log.d("Weather","Success JSON :"+response.toString());

                WeatherDataModel weatherData = WeatherDataModel.fromJson(response);
                updateUI(weatherData);
            }

            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response){
                Log.e("Weather","Fail "+e.toString());
                Log.d("Weather","Status code "+statusCode);
                Toast.makeText(MainActivity.this,"Request Failed",Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Weather","onRequestPermissionResult() Permission granted!");
                getWeatherForCurrentLocation();
            } else {
                Log.d("Weather","Permission Denied!");
            }
        }
    }

    private void updateNewsUI(NewsDataModel newsData){
        mNewsView.setText(newsData.getTitel());
        mNewsView1.setText(newsData.getOtherNews1());
        mNewsView2.setText(newsData.getOtherNews2());
        mTime.setText(newsData.getTime());
        mAuthor.setText(newsData.getAuthor());
        
    }
    private void updateUI(WeatherDataModel weather){
        mTemperature.setText(weather.getTemperature());
        mCity.setText(weather.getCityName());

        int resourceID = getResources().getIdentifier(weather.getIconName(),"drawable",getPackageName());

        mWeatherImage.setImageResource(resourceID);
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mLocationManager != null)
        {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }


}