package com.example.medmate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.atomic.AtomicInteger;

public class Medicine_Days_frequency extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medicine_days_frequency);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String medicineName = intent.getStringExtra("SELECTED_MEDICINE_NAME");
        String medicineType = intent.getStringExtra("SELECTED_MEDICINE_TYPE");

        RadioGroup frequencyRadioGroup = findViewById(R.id.frequencyRadioGroup);
        AtomicInteger selectedFrequency = new AtomicInteger(1);

        frequencyRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.onceRadioButton) {
                selectedFrequency.set(1);
            } else if (checkedId == R.id.twiceRadioButton) {
                selectedFrequency.set(2);
            } else if (checkedId == R.id.thriceRadioButton) {
                selectedFrequency.set(3);
            }

            Intent nextIntent = new Intent(Medicine_Days_frequency.this, TimePicker.class);
            nextIntent.putExtra("SELECTED_FREQUENCY", selectedFrequency.get());
            nextIntent.putExtra("SELECTED_MEDICINE_NAME", medicineName);
            nextIntent.putExtra("SELECTED_MEDICINE_TYPE", medicineType);
            startActivity(nextIntent);
        });
    }
}
