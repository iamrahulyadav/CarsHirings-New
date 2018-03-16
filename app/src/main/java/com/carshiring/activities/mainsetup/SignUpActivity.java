package com.carshiring.activities.mainsetup;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.carshiring.R;
import com.carshiring.utilities.AppBaseActivity;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.mukesh.tinydb.TinyDB;
import com.carshiring.webservices.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppBaseActivity implements TextView.OnEditorActionListener {

    EditText username,pass,confirmpassword;
    View v;
    TinyDB sharedpref;
    String email,password,confirmpass;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        v=findViewById(android.R.id.content);
        sharedpref=new TinyDB(getApplicationContext());

        actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
        username= (EditText) findViewById(R.id.et_Signupemail);
        pass= (EditText) findViewById(R.id.et_signuppassword);
        confirmpassword= (EditText) findViewById(R.id.et_signupconfirmpassword);
        pass.setTypeface(username.getTypeface());
        confirmpassword.setTypeface(username.getTypeface());

        username.setOnEditorActionListener(this);
        pass.setOnEditorActionListener(this);
        confirmpassword.setOnEditorActionListener(this);

        setuptoolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        actionBar.setTitle(getResources().getString(R.string.title_create_account));
        checknetwork();
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
    private void setuptoolbar() {

        toolbar= (Toolbar) findViewById(R.id.bottomToolBar);
        TextView textView= (TextView)toolbar.findViewById(R.id.txt_bot);
        textView.setText(getResources().getString(R.string.sign_up));
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }
    public String Msg;
    private void signup() {
        InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(toolbar.getWindowToken(),0);

        email = username.getText().toString().trim();
        password = pass.getText().toString().trim();
        confirmpass = confirmpassword.getText().toString();

        checknetwork();
        if (!email.isEmpty() && !password.isEmpty() && !confirmpass.isEmpty()) {
            if (Utility.checkemail(email)) {
                if (password.equals(confirmpass)) {
                    Utility.showloadingPopup(this);
                    RetroFitApis fitApis = RetrofitApiBuilder.getCargHiresapis();
                    Call<ApiResponse> call = fitApis.signup(email, password);
                    call.enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            Msg = response.body().msg;
                            Utility.message(SignUpActivity.this,Msg);
                            Utility.hidepopup();
                            finish();
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            Utility.message(SignUpActivity.this, getResources().getString(R.string.no_internet_connection));
                        }
                    });
                } else {
                    Utility.message(this, getResources().getString(R.string.pass_doesnotmatch));
                }
            } else {
                Utility.message(this, getResources().getString(R.string.please_enter_valid_email));
            }
        } else {
            if (email.isEmpty() && password.isEmpty() && confirmpass.isEmpty()) {
                Utility.message(this, getResources().getString(R.string.fill_all_field));
            }
            else
            {
                if(email.isEmpty())
                {
                    Utility.message(this, getResources().getString(R.string.please_enter_email));
                }
                if(password.isEmpty())
                {
                    Utility.message(this, getResources().getString(R.string.please_enter_pass));
                }
                if(confirmpass.isEmpty())
                {
                    Utility.message(this, getResources().getString(R.string.please_enter_confirm_pass));
                }
            }
        }
    }
    public void checknetwork() {
        if(!Utility.isNetworkConnected(this))
        {
            Snackbar.make(v,getResources().getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checknetwork();
                        }
                    }).setActionTextColor(getResources().getColor(R.color.redStrong)).show();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        int id=v.getId();
        email = username.getText().toString().trim();
        password = pass.getText().toString().trim();
        confirmpass = confirmpassword.getText().toString();
        switch (id)
        {
            case R.id.et_Signupemail:
                if(actionId== EditorInfo.IME_ACTION_NEXT)
                {
                    if(!email.isEmpty())
                    {
                        if(!Utility.checkemail(email))
                        {
                            Utility.message(this, getResources().getString(R.string.please_enter_valid_email));
                            username.setFocusableInTouchMode(true);
                       /* username.requestFocus();
                        InputMethodManager methodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        methodManager.showSoftInput(username,InputMethodManager.SHOW_FORCED);*/
                        }
                    }
                    else {
                        Utility.message(SignUpActivity.this, getResources().getString(R.string.please_enter_email));
                    }

                }
                break;
            case R.id.et_signupconfirmpassword:
                if(actionId==EditorInfo.IME_ACTION_DONE)
                {
                    signup();
                }
                break;
        }
        return false;
    }
}
