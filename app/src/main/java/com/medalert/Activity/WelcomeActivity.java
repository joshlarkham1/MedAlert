package com.medalert.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.medalert.R;
import com.medalert.Utilities.Helper;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

    }

    public void signUp(View view) {
        // Used helper class and call goToActivity method and passing the current activity reference and target activity.
        // Also passed true so the current activity will be finish
        Helper.goToActivity(WelcomeActivity.this, SignupActivity.class, true);
    }
    public void login(View view) {
        Helper.goToActivity(WelcomeActivity.this, LoginActivity.class, true);
    }
}