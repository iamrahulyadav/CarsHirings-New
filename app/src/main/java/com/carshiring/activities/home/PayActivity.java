package com.carshiring.activities.home;
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
import com.carshiring.models.TokenResponse;
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
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class PayActivity extends AppBaseActivity {
    ActionBar actionBar;
    TextView txtPay, txtTotalAmyVal;
    CheckedTextView txtcheckPoint,txtCheckPay, txtCheckWallet;
    public static String price="", email="",sdk_token="";
    public FortCallBackManager fortCallback;
    String name="",sarname="",number="",address="",city="",zipcode="",countrycode="",car_id="",
            type="",rtype="",fullprotection="",flight_no="",extradata="",dob="",user_id="",pick_date="",
            drop_date="", pick_city="",drop_city="",protection_val="",booking_point="",booking_wallet="",
            booking_payfort="",transaction_id="",language="";
    TinyDB tinyDB;
    CheckBox checkPayOnline, checkWallet, checkPoint;

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
        txtCheckPay = findViewById(R.id.check_pay_online);
        txtcheckPoint = findViewById(R.id.check_points);
        txtCheckWallet = findViewById(R.id.check_wallet);
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
        language=tinyDB.getString("language_code");
        getSDKToken(language);
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
/*
    Call<ApiResponse> bookCar(@Field("language_code") String language_code,
                              @Field("name") String name,
                              @Field("sarname") String sarname,
                              @Field("number") String number,
                              @Field("email") String email,
                              @Field("address") String address,
                              @Field("city") String city,
                              @Field("zipcode") String zipcode,
                              @Field("countrycode") String countrycode,
                              @Field("car_id") String car_id,
                              @Field("type") String type,
                              @Field("rtype") String rtype,
                              @Field("fullprotection") String fullprotection,
                              @Field("flight_no") String flight_no,
                              @Field("extradata") String extradata,
                              @Field("dob") String dob,
                              @Field("user_id") String user_id,
                              @Field("pick_date") String pick_date,
                              @Field("drop_date") String drop_date,
                              @Field("pick_city") String pick_city,
                              @Field("drop_city") String drop_city,
                              @Field("protection_val") String protection_val,
                              @Field("booking_point") String booking_point,
                              @Field("booking_wallet") String booking_wallet,
                              @Field("booking_payfort") String booking_payfort);*/

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

}
