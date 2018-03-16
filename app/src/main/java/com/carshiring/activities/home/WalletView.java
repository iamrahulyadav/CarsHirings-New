package com.carshiring.activities.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.carshiring.R;
import com.carshiring.adapters.WalletViewAdapter;
import com.carshiring.utilities.AppBaseActivity;

public class WalletView extends AppBaseActivity {

    private RecyclerView recyclerView;
    private String wallet_title;
    private TextView textView_amount, textView_affected, textView_type, textView_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_view);

        actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }

        init();
    }

    private void init(){
        textView_amount = (TextView) findViewById(R.id.wallet_view_amount);
        textView_affected = (TextView) findViewById(R.id.wallet_view_affected);
        textView_type = (TextView) findViewById(R.id.wallet_view_type);
        textView_title = (TextView) findViewById(R.id.wallet_view_title);

        recyclerView = (RecyclerView) findViewById(R.id.wallet_view_recycler);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(WalletView.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);

//        setMyAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        wallet_title = getIntent().getExtras().getString("wallet_title");
        setValues(wallet_title);
        setMyAdapter();
    }

    private void setValues(String wallet_title){
        if(wallet_title.equals("Wallet History Details")){
            textView_title.setText(wallet_title);
            textView_amount.setText(getResources().getString(R.string.wallet_amnt));
            textView_affected.setText(getResources().getString(R.string.wallet_affected_amnt));
            textView_type.setText(getResources().getString(R.string.wallet_type));
            actionBar.setTitle(getResources().getString(R.string.my_wallwt_hist));
        }else if(wallet_title.equals("Point History Details")){
            textView_title.setText(wallet_title);
            textView_amount.setText(getResources().getString(R.string.booking_point));
            textView_affected.setText(getResources().getString(R.string.booking_affect_point));
            textView_type.setText(getResources().getString(R.string.booking_point));
            actionBar.setTitle(getResources().getString(R.string.point_hist));
        } else{}
    }

    private void setMyAdapter(){
        WalletViewAdapter adapter = new WalletViewAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
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
