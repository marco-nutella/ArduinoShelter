package com.example.raduinoandroid;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class RadioActivity extends AppCompatActivity {
    BottomNavigationView buttom_nav_option;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        buttom_nav_option = findViewById(R.id.bottom_nav);

        // Set Home selected
        buttom_nav_option.setSelectedItemId(R.id.navigation_radio);

        // Perform item selected listener

        buttom_nav_option.setOnItemSelectedListener(item -> {

            switch(item.getItemId())
            {
                case R.id.navigation_temperature:
                    Intent toTemperature = new Intent(getApplicationContext(),TemperatureActivity.class);
                    startActivity(toTemperature);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.navigation_radio:
                    return false;
                /*case R.id.navigation_alarm:
                    Intent toAlarm = new Intent(getApplicationContext(), AlarmActivity.class);
                    startActivity(toAlarm);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.navigation_lights:
                    Intent toMaps = new Intent(getApplicationContext(), LightsActivity.class);
                    startActivity(toMaps);
                    overridePendingTransition(0,0);
                    return true;*/
            }
            return false;
        });
    }

}