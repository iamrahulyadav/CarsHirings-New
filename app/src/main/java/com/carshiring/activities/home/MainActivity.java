package com.carshiring.activities.home;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.activities.mainsetup.ChangePasswordActivity;
import com.carshiring.activities.mainsetup.LoginActivity;
import com.carshiring.activities.mainsetup.MyAccountActivity;
import com.carshiring.fragments.SearchCarFragment;
import com.carshiring.interfaces.ISubViewSetupHandler;
import com.carshiring.models.UserDetails;
import com.carshiring.utilities.AppGlobal;
import com.carshiring.utilities.Utility;
import com.google.gson.Gson;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created byRakhi on 9/2/2018.
 * Contact Number : +91 9958187463
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ISubViewSetupHandler {
    NavigationView navigationView;
    public Toolbar toolbar;
    public SearchQuery searchQuery = new SearchQuery();
    View v;
    TinyDB tinyDB;
    String qu,set,fname,lname,email,phone,zip,license,licenseorigin,city,address;
    TinyDB sherprf;
    DrawerLayout drawer;
    String userId,language_code;
    Dialog dialog;
    TextView txtemail, txtusername;
    UserDetails userDetails = new UserDetails();
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
     //   sherprf = new TinyDB(getApplicationContext());
     //   language_code = sherprf.getString("language_id");
//        updateResources(this,language_code);
        setContentView(R.layout.activity_home);
        appGlobal.context = getApplicationContext();
        tinyDB = new TinyDB(getApplicationContext());
        sherprf = new TinyDB(getApplicationContext());
        if(tinyDB.contains("login_data"))
        {
            String data = tinyDB.getString("login_data");
            userDetails = gson.fromJson(data,UserDetails.class);
            userId = userDetails.getUser_id();

        }

      //  userId = sherprf.getString("userid");
        v = MainActivity.this.findViewById(android.R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        dialog=new Dialog(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_home);

       // language_code = sherprf.getString("language_code") ;
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

        if (sherprf.contains("login_data")){
            String data = sherprf.getString("login_data");
            userDetails = gson.fromJson(data,UserDetails.class);
            userId = userDetails.getUser_id();
            if (userDetails.getUser_name()==null || userDetails.getUser_name().length()==0){
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
//        updateResources(this,language_code);
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

  /*  private boolean updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return true;
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        updateResources(this,language_code);
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
        setupSubView(id);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
//        updateResources(this,language_code);

        return true;
    }

    AppGlobal appGlobal = AppGlobal.getInstancess();


    @Override
    public void setupSubView(int id) {
//        updateResources(this,language_code);
        switch (id) {
            case R.id.action_accounts:
                if (checkLogin()) {
//                    startActivity(new Intent(MainActivity.this, MyAccountActivity.class));
                    /*MyAccountsFragment myaccountFragment = new MyAccountsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.subview_container, myaccountFragment)
                            .addToBackStack("null").commit();
                    toolbar.setTitle(getResources().getString(R.string.action_accounts));*/
                }
                break;

            case R.id.action_quotes:
/*
                if (checkLogin()) {
                    MyBookingsFragment fragment = new MyBookingsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Check", qu);
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.subview_container,
                            fragment).addToBackStack("mybooking").commit();
                    toolbar.setTitle(getResources().getString(R.string.action_quotes));
                }
*/
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

            /*case R.id.action_contact_us:
                startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
                Toast.makeText(MainActivity.this, "Contact US Action", Toast.LENGTH_SHORT).show();
                break;*/
            case R.id.action_language:

//                startActivity(new Intent(MainActivity.this, Language.class));
               /* tinyDB.remove("login_data");
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                Toast.makeText(MainActivity.this, "Language", Toast.LENGTH_SHORT).show();*/
                break;
          /*  case R.id.action_currency:
                startActivity(new Intent(MainActivity.this, CurrencyActivity.class));
                Toast.makeText(MainActivity.this, "Currency Change", Toast.LENGTH_SHORT).show();
                break;*/

           /* case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
                Toast.makeText(MainActivity.this, "Settings Action", Toast.LENGTH_SHORT).show();
                break;*/
        }
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
                                                        Utility.message(getApplication(),"Please enter address");
                                                    }
                                                } else {
                                                    Utility.message(getApplication(),"Please enter city");
                                                }
                                            } else {
                                                Utility.message(getApplication(),"Please enter licenseorigin");
                                            }
                                        } else {
                                            Utility.message(getApplication(),"Please enter license");
                                        }
                                    } else {
                                        Utility.message(getApplication(),"Please enter zipcode");
                                    }
                                } else {
                                    Utility.message(getApplication(),"Please enter valid phone number");
                                }
                            } else {
                                Utility.message(getApplication(),"Please enter valid email");
                            }
                        } else {
                            Utility.message(getApplication(),"Please enter last name");
                        }
                    } else {
                        Utility.message(getApplication(),"Please enter First name");
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
            Toast.makeText(MainActivity.this, "No Network Connection!", Toast.LENGTH_SHORT).show();
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
                    Utility.message(getApplicationContext(), response.body().msg);
//                    String logindata=gson.toJson(response.body().response.userdetail);
                    userDetails = response.body().response.userdetail;
                    String logindata=gson.toJson(userDetails);
                    appGlobal.setLoginData(logindata);
                    String st=  appGlobal.getUser_id();
                    dialog.dismiss();
                    dialog.dismiss();

                }
                else{
                    Utility.message(getApplicationContext(), response.body().msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.hidepopup();
                Utility.message(getApplicationContext(),"Connection Error");
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
        language_code = sherprf.getString("language_code") ;
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
        String username = sherprf.getString("user_name");
        String email = sherprf.getString("user_email");
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
            Snackbar.make(v, "Check Your Internet Connection", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checknetwork();
                }
            }).setActionTextColor(getResources().getColor(R.color.redStrong)).show();
        }
    }
}

