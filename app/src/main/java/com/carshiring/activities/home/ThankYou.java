package com.carshiring.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.carshiring.R;
import com.mukesh.tinydb.TinyDB;


public class ThankYou extends AppCompatActivity implements View.OnClickListener {
    Button bt_viewquotes;
    TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);
        bt_viewquotes= (Button) findViewById(R.id.bt_viewquotes);
        bt_viewquotes.setOnClickListener(this);
        tinyDB = new TinyDB(getApplicationContext());
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setTitle("Thank You");
            tinyDB.remove("extra_added");
            tinyDB.remove("full_prot");
        }
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
    public void onClick(View v) {
        Intent intent=new Intent(ThankYou.this, MyBookingActivity.class);
        intent.putExtra("From Quotes","Quotes");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
//        finish();
    }
}
