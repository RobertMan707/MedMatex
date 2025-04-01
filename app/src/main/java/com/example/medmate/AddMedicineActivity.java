package com.example.medmate;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddMedicineActivity extends AppCompatActivity {

    private EditText editMedicineName, editExpiryDate, editReminderDays;
    private DatabaseReference database;
    private String chestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        // Initialize views
        editMedicineName = findViewById(R.id.edit_medicine_name);
        editExpiryDate = findViewById(R.id.edit_expiry_date);
        editReminderDays = findViewById(R.id.edit_reminder_days);
        Button btnSaveMedicine = findViewById(R.id.btn_save_medicine);

        // Get chestId from Intent
        chestId = getIntent().getStringExtra("CHEST_ID");
        if (chestId == null || chestId.isEmpty()) {
            Toast.makeText(this, "Error: Chest ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Firebase setup
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("medicine_chests").child(chestId).child("med");

        // Date picker
        editExpiryDate.setOnClickListener(v -> showDatePickerDialog());

        // Save medicine
        btnSaveMedicine.setOnClickListener(v -> saveMedicine());
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

    private void saveMedicine() {
        String name = editMedicineName.getText().toString().trim();
        String expiryDate = editExpiryDate.getText().toString().trim();
        String reminderDays = editReminderDays.getText().toString().trim();

        if (name.isEmpty() || expiryDate.isEmpty() || reminderDays.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate unique ID for medID
        String medID = database.push().getKey();
        if (medID == null) {
            Toast.makeText(this, "Failed to generate medicine ID!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create medicine object
        Map<String, Object> medicine = new HashMap<>();
        medicine.put("medID", medID); // Explicitly include medID
        medicine.put("name", name);
        medicine.put("expiry_date", expiryDate);
        medicine.put("reminder_days", reminderDays);

        // Save to Firebase under "med"
        database.child(medID).setValue(medicine)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Medicine saved!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}