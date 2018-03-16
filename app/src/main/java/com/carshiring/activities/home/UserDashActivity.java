package com.carshiring.activities.home;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carshiring.R;
import com.carshiring.models.PointHistoryData;
import com.carshiring.models.UserDetails;
import com.carshiring.models.WalletHistoryData;
import com.carshiring.utilities.AppBaseActivity;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDashActivity extends AppBaseActivity {
    FrameLayout wallFrame;
    ImageView imgEdit,imgUser;
    TextView txtEmail, txtPhone, txtDrvLnc, txtCreditPt, txtdebitPt,txtWalletAmt,txtPointValue, txtName;
    UserDetails userDetails = new UserDetails();
    Gson gson = new Gson();
    TinyDB tinyDB;
    LinearLayout walletView, pointView;
    String user_id, language;
    public static List<WalletHistoryData>walletHistoryData = new ArrayList<>();
    public static List<PointHistoryData>pointHistoryData = new ArrayList<>();
    double creditAmt, debitAmt,walletAmt, totalDebit, totalCredit,totalPoint,totalDebitPoint, totalCreditPoint, creditPoint, debitPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash);
        actionBar = getSupportActionBar() ;
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
//        initialize
        tinyDB = new TinyDB(getApplicationContext());
        String login = tinyDB.getString("login_data");
        userDetails = gson.fromJson(login, UserDetails.class);
        language=tinyDB.getString("language_code");
        user_id = userDetails.getUser_id();
//        find id
        wallFrame = findViewById(R.id.dash_profile_wal);
        imgEdit = findViewById(R.id.dash_profile_edit);
        imgUser = findViewById(R.id.dash_profile_img);
        txtEmail = findViewById(R.id.dash_profile_txtemail);
        txtPhone = findViewById(R.id.dash_profile_txtPhn);
        txtDrvLnc = findViewById(R.id.dash_profile_txtDrivingLnc);
        txtCreditPt = findViewById(R.id.dash_profile_txtcredit);
        txtdebitPt = findViewById(R.id.dash_profile_txtDebit);
        txtWalletAmt = findViewById(R.id.dash_profile_txtWalletValue);
        txtName = findViewById(R.id.dash_profile_txtName);
        txtPointValue = findViewById(R.id.dash_profile_txtpointValue);
        pointView = findViewById(R.id.point_lay);
        walletView = findViewById(R.id.wallet_lay);

        txtEmail.setText(getResources().getString(R.string.email)+": "+userDetails.getUser_email());
        txtPhone.setText(getResources().getString(R.string.phone)+ ": "+userDetails.getUser_phone());
        txtDrvLnc.setText(getResources().getString(R.string.drvlncno)+": "+userDetails.getUser_license_no());
        txtName.setText(userDetails.getUser_name()+ " "+userDetails.getUser_lname());

        walletView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        pointView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        actionBar.setTitle(getResources().getString(R.string.txtDashTitle));
        getWal();
        getPoint();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    public void getWal(){
        RetroFitApis fitApis= RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> walList = fitApis.walletHistory(user_id);
        walList.enqueue(new Callback<ApiResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response!=null){
                    walletHistoryData = response.body().response.wallet;
                    Log.d("TAG", "onResponse: "+gson.toJson(walletHistoryData));
                    for (WalletHistoryData walletHistoryData1 : walletHistoryData){
                        if (walletHistoryData1.get_$WalletType204().equals("debit")){
                            String debit = walletHistoryData1.get_$WalletAmount169();
                            debitAmt = Double.parseDouble(debit);
                            totalDebit+= debitAmt;
//                            Log.d("TAG", "onResponse: "+debit);
                        }
                        if (walletHistoryData1.get_$WalletType204().equals("credit")){
                            String debit = walletHistoryData1.get_$WalletAmount169();
                            creditAmt = Double.parseDouble(debit);
                            totalCredit+= creditAmt;
                        }
                    }
                    Log.d("TAG", "onResponse: totalDebit"+debitAmt);
                    walletAmt = totalCredit-totalDebit;
                    Log.d("TAG", "onResponse: totalDebit"+totalCredit+"\n"+walletAmt);
//                    txtCreditPt.setText(getResources().getString(R.string.txtCredit)+" : "+ String.valueOf(totalCredit));
//                    txtdebitPt.setText(getResources().getString(R.string.txtDebit)+" : "+ String.valueOf(totalDebit));
                    txtWalletAmt.setText(String.valueOf(walletAmt));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.message(getApplicationContext(), getResources().getString(R.string.check_internet));
                Log.d("TAG", "onFailure: "+t.getMessage());
            }
        });
    }

    public void getPoint(){
        RetroFitApis fitApis= RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> walList = fitApis.pointHistory(user_id);
        walList.enqueue(new Callback<ApiResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response!=null){
                    pointHistoryData = response.body().response.points;
                    Log.d("TAG", "onResponse: "+gson.toJson(pointHistoryData));
                    for (PointHistoryData walletHistoryData1 : pointHistoryData){
                        if (walletHistoryData1.get_$BookingPointType184().equals("debit")){
                            String debit = walletHistoryData1.get_$BookingPoint18();
                            debitPoint = Double.parseDouble(debit);
                            totalDebitPoint+= debitPoint;
//                            Log.d("TAG", "onResponse: "+debit);
                        }
                        if (walletHistoryData1.get_$BookingPointType184().equals("credit")){
                            String debit = walletHistoryData1.get_$BookingPoint18();
                            creditPoint = Double.parseDouble(debit);
                            totalCreditPoint+= creditPoint;
                        }
                    }
                    Log.d("TAG", "onResponse: totalDebit"+debitPoint);
                    totalPoint = totalCreditPoint-totalDebitPoint;
                    Log.d("TAG", "onResponse: totalDebit"+totalCreditPoint+"\n"+totalPoint);
                    txtCreditPt.setText(getResources().getString(R.string.txtCredit)+" : "+ String.valueOf(creditPoint));
                    txtdebitPt.setText(getResources().getString(R.string.txtDebit)+" : "+ String.valueOf(debitPoint));
                    txtPointValue.setText(String.valueOf(totalPoint));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.message(getApplicationContext(), getResources().getString(R.string.check_internet));
                Log.d("TAG", "onFailure: "+t.getMessage());
            }
        });
    }

}
