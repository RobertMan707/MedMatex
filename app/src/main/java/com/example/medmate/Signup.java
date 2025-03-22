package com.example.medmate;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout regName, regUsername, regEmail, regPassword, regConfirmPassword;
    Button regBtn, regToLoginBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        regToLoginBtn = findViewById(R.id.login_signup_button);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_text);
        sloganText = findViewById(R.id.slogan_name);

        regName = findViewById(R.id.reg_name);
        regUsername = findViewById(R.id.reg_username);
        regEmail = findViewById(R.id.reg_email);
        regPassword = findViewById(R.id.reg_password);
        regConfirmPassword = findViewById(R.id.reg_confirm_password);
        regBtn = findViewById(R.id.reg_btn);

        regToLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Signup.this, Login.class);
            Pair[] pairs = new Pair[] {
                    new Pair<>(image, "logo_image"),
                    new Pair<>(logoText, "logo_text"),
                    new Pair<>(sloganText, "signInText"),
                    new Pair<>(regEmail, "email_tran"),
                    new Pair<>(regPassword, "password_tran"),
                    new Pair<>(regToLoginBtn, "login_signup_tran")
            };

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Signup.this, pairs);
            startActivity(intent, options.toBundle());
        });

        regBtn.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        if (!validateName() | !validateUsername() | !validateEmail() | !validatePassword() | !validateConfirmPassword()) {
            return;
        }

        String email = regEmail.getEditText().getText().toString().trim();
        String password = regPassword.getEditText().getText().toString().trim();

        // Create user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User registered successfully
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Save additional user data to Realtime Database
                            saveUserDataToDatabase(user.getUid(), email);
                        }
                    } else {
                        // Registration failed
                        Toast.makeText(Signup.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDataToDatabase(String userId, String email) {
        String name = regName.getEditText().getText().toString().trim();
        String username = regUsername.getEditText().getText().toString().trim();

        reference = FirebaseDatabase.getInstance().getReference("users");
        UserHelperClass user = new UserHelperClass(name, username, email, ""); // Don't store password in Realtime Database

        reference.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Signup.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Signup.this, Login.class));
                        finish();
                    } else {
                        Toast.makeText(Signup.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Boolean validateName() {
        String val = regName.getEditText() != null ? regName.getEditText().getText().toString().trim() : "";
        if (val.isEmpty()) {
            regName.setError("Field can't be empty");
            return false;
        } else {
            regName.setError(null);
            return true;
        }
    }

    private Boolean validateUsername() {
        String val = regUsername.getEditText() != null ? regUsername.getEditText().getText().toString().trim() : "";
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            regUsername.setError("Field can't be empty");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            regUsername.setError("Invalid username. No spaces allowed.");
            return false;
        } else {
            regUsername.setError(null);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = regEmail.getEditText() != null ? regEmail.getEditText().getText().toString().trim() : "";
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            regEmail.setError("Field can't be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            regEmail.setError("Invalid email address");
            return false;
        } else {
            regEmail.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = regPassword.getEditText() != null ? regPassword.getEditText().getText().toString().trim() : "";
        String passwordVal = "^(?=.*[a-zA-Z])(?=.*[@#$%^&+=.]).{4,}$";

        if (val.isEmpty()) {
            regPassword.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            regPassword.setError("Password is too weak");
            return false;
        } else {
            regPassword.setError(null);
            return true;
        }
    }

    private Boolean validateConfirmPassword() {
        String password = regPassword.getEditText() != null ? regPassword.getEditText().getText().toString().trim() : "";
        String confirmPassword = regConfirmPassword.getEditText() != null ? regConfirmPassword.getEditText().getText().toString().trim() : "";

        if (confirmPassword.isEmpty()) {
            regConfirmPassword.setError("Field cannot be empty");
            return false;
        } else if (!confirmPassword.equals(password)) {
            regConfirmPassword.setError("Passwords do not match");
            return false;
        } else {
            regConfirmPassword.setError(null);
            return true;
        }
    }
}