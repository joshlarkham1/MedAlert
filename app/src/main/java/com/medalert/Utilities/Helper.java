package com.medalert.Utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.medalert.Activity.ProfileActivity;

import java.util.List;

public class Helper {

    // Validates an email address using a regular expression
    public static boolean isValidEmail(String email) {
        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(emailRegex);
    }

    // Displaying toast message
    public static void showMessage(Context context, String message){
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }

    // Starts a new Activity
    public static void goToActivity(Context context, Class<?> targetActivityClass) {
        Intent intent = new Intent(context, targetActivityClass);
        context.startActivity(intent);
    }

    // Starts a new Activity and close the current activity to prevent come back to the previous activity if press back
    public static void goToActivity(Context context, Class<?> targetActivityClass, boolean finishActivity) {
        Intent intent = new Intent(context, targetActivityClass);
        context.startActivity(intent);
        ((Activity) context).finish();
    }


    public static ProgressDialog showProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);

        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;

    }

    public static void dismissProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();

        }
    }

    public static void setupSpinner(ProfileActivity profileActivity, Spinner spinner, List<String> options) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                profileActivity, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public static void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(
                    value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase());
            if (position >= 0) {
                spinner.setSelection(position);
            }
        }
    }

}
