package com.example.kenne.box_a_lot.classes;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.util.concurrent.AtomicDoubleArray;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Locale;

public class GeocodingLocation {

    private static final String TAG = "GeocodingLocation";

    public static void getAddressFromLocation(final String locationAddress,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                final double[] result = new double[2];
                final String[] addressResult = new String[5];
                try {
                    List addressList = geocoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = (Address) addressList.get(0);
                        result[0] = address.getLatitude();
                        result[1] = address.getLongitude();
                        addressResult[0] = address.getCountryCode();
                        addressResult[1] = address.getPostalCode();
                        addressResult[2] = ((address.getSubLocality() == null) ? address.getLocality() : address.getSubLocality());
                        addressResult[3] = address.getThoroughfare();
                        addressResult[4] = address.getSubThoroughfare();

                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable to connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        //bundle.putDouble("Lat", result);
                        bundle.putDoubleArray("LatLng", result);
                        bundle.putStringArray("address",addressResult);
                        message.setData(bundle);
                    } else {

                        Bundle bundle = new Bundle();
                        bundle.putString("error", "no LatLng found");
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}
