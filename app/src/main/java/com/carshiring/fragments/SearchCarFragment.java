package com.carshiring.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.carshiring.R;
import com.carshiring.activities.home.CarsResultListActivity;
import com.carshiring.activities.home.LocationSelectionActivity;
import com.carshiring.activities.home.MainActivity;
import com.carshiring.activities.home.SearchQuery;
import com.carshiring.activities.home.SearchbyMapActivity;
import com.carshiring.models.CatRequest;
import com.carshiring.models.Category;
import com.carshiring.models.MArkupdata;
import com.carshiring.models.Point;
import com.carshiring.models.SearchData;
import com.carshiring.splash.SplashActivity;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.Location;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.mukesh.tinydb.TinyDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import in.technobuff.helper.utils.PermissionRequestHandler;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.internal.zzahg.runOnUiThread;


public class SearchCarFragment extends BaseFragment implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {
    private static final int REQUEST_PICKUP_LOCATION = 500;
    private static final int REQUEST_DESTINATION_LOCATION = 501;
    private static final int REQUEST_BY_MAP_LOCATION = 502;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    Location location = new Location();
    EditText et_return_location, et_pickup_location, et_driver_age;

    MainActivity activity;
    SearchQuery searchQuery;
    SwitchCompat switchSameDestLocation,switchDriverAge;
    private GoogleApiClient mgoogleApiclient;
    CheckBox chkUseCurrentLocation;
    TinyDB tinyDB ;
    private String token ;
    Calendar calendar_pick, calendar_drop;

    public static String pickName ="",pickup_loc_id="",drop_loc_id="", dropName="",drop_date="",pick_date="",
            drop_hour="",drop_minute="",pick_minute="",markup="",pointper="",pick_hour="",pickTime="", dropTime="";

    int useCurrentLocation = 0;
    int useSameDestLocation = 1;
    int isBetweenDriverAge = 1 ;
    int pick_hours,pick_minutes,drop_hours, drop_minutes;
    private double currentLat,currentLng;
    String driver_age="",languagecode ,
            location_code="",location_iata="",location_type="",location_code_drop="",location_iata_drop="",
            location_type_drop="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_car, container, false);
        tinyDB = new TinyDB(getActivity().getApplicationContext());
        token = tinyDB.getString("access_token");
        languagecode = tinyDB.getString("language_code");
        et_return_location = (EditText) view.findViewById(R.id.et_return_location);
        et_return_location.setOnClickListener(this);
        et_pickup_location = (EditText) view.findViewById(R.id.et_pickup_location);
        et_pickup_location.setOnClickListener(this);

        et_driver_age = (EditText) view.findViewById(R.id.et_driver_age);

        final TextView btn_search_car = (TextView) view.findViewById(R.id.btn_search_car);
        btn_search_car.setOnClickListener(this);

        switchSameDestLocation = (SwitchCompat) view.findViewById(R.id.switchSameDestLocation);
        switchSameDestLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    et_return_location.setVisibility(View.GONE);
                    if (pickup_loc_id != null && !pickup_loc_id.isEmpty())
                        drop_loc_id = pickup_loc_id;
                } else {
                    drop_loc_id = null ;
                    et_return_location.setVisibility(View.VISIBLE);
                }
            }
        });
        switchDriverAge = (SwitchCompat) view.findViewById(R.id.switchDriverAge);
        switchDriverAge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    driver_age="";
                    et_driver_age.setVisibility(View.GONE);
                } else {
                    et_driver_age.setVisibility(View.VISIBLE);
                }

            }
        });

        final LinearLayout dt_picker_journey = (LinearLayout) view.findViewById(R.id.dt_picker_journey);

        final TextView tvJourneyDatePicker = (TextView) dt_picker_journey.findViewById(R.id.tvDateDayValue);
        tvJourneyDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDataPicker("journey");
            }
        });
        final TextView tvJourneyFullTimePicker = (TextView) dt_picker_journey.findViewById(R.id.tvFullTime);
        tvJourneyFullTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker("journey");
            }
        });
        final LinearLayout dt_picker_returning = (LinearLayout) view.findViewById(R.id.dt_picker_returning);
        final TextView tvReturningDatePicker = (TextView) dt_picker_returning.findViewById(R.id.tvDateDayValue);
        tvReturningDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDataPicker("returning");
            }
        });
        final TextView tvReturningFullTimePicker = (TextView) dt_picker_returning.findViewById(R.id.tvFullTime);
        tvReturningFullTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker("returning");
            }
        });


        chkUseCurrentLocation = (CheckBox) view.findViewById(R.id.chkUseCurrentLocation);
        chkUseCurrentLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //comment for testing
                if (isChecked) {
                    et_pickup_location.setText("");
                    et_pickup_location.setEnabled(false);
                    if (Utility.checkGooglePlayService(getActivity()))
                        setupLocation();
                }else{
                    et_pickup_location.setEnabled(true);
                }
            }
        });

        return view;
    }

    private void showTimePicker(final String type) {

        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                bindTimeToGUI(type, i, i1);
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void bindTimeToGUI(String type, int i, int i1) {
        String timeStr = setFullTime(i, i1);
        DateFormat monthFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Date date = null;
        String output = null;
        try{
            //Converting the input String to Date
            date= monthFormat.parse(timeStr);
            //Changing the format of date and storing it in String
            output = dateFormat.format(date);
            //Displaying the date
            System.out.println(output);
        }catch(ParseException pe){
            pe.printStackTrace();
        }
        calendar_drop.set(calendar_drop.get(Calendar.YEAR),calendar_drop.get(Calendar.MONTH),
                calendar_drop.get(Calendar.DAY_OF_MONTH),i,i1);
        switch (type) {
            case "journey":
                pick_hours = i ;
                pick_minutes =i1 ;
                final LinearLayout dt_picker_journey = (LinearLayout) view.findViewById(R.id.dt_picker_journey);
                final TextView tvPickFullTime = (TextView) dt_picker_journey.findViewById(R.id.tvFullTime);
                tvPickFullTime.setText(getResources().getString(R.string.txtTime) +" : " + output);
                pickTime= timeStr;
                calendar_pick.set(calendar_pick.get(Calendar.YEAR),calendar_pick.get(Calendar.MONTH),
                        calendar_pick.get(Calendar.DAY_OF_MONTH),i,i1);
                bindTimeToGUI("returning", calendar_drop.get(Calendar.HOUR_OF_DAY), calendar_drop.get(Calendar.MINUTE));
                break;

            case "returning":
                drop_hours = i ;
                drop_minutes = i1 ;
                final LinearLayout dt_picker_returning = (LinearLayout) view.findViewById(R.id.dt_picker_returning);
                final TextView tvDropFullTime = (TextView) dt_picker_returning.findViewById(R.id.tvFullTime);
                dropTime=timeStr;
                tvDropFullTime.setText(getResources().getString(R.string.txtTime) +" : " + output);
                break;
        }
    }

    private String setFullTime(int hoursOfDay, int minutes) {

        String result;

        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 3, 1, hoursOfDay, minutes);
//        SimpleDateFormat monthFormat = new SimpleDateFormat("h:mm:a", Locale.US);
        SimpleDateFormat monthFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        result = monthFormat.format(calendar.getTime());
        return result;
    }

    private void showDataPicker(final String type) {
        Calendar calendar = calendar_pick;
        if(type.equals("returning")){
            calendar = calendar_drop ;
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                bindDateToGUI(type, i, i1, i2);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void bindDateToGUI(String type, int i, int i1, int i2) {

        String dayMon = setFullDate(i, i1, i2);
        final String dateValueString = setValueFullDate(i, i1, i2);
        switch (type) {
            case "journey":
                final LinearLayout dt_picker_journey = (LinearLayout) view.findViewById(R.id.dt_picker_journey);
                final TextView tvJourneyDatePicker = (TextView) dt_picker_journey.findViewById(R.id.tvDateDayValue);
                final TextView tvJourneyDateDayNameWithMonthName = (TextView) dt_picker_journey.findViewById(R.id.tvDateDayNameWithMonthName);

                calendar_pick.set(i,i1,i2);
                tvJourneyDatePicker.setText(String.valueOf(i2));
                tvJourneyDateDayNameWithMonthName.setText(dayMon);
                pick_date = dateValueString;
                calendar_drop  = (Calendar) calendar_pick.clone();
                calendar_drop.add(Calendar.DATE,2);

                bindDateToGUI("returning", calendar_drop.get(Calendar.YEAR), calendar_drop.get(Calendar.MONTH), calendar_drop.get(Calendar.DAY_OF_MONTH));
                break;

            case "returning":
                final LinearLayout dt_picker_returning = (LinearLayout) view.findViewById(R.id.dt_picker_returning);
                final TextView tvReturningDatePicker = (TextView) dt_picker_returning.findViewById(R.id.tvDateDayValue);
                final TextView tvReturningDateDayNameWithMonthName = (TextView) dt_picker_returning.findViewById(R.id.tvDateDayNameWithMonthName);
                tvReturningDatePicker.setText(String.valueOf(i2));
                tvReturningDateDayNameWithMonthName.setText(dayMon);
                drop_date = dateValueString ;
                calendar_drop.set(i,i1,i2);
                break;
        }

    }

    private String setValueFullDate(int year, int month, int day) {
        String result;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        result = monthFormat.format(calendar.getTime());
        return result;
    }

    private String setFullDate(int year, int month, int day) {
        String result;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat monthFormat = new SimpleDateFormat("EEE | MMMM", Locale.US);
        result = monthFormat.format(calendar.getTime());
        return result;
    }

    @Override
    public void setDefaultSettings() {

        activity = (MainActivity) getActivity();
        searchQuery = activity.searchQuery;

        et_driver_age.setVisibility(View.GONE);
        et_return_location.setVisibility(View.GONE);
        et_return_location.setInputType(InputType.TYPE_NULL);
        et_return_location.setFocusable(false);

        et_pickup_location.setInputType(InputType.TYPE_NULL);
        et_pickup_location.setFocusable(false);

        switchSameDestLocation = (SwitchCompat) view.findViewById(R.id.switchSameDestLocation);
        switchSameDestLocation.setChecked(searchQuery.isDestAsPickup);

        switchDriverAge = (SwitchCompat) view.findViewById(R.id.switchDriverAge);
        switchDriverAge.setChecked(searchQuery.isDriverAged);

        final SwitchCompat switchSearchByMap = (SwitchCompat) view.findViewById(R.id.switchSearchByMap);
        switchSearchByMap.setChecked(searchQuery.isSearchByMap);

        calendar_pick = Calendar.getInstance();
        calendar_pick.add(Calendar.DATE,2);
        bindDateToGUI("journey", calendar_pick.get(Calendar.YEAR), calendar_pick.get(Calendar.MONTH), calendar_pick.get(Calendar.DAY_OF_MONTH));
        bindTimeToGUI("journey", calendar_pick.get(Calendar.HOUR_OF_DAY), calendar_pick.get(Calendar.MINUTE));

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Toolbar toolbar = ((MainActivity) getActivity()).toolbar;
        toolbar.setTitle(getResources().getString(R.string.action_search_car));
        getMarkUp();

//        checkGPSStatus();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Preserve Data
        switchSameDestLocation = (SwitchCompat) view.findViewById(R.id.switchSameDestLocation);
        searchQuery.isDestAsPickup = switchSameDestLocation.isChecked();

        switchDriverAge = (SwitchCompat) view.findViewById(R.id.switchDriverAge);
        searchQuery.isDriverAged = switchDriverAge.isChecked();

        final SwitchCompat switchSearchByMap = (SwitchCompat) view.findViewById(R.id.switchSearchByMap);
        searchQuery.isSearchByMap = switchSearchByMap.isChecked();

        activity.searchQuery = searchQuery;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_pickup_location:
                startActivityForResult(new Intent(getActivity(), LocationSelectionActivity.class), REQUEST_PICKUP_LOCATION);
                break;

            case R.id.et_return_location:
                startActivityForResult(new Intent(getActivity(), LocationSelectionActivity.class), REQUEST_DESTINATION_LOCATION);
                break;

            case R.id.btn_search_car:
//                requestForSearchCar();
                requestForSearchCar1();

        }
    }
    Gson gson = new Gson();
    String TAG = SearchCarFragment.class.getName();

    public static List<Category.ResponseBean.CatBean>catBeanList = new ArrayList<>();
    public static Category category = new Category();
    CatRequest cateRequest = new CatRequest();
    HashMap<Double,String>map1;

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    public void getCat() {
        if (catBeanList!=null){
            catBeanList.clear();
        }
        Utility.showloadingPopup(getActivity());
        String cat = gson.toJson(cateRequest);
        Log.d(TAG, "getCat: "+cat);

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE,cat);

        final Request request = new Request.Builder()
                .url(RetrofitApiBuilder.CarHires_BASE_URL+"category_list")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.SECONDS)
                .writeTimeout(10000, TimeUnit.SECONDS)
                .readTimeout(30000, TimeUnit.SECONDS)
                .build();

        Utility.showloadingPopup(getActivity());
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = e.getMessage();
                        category = gson.fromJson(msg, Category.class);
                        Utility.message(getContext(), getResources().getString(R.string.no_internet_connection));
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
                        Log.d(TAG, "onResponse: msg"+msg);
                        category = gson.fromJson(msg,Category.class);
                        try {
                            JSONObject jsonObject = new JSONObject(msg);
                            if (jsonObject.getBoolean("status")){
                                List<Category.ResponseBean.CatBean>catBeans = new ArrayList<>();
                                if (category.getResponse()!=null){
                                    catBeans = category.getResponse().getCat();
                                }
                                ArrayList<String>name = new ArrayList<>();
                                Set<String>set = new HashSet<>();
                                map1 = new HashMap<>();
                                for (Category.ResponseBean.CatBean catBean: catBeans){
                                    name.add(String.valueOf(catBean.getCode()));
                                    set.addAll(name);
                                    name.clear();
                                    name.addAll(set);
                                    Log.d(TAG, "onResponse: name"+gson.toJson(name));

                                    for (SearchData searchDatas: searchData){
                                        ArrayList<Double>doubles = new ArrayList<>();
                                        if (String.valueOf(catBean.getCode()).equalsIgnoreCase(searchDatas.getCategory())){
                                            double d = Double.parseDouble(searchDatas.getPrice());
                                            double priceNew  = d+(d*Double.parseDouble(markup))/100;
                                            doubles.add(priceNew);
//                                            Collections.min(doubles);
                                            map1 .put(priceNew,catBean.getCategory_name());
                                        }
                                    }
                                }
                                Log.d(TAG, "onResponse: map1"+gson.toJson(map1));

                                final List<Category.ResponseBean.CatBean> finalCatBeans = catBeans;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        catBeanList.addAll(finalCatBeans);

                                        TreeSet<Category.ResponseBean.CatBean> set = new TreeSet<>(new Comparator<Category.ResponseBean.CatBean>() {
                                            @Override
                                            public int compare(Category.ResponseBean.CatBean o1, Category.ResponseBean.CatBean o2) {
                                                if(o1.getCategory_name().equalsIgnoreCase(o2.getCategory_name())){
                                                    return 0;
                                                }
                                                return 1;
                                            }
                                        });
                                        set.addAll(catBeanList);
                                        catBeanList.clear();
                                        catBeanList.addAll(set);
                                        chooseSearchAction(searchData);
                                    }
                                });
                                for (int i=0;i<name.size();i++){
                                    List<Object>d =new ArrayList<>();
                                    d = getKeysFromValue(map1,name.get(i));
                                    Log.d(TAG, "onResponse: data"+name.get(i));
                                    Log.d(TAG, "onResponse: data"+d);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static List<Object> getKeysFromValue(Map<?, ?> hm, Object value){
        List <Object>list = new ArrayList<Object>();
        for(Object o:hm.keySet()){
            if(hm.get(o).equals(value)) {
                list.add(o);
            }
        }
        return list;
    }

    private void chooseSearchAction(List<SearchData> car_list) {

        final SwitchCompat switchSearchByMap = (SwitchCompat) view.findViewById(R.id.switchSearchByMap);
        Intent intent ;
        if (switchSearchByMap.isChecked()) {
            intent = new Intent(getActivity(), SearchbyMapActivity.class);
//            intent.putExtra("car_list", (Serializable) searchData) ;
            startActivity(intent);
        } else {
            getPoint();
        }
    }

    public static List<SearchData>searchData = new ArrayList<>();
    List<Integer>cateList=new ArrayList<>();
    private boolean status;
    private SearchData.FeatureBean feature;
    private String covprice;
    private String covcurrency;
    private String category1;                              //
    private String model;
    private String model_code;
    private String image;
    String packaged;
    private String packageX;
    private String price;
    private String currency;
    private String time_unit;
    private String time;
    private String id_context;
    private String refer_type;
    private String deposit_currency;
    private String deposit_price;
    private String deposit_desc;
    private String deposit_name;
    private String supplier;
    private String supplier_city;
    private String supplier_logo;
    private String drop_city;
    private String tc;
    private String rule;
    private String type;
    private List<SearchData.CoveragesBean> coverages;

    private void requestForSearchCar1() {
        if(!validateData()){
            return ;
        }
        feature = new SearchData.FeatureBean();
        coverages = new ArrayList<>();
        Utility.showLoading(getActivity(),getResources().getString(R.string.searching_cars));
        final SearchCarFragment _this = SearchCarFragment.this ;
        RetroFitApis retroFitApis = RetrofitApiBuilder.getCarGatesapi() ;

        pick_hour=String.valueOf(pick_hours>9?pick_hours:"0"+pick_hours);
        pick_minute=String.valueOf(pick_minutes>9?pick_minutes:"0"+pick_minutes);

        drop_minute=String.valueOf(drop_minutes>9?drop_minutes:"0"+drop_minutes);
        drop_hour=String.valueOf(drop_hours>9?drop_hours:"0"+drop_hours);
        if (switchSameDestLocation.isChecked()){
            dropName = pickName;
            location_code_drop =location_code;
            location_iata_drop = location_iata;
            location_type_drop = location_type;
        }

        Log.d(TAG, "requestForSearchCar: "+token+"\n"+pickName+"\n"+
                pick_date+"\n"+pick_hour+"\n"+
                pick_minute+"\n"+dropName+"\n"+drop_date+"\n"+drop_hour+"\n"+drop_minute+"\n"+driver_age+"\n"+
                useCurrentLocation+"\n"+ useSameDestLocation+"\n"+isBetweenDriverAge+"\n"+currentLat+"\n"+
                currentLng+"\n"+location_code+"\n"+location_iata+"\n"+
                location_type+"\n"+location_code_drop+"\n"+location_iata_drop+"\n"+location_type_drop+"\n"+languagecode);
        final Gson gson = new Gson();


        String url= RetrofitApiBuilder.CarGates_BASE_WEBSERVICE_URL+"webservice/search";

        StringRequest  stringRequest = new StringRequest(com.android.volley.Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Utility.hidepopup();
                String d = gson.toJson(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    status = jsonObject.getBoolean("status");
                    if (status){
                        JSONObject responseObject = jsonObject.getJSONObject("response");
                        JSONObject car_listObject = responseObject.getJSONObject("car_list");
                        Iterator<String> iter = car_listObject.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            try {
                                Object value = car_listObject.get(key);
                                JSONObject object = car_listObject.getJSONObject(key);

                                if (key.equals("category_list")){
                                    Iterator<String> iterCatList = object.keys();
                                    while (iterCatList.hasNext()){
                                        String catkey = iterCatList.next();
                                        Object Catvalue = object.get(catkey);
                                        Log.d(TAG, "onResponse: catvalue"+Catvalue);
                                    }

                                } else {
                                    if (object.has("feature")){
                                        JSONObject featureObject = object.getJSONObject("feature");
                                        feature.setAircondition((String) featureObject.get("aircondition"));
                                        feature.setBag((String) featureObject.get("bag"));
                                        feature.setFueltype(featureObject.getString("fueltype"));
                                        feature.setTransmission(featureObject.getString("transmission"));
                                    }
                                    category1 = (String) object.get("category");
                                    model = object.getString("model");
                                    model_code = object.getString("model_code");
                                    image = (String) object.get("image");
                                    packaged = object.getString("package");
                                    price = object.getString("price");
                                    currency = (String) object.get("currency");
                                    time_unit = object.getString("time_unit");
                                    time = object.getString("time");
                                    id_context = (String) object.get("id_context");
                                    refer_type = object.getString("refer_type");
                                    supplier = object.getString("supplier");
                                    supplier_city = (String) object.get("supplier_city");
                                    supplier_logo = object.getString("supplier_logo");
                                    drop_city = object.getString("drop_city");
                                    tc = (String) object.get("tc");
                                    type = (String)object.get("type");
                                    JSONArray coveragesArray = object.getJSONArray("coverages");
                                    for (int i=0;i<coveragesArray.length();i++){
                                        SearchData.CoveragesBean bean = new SearchData.CoveragesBean();
                                        JSONObject jsonObject1 = (JSONObject) coveragesArray.get(i);
                                        String code = jsonObject1.getString("code");
                                        String name = jsonObject1.getString("name");
                                        String currency = jsonObject1.getString("currency");
                                        String desc = jsonObject1.getString("desc");
                                        String amount2 = jsonObject1.getString("amount2");
                                        String currency2 = jsonObject1.getString("currency2");

                                        if (code!=null){
                                            bean.setCode(code);
                                        }
                                        if (name!=null){
                                            bean.setName(name);
                                        }
                                        if (currency2!=null){
                                            bean.setCurrency2(currency2);
                                        }
                                        if (desc!=null){
                                            bean.setDesc(desc);
                                        }
                                        if (amount2!=null){
                                            bean.setAmount2(amount2);
                                        }
                                        coverages.add(bean);
                                    }
                                    SearchData carData = new SearchData();

                                    carData.setFeature(feature);
                                    carData.setCategory(category1);
                                    carData.setModel(model);
                                    carData.setModel_code(model_code);
                                    carData.setImage(image);
                                    carData.setPackageX(packageX);
                                    carData.setPrice(price);
                                    carData.setCurrency(currency);
                                    carData.setTime(time);
                                    carData.setTime_unit(time_unit);
                                    carData.setTime_unit(id_context);
                                    carData.setRefer_type(refer_type);
                                    carData.setSupplier(supplier);
                                    carData.setSupplier_city(supplier_city);
                                    carData.setSupplier_logo(supplier_logo);
                                    carData.setDrop_city(drop_city);
                                    carData.setTc(tc);
                                    carData.setCoverages(coverages);
                                    carData.setType(type);
                                   searchData.add(carData);
                                }

                            } catch (JSONException e) {
                                // Something went wrong!
                            }
                        }
                        chooseSearchAction(searchData);

                    } else {

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "onResponse: new "+d);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utility.hidepopup();
                Log.d(TAG, "onErrorResponse: "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String>serachRequest = new HashMap<>();
                serachRequest.put("access_token", token);
                serachRequest.put("pick_city", pickName);
                serachRequest.put("pick_date", pick_date);
                serachRequest.put("pick_houre", pick_hour);
                serachRequest.put("pick_minute", pick_minute);
                serachRequest.put("drop_city", dropName);
                serachRequest.put("drop_date", drop_date);
                serachRequest.put("drop_houre", drop_hour);
                serachRequest.put("drop_minute", drop_minute);
                serachRequest.put("driver_age", driver_age);
                serachRequest.put("use_current_location", String.valueOf(useCurrentLocation));
                serachRequest.put("sameas_pick_location", String.valueOf(useSameDestLocation));
                serachRequest.put("between_driver_age" , String.valueOf(isBetweenDriverAge));
                serachRequest.put("lat", String.valueOf(currentLat));
                serachRequest.put("long", String.valueOf(currentLng));
                serachRequest.put("location_code", location_code);
                serachRequest.put("location_iata", location_iata);
                serachRequest.put("location_type", location_type);
                serachRequest.put("location_code_drop", location_code_drop);
                serachRequest.put("location_iata_drop", location_iata_drop);
                serachRequest.put("location_type_drop", location_type_drop);
                serachRequest.put("language_code", languagecode);
                return serachRequest;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);


    }



    private void requestForSearchCar() {
        if(!validateData()){
            return ;
        }

        Utility.showLoading(getActivity(),getResources().getString(R.string.searching_cars));
        final SearchCarFragment _this = SearchCarFragment.this ;
        RetroFitApis retroFitApis = RetrofitApiBuilder.getCarGatesapi() ;

        pick_hour=String.valueOf(pick_hours>9?pick_hours:"0"+pick_hours);
        pick_minute=String.valueOf(pick_minutes>9?pick_minutes:"0"+pick_minutes);

        drop_minute=String.valueOf(drop_minutes>9?drop_minutes:"0"+drop_minutes);
        drop_hour=String.valueOf(drop_hours>9?drop_hours:"0"+drop_hours);
        if (switchSameDestLocation.isChecked()){
            dropName = pickName;
            location_code_drop =location_code;
            location_iata_drop = location_iata;
            location_type_drop = location_type;
        }
        Call<ApiResponse> responseCall = retroFitApis.search(token,pickName,
                pick_date,pick_hour,
                pick_minute,dropName,drop_date,drop_hour,drop_minute,driver_age,
                useCurrentLocation, useSameDestLocation,isBetweenDriverAge,currentLat,
                currentLng,location_code,location_iata,
                location_type,location_code_drop,location_iata_drop,location_type_drop,languagecode) ;


        Log.d(TAG, "requestForSearchCar: "+token+"\n"+pickName+"\n"+
                pick_date+"\n"+pick_hour+"\n"+
                pick_minute+"\n"+dropName+"\n"+drop_date+"\n"+drop_hour+"\n"+drop_minute+"\n"+driver_age+"\n"+
                useCurrentLocation+"\n"+ useSameDestLocation+"\n"+isBetweenDriverAge+"\n"+currentLat+"\n"+
                currentLng+"\n"+location_code+"\n"+location_iata+"\n"+
                location_type+"\n"+location_code_drop+"\n"+location_iata_drop+"\n"+location_type_drop+"\n"+languagecode);
        final Gson gson = new Gson();
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override

            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                Log.d(TAG, "Complete Search List: " + gson.toJson(response.body()));
                if (response!=null){
                    if (response.body().status){
                        searchData=response.body().response.car_list;
                        String data = gson.toJson(searchData);
                        ArrayList<SearchData>searchData1 = new ArrayList<>();
                        searchData1.addAll(searchData);
                        for (SearchData searchDatas : searchData){
                            cateList.add(Integer.parseInt(searchDatas.getCategory()));
                        }
                        cateRequest.setCode(cateList);
                        getCat();

                    } else {
                        Toast.makeText(activity, ""+response.body().msg, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.hidepopup();
                Log.d(TAG, "onFailure: "+t.getMessage());

                Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getPoint(){
        RetroFitApis retroFitApis = RetrofitApiBuilder.getCargHiresapis() ;

        Call<ApiResponse> responseCall = retroFitApis.point(" ") ;
        final Gson gson = new Gson();
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();

                if(response.body()!=null){
                    Log.d(SplashActivity.TAG, "onResponse: point "+gson.toJson(response.body().response.point));
                    Point point = new Point();

                    if(response.body().status){
                        point = response.body().response.point;
                        pointper = point.getPoint_percentage();
                        if (switchSameDestLocation.isChecked()){
                            dropName = pickName;
                        }
                        Intent intent = new Intent(getActivity(), CarsResultListActivity.class);
                        startActivity(intent);
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.hidepopup();
                Log.d(TAG, "onFailure: "+t.getMessage());
                Toast.makeText(getContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getMarkUp(){
        RetroFitApis retroFitApis = RetrofitApiBuilder.getCargHiresapis() ;

        Call<ApiResponse> responseCall = retroFitApis.markup(" ") ;
        final Gson gson = new Gson();
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                MArkupdata point = new MArkupdata();
                if(response.body()!=null){
                    Log.d(SplashActivity.TAG, "onResponse: point "+gson.toJson(response.body().response.point));

                    if(response.body().status){
                        point = response.body().response.markup;
                        markup = point.getMarkup_percentage();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.hidepopup();
                Toast.makeText(getContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            location = (Location) data.getSerializableExtra(LocationSelectionActivity.RESPONSE_DATA);
        }
        if (resultCode == 0) {
            if (requestCode == REQUEST_BY_MAP_LOCATION) {

            }
        }
        else if (resultCode == LocationSelectionActivity.RESPONSE_LOCATION) {
            if (requestCode == REQUEST_PICKUP_LOCATION) {
                pickName = location.getCity_name();
                et_pickup_location.setText(pickName);
                pickup_loc_id = location.getCity_id();
                location_code = location.getCode();
                location_iata = location.getIata();
                location_type = location.getType();

                if (switchSameDestLocation.isChecked()) {
                    drop_loc_id = pickup_loc_id;
                }
            } else if (requestCode == REQUEST_DESTINATION_LOCATION) {
                drop_loc_id = location.getCity_id();
                dropName = location.getCity_name();
                et_return_location.setText(dropName);
                location_code_drop = location.getCode();
                location_iata_drop = location.getIata();
                location_type_drop = location.getType();
            }
        }
//        else if (resultCode == LocationSelectionActivity.RESPONSE_LOCATION) {
//            if (requestCode == REQUEST_PICKUP_LOCATION) {
//                et_pickup_location.setText(location.getCity_name());
//                pickup_loc_id = location.getCity_id();
//                if (switchSameDestLocation.isChecked()) {
//                    drop_loc_id = pickup_loc_id;
//                }
//            } else if (requestCode == REQUEST_DESTINATION_LOCATION) {
//                et_return_location.setText(location.getCity_name());
//                drop_loc_id = location.getCity_id();
//            }
//        }
    }

    protected synchronized void setupLocation() {
        if (PermissionRequestHandler.requestPermissionToLocation(getActivity(),this)) {
            checkGPSStatus();
            mgoogleApiclient = new GoogleApiClient.Builder(getActivity()).
                    addConnectionCallbacks(this).
                    addOnConnectionFailedListener(this).
                    addApi(LocationServices.API).build();

            mgoogleApiclient.connect();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        android.location.Location mLocation = LocationServices.FusedLocationApi.getLastLocation(mgoogleApiclient);
        if (mLocation != null) {
            currentLat = mLocation.getLatitude();
            currentLng = mLocation.getLongitude();
            Log.d("Current Locations",currentLat+" , "+currentLng);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1]==PackageManager.PERMISSION_GRANTED) {
                    setupLocation();
                } else {
                    chkUseCurrentLocation.setChecked(false);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private boolean validateData(){

        // Location
        if(!chkUseCurrentLocation.isChecked()){
            useCurrentLocation = 0 ;
            currentLng = 0.0;
            currentLat = 0.0 ;
            // pickup_loc_id
            if(pickup_loc_id==null || pickup_loc_id.trim().isEmpty()){
                Toast.makeText(activity, getResources().getString(R.string.pick_up_location), Toast.LENGTH_SHORT).show();
                return false;
            }
            if(switchSameDestLocation.isChecked()){
                useSameDestLocation = 0;
                drop_loc_id = pickup_loc_id ;
            }else {
                if(drop_loc_id==null || drop_loc_id.trim().isEmpty()){
                    useSameDestLocation = 1;
                    Toast.makeText(activity, getResources().getString(R.string.pleae_select_drop_loc), Toast.LENGTH_SHORT).show();
                    return false ;
                }
            }

        }else{
            useCurrentLocation = 1 ;
            //currentLat
            // currentLng
            pickup_loc_id = "" ;
            if(!switchSameDestLocation.isChecked()) {
                // drop_loc_id
                if(drop_loc_id==null || drop_loc_id.trim().isEmpty()){
                    Toast.makeText(activity, getResources().getString(R.string.pleae_select_drop_loc), Toast.LENGTH_SHORT).show();
                    return false ;
                }
            }else{
                drop_loc_id= "";
                useSameDestLocation = 0 ;
            }

        }
        // Date Time
        Log.d("Calender ",calendar_pick.getTime()+" "+calendar_drop.getTime());
        Log.d("Calender ",calendar_drop.compareTo(calendar_pick)+"");

        if(calendar_drop.compareTo(calendar_pick) <= 0){
            Toast.makeText(activity, getResources().getString(R.string.selsect_valid_drop_date), Toast.LENGTH_SHORT).show();
            return false ;
        }

        // Age
        if(switchDriverAge.isChecked()) {
            isBetweenDriverAge = 1 ;
            driver_age =  "25";
        }else{
            isBetweenDriverAge= 0 ;
            driver_age = et_driver_age.getText().toString().trim();
            if(driver_age.isEmpty()){
                Toast.makeText(activity, getResources().getString(R.string.enter_driver_age), Toast.LENGTH_SHORT).show();
                return false ;
            }
        }
        return true ;
    }

    @Override
    public void refreshTokenCallBack() {
        token =  tinyDB.getString("access_token") ;
        requestForSearchCar() ;
    }

}
