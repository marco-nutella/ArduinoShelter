package com.example.raduinoandroid;

import android.app.Application;

public class RaduinoApp extends Application {
    private BluetoothService bs;
    public BluetoothService getBluetoothService(){
        return bs;
    }

    public void setBluetoothService(BluetoothService newBs) {
        bs = newBs;
    }
}
