package com.carshiring.activities.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
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
import java.io.FileOutputStream;
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
    private ImageView iv, imgWallEdit, imgUserWall;
    private String str_fname, str_lname, str_dob, str_email, str_phone, str_zipcode, str_licence_no="", str_licence_origin ,
            str_city, str_address, str_image;

    UserDetails userDetails = new UserDetails();
    //    UserImage userImage = new UserImage();
    Gson gson = new Gson();
    AppGlobal appGlobal = AppGlobal.getInstancess();
    private int SELECT_PROFILE_PICTURE = 100;
    private static final int SELECT_WALL_PICTURE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);



        init();
    }

    private void init(){

        appGlobal.context = getApplicationContext();
        tinyDB = new TinyDB(getApplicationContext());
        String data= tinyDB.getString("login_data");
        userDetails = gson.fromJson(data, UserDetails.class);
        userId = userDetails.getUser_id();

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
        imgWallEdit = findViewById(R.id.imgWallEdit);
        imgUserWall = findViewById(R.id.user_wall);

        imgWallEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AccountDetailsActivity.this,
                            new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CAMERA);
                } else {
                    Log.e("DB", "PERMISSION GRANTED");
                    result = true;
                }
                SELECT_PROFILE_PICTURE =200;
                selectImage(SELECT_PROFILE_PICTURE);
//                openImageChooser(SELECT_WALL_PICTURE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        setMyToolBar();
        getProfile();
    }

    String s;
    private void countrySpinner() {
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_row, SplashActivity.counrtyList);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_row);
        edt_licence_origin.setAdapter(arrayAdapter);
        if (str_licence_origin != null) {
            s=SplashActivity.country.get(str_licence_origin);
            int spinnerPosition = arrayAdapter.getPosition(s);
            edt_licence_origin.setSelection(spinnerPosition);
        }

        edt_licence_origin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                str_licence_origin= (String) getKeyFromValue(SplashActivity.country,item);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        Utility.showLoading(AccountDetailsActivity.this,getResources().getString(R.string.loading));
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
                        Log.d("TAG", "onResponse: "+response.body().response.user_detail);
                        appGlobal.setLoginData(logindata);
                        String st =  appGlobal.getUser_id();
                        edt_email.setText(userDetails.getUser_email());
                        edt_phone.setText(userDetails.getUser_phone());
                        edt_licence_no.setText(userDetails.getUser_license_no());
                        edt_fname.setText(userDetails.getUser_name());
                        if (userDetails.getUser_dob()!=null&&!userDetails.getUser_dob().equalsIgnoreCase("0000-00-00")){
                            edt_dob.setText(Utility.convertSimpleDate(userDetails.getUser_dob()));
                        }
                        str_licence_origin = (String)userDetails.getUser_country();
                        edt_zipcode.setText(userDetails.getUser_zipcode());
                        // edt_licence_origin.setText(userDetails.getUser_license_no());
                        edt_city.setText((String)userDetails.getUser_city());
                        edt_address.setText(userDetails.getUser_address());
                        if (userDetails.getUser_lname()!=null){
                            edt_lname.setText(userDetails.getUser_lname());
                        }
                        if (userDetails.getUser_image()!=null&&userDetails.getUser_image().length()>1){
                            String url = RetrofitApiBuilder.IMG_BASE_URL+userDetails.getUser_image();
                            Log.d("TAG", "onResponse: imagewall"+url);
                            GetImage task = new GetImage();
                            // Execute the task
                            task.execute(new String[] { url });
                        }

                        if (userDetails.getUser_cover()!=null&&userDetails.getUser_cover().length()>0){
                            String url = RetrofitApiBuilder.IMG_BASE_URL+userDetails.getUser_cover();
                            GetImageWall task = new GetImageWall();
                            // Execute the task
                            task.execute(new String[] { url });
                        }
                        Utility.hidepopup();
                        countrySpinner();
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

    public class GetImageWall extends AsyncTask<String, Void, Bitmap> {
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
            imgUserWall.setImageBitmap(result);
            encodedImage = Utility.BitMapToString(result);
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
        if (str_licence_no.isEmpty()){
            str_licence_no = " ";
        }

        if (!str_fname.isEmpty()){
            if (!str_lname.isEmpty()){
                if (Utility.checkemail(str_email)){
                    if (Utility.checkphone(str_phone)){
                        if (!str_zipcode.isEmpty()){
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
                Log.d("TAG", "onResponse: "+response.body().msg);
                assert response.body() != null;
                if(response.body().status)
                {
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
    long timeInMilliseconds;
    private int mYear, mMonth, mDay;
    public void dob_pick(View view){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = Calendar.getInstance().get(Calendar.YEAR)-18;
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        timeInMilliseconds = Utility.getTimeDate(mYear+"-"+mMonth+"-"+mDay);
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        str_dob =year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        edt_dob.setText(Utility.convertSimpleDate(str_dob));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(timeInMilliseconds);
        datePickerDialog.show();

    }

    public void upload_my_image(View view){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AccountDetailsActivity.this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CAMERA);
        } else {
            Log.e("DB", "PERMISSION GRANTED");
            result = true;
        }
        SELECT_PROFILE_PICTURE = 100;
        selectImage(SELECT_PROFILE_PICTURE);
    }

    /* Choose an image from Gallery */
    void openImageChooser(int SELECT_PICTURE) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

  /*  public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PROFILE_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    Log.d("TAG", selectedImageUri.toString());
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
            } else if (requestCode == SELECT_WALL_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    Log.d("TAG", selectedImageUri.toString());
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    encodedImage = encodeImage(selectedImage);
                    Log.d("Image123", encodedImage);
                    imgUserWall.setImageURI(selectedImageUri);
                    update_user_wall(encodedImage);

                }
            }
        }
    }
*/
    String encodedImage;
    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }


    private void update_user_wall(String image){

        if(!Utility.isNetworkConnected(getApplicationContext())){
            Toast.makeText(AccountDetailsActivity.this, getResources().getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Utility.showLoading(AccountDetailsActivity.this,getResources().getString(R.string.loading));
        RetroFitApis retroFitApis= RetrofitApiBuilder.getCargHiresapis();
        Call<ApiResponse> responseCall = retroFitApis.update_user_wall(userId, image);
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d("TAG", "onResponse: data" + gson.toJson(response.body()));

                if(response.body().status==true)
                {
                    Utility.hidepopup();
                    Toast.makeText(getApplicationContext(), response.body().msg, Toast.LENGTH_SHORT).show();
                    getProfile();
                }
                else{
                    Utility.message(getApplicationContext(), response.body().msg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.hidepopup();
                Log.d("TAG", "onFailure: "+t.getMessage());
                Utility.message(getApplicationContext(),getResources().getString(R.string.no_internet_connection));
            }
        });

    }


    private void update_user_pic(String image){

        if(!Utility.isNetworkConnected(getApplicationContext())){
            Toast.makeText(AccountDetailsActivity.this, getResources().getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Utility.showLoading(AccountDetailsActivity.this,getResources().getString(R.string.loading));
        RetroFitApis retroFitApis= RetrofitApiBuilder.getCargHiresapis();
        Call<ApiResponse> responseCall = retroFitApis.update_user_DP(userId, image);
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body().status==true)
                {
                    Log.d("TAG", "onResponse: " + response.body().message);
                    Utility.hidepopup();
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



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    Bitmap bitmap;
    private String bitmapString="a",userChoosenTask;
    private boolean result;


    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(AccountDetailsActivity.this.getApplicationContext().getContentResolver(), data.getData());
                Uri tempUri = getImageUri(getApplicationContext(), bm);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));

                System.out.println(finalFile + "");

                String filePath = finalFile + "";
                Log.d("MyImagePath", filePath.substring(filePath.lastIndexOf("/") + 1));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        bitmap = bm;
        bitmapString = Utility.BitMapToString(bm);
       if (SELECT_PROFILE_PICTURE==100){
           iv.setImageBitmap(bm);
           update_user_pic(bitmapString);
       } else if (SELECT_PROFILE_PICTURE==200){

           imgUserWall.setImageBitmap(bm);
           update_user_wall(bitmapString);
       }

    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap = thumbnail;

        Uri tempUri = getImageUri(getApplicationContext(), bitmap);

        // CALL THIS METHOD TO GET THE ACTUAL PATH
        File finalFile = new File(getRealPathFromURI(tempUri));

        System.out.println(finalFile + "");

        String filePath = finalFile + "";
        Log.d("MyImagePath", filePath.substring(filePath.lastIndexOf("/") + 1));
/*
        bitmapString = Utility.BitMapToString(bitmap);
        iv.setImageBitmap(bitmap) ;
        update_user_pic(bitmapString);*/
        bitmapString = Utility.BitMapToString(bitmap);
        if (SELECT_PROFILE_PICTURE==100){
            iv.setImageBitmap(bitmap);
            update_user_pic(bitmapString);
        } else if (SELECT_PROFILE_PICTURE==200){
            imgUserWall.setImageBitmap(bitmap);
            update_user_wall(bitmapString);
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    private final int  REQUEST_CAMERA=0, SELECT_FILE = 1;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), "Camera Permission not granted", Toast.LENGTH_SHORT).show();
                } else {
                    result =true;
                    if(result){
//                        cameraIntent();
                    }
                }
                break;
            case SELECT_FILE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), "file Permission not granted", Toast.LENGTH_SHORT).show();
                } else {
                    result =true;
                    if(result){
//                        galleryIntent();
                    }
                }
                break;
        }
    }

    private void selectImage(int SELECT_PROFILE_PICTURE) {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AccountDetailsActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                        ActivityCompat.requestPermissions(AccountDetailsActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                AccountDetailsActivity.this.REQUEST_CAMERA);
                    } else {
                        Log.e("DB", "PERMISSION GRANTED");
                        result = true;
                        cameraIntent();
                    }

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AccountDetailsActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                SELECT_FILE);
                    } else {
                        Log.e("DB", "PERMISSION GRANTED");
                        result = true;
                        galleryIntent();
                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }


}
