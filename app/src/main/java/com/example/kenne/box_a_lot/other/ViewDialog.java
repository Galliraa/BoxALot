package com.example.kenne.box_a_lot.other;

import android.app.Dialog;
import android.view.Window;

import com.example.kenne.box_a_lot.R;
import com.example.kenne.box_a_lot.customViews.GifImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewDialog {

    AppCompatActivity activity;
    Dialog dialog;
    //..we need the context else we can not create the dialog so get context in constructor
    public ViewDialog(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void showDialog() {

        dialog  = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //...set cancelable false so that it's never get hidden
        dialog.setCancelable(false);
        //...that's the layout i told you will inflate later
        dialog.setContentView(R.layout.custom_loading);

        GifImageView gifImageView = (GifImageView) dialog.findViewById(R.id.GifImageView);
        gifImageView.setGifImageResource(R.drawable.loading);

        //...finaly show it
        dialog.show();
    }

    //..also create a method which will hide the dialog when some work is done 
    public void hideDialog(){
        dialog.dismiss();
    }

}