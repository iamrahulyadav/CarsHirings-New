package com.carshiring.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.carshiring.R;
import com.carshiring.fragments.SearchCarFragment;
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

    String type;

    public MyBookingAdapter(List<BookingHistory> bookinglist, Context context, String type) {
        this.bookinglist = bookinglist;
        this.context = context;
        this.type = type;
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        //BookingData bookingModel=bookinglist.get(position);
        holder.refnumb.setText(bookinglist.get(position).getBooking_id());
        holder.date_time.setText(bookinglist.get(position).getBooking_from_date());
        holder.txtjourneyToDate.setText(bookinglist.get(position).getBooking_to_date());
        holder.rate.setText(bookinglist.get(position).getBooking_currency()
                + " " + bookinglist.get(position).getBooking_actual_price());
        if (!bookinglist.get(position).getBooking_payfort_value().equals("0.00")){
            holder.txtCreditValue.setVisibility(View.VISIBLE);
            holder.txtCreditValue.setText("Credit value : SAR "+ bookinglist.get(position).getBooking_payfort_value());
        }
        if (!bookinglist.get(position).getBooking_coupon_value().equals("0.00")){
            holder.txtCoupon.setVisibility(View.VISIBLE);
            holder.txtCoupon.setText("Coupon value : SAR "+bookinglist.get(position).getBooking_coupon_value());
        }
        if (!bookinglist.get(position).getBooking_wallet_value().equals("0.00")){
            holder.txtWallet.setVisibility(View.VISIBLE);
            holder.txtWallet.setText("Wallet Amount : SAR "+ bookinglist.get(position).getBooking_wallet_value());
        }
        if (!bookinglist.get(position).getBooking_pont_value().equals("0.00")){
            holder.txtPoint.setVisibility(View.VISIBLE);
            holder.txtPoint.setText("Point value : SAR " + bookinglist.get(position).getBooking_pont_value() );
        }

        if (bookinglist.get(position).getBooking_status().equals("0")){
            holder.txtStatus.setText(context.getResources().getString(R.string.failed));
        } else if (bookinglist.get(position).getBooking_status().equals("1")){
            holder.txtStatus.setText(context.getResources().getString(R.string.completed));
        } else if (bookinglist.get(position).getBooking_status().equals("2")){
            holder.txtStatus.setText(context.getResources().getString(R.string.canceled));
        } else {
            holder.txtStatus.setText(context.getResources().getString(R.string.failed));
        }
        holder.txtDropUp.setText(bookinglist.get(position).getBooking_to_location());
        holder.txtPickUp.setText(bookinglist.get(position).getBooking_from_location());
        holder.txtCarName.setText(bookinglist.get(position).getBooking_car_model());
        holder.txtPaymentBy.setText(bookinglist.get(position).getBooking_company_name());
        holder.txtBookingDate.setText("Booking Date: "+ bookinglist.get(position).getBokking_date());
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

        TextView refnumb,date_time,rate,txtjourneyToDate, txtStatus, txtPickUp,txtBookingDate,
                txtDropUp,  txtCancel, txtCarName, txtPaymentBy, txtCreditValue, txtWallet, txtPoint, txtCoupon;
        ImageView imgLogo, car_image;

        public MyViewHolder(final View itemView) {
            super(itemView);

            refnumb= (TextView) itemView.findViewById(R.id.txt_bookingInvoicenumber);
            date_time= (TextView) itemView.findViewById(R.id.txt_date_time);
            rate=(TextView) itemView.findViewById(R.id.txt_rate);
            txtStatus = (TextView) itemView.findViewById(R.id.previous_booking_txtStatus);
            txtPickUp = (TextView) itemView.findViewById(R.id.previous_booking_pickUp);
            txtDropUp = (TextView) itemView.findViewById(R.id.previous_booking_dropUp);
            txtPaymentBy = (TextView) itemView.findViewById(R.id.previous_booking_txtCarName);
            txtCarName = (TextView) itemView.findViewById(R.id.previous_booking_txtPaymentBy);
            imgLogo = (ImageView) itemView.findViewById(R.id.previous_booking_imgCarLogo);
            car_image = (ImageView) itemView.findViewById(R.id.my_booking_image);
            txtCancel = itemView.findViewById(R.id.previous_booking_txtcancel);
            txtBookingDate = itemView.findViewById(R.id.txt_bookingdate);
            txtjourneyToDate = itemView.findViewById(R.id.txt_date_totime);
            txtCoupon = itemView.findViewById(R.id.txtCuoponValue);
            txtCreditValue = itemView.findViewById(R.id.txtCreditValueAmt);
            txtWallet = itemView.findViewById(R.id.txtWalletValueAmt);
            txtPoint = itemView.findViewById(R.id.txtPointValueAmt);
          /*  txtCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                }
            });*/

          if (type.equals("p")){
              txtCancel.setVisibility(View.GONE);
          }
            txtCancel.setOnClickListener(this);
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
