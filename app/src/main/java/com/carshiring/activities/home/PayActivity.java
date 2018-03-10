package com.carshiring.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.models.TokenResponse;
import com.carshiring.utilities.Utility;
import com.google.gson.Gson;
import com.payfort.fort.android.sdk.base.FortSdk;
import com.payfort.fort.android.sdk.base.callbacks.FortCallBackManager;
import com.payfort.fort.android.sdk.base.callbacks.FortCallback;
import com.payfort.sdk.android.dependancies.base.FortInterfaces;
import com.payfort.sdk.android.dependancies.models.FortRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PayActivity extends AppCompatActivity {
    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
         bt = (Button) findViewById(R.id.a);
         getSDKToken();
         bt.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (sdk_token!=null){
                     requestPurchase();
                 }
             }
         });
    }
    private FortCallBackManager fortCallback;

    private String sdk_token  ;

    private void requestSDKToken() {
        createFORTMobileSDKToken();
    }

    private void requestOperation(String command, String sdk_token) {
        final String ECI = "ECOMMERCE";
        final String CUSTOMER_EMAIL = "imnotalone30@gmail.com";
        final String LANGUAGE = "en";
        final String CURRENCY = "SAR";
        final String AMOUNT = "10000";
        final String MERCHANT_REFERENCE =  Utility.getRandomString(40) ;
        FortRequest fortRequest = new FortRequest();
        fortRequest.setShowResponsePage(true);
        Map<String, String> requestMap = new HashMap<>();


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
//        requestMap.put("phone_number",ECI);
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
                            Toast.makeText(PayActivity.this, responseMap.get("response_message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess(Map<String, String> requestParamsMap, Map<String, String> fortResponseMap) {
                            Toast.makeText(PayActivity.this, fortResponseMap.get("response_message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onFailure(Map<String, String> requestParamsMap, Map<String, String> fortResponseMap) {
                            Toast.makeText(PayActivity.this, fortResponseMap.get("response_message"),
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
        String device_id = FortSdk.getDeviceId(this);
        StringBuilder base = new StringBuilder();
        base.append(REQUEST_PHRASE)
                .append("access_code=").append(ACCESS_TOKEN)
                .append("device_id=").append(device_id)
                .append("language=").append("en")
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
            jsonObject.put("language","en") ;
            jsonObject.put("signature",signature) ;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
        Request request;
        request = new Request.Builder()
                .url("https://sbpaymentservices.payfort.com/FortAPI/paymentApi")
                .method("POST", RequestBody.create(null, new byte[0]))
                .post(body)
                .addHeader("Content-Type", "application/json; charset=utf8")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                //call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {

                String mMessage = response.body().string();
                if (response.isSuccessful()){
                    try {
                        JSONObject json = new JSONObject(mMessage);
                        Gson gson = new Gson();
                        TokenResponse tokenResponse = new TokenResponse();
                        tokenResponse = gson.fromJson(json.toString(),TokenResponse.class);
                        sdk_token = tokenResponse.getSdk_token();
                        Toast.makeText(PayActivity.this, sdk_token, Toast.LENGTH_SHORT).show();

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fortCallback.onActivityResult(requestCode,resultCode,data);
    }


    public void getSDKToken() {
        requestSDKToken() ;
    }

    public void requestAuthorization(View view) {
        requestOperation("AUTHORIZATION" ,sdk_token) ;
    }

    public void requestPurchase() {
        requestOperation("PURCHASE" ,sdk_token) ;
    }

}
