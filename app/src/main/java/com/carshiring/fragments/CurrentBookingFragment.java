package com.carshiring.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.carshiring.activities.home.MainActivity;
import com.carshiring.activities.home.MyBookingActivity;
import com.carshiring.adapters.BookingAdapter;
import com.carshiring.adapters.MyBookingAdapter;
import com.carshiring.models.BookingData;
import com.carshiring.models.BookingHistory;
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

/**
 * Created by rakhi on 13-03-2018.
 */

public class CurrentBookingFragment extends Fragment implements View.OnClickListener {

    public List<BookingHistory> currentBookingData;
    private TinyDB tinyDB;
    private String token, userId;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private Button btn_search;
    private List<BookingHistory> list = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        list = getArguments().getParcelableArrayList("bookingHistoryList");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_my_booking_current,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tinyDB = new TinyDB(getContext());
        currentBookingData = new ArrayList<>();

        userId = tinyDB.getString("userid");
        token = tinyDB.getString("access_token");

//        bookingAdapter=new BookingAdapter(getContext(), currentBookingData);
  //      recyclerView.setVisibility(View.VISIBLE);
    //    recyclerView.setAdapter(bookingAdapter);
      init();
//      getBooking();
    }

    private void init(){
        linearLayout = (LinearLayout) getView().findViewById(R.id.fragment_my_booking_current_no_data_view);

        btn_search= (Button) getView().findViewById(R.id.bt_current_search);
        btn_search.setOnClickListener(this);

        recyclerView= (RecyclerView) getView().findViewById(R.id.fragment_my_booking_current_recycler);
        RecyclerView.LayoutManager mlayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mlayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        setMyAdapter(MyBookingActivity.bookingHistoryList1);
    }

    @Override
    public void onResume() {
        super.onResume();
//        getBooking();
    }

    private Gson gson = new Gson();
    private List<BookingHistory> bookingHistory = new ArrayList<>();

    private void getBooking(){
        if(currentBookingData !=null)
        {
            currentBookingData.clear();
        }

   //     Toast.makeText(getContext(), userId+"", Toast.LENGTH_SHORT).show();

        Utility.showloadingPopup(getActivity());
        RetroFitApis fitApis= RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> bookingDataCall = fitApis.booking_history("19");

        bookingDataCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                if (response.body()!=null){
                    if (response.body().status==true){

                        bookingHistory= response.body().response.booking;
                        setMyAdapter(bookingHistory);
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

    private void setMyAdapter(List<BookingHistory> bookingHistory){
        MyBookingAdapter adapter = new MyBookingAdapter(getContext(), bookingHistory);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.bt_current_search){
            SearchCarFragment searchCarFragment=new SearchCarFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.subview_container,searchCarFragment).addToBackStack(null).commit();
        }else{

        }
    }

    private void setuptoolbar() {
/*        Toolbar toolbar= (Toolbar) view.findViewById(R.id.bottomToolBar);
        TextView textView= (TextView) toolbar.findViewById(R.id.txt_bot);
        ImageView imageView= (ImageView) toolbar.findViewById(R.id.img_bot);
        textView.setText("Add");
        imageView.setImageResource(R.drawable.ic_add);*/
    }

}
