package com.example.medmate;

import android.app.IntentService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class RescheduleAlarmsService extends IntentService {
    private static final String TAG = "RescheduleAlarmsService";

    public RescheduleAlarmsService() {
        super("RescheduleAlarmsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Rescheduling all alarms");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference userMedicinesRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid())
                .child("medicines");

        userMedicinesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot medicineSnapshot : dataSnapshot.getChildren()) {
                    Medicine medicine = medicineSnapshot.getValue(Medicine.class);
                    if (medicine != null) {
                        rescheduleMedicineAlarms(medicine);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to load medicines: " + databaseError.getMessage());
            }
        });
    }

    private void rescheduleMedicineAlarms(Medicine medicine) {
        try {
            ArrayList<String> days = parseDaysFromFrequency(medicine.getDaysFrequency());
            ArrayList<String> times = parseTimesFromSelection(medicine.getTimeSelection());

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager == null) return;

            for (String day : days) {
                int dayOfWeek = convertDayToCalendar(day);
                for (String time : times) {
                    scheduleSingleAlarm(alarmManager, medicine.getName(), dayOfWeek, time);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to reschedule alarms for " + medicine.getName(), e);
        }
    }

    // Helper method to parse days from frequency string
    private ArrayList<String> parseDaysFromFrequency(String daysFrequency) {
        ArrayList<String> days = new ArrayList<>();
        // Remove brackets and split by comma
        String cleaned = daysFrequency.replace("[", "").replace("]", "");
        String[] dayArray = cleaned.split(", ");
        for (String day : dayArray) {
            if (!day.isEmpty()) {
                days.add(day);
            }
        }
        return days;
    }

    // Helper method to parse times from selection string
    private ArrayList<String> parseTimesFromSelection(String timeSelection) {
        ArrayList<String> times = new ArrayList<>();
        // Remove brackets and split by comma
        String cleaned = timeSelection.replace("[", "").replace("]", "");
        String[] timeArray = cleaned.split(", ");
        for (String time : timeArray) {
            if (!time.isEmpty()) {
                times.add(time);
            }
        }
        return times;
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

    private void scheduleSingleAlarm(AlarmManager alarmManager, String medicineName,
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
                    return;
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

            Log.d(TAG, "Alarm rescheduled for " + medicineName + " at " + alarmTime.getTime());
        } catch (Exception e) {
            Log.e(TAG, "Failed to reschedule alarm", e);
        }
    }

    private PendingIntent createPendingIntent(String medicineName, int requestCode) {
        Intent intent = new Intent(this, ReminderReceiver.class);
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
}