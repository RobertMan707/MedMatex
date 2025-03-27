package com.example.medmate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class medicine_dosage_selection extends AppCompatActivity {
    private static final String TAG = "DosageSelection";

    private EditText editTextDosage, editTextLowStock;
    private Button btnSaveDosage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_dosage_selection);

        editTextDosage = findViewById(R.id.editTextDosage);
        editTextLowStock = findViewById(R.id.editTextLowStock);
        btnSaveDosage = findViewById(R.id.btnSaveDosage);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        String medicineName = intent.getStringExtra("MEDICINE_NAME");
        String medicineType = intent.getStringExtra("SELECTED_MEDICINE_TYPE");
        int frequency = intent.getIntExtra("SELECTED_FREQUENCY", 1);
        ArrayList<String> selectedTimes = intent.getStringArrayListExtra("SELECTED_TIMES");
        double amount = intent.getDoubleExtra("MEDICINE_AMOUNT", 0);
        ArrayList<String> selectedDays = intent.getStringArrayListExtra("SELECTED_DAYS");

        Log.d(TAG, "Medicine: " + medicineName + ", Days: " + selectedDays);

        btnSaveDosage.setOnClickListener(v -> saveMedicine(medicineName, medicineType, frequency,
                selectedTimes, amount, selectedDays));
    }

    private void saveMedicine(String medicineName, String medicineType, int frequency,
                              ArrayList<String> selectedTimes, double amount,
                              ArrayList<String> selectedDays) {
        // Validate input fields
        String dosage = editTextDosage.getText().toString().trim();
        String lowStock = editTextLowStock.getText().toString().trim();

        if (dosage.isEmpty() || lowStock.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate medicine name
        if (medicineName == null || medicineName.isEmpty()) {
            Toast.makeText(this, "Medicine name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check user authentication
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get reference to user's medicines
        String userId = user.getUid();
        DatabaseReference userMedicinesRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("medicines");

        // Create new medicine entry
        String medicineId = userMedicinesRef.push().getKey();
        Medicine medicine = new Medicine(
                medicineName,
                (int) amount,
                selectedTimes.toString(),
                medicineType,
                false,
                selectedDays.toString(),
                dosage,
                lowStock,
                String.valueOf(amount)
        );

        // Save to Firebase
        if (medicineId != null) {
            userMedicinesRef.child(medicineId).setValue(medicine)
                    .addOnSuccessListener(aVoid -> {
                        // Schedule alarms only after successful save
                        if (scheduleAllAlarms(medicineName, selectedDays, selectedTimes)) {
                            Toast.makeText(this, "Medicine saved with reminders!",
                                    Toast.LENGTH_SHORT).show();
                            goToHome();
                        } else {
                            // Delete the medicine if alarm scheduling fails
                            userMedicinesRef.child(medicineId).removeValue();
                            Toast.makeText(this, "Failed to schedule reminders",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Save failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Firebase save error", e);
                    });
        }
    }

    private boolean scheduleAllAlarms(String medicineName, ArrayList<String> selectedDays,
                                      ArrayList<String> selectedTimes) {
        Log.d(TAG, "Scheduling alarms for: " + medicineName);

        if (medicineName == null || medicineName.isEmpty()) {
            Log.e(TAG, "Medicine name is null/empty!");
            return false;
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        boolean allAlarmsSet = true;

        for (String day : selectedDays) {
            int dayOfWeek = convertDayToCalendar(day);
            Log.d(TAG, "Day: " + day + " → " + dayOfWeek);

            for (String time : selectedTimes) {
                if (!scheduleSingleAlarm(alarmManager, medicineName, dayOfWeek, time)) {
                    allAlarmsSet = false;
                }
            }
        }
        return allAlarmsSet;
    }

    private boolean scheduleSingleAlarm(AlarmManager alarmManager, String medicineName,
                                        int dayOfWeek, String time) {
        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            Calendar alarmTime = Calendar.getInstance();
            alarmTime.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            alarmTime.set(Calendar.HOUR_OF_DAY, hour);
            alarmTime.set(Calendar.MINUTE, minute);
            alarmTime.set(Calendar.SECOND, 0);
            alarmTime.set(Calendar.MILLISECOND, 0);

            if (alarmTime.before(Calendar.getInstance())) {
                alarmTime.add(Calendar.DAY_OF_YEAR, 7);
            }

            int requestCode = generateUniqueId(dayOfWeek, hour, minute);
            PendingIntent pendingIntent = createPendingIntent(medicineName, requestCode);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime.getTimeInMillis(),
                        pendingIntent
                );
            }

            Log.d(TAG, "Alarm set: " + alarmTime.getTime() + " for " + medicineName);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Alarm error: ", e);
            return false;
        }
    }

    private PendingIntent createPendingIntent(String medicineName, int requestCode) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.setAction("ACTION_MEDICINE_REMINDER_" + requestCode);
        intent.putExtra("MEDICINE_NAME", medicineName);
        intent.putExtra("NOTIFICATION_ID", requestCode);

        return PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );
    }

    private int generateUniqueId(int dayOfWeek, int hour, int minute) {
        return (dayOfWeek * 10000) + (hour * 100) + minute;
    }

    private int convertDayToCalendar(String day) {
        String dayLower = day.toLowerCase();
        if (dayLower.startsWith("sun")) return Calendar.SUNDAY;
        if (dayLower.startsWith("mon")) return Calendar.MONDAY;
        if (dayLower.startsWith("tue")) return Calendar.TUESDAY;
        if (dayLower.startsWith("wed")) return Calendar.WEDNESDAY;
        if (dayLower.startsWith("thu")) return Calendar.THURSDAY;
        if (dayLower.startsWith("fri")) return Calendar.FRIDAY;
        if (dayLower.startsWith("sat")) return Calendar.SATURDAY;
        return Calendar.SUNDAY;
    }

    private void goToHome() {
        startActivity(new Intent(this, Home.class));
        finish();
    }
}