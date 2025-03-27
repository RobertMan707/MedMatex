package com.example.medmate;

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
    private static final String TAG = "ReminderReceiver";
    private static final String CHANNEL_ID = "MED_REMINDER_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received intent: " + intent.getExtras());

        String medicineName = intent.getStringExtra("MEDICINE_NAME");
        if (medicineName == null) {
            Log.e(TAG, "Medicine name is null! Using fallback");
            medicineName = "Your Medicine";
        }

        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);
        showNotification(context, medicineName, notificationId);
    }

    private void showNotification(Context context, String medicineName, int notificationId) {
        createNotificationChannel(context);

        Intent appIntent = new Intent(context, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                appIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pill)
                .setContentTitle("Time for " + medicineName)
                .setContentText("Don't forget to take your medicine")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            manager.notify(notificationId, notification);
            Log.d(TAG, "Notification shown for: " + medicineName);
        } catch (SecurityException e) {
            Log.e(TAG, "Notification failed: ", e);
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

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}