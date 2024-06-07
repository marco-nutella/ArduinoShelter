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
import android.widget.ImageButton;
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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private Button buttonTemperature, buttonRadio;
    private ImageButton buttonBT;
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            // Permission is not granted. You can choose to request it or skip the location functionality.
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
            // You can still proceed with other parts of your app without location
            // For example, initialize other views or components here
        }


        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, String.valueOf(msg));
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
                boolean isChecked = switchAlarm.isChecked();
                if (isChecked) {
                    // Switch is now checked
                    //sendBluetoothMessage("Alarm ON 1");
                } else {
                    // Switch is now unchecked
                    //sendBluetoothMessage("Alarm OFF 1");
                }
            }
        });

        switchLights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isChecked = switchLights.isChecked();
                if (isChecked) {
                    // Switch is now checked
                    //sendBluetoothMessage("Lights ON 1");
                } else {
                    // Switch is now unchecked
                    //sendBluetoothMessage("Lights OFF 1");
                }
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
                        } else {
                            Log.d(TAG, "We have BT Permissions");
                        }
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        Log.d(TAG, "Bluetooth is enabled now");

                    } else {
                        Log.d(TAG, "Bluetooth is enabled");
                    }
                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                    if (pairedDevices.size() > 0) {
                        // There are paired devices. Get the name and address of each paired device.
                        for (BluetoothDevice device : pairedDevices) {
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
                                        }

                                        ;
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


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            getAddressFromLocation(latitude, longitude);
                        }
                    }
                });
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String city = address.getLocality();
                String region = address.getAdminArea();
                String country = address.getCountryName();

                // Save to SharedPreferences
                SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("city", city);
                editor.putString("region", region);
                editor.putString("country", country);
                editor.apply();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to get address from location", e);
        }
    }
}



