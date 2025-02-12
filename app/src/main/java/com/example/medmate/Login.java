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
    Button login_btn, callSignUp, guestLoginBtn;
    TextView logoText, sloganText;
    CheckBox rememberMe;

    SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callSignUp = findViewById(R.id.login_signup_button);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_text);
        sloganText = findViewById(R.id.slogan_name);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.signInButton);
        rememberMe = findViewById(R.id.remember_me);
        guestLoginBtn = findViewById(R.id.guestLoginButton);

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        checkAutoLogin();

        callSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Signup.class);
            startActivity(intent);
        });

        login_btn.setOnClickListener(this::loginUser);

        guestLoginBtn.setOnClickListener(v -> {
            navigateToProfile("Guest");
        });
    }

    private void checkAutoLogin() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            String savedEmail = sharedPreferences.getString("email", "");
            navigateToProfile(savedEmail);
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

    public void loginUser(View view) {
        if (!validateEmail() || !validatePassword()) {
            return;
        } else {
            isUser();
        }
    }

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

                            if (rememberMe.isChecked()) {
                                saveLoginState(userEmail);
                            }

                            navigateToProfile(emailDB);
                            return;
                        }
                    }
                    password.setError("Password is wrong");
                    password.requestFocus();
                } else {
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

    private void saveLoginState(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    private void navigateToProfile(String email) {
        Intent intent = new Intent(getApplicationContext(), Profile.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }
}
