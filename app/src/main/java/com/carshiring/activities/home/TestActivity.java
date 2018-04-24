package com.carshiring.activities.home;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.carshiring.R;
import com.carshiring.adapters.TestAdapter;
import com.carshiring.fragments.SearchCarFragment;
import com.carshiring.models.Category;
import com.carshiring.models.SearchData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<SearchData>searchDataList = new ArrayList<>();
    Category category = new Category();
    List<Category.ResponseBean.CatBean>catBeanList = new ArrayList<>();
    HashMap<String, String>codeNameMap=new HashMap<>();
    HashMap<String, ArrayList<Double>>codePriceMap=new HashMap<>();
    HashMap<String, ArrayList<Double>>idMap=new HashMap<>();
    TestAdapter adapter;
    String TAG = TestActivity.class.getName();
    ArrayList<Double>priceList;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        recyclerView = findViewById(R.id.recyc);

        searchDataList = SearchCarFragment.searchData;
        category = SearchCarFragment.category;
        catBeanList = category.getResponse().getCat();
        for (Category.ResponseBean.CatBean catBean : catBeanList){
            priceList = new ArrayList<>();
            codeNameMap.put(String.valueOf(catBean.getCode()), catBean.getCategory_name());
            for (SearchData searchData : searchDataList){
                if (String.valueOf(catBean.getCode()).equalsIgnoreCase(searchData.getCategory())){
                    String code  = String.valueOf(catBean.getCode());
                    priceList.add(Double.parseDouble(searchData.getPrice()));
                    codePriceMap.put(String.valueOf(catBean.getCode()), priceList);
                    double price = Collections.min(priceList);

                }

                idMap.put(String.valueOf(catBean.getCategory_id()), priceList);

            }
        }


        Log.d(TAG, "onCreate: testmap"+gson.toJson(codePriceMap));
        Log.d(TAG, "onCreate: testprice"+gson.toJson(idMap));




    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(TestActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        adapter = new TestAdapter(getApplicationContext(), searchDataList, catBeanList);
        recyclerView.setAdapter(adapter);
    }
}
