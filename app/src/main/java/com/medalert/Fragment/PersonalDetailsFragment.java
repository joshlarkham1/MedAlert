package com.medalert.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.medalert.Model.SharedViewModel;
import com.medalert.R;
import com.medalert.Activity.SignupActivity;
import com.medalert.Utilities.Helper;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class PersonalDetailsFragment extends Fragment {

    private EditText edtName, edtDateOfBirth;
    private Spinner spinnerGender, spinnerMedical;
    private String gender ="", medicalCondition ="", dateOfBirth="";
    private SharedViewModel sharedViewModel;

    public PersonalDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_personal_details, container, false);


        // Initialize the ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);


        // Finding a id for the UI components
        edtName = view.findViewById(R.id.edtName);
        edtDateOfBirth = view.findViewById(R.id.edtDateOfBirth);

        spinnerGender = view.findViewById(R.id.spinnerGender);
        spinnerMedical = view.findViewById(R.id.spinnerMedical);

        edtDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call date picker method
                showDatePickerDialog();
            }
        });

        // Set up data for gender and medical conditions spinner
        setupGenderSpinner();
        setupMedicalSpinner();


        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
                if (!gender.equals("Select Gender")) {
                    gender = gender.toLowerCase(); // convert to lowercase
                }else {
                    gender = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMedical.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                medicalCondition = parent.getItemAtPosition(position).toString();
                if (!medicalCondition.equals("Select Condition")) {
                    medicalCondition =  medicalCondition.toLowerCase(); // convert to lowercase
                }else {
                    medicalCondition = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AppCompatButton nextButton = view.findViewById(R.id.btnNext);
        nextButton.setOnClickListener(v -> {
            // Navigate to the next page in the ViewPager2

            String name = edtName.getText().toString().trim();

            // Checking if the name, dateOfBirth, gender and medicalCondition field is empty or not
            if (name.isEmpty()){
                Helper.showMessage(getActivity(), "Please enter your full name");
            } else if(dateOfBirth.isEmpty()){
                Helper.showMessage(getActivity(), "Please select your date of birth");
            }else if(gender.isEmpty()){
                Helper.showMessage(getActivity(), "Please select your gender");
            }else if (medicalCondition.isEmpty()){
                Helper.showMessage(getActivity(), "Please select your medical condition");
            }else{

                // Set data to the shared view model
                sharedViewModel.setFullName(name);
                sharedViewModel.setDateOfBirth(dateOfBirth);
                sharedViewModel.setMedicalCondition(medicalCondition);
                sharedViewModel.setGender(gender);

                // Go to the login details fragment
                ((SignupActivity) getActivity()).moveToNextPage();

            }

        });
        return view;
    }

    private void setupGenderSpinner() {
        // Initialize an array list of gender options
        List<String> genderOptions = Arrays.asList("Select Gender", "Male", "Female", "Other");
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, genderOptions);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
    }

    private void setupMedicalSpinner() {
        // Initialize an array list of medical condition options
        List<String> medicalOptions = Arrays.asList("Select Condition", "Disable", "Diabetes", "Hypertension", "Asthma", "None");
        ArrayAdapter<String> medicalAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, medicalOptions);
        medicalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedical.setAdapter(medicalAdapter);
    }



    private void showDatePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Set the selected date in the EditText
                    String dateOfBir = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    edtDateOfBirth.setText(dateOfBir);
                    // Set the date to the global variable
                    dateOfBirth = dateOfBir;
                }, year, month, day);

        datePickerDialog.show();
    }



}