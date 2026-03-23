package com.medalert.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.medalert.Fragment.LoginDetailsFragment;
import com.medalert.Fragment.PersonalDetailsFragment;

public class SignupPagerAdapter extends FragmentStateAdapter {

    public SignupPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Checking the position of the fragment
        switch (position) {
            case 0:
                return new PersonalDetailsFragment(); // First fragment
            case 1:
                return new LoginDetailsFragment(); // Second fragment
            default:
                return new PersonalDetailsFragment(); // Default is first fragment
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Total number of fragments
    }
}
