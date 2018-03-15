package com.carshiring.activities.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.models.TokenResponse;
import com.carshiring.utilities.AppGlobal;
import com.carshiring.utilities.Utility;
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

public class UpgradePlanActivity extends AppCompatActivity {
    ActionBar actionBar;
    public static String user_type, price,sdk_token, package_id, email ;
    TinyDB tinyDB;
    String language, transaction_id, userId;
    AppGlobal appGlobal = AppGlobal.getInstancess();

    Gson gson  = new Gson();
    Context mContext;
    public FortCallBackManager fortCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        mContext = this;
        appGlobal.context=getApplicationContext();
        tinyDB = new TinyDB(getApplicationContext());

        language=tinyDB.getString("language_code");

        /*if(loginData.getUser_detail().getUser_type().equals("2"))     // Checking is it Jobseeker
        {
            JobPlanFragment istFragment=new JobPlanFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.upgrade_frame,istFragment).commit();
        }
        else if(loginData.getUser_detail().getUser_type().equals("1"))    //Checking is it Employer
        {
            EmpPlanFragment ndfragment=new EmpPlanFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.upgrade_frame,ndfragment).commit();
        }*/
     /*   JobPlanFragment istFragment=new JobPlanFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.upgrade_frame,istFragment).commit();*/

    }


    @Override
    protected void onResume() {
        super.onResume();
        getSDKToken();

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

    private void requestSDKToken() {
        createFORTMobileSDKToken();
    }

    private void requestOperation(String command, String sdk_token) {
        final String ECI = "ECOMMERCE";
        final String CUSTOMER_EMAIL = "a@g.in";
        final String LANGUAGE = language;
        final String CURRENCY = "SAR";
        int f = 100;
        String price = String.valueOf(f);
        final String AMOUNT = String.valueOf(f);
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
        Log.d(TAG, "requestOperation: "+requestMap);


        fortRequest.setRequestMap(requestMap);
        fortCallback = FortCallback.Factory.create();
        boolean showLoading= true;
        try {
            FortSdk.getInstance().registerCallback(this, fortRequest, FortSdk.ENVIRONMENT.TEST,
                    5, fortCallback,showLoading, new FortInterfaces.OnTnxProcessed() {
                        @Override
                        public void onCancel(Map<String, String> requestParamsMap, Map<String, String> responseMap) {
                            Log.d(TAG, "onCancel: "+ responseMap.get("response_message"));
                            Utility.message(getApplicationContext(), responseMap.get("response_message"));
                        }
                        @Override
                        public void onSuccess(Map<String, String> requestParamsMap, Map<String, String> fortResponseMap) {
                            Log.d(TAG, "onSuccess: "+fortResponseMap.toString());
                            transaction_id = fortResponseMap.get("fort_id");
                            Utility.message(getApplicationContext(), "Payment successful ");
//                            upgradePackage(transaction_id);
                        }
                        @Override
                        public void onFailure(Map<String, String> requestParamsMap, Map<String, String> fortResponseMap) {
                            Log.d(TAG, "onFailure: "+fortResponseMap.toString());
                            Toast.makeText(getApplicationContext(), fortResponseMap.get("response_message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    final String ACCESS_TOKEN =  "qa2s6awTpBNc04Q65T8v";
    final String MERCHANT_IDENTIFIER =  "GjitDYjm";
    final String REQUEST_PHRASE = "PASS" ;


    private void createFORTMobileSDKToken() {
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

    public void getSDKToken() {
        requestSDKToken() ;
    }

    public void requestAuthorization() {
        requestOperation("AUTHORIZATION" ,sdk_token) ;
    }

    public void planrequestPurchase(View view) {
        requestOperation("PURCHASE" ,sdk_token) ;
    }

}
