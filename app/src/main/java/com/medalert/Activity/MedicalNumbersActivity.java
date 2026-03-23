package com.medalert.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.medalert.R;

public class MedicalNumbersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_numbers);

        // Initialize the ListView and Back Button
        ListView listView = findViewById(R.id.listViewNumbers);
        ImageButton backButton = findViewById(R.id.btn_back);

        // List of UK Emergency Numbers
        String[] medicalNumbers = {
                "Emergency Services: 999",
                "NHS 111 Service: 111",
                "Non-Emergency Police: 101",
                "Non-Emergency Medical Helpline: 0800 123 4567",
                "Northern Ireland Ambulance Service: 028 9040 0999",
                "Mental Health Helpline: 0808 808 8000",
                "Childline: 0800 1111",
                "Samaritans: 116 123",
                "Domestic Abuse Helpline: 0808 2000 247",
                "Gas Emergency: 0800 111 999",
                "Power Cut Emergency: 105"
        };

        // Populate the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, medicalNumbers);
        listView.setAdapter(adapter);

        // Back Button Functionality
        backButton.setOnClickListener(v -> {
            // Close this activity and go back to the previous one
            finish();
        });
    }
}
