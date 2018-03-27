package com.carshiring.splash;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.activities.home.MainActivity;
import com.carshiring.activities.mainsetup.LoginActivity;
import com.carshiring.models.LanguageModel;
import com.carshiring.utilities.AppBaseActivity;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.mukesh.tinydb.TinyDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppBaseActivity {

    public String accessToken,languageselected;
    TinyDB sharedpref;
    Spinner spinner_language;
    TextView txten, txtAr;
    View v;
    LinearLayout layout;
    TextView txtChoose;
    ArrayList<String> langlistname,langlistcode,langlistId  ;
    String[] lan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        sharedpref=new TinyDB(getApplicationContext());
        layout = findViewById(R.id.language_layout);
        txtChoose = findViewById(R.id.textView);
         String language_code = sharedpref.getString("language_code") ;
        boolean isSkipLogin = sharedpref.getBoolean("isSkipLogin");
        spinner_language = (Spinner) findViewById(R.id.spinner_language);
        txten = findViewById(R.id.splashtxtEn);
        txtAr = findViewById(R.id.splashtxtAr);

        if(language_code!=null && !language_code.isEmpty()) {
//            updateResources(this, language_code);
            if (isSkipLogin) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.setVisibility(View.GONE);
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                },2000);

            } else if (sharedpref.contains("login_data")){
                layout.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                },2000);

            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));finish();
            }
            return ;
        } else {
            layout.setVisibility(View.VISIBLE);
            txtChoose.setVisibility(View.VISIBLE);
        }

        v=findViewById(android.R.id.content);
        langlistname=new ArrayList<>();
        langlistcode=new ArrayList<>();
        langlistId=new ArrayList<>();
        lan=new String[langlistname.size()];
        if(actionBar!=null) actionBar.hide();
        accessToken = sharedpref.getString("access_token");
        if(accessToken!=null && !accessToken.isEmpty()) {
            getLanguageList();
        }else{
            getAccessToken();
        }

    }

    private static boolean updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return true;
    }

    private void updateRes(String lang){
        Resources res = getApplicationContext().getResources();
// Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(lang.toLowerCase())); // API 17+ only.
// Use conf.locale = new Locale(...) if targeting lower versions
        res.updateConfiguration(conf, dm);
    }

    public static String TAG = SplashActivity.class.getName();

    private void getLanguageList() {
        final  SplashActivity _this =  this ;
        RetroFitApis apis=RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> responseCall=apis.lang_list(" ");
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                Log.d(TAG, "onResponse: "+response.body().toString());
                if(response.body()!=null) {
                    if(response.body().status) {
                        List<LanguageModel> language_list = response.body().response.language_list;
                        if (language_list != null) {
                           /* langlistname.add("Select Language");
                            langlistcode.add("");

                            langlistId.add("");*/
                            for (int i = 0; i < language_list.size(); i++) {
                                LanguageModel languages = language_list.get(i);
                                txten.setText(language_list.get(0).language_code);
                                txtAr.setText(language_list.get(1).language_code);
                                String languageId = languages.language_id;
                                String languageCode = languages.language_code;
                                String languageName = languages.language_name;
                                langlistname.add(languageName);
                                langlistcode.add(languageCode);
                                langlistId.add(languageId);
                            }
                            lan = langlistname.toArray(lan);
                            handlespinner();
                        } else {
                            Utility.message(SplashActivity.this, response.body().message);
                        }
                    }else{
                        if(response.body().error_code==102){
                        }
                        Toast.makeText(_this, response.body().message, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SplashActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(SplashActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getAccessToken() {
        RetroFitApis retroFitApis = RetrofitApiBuilder.getCarGatesapi();
        String grant_type = "client_credentials";
        String client_id = "developer";
        String client_secret = "5a633cf4392e8";
        Call<ApiResponse> apiResponseCall = retroFitApis.token(grant_type, client_id, client_secret);
        apiResponseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.body()!=null){
                    accessToken = response.body().access_token;
                    if(accessToken!=null && !accessToken.isEmpty()) {
                        sharedpref.putString("access_token", accessToken);
                        getLanguageList();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
                Toast.makeText(SplashActivity.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                spinner_language.setClickable(false);
                spinner_language.setEnabled(false);
            }
        });
    }

    private void handlespinner() {

        txtAr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String language_code  = txtAr.getText().toString().trim();
                sharedpref.putString("language_code",language_code);
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        });

        txten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String language_code  = txten.getText().toString().trim();
                sharedpref.putString("language_code",language_code);
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        });

        final ArrayAdapter<String> adapt=new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_item,lan);
        adapt.setDropDownViewResource(R.layout.spinner_item);
        spinner_language.setAdapter(adapt);
        spinner_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0) {
                    languageselected = (String) adapterView.getItemAtPosition(i);
                    String language_code  = langlistcode.get(i);
                    String language_id=langlistId.get(i);
                    sharedpref.putString("language_code",language_code);
                    sharedpref.putString("language_id",language_id);
                    updateRes(language_code);

                    Toast.makeText(SplashActivity.this, language_code, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Utility.message(getApplicationContext(),"no");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checknetwork();
    }

    public void checknetwork() {
        if(!Utility.isNetworkConnected(this))
        {
            spinner_language.setClickable(false);
            spinner_language.setEnabled(false);
            Snackbar.make(v,getResources().getString(R.string.check_internet),Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checknetwork();
                }
            }).setActionTextColor(getResources().getColor(R.color.redStrong)).show();
        }
        else
        {

        }
    }
}
