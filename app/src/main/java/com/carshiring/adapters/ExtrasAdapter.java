package com.carshiring.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.models.ExtraAdded;
import com.carshiring.models.ExtraBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class ExtrasAdapter extends RecyclerView.Adapter<ExtrasAdapter.ViewHolder> {
   public List<ExtraAdded> extraData = new ArrayList<>();

    Context context;
    ArrayList<ExtraBean> beanArrayList=new ArrayList<>();

    public ExtrasAdapter(Context context, ArrayList<ExtraBean> beanArrayList) {
        this.context = context;
        this.beanArrayList = beanArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(parent.getContext()).inflate(R.layout.extraslist,parent,false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
       holder.txt_extrasname.setText(beanArrayList.get(position).getName());
       holder.txt_price.setText(beanArrayList.get(position).getCurrency()+" "+beanArrayList.get(position).getPrice());
      holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              if (i>0){
                  ExtraAdded extraAdded = new ExtraAdded();
                  extraAdded.setName(holder.txt_extrasname.getText().toString().trim());
                  extraAdded.setPrice(beanArrayList.get(position).getPrice());
                  extraAdded.setCurrency(beanArrayList.get(position).getCurrency());
                  extraAdded.setNumber((String) holder.spinner.getItemAtPosition(i));
                  extraData.add(extraAdded);
                  Log.d("TAG", "onItemSelected: "+holder.spinner.getItemAtPosition(i)+"\n"+ holder.txt_extrasname.getText().toString()+
                          "\n"+holder.txt_price.getText().toString()+"\n"+extraData.size());
              }

          }

          @Override
          public void onNothingSelected(AdapterView<?> adapterView) {

          }
      });

    }

    @Override
    public int getItemCount() {
        return beanArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_extrasname,txt_price;
        Spinner spinner;
        public ViewHolder(View itemView) {
            super(itemView);
            txt_extrasname=itemView.findViewById(R.id.txt_extrasname);
            txt_price=itemView.findViewById(R.id.txt_price);
            spinner=itemView.findViewById(R.id.spinner2);
        }
    }
    public List<ExtraAdded> getExtra(){
        return extraData;
    }
}
