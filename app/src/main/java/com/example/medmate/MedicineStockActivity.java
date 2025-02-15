package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Toast.makeText(MedicineStockActivity.this, "User not logged in!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = user.getUid();
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                String medicineId = database.child("medicines").child(userId).push().getKey();

                if (medicineId != null) {
                    database.child("medicines").child(userId).child(medicineId).child("stock").setValue(stockAmount);
                }

                Intent intent = new Intent(MedicineStockActivity.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
