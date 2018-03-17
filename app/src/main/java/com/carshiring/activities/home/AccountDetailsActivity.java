package com.carshiring.activities.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.models.UserDetails;
import com.carshiring.utilities.AppBaseActivity;
import com.carshiring.utilities.AppGlobal;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rakhi on 13-03-2018.
 */

public class AccountDetailsActivity extends AppBaseActivity {

    private TinyDB sharedpref;
    String userId,token,title,ages,Rtitle;
    Spinner spTitle;
    int age;
    EditText etUserFirstName,etUserLastName, etUserEmail,etUserPhoneNo,etUserAge;

    @Override
    protected void onResume() {
        super.onResume();

        setMyToolBar();
        getUserData();
    }

    private void setMyToolBar(){
        actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setTitle("Update Your Profile");
        }
    }

    private void getUserData(){
        sharedpref = new TinyDB(getApplicationContext());
        userId=sharedpref.getString("userid");
        token=sharedpref.getString("access_token");
        Rtitle= sharedpref.getString("usertitle");
    }

    private EditText edt_fname, edt_lname, edt_dob, edt_email, edt_phone, edt_zipcode, edt_licence_no, edt_licence_origin,
                        edt_city, edt_address;
    private String str_fname, str_lname, str_dob, str_email, str_phone, str_zipcode, str_licence_no, str_licence_origin ,
                        str_city,str_address;
    UserDetails userDetails = new UserDetails();
    Gson gson = new Gson();
    AppGlobal appGlobal = AppGlobal.getInstancess();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        appGlobal.context = getApplicationContext();

//        setUptoolbar();

        //spTitle= (Spinner) findViewById(R.id.sp_title);
        /*if(!Rtitle.isEmpty() || !Rtitle.equals(null))
        {
//            updatespinner();
        }
        else
        {
  //          setupspinner();
        }*/

    /*    etUserFirstName =  (EditText) findViewById(R.id.etUserFirstName);
        etUserLastName =  (EditText) findViewById(R.id.etUserLastName);
        etUserEmail =  (EditText) findViewById(R.id.etUserEmail);
        etUserPhoneNo =  (EditText) findViewById(R.id.etUserPhoneNo);
        etUserPhoneNo.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        etUserAge =  (EditText) findViewById(R.id.etUserAge);

        etUserFirstName.setText(sharedpref.getString("user_name"));
        etUserLastName.setText(sharedpref.getString("user_lname"));
        if(!userId.isEmpty() || !userId.equals(null))
        {
            if(!sharedpref.getString("user_email").equals(null) || !sharedpref.getString("user_email").isEmpty()) {
                etUserEmail.setEnabled(false);
                etUserEmail.setText(sharedpref.getString("user_email"));
            }
        }
        etUserPhoneNo.setText(sharedpref.getString("user_phone"));
        etUserAge.setText(Integer.toString(sharedpref.getInt("userage")));
    */

    init();
//    setUserData();
    }

    private void init(){
        edt_fname = (EditText) findViewById(R.id.update_user_fname);
        edt_fname = (EditText) findViewById(R.id.update_user_lname);
        edt_fname = (EditText) findViewById(R.id.update_user_dob);
        edt_fname = (EditText) findViewById(R.id.update_user_email);
        edt_fname = (EditText) findViewById(R.id.update_user_phone);
        edt_fname = (EditText) findViewById(R.id.update_user_zip);
        edt_fname = (EditText) findViewById(R.id.update_user_licence);
        edt_fname = (EditText) findViewById(R.id.update_user_licnce_origin);
        edt_fname = (EditText) findViewById(R.id.update_user_city);
        edt_fname = (EditText) findViewById(R.id.update_user_address);
    }

    public void update_profile(View view){
        str_fname = edt_fname.getText().toString().trim();
        str_lname = edt_lname.getText().toString().trim();
        str_email = edt_email.getText().toString().trim();
        str_phone = edt_phone.getText().toString().trim();
        str_zipcode = edt_zipcode.getText().toString().trim();
        str_licence_no = edt_licence_no.getText().toString().trim();
        str_licence_origin = edt_licence_origin.getText().toString().trim();
        str_city = edt_city.getText().toString().trim();
        str_address = edt_address.getText().toString().trim();
        if (!str_fname.isEmpty()){
            if (!str_lname.isEmpty()){
                if (Utility.checkemail(str_email)){
                    if (Utility.checkphone(str_phone)){
                        if (!str_zipcode.isEmpty()){
                            if (!str_licence_no.isEmpty()){
                                if (!str_licence_origin.isEmpty()){
                                    if (!str_city.isEmpty()){
                                        if (!str_address.isEmpty()){
                                            update_profile_1(userId,str_fname);
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


    private void update_profile_1(String userid, String str_fname) {
        if(!Utility.isNetworkConnected(getApplicationContext())){
            Toast.makeText(AccountDetailsActivity.this, getResources().getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Utility.showloadingPopup(this);
        RetroFitApis retroFitApis= RetrofitApiBuilder.getCargHiresapis();
        Call<ApiResponse> responseCall=retroFitApis.updateprofile(userid, str_fname, str_lname, str_email, str_phone,
                                str_zipcode, str_licence_no, str_licence_origin,"dob", str_city, str_address);
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                if(response.body().status==true)
                {
                    Log.d("TAG", "onResponse: "+response.body().msg);
                    Utility.message(getApplicationContext(), response.body().msg);
//                    String logindata=gson.toJson(response.body().response.userdetail);
                    userDetails = response.body().response.user_detail;
                    String logindata=gson.toJson(userDetails);
                    appGlobal.setLoginData(logindata);
                    String st=  appGlobal.getUser_id();
                    //dialog.dismiss();
                    //dialog.dismiss();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void setUserData(){

        edt_fname.setText(sharedpref.getString("user_name"));
        edt_lname.setText(sharedpref.getString("user_lname"));
        if(!userId.isEmpty() || !userId.equals(null))
        {
            if(!sharedpref.getString("user_email").equals(null) || !sharedpref.getString("user_email").isEmpty()) {
                etUserEmail.setEnabled(false);
                etUserEmail.setText(sharedpref.getString("user_email"));
            }
        }
        edt_phone.setText(sharedpref.getString("user_phone"));
//        etUserAge.setText(Integer.toString(sharedpref.getInt("userage")));
    }

    private void updatespinner() {
        ArrayAdapter<CharSequence> charSequenceArrayAdapter=ArrayAdapter.createFromResource(this,R.array.Titles,android.R.layout.simple_spinner_item);
        spTitle.setAdapter(charSequenceArrayAdapter);
        if(!Rtitle.equals(null))
        {
            int i=charSequenceArrayAdapter.getPosition(Rtitle);
            spTitle.setSelection(i);
        }
    }

    private void setupspinner() {
        spTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                title= (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

/*
    private void setUptoolbar() {

        toolbar= (Toolbar) findViewById(R.id.bottomToolBar);
        TextView textView= (TextView) toolbar.findViewById(R.id.txt_bot);
        textView.setText("Save & Exit");

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title= (String) spTitle.getSelectedItem();
                fname = etUserFirstName.getText().toString().trim();
                lname = etUserLastName.getText().toString().trim();
                phone = etUserPhoneNo.getText().toString().trim();
                ages = etUserAge.getText().toString().trim();
                age = (!ages.equals("") || !ages.isEmpty()) ? Integer.parseInt(ages) : 0;
                RetroFitApis fitApis= RetrofitApiBuilder.getRetrofitGlobal();
                if(age>10 && age<=99)
                {
                    if(isValidMobile(phone))
                    {
                        Call<ApiResponse> call = fitApis.update_profile(token, title, fname, lname, phone, age, userId);
                        call.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (response.body() != null) {
                                    if (response.body().status == true) {
                                        Utility.message(AccountDetailsActivity.this, response.body().msg);
                                        sharedpref.putString("usertitle",title);
                                        sharedpref.putString("user_name", fname);
                                        sharedpref.putString("user_lname", lname);
                                        sharedpref.putString("user_phone", phone);
                                        sharedpref.putInt("userage", age);
                                        finish();
                                    } else {
                                        Utility.message(AccountDetailsActivity.this, response.body().msg);
                                    }
                                } else {
                                    Utility.message(AccountDetailsActivity.this, "Getting Some Error");
                                }
                            }
                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                Utility.message(AccountDetailsActivity.this, "Connection Error");
                            }
                        });
                    }
                    else
                    {
                        Utility.message(AccountDetailsActivity.this, "Enter valid Phone Number");
                    }
                }
                else
                {
                    Utility.message(AccountDetailsActivity.this, "Enter Valid Age");
                }
            }
        });

    }
*/
    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

}
