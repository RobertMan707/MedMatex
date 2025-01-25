package com.example.medmate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    TextInputLayout mail, password;
    ImageView image;
    Button login_btn, callSignUp;
    TextView logoText, sloganText;
    CheckBox rememberMe;

    SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        callSignUp = findViewById(R.id.login_signup_button);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_text);
        sloganText = findViewById(R.id.slogan_name);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.signInButton);
        rememberMe = findViewById(R.id.remember_me);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        // Load saved credentials if Remember Me was enabled
        loadRememberedCredentials();

        // Set up Sign-Up button with animation (optional)
        callSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Signup.class);
            startActivity(intent);
        });

        // Set login button click listener
        login_btn.setOnClickListener(this::loginUser);
    }

    // Load saved credentials
    private void loadRememberedCredentials() {
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");
        boolean isRemembered = sharedPreferences.getBoolean("rememberMe", false);

        if (isRemembered) {
            mail.getEditText().setText(savedEmail);
            password.getEditText().setText(savedPassword);
            rememberMe.setChecked(true);
        }
    }

    // Validate email input
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

    // Validate password input
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

    // Handle user login
    public void loginUser(View view) {
        if (!validateEmail() || !validatePassword()) {
            return;
        } else {
            isUser();
        }
    }

    // Check if the user exists in the database
    private void isUser() {
        String userEmail = mail.getEditText().getText().toString().trim();
        String userPassword = password.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("email").equalTo(userEmail);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot datasnapshot) {
                if (datasnapshot.exists()) {
                    mail.setError(null);
                    mail.setErrorEnabled(false);

                    for (DataSnapshot userSnapshot : datasnapshot.getChildren()) {
                        String passwordDB = userSnapshot.child("password").getValue(String.class);

                        if (passwordDB != null && passwordDB.equals(userPassword)) {
                            String nameDB = userSnapshot.child("name").getValue(String.class);
                            String emailDB = userSnapshot.child("email").getValue(String.class);

                            // Save credentials if Remember Me is checked
                            if (rememberMe.isChecked()) {
                                saveCredentials(userEmail, userPassword);
                            } else {
                                clearSavedCredentials();
                            }

                            // Navigate to Profile activity with user data
                            Intent intent = new Intent(getApplicationContext(), Profile.class);
                            intent.putExtra("name", nameDB);
                            intent.putExtra("email", emailDB);
                            startActivity(intent);
                            return;
                        }
                    }
                    // If no matching password is found
                    password.setError("Password is wrong");
                    password.requestFocus();
                } else {
                    // If no such user exists
                    mail.setError("No such user exists");
                    mail.requestFocus();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

    // Save credentials in SharedPreferences
    private void saveCredentials(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putBoolean("rememberMe", true);
        editor.apply();
    }

    // Clear saved credentials from SharedPreferences
    private void clearSavedCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
