package com.carshiring.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.carshiring.R;
import com.carshiring.models.SearchData;

import java.util.List;


/**
 * Created by rakhi on 09/02/2018.
 * Contact Number : +91 9958187463
 */

public class CarResultsListAdapter extends RecyclerView.Adapter<CarResultsListAdapter.MyViewHolder>{
    final OnItemClickListener listener;
    private final Context context;
    List<SearchData> list;

    public interface  OnItemClickListener {
        void onItemClick(SearchData carDetail);
    }

    public CarResultsListAdapter(Context context , List<SearchData> list, OnItemClickListener listener){
        this.context = context ;
        this.list =  list ;
        this.listener =  listener ;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  =  LayoutInflater.from(parent.getContext()).inflate(R.layout.search_car_result_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SearchData model=list.get(position);

        holder.tvCarModelName.setText(model.getModel());
        holder.tvCarPricing.setText(" SAR 341.30 / 2 day\t");
        holder.bindListener(model,listener);
    }

    @Override
    public int getItemCount() {
        return list.size() ;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCarModelName,tvCarPricing;
        LinearLayout spec1Container ;
        private View itemView  ;
        ImageView imgCarResult,imgCarAgencyLogo ;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvCarModelName= (TextView) itemView.findViewById(R.id.tvCarModelName);
            tvCarPricing= (TextView) itemView.findViewById(R.id.tvCarPricing);
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
//                    case "1" :
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
