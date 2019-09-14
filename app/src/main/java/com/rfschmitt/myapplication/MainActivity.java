package com.rfschmitt.myapplication;

import androidx.appcompat.app.AppCompatActivity;



import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String mydate;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE");
        String dayOfWeek = dayOfWeekFormat.format(c.getTime()).toUpperCase();
        TextView dayOfWeekTV = (TextView) findViewById(R.id.dayOfWeek);
        dayOfWeekTV.setText(dayOfWeek);

        SimpleDateFormat dayAndMonthFormat = new SimpleDateFormat("MMM dd");
        String dayAndMonth = dayAndMonthFormat.format(c.getTime());
        TextView dayAndMonthTV = (TextView) findViewById(R.id.dayAndMonth);
        dayAndMonthTV.setText(dayAndMonth);

        String currentTemp = "71";
        TextView temperatureTV = (TextView) findViewById(R.id.temperature);
        temperatureTV.setText(currentTemp);

        String temperatureDot = "o";
        TextView temperatureDotTV = (TextView) findViewById(R.id.temperatureDot);
        temperatureDotTV.setText(temperatureDot);


    }
}
