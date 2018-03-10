package com.carshiring.utilities;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.carshiring.interfaces.IBaseActivity;
import com.carshiring.interfaces.IRefreshToken;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.mukesh.tinydb.TinyDB;

import com.carshiring.webservices.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Rakhi on 8/2/2018.
 */

public class AppBaseActivity extends AppCompatActivity implements IBaseActivity,IRefreshToken {
    int CALLBACK ;
   public ActionBar actionBar ;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    public void getToken(final IRefreshToken iRefreshToken){
        final TinyDB sharedpref=new TinyDB(getApplicationContext());
        RetroFitApis retroFitApis = RetrofitApiBuilder.getCarGatesapi();
        String grant_type = "client_credentials";
        String client_id = "developer";
        String client_secret = "5a633cf4392e8";

        Call<ApiResponse> apiResponseCall = retroFitApis.token(grant_type, client_id, client_secret);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                String token = response.body().access_token;
                sharedpref.putString("access_token",token);
                iRefreshToken.refreshTokenCallBack();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AppBaseActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void refreshTokenCallBack() {

    }

    @Override
    public void setLanguage() {

    }
}
