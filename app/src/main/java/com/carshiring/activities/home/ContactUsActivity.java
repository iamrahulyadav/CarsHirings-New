package com.carshiring.activities.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carshiring.R;
import com.carshiring.utilities.AppBaseActivity;
import com.mukesh.tinydb.TinyDB;

public class ContactUsActivity extends AppBaseActivity {

    private TinyDB sharedpref;
    private String language_code;

    private TextView tv_supplier, tv_pvt, tv_gen_query, tv_title;
    private LinearLayout layout_supplier, layout_pvt, layout_gen_query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        sharedpref = new TinyDB(getApplicationContext());
        language_code = sharedpref.getString("language_code");
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}