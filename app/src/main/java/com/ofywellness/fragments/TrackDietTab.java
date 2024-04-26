package com.ofywellness.fragments;

import static com.ofywellness.db.ofyDatabase.getTrackDietDataAndSetView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ofywellness.R;

/**
 * Fragment for DietTrack tab in Home page
 */
public class TrackDietTab extends Fragment {

    /* TODO : Remove the TextView assignment and context to database operation method */

    private TextView energyValueLabel, proteinsValueLabel, fatsValueLabel, carbohydratesValueLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the view for this fragment and inflate it
        View view = inflater.inflate(R.layout.fragment_track_diet_tab, container, false);

        // Assign the text views so that tracking data can be set
        energyValueLabel = view.findViewById(R.id.track_energy_display_label);
        proteinsValueLabel = view.findViewById(R.id.track_protein_display_label);
        fatsValueLabel = view.findViewById(R.id.track_fats_display_label);
        carbohydratesValueLabel = view.findViewById(R.id.track_carbohydrates_display_label);

        // Call the method to get tracking data and set the text views to the tracking data
        getTrackDietDataAndSetView(requireActivity(),energyValueLabel, proteinsValueLabel, fatsValueLabel, carbohydratesValueLabel);

        // Add on click listener to the update tracking data view
        view.findViewById(R.id.track_update_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Call the method to get the "updated" tracking data and set the text views to the tracking data
                getTrackDietDataAndSetView(requireActivity(),energyValueLabel, proteinsValueLabel, fatsValueLabel, carbohydratesValueLabel);

            }
        });

        return view;
    }
}