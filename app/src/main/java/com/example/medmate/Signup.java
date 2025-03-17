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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;
import java.util.Random;

public class Signup extends AppCompatActivity {

    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout regName, regUsername, regEmail, regPassword, regConfirmPassword;
    Button regBtn, regToLoginBtn;

    DatabaseReference reference;
    FirebaseDatabase rootNode;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

        String name = regName.getEditText().getText().toString();
        String username = regUsername.getEditText().getText().toString();
        String email = regEmail.getEditText().getText().toString();
        String password = regPassword.getEditText().getText().toString();

        String userId = reference.push().getKey();

        if (userId != null) {
            UserHelperClass user = new UserHelperClass(name, username, email, password);

            reference.child(userId).setValue(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String verificationCode = generateVerificationCode();
                            sendVerificationEmail(email, verificationCode);

                            Intent intent = new Intent(Signup.this, EmailVerification.class);
                            intent.putExtra("email", email);
                            intent.putExtra("verificationCode", verificationCode);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Signup.this, "Registration failed! Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Error generating user ID. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateVerificationCode() {
        Random rand = new Random();
        int verificationCode = rand.nextInt(999999);
        return String.format("%06d", verificationCode);
    }

    private void sendVerificationEmail(String email, String verificationCode) {
        String host = "smtp.gmail.com";
        String from = "med.and.mate@gmail.com";
        String password = "gyln cuxd vnun cpdg";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        try {
            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Email Verification");
            message.setText("Your verification code is: " + verificationCode);

            new Thread(() -> {
                try {
                    Transport.send(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
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
