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

public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "MED_REMINDER_CHANNEL";
    private static final String TAG = "ReminderReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            // Check if alarms were cancelled (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (!am.canScheduleExactAlarms()) {
                    showPermissionLostNotification(context);
                    return;
                }
            }

            String medicineName = intent.getStringExtra("MEDICINE_NAME");
            int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);

            createNotificationChannel(context);
            Notification notification = buildNotification(context, medicineName);
            showNotification(context, notificationId, notification);

            Log.d(TAG, "Notification shown for: " + medicineName);
        } catch (Exception e) {
            Log.e(TAG, "Error in onReceive: " + e.getMessage());
        }
    }

    private Notification buildNotification(Context context, String medicineName) {
        Intent appIntent = new Intent(context, Home.class);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                appIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pill)
                .setContentTitle("💊 Time for " + medicineName)
                .setContentText("Don't forget to take your medicine")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(false)
                .build();
    }

    private void showNotification(Context context, int notificationId, Notification notification) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            manager.notify(notificationId, notification);
        } catch (SecurityException e) {
            Log.e(TAG, "Notification permission error: " + e.getMessage());
        }
    }

    private void showPermissionLostNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pill)
                .setContentTitle("MedMate Alarms Disabled")
                .setContentText("Please reopen app to re-enable medication reminders")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            manager.notify(999999, builder.build());
        } catch (Exception e) {
            Log.e(TAG, "Failed to show permission lost notification: " + e.getMessage());
        }
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
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);
            try {
                manager.createNotificationChannel(channel);
            } catch (Exception e) {
                Log.e(TAG, "Failed to create notification channel: " + e.getMessage());
            }
        }
    }
}