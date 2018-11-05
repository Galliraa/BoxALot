package com.example.kenne.box_a_lot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kenne.box_a_lot.classes.GeocodingLocation;
import com.example.kenne.box_a_lot.models.StorageRoom;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.imperiumlabs.geofirestore.GeoFirestore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateStorageActivity extends AppCompatActivity {

    private FirebaseStorage FBstorage = FirebaseStorage.getInstance();

    private EditText countryET;
    private EditText cityET;
    private EditText addressET;
    private EditText priceET;
    private ImageView storageIV;
    private Button submitBtn;
    private StorageRoom storageRoom = new StorageRoom();

    private static final int SIGN_IN_REQUEST_CODE = 1;
    private static final int PHOTO_CAPTURE_REQUEST_CODE = 2;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_storage);

        countryET = findViewById(R.id.countryET);
        cityET = findViewById(R.id.cityET);
        addressET = findViewById(R.id.addressET);
        priceET = findViewById(R.id.priceET);
        storageIV = findViewById(R.id.ph00);
        submitBtn = findViewById(R.id.submitStorageBtn);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();

            // Load chat room contents
            setupButtonClickListener();
            setupImageClickListener();
        }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                setupButtonClickListener();
                setupImageClickListener();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }
        if(requestCode == PHOTO_CAPTURE_REQUEST_CODE){
            if(resultCode == RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                storageIV.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this,
                        "The photo could not be retrieved",
                        Toast.LENGTH_LONG)
                        .show();

            }
        }

    }

        @Override
        public void onStart() {
            super.onStart();

        }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            List<Double> locationAddress = new ArrayList<>();
            List<Boolean> generalInfo = new ArrayList<>();
            generalInfo.add(true);
            generalInfo.add(false);
            generalInfo.add(true);
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress.add(bundle.getDoubleArray("LatLng")[0]);
                    locationAddress.add(bundle.getDoubleArray("LatLng")[1]);

                    break;
                default:
                    locationAddress = null;
            }
            // Create a new user with a first and last name
            if(locationAddress!=null) {


                String path = FirebaseAuth.getInstance()
                        .getCurrentUser().getUid() + "/" + UUID.randomUUID() + ".PNG";
                storageIV.setDrawingCacheEnabled(true);
                Bitmap bitmap = storageIV.getDrawingCache();
                Bitmap resized = getResizedBitmap(bitmap, 300);
                //Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int)(160*(getResources().getDisplayMetrics().density)), (int)(160*(getResources().getDisplayMetrics().density)), true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resized.compress(Bitmap.CompressFormat.PNG, 0, baos);

                byte[] data = baos.toByteArray();

                StorageReference firePicRef = FBstorage.getReference(path);
                firePicRef.putBytes(data);
                List<String> picrefs = new ArrayList<String>();
                picrefs.add(path);
                storageRoom.setPicRef(picrefs);

                storageRoom.setCoordinates(locationAddress);
                storageRoom.setAddress(countryET.getText().toString() + " , " + cityET.getText().toString() + " , " + addressET.getText().toString());
                storageRoom.setUserId(currentUser);
                storageRoom.setPrice(priceET.getText().toString());
                storageRoom.setAvailable(true);
                //storageRoom.setPicRef(R.drawable.garage_depot);
                storageRoom.setGeneralInfo(generalInfo);
                storageRoom.setChatIds(null);

                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                        .setTimestampsInSnapshotsEnabled(true)
                        .build();
                db.setFirestoreSettings(settings);

                // Add a new document with a generated ID
                db.collection("StorageRooms")
                        .add(storageRoom.getStorageMap())
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                CollectionReference geoFirestoreRef = db.collection("StorageRooms");
                                GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);

                                geoFirestore = new GeoFirestore(db.collection("GeoFire"));
                                geoFirestore.setLocation(documentReference.getId(), new GeoPoint(storageRoom.getCoordinates().get(0),storageRoom.getCoordinates().get(1)), new GeoFirestore.CompletionListener() {
                                    @Override
                                    public void onComplete(Exception exception) {
                                        if (exception == null){
                                            System.out.println("Location saved on server successfully!");
                                        }
                                    }
                                });
                                //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Log.w(TAG, "Error adding document", e);
                            }
                        });


            }
        }
    }

    private void setupButtonClickListener() {
        mAuth = FirebaseAuth.getInstance();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                GeocodingLocation locationAddress = new GeocodingLocation();
                locationAddress.getAddressFromLocation(countryET.getText().toString() + " , "
                                + cityET.getText().toString() + " , "
                                + addressET.getText().toString(),
                                getApplicationContext(), new GeocoderHandler());
            }
        });

    }

    private void setupImageClickListener() {
        mAuth = FirebaseAuth.getInstance();
        storageIV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, PHOTO_CAPTURE_REQUEST_CODE);
            }
        });

    }

    /**
     * reduces the size of the image
     * @param image
     * @param maxSize
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    }