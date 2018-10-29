package com.example.kenne.box_a_lot.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kenne.box_a_lot.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter  {

    private FirebaseStorage FBstorage = FirebaseStorage.getInstance();
    private final View mWindow;
    private Context mContext;
    private Marker mMarker;
    ImageView ivStorage;
    private boolean picRetrieved = false;


    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void rendowWindowText(Marker marker, View view){

        if(picRetrieved && mMarker !=null)
            if(!mMarker.getSnippet().equals(marker.getSnippet()))
                picRetrieved = false;

        if (!picRetrieved) {
        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.priceTV);
        ivStorage = view.findViewById(R.id.storageRoomMarkerIV);
            ivStorage.setImageResource(R.drawable.garage_depot);
            picRetrieved=true;

        // Create a storage reference from our app
        mMarker = marker;

        final String picRef = mMarker.getSnippet();
        StorageReference storageRef = FBstorage.getReference().child(picRef);


        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL
                // Pass it to Picasso to download, show in ImageView and caching
                Picasso.with(mContext).load(uri.toString()).fit().centerCrop().into(ivStorage,new Callback(){
                    @Override
                    public void onSuccess() {
                        mMarker.showInfoWindow();
                        picRetrieved = true;
                    }

                    @Override
                    public void onError() {
                        picRetrieved = true;
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        if(!title.equals("")) {
            tvTitle.setText(title);
        }
        }
    }


    @Override
    public View getInfoWindow(Marker marker) {
        rendowWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        rendowWindowText(marker, mWindow);

        return mWindow;
    }
}