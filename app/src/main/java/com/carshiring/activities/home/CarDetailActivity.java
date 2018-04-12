package com.carshiring.activities.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.carshiring.R;
import com.carshiring.adapters.Page_Adapter;
import com.carshiring.fragments.CarDetailTab1Fragment;
import com.carshiring.fragments.CarDetailTab2Fragment;
import com.carshiring.fragments.CarDetailTab3Fragment;
import com.carshiring.fragments.SearchCarFragment;
import com.carshiring.models.CarDetailBean;
import com.carshiring.models.CoveragesBean;
import com.carshiring.models.ExtraBean;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CarDetailActivity extends AppCompatActivity {
    String token="fa6e9fcf829fbf3217d8e361421acf2c588903aa";
    public static String type="0";
    public static String refer_type="16", day="2";

    public static String id_context="6226549648764871585476470";
    TabLayout tabLayout;
    Page_Adapter adapter;
    ActionBar actionBar;
    TinyDB tinyDB ;
    double markUp;
    public static double point;
    public static String logo,carPrice,carImage,modelname,currency,suppliername,suppliercity,termsurl
            ,fullprotectioncurrency,fullprotectionammount,fullProcted,time, driver_minage,driver_maxage,CDW,THP,carid;
    Gson gson = new Gson();
    public static double fullProAmt, fullAmtValue;
    public static ArrayList<ExtraBean> extralist=new ArrayList<>();
    public static List<CarDetailBean.FeatureBean> carSpecificationList=new ArrayList<>();
    public static List<CoveragesBean> coveragelist=new ArrayList<>();
    public static List<String> theft_protection=new ArrayList<>();
    private String urla="https://static.carhire-solutions.com/images/car/Alamo/large/t_MCAR_AE.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);

        actionBar = getSupportActionBar() ;
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }

        tinyDB = new TinyDB(getApplicationContext());
        token = tinyDB.getString("access_token");
        type = getIntent().getStringExtra("type");
        refer_type = getIntent().getStringExtra("refer_type");
        day = getIntent().getStringExtra("day");
        id_context = getIntent().getStringExtra("id_context");
        String s = getIntent().getStringExtra("point_earn");
        point = Double.parseDouble(s);

//        call api

        setupApi();
//        getCarDetail();
    }

    public final okhttp3.MediaType MEDIA_TYPE = okhttp3.MediaType.parse("application/json");


    private void getCarDetail(){
       /* final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Booking in processing...");
        progressDialog.show();*/


        String url = "https://carsgates.com/webservices/webservice/car_detail";

        StringRequest  stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String s =response;
                Log.d("TAG", "onResponse: details"+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean status = jsonObject.getBoolean("status");
                    if (status){
                        JSONObject responseObject = jsonObject.getJSONObject("response");
                        JSONObject car_detailObject = responseObject.getJSONObject("car_detail");
                        JSONObject featureObject = car_detailObject.getJSONObject("feature");
                        String aircondition = featureObject.getString("aircondition");
                        String transmission = featureObject.getString("transmission");
                        String fueltype = featureObject.getString("fueltype");
                        String bag = featureObject.getString("bag");
                        String passenger = featureObject.getString("passenger");
                        String door = featureObject.getString("door");
                        String category = car_detailObject.getString("category");
                        String model= car_detailObject.getString("model");
                        String model_code = car_detailObject.getString("model_code");
                        String image = car_detailObject.getString("image");
                        String price = car_detailObject.getString("price");
                        String currency = car_detailObject.getString("currency");
                        String time_unit = car_detailObject.getString("time_unit");
                        String time = car_detailObject.getString("time");
                        String driver_min_age = car_detailObject.getString("driver_min_age");
                        String driver_max_age = car_detailObject.getString("driver_max_age");
                        String opening_hours_start = car_detailObject.getString("opening_hours_start");
                        String opening_hours_end = car_detailObject.getString("opening_hours_end");
                        JSONArray collision_damage_waiver = car_detailObject.getJSONArray("collision_damage_waiver");
                        if (car_detailObject.has("theft_protection")){
                            Object json = new JSONTokener(car_detailObject.getString("theft_protection")).nextValue();
                            if (json instanceof String){
                                String theft_protection = car_detailObject.getString("theft_protection");
                            }
                            else if (json instanceof JSONArray){
                                JSONArray jsonArray = car_detailObject.getJSONArray("theft_protection");
                            }
                        }

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onFailure: detail"+error);

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String>cateRequest = new HashMap<>();
        /*l(token,id_context,type,day,refer_type);*/
                cateRequest.put("access_token",token);
                cateRequest.put("id_context",id_context);
                cateRequest.put("type",type);
                cateRequest.put("day",day);
                cateRequest.put("refer_type",refer_type);
                return cateRequest;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

       /*
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(MEDIA_TYPE, String.valueOf(cateRequest));

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://carsgates.com/webservices/webservice/car_detail")
                .post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.SECONDS)
                .writeTimeout(10000, TimeUnit.SECONDS)
                .readTimeout(30000, TimeUnit.SECONDS)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("TAG", "onFailure: detail"+e.getMessage());
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String s = gson.toJson(response.body());
                Log.d("TAG", "onResponse: details"+s);
            }
        });
*/
    }

    private void setupApi() {
        Utility.showloadingPopup(this);
        RetroFitApis retroFitApis= RetrofitApiBuilder.getCarGatesapi();
        Call<ApiResponse> apiResponseCall=retroFitApis.car_detail(token,id_context,type,day,refer_type);
        Log.d("TAG", "setupApi: "+token+"\n"+id_context+"\n"+type+"\n"+day+"\n"+refer_type);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                Log.d("TAG", "onResponse:cardetails "+gson.toJson(response.body()));
               if (response.body()!=null){
                   if (response.body().status){
                       CarDetailBean carDetailBean = new CarDetailBean();
                       carDetailBean = response.body().response.car_detail;
                       carImage=response.body().response.car_detail.image;
                       logo=response.body().response.car_detail.supplier_logo;
                       modelname=response.body().response.car_detail.model;
                       markUp = Double.parseDouble(SearchCarFragment.markup);
                       time = response.body().response.car_detail.time;
                       day = response.body().response.car_detail.time_unit;
                       String price =response.body().response.car_detail.price;
                       double d = Double.parseDouble(price);
                       double priceNew  = d+(d*markUp)/100;
                       carPrice=String.valueOf(df2.format(priceNew));
                       currency=response.body().response.car_detail.currency;
                       if (response.body().response.car_detail.extra!=null){
                           extralist= (ArrayList<ExtraBean>) response.body().response.car_detail.extra;
                           for (int i=0; i<extralist.size();i++){
                               if (extralist.get(i).name!=null){
                                   if (extralist.get(i).getName().equalsIgnoreCase("Full Protection")){
                                       extralist.remove(i);
                                   }
                               }
                           }
                       }

                       carSpecificationList= Arrays.asList(response.body().response.car_detail.feature);
                       suppliername=response.body().response.car_detail.supplier;
                       suppliercity=response.body().response.car_detail.supplier_city;
                       termsurl=response.body().response.car_detail.tc;
                       if (response.body().response.car_detail.fullprotection_amount!=null){
                           fullprotectionammount=response.body().response.car_detail.fullprotection_amount;
                           fullProAmt = Float.parseFloat(fullprotectionammount);
                           double du = fullProAmt*3.75;
                           fullAmtValue = Double.parseDouble(df2.format(du));

                       }
                       if (response.body().response.car_detail.fullprotection_currency!=null){
                           fullprotectioncurrency=response.body().response.car_detail.fullprotection_currency;
                       }

                       coveragelist=response.body().response.car_detail.coverages;
                       driver_minage=response.body().response.car_detail.driver_min_age;
                       driver_maxage=response.body().response.car_detail.driver_max_age;
                       if (response.body().response.car_detail.theft_protection!=null){
                           theft_protection=response.body().response.car_detail.theft_protection;
                       }
//                   carid = response.body().response.car_detail.ca
                       if (response.body().response.car_detail.collision_damage_waiver!=null){
                           for (int i=0;i<1;i++) {
                               CDW = response.body().response.car_detail.collision_damage_waiver.get(i);
                           }
                       }

                       if (response.body().response.car_detail.theft_protection!=null){
                           for (int i=0;i<1;i++) {
                               THP = response.body().response.car_detail.theft_protection.get(i);
                           }
                       }

                       handletablayout();
                   } else {
                       Utility.message(getApplicationContext(), response.body().msg);
                   }
               }

            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("TAG", "onFailure: "+t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection) ,Toast.LENGTH_SHORT).show();
                Utility.hidepopup();
            }
        });
    }


    public static boolean isValid(String url)
    {
        /* Try creating a valid URL */
        try {
            new URL(url).toURI();
            return true;
        }

        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }



    private static DecimalFormat df2 = new DecimalFormat(".##");

    @Override
    protected void onResume() {
        super.onResume();
        actionBar.setTitle(getResources().getString(R.string.car_details));
        /*tinyDB.remove("extra_added");
        tinyDB.remove("full_prot");*/
        setupToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handletablayout() {
        Utility.hidepopup();
        View view1=getLayoutInflater().inflate(R.layout.tabstyle,null);
        View view2=getLayoutInflater().inflate(R.layout.tabstyle,null);
        View view3=getLayoutInflater().inflate(R.layout.tabstyle,null);
        view1.findViewById(R.id.img_tab).setBackgroundResource(R.mipmap.ic_launcher);
        ImageView tab2= (ImageView) view2.findViewById(R.id.img_tab);
        Glide.with(getApplicationContext()).load(logo).into(tab2);
        view3.findViewById(R.id.img_tab).setBackgroundResource(R.drawable.ic_tab3);
        tabLayout= (TabLayout) findViewById(R.id.cardet_Tab);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view1));
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));
        tabLayout.addTab(tabLayout.newTab().setCustomView(view3));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        CarDetailTab1Fragment tab1Fragment=new CarDetailTab1Fragment();
        CarDetailTab2Fragment tab2Fragment=new CarDetailTab2Fragment();
        CarDetailTab3Fragment tab3Fragment=new CarDetailTab3Fragment();
        final ViewPager pager= (ViewPager) findViewById(R.id.cardet_viewpager);
        adapter=new Page_Adapter(getSupportFragmentManager(),tabLayout.getTabCount(),tab1Fragment,tab2Fragment,tab3Fragment);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar= (Toolbar) findViewById(R.id.bottomToolBar);
        TextView textView= (TextView) toolbar.findViewById(R.id.txt_bot);
        textView.setText(getResources().getString(R.string.book_this_car));
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(CarDetailActivity.this, BookCarActivity.class);
                it.putExtra("get","FromActi");
                startActivity(it);
            }
        });
    }



}
