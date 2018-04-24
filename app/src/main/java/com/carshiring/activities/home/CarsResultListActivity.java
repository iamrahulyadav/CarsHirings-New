package com.carshiring.activities.home;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.carshiring.R;
import com.carshiring.activities.mainsetup.SignUpActivity;
import com.carshiring.adapters.CarListCategory;
import com.carshiring.adapters.CarResultsListAdapter;
import com.carshiring.fragments.SearchCarFragment;
import com.carshiring.models.FilterDefaultMultipleListModel;
import com.carshiring.models.CatRequest;
import com.carshiring.models.Category;
import com.carshiring.models.SearchData;
import com.carshiring.models.UserDetails;
import com.carshiring.splash.SplashActivity;
import com.carshiring.utilities.AppBaseActivity;
import com.carshiring.utilities.AppGlobal;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.carshiring.activities.home.MainActivity.getKeyFromValue;

public class CarsResultListActivity extends AppBaseActivity {
    Gson gson = new Gson();
    public String filter ;
    Category category = new Category();
    public List<Category.ResponseBean.CatBean>catBeanList = new ArrayList<>();
    List<SearchData> listCarResult =  new ArrayList<>();
    List<SearchData.FeatureBean> featuresAllList =  new ArrayList<>();
    public static List<String>supplierList=new ArrayList<>();
    List<Integer>cateList=new ArrayList<>();
    CarResultsListAdapter listAdapter;
    UserDetails userDetails = new UserDetails();
    TinyDB tinyDB;
    LinearLayout allView;
    public static String id_context, refertype, type, day, time;
    AppGlobal appGlobal=AppGlobal.getInstancess();
    Dialog dialog;
    TextView txtDob;
    TextView tvFromDate,tvPickDate,tvTodate,txtPlaceDrop;
    String fname,lname,email,phone,zip,license,licenseorigin,city,address,emaillogin,pass,set ="",userid="",dob;
    RecyclerView recycler_search_cars;
    CatRequest cateRequest = new CatRequest();
    CarListCategory adapter;
    ImageView cat_image_all;
    private RecyclerView recyclerView_carlist_category;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_result_list2);

        filter =  getResources().getString(R.string.recommended);
        actionBar = getSupportActionBar() ;
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }

        appGlobal.context=getApplicationContext();
        tinyDB = new TinyDB(getApplicationContext());
        dialog=new Dialog(this);
        allView = findViewById(R.id.all_view);
        listCarResult.addAll(SearchCarFragment.searchData);
        tvFromDate= (TextView) findViewById(R.id.tvFromDT);
        tvPickDate= (TextView) findViewById(R.id.txtPlaceName);
        tvTodate= (TextView) findViewById(R.id.tvToDT);
        txtPlaceDrop = findViewById(R.id.txtPlaceName_drop);

        tvFromDate.setText(SearchCarFragment.pick_date+"\n"+ SearchCarFragment.pickTime);
        tvPickDate.setText(SearchCarFragment.pickName);
        tvTodate.setText(SearchCarFragment.drop_date+"\n"+SearchCarFragment.dropTime);
        txtPlaceDrop.setText(SearchCarFragment.dropName);

        cat_image_all = (ImageView) findViewById(R.id.car_cat_image_all);
        Glide.with(getApplicationContext())
                .load("https://carshiring.com/site/images/car.png")
                .into(cat_image_all);

//        get supplier
        if (supplierList!=null){
            supplierList.clear();
        }
        for (SearchData searchData : listCarResult){

            supplierList.add(searchData.getSupplier());
            cateList.add(Integer.parseInt(searchData.getCategory()));
        }

        cateRequest.setCode(cateList);
        Set<String> hs = new HashSet<>();
        hs.addAll(supplierList);
        supplierList.clear();
        supplierList.addAll(hs);
        recycler_search_cars = (RecyclerView) findViewById(R.id.recycler_search_cars);
        recyclerView_carlist_category = (RecyclerView) findViewById(R.id.recycler_carlist_by_category);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(CarsResultListActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView_carlist_category.setLayoutManager(horizontalLayoutManagaer);
        catBeanList = SearchCarFragment.catBeanList;



        adapter = new CarListCategory(getApplicationContext(), listCarResult, catBeanList,
                new CarListCategory.OnItemClickListenerCategory() {
                    @Override
                    public void onItemClickCategory(int position) {
                        catgory_clicked(position);
                    }
                });
        recyclerView_carlist_category.setAdapter(adapter);

    }

    private void catgory_clicked(int position){
        List<SearchData> listCarResult1 =  new ArrayList<>();
        listCarResult1.clear();

            for(int i=0; i<listCarResult.size(); i++) {
                if((catBeanList.get(position).getCode()+"").equals(listCarResult.get(i).getCategory())){
                    listCarResult1.add(listCarResult.get(i));
                }
            }
            listdispaly(listCarResult1);


    }
    int coun=0;
    public void cat_All(View v){
//        allView.setBackgroundColor(Color.parseColor("#079607"));
        listdispaly(listCarResult);
    }

    double pointpercent, calPoint,calPrice;
    String oneway,driverSur;
    public void listdispaly(List<SearchData> listCarResult ) {
        CarResultsListAdapter listAdapter;
        listAdapter = new CarResultsListAdapter(this,listCarResult,catBeanList, new CarResultsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SearchData carDetail) {
                if (tinyDB.contains("login_data")){
                    String data = tinyDB.getString("login_data");
                    userDetails = gson.fromJson(data,UserDetails.class);
                    userid = userDetails.getUser_id();
                    if (userDetails.getUser_lname()==null || userDetails.getUser_lname().length()==0){
                        set = "update_profile";
                        setupoverlay(set);
                    } else {

                        id_context = carDetail.getId_context();
                        type = carDetail.getType();
                        refertype = carDetail.getRefer_type();
                        for (SearchData.CoveragesBean bean: carDetail.getCoverages()){
                            if (bean.getCode().equalsIgnoreCase("412")){
                                oneway = bean.getName() +" : "+ bean.getCurrency2()+" "
                                        +bean.getAmount2();
                            } else if ( bean.getCode().equalsIgnoreCase("410")){
                                driverSur =bean.getName()+ " : "+bean.getCurrency2()+" "
                                        + bean.getAmount2();
                            }
                        }
                        Intent intent = new Intent(CarsResultListActivity.this,CarDetailActivity.class);
                        intent.putExtra("id_context",id_context);
                        intent.putExtra("type",type);
                        double pricea = Double.parseDouble(carDetail.getPrice());
                        pointpercent = Double.parseDouble(SearchCarFragment.pointper);
                        double markUp = Double.parseDouble(SearchCarFragment.markup);
                        double d = pricea;
                        double priceNew  = d+(d*markUp)/100;
                        calPrice = (priceNew*pointpercent)/100;
                        calPoint = (int) (calPrice/0.02);
                        day = carDetail.getTime();
                        time = carDetail.getTime_unit();
                        intent.putExtra("day",carDetail.getTime());
                        intent.putExtra("refer_type",refertype);
                        intent.putExtra("point_earn",String.valueOf(calPoint));
                        intent.putExtra("one_way_fee", oneway);
                        intent.putExtra("driverSur", driverSur);
                        startActivity(intent);
                    }
                } else {
                    set = "login";
                    setupoverlay(set);
                }
            }
        });
        recycler_search_cars.setAdapter(listAdapter);

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

    @Override
    protected void onResume() {
        super.onResume();
        actionBar.setTitle(getResources().getString(R.string.car_results));
      /*  tinyDB.remove("extra_added");
        tinyDB.remove("full_prot");*/

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_search_cars.setLayoutManager(layoutManager);
        recycler_search_cars.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL) {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                // Do not draw the divider
            }
        });
        if(isApplyFiltered)
        {
            if (filteredtList.size()>0){
                listdispaly(filteredtList);
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_filters_applied), Toast.LENGTH_SHORT).show();
                listdispaly(filteredtList);
            }
        }
        else
        {
            listdispaly(listCarResult);
        }
        isApplyFiltered = false ;

//        recycler_search_cars.setAdapter(listAdapter);
//        getCat();
    }

    public void openSelectionSortedBy(View view) {
     /*   Intent intent = new Intent(CarsResultListActivity.this,SortingSelectionActivity.class);
        intent.putExtra("filter",filter) ;
        startActivityForResult(intent,200);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== 200){
            if(resultCode== RESULT_OK){
                filter = data.getStringExtra("filter");
                getShortedData() ;
            }
        }
        if(requestCode== 201){
            //Toast.makeText(getApplicationContext(), resultCode + "", Toast.LENGTH_SHORT).show();
            if(resultCode== SelectFilterActivity.FILTER_RESPONSE_CODE)
            {
                FilterDefaultMultipleListModel multipleListModel= (FilterDefaultMultipleListModel) data.getSerializableExtra(SelectFilterActivity.FILTER_RESPONSE);
                String supl=multipleListModel.getSupplier();
                String feat=multipleListModel.getFeatures();
                if(supl!=null || feat!=null)
                {
                    filter(supl,feat);
                }
            }
        }
    }

    private boolean isApplyFiltered = false ;
    private  ArrayList<SearchData>  filteredtList ;

    private void filter(String supl, String feat){
        String[] suplier=supl.split(",");
        String[] features=feat.split(",");

        filteredtList = new ArrayList<>();
        for(int i=0;i<listCarResult.size();i++) {
            SearchData data = listCarResult.get(i);
            boolean issuplierfound = false;
            String supleir_strg = data.getSupplier();
            Log.d("Supplier", supleir_strg);
            if (!supleir_strg.isEmpty()) {
                for (String suply : suplier) {
                    if (!suply.isEmpty()) {
                        if (supleir_strg.equalsIgnoreCase(suply)) {
                            filteredtList.add(data);
                            issuplierfound = true;
                            Log.d("Filter", "Supplier Matched");
                            break;
                        }
                    }
                }
            }
        }
        if (Arrays.asList(features).contains("Air Condition")&&Arrays.asList(features).contains("Automatic")
                &&Arrays.asList(features).contains("4+ Doors")){
            Log.d(TAG, "filter: "+true);
            getACA("Air Condition","Automatic","4+ Doors");
        } else  if (Arrays.asList(features).contains("Air Condition")&&Arrays.asList(features).contains("Automatic")){
            getACA("Air Condition","Automatic","");
        } else  if (Arrays.asList(features).contains("Air Condition")&&Arrays.asList(features).contains("4+ Doors")){
            getACA("Automatic","","4+ Doors");
        }  else  if (Arrays.asList(features).contains("Air Condition")&&Arrays.asList(features).contains("4+ Doors")){
            getACA("","Automatic","4+ Doors");
        } else  if (Arrays.asList(features).contains("Air Condition")){
            getACA("Air Condition","","");
        } else  if (Arrays.asList(features).contains("Automatic")){
            getACA("","Automatic","");
        } else  if (Arrays.asList(features).contains("4+ Doors")){
            getACA("","","4+ Doors");
        }

/*
        for (String featue: features){
            String s = featue;
            if (s.equalsIgnoreCase("Air Condition")){
                getAC(s);
            } else if (s.equalsIgnoreCase("Automatic")){
                getAutomatic(s);
            } else if (s.equalsIgnoreCase("4+ Doors")){
                getDoor(s);
            }
        }
*/
        isApplyFiltered = true;
    }

    private void getAC(String AC){

        for (SearchData searchData: listCarResult){

            if (AC.equalsIgnoreCase("Air Condition")){
                if (searchData.getFeature().getAircondition().equalsIgnoreCase("true")){
                    filteredtList.add(searchData);
                }
            }
        }
    }
    private void getACA(String AC, String Automatic, String Doors){
        filteredtList = new ArrayList<>();

        for (SearchData searchData: listCarResult){
            if (AC.length()>0&&Automatic.length()>0&&Doors.length()>0){
                if (AC.equalsIgnoreCase("Air Condition")&&Automatic.equalsIgnoreCase("Automaic")
                        &&!searchData.getFeature().getDoor().equalsIgnoreCase("1")&&
                        !searchData.getFeature().getDoor().equalsIgnoreCase("2")
                        &&!searchData.getFeature().getDoor().equalsIgnoreCase("3")){
                    if (searchData.getFeature().getAircondition().equalsIgnoreCase("true")
                            &&searchData.getFeature().getTransmission().equalsIgnoreCase("Automatic")){
                        filteredtList.add(searchData);
                    }
                }

            }else if (AC.length()>0&&Automatic.length()>0){
                if (AC.equalsIgnoreCase("Air Condition")
                        &&Automatic.equalsIgnoreCase("Automaic")){
                    if (searchData.getFeature().getAircondition().equalsIgnoreCase("true")
                            &&searchData.getFeature().getTransmission().equalsIgnoreCase("Automatic")){
                        filteredtList.add(searchData);
                    }
                }
            }else if (AC.length()>0&&Doors.length()>0){
                if (AC.equalsIgnoreCase("Air Condition")
                        &&!searchData.getFeature().getDoor().equalsIgnoreCase("1")&&
                        !searchData.getFeature().getDoor().equalsIgnoreCase("2")
                        &&!searchData.getFeature().getDoor().equalsIgnoreCase("3")){
                    if (searchData.getFeature().getAircondition().equalsIgnoreCase("true")
                            &&searchData.getFeature().getTransmission().equalsIgnoreCase("Automatic")){
                        filteredtList.add(searchData);
                    }
                }
            }else if (Automatic.length()>0&&Doors.length()>0){
                if (!searchData.getFeature().getDoor().equalsIgnoreCase("1")&&
                        !searchData.getFeature().getDoor().equalsIgnoreCase("2")
                        &&!searchData.getFeature().getDoor().equalsIgnoreCase("3")
                        &&Automatic.equalsIgnoreCase("Automaic")){
                    if (searchData.getFeature().getAircondition().equalsIgnoreCase("true")
                            &&searchData.getFeature().getTransmission().equalsIgnoreCase("Automatic")){
                        filteredtList.add(searchData);
                    }
                }
            } else if (AC.length()>0){
                getAC(AC);
            } else if (Doors.length()>0){
                getDoor(Doors);
            } else if (Automatic.length()>0){
                getAutomatic(Doors);
            }
        }
    }

    private void getAutomatic(String AC){
        for (SearchData searchData: listCarResult){

            if (AC.equalsIgnoreCase("Automatic")){
                if (searchData.getFeature().getTransmission().equalsIgnoreCase("Automatic")){
                    filteredtList.add(searchData);
                }
            }
        }
    }
    private void getDoor(String AC){
        for (SearchData searchData: listCarResult){

            if (AC.equalsIgnoreCase("4+ Doors")){
                if (searchData.getFeature().getDoor()!=null && !searchData.getFeature().getDoor().equalsIgnoreCase("1")&&
                        !searchData.getFeature().getDoor().equalsIgnoreCase("2")
                        &&!searchData.getFeature().getDoor().equalsIgnoreCase("3")){
                    filteredtList.add(searchData);
                }
            }
        }
    }


    private void filterlist(String supl, String feat) {

        String[] suplier=supl.split(",");
        String[] features=feat.split(",");

        filteredtList=new ArrayList<>();
        int listsize=listCarResult.size();

        for(int i=0;i<listsize;i++) {
            SearchData data = listCarResult.get(i);

            boolean issuplierfound = false;
            String supleir_strg = data.getSupplier();
            Log.d("Supplier", supleir_strg);

            if (!supleir_strg.isEmpty()) {
                for (String suply : suplier) {
                    if (!suply.isEmpty()) {
                        if (supleir_strg.equalsIgnoreCase(suply)) {
                            filteredtList.add(data);
                            issuplierfound = true;
                            Log.d("Filter", "Supplier Matched");
                            break;
                        }
                    }
                }
                if (issuplierfound) {
                    continue;
                }

                boolean isAc = false;
                String AC = data.getFeature().getAircondition();
                boolean isfeaturefound = false;
                String feature_Aircondition = data.getFeature().getAircondition();
                Log.d("feature_Aircondition", feature_Aircondition);
                if(!AC.isEmpty() && !feature_Aircondition.isEmpty())
                {
                    for (String suply:features)
                    {
                        if(!suply.isEmpty())
                        {
                            if(suply.equalsIgnoreCase("Air Condition"))
                            {
                                filteredtList.add(data);
                                isAc=true;
                                Log.d("Filter","Ac Matched");
                                break;
                            } else if (suply.equalsIgnoreCase("Automatic")){
                                filteredtList.add(data);
                                isfeaturefound=true;
                            }
                        }
                    }
                    if(isAc && isfeaturefound)
                    {
                        continue;
                    }
                }

            }
        }
        Log.d("Filtere list",""+ filteredtList.size());
        isApplyFiltered=true;
    }

    private void getShortedData() {
        Collections.sort(listCarResult, new Comparator<SearchData>() {
            @Override
            public int compare(SearchData o1, SearchData o2) {
                int result = 0 ;
                switch(filter){
                    case "Recommended" :

                        break ;
                  /*  case "Price (Low to High)" :
                        result = (int)(Double.parseDouble(o1.getCar_list())-Double.parseDouble(o2.carmanagement_price)+ab);
                        break ;

                    case "Price (High to Low)" :
                        result = (int)(Double.parseDouble(o2.carmanagement_price)-Double.parseDouble(o1.carmanagement_price));
                        break ;
*/
                    case "Rating" :

                        break ;
                }
                return result;
            }
        });
        listAdapter.notifyDataSetChanged();
    }

    public void openSelectionFilter(View view) {
        Intent intent = new Intent(CarsResultListActivity.this,FilterListActivity.class);
        startActivityForResult(intent,201);
    }

    private void setupoverlay(String set) {

        final EditText edtFname, edtLname, edtemail,edtPhone,edtZip, edtLicense,edtCity,
                edtAddress;
        Spinner edtLicenseOrign;

        Button btupdate, btnCancel;
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (set.equals("login")){
            dialog.setContentView(R.layout.popup_login);
            final EditText edtEmail= dialog.findViewById(R.id.et_email);
            final EditText edtPass= dialog.findViewById(R.id.et_password);
            final Button login= dialog.findViewById(R.id.bt_login);
            Button signup =(Button)dialog.findViewById(R.id.bt_signup);
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(CarsResultListActivity.this, SignUpActivity.class));
                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    emaillogin =edtEmail.getText().toString().trim();
                    pass =edtPass.getText().toString().trim();

                    if (Utility.checkemail(emaillogin)){
                        if (!pass.isEmpty()){
                            login(emaillogin,pass);
                        } else {
                            Utility.message(getApplicationContext(),"Please enter your password");
                        }
                    } else {
                        Utility.message(getApplicationContext(),"Please enter valid email");
                    }
                }
            });

        } else if (set.equals("update_profile")){
            dialog.setContentView(R.layout.popup_updateprofile);
            edtFname = dialog.findViewById(R.id.etUserFirstName);
            edtLname = dialog.findViewById(R.id.etUserLastName);
            edtemail = dialog.findViewById(R.id.etUserEmail);
            edtemail.setText(userDetails.getUser_email());
            edtemail.setEnabled(false);
            edtPhone = dialog.findViewById(R.id.etUserPhoneNo);
            edtZip = dialog.findViewById(R.id.etUserzip);
            edtLicense = dialog.findViewById(R.id.etlicense);
            edtLicenseOrign = dialog.findViewById(R.id.spinnerlicenseorigion);
            txtDob = dialog.findViewById(R.id.etUserDob);
            edtCity = dialog.findViewById(R.id.etcity);
            edtAddress = dialog.findViewById(R.id.etAddress);
            btupdate = dialog.findViewById(R.id.bt_update);
            btnCancel = dialog.findViewById(R.id.bt_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            txtDob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatePickerDialog(CarsResultListActivity.this, date, mCalendar
                            .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                            mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            // Creating adapter for spinner
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, SplashActivity.counrtyList);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            edtLicenseOrign.setAdapter(dataAdapter);

            edtLicenseOrign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String item = adapterView.getItemAtPosition(i).toString();
                    licenseorigin = (String) getKeyFromValue(SplashActivity.country,item);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

//            set onclick on update
            btupdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fname = edtFname.getText().toString().trim();
                    lname = edtLname.getText().toString().trim();
                    email = edtemail.getText().toString().trim();
                    phone = edtPhone.getText().toString().trim();
                    zip = edtZip.getText().toString().trim();
                    license = edtLicense.getText().toString().trim();
                    city = edtCity.getText().toString().trim();
                    address = edtAddress.getText().toString().trim();
                    if (!fname.isEmpty()){
                        if (!lname.isEmpty()){
                            if (Utility.checkemail(email)){
                                if (Utility.checkphone(phone)){
                                    if (!zip.isEmpty()){
                                        if (!license.isEmpty()){
                                            if (!licenseorigin.isEmpty()){
                                                if (!city.isEmpty()){
                                                    if (!address.isEmpty()){
                                                        updateProfile(userid,fname);
                                                    } else {
                                                        Utility.message(getApplication(), getResources().getString(R.string.please_enter_address));
                                                    }
                                                } else {
                                                    Utility.message(getApplication(), getResources().getString(R.string.please_enter_city));
                                                }
                                            } else {
                                                Utility.message(getApplication(), getResources().getString(R.string.please_enter_license_origin));
                                            }
                                        } else {
                                            Utility.message(getApplication(), getResources().getString(R.string.please_enter_license));
                                        }
                                    } else {
                                        Utility.message(getApplication(), getResources().getString(R.string.please_enter_zipcode));
                                    }
                                } else {
                                    Utility.message(getApplication(), getResources().getString(R.string.please_enter_valid_phone_number));
                                }
                            } else {
                                Utility.message(getApplication(), getResources().getString(R.string.please_enter_valid_email));
                            }
                        } else {
                            Utility.message(getApplication(),getResources().getString(R.string.please_enter_last_name));
                        }
                    } else {
                        Utility.message(getApplication(),getResources().getString(R.string.please_enter_first_name));
                    }
                }
            });
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }
    Calendar mCalendar = Calendar.getInstance();
    int year, monthOfYear, dayOfMonth;

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {

        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dob = sdf.format(mCalendar.getTime());
        txtDob.setText(dob);
    }

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    public void getCat() {
        if (catBeanList!=null){
            catBeanList.clear();
        }
        Utility.showloadingPopup(this);
        String cat = gson.toJson(cateRequest);

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
                        category = gson.fromJson(msg,Category.class);
                        try {
                            JSONObject jsonObject = new JSONObject(msg);
                            if (jsonObject.getBoolean("status")){
                                List<Category.ResponseBean.CatBean>catBeans = new ArrayList<>();
                                if (category.getResponse()!=null){
                                    catBeans = category.getResponse().getCat();
                                }

                                final List<Category.ResponseBean.CatBean> finalCatBeans = catBeans;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        catBeanList.addAll(finalCatBeans);
                                      /*  HashMap<String, String>stringHashMap = new HashMap();
                                        HashMap<String, String>imgHashMap = new HashMap();
                                        for (Category.ResponseBean.CatBean catBeans1: catBeanList){
                                            if (!catBeans1.getCategory_name().isEmpty()){
                                                stringHashMap.put(catBeans1.getCategory_name(), String.valueOf(catBeans1.getCode()));
                                                imgHashMap.put(catBeans1.getCategory_name(), String.valueOf(catBeans1.getCategory_image()));
                                            }
                                        }
                                        if (catBeanList!=null){
                                            catBeanList.clear();
                                        }
                                        for ( String key : stringHashMap.keySet() ) {
                                            Category.ResponseBean.CatBean catBean = new Category.ResponseBean.CatBean();
                                            catBean.setCategory_name(key);
                                            catBean.setCode(Integer.parseInt(stringHashMap.get(key)));
                                            catBean.setCategory_image(imgHashMap.get(key));
                                            catBeanList.add(catBean);
                                            System.out.println( key );
                                        }*/

                                           /*//List<String> al = new ArrayList<>();
// add elements to al, including duplicates
                                Set<Category.ResponseBean.CatBean> hs = new LinkedHashSet<>();
                                hs.addAll(catBeanList);
                                catBeanList.clear();
                                catBeanList.addAll(hs);*/
                                        TreeSet<Category.ResponseBean.CatBean> set = new TreeSet<>(new Comparator<Category.ResponseBean.CatBean>() {
                                            @Override
                                            public int compare(Category.ResponseBean.CatBean o1, Category.ResponseBean.CatBean o2) {
                                                if(o1.getCategory_name().equalsIgnoreCase(o2.getCategory_name())){
                                                    return 0;
                                                }
                                                return 1;
                                            }
                                        });
                                        Log.d("TAG", "onResponse: " + catBeanList.size());   // 116
                                        set.addAll(catBeanList);
                                        catBeanList.clear();
                                        catBeanList.addAll(set);
                                        Log.d("TAG", "onResponse: " + catBeanList.size());   // 16



/*
                                        Set<Category.ResponseBean.CatBean>catBeans1 = new TreeSet<>(new Comparator<Category.ResponseBean.CatBean>() {
                                            @Override
                                            public int compare(Category.ResponseBean.CatBean catBean, Category.ResponseBean.CatBean t1) {
                                                if(catBean.getCategory_name().equalsIgnoreCase(t1.getCategory_name())){
                                                    return 0;
                                                }
                                                return 1;
                                            }
                                        });

                                        catBeans1.addAll(catBeanList);
                                        catBeanList.clear();
                                        catBeanList.addAll(catBeans1);*/
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }



    private void login(String user, String pass) {
        if(!Utility.isNetworkConnected(getApplicationContext())){
            Toast.makeText(CarsResultListActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            return;
        }
        Utility.showloadingPopup(this);
        RetroFitApis retroFitApis= RetrofitApiBuilder.getCargHiresapis();
        Call<ApiResponse> responseCall=retroFitApis.login(user,pass);
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                if(response.body().status==true)
                {
                    UserDetails userDetails = new UserDetails();
                    userDetails = response.body().response.user_detail;
                    String logindata=gson.toJson(userDetails);
                    appGlobal.setLoginData(logindata);
                    String st=  appGlobal.getUser_id();
                    dialog.dismiss();

                }
                else{
                    Utility.message(getApplicationContext(), response.body().msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.hidepopup();
                Utility.message(getApplicationContext(),getResources().getString(R.string.no_internet_connection));
            }
        });
    }

    private void updateProfile(String userid, String fname) {
        if(!Utility.isNetworkConnected(getApplicationContext())){
            Toast.makeText(CarsResultListActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            return;
        }
        Utility.showloadingPopup(this);
        RetroFitApis retroFitApis= RetrofitApiBuilder.getCargHiresapis();
        Call<ApiResponse> responseCall=retroFitApis.updateprofile(userid,fname,lname,email,phone,zip,license,
                licenseorigin,dob,city,address);
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                if(response.body().status==true)
                {
                    UserDetails userDetails = new UserDetails();
                    userDetails = response.body().response.user_detail;
                    String logindata=gson.toJson(response.body().response.user_detail);
                    appGlobal.setLoginData(logindata);
                    String st=  appGlobal.getUser_id();
                    dialog.dismiss();
                    Utility.message(getApplicationContext(), response.body().msg);
                }
                else{
                    Utility.message(getApplicationContext(), response.body().msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.hidepopup();
                Utility.message(getApplicationContext(),getResources().getString(R.string.no_internet_connection));
            }
        });
    }


}