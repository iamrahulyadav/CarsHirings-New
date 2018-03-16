package com.carshiring.activities.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.carshiring.R;
import com.carshiring.adapters.CarListCategory;
import com.carshiring.adapters.CarResultsListAdapter;
import com.carshiring.fragments.SearchCarFragment;
import com.carshiring.models.FilterDefaultMultipleListModel;
import com.carshiring.models.CatRequest;
import com.carshiring.models.Category;
import com.carshiring.models.SearchData;
import com.carshiring.models.UserDetails;
import com.carshiring.utilities.AppBaseActivity;
import com.carshiring.utilities.AppGlobal;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarsResultListActivity extends AppBaseActivity {
    Gson gson = new Gson();

    public String filter ;
    Category category = new Category();
    public static List<Category.ResponseBean.CatBean>catBeanList = new ArrayList<>();
    List<SearchData> listCarResult =  new ArrayList<>();
    List<SearchData.FeatureBean> featuresAllList =  new ArrayList<>();
    public static List<String>supplierList=new ArrayList<>();
    List<String>featuresList=new ArrayList<>();
    List<Integer>cateList=new ArrayList<>();
    CarResultsListAdapter listAdapter;
    UserDetails userDetails = new UserDetails();
    TinyDB tinyDB;
    AppGlobal appGlobal=AppGlobal.getInstancess();
    Dialog dialog;
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

        listCarResult = SearchCarFragment.searchData;
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

        adapter = new CarListCategory(getApplicationContext(), listCarResult, catBeanList, new CarListCategory.OnItemClickListenerCategory() {
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


        Toast.makeText(getApplicationContext(), catBeanList.get(position).getCode() + "", Toast.LENGTH_SHORT).show();

        for(int i=0; i<listCarResult.size(); i++) {
            if((catBeanList.get(position).getCode()+"").equals(listCarResult.get(i).getCategory())){

                listCarResult1.add(listCarResult.get(i));
            }
        }
        Log.d("Size", listCarResult.size()+"");
        Log.d("size", listCarResult1.size()+"");
        listdispaly(listCarResult1);
//        listAdapter.notifyDataSetChanged();

    }

    public void cat_All(View v){
        listdispaly(listCarResult);
    }

    public void listdispaly(List<SearchData> listCarResult ) {
        Log.d("Search Data List", listCarResult.size()+"");
        CarResultsListAdapter listAdapter;
        listAdapter = new CarResultsListAdapter(this,listCarResult, new CarResultsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SearchData carDetail) {

                if (tinyDB.contains("login_data")){
                    String data = tinyDB.getString("login_data");
                    userDetails = gson.fromJson(data,UserDetails.class);
                    userid = userDetails.getUser_id();
                    if (userDetails.getUser_name()==null || userDetails.getUser_name().length()==0){
                        set = "update_profile";
                        setupoverlay(set);
                    } else {
                        Intent intent = new Intent(CarsResultListActivity.this,CarDetailActivity.class);
                        intent.putExtra("id_context",carDetail.getId_context());
                        intent.putExtra("type",carDetail.getType());
                        intent.putExtra("day",carDetail.getTime());
                        intent.putExtra("refer_type",carDetail.getRefer_type());
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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_search_cars.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recycler_search_cars.addItemDecoration(itemDecoration);
        if(isApplyFiltered)
        {
            if(filteredtList.size() == 0) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_filters_applied), Toast.LENGTH_SHORT).show();
                listdispaly(listCarResult);
            }else listdispaly(filteredtList);
        }
        else
        {
            listdispaly(listCarResult);
        }
        isApplyFiltered = false ;
//        recycler_search_cars.setAdapter(listAdapter);
        getCat();
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
                String pack=multipleListModel.getPackages();
                String feat=multipleListModel.getFeatures();
                String insur=multipleListModel.getInsurances();
                if(supl!=null || pack!=null || feat!=null || insur!=null)
                {
                    filterlist(supl,pack,feat,insur);
                }
            }
        }
    }

    private boolean isApplyFiltered = false ;
    private  ArrayList<SearchData>  filteredtList ;
    private void filterlist(String supl, String pack, String feat, String insur) {

        String[] suplier=supl.split(",");
        String[] packages=pack.split(",");
        String[] features=feat.split(",");
        String[] insurance=insur.split(",");

        filteredtList=new ArrayList<>();
        int listsize=listCarResult.size();

        for(int i=0;i<listsize;i++)
        {
            SearchData data = listCarResult.get(i);

            boolean issuplierfound=false;
            String supleir_strg=  data.getSupplier();
            Log.d("Supplier",supleir_strg);

            if(!supleir_strg.isEmpty())
            {
                for (String suply:suplier)
                {
                    if(!suply.isEmpty())
                    {
                        if(supleir_strg.equalsIgnoreCase(suply))
                        {
                            filteredtList.add(data);
                            issuplierfound=true;
                            Log.d("Filter","Supplier Matched");
                            break;
                        }
                    }
                }
                if(issuplierfound)
                {
                    continue;
                }
            }
            boolean ispackagefound=false;
            String package_strg=data.getPackageX();
            Log.d("Package",package_strg);
            if(!package_strg.isEmpty())
            {
                for (String pac:packages)
                {
                    if(!pac.isEmpty())
                    {
                        if(package_strg.equalsIgnoreCase(pac))
                        {
                            filteredtList.add(data);
                            ispackagefound=true;
                            Log.d("Filter","Package Matched");
                            break;
                        }
                    }
                }
                if(ispackagefound)
                {
                    continue;
                }
            }
            boolean isfeaturefound=false;
           String feature_Aircondition=data.getFeature().getTransmission();
            Log.d("feature_Aircondition",feature_Aircondition);
            if(!feature_Aircondition.isEmpty())
            {
               for (String Air:features)
               {
                   if (!Air.isEmpty())
                   {
                       if(feature_Aircondition.contains("Automatic"))
                       {
                           filteredtList.add(data);
                           isfeaturefound=true;
                           Log.d("Filter","Package Matched");
                           break;
                       }
                   }
               }
               if (isfeaturefound)
               {
                   continue;
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
                        result = (int)(Double.parseDouble(o1.getCar_list())-Double.parseDouble(o2.carmanagement_price)+1);
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
        Intent intent = new Intent(CarsResultListActivity.this,SelectFilterActivity.class);
        startActivityForResult(intent,201);
    }

    private void setupoverlay(String set) {

        final EditText edtFname, edtLname, edtemail,edtPhone,edtZip, edtLicense,edtLicenseOrign,edtCity, edtAddress,etdob;
        Button btupdate, btnCancel;
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (set.equals("login")){
            dialog.setContentView(R.layout.popup_login);
            final EditText edtEmail= dialog.findViewById(R.id.et_email);
            final EditText edtPass= dialog.findViewById(R.id.et_password);
            final Button login= dialog.findViewById(R.id.bt_login);
            Button signup =(Button)dialog.findViewById(R.id.bt_signup);
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
            etdob= dialog.findViewById(R.id.etUserDob);
            edtemail = dialog.findViewById(R.id.etUserEmail);
            edtPhone = dialog.findViewById(R.id.etUserPhoneNo);
            edtZip = dialog.findViewById(R.id.etUserzip);
            edtLicense = dialog.findViewById(R.id.etlicense);
            edtLicenseOrign = dialog.findViewById(R.id.etlicenseorigion);
            edtCity = dialog.findViewById(R.id.etcity);
            edtAddress = dialog.findViewById(R.id.etAddress);
            btupdate = dialog.findViewById(R.id.bt_update);
            btnCancel = dialog.findViewById(R.id.bt_cancel);
            edtemail.setText(userDetails.getUser_email());
            edtemail.setEnabled(false);
//            set onclick on update

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            btupdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fname = edtFname.getText().toString().trim();
                    lname = edtLname.getText().toString().trim();
                    dob=etdob.getText().toString().trim();
                    email = edtemail.getText().toString().trim();
                    phone = edtPhone.getText().toString().trim();
                    zip = edtZip.getText().toString().trim();
                    license = edtLicense.getText().toString().trim();
                    licenseorigin = edtLicenseOrign.getText().toString().trim();
                    city = edtCity.getText().toString().trim();
                    address = edtAddress.getText().toString().trim();
                    if (!fname.isEmpty()){
                        if (!lname.isEmpty()){
                            if(!dob.isEmpty()) {
                                if (Utility.checkemail(email)) {
                                    if (Utility.checkphone(phone)) {
                                        if (!zip.isEmpty()) {
                                            if (!license.isEmpty()) {
                                                if (!licenseorigin.isEmpty()) {
                                                    if (!city.isEmpty()) {
                                                        if (!address.isEmpty()) {
                                                            updateProfile(userid, fname);
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
                            }
                            else {
                                Utility.message(getApplication(),getResources().getString(R.string.please_enter_dob));
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

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    public void getCat(){
        catBeanList.clear();
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

                        List<Category.ResponseBean.CatBean>catBeans = new ArrayList<>();
                        catBeans = category.getResponse().getCat();
                        final List<Category.ResponseBean.CatBean> finalCatBeans = catBeans;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                catBeanList.addAll(finalCatBeans);

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


                                //2nd
/*
                                TreeSet<Category.ResponseBean.CatBean> set1 = new TreeSet<>(new Comparator<Category.ResponseBean.CatBean>() {
                                    @Override
                                    public int compare(Category.ResponseBean.CatBean o1, Category.ResponseBean.CatBean o2) {
                                        if(o1.getCategory_name().equalsIgnoreCase(o2.getCategory_name())){
                                            return 0;
                                        }
                                        return 1;
                                    }
                                });
                                Log.d("TAG", "onResponse: " + catBeanList.size());    // 16
                                set1.addAll(catBeanList);
                                catBeanList.clear();
                                catBeanList.addAll(set1);
                                Log.d("TAG", "onResponse: " + catBeanList.size());   // 16
*/



                                adapter.notifyDataSetChanged();
                            }
                        });

                        Log.d("TAG", "onResponse: "+ msg);
                    }
  //                  Log.d("TAG", "onResponse: " + catBeanList.size());
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