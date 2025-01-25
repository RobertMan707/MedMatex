package com.example.medmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerification extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText codeInput;
    private Button verifyButton;
    private TextView resendCodeText;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // UI Components
        codeInput = findViewById(R.id.codeInput);
        verifyButton = findViewById(R.id.verifyButton);
        resendCodeText = findViewById(R.id.resendCodeText);

        // Generate a random 6-digit code and send it via email
        generateVerificationCode();

        // Verify the entered code when the "Verify" button is clicked
        verifyButton.setOnClickListener(v -> verifyCode());

        // Resend the code if the user clicks on the "Resend code" text
        resendCodeText.setOnClickListener(v -> resendVerificationCode());
    }

    private void generateVerificationCode() {
        // Generate a 6-digit code (can be stored in Firebase, here using a simple method)
        verificationCode = String.format("%06d", (int) (Math.random() * 1000000));
        sendVerificationCodeEmail();
    }

    private void sendVerificationCodeEmail() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Send the verification code via email (this example uses a Firebase function to send an email)
            String email = user.getEmail();

            // You can use Firebase Functions or your custom backend to send the email with the verification code.
            // Example: FirebaseFunctions.getInstance().sendVerificationCode(email, verificationCode);

            Log.d("EmailVerification", "Sending code to: " + email);
            // For now, simulate the code being sent by just logging it.
            Toast.makeText(this, "Code sent to your email!", Toast.LENGTH_SHORT).show();
        }
    }

    private void verifyCode() {
        String enteredCode = codeInput.getText().toString().trim();

        if (enteredCode.equals(verificationCode)) {
            // If the codes match, redirect to the next activity (e.g., Profile)
            Intent intent = new Intent(EmailVerification.this, Profile.class);
            startActivity(intent);
            finish();
        } else {
            // If the codes don't match, show an error
            Toast.makeText(this, "Invalid verification code", Toast.LENGTH_SHORT).show();
        }
    }

    private void resendVerificationCode() {
        generateVerificationCode();
        sendVerificationCodeEmail();
    }
}
