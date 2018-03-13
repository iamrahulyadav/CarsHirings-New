package com.carshiring.activities.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.models.LanguageModel;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.mukesh.tinydb.TinyDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Language extends AppCompatActivity
{
    RadioGroup langgroup;
    String language,token,langId,language_code ;
    TinyDB sharedpref;
    HashMap<String, String>langMap;
    ArrayList<String> langlistname,langlistcode,langlistId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        language="English";
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setTitle("Language Selection");
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        sharedpref=new TinyDB(getApplicationContext());
        token=sharedpref.getString("access_token");
        langId=sharedpref.getString("language_id");
        langlistname=new ArrayList<>();
        langlistcode=new ArrayList<>();
        langlistId=new ArrayList<>();
        langMap = new HashMap<>();
        getLanguageList();

        //  langgroup= (RadioGroup) findViewById(R.id.LanguageGroup);
        setSelectedFilter(language);


    }

    private void setSelectedFilter(String language)
    {

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
    public void addradio()
    {
        for(int row=0;row<1;row++)
        {
            langgroup= (RadioGroup) findViewById(R.id.LanguageGroup);
            langgroup.setOrientation(RadioGroup.VERTICAL);
            for(int i=0;i<langlistname.size();i++)
            {
                View v=new View(this);
                v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1));
                v.setBackgroundColor(Color.parseColor("#ccc4c4"));
                RadioButton radioButton = (RadioButton) getLayoutInflater().inflate(R.layout.customradio,null);
                radioButton.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                radioButton.setId(Integer.parseInt(langlistId.get(i)));

                // language_code  = langlistcode.get(i);
                radioButton.setText(langlistname.get(i));
                langgroup.addView(radioButton);
                langgroup.addView(v);
            }
        }
        langgroup.check(Integer.parseInt(langId));
        //   ((RadioButton)langgroup.getChildAt(Integer.parseInt(langId))).setChecked(true);

        langgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton= (RadioButton) langgroup.findViewById(langgroup.getCheckedRadioButtonId());
                if (radioButton!=null)
                {
                    int i=checkedId-1;
                    String id= String.valueOf(radioButton.getId());
                    language_code  = langMap.get(radioButton.getText());
                    radioButton.setChecked(true);
                    sharedpref.putString("language_code",language_code);
                    sharedpref.putString("language_id",id);
                    Intent intent = new Intent(Language.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void getLanguageList() {
        RetroFitApis apis= RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> responseCall=apis.lang_list(sharedpref.getString("access_token"));
        responseCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null) {
                    List<LanguageModel> language_list = response.body().response.language_list;
                    if (language_list != null) {
                        for (int i = 0; i < language_list.size(); i++) {
                            LanguageModel languages = language_list.get(i);
                            String languageId = languages.language_id;
                            String languageCode = languages.language_code;
                            String languageName = languages.language_name;
                            langlistname.add(languageName);
                            langlistcode.add(languageCode);
                            langMap.put(languageName,languageCode);
                            langlistId.add(languageId);
                        }
                        addradio();
                    } else {
                        Utility.message(Language.this, response.body().msg);
                    }
                }else{
                    Toast.makeText(Language.this, "Connection Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(Language.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
