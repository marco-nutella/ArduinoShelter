package com.example.raduinoandroid;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class TemperatureActivity extends AppCompatActivity {
    BottomNavigationView button_nav_option;
    int tent_id;
    Info info;
    ArrayList<Info> infe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        info = new Info("no_id", 10, 20, 20.0, 30.0);


        try {
            RetrofitInfoInterface aa =
                    RetrofitService.getRetrofit().create(RetrofitInfoInterface.class);
            Log.e(TAG, "MOMMENT OF COCAINE: YIASDDKJASHDUIASI");
            aa.GetAll().enqueue(new Callback<ArrayList<Info>>() {
                @Override
                public void onResponse(Call<ArrayList<Info>> call, Response<ArrayList<Info>> response) {
                    infe  = response.body();
                }

                @Override
                public void onFailure(Call<ArrayList<Info>> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            System.out.println(e);
        }



        button_nav_option = findViewById(R.id.bottom_nav);

        // Set Home selected
        button_nav_option.setSelectedItemId(R.id.navigation_temperature);

        // Perform item selected listener

        button_nav_option.setOnItemSelectedListener(item -> {

            switch(item.getItemId())
            {
                case R.id.navigation_temperature:
                    return false;
                case R.id.navigation_radio:
                    Intent toFriends = new Intent(getApplicationContext(),RadioActivity.class);
                    startActivity(toFriends);
                    overridePendingTransition(0,0);
                    return true;
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