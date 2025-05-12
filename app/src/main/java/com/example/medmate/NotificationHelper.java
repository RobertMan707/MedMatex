package com.example.medmate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

public class NotificationHelper {
    public static void scheduleRecurringExpiryReminder(Context context, String medId, String medicineName, String expiryDate, String startReminderDaysStr, int intervalDays) {
        String[] dateParts = expiryDate.split("/");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int year = Integer.parseInt(dateParts[2]);

        Calendar expiryCalendar = Calendar.getInstance();
        expiryCalendar.set(year, month, day, 9, 0, 0); // Reminder time

        try {
            int startDaysBefore = Integer.parseInt(startReminderDaysStr.trim());

            Calendar startReminderCalendar = (Calendar) expiryCalendar.clone();
            startReminderCalendar.add(Calendar.DAY_OF_YEAR, -startDaysBefore);

            if (startReminderCalendar.after(Calendar.getInstance())) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    Calendar currentReminderTime = (Calendar) startReminderCalendar.clone();
                    while (currentReminderTime.before(expiryCalendar)) {
                        Intent intent = new Intent(context, ReminderReceiver.class);
                        intent.putExtra("medicineName", medicineName);
                        intent.putExtra("daysBefore", calculateDaysBefore(currentReminderTime, expiryCalendar));
                        intent.putExtra("type", "medicine_expiry");

                        int requestCode = (medId + "_expiry_" + currentReminderTime.getTimeInMillis()).hashCode();
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                context,
                                requestCode,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        );

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            alarmManager.setExactAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    currentReminderTime.getTimeInMillis(),
                                    pendingIntent
                            );
                        } else {
                            alarmManager.setExact(
                                    AlarmManager.RTC_WAKEUP,
                                    currentReminderTime.getTimeInMillis(),
                                    pendingIntent
                            );
                        }
                        Log.d("NotificationHelper", "Scheduled expiry reminder for " + medicineName + " on " + currentReminderTime.getTime());
                        currentReminderTime.add(Calendar.DAY_OF_YEAR, intervalDays);
                    }
                }
            } else {
                Log.w("NotificationHelper", "Start reminder date is in the past for " + medicineName);
            }

        } catch (NumberFormatException e) {
            Log.e("NotificationHelper", "Invalid start reminder days input: " + startReminderDaysStr, e);
        }
    }

    private static int calculateDaysBefore(Calendar reminderDate, Calendar expiryDate) {
        long diff = expiryDate.getTimeInMillis() - reminderDate.getTimeInMillis();
        return (int) (diff / (24 * 60 * 60 * 1000));
    }
}