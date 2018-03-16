package com.carshiring.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
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

/**
 * Created by sony on 01-05-2017.
 */

public class PreviousBookingFragment extends Fragment implements BookingAdapter.ClickItem, SwipeRefreshLayout.OnRefreshListener{

    View view;
    Button bt_search;
    RecyclerView recyclerView;
    BookingAdapter bookingAdapter;
    public List<BookingData> bookingData;
    TinyDB tinyDB;
    SwipeRefreshLayout swipeRefreshLayout;
    String token, userId;
    LinearLayout linearLayout;

    private List<BookingHistory> list = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        list = getArguments().getParcelableArrayList("bookingHistoryList");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_my_booking_previous,container,false);

        tinyDB = new TinyDB(getContext());
        bookingData = new ArrayList<>();
        userId = tinyDB.getString("userid");
        token = tinyDB.getString("access_token");
        bt_search= (Button) view.findViewById(R.id.bt_previousbooking);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.previous_booking_refresh_layout);
        linearLayout = (LinearLayout) view.findViewById(R.id.rec_prev_booki_no_data);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.redStrong));
        swipeRefreshLayout.setOnRefreshListener(this);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchCarFragment searchCarFragment=new SearchCarFragment();
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.subview_container,searchCarFragment).addToBackStack(null).commit();
            }
        });

        recyclerView= (RecyclerView) view.findViewById(R.id.rec_prev_booki_list);
//        bookingAdapter=new BookingAdapter(getContext(),list);
////        bookingAdapter.submit(this);
        RecyclerView.LayoutManager mlayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mlayoutManager);
//        recyclerView.setAdapter(bookingAdapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
//        recyclerView.setVisibility(View.VISIBLE);

        setMyAdapter(MyBookingActivity.bookingHistoryList2);
        setuptoolbar();
        return view;
    }

    private void setMyAdapter(List<BookingHistory> bookingHistory){
        MyBookingAdapter adapter = new MyBookingAdapter(getContext(), bookingHistory);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(adapter);
    }
/*
    private void getBooking(){
        if(bookingData!=null)
        {
            bookingData.clear();
        }
        Utility.showloadingPopup(getActivity());
        RetroFitApis fitApis= RetrofitApiBuilder.getRetrofitGlobal();
        Call<ApiResponse> bookingDataCall = fitApis.getBooking(token, userId);
        bookingDataCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                if (response.body()!=null){
                    if (response.body().status==true){
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
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
                            if (date1.compareTo(date2) >0 || bookingData1.getBooking_status().equals("3")
                                    || bookingData1.getBooking_status().equals("2")) {
                                if (!bookingData1.getBooking_status().equals("0")){
                                    bookingData.add(bookingData1);
                                    Collections.sort(bookingData, new Comparator<BookingData>() {
                                        @Override
                                        public int compare(BookingData o1, BookingData o2) {
                                            if (o1.getBookingdetail_from_date() == null || o2.getBookingdetail_from_date() == null)
                                                return 0;
                                            return o2.getBookingdetail_from_date().compareTo(o1.getBookingdetail_from_date());
                                        }
                                    });
                                }
                            }
                        }
                        if (bookingData.size()>0){
                            linearLayout.setVisibility(View.GONE);
                        } else {
                            linearLayout.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                        bookingAdapter.notifyDataSetChanged();
                    }
                    else {
                        Utility.message(getContext(), response.body().msg);
                    }
                } else {
                    Utility.message(getContext(), response.body().msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.message(getContext(), t.getMessage());
            }
        });
    }
*/


    @Override
    public void onResume() {
        super.onResume();
//        getBooking();
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
       /* getBooking();
        bookingAdapter=new BookingAdapter(getContext(),bookingData);
        recyclerView.setAdapter(bookingAdapter);
        swipeRefreshLayout.setRefreshing(false);*/

    }
}
