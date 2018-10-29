package com.example.kenne.box_a_lot.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.interfaces.ChangeFragmentInterface;
import com.example.kenne.box_a_lot.models.StorageRoom;

public class MapsRootFragment extends Fragment implements ChangeFragmentInterface{

    private static final String TAG = "RootFragment";
    private static String FRAG_TAG = null;
    private static final String LIST_TAG = "ListFragment";
    private static final String MAPS_TAG = "MapsFragment";
    private static MapsFragment mapsFragment = new MapsFragment();
    private static StorageRoomFragment storageRoomFragment = new StorageRoomFragment();
    private FloatingActionButton listFab;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_root_maps, container, false);

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        /*
         * When this container fragment is created, we fill it with our first
         * "real" fragment
         */
        transaction.replace(R.id.root_frame, mapsFragment);
        FRAG_TAG = MAPS_TAG;

        transaction.commit();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        listFab = view.findViewById(R.id.listviewFabBtn);
        listFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                switch (FRAG_TAG){

                    case MAPS_TAG:
                        transaction.replace(R.id.root_frame, storageRoomFragment, LIST_TAG);
                        listFab.setImageResource(R.drawable.ic_dialog_map);
                        FRAG_TAG = LIST_TAG;
                    break;

                    case LIST_TAG:
                        transaction.replace(R.id.root_frame, mapsFragment, MAPS_TAG);
                        listFab.setImageResource(R.drawable.ic_dialog_dialer);
                        FRAG_TAG = MAPS_TAG;
                    break;
                }
                transaction.commit();
            }
        });

    }



    public void SwitchFrag() {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        switch (FRAG_TAG){

            case MAPS_TAG:
                transaction.replace(R.id.root_frame, mapsFragment, MAPS_TAG);
                listFab.setImageResource(R.drawable.ic_dialog_dialer);
                break;

            case LIST_TAG:
                transaction.replace(R.id.root_frame, storageRoomFragment, LIST_TAG);
                listFab.setImageResource(R.drawable.ic_dialog_map);
                break;
        }
        transaction.commit();
        /*
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) listFab.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        listFab.setLayoutParams(p);*/
        listFab.setVisibility(View.VISIBLE);
    }
}
