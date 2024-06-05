package com.example.raduinoandroid;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class TemperatureActivity extends AppCompatActivity {
    int maxTemperature = 20;
    private Switch switchVentilation, switchAutoVentilation;
    private EditText editTextNumber;

    private TextView maxTemperatureText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        ImageButton buttonBack = findViewById(R.id.back_button);
        switchVentilation = findViewById(R.id.switch_ventilation);
        switchAutoVentilation = findViewById(R.id.switch_auto_ventilation);
        Button incrementTemperature = findViewById(R.id.button_increment);
        Button decreementTemperature = findViewById(R.id.button_decrement);
        Button confirmButton = findViewById(R.id.button_update);
        maxTemperatureText = findViewById(R.id.max_temperature_text);
        editTextNumber = findViewById(R.id.editTextNumber);

        editTextNumber.setText(String.valueOf(maxTemperature));


        switchVentilation.setOnClickListener(v -> {
                boolean isChecked = switchVentilation.isChecked();
                if (isChecked) {
                    // Switch is now checked
                    //sendBluetoothMessage("Ventilation ON 1");
                } else {
                    // Switch is now unchecked
                    //sendBluetoothMessage("Ventilation OFF 1");
                }
        });

        switchAutoVentilation.setOnClickListener(v -> {
            boolean isChecked = switchAutoVentilation.isChecked();
            if (isChecked) {
                // Switch is now checked
                //sendBluetoothMessage("AutoVentilation ON 1");
            } else {
                // Switch is now unchecked
                //sendBluetoothMessage("AutoVentilation OFF 1");
            }
        });




        decreementTemperature.setOnClickListener(v -> {maxTemperature--;
            editTextNumber.setText(String.valueOf(maxTemperature));
        });

        editTextNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used
            }
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                try {
                    maxTemperature = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    // Handle the exception if the input is not a valid number
                    Toast.makeText(TemperatureActivity.this, "Invalid number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        incrementTemperature.setOnClickListener(v -> {maxTemperature++;
            editTextNumber.setText(String.valueOf(maxTemperature));
        });

        confirmButton.setOnClickListener(v -> {
            String text = "Max Temperature: "+ maxTemperature + " C";

            maxTemperatureText.setText(text);
            text = "Temperature MaxTemperature " + maxTemperature;
            //sendBluetoothMessage(text);
        });


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