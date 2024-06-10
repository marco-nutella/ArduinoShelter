package com.example.raduinoandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CALL_PHONE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 2;

    private FusedLocationProviderClient fusedLocationClient;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchAlarm, switchLights;
    private static final String TAG = "FrugalLogs";

    // We will use a Handler to get the BT Connection status
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
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        schedulePeriodicWork();
        Intent intent = new Intent(this, BluetoothService.class);

        // Request necessary permissions
        requestBluetoothPermissions();
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        ImageButton callButton = findViewById(R.id.emergency_button);
        callButton.setOnClickListener(v -> makePhoneCall());

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

        Button buttonTemperature = findViewById(R.id.button_temperature);
        Button buttonRadio = findViewById(R.id.button_radio);
        ImageButton buttonBT = findViewById(R.id.button_bt);
        switchAlarm = findViewById(R.id.switch1);
        switchLights = findViewById(R.id.switch2);

        switchAlarm.setOnClickListener(v -> {
            boolean isChecked = switchAlarm.isChecked();
            if (isChecked) {
                if (isBound) {
                    try {
                        bluetoothService.sendBluetoothMessage("Alarm ON 1");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                if (isBound) {
                    try {
                        bluetoothService.sendBluetoothMessage("Alarm OFF 1");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        switchLights.setOnClickListener(v -> {
            boolean isChecked = switchLights.isChecked();
            if (isChecked) {
                if (isBound) {
                    try {
                        bluetoothService.sendBluetoothMessage("Lights ON 1");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                if (isBound) {
                    try {
                        bluetoothService.sendBluetoothMessage("Lights OFF 1");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        buttonTemperature.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TemperatureActivity.class)));

        buttonRadio.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RadioActivity.class)));

        buttonBT.setOnClickListener(view -> {
            if (isBound) {
                bluetoothService.startBluetoothConnection(handler);
            } else {
                bluetoothService.startBluetoothConnection(handler);
            }
            Log.d(TAG, "Button Pressed");
        });
    }

    @Override
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

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        getAddressFromLocation(latitude, longitude);
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

    private void makePhoneCall() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
        } else {
            startCall();
        }
    }

    private void startCall() {
        // Access the SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);

        // Retrieve the values
        String city = sharedPref.getString("city", "Default City");
        String region = sharedPref.getString("region", "Default Region");
        String country = sharedPref.getString("country", "Default Country");

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        // Replace with your phone number
        String phoneNumber = "969787598";
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    private void requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT},
                        BLUETOOTH_PERMISSION_REQUEST_CODE);
            } else {
                Log.d(TAG, "Bluetooth permissions already granted for Android 12+.");
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN},
                        BLUETOOTH_PERMISSION_REQUEST_CODE);
            } else {
                Log.d(TAG, "Bluetooth permissions already granted for below Android 12.");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Bluetooth permissions granted.");
            } else {
                Log.e(TAG, "Bluetooth permissions denied.");
                // Optionally, you can re-request the permission or inform the user.
            }
        } else if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Call permissions granted.");
                startCall();
            } else {
                Log.e(TAG, "Call permissions denied.");
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permissions granted.");
                getLocation();
            } else {
                Log.e(TAG, "Location permissions denied.");
            }
        }
    }

    private void schedulePeriodicWork() {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(TimerCall.class, 15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(this).enqueue(periodicWorkRequest);
    }
}
