package com.example.kenne.box_a_lot.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private TextView squaremeterPriceTV;

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
        squaremeterPriceTV = view.findViewById(R.id.create_storageroom_page1_sqm_price_TV);

        widthET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().equals("")){return;}

                if(lengthET.getText().toString().equals("")||lengthET.getText().toString().equals("0"))
                {return;}
                if(priceET.getText().toString().equals("")||priceET.getText().toString().equals("0"))
                {return;}

                double price = Integer.parseInt(priceET.getText().toString());
                double length = Integer.parseInt(lengthET.getText().toString());
                double width = Integer.parseInt(widthET.getText().toString());
                double tempres100 = Math.round((price/(length*width))*100);
                double result = tempres100/100;
                String res = Double.toString(result);
                squaremeterPriceTV.setText(res);
            }
        });

        lengthET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().equals("")){return;}

                if(widthET.getText().toString().equals("")||widthET.getText().toString().equals("0"))
                {return;}
                if(priceET.getText().toString().equals("")||priceET.getText().toString().equals("0"))
                {return;}

                double price = Integer.parseInt(priceET.getText().toString());
                double length = Integer.parseInt(lengthET.getText().toString());
                double width = Integer.parseInt(widthET.getText().toString());
                double tempres100 = Math.round((price/(length*width))*100);
                double result = tempres100/100;
                String res = Double.toString(result);
                squaremeterPriceTV.setText(res);
            }
        });

        priceET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().equals("")){return;}

                if(lengthET.getText().toString().equals("")||lengthET.getText().toString().equals("0"))
                {return;}
                if(widthET.getText().toString().equals("")||widthET.getText().toString().equals("0"))
                {return;}

                double price = Integer.parseInt(priceET.getText().toString());
                double length = Integer.parseInt(lengthET.getText().toString());
                double width = Integer.parseInt(widthET.getText().toString());
                double tempres100 = Math.round((price/(length*width))*100);
                double result = tempres100/100;
                String res = Double.toString(result);
                squaremeterPriceTV.setText(res);
            }
        });
    }

    public String validateUserInput(){

        if(typeStorageSpinner.getSelectedItem() == null) {
            return "You must choose a type";
        }

        if(widthET.getText().toString().equals("")) {
            return "You must specify a width";
        }

        if(lengthET.getText().toString().equals("")) {
            return "You must specify a length";
        }

        if(priceET.getText().toString().equals("")) {
            return "You must specify a price";
        }

        return null;
    }
}

