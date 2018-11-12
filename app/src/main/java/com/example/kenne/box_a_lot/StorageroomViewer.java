package com.example.kenne.box_a_lot;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

public class StorageroomViewer extends AppCompatActivity {

    private FloatingActionButton editFab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storageroom_viewer);

        editFab = findViewById(R.id.editFabBtn);
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {

            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();

            // Load chat room contents
            setupButtonClickListener();
            setupImageClickListener();
        }
    }

}
