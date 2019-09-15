package com.rfschmitt.weatherone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    LocationManager locationManager;
    Criteria criteria;
    Context context;
    String bestProvider;
    Location location = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context = getApplicationContext();


        Calendar c = Calendar.getInstance();
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE");
        String dayOfWeek = dayOfWeekFormat.format(c.getTime()).toUpperCase();
        TextView dayOfWeekTV = findViewById(R.id.dayOfWeek);
        dayOfWeekTV.setText(dayOfWeek);

        SimpleDateFormat dayAndMonthFormat = new SimpleDateFormat("MMM dd");
        String dayAndMonth = dayAndMonthFormat.format(c.getTime());
        TextView dayAndMonthTV = findViewById(R.id.dayAndMonth);
        dayAndMonthTV.setText(dayAndMonth);

        String currentTemp = "75";
        TextView temperatureTV = findViewById(R.id.temperature);
        temperatureTV.setText(currentTemp);

        String temperatureDot = "o";
        TextView temperatureDotTV = findViewById(R.id.temperatureDot);
        temperatureDotTV.setText(temperatureDot);

        String locationString = "Silver Spring, MD";
        TextView locationTV = findViewById(R.id.location);
        locationTV.setText(locationString);

        String conditions = "Clear";
        TextView conditionsTV = findViewById(R.id.conditions);
        conditionsTV.setText(conditions);

        ImageView nowImage = findViewById(R.id.imageNow);
        nowImage.setImageResource(R.drawable.ic_moonwithclouds);

        getLocation();

    }

    public void requestPermissionsFromUser() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.println(Log.INFO, "MainActivity", "No permission at this point");
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    ACCESS_COARSE_LOCATION)) {
                Log.println(Log.INFO, "MainActivity", " Show an explanation to the user");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                Log.println(Log.INFO, "MainActivity", "No explanation needed; request the permission");

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
        } else {
            // Permission has already been granted
            Log.println(Log.INFO, "MainActivity", "Location Permission has already been granted");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        String s = "permissions result received: requestCode=" + Integer.toString(requestCode);
        s += "  grantResults.length=" + Integer.toString(grantResults.length);
        Log.println(Log.INFO, "MainActivity", s);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.println(Log.INFO, "MainActivity", "Permission granted");
                    // permission was granted, yay! Do the
                    getLocation();
                } else {
                    Log.println(Log.INFO, "MainActivity", "Permission denied");
                    // permission denied, boo! Disable the
                    setDisplayedLocation();
                    return;
                }

                // other 'case' lines to check for other
                // permissions this app might request.
            }
        }
    }

        @SuppressLint("MissingPermission")
        protected void getLocation() {
            if (!isLocationPermitted()) {
                requestPermissionsFromUser();
            }
            if (isLocationEnabled(context)) {
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                criteria = new Criteria();
                bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

                // permissions and GPS checked in isLocationEnabled
                Location newLocation = null;

                try {

                    newLocation = locationManager.getLastKnownLocation(bestProvider);
                } catch (Exception e){

                }

                 this.location = newLocation;
                setDisplayedLocation();
                if (location != null) {
                    Log.println(Log.INFO, "GPS", "is ON");
                }
                else{
                    // TODO:
                    //locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
                }
            }
            else {

            }
        }

        @Override
        public void onLocationChanged(Location location) {
            //remove location callback:
//        locationManager.removeUpdates(this);

            //open the map:
            this.location = location;
            setDisplayedLocation();


        }

        public void setDisplayedLocation() {
            TextView locationTV = findViewById(R.id.location);
            //TODO: place error/info messages in a separate TextView
            if (!isLocationPermitted()) {
                locationTV.setText("Need user permission for location services");
                return;
            }
            if (location == null) {
                locationTV.setText("Location (GPS) services unavailable");
                return;
            }

            double latitude = location.getLatitude();
            double longitude = location.getLatitude();
            //Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
            String locationString = Utils.getLocationText(location, context);
            Log.println(Log.INFO, "GPS", "location="+locationString);
            locationTV.setText(locationString);
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        public boolean isLocationPermitted() {
            return (ContextCompat.checkSelfPermission(MainActivity.this, ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED);
        }


        public boolean isLocationEnabled(Context context) {
            int locationMode = 0;
            String locationProviders;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                try {
                    locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }

                return locationMode != Settings.Secure.LOCATION_MODE_OFF;

            }else{
                locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                return !TextUtils.isEmpty(locationProviders);
            }


        }
    }
