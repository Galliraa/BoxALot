package com.example.kenne.box_a_lot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.example.kenne.box_a_lot.fragments.StorageFragment;


import com.example.kenne.box_a_lot.adapters.CustomInfoWindowAdapter;
import com.example.kenne.box_a_lot.models.StorageRoom;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import java.util.List;
import java.util.Map;


public class MapsActivity extends AppCompatActivity implements GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        StorageFragment.OnFragmentInteractionListener
{

    Map<String, StorageRoom> storageroomsOnMap = null;
    SupportMapFragment mapFragment;
    StorageRoom storageRoom = null;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LinearLayout storageContainer;
    private StorageFragment storageFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fragmentManager = getSupportFragmentManager();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        storageContainer = findViewById(R.id.storageContainer);

        storageroomsOnMap = new HashMap<>();

        mFusedLocationClient = new FusedLocationProviderClient(this);

    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);


        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);

        }
        else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Show Sydney on the map.
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                            }
                        }
                    });

        }


    }

    @Override
    public void onCameraIdle() {

        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));

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
                                            MarkerOptions marker = new MarkerOptions()
                                                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                                    .title(document.getString("price"))
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                                            String id = mMap.addMarker(marker).getId();
                                            storageroomsOnMap.put(id, storageRoom);
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


        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        storageFragment = new StorageFragment();

        Bundle storageFragBundle = new Bundle();

        StorageRoom storageroom = storageroomsOnMap.get(marker.getId());

        storageFragBundle.putString("address", storageroom.getAddress());
        storageFragBundle.putString("price", storageroom.getPrice());
        storageFragBundle.putBoolean("available", storageroom.getAvailable());
        storageFragBundle.putBooleanArray("generalInfo", toPrimitiveArray(storageroom.getGeneralInfo()));
        storageFragBundle.putString("storageroomID" , storageroom.getStorageRoomId());
        storageFragBundle.putString("imagePath", "filepath");

        storageFragment.setArguments(storageFragBundle);
        fragmentTransaction.add(R.id.storageContainer, storageFragment);
        fragmentTransaction.commit();

        storageContainer.setVisibility(View.VISIBLE);


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        if(storageContainer.getVisibility() == View.VISIBLE) {
            storageContainer.setVisibility(View.GONE);
            fragmentManager.beginTransaction().remove(storageFragment).commit();
        }
        else
            finish();

    }

    private boolean[] toPrimitiveArray(final List<Boolean> booleanList) {
        final boolean[] primitives = new boolean[booleanList.size()];
        int index = 0;
        for (Boolean object : booleanList) {
            primitives[index++] = object;
        }
        return primitives;
    }


}