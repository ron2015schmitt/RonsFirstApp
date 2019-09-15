package com.rfschmitt.myapplication;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import	android.graphics.drawable.Drawable;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        Calendar c = Calendar.getInstance();
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE");
        String dayOfWeek = dayOfWeekFormat.format(c.getTime()).toUpperCase();
        TextView dayOfWeekTV = findViewById(R.id.dayOfWeek);
        dayOfWeekTV.setText(dayOfWeek);

        SimpleDateFormat dayAndMonthFormat = new SimpleDateFormat("MMM dd");
        String dayAndMonth = dayAndMonthFormat.format(c.getTime());
        TextView dayAndMonthTV = findViewById(R.id.dayAndMonth);
        dayAndMonthTV.setText(dayAndMonth);

        String currentTemp = "102";
        TextView temperatureTV = findViewById(R.id.temperature);
        temperatureTV.setText(currentTemp);

        String temperatureDot = "o";
        TextView temperatureDotTV = findViewById(R.id.temperatureDot);
        temperatureDotTV.setText(temperatureDot);

        String location = "Silver Spring, MD";
        TextView locationTV = findViewById(R.id.location);
        locationTV.setText(location);

        String conditions = "Clear";
        TextView conditionsTV = findViewById(R.id.conditions);
        conditionsTV.setText(conditions);

        ImageView nowImage = findViewById(R.id.imageNow);
        nowImage.setImageResource(R.drawable.ic_moonwithclouds);


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.println(Log.INFO,"MainActivity","No permission at this point");
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Log.println(Log.INFO,"MainActivity"," Show an explanation to the user");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                Log.println(Log.INFO,"MainActivity","No explanation needed; request the permission");

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

           }
        } else {
            // Permission has already been granted
            Log.println(Log.INFO,"MainActivity","Permission has already been granted");

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        String s = "permissions result received: requestCode="+Integer.toString(requestCode);
        s += "  grantResults.length="+Integer.toString(grantResults.length);
        Log.println(Log.INFO,"MainActivity",s);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.println(Log.INFO,"MainActivity","Permission granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Log.println(Log.INFO,"MainActivity","Permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
