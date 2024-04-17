package com.ofywellness.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdapterForTabs extends FragmentStateAdapter {
    public AdapterForTabs(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TrackDietTab();
            case 1:
                return new ServicesOfferedTab();
            case 2:
                return new AddMealTab();
            default:
                return new TrackDietTab();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
