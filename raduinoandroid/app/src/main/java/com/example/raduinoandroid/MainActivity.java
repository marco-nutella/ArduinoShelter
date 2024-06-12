package com.example.raduinoandroid;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.Channel;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CALL_PHONE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 2;

    private FusedLocationProviderClient fusedLocationClient;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchAlarm, switchLights;
    private static final String TAG = "Raduino";

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
    private String temperature;
    private String maxTemperature;
    private String channel;
    private String volume;
    private String lights;
    private String alarm;
    private String ventilation;
    private String autoVentilation;
    private String tendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = new Intent(this, BluetoothService.class);
        TextView warning = findViewById(R.id.Pop_up);
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
        callButton.setOnClickListener(v -> {
            try {
                makePhoneCall();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        if (isBound) {
            try {
                bluetoothService.sendBluetoothMessage("All Request 1");
                wait(50);
                if("true".equals(alarm)){switchAlarm.setChecked(true);}else{switchAlarm.setChecked(false);}
                if("true".equals(lights)){
                    switchLights.setChecked(true);
                    warning.setVisibility(View.INVISIBLE);}
                else if("false".equals(lights)){
                    switchLights.setChecked(false);
                    warning.setVisibility(View.INVISIBLE);}
                else {switchLights.setChecked(false);
                    warning.setVisibility(View.VISIBLE);}

            } catch (UnsupportedEncodingException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, String.valueOf(msg));

                switch (msg.what) {
                    case MessageConstants.MESSAGE_READ:
                        if("Unable to connect to the BT device".equals(msg.obj)){ return;}
                        byte[] readBuf = (byte[]) msg.obj; // Read the byte array from the message
                        String message = new String(readBuf, 0, msg.arg1); // Convert the byte array to a string
                        message = message.replace("\r", "").replace("\n", "");
                        String part1 = null, part2 = null;
                        int part3 = 0;
                        int firstSpaceIndex = message.indexOf(' ');
                        int secondSpaceIndex = message.indexOf(' ', firstSpaceIndex + 1);

                        if (firstSpaceIndex != -1 && secondSpaceIndex != -1) {
                            part1 = message.substring(0, firstSpaceIndex);
                            part2 = message.substring(firstSpaceIndex + 1, secondSpaceIndex);
                            String part3String = message.substring(secondSpaceIndex + 1);
                            try {
                                part3 = Integer.parseInt(part3String);
                            } catch (NumberFormatException e) {
                                part3 = -1; // or some default value indicating the error
                            }
                        }
                        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        if ("All".equals(part1)||"ASAll".equals(part1)||"l".equals(part1)) {
                            String[] parts = part2.split("\\.");
                            // Parse each part into the respective data type
                            temperature = parts[0];
                            maxTemperature = parts[1];
                            channel = parts[2];
                            volume = parts[3];
                            lights = parts[4];
                            alarm = parts[5];
                            ventilation = parts[6];
                            autoVentilation = parts[7];
                            String humidity = parts[8];
                            tendId = part3 + "";
                            warning.setVisibility(View.INVISIBLE);
                            editor.putString("temperature", temperature);
                            editor.putString("maxTemperature", maxTemperature);
                            editor.putString("channel", channel);
                            editor.putString("volume", volume);
                            editor.putString("lights", lights);
                            editor.putString("alarm", alarm);
                            editor.putString("ventilation", ventilation);
                            editor.putString("autoVentilation", autoVentilation);
                            editor.putString("tendId", tendId);
                            editor.putString("humidity", humidity);

                        } else if("Fm".equals(part1)||"ASFm".equals(part1)){
                            if("Channel".equals(part2)){
                                channel = part3 + "";
                                editor.putString("channel", channel);
                            } else if ("Volume".equals(part2)) {
                                volume = part3 + "";
                                editor.putString("volume", volume);

                            }
                        } else if("Distress".equals(part1)||"ASDistress".equals(part1)||"stress".equals(part1)){
                            if("Signal".equals(part2)){
                                try {
                                    makePhoneCall();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } else if("Ventilation".equals(part1)||"ntilation".equals(part1)||"ASVentilation".equals(part1)){
                            if("On".equals(part2)){
                                ventilation = "true";
                                editor.putString("ventilation", ventilation);
                            } else if ("Off".equals(part2)) {
                                ventilation = "false";
                                editor.putString("ventilation", ventilation);

                            }
                        } else if("autoVentilation".equals(part1)||"ASautoVentilation".equals(part1)||"toVentilation".equals(part1)){
                            if("On".equals(part2)){
                                autoVentilation = "true";
                                editor.putString("autoVentilation", autoVentilation);
                            } else if ("Off".equals(part2)) {
                                autoVentilation = "false";
                                editor.putString("autoVentilation", autoVentilation);

                            }
                        } else if("Lights".equals(part1)||"ASLights".equals(part1)||"ghts".equals(part1)){
                            if("On".equals(part2)){
                                lights = "true";
                                editor.putString("lights", lights);
                                switchLights.setChecked(true);
                                warning.setVisibility(View.INVISIBLE);
                            } else if ("Off".equals(part2)) {
                                lights = "false";
                                editor.putString("lights", lights);
                                switchLights.setChecked(false);
                                warning.setVisibility(View.INVISIBLE);
                            } else if ("Emergency".equals(part2)) {
                                lights = "Emergency";
                                editor.putString("lights", lights);
                                switchLights.setChecked(false);
                                warning.setVisibility(View.VISIBLE);
                            }
                        } else if("Alarm".equals(part1)||"arm".equals(part1)||"ASAlarm".equals(part1)){
                            if("On".equals(part2)){
                                alarm = "true";
                                editor.putString("alarm", alarm);
                                switchAlarm.setChecked(false);
                            } else if ("Off".equals(part2)) {
                                alarm = "false";
                                editor.putString("alarm", alarm);
                                switchAlarm.setChecked(false);

                            }
                        } else if("Temperature".equals(part1)||"ASTemperature".equals(part1)||"mperature".equals(part1)){
                            if("Temperature".equals(part2)){
                                temperature = part3+"";
                                editor.putString("temperature", temperature);
                            } else if ("Max".equals(part2)) {
                                maxTemperature = part3+"";
                                editor.putString("maxTemperature", maxTemperature);
                            }
                        } else if("Data".equals(part1)||"ASData".equals(part1)||"ta".equals(part1)){
                            String[] parts = part2.split("\\.");
                            // Parse each part into the respective data type
                            temperature = parts[0];
                            String humidity = parts[1];
                            tendId = part3 + "";
                            editor.putString("temperature", temperature);
                            editor.putString("humidity", humidity);
                            editor.putString("tendId", tendId);
                        } else{return;}
                        editor.apply();
                        break;
                    case MessageConstants.MESSAGE_WRITE:
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
                try {
                    bluetoothService.sendBluetoothMessage("All Request 1");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                bluetoothService.startBluetoothConnection(this,handler);
            } else {
                try {
                    bluetoothService.sendBluetoothMessage("All Request 1");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                bluetoothService.startBluetoothConnection(this,handler);
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
            bluetoothService.startBluetoothConnection(this, handler);
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

    private void makePhoneCall() throws InterruptedException {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
        } else {
            startCall();
        }
    }

    private void startCall() throws InterruptedException {
        // Access the SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        getLocation();
        if (isBound) {
            try {
                bluetoothService.sendBluetoothMessage("Request Data 1");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        Thread.sleep(50);
        // Retrieve the values
        String city = sharedPref.getString("city", "Default City");
        String region = sharedPref.getString("region", "Default Region");
        String country = sharedPref.getString("country", "Default Country");
        String id = sharedPref.getString("tendId", "0");
        String humidity = sharedPref.getString("humidity", "0");
        String temperature = sharedPref.getString("temperature", "0");

        Info info = new Info(Integer.parseInt(id), Integer.parseInt(humidity), Integer.parseInt(temperature), country, city, region, true);

        try {
            RetrofitInfoInterface aa = RetrofitService.getRetrofit().create(RetrofitInfoInterface.class);
            aa.PostInfo(info).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    Integer newid = response.body();
                    SharedPreferences.Editor editor = sharedPref.edit();
                    String tendId = newid + "";
                    editor.putString("tendId", tendId);
                    String text = "Change Id " + newid;
                    editor.apply();
                    if (isBound) {
                        try {
                            bluetoothService.sendBluetoothMessage(text);
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            System.out.println(e);
        }
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
                try {
                    startCall();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
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


}
