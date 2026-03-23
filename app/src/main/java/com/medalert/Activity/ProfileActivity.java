package com.medalert.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medalert.Model.User;
import com.medalert.R;
import com.medalert.Utilities.Helper;

import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private EditText edtProfileName, edtProfileTel, edtProfileEmail, edtProfileDateOfBirth;
    private Spinner spinnerProfileGender, spinnerProfileMedical;
    private Button btnSaveProfile;
    private ImageButton btnBack;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Ensure userID is initialized properly
        if (firebaseAuth.getCurrentUser() != null) {
            userID = firebaseAuth.getCurrentUser().getUid();
        } else {
            Helper.showMessage(this, "User not authenticated. Please log in.");
            finish(); // End the activity if no user is logged in
            return;
        }

        // Initialize UI components
        edtProfileName = findViewById(R.id.edtProfileName);
        edtProfileTel = findViewById(R.id.edtProfileTel);
        edtProfileEmail = findViewById(R.id.edtProfileEmail);
        edtProfileDateOfBirth = findViewById(R.id.edtProfileDateOfBirth);
        spinnerProfileGender = findViewById(R.id.spinnerProfileGender);
        spinnerProfileMedical = findViewById(R.id.spinnerProfileMedical);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnBack = findViewById(R.id.btn_back);

        // Set up spinner lists
        setupSpinner(spinnerProfileGender, Arrays.asList("Select Gender", "Male", "Female", "Other"));
        setupSpinner(spinnerProfileMedical, Arrays.asList("Select Condition", "Disable", "Diabetes", "Hypertension", "Asthma", "None"));

        // Load user data
        loadUserData();

        // Save profile button action
        btnSaveProfile.setOnClickListener(v -> updateUserData());

        // Back button click: Go back to home
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, NewHomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupSpinner(Spinner spinner, List<String> options) {
        Helper.setupSpinner(this, spinner, options);
    }

    private void loadUserData() {
        databaseReference.child(userID).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            edtProfileName.setText(user.getFullName());
                            edtProfileTel.setText(user.getTelephone());
                            edtProfileEmail.setText(user.getEmail());
                            edtProfileDateOfBirth.setText(user.getDateOfBirth());

                            Helper.setSpinnerSelection(spinnerProfileGender, user.getGender());
                            Helper.setSpinnerSelection(spinnerProfileMedical, user.getMedicalCondition());
                        }
                    }
                })
                .addOnFailureListener(e -> Helper.showMessage(this, "Failed to load data: " + e.getMessage()));
    }

    private void updateUserData() {
        if (userID == null) {
            Helper.showMessage(this, "User not authenticated. Cannot save changes.");
            return;
        }

        String name = edtProfileName.getText().toString().trim();
        String tel = edtProfileTel.getText().toString().trim();
        String dateOfBirth = edtProfileDateOfBirth.getText().toString().trim();
        String gender = spinnerProfileGender.getSelectedItem().toString().toLowerCase();
        String medicalCondition = spinnerProfileMedical.getSelectedItem().toString().toLowerCase();

        if (!validateInputs(name, tel, dateOfBirth, gender, medicalCondition)) {
            Helper.showMessage(this, "Please fill in all fields correctly.");
            return;
        }

        // Update Firebase Database
        User updatedUser = new User(userID, name, dateOfBirth, medicalCondition, gender, tel, edtProfileEmail.getText().toString());

        databaseReference.child(userID).setValue(updatedUser)
                .addOnSuccessListener(aVoid -> Helper.showMessage(this, "Profile updated successfully!"))
                .addOnFailureListener(e -> Helper.showMessage(this, "Failed to update profile: " + e.getMessage()));
    }


    private boolean validateInputs(String name, String tel, String dob, String gender, String condition) {
        return !name.isEmpty() && !tel.isEmpty() && !dob.isEmpty() &&
                !gender.equals("select gender") && !condition.equals("select condition");
    }
}
