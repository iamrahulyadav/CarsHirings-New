package com.carshiring.activities.home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.carshiring.R;
import com.carshiring.adapters.FilterValRecyclerAdapter;
import com.carshiring.models.FilterDefaultMultipleListModel;
import com.carshiring.utilities.AppBaseActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class FilterListActivity extends AppBaseActivity implements View.OnClickListener {
    public static String FILTER_RESPONSE = "filter_response";
    public static int FILTER_RESPONSE_CODE = 220;
    private static final String SEPARATOR = ",";
    private FilterValRecyclerAdapter filterValAdapterSupl,filterValAdapterpackFeature;
    Button reset,applyfilter;
    RecyclerView rec_supplier,recy_carfeatures;
    private ArrayList<String> supplier = new ArrayList<>();
    private ArrayList<String> features = new ArrayList<>();

    public static ArrayList<FilterDefaultMultipleListModel> supplierMultipleListModelsSelected = new ArrayList<>();
    public static ArrayList<FilterDefaultMultipleListModel> featuresMultipleListModelsSelected  = new ArrayList<>();
    private ArrayList<FilterDefaultMultipleListModel> supplierMultipleListModels = new ArrayList<>();
    private ArrayList<FilterDefaultMultipleListModel> featuresMultipleListModels = new ArrayList<>();
    private ArrayList<String> SelectedSupplier = new ArrayList<String>();
    private ArrayList<String> SelectedFeatures = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_list);
//        find id

        rec_supplier=findViewById(R.id.rec_supplier);
        recy_carfeatures=findViewById(R.id.recy_carfeatures);
        ArrayList<String> getlist= (ArrayList<String>) CarsResultListActivity.supplierList;
        supplier .addAll(getlist);
        features = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.filter_features)));

        FilterDefaultMultipleListModel modelsup,modelfeat;
        if (supplierMultipleListModelsSelected!=null){
            for(FilterDefaultMultipleListModel model:supplierMultipleListModelsSelected)
            {
                model.setChecked(true);
            }
        }
        if (featuresMultipleListModelsSelected!=null){
            for(FilterDefaultMultipleListModel model:featuresMultipleListModelsSelected)
            {
                model.setChecked(true);
            }
        }

        for (String sup:supplier)
        {
            modelsup = new FilterDefaultMultipleListModel();
            modelsup.setName(sup);
            supplierMultipleListModels.add(modelsup);
            supplierMultipleListModelsSelected.addAll(supplierMultipleListModels);
        }

        for(String feat:features)
        {
            modelfeat = new FilterDefaultMultipleListModel();
            modelfeat.setName(feat);
            featuresMultipleListModels.add(modelfeat);
            featuresMultipleListModelsSelected.addAll(featuresMultipleListModels);
        }

        filterValAdapterSupl=new FilterValRecyclerAdapter(this,R.layout.filter_list_val_item_layout,supplierMultipleListModels);
        rec_supplier.setAdapter(filterValAdapterSupl);
        rec_supplier.setLayoutManager(new LinearLayoutManager(this));
        rec_supplier.setHasFixedSize(true);

        filterValAdapterpackFeature=new FilterValRecyclerAdapter(this,R.layout.filter_list_val_item_layout,featuresMultipleListModels);
        recy_carfeatures.setAdapter(filterValAdapterpackFeature);
        recy_carfeatures.setLayoutManager(new LinearLayoutManager(this));
        recy_carfeatures.setHasFixedSize(true);

        filterValAdapterSupl.setonclick(new FilterValRecyclerAdapter.OnClickItem() {
            @Override
            public void itemclick(View v, int i) {
                selectedSupplier(i);
            }
        });
        filterValAdapterpackFeature.setonclick(new FilterValRecyclerAdapter.OnClickItem() {
            @Override
            public void itemclick(View v, int i) {
                selectfeature(i);
            }
        });

        setuptoolbar();

        //Buttons
        reset= findViewById(R.id.reset);
        applyfilter=  findViewById(R.id.apply_filter);

        reset.setOnClickListener(this);
        applyfilter.setOnClickListener(this);


    }

    private void selectfeature(int i) {
        filterValAdapterpackFeature.setitemselected(i);
    }

    private void selectedSupplier(int i) {
        filterValAdapterSupl.setitemselected(i);
    }

    private void setuptoolbar() {
        actionBar = getSupportActionBar() ;
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        actionBar.setTitle(getResources().getString(R.string.filter));
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
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.reset:
                for(FilterDefaultMultipleListModel model:supplierMultipleListModels)
                {
                    model.setChecked(false);
                }
                for(FilterDefaultMultipleListModel model:featuresMultipleListModels)
                {
                    model.setChecked(false);
                }
                filterValAdapterSupl.notifyDataSetChanged();
                filterValAdapterpackFeature.notifyDataSetChanged();
                break;
            case R.id.apply_filter:
                if(SelectedFeatures!=null || SelectedSupplier!=null)
                {
                    SelectedSupplier.clear();
                    SelectedFeatures.clear();
                }
                for(FilterDefaultMultipleListModel model:supplierMultipleListModels)
                {
                    if (model.isChecked())
                    {
                        SelectedSupplier.add(model.getName());
                    }
                }
                for(FilterDefaultMultipleListModel model:featuresMultipleListModels)
                {
                    if (model.isChecked())
                    {
                        SelectedFeatures.add(model.getName());
                    }
                }
                FilterDefaultMultipleListModel listModel=new FilterDefaultMultipleListModel();

                if (SelectedSupplier.size()>0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String se : SelectedSupplier) {
                        stringBuilder.append(se);
                        stringBuilder.append(SEPARATOR);
                    }
                    String sel = stringBuilder.toString();
                    sel = sel.substring(0, sel.length() - SEPARATOR.length());
                    listModel.setSupplier(sel);
                }
                else
                {
                    listModel.setSupplier("");
                }

                if (SelectedFeatures.size()>0)
                {
                    StringBuilder stringBuilder2=new StringBuilder();
                    for(String se:SelectedFeatures)
                    {
                        stringBuilder2.append(se);
                        stringBuilder2.append(SEPARATOR);
                    }
                    String sel2=stringBuilder2.toString();
                    sel2=sel2.substring(0,sel2.length()-SEPARATOR.length());
                    listModel.setFeatures(sel2.toString());
                }
                else
                {
                    listModel.setFeatures("");
                }
                Intent filrestintent=new Intent();
                filrestintent.putExtra(FILTER_RESPONSE,listModel);
                setResult(FILTER_RESPONSE_CODE,filrestintent);
                finish();
                break;
        }

    }
}
