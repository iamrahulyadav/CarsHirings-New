package com.carshiring.activities.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.activities.mainsetup.LoginActivity;
import com.carshiring.models.PointHistoryData;
import com.carshiring.models.UserDetails;
import com.carshiring.models.WalletHistoryData;
import com.carshiring.utilities.AppBaseActivity;
import com.carshiring.utilities.AppGlobal;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDashActivity extends AppBaseActivity {

    FrameLayout wallFrame;
    ImageView imgEdit,imgUser, imgUserWall;
    TextView txtEmail, txtPhone,txtAddress, txtAge, txtDrvLnc, txtCreditPt, txtdebitPt,txtWalletAmt,txtPointValue, txtName;
    UserDetails userDetails = new UserDetails();
    Gson gson = new Gson();
    TinyDB tinyDB;
    LinearLayout walletView, pointView;
    String user_id, language;
    public static List<WalletHistoryData>walletHistoryData = new ArrayList<>();
    public static List<PointHistoryData>pointHistoryData = new ArrayList<>();
    double creditAmt, debitAmt,walletAmt, totalDebit, totalCredit,totalPoint,totalDebitPoint, totalCreditPoint, creditPoint, debitPoint;
    AppGlobal appGlobal=AppGlobal.getInstancess();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash);
        appGlobal.context=getApplicationContext();

        actionBar = getSupportActionBar() ;
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }

        //        initialize
        tinyDB = new TinyDB(getApplicationContext());
        String login = tinyDB.getString("login_data");
        userDetails = gson.fromJson(login, UserDetails.class);
        language = tinyDB.getString("language_code");
        user_id = userDetails.getUser_id();

        //        find id
        imgUserWall = findViewById(R.id.user_profile_wall);
        txtAddress = findViewById(R.id.dash_profile_txtAddress);
        txtAge = findViewById(R.id.dash_profile_txtage);
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

//        get wallet and point value
        getWal();
        getPoint();
    }

    public void walletView(View view){
        Intent i = new Intent(UserDashActivity.this, WalletView.class);
        if(view.getId() == R.id.wallet_lay){
            i.putExtra("wallet_title", "Wallet History Details");
//            i.putExtra("history_details", walletHistoryData);
        } else if(view.getId() == R.id.point_lay){
            i.putExtra("wallet_title", "Point History Details");
        }else{}
        startActivity(i);
    }

    public void icon_update(View view){
        startActivity(new Intent(UserDashActivity.this, AccountDetailsActivity.class));
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
    @Override
    protected void onResume() {
        super.onResume();
        actionBar.setTitle(getResources().getString(R.string.txtDashTitle));
        getProfile();
    }


    public void getWal(){
        if (walletHistoryData!=null){
            walletHistoryData.clear();
        }

        RetroFitApis fitApis= RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> walList = fitApis.walletHistory(user_id);
        walList.enqueue(new Callback<ApiResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response!=null){
                    if (response.body().status){
                        walletHistoryData = response.body().response.wallet;
                        Log.d("TAG", "onResponse: "+gson.toJson(walletHistoryData));
                        for (WalletHistoryData walletHistoryData1 : walletHistoryData){
                            if (walletHistoryData1.get_$WalletType204().equals("debit")){
                                String debit = walletHistoryData1.get_$WalletAmount169();
                                debitAmt = Double.parseDouble(debit);
                                totalDebit += debitAmt;
//                            Log.d("TAG", "onResponse: "+debit);
                            }
                            if (walletHistoryData1.get_$WalletType204().equals("credit")){
                                String debit = walletHistoryData1.get_$WalletAmount169();
                                creditAmt = Double.parseDouble(debit);
                                totalCredit+= creditAmt;
                            }
                        }
                        walletAmt = totalCredit-totalDebit;
                        Log.d("TAG", "onResponse: totalDebit"+totalCredit+"\n"+walletAmt);
                        if (walletAmt>0){
                            txtWalletAmt.setText(String.valueOf( df2.format(walletAmt)));
                        }

                    } else {
                        //  Toast.makeText(UserDashActivity.this, ""+response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(UserDashActivity.this, ""+ getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

                //   Utility.message(getApplicationContext(), getResources().getString(R.string.check_internet));
                Log.d("TAG", "onFailure: "+t.getMessage());
            }
        });
    }

    String img;
    public class GetImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utility.showLoading(UserDashActivity.this,getResources().getString(R.string.loading));
                }
            });
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utility.hidepopup();
                }
            });
            imgUser.setImageBitmap(result);
            img = Utility.BitMapToString(result);
//
//            Glide.with(getActivity()).load(result)
//                    .apply(RequestOptions.circleCropTransform()).into(imgUser);
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();
                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }


    public void getProfile(){
        RetroFitApis fitApis= RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> walList = fitApis.profile(user_id);
        walList.enqueue(new Callback<ApiResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response!=null){
                    if(response.body().status)
                    {
                        UserDetails userDetails = new UserDetails();
                        userDetails = response.body().response.user_detail;
                        String logindata=gson.toJson(userDetails);
                        Log.d("TAG", "onResponse: "+logindata);
                        appGlobal.setLoginData(logindata);
                        String st =  appGlobal.getUser_id();
                        txtEmail.setText(getResources().getString(R.string.email)+": "+userDetails.getUser_email());
                        txtPhone.setText(getResources().getString(R.string.phone)+ ": "+userDetails.getUser_phone());
                        txtDrvLnc.setText(getResources().getString(R.string.drvlncno)+": "+userDetails.getUser_license_no());
                        if (userDetails.getUser_age()!=null&&!userDetails.getUser_age().equalsIgnoreCase("0")){
                            txtAge.setText(getResources().getString(R.string.age)+" : "+ userDetails.getUser_age()+" years");
                        }

                        txtAddress.setText(getResources().getString(R.string.address)+" : "+ userDetails.getUser_address());
                        if (userDetails.getUser_lname()!=null){
                            txtName.setText(userDetails.getUser_name()+ " "+userDetails.getUser_lname());
                        }
                        if (userDetails.getUser_image()!=null&&userDetails.getUser_image().length()>1){
                            String url = RetrofitApiBuilder.IMG_BASE_URL+userDetails.getUser_image();
                            GetImage task = new GetImage();
                            // Execute the task
                            task.execute(new String[] { url });
                        }
                        if (userDetails.getUser_image()!=null&&userDetails.getUser_image().length()>1){
                            String url = RetrofitApiBuilder.IMG_BASE_URL+userDetails.getUser_cover();
                            GetImageWall task = new GetImageWall();
                            // Execute the task
                            task.execute(new String[] { url });
                        }

                    }
                    else{
                        Utility.message(getApplicationContext(), getResources().getString(R.string.something_wrong));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(UserDashActivity.this, ""+ getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

                //   Utility.message(getApplicationContext(), getResources().getString(R.string.check_internet));
                Log.d("TAG", "onFailure: "+t.getMessage());
            }
        });
    }

    public class GetImageWall extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utility.showLoading(UserDashActivity.this,getResources().getString(R.string.loading));
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            Utility.hidepopup();
            imgUserWall.setImageBitmap(result);
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }


    private static DecimalFormat df2 = new DecimalFormat(".##");
    public void getPoint(){
        if (pointHistoryData!=null){
            pointHistoryData.clear();
        }
//        debitPoint = 0;

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
                       /* if (totalPoint>0){
                            txtPointValue.setText(getResources().getString(R.string.points)+" : "+String.format("%.2f", Float.parseFloat(String.valueOf(totalPoint))));

//                           txtPointValue.setText(String.valueOf(totalPoint));
                        } else {
                            txtPointValue.setText(String.valueOf(0.0));
                        }*/
                        totalPoint = totalCreditPoint-totalDebitPoint;

                        txtPointValue.setText(getResources().getString(R.string.points)+" : "+String.format("%.2f", Float.parseFloat(String.valueOf(totalPoint))));

                        Log.d("TAG", "onResponse: totalDebit"+totalCreditPoint+"\n"+totalPoint);
                        txtCreditPt.setText(getResources().getString(R.string.txtCredit)+" : "+ String.valueOf(totalCreditPoint));
                        txtdebitPt.setText(getResources().getString(R.string.txtDebit)+" : "+ String.valueOf(totalDebitPoint));
                    } else {
                        //  Toast.makeText(UserDashActivity.this, ""+response.body().message, Toast.LENGTH_SHORT).show();
                    }
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
