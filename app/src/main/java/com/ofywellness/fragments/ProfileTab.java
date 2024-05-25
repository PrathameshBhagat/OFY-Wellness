package com.ofywellness.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.ofywellness.LoginActivity;
import com.ofywellness.R;
import com.ofywellness.UpdateDietTargetActivity;
import com.ofywellness.UpdateMedicinePrescriptionActivity;

/**
 */
public class ProfileTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and store it,
        // modification for getting the view object
        View view = inflater.inflate(R.layout.fragment_profile_tab, container, false);

        // Google signout functionality
        GoogleSignInOptions gso;
        GoogleSignInClient gsc;
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(requireActivity(), gso);

        // Set onClick listener to the log out card view
        view.findViewById(R.id.profile_logout_card).setOnClickListener(view1 -> {

            // Call an alert dialog to confirm logout
            new AlertDialog.Builder(requireActivity())
                    .setTitle("Logout")
                    .setMessage("Do you really want to log out ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Call GoogleSignInClient's log out method to logout the user
                                gsc.signOut().addOnCompleteListener(task -> {
                                    // On logout finish all activities
                                    requireActivity().finish();
                                    requireActivity().finishAffinity();

                                    // Start new activity to make user able to login again
                                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                                    // Show toast message to login again
                                    Toast.makeText(requireActivity(), " Logged Out ! ", Toast.LENGTH_SHORT).show();
                            });
                        }
                    })
                    .setNegativeButton("No", null).show();
        });

        view.findViewById(R.id.profile_update_prescription_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start new activity with intent to move to update prescription activity
                startActivity(new Intent(requireActivity(), UpdateMedicinePrescriptionActivity.class));
            }
        });

        view.findViewById(R.id.profile_update_target_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start new activity with intent to move to update diet target activity
                startActivity(new Intent(requireActivity(), UpdateDietTargetActivity.class));
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}