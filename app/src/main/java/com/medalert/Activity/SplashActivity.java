package com.medalert.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medalert.R;
import com.medalert.Utilities.Helper;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(() -> {

            // Get the currently signed-in user from Firebase Authentication
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // User is signed in
                Helper.goToActivity(SplashActivity.this, HomeActivity.class, true);

            } else {
                // No user is signed in
                Helper.goToActivity(SplashActivity.this, WelcomeActivity.class, true);
            }

        }, 5000); // It will wait 5 seconds

    }
}