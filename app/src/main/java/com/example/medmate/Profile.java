package com.example.medmate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton; // Changed from ImageView
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Profile extends AppCompatActivity {

    private TextInputLayout userName, email;
    private TextView fullNameLabel;
    private BottomNavigationView bottomNav;
    private ImageButton languageFlagButton;

    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    private String[] languages = {"en", "hy", "ru"};
    private int currentLanguageIndex = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        String currentLang = LocaleHelper.getLanguage(newBase);
        super.attachBaseContext(LocaleHelper.setLocale(newBase, currentLang));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String savedLang = LocaleHelper.getLanguage(this);
        for (int i = 0; i < languages.length; i++) {
            if (languages[i].equals(savedLang)) {
                currentLanguageIndex = i;
                break;
            }
        }

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        userName = findViewById(R.id.username_profile);
        email = findViewById(R.id.profile_email);
        fullNameLabel = findViewById(R.id.full_name_field);
        bottomNav = findViewById(R.id.nav_menu);

        languageFlagButton = findViewById(R.id.language_flag_button_profile); // New ID for profile
        updateFlagImage();

        languageFlagButton.setOnClickListener(v -> {
            cycleLanguage();
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        loadUserData();

        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(Profile.this, Home.class));
                return true;
            } else if (itemId == R.id.nav_add) {
                startActivity(new Intent(Profile.this, AddActivity.class));
                return true;
            } else if (itemId == R.id.nav_chest) {
                startActivity(new Intent(Profile.this, ChestActivity.class));
                return true;
            }else if (itemId == R.id.nav_chat) {
                startActivity(new Intent(Profile.this, ChatActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;}
            else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    /**
     * Cycles through the defined languages and updates the app's locale.
     */
    private void cycleLanguage() {
        // Move to the next language in the sequence
        currentLanguageIndex = (currentLanguageIndex + 1) % languages.length;
        String nextLangCode = languages[currentLanguageIndex];

        // Apply the new locale
        LocaleHelper.setLocale(this, nextLangCode);

        // Recreate the activity to apply language changes to all views
        recreate();
    }

    /**
     * Updates the ImageButton's drawable to show the flag of the *next* language.
     */
    private void updateFlagImage() {
        // Determine the language whose flag should be displayed (the one user will switch TO)
        String displayFlagForLang = languages[(currentLanguageIndex + 1) % languages.length];
        int flagResId;

        switch (displayFlagForLang) {
            case "en":
                flagResId = R.drawable.ic_flag_usa; // Your USA flag drawable resource
                break;
            case "hy":
                flagResId = R.drawable.ic_flag_armenia; // Your Armenian flag drawable resource
                break;
            case "ru":
                flagResId = R.drawable.ic_flag_russia; // Your Russian flag drawable resource
                break;
            default:
                flagResId = R.drawable.ic_flag_usa; // Fallback to USA flag
                break;
        }
        languageFlagButton.setImageResource(flagResId);
    }

    private void loadUserData() {
        progressDialog.show();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss();
                    UserHelperClass user = snapshot.getValue(UserHelperClass.class);

                    if (user != null) {
                        fullNameLabel.setText(user.getName());
                        if (userName.getEditText() != null) {
                            userName.getEditText().setText(user.getUsername());
                        }
                        if (email.getEditText() != null) {
                            email.getEditText().setText(user.getEmail());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss();
                    Toast.makeText(Profile.this,
                            "Failed to load user data: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
        updateFlagImage();
    }
}