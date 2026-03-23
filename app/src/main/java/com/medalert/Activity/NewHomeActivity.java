package com.medalert.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.medalert.Model.User;
import com.medalert.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewHomeActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private TextView tvUserName, tvAddressDetail;
    private Button btnSOS, navHome, navBook, navPhone, navMap;
    private ImageButton btnUser;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_home);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize UI components
        tvUserName = findViewById(R.id.tv_user_name);
        tvAddressDetail = findViewById(R.id.tv_address_detail);
        btnSOS = findViewById(R.id.btn_sos);
        navHome = findViewById(R.id.nav_home);
        navBook = findViewById(R.id.nav_book);
        navPhone = findViewById(R.id.nav_phone);
        navMap = findViewById(R.id.nav_map);
        btnUser = findViewById(R.id.btn_user);

        // Fetch and display user details
        fetchAndDisplayUserDetails();

        // Check location and display current address
        checkLocationPermission();

        // Set up button listeners
        setUpButtonListeners();
    }

    private void fetchAndDisplayUserDetails() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String userID = currentUser.getUid();

            // Fetch user details from Firebase Realtime Database
            databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            // Display the user's full name
                            tvUserName.setText(user.getFullName() + "!");
                        } else {
                            // Handle null user object
                            tvUserName.setText("Welcome back, User!");
                        }
                    } else {
                        // Handle case where user data doesn't exist
                        tvUserName.setText("Welcome back, Guest!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                    Toast.makeText(NewHomeActivity.this, "Failed to load user info.", Toast.LENGTH_SHORT).show();
                    tvUserName.setText("Welcome back, Guest!");
                }
            });
        } else {
            // Handle case where no user is signed in
            tvUserName.setText("Welcome back, Guest!");
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchCurrentLocation();
        }
    }

    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        getAddressFromLocation(location);
                    } else {
                        tvAddressDetail.setText("Unable to determine current location");
                    }
                })
                .addOnFailureListener(e -> tvAddressDetail.setText("Failed to retrieve location"));
    }

    private void getAddressFromLocation(Location location) {
        if (location == null) {
            tvAddressDetail.setText("Location is null, unable to fetch address.");
            return;
        }

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                // Extract the main address line and the postcode
                String addressLine = address.getAddressLine(0);
                String postalCode = address.getPostalCode();

                if (postalCode != null && !postalCode.isEmpty()) {
                    tvAddressDetail.setText("Current address:\n" + addressLine + "\n" + postalCode);
                } else {
                    tvAddressDetail.setText("Current address:\n" + addressLine);
                }
            } else {
                tvAddressDetail.setText("No address found for the given location.");
            }
        } catch (IOException e) {
            tvAddressDetail.setText("Error fetching address: " + e.getMessage());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation();
            } else {
                tvAddressDetail.setText("Location permission denied");
            }
        }
    }

    private void setUpButtonListeners() {
        // SOS Button
        btnSOS.setOnClickListener(v -> {
            // Launch SOSActivity
            Intent intent = new Intent(NewHomeActivity.this, SOSActivity.class);
            startActivity(intent);
        });

        // Navigation Buttons
        navHome.setOnClickListener(v -> {
            // Open MedicalNumbersActivity
            Intent intent = new Intent(NewHomeActivity.this, SymptomCheckerActivity.class);
            startActivity(intent);
        });

        navBook.setOnClickListener(v -> {
            // Open MedicalNumbersActivity
            Intent intent = new Intent(NewHomeActivity.this, GuidesActivity.class);
            startActivity(intent);
        });

        navPhone.setOnClickListener(v -> {
            // Open MedicalNumbersActivity
            Intent intent = new Intent(NewHomeActivity.this, MedicalNumbersActivity.class);
            startActivity(intent);
        });

        navMap.setOnClickListener(v -> {
            // Navigate to MapActivity
            Intent intent = new Intent(NewHomeActivity.this, MapActivity.class);
            startActivity(intent);
        });

        // User Button -> Navigate to Profile Page
        btnUser.setOnClickListener(v -> {
            Intent intent = new Intent(NewHomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
    }

