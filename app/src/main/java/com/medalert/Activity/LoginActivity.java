package com.medalert.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medalert.R;
import com.medalert.Utilities.Helper;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find views for email, password, and login button
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        AppCompatButton btnLogin = findViewById(R.id.btnLogin);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(v -> {
            // Show a progress dialog
            ProgressDialog progressDialog = Helper.showProgressDialog(LoginActivity.this, "Please wait...");

            // Get email and password
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // Validate email and password
            if (email.isEmpty() || !Helper.isValidEmail(email)) {
                Helper.dismissProgressDialog(progressDialog);
                Helper.showMessage(LoginActivity.this, "Please enter a valid email address");
            } else if (password.isEmpty()) {
                Helper.dismissProgressDialog(progressDialog);
                Helper.showMessage(LoginActivity.this, "Please enter your password");
            } else {
                // Attempt to log in
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            Helper.dismissProgressDialog(progressDialog);
                            if (task.isSuccessful()) {
                                // Login successful, navigate to NewHomeActivity
                                Intent intent = new Intent(LoginActivity.this, NewHomeActivity.class);
                                startActivity(intent);
                                finish(); // Close LoginActivity
                            } else {
                                // Login failed
                                Helper.showMessage(LoginActivity.this, "Authentication failed: " + task.getException().getMessage());
                            }
                        });
            }
        });
    }

    // Forgot Password Functionality
    public void forgetPassword(View view) {
        // Create an AlertDialog for password reset
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_forgot_password, null);

        EditText etForgotEmail = dialogView.findViewById(R.id.etForgotEmail);
        AppCompatButton btnForgotPassword = dialogView.findViewById(R.id.btnForgotPassword);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnForgotPassword.setOnClickListener(v -> {
            // Show a progress dialog
            ProgressDialog progressDialog = Helper.showProgressDialog(LoginActivity.this, "Please wait...");
            String email = etForgotEmail.getText().toString().trim();

            // Validate email
            if (email.isEmpty() || !Helper.isValidEmail(email)) {
                Helper.dismissProgressDialog(progressDialog);
                Helper.showMessage(LoginActivity.this, "Please enter a valid email address");
            } else {
                // Send password reset email
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(resetTask -> {
                            Helper.dismissProgressDialog(progressDialog);
                            if (resetTask.isSuccessful()) {
                                // Reset email sent successfully
                                Helper.showMessage(LoginActivity.this, "A password reset email has been sent.");
                                dialog.dismiss();
                            } else {
                                // Failed to send reset email
                                Helper.showMessage(LoginActivity.this, "Failed to send reset email. Try again later.");
                                dialog.dismiss();
                            }
                        });
            }
        });
    }
}
