package com.ofywellness;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ofywellness.db.ofyDatabase;
import com.ofywellness.modals.Meal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AddMealActivity extends AppCompatActivity {

    /* TODO : Database operation to proper files and folder */
    private Spinner mealTypeSpinner;
    private Uri mealImageUri;
    private ImageView mealImageView;
    private EditText mealNameEditText, mealEnergyEditText, mealProteinsEditText, mealFatsEditText, mealCarbohydratesEditText;
    private CardView mealUploadProgressBarCardView;
    private ConstraintLayout addMealConstrainedLayout;
    private String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        // Get the spinner object and set listeners for events
        mealTypeSpinner = findViewById(R.id.meal_type_spinner);

        // Add working to the meal type spinner
        addSpinnerForMealType();

        // Set the received userID
        Uid = getIntent().getStringExtra("ID");

        // Get all objects required from layout
        mealNameEditText = findViewById(R.id.meal_name_field);
        mealEnergyEditText = findViewById(R.id.meal_energy_field);
        mealProteinsEditText = findViewById(R.id.meal_protein_field);
        mealFatsEditText = findViewById(R.id.meal_fats_field);
        mealCarbohydratesEditText = findViewById(R.id.meal_carbohydrates_field);
        mealImageView = findViewById(R.id.meal_add_image_view);
        mealUploadProgressBarCardView = findViewById(R.id.meal_upload_meal_progress_bar_card);

        // Constrained Layout to hide content when uploading/saving content
        addMealConstrainedLayout = findViewById(R.id.meal_add_meal_content_constraint_layout);

        // Hide the progress bar
        mealUploadProgressBarCardView.setVisibility(View.GONE);


        // Add button to upload today's meal to the "DATABASE"
        findViewById(R.id.meal_upload_meal_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if mealImageUri is not null (an image is selected)
                if (mealImageUri != null) {

                    // Upload meal to firebase database
                    uploadMealToDatabase(mealImageUri);

                } else {
                    Toast.makeText(AddMealActivity.this, "Please select an image ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.meal_select_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Intent to get image from user
                Intent galleryIntent = new Intent();
                // set Intent to get content from user
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                // set content type to image
                galleryIntent.setType("image/*");
                // start activity and pass a request code
                startActivityForResult(galleryIntent, 2);
                // above line takes the flow to onActivityResult method
            }
        });

    }

    // Method to upload Meal to Firebase
    public void uploadMealToDatabase(Uri uri) {

        // Check if previous upload is currently in progress
        if (mealUploadProgressBarCardView.getVisibility() == View.VISIBLE) {
            // Show toast messages
            Toast.makeText(AddMealActivity.this, "Please wait until previous upload", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get Firebase Storage Reference to upload image
        StorageReference fileReference = FirebaseStorage.getInstance().getReference()
                .child("Images/Meals/" + Uid + "/" + LocalDate.now())
                .child(LocalTime.now().withNano(0) + "." + getFileExtension(uri));

        // Add event listener to execute code on successful image upload and hence upload the meal
        fileReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // Now as image is uploaded, get its download url, if got move to onSuccessListener
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri linkToFirebaseStorageImage) {

                        // Simple try catch block to catch any errors and exceptions
                        try {
                            // Now uri points to firebase location, if it has http
                            if (linkToFirebaseStorageImage.toString().contains("http")) {

                                // Create new meal object to add to database with image url
                                Meal newMeal = new Meal(linkToFirebaseStorageImage.toString(),
                                        mealNameEditText.getText().toString(),
                                        Integer.parseInt(mealEnergyEditText.getText().toString()),
                                        Integer.parseInt(mealProteinsEditText.getText().toString()),
                                        Integer.parseInt(mealFatsEditText.getText().toString()),
                                        Integer.parseInt(mealCarbohydratesEditText.getText().toString()));

                                // Add meal to the database
                                ofyDatabase.addMeal(newMeal, mealTypeSpinner.getSelectedItem().toString(), AddMealActivity.this);

                                // Show toast messages
                                Toast.makeText(AddMealActivity.this, "Successfully added the meal", Toast.LENGTH_SHORT).show();

                            } else {
                                // Show toast messages
                                Toast.makeText(AddMealActivity.this, "Successfully added the meal, but unable to retrive link, aborted", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            // Catch exception and show toast message
                            Toast.makeText(AddMealActivity.this, "Please check inputs and internet", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                });

                //Hide the progress bar's Card View
                mealUploadProgressBarCardView.setVisibility(View.GONE);
                // Display the add meals Constrained Layout
                addMealConstrainedLayout.setVisibility(View.VISIBLE);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                // Show the progress bar
                mealUploadProgressBarCardView.setVisibility(View.VISIBLE);
                // Hide the add meals Constrained Layout
                addMealConstrainedLayout.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // Hide the progress bar
                mealUploadProgressBarCardView.setVisibility(View.GONE);
                // Display the add meals Constrained Layout
                addMealConstrainedLayout.setVisibility(View.VISIBLE);

                // If failed to upload image abort and show error message
                Toast.makeText(AddMealActivity.this, "Failed to upload image, hence aborted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to get file extension of image selected
    private String getFileExtension(Uri uri) {
        // below code returns the file extension of selected image
        ContentResolver cr = AddMealActivity.this.getContentResolver();
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddMealActivity.this, android.R.layout.simple_spinner_item, options);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(arrayAdapter);
        mealTypeSpinner.setSelection(0);
    }


    // Method to check and operate input image from user
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if successfully got image
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            // Save url to upload to data base
            mealImageUri = data.getData();
            // Set image view to selected image
            mealImageView.setImageURI(mealImageUri);
        } else {
            // Show error message
            Toast.makeText(AddMealActivity.this, "Unable to select image ", Toast.LENGTH_SHORT).show();
        }
    }

}