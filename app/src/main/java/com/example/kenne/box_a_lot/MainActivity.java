
package com.example.kenne.box_a_lot;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.kenne.box_a_lot.customViews.LockableViewPager;
import com.example.kenne.box_a_lot.fragments.StorageRoomFragment;
import com.example.kenne.box_a_lot.interfaces.ChangeFragmentInterface;
import com.example.kenne.box_a_lot.interfaces.UiUpdateInterface;
import com.example.kenne.box_a_lot.models.StorageRoom;
import com.example.kenne.box_a_lot.adapters.PageAdapter;
import java.util.List;


public class MainActivity extends AppCompatActivity implements UiUpdateInterface, StorageRoomFragment.OnListFragmentInteractionListener {

    private final static String EVENT_TAG = "EVENT";
    private final static String RESUME_TAG = "ONRESUME";
    private static final String DETAILS_FRAG = "details_fragment";

    @Override
    public void onListFragmentInteraction(StorageRoom item) {

    }

    private enum UserMode {LIST_VIEW, DETAILS_VIEW, STORAGEROOM_VIEW}
    private List<StorageRoom> storageRooms;
    private UserMode userMode;
    private int selectedEventIndex;
    private LockableViewPager vpPager;

    FragmentPagerAdapter adapterViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vpPager = (LockableViewPager) findViewById(R.id.list_container);

        adapterViewPager = new PageAdapter(getSupportFragmentManager(), getApplicationContext());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setOffscreenPageLimit(3);


            selectedEventIndex = 0;

            if(userMode == null){
                userMode = UserMode.LIST_VIEW;  //default
            }


        // Attach the page change listener inside the activity
        vpPager.addOnPageChangeListener( new LockableViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                Fragment fragment = ((PageAdapter)vpPager.getAdapter()).getFragment(position);

                if (fragment != null)
                {
                   // ((UiUpdateInterface)fragment).updateEvents();
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
        Log.d(RESUME_TAG, "onResume: run");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showStorageRoom() {

        updateFragmentViewState(UserMode.DETAILS_VIEW);
    }


}