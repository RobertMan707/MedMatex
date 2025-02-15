package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        bottomNav = findViewById(R.id.nav_menu);
        bottomNav.setSelectedItemId(R.id.nav_add);

        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(AddActivity.this, Home.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_add) {
                    return true;  // Stay in AddActivity
                } else if (itemId == R.id.nav_chest) {
                    startActivity(new Intent(AddActivity.this, ChestActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(AddActivity.this, Profile.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });


        addButton = findViewById(R.id.add_button);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddActivity.this, TypeMedicine.class);
                startActivity(intent);
            }
        });
    }
}
