package com.carshiring.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carshiring.R;
import com.carshiring.activities.home.MyBookingActivity;
import com.carshiring.adapters.MyBookingAdapter;
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

    UserDetails userDetails = new UserDetails();

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
        String  login = tinyDB.getString("login_data");
        userDetails = gson.fromJson(login, UserDetails.class);
        userId =userDetails.getUser_id();

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
