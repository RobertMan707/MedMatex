package com.example.medmate;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;

public class AddMedicineActivity extends AppCompatActivity {

    private EditText editMedicineName, editExpiryDate, edit_start_reminder_days, edit_reminder_interval;
    private DatabaseReference database;
    private String chestId;
    private static final int EXACT_ALARM_PERMISSION_REQUEST = 201;

    private String medicineNameToSave;
    private String expiryDateToSave;
    private String startReminderDaysInput;
    private String reminderIntervalInput;
    private String currentMedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        editMedicineName = findViewById(R.id.edit_medicine_name);
        editExpiryDate = findViewById(R.id.edit_expiry_date);
        edit_start_reminder_days = findViewById(R.id.edit_start_reminder_days);
        edit_reminder_interval = findViewById(R.id.edit_reminder_interval);
        Button btnSaveMedicine = findViewById(R.id.btn_save_medicine);

        chestId = getIntent().getStringExtra("CHEST_ID");
        if (chestId == null || chestId.isEmpty()) {
            Toast.makeText(this, "Error: Chest ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("medicine_chests").child(chestId).child("med");

        editExpiryDate.setOnClickListener(v -> showDatePickerDialog());

        btnSaveMedicine.setOnClickListener(v -> {
            medicineNameToSave = editMedicineName.getText().toString().trim();
            expiryDateToSave = editExpiryDate.getText().toString().trim();
            startReminderDaysInput = edit_start_reminder_days.getText().toString().trim();
            reminderIntervalInput = edit_reminder_interval.getText().toString().trim();

            if (validateInput(medicineNameToSave, expiryDateToSave, startReminderDaysInput, reminderIntervalInput)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                        !AlarmPermissionHelper.hasExactAlarmPermission(this)) {
                    AlarmPermissionHelper.requestExactAlarmPermission(this);
                } else {
                    saveMedicineToFirebase();
                }
            }
        });
    }

    private boolean validateInput(String name, String expiry, String startDays, String interval) {
        if (name.isEmpty()) {
            editMedicineName.setError("Medicine name is required!");
            editMedicineName.requestFocus();
            return false;
        }
        if (expiry.isEmpty()) {
            editExpiryDate.setError("Expiry date is required!");
            editExpiryDate.requestFocus();
            return false;
        }
        if (startDays.isEmpty()) {
            edit_start_reminder_days.setError("Start reminder days are required (e.g., 14)!");
            edit_start_reminder_days.requestFocus();
            return false;
        }
        if (!startDays.matches("^\\d+$")) {
            edit_start_reminder_days.setError("Enter a valid number for start reminder days.");
            edit_start_reminder_days.requestFocus();
            return false;
        }
        if (interval.isEmpty()) {
            edit_reminder_interval.setError("Reminder interval is required (e.g., 5)!");
            edit_reminder_interval.requestFocus();
            return false;
        }
        if (!interval.matches("^\\d+$")) {
            edit_reminder_interval.setError("Enter a valid number for reminder interval.");
            edit_reminder_interval.requestFocus();
            return false;
        }
        return true;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    String selectedDate = day + "/" + (month + 1) + "/" + year;
                    editExpiryDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void saveMedicineToFirebase() {
        String medID = database.push().getKey();
        if (medID == null) {
            Toast.makeText(this, "Failed to generate medicine ID!", Toast.LENGTH_SHORT).show();
            return;
        }
        currentMedId = medID;

        ChestMedicine medicine = new ChestMedicine(medID, medicineNameToSave, expiryDateToSave, startReminderDaysInput + "," + reminderIntervalInput); // Store both in DB

        database.child(medID).setValue(medicine)
                .addOnSuccessListener(aVoid -> {
                    NotificationHelper.scheduleRecurringExpiryReminder(
                            this,
                            medID,
                            medicineNameToSave,
                            expiryDateToSave,
                            startReminderDaysInput,
                            Integer.parseInt(reminderIntervalInput)
                    );
                    Toast.makeText(this, "Medicine saved with expiry reminders!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXACT_ALARM_PERMISSION_REQUEST) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                    saveMedicineToFirebase();
                } else {
                    Toast.makeText(this, "Exact alarm permission was not granted. Expiry reminders might not be reliable.", Toast.LENGTH_LONG).show();
                }
            } else {
                saveMedicineToFirebase();
            }
        }
    }
}