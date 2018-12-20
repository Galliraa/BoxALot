package com.example.kenne.box_a_lot.fragments;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.interfaces.UiUpdateInterface;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class UserFragment extends Fragment/* implements UpdateAble*/ {

    private String mUsername;
    private String mPhoneNumber;
    private String mPhotoUrl;

    private View actionBarView;

    private static final int DIALOG_REQUEST_CODE = 10;
    public static final String ANONYMOUS = "anonymous";

    static private CircleImageView profileIV;
    static private TextView usernameTV;
    static private TextView phoneNumberTV;
    static private Button  signOutBtn;

    private static final int LOGIN_REQUEST = 1;

    static private FirebaseAuth mFirebaseAuth;
    static private FirebaseUser mFirebaseUser;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {

        androidx.appcompat.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_HOME_AS_UP); // what's mainly important here is DISPLAY_SHOW_CUSTOM. the rest is optional

        actionBar.setDisplayHomeAsUpEnabled(false);

        //actionBar.setCustomView(actionBarView);
        //actionBar.hide();

    }
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {

        }
        else {
            mUsername = mFirebaseUser.getDisplayName();
            mPhoneNumber = mFirebaseUser.getPhoneNumber();
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }

        usernameTV = view.findViewById(R.id.user_usernameTV);
        phoneNumberTV = view.findViewById(R.id.user_phonenumberTV);
        profileIV = view.findViewById(R.id.user_userIV);
        signOutBtn = view.findViewById(R.id.user_signOutBtn);

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth = FirebaseAuth.getInstance();
                List<? extends UserInfo> provider = mFirebaseUser.getProviderData();

                for(int i = 0; i < provider.size(); i++)
                {
                    if(provider.get(i).getProviderId().equals("facebook.com")){
                        LoginManager.getInstance().logOut();
                    }
                    else if(provider.get(i).getProviderId().equals("firebase")){
                        mFirebaseAuth.signOut();
                    }
                }


                ((UiUpdateInterface)getActivity()).goToMap(false);
                //((UiUpdateInterface)getActivity()).hideTab();
            }
        });
        usernameTV.setText(mUsername);
        phoneNumberTV.setText(mPhoneNumber);
        if(mPhotoUrl != null) {
            if (mPhotoUrl.startsWith("gs://")) {
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(mPhotoUrl);
                storageReference.getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            private String TAG = "UserFragment";

                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    Glide.with(profileIV.getContext())
                                            .load(downloadUrl)
                                            .into(profileIV);
                                } else {
                                    Log.w(TAG, "Getting download url was not successful.",
                                            task.getException());
                                }
                            }
                        });
            } else {
                Glide.with(getActivity())
                        .load(mPhotoUrl)
                        .into(profileIV);
            }
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == LOGIN_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
            else
            {
                ((UiUpdateInterface)getActivity()).goToMap(false);
            }
        }
    }
}
