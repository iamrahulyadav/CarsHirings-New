package com.carshiring.activities.home;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.activities.mainsetup.LoginActivity;
import com.carshiring.fragments.AccountFragment;
import com.carshiring.fragments.SearchCarFragment;
import com.carshiring.interfaces.ISubViewSetupHandler;
import com.carshiring.models.UserDetails;
import com.carshiring.utilities.AppGlobal;
import com.carshiring.utilities.Utility;
import com.google.gson.Gson;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.mukesh.tinydb.TinyDB;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rakhi on 9/2/2018.
 * Contact Number : +91 9958187463
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ISubViewSetupHandler {
    //public static int bookingHistory;
    NavigationView navigationView;
    public Toolbar toolbar;
    public SearchQuery searchQuery = new SearchQuery();
    View v;
    String qu,set,fname,lname,email,phone,zip,license,licenseorigin,city,address;
    TinyDB tinyDB;
    DrawerLayout drawer;
    String userId,language_code;
    Dialog dialog;
    TextView txtemail, txtusername;
    UserDetails userDetails = new UserDetails();
    Gson gson = new Gson();
//    ////

//chnage by vaibhav
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        checkGPSStatus();

        tinyDB = new TinyDB(getApplicationContext());
        language_code = tinyDB.getString("language_code");
        updateRes(language_code);
        setContentView(R.layout.activity_home);
        appGlobal.context = getApplicationContext();
        if(tinyDB.contains("login_data"))
        {
            String data = tinyDB.getString("login_data");
            userDetails = gson.fromJson(data,UserDetails.class);
            userId = userDetails.getUser_id();

        }

      //  userId = tinyDB.getString("userid");
        v = MainActivity.this.findViewById(android.R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        dialog=new Dialog(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_home);

       // language_code = tinyDB.getString("language_code") ;
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        updateResources(this,language_code);
        Intent it = getIntent();
       if (it != null) {
            qu = it.getStringExtra("From Quotes");
           /* if (qu != null) {
                if (qu.equalsIgnoreCase("Quotes")) {
                    setupSubView(R.id.action_quotes);
                }
            } else {
                setupSubView(R.id.action_search_car);
            }*/
//for test
//           setupSubView(R.id.action_search_car);
       }

        SearchCarFragment searchCarFragment = new SearchCarFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.subview_container, searchCarFragment)
               .commit();

        if (tinyDB.contains("login_data")){
            String data = tinyDB.getString("login_data");
            userDetails = gson.fromJson(data,UserDetails.class);
            userId = userDetails.getUser_id();
            if (userDetails.getUser_lname()==null || userDetails.getUser_lname().length()==0){
                set = "update_profile";
                setupoverlay(set);
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        super.onPrepareOptionsMenu(menu);
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }
    public void checkGPSStatus()
    {
        LocationManager locationManager =(LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSProviderEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPSProviderEnable)
        {
            showSettingsAlert();
        }
    }

    private void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(getResources().getString(R.string.gps_sett));

        // Setting Dialog Message
        alertDialog.setMessage(getResources().getString(R.string.gps_not_enabled));

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton(getResources().getString(R.string.settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
//                checkGPSStatus();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.exit));
            builder.setMessage(getResources().getString(R.string.wantExit));
            builder.setNegativeButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setPositiveButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create();
            builder.show();
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        qu = "";
        int id = item.getItemId();
        setupSubView(id);
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        qu = "";
        int id = item.getItemId();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        setupSubView(id);
        switch (item.getItemId()){
            case R.id.action_accounts:
                if (checkLogin()) {
                    AccountFragment myaccountFragment = new AccountFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.subview_container, myaccountFragment)
                            .addToBackStack("null").commit();
                    toolbar.setTitle(getResources().getString(R.string.action_accounts));
                }
                break;

            case R.id.action_booking:
                if (checkLogin()) {
                    startActivity(new Intent(MainActivity.this,MyBookingActivity.class));
                }
                break;

            case R.id.action_search_car:
                Bundle bundle = new Bundle();
                bundle.putSerializable("query", searchQuery);
                SearchCarFragment searchCarFragment = new SearchCarFragment();
                searchCarFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.subview_container, searchCarFragment)
                        .addToBackStack("search").commit();
                toolbar.setTitle(getResources().getString(R.string.action_search_car));
                break;

            case R.id.action_about_us:
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                break;

            case R.id.action_contact_us:
                startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
                break;
         /*   case R.id.action_logout:
                tinyDB.remove("login_data");
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
         */   case R.id.action_language:
                startActivity(new Intent(MainActivity.this, Language.class));
//                Toast.makeText(MainActivity.this, "Currency Change", Toast.LENGTH_SHORT).show();
                break;

           /* case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
                Toast.makeText(MainActivity.this, "Settings Action", Toast.LENGTH_SHORT).show();
                break;*/

        }

        return true;
    }

    AppGlobal appGlobal = AppGlobal.getInstancess();


    private void updateRes(String lang){
        Resources res = getApplicationContext().getResources();
// Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(lang.toLowerCase())); // API 17+ only.
// Use conf.locale = new Locale(...) if targeting lower versions
        res.updateConfiguration(conf, dm);
    }
    @Override
    public void setupSubView(int id) {
//        updateResources(this,language_code);
//        switch (id) {

    }

    private boolean checkLogin() {
        if (userId != null && !userId.trim().isEmpty()) {
            return true;
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return false;
        }
    }
    Button btupdate,btnCancel;

    private void setupoverlay(String set) {

        final EditText edtFname, edtLname, edtemail,edtPhone,edtZip, edtLicense,edtLicenseOrign,edtCity, edtAddress;
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (set.equals("update_profile")){
            dialog.setContentView(R.layout.popup_updateprofile);
            edtFname = dialog.findViewById(R.id.etUserFirstName);
            edtLname = dialog.findViewById(R.id.etUserLastName);
            edtemail = dialog.findViewById(R.id.etUserEmail);
            edtemail.setText(userDetails.getUser_email());
            edtemail.setEnabled(false);
            edtPhone = dialog.findViewById(R.id.etUserPhoneNo);
            edtZip = dialog.findViewById(R.id.etUserzip);
            edtLicense = dialog.findViewById(R.id.etlicense);
            edtLicenseOrign = dialog.findViewById(R.id.etlicenseorigion);
            edtCity = dialog.findViewById(R.id.etcity);
            edtAddress = dialog.findViewById(R.id.etAddress);
            btupdate = dialog.findViewById(R.id.bt_update);
            btnCancel = dialog.findViewById(R.id.bt_cancel);
//            set onclick on update
            btupdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fname = edtFname.getText().toString().trim();
                    lname = edtLname.getText().toString().trim();
                    email = edtemail.getText().toString().trim();
                    phone = edtPhone.getText().toString().trim();
                    zip = edtZip.getText().toString().trim();
                    license = edtLicense.getText().toString().trim();
                    licenseorigin = edtLicenseOrign.getText().toString().trim();
                    city = edtCity.getText().toString().trim();
                    address = edtAddress.getText().toString().trim();
                    if (!fname.isEmpty()){
                        if (!lname.isEmpty()){
                            if (Utility.checkemail(email)){
                                if (Utility.checkphone(phone)){
                                    if (!zip.isEmpty()){
                                        if (!license.isEmpty()){
                                            if (!licenseorigin.isEmpty()){
                                                if (!city.isEmpty()){
                                                    if (!address.isEmpty()){
                                                        updateProfile(userId,fname);
                                                    } else {
                                                        Utility.message(getApplication(), getResources().getString(R.string.please_enter_address));
                                                    }
                                                } else {
                                                    Utility.message(getApplication(), getResources().getString(R.string.please_enter_city));
                                                }
                                            } else {
                                                Utility.message(getApplication(), getResources().getString(R.string.please_enter_license_origin));
                                            }
                                        } else {
                                            Utility.message(getApplication(), getResources().getString(R.string.please_enter_license));
                                        }
                                    } else {
                                        Utility.message(getApplication(), getResources().getString(R.string.please_enter_zipcode));
                                    }
                                } else {
                                    Utility.message(getApplication(), getResources().getString(R.string.please_enter_valid_phone_number));
                                }
                            } else {
                                Utility.message(getApplication(), getResources().getString(R.string.please_enter_valid_email));
                            }
                    } else {
                        Utility.message(getApplication(),getResources().getString(R.string.please_enter_last_name));
                    }
                } else {
                    Utility.message(getApplication(),getResources().getString(R.string.please_enter_first_name));
                }
            }
        });
        }


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }


    private void updateProfile(String userid, String fname) {
        if(!Utility.isNetworkConnected(getApplicationContext())){
            Toast.makeText(MainActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            return;
        }
        Utility.showloadingPopup(this);
        RetroFitApis retroFitApis= RetrofitApiBuilder.getCargHiresapis();
        Call<ApiResponse> responseCall=retroFitApis.updateprofile(userid,fname,lname,email,phone,zip,license,
                licenseorigin,"dob",city,address);
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                if(response.body().status==true)
                {
                    Log.d("TAG", "onResponse: "+response.body().msg);
                    Toast.makeText(MainActivity.this, ""+ response.body().msg, Toast.LENGTH_SHORT).show();
                    userDetails = response.body().response.user_detail;
                    String logindata=gson.toJson(userDetails);
                    appGlobal.setLoginData(logindata);
                    dialog.dismiss();

                }
                else{
                    Utility.message(getApplicationContext(), response.body().msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.hidepopup();
                Utility.message(getApplicationContext(),getResources().getString(R.string.no_internet_connection));
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
      /*  this.setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_home);
        language_code = tinyDB.getString("language_code") ;
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Intent it = getIntent();
        final Fragment fragment;
        if (getSupportFragmentManager().getBackStackEntryCount()>0){
        }
//        if(getSupportFragmentManager().getBackStackEntryCount()>1)
//        {
//            getSupportFragmentManager().popBackStackImmediate();
//
//
//            } else {
//                setupSubView(R.id.action_search_car);
//        }

        qu = it.getStringExtra("From Quotes");
        if (qu != null) {
            if (qu.equalsIgnoreCase("Quotes")) {
                setupSubView(R.id.action_quotes);
            }
        }
//            else if (getSupportFragmentManager().getBackStackEntryCount()>1){
//
//                int index = getSupportFragmentManager().getBackStackEntryCount() - 1;
//                FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(index);
//                String tag = backStackEntry.getName();
//
//                fragment = getSupportFragmentManager().findFragmentByTag(tag);
//                if (fragment!=null){
//                    getSupportFragmentManager().beginTransaction().replace(R.id.subview_container, fragment).commit();
//                }
//            }
        else {
            setupSubView(R.id.action_search_car);
        }

        View view = navigationView.getHeaderView(0);
        txtemail = (TextView) view.findViewById(R.id.tvEmail);
        txtusername = (TextView) view.findViewById(R.id.tvUserName);
        String username = tinyDB.getString("user_name");
        String email = tinyDB.getString("user_email");
        if (username != null && email != null) {
            if (!username.isEmpty() || !email.isEmpty()) {
                txtemail.setText(email);
                txtusername.setText(username);
            }
        }*/
        checknetwork();
    }

    public void checknetwork() {
        if (!Utility.isNetworkConnected(MainActivity.this)) {
            Snackbar.make(v, getResources().getString(R.string.check_internet), Snackbar.LENGTH_INDEFINITE).setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checknetwork();
                }
            }).setActionTextColor(getResources().getColor(R.color.redStrong)).show();
        }
    }
}

