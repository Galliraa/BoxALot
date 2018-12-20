package com.example.kenne.box_a_lot;

import android.os.Bundle;

import com.example.kenne.box_a_lot.customViews.LockableViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class CreateStorageroomActivity extends AppCompatActivity {

    private LockableViewPager vpPager;
    private TabLayout VPHeader;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_storageroom);

        VPHeader = findViewById(R.id.create_storageroom_pager_header);

        vpPager = (LockableViewPager) findViewById(R.id.create_storageroom_viewpager);

    }
}
