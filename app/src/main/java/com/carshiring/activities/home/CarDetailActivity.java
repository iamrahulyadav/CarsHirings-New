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
import com.android.volley.DefaultRetryPolicy;
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
    public static String logo,oneway,driverSur,carPrice,carImage,modelname,currency,suppliername,suppliercity,termsurl
            ,fullprotectioncurrency,fullprotectionammount,fullProcted,time, driver_minage,driver_maxage,CDW,THP,carid;
    Gson gson = new Gson();
    public static double fullProAmt, fullAmtValue;
    public static ArrayList<ExtraBean>extralist;
    public static List<CarDetailBean.FeatureBean> carSpecificationList=new ArrayList<>();
    public static List<CoveragesBean>coveragelist;
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
        if (getIntent().hasExtra("point_earn")){
            String s = (String)getIntent().getStringExtra("point_earn");
            if (s!=null){
                point = Double.parseDouble(s);
            }
        }

        if (getIntent().hasExtra("one_way_fee")){
            oneway = getIntent().getStringExtra("one_way_fee");
        }
        if (getIntent().hasExtra("driverSur")){
            driverSur = getIntent().getStringExtra("driverSur");
        }

//        call api

//        setupApi();
        getCarDetail();
    }

    public final okhttp3.MediaType MEDIA_TYPE = okhttp3.MediaType.parse("application/json");

    JSONArray collision_damage_waiver;
    private void getCarDetail(){
       /* final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Booking in processing...");
        progressDialog.show();*/

        Utility.showloadingPopup(this);
        extralist=new ArrayList<>();
        coveragelist=new ArrayList<>();
        String url = "https://carsgates.com/webservices/webservice/car_detail";
        final CarDetailBean carDetailBean = new CarDetailBean();
        StringRequest  stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String s =response;
                Log.d("TAG", "onResponse: details"+response);
                Utility.hidepopup();
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
                        modelname = car_detailObject.getString("model");
                        String model_code = car_detailObject.getString("model_code");
                        carImage = car_detailObject.getString("image");
                        String price = car_detailObject.getString("price");
                        currency = car_detailObject.getString("currency");
                        day = car_detailObject.getString("time_unit");
                        time = car_detailObject.getString("time");
                        driver_minage = car_detailObject.getString("driver_min_age");
                        driver_maxage = car_detailObject.getString("driver_max_age");
                        String opening_hours_start = car_detailObject.getString("opening_hours_start");
                        String opening_hours_end = car_detailObject.getString("opening_hours_end");
                        if (car_detailObject.has("theft_protection")&&car_detailObject.getString("theft_protection").length()>0) {
                            Object json = new JSONTokener(car_detailObject.getString("collision_damage_waiver")).nextValue();
                            if (json instanceof String) {
                                String collision_damage_waiver = car_detailObject.getString("collision_damage_waiver");
                            } else if (json instanceof JSONArray) {
                                collision_damage_waiver = car_detailObject.getJSONArray("collision_damage_waiver");
                            }
                        }
                        markUp = Double.parseDouble(SearchCarFragment.markup);
                        double d = Double.parseDouble(price);
                        double priceNew  = d+(d*markUp)/100;
                        carPrice=String.valueOf(df2.format(priceNew));

                        CarDetailBean.FeatureBean featureBean = new CarDetailBean.FeatureBean();
                        featureBean.setAircondition(aircondition);
                        featureBean.setBag(bag);
                        featureBean.setFueltype(fueltype);
                        featureBean.setDoor(door);
                        featureBean.setTransmission(transmission);
                        featureBean.setPassenger(passenger);

                        carSpecificationList= Arrays.asList(featureBean);

                        carDetailBean.setFeature(featureBean);
                        carDetailBean.setCategory(category);
                        carDetailBean.setModel(modelname);
                        carDetailBean.setModel_code(model_code);
                        carDetailBean.setImage(carImage);
                        carDetailBean.setPrice(price);
                        carDetailBean.setCurrency(currency);
                        carDetailBean.setTime(time);
                        carDetailBean.setTime_unit(day);
                        carDetailBean.setDriver_min_age(driver_minage);
                        carDetailBean.setDriver_max_age(driver_maxage);
                        carDetailBean.setOpening_hours_start(opening_hours_start);
                        carDetailBean.setOpening_hours_end(opening_hours_end);
                        carDetailBean.setTime(time);
                        List<String>collosion = new ArrayList<>();
                        List<String>theft_protectionList = new ArrayList<>();
                        if (collision_damage_waiver!=null){
                            for (int i=0;i<collision_damage_waiver.length();i++){
                                CDW = collision_damage_waiver.getString(i);
                                collosion.add(collision_damage_waiver.getString(i));
                            }

                        }

                        carDetailBean.setCollision_damage_waiver(collosion);
                        if (car_detailObject.has("theft_protection")&&car_detailObject.getString("theft_protection").length()>0){
                            Object json = new JSONTokener(car_detailObject.getString("theft_protection")).nextValue();
                            if (json instanceof String){
                                String theft_protection = car_detailObject.getString("theft_protection");
                            }
                            else if (json instanceof JSONArray){
                                JSONArray theft_protectionjsonArray = car_detailObject.getJSONArray("theft_protection");
                                for (int i=0;i<theft_protectionjsonArray.length();i++){
                                    theft_protectionList.add(theft_protectionjsonArray.getString(i));
                                    THP = theft_protectionjsonArray.getString(i);
                                }
                                carDetailBean.setTheft_protection(theft_protectionList);
                                theft_protection.addAll(theft_protectionList);
                            }
                        }


                        String deposit_currency = car_detailObject.getString("deposit_currency");
                        String deposit_price = car_detailObject.getString("deposit_price");
                        String deposit_desc = car_detailObject.getString("deposit_desc");
                        String deposit_name = car_detailObject.getString("deposit_name");
                        if (car_detailObject.has("fullprotection_amount")&& car_detailObject.getString("fullprotection_amount")!=null){
                            fullprotectionammount="";
                            fullprotectionammount=car_detailObject.getString("fullprotection_amount");
                            if (fullprotectionammount!=null&&!fullprotectionammount.equalsIgnoreCase("null")){
                                fullProAmt = Double.parseDouble(fullprotectionammount);
                                double du = fullProAmt*3.75;
                                fullAmtValue = Double.parseDouble(df2.format(du));
                            }
                        }

                        if (car_detailObject.getString("fullprotection_currency")!=null){
                            fullprotectioncurrency="";
                            fullprotectioncurrency=car_detailObject.getString("fullprotection_currency");
                        }
                        suppliername = car_detailObject.getString("supplier");
                        suppliercity = car_detailObject.getString("supplier_city");
                        logo = car_detailObject.getString("supplier_logo");
                        String drop_city = car_detailObject.getString("drop_city");
                        termsurl = car_detailObject.getString("tc");
                        JSONArray extraJsonArray = car_detailObject.getJSONArray("extra");
                        JSONArray coveragesArray = car_detailObject.getJSONArray("coverages");
                        JSONObject cancledetail = car_detailObject.getJSONObject("cancledetail");
                        carDetailBean.setDeposit_currency(deposit_currency);
                        carDetailBean.setDeposit_price(deposit_price);
                        carDetailBean.setDeposit_desc(deposit_desc);
                        carDetailBean.setDeposit_name(deposit_name);
                        carDetailBean.setFullprotection_currency(fullprotectioncurrency);
                        carDetailBean.setFullprotection_amount(fullprotectionammount);
                        carDetailBean.setSupplier(suppliername);
                        carDetailBean.setSupplier_city(suppliercity);
                        carDetailBean.setSupplier_logo(logo);
//                        carDetailBean.setd(drop_city);
                        carDetailBean.setTc(termsurl);

                        for (int i =0; i<extraJsonArray.length();i++){
                            ExtraBean extraBean = new ExtraBean();
                            JSONObject jsonObject1 = extraJsonArray.getJSONObject(i);
                            if (jsonObject1.getString("name")!=null){
                                extraBean.setName(jsonObject1.getString("name"));
                                extraBean.setCurrency(jsonObject1.getString("currency"));
                                extraBean.setPrice(jsonObject1.getString("price"));
                                extraBean.setType(jsonObject1.getString("type"));
                                extralist.add(extraBean);
                            }

                        }
                        if (extralist!=null&&extralist.size()>0){
                            for (int i=0; i<extralist.size();i++){
                                if (extralist.get(i).name!=null){
                                    if (extralist.get(i).getName().equalsIgnoreCase("Full Protection")){
                                        extralist.remove(i);
                                    }
                                }
                            }
                        }
                        carDetailBean.setExtra(extralist);
                        List<CoveragesBean>coveragesBeans = new ArrayList<>();
                        for (int i=0;i<coveragesArray.length();i++){
                            CoveragesBean coveragesBean = new CoveragesBean();
                            JSONObject jsonObject1 = coveragesArray.getJSONObject(i);
                            coveragesBean.setAmount(jsonObject1.getString("amount"));
                            coveragesBean.setCurrency(jsonObject1.getString("currency"));
                            coveragesBean.setDesc(jsonObject1.getString("desc"));
                            coveragesBean.setName(jsonObject1.getString("name"));
                            coveragelist.add(coveragesBean);

                        }

                        handletablayout();
                        carDetailBean.setCoverages(coveragesBeans);

                        Log.d("TAG", "onResponse: carsdta"+gson.toJson(carDetailBean));

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
                Utility.hidepopup();
                Utility.message(getApplicationContext(), getResources().getString(R.string.no_internet_connection));
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String>cateRequest = new HashMap<>();
                cateRequest.put("access_token",token);
                cateRequest.put("id_context",id_context);
                cateRequest.put("type",type);
                cateRequest.put("day",day);
                cateRequest.put("refer_type",refer_type);
                Log.d("TAG", "getParams: "+cateRequest);
                return cateRequest;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
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
