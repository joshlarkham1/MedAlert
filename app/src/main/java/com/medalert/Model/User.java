package com.medalert.Model;

public class User {

    private String userID;
    private String fullName;
    private String dateOfBirth;
    private String medicalCondition;
    private String gender;
    private String telephone;
    private String email;

    // Empty constructor required for Firebase
    public User() {
    }

    // Constructor with parameters
    public User(String userID, String fullName, String dateOfBirth, String medicalCondition, String gender, String telephone, String email) {
        this.userID = userID;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.medicalCondition = medicalCondition;
        this.gender = gender;
        this.telephone = telephone;
        this.email = email;
    }

    // Getters and setters
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMedicalCondition() {
        return medicalCondition;
    }

    public void setMedicalCondition(String medicalCondition) {
        this.medicalCondition = medicalCondition;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
