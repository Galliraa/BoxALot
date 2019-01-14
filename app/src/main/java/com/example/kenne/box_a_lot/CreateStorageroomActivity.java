package com.example.kenne.box_a_lot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kenne.box_a_lot.adapters.CreateStorageroomPagerAdapter;
import com.example.kenne.box_a_lot.customViews.LockableViewPager;
import com.example.kenne.box_a_lot.fragments.CreateStoragePage4Fragment;
import com.example.kenne.box_a_lot.fragments.CreateStoragePageFragment;
import com.example.kenne.box_a_lot.models.StorageRoom;
import com.example.kenne.box_a_lot.models.User;
import com.example.kenne.box_a_lot.other.ViewDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class CreateStorageroomActivity extends AppCompatActivity {

    private static final String MESSAGES_CHILD = "Users/";

    private LockableViewPager vpPager;
    private TabLayout VPHeader;
    final int MAX_PIC_NUMBER = 4;
    private static final int LOGIN_REQUEST = 1;

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth auth;

    private FirebaseStorage FBstorage = FirebaseStorage.getInstance();
    private int counter = 0;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private CreateStorageroomPagerAdapter adapterViewPager;

    private ViewDialog viewDialog;

    private Button nextBtn;
    private Button previousBtn;
    private TextView errorTV;

    private StorageRoom storageroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_storageroom);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        viewDialog = new ViewDialog(this);

        errorTV = findViewById(R.id.create_storageroom_error_TV);
        VPHeader = findViewById(R.id.create_storageroom_pager_header);
        vpPager = (LockableViewPager) findViewById(R.id.create_storageroom_viewpager);
        VPHeader.setupWithViewPager(vpPager, true);
        adapterViewPager = new CreateStorageroomPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setOffscreenPageLimit(4);

        nextBtn = findViewById(R.id.create_storageroom_next_button);
        previousBtn = findViewById(R.id.create_storageroom_previous_button);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String error = ((CreateStoragePageFragment)(((CreateStorageroomPagerAdapter)vpPager.getAdapter()).getFragment(vpPager.getCurrentItem()))).validateUserInput();
                if(error != null){

                    errorTV.setText(error);
                    return;
                }

                errorTV.setText("");


                    switch(vpPager.getCurrentItem()){
                    case 0:
                        previousBtn.setVisibility(View.VISIBLE);
                        vpPager.setCurrentItem(vpPager.getCurrentItem()+1);
                        break;
                    case 1:
                        vpPager.setCurrentItem(vpPager.getCurrentItem()+1);
                        break;
                    case 2:
                        vpPager.setCurrentItem(vpPager.getCurrentItem()+1);
                        nextBtn.setText(R.string.createStorageNextBtnCreateTxt);
                        break;
                    case 3:
                        createStorageroom();
                        break;

                }
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vpPager.getCurrentItem() == 1)
                {
                    previousBtn.setVisibility(View.GONE);
                }
                else if(vpPager.getCurrentItem() == 3){
                    nextBtn.setText(R.string.createStorageNextBtnTxt);
                }

                vpPager.setCurrentItem(vpPager.getCurrentItem()-1);
                errorTV.setText("");
            }
        });
    }

    private boolean createStorageroom(){

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(mFirebaseUser != null) {
            viewDialog.showDialog();
            storageroom = new StorageRoom();
            for (int i = 0; i < vpPager.getAdapter().getCount(); i++) {
                ((CreateStoragePageFragment) (((CreateStorageroomPagerAdapter) vpPager.getAdapter()).getFragment(i))).getData(storageroom);
            }

            Drawable.ConstantState defaultPic = getResources().getDrawable(R.drawable.ic_camera_alt_black_24dp).getConstantState();

            final List<String> picrefs = new ArrayList<String>();

            ImageView[] myImageViewArray = new ImageView[MAX_PIC_NUMBER];

            myImageViewArray = ((CreateStoragePage4Fragment) (((CreateStorageroomPagerAdapter) vpPager.getAdapter()).getFragment(3))).getImageviewArray();

            for (int i = 0; i < MAX_PIC_NUMBER; i++) {
                if (myImageViewArray[i].getDrawable().getConstantState() != defaultPic) {
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
                                    if (counter == 0) {
                                        storageroom.setPicRef(picrefs);
                                        storageroom.setUserId(null);
                                        storageroom.setAvailable(true);
                                        storageroom.setChatIds(null);
                                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                                                .setTimestampsInSnapshotsEnabled(true)
                                                .build();
                                        db.setFirestoreSettings(settings);

                                        // Add a new document with a generated ID
                                        db.collection("StorageRooms")
                                                .add(storageroom.getStorageMap())
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        CollectionReference geoFirestoreRef = db.collection("StorageRooms");
                                                        GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);

                                                        geoFirestore = new GeoFirestore(db.collection("GeoFire"));
                                                        geoFirestore.setLocation(documentReference.getId(), new GeoPoint(storageroom.getCoordinates().get(0), storageroom.getCoordinates().get(1)), new GeoFirestore.CompletionListener() {
                                                            @Override
                                                            public void onComplete(Exception exception) {
                                                                if (exception == null) {
                                                                    User user = new User();
                                                                    List<String> roomIds = new ArrayList<String>();
                                                                    roomIds.add(documentReference.getId());
                                                                    user.setStorageroomIds(roomIds);

                                                                    mFirebaseDatabaseReference.child("MESSAGES_CHILD")
                                                                            .child(auth.getCurrentUser().getUid())
                                                                            .child("storageroomId")
                                                                            .setValue(documentReference.getId());


                                                                    mFirebaseDatabaseReference.child(MESSAGES_CHILD + "/" + auth.getCurrentUser().getUid()).updateChildren(user.getUserMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            viewDialog.hideDialog();
                                                                            System.out.println("Location saved on server successfully!");
                                                                            Intent intent = new Intent(getApplicationContext(), StorageroomViewer.class);
                                                                            intent.putExtra("storageroom", (Serializable) storageroom.StorageMap);
                                                                            CreateStorageroomActivity.this.finish();
                                                                            startActivity(intent);
                                                                        }
                                                                    });

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
        else
        {
            startActivityForResult(new Intent(this, LoginActivity.class),LOGIN_REQUEST);
        }
        return true;
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
