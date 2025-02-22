package com.example.medmate;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Medicine_count extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medicine_count);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String medicineName = getIntent().getStringExtra("MEDICINE_NAME");
        String medicineType = getIntent().getStringExtra("SELECTED_MEDICINE_TYPE");
        int frequency = getIntent().getIntExtra("SELECTED_FREQUENCY", 1);

        Log.d("Medicine_count", "Medicine Name: " + medicineName);
        Log.d("Medicine_count", "Medicine Type: " + medicineType);
        Log.d("Medicine_count", "Frequency: " + frequency);


    }
}
