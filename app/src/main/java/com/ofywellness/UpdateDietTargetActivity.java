package com.ofywellness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ofywellness.db.ofyDatabase;
import com.ofywellness.modals.Meal;

public class UpdateDietTargetActivity extends AppCompatActivity {
    private EditText targetEnergyEditText, targetProteinsEditText, targetFatsEditText, targetCarbohydratesEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_diet_target);

        targetEnergyEditText = findViewById(R.id.target_energy_field);
        targetProteinsEditText = findViewById(R.id.target_protein_field);
        targetFatsEditText = findViewById(R.id.target_fats_field);
        targetCarbohydratesEditText = findViewById(R.id.target_carbohydrates_field);

        findViewById(R.id.target_update_target_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to update diet target
                updateDietTarget();
            }
        });

    }

    void updateDietTarget(){

        // simple try catch block
        try {

            // Get the updated diet target values
            int targetEnergy = Integer.parseInt(targetEnergyEditText.getText().toString());
            int targetProteins = Integer.parseInt(targetProteinsEditText.getText().toString());
            int targetFats = Integer.parseInt(targetFatsEditText.getText().toString());
            int targetCarbohydrates = Integer.parseInt(targetCarbohydratesEditText.getText().toString());

            // Make sure none of them is zero, as diet target values should not be zero
            if( targetEnergy == 0 || targetProteins == 0 || targetFats == 0 || targetCarbohydrates == 0 )

                throw new Exception(" Diet target values should not be zero");

            // Store the updated diet target values in a meal object for ease of transfer
            Meal updatedDietTarget = new Meal(null, null,targetEnergy,
                    targetProteins,
                    targetFats,
                    targetCarbohydrates);

            // Call the database method to update diet target
            ofyDatabase.addDietTarget(updatedDietTarget, UpdateDietTargetActivity.this);

            // Show a toast message
            Toast.makeText(UpdateDietTargetActivity.this, "Successfully updated target ", Toast.LENGTH_SHORT).show();

            // End the current activity and move back to Home activity
            finish();
            startActivity(new Intent(this,HomeActivity.class));

        } catch (Exception e) {
            // Catch exception, show a toast error message and print error stack
            Toast.makeText(UpdateDietTargetActivity.this, "Please check inputs, no decimals or zeros", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}