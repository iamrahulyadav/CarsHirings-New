package com.carshiring.activities.home;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.adapters.Page_Adapter;
import com.carshiring.fragments.CurrentBookingFragment;
import com.carshiring.fragments.DriversFragment;
import com.carshiring.fragments.PreviousBookingFragment;
import com.carshiring.fragments.QuotesFragment;
import com.carshiring.models.BookingHistory;
import com.carshiring.utilities.AppBaseActivity;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.mukesh.tinydb.TinyDB;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyBookingActivity extends AppBaseActivity {

    ViewPager pager;
    String s;

    public List<BookingHistory> currentBookingData;
    private TinyDB tinyDB;
    private String token, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);

        actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }

        tinyDB = new TinyDB(getApplicationContext());
        currentBookingData = new ArrayList<>();

        userId = tinyDB.getString("userid");
        token = tinyDB.getString("access_token");

//        getBooking();

    }

    private List<BookingHistory> bookingHistory = new ArrayList<>();
    private void getBooking(){
        if(currentBookingData !=null)
        {
            currentBookingData.clear();
        }

        //     Toast.makeText(getContext(), userId+"", Toast.LENGTH_SHORT).show();

        Utility.showloadingPopup(MyBookingActivity.this);
        RetroFitApis fitApis= RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> bookingDataCall = fitApis.booking_history("19");

        bookingDataCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                if (response.body()!=null){
                    if (response.body().status==true){

                        bookingHistory = response.body().response.booking;

                        setMyTabs(bookingHistory);
                        //
// setMyAdapter(bookingHistory);
/*
                        String str = gson.toJson(bookingHistory);
                        Log.d("TAG", str);
                        List<BookingHistory> bookingHistory1 = new ArrayList<>();
                        bookingHistory1.addAll(bookingHistory1);
*/

//                        String msg = response.body().toString();
                        //                      bookingHistory = gson.fromJson(msg, BookingHistory.class);

//                        bookingHistory = response.body().response.bookingHistory;
//                        Toast.makeText(getContext(), bookingHistory.getMessage(), Toast.LENGTH_SHORT).show();;
//                        List<BookingHistory.ResponseBean.BookingBean> obj = new ArrayList<>();
//                        obj = response.body().response.bookingData;
//                       Toast.makeText(getContext(), obj.size()+" ", Toast.LENGTH_SHORT).show();



                        /*
                        BookingHistory history = gson.fromJson(response.body().toString(), BookingHistory.class);

                        List<BookingHistory.ResponseBean.BookingBean> bookingBeans = new ArrayList<>();

                        bookingBeans = history.getResponse().getBooking();

                        currentBookingData.addAll(bookingBeans);

                        Log.d("Data Size", bookingBeans.size()+"");
*/
                        /*String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(Calendar.getInstance().getTime());

                        List<BookingData>booking_detail = response.body().response.booking_detail;
                        for (BookingData bookingData1 : booking_detail){

                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                            Date date1 = null;
                            try {
                                date1 = format.parse(timeStamp);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date date2 = null;
                            try {
                                date2 = format.parse(bookingData1.getBookingdetail_from_date());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date1.compareTo(date2) <0 ) {
                                    currentBookingData.add(bookingData1);
                                    Collections.sort(currentBookingData, new Comparator<BookingData>() {
                                        @Override
                                        public int compare(BookingData o1, BookingData o2) {
                                            if (o1.getBookingdetail_from_date() == null || o2.getBookingdetail_from_date() == null)
                                                return 0;
                                            return o2.getBookingdetail_from_date().compareTo(o1.getBookingdetail_from_date());
                                        }
                                    });


                            }
                        }
                        if (currentBookingData.size()>0){
                            linearLayout.setVisibility(View.GONE);
                        } else {
                            linearLayout.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                        bookingAdapter.notifyDataSetChanged();

                    */
                    }
                    else {
                        Utility.message(getApplicationContext(), response.body().msg);
                    }
                } else {
                    Utility.message(getApplicationContext(), response.body().msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.message(getApplicationContext(), t.getMessage());
            }
        });
    }


    private void setMyTabs(List<BookingHistory> bookingHistory){

        TabLayout tabLayout= (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.current)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.previous)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.quotes)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        pager= (ViewPager) findViewById(R.id.viewpager_view);
        setupViewPager(pager, bookingHistory);
        tabLayout.setupWithViewPager(pager);
    }

    public static List<BookingHistory> bookingHistoryList1 = new ArrayList<>();
    public static List<BookingHistory> bookingHistoryList2 = new ArrayList<>();

    private void setupViewPager(ViewPager viewPager, List<BookingHistory> bookingHistory) {

//        CurrentBookingFragment fragment1 = new CurrentBookingFragment();
//        PreviousBookingFragment fragment2 = new PreviousBookingFragment();
//        Bundle bundle = new Bundle();

        for(int i=0; i<bookingHistory.size(); i++){
            if(bookingHistory.get(i).getBooking_status().equals("0")){
                bookingHistoryList1.add(bookingHistory.get(i));
      //          ArrayList<BookingHistory> list = new ArrayList<>();
    //            list.addAll(bookingHistoryList);
  //              bundle.putParcelableArrayList("bookingHistoryList", list);
//                fragment1.setArguments(bundle);
            } else if (bookingHistory.get(i).getBooking_status().equals("1") || bookingHistory.get(i).getBooking_status().equals("2")){
                bookingHistoryList2.add(bookingHistory.get(i));
        //        ArrayList<BookingHistory> list = new ArrayList<>();
          //      list.addAll(bookingHistoryList);
            //    bundle.putParcelableArrayList("bookingHistoryList", list);
              //  fragment2.setArguments(bundle);
            } else {}
        }
//        Toast.makeText(getApplicationContext(), bookingHistory.size() + " " + bookingHistoryList1.size() + " " + bookingHistoryList2.size(), Toast.LENGTH_SHORT).show();

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

        userId = tinyDB.getString("userid");
        token = tinyDB.getString("access_token");

        getBooking();
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
