package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Medicine_Days_frequency extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_days_frequency);

        Intent intent = getIntent();
        String medicineName = intent.getStringExtra("SELECTED_MEDICINE_NAME");
        String medicineType = intent.getStringExtra("SELECTED_MEDICINE_TYPE");

        if (medicineName == null || medicineType == null) {
            Toast.makeText(this, "Error: Missing medicine data", Toast.LENGTH_SHORT).show();
            return;
        }

        ToggleButton sundayToggle = findViewById(R.id.sundayToggleButton);
        ToggleButton mondayToggle = findViewById(R.id.mondayToggleButton);
        ToggleButton tuesdayToggle = findViewById(R.id.tuesdayToggleButton);
        ToggleButton wednesdayToggle = findViewById(R.id.wednesdayToggleButton);
        ToggleButton thursdayToggle = findViewById(R.id.thursdayToggleButton);
        ToggleButton fridayToggle = findViewById(R.id.fridayToggleButton);
        ToggleButton saturdayToggle = findViewById(R.id.saturdayToggleButton);

        RadioGroup frequencyRadioGroup = findViewById(R.id.frequencyRadioGroup);

        frequencyRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            String selectedFrequency = "";
            if (checkedId == R.id.onceRadioButton) {
                selectedFrequency = "Once a day";
            } else if (checkedId == R.id.twiceRadioButton) {
                selectedFrequency = "Twice a day";
            } else if (checkedId == R.id.thriceRadioButton) {
                selectedFrequency = "Three times a day";
            }

            ArrayList<String> selectedDays = new ArrayList<>();
            if (sundayToggle.isChecked()) selectedDays.add("Sun");
            if (mondayToggle.isChecked()) selectedDays.add("Mon");
            if (tuesdayToggle.isChecked()) selectedDays.add("Tue");
            if (wednesdayToggle.isChecked()) selectedDays.add("Wed");
            if (thursdayToggle.isChecked()) selectedDays.add("Thu");
            if (fridayToggle.isChecked()) selectedDays.add("Fri");
            if (saturdayToggle.isChecked()) selectedDays.add("Sat");

            if (selectedDays.isEmpty()) {
                Toast.makeText(Medicine_Days_frequency.this, "Please select at least one day!", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedDaysStr;
            if (selectedDays.size() == 7) {
                selectedDaysStr = "Daily";
            } else {
                selectedDaysStr = String.join(", ", selectedDays);
            }

            Intent nextIntent = new Intent(Medicine_Days_frequency.this, TimePicker.class);
            nextIntent.putExtra("SELECTED_FREQUENCY", selectedFrequency);
            nextIntent.putExtra("SELECTED_MEDICINE_NAME", medicineName);
            nextIntent.putExtra("SELECTED_MEDICINE_TYPE", medicineType);
            nextIntent.putExtra("SELECTED_DAYS", selectedDaysStr);
            startActivity(nextIntent);
        });
    }
}
