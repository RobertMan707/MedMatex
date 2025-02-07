package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MedicineStockActivity extends AppCompatActivity {

    private EditText stockInput;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meicine_stock);

        stockInput = findViewById(R.id.medicine_stock_input);
        confirmButton = findViewById(R.id.btn_confirm_stock);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stockText = stockInput.getText().toString().trim();
                if (stockText.isEmpty()) {
                    Toast.makeText(MedicineStockActivity.this, "Please enter stock quantity", Toast.LENGTH_SHORT).show();
                    return;
                }
                int stockAmount = Integer.parseInt(stockText);

                String medicineName = getIntent().getStringExtra("medicineName");
                String medicineType = getIntent().getStringExtra("medicineType");
                String frequency = getIntent().getStringExtra("frequency");

                Intent intent = new Intent(MedicineStockActivity.this, Home.class);
                intent.putExtra("medicine_name", medicineName);
                intent.putExtra("medicine_type", medicineType);
                intent.putExtra("frequency", frequency);
                intent.putExtra("medicine_stock", stockAmount);
                startActivity(intent);
                finish();
            }
        });
    }
}
