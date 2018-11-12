package com.example.kenne.box_a_lot.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        // Got the download URL
        // Pass it to Picasso to download, show in ImageView and caching
        Picasso.with(mContext).load(picRef).fit().centerCrop().into(ivStorage,new MarkerCallback(mMarker));/*{
            @Override
            public void onSuccess() {
                mMarker.showInfoWindow();
                picRetrieved = true;
                Toast.makeText(mContext,"succes", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                picRetrieved = true;
                Toast.makeText(mContext,"fail", Toast.LENGTH_SHORT).show();
            }
        });

*/
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

    static class MarkerCallback implements Callback {
        Marker marker = null;

        MarkerCallback(Marker marker)
        {
            this.marker = marker;
        }

        @Override
        public void onError() {}

        @Override
        public void onSuccess()
        {
            if (marker == null)
            {
                return;
            }

            if (!marker.isInfoWindowShown())
            {
                return;
            }

            // If Info Window is showing, then refresh it's contents:

            marker.hideInfoWindow(); // Calling only showInfoWindow() throws an error
            marker.showInfoWindow();
        }
    }

}