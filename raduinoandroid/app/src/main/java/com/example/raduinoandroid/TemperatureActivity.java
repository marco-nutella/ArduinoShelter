package com.example.raduinoandroid;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;


public class TemperatureActivity extends AppCompatActivity {
    int maxTemperature = 20;
    private Switch switchVentilation, switchAutoVentilation;
    private EditText editTextNumber;

    private TextView maxTemperatureText;

    private static final String TAG = "FrugalLogs";

    //We will use a Handler to get the BT Connection statys
    public static Handler handler;


    @RequiresApi(api = Build.VERSION_CODES.M)

    private BluetoothService bluetoothService;
    private boolean isBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            bluetoothService = binder.getService();
            isBound = true;
            initializeBluetooth();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

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
                if (isBound) {
                    try {
                        bluetoothService.sendBluetoothMessage("Ventilation ON 1");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                if (isBound) {
                    try {
                        bluetoothService.sendBluetoothMessage("Ventilation OFF 1");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        switchAutoVentilation.setOnClickListener(v -> {
            boolean isChecked = switchAutoVentilation.isChecked();
            if (isChecked) {
                if (isBound) {
                    try {
                        bluetoothService.sendBluetoothMessage("AutoVentilation ON 1");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                if (isBound) {
                    try {
                        bluetoothService.sendBluetoothMessage("AutoVentilation OFF 1");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });


        decreementTemperature.setOnClickListener(v -> {
            maxTemperature--;
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

        incrementTemperature.setOnClickListener(v -> {
            maxTemperature++;
            editTextNumber.setText(String.valueOf(maxTemperature));
        });

        confirmButton.setOnClickListener(v -> {
            String text = "Max Temperature: " + maxTemperature + " C";

            maxTemperatureText.setText(text);
            text = "Temperature MaxTemperature " + maxTemperature;
            if (isBound) {
                try {
                    bluetoothService.sendBluetoothMessage(text);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        ImageButton buttonBT = findViewById(R.id.button_bt);
        buttonBT.setOnClickListener(view -> {
            if (isBound) {
                bluetoothService.startBluetoothConnection(handler);
            }
            Log.d(TAG, "Button Pressed");
        });

        buttonBack.setOnClickListener(v -> startActivity(new Intent(TemperatureActivity.this, MainActivity.class)));
    }

    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    private void initializeBluetooth() {
        if (isBound) {
            bluetoothService.startBluetoothConnection(handler);
        }
    }
}
