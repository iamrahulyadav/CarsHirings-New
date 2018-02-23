package com.carshiring.activities.home;

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
import android.widget.Toast;

import com.carshiring.R;
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
import java.util.List;
import java.util.Set;
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
    String fname,lname,email,phone,zip,license,licenseorigin,city,address,emaillogin,pass,set ="",userid="",dob;
    RecyclerView recycler_search_cars;
    CatRequest cateRequest = new CatRequest();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_result_list);
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


    }


    public void listdispaly(List<SearchData> listCarResult )
    {
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
            listdispaly(filteredtList);
        }
        else
        {
            listdispaly(listCarResult);
        }
        isApplyFiltered = false ;
        recycler_search_cars.setAdapter(listAdapter);
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
        Button btupdate;
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
//            set onclick on update
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
                                                            Utility.message(getApplication(), "Please enter address");
                                                        }
                                                    } else {
                                                        Utility.message(getApplication(), "Please enter city");
                                                    }
                                                } else {
                                                    Utility.message(getApplication(), "Please enter licenseorigin");
                                                }
                                            } else {
                                                Utility.message(getApplication(), "Please enter license");
                                            }
                                        } else {
                                            Utility.message(getApplication(), "Please enter zipcode");
                                        }
                                    } else {
                                        Utility.message(getApplication(), "Please enter valid phone number");
                                    }
                                } else {
                                    Utility.message(getApplication(), "Please enter valid email");
                                }
                            }
                            else {
                                Utility.message(getApplication(),"Please enter Date of Birth");
                            }
                        } else {
                            Utility.message(getApplication(),"Please enter last name");
                        }
                    } else {
                        Utility.message(getApplication(),"Please enter First name");
                    }

                    dialog.dismiss();
                }
            });
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    private void login(String user, String pass) {
        if(!Utility.isNetworkConnected(getApplicationContext())){
            Toast.makeText(CarsResultListActivity.this, "No Network Connection!", Toast.LENGTH_SHORT).show();
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
                    userDetails = response.body().response.userdetail;
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
                Utility.message(getApplicationContext(),"Connection Error");
            }
        });
    }

    private void updateProfile(String userid, String fname) {
        if(!Utility.isNetworkConnected(getApplicationContext())){
            Toast.makeText(CarsResultListActivity.this, "No Network Connection!", Toast.LENGTH_SHORT).show();
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
                    userDetails = response.body().response.userdetail;
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
                Utility.message(getApplicationContext(),"Connection Error");
            }
        });
    }

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    public void getCat(){
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
                        Utility.message(getApplicationContext(), "Connection error ");
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
                        catBeanList.addAll(category.getResponse().getCat());
                    }
                    Log.d("TAG", "onResponse: "+catBeanList.size());

                }
            }

        });
    }


}
