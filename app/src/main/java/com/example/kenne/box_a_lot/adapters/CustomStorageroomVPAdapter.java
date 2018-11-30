package com.example.kenne.box_a_lot.adapters;

import android.content.Context;
import android.net.Uri;
//import android.support.annotation.NonNull;
//import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class CustomStorageroomVPAdapter extends PagerAdapter {
    private Context context;
    private List<String> imageUrls;
    private FirebaseStorage FBstorage = FirebaseStorage.getInstance();

    public CustomStorageroomVPAdapter(Context context, List<String> imageUrls){
         this.context = context;
         this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {


        // Got the download URL
        // Pass it to Picasso to download, show in ImageView and caching
        ImageView imageView = new ImageView(context);
        Picasso.with(context).load(imageUrls.get(position)).fit().centerCrop().into(imageView);
        container.addView(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
