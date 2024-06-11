package com.example.raduinoandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;


public class RadioActivity extends AppCompatActivity {

    private RaduinoApp ra;
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


    public static Handler handler;
    private static final String TAG = "Raduino";
    int Volume = 20;
    private TextView editTextVolume;
    private TextView editTextFrequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        ra = (RaduinoApp) getApplicationContext();
        bluetoothService = ra.getBluetoothService();
        editTextVolume= findViewById(R.id.Volume_text);
        editTextFrequency =  findViewById(R.id.frequency_text);
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String channel = sharedPref.getString("channel", "0");
        String texto = "Radio: " + channel;
        editTextFrequency.setText(texto);
        String volume = sharedPref.getString("volume", "0");
        texto = "Volume: "+ volume ;
        editTextVolume.setText(texto);
        Volume = Integer.parseInt(volume);




        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        Button incrementVolume = findViewById(R.id.button_increment);
        Button decrementVolume = findViewById(R.id.button_decrement);
        Button tuneUp = findViewById(R.id.up_button);
        Button tuneDown = findViewById(R.id.down_button);

        ImageButton buttonBack = findViewById(R.id.back_button);

        ListView listView = findViewById(R.id.listView);

        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Ultra FM ", 882));
        items.add(new Item("TSF ", 895));
        items.add(new Item("SBSR ", 904));
        items.add(new Item("Radio Clube de Sintra ", 912));
        items.add(new Item("Cidade FM ", 916));
        items.add(new Item("Radio Amalia ", 920));
        items.add(new Item("Mega Hits FM ", 924));
        items.add(new Item("RFM ", 932));
        items.add(new Item("Antena 2 ", 944));
        items.add(new Item("Kiss ", 950));
        items.add(new Item("Radio Valdevez ", 964));
        items.add(new Item("Radio Commercial ", 974));
        items.add(new Item("Marginal ", 981));
        items.add(new Item("Radio Meo Sudoeste ", 1008));
        items.add(new Item("Radio Saturno ", 1016));
        items.add(new Item("Radio Orbital ", 1019));
        items.add(new Item("Radio Sim ", 1022));
        items.add(new Item("Oxigenio ", 1026));
        items.add(new Item("Smooth FM ", 1030));
        items.add(new Item("Radio Renascenca ", 1034));
        items.add(new Item("M80 ", 1043));
        items.add(new Item("Radio Cascais ", 1054));
        items.add(new Item("Vadafone FM ", 1072));


        RadioAdapter adapter = new RadioAdapter(this, R.layout.list_item, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Item clickedItem = (Item) parent.getItemAtPosition(position);
            String text = "Radio: " + clickedItem.getVariable();
            editTextFrequency.setText(text);
            int val = clickedItem.getVariable();
            text = "F C " + val;
            if (isBound) {
                try {
                    bluetoothService.sendBluetoothMessage("AlarF CR "+ val);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        tuneUp.setOnClickListener(v -> {
            String text;
            if (isBound) {
                try {
                    bluetoothService.sendBluetoothMessage("AlarF UR 1");
                    wait(100);
                    String channele = sharedPref.getString("channel", "0");
                    text = "Radio: " + channele;
                    editTextFrequency.setText(text);
                } catch (UnsupportedEncodingException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }        });
        tuneDown.setOnClickListener(v -> {
            String text = "F D 1";
            if (isBound) {
                try {
                    bluetoothService.sendBluetoothMessage("AlarF DR 1");
                    wait(100);
                    String channele = sharedPref.getString("channel", "0");
                    text = "Radio: " + channele;
                    editTextFrequency.setText(text);
                } catch (UnsupportedEncodingException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }        });

        decrementVolume.setOnClickListener(v -> {
            Volume--;
            if(Volume <= 0){Volume = 0;}
            String text = "Volume: "+ Volume ;
            editTextVolume.setText(text);
            text = "F V " + Volume;
            if (isBound) {
                try {
                    bluetoothService.sendBluetoothMessage("AlarF VR "+ Volume);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        incrementVolume.setOnClickListener(v -> {
            Volume++;
            if(Volume >= 16){Volume = 15;}
            String text = "Volume: "+ Volume ;
            editTextVolume.setText(text);
            text = "F V " + Volume;
            if (isBound) {
                try {
                    bluetoothService.sendBluetoothMessage("AlarF VR "+ Volume);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }

        });

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



        buttonBack.setOnClickListener(v -> startActivity(new Intent(RadioActivity.this, MainActivity.class)));

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
            bluetoothService.startBluetoothConnection(this,handler);
        }
    }





}