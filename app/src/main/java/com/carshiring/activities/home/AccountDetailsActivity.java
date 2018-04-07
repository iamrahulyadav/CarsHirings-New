package com.carshiring.activities.home;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.carshiring.R;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carshiring.activities.home.MainActivity.getKeyFromValue;

/**
 * Created by rakhi on 13-03-2018.
 */

public class AccountDetailsActivity extends AppBaseActivity {

    private TinyDB sharedpref;
    String userId,token,title,ages,Rtitle;
    Spinner spTitle;
    int age;
    TinyDB tinyDB;
    EditText etUserFirstName,etUserLastName, etUserEmail,etUserPhoneNo,etUserAge;

    private EditText edt_fname, edt_lname, edt_email, edt_phone, edt_zipcode, edt_licence_no,
            edt_city, edt_address;
    Spinner edt_licence_origin;
    private  TextView edt_dob;
    private ImageView iv;
    private String str_fname, str_lname, str_dob, str_email, str_phone, str_zipcode, str_licence_no, str_licence_origin ,
            str_city, str_address, str_image;

    UserDetails userDetails = new UserDetails();
//    UserImage userImage = new UserImage();
    Gson gson = new Gson();
    AppGlobal appGlobal = AppGlobal.getInstancess();
    private static final int SELECT_PICTURE = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        appGlobal.context = getApplicationContext();
        tinyDB = new TinyDB(getApplicationContext());
        String data= tinyDB.getString("login_data");
        userDetails = gson.fromJson(data, UserDetails.class);
        userId = userDetails.getUser_id();

        init();
    }

    private void init(){
        iv = (ImageView) findViewById(R.id.update_user_image);

        edt_fname = (EditText) findViewById(R.id.update_user_fname);
        edt_lname = (EditText) findViewById(R.id.update_user_lname);
        edt_dob = (TextView) findViewById(R.id.update_user_dob);
        edt_email = (EditText) findViewById(R.id.update_user_email);
        edt_phone = (EditText) findViewById(R.id.update_user_phone);
        edt_zipcode = (EditText) findViewById(R.id.update_user_zip);
        edt_licence_no = (EditText) findViewById(R.id.update_user_licence);
        edt_licence_origin =  findViewById(R.id.update_user_licnce_origin);
        edt_city = (EditText) findViewById(R.id.update_user_city);
        edt_address = (EditText) findViewById(R.id.update_user_address);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, SplashActivity.counrtyList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        edt_licence_origin.setAdapter(dataAdapter);

        edt_licence_origin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                str_licence_origin= (String) getKeyFromValue(SplashActivity.country,item);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        setMyToolBar();
        getProfile();
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

    public void getProfile(){
        RetroFitApis fitApis= RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> walList = fitApis.profile(userId);
        walList.enqueue(new Callback<ApiResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response!=null){
                    if(response.body().status)
                    {
                        UserDetails userDetails = new UserDetails();
                        userDetails = response.body().response.user_detail;
                        String logindata=gson.toJson(userDetails);
                        Log.d("TAG", "onResponse: "+logindata);
                        appGlobal.setLoginData(logindata);
                        String st =  appGlobal.getUser_id();
                        edt_email.setText(userDetails.getUser_email());
                        edt_phone.setText(userDetails.getUser_phone());
                        edt_licence_no.setText(userDetails.getUser_license_no());
                        edt_fname.setText(userDetails.getUser_name());
                        Log.d("TAG", "onResponse: "+userDetails.getUser_dob());
                        if (userDetails.getUser_dob()!=null&&!userDetails.getUser_dob().equalsIgnoreCase("0000-00-00")){
                            edt_dob.setText(Utility.convertSimpleDate(userDetails.getUser_dob()));
                        }
                        edt_zipcode.setText(userDetails.getUser_zipcode());
                       // edt_licence_origin.setText(userDetails.getUser_license_no());
                        edt_city.setText((String)userDetails.getUser_city());
                        edt_address.setText(userDetails.getUser_address());
                        if (userDetails.getUser_lname()!=null){
                            edt_lname.setText(userDetails.getUser_lname());
                        }
                        if (userDetails.getUser_image()!=null&&userDetails.getUser_image().length()>1){
                            String url = RetrofitApiBuilder.IMG_BASE_URL+userDetails.getUser_image();
                            GetImage task = new GetImage();
                            // Execute the task
                            task.execute(new String[] { url });
                        }

                    }
                    else{
                        Utility.message(getApplicationContext(), getResources().getString(R.string.something_wrong));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AccountDetailsActivity.this, ""+ getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

                //   Utility.message(getApplicationContext(), getResources().getString(R.string.check_internet));
                Log.d("TAG", "onFailure: "+t.getMessage());
            }
        });
    }

    String img;
    public class GetImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utility.showLoading(AccountDetailsActivity.this,getResources().getString(R.string.loading));
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            Utility.hidepopup();
            iv.setImageBitmap(result);
            img = Utility.BitMapToString(result);
//
//            Glide.with(getActivity()).load(result)
//                    .apply(RequestOptions.circleCropTransform()).into(imgUser);
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.
                        decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }

    public void cancel(View view){
        finish();
    }
    public void update_profile(View view){

        str_fname = edt_fname.getText().toString().trim();
        str_lname = edt_lname.getText().toString().trim();
        str_email = edt_email.getText().toString().trim();
        str_phone = edt_phone.getText().toString().trim();
        str_zipcode = edt_zipcode.getText().toString().trim();
        str_licence_no = edt_licence_no.getText().toString().trim();
      //  str_licence_origin = edt_licence_origin.getText().toString().trim();
        str_city = edt_city.getText().toString().trim();
        str_address = edt_address.getText().toString().trim();
        str_dob = edt_dob.getText().toString().trim();

        if (!str_fname.isEmpty()){
            if (!str_lname.isEmpty()){
                if (Utility.checkemail(str_email)){
                    if (Utility.checkphone(str_phone)){
                        if (!str_zipcode.isEmpty()){
                            if (!str_licence_no.isEmpty()){
                                if (!str_licence_origin.isEmpty()){
                                    if (!str_city.isEmpty()){
                                        if (!str_address.isEmpty()){
                                            if(!str_dob.isEmpty()) {
                                                update_profile_1(userId, str_fname);
                                            }else{
                                                Utility.message(getApplication(), getResources().getString(R.string.please_enter_dob));
                                            }
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
                                str_zipcode, str_licence_no, str_licence_origin, str_dob, str_city, str_address);
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
                    String logindata = gson.toJson(userDetails);
                    appGlobal.setLoginData(logindata);
                    finish();

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

    private int mYear, mMonth, mDay;
    public void dob_pick(View view){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        str_dob =year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        edt_dob.setText(Utility.convertSimpleDate(str_dob));

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    public void upload_my_image(View view){

        //imageInputHelper.selectImageFromGallery();
        openImageChooser();
    }

    /* Choose an image from Gallery */
    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    Log.d("TAG", selectedImageUri.toString());
                    // Get the path from the Uri
//                    String path = getPathFromURI(selectedImageUri);
//                    Log.i("aa", "Image Path : " + path);

//                    imageInputHelper.requestCropImage(selectedImageUri, 800, 450, 16, 9);
                    // Set the image in ImageView


                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    String encodedImage = encodeImage(selectedImage);
                    Log.d("Image123", encodedImage);



                    update_user_pic(encodedImage);



                    iv.setImageURI(selectedImageUri);
                }
            }
        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    private void update_user_pic(String image){

        if(!Utility.isNetworkConnected(getApplicationContext())){
            Toast.makeText(AccountDetailsActivity.this, getResources().getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Utility.showloadingPopup(this);
        RetroFitApis retroFitApis= RetrofitApiBuilder.getCargHiresapis();
        Call<ApiResponse> responseCall = retroFitApis.update_user_DP(userId, image);
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Utility.hidepopup();
                if(response.body().status==true)
                {
                    Log.d("TAG", "onResponse: " + response.body().message);


//                    userImage = response.body().response.user_dp;

//                    Toast.makeText(getApplicationContext(), userImage.getMsg(), Toast.LENGTH_SHORT).show();

//                    userDetails.setUser_image();
//                    appGlobal.setUser_image(response.body().image);
                    String image = response.body().image;
       /*             Glide.with(getApplicationContext())
                            .load(RetrofitApiBuilder.IMG_BASE_URL + image)
                            .into(iv);
*/
                    Toast.makeText(getApplicationContext(), response.body().msg, Toast.LENGTH_SHORT).show();
                    getProfile();

                }
                else{
                    Utility.message(getApplicationContext(), response.body().message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.hidepopup();
                Utility.message(getApplicationContext(),getResources().getString(R.string.no_internet_connection));
            }
        });

    }

    public static String BitMapToString(Bitmap bitmap){
        String temp="";
        if(bitmap!=null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
            byte[] b = baos.toByteArray();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
        }
        return temp;
    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageInputHelper.onActivityResult(requestCode, resultCode, data);
    }
*/

/*
    @Override
    public void onImageSelectedFromGallery(Uri uri, File imageFile) {
        imageInputHelper.requestCropImage(uri, 800, 450, 16, 9);
    }

    @Override
    public void onImageTakenFromCamera(Uri uri, File imageFile) {
        imageInputHelper.requestCropImage(uri, 800, 450, 16, 9);

    }

*/



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

    private Toolbar toolbar ;
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


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
