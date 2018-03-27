package com.carshiring.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carshiring.R;
import com.carshiring.activities.home.MyBookingActivity;
import com.carshiring.activities.home.UserDashActivity;
import com.carshiring.activities.mainsetup.ChangePasswordActivity;
import com.carshiring.activities.mainsetup.LoginActivity;
import com.carshiring.models.PointHistoryData;
import com.carshiring.models.UserDetails;
import com.carshiring.utilities.AppGlobal;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by rakhi on 13/3/2018.
 * Contact Number : +91 9958187463
 */

public class AccountFragment extends Fragment implements View.OnClickListener {
    AppGlobal global=AppGlobal.getInstancess();
    LinearLayout ll_mybooking,ll_accountdetails,ll_changepassword,li_profile;
    Toolbar toolbar;
    TinyDB tinyDB ;
    TextView txtPoint;
    View view;
    String user_id;
    UserDetails userDetails = new UserDetails();
    Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.fragment_account,container,false);
        global.context=getContext();
        ll_mybooking= (LinearLayout) view.findViewById(R.id.ll_booking);
        ll_accountdetails= (LinearLayout) view.findViewById(R.id.ll_acccountdetails);
        ll_changepassword= (LinearLayout) view.findViewById(R.id.ll_change_password);
        li_profile= (LinearLayout) view.findViewById(R.id.ll_profile);
        ll_mybooking.setOnClickListener(this);
        ll_accountdetails.setOnClickListener(this);
        ll_changepassword.setOnClickListener(this);
        li_profile.setOnClickListener(this);
        setuptoolbar();
        tinyDB = new TinyDB(getContext());
        String data = tinyDB.getString("login_data");
        userDetails = gson.fromJson(data, UserDetails.class);
        user_id = userDetails.getUser_id();
        txtPoint = view.findViewById(R.id.fragment_account_point);

        return view;
    }

    private void setuptoolbar() {
        toolbar= (Toolbar) view.findViewById(R.id.bottomToolBar);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tinyDB.remove("login_data");
                Intent intent=new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
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
              // startActivity(new Intent(getActivity(),AccountDetailsActivity.class));
                break;
            case R.id.ll_change_password:
                startActivity(new Intent(getActivity(),ChangePasswordActivity.class));
             break;
            case R.id.ll_profile:
                startActivity(new Intent(getContext(), UserDashActivity.class));
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getPoint();

    }

    public List<PointHistoryData> pointHistoryData = new ArrayList<>();


    private static DecimalFormat df2 = new DecimalFormat(".##");
    double creditAmt, debitAmt,walletAmt, totalDebit, totalCredit,totalPoint,totalDebitPoint, totalCreditPoint, creditPoint, debitPoint;

    public void getPoint(){
        if (pointHistoryData!=null){
            pointHistoryData.clear();
        }
//        debitPoint = 0;
        totalPoint=0;
        totalDebitPoint=0;
        totalCreditPoint=0;
        creditPoint=0;
        debitPoint=0;

        RetroFitApis fitApis= RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> walList = fitApis.pointHistory(user_id);
        walList.enqueue(new Callback<ApiResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response!=null){
                    if (response.body().status){
                        pointHistoryData = response.body().response.points;
                        Log.d("TAG", "onResponse: "+gson.toJson(pointHistoryData));
                        for (PointHistoryData walletHistoryData1 : pointHistoryData){

                            if (walletHistoryData1.get_$BookingPointType184().equals("debit")){
                                String debit = walletHistoryData1.get_$BookingPoint18();
                                debitPoint = Double.parseDouble(debit);
                                totalDebitPoint += debitPoint;
//                            Log.d("TAG", "onResponse: "+debit);
                            }
                            if (walletHistoryData1.get_$BookingPointType184().equals("credit")){
                                String debit = walletHistoryData1.get_$BookingPoint18();
                                creditPoint = Double.parseDouble(debit);
                                totalCreditPoint+= creditPoint;
                            }
                        }
                        if (totalCreditPoint>totalDebitPoint){
                            totalPoint = totalCreditPoint-totalDebitPoint;
                        }
                      /* else {
                           totalPoint = totalDebitPoint-totalCreditPoint;
                       }*/
                        if (totalPoint>0){
                            txtPoint.setText(getResources().getString(R.string.points)+" "+String.valueOf(totalPoint));
                        } else {
                            txtPoint.setText(getResources().getString(R.string.points)+" "+String.valueOf(00.00));
                        }
                       /* Log.d("TAG", "onResponse: totalDebit"+totalCreditPoint+"\n"+totalPoint);
                        txtCreditPt.setText(getResources().getString(R.string.txtCredit)+" : "+ String.valueOf(totalCreditPoint));
                        txtdebitPt.setText(getResources().getString(R.string.txtDebit)+" : "+ String.valueOf(debitPoint));*/
                    } else {
                        //  Toast.makeText(UserDashActivity.this, ""+response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.message(getContext(), getResources().getString(R.string.check_internet));
                Log.d("TAG", "onFailure: "+t.getMessage());
            }
        });
    }

}
