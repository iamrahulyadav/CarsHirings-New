package com.carshiring.activities.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carshiring.R;
import com.carshiring.adapters.MyExListAdapter;
import com.carshiring.utilities.Utility;
import com.mukesh.tinydb.TinyDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.carshiring.activities.home.CarDetailActivity.fullAmtValue;
import static com.carshiring.activities.home.CarDetailActivity.fullprotectionammount;
import static com.carshiring.activities.home.CarDetailActivity.fullprotectioncurrency;


public class ExcessProtectionActivity extends AppCompatActivity implements View.OnClickListener {
    ExpandableListView expListView;
    List<String> head;
    HashMap<String,List<String>> body;
    MyExListAdapter listAdapter;
    TextView text1,text2,text3,toolbartext,txt_fullprotection;
    LinearLayout layout1,layout2,layout3,layoutbuttons;
    TextView extrapro;
    Toolbar toolbar;
    Button bt_standPro,bt_fullPro;
    String FromQuotes="";
    TinyDB tinyDB;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excess_protection);
        View v=findViewById(android.R.id.content);
        toolbar=  findViewById(R.id.bottomToolBar);
        toolbartext= toolbar.findViewById(R.id.txt_bot);
        toolbartext.setText(getResources().getString(R.string.add_full_protection));
        tinyDB = new TinyDB(getApplicationContext());
        layoutbuttons= (LinearLayout) findViewById(R.id.ll_buttons);
        bt_standPro= (Button)findViewById(R.id.bt_standardPro);
        bt_fullPro=(Button) findViewById(R.id.bt_fullpro);
        txt_fullprotection=findViewById(R.id.txt_fullprotection);

        txt_fullprotection.setText(getResources().getString(R.string.full_protection_only) +" "+ "SAR " +
                " " + String.valueOf(fullAmtValue) + getResources().getString(R.string.for_day));

        bt_fullPro.setOnClickListener(this);
        bt_standPro.setOnClickListener(this);

        toolbartext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tinyDB.putString("full_prot", fullprotectioncurrency+" "+String.valueOf(fullAmtValue));
                finish();
                Utility.message(getApplication(), getResources().getString(R.string.full_protection_addedd));
            }
        });

        Intent it=getIntent();
        if(it!=null)
        {
            String val=it.getStringExtra("get");
            if(val.equalsIgnoreCase("ForProtec"))
            {
                layoutbuttons.setVisibility(View.GONE);

            }
            else if(val.equalsIgnoreCase("FromActi"))
            {
                toolbar.setVisibility(View.GONE);

            }
            else if(val.equalsIgnoreCase("Forquotes"))
            {
               toolbar.setVisibility(View.GONE);
                bt_standPro.setText(getResources().getString(R.string.get_a_quote_1));
                bt_fullPro.setText(getResources().getString(R.string.get_a_quote_2));
                FromQuotes="Yes";
            }

        }

        extrapro= (TextView) findViewById(R.id.txt_extra_pro);
        extrapro.setOnClickListener(this);

        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.excess_prot));
        }
    }


    LinearLayout  layout ;
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id) {
            case R.id.txt_extra_pro:
                startActivity(new Intent(ExcessProtectionActivity.this, FullProtectionActivity.class));
                break;
        }
     /*   int id=v.getId();
       Intent intent= new Intent(ExcessProtectionActivity.this,DriverSelectionActivity.class);
        if(layout!=null) layout.setVisibility(View.GONE);
        switch (id)
        {
            case R.id.txt_1:
                layout1.setVisibility(View.VISIBLE);
                layout = layout1 ;
                break;
            case R.id.txt_2:
                layout2.setVisibility(View.VISIBLE);
                layout = layout2 ;
                break;
            case R.id.txt_3:
                layout3.setVisibility(View.VISIBLE);
                layout = layout3;
                break;
            case R.id.txt_extra_pro:
                startActivity(new Intent(ExcessProtectionActivity.this,FullProtectionActivity.class));
                break;
            case R.id.bt_fullpro:
                if(FromQuotes.isEmpty()) {
                    startActivity(new Intent(ExcessProtectionActivity.this, DriverSelectionActivity.class));
                }
                else {
                    intent.putExtra("FromQuotes", FromQuotes);
                    startActivity(intent);
                }
                break;
            case R.id.bt_standardPro:
                if(FromQuotes.isEmpty())
                    startActivity(new Intent(ExcessProtectionActivity.this,DriverSelectionActivity.class));
                else {
                    intent.putExtra("FromQuotes", FromQuotes);
                    startActivity(intent);
                }
                break;
            default:
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
