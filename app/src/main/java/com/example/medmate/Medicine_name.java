package com.example.medmate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class Medicine_name extends AppCompatActivity {

    Button nextButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_name);

        Intent intent = getIntent();
        String medicineType = intent.getStringExtra("SELECTED_MEDICINE_TYPE");

        if (medicineType == null) {
            Toast.makeText(Medicine_name.this, "Error: No medicine type passed", Toast.LENGTH_SHORT).show();
            return;
        }

        nextButton = findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String medicineName = ((TextInputEditText) findViewById(R.id.medicineNameEditText)).getText().toString();

                if (medicineName.isEmpty()) {
                    Toast.makeText(Medicine_name.this, "Please enter a medicine name!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent nextIntent = new Intent(Medicine_name.this, Medicine_Days_frequency.class);
                    nextIntent.putExtra("SELECTED_MEDICINE_NAME", medicineName); // Fixed key
                    nextIntent.putExtra("SELECTED_MEDICINE_TYPE", medicineType); // Fixed key
                    startActivity(nextIntent);
                }
            }
        });
    }
}