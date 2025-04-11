package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChestNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest_name);

        EditText etChestName = findViewById(R.id.etChestName);
        Button btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {
            String chestName = etChestName.getText().toString().trim();

            if (chestName.isEmpty()) {
                Toast.makeText(this, "Please enter a chest name", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(ChestNameActivity.this, ChestSizeActivity.class);
                intent.putExtra("CHEST_NAME", chestName);
                startActivity(intent);
            }
        });
    }
}