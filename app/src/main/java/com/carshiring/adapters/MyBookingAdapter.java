package com.carshiring.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.carshiring.R;
import com.carshiring.models.BookingData;
import com.carshiring.models.BookingHistory;

import java.util.List;

/**
 * Created by Rakhi
 *
 */

public class MyBookingAdapter extends RecyclerView.Adapter<MyBookingAdapter.MyViewHolder>
{
    List<BookingHistory> bookinglist;
    Context context;
    int lastpositon= -1;
    public MyBookingAdapter(Context context, List<BookingHistory> bookinglist) {
        this.bookinglist=bookinglist;
        this.context=context;
    }
    ClickItem item;

    public interface ClickItem
    {
       public void click(View view, int Position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(parent.getContext()).inflate(R.layout.previous_booking_row,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        //BookingData bookingModel=bookinglist.get(position);
        holder.refnumb.setText(bookinglist.get(position).getBooking_id());
        holder.date_time.setText(bookinglist.get(position).getBooking_from_date());
        holder.rate.setText(bookinglist.get(position).getBooking_currency() + " " + bookinglist.get(position).getBooking_actual_price());

/*rate, txtStatus, txtPickUp, txtDropUp, txtPoint*/

        if (bookinglist.get(position).getBooking_status().equals("0")){
            holder.txtStatus.setText("Processing");
        } else if (bookinglist.get(position).getBooking_status().equals("1")){
            holder.txtStatus.setText("Completed");
        } else if (bookinglist.get(position).getBooking_status().equals("2")){
            holder.txtStatus.setText("Canceled");
        } else {
            holder.txtStatus.setText("Failed");
        }
        holder.txtPoint.setText(" " /*+ bookinglist.get(position).getBooking_point_used()*/);
        holder.txtDropUp.setText(bookinglist.get(position).getBooking_to_location());
        holder.txtPickUp.setText(bookinglist.get(position).getBooking_from_location());
        holder.txtCarName.setText(bookinglist.get(position).getBooking_car_model());
        holder.txtPaymentBy.setText(bookinglist.get(position).getBooking_company_name());
        Glide.with(context).load(bookinglist.get(position).getBooking_supllier_log()).into(holder.imgLogo);
        Glide.with(context).load(bookinglist.get(position).getBooking_car_image()).into(holder.car_image);


       /* if(position>lastPosition)
        {
            Animation animation= AnimationUtils.loadAnimation(context,R.anim.up_from_bottom);
            holder.itemView.startAnimation(animation);
            lastPosition=position;
        }*/
      /*  Animation animation= AnimationUtils.loadAnimation(context,(position>lastPosition)? R.anim.bottom_from_up :R.anim.up_from_bottom);
        holder.itemView.startAnimation(animation);
        lastPosition=position;*/
       /* if (position>lastpositon) {
            AnimatioonUtils.animate(holder,true);
        }
        else
        {
            AnimatioonUtils.animate(holder,false);
        }
        lastpositon=position;*/
    }
        public void submit(ClickItem item) {
        this.item=item;
    }

    @Override
    public int getItemCount() {
        return bookinglist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView refnumb,date_time,rate, txtStatus, txtPickUp, txtDropUp, txtPoint, txtCarName, txtPaymentBy;
        ImageView imgLogo, car_image;

        public MyViewHolder(View itemView) {
            super(itemView);

            refnumb= (TextView) itemView.findViewById(R.id.txt_bookingInvoicenumber);
            date_time= (TextView) itemView.findViewById(R.id.txt_date_time);
            rate=(TextView) itemView.findViewById(R.id.txt_rate);
            txtStatus = (TextView) itemView.findViewById(R.id.previous_booking_txtStatus);
            txtPickUp = (TextView) itemView.findViewById(R.id.previous_booking_pickUp);
            txtDropUp = (TextView) itemView.findViewById(R.id.previous_booking_dropUp);
            txtPoint = (TextView) itemView.findViewById(R.id.previous_booking_txtPoints);
            txtPaymentBy = (TextView) itemView.findViewById(R.id.previous_booking_txtCarName);
            txtCarName = (TextView) itemView.findViewById(R.id.previous_booking_txtPaymentBy);
            imgLogo = (ImageView) itemView.findViewById(R.id.previous_booking_imgCarLogo);
            car_image = (ImageView) itemView.findViewById(R.id.my_booking_image);

            itemView.setOnClickListener(this);
    }
    @Override
        public void onClick(View v) {
            if(item!=null)
            {
                item.click(v,getPosition());


            }
        }
    }
}
