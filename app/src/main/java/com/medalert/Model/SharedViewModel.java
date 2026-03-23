package com.medalert.Model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<String> fullName = new MutableLiveData<>();
    private final MutableLiveData<String> dateOfBirth = new MutableLiveData<>();
    private final MutableLiveData<String> medicalCondition = new MutableLiveData<>();
    private final MutableLiveData<String> gender = new MutableLiveData<>();

    // Full Name
    public void setFullName(String name) {
        fullName.setValue(name);
    }

    public LiveData<String> getFullName() {
        return fullName;
    }

    // Date of Birth
    public void setDateOfBirth(String dob) {
        dateOfBirth.setValue(dob);
    }

    public LiveData<String> getDateOfBirth() {
        return dateOfBirth;
    }

    // Medical Condition
    public void setMedicalCondition(String condition) {
        medicalCondition.setValue(condition);
    }

    public LiveData<String> getMedicalCondition() {
        return medicalCondition;
    }

    // Gender
    public void setGender(String genderValue) {
        gender.setValue(genderValue);
    }

    public LiveData<String> getGender() {
        return gender;
    }

}
