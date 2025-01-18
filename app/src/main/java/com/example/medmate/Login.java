package com.example.medmate;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        // Prevent unwanted focus changes
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        // Initialize UI components
        callSignUp = findViewById(R.id.login_signup_button);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_text);
        sloganText = findViewById(R.id.slogan_name);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.signInButton);

        // Set up Sign-Up button with animation
        callSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Signup.class);

            Pair[] pairs = new Pair[7];
            pairs[0] = new Pair<>(image, "logo_image");
            pairs[1] = new Pair<>(logoText, "logo_text");
            pairs[2] = new Pair<>(sloganText, "signInText");
            pairs[3] = new Pair<>(mail, "email_tran");
            pairs[4] = new Pair<>(password, "password_tran");
            pairs[5] = new Pair<>(login_btn, "button_tran");
            pairs[6] = new Pair<>(callSignUp, "login_signup_tran");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
            startActivity(intent, options.toBundle());
        });

        // Set login button click listener
        login_btn.setOnClickListener(this::loginUser);
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
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.exists()) {
                    mail.setError(null);
                    mail.setErrorEnabled(false);

                    // Loop through all matched users
                    for (DataSnapshot userSnapshot : datasnapshot.getChildren()) {
                        String passwordDB = userSnapshot.child("password").getValue(String.class);
                        Log.d("Debug", "Password from DB: " + passwordDB); // Debugging

                        if (passwordDB != null && passwordDB.equals(userPassword)) {
                            String nameDB = userSnapshot.child("name").getValue(String.class);
                            String emailDB = userSnapshot.child("email").getValue(String.class);

                            Intent intent = new Intent(getApplicationContext(), Profile.class);
                            intent.putExtra("name", nameDB);
                            intent.putExtra("email", emailDB);
                            startActivity(intent);
                            return;
                        }
                    }
                    // If no matching password is found
                    password.setError("Password is Wrong");
                    password.requestFocus();
                } else {
                    mail.setError("No such User exists");
                    mail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }

}
