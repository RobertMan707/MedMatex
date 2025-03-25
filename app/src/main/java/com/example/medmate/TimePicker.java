package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Locale;

public class TimePicker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        // Get intent data
        String medicineName = getIntent().getStringExtra("MEDICINE_NAME");
        String medicineType = getIntent().getStringExtra("SELECTED_MEDICINE_TYPE");
        int frequency = getIntent().getIntExtra("SELECTED_FREQUENCY", 1);
        ArrayList<String> selectedDays = getIntent().getStringArrayListExtra("SELECTED_DAYS");

        LinearLayout timePickerLayout = findViewById(R.id.timepickerLayout);
        timePickerLayout.removeAllViews();

        ArrayList<String> selectedTimes = new ArrayList<>();

        // Create time pickers based on frequency
        for (int i = 0; i < frequency; i++) {
            // Create hour picker (0-23)
            NumberPicker hourPicker = new NumberPicker(this);
            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(23);
            hourPicker.setValue(8); // Default to 8 AM
            hourPicker.setFormatter(value -> String.format(Locale.getDefault(), "%02d", value)); // Show as "08"
            hourPicker.setWrapSelectorWheel(true);

            // Create minute picker (0-59)
            NumberPicker minutePicker = new NumberPicker(this);
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(59);
            minutePicker.setValue(0);
            minutePicker.setFormatter(value -> String.format(Locale.getDefault(), "%02d", value)); // Show as "00"
            minutePicker.setWrapSelectorWheel(true);

            // Create the row layout
            LinearLayout pickerContainer = new LinearLayout(this);
            pickerContainer.setOrientation(LinearLayout.HORIZONTAL);
            pickerContainer.setPadding(8, 8, 8, 8);
            pickerContainer.setGravity(Gravity.CENTER);

            // Add components to row
            pickerContainer.addView(hourPicker);

            TextView colon = new TextView(this);
            colon.setText(":");
            colon.setTextSize(36);
            colon.setGravity(Gravity.CENTER);
            pickerContainer.addView(colon);

            pickerContainer.addView(minutePicker);

            // Add row to main layout
            timePickerLayout.addView(pickerContainer);
        }

        // Set title based on frequency
        TextView timeTitle = findViewById(R.id.timeTitle);
        switch (frequency) {
            case 1: timeTitle.setText("Select Time (Once a Day)"); break;
            case 2: timeTitle.setText("Select Time (Twice a Day)"); break;
            case 3: timeTitle.setText("Select Time (Three Times a Day)"); break;
        }

        // Next button handler
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            selectedTimes.clear();

            // Collect all selected times with proper formatting
            for (int i = 0; i < frequency; i++) {
                LinearLayout pickerContainer = (LinearLayout) timePickerLayout.getChildAt(i);
                NumberPicker hourPicker = (NumberPicker) pickerContainer.getChildAt(0);
                NumberPicker minutePicker = (NumberPicker) pickerContainer.getChildAt(2);

                String formattedTime = String.format(Locale.getDefault(),
                        "%02d:%02d",
                        hourPicker.getValue(),
                        minutePicker.getValue());

                selectedTimes.add(formattedTime);
            }

            Intent nextIntent = new Intent(TimePicker.this, Medicine_count.class);
            nextIntent.putExtra("MEDICINE_NAME", medicineName);
            nextIntent.putExtra("SELECTED_MEDICINE_TYPE", medicineType);
            nextIntent.putExtra("SELECTED_FREQUENCY", frequency);
            nextIntent.putStringArrayListExtra("SELECTED_DAYS", selectedDays);
            nextIntent.putStringArrayListExtra("SELECTED_TIMES", selectedTimes);
            startActivity(nextIntent);
        });
    }
}