package com.rfschmitt.myapplication;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {



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

        String currentTemp = "71";
        TextView temperatureTV = findViewById(R.id.temperature);
        temperatureTV.setText(currentTemp);

        String temperatureDot = "o";
        TextView temperatureDotTV = findViewById(R.id.temperatureDot);
        temperatureDotTV.setText(temperatureDot);

        String location = "Silver Spring, MD";
        TextView locationTV = findViewById(R.id.location);
        locationTV.setText(location);

        String conditions = "Sunny";
        TextView conditionsTV = findViewById(R.id.conditions);
        conditionsTV.setText(conditions);

    }

}
