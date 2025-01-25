package com.example.medmate;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout regName, regUsername, regEmail, regPassword, regConfirmpassword;
    Button regBtn, regToLoginBtn;

    DatabaseReference reference;
    FirebaseDatabase rootnode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up); // Only call once

        // Remove the EdgeToEdge logic unless necessary and defined
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        );

        // Initialize views
        regToLoginBtn = findViewById(R.id.login_signup_button);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_text);
        sloganText = findViewById(R.id.slogan_name);

        regName = findViewById(R.id.reg_name);
        regUsername = findViewById(R.id.reg_username);
        regEmail = findViewById(R.id.reg_email);
        regPassword = findViewById(R.id.reg_password);
        regConfirmpassword = findViewById(R.id.reg_confirm_passowrd);
        regBtn = findViewById(R.id.reg_btn);

        // Navigate to Login screen
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

        // Handle Registration
        regBtn.setOnClickListener(v -> {
            registerUser();
        });
    }

    private void registerUser() {
        // Validate fields
        if (!validateName() | !validateUsername() | !validateEmail() | !validatePassword() | !validateConfirmPassword()) {
            return; // Stop if any validation fails
        }

        rootnode = FirebaseDatabase.getInstance();
        reference = rootnode.getReference("users");

        // Retrieve user inputs
        String name = regName.getEditText().getText().toString();
        String username = regUsername.getEditText().getText().toString();
        String email = regEmail.getEditText().getText().toString();
        String password = regPassword.getEditText().getText().toString();

        // Create helper object
        UserHelperClass helperClass = new UserHelperClass(name, username, email, password);

        // Store in the database under the username node
        reference.child(username).setValue(helperClass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // After successful registration, navigate to Email Verification Activity
                        Intent intent = new Intent(Signup.this, Login.class);
                        intent.putExtra("email", email); // Pass the email for verification
                        startActivity(intent);
                        finish(); // Close the current Signup activity so the user can't go back to it
                    }
                });
    }

    // Validation methods
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
        String confirmPassword = regConfirmpassword.getEditText() != null ? regConfirmpassword.getEditText().getText().toString().trim() : "";

        if (confirmPassword.isEmpty()) {
            regConfirmpassword.setError("Field cannot be empty");
            return false;
        } else if (!confirmPassword.equals(password)) {
            regConfirmpassword.setError("Passwords do not match");
            return false;
        } else {
            regConfirmpassword.setError(null);
            return true;
        }
    }
}
