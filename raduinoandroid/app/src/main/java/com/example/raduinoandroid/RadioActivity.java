package com.example.raduinoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


public class RadioActivity extends AppCompatActivity {
    int Volume = 20;
    private TextView editTextVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        Button incrementVolume = findViewById(R.id.button_increment);
        Button decrementVolume = findViewById(R.id.button_decrement);
        Button tuneUp = findViewById(R.id.up_button);
        Button tuneDown = findViewById(R.id.down_button);
        editTextVolume= findViewById(R.id.Volume_text);

        ImageButton buttonBack = findViewById(R.id.back_button);

        ListView listView = findViewById(R.id.listView);

        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("Item 1", 10));
        items.add(new Item("Item 2", 20));
        items.add(new Item("Item 3", 30));

        RadioAdapter adapter = new RadioAdapter(this, R.layout.list_item, items);
        listView.setAdapter(adapter);

        tuneUp.setOnClickListener(v -> {
            String text = "Fm Up 1";
            //sendBluetoothMessage(text);
        });
        tuneDown.setOnClickListener(v -> {Volume++;
            String text = "Fm Down 1";
            //sendBluetoothMessage(text);
        });

        decrementVolume.setOnClickListener(v -> {Volume--;
            String text = "Volume: "+ Volume ;
            editTextVolume.setText(text);
            text = "Fm Volume " + Volume;
            //sendBluetoothMessage(text);

        });
        incrementVolume.setOnClickListener(v -> {Volume++;
            String text = "Volume: "+ Volume ;
            editTextVolume.setText(text);
            text = "Fm Volume " + Volume;
            //sendBluetoothMessage(text);

        });




        buttonBack.setOnClickListener(v -> startActivity(new Intent(RadioActivity.this, MainActivity.class)));

    }

}