package com.medalert.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medalert.Activity.LoginActivity;
import com.medalert.Activity.HomeActivity;
import com.medalert.Activity.WelcomeActivity;
import com.medalert.Model.SharedViewModel;
import com.medalert.Model.User;
import com.medalert.R;
import com.medalert.Utilities.Helper;

import java.util.Objects;

public class LoginDetailsFragment extends Fragment {


    private SharedViewModel sharedViewModel;

    private AppCompatButton btnConfirm;
    private EditText edtTel, edtEmail, edtPassword, edtConfirmPassword;

    private String tel, email, password, confirmPassword;
    private String fullName, dateOfBirth, gender, medicalCond , userID;


    public LoginDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login_details, container, false);

        // Create a SharedViewModel instance using the parent Activity's ViewModelStoreOwner
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Assigning a value to the local variable
        sharedViewModel.getFullName().observe(getViewLifecycleOwner(), fullName ->  this.fullName= fullName );
        sharedViewModel.getDateOfBirth().observe(getViewLifecycleOwner(), dateOfBirth ->  this.dateOfBirth = dateOfBirth );
        sharedViewModel.getGender().observe(getViewLifecycleOwner(), gender ->  this.gender= gender );
        sharedViewModel.getMedicalCondition().observe(getViewLifecycleOwner(), medicalCond ->  this.medicalCond = medicalCond );

        // Finding a id for the UI components
        edtTel = view.findViewById(R.id.edtTel);
        edtEmail = view.findViewById(R.id.edtEmail);

        edtPassword = view.findViewById(R.id.edtPassword);
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);

        btnConfirm = view.findViewById(R.id.btnConfirm);


        btnConfirm.setOnClickListener(v -> {
            // Create a new ProgressDialog instance
            ProgressDialog progressDialog = Helper.showProgressDialog(getContext(), "Please wait...");

            tel =  edtTel.getText().toString().trim();
            email =  edtEmail.getText().toString().trim();
            password =  edtPassword.getText().toString().trim();
            confirmPassword =  edtConfirmPassword.getText().toString().trim();

            // The telephone field is empty or the telephone number length is less than 10
            if (tel.isEmpty() || tel.length() < 10){
                Helper.dismissProgressDialog(progressDialog);
                Helper.showMessage(getContext(), "Please enter a valid telephone number");

                // Check if the email field is empty or the email format is invalid
            }else if (email.isEmpty() || !Helper.isValidEmail(email)){
                Helper.dismissProgressDialog(progressDialog);
                Helper.showMessage(getContext(), "Please enter valid email address");

            } else if (password.isEmpty() || confirmPassword.isEmpty() || password.length() < 6) {
                // Password or confirm password field is empty or the password is length is less than 6
                Helper.dismissProgressDialog(progressDialog);
                Helper.showMessage(getContext(), "Please enter password at least 6 character long");

            }else if (password.equals(confirmPassword)) {
                // password and confirm password is matches

                // Get an instance of FirebaseAuth
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

                // Create a new user with the email and password
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity(), task -> {
                            if (task.isSuccessful()) {
                                // Sing up success

                                // Get the newly created user after successful authentication
                                FirebaseUser newUser = firebaseAuth.getCurrentUser();

                                // Assign the user Uid to the userID
                                userID = newUser.getUid();

                                // Create a new User object with the specified details
                                User user = new User(userID, fullName, dateOfBirth, medicalCond, gender, tel, email);

                                // Get a reference to the Firebase Database
                                FirebaseDatabase database = FirebaseDatabase.getInstance();

                                // Get a reference to the specific user's node in the "Users" child of the Firebase Database
                                DatabaseReference myRef = database.getReference().child("Users").child(userID);

                                // Set the User object as the value for the specified database reference
                                // If successful, dismiss the progress dialog, show a success message, and navigate to HomeActivity
                                myRef.setValue(user)
                                        .addOnSuccessListener(aVoid -> {
                                            // Successfully saved the user data
                                            // Dismiss the progress dialog and navigate to HomeActivity
                                            Helper.dismissProgressDialog(progressDialog);
                                            Helper.showMessage(getContext(), "Registration complete");
                                            Helper.goToActivity(getContext(), HomeActivity.class, true);
                                        })
                                        .addOnFailureListener(e -> {
                                            // Failed to save, show error message
                                            Helper.dismissProgressDialog(progressDialog);
                                            Helper.showMessage(getContext(), "Something went wrong, please try again");
                                        });
                            }else {
                                // Failed to register new user
                                // Possible errors can be are the email address is already used or the internet connection is slow
                                Helper.showMessage(getContext(), "Authentication failed: " + task.getException().getMessage());
                                Helper.dismissProgressDialog(progressDialog);
                            }
                        });
            }else{
                // Display message
                Helper.dismissProgressDialog(progressDialog);
                Helper.showMessage(getContext(), "Password does not match with confirm password");
            }

        });

        // User already have an account
        // Finish the activity and go to login activity
        TextView txtHaveAccount = view.findViewById(R.id.txtHaveAccount);
        txtHaveAccount.setOnClickListener(v -> Helper.goToActivity(getContext(), LoginActivity.class, true));

        return view; // Return the fragment view
    }

}