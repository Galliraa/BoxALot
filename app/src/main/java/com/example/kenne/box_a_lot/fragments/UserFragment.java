package com.example.kenne.box_a_lot.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.dialogFragments.CustomBlurDialogFragment;
import com.example.kenne.box_a_lot.interfaces.UiUpdateInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class UserFragment extends Fragment {

    private String mUsername;
    private String mPhoneNumber;
    private String mPhotoUrl;

    public static final String ANONYMOUS = "anonymous";

    private CircleImageView profileIV;
    private TextView usernameTV;
    private TextView phoneNumberTV;
    private Button  signOutBtn;

    private static final int LOGIN_REQUEST = 1;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            /*final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_not_logged_in_user_frag);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();*/
            //startActivityForResult(new Intent(getActivity(), LoginActivity.class), LOGIN_REQUEST);

            // return;
        }
        else {
            mUsername = mFirebaseUser.getDisplayName();
            mPhoneNumber = mFirebaseUser.getPhoneNumber();
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       /* if (getArguments() != null) {

        }

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), LOGIN_REQUEST);

            return;
        }*/

        usernameTV = view.findViewById(R.id.user_usernameTV);
        phoneNumberTV = view.findViewById(R.id.user_phonenumberTV);
        profileIV = view.findViewById(R.id.user_userIV);
        signOutBtn = view.findViewById(R.id.user_signOutBtn);

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth = FirebaseAuth.getInstance();
                mFirebaseAuth.signOut();
                //mFirebaseAuth.getCurrentUser().getProviderId();
                //Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                //startActivity(new Intent(this, SignInActivity.class));
                ((UiUpdateInterface)getActivity()).goToMap();
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
                ((UiUpdateInterface)getActivity()).goToMap();
            }
        }
    }



    public void showLoginDialog(Context context, Activity activity, FragmentManager supportFragmentManager) {
        CustomBlurDialogFragment customDialog= new CustomBlurDialogFragment();

        customDialog.show(supportFragmentManager, "");
    }
}
