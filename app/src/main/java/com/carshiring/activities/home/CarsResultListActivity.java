package com.carshiring.activities.home;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
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

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.carshiring.models.TestData;
import com.carshiring.models.UserDetails;
import com.carshiring.splash.SplashActivity;
import com.carshiring.utilities.AppBaseActivity;
import com.carshiring.utilities.AppGlobal;
import com.carshiring.utilities.CustomLayoutManager;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.carshiring.activities.home.MainActivity.getKeyFromValue;

public class CarsResultListActivity extends AppBaseActivity {
    Gson gson = new Gson();
    private List<SearchData> listCarResultCatFilter;

    public List<TestData> catBeanList = new ArrayList<>();
    List<SearchData> listCarResult = new ArrayList<>();
    List<SearchData.FeatureBean> featuresAllList = new ArrayList<>();
    public static List<String> supplierList = new ArrayList<>();
    List<Integer> cateList = new ArrayList<>();
    CarResultsListAdapter listAdapter;
    UserDetails userDetails = new UserDetails();
    TinyDB tinyDB;
    private boolean isClicked;
    private   LinearLayout allView;
    public static String id_context, refertype, type, day, time;
    AppGlobal appGlobal = AppGlobal.getInstancess();
    Dialog dialog;
    TextView tvFromDate,txtDob, tvPickDate, tvTodate, txtPlaceDrop;
    String fname, lname,filter ,email, phone, zip, license, licenseorigin, city, address, emaillogin, pass, set = "", userid = "", dob;
    RecyclerView recycler_search_cars;
    CatRequest cateRequest = new CatRequest();
    CarListCategory adapter;
    ImageView cat_image_all;
    HashMap<String, ArrayList<String>> codePrice;
    private RecyclerView recyclerView_carlist_category;
    private List<Category.ResponseBean.CatBean> catListdata = new ArrayList<>();
    public static int row_index=-1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars_result_list2);

        filter = getResources().getString(R.string.recommended);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
        codePrice = new HashMap<>();
        appGlobal.context = getApplicationContext();
        tinyDB = new TinyDB(getApplicationContext());
        dialog = new Dialog(this);
        allView = findViewById(R.id.all_view);

        // Complet Car List
        listCarResult.addAll(SearchCarFragment.searchData);
        //   catListdata = SearchCarFragment.categoryBeanList;

        tvFromDate = (TextView) findViewById(R.id.tvFromDT);
        tvPickDate = (TextView) findViewById(R.id.txtPlaceName);
        tvTodate = (TextView) findViewById(R.id.tvToDT);
        txtPlaceDrop = findViewById(R.id.txtPlaceName_drop);

        tvFromDate.setText(SearchCarFragment.pick_date + "\n" + SearchCarFragment.pickTime);
        tvPickDate.setText(SearchCarFragment.pickName);
        tvTodate.setText(SearchCarFragment.drop_date + "\n" + SearchCarFragment.dropTime);
        txtPlaceDrop.setText(SearchCarFragment.dropName);

        cat_image_all = (ImageView) findViewById(R.id.car_cat_image_all);
        Glide.with(getApplicationContext())
                .load("https://carshiring.com/site/images/car.png")
                .into(cat_image_all);

//        get supplier
        if (supplierList != null) {
            supplierList.clear();
        }
        for (SearchData searchData : listCarResult) {
            supplierList.add(searchData.getSupplier());
        }

        allView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index=-1;
                adapter.notifyDataSetChanged();
                cat_All();
            }
        });

        Set<String> hs = new HashSet<>();
        hs.addAll(supplierList);
        supplierList.clear();
        supplierList.addAll(hs);
        recycler_search_cars = (RecyclerView) findViewById(R.id.recycler_search_cars);
        recyclerView_carlist_category = (RecyclerView) findViewById(R.id.recycler_carlist_by_category);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(CarsResultListActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView_carlist_category.setLayoutManager(horizontalLayoutManagaer);
        // Category List
        catBeanList = SearchCarFragment.catBeanList;
        for (TestData testData : catBeanList) {
            cateList.add(Integer.parseInt(testData.getCat_id()));
        }
        Log.d(TAG, "onCreate: dataaaaa"+new Gson().toJson(cateList));
        getCat(cateList);
    }

    JSONObject obj;
    private void getCat(List<Integer> list) {
        if (catListdata != null) {
            catListdata.clear();
        }
        cateRequest.setCode(list);
        Utility.showloadingPopup(CarsResultListActivity.this);
        String cat = gson.toJson(cateRequest);
        Log.d(TAG, "getCat: " + cat);
        String url = RetrofitApiBuilder.CarHires_BASE_URL + "category_list_byid";
        try {
            obj = new JSONObject(cat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                url, obj, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Utility.hidepopup();
                Category category = new Category();
                category = gson.fromJson(response.toString(), Category.class);
                if (category!=null&&category.getResponse()!=null){
                    if (category.getResponse().getCat()!=null){
                        catListdata.addAll(category.getResponse().getCat());

                        adapter = new CarListCategory(CarsResultListActivity.this, listCarResult, catListdata, catBeanList,
                                new CarListCategory.OnItemClickListenerCategory() {
                                    @Override
                                    public void onItemClickCategory(int position) {
                                        allView.setBackgroundResource(R.drawable.buttoncurve_caategory);
                                        isClicked = true;
                                        catgory_clicked(position);
                                    }
                                });
                        recyclerView_carlist_category.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utility.hidepopup();

                Log.d(TAG, "onErrorResponse: " + error);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);

    }

    private void catgory_clicked(int position) {
        listCarResultCatFilter = new ArrayList<>();
        listCarResultCatFilter.clear();

        for (int i = 0; i < listCarResult.size(); i++) {
            ArrayList<String> integers = new ArrayList<>();
            integers.addAll(catBeanList.get(position).getCat_code());
            for (int j = 0; j < integers.size(); j++) {
                String k = integers.get(j) + "";
                if (String.valueOf(k).equals(listCarResult.get(i).getCategory())) {
                    listCarResultCatFilter.add(listCarResult.get(i));
                }
            }
        }
        listdispaly(listCarResultCatFilter);
    }

    public void cat_All() {
        allView.setBackgroundColor(Color.parseColor("#079607"));
        listdispaly(listCarResult);
    }

    double pointpercent, calPoint, calPrice;
    String oneway, driverSur;

    public void listdispaly(List<SearchData> listCarResult) {
        CarResultsListAdapter listAdapter;
        listAdapter = new CarResultsListAdapter(this, listCarResult, catBeanList, new CarResultsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SearchData carDetail) {
                if (tinyDB.contains("login_data")) {
                    String data = tinyDB.getString("login_data");
                    userDetails = gson.fromJson(data, UserDetails.class);
                    userid = userDetails.getUser_id();
                    if (userDetails.getUser_lname() == null || userDetails.getUser_lname().length() == 0) {
                        set = "update_profile";
                        setupoverlay(set);
                    } else {

                        id_context = carDetail.getId_context();
                        type = carDetail.getType();
                        refertype = carDetail.getRefer_type();
                        for (SearchData.CoveragesBean bean : carDetail.getCoverages()) {
                            if (bean.getCode().equalsIgnoreCase("412")) {
                                oneway = bean.getName() + " : " + bean.getCurrency2() + " "
                                        + bean.getAmount2();
                            } else if (bean.getCode().equalsIgnoreCase("410")) {
                                driverSur = bean.getName() + " : " + bean.getCurrency2() + " "
                                        + bean.getAmount2();
                            }
                        }
                        Intent intent = new Intent(CarsResultListActivity.this, CarDetailActivity.class);
                        intent.putExtra("id_context", id_context);
                        intent.putExtra("type", type);
                        double pricea = Double.parseDouble(carDetail.getPrice());
                        pointpercent = Double.parseDouble(SearchCarFragment.pointper);
                        double markUp = Double.parseDouble(SearchCarFragment.markup);
                        double d = pricea;
                        double priceNew = d + (d * markUp) / 100;
                        calPrice = (priceNew * pointpercent) / 100;
                        calPoint = (int) (calPrice / 0.02);
                        day = carDetail.getTime();
                        time = carDetail.getTime_unit();
                        intent.putExtra("day", carDetail.getTime());
                        intent.putExtra("refer_type", refertype);
                        intent.putExtra("point_earn", String.valueOf(calPoint));
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//     CustomLayoutManager manager = new CustomLayoutManager(getApplicationContext()){
//            @Override
//            public boolean canScrollVertically() {
//                return false;
//            }
//        };
        recycler_search_cars.setLayoutManager(layoutManager);
        recycler_search_cars.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL) {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                // Do not draw the divider
            }
        });

        if (isApplyFiltered) {
            if (filteredtList.size() > 0) {
                listdispaly(filteredtList);
            } else {
                // Old List
//                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_filters_applied), Toast.LENGTH_SHORT).show();
                listdispaly(filteredtList);
            }
        } else {
            listdispaly(listCarResult);
        }
        isApplyFiltered = false;

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


        // Sort
        /*if(requestCode== 200){
            if(resultCode== RESULT_OK){
                filter = data.getStringExtra("filter");
                getShortedData() ;
            }
        }*/

        // Filter
        if (requestCode == 201) {
            if (resultCode == SelectFilterActivity.FILTER_RESPONSE_CODE) {
                Log.d("VKK", "Total no of car list = " + listCarResult.size());
                FilterDefaultMultipleListModel multipleListModel = (FilterDefaultMultipleListModel) data.getSerializableExtra(SelectFilterActivity.FILTER_RESPONSE);
                Log.d("VKK", "Total Filtered List = " + gson.toJson(multipleListModel));
                String supl = multipleListModel.getSupplier();
                String feat = multipleListModel.getFeatures();
                Log.d("VKK", "Total Suppliers Filtered = " + supl);
                Log.d("VKK", "Total Features Filtered = " + feat);

                if (supl.equals("NoSuppliers") && feat.equals("NoFeatures")) {
                    Toast.makeText(getApplicationContext(), "No Filter Applied", Toast.LENGTH_SHORT).show();
                } else {
                    filter(supl, feat);
                }
            }
        }
    }

    private boolean isApplyFiltered = false, isFeatureFound = false, isSupplierFound = false;
    private ArrayList<SearchData> filteredtList, filteredtList1, filteredtList2;
    List<SearchData>searchDataList = new ArrayList<>();

    private void filter(String supl, String feat) {

        String[] suplier = supl.split(",");
        String[] features = feat.split(",");

        Log.d("VKK", "Total no of Suppliers Filtered = " + suplier.length + " ");
        Log.d("VKK", "Total no of Features Filtered = " + features.length + " ");
        filteredtList = new ArrayList<>();
        filteredtList1 = new ArrayList<>();
        filteredtList2 = new ArrayList<>();

        outerloop:

        if (isClicked){
            searchDataList = listCarResultCatFilter;
        } else {
            searchDataList = listCarResult;
        }
        for (int i = 0; i < searchDataList.size(); i++) {

            isFeatureFound = false;
            isSupplierFound = false;

            SearchData data = searchDataList.get(i);

            for (int z = 0; z < suplier.length; z++) {

                String str = suplier[z];
                if (str.equals(data.getSupplier())) {
                    filteredtList1.add(data);
                    isSupplierFound = true;
                } else {
                    isSupplierFound = false;
                    //            break;
                }
            }

          /*  if (isSupplierFound){
                filteredtList1.add(data);
            }else{}
*/

            for (int z = 0; z < features.length; z++) {
                String str = features[z];
                SearchData.FeatureBean data1 = new SearchData.FeatureBean();

                data1 = data.getFeature();
                String f = gson.toJson(data1);

                try {
                    JSONObject job = new JSONObject(f);

                    String aircondition = job.getString("aircondition");
                    String bag = job.getString("bag");
                    String door = job.getString("door");
                    String fueltype = job.getString("fueltype");
                    String transmission = job.getString("transmission");

                    if (str.equals("Air Condition")) {
                        if (aircondition.equals("true")) {
                            isFeatureFound = true;
//                        filteredtList.add(data);
                        } else {
                            isFeatureFound = false;
                            break;
                        }
                    } else if (str.equals("Automatic")) {
                        if (transmission.equals("Automatic")) {
                            isFeatureFound = true;
//                        filteredtList.add(data);
                        } else {
                            isFeatureFound = false;
                            break;
                        }
                    } else if (str.equals("4+ Doors")) {
                        if (door.equals("4")) {
                            isFeatureFound = true;
//                        filteredtList.add(data);
                        } else {
                            isFeatureFound = false;
                            break;
                        }
                    } else {
                    }

/*                    if (aircondition.equals("true")) {
                        isFeatureFound = true;
//                        filteredtList.add(data);
                    } else if (transmission.equals("Automatic")) {
                        isFeatureFound = true;
//                        filteredtList.add(data);
                    } else if (door.equals("4")) {
                        isFeatureFound = true;
//                        filteredtList.add(data);
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }


/*
                String[] f1 = f.split(",");

                for(int y =0; y<f1.length; y++){

                    if(str.equals(f1[y])) {

                        isFeatureFound = true;
                    }
*/
            }
            if (isFeatureFound) {
                filteredtList2.add(data);
            } else {
            }
        }

        // FilteredList1 = 15
        // FilteredList2 = 64

        if (filteredtList1.size() != 0) {

            for (int f1 = 0; f1 < filteredtList1.size(); f1++) {
                SearchData data1 = new SearchData();
                data1 = filteredtList1.get(f1);

                if (filteredtList2.size() != 0) {
                    for (int f2 = 0; f2 < filteredtList2.size(); f2++) {

                        SearchData data2 = new SearchData();
                        data2 = filteredtList2.get(f2);

                        if (data1.getId_context().equals(data2.getId_context())) {
                            filteredtList.add(data1);
                        } else {
                        }
                    }
                } else {
                    if (features[0].equals("NoFeatures")) {
                        // No Features selected
                        filteredtList.addAll(filteredtList1);
                    } else {
                        // Features Selected but FilterLis2 Size is 0
                        filteredtList.clear();
                    }
                    break;
                }
            }
        } else {
            filteredtList.addAll(filteredtList2);
        }


        Log.d("VKK", filteredtList1.size() + " ");
        Log.d("VKK", filteredtList2.size() + " ");
        Log.d("VKK", filteredtList.size() + " ");

        isApplyFiltered = true;
    }


    private void filter1(String supl, String feat) {

        String[] suplier = supl.split(",");
        String[] features = feat.split(",");


        Log.d("VKK", "Total no of Suppliers Filtered = " + suplier.length + " ");
        Log.d("VKK", "Total no of Features Filtered = " + features.length + " ");


        filteredtList1 = new ArrayList<>();

        outerloop:
        for (int i = 0; i < listCarResult.size(); i++) {

            isFeatureFound = false;
            isSupplierFound = false;

            SearchData data = listCarResult.get(i);

            for (int z = 0; z < suplier.length; z++) {

                String str = suplier[z];
                if (str.equals(data.getSupplier())) {
                    //filteredtList.add(data);
                    isSupplierFound = true;
                } else {
                    isSupplierFound = false;
                    break;
                }
            }


            if (isSupplierFound) {
                filteredtList1.add(data);
            } else {
            }


            for (int z = 0; z < features.length; z++) {
                String str = features[z];
                SearchData.FeatureBean data1 = new SearchData.FeatureBean();

                data1 = data.getFeature();
                String f = gson.toJson(data1);

                try {
                    JSONObject job = new JSONObject(f);

                    String aircondition = job.getString("aircondition");
                    String bag = job.getString("bag");
                    String door = job.getString("door");
                    String fueltype = job.getString("fueltype");
                    String transmission = job.getString("transmission");

                    if (str.equals("Air Condition")) {
                        if (aircondition.equals("true")) {
                            isFeatureFound = true;
//                        filteredtList.add(data);
                        } else {
                            isFeatureFound = false;
                            break outerloop;
                        }
                    } else if (str.equals("Automatic")) {
                        if (transmission.equals("Automatic")) {
                            isFeatureFound = true;
//                        filteredtList.add(data);
                        } else {
                            isFeatureFound = false;
                            break outerloop;
                        }
                    } else if (str.equals("4+ Doors")) {
                        if (door.equals("4")) {
                            isFeatureFound = true;
//                        filteredtList.add(data);
                        } else {
                            isFeatureFound = false;
                            break outerloop;
                        }
                    } else {
                    }

/*                    if (aircondition.equals("true")) {
                        isFeatureFound = true;
//                        filteredtList.add(data);
                    } else if (transmission.equals("Automatic")) {
                        isFeatureFound = true;
//                        filteredtList.add(data);
                    } else if (door.equals("4")) {
                        isFeatureFound = true;
//                        filteredtList.add(data);
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }


/*
                String[] f1 = f.split(",");

                for(int y =0; y<f1.length; y++){

                    if(str.equals(f1[y])) {



                        isFeatureFound = true;
                    }
*/
            }

            boolean alreadyExist = false;
            if (isFeatureFound) {

                if (isSupplierFound) {

                    for (int x = 0; x < filteredtList1.size(); x++) {
                        SearchData data2 = new SearchData();
                        data2 = filteredtList1.get(x);
                        if (!data2.getId_context().equals(data.getId_context())) {
                            alreadyExist = false;
                            //filteredtList.add(data);
                        } else {
                            alreadyExist = true;
                            break;
                        }
                    }
                    if (!alreadyExist) {
                        filteredtList1.add(data);
                    } else {
                    }
                } else {
                    filteredtList1.add(data);
                }
            } else {
            }
        }

        Log.d("VKK", filteredtList1.size() + " ");

        isApplyFiltered = true;
    }

    private void getShortedData() {
        Collections.sort(listCarResult, new Comparator<SearchData>() {
            @Override
            public int compare(SearchData o1, SearchData o2) {
                int result = 0;
                switch (filter) {
                    case "Recommended":

                        break;
                  /*  case "Price (Low to High)" :
                        result = (int)(Double.parseDouble(o1.getCar_list())-Double.parseDouble(o2.carmanagement_price)+ab);
                        break ;

                    case "Price (High to Low)" :
                        result = (int)(Double.parseDouble(o2.carmanagement_price)-Double.parseDouble(o1.carmanagement_price));
                        break ;
*/
                    case "Rating":

                        break;
                }
                return result;
            }
        });
        listAdapter.notifyDataSetChanged();
    }

    public void openSelectionFilter(View view) {
        Intent intent = new Intent(CarsResultListActivity.this, FilterListActivity.class);
        startActivityForResult(intent, 201);
    }

    private void setupoverlay(String set) {

        final EditText edtFname, edtLname, edtemail, edtPhone, edtZip, edtLicense, edtCity,
                edtAddress;
        Spinner edtLicenseOrign;

        Button btupdate, btnCancel;
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (set.equals("login")) {
            dialog.setContentView(R.layout.popup_login);
            final EditText edtEmail = dialog.findViewById(R.id.et_email);
            final EditText edtPass = dialog.findViewById(R.id.et_password);
            final Button login = dialog.findViewById(R.id.bt_login);
            Button signup = (Button) dialog.findViewById(R.id.bt_signup);
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(CarsResultListActivity.this, SignUpActivity.class));
                }
            });
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    emaillogin = edtEmail.getText().toString().trim();
                    pass = edtPass.getText().toString().trim();

                    if (Utility.checkemail(emaillogin)) {
                        if (!pass.isEmpty()) {
                            login(emaillogin, pass);
                        } else {
                            Utility.message(getApplicationContext(), "Please enter your password");
                        }
                    } else {
                        Utility.message(getApplicationContext(), "Please enter valid email");
                    }
                }
            });

        } else if (set.equals("update_profile")) {
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
                    dob_pick();
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
                    licenseorigin = (String) getKeyFromValue(SplashActivity.country, item);

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
                    if (!fname.isEmpty()) {
                        if (!lname.isEmpty()) {
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
                        } else {
                            Utility.message(getApplication(), getResources().getString(R.string.please_enter_last_name));
                        }
                    } else {
                        Utility.message(getApplication(), getResources().getString(R.string.please_enter_first_name));
                    }
                }
            });
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    Calendar mCalendar = Calendar.getInstance();
    long timeInMilliseconds;
    private int mYear, mMonth, mDay;

    public void dob_pick() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = Calendar.getInstance().get(Calendar.YEAR) - 18;
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        timeInMilliseconds = Utility.getTimeDate(mYear + "-" + mMonth + "-" + mDay);
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        dob = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        txtDob.setText(Utility.convertSimpleDate(dob));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(timeInMilliseconds);
        datePickerDialog.show();

    }

    private void updateLabel() {

        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dob = sdf.format(mCalendar.getTime());
        txtDob.setText(dob);
    }

    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");

    private void login(String user, String pass) {
        if (!Utility.isNetworkConnected(getApplicationContext())) {
            Toast.makeText(CarsResultListActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            return;
        }
        Utility.showloadingPopup(this);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getCargHiresapis();
        Call<ApiResponse> responseCall = retroFitApis.login(user, pass);
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                if (response.body().status == true) {
                    UserDetails userDetails = new UserDetails();
                    userDetails = response.body().response.user_detail;
                    String logindata = gson.toJson(userDetails);
                    appGlobal.setLoginData(logindata);
                    String st = appGlobal.getUser_id();
                    dialog.dismiss();

                } else {
                    Utility.message(getApplicationContext(), response.body().msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.hidepopup();
                Utility.message(getApplicationContext(), getResources().getString(R.string.no_internet_connection));
            }
        });
    }

    private void updateProfile(String userid, String fname) {
        if (!Utility.isNetworkConnected(getApplicationContext())) {
            Toast.makeText(CarsResultListActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            return;
        }
        Utility.showloadingPopup(this);
        RetroFitApis retroFitApis = RetrofitApiBuilder.getCargHiresapis();
        Call<ApiResponse> responseCall = retroFitApis.updateprofile(userid, fname, lname, email, phone, zip, license,
                licenseorigin, dob, city, address);
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                if (response.body().status == true) {
                    UserDetails userDetails = new UserDetails();
                    userDetails = response.body().response.user_detail;
                    String logindata = gson.toJson(response.body().response.user_detail);
                    appGlobal.setLoginData(logindata);
                    String st = appGlobal.getUser_id();
                    dialog.dismiss();
                    Utility.message(getApplicationContext(), response.body().msg);
                } else {
                    Utility.message(getApplicationContext(), response.body().msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.hidepopup();
                Utility.message(getApplicationContext(), getResources().getString(R.string.no_internet_connection));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        tinyDB.remove("listSup");
        tinyDB.remove("listFet");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        tinyDB.remove("listSup");
//        tinyDB.remove("listFet");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        row_index=-1;
    }
}