package com.ofywellness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ofywellness.db.ofyDatabase;

import java.util.HashMap;

public class UpdateMedicinePrescriptionActivity extends AppCompatActivity {
    private EditText medicine_1Prescription, medicine_2Prescription, medicine_3Prescription, medicine_4Prescription, medicine_5Prescription;
    private EditText medicine_1Name, medicine_2Name, medicine_3Name, medicine_4Name, medicine_5Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_prescription);

        medicine_1Prescription = findViewById(R.id.prescription_update_medicine_1_r_intake);
        medicine_2Prescription = findViewById(R.id.prescription_update_medicine_2_r_intake);
        medicine_3Prescription = findViewById(R.id.prescription_update_medicine_3_r_intake);
        medicine_4Prescription = findViewById(R.id.prescription_update_medicine_4_r_intake);
        medicine_5Prescription = findViewById(R.id.prescription_update_medicine_5_r_intake);


        medicine_1Name = findViewById(R.id.prescription_update_medicine_1_name);
        medicine_2Name = findViewById(R.id.prescription_update_medicine_2_name);
        medicine_3Name = findViewById(R.id.prescription_update_medicine_3_name);
        medicine_4Name = findViewById(R.id.prescription_update_medicine_4_name);
        medicine_5Name = findViewById(R.id.prescription_update_medicine_5_name);

        findViewById(R.id.target_update_prescription_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to update diet target
                updatePrescription();
            }
        });

    }

    void updatePrescription() {

        // Simple try catch block
        try {

            // Get all the updated prescription values
            int prescription1 = Integer.parseInt(medicine_1Prescription.getText().toString());
            int prescription2 = Integer.parseInt(medicine_2Prescription.getText().toString());
            int prescription3 = Integer.parseInt(medicine_3Prescription.getText().toString());
            int prescription4 = Integer.parseInt(medicine_4Prescription.getText().toString());
            int prescription5 = Integer.parseInt(medicine_5Prescription.getText().toString());

            // Get all the updated medicine names
            String name1 = medicine_1Name.getText().toString();
            String name2 = medicine_2Name.getText().toString();
            String name3 = medicine_3Name.getText().toString();
            String name4 = medicine_4Name.getText().toString();
            String name5 = medicine_5Name.getText().toString();

            // Make sure none of them is zero, as diet target values should not be zero
            if (prescription1 == 0 || prescription2 == 0 || prescription3 == 0
                    || prescription4 == 0 || prescription5 == 0)

                throw new Exception(" Please zeros or decimals or text in intake");

            // Collect the prescription in a single variable
            HashMap<String, Integer> prescription = new HashMap<>();

            // Add all medicine names and new prescription
            prescription.put(name1, prescription1);
            prescription.put(name2, prescription2);
            prescription.put(name3, prescription3);
            prescription.put(name4, prescription4);
            prescription.put(name5, prescription5);

            // Call the database method to update diet target
            ofyDatabase.setPrescription(prescription, UpdateMedicinePrescriptionActivity.this);

            // Show a toast message
            Toast.makeText(UpdateMedicinePrescriptionActivity.this, "Successfully updated target ", Toast.LENGTH_SHORT).show();

            // End the current activity and move back to Home activity
            finish();
            startActivity(new Intent(this, HomeActivity.class));

        } catch (Exception e) {
            // Catch exception, show a toast error message and print error stack
            Toast.makeText(UpdateMedicinePrescriptionActivity.this, "Please check inputs, no decimals or zeros", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}

