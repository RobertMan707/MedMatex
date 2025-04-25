package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Random;

public class ChestSizeActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest_size);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        String chestName = getIntent().getStringExtra("CHEST_NAME");

        RadioGroup sizeGroup = findViewById(R.id.radioGroupSize);
        Button btnCreate = findViewById(R.id.btnCreateChest);

        btnCreate.setOnClickListener(v -> {
            int selectedId = sizeGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Please select size", Toast.LENGTH_SHORT).show();
                return;
            }

            String size = getSizeFromSelection(selectedId);
            createChest(chestName, size);
        });
    }

    private String getSizeFromSelection(int selectedId) {
        if (selectedId == R.id.radioSmall) return "10";
        if (selectedId == R.id.radioMedium) return "30";
        return "50";
    }

    private void createChest(String name, String size) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String chestId = "CHEST_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);

        MedicineChest chest = new MedicineChest(chestId, name, size);

        mDatabase.child("users").child(userId).child("medicine_chests").child(chestId)
                .setValue(chest)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Chest created!", Toast.LENGTH_SHORT).show();

                    // Go directly to ChestActivity and clear back stack
                    Intent intent = new Intent(this, ChestActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}