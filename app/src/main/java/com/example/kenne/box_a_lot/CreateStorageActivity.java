package com.example.kenne.box_a_lot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kenne.box_a_lot.classes.GeocodingLocation;
import com.example.kenne.box_a_lot.models.StorageRoom;
import com.example.kenne.box_a_lot.other.ViewDialog;
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
import com.google.firebase.storage.UploadTask;

import org.imperiumlabs.geofirestore.GeoFirestore;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CreateStorageActivity extends AppCompatActivity {

    private static final int MAX_PIC_NUMBER = 4;
    private static final int MAX_CHECK_NUMBER = 9;

    private FirebaseStorage FBstorage = FirebaseStorage.getInstance();
    private ViewDialog viewDialog;
    private EditText countryET;
    private EditText cityET;
    private EditText addressET;
    private EditText priceET;
    private EditText descET;
    private EditText sizeET;
    private Button submitBtn;

    private StorageRoom storageRoom = new StorageRoom();
    private ImageView[] myImageViewArray = new ImageView[MAX_PIC_NUMBER];
    private CheckBox[]  myCheckBoxArray = new CheckBox[9];
    private int counter = 0;
    List<Double> locationAddress = new ArrayList<>();
    List<Boolean> generalInfo = new ArrayList<>();
    List<String> addressStrings = new ArrayList<>();

    private static final int LOGIN_REQUEST = 1;
    private static final int[] PHOTO_CAPTURE_REQUEST_CODE = new int[]{2,3,4,5};

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_storage);

        viewDialog = new ViewDialog(this);

        countryET = findViewById(R.id.countryET);
        cityET = findViewById(R.id.cityET);
        addressET = findViewById(R.id.addressET);
        priceET = findViewById(R.id.priceET);
        descET = findViewById(R.id.descET);
        sizeET = findViewById(R.id.sizeET);
        myImageViewArray[0] = findViewById(R.id.ph00);
        myImageViewArray[1] = findViewById(R.id.ph01);
        myImageViewArray[2] = findViewById(R.id.ph02);
        myImageViewArray[3] = findViewById(R.id.ph03);
        myCheckBoxArray[0] = findViewById(R.id.checkAllTimeAcces);
        myCheckBoxArray[1] = findViewById(R.id.checkEasyAcces);
        myCheckBoxArray[2] = findViewById(R.id.checkElevator);
        myCheckBoxArray[3] = findViewById(R.id.checkOnlyAfterSix);
        myCheckBoxArray[4] = findViewById(R.id.checkOwnEntrance);
        myCheckBoxArray[5] = findViewById(R.id.checkOwnKey);
        myCheckBoxArray[6] = findViewById(R.id.checkParking);
        myCheckBoxArray[7] = findViewById(R.id.checkSharedRoom);
        myCheckBoxArray[8] = findViewById(R.id.checkShortNotice);

        submitBtn = findViewById(R.id.submitStorageBtn);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Not signed in, launch the Sign In activity
            startActivityForResult(new Intent(this, LoginActivity.class),LOGIN_REQUEST);

            return;
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

        if(requestCode == LOGIN_REQUEST) {
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
        for(int i = 0; i < MAX_PIC_NUMBER; i++) {
            if (requestCode == PHOTO_CAPTURE_REQUEST_CODE[i]) {
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    myImageViewArray[i].setImageBitmap(bitmap);
                } else {
                    Toast.makeText(this,
                            "The photo could not be retrieved",
                            Toast.LENGTH_LONG)
                            .show();

                }
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

            for(int i = 0; i < MAX_CHECK_NUMBER; i++)
                generalInfo.add(myCheckBoxArray[i].isChecked());

            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress.add(bundle.getDoubleArray("LatLng")[0]);
                    locationAddress.add(bundle.getDoubleArray("LatLng")[1]);

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
            Drawable.ConstantState defaultPic = getResources().getDrawable(R.drawable.add_icon).getConstantState();
                for(int i = 0; i < MAX_PIC_NUMBER; i++) {
                    if(myImageViewArray[i].getDrawable().getConstantState() != defaultPic) {
                        final String path = FirebaseAuth.getInstance()
                                .getCurrentUser().getUid() + "/" + UUID.randomUUID() + ".PNG";
                        myImageViewArray[i].setDrawingCacheEnabled(true);
                        Bitmap bitmap = myImageViewArray[i].getDrawingCache();
                        Bitmap resized = getResizedBitmap(bitmap, 300);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resized.compress(Bitmap.CompressFormat.PNG, 0, baos);
                        byte[] data = baos.toByteArray();

                        final StorageReference firePicRef = FBstorage.getReference(path);
                        counter++;
                        firePicRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                firePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        counter--;
                                        //Do what you want with the url
                                        picrefs.add(uri.toString());
                                        if(counter == 0)
                                        {
                                            storageRoom.setPicRef(picrefs);
                                            storageRoom.setCoordinates(locationAddress);
                                            storageRoom.setAddress(addressStrings);
                                            storageRoom.setUserId(currentUser);
                                            storageRoom.setPrice(priceET.getText().toString());
                                            storageRoom.setAvailable(true);
                                            storageRoom.setGeneralInfo(generalInfo);
                                            storageRoom.setChatIds(null);
                                            storageRoom.setDesc(descET.getText().toString());
                                            storageRoom.setSize(sizeET.getText().toString());

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
                                                                        viewDialog.hideDialog();
                                                                        System.out.println("Location saved on server successfully!");
                                                                        Intent intent = new Intent(getApplicationContext(), StorageroomViewer.class);
                                                                        intent.putExtra("storageroom", (Serializable) storageRoom.StorageMap);
                                                                        CreateStorageActivity.this.finish();
                                                                        startActivity(intent);
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
                                });
                            }
                        });
                    }
                }




            }
        }
    }

    private void setupButtonClickListener() {
        mAuth = FirebaseAuth.getInstance();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                GeocodingLocation locationAddress = new GeocodingLocation();

                viewDialog.showDialog();

                locationAddress.getAddressFromLocation(countryET.getText().toString() + " , "
                                + cityET.getText().toString() + " , "
                                + addressET.getText().toString(),
                                getApplicationContext(), new GeocoderHandler());
            }
        });

    }

    private void setupImageClickListener() {
        mAuth = FirebaseAuth.getInstance();

        myImageViewArray[0].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, PHOTO_CAPTURE_REQUEST_CODE[0]);
            }
        });
        myImageViewArray[1].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, PHOTO_CAPTURE_REQUEST_CODE[1]);
            }
        });
        myImageViewArray[2].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, PHOTO_CAPTURE_REQUEST_CODE[2]);
            }
        });
        myImageViewArray[3].setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, PHOTO_CAPTURE_REQUEST_CODE[3]);
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