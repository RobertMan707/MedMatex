package com.example.medmate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.os.Bundle;
import java.util.Locale;

public class Login extends AppCompatActivity {

    TextInputLayout mail, password;
    ImageButton languageFlagButton;
    Button login_btn, callSignUp, guestLoginBtn;
    TextView logoText, sloganText;
    CheckBox rememberMe;

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private String[] languages = {"en", "hy", "ru"};
    private int currentLanguageIndex = 0;


    @Override
    protected void attachBaseContext(Context newBase) {
        String currentLang = LocaleHelper.getLanguage(newBase);

        super.attachBaseContext(LocaleHelper.setLocale(newBase, currentLang));
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String savedLang = LocaleHelper.getLanguage(this);
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].equals(savedLang)) {
                currentLanguageIndex = i;
                break;
            }
        }

        mAuth = FirebaseAuth.getInstance();

        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);

        if (password == null) {
            Log.e("LoginDebug", "Password field is not initialized!");
        } else {
            Log.d("LoginDebug", "Password field initialized successfully.");
        }

        callSignUp = findViewById(R.id.login_signup_button);
        logoText = findViewById(R.id.logo_text);
        sloganText = findViewById(R.id.slogan_name);
        login_btn = findViewById(R.id.signInButton);
        rememberMe = findViewById(R.id.remember_me);
        guestLoginBtn = findViewById(R.id.TestLoginButton);

        languageFlagButton = findViewById(R.id.language_flag_button);
        updateFlagImage();

        languageFlagButton.setOnClickListener(v -> {
            cycleLanguage();
        });

        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        checkAutoLogin();

        callSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Signup.class);
            startActivity(intent);
        });

        login_btn.setOnClickListener(v -> loginUser());

        guestLoginBtn.setOnClickListener(v -> {
            String testEmail = "individualproject2025@gmail.com";
            String testPassword = "Samsung2025";

            if (mail.getEditText() != null) {
                mail.getEditText().setText(testEmail);
            }
            if (password.getEditText() != null) {
                password.getEditText().setText(testPassword);
            }

            loginUser();
        });
    }


    private void cycleLanguage() {
        currentLanguageIndex = (currentLanguageIndex + 1) % languages.length;
        String nextLangCode = languages[currentLanguageIndex];

        LocaleHelper.setLocale(this, nextLangCode);

        recreate();
    }

    private void updateFlagImage() {
        String displayFlagForLang = languages[(currentLanguageIndex + 1) % languages.length];
        int flagResId;

        switch (displayFlagForLang) {
            case "en":
                flagResId = R.drawable.ic_flag_usa;
                break;
            case "hy":
                flagResId = R.drawable.ic_flag_armenia;
                break;
            case "ru":
                flagResId = R.drawable.ic_flag_russia;
                break;
            default:
                flagResId = R.drawable.ic_flag_usa;
                break;
        }
        languageFlagButton.setImageResource(flagResId);
    }

    private void checkAutoLogin() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
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
            password.setError("Field can't be empty");
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

        String email = mail.getEditText() != null ? mail.getEditText().getText().toString().trim() : "";
        String passwordText = password.getEditText() != null ? password.getEditText().getText().toString().trim() : "";

        if (email.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, passwordText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (rememberMe.isChecked()) {
                                saveLoginState(user.getEmail());
                            }
                            navigateToProfile();
                        }
                    } else {
                        Toast.makeText(Login.this, "Failed to change"  + task.getException().getMessage(), Toast.LENGTH_SHORT).show(); // Using string resource
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