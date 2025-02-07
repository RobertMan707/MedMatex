package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MedicineName extends AppCompatActivity {

    EditText medicineNameEditText;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_name);

        medicineNameEditText = findViewById(R.id.medicineNameEditText);
        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String medicineName = medicineNameEditText.getText().toString();
                if (!medicineName.isEmpty()) {
                    String medicineType = getIntent().getStringExtra("medicineType");
                    Intent intent = new Intent(MedicineName.this, MedicineDays.class);
                    intent.putExtra("medicineName", medicineName);
                    intent.putExtra("medicineType", medicineType);
                    startActivity(intent);
                }
            }
        });
    }
}
