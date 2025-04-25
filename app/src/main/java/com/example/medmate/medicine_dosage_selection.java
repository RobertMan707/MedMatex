package com.example.medmate;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
    private static final int EXACT_ALARM_PERMISSION_REQUEST = 101;

    private EditText editTextDosage, editTextLowStock;
    private Button btnSaveDosage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_dosage_selection);

        // Initialize views
        editTextDosage = findViewById(R.id.editTextDosage);
        editTextLowStock = findViewById(R.id.editTextLowStock);
        btnSaveDosage = findViewById(R.id.btnSaveDosage);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get intent data
        Intent intent = getIntent();
        String medicineName = intent.getStringExtra("MEDICINE_NAME");
        String medicineType = intent.getStringExtra("SELECTED_MEDICINE_TYPE");
        int frequency = intent.getIntExtra("SELECTED_FREQUENCY", 1);
        ArrayList<String> selectedTimes = intent.getStringArrayListExtra("SELECTED_TIMES");
        double amount = intent.getDoubleExtra("MEDICINE_AMOUNT", 0);
        ArrayList<String> selectedDays = intent.getStringArrayListExtra("SELECTED_DAYS");

        Log.d(TAG, "Medicine: " + medicineName + ", Days: " + selectedDays);

        btnSaveDosage.setOnClickListener(v -> {
            // Check exact alarm permission before proceeding
            if (!AlarmPermissionHelper.hasExactAlarmPermission(this)) {
                AlarmPermissionHelper.showPermissionRationale(this);
                return;
            }
            saveMedicine(medicineName, medicineType, frequency, selectedTimes, amount, selectedDays);
        });
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
        DatabaseReference userMedicinesRef = mDatabase.child("users")
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

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null");
            return false;
        }

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

            // If time is in past, schedule for next week
            if (alarmTime.before(Calendar.getInstance())) {
                alarmTime.add(Calendar.DAY_OF_YEAR, 7);
            }

            int requestCode = generateUniqueId(dayOfWeek, hour, minute);
            PendingIntent pendingIntent = createPendingIntent(medicineName, requestCode);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.e(TAG, "Exact alarm permission not granted");
                    return false;
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime.getTimeInMillis(),
                        pendingIntent);
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime.getTimeInMillis(),
                        pendingIntent);
            }

            Log.d(TAG, "Alarm set: " + alarmTime.getTime() + " for " + medicineName);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Alarm scheduling error: ", e);
            return false;
        }
    }

    private PendingIntent createPendingIntent(String medicineName, int requestCode) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.setAction("ACTION_MEDICINE_REMINDER_" + requestCode);
        intent.putExtra("MEDICINE_NAME", medicineName);
        intent.putExtra("NOTIFICATION_ID", requestCode);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        return PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                flags
        );
    }

    private int generateUniqueId(int dayOfWeek, int hour, int minute) {
        return (dayOfWeek * 10000) + (hour * 100) + minute;
    }

    private int convertDayToCalendar(String day) {
        switch (day.toLowerCase()) {
            case "monday": return Calendar.MONDAY;
            case "tuesday": return Calendar.TUESDAY;
            case "wednesday": return Calendar.WEDNESDAY;
            case "thursday": return Calendar.THURSDAY;
            case "friday": return Calendar.FRIDAY;
            case "saturday": return Calendar.SATURDAY;
            default: return Calendar.SUNDAY;
        }
    }

    private void goToHome() {
        startActivity(new Intent(this, Home.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXACT_ALARM_PERMISSION_REQUEST) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                    btnSaveDosage.performClick();
                }
            }
        }
    }
}