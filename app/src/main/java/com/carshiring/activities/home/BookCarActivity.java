package com.carshiring.activities.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.carshiring.adapters.CarResultsListAdapter;
import com.carshiring.fragments.SearchCarFragment;
import com.carshiring.models.BookingRequest;
import com.carshiring.models.ExtraAdded;
import com.carshiring.models.UserDetails;
import com.carshiring.utilities.AppBaseActivity;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.carshiring.activities.home.CarDetailActivity.fullprotectionammount;
import static com.carshiring.activities.home.CarDetailActivity.fullprotectioncurrency;
import static com.carshiring.activities.home.CarDetailActivity.termsurl;

public class BookCarActivity extends AppBaseActivity implements View.OnClickListener{

    TextView terms,quotes,carname,carprice,txtAddExtra, txtFull,txtPoint, txtFullValue;
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
        name = userDetails.getUser_name();

        if (tinyDB.contains("extra_added")){
            String extra = tinyDB.getString("extra_added");
            extraData= Arrays.asList(gson.fromJson(extra,ExtraAdded[].class));
            Log.d("TAG", "onCreate: "+extraData.size());
        }

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
        if (tinyDB.contains("full_prot")){
            String full = tinyDB.getString("full_prot");
            txtFull.setVisibility(View.VISIBLE);
            txtFullValue.setVisibility(View.VISIBLE);
            fullProtection = "yes";
            protection_val = full;
            txtFullValue.setText(getResources().getString(R.string.full_protection_only) + full + getResources()
                    .getString(R.string.full_day));
        } else {
            fullProtection = "no";
        }

        txtPoint.setText(getResources().getString(R.string.colletcted_point) + CarResultsListAdapter.calPoint);
        terms.setOnClickListener(this);
        quotes.setOnClickListener(this);

        bar.setVisibility(View.VISIBLE);
        bar1.setVisibility(View.VISIBLE);
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
        carprice.setText(CarDetailActivity.currency + "  " + CarDetailActivity.carPrice);

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
        tinyDB.remove("extra_added");
        tinyDB.remove("full_prot");

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            /*Intent intent = new Intent(getApplicationContext(), TeacherSide.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
            // you don't need to call finish(); because
            // return super.onKeyDown(keyCode, event); does that for you

            // clear your SharedPreferences
            tinyDB.remove("extra_added");
            tinyDB.remove("full_prot");

        }
        return super.onKeyDown(keyCode, event);
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
