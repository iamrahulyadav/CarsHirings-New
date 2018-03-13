package com.carshiring.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.carshiring.R;
import com.carshiring.adapters.BookingAdapter;
import com.carshiring.models.BookingData;
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
 * Created by rakhi on 13-03-2018.
 */

public class CurrentBookingFragment extends Fragment {
    View view;
    Button bt_search;
    BookingAdapter bookingAdapter;
    public List<BookingData> currentBookingData;
    TinyDB tinyDB;
    String token, userId;
    LinearLayout linearLayout;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_my_booking_current,container,false);
        recyclerView= (RecyclerView) view.findViewById(R.id.fragment_my_booking_current_recycler);
        linearLayout = (LinearLayout) view.findViewById(R.id.fragment_my_booking_current_no_data_view);
        bt_search= (Button) view.findViewById(R.id.bt_current_search);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchCarFragment searchCarFragment=new SearchCarFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.subview_container,searchCarFragment).addToBackStack(null).commit();
            }
        });
        tinyDB = new TinyDB(getContext());
        currentBookingData = new ArrayList<>();

        userId = tinyDB.getString("userid");
        token = tinyDB.getString("access_token");
        bookingAdapter=new BookingAdapter(getContext(), currentBookingData);
        recyclerView.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager mlayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mlayoutManager);
        recyclerView.setAdapter(bookingAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));


        setuptoolbar();
        return view;
    }

    private void setuptoolbar() {
        Toolbar toolbar= (Toolbar) view.findViewById(R.id.bottomToolBar);
        TextView textView= (TextView) toolbar.findViewById(R.id.txt_bot);
        ImageView imageView= (ImageView) toolbar.findViewById(R.id.img_bot);
        textView.setText("Add");
        imageView.setImageResource(R.drawable.ic_add);
    }

    @Override
    public void onResume() {
        super.onResume();
//        getBooking();
    }

/*
    private void getBooking(){
        if(currentBookingData !=null)
        {
            currentBookingData.clear();
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


}
