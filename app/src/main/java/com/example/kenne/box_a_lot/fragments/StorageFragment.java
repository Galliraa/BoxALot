package com.example.kenne.box_a_lot.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kenne.box_a_lot.ContactStorage;
import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.models.StorageRoom;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class StorageFragment extends Fragment {

    private String stId;

    private OnFragmentInteractionListener mListener;

    private StorageRoom prvStorageRoom;

    public StorageFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_storage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       /* TextView priceTV = getView().findViewById(R.id.priceTvStorageFrag);
        priceTV.setText(getArguments().getString("price"));
        TextView addressTV = getView().findViewById(R.id.addressTvStorageFrag);
        addressTV.setText(getArguments().getString("address"));
        Button contactLandlordBtn = getView().findViewById(R.id.contactLandlordBtnStorageFrag);
        stId = getArguments().getString("storageroomID");
        contactLandlordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StorageFragment.this.getActivity(), ContactStorage.class);
                intent.putExtra("storageRoomId", stId);
                startActivity(intent);
            }
        });
            //storageFragBundle.getArguments().getString("available");
            //storageFragBundle.getArguments().getString("generalInfo");
            //storageFragBundle.getArguments().getString("userId");
            //storageFragBundle.getArguments().getString("storageroomID");
            //storageFragBundle.getArguments().getString("imagePath");*/

        TextView priceTV = getView().findViewById(R.id.priceTvStorageFrag);
        priceTV.setText(prvStorageRoom.getPrice());
        TextView addressTV = getView().findViewById(R.id.addressTvStorageFrag);
        addressTV.setText(prvStorageRoom.getAddress());
        Button contactLandlordBtn = getView().findViewById(R.id.contactLandlordBtnStorageFrag);
        stId = prvStorageRoom.getStorageRoomId();
        contactLandlordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StorageFragment.this.getActivity(), ContactStorage.class);
                intent.putExtra("storageRoomId", stId);
                startActivity(intent);
            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setStorage(StorageRoom storageRoom)
    {
        prvStorageRoom = storageRoom;
        /*
        TextView priceTV = getView().findViewById(R.id.priceTvStorageFrag);
        priceTV.setText(storageRoom.getPrice());
        TextView addressTV = getView().findViewById(R.id.addressTvStorageFrag);
        addressTV.setText(storageRoom.getAddress());
        Button contactLandlordBtn = getView().findViewById(R.id.contactLandlordBtnStorageFrag);
        stId = storageRoom.getStorageRoomId();
        contactLandlordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StorageFragment.this.getActivity(), ContactStorage.class);
                intent.putExtra("storageRoomId", stId);
                startActivity(intent);
            }
        });
        */
    }
}
