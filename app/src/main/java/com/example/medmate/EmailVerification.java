package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EmailVerification extends AppCompatActivity {
    private EditText codeInput;
    private Button verifyButton;
    private String receivedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        codeInput = findViewById(R.id.codeInput);
        verifyButton = findViewById(R.id.verifyButton);

        receivedCode = getIntent().getStringExtra("code");

        verifyButton.setOnClickListener(v -> verifyCode());
    }

    private void verifyCode() {
        String enteredCode = codeInput.getText().toString().trim();

        if (enteredCode.equals(receivedCode)) {
            Intent intent = new Intent(EmailVerification.this, Profile.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid verification code", Toast.LENGTH_SHORT).show();
        }
    }
}
