package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

public class MedicineTime extends AppCompatActivity {

    private LinearLayout timePickerLayout;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_time);

        timePickerLayout = findViewById(R.id.timePickerLayout);
        nextButton = findViewById(R.id.btnNext);

        String frequency = getIntent().getStringExtra("frequency");
        String medicineName = getIntent().getStringExtra("medicineName");
        String medicineType = getIntent().getStringExtra("medicineType");
        String selectedDays = getIntent().getStringExtra("selectedDays");

        updateTimePickers(frequency);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> times = getTimePickersData();
                storeMedicineInFirebase(medicineName, medicineType, frequency, selectedDays, times);

                Intent intent = new Intent(MedicineTime.this, MedicineStockActivity.class);
                intent.putExtra("medicineName", medicineName);
                intent.putExtra("medicineType", medicineType);
                intent.putExtra("frequency", frequency);
                intent.putExtra("selectedDays", selectedDays);
                startActivity(intent);
            }
        });
    }

    private void updateTimePickers(String frequency) {
        timePickerLayout.removeAllViews();

        int freqCount = 1;

        if (frequency != null) {
            switch (frequency) {
                case "Twice per day":
                    freqCount = 2;
                    break;
                case "Three times per day":
                    freqCount = 3;
                    break;
            }
        }

        for (int i = 0; i < freqCount; i++) {
            TimePicker timePicker = new TimePicker(this);
            timePicker.setId(View.generateViewId());
            timePicker.setIs24HourView(true);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            timePicker.setLayoutParams(params);

            timePickerLayout.addView(timePicker);
        }
    }

    private List<String> getTimePickersData() {
        List<String> times = new ArrayList<>();
        int childCount = timePickerLayout.getChildCount();

        for (int i = 0; i < childCount; i++) {
            TimePicker timePicker = (TimePicker) timePickerLayout.getChildAt(i);
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            times.add(hour + ":" + minute);
        }

        return times;
    }

    private void storeMedicineInFirebase(String medicineName, String medicineType, String frequency, String selectedDays, List<String> times) {
        String userId = UserUtils.getDeviceId(this);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String medicineId = database.child("medicines").child(userId).push().getKey();

        if (medicineId == null) {
            Log.e("Firebase", "Medicine ID is null. Cannot store data.");
            return;
        }

        // Convert selectedDays from a String to a List<String>
        List<String> selectedDaysList = new ArrayList<>();
        if (selectedDays != null) {
            String[] daysArray = selectedDays.split(", ");
            selectedDaysList = Arrays.asList(daysArray);
        }

        Map<String, Object> medicineData = new HashMap<>();
        medicineData.put("medicine_name", medicineName);
        medicineData.put("medicine_type", medicineType);
        medicineData.put("frequency", frequency);
        medicineData.put("selected_days", selectedDaysList);
        medicineData.put("times", times);

        database.child("medicines").child(userId).child(medicineId).setValue(medicineData);
    }


}
