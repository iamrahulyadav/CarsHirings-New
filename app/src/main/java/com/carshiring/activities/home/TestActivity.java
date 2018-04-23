package com.carshiring.activities.home;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.carshiring.R;
import com.carshiring.adapters.TestAdapter;
import com.carshiring.fragments.SearchCarFragment;
import com.carshiring.models.Category;
import com.carshiring.models.SearchData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<SearchData>searchDataList = new ArrayList<>();
    Category category = new Category();
    List<Category.ResponseBean.CatBean>catBeanList = new ArrayList<>();
    TestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        recyclerView = findViewById(R.id.recyc);

        searchDataList = SearchCarFragment.searchData;
        category = SearchCarFragment.category;
        catBeanList = category.getResponse().getCat();
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(catBeanList, (Comparator.comparing(Category.ResponseBean.CatBean::getCategory_name)));
        }*/

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
