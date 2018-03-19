package com.carshiring.activities.home;

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
    UserDetails userDetails = new UserDetails();
    Gson gson = new Gson();
    public List<BookingHistory> currentBookingData;
    private TinyDB tinyDB;
    private String token, userId;

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

    }

    private List<BookingHistory> bookingHistory = new ArrayList<>();

    public void getBook() {
        if (bookingHistory != null) {
            bookingHistory.clear();
        }
        if (bookingHistoryList1 != null){
            bookingHistoryList1.clear();
        }
        if (bookingHistoryList2 != null){
            bookingHistoryList2.clear();
        }
        Utility.showloadingPopup(MyBookingActivity.this);
        RetroFitApis fitApis= RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> bookingDataCall = fitApis.booking_history(userId);

        bookingDataCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                Log.d("TAG", "onResponse:book "+gson.toJson(response.body().response));

                if (response.body()!=null){
                    if (response.body().status==true){
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                        List<BookingHistory>booking_detail = response.body().response.booking;
                        setMyTabs();
                        for (BookingHistory bookingData1 : booking_detail){
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date1 = null;
                            try {
                                date1 = format.parse(timeStamp);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date date2 = null;
                            try {
                                date2 = format.parse(bookingData1.getBooking_from_date());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date1.compareTo(date2) >0 || bookingData1.getBooking_status().equals("3")
                                    || bookingData1.getBooking_status().equals("2")) {
                                if (!bookingData1.getBooking_status().equals("0")){
                                    bookingHistoryList2.add(bookingData1);
                                    Collections.sort(bookingHistoryList2, new Comparator<BookingHistory>() {
                                        @Override
                                        public int compare(BookingHistory o1, BookingHistory o2) {
                                            if (o1.getBooking_from_date() == null || o2.getBooking_from_date() == null)
                                                return 0;
                                            return o2.getBooking_from_date().compareTo(o1.getBooking_from_date());
                                        }
                                    });
                                }
                            }

                            if (date1.compareTo(date2) <0 ) {
                                bookingHistoryList1.add(bookingData1);
                                Collections.sort(bookingHistoryList1, new Comparator<BookingHistory>() {
                                    @Override
                                    public int compare(BookingHistory o1, BookingHistory o2) {
                                        if (o1.getBooking_from_date() == null || o2.getBooking_from_date() == null)
                                            return 0;
                                        return o2.getBooking_from_date().compareTo(o1.getBooking_from_date());
                                    }
                                });
                            }
                        }
                    }
                    else {
                        Utility.message(getApplicationContext(), response.body().msg);
                    }
                } else {
                    Utility.message(getApplicationContext(), response.body().msg);
                }



               /* if (response!=null && response.body().status){
                    bookingHistory.addAll(response.body().response.booking);
                    setMyTabs(bookingHistory);
                } else {
                    Toast.makeText(MyBookingActivity.this, "No data found ", Toast.LENGTH_SHORT).show();
                }*/
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("TAG", "onFailure: "+t.getMessage());
                Utility.hidepopup();
            }
        });

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

        getBook();
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
