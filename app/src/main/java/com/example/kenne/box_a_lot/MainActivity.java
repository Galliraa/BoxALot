package com.example.kenne.box_a_lot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.kenne.box_a_lot.adapters.PageAdapter;
import com.example.kenne.box_a_lot.customViews.LockableViewPager;
import com.example.kenne.box_a_lot.fragments.StorageRoomFragment;
import com.example.kenne.box_a_lot.fragments.UserFragment;
import com.example.kenne.box_a_lot.interfaces.ChangeFragmentInterface;
import com.example.kenne.box_a_lot.interfaces.UiUpdateInterface;
import com.example.kenne.box_a_lot.models.StorageRoom;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;


public class MainActivity extends AppCompatActivity implements UiUpdateInterface, StorageRoomFragment.OnListFragmentInteractionListener {


    @Override
    public void onListFragmentInteraction(StorageRoom item) {

    }

    private enum UserMode {LIST_VIEW, DETAILS_VIEW, STORAGEROOM_VIEW}
    private UserMode userMode;
    private LockableViewPager vpPager;
    private TabLayout VPHeader;

    private static final int LOGIN_REQUEST = 1;

    FragmentPagerAdapter adapterViewPager;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        VPHeader = (TabLayout) findViewById(R.id.pager_header);

        vpPager = (LockableViewPager) findViewById(R.id.list_container);

        adapterViewPager = new PageAdapter(getSupportFragmentManager(), getApplicationContext());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setOffscreenPageLimit(3);

            if(userMode == null){
                userMode = UserMode.LIST_VIEW;  //default
            }


            VPHeader.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int i = tab.getPosition();
                    switch(i){
                        case 0:
                            break;
                        case 1:
                            if (mFirebaseUser == null) {
                                // Not signed in, launch the Sign In activity
                                //((UserFragment)(((PageAdapter)vpPager.getAdapter()).getItem(i))).showLoginDialog(MainActivity.this, (Activity)MainActivity.this, getSupportFragmentManager());

                            }
                            break;
                        case 2:
                            if (mFirebaseUser == null) {
                                // Not signed in, launch the Sign In activity
                                //((UserFragment)(((PageAdapter)vpPager.getAdapter()).getItem(i))).showLoginDialog(MainActivity.this, (Activity)MainActivity.this, getSupportFragmentManager());
                            }
                            break;
                        case 3:
                            if (mFirebaseUser == null) {
                                ((UserFragment)(((PageAdapter)vpPager.getAdapter()).getItem(i))).showLoginDialog(MainActivity.this, (Activity)MainActivity.this, getSupportFragmentManager());
                            }
                            break;
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });


        // Attach the page change listener inside the activity
        vpPager.addOnPageChangeListener( new LockableViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                Fragment fragment = ((PageAdapter)vpPager.getAdapter()).getFragment(position);

                if (fragment != null)
                {

                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(vpPager.getCurrentItem() == 0) {
            if (userMode == UserMode.DETAILS_VIEW) {
                //go back to last used listview
                updateFragmentViewState(UserMode.LIST_VIEW);
                ChangeFragmentInterface changeFragmentInterface = (ChangeFragmentInterface) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.list_container + ":" + vpPager.getCurrentItem());
                changeFragmentInterface.SwitchFrag();
            } else if (userMode == UserMode.LIST_VIEW) {
                //go to search activity from listview
                setResult(RESULT_OK);
                finish();
            }
        }
        else{
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
            else
            {
                vpPager.setCurrentItem(0);
            }
        }
    }

    private void updateFragmentViewState(UserMode targetMode){
        if(targetMode == UserMode.LIST_VIEW) {
            userMode = UserMode.LIST_VIEW;
        }
        if(targetMode == UserMode.DETAILS_VIEW) {
            userMode = UserMode.DETAILS_VIEW;
        } else {
            //ignore
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateFragmentViewState(userMode);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showStorageRoom() {

        updateFragmentViewState(UserMode.DETAILS_VIEW);
    }

    @Override
    public void goToMap() {
        vpPager.setCurrentItem(0);
    }


}