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
import com.carshiring.models.BookingRequest;
import com.carshiring.models.Category;
import com.carshiring.models.ExtraAdded;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;

import static android.content.ContentValues.TAG;

public class PayActivity extends AppBaseActivity {
    ActionBar actionBar;
    public List<ExtraAdded> extraData = new ArrayList<>();
    TextView txtPay, txtTotalAmyVal,txtWalletBal,txtPointVal;
    CheckBox txtcheckPoint,txtCheckPay, txtCheckWallet;
    public String price="", email="",sdk_token="";
    public FortCallBackManager fortCallback;
    final String ACCESS_TOKEN =  "qa2s6awTpBNc04Q65T8v";
    final String MERCHANT_IDENTIFIER = "GjitDYjm";
    final String REQUEST_PHRASE = "PASS" ;
    String name="",sarname="",number="",address="",city="",zipcode="",countrycode="",car_id="",
            type="",rtype="",
            fullprotection="",flight_no="",extradata="",dob="",user_id="",pick_date="",
            drop_date="", pick_city="",drop_city="",protection_val="",booking_point="",booking_wallet="",
            booking_payfort="",transaction_id="",language="";
    TinyDB tinyDB;
    public List<WalletHistoryData> walletHistoryData = new ArrayList<>();
    public List<PointHistoryData>pointHistoryData = new ArrayList<>();
    double creditAmt, debitAmt,walletAmt, totalDebit, totalPrice,totalCredit,totalPoint,pointValue,totalDebitPoint,
            totalCreditPoint, creditPoint, debitPoint, walBal,payfortAmt;
    ;
    Gson gson = new Gson();
    UserDetails userDetails = new UserDetails();
    BookingRequest bookingRequest = new BookingRequest();

    @SuppressLint("SetTextI18n")
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
        flight_no = BookCarActivity.flight_no;
        car_id = CarsResultListActivity.id_context;
        type = CarsResultListActivity.type;
        rtype = CarsResultListActivity.refertype;
        pick_city = SearchCarFragment.pickName;
        pick_date = SearchCarFragment.pick_date;
        drop_city = SearchCarFragment.dropName;
        drop_date = SearchCarFragment.drop_date;
        fullprotection = BookCarActivity.fullProtection;
        protection_val = BookCarActivity.protection_val;
        extraData = BookCarActivity.extraData;

        price = CarDetailActivity.carPrice;
        totalPrice = Double.parseDouble(price);
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
                if (txtCheckWallet.isChecked()){
                    if (walletAmt>totalPrice){
                        booking_wallet=String.valueOf(walletAmt);
                        walBal = walletAmt-totalPrice;   // remaining wallet balance
                        transaction_id = "";
                        String s = gson.toJson(bookingRequest);
                        Log.d(TAG, "onSuccess: "+s);
                        txtCheckPay.setChecked(false);
                        makeBooking(s);
                    } else {
                        payfortAmt = totalPrice-walletAmt;
                        txtCheckPay.setChecked(true);
                        requestPurchase(String.valueOf(payfortAmt));
                    }
                } else {
                    booking_payfort = price;
                    payfortAmt = Double.parseDouble(booking_payfort);
                    requestPurchase(String.valueOf(payfortAmt));
                }

                if (txtcheckPoint.isChecked()){
                    booking_point = "0.0";
                    if (totalPoint>totalPrice){
                        booking_wallet="0.0";
                        walBal = walletAmt-totalPrice;   // remaining wallet balance
                        transaction_id = "";
                        String s = gson.toJson(bookingRequest);
                        Log.d(TAG, "onSuccess: "+s);
                        booking_wallet=price;
                        booking_payfort="0.0";
                        makeBooking(s);
                    } else {
                        payfortAmt = totalPrice-walletAmt;
                        booking_wallet = String.valueOf(walletAmt);
                        booking_payfort = String.valueOf(payfortAmt);
                        txtCheckPay.setChecked(true);
                        requestPurchase(String.valueOf(payfortAmt));
                    }
                } else {
                    booking_payfort = price;
                    booking_point = "0.0";
                    booking_wallet = "0.0";
                    payfortAmt = Double.parseDouble(booking_payfort);
                    requestPurchase(String.valueOf(payfortAmt));
                }




               /* if (txtcheckPoint.isChecked()){
                    booking_point = String.valueOf(pointValue);
                } else {
                    booking_point = String.valueOf(pointValue);
                }
                if (txtCheckPay.isChecked()){
                    booking_payfort = price;
                }*/
                bookingRequest.setName(name);
                bookingRequest.setSarname(sarname);
                bookingRequest.setNumber(number);
                bookingRequest.setEmail(email);
                bookingRequest.setAddress(address);
                bookingRequest.setCity(city);
                bookingRequest.setZipcode(zipcode);
                bookingRequest.setCountrycode(countrycode);
                bookingRequest.setCar_id(car_id);
                bookingRequest.setType(type);
                bookingRequest.setRtype(rtype);
                bookingRequest.setFullprotection(fullprotection);
                bookingRequest.setFlight_no(flight_no);
                bookingRequest.setDob(dob);
                bookingRequest.setUser_id(user_id);
                bookingRequest.setPick_date(pick_date);
                bookingRequest.setDrop_date(drop_date);
                bookingRequest.setPick_city(pick_city);
                bookingRequest.setDrop_city(drop_city);
                bookingRequest.setProtection_val(protection_val);
                bookingRequest.setBooking_point(booking_point);
                bookingRequest.setBooking_wallet(booking_wallet);
                bookingRequest.setBooking_payfort(String.valueOf(payfortAmt));
                bookingRequest.setExtraData(extraData);
                String s = gson.toJson(bookingRequest);
                Log.d(TAG, "onClick: "+s);
            }
        });
        txtTotalAmyVal.setText(CarDetailActivity.currency+ " "+CarDetailActivity.carPrice);
        getPoint();
        getWal();
    }

    @Override
    protected void onResume() {
        super.onResume();
        actionBar.setTitle(getResources().getString(R.string.txtPayNow));
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

    private void requestOperation(String command, String sdk_token, String price) {
        final String ECI = "ECOMMERCE";
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
                            bookingRequest.setTransaction_id(transaction_id);
                            String s = gson.toJson(bookingRequest);
                            Log.d(TAG, "onSuccess: "+s);
                            makeBooking(s);
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
        requestOperation("AUTHORIZATION" ,sdk_token,"") ;
    }

    public void requestPurchase(String payfortAmt) {
        requestOperation("PURCHASE" ,sdk_token,payfortAmt) ;
    }

    public void bookCar(String s){
        Utility.showLoading(this,"Searching cars...");
        RetroFitApis retroFitApis = RetrofitApiBuilder.getCargHiresapis();
        retrofit2.Call<ApiResponse> responseCall = retroFitApis.bookCar("");
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
    public final okhttp3.MediaType MEDIA_TYPE = okhttp3.MediaType.parse("application/json");
    public void makeBooking(String cateRequest){
        Utility.showloadingPopup(this);
        String cat = gson.toJson(cateRequest);

        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(MEDIA_TYPE,cateRequest);

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://carshiring.com/webservice/make_booking")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.SECONDS)
                .writeTimeout(10000, TimeUnit.SECONDS)
                .readTimeout(30000, TimeUnit.SECONDS)
                .build();

        Utility.showloadingPopup(this);
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = e.getMessage();
                        Utility.message(getApplicationContext(), getResources().getString(R.string.no_internet_connection));
                        Utility.hidepopup();
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Utility.hidepopup();
                if (response!=null&&response.body().toString().length()>0){
                    if (request.body()!=null){
                        String msg = response.body().string();

                        Log.d("TAG", "onResponse: "+ msg);
                    }
                }
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
                           booking_wallet=String.valueOf(walletAmt);
                           txtWalletBal.setVisibility(View.VISIBLE);
                           txtCheckWallet.setVisibility(View.VISIBLE);

                       } else {
                           booking_wallet=String.valueOf(walletAmt);
                           txtWalletBal.setVisibility(View.GONE);
                           txtCheckWallet.setVisibility(View.GONE);

                       }
                   } else {
                       Log.d(TAG, "onResponse: "+response.body().message);
                      // Utility.message(getApplicationContext(),response.body().message);
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
                        totalPoint = totalCreditPoint-totalDebitPoint;
                        pointValue = totalPoint*0.05;
                        Log.d("TAG", "onResponse: totalDebit"+totalCreditPoint+"\n"+pointValue);
//                    txtPointVal.setText(String.valueOf(totalPoint));
                        if (totalPoint>0.0){
                            txtPointVal.setText("("+totalPoint+" Point value is : "+String.valueOf(pointValue)+")");
                            booking_point = String.valueOf(pointValue);
                            txtPointVal.setVisibility(View.VISIBLE);
                            txtcheckPoint.setVisibility(View.VISIBLE);
                        } else {
                            booking_point = String.valueOf(pointValue);
                            txtPointVal.setVisibility(View.GONE);
                            txtcheckPoint.setVisibility(View.GONE);
                        }
                    } else {
                        Log.d(TAG, "onResponse: "+response.body().message);
                      //  Utility.message(getApplicationContext(),response.body().message);
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
