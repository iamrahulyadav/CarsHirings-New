package com.carshiring.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.carshiring.R;

import com.carshiring.activities.home.CarDetailActivity;
import com.carshiring.activities.home.CarsResultListActivity;
import com.carshiring.activities.home.ExcessProtectionActivity;
import com.carshiring.activities.home.Extras;
import com.carshiring.adapters.CarResultsListAdapter;
import com.carshiring.models.BookingHistory;
import com.carshiring.models.CarDetailBean;
import com.carshiring.models.ExtraBean;
import com.carshiring.models.UserDetails;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

import static com.carshiring.activities.home.CarDetailActivity.carImage;
import static com.carshiring.activities.home.CarDetailActivity.carSpecificationList;
import static com.carshiring.activities.home.CarDetailActivity.day;
import static com.carshiring.activities.home.CarDetailActivity.point;
import static com.carshiring.activities.home.CarDetailActivity.refer_type;
import static com.carshiring.activities.home.CarDetailActivity.termsurl;
import static com.carshiring.activities.home.CarDetailActivity.time;
import static com.carshiring.activities.home.CarDetailActivity.type;
import static com.carshiring.splash.SplashActivity.TAG;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class CarDetailTab1Fragment extends Fragment implements View.OnClickListener {
    View view;
    LinearLayout ll_extra,ll_protection;
    TextView terms,quotes,carname,carprice, txtPoint;
    ImageView carImg,imglogo;
    //  List<CarSpecification> carSpecificationList;
    ProgressBar bar,bar1;
    LinearLayout ll,gl;
    UserDetails userDetails  = new UserDetails();
    Gson gson = new Gson();
    String userId,language, carnect_id,car_model,carnect_type,pick_city,pick_houre,pick_minute,
            pick_datetyme,drop_city,pick_date,drop_date,drop_houre,drop_minute,drop_datetyme,age,image,booking_price;
    TinyDB tinyDB ;
    TextView tvFromDate,txtOneway,tvPickDate,tvTodate,txtPlaceDrop;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.cardetail_tab1,container,false);
        ll_extra= (LinearLayout) view.findViewById(R.id.ll_extra);
        ll_protection=(LinearLayout) view.findViewById(R.id.ll_protection);
        terms= (TextView) view.findViewById(R.id.txt_terms);
        quotes=(TextView) view.findViewById(R.id.txt_savequote);
        carname= (TextView) view.findViewById(R.id.txt_modelname);
        carprice= (TextView) view.findViewById(R.id.txt_carPrice);
        carImg= (ImageView) view.findViewById(R.id.img_car);
        imglogo= (ImageView) view.findViewById(R.id.img_carlogo);
        bar= (ProgressBar) view.findViewById(R.id.progressbar);
        bar1= (ProgressBar) view.findViewById(R.id.progressbar1);
        txtPoint = view.findViewById(R.id.point_get);
        txtOneway = view.findViewById(R.id.oneway);
       /* SingleCarDetails singleCarDetails = new SingleCarDetails();
        singleCarDetails = CarDetail.singleCarDetails;
        carSpecificationList=CarDetail.spec;*/
        tinyDB = new TinyDB(getContext());

        String s= tinyDB.getString("login_data");
        userDetails = gson.fromJson(s, UserDetails.class);
        userId = userDetails.getUser_id();
        language = tinyDB.getString("language_code");
        carnect_id = CarDetailActivity.id_context;
        car_model = CarDetailActivity.modelname;

        tvFromDate= (TextView) view.findViewById(R.id.tvFromDT);
        tvPickDate= (TextView) view.findViewById(R.id.txtPlaceName);
        tvTodate= (TextView) view.findViewById(R.id.tvToDT);
        txtPlaceDrop = view.findViewById(R.id.txtPlaceName_drop);

        tvFromDate.setText(SearchCarFragment.pick_date+"\n"+ SearchCarFragment.pickTime);
        tvPickDate.setText(SearchCarFragment.pickName);
        tvTodate.setText(SearchCarFragment.drop_date+"\n"+SearchCarFragment.dropTime);
        txtPlaceDrop.setText(SearchCarFragment.dropName);

        if (CarDetailActivity.oneway!=null){
            txtOneway.setText(CarDetailActivity.oneway);
            txtOneway.setVisibility(View.VISIBLE);
        } else {
            txtOneway.setVisibility(View.GONE);
        }
        if ( CarDetailActivity.extralist!=null&&CarDetailActivity.extralist.size()>0){
            for (ExtraBean extraBean: CarDetailActivity.extralist){
                if (extraBean.getName()!=null&&!extraBean.getName().equalsIgnoreCase("null")){
                    ll_extra.setVisibility(View.VISIBLE);
                } else {
                    ll_extra.setVisibility(View.GONE);
                }
            }

        } else {
            ll_extra.setVisibility(View.GONE);
        }

        carnect_type = CarDetailActivity.type;
        pick_city = SearchCarFragment.pickName;
        pick_houre = SearchCarFragment.pick_hour;
        pick_minute = SearchCarFragment.pick_minute;
        pick_date = SearchCarFragment.pick_date;
        pick_datetyme=pick_date +" "+SearchCarFragment.pickTime;
        drop_city = SearchCarFragment.dropName;
        drop_date = SearchCarFragment.drop_date;
        drop_houre = SearchCarFragment.drop_hour;
        drop_minute = SearchCarFragment.drop_minute;
        drop_datetyme= drop_date +" "+SearchCarFragment.dropTime;
        booking_price = CarDetailActivity.currency + "  " + CarDetailActivity.carPrice;
        image = CarDetailActivity.carImage;
        age = userDetails.getUser_age();

        ll_extra.setOnClickListener(this);
        ll_protection.setOnClickListener(this);
        terms.setOnClickListener(this);
        quotes.setOnClickListener(this);
        ll= (LinearLayout) view.findViewById(R.id.ll_otherspec);
        gl=  view.findViewById(R.id.gr_Spec);
        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bar.setVisibility(View.VISIBLE);
        bar1.setVisibility(View.VISIBLE);
       /* Glide.with(getContext()).load(carImage).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                bar.setVisibility(View.GONE);
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                bar.setVisibility(View.GONE);
                return false;
            }
        }).into(carImg);*/
        if (carImage!=null){
            new AsyncCaller().execute();

        }

        Glide.with(getContext()).load(CarDetailActivity.logo).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                bar1.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                bar1.setVisibility(View.GONE);
                return false;
            }
        }).into(imglogo);
        carname.setText(CarDetailActivity.modelname + getResources().getString(R.string.or_similar));
        txtPoint.setText(getResources().getString(R.string.points_collected )+String.valueOf( CarDetailActivity.point));
        carprice.setText(CarDetailActivity.currency + "  " + CarDetailActivity.carPrice+ "/ "
                + time + " "+ CarDetailActivity.day);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if(carSpecificationList!=null) {
            for(int i=0;i<carSpecificationList.size();i++) {
                CarDetailBean.FeatureBean carSpecification =carSpecificationList.get(i);
                if (carSpecification.aircondition != null) {
                    if(!carSpecification.passenger.isEmpty())
                    {
                        TextView tt1 = new TextView(getContext());
                        tt1.setLayoutParams(lparams);
                        tt1.setText(carSpecification.getPassenger() + " " + getResources().getString(R.string.passanger));
                        tt1.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_car_seat,0,0);
                        tt1.setCompoundDrawablePadding(5);
                        tt1.setTextSize(10);
                        tt1.setPadding(0,0,8,0);
                        tt1.setTypeface(Typeface.DEFAULT);
                        gl.addView(tt1);
                        gl.setOrientation(LinearLayout.HORIZONTAL);
                    }

                    if(!carSpecification.door.isEmpty())
                    {

                        TextView tt1 = new TextView(getContext());
                        tt1.setLayoutParams(lparams);
                        tt1.setText(carSpecification.getDoor()+" "+ getResources().getString(R.string.doors));
                        tt1.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_car_door,0,0);
                        tt1.setCompoundDrawablePadding(5);
                        tt1.setTextSize(10);
                        tt1.setPadding(5,0,8,0);
                        tt1.setTypeface(Typeface.DEFAULT);
                        gl.addView(tt1);
                        gl.setOrientation(LinearLayout.HORIZONTAL);
                    }
                    if(!carSpecification.transmission.isEmpty())
                    {

                        TextView tt1 = new TextView(getContext());
                        tt1.setLayoutParams(lparams);
                        tt1.setText(carSpecification.getTransmission());
                        tt1.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.manual,0,0);
                        tt1.setCompoundDrawablePadding(5);
                        tt1.setTextSize(10);
                        tt1.setPadding(5,0,8,0);
                        tt1.setTypeface(Typeface.DEFAULT);
                        gl.addView(tt1);
                        gl.setOrientation(LinearLayout.HORIZONTAL);
                    }


                    if (!carSpecification.aircondition.equalsIgnoreCase("false")) {
                        View viw = getActivity().getLayoutInflater().inflate(R.layout.gridcustomstyle, null);
                        // TextView tt = (TextView) viw.findViewById(R.id.txt_spec);
                        TextView tt1 = new TextView(getContext());
                        tt1.setLayoutParams(lparams);
                        tt1.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_ac,0,0);
                        tt1.setCompoundDrawablePadding(5);
                        tt1.setTextSize(10);
                        tt1.setTypeface(Typeface.DEFAULT);
                        tt1.setText(getResources().getString(R.string.air_condition));
                        gl.addView(tt1);
                        tt1.setPadding(5,0,8,0);
                        gl.setOrientation(LinearLayout.HORIZONTAL);
                    }

                    if(!carSpecification.fueltype.isEmpty())
                    {

                        TextView tt1 = new TextView(getContext());
                        tt1.setLayoutParams(lparams);
//                        tt1.setText(carSpecification.getFueltype());
                        tt1.setText("Full to Full");
                        tt1.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_fuel,0,0);
                        tt1.setCompoundDrawablePadding(5);
                        tt1.setTextSize(10);
                        tt1.setPadding(5,0,8,0);
                        tt1.setTypeface(Typeface.DEFAULT);
                        gl.addView(tt1);
                        gl.setOrientation(LinearLayout.HORIZONTAL);
                    }
                    if(!carSpecification.bag.isEmpty())
                    {
                        TextView tt1 = new TextView(getContext());
                        tt1.setLayoutParams(lparams);
                        tt1.setText(carSpecification.getBag() + " " + getResources().getString(R.string.bags));
                        tt1.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ic_bag,0,0);
                        tt1.setCompoundDrawablePadding(5);
                        tt1.setPadding(5,0,8,0);
                        tt1.setTextSize(10);
                        tt1.setTypeface(Typeface.DEFAULT);
                        gl.addView(tt1);
                        gl.setOrientation(LinearLayout.HORIZONTAL);
                    }
                }
            }
        }
    }

    private class AsyncCaller extends AsyncTask<Integer, Void, Integer> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.show();
        }
        @Override
        protected Integer doInBackground(Integer... params) {
            URL url = null;
            int s = 0;
            try {
                url = new URL(carImage);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            HttpURLConnection httpConn = null;
            try {
                httpConn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            try {
                httpConn.setInstanceFollowRedirects(false);
                httpConn.setRequestMethod("HEAD");
                httpConn.connect();

                s = httpConn.getResponseCode();
                Log.d("TAG", "doInBackground: "+httpConn.getResponseCode());
                System.out.println("Response Code : " + httpConn.getResponseCode());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return s;

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            Log.d("TAG", "onPostExecute: "+result);

            //this method will be running on UI thread

            pdLoading.dismiss();
            if (result==404){
                Glide.with(getActivity()).load("").listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        bar.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        bar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(carImg);

            } else {
                Glide.with(getActivity()).load(carImage).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        bar.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        bar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(carImg);

            }
        }

    }


    @Override
    public void onClick(View v) {
        Uri url=Uri.parse(termsurl);

        Intent it=new Intent(getActivity(), ExcessProtectionActivity.class);
        switch (v.getId())
        {
            case R.id.ll_extra:
                startActivity(new Intent(getActivity(),Extras.class));
                break;
            case R.id.ll_protection:
                it.putExtra("get","ForProtec");
                startActivity(it);
                break;
            case R.id.txt_terms:
                Intent intent=new Intent(Intent.ACTION_VIEW,url);
                startActivity(intent);
                //    startActivity(new Intent(getActivity(), TermsandCondition.class));
                break;
            case R.id.txt_savequote:
              /*  it.putExtra("get","Forquotes");
                startActivity(it);*/
                savelater();
                break;
        }
    }


    private void savelater() {

        Utility.showloadingPopup(getActivity());
        RetroFitApis fitApis= RetrofitApiBuilder.getCargHiresapis();

        final Call<ApiResponse> bookingDataCall = fitApis.savelater(language,carnect_id,car_model,carnect_type,
                userId, pick_city, pick_date, pick_houre, pick_minute, pick_datetyme,drop_city,
                drop_date,drop_houre, drop_minute, drop_datetyme, age, image, booking_price,time,refer_type,type,point);

        bookingDataCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();

                if (response.body()!=null){
                    Log.d(TAG, "onResponse: savelater"+gson.toJson(response.body()));
                    Toast.makeText(getContext(), ""+response.body().msg, Toast.LENGTH_SHORT).show();
                } else {
                    Utility.message(getContext(), response.body().msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("TAG", "onFailure: "+t.getMessage());
                Utility.hidepopup();
            }
        });
    }

}
