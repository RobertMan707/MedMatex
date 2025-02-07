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

        timePickerLayout = findViewById(R.id.timePickerLayout);
        nextButton = findViewById(R.id.btnNext);

        String frequency = getIntent().getStringExtra("frequency");
        String medicineName = getIntent().getStringExtra("medicineName");
        String medicineType = getIntent().getStringExtra("medicineType");
        updateTimePickers(frequency);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MedicineTime.this, MedicineStockActivity.class);
                intent.putExtra("medicineName", medicineName);
                intent.putExtra("medicineType", medicineType);
                intent.putExtra("frequency", frequency);
                startActivity(intent);
            }
        });
    }

    private void updateTimePickers(String frequency) {
        timePickerLayout.removeAllViews();

        int freqCount = 1;
        int height = 1000;
        int width = LinearLayout.LayoutParams.MATCH_PARENT;

        if (frequency != null) {
            switch (frequency) {
                case "Twice per day":
                    freqCount = 2;
                    height = 1000;
                    width = 800;
                    break;
                case "Three times per day":
                    freqCount = 3;
                    height = 1000;
                    width = 600;
                    break;
                default:
                    freqCount = 1;
                    height = 1000;
                    width = LinearLayout.LayoutParams.MATCH_PARENT;
                    break;
            }
        }

        for (int i = 0; i < freqCount; i++) {
            TimePicker timePicker = new TimePicker(this);
            timePicker.setId(View.generateViewId());
            timePicker.setIs24HourView(true);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    width,
                    height
            );
            timePicker.setLayoutParams(params);

            timePickerLayout.addView(timePicker);
        }
    }
}
