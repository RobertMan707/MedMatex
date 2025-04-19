package com.example.medmate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    TextInputLayout mail, password;
    ImageView image;
    Button login_btn, callSignUp, guestLoginBtn;
    TextView logoText, sloganText;
    CheckBox rememberMe;

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);

        // Debug: Check if password is initialized
        if (password == null) {
            Log.e("LoginDebug", "Password field is not initialized!");
        } else {
            Log.d("LoginDebug", "Password field initialized successfully.");
        }

        callSignUp = findViewById(R.id.login_signup_button);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_text);
        sloganText = findViewById(R.id.slogan_name);
        login_btn = findViewById(R.id.signInButton);
        rememberMe = findViewById(R.id.remember_me);
        guestLoginBtn = findViewById(R.id.guestLoginButton);

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        checkAutoLogin();

        callSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Signup.class);
            startActivity(intent);
        });

        login_btn.setOnClickListener(v -> loginUser());

        guestLoginBtn.setOnClickListener(v -> {
            navigateToProfile();
        });
    }

    private void checkAutoLogin() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            String savedEmail = sharedPreferences.getString("email", "");
            navigateToProfile();
        }
    }

    private Boolean validateEmail() {
        String val = mail.getEditText() != null ? mail.getEditText().getText().toString().trim() : "";
        if (val.isEmpty()) {
            mail.setError("Field can't be empty");
            return false;
        } else {
            mail.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = password.getEditText() != null ? password.getEditText().getText().toString().trim() : "";
        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private void loginUser() {
        if (!validateEmail() || !validatePassword()) {
            return;
        }

        // Access the TextInputEditText inside the TextInputLayout
        String email = mail.getEditText() != null ? mail.getEditText().getText().toString().trim() : "";
        String passwordText = password.getEditText() != null ? password.getEditText().getText().toString().trim() : "";

        if (email.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(Login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign in with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, passwordText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (rememberMe.isChecked()) {
                                saveLoginState(user.getEmail());
                            }
                            navigateToProfile();
                        }
                    } else {
                        Toast.makeText(Login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveLoginState(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    private void navigateToProfile() {
        startActivity(new Intent(Login.this, Profile.class));
        finish();
    }
}