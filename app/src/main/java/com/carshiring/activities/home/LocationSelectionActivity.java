package com.carshiring.activities.home;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.models.LocationAdapter;
import com.carshiring.utilities.AppBaseActivity;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.Data;
import com.carshiring.webservices.Location;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationSelectionActivity extends AppBaseActivity {
    public static final String RESPONSE_DATA = "location" ;
    public static final int RESPONSE_LOCATION = 200;
    LocationAdapter adapter ;
    RecyclerView rvLocations ;
    AutoCompleteTextView etSearchLocation;
    private String token,cityname, languagecode, keyword,TAG = LocationSelectionActivity.class.getName();
    List<Location> listLocations;
    ImageView imgBack;
    TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection);

        tinyDB = new TinyDB(getApplicationContext());
        token  = tinyDB.getString("access_token") ;
        languagecode = tinyDB.getString("language_code");
//        setLanguages(languagecode);
        actionBar=getSupportActionBar();
        if(actionBar!=null) actionBar.hide();
        etSearchLocation  =  findViewById(R.id.etSearchLocation) ;
        etSearchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                keyword  = charSequence.toString().trim() ;
                if (keyword.length()>2){
                    getLocationList(keyword,languagecode);
                }
                /*else {
                    refreshList(keyword);
                }*/
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        etSearchLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                clickonDrawable(v, event);
                return false;
            }
        });
        imgBack = findViewById(R.id.loc_back);

        rvLocations = (RecyclerView) findViewById(R.id.rvLocations) ;
        listLocations =  new ArrayList<>();
        RecyclerView.LayoutManager layoutManager  =  new LinearLayoutManager(this);
        rvLocations.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        rvLocations.addItemDecoration(itemDecoration);
        adapter = new LocationAdapter(listLocations, new LocationAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Location location) {
                Intent intent  =  new Intent();
                intent.putExtra(RESPONSE_DATA,location);
                setResult(RESPONSE_LOCATION,intent);
                finish();
            }
        });
        rvLocations.setAdapter(adapter);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

//        getLocationList();
    }

/*    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }
    public void setLanguages(String language_code){
        Locale locale = new Locale(language_code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }*/

    boolean clickonDrawable(View v, MotionEvent event)
    {
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;
        if(event.getAction() == MotionEvent.ACTION_UP) {
            if(event.getRawX() >= (etSearchLocation.getRight() - etSearchLocation.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                getLocationList(keyword,languagecode);
            }
        }
        return true;
    }

    public void getLocationList(String keyword, String languagecode) {
        if (listLocations!=null){
            listLocations.clear();
        }

        final LocationSelectionActivity _this =  this ;
        Utility.showLoading(_this,"Finding Location List...");
        RetroFitApis retroFitApis =  RetrofitApiBuilder.getCarGatesapi() ;
        Call<ApiResponse> responseCall = retroFitApis.location(token,keyword,languagecode) ;
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                Gson  gson =new Gson();
                if(response.body()!=null){
                    Log.d(TAG, "onResponse: "+gson.toJson(response.body().response));

                    if(response.body().error_code!=102){
                        Data data  = response.body().response ;
                        if(data!=null){
                            if (data.location.size()>0){
                                listLocations.addAll(data.location);
                                adapter.notifyDataSetChanged();
                            } else {
                                Utility.message(getApplicationContext(), "Location not found");
                            }

                        }
                    }else{
                            getToken(_this);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.hidepopup();
                Log.d(TAG, "onFailure: "+t.getMessage());
                Toast.makeText(LocationSelectionActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void refreshList(List<Location> filterList) {
        filterList = new ArrayList<>();

        adapter = new LocationAdapter(filterList, new LocationAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Location location) {
                Intent intent  =  new Intent();
                intent.putExtra(RESPONSE_DATA,location);
                setResult(RESPONSE_LOCATION,intent);
                finish();
            }
        });
        rvLocations.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actionBar.setTitle("Select Location");
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
    public void refreshTokenCallBack() {
        token = tinyDB.getString("access_token");
        getLocationList(keyword,languagecode);
    }
}
