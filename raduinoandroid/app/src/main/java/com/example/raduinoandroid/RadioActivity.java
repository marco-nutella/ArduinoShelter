package com.example.raduinoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class RadioActivity extends AppCompatActivity {
    int Volume = 20;
    private TextView editTextVolume;
    private TextView editTextFrequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        Button incrementVolume = findViewById(R.id.button_increment);
        Button decrementVolume = findViewById(R.id.button_decrement);
        Button tuneUp = findViewById(R.id.up_button);
        Button tuneDown = findViewById(R.id.down_button);
        editTextVolume= findViewById(R.id.Volume_text);
        editTextFrequency =  findViewById(R.id.frequency_text);
        ImageButton buttonBack = findViewById(R.id.back_button);

        ListView listView = findViewById(R.id.listView);

        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Item 1", 10));
        items.add(new Item("Item 2", 20));
        items.add(new Item("Item 3", 30));

        RadioAdapter adapter = new RadioAdapter(this, R.layout.list_item, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Item clickedItem = (Item) parent.getItemAtPosition(position);
            String text = "Radio: " + clickedItem.getVariable();
            editTextFrequency.setText(text);
            text = "Fm Channel " + clickedItem.getVariable();
            //sendBluetoothMessage(text);
        });


        tuneUp.setOnClickListener(v -> {
            String text = "Fm Up 1";
            //sendBluetoothMessage(text);
        });
        tuneDown.setOnClickListener(v -> {
            String text = "Fm Down 1";
            //sendBluetoothMessage(text);
        });

        decrementVolume.setOnClickListener(v -> {
            Volume--;
            if(Volume <= 0){Volume = 0;}
            String text = "Volume: "+ Volume ;
            editTextVolume.setText(text);
            text = "Fm Volume " + Volume;
            //sendBluetoothMessage(text);

        });
        incrementVolume.setOnClickListener(v -> {
            Volume++;
            if(Volume >= 16){Volume = 15;}
            String text = "Volume: "+ Volume ;
            editTextVolume.setText(text);
            text = "Fm Volume " + Volume;
            //sendBluetoothMessage(text);

        });




        buttonBack.setOnClickListener(v -> startActivity(new Intent(RadioActivity.this, MainActivity.class)));

    }

}