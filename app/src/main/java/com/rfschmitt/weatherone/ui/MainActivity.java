package com.rfschmitt.weatherone.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;


import com.rfschmitt.weatherone.Injection;
import com.rfschmitt.weatherone.persistence.R;
import com.rfschmitt.weatherone.Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;





public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = MainActivity.class.getSimpleName();

    private ViewModelFactory mViewModelFactory;
    private UserViewModel mViewModel;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private ImageView mCurrentConditions;
    private ImageView mUpdateButton;

    // testing ROOM DB
    private TextView mUserName;
    private EditText mUserNameInput;
    private Button mGoButton;



    private LocationManager locationManager;
    private Criteria criteria;
    private Context context;
    private String bestProvider;
    private Location location = null;

    public enum Conditions {
        UNKNOWN(" "),
        SUNNY("Sunny"),
        CLEARNIGHT("Clear"),
        PARTLYCLOUDYNIGHT("Partly Cloudy"),
        PARTLYSUNNY("Partly Sunny"),
        CLOUDY("Cloudy"),
        RAINY("Rain"),
        SNOW("Snow"),
        THUNDERSTORM("Thunderstorm"),
        WINDY("Wind");

        private final String label;
        public String toString(){
            return label;
        }
        private Conditions(String label) {
            this.label = label;
        }
    }

    public static class CurrentConditionsAndTemp {
        static Integer tempF = null;
        static Conditions conditions = Conditions.CLEARNIGHT;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = getApplicationContext();

        mViewModelFactory = Injection.provideViewModelFactory(this);
        mViewModel = new ViewModelProvider(this, mViewModelFactory).get(UserViewModel.class);

        mCurrentConditions = findViewById(R.id.imageCurrentConditions);
        mUpdateButton = findViewById(R.id.updateDataButton);

        // ROOM testing components
        mUserName = findViewById(R.id.user_name);
        mUserNameInput = findViewById(R.id.user_name_input);
        mGoButton = findViewById(R.id.goButton);

        mGoButton.setOnClickListener(v -> updateUserName());
        mGoButton.setEnabled(true);

        // TODO: remove these
        CurrentConditionsAndTemp.tempF = 77;
        CurrentConditionsAndTemp.conditions = Conditions.CLEARNIGHT;

        setInfoUpdating();
        setDisplayedLocation();
        setDisplayedDayAndDate();
        getLocation();

        mUserName.setText("joe");

    }



    @Override
    protected void onStart() {
        super.onStart();
        // Subscribe to the emissions of the user name from the view model.
        // Update the user name text view, at every onNext emission.
        // In case of error, log the exception.
        Log.println(Log.INFO, TAG, "onStart()");

        mDisposable.add(mViewModel.getUserName()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userName -> mUserName.setText(userName),
                        throwable -> Log.e(TAG, "Unable to update username", throwable)));
    }


    @Override
    protected void onResume() {
        super.onResume();
        //careful what you put in this method
        setInfoUpdating();
        setDisplayedDayAndDate();
        getLocation();

    }

    @Override
    protected void onStop() {
        super.onStop();

        // clear all the subscriptions
        mDisposable.clear();
    }


    //------------------------------------------------------
    //  permissions
    //------------------------------------------------------


    public void requestPermissionsFromUser() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.println(Log.INFO, TAG, "No permission at this point");
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    ACCESS_COARSE_LOCATION)) {
                Log.println(Log.INFO, TAG, " Show an explanation to the user");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                Log.println(Log.INFO, TAG, "No explanation needed; request the permission");

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
        } else {
            // Permission has already been granted
            Log.println(Log.INFO, TAG, "Location Permission has already been granted");
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
                    setAllDisplay();
                    return;
                }

                // other 'case' lines to check for other
                // permissions this app might request.
            }
        }
    }


    //------------------------------------------------------
    //  LocationListener
    //------------------------------------------------------

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
            } catch (Exception e) {

            }

            this.location = newLocation;
            setAllDisplay();
            if (location != null) {
                Log.println(Log.INFO, "GPS", "is ON");
                locationManager.requestLocationUpdates(bestProvider, 60000, 0, this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //remove location callback:
//        locationManager.removeUpdates(this);
        int permission_denied = Log.println(Log.INFO, TAG, "Location Changed");
        this.location = location;
        setAllDisplay();

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    //----------------------------------------------------------------
    // forecast related
    //----------------------------------------------------------------

    private void getForecast() {
    }



    //----------------------------------------------------------------
    // UI  update methods
    //----------------------------------------------------------------




    public void updateUserName() {
        String userName = mUserNameInput.getText().toString();
        Log.println(Log.INFO,TAG, "Button click username="+userName);
        // Disable the update button until the user name update has been done
        mGoButton.setEnabled(false);
        // Subscribe to updating the user name.
        // Re-enable the button once the user name has been updated
        mDisposable.add(mViewModel.updateUserName(userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> mGoButton.setEnabled(true),
                        throwable -> Log.e(TAG, "Unable to update username", throwable)));
    }

    public void setAllDisplay() {
        setDisplayedLocation();
        setDisplayedDayAndDate();
        setInfoUpdateTime();
        setDisplayCurrentConditions();
    }

    public void setDisplayedLocation() {
        TextView locationTV = findViewById(R.id.location);
        TextView infoTV = findViewById(R.id.info);
        if(location == null) {
            locationTV.setText("—");
        } else {
            double latitude = location.getLatitude();
            double longitude = location.getLatitude();
            //Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
            String locationString = Utils.getLocationText(location, context);
            Log.println(Log.INFO, "GPS", "location=" + locationString);
            locationTV.setText(locationString);
        }

        if (!isLocationPermitted()) {
            infoTV.setText("Need permission for location services");
            return;
        }
        if (!isLocationEnabled(context)) {
            infoTV.setText("Location (GPS) services unavailable");
            return;
        }

    }

    void setDisplayedDayAndDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE");
        String dayOfWeek = dayOfWeekFormat.format(c.getTime()).toUpperCase();
        TextView dayOfWeekTV = findViewById(R.id.dayOfWeek);
        dayOfWeekTV.setText(dayOfWeek);

        SimpleDateFormat dayAndMonthFormat = new SimpleDateFormat("MMM dd");
        String dayAndMonth = dayAndMonthFormat.format(c.getTime());
        TextView dayAndMonthTV = findViewById(R.id.dayAndMonth);
        dayAndMonthTV.setText(dayAndMonth);

    }


    void setDisplayCurrentConditions() {



        String currentTemp =  "";
        if (CurrentConditionsAndTemp.tempF !=null) {
            currentTemp = Integer.toString(CurrentConditionsAndTemp.tempF);
        }
        TextView temperatureTV = findViewById(R.id.temperature);
        temperatureTV.setText(currentTemp);

        String conditions = "";
        if (CurrentConditionsAndTemp.conditions != Conditions.UNKNOWN) {
            conditions = CurrentConditionsAndTemp.conditions.toString();
        }
        TextView conditionsTV = findViewById(R.id.conditions);
        conditionsTV.setText(conditions);


        Log.println(Log.INFO, "MainActivity", "setDisplayCurrentConditions temp="+currentTemp+"  conditions="+conditions);

        switch (CurrentConditionsAndTemp.conditions) {
            case UNKNOWN:
                mCurrentConditions.setImageResource(R.drawable.ic_hourglass);
                break;
            case SUNNY:
                mCurrentConditions.setImageResource(R.drawable.ic_sunny);
                break;
            case CLEARNIGHT:
                Log.println(Log.INFO, "MainActivity", "case CLEARNIGHT");
                mCurrentConditions.setImageResource(R.drawable.ic_moon);
                break;
            case PARTLYCLOUDYNIGHT:
                mCurrentConditions.setImageResource(R.drawable.ic_moonwithclouds);
                break;
            case PARTLYSUNNY:
                mCurrentConditions.setImageResource(R.drawable.ic_partlysunny);
                break;
            case CLOUDY:
                mCurrentConditions.setImageResource(R.drawable.ic_clouds);
                break;
            case RAINY:
                mCurrentConditions.setImageResource(R.drawable.ic_rain);
                break;
            case SNOW:
                mCurrentConditions.setImageResource(R.drawable.ic_snow);
                break;
            case THUNDERSTORM:
                mCurrentConditions.setImageResource(R.drawable.ic_thunderstorm);
                break;
            case WINDY:
                mCurrentConditions.setImageResource(R.drawable.ic_wind);
                break;
        }



    }


    void setInfoUpdateTime() {
        Calendar c = Calendar.getInstance();
        // add an extra space since italics otherwise get cut off
        SimpleDateFormat dayAndMonthFormat = new SimpleDateFormat("h:mma MMM dd z ");
        String updateString = "Last updated at ";
        updateString +=dayAndMonthFormat.format(c.getTime());
        TextView infoTV = findViewById(R.id.info);
        infoTV.setText(updateString);
    }


    private void setInfoUpdating() {
        TextView infoTV = findViewById(R.id.info);
        infoTV.setText("Updating...");
    }


    public void refreshAll(View view) {
        mGoButton.performClick();
        setInfoUpdating();
        getLocation();
        getForecast();
    }
}