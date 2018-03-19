package com.carshiring.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.carshiring.R;

import com.carshiring.activities.home.CarDetailActivity;
import com.carshiring.activities.home.ExcessProtectionActivity;
import com.carshiring.activities.home.Extras;
import com.carshiring.adapters.CarResultsListAdapter;
import com.carshiring.models.CarDetailBean;
import com.carshiring.utilities.Utility;

import static com.carshiring.activities.home.CarDetailActivity.carSpecificationList;
import static com.carshiring.activities.home.CarDetailActivity.termsurl;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class CarDetailTab1Fragment extends Fragment implements View.OnClickListener {
    View view;
    LinearLayout ll_extra,ll_protection;
    TextView terms,quotes,carname,carprice, txtPoint;
    ImageView carImg,imglogo;
  //  List<CarSpecification> carSpecificationList;
    ProgressBar bar,bar1;
    LinearLayout ll;
    LinearLayout gl;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.cardetail_tab1,container,false);
        ll_extra= (LinearLayout) view.findViewById(R.id.ll_extra);
        ll_protection=(LinearLayout) view.findViewById(R.id.ll_protection);
        terms= (TextView) view.findViewById(R.id.txt_terms);
        quotes=(TextView) view.findViewById(R.id.txt_savequote);
        carname= (TextView) view.findViewById(R.id.txt_modelname);
        carprice= (TextView) view.findViewById(R.id.txt_carPrice);
        carImg= (ImageView) view.findViewById(R.id.img_car);
        imglogo= (ImageView) view.findViewById(R.id.img_carlogo);
        bar= (ProgressBar) view.findViewById(R.id.progressbar);
        bar1= (ProgressBar) view.findViewById(R.id.progressbar1);
        txtPoint = view.findViewById(R.id.point_get);
       /* SingleCarDetails singleCarDetails = new SingleCarDetails();
        singleCarDetails = CarDetail.singleCarDetails;
        carSpecificationList=CarDetail.spec;*/
        ll_extra.setOnClickListener(this);
        ll_protection.setOnClickListener(this);
        terms.setOnClickListener(this);
        quotes.setOnClickListener(this);
        ll= (LinearLayout) view.findViewById(R.id.ll_otherspec);
        gl=  view.findViewById(R.id.gr_Spec);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bar.setVisibility(View.VISIBLE);
        bar1.setVisibility(View.VISIBLE);
        Glide.with(getContext()).load(CarDetailActivity.carImage).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                bar.setVisibility(View.GONE);
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                bar.setVisibility(View.GONE);
                return false;
            }
        }).into(carImg);
        Glide.with(getContext()).load(CarDetailActivity.logo).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                bar1.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                bar1.setVisibility(View.GONE);
                return false;
            }
        }).into(imglogo);
        carname.setText(CarDetailActivity.modelname + getResources().getString(R.string.or_similar));
        txtPoint.setText(getResources().getString(R.string.points_collected )+ CarResultsListAdapter.calPoint);
        carprice.setText(CarDetailActivity.currency+"  "+CarDetailActivity.carPrice);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if(carSpecificationList!=null) {
            for(int i=0;i<carSpecificationList.size();i++) {
                CarDetailBean.FeatureBean carSpecification =carSpecificationList.get(i);
                if (carSpecification.aircondition != null) {
                    if (!carSpecification.aircondition.equalsIgnoreCase("false")) {
                        View viw = getActivity().getLayoutInflater().inflate(R.layout.gridcustomstyle, null);
                       // TextView tt = (TextView) viw.findViewById(R.id.txt_spec);
                        TextView tt1 = new TextView(getContext());
                        tt1.setLayoutParams(lparams);
                        tt1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ac,0,0,0);
                        tt1.setCompoundDrawablePadding(25);
                        tt1.setTextSize(16);
                        tt1.setTypeface(Typeface.DEFAULT_BOLD);
                        tt1.setText(getResources().getString(R.string.air_condition));
                        gl.addView(tt1);
                        gl.setOrientation(LinearLayout.VERTICAL);
                    }

                    if(!carSpecification.transmission.isEmpty())
                    {

                        TextView tt1 = new TextView(getContext());
                        tt1.setLayoutParams(lparams);
                        tt1.setText(carSpecification.getTransmission());
                        tt1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.manual,0,0,0);
                        tt1.setCompoundDrawablePadding(25);
                        tt1.setTextSize(16);
                        tt1.setTypeface(Typeface.DEFAULT_BOLD);
                        gl.addView(tt1);
                        gl.setOrientation(LinearLayout.VERTICAL);
                    }
                    if(!carSpecification.fueltype.isEmpty())
                    {

                        TextView tt1 = new TextView(getContext());
                        tt1.setLayoutParams(lparams);
                        tt1.setText(carSpecification.getFueltype());
                        tt1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fuel,0,0,0);
                        tt1.setCompoundDrawablePadding(25);
                        tt1.setTextSize(16);
                        tt1.setTypeface(Typeface.DEFAULT_BOLD);
                        gl.addView(tt1);
                        gl.setOrientation(LinearLayout.VERTICAL);
                    }
                    if(!carSpecification.bag.isEmpty())
                    {

                        TextView tt1 = new TextView(getContext());
                        tt1.setLayoutParams(lparams);
                        tt1.setText(carSpecification.getBag() + " " + getResources().getString(R.string.bags));
                        tt1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bag,0,0,0);
                        tt1.setCompoundDrawablePadding(25);
                        tt1.setTextSize(16);
                        tt1.setTypeface(Typeface.DEFAULT_BOLD);
                        gl.addView(tt1);
                        gl.setOrientation(LinearLayout.VERTICAL);
                    }
                    if(!carSpecification.passenger.isEmpty())
                    {

                        TextView tt1 = new TextView(getContext());
                        tt1.setLayoutParams(lparams);
                        tt1.setText(carSpecification.getPassenger() + " " + getResources().getString(R.string.passanger));
                        tt1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_walkway,0,0,0);
                        tt1.setCompoundDrawablePadding(25);
                        tt1.setTextSize(16);
                        tt1.setTypeface(Typeface.DEFAULT_BOLD);
                        gl.addView(tt1);
                        gl.setOrientation(LinearLayout.VERTICAL);
                    }
                    if(!carSpecification.door.isEmpty())
                    {

                        TextView tt1 = new TextView(getContext());
                        tt1.setLayoutParams(lparams);
                        tt1.setText(carSpecification.getDoor()+" "+ getResources().getString(R.string.doors));
                        tt1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_car_door,0,0,0);
                        tt1.setCompoundDrawablePadding(25);
                        tt1.setTextSize(16);
                        tt1.setTypeface(Typeface.DEFAULT_BOLD);
                        gl.addView(tt1);
                        gl.setOrientation(LinearLayout.VERTICAL);
                    }
                }
            }
        }
   }


    @Override
    public void onClick(View v) {
        Uri url=Uri.parse(termsurl);

        Intent it=new Intent(getActivity(), ExcessProtectionActivity.class);
        switch (v.getId())
        {
            case R.id.ll_extra:
                startActivity(new Intent(getActivity(),Extras.class));
                break;
            case R.id.ll_protection:
                it.putExtra("get","ForProtec");
                startActivity(it);
                break;
            case R.id.txt_terms:
                Intent intent=new Intent(Intent.ACTION_VIEW,url);
                startActivity(intent);
                //    startActivity(new Intent(getActivity(), TermsandCondition.class));
                break;
            case R.id.txt_savequote:
                it.putExtra("get","Forquotes");
                startActivity(it);
                break;
        }
    }
}
