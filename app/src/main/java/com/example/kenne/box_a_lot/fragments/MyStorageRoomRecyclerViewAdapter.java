package com.example.kenne.box_a_lot.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.fragments.StorageRoomFragment.OnListFragmentInteractionListener;
import com.example.kenne.box_a_lot.models.StorageRoom;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link StorageRoom} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyStorageRoomRecyclerViewAdapter extends RecyclerView.Adapter<MyStorageRoomRecyclerViewAdapter.MyStorageRoomRecyclerViewHolder> {

    private FirebaseStorage FBstorage = FirebaseStorage.getInstance();
    private final List<StorageRoom> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    FloatingActionButton listFab;

    public MyStorageRoomRecyclerViewAdapter(List<StorageRoom> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public MyStorageRoomRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_storageroom, parent, false);
        listFab = (FloatingActionButton) ((Activity)context).findViewById(R.id.listviewFabBtn);
        return new MyStorageRoomRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyStorageRoomRecyclerViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mPriceTV.setText(mValues.get(position).getPrice());
        holder.mAddressTV.setText(mValues.get(position).getAddress());
        holder.mDescriptionTV.setText(mValues.get(position).getPrice());

        String picRef = mValues.get(position).getPicRef().get(0);

        if(picRef != null) {
            StorageReference storageRef = FBstorage.getReference().child(picRef);


            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL
                    // Pass it to Picasso to download, show in ImageView and caching
                    Picasso.with(context).load(uri.toString()).fit().centerCrop().into(holder.mStoragePic, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class MyStorageRoomRecyclerViewHolder extends RecyclerView.ViewHolder{
        public final View mView;
        public final ImageView mStoragePic;
        public final TextView mPriceTV;
        public final TextView mAddressTV;
        public final TextView mDescriptionTV;
        public StorageRoom mItem;

        public MyStorageRoomRecyclerViewHolder(View view) {
            super(view);
            mView = view;
            mStoragePic = (ImageView) view.findViewById(R.id.storageIV);
            mPriceTV = (TextView) view.findViewById(R.id.priceTV);
            mAddressTV = (TextView) view.findViewById(R.id.addressTV);
            mDescriptionTV = (TextView) view.findViewById(R.id.descTV);
        }

    }
}
