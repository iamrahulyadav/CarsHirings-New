package com.carshiring.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.carshiring.R;
import com.carshiring.activities.home.AccountDetailsActivity;
import com.carshiring.activities.home.MyBookingActivity;
import com.carshiring.activities.home.UserDashActivity;
import com.carshiring.activities.mainsetup.LoginActivity;
import com.carshiring.utilities.AppGlobal;


/**
 * Created by rakhi on 13/3/2018.
 * Contact Number : +91 9958187463
 */

public class AccountFragment extends Fragment implements View.OnClickListener {
    AppGlobal global=AppGlobal.getInstancess();
    LinearLayout ll_mybooking,ll_accountdetails,ll_changepassword,li_profile;
    Toolbar toolbar;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.fragment_account,container,false);
        global.context=getContext();
        ll_mybooking= (LinearLayout) view.findViewById(R.id.ll_booking);
        ll_accountdetails= (LinearLayout) view.findViewById(R.id.ll_acccountdetails);
        ll_changepassword= (LinearLayout) view.findViewById(R.id.ll_booking_history);
        li_profile= (LinearLayout) view.findViewById(R.id.ll_profile);
        ll_mybooking.setOnClickListener(this);
        ll_accountdetails.setOnClickListener(this);
        ll_changepassword.setOnClickListener(this);
        li_profile.setOnClickListener(this);
        setuptoolbar();
        return view;
    }

    private void setuptoolbar() {
        toolbar= (Toolbar) view.findViewById(R.id.bottomToolBar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                global.resetAllData();
                getActivity().finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id)
        {
            case R.id.ll_booking:
                startActivity(new Intent(getContext(), MyBookingActivity.class));
//               MyBookingsFragment myBookingsFragment=new MyBookingsFragment();
//                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.subview_container,myBookingsFragment).addToBackStack(null).commit();
                break;
            case R.id.ll_acccountdetails:
               startActivity(new Intent(getActivity(),AccountDetailsActivity.class));
                break;
            case R.id.ll_booking_history:
                //startActivity(new Intent(getActivity(),ChangePasswordActivity.class));
             break;
            case R.id.ll_profile:
                startActivity(new Intent(getContext(), UserDashActivity.class));
                break;
        }

    }

}
