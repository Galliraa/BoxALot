package com.example.kenne.box_a_lot.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kenne.box_a_lot.R;


public class CreateStoragePage2Fragment extends Fragment {

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

    // Html.fromHtml(htmlString)
    String htmlString = "<u>This text will be underlined</u>";
        tvfromHtmlDemo.setText(Html.fromHtml(htmlString));


}


