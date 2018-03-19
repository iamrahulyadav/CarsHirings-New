package com.carshiring.activities.home;

import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.carshiring.R;
import com.carshiring.adapters.Page_Adapter;
import com.carshiring.fragments.CarDetailTab1Fragment;
import com.carshiring.fragments.CarDetailTab2Fragment;
import com.carshiring.fragments.CarDetailTab3Fragment;
import com.carshiring.models.CarDetailBean;
import com.carshiring.models.CoveragesBean;
import com.carshiring.models.ExtraBean;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CarDetailActivity extends AppCompatActivity {
    String token="fa6e9fcf829fbf3217d8e361421acf2c588903aa";
    String type="0";
    String refer_type="16";
    String day="2";
    String id_context="6226549648764871585476470";
    TabLayout tabLayout;
    Page_Adapter adapter;
    ActionBar actionBar;
    TinyDB tinyDB ;

    public static String logo,carPrice,carImage,modelname,currency,suppliername,suppliercity,termsurl
            ,fullprotectioncurrency,fullprotectionammount,fullProcted,driver_minage,driver_maxage,CDW,THP,carid;
    Gson gson = new Gson();
    public static ArrayList<ExtraBean> extralist=new ArrayList<>();
    public static List<CarDetailBean.FeatureBean> carSpecificationList=new ArrayList<>();
    public static List<CoveragesBean> coveragelist=new ArrayList<>();
    public static List<String> theft_protection=new ArrayList<>();

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

//        call api

        setupApi();
    }

    private void setupApi() {

        Utility.showloadingPopup(this);
        RetroFitApis retroFitApis= RetrofitApiBuilder.getCarGatesapi();
        Call<ApiResponse> apiResponseCall=retroFitApis.car_detail(token,id_context,type,day,refer_type);

        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

               if (response.body()!=null){
                   CarDetailBean carDetailBean = new CarDetailBean();
                   carDetailBean = response.body().response.car_detail;
                   Log.d("TAG", "onResponse: "+gson.toJson(carDetailBean));
                   carImage=response.body().response.car_detail.image;
                   Log.d("respsonse",carImage);
                   logo=response.body().response.car_detail.supplier_logo;
                   Log.d("respsonse",logo);
                   modelname=response.body().response.car_detail.model;
                   carPrice=response.body().response.car_detail.price;
                   currency=response.body().response.car_detail.currency;
                   extralist= (ArrayList<ExtraBean>) response.body().response.car_detail.extra;
                   carSpecificationList= Arrays.asList(response.body().response.car_detail.feature);
                   suppliername=response.body().response.car_detail.supplier;
                   suppliercity=response.body().response.car_detail.supplier_city;
                   termsurl=response.body().response.car_detail.tc;
                   fullprotectioncurrency=response.body().response.car_detail.fullprotection_currency;
                   fullprotectionammount=response.body().response.car_detail.fullprotection_amount;
                   coveragelist=response.body().response.car_detail.coverages;
                   driver_minage=response.body().response.car_detail.driver_min_age;
                   driver_maxage=response.body().response.car_detail.driver_max_age;
                   theft_protection=response.body().response.car_detail.collision_damage_waiver;
//                   carid = response.body().response.car_detail.ca
                   for (int i=0;i<1;i++) {
                       CDW = response.body().response.car_detail.collision_damage_waiver.get(i);
                   }
                   for (int i=0;i<1;i++) {
                       THP = response.body().response.car_detail.theft_protection.get(i);
                   }
                   Log.d("respsonse",""+carSpecificationList.size());
                   handletablayout();
               }

            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.error) ,Toast.LENGTH_SHORT).show();
                Utility.hidepopup();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        tinyDB.remove("extra_added");
        tinyDB.remove("full_prot");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /*Intent intent = new Intent(getApplicationContext(), TeacherSide.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
            // you don't need to call finish(); because
            // return super.onKeyDown(keyCode, event); does that for\ you

            // clear your SharedPreferences
            tinyDB.remove("extra_added");
            tinyDB.remove("full_prot");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actionBar.setTitle(getResources().getString(R.string.car_details));
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
