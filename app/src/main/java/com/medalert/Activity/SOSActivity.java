package com.medalert.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.medalert.R;

public class SOSActivity extends AppCompatActivity {

    private static final int REQUEST_CALL_PERMISSION = 1;
    private TextView tvTimer;
    private Button btnCancel;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        // Initialize the UI elements
        tvTimer = findViewById(R.id.tv_timer);
        btnCancel = findViewById(R.id.btn_cancel);

        // Start the 10-second countdown
        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Calling 999 in " + millisUntilFinished / 1000 + " seconds...");
            }

            @Override
            public void onFinish() {
                // Check for CALL_PHONE permission and make the call
                makeEmergencyCall();
            }
        }.start();

        // Set up Cancel button listener
        btnCancel.setOnClickListener(v -> {
            // Cancel the countdown and close the activity
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            Toast.makeText(SOSActivity.this, "Emergency Call Cancelled", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void makeEmergencyCall() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, make the call
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:999"));
            startActivity(callIntent);
        } else {
            // Request CALL_PHONE permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        }
    }

    // Handle the permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, make the call
                makeEmergencyCall();
            } else {
                // Permission denied
                Toast.makeText(this, "Call Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
