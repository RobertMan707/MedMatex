package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class MedicineDays extends AppCompatActivity {

    ToggleButton toggleMonday, toggleTuesday, toggleWednesday, toggleThursday, toggleFriday, toggleSaturday, toggleSunday;
    RadioGroup radioGroupFrequency;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_days);

        toggleMonday = findViewById(R.id.toggleMonday);
        toggleTuesday = findViewById(R.id.toggleTuesday);
        toggleWednesday = findViewById(R.id.toggleWednesday);
        toggleThursday = findViewById(R.id.toggleThursday);
        toggleFriday = findViewById(R.id.toggleFriday);
        toggleSaturday = findViewById(R.id.toggleSaturday);
        toggleSunday = findViewById(R.id.toggleSunday);
        radioGroupFrequency = findViewById(R.id.radioGroupFrequency);
        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedDays = getSelectedDays();
                int selectedFrequencyId = radioGroupFrequency.getCheckedRadioButtonId();
                RadioButton selectedFrequency = findViewById(selectedFrequencyId);

                if (selectedDays.isEmpty()) {
                    Toast.makeText(MedicineDays.this, "Please select at least one day.", Toast.LENGTH_SHORT).show();
                } else if (selectedFrequency == null) {
                    Toast.makeText(MedicineDays.this, "Please select a frequency.", Toast.LENGTH_SHORT).show();
                } else {
                    String frequency = selectedFrequency.getText().toString();

                    String medicineName = getIntent().getStringExtra("medicineName");
                    String medicineType = getIntent().getStringExtra("medicineType");

                    Intent intent = new Intent(MedicineDays.this, MedicineTime.class);
                    intent.putExtra("medicineName", medicineName);
                    intent.putExtra("medicineType", medicineType);
                    intent.putExtra("selectedDays", selectedDays);
                    intent.putExtra("frequency", frequency);
                    startActivity(intent);
                }
            }
        });
    }

    private String getSelectedDays() {
        StringBuilder selectedDays = new StringBuilder();

        if (toggleMonday.isChecked()) selectedDays.append("Monday, ");
        if (toggleTuesday.isChecked()) selectedDays.append("Tuesday, ");
        if (toggleWednesday.isChecked()) selectedDays.append("Wednesday, ");
        if (toggleThursday.isChecked()) selectedDays.append("Thursday, ");
        if (toggleFriday.isChecked()) selectedDays.append("Friday, ");
        if (toggleSaturday.isChecked()) selectedDays.append("Saturday, ");
        if (toggleSunday.isChecked()) selectedDays.append("Sunday, ");

        if (selectedDays.length() > 0) {
            selectedDays.setLength(selectedDays.length() - 2);
        }

        return selectedDays.toString();
    }
}
