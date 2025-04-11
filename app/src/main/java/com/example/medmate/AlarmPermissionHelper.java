package com.example.medmate;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class AlarmPermissionHelper {
    private static final String TAG = "AlarmPermissionHelper";

    public static boolean hasExactAlarmPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                return am.canScheduleExactAlarms();
            } catch (Exception e) {
                Log.e(TAG, "Permission check failed", e);
                return false;
            }
        }
        return true;
    }

    public static void requestExactAlarmPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Failed to launch permission intent", e);

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(android.net.Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            }
        }
    }

    public static void showPermissionRationale(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Alarm Permission Needed")
                .setMessage("MedMate needs permission to set exact alarms for reliable medication reminders")
                .setPositiveButton("Allow", (dialog, which) -> {
                    requestExactAlarmPermission(context);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}