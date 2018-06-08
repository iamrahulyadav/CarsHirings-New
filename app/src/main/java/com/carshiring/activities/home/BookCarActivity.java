package com.carshiring.activities.home;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.carshiring.R;
import com.carshiring.activities.mainsetup.SignUpActivity;
import com.carshiring.fragments.SearchCarFragment;
import com.carshiring.models.ExtraAdded;
import com.carshiring.models.SearchData;
import com.carshiring.models.UserDetails;
import com.carshiring.splash.SplashActivity;
import com.carshiring.utilities.AppBaseActivity;
import com.carshiring.utilities.AppGlobal;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carshiring.activities.home.CarDetailActivity.carImage;
import static com.carshiring.activities.home.CarDetailActivity.termsurl;
import static com.carshiring.activities.home.MainActivity.getKeyFromValue;

public class BookCarActivity extends AppBaseActivity implements View.OnClickListener{

    TextView terms,quotes,carname,carprice,txtAddExtra, txtSaveLater, txtFull,txtPoint, txtFullValue;
    ImageView carImg,imglogo;
    LinearLayout extraView,addExtra;
    //  List<CarSpecification> carSpecificationList;
    ProgressBar bar,bar1;
    TinyDB tinyDB;
    UserDetails userDetails = new UserDetails();
    public String price, name, number,currency;
    Gson gson =new Gson();
    public static List<ExtraAdded> extraData = new ArrayList<>();
    EditText edtflight;
    public static String flight_no,fullProtection ,protection_val;
    TextView tvFromDate,tvPickDate,txtDob,txtOneway,txtDriverSur,tvTodate,txtPlaceDrop;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_car);
        actionBar = getSupportActionBar() ;
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
        setupToolbar();
        tinyDB = new TinyDB(getApplicationContext());
        String logindata= tinyDB.getString("login_data");

        userDetails = gson.fromJson(logindata,UserDetails.class);
        if (userDetails!=null&&userDetails.getUser_name()!=null){
            name = userDetails.getUser_name();
        }

        tvFromDate= (TextView) findViewById(R.id.tvFromDT);
        tvPickDate= (TextView) findViewById(R.id.txtPlaceName);
        tvTodate= (TextView) findViewById(R.id.tvToDT);
        txtPlaceDrop = findViewById(R.id.txtPlaceName_drop);
        txtOneway = findViewById(R.id.oneway);

        tvFromDate.setText(SearchCarFragment.pick_date+"\n"+ SearchCarFragment.pickTime);
        tvPickDate.setText(SearchCarFragment.pickName);
        tvTodate.setText(SearchCarFragment.drop_date+"\n"+SearchCarFragment.dropTime);
        txtPlaceDrop.setText(SearchCarFragment.dropName);
        extraData = Extras.extraData;
        terms= (TextView)findViewById(R.id.txt_terms);
        txtPoint = findViewById(R.id.txtpoint_cal);
        edtflight = findViewById(R.id.edtFlight);
        quotes=(TextView) findViewById(R.id.txt_savequote);
        carname= (TextView)findViewById(R.id.txt_modelname);
        carprice= (TextView) findViewById(R.id.txt_carPrice);
        carImg= (ImageView) findViewById(R.id.img_car);
        imglogo= (ImageView) findViewById(R.id.img_carlogo);
        bar= (ProgressBar) findViewById(R.id.progressbar);
        bar1= (ProgressBar) findViewById(R.id.progressbar1);
        addExtra = findViewById(R.id.extraadded_view);
        txtAddExtra = findViewById(R.id.txt_additional_extra);
        txtFull = findViewById(R.id.txt_full_prote);
        txtFullValue = findViewById(R.id.txt_full_prote_value);
        txtSaveLater = findViewById(R.id.activity_booking_txtSaveLater);
        txtDriverSur = findViewById(R.id.driverSurCharge);

        if (tinyDB.contains("full_prot")){
            String full = String.valueOf(CarDetailActivity.fullAmtValue);
            txtFull.setVisibility(View.VISIBLE);
            txtFullValue.setVisibility(View.VISIBLE);
            fullProtection = "yes";
            protection_val = full;
            txtFullValue.setText(getResources().getString(R.string.full_protection_only) +" SAR "+ full + " "+getResources()
                    .getString(R.string.full_day));
        } else {
            fullProtection = "no";
        }

        if (CarDetailActivity.driverSur!=null){
            txtDriverSur.setText(CarDetailActivity.driverSur);
            txtDriverSur.setVisibility(View.VISIBLE);
        } else {
            txtDriverSur.setVisibility(View.GONE);
        }
        if (CarDetailActivity.oneway!=null){
            txtOneway.setText(CarDetailActivity.oneway);
            txtOneway.setVisibility(View.VISIBLE);
        } else {
            txtOneway.setVisibility(View.GONE);
        }


        if (carImage!=null){
            new AsyncCaller().execute();
        }
        txtPoint.setText(getResources().getString(R.string.colletcted_point) + String.valueOf(CarDetailActivity.point));
        terms.setOnClickListener(this);
        quotes.setOnClickListener(this);

        bar.setVisibility(View.VISIBLE);
        bar1.setVisibility(View.VISIBLE);
        Glide.with(getApplication()).load(CarDetailActivity.logo).listener(new RequestListener<Drawable>() {
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
        carprice.setText(CarDetailActivity.currency + "  " + CarDetailActivity.carPrice+ "/ "
                +CarsResultListActivity.day + " "+ CarsResultListActivity.time);

        if (extraData.size()>0){
            txtAddExtra.setVisibility(View.VISIBLE);
            for (int i=0;i<extraData.size();i++){
                price = extraData.get(i).getPrice();
                number = extraData.get(i).getQty();
                name = extraData.get(i).getName();
                currency = extraData.get(i).getCurrency();
                addLayout(name,price,number,currency,extraData.get(i).getId());
            }
        }
        txtSaveLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private class AsyncCaller extends AsyncTask<Integer, Void, Integer>
    {
        ProgressDialog pdLoading = new ProgressDialog(BookCarActivity.this);

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

                //  String sv="https://www.raceentry.com/img/Race-Registration-Image-Not-Found.png";
                Glide.with(getApplication()).load("").listener(new RequestListener<Drawable>() {
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
                Glide.with(getApplication()).load(CarDetailActivity.carImage).listener(new RequestListener<Drawable>() {
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        actionBar.setTitle(getResources().getString(R.string.car_book));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        tinyDB.remove("extra_added");
//        tinyDB.remove("full_prot");

        if (extraData!=null){
            extraData.clear();
        }

    }


    private void addLayout(String name, String price, String number, String currency, final String i) {
        LayoutInflater layoutInflater =
                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout=layoutInflater.inflate(R.layout.book_global_extra_view, null);
        TextView txtGlobal = layout.findViewById(R.id.txtGlobal);
        TextView txtPrice = layout.findViewById(R.id.txtPrice);
        TextView txtTotal = layout.findViewById(R.id.txtSubtotal);
        if (name.length()>0 && price.length()>0){
            txtGlobal.setText(name +" : "+ number);
            txtPrice.setText(getResources().getString(R.string.price) + currency + " " + price);
            double d = Double.parseDouble(price);
            double total = d*Integer.parseInt(number);
            txtTotal.setText(getResources().getString(R.string.sub_total) + currency + " " + String.valueOf(total));
        }

        ImageView buttonRemove = (ImageView) layout.findViewById(R.id.imgCross);
        buttonRemove.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ((LinearLayout)layout.getParent()).removeView(layout);
            }});
        addExtra.addView(layout);
    }

    String set = "", userid;
    private void setupToolbar() {
        Toolbar toolbar= (Toolbar) findViewById(R.id.bottomToolBar);
        TextView textView= (TextView) toolbar.findViewById(R.id.txt_bot);
        textView.setText(getResources().getString(R.string.book_this_car));
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flight_no = edtflight.getText().toString().trim();

                Intent it=new Intent(BookCarActivity.this, PayActivity.class);
                startActivity(it);

            }
        });
    }

    @Override
    public void onClick(View view) {
        Uri url=Uri.parse(termsurl);

        switch (view.getId()){
            case R.id.txt_terms:
                Intent intent=new Intent(Intent.ACTION_VIEW,url);
                startActivity(intent);
                //    startActivity(new Intent(getActivity(), TermsandCondition.class));
                break;
        }
    }

}
