package com.example.medmate;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MedicineView extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChestMedicineAdapter adapter;
    private List<ChestMedicine> medicineList;
    private DatabaseReference databaseRef;
    private String chestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_view);

        chestId = getIntent().getStringExtra("CHEST_ID");
        if (chestId == null || chestId.isEmpty()) {
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recycler_medicines);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        medicineList = new ArrayList<>();
        adapter = new ChestMedicineAdapter(medicineList);
        recyclerView.setAdapter(adapter);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("medicine_chests").child(chestId).child("med");

        loadMedicines();
    }

    private void loadMedicines() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                medicineList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChestMedicine medicine = dataSnapshot.getValue(ChestMedicine.class);
                    if (medicine != null) {
                        medicineList.add(medicine);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}