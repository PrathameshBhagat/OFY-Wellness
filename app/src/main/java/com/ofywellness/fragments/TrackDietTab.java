package com.ofywellness.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ofywellness.R;
import com.ofywellness.db.ofyDatabase;

/**
 * Fragment for DietTrack tab in Home page
 */
public class TrackDietTab extends Fragment {

    /* TODO : Remove the TextView assignment and context to database operation method */

    private TextView energyValueLabel, proteinsValueLabel, fatsValueLabel, carbohydratesValueLabel;
    private ProgressBar energyProgressBar, proteinsProgressBar, fatsProgressBar, carbohydratesProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the view for this fragment and inflate it
        View view = inflater.inflate(R.layout.fragment_track_diet_tab, container, false);

        // Assign the text views so that tracking data can be set
        energyValueLabel = view.findViewById(R.id.track_energy_display_label);
        proteinsValueLabel = view.findViewById(R.id.track_protein_display_label);
        fatsValueLabel = view.findViewById(R.id.track_fats_display_label);
        carbohydratesValueLabel = view.findViewById(R.id.track_carbohydrates_display_label);
        // Assign the progress bars so that the progress can be shown
        energyProgressBar = view.findViewById(R.id.track_energy_progress_bar);
        proteinsProgressBar = view.findViewById(R.id.track_protein_progress_bar);
        fatsProgressBar = view.findViewById(R.id.track_fats_progress_bar);
        carbohydratesProgressBar = view.findViewById(R.id.track_carbohydrates_progress_bar);

        // Update tracking tracking data as soon as this tab loads
        updateDietTrackingData();

        // Add on click listener to the update tracking data
        view.findViewById(R.id.track_update_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update tracking data each time user clicks update button
                updateDietTrackingData();
            }
        });

        return view;
    }

    // Update tracking data each time user clicks update button
    void updateDietTrackingData() {

        // Simple try catch block to catch any errors and exceptions
        try {

            // Call the method to get the "updated" tracking data and set the text views to the tracking data
            ofyDatabase.getTrackDietDataAndSetData(requireActivity(), energyValueLabel, proteinsValueLabel, fatsValueLabel, carbohydratesValueLabel, energyProgressBar, proteinsProgressBar, fatsProgressBar, carbohydratesProgressBar);

        } catch (Exception e) {
            // Catch exception and show toast message
            Toast.makeText(requireActivity(), "Error:" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}