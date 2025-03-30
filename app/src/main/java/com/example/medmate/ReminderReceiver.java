package com.example.medmate;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "MED_REMINDER_CHANNEL";
    private static final String TAG = "ReminderReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received reminder intent");

        String medicineName = intent.getStringExtra("MEDICINE_NAME");
        if (medicineName == null) {
            medicineName = "Your medication";
        }

        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);
        showNotification(context, medicineName, notificationId);

        // Reschedule for next week
        rescheduleAlarm(context, intent);
    }

    private void showNotification(Context context, String medicineName, int notificationId) {
        createNotificationChannel(context);

        Intent appIntent = new Intent(context, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                appIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pill)
                .setContentTitle("Time for " + medicineName)
                .setContentText("Don't forget to take your medicine")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build();

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationId, notification);
            Log.d(TAG, "Notification shown for: " + medicineName);
        }
    }

    private void rescheduleAlarm(Context context, Intent originalIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        int requestCode = originalIntent.getIntExtra("NOTIFICATION_ID", 0);
        String medicineName = originalIntent.getStringExtra("MEDICINE_NAME");

        PendingIntent pendingIntent = createPendingIntent(context, medicineName, requestCode);

        Calendar nextAlarm = Calendar.getInstance();
        nextAlarm.add(Calendar.DAY_OF_YEAR, 7); // Schedule for same time next week

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextAlarm.getTimeInMillis(),
                    pendingIntent);
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    nextAlarm.getTimeInMillis(),
                    pendingIntent);
        }
    }

    private PendingIntent createPendingIntent(Context context, String medicineName, int requestCode) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("MEDICINE_NAME", medicineName);
        intent.putExtra("NOTIFICATION_ID", requestCode);

        return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Medicine Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Alerts for medication schedules");
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}