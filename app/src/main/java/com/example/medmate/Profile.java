package com.example.medmate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

public class Profile extends AppCompatActivity {

    TextInputLayout userName, email;
    TextView fullNameLabel;
    BottomNavigationView bottomNav;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize the views
        userName = findViewById(R.id.username_profile);
        email = findViewById(R.id.profile_email);
        fullNameLabel = findViewById(R.id.full_name_field);
        bottomNav = findViewById(R.id.nav_menu);

        // Select the Profile menu item by default
        bottomNav.setSelectedItemId(R.id.nav_profile);

        // Handle bottom navigation item clicks
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(Profile.this, Home.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_add) {
                    startActivity(new Intent(Profile.this, AddActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_chest) {
                    startActivity(new Intent(Profile.this, ChestActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    // Stay in the Profile activity
                    return true;
                }
                return false;
            }
        });

        // Show the user data passed from the login or previous activity
        showAllUserData();
    }

    private void showAllUserData() {
        Intent intent = getIntent();

        // Get data from intent
        String user_name = intent.getStringExtra("name");
        String user_username = intent.getStringExtra("username");
        String user_email = intent.getStringExtra("email");

        // Set default values if null
        if (user_name == null) user_name = "No name set";
        if (user_username == null) user_username = "No username set";
        if (user_email == null) user_email = "No email set";

        // Display data
        fullNameLabel.setText(user_name);
        if (userName.getEditText() != null) {
            userName.getEditText().setText(user_username);
        }
        if (email.getEditText() != null) {
            email.getEditText().setText(user_email);
        }
    }
}
