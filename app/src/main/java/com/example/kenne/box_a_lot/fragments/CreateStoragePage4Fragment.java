package com.example.kenne.box_a_lot.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.models.StorageRoom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.app.Activity.RESULT_OK;

public class CreateStoragePage4Fragment extends CreateStoragePageFragment {


    private static final int MAX_PIC_NUMBER = 4;
    private ImageView[] myImageViewArray = new ImageView[MAX_PIC_NUMBER];

    private static final int[] PHOTO_CAPTURE_REQUEST_CODE = new int[]{2,3,4,5};

    public CreateStoragePage4Fragment() {
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
        myImageViewArray[0] = view.findViewById(R.id.ph00);
        myImageViewArray[1] = view.findViewById(R.id.ph01);
        myImageViewArray[2] = view.findViewById(R.id.ph02);
        myImageViewArray[3] = view.findViewById(R.id.ph03);

        setupImageClickListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_storageroom_page4, container, false);
    }

    private void setupImageClickListener() {

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            for(int i = 0; i < MAX_PIC_NUMBER; i++) {
                if (requestCode == PHOTO_CAPTURE_REQUEST_CODE[i]) {
                    if (resultCode == RESULT_OK) {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        myImageViewArray[i].setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(getActivity(),
                                "The photo could not be retrieved",
                                Toast.LENGTH_LONG)
                                .show();

                    }
                }
            }
    }

    @Override
    public boolean getData(StorageRoom storageroom) {
        return true;
    }

    public ImageView[] getImageviewArray(){
        return myImageViewArray;
    }

}
