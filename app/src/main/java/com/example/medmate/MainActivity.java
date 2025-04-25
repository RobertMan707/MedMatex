package com.example.medmate;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        EdgeToEdge.enable(this);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } else {
            Log.e("MainActivity", "Main view is null. Check your activity_main.xml for a view with id 'main'.");
        }

        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);

        View image = findViewById(R.id.mymed);
        View logo = findViewById(R.id.MedMate);
        View slogan = findViewById(R.id.slogan_name);

        // Apply animations
        if (image != null) image.setAnimation(topAnim);
        if (logo != null) logo.setAnimation(bottomAnim);
        if (slogan != null) slogan.setAnimation(bottomAnim);

        long splashScreenDuration = 2500;

        new Handler().postDelayed(() -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {

                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
            } else {

                Intent intent = new Intent(MainActivity.this, Login.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<>(image, "logo_image");
                pairs[1] = new Pair<>(logo, "logo_text");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
            finish();
        }, splashScreenDuration);

    }
}
