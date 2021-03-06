package com.carshiring.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.carshiring.R;
import com.carshiring.fragments.SearchCarFragment;
import com.carshiring.models.Category;
import com.carshiring.models.SearchData;
import com.carshiring.models.TestData;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.carshiring.splash.SplashActivity.TAG;


/**
 * Created by rakhi on 09/02/2018.
 * Contact Number : +91 9958187463
 */

public class CarResultsListAdapter extends RecyclerView.Adapter<CarResultsListAdapter.MyViewHolder>{
    final OnItemClickListener listener;
    private final Context context;
    List<SearchData> list;
    List<TestData>catBeanList;

    ProgressBar bar1;
    double pointpercent,calPrice, markUp;
    public static int calPoint;

    public interface  OnItemClickListener {
        void onItemClick(SearchData carDetail);
    }

    public CarResultsListAdapter(Context context , List<SearchData> list,List<TestData>catBeanList,
                                 OnItemClickListener listener){
        this.context = context ;
        this.list =  list ;
        this.catBeanList = catBeanList;
        this.listener =  listener ;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  =  LayoutInflater.from(parent.getContext()).inflate(R.layout.search_car_result_item,parent,false);

        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final SearchData model=list.get(position);

        holder.tvCarModelName.setText(model.getModel());
        holder.txtSupplierNmae.setText(context.getResources().getString(R.string.supplied_by) + model.getSupplier());
        holder.txtDropCity.setText(SearchCarFragment.pickName);
        if (model.getFeature().getBag().equals("0")){
            holder.tvBagNo.setVisibility(View.GONE);
        }
        holder.tvBagNo.setText(model.getFeature().getBag()+" " + context.getResources().getString(R.string.large_bag));
        markUp = Double.parseDouble(SearchCarFragment.markup);
        String price = model.getPrice();
        double d = Double.parseDouble(price);
        double priceNew  = d+(d*markUp)/100;

        holder.tvCarPricing.setText(model.getCurrency()
                +" "+String.valueOf(df2.format(priceNew))+" /"+ model.getTime()
                +" "+model.getTime_unit());
        holder.txtDoor.setText(model.getFeature().getDoor()+" "+ context.getResources().getString(R.string.doors));
        if (model.getFeature().getAircondition().equals("true")){
            holder.txtAc.setVisibility(View.VISIBLE);
        }

        for (TestData catBean: catBeanList){
            if (catBean.getCat_code()!=null){
                for (String s:catBean.getCat_code()){
                    if (s.equals(model.getCategory())){
                        holder.txtClass.setText("Class : "+catBean.getCat_name());
                    }
                }
            }
        }

        holder.txtTrans.setText(model.getFeature().getTransmission());
        holder.txtFuel.setText("Full to fuel");
        List<SearchData.CoveragesBean>coveragesBeans=new ArrayList<>();
        coveragesBeans.addAll(model.getCoverages());

        for (SearchData.CoveragesBean bean : list.get(position).getCoverages()){
            if (bean.getCode().equalsIgnoreCase("412")){
                holder.txtOneway.setText(bean.getName() +" : "+ bean.getCurrency2()+" "
                        +bean.getAmount2());
                holder.txtOneway.setVisibility(View.VISIBLE);
            } else if ( bean.getCode().equalsIgnoreCase("410")){
                holder.txtDriverSur.setText(bean.getName()+" : "+bean.getCurrency2()+" "
                        + bean.getAmount2());
                holder.txtDriverSur.setVisibility(View.VISIBLE);
            }
        }

        holder.txtTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = model.getTc();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        String urla =model.getSupplier_logo();
        Glide.with(context)
                .load(urla)
                .into(holder.imgCarAgencyLogo);
        try {
            URL url = new URL(urla);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setInstanceFollowRedirects(false);
            httpConn.setRequestMethod("HEAD");
            httpConn.connect();
            if (httpConn.getResponseCode()!=404){
                Glide.with(context)
                        .load(urla)
                        .into(holder.imgCarAgencyLogo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String m = model.getImage();
        Glide.with(context)
                .load(model.getImage())
                .into(holder.imgCarResult);
        bar1.setVisibility(View.GONE);
        double pricea = Double.parseDouble(model.getPrice());
//        calculate point
        pointpercent = Double.parseDouble(SearchCarFragment.pointper);
        calPrice = (priceNew*pointpercent)/100;
        calPoint = (int) (calPrice/0.02);
        holder.txtPoint.setText(context.getResources().getString(R.string.points_collected) + String.valueOf(calPoint));
        holder.bindListener(model,listener);

    }

    private static DecimalFormat df2 = new DecimalFormat(".##");

    @Override
    public int getItemCount() {
        return list.size() ;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCarModelName,tvCarPricing, txtAc,txtClass,txtSupplierNmae, tvBagNo,
                txtDropCity,txtDoor,txtTrans, txtTerms,txtDriverSur,txtOneway,txtFuel,txtPoint, txtSeat;
        LinearLayout spec1Container ;
        private View itemView  ;
        ImageView imgCarResult,imgCarAgencyLogo ;
        public MyViewHolder(View itemView) {
            super(itemView);

            bar1= (ProgressBar) itemView.findViewById(R.id.progressbar);
            tvCarModelName= (TextView) itemView.findViewById(R.id.tvCarModelName);
            tvCarPricing= (TextView) itemView.findViewById(R.id.tvCarPricing);

            tvBagNo = itemView.findViewById(R.id.tvBagSp);
            txtClass = itemView.findViewById(R.id.txtclass);
            txtSupplierNmae = itemView.findViewById(R.id.txtSupplierName);
            txtDoor = itemView.findViewById(R.id.tvDoor);
            txtDropCity = itemView.findViewById(R.id.dropCity);
            bar1.setVisibility(View.VISIBLE);
            txtAc = itemView.findViewById(R.id.txtac);
            txtTrans = itemView.findViewById(R.id.txttrans);
            txtTerms = itemView.findViewById(R.id.txtTermsCond);
            txtFuel = itemView.findViewById(R.id.txtFuel);
            txtPoint = itemView.findViewById(R.id.txtpoint);
            txtSeat = itemView.findViewById(R.id.tvSeat);
            txtDriverSur = itemView.findViewById(R.id.txtDriverSurCharge);
            txtOneway = itemView.findViewById(R.id.txtOneway);

            spec1Container= (LinearLayout) itemView.findViewById(R.id.spec1Container) ;
            imgCarResult= (ImageView) itemView.findViewById(R.id.imgCarResult) ;
            imgCarAgencyLogo= (ImageView) itemView.findViewById(R.id.imgCarAgencyLogo) ;
            this.itemView = itemView ;

        }

        void bindListener(final SearchData carDetail, final OnItemClickListener listener){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(carDetail);
                }
            });
//            setCarImage(carDetail.carmanagement_carimage);
//            setCompanyLogo(carDetail.company_logo);
//            setupSpecification(carDetail.carmanagement_specifications);
        }

        private void setCompanyLogo(String cmpny_logo) {
//            Glide.with(context).load(RetrofitApiBuilder.IMG_BASE_URL+cmpny_logo).into(imgCarAgencyLogo);

            Glide.with(context).load(cmpny_logo).into(imgCarAgencyLogo);
        }

        private void setCarImage(String img) {
//            Glide.with(context).load(RetrofitApiBuilder.IMG_BASE_URL+img).into(imgCarResult);

            Glide.with(context).load(img).into(imgCarResult);
        }

//        private void setupSpecification(List<CarSpecification> specs) {
//            spec1Container.removeAllViews();
//            StringBuilder specification  = new StringBuilder();
//            StringBuilder spec1 =  new StringBuilder();
//            StringBuilder spec2 =  new StringBuilder();
//            StringBuilder spec3 =  new StringBuilder();
//            StringBuilder spec4 =  new StringBuilder();
//            for (int indexSpec = 0; indexSpec < specs.size(); indexSpec++) {
//                CarSpecification spec = specs.get(indexSpec);
//
//                switch(spec.specification_display) {
//                    case "ab" :
//                        View v  = LayoutInflater.from(context).inflate(R.layout.spec1_item,spec1Container,false) ;
//                        TextView tvCarSpec1 = (TextView) v.findViewById(R.id.tvCarSpec1) ;
//                        tvCarSpec1.setText(spec.specification_name);
//                        spec1Container.addView(v);
//                        break ;
//
//                    case "2" :
//                        spec2.append(spec.specification_name).append(" | ");
//                        break ;
//
//                    case "3" :
//                        spec3.append(spec.specification_name).append(" | ");
//                        break ;
//
//                    case "4" :
//                        spec4.append(spec.specification_name).append(" | ");
//                        break ;
//                }
//
//            }
//
//
//        }

    }





}
