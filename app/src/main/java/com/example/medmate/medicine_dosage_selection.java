package com.example.medmate;

import android.content.Intent;
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

public class medicine_dosage_selection extends AppCompatActivity {

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

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Debug: Check if FirebaseAuth is initialized
        if (mAuth == null) {
            Log.e("FirebaseDebug", "FirebaseAuth is not initialized!");
        } else {
            Log.d("FirebaseDebug", "FirebaseAuth initialized successfully.");
        }

        // Get data from the previous activity
        String medicineName = getIntent().getStringExtra("MEDICINE_NAME");
        String medicineType = getIntent().getStringExtra("SELECTED_MEDICINE_TYPE");
        int frequency = getIntent().getIntExtra("SELECTED_FREQUENCY", 1);
        ArrayList<String> selectedTimes = getIntent().getStringArrayListExtra("SELECTED_TIMES");
        double amount = getIntent().getDoubleExtra("MEDICINE_AMOUNT", 0);
        String daysFrequency = getIntent().getStringExtra("DAYS_FREQUENCY");

        btnSaveDosage.setOnClickListener(v -> {
            saveMedicineData(medicineName, medicineType, frequency, selectedTimes, amount, daysFrequency);
        });
    }

    private void saveMedicineData(String medicineName, String medicineType, int frequency, ArrayList<String> selectedTimes, double amount, String daysFrequency) {
        String dosage = editTextDosage.getText().toString();
        String lowStock = editTextLowStock.getText().toString();

        if (dosage.isEmpty() || lowStock.isEmpty()) {
            Toast.makeText(medicine_dosage_selection.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int count = (int) amount;

        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();

        // Debug: Check if user is logged in
        if (user == null) {
            Log.e("FirebaseDebug", "User is not logged in!");
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Log.d("FirebaseDebug", "User is logged in: " + user.getUid());
        }

        // Get the user ID
        String userId = user.getUid();

        // Create a unique ID for the medicine
        String medicineId = mDatabase.child("users").child(userId).child("medicines").push().getKey();

        // Create the Medicine object
        Medicine medicine = new Medicine(
                medicineName,
                count,
                selectedTimes.toString(),
                medicineType,
                false,
                daysFrequency,
                dosage,
                lowStock,
                String.valueOf(amount)
        );

        // Save the medicine under the user's medicines node
        if (medicineId != null) {
            mDatabase.child("users").child(userId).child("medicines").child(medicineId).setValue(medicine)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(medicine_dosage_selection.this, "Medicine saved successfully!", Toast.LENGTH_SHORT).show();
                        goToHome();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(medicine_dosage_selection.this, "Failed to save medicine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void goToHome() {
        Intent homeIntent = new Intent(medicine_dosage_selection.this, Home.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }
}