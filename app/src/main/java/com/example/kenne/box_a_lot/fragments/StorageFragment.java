package com.example.kenne.box_a_lot.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kenne.box_a_lot.ContactStorage;
import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.adapters.CustomStorageroomVPAdapter;
import com.example.kenne.box_a_lot.models.StorageRoom;



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

        ViewPager imageViewPager = getView().findViewById(R.id.storageroomDetailsViewPager);
        CustomStorageroomVPAdapter viewPagerAdapter = new CustomStorageroomVPAdapter(getContext(), prvStorageRoom.getPicRef());
        imageViewPager.setAdapter(viewPagerAdapter);
        TextView priceTV = getView().findViewById(R.id.priceTvStorageFrag);
        priceTV.setText(prvStorageRoom.getPrice());
        TextView addressTV = getView().findViewById(R.id.addressTvStorageFrag);
        addressTV.setText(prvStorageRoom.getAddress().get(3) + " " + prvStorageRoom.getAddress().get(4));
        TextView descTV = getView().findViewById(R.id.descTvStorageFrag);
        descTV.setText(prvStorageRoom.getDesc());
        TextView sizeTV = getView().findViewById(R.id.sizeTvStorageFrag);
        sizeTV.setText(prvStorageRoom.getSize());
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

    }
}
