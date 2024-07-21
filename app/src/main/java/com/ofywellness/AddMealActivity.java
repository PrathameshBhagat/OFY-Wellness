package com.ofywellness;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ofywellness.db.ofyDatabase;
import com.ofywellness.modals.Meal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddMealActivity extends AppCompatActivity {

    /* TODO : Database operation to proper files and folder */
    /* Decimals in nutrients, very important
    *  Method extraction and effective tests
    *  Proper progress bars and re-detect meal "button" needed
    *  Try Catch block indentation repair
    *   */
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

        // Add listener to button for selecting today's meal
        findViewById(R.id.meal_select_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Intent to get image from user
                Intent galleryIntent = new Intent();

                // Create a dialog and add respective properties to get ask user's preference for image source
                new AlertDialog.Builder(AddMealActivity.this)
                        // Add title to dialog
                        .setTitle("Image")
                        // Add message to dialog
                        .setMessage("Select image from ")
                        // Add button to take photo from camera
                        .setPositiveButton("Take Photo", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Set Intent to capture image from camera
                                galleryIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                                // Start activity with intent and pass a request code of 3
                                // Takes the flow to onActivityResult method with code 3
                                startActivityForResult(galleryIntent, 3);
                            }
                        })
                        // Add button to take photo from gallery
                        .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Set Intent to get content from gallery
                                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                                // Set content type to image
                                galleryIntent.setType("image/*");
                                // Start activity and pass a request code of 2
                                // Takes the flow to onActivityResult method with code 2
                                startActivityForResult(galleryIntent, 2);

                            }
                        })
                        // Show the alert dialog
                        .show();
            }
        });

    }

    // Method to add spinner for meal type to layout
    private void addSpinnerForMealType() {

        // Array List to add options to spinner
        List<String> options = new ArrayList<>();
        options.add("Breakfast");
        options.add("Lunch");
        options.add("Snacks");
        options.add("Dinner");

        // Create adapter for spinner and add adapter to spinner and set default value
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddMealActivity.this, android.R.layout.simple_spinner_item, options);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(arrayAdapter);
        mealTypeSpinner.setSelection(0);
    }

    // Method to upload Meal to Firebase
    public void uploadMealToDatabase(Uri uri) {

        // Check if previous upload is currently in progress
        if (mealUploadProgressBarCardView.getVisibility() == View.VISIBLE) {
            // Show toast messages
            Toast.makeText(AddMealActivity.this, "Please wait until previous upload", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create temp meal object
        Meal tempMeal;

        // Simple try catch block to check if inputs can be converted to integer
        try {

            // Store meal data in proper format for use in future in temp meal object
            tempMeal = new Meal("",
                    mealNameEditText.getText().toString(),
                    Integer.parseInt(mealEnergyEditText.getText().toString()),
                    Integer.parseInt(mealProteinsEditText.getText().toString()),
                    Integer.parseInt(mealFatsEditText.getText().toString()),
                    Integer.parseInt(mealCarbohydratesEditText.getText().toString()));

        } catch (Exception e) {
            // Catch exception and show toast message
            Toast.makeText(AddMealActivity.this, "Please do not enter decimals", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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

                                // Create new meal object with image url and data from temp Meal
                                Meal newMeal = new Meal(linkToFirebaseStorageImage.toString(),
                                        tempMeal.getName(),
                                        tempMeal.getEnergy(),
                                        tempMeal.getProteins(),
                                        tempMeal.getFats(),
                                        tempMeal.getCarbohydrates());

                                // Add meal object to the database
                                ofyDatabase.addMeal(newMeal, mealTypeSpinner.getSelectedItem().toString(), AddMealActivity.this);

                                // Show toast messages
                                Toast.makeText(AddMealActivity.this, "Successfully added the meal", Toast.LENGTH_SHORT).show();

                            } else {
                                // Show toast messages
                                Toast.makeText(AddMealActivity.this, "Saved image, but unable to retrieve link, aborted", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            // Catch exception and show toast message
                            Toast.makeText(AddMealActivity.this, "Error in saving", Toast.LENGTH_SHORT).show();
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

    // Method to check and operate input image from user
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Simple try catch block
        try {

        // Check if successfully got image
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            // If request code is two i.e image was obtained from gallery
            // Save url to upload to data base
            mealImageUri = data.getData();
            // Set image view to selected image
            mealImageView.setImageURI(mealImageUri);

            // Now detect nutrients from the image via AI and set the views
            detectNutrientsAndSetViews(MediaStore.Images.Media.getBitmap(this.getContentResolver(), mealImageUri));

        } else if (requestCode == 3 && resultCode == RESULT_OK && data != null) {
            // If request code is three i.e image was captured from camera
            // Set image view to captured image
            mealImageView.setImageBitmap((Bitmap) data.getExtras().get("data"));

            // Save url to upload to data base
            mealImageUri = getImageUri((Bitmap) data.getExtras().get("data"));

            // Now detect nutrients from the image via AI and set the views
            detectNutrientsAndSetViews((Bitmap) data.getExtras().get("data"));

        } else {
            // Show error message
            Toast.makeText(AddMealActivity.this, "Unable to select image ", Toast.LENGTH_SHORT).show();
        }

        } catch (Exception e) {
            // Print error message
            e.printStackTrace();
            // Show error message to user
            Toast.makeText(AddMealActivity.this, "Unable to select image ", Toast.LENGTH_SHORT).show();

        }
    }

    // Method to store image and get its uri
    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(AddMealActivity.this.getContentResolver(), inImage,
                "OFY_" + new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.getDefault()).format(new Date()), null);
        return Uri.parse(path);
    }

    void detectNutrientsAndSetViews(Bitmap mealImage) {

        // Create alert dialog to show user we are detecting the meal and its nutrients
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Please wait ...")
                .setCancelable(false)
                .setMessage("Detecting meal and nutrients")
                .create();

        // Show the alert dialog
        alertDialog.show();

        // Create Generative AI Model to detect meal and its nutrients
        GenerativeModelFutures model = GenerativeModelFutures.from(
                new GenerativeModel( "gemini-1.5-pro",
                        BuildConfig.GEMINI_API_KEY));

        // Generate content for the Gen AI model and add query text and meal image
        Content content = new Content.Builder()
                .addText("Identify the meal in the image, calculate energy, proteins, fats and carbohydrates content in it and return JSON object with no extra text, with meal name and do not add units to the values. Object names should be name, energy, proteins, fats, carbohydrates and only detect if proper meal identification was done.")
                .addImage(mealImage)
                .build();

        // Now add a callback and generate response
        Futures.addCallback( model.generateContent(content) , new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {

                // On Success get the result string and replace unnecessary content
                String resultText = result.getText().replace("```","").replace("json","");;

                // Now try to convert result in to JSON object to get and set nutrient data
                try {
                    // Convert to JSON object
                    JSONObject mealNutrients = new JSONObject(resultText);

                    // Get the values from the JSON Object and set the text views
                    mealNameEditText.setText(mealNutrients.getString("name"));
                    mealEnergyEditText.setText(mealNutrients.getString("energy"));
                    mealProteinsEditText.setText(mealNutrients.getString("proteins"));
                    mealFatsEditText.setText(mealNutrients.getString("fats"));
                    mealCarbohydratesEditText.setText(mealNutrients.getString("carbohydrates"));

                } catch (JSONException e) {
                    // Print Error message
                    e.printStackTrace();
                    // Show user error message
                    Toast.makeText(AddMealActivity.this, "Unable to detect meal and nutrients. Please reselect image or enter manually", Toast.LENGTH_SHORT ).show();
                }

                // Now dismiss/remove the alert dialog
                alertDialog.dismiss();
            }

            @Override
            public void onFailure(Throwable t) {

                // First dismiss/remove the alert dialog
                alertDialog.dismiss();

                // On failure print Error message
                t.printStackTrace();
                // Show user error message
                Toast.makeText(AddMealActivity.this, "Unable to detect meal and nutrients. Please reselect image or enter manually", Toast.LENGTH_SHORT ).show();
            }
        }, Runnable::run);
    }

}