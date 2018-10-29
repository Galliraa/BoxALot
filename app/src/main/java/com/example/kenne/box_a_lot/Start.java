package com.example.kenne.box_a_lot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class Start extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ImageButton renterButton = (ImageButton) findViewById(R.id.IB_Renter);
        ImageButton renteeButton = (ImageButton) findViewById(R.id.IB_Rentee);

        renterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Start.this,
                        "Show map", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(Start.this, MainActivity.class);
                        startActivity(myIntent);
            }
        });

        renteeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Start.this,
                        "Show application form", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(Start.this, CreateStorageActivity.class);
                startActivity(myIntent);
            }
        });

    }
}
