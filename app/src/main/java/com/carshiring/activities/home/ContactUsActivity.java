package com.carshiring.activities.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carshiring.R;
import com.carshiring.utilities.AppBaseActivity;
import com.mukesh.tinydb.TinyDB;

public class ContactUsActivity extends AppBaseActivity implements View.OnClickListener {

    private TinyDB sharedpref;
    private String language_code ;

    private TextView tv_supplier, tv_pvt, tv_gen_query, tv_title;
    private LinearLayout layout_supplier, layout_pvt, layout_gen_query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        sharedpref=new TinyDB(getApplicationContext());
        language_code = sharedpref.getString("language_code");
        actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }

        init();
    }

    private void init(){
        tv_title = (TextView) findViewById(R.id.contact_us_tv_form_title);
        tv_supplier = (TextView) findViewById(R.id.contact_us_tv_supplier);
        tv_pvt = (TextView) findViewById(R.id.contact_us_tv_pvt);
        tv_gen_query = (TextView) findViewById(R.id.contact_us_tv_gen_query);
        layout_supplier = (LinearLayout) findViewById(R.id.contact_us_layout_supplier);
        layout_pvt = (LinearLayout) findViewById(R.id.contact_us_layout_pvt);
        layout_gen_query = (LinearLayout) findViewById(R.id.contact_us_layout_gen_query);

        tv_supplier.setOnClickListener(this);
        tv_pvt.setOnClickListener(this);
        tv_gen_query.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        tv_supplier.setBackground(getResources().getDrawable(R.drawable.buttoncurve_2));
        tv_pvt.setBackground(getResources().getDrawable(R.drawable.buttoncurve_1));
        tv_gen_query.setBackground(getResources().getDrawable(R.drawable.buttoncurve_1));

        tv_supplier.setTextColor(getResources().getColor(R.color.white));
        tv_pvt.setTextColor(getResources().getColor(R.color.black));
        tv_gen_query.setTextColor(getResources().getColor(R.color.black));

        actionBar.setTitle(getResources().getString(R.string.action_contact_us));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.contact_us_tv_supplier){

            tv_title.setText(getResources().getString(R.string.supplier));

            tv_supplier.setTextColor(getResources().getColor(R.color.white));
            tv_pvt.setTextColor(getResources().getColor(R.color.black));
            tv_gen_query.setTextColor(getResources().getColor(R.color.black));

            tv_supplier.setBackground(getResources().getDrawable(R.drawable.buttoncurve_2));
            tv_pvt.setBackground(getResources().getDrawable(R.drawable.buttoncurve_1));
            tv_gen_query.setBackground(getResources().getDrawable(R.drawable.buttoncurve_1));

            layout_supplier.setVisibility(View.VISIBLE);
            layout_pvt.setVisibility(View.GONE);
            layout_gen_query.setVisibility(View.GONE);

        }else if(v.getId() == R.id.contact_us_tv_pvt){

            tv_title.setText(getResources().getString(R.string.pvt_co_hire_car));

            tv_supplier.setTextColor(getResources().getColor(R.color.black));
            tv_pvt.setTextColor(getResources().getColor(R.color.white));
            tv_gen_query.setTextColor(getResources().getColor(R.color.black));

            tv_pvt.setBackground(getResources().getDrawable(R.drawable.buttoncurve_2));
            tv_supplier.setBackground(getResources().getDrawable(R.drawable.buttoncurve_1));
            tv_gen_query.setBackground(getResources().getDrawable(R.drawable.buttoncurve_1));

            layout_supplier.setVisibility(View.GONE);
            layout_pvt.setVisibility(View.VISIBLE);
            layout_gen_query.setVisibility(View.GONE);

        }else if(v.getId() == R.id.contact_us_tv_gen_query){

            tv_title.setText(getResources().getString(R.string.general_query));

            tv_supplier.setTextColor(getResources().getColor(R.color.black));
            tv_pvt.setTextColor(getResources().getColor(R.color.black));
            tv_gen_query.setTextColor(getResources().getColor(R.color.white));

            tv_gen_query.setBackground(getResources().getDrawable(R.drawable.buttoncurve_2));
            tv_pvt.setBackground(getResources().getDrawable(R.drawable.buttoncurve_1));
            tv_supplier.setBackground(getResources().getDrawable(R.drawable.buttoncurve_1));

            layout_supplier.setVisibility(View.GONE);
            layout_pvt.setVisibility(View.GONE);
            layout_gen_query.setVisibility(View.VISIBLE);
        }
        else{

        }
    }
}
