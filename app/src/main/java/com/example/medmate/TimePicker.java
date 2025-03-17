package com.example.medmate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TimePicker extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        String medicineName = getIntent().getStringExtra("MEDICINE_NAME");
        String medicineType = getIntent().getStringExtra("SELECTED_MEDICINE_TYPE");
        int frequency = getIntent().getIntExtra("SELECTED_FREQUENCY", 1);

        LinearLayout timePickerLayout = findViewById(R.id.timepickerLayout);
        timePickerLayout.removeAllViews();

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
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextIntent = new Intent(TimePicker.this, Medicine_count.class);
                nextIntent.putExtra("MEDICINE_NAME", medicineName);
                nextIntent.putExtra("SELECTED_MEDICINE_TYPE", medicineType);
                // Optionally pass selected times if needed (if you collect the data)
                startActivity(nextIntent);
            }
        });
    }
}
