package com.example.kenne.box_a_lot.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.models.StorageRoom;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class CreateStoragePage2Fragment extends CreateStoragePageFragment {

    private static final int MAX_CHECK_NUMBER = 9;

    private CheckBox[]  myCheckBoxArray = new CheckBox[9];
    private EditText descET;
    List<Boolean> generalInfo = new ArrayList<>();

    public CreateStoragePage2Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_storageroom_page2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        myCheckBoxArray[0] = view.findViewById(R.id.fragment_create_room_page2_checkbox_elevator);
        myCheckBoxArray[1] = view.findViewById(R.id.fragment_create_room_page2_checkbox_video_surveillance);
        myCheckBoxArray[2] = view.findViewById(R.id.fragment_create_room_page2_checkbox_clima_control);
        myCheckBoxArray[3] = view.findViewById(R.id.fragment_create_room_page2_checkbox_smokealarm);
        myCheckBoxArray[4] = view.findViewById(R.id.fragment_create_room_page2_checkbox_locked);
        myCheckBoxArray[5] = view.findViewById(R.id.fragment_create_room_page2_checkbox_no_smoke);
        myCheckBoxArray[6] = view.findViewById(R.id.fragment_create_room_page2_checkbox_private_entrance);
        myCheckBoxArray[7] = view.findViewById(R.id.fragment_create_room_page2_checkbox_on_house_animals);
        myCheckBoxArray[8] = view.findViewById(R.id.fragment_create_room_page2_checkbox_24_7_access);

        descET = view.findViewById(R.id.fragment_create_room_page2_edittext_description);
    }

    @Override
    public boolean getData(StorageRoom storageroom) {


        for(int i = 0; i < MAX_CHECK_NUMBER; i++)
            generalInfo.add(myCheckBoxArray[i].isChecked());

        storageroom.setGeneralInfo(generalInfo);
        storageroom.setDesc(descET.getText().toString());

        return true;
    }
}


