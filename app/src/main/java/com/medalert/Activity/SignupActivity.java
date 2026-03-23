package com.medalert.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.medalert.Adapter.SignupPagerAdapter;
import com.medalert.Model.SharedViewModel;
import com.medalert.R;


public class SignupActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        viewPager = findViewById(R.id.viewPager);
        SignupPagerAdapter adapter = new SignupPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false);


        // Initialize the SharedViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);


    }



    // Method to move to the next viewpage
    public void moveToNextPage() {
        if (viewPager.getCurrentItem() < 1) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // If the login details fragment is open and if user press back, the app will be return back to the previous fragment
            if (viewPager.getCurrentItem() > 0) {
                viewPager.setCurrentItem(viewPager.getCurrentItem()- 1);
                return true;

            }
        }
        return super.onKeyDown(keyCode, event);
    }



}