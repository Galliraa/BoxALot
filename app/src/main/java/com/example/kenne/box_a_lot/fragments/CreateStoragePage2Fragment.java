package com.example.kenne.box_a_lot.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.interfaces.UpdateAble;

import androidx.fragment.app.Fragment;


public class CreateStoragePage2Fragment extends Fragment implements UpdateAble {

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
        return inflater.inflate(R.layout.fragment_creat_room_depotet_page2, container, false);
    }

    @Override
    public void update() {

    }
}


