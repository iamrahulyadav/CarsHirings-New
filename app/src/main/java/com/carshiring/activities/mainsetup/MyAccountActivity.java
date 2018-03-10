package com.carshiring.activities.mainsetup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.activities.home.CarsResultListActivity;
import com.carshiring.models.UserDetails;
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

public class MyAccountActivity extends AppCompatActivity {
    EditText edtFname, edtLname, edtemail,edtPhone,edtZip, edtLicense,edtLicenseOrign,edtCity, edtAddress,etdob;
    Button btupdate;
    String fname,lname,email,phone,zip,license,licenseorigin,city,address,dob,pass,set ="",userid="";
    Gson gson = new Gson();
    TinyDB tinyDB;
    AppGlobal appGlobal=AppGlobal.getInstancess();
    UserDetails userDetails = new UserDetails();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        handleeditboxes();
        tinyDB = new TinyDB(getApplicationContext());
        if(tinyDB.contains("login_data"))
        {
            String data = tinyDB.getString("login_data");
           userDetails = gson.fromJson(data,UserDetails.class);
            edtFname.setText(userDetails.getUser_name());
            edtLname.setText(userDetails.getUser_lname().toString());
            edtemail.setText(userDetails.getUser_email());
            etdob.setText("");
            edtPhone.setText(userDetails.getUser_phone());
            edtZip.setText(userDetails.getUser_zipcode());
            edtLicense.setText(userDetails.getUser_license_no());
            edtLicenseOrign.setText("");
            edtCity.setText(userDetails.getUser_city().toString());
            edtAddress.setText(userDetails.getUser_address());
        }
    }

    private void handleeditboxes() {
        edtFname =  findViewById(R.id.etUserFirstName);
        edtLname =  findViewById(R.id.etUserLastName);
        edtemail =  findViewById(R.id.etUserEmail);
        etdob=  findViewById(R.id.etUserDob);
        edtPhone =  findViewById(R.id.etUserPhoneNo);
        edtZip =  findViewById(R.id.etUserzip);
        edtLicense =  findViewById(R.id.etlicense);
        edtLicenseOrign =  findViewById(R.id.etlicenseorigion);
        edtCity =  findViewById(R.id.etcity);
        edtAddress =  findViewById(R.id.etAddress);
        btupdate =  findViewById(R.id.bt_update);
        btupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitApi();
            }
        });
    }

    private void hitApi() {
        fname = edtFname.getText().toString().trim();
        lname = edtLname.getText().toString().trim();
        dob=etdob.getText().toString().trim();
        email = edtemail.getText().toString().trim();
        phone = edtPhone.getText().toString().trim();
        zip = edtZip.getText().toString().trim();
        license = edtLicense.getText().toString().trim();
        licenseorigin = edtLicenseOrign.getText().toString().trim();
        city = edtCity.getText().toString().trim();
        address = edtAddress.getText().toString().trim();
        if (!fname.isEmpty()){
            if (!lname.isEmpty()){
                if(!dob.isEmpty()) {
                    if (Utility.checkemail(email)) {
                        if (Utility.checkphone(phone)) {
                            if (!zip.isEmpty()) {
                                if (!license.isEmpty()) {
                                    if (!licenseorigin.isEmpty()) {
                                        if (!city.isEmpty()) {
                                            if (!address.isEmpty()) {
                                                updateProfile(userid, fname);
                                            } else {
                                                  Utility.message(getApplication(), "Please enter address");
                                            }
                                        } else {
                                            Utility.message(getApplication(), "Please enter city");
                                        }
                                    } else {
                                        Utility.message(getApplication(), "Please enter licenseorigin");
                                    }
                                } else {
                                    Utility.message(getApplication(), "Please enter license");
                                }
                            } else {
                                Utility.message(getApplication(), "Please enter zipcode");
                            }
                        } else {
                            Utility.message(getApplication(), "Please enter valid phone number");
                        }
                    } else {
                        Utility.message(getApplication(), "Please enter valid email");
                    }
                }
                else {
                    Utility.message(getApplication(),"Please enter Date of Birth");
                }
            } else {
                Utility.message(getApplication(),"Please enter last name");
            }
        } else {
            Utility.message(getApplication(),"Please enter First name");
        }

    }
    private void updateProfile(String userid, String fname) {
        if(!Utility.isNetworkConnected(getApplicationContext())){
            Toast.makeText(MyAccountActivity.this, "No Network Connection!", Toast.LENGTH_SHORT).show();
            return;
        }
        Utility.showloadingPopup(this);
        RetroFitApis retroFitApis= RetrofitApiBuilder.getCargHiresapis();
        Call<ApiResponse> responseCall=retroFitApis.updateprofile(userid,fname,lname,email,phone,zip,license,
                licenseorigin,dob,city,address);
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                if(response.body().status==true)
                {
                    UserDetails userDetails = new UserDetails();
                    userDetails = response.body().response.userdetail;
                    String logindata=gson.toJson(userDetails);
                    appGlobal.setLoginData(logindata);
                    String st=  appGlobal.getUser_id();
                    Utility.message(MyAccountActivity.this,"Updated Successfully");
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

}
