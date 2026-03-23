package com.medalert.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medalert.R;
import com.medalert.Utilities.Helper;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView textView = findViewById(R.id.name);
        Button btnSignOut = findViewById(R.id.btnSignOut);
        Button btnStaySignedIn = findViewById(R.id.btnStaySignedIn);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Check if the user is logged in
        if (currentUser != null) {
            String email = currentUser.getEmail();
            textView.setText("User email: " + (email != null ? email : "No email found"));
        } else {
            Helper.goToActivity(HomeActivity.this, WelcomeActivity.class, true);
        }

        // Sign out button functionality
        btnSignOut.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Helper.goToActivity(HomeActivity.this, WelcomeActivity.class, true);
        });

        // Stay signed in button functionality
        btnStaySignedIn.setOnClickListener(v -> {
            // Launch NewHomeActivity
            Intent intent = new Intent(HomeActivity.this, NewHomeActivity.class);
            startActivity(intent);
        });
    }
}
