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
        // Get the data passed from the previous activity (for example, from login)
        Intent intent = getIntent();
        String user_name = intent.getStringExtra("name");
        String user_email = intent.getStringExtra("email");
        String user_username = intent.getStringExtra("username");

        // Display the user data in the profile view
        fullNameLabel.setText(user_name);
        userName.getEditText().setText(user_username);
        email.getEditText().setText(user_email);
    }
}
