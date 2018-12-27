package com.example.kenne.box_a_lot;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kenne.box_a_lot.adapters.CreateStorageroomPagerAdapter;
import com.example.kenne.box_a_lot.customViews.LockableViewPager;
import com.example.kenne.box_a_lot.fragments.CreateStoragePageFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class CreateStorageroomActivity extends AppCompatActivity {

    private LockableViewPager vpPager;
    private TabLayout VPHeader;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private CreateStorageroomPagerAdapter adapterViewPager;

    private Button nextBtn;
    private Button previousBtn;
    private TextView errorTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_storageroom);

        errorTV = findViewById(R.id.create_storageroom_error_TV);
        VPHeader = findViewById(R.id.create_storageroom_pager_header);
        vpPager = (LockableViewPager) findViewById(R.id.create_storageroom_viewpager);
        VPHeader.setupWithViewPager(vpPager, true);
        adapterViewPager = new CreateStorageroomPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setOffscreenPageLimit(4);

        nextBtn = findViewById(R.id.create_storageroom_next_button);
        previousBtn = findViewById(R.id.create_storageroom_previous_button);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String error = ((CreateStoragePageFragment)(((CreateStorageroomPagerAdapter)vpPager.getAdapter()).getFragment(vpPager.getCurrentItem()))).validateUserInput();
                if(error != null){

                    errorTV.setText(error);
                    return;
                }

                errorTV.setText("");


                    switch(vpPager.getCurrentItem()){
                    case 0:
                        previousBtn.setVisibility(View.VISIBLE);
                        vpPager.setCurrentItem(vpPager.getCurrentItem()+1);
                        break;
                    case 1:
                        vpPager.setCurrentItem(vpPager.getCurrentItem()+1);
                        break;
                    case 2:
                        vpPager.setCurrentItem(vpPager.getCurrentItem()+1);
                        nextBtn.setText(R.string.createStorageNextBtnCreateTxt);
                        break;
                    case 3:
                        //createStorageroom();
                        break;

                }
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vpPager.getCurrentItem() == 1)
                {
                    previousBtn.setVisibility(View.GONE);
                }
                else if(vpPager.getCurrentItem() == 3){
                    nextBtn.setText(R.string.createStorageNextBtnTxt);
                }

                vpPager.setCurrentItem(vpPager.getCurrentItem()-1);
                errorTV.setText("");
            }
        });
    }

//    private boolean createStorageroom(){

    //}
}
