package com.ofywellness.register;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RegisterTabAdapter extends FragmentStateAdapter {
    public RegisterTabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new WeightTab();
            case 2:
                return new HeightTab();
            case 3:
                return new GenderTab();
            case 4:
                return new PhoneTab();
            default:
                return new AgeTab();
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
