package com.example.kenne.box_a_lot.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.adapters.CustomInfoWindowAdapter;
import com.example.kenne.box_a_lot.interfaces.UiUpdateInterface;
import com.example.kenne.box_a_lot.interfaces.UpdateAble;
import com.example.kenne.box_a_lot.models.StorageRoom;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class MapsFragment extends Fragment implements GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        StorageFragment.OnFragmentInteractionListener,
        UpdateAble {

    private List<String> markerMap;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private FragmentActivity myContext;
    private UiUpdateInterface uiUpdateInterface;
    private FloatingActionButton listFab;
    private LatLng lastSearchLocaton = null;
    private double lastRadius = 0;


    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        Object mf = getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment = (SupportMapFragment) mf;
        mapFragment.getMapAsync(this);

        listFab = (FloatingActionButton) getActivity().findViewById(R.id.listviewFabBtn);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mFusedLocationClient = new FusedLocationProviderClient(getActivity());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        markerMap = new ArrayList<>();

        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity()));
        mMap.setOnInfoWindowClickListener(this);



        if ( ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        else {
            if(lastSearchLocaton == null) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Show Sydney on the map.

                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    mMap.moveCamera(CameraUpdateFactory
                                            .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));
                                    // Logic to handle location object
                                }
                            }
                        });
            }
            else{
                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(lastSearchLocaton, (float) lastRadius));
            }
            List<MarkerOptions> markers = ((MapsRootFragment)getParentFragment()).getMarkerArray();
            if(markers != null)
            {
                for(int i = 0; i <markers.size(); i++)
                    markerMap.add(mMap.addMarker(markers.get(i)).getId());
            }
        }


    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        int StorageRoomNumber = Integer.parseInt(marker.getId().toString().replaceAll("\\D+",""))-
                Integer.parseInt(markerMap.get(0).toString().replaceAll("\\D+",""));
        StorageRoom storageroom = StorageRoom.ITEMS.get(StorageRoomNumber);
        FragmentTransaction trans = getFragmentManager()
                .beginTransaction();

        StorageFragment storageFragment = new StorageFragment();
        trans.replace(R.id.root_frame, storageFragment);

        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);

        trans.commit();
        storageFragment.setStorage(storageroom);
        uiUpdateInterface.showStorageRoom();

        listFab.setVisibility(View.GONE);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
        try {
            uiUpdateInterface = (UiUpdateInterface) activity;
        }catch (ClassCastException ex)
        {
            throw new ClassCastException(activity.toString() + " must implement EventSelectorInterface");
        }
    }

    public void selectionChanged(MarkerOptions marker){

        markerMap.add(mMap.addMarker(marker).getId());
    }

    public void clearMap(){
        markerMap.clear();
        mMap.clear();
    }

    public void moveToLocation(LatLng latLng, double radius){
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                latLng, 10);
        mMap.animateCamera(location);
        lastRadius = radius;
        lastSearchLocaton = latLng;
    }

    @Override
    public void update() {

    }
}
