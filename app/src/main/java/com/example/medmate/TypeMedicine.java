package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TypeMedicine extends AppCompatActivity {

    Button btnPill, btnCapsule, btnLiquid, btnDrops, btnTopical, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_medicine);

        btnPill = findViewById(R.id.btnPill);
        btnCapsule = findViewById(R.id.btnCapsule);
        btnLiquid = findViewById(R.id.btnLiquid);
        btnDrops = findViewById(R.id.btnDrops);
        btnTopical = findViewById(R.id.btnTopical);
        btnNext = findViewById(R.id.btnNext);

        btnPill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextScreen("Pill/Tablet");
            }
        });

        btnCapsule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextScreen("Capsule");
            }
        });

        btnLiquid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextScreen("Liquid/Syrup");
            }
        });

        btnDrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextScreen("Drops (Eye/Ear/Nasal)");
            }
        });

        btnTopical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToNextScreen("Topical (Cream/Ointment)");
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Proceed to the next step, passing the selected type
                Intent intent = new Intent(TypeMedicine.this, MedicineName.class);
                startActivity(intent);
            }
        });
    }

    private void navigateToNextScreen(String type) {
        Intent intent = new Intent(TypeMedicine.this, MedicineName.class);
        intent.putExtra("medicineType", type);
        startActivity(intent);
    }
}
