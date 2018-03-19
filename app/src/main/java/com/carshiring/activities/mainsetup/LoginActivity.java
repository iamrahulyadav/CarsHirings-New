package com.carshiring.activities.mainsetup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.activities.home.MainActivity;
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

public class LoginActivity extends AppBaseActivity implements View.OnClickListener,TextView.OnEditorActionListener {

    AppGlobal appGlobal=AppGlobal.getInstancess();
    TextView txtWelcome, txtEmail, txtPass, txtLoginForgot;
    EditText username,password;
    LinearLayout ll_forgetpassword;
    Button bt_signup, btnSkip;
    TinyDB sharedpref;
    String token,language_code;
    Toolbar toolbar;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedpref=new TinyDB(getApplicationContext());
        language_code = sharedpref.getString("language_code") ;
        token=sharedpref.getString("access_token");
        appGlobal.context=getApplicationContext();

//        find id
        ll_forgetpassword= (LinearLayout) findViewById(R.id.ll_forgetpassword);
        bt_signup= (Button) findViewById(R.id.bt_signup);
        txtWelcome = (TextView) findViewById(R.id.login_welcome);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtPass = (TextView) findViewById(R.id.txtPass);
        username= (EditText) findViewById(R.id.et_email);
        password= (EditText) findViewById(R.id.et_password);
        txtLoginForgot = (TextView) findViewById(R.id.txtloginForget);
        btnSkip = (Button) findViewById(R.id.btnSkip);

//        updateViews(language_code);

        bt_signup.setOnClickListener(this);
        ll_forgetpassword.setOnClickListener(this);
        actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            /*actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);*/
        }

        setuptoolbar();
    }

    private void setuptoolbar() {
        toolbar= (Toolbar) findViewById(R.id.bottomToolBar);
        TextView textView= (TextView)toolbar.findViewById(R.id.txt_bot);
        textView.setText(getResources().getString(R.string.sign_in));
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }
    String UserId,Msg;
    private void login() {
        InputMethodManager methodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        methodManager.hideSoftInputFromWindow(toolbar.getWindowToken(),0);

        String user=username.getText().toString().trim();
        String pass=password.getText().toString().trim();


        if (!user.isEmpty() && !pass.isEmpty())
        {
            if(!Utility.isNetworkConnected(getApplicationContext())){
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                return;
            }
            Utility.showloadingPopup(this);
            RetroFitApis retroFitApis = RetrofitApiBuilder.getCargHiresapis();
            Call<ApiResponse> responseCall=retroFitApis.login(user,pass);
            responseCall.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Utility.hidepopup();
                    if(response.body().status)
                    {
                        UserDetails userDetails = new UserDetails();
                        userDetails = response.body().response.user_detail;
                        String logindata=gson.toJson(userDetails);
                        Log.d("TAG", "onResponse: "+logindata);
                        appGlobal.setLoginData(logindata);
                        String st =  appGlobal.getUser_id();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Utility.message(getApplicationContext(), "Username or password invalid");
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Utility.hidepopup();
                    Utility.message(getApplicationContext(),getResources().getString(R.string.no_internet_connection));
                }
            });
        }
        else {
            if (user.isEmpty() && pass.isEmpty()) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.enter_email_pass), Toast.LENGTH_SHORT).show();
            } else {
                if (Utility.checkemail(user)) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.enter_uname), Toast.LENGTH_SHORT).show();
                }
                if (pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.enter_pass), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void skipLogin(View view) {
        sharedpref.putBoolean("isSkipLogin",true);
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();
    }

   /* @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void updateViews(String languageCode) {
        Context context = LocaleHelper.setLocale(this, languageCode);
        Resources resources = context.getResources();

        txtPass.setText(R.string.enter_password);
        txtEmail.setText(R.string.enter_email);
        username.setHint(R.string.enter_email);
        password.setHint(R.string.enter_password);
        txtPass.setHint(R.string.enter_password);
        txtWelcome.setText(R.string.Welcome);
        bt_signup.setText(R.string.sign_up);
        txtLoginForgot.setText(R.string.login_forgetpassord);
        password.setTypeface(username.getTypeface());
        password.setOnEditorActionListener(this);
        btnSkip.setText(R.string.skip);

    }
*/
    @Override
    protected void onResume() {
        super.onResume();
        actionBar.setTitle(R.string.login);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id) {
            case R.id.ll_forgetpassword:
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
                break;
            case R.id.bt_signup:
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (v.getId()) {
            case R.id.et_password:
                login();
                break;
        }
        return false;
    }
}
