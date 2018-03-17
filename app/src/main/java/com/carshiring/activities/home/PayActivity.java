package com.carshiring.activities.home;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.fragments.SearchCarFragment;
import com.carshiring.models.PointHistoryData;
import com.carshiring.models.TokenResponse;
import com.carshiring.models.UserDetails;
import com.carshiring.models.WalletHistoryData;
import com.carshiring.utilities.AppBaseActivity;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;
import com.payfort.fort.android.sdk.base.FortSdk;
import com.payfort.fort.android.sdk.base.callbacks.FortCallBackManager;
import com.payfort.fort.android.sdk.base.callbacks.FortCallback;
import com.payfort.sdk.android.dependancies.base.FortInterfaces;
import com.payfort.sdk.android.dependancies.models.FortRequest;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

import static android.content.ContentValues.TAG;

public class PayActivity extends AppBaseActivity {
    ActionBar actionBar;
    TextView txtPay, txtTotalAmyVal,txtWalletBal,txtPointVal;
    CheckedTextView txtcheckPoint,txtCheckPay, txtCheckWallet;
    public static String price="", email="",sdk_token="";
    public FortCallBackManager fortCallback;
    String name="",sarname="",number="",address="",city="",zipcode="",countrycode="",car_id="",
            type="",rtype="",fullprotection="",flight_no="",extradata="",dob="",user_id="",pick_date="",
            drop_date="", pick_city="",drop_city="",protection_val="",booking_point="",booking_wallet="",
            booking_payfort="",transaction_id="",language="";
    TinyDB tinyDB;
    CheckBox checkPayOnline, checkWallet, checkPoint;
    public List<WalletHistoryData> walletHistoryData = new ArrayList<>();
    public List<PointHistoryData>pointHistoryData = new ArrayList<>();
    double creditAmt, debitAmt,walletAmt, totalDebit, totalCredit,totalPoint,totalDebitPoint, totalCreditPoint, creditPoint, debitPoint;
    Gson gson = new Gson();
    UserDetails userDetails = new UserDetails();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        actionBar = getSupportActionBar() ;
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
        tinyDB = new TinyDB(getApplicationContext());
        String login = tinyDB.getString("login_data");
        language=tinyDB.getString("language_code");
        userDetails = gson.fromJson(login, UserDetails.class);
        /* (language,name,sarname,number,email,address,
                city,zipcode,countrycode,car_id,type,rtype,fullprotection,flight_no,extradata,dob,user_id,
                pick_date,drop_date,pick_city,drop_city,protection_val,booking_point,booking_wallet,booking_payfort,
                transaction_id);*/
        user_id = userDetails.getUser_id();
        name = userDetails.getUser_name();
        sarname = (String) userDetails.getUser_lname();
        number = userDetails.getUser_phone();
        email   = userDetails.getUser_email();
        address = userDetails.getUser_address();
        city = (String) userDetails.getUser_city();
        zipcode = userDetails.getUser_zipcode();
        countrycode = userDetails.getUser_countrycode();
        dob = userDetails.getUser_age();
        flight_no = " ";
        car_id = CarsResultListActivity.id_context;
        type = CarsResultListActivity.type;
        rtype = CarsResultListActivity.refertype;
        pick_city = SearchCarFragment.pickName;
        pick_date = SearchCarFragment.pick_date;
        drop_city = SearchCarFragment.dropName;
        drop_date = SearchCarFragment.drop_date;


        txtCheckPay = findViewById(R.id.check_pay_online);
        txtcheckPoint = findViewById(R.id.check_points);
        txtCheckWallet = findViewById(R.id.check_wallet);
        txtWalletBal = findViewById(R.id.txtWaletValue);
        txtPointVal = findViewById(R.id.txtPointValue);
        txtPay = findViewById(R.id.txtPay);
        txtTotalAmyVal = findViewById(R.id.txtTotalPayValue);
        txtPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPurchase();
            }
        });
        txtTotalAmyVal.setText(CarDetailActivity.currency+ " "+CarDetailActivity.carPrice);

        txtCheckWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtCheckWallet.isChecked()){
                    txtCheckWallet.setChecked(false);
                } else {
                    txtCheckWallet.setChecked(true);
                }
            }
        });
        txtCheckPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtCheckPay.isChecked()){
                    txtCheckPay.setChecked(false);
                } else {
                    txtCheckPay.setChecked(true);
                }
            }
        });
        txtcheckPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtcheckPoint.isChecked()){
                    txtcheckPoint.setChecked(false);
                } else {
                    txtcheckPoint.setChecked(true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        actionBar.setTitle(getResources().getString(R.string.txtPayNow));
        getSDKToken(language);
        getPoint();
        getWal();
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

    private void requestSDKToken(String language) {
        createFORTMobileSDKToken(language);
    }

    private void requestOperation(String command, String sdk_token) {
        final String ECI = "ECOMMERCE";
        email = "a@g.in";
        final String CUSTOMER_EMAIL = email;
        final String LANGUAGE = language;
        final String CURRENCY = "SAR";
        double amt = Double.parseDouble(CarDetailActivity.carPrice)*100;
        int i = (int)amt;
        final String AMOUNT = String.valueOf(i);
        final String MERCHANT_REFERENCE = Utility.getRandomString(40) ;
        FortRequest fortRequest = new FortRequest();
        fortRequest.setShowResponsePage(true);
        final Map<String, String> requestMap = new HashMap<>();

        requestMap.put("command",command);
        requestMap.put("merchant_reference",MERCHANT_REFERENCE);
        requestMap.put("amount",AMOUNT);
        requestMap.put("currency",CURRENCY);
        requestMap.put("language",LANGUAGE);
        requestMap.put("customer_email",CUSTOMER_EMAIL);
        requestMap.put("sdk_token",sdk_token);

//        requestMap.put("payment_option","AMEX");
//        requestMap.put("eci",ECI);
//        requestMap.put("order_description",command);
//        requestMap.put("customer_ip",command);
//        requestMap.put("customer_name",sdk_token);
//        if (mobile!=null){
//            requestMap.put("phone_number",mobile);
//        }
//        requestMap.put("settlement_reference",command);
//        requestMap.put("return_url",command);

        fortRequest.setRequestMap(requestMap);
        fortCallback = FortCallback.Factory.create();
        boolean showLoading= true;
        try {
            FortSdk.getInstance().registerCallback(this, fortRequest,FortSdk.ENVIRONMENT.TEST,
                    5, fortCallback,showLoading, new FortInterfaces.OnTnxProcessed() {
                        @Override
                        public void onCancel(Map<String, String> requestParamsMap, Map<String, String> responseMap) {

                            Toast.makeText(getApplicationContext(), responseMap.get("response_message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess(Map<String, String> requestParamsMap, Map<String, String> fortResponseMap) {
                            Log.d(TAG, "onSuccess: "+fortResponseMap.toString());
                            Toast.makeText(getApplicationContext(), fortResponseMap.get("response_message"),
                                    Toast.LENGTH_SHORT).show();
                            transaction_id = fortResponseMap.get("fort_id");

                        }
                        @Override
                        public void onFailure(Map<String, String> requestParamsMap, Map<String, String> fortResponseMap) {
                            Log.d(TAG, "onFailure: "+fortResponseMap.get("response_message"));
                            Toast.makeText(getApplicationContext(), fortResponseMap.get("response_message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    final String ACCESS_TOKEN =  "qa2s6awTpBNc04Q65T8v";
    final String MERCHANT_IDENTIFIER = "GjitDYjm";
    final String REQUEST_PHRASE = "PASS" ;

    private void createFORTMobileSDKToken(String language) {
        OkHttpClient client = new OkHttpClient();
        String device_id = FortSdk.getDeviceId(getApplicationContext());
        StringBuilder base = new StringBuilder();
        base.append(REQUEST_PHRASE)
                .append("access_code=").append(ACCESS_TOKEN)
                .append("device_id=").append(device_id)
                .append("language=").append(language)
                .append("merchant_identifier=").append(MERCHANT_IDENTIFIER)
                .append("service_command=").append("SDK_TOKEN")
                .append(REQUEST_PHRASE);

        String signature = Utility.getSHA256Hash(base.toString());
        final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject() ;
        try {
            jsonObject.put("service_command","SDK_TOKEN") ;
            jsonObject.put("access_code",ACCESS_TOKEN) ;
            jsonObject.put("merchant_identifier",MERCHANT_IDENTIFIER) ;
            jsonObject.put("device_id",device_id) ;
            jsonObject.put("language",language) ;
            jsonObject.put("signature",signature) ;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
        Request request;
        request = new Request.Builder()
//                for test URL
                .url("https://sbpaymentservices.payfort.com/FortAPI/paymentApi")
                .method("POST", RequestBody.create(null, new byte[0]))
                .post(body)
                .addHeader("Content-Type", "application/json; charset=utf8")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                //call.cancel();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String mMessage = response.body().string();
                if (response.isSuccessful()){
                    try {
                        JSONObject json = new JSONObject(mMessage);
                        Gson gson = new Gson();
                        TokenResponse tokenResponse = new TokenResponse();
                        tokenResponse = gson.fromJson(json.toString(),TokenResponse.class);
                        sdk_token = tokenResponse.getSdk_token();

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fortCallback.onActivityResult(requestCode,resultCode,data);
    }

    public void getSDKToken(String language) {
        requestSDKToken(language) ;
    }

    public void requestAuthorization() {
        requestOperation("AUTHORIZATION" ,sdk_token) ;
    }

    public void requestPurchase() {
        requestOperation("PURCHASE" ,sdk_token) ;
    }

    public void bookCar(){
        Utility.showLoading(this,"Searching cars...");
        RetroFitApis retroFitApis = RetrofitApiBuilder.getCargHiresapis();
        retrofit2.Call<ApiResponse> responseCall = retroFitApis.bookCar(language,name,sarname,number,email,address,
                city,zipcode,countrycode,car_id,type,rtype,fullprotection,flight_no,extradata,dob,user_id,
                pick_date,drop_date,pick_city,drop_city,protection_val,booking_point,booking_wallet,booking_payfort,
                transaction_id);
        responseCall.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                if (response!=null){
                    Log.d(TAG, "onResponse: "+response.body().response.booking_id);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ApiResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });

    }

    public void getWal(){
        RetroFitApis fitApis= RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> walList = fitApis.walletHistory(user_id);
        walList.enqueue(new retrofit2.Callback<ApiResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                if (response!=null){
                   if (response.body().status){
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
                       if (walletAmt>0){
                           txtWalletBal.setText("("+walletAmt+" Wallet value is : "+walletAmt+")");

                           txtWalletBal.setText("("+getResources().getString(R.string.txtAvailable)+" "
                                   +String.valueOf(walletAmt)+")");
                           txtWalletBal.setVisibility(View.VISIBLE);
                           txtCheckWallet.setVisibility(View.VISIBLE);

                       } else {
                           txtWalletBal.setVisibility(View.GONE);
                           txtCheckWallet.setVisibility(View.GONE);

                       }
                   } else {
                       Utility.message(getApplicationContext(),response.body().message);
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

    public void getPoint(){
        RetroFitApis fitApis= RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> walList = fitApis.pointHistory(user_id);
        walList.enqueue(new retrofit2.Callback<ApiResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                if (response!=null){
                    if (response.body().status){
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
                        double d = totalPoint*0.05;
                        Log.d("TAG", "onResponse: totalDebit"+totalCreditPoint+"\n"+d);
//                    txtPointVal.setText(String.valueOf(totalPoint));
                        if (totalPoint>0.0){
                            txtPointVal.setText("("+totalPoint+" Point value is : "+String.valueOf(d)+")");
                            txtPointVal.setVisibility(View.VISIBLE);
                            txtcheckPoint.setVisibility(View.VISIBLE);
                        } else {
                            txtPointVal.setVisibility(View.GONE);
                            txtcheckPoint.setVisibility(View.GONE);
                        }
                    } else {
                        Utility.message(getApplicationContext(),response.body().message);
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
