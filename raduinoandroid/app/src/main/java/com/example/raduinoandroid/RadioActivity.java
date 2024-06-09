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