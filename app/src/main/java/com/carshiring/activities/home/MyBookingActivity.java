package com.carshiring.activities.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.adapters.MyBookingAdapter;
import com.carshiring.fragments.CurrentBookingFragment;
import com.carshiring.fragments.PreviousBookingFragment;
import com.carshiring.fragments.QuotesFragment;
import com.carshiring.models.BookingData;
import com.carshiring.models.BookingHistory;
import com.carshiring.models.UserDetails;
import com.carshiring.utilities.AppBaseActivity;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookingActivity extends AppBaseActivity {

    ViewPager pager;
    String s;
    public static MyBookingAdapter adapter;

    UserDetails userDetails = new UserDetails();
    Gson gson = new Gson();
    public List<BookingHistory> currentBookingData;
    private TinyDB tinyDB;
    private String token, userId;
    String qu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);
        tinyDB = new TinyDB(getApplicationContext());
        String login = tinyDB.getString("login_data");
        userDetails = gson.fromJson(login, UserDetails.class);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }

        currentBookingData = new ArrayList<>();

        userId = userDetails.getUser_id();
        token = tinyDB.getString("access_token");
        Intent it = getIntent();
      /* if (it != null) {
            qu = it.getStringExtra("From Quotes");
            if (qu != null) {
                if (qu.equalsIgnoreCase("Quotes")) {
                    setupSubView(R.id.action_quotes);
                }
            } else {
                setupSubView(R.id.action_search_car);
            }
        }*/

    }


    private void setMyTabs(){

        TabLayout tabLayout= (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.current)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.previous)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.quotes)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        pager= (ViewPager) findViewById(R.id.viewpager_view);
        setupViewPager(pager);
        tabLayout.setupWithViewPager(pager);
    }

    public static List<BookingHistory> bookingHistoryList1 = new ArrayList<>();
    public static List<BookingHistory> bookingHistoryList2 = new ArrayList<>();

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CurrentBookingFragment(), getResources().getString(R.string.txtCurrent));
        adapter.addFragment(new PreviousBookingFragment(),  getResources().getString(R.string.txtPrevious));
        adapter.addFragment(new QuotesFragment(),  getResources().getString(R.string.txtQuotes));
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actionBar.setTitle(R.string.txtmybooking);
        token = tinyDB.getString("access_token");
        setMyTabs();
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i); // Launch the HomescreenActivity
        finish();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
