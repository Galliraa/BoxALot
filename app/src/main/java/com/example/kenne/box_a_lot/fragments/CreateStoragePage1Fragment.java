package com.example.kenne.box_a_lot.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kenne.box_a_lot.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CreateStoragePage1Fragment extends CreateStoragePageFragment {

    private Spinner typeStorageSpinner;
    private EditText widthET;
    private EditText lengthET;
    private EditText priceET;
    private TextView SquaremeterPriceTV;

    public CreateStoragePage1Fragment() {
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
        return inflater.inflate(R.layout.fragment_create_storageroom_page1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        typeStorageSpinner = view.findViewById(R.id.create_storageroom_page1_type_storage_spinner);
        widthET  = view.findViewById(R.id.create_storageroom_page1_width_ET);
        lengthET = view.findViewById(R.id.create_storageroom_page1_length_ET);
        priceET = view.findViewById(R.id.create_storageroom_page1_price_ET);
        SquaremeterPriceTV = view.findViewById(R.id.create_storageroom_page1_sqm_price_TV);
    }

    public String ValidateUserInput(){

        if(typeStorageSpinner.getSelectedItem() == null)
            return "You must choose a type";

        else if(widthET.getText().toString() == "")
            return "You must specify a width";

        else if(lengthET.getText().toString() == "")
            return "You must specify a length";

        else if(priceET.getText().toString() == "")
            return "You must specify a price";

        return null;
    }
}

