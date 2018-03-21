package com.carshiring.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.carshiring.R;
import com.carshiring.fragments.SearchCarFragment;
import com.carshiring.models.Category;
import com.carshiring.models.SearchData;
import com.carshiring.webservices.RetrofitApiBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class CarListCategory extends RecyclerView.Adapter<CarListCategory.MyViewHolder> {

    OnItemClickListenerCategory listener;
    double markUp;
    //private List<String> horizontalList;
    private Context context;
    private List<Category.ResponseBean.CatBean>catBeanList = new ArrayList<>();
    private List<SearchData>listCarResult = new ArrayList<>();

    public interface  OnItemClickListenerCategory {
        void onItemClickCategory(int position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public TextView txtCode;
        ImageView image;

        public MyViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.car_cat_name);
            txtCode = (TextView) view.findViewById(R.id.car_cat_code);
            image = (ImageView) view.findViewById(R.id.car_cat_image);

        }

        void bindListener(final int position, final OnItemClickListenerCategory listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickCategory(position);
                }
            });

        }
    }


    public CarListCategory(Context context, List<SearchData> listCarResult, List<Category.ResponseBean.CatBean> catBeanList, OnItemClickListenerCategory listener
                         ) {

        this.listener = listener;
        //this.horizontalList = horizontalList;
        this.context = context;
        this.catBeanList = catBeanList;
        this.listCarResult = listCarResult;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_list_category, parent, false);

        return new MyViewHolder(itemView);
    }
    private static DecimalFormat df2 = new DecimalFormat(".##");
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        /*Set<Category.ResponseBean.CatBean> catBeans = new HashSet<>();
        catBeans.addAll(catBeanList);
        catBeanList.clear();
        catBeanList.addAll(catBeans);*/

/*        Set<Category.ResponseBean.CatBean> s = new LinkedHashSet<Category.ResponseBean.CatBean>(catBeanList);*/


        //      Toast.makeText(context, catBeanList.get(0).getCategory_name(), Toast.LENGTH_SHORT).show();
        holder.txtName.setText(/*"name"*/catBeanList.get(position).getCategory_name());

        Glide.with(context)
                .load(RetrofitApiBuilder.IMG_BASE_URL + catBeanList.get(position).getCategory_image())
                .into(holder.image);
        //holder.txtCode.setText(/*"code"*/catBeanList.get(position).getCode() +"");
        markUp = Double.parseDouble(SearchCarFragment.markup);
        String price = listCarResult.get(position).getPrice();
        double d = Double.parseDouble(price);
        double priceNew  = d+(d*markUp)/100;

        holder.txtCode.setText(/*"code"*/listCarResult.get(position).getCurrency() + " " +
                " "+String.valueOf(df2.format(priceNew)));

        Log.d("TAG", catBeanList.get(position).getCategory_name() + " - "
                + catBeanList.get(position).getCategory_image() + " - "
                + catBeanList.get(position).getCode() + " - ");
        //   holder.txtView.setText(horizontalList.get(position));

        holder.bindListener(position, listener);
/*        holder.txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,holder.txtView.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return catBeanList.size();
    }
}