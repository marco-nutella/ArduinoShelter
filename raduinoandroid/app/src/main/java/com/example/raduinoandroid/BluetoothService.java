package com.example.raduinoandroid;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

public class BluetoothService extends Service {
    private static final String TAG = "BluetoothService";
    private final IBinder binder = new LocalBinder();
    public BluetoothThread btThread = null;
    public ConnectedThread connectedThread = null;

    private BluetoothAdapter bluetoothAdapter;

    public class LocalBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    public void startBluetoothConnection(Handler handler) {
        if (bluetoothAdapter == null) {
            Log.d(TAG, "Device doesn't support Bluetooth");
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth is disabled");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
            Log.d(TAG, "Bluetooth is enabled now");
        } else {
            Log.d(TAG, "Bluetooth is enabled");
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                Log.d(TAG, "deviceName:" + deviceName);
                Log.d(TAG, "deviceHardwareAddress:" + deviceHardwareAddress);

                if (deviceName.equals("Raduino")) {
                    Log.d(TAG, "Raduino found");
                    UUID arduinoUUID = device.getUuids()[0].getUuid();

                    if (device != null) {
                        btThread = new BluetoothThread(device, arduinoUUID, handler);
                        new Thread(btThread).start();
                        if (btThread.getMmSocket().isConnected()) {
                            connectedThread = new ConnectedThread(btThread);
                            new Thread(connectedThread).start();
                        }
                    }
                }
            }
        }
    }

    public void sendBluetoothMessage(String message) throws UnsupportedEncodingException {
        if (connectedThread != null) {
            message = message + "\n";
            connectedThread.write(message.getBytes("UTF-8"));
        }
    }
}