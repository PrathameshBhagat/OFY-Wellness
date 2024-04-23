package com.ofywellness.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.ofywellness.R;
import com.ofywellness.db.ofyDatabase;

/**
 * Fragment for DietTrack tab in Home page
 */
public class TrackDietTab extends Fragment {

    /* TODO : Complete and add comments*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the view for this fragment and inflate it
        View view = inflater.inflate(R.layout.fragment_track_diet_tab, container, false);

        view.findViewById(R.id.track_update_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                ofyDatabase.trackDiet();
            }
        });

        return view;
    }
}