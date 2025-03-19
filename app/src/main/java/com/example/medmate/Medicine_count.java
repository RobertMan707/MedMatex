package com.example.medmate;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Medicine_count extends AppCompatActivity {

    private TextView medicineTitle, unitText;
    private EditText medicineAmount;
    private Button submitButton;
    private String selectedMedicineType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_count);

        medicineTitle= findViewById(R.id.medicineTitle);
        unitText = findViewById(R.id.unitText);
        medicineAmount = findViewById(R.id.medicineAmountLayout);
        submitButton = findViewById(R.id.submitButton);

        selectedMedicineType = getIntent().getStringExtra("SELECTED_MEDICINE_TYPE");

        medicineTitlesetText(selectedMedicineType);

        setUnitBasedOnType(selectedMedicineType);

        submitButton.setOnClickListener(v -> {
            String amountText = medicineAmount.getText().toString();
            if (!amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                // Process the entered amount and the selected unit
                processAmount(amount);
            } else {
                Toast.makeText(Medicine_count.this, "Please enter the amount", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUnitBasedOnType(String medicineType) {
        switch (medicineType) {
            case "Pill/Tablet":
                unitText.setText("Unit: Count");
                break;
            case "Capsule":
                unitText.setText("Unit: Count");
                break;
            case "Liquid/Syrup":
                unitText.setText("Unit: ml");
                break;
            case "Drops (Eye/Ear/Nasal)":
                unitText.setText("Unit: Drops");
                break;
            case "Topical (Cream/Ointment)":
                unitText.setText("Unit: g");
                break;
            default:
                unitText.setText("Unit: Unknown");
        }
    }

    private void processAmount(double amount) {
        Toast.makeText(Medicine_count.this, "Amount: " + amount + " " + unitText.getText().toString(), Toast.LENGTH_SHORT).show();
    }
}
