package com.example.kenne.box_a_lot.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.kenne.box_a_lot.MainActivity;
import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.adapters.PlaceArrayAdapter;
import com.example.kenne.box_a_lot.interfaces.ChangeFragmentInterface;
import com.example.kenne.box_a_lot.models.StorageRoom;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MapsRootFragment extends Fragment implements ChangeFragmentInterface  {

    private static final String TAG = "RootFragment";
    private static String FRAG_TAG = "MapsFragment";
    private static final String LIST_TAG = "ListFragment";
    private static final String MAPS_TAG = "MapsFragment";
    private static MapsFragment mapsFragment = new MapsFragment();
    private static StorageRoomFragment storageRoomFragment = new StorageRoomFragment();
    private FloatingActionButton listFab;
    private List<MarkerOptions> markerArray = new ArrayList<>();
    private PlaceAutocompleteFragment placeAutoComplete;
    private StorageRoom storageRoom = null;
    private Place lastPlace = null;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    //private GoogleApi mGoogleApiClient;
    private PlaceDetectionClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        mGoogleApiClient = Places.getPlaceDetectionClient(getActivity());

        mPlaceArrayAdapter = new PlaceArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);

        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.fragment_root_maps, container, false);

        FragmentTransaction transaction = getChildFragmentManager()
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
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                switch (FRAG_TAG){

                    case MAPS_TAG:
                        transaction.replace(R.id.root_frame, storageRoomFragment, LIST_TAG);
                        listFab.setImageResource(R.drawable.ic_dialog_map);
                        FRAG_TAG = LIST_TAG;
                        transaction.commit();
                    break;

                    case LIST_TAG:
                        transaction.replace(R.id.root_frame, mapsFragment, MAPS_TAG);
                        listFab.setImageResource(R.drawable.ic_dialog_dialer);
                        FRAG_TAG = MAPS_TAG;
                        transaction.commit();

                    break;
                }

            }
        });
        Object pac = getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete = (PlaceAutocompleteFragment) pac;
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (place != lastPlace) {
                    lastPlace = place;
                    StorageRoom.ITEMS.clear();
                    markerArray = new ArrayList<>();
                    mapsFragment.clearMap();
                    //storage
                    LatLng searchLocation = place.getLatLng();

                    double radius = 10;//111.19*(searchLocation.northeast.latitude - searchLocation.getCenter().latitude);

                    mapsFragment.moveToLocation(searchLocation, radius);

                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                            .setTimestampsInSnapshotsEnabled(true)
                            .build();
                    db.setFirestoreSettings(settings);

                    CollectionReference geoFirestoreRef = FirebaseFirestore.getInstance().collection("GeoFire");
                    GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);

                    GeoPoint geoPoint = new GeoPoint(searchLocation.latitude, searchLocation.longitude);
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
                                        boolean storageRoomIsOnMap = false;
                                        if (document.exists()) {
                                            {

                                                for(int i = 0; i < StorageRoom.ITEMS.size(); i++)
                                                    if(StorageRoom.ITEMS.get(i).getStorageRoomId() == document.getId()){
                                                        storageRoomIsOnMap = true;
                                                        break;
                                                    }
                                                }
                                            if(storageRoomIsOnMap != true) {
                                                storageRoom = new StorageRoom();
                                                storageRoom.setStorageMap(document);
                                                String picRef = null;
                                                if (storageRoom.getPicRef() != null)
                                                    picRef = storageRoom.getPicRef().get(0);

                                                MarkerOptions marker = new MarkerOptions()
                                                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                                                        .title(document.getString("price"))
                                                        .snippet(picRef)
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));


                                                markerArray.add(marker);
                                                StorageRoom.ITEMS.add(storageRoom);

                                                switch (FRAG_TAG) {
                                                    case MAPS_TAG:
                                                        mapsFragment.selectionChanged(marker);
                                                        break;

                                                    case LIST_TAG:
                                                        storageRoomFragment.SelectionChanged();
                                                        break;
                                                }
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
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getContext(), "Something went wrong, try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        androidx.appcompat.app.ActionBar actionBar=((MainActivity)activity).getSupportActionBar();
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {


        androidx.appcompat.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar(); // you can use ABS or the non-bc ActionBar
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_HOME_AS_UP); // what's mainly important here is DISPLAY_SHOW_CUSTOM. the rest is optional

        LayoutInflater inflater1 = (LayoutInflater)((AppCompatActivity) getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflate the view that we created before
        View v = inflater1.inflate(R.layout.search_bar, null);
        AutoCompleteTextView textView =  (AutoCompleteTextView) v.findViewById(R.id.editText1);

        //textView.setAdapter(searchAdapter);

        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // do something when the user clicks
            }
        });
        actionBar.setCustomView(v);


        inflater.inflate(R.menu.main_menu, menu);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i("", "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i("", "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("", "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            if (attributions != null) {

            }
        }
    };

    public void SwitchFrag() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        switch (FRAG_TAG){

            case MAPS_TAG:
                mapsFragment.clearMap();
                transaction.replace(R.id.root_frame, mapsFragment, MAPS_TAG);
                listFab.setImageResource(R.drawable.ic_dialog_dialer);
                break;

            case LIST_TAG:
                transaction.replace(R.id.root_frame, storageRoomFragment, LIST_TAG);
                listFab.setImageResource(R.drawable.ic_dialog_map);
                break;
        }
        transaction.commit();
        listFab.setVisibility(View.VISIBLE);
    }

    public List<MarkerOptions> getMarkerArray(){
            mapsFragment.clearMap();
            return markerArray;
    }

}
