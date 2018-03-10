package com.carshiring.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.carshiring.interfaces.IFragment;
import com.carshiring.interfaces.IRefreshToken;
import com.mukesh.tinydb.TinyDB;

/**
 * Created by rakhi on 9/2/2018.
 */

public class BaseFragment extends Fragment implements IFragment,IRefreshToken {

    View view ;
    @Override
    public void setDefaultSettings() {
        Log.d("Carshiring","No Default Setting for this View");
    }

    @Override
    public void featureStatus(boolean status) {

    }

    @Override
    public void refreshTokenCallBack() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDefaultSettings();
    }

    public void checkGPSStatus()
    {
        LocationManager locationManager =(LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSProviderEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPSProviderEnable)
        {
            showSettingsAlert();
        }
    }

    private void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                checkGPSStatus();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onResume() {
       super.onResume();
        TinyDB tinyDB = new TinyDB(getContext());
        String language_code = tinyDB.getString("language_code") ;
//        Context contexta = LocaleHelper.setLocale(getActivity(), language_code);
//        setDefaultSettings();
    }
}
