package com.example.kenne.box_a_lot;

import android.os.Bundle;

import com.example.kenne.box_a_lot.fragments.StorageFragment;
import com.example.kenne.box_a_lot.models.StorageRoom;
import com.example.kenne.box_a_lot.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class StorageroomViewer extends AppCompatActivity {

    private static final String MESSAGES_CHILD = "Users/";

    private FloatingActionButton editFab;

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth auth;

    private StorageFragment storageFragment= null;
    private StorageRoom storageroom = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storageroom_viewer);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        editFab = findViewById(R.id.editFabBtn);

        String id = getIntent().getExtras().getString("RoomId");
        storageroom = new StorageRoom();
        storageroom.StorageMap = (HashMap<String,Object>)getIntent().getSerializableExtra("storageroom");
        storageFragment = new StorageFragment();
        storageFragment.setStorage(storageroom);

        if(id != null) {
            User user = new User();
            List<String> roomIds = new ArrayList<String>();
            roomIds.add(id);
            user.setStorageroomIds(roomIds);
            user.setAddress("test2");

            mFirebaseDatabaseReference.child(MESSAGES_CHILD + "/" + auth.getCurrentUser().getUid()).setValue(user.getUserMap());
        }

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
