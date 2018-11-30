package com.example.kenne.box_a_lot.dialogFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kenne.box_a_lot.BlurDialogFragment.BlurDialogFragment;
import com.example.kenne.box_a_lot.R;


public class CustomBlurDialogFragment extends BlurDialogFragment {

    private Button signInBtn;
    private Button registerBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_not_logged_in_user_frag, container,
                false);

        signInBtn = v.findViewById(R.id.dialog_not_in_sign_in_btn);
        registerBtn = v.findViewById(R.id.dialog_not_signed_in_register_btn);

        return v;
    }
}
