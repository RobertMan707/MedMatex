package com.example.medmate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class MedicineView extends AppCompatActivity implements ChestMedicineAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ChestMedicineAdapter adapter;
    private List<ChestMedicine> medicineList;
    private DatabaseReference databaseRef;
    private String chestId;
    private static final String TAG = "MedicineView";

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
        adapter = new ChestMedicineAdapter(medicineList, this);
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
                        medicine.setKey(dataSnapshot.getKey());
                        medicineList.add(medicine);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database Error: " + error.getMessage());
            }
        });
    }

    @Override
    public void onDeleteClick(int position) {
        if (position != RecyclerView.NO_POSITION) {
            ChestMedicine medicine = medicineList.get(position);
            String medicineKey = medicine.getKey();

            // 1. Cancel the alarm *before* deleting from Firebase
            cancelExpiryAlarm(medicineKey);

            // 2. Remove from Firebase
            databaseRef.child(medicineKey).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (medicineList.size() > position) {
                        medicineList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Log.d(TAG, "Medicine deleted successfully from Firebase");
                    } else {
                        Log.e(TAG, "Error: position is out of bounds.  position: " + position + ", size: " + medicineList.size());
                    }
                } else {
                    Log.e(TAG, "Error deleting medicine: " + task.getException().getMessage());
                }
            });
        }
    }

    private void cancelExpiryAlarm(String medicineKey) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null");
            return;
        }

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("type", "medicine_expiry");
        // Use the same request code (medId.hashCode()) that you used when creating the alarm
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                medicineKey.hashCode(), //  medicineKey.hashCode()
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE // Use FLAG_CANCEL_CURRENT
        );

        alarmManager.cancel(pendingIntent);
        Log.d(TAG, "Expiry alarm cancelled for medicine key: " + medicineKey);
    }
}

