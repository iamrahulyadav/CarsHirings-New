package com.carshiring.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.carshiring.splash.SplashActivity.TAG;

/**
 * Created by rakhi on 13-03-2018.
 */

public class CurrentBookingFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public List<BookingHistory> currentBookingData;
    private TinyDB tinyDB;
    private String token, userId, language, bookingid, accountType="";// ab=wallet, 2 account
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private Button btn_search;
    UserDetails userDetails = new UserDetails();
    SwipeRefreshLayout swipeRefreshLayout;
    private List<BookingHistory> bookingData;
    MyBookingAdapter bookingAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        list = getArguments().getParcelableArrayList("bookingHistoryList");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_booking_current,container,false);
         dialog=new Dialog(getContext());
        tinyDB = new TinyDB(getContext());
        currentBookingData = new ArrayList<>();
        String  login = tinyDB.getString("login_data");
        userDetails = gson.fromJson(login, UserDetails.class);
        userId =userDetails.getUser_id();
        bookingData = new ArrayList<>();
        token = tinyDB.getString("access_token");
        language = tinyDB.getString("language_code");
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.previous_booking_refresh_layout);
        linearLayout = (LinearLayout) view.findViewById(R.id.rec_prev_booki_no_data);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.redStrong));
        btn_search= (Button) view.findViewById(R.id.bt_previousbooking);
        swipeRefreshLayout.setOnRefreshListener(this);
        btn_search.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onResume() {
        super.onResume();
        setMyAdapter(bookingData);
        getBook();

    }

    private Gson gson = new Gson();

    private void setMyAdapter(final List<BookingHistory> bookingHistory){
        bookingAdapter = new MyBookingAdapter(bookingHistory, getContext(),"c");

        if (bookingHistory.size()>0){
            recyclerView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            recyclerView.setAdapter(bookingAdapter);

            bookingAdapter.submit(new MyBookingAdapter.ClickItem() {
                @Override
                public void click(View view, int Position) {
                    bookingid=bookingHistory.get(Position).getBooking_id();
                    setCancelationPop(bookingid);
                }
            });
        } else {
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }

    }


    Dialog dialog;

    private void setCancelationPop(final String bookingid){
        dialog.setContentView(R.layout.account_popup_view);
        final RadioButton walletRadioButton= dialog.findViewById(R.id.radio_wallet);
        final RadioButton accountRadioButton= dialog.findViewById(R.id.radio_account);
        final TextView txtSubmit= dialog.findViewById(R.id.account_btnsubit);
        TextView txtCancel = dialog.findViewById(R.id.account_btnCancel);
        dialog.show();
        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelBooking(accountType, bookingid);
                dialog.dismiss();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        accountRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((RadioButton) view).isChecked();

                if (checked){
                    accountType = "2";
                }
            }
        });
        walletRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((RadioButton) view).isChecked();

                if (checked){
                    accountType="ab";
                }
            }
        });
    }

    private void getBook() {
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
                            if (date2.compareTo(date1) >0 && bookingData1.getBooking_status().equals("1")) {

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

                        Log.d("TAG", "onResponse:book current"+gson.toJson(bookingData));
                        if (bookingData.size()>0){
                            linearLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
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
    public void onClick(View v) {

    }

    private void cancelBooking(String type,String bookingid){
        Utility.showloadingPopup(getActivity());
        RetroFitApis fitApis= RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> bookingCancel = fitApis.cancelBooking(language,type,bookingid);

        bookingCancel.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                dialog.dismiss();
                if (response!=null){
                    if (response.body().status){

                        Toast.makeText(getContext(), ""+response.body().msg, Toast.LENGTH_SHORT).show();
                        getBook();
                    } else {
                        Toast.makeText(getContext(), ""+getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.hidepopup();
                dialog.dismiss();
                Toast.makeText(getContext(), ""+getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });

    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        getBook();
        setMyAdapter(bookingData);

        swipeRefreshLayout.setRefreshing(false);
    }
}
