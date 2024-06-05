package com.example.raduinoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class RadioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        ImageButton buttonBack = findViewById(R.id.back_button);
        buttonBack.setOnClickListener(v -> startActivity(new Intent(RadioActivity.this, MainActivity.class)));

    }

}