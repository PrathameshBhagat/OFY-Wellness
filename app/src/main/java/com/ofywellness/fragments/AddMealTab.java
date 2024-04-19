package com.ofywellness.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ofywellness.R;
import com.ofywellness.db.ofyDatabase;
import com.ofywellness.modals.Meal;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for AddMealTab tab in Home page
 */
public class AddMealTab extends Fragment {
    /* TODO : Add progress bar while uploading image,
         and comments required, DB operation to proper folder
         proper error handling see logcat */
    private Spinner mealTypeSpinner;
    private Uri imageuri;
    private ImageView mealImageView;
    private EditText mealNameTextView, mealEnergyTextView, mealProteinsTextView, mealFatsTextView, mealCarbohydratesTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment and store it,
        // modification for the spinner
        View view = inflater.inflate(R.layout.fragment_add_meal_tab, container, false);

        // Get the spinner object and set listeners for events
        mealTypeSpinner = view.findViewById(R.id.meal_type_spinner);

        // Add working to the meal type spinner
        addSpinnerForMealType();

        // Get all objects required from layout
        mealNameTextView = view.findViewById(R.id.meal_name_field);
        mealEnergyTextView = view.findViewById(R.id.meal_energy_field);
        mealProteinsTextView = view.findViewById(R.id.meal_protein_field);
        mealFatsTextView = view.findViewById(R.id.meal_fats_field);
        mealCarbohydratesTextView = view.findViewById(R.id.meal_carbohydrates_field);
        mealImageView = view.findViewById(R.id.meal_add_image_view);

        // Add button to upload today's meal to the "DATABASE"
        view.findViewById(R.id.upload_meal_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageuri != null) {

                    uploadImageToFirebaseStorage(imageuri);

                    // Careful now imageuri should point to firebase location instead of a local file
                    if( imageuri.toString().contains("http")) {

                        // Create new meal object to add to database with random url
                        Meal newMeal = new Meal(imageuri.toString(), mealNameTextView.getText().toString(), Integer.parseInt(mealEnergyTextView.getText().toString()), Integer.parseInt(mealProteinsTextView.getText().toString()), Integer.parseInt(mealFatsTextView.getText().toString()), Integer.parseInt(mealCarbohydratesTextView.getText().toString()));

                        // Add meal to the database
                        ofyDatabase.addMeal(newMeal, mealTypeSpinner.getSelectedItem().toString());

                        Toast.makeText(requireActivity(),"Successfully added the meal",Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Toast.makeText(requireActivity(),"Successfully added the meal",Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(requireActivity(), "Please select an image ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.save_meal_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);

            }
        });

        return view;
    }
    public void uploadImageToFirebaseStorage(Uri uri) {

        StorageReference filereference = FirebaseStorage.getInstance().getReference().child(System.currentTimeMillis() + "." + getFileExtension(imageuri));
        filereference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filereference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageuri = uri;
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cr = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    // Method to add spinner for meal type to layout
    private void addSpinnerForMealType() {

        // Array List to add options to spinner
        List<String> options = new ArrayList<>();
        options.add("Breakfast");
        options.add("Lunch");
        options.add("Dinner");

        // Create adapter for spinner and add adapter to spinner and set default value
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, options);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(arrayAdapter);
        mealTypeSpinner.setSelection(0);
    }

    // Method to input image from user
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if successfully got image
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            // Save url to upload to data base
            imageuri = data.getData();
            // Set image view to selected image
            mealImageView.setImageURI(imageuri);
        }
        else {
            Toast.makeText(requireActivity(), "Unable to select image ",Toast.LENGTH_SHORT).show();
        }
    }
}