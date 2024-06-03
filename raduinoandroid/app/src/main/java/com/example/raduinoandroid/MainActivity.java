// Bluetooth functionality adapted from: https://github.com/The-Frugal-Engineer/ArduinoBTExamplev3/blob/main/app/src/main/java/com/sarmale/arduinobtexample_v3/MainActivity.java
// https://www.youtube.com/watch?v=aE8EbDmrUfQ


package com.example.raduinoandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button buttonTemperature, buttonRadio, buttonBT;
    private Switch switchAlarm, switchLights;
    private static final String TAG = "FrugalLogs";
    private static final int REQUEST_ENABLE_BT = 1;
    //We will use a Handler to get the BT Connection statys
    public static Handler handler;
    BluetoothDevice esp32BT = null;
    UUID arduinoUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //We declare a default UUID to create the global variable
    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MessageConstants.MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString(); // Read message from Arduino
                        break;
                    case MessageConstants.MESSAGE_WRITE:
                        break;
                    case MessageConstants.MESSAGE_ERROR:
                        break;
                }
            }
        };

        buttonTemperature = findViewById(R.id.button_temperature);
        buttonRadio = findViewById(R.id.button_radio);
        buttonBT = findViewById(R.id.button_bt);
        switchAlarm = findViewById(R.id.switch1);
        switchLights = findViewById(R.id.switch2);

        switchAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchAlarm.setChecked(!switchAlarm.isChecked());
            }
        });

        switchLights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchLights.setChecked(!switchLights.isChecked());
            }
        });

        buttonTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TemperatureActivity.class));
            }
        });

        buttonRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RadioActivity.class));
            }
        });

        buttonBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if the phone supports BT
                if (bluetoothAdapter == null) {
                    // Device doesn't support Bluetooth
                    Log.d(TAG, "Device doesn't support Bluetooth");
                } else {
                    Log.d(TAG, "Device support Bluetooth");
                    //Check BT enabled. If disabled, we ask the user to enable BT
                    if (!bluetoothAdapter.isEnabled()) {
                        Log.d(TAG, "Bluetooth is disabled");
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            Log.d(TAG, "We don't BT Permissions");
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            Log.d(TAG, "Bluetooth is enabled now");
                        } else {
                            Log.d(TAG, "We have BT Permissions");
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                            Log.d(TAG, "Bluetooth is enabled now");
                        }

                    } else {
                        Log.d(TAG, "Bluetooth is enabled");
                    }
                    Set < BluetoothDevice > pairedDevices = bluetoothAdapter.getBondedDevices();

                    if (pairedDevices.size() > 0) {
                        // There are paired devices. Get the name and address of each paired device.
                        for (BluetoothDevice device: pairedDevices) {
                            String deviceName = device.getName();
                            String deviceHardwareAddress = device.getAddress(); // MAC address
                            Log.d(TAG, "deviceName:" + deviceName);
                            Log.d(TAG, "deviceHardwareAddress:" + deviceHardwareAddress);

                            if (deviceName.equals("Raduino")) {
                                Log.d(TAG, "Raduino found");
                                arduinoUUID = device.getUuids()[0].getUuid();
                                esp32BT = device;
                                //ESP32 Found!

                                if (esp32BT != null) {
                                    new Thread(new Runnable() {
                                        public void run() {
                                            BluetoothThread btThread = new BluetoothThread(esp32BT, arduinoUUID, handler);
                                            btThread.run();
                                            //Check if Socket connected
                                            if (btThread.getMmSocket().isConnected()) {
                                                Log.d(TAG, "Calling ConnectedThread class");
                                                ConnectedThread connectedThread = new ConnectedThread(btThread);
                                                connectedThread.run();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        }
                    }
                }
            }
            Log.d(TAG, "Button Pressed");
        }
    });
}
}
