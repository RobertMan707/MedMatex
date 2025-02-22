package com.example.medmate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class TimePicker extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_time_picker);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String medicineName = getIntent().getStringExtra("MEDICINE_NAME");
        String medicineType = getIntent().getStringExtra("SELECTED_MEDICINE_TYPE");
        int frequency = getIntent().getIntExtra("SELECTED_FREQUENCY", 1);



        Log.d("TimePicker", "Medicine Name: " + medicineName);
        Log.d("TimePicker", "Medicine Type: " + medicineType);
        Log.d("TimePicker", "Frequency: " + frequency);

        LinearLayout timePickerLayout = findViewById(R.id.timepickerLayout);
        timePickerLayout.removeAllViews();

        for (int i = 0; i < frequency; i++) {
            NumberPicker hourPicker = new NumberPicker(this);
            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(23);

            NumberPicker minutePicker = new NumberPicker(this);
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(59);

            LinearLayout pickerContainer = new LinearLayout(this);
            pickerContainer.setOrientation(LinearLayout.HORIZONTAL);
            pickerContainer.addView(hourPicker);
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



            Button nextButton = findViewById(R.id.nextButton);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent nextIntent = new Intent(TimePicker.this, Medicine_count.class);
                    nextIntent.putExtra("MEDICINE_NAME", medicineName);
                    nextIntent.putExtra("SELECTED_MEDICINE_TYPE", medicineType);
                    startActivity(nextIntent);
                }
            });
        }
    }
}