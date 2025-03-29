package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ChestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChestAdapter adapter;
    private List<MedicineChest> chestList = new ArrayList<>();
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_chests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChestAdapter(chestList);
        recyclerView.setAdapter(adapter);

        // Load chests
        loadChestsFromRealtimeDB();

        // Button click
        findViewById(R.id.add_chest_button).setOnClickListener(v -> {
            startActivity(new Intent(ChestActivity.this, ChestNameActivity.class));
        });

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.nav_menu);
        bottomNav.setSelectedItemId(R.id.nav_chest);
        bottomNav.setOnItemSelectedListener(this::handleNavigation);
    }

    private void loadChestsFromRealtimeDB() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase.child("users").child(userId).child("medicine_chests")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chestList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MedicineChest chest = dataSnapshot.getValue(MedicineChest.class);
                            chestList.add(chest);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChestActivity.this, "Failed to load chests", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean handleNavigation(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            startActivity(new Intent(this, Home.class));
        } else if (itemId == R.id.nav_add) {
            startActivity(new Intent(this, AddActivity.class));
        } else if (itemId == R.id.nav_profile) {
            startActivity(new Intent(this, Profile.class));
        }
        overridePendingTransition(0, 0);
        return true;
    }
}