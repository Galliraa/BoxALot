package com.example.kenne.box_a_lot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.kenne.box_a_lot.adapters.PageAdapter;
import com.example.kenne.box_a_lot.customViews.LockableViewPager;
import com.example.kenne.box_a_lot.fragments.CreateStoragePage1Fragment;
import com.example.kenne.box_a_lot.fragments.CreateStoragePage2Fragment;
import com.example.kenne.box_a_lot.fragments.MapsRootFragment;
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


public class MainActivity extends AppCompatActivity implements UiUpdateInterface, StorageRoomFragment.OnListFragmentInteractionListener {


    @Override
    public void onListFragmentInteraction(StorageRoom item) {

    }

    private boolean showTabBool;
    private androidx.appcompat.app.ActionBar actionBar;
    private enum UserMode {LIST_VIEW, DETAILS_VIEW, STORAGEROOM_VIEW}
    private UserMode userMode;
    private LockableViewPager vpPager;
    private LinearLayout mapsRootFragment;
    private TabLayout VPHeader;

    private static final int LOGIN_REQUEST = 1;
    private static final int USERPAGE = 3;

    PageAdapter adapterViewPager;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VPHeader = findViewById(R.id.pager_header);

        actionBar = getSupportActionBar();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        userMode = UserMode.LIST_VIEW;  //default


        vpPager = (LockableViewPager) findViewById(R.id.list_container);

        adapterViewPager = new PageAdapter(getSupportFragmentManager(), getApplicationContext());



        if(mFirebaseUser != null) {
            showTabBool = true;
            vpPager.setOffscreenPageLimit(4);
            adapterViewPager.addFragment(new MapsRootFragment(),0);
            adapterViewPager.addFragment(new CreateStoragePage2Fragment(),1);
            adapterViewPager.addFragment(new CreateStoragePage1Fragment(),2);
            adapterViewPager.addFragment(new UserFragment(),3);
            VPHeader.setVisibility(View.VISIBLE);
        }
        else {
            showTabBool = false;
            vpPager.setOffscreenPageLimit(1);
            adapterViewPager.addFragment(new MapsRootFragment(),0);
            VPHeader.setVisibility(View.GONE);
        }

        vpPager.setAdapter(adapterViewPager);




            if (userMode == null) {

            }

            // Attach the page change listener inside the activity
            vpPager.addOnPageChangeListener(new LockableViewPager.OnPageChangeListener() {

                int currentPosition = 0;

                // This method will be invoked when a new page becomes selected.
                @Override
                public void onPageSelected(int position) {
                    invalidateFragmentMenus(position);

                    Fragment fragment = ((PageAdapter)vpPager.getAdapter()).getFragment(position);

                    if(position != 0 && fragment != null && mFirebaseUser != null)
                    {
                        fragment.onResume();
                    }
                    else if(position == 0 && showTabBool == false && adapterViewPager.getCount() != 1)
                    {
                        VPHeader.setVisibility(View.GONE);
                        adapterViewPager.removeFragment(3);
                        adapterViewPager.removeFragment(2);
                        adapterViewPager.removeFragment(1);
                        adapterViewPager.notifyDataSetChanged();
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

    private void invalidateFragmentMenus(int position) {
        for (int i = 0; i < adapterViewPager.getCount(); i++) {
            adapterViewPager.getItem(i).setHasOptionsMenu(i == position);
        }
        invalidateOptionsMenu(); //or respectively its support method.
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

                this.showTab();
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
    public void goToMap(boolean loggedIn) {
        if(!loggedIn)
            showTabBool = false;
        if(vpPager.getCurrentItem() != 0)
            vpPager.setCurrentItem(0);
        else{
            VPHeader.setVisibility(View.GONE);
            adapterViewPager.removeFragment(3);
            adapterViewPager.removeFragment(2);
            adapterViewPager.removeFragment(1);
            adapterViewPager.notifyDataSetChanged();
        }

    }

    @Override
    public void loginFailed() {
        goToMap(false);
    }

    @Override
    public void showTab() {

        showTabBool = true;
        adapterViewPager.addFragment(new CreateStoragePage2Fragment(),1);
        adapterViewPager.addFragment(new CreateStoragePage1Fragment(),2);
        adapterViewPager.addFragment(new UserFragment(),3);
        adapterViewPager.notifyDataSetChanged();
        VPHeader.setVisibility(View.VISIBLE);
    }
}