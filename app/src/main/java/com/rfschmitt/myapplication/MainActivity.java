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
        SimpleDateFormat dateformat1 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE");
        String datetime1 = dateformat1.format(c.getTime());
        String dayOfWeek = dayOfWeekFormat.format(c.getTime()).toUpperCase();
        TextView dayOfWeekTV = (TextView) findViewById(R.id.dayOfWeek);
        dayOfWeekTV.setText(dayOfWeek);
    }
}
