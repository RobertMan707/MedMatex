package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class Medicine_count extends AppCompatActivity {

    private TextView medicineTitle, unitText;
    private TextInputEditText medicineAmount;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_count);

        medicineTitle = findViewById(R.id.medicineTitle);
        unitText = findViewById(R.id.unitText);
        medicineAmount = findViewById(R.id.medicineAmountInput);
        submitButton = findViewById(R.id.submitButton);

        String medicineName = getIntent().getStringExtra("MEDICINE_NAME");
        String medicineType = getIntent().getStringExtra("SELECTED_MEDICINE_TYPE");
        int frequency = getIntent().getIntExtra("SELECTED_FREQUENCY", 1);
        ArrayList<String> selectedTimes = getIntent().getStringArrayListExtra("SELECTED_TIMES");
        ArrayList<String> selectedDays = getIntent().getStringArrayListExtra("SELECTED_DAYS");

        medicineTitle.setText(medicineName + " - " + medicineType);

        setUnitBasedOnType(medicineType);

        submitButton.setOnClickListener(v -> {
            String amountText = medicineAmount.getText().toString();
            if (!amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);

                passDataToDosageActivity(medicineName, medicineType, frequency, selectedTimes, selectedDays, amount);
            } else {
                Toast.makeText(Medicine_count.this, "Please enter the amount", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUnitBasedOnType(String medicineType) {
        switch (medicineType) {
            case "Pill/Tablet":
                unitText.setText("Unit: Tablets");
                break;
            case "Capsule":
                unitText.setText("Unit: Capsules");
                break;
            case "Liquid/Syrup":
                unitText.setText("Unit: Ml");
                break;
            case "Drops (Eye/Ear/Nasal)":
                unitText.setText("Unit: Drops");
                break;
            case "Topical (Cream/Ointment)":
                unitText.setText("Unit: Gramms");
                break;
            default:
                unitText.setText("Unit: Unknown");
        }
    }

    private void passDataToDosageActivity(String medicineName, String medicineType, int frequency, ArrayList<String> selectedTimes, ArrayList<String> selectedDays, double amount) {
        Intent dosageIntent = new Intent(Medicine_count.this, medicine_dosage_selection.class);

        dosageIntent.putExtra("MEDICINE_NAME", medicineName);
        dosageIntent.putExtra("SELECTED_MEDICINE_TYPE", medicineType);
        dosageIntent.putExtra("SELECTED_FREQUENCY", frequency);
        dosageIntent.putStringArrayListExtra("SELECTED_TIMES", selectedTimes);
        dosageIntent.putExtra("MEDICINE_AMOUNT", amount);
        dosageIntent.putExtra("SELECTED_DAYS", selectedDays);

        startActivity(dosageIntent);
    }
}
