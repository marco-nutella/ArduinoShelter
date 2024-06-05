package com.example.raduinoandroid;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class TemperatureActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        ImageButton buttonBack = findViewById(R.id.back_button);
        buttonBack.setOnClickListener(v -> startActivity(new Intent(TemperatureActivity.this, MainActivity.class)));



    }
}

/* info = new Info("no_id", 10, 20, 20.0, 30.0);
        try {
            RetrofitInfoInterface aa =
                    RetrofitService.getRetrofit().create(RetrofitInfoInterface.class);
            Log.e(TAG, "MOMMENT OF COCAINE: YIASDDKJASHDUIASI");
            aa.GetAll().enqueue(new Callback<ArrayList<Info>>() {
                @Override
                public void onResponse(Call<ArrayList<Info>> call, Response<ArrayList<Info>> response) {
                    infe = response.body();
                }

                @Override
                public void onFailure(Call<ArrayList<Info>> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            System.out.println(e);
        }*/