package com.example.smartwatermanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Firebase extends AppCompatActivity {

    private GaugeView distanceGauge;
    private TextView humidityGauge;
    private Spinner locationSpinner;
    private DatabaseReference sensorDataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        locationSpinner = findViewById(R.id.locationSpinner);

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        // Set up initial DatabaseReference based on the default spinner selection
        updateSensorDataRef(locationSpinner.getSelectedItemPosition(), firebaseDatabase);

        // Get references to GaugeView widgets in your layout
        distanceGauge = findViewById(R.id.gaugeView);
        humidityGauge = findViewById(R.id.waterFlowTextView);

        // Set up a listener to retrieve data from Firebase based on the selected sensor
        sensorDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve values from the dataSnapshot
                    DataSnapshot waterLevelSnapshot = dataSnapshot.child("WaterLevel");

                    if (waterLevelSnapshot.exists()) {
                        Integer distance = waterLevelSnapshot.getValue(Integer.class);

                        if (distance != null) {
                            int distanceValue = distance;
                            // Now you can safely use distanceValue
                            distanceGauge.setValue(distanceValue);
                            humidityGauge.setText("40"); // Assuming you want to set a string, not an integer
                            Toast.makeText(Firebase.this, "Waterlevel: " + distanceValue, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Firebase.this, "WaterLevel itself exists but is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle the case where "WaterLevel" does not exist in the dataSnapshot
                        Toast.makeText(Firebase.this, "WaterLevel itself doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where the dataSnapshot itself doesn't exist
                    Toast.makeText(Firebase.this, "dataSnapshot itself doesn't exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });

        // Add a listener to the spinner to change the selected sensor
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // When the spinner selection changes, update the DatabaseReference
                updateSensorDataRef(position, firebaseDatabase);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
    }

    // Method to update the DatabaseReference based on spinner selection
    private void updateSensorDataRef(int selectedSensor, FirebaseDatabase firebaseDatabase) {
        if (selectedSensor == 0) {
            sensorDataRef = firebaseDatabase.getReference("sensor_0");
        } else if (selectedSensor == 1) {
            sensorDataRef = firebaseDatabase.getReference("sensor_1");
        }
    }
}
