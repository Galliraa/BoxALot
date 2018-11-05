package com.example.kenne.box_a_lot.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.adapters.CustomInfoWindowAdapter;
import com.example.kenne.box_a_lot.interfaces.UiUpdateInterface;
import com.example.kenne.box_a_lot.models.StorageRoom;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.GeoQueryEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapsFragment extends Fragment implements GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        StorageFragment.OnFragmentInteractionListener {

    String Trash = "Garbage";
    Map<String, StorageRoom> storageroomsOnMap = null;
    SupportMapFragment mapFragment;
    StorageRoom storageRoom = null;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private FragmentActivity myContext;
    private UiUpdateInterface uiUpdateInterface;
    private PlaceAutocompleteFragment placeAutoComplete;
    private FloatingActionButton listFab;

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

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listFab = (FloatingActionButton) getActivity().findViewById(R.id.listviewFabBtn);
        placeAutoComplete = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 10));
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                        place.getLatLng(), 10);
                mMap.animateCamera(location);
                Log.d("Maps", "Place selected: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        storageroomsOnMap = new HashMap<>();
        StorageRoom.ITEMS = new ArrayList<>();
        mFusedLocationClient = new FusedLocationProviderClient(getActivity());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);


        if ( ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);

        }
        else {
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


    }

    @Override
    public void onCameraIdle() {

        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity()));

        mMap.setOnInfoWindowClickListener(this);

        double radius = 111.19*(bounds.northeast.latitude - bounds.getCenter().latitude);
        //if(radius < 10) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        CollectionReference geoFirestoreRef = FirebaseFirestore.getInstance().collection("GeoFire");
        GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);

        GeoPoint geoPoint = new GeoPoint(bounds.getCenter().latitude, bounds.getCenter().longitude);
        // creates a new query around map coordinate with a radius of center to maps top
        GeoQuery geoQuery = geoFirestore.queryAtLocation(geoPoint, radius);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String documentID, final GeoPoint location) {
                DocumentReference docRef = db.collection("StorageRooms").document(documentID);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                boolean setMarker = true;
                                Iterator it = storageroomsOnMap.entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry pair = (Map.Entry)it.next();
                                    if(((StorageRoom)pair.getValue()).getStorageRoomId().equals(document.getId()))
                                        setMarker = false;
                                }


                                if(setMarker) {
                                    storageRoom = new StorageRoom();
                                    storageRoom.setStorageMap(document);
                                    String picRef = null;
                                    if(storageRoom.getPicRef() != null)
                                        picRef = storageRoom.getPicRef().get(0);

                                    MarkerOptions marker = new MarkerOptions()
                                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                            .title(document.getString("price"))
                                            .snippet(picRef)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                                    String id = mMap.addMarker(marker).getId();
                                    storageroomsOnMap.put(id, storageRoom);
                                    StorageRoom.ITEMS.add(storageRoom);
                                }
                                //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                //Log.d(TAG, "No such document");
                            }
                        } else {
                            //Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

                System.out.println(String.format("Document %s entered the search area at [%f,%f]", documentID, location.getLatitude(), location.getLongitude()));
            }

            @Override
            public void onKeyExited(String documentID) {
                System.out.println(String.format("Document %s is no longer in the search area", documentID));
            }

            @Override
            public void onKeyMoved(String documentID, GeoPoint location) {
                System.out.println(String.format("Document %s moved within the search area to [%f,%f]", documentID, location.getLatitude(), location.getLongitude()));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(Exception exception) {
                System.err.println("There was an error with this query: " + exception.getLocalizedMessage());
            }
        });
    }

    //}

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

        StorageRoom storageroom = storageroomsOnMap.get(marker.getId());
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
}
