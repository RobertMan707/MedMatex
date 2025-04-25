package com.example.medmate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    // UI Elements
    private TextInputLayout userName, email;
    private TextView fullNameLabel;
    private BottomNavigationView bottomNav;

    // Firebase
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        // Initialize Views
        userName = findViewById(R.id.username_profile);
        email = findViewById(R.id.profile_email);
        fullNameLabel = findViewById(R.id.full_name_field);
        bottomNav = findViewById(R.id.nav_menu);

        // Setup Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading profile...");
        progressDialog.setCancelable(false);

        // Load user data
        loadUserData();

        // Bottom Navigation Setup
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(Profile.this, Home.class));
                return true;
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(Profile.this, AddActivity.class));
                return true;
            } else if (itemId == R.id.nav_chest) {
                startActivity(new Intent(Profile.this, ChestActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Already on profile
                return true;
            }
            return false;
        });
    }

    private void loadUserData() {
        progressDialog.show();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss();
                    UserHelperClass user = snapshot.getValue(UserHelperClass.class);

                    if (user != null) {
                        // Update all UI fields
                        fullNameLabel.setText(user.getName());
                        if (userName.getEditText() != null) {
                            userName.getEditText().setText(user.getUsername());
                        }
                        if (email.getEditText() != null) {
                            email.getEditText().setText(user.getEmail());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(Profile.this,
                            "Failed to load profile: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Not logged in!", Toast.LENGTH_SHORT).show();
            finish(); // Close profile if not logged in
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadUserData();
    }
}