package com.example.medmate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

public class MedicineTime extends AppCompatActivity {

    private LinearLayout timePickerLayout;
    private Button nextButton;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_time);

        timePickerLayout = findViewById(R.id.timePickerLayout); // Corrected to match layout ID
        nextButton = findViewById(R.id.btnNext);

        // Get frequency from the previous activity (MedicineDays)
        String frequency = getIntent().getStringExtra("frequency");
        updateTimePickers(frequency); // Initialize time pickers based on frequency

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // You can pass data to the Home activity, if necessary
                Intent intent = new Intent(MedicineTime.this, Home.class);
                startActivity(intent);
            }
        });
    }

    // This method updates the TimePicker views based on frequency selected
    private void updateTimePickers(String frequency) {
        // Clear previous time pickers
        timePickerLayout.removeAllViews();

        int freqCount = 1; // Default to once per day
        int height = 1000; // Default height for "Once per day"
        int width = LinearLayout.LayoutParams.MATCH_PARENT; // Default width (full width)

        if (frequency != null) {
            switch (frequency) {
                case "Twice per day":
                    freqCount = 2;
                    height = 1000; // Height for "Twice per day"
                    width = 800; // Set a smaller width for "Twice per day"
                    break;
                case "Three times per day":
                    freqCount = 3;
                    height = 1000; // Height for "Three times per day"
                    width = 600; // Set a smaller width for "Three times per day"
                    break;
                default:
                    freqCount = 1;
                    height = 1000; // Height for "Once per day"
                    width = LinearLayout.LayoutParams.MATCH_PARENT; // Default width (full width)
                    break;
            }
        }

        // Add dynamic TimePickers based on frequency
        for (int i = 0; i < freqCount; i++) {
            TimePicker timePicker = new TimePicker(this);
            timePicker.setId(View.generateViewId()); // Unique ID for each TimePicker
            timePicker.setIs24HourView(true); // Set 24-hour format

            // Set custom width and height based on frequency
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    width,  // Dynamic width based on frequency
                    height  // Dynamic height based on frequency
            );
            timePicker.setLayoutParams(params); // Apply the size

            timePickerLayout.addView(timePicker); // Add TimePicker to layout
        }
    }
}
