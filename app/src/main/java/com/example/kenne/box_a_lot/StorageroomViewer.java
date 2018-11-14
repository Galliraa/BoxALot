package com.example.kenne.box_a_lot;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.kenne.box_a_lot.fragments.StorageFragment;
import com.example.kenne.box_a_lot.models.StorageRoom;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class StorageroomViewer extends AppCompatActivity {

    private FloatingActionButton editFab;

    private StorageFragment storageFragment= null;
    private StorageRoom storageroom = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storageroom_viewer);

        editFab = findViewById(R.id.editFabBtn);


        storageroom = new StorageRoom();
        storageroom.StorageMap = (HashMap<String,Object>)getIntent().getSerializableExtra("storageroom");
        storageFragment = new StorageFragment();
        storageFragment.setStorage(storageroom);
        //storageFragment.setStorage();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        /*
         * When this container fragment is created, we fill it with our first
         * "real" fragment
         */
        transaction.replace(R.id.storageroom_frame, storageFragment);

        transaction.commit();

    }

}