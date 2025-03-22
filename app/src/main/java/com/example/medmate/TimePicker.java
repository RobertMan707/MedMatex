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

public class TimePicker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        String medicineName = getIntent().getStringExtra("SELECTED_MEDICINE_NAME");
        String medicineType = getIntent().getStringExtra("SELECTED_MEDICINE_TYPE");
        int frequency = getIntent().getIntExtra("SELECTED_FREQUENCY", 1);
        ArrayList<String> selectedDays = getIntent().getStringArrayListExtra("SELECTED_DAYS");

        LinearLayout timePickerLayout = findViewById(R.id.timepickerLayout);
        timePickerLayout.removeAllViews();

        ArrayList<String> selectedTimes = new ArrayList<>();

        for (int i = 0; i < frequency; i++) {
            NumberPicker hourPicker = new NumberPicker(this);
            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(23);
            hourPicker.setValue(8);
            hourPicker.setWrapSelectorWheel(true);

            NumberPicker minutePicker = new NumberPicker(this);
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(59);
            minutePicker.setValue(0);
            minutePicker.setWrapSelectorWheel(true);

            LinearLayout pickerContainer = new LinearLayout(this);
            pickerContainer.setOrientation(LinearLayout.HORIZONTAL);
            pickerContainer.setPadding(8, 8, 8, 8);
            pickerContainer.setGravity(Gravity.CENTER);

            pickerContainer.addView(hourPicker);

            TextView colon = new TextView(this);
            colon.setText(":");
            colon.setTextSize(36);
            colon.setGravity(Gravity.CENTER);
            pickerContainer.addView(colon);

            pickerContainer.addView(minutePicker);

            timePickerLayout.addView(pickerContainer);

            selectedTimes.add(hourPicker.getValue() + ":" + minutePicker.getValue());
        }

        TextView timeTitle = findViewById(R.id.timeTitle);
        if (frequency == 1) {
            timeTitle.setText("Select Time (Once a Day)");
        } else if (frequency == 2) {
            timeTitle.setText("Select Time (Twice a Day)");
        } else if (frequency == 3) {
            timeTitle.setText("Select Time (Three Times a Day)");
        }

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            selectedTimes.clear();
            for (int i = 0; i < frequency; i++) {
                LinearLayout pickerContainer = (LinearLayout) timePickerLayout.getChildAt(i);
                NumberPicker hourPicker = (NumberPicker) pickerContainer.getChildAt(0);
                NumberPicker minutePicker = (NumberPicker) pickerContainer.getChildAt(2);
                selectedTimes.add(hourPicker.getValue() + ":" + minutePicker.getValue());
            }

            Intent nextIntent = new Intent(TimePicker.this, Medicine_count.class);
            nextIntent.putExtra("SELECTED_MEDICINE_NAME", medicineName);
            nextIntent.putExtra("SELECTED_MEDICINE_TYPE", medicineType);
            nextIntent.putExtra("SELECTED_FREQUENCY", frequency);
            nextIntent.putStringArrayListExtra("SELECTED_DAYS", selectedDays);
            nextIntent.putStringArrayListExtra("SELECTED_TIMES", selectedTimes);
            startActivity(nextIntent);
        });
    }
}