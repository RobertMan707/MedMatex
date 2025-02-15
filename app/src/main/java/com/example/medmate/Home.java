package com.example.medmate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Home extends AppCompatActivity {

    BottomNavigationView bottomNav;
    RecyclerView recyclerView;
    MedicineAdapter medicineAdapter;
    List<Medicine> medicineList;
    DatabaseReference databaseReference;
    TextView nextMedicineTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        medicineList = new ArrayList<>();
        medicineAdapter = new MedicineAdapter(medicineList);
        recyclerView.setAdapter(medicineAdapter);

        nextMedicineTextView = findViewById(R.id.tv_next_medicine);

        databaseReference = FirebaseDatabase.getInstance().getReference("medicines");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                medicineList.clear();
                for (DataSnapshot medicineSnapshot : snapshot.getChildren()) {
                    Medicine medicine = medicineSnapshot.getValue(Medicine.class);
                    if (medicine != null) {
                        medicineList.add(medicine);
                    }
                }

                Collections.sort(medicineList, (m1, m2) -> m1.getTime().compareTo(m2.getTime()));

                // Display the next medicine
                if (!medicineList.isEmpty()) {
                    Medicine nextMedicine = medicineList.get(0);  // Next medicine is the first one in sorted list
                    nextMedicineTextView.setText("Next medicine: " + nextMedicine.getName() + " at " + nextMedicine.getTime());
                }

                // Notify the adapter
                medicineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });

        bottomNav = findViewById(R.id.nav_menu);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    return true;
                } else if (itemId == R.id.nav_add) {
                    startActivity(new Intent(Home.this, AddActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_chest) {
                    startActivity(new Intent(Home.this, ChestActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(Home.this, Profile.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }

    public void updateMedicineStatus(Medicine medicine) {

        databaseReference.child(medicine.getId()).child("isTaken").setValue(medicine.isTaken());
    }
}
