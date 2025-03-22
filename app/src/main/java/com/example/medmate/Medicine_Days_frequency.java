package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
        Button nextButton = findViewById(R.id.nextButton);

        nextButton.setOnClickListener(v -> {
            Toast.makeText(Medicine_Days_frequency.this, "Next button clicked!", Toast.LENGTH_SHORT).show();

            int selectedFrequency = 1; // Default value
            int checkedId = frequencyRadioGroup.getCheckedRadioButtonId();
            if (checkedId == R.id.onceRadioButton) {
                selectedFrequency = 1;
            } else if (checkedId == R.id.twiceRadioButton) {
                selectedFrequency = 2;
            } else if (checkedId == R.id.thriceRadioButton) {
                selectedFrequency = 3;
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

            Intent nextIntent = new Intent(Medicine_Days_frequency.this, TimePicker.class);
            nextIntent.putExtra("SELECTED_FREQUENCY", selectedFrequency);
            nextIntent.putExtra("SELECTED_MEDICINE_NAME", medicineName);
            nextIntent.putExtra("SELECTED_MEDICINE_TYPE", medicineType);
            nextIntent.putStringArrayListExtra("SELECTED_DAYS", selectedDays);

            Log.d("Medicine_Days_frequency", "Frequency: " + selectedFrequency);
            Log.d("Medicine_Days_frequency", "Medicine Name: " + medicineName);
            Log.d("Medicine_Days_frequency", "Medicine Type: " + medicineType);
            Log.d("Medicine_Days_frequency", "Selected Days: " + selectedDays.toString());

            startActivity(nextIntent);
        });
    }
}