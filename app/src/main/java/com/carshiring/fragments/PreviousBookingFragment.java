package com.carshiring.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.activities.home.MyBookingActivity;
import com.carshiring.adapters.BookingAdapter;
import com.carshiring.adapters.MyBookingAdapter;
import com.carshiring.models.BookingData;
import com.carshiring.models.BookingHistory;
import com.carshiring.models.UserDetails;
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

import static com.carshiring.activities.home.MyBookingActivity.adapter;
import static com.carshiring.activities.home.MyBookingActivity.bookingHistoryList1;

/**
 * Created by sony on 01-05-2017.
 */

public class PreviousBookingFragment extends Fragment implements BookingAdapter.ClickItem, SwipeRefreshLayout.OnRefreshListener{

    View view;
    Button bt_search;
    RecyclerView recyclerView;
    MyBookingAdapter bookingAdapter;
    private List<BookingHistory> bookingData;
    TinyDB tinyDB;
    SwipeRefreshLayout swipeRefreshLayout;
    String token, userId;
    LinearLayout linearLayout;
    UserDetails userDetails = new UserDetails();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_my_booking_previous,container,false);

        bookingData = new ArrayList<>();
        tinyDB = new TinyDB(getContext());
        String  login = tinyDB.getString("login_data");
        userDetails = gson.fromJson(login, UserDetails.class);
        userId =userDetails.getUser_id();
        token = tinyDB.getString("access_token");
        bt_search= (Button) view.findViewById(R.id.bt_previousbooking);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.previous_booking_refresh_layout);
        linearLayout = (LinearLayout) view.findViewById(R.id.rec_prev_booki_no_data);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.redStrong));
        swipeRefreshLayout.setOnRefreshListener(this);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        recyclerView= (RecyclerView) view.findViewById(R.id.rec_prev_booki_list);

        RecyclerView.LayoutManager mlayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mlayoutManager);

        return view;
    }

    private void setMyAdapter(List<BookingHistory> bookingHistory){
        bookingAdapter = new MyBookingAdapter(bookingHistory, getContext(),"p");
        if (bookingHistory.size()>0){
            recyclerView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            recyclerView.setAdapter(bookingAdapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    Gson gson = new Gson() ;


    public void getBook() {
        if (bookingData != null) {
            bookingData.clear();
        }

        Utility.showloadingPopup(getActivity());
        RetroFitApis fitApis= RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> bookingDataCall = fitApis.booking_history(userId);

        bookingDataCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                Log.d("TAG", "onResponse:book "+gson.toJson(response.body().response));

                if (response.body()!=null){
                    if (response.body().status){
                        @SuppressLint("SimpleDateFormat")
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                        List<BookingHistory>booking_detail =new ArrayList<>();
                        booking_detail = response.body().response.booking;
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
                                    || bookingData1.getBooking_status().equals("2")||bookingData1.getBooking_status().equals("0")) {

                                    bookingData.add(bookingData1);
                                    Collections.sort(bookingData, new Comparator<BookingHistory>() {
                                        @Override
                                        public int compare(BookingHistory o1, BookingHistory o2) {
                                            if (o1.getBooking_from_date() == null || o2.getBooking_from_date() == null)
                                                return 0;
                                            return o2.getBooking_from_date().compareTo(o1.getBooking_from_date());
                                        }
                                    });
                            }
                        }
                        if (bookingData.size()>0){
                            linearLayout.setVisibility(View.GONE);
                            setMyAdapter(bookingData);

                        } else {

                            linearLayout.setVisibility(View.VISIBLE);
                        }
                        bookingAdapter.notifyDataSetChanged();
                    }
                    else {
                        Utility.message(getContext(), response.body().message);
                    }
                } else {
                    Utility.message(getContext(), response.body().message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("TAG", "onFailure: "+t.getMessage());
                Utility.hidepopup();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setMyAdapter(bookingData);
        getBook();
    }

    private void setuptoolbar() {
        Toolbar toolbar= (Toolbar) view.findViewById(R.id.bottomToolBar);
        TextView textView= (TextView) toolbar.findViewById(R.id.txt_bot);
        ImageView imageView= (ImageView) toolbar.findViewById(R.id.img_bot);
        textView.setText("Add");
        imageView.setImageResource(R.drawable.ic_add);
    }

    @Override
    public void click(View view, int Position) {
     /*   Toast.makeText(getContext(),""+Position,Toast.LENGTH_LONG).show();
        startActivity(new Intent(getContext(), BookingHistory.class));*/

    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        getBook();
        setMyAdapter(bookingData);

        swipeRefreshLayout.setRefreshing(false);

    }
}
