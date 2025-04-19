package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class Medicine_Days_frequency extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_days_frequency);

        // Get medicine data from intent
        Intent intent = getIntent();
        String medicineName = intent.getStringExtra("MEDICINE_NAME");
        String medicineType = intent.getStringExtra("SELECTED_MEDICINE_TYPE");

        if (medicineName == null || medicineType == null) {
            Toast.makeText(this, "Error: Missing medicine data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize all day toggle buttons
        ToggleButton sundayToggle = findViewById(R.id.sundayToggleButton);
        ToggleButton mondayToggle = findViewById(R.id.mondayToggleButton);
        ToggleButton tuesdayToggle = findViewById(R.id.tuesdayToggleButton);
        ToggleButton wednesdayToggle = findViewById(R.id.wednesdayToggleButton);
        ToggleButton thursdayToggle = findViewById(R.id.thursdayToggleButton);
        ToggleButton fridayToggle = findViewById(R.id.fridayToggleButton);
        ToggleButton saturdayToggle = findViewById(R.id.saturdayToggleButton);

        // Initialize and configure NumberPicker (1-6 times/day)
        NumberPicker frequencyNumberPicker = findViewById(R.id.frequencyNumberPicker);
        frequencyNumberPicker.setMinValue(1);  // Minimum 1 time/day
        frequencyNumberPicker.setMaxValue(6);  // Maximum 6 times/day
        frequencyNumberPicker.setValue(1);     // Default to 1 time/day
        frequencyNumberPicker.setWrapSelectorWheel(false); // Prevent infinite scrolling




        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            int selectedFrequency = frequencyNumberPicker.getValue();

            // Collect selected days
            ArrayList<String> selectedDays = new ArrayList<>();
            if (sundayToggle.isChecked()) selectedDays.add("Sunday");
            if (mondayToggle.isChecked()) selectedDays.add("Monday");
            if (tuesdayToggle.isChecked()) selectedDays.add("Tuesday");
            if (wednesdayToggle.isChecked()) selectedDays.add("Wednesday");
            if (thursdayToggle.isChecked()) selectedDays.add("Thursday");
            if (fridayToggle.isChecked()) selectedDays.add("Friday");
            if (saturdayToggle.isChecked()) selectedDays.add("Saturday");

            // Validate day selection
            if (selectedDays.isEmpty()) {
                Toast.makeText(this, "Please select at least one day!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pass data to TimePicker activity
            Intent nextIntent = new Intent(this, TimePicker.class);
            nextIntent.putExtra("SELECTED_FREQUENCY", selectedFrequency);
            nextIntent.putExtra("MEDICINE_NAME", medicineName);
            nextIntent.putExtra("SELECTED_MEDICINE_TYPE", medicineType);
            nextIntent.putStringArrayListExtra("SELECTED_DAYS", selectedDays);
            startActivity(nextIntent);
        });
    }
}