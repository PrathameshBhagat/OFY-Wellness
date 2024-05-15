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

/**
 */
public class ProfileTab extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_tab, container, false);

        // Google signout functionality
        GoogleSignInOptions gso;
        GoogleSignInClient gsc;
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(requireActivity(), gso);

        view.findViewById(R.id.profile_logout_card).setOnClickListener(view1 -> {

            new AlertDialog.Builder(requireActivity())
                    .setTitle("Logout")
                    .setMessage("Do you really want to log out ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            gsc.signOut().addOnCompleteListener(task -> {
                                requireActivity().finish();
                                requireActivity().finishAffinity();
                                startActivity(new Intent(requireActivity(), LoginActivity.class));
                                Toast.makeText(requireActivity(), "Logged Out !, Signin Again ", Toast.LENGTH_SHORT).show();
                            });
                        }
                    })
                    .setNegativeButton("No", null).show();
        });

        // Inflate the layout for this fragment
        return view;
    }
}