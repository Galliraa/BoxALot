package com.example.kenne.box_a_lot.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.classes.GeocodingLocation;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class CreateStoragePage3Fragment extends CreateStoragePageFragment implements OnMapReadyCallback {

    private TextView postalcodeTV;
    private TextView cityTV;
    private TextView addressTV;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient mFusedLocationClient;
    private PlaceAutocompleteFragment placeAutoComplete;

    List<Double> locationAddress = new ArrayList<>();
    List<String> addressStrings = new ArrayList<>();

    private Place lastPlace = null;

    private LatLng lastSearchLocaton = null;
    private double lastRadius = 0;
    private boolean searchComplete = false;

    public CreateStoragePage3Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFusedLocationClient = new FusedLocationProviderClient(getActivity());
        postalcodeTV = view.findViewById(R.id.fragment_create_room_page3_TextView_postalcode);
        cityTV = view.findViewById(R.id.fragment_create_room_page3_TextView_city);
        addressTV = view.findViewById(R.id.fragment_create_room_page3_TextView_address);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_storageroom_page3, container, false);
        Object mf = getChildFragmentManager().findFragmentById(R.id.fragment_create_room_page3_map);
        mapFragment = (SupportMapFragment) mf;
        mapFragment.getMapAsync(this);

        return v;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

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
        }

        Object pac = getActivity().getFragmentManager().findFragmentById(R.id.fragment_create_room_page3_place_autocomplete);
        placeAutoComplete = (PlaceAutocompleteFragment) pac;

        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.clear();
                if (place != lastPlace) {
                    lastPlace = place;

                    LatLng searchLocation = place.getLatLng();

                    float radius = 15;

                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                            searchLocation, radius);
                    mMap.animateCamera(location);
                    lastRadius = radius;
                    lastSearchLocaton = searchLocation;

                    MarkerOptions marker = new MarkerOptions()
                            .position(searchLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                    mMap.addMarker(marker);
                    place.getAddress();

                    GeocodingLocation locationAddress = new GeocodingLocation();

                    locationAddress.getAddressFromLocation(place.getAddress().toString(),
                            getApplicationContext(), new GeocoderHandler());

                }
            }

            @Override
            public void onError(Status status) {

            }
        });


    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress.add(bundle.getDoubleArray("LatLng")[0]);
                    locationAddress.add(bundle.getDoubleArray("LatLng")[1]);

                    addressStrings = new ArrayList<>();
                    String[] tempAddressStrings = new String[5];
                    tempAddressStrings = bundle.getStringArray("address");
                    for(int i = 0; i < tempAddressStrings.length; i++)
                    {
                        addressStrings.add(tempAddressStrings[i]);
                    }
                    break;
                default:
                    locationAddress = null;
            }

            final List<String> picrefs = new ArrayList<String>();
            // Create a new user with a first and last name
            if(locationAddress!=null) {

                searchComplete = true;
                postalcodeTV.setText(addressStrings.get(1));
                cityTV.setText(addressStrings.get(2));
                if(addressStrings.get(3) != null) {
                    if (addressStrings.get(4) == null)
                        addressTV.setText(addressStrings.get(3));
                    else
                        addressTV.setText(addressStrings.get(3) + ", " + addressStrings.get(4));
                }
            }
        }
    }

    @Override
    public String validateUserInput() {

        if(searchComplete != true)
            return "You must enter an address";

        return null;
    }
}
