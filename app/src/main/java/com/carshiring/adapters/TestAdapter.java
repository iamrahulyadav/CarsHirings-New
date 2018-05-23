package com.carshiring.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.carshiring.R;
import com.carshiring.fragments.SearchCarFragment;
import com.carshiring.models.BookingHistory;
import com.carshiring.models.Category;
import com.carshiring.models.SearchData;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class TestAdapter extends RecyclerView.Adapter<TestAdapter.MyViewHolder> {


    public TestAdapter(Activity context, List<BookingHistory> bookinglist) {
        this.context = context;
        this.bookinglist = bookinglist;
    }

    public interface  OnItemClickListenerCategory {
      //  void onItemClickCategory(int position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView refnumb, date_time, rate, txtjourneyToDate, txtStatus, txtOneway, txtPickUp, txtBookingDate,
                txtDropUp, txtCancel, txtCarName, txtPricedetails, txtFullProt, txtPaymentBy, txtCreditValue, txtWallet, txtPoint, txtCoupon;
        ImageView imgLogo, car_image;

        public MyViewHolder(View view) {
            super(view);
            refnumb = (TextView) itemView.findViewById(R.id.txt_bookingInvoicenumber);
            date_time = (TextView) itemView.findViewById(R.id.txt_date_time);
            rate = (TextView) itemView.findViewById(R.id.txt_rate);
            txtStatus = (TextView) itemView.findViewById(R.id.previous_booking_txtStatus);
            txtPickUp = (TextView) itemView.findViewById(R.id.previous_booking_pickUp);
            txtDropUp = (TextView) itemView.findViewById(R.id.previous_booking_dropUp);
            txtPaymentBy = (TextView) itemView.findViewById(R.id.previous_booking_txtCarName);
            txtFullProt = (TextView) itemView.findViewById(R.id.txtFullProtection);
            txtCarName = (TextView) itemView.findViewById(R.id.previous_booking_txtPaymentBy);
            txtPricedetails = itemView.findViewById(R.id.previous_booking_txtPricedetails);
            imgLogo = (ImageView) itemView.findViewById(R.id.previous_booking_imgCarLogo);
            car_image = (ImageView) itemView.findViewById(R.id.my_booking_image);
            txtCancel = itemView.findViewById(R.id.previous_booking_txtcancel);
            txtjourneyToDate = itemView.findViewById(R.id.txt_date_totime);
            txtCoupon = itemView.findViewById(R.id.txtCuoponValue);
            txtCreditValue = itemView.findViewById(R.id.txtCreditValueAmt);
            txtWallet = itemView.findViewById(R.id.txtWalletValueAmt);
            txtPoint = itemView.findViewById(R.id.txtPointValueAmt);
//            btnBookingCharge = itemView.findViewById(R.id.btnBookingCharge);
//            btnCancelationCharge = itemView.findViewById(R.id.btn_cancelCharge);
            txtPricedetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,bookinglist.get(getAdapterPosition()).getBooking_id(),Toast.LENGTH_SHORT).show();

                }
            });

        }



    }

    int row_index=-1;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.previous_booking_row, parent, false);

        return new MyViewHolder(itemView);
    }
  Activity context;
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        //BookingData bookingModel=bookinglist.get(position);
        holder.refnumb.setText(bookinglist.get(position).getBooking_id());
        holder.date_time.setText(Utility.convertdate(bookinglist.get(position).getBooking_from_date()));
        Log.d("datarow", bookinglist.get(position).getBooking_actual_price());
        holder.txtjourneyToDate.setText(Utility.convertdate(bookinglist.get(position).getBooking_to_date()));

        if (bookinglist.get(position).getBooking_status().equals("0")) {
            holder.txtStatus.setText(context.getResources().getString(R.string.failed));
        } else if (bookinglist.get(position).getBooking_status().equals("1")) {
            holder.txtStatus.setText(context.getResources().getString(R.string.completed));
        } else if (bookinglist.get(position).getBooking_status().equals("2")) {
            holder.txtStatus.setText(context.getResources().getString(R.string.canceled));
        } else {
            holder.txtStatus.setText(context.getResources().getString(R.string.failed));
        }
        holder.txtDropUp.setText(bookinglist.get(position).getBooking_to_location());
        holder.txtPickUp.setText(bookinglist.get(position).getBooking_from_location());
        holder.txtCarName.setText(bookinglist.get(position).getBooking_car_model());
        Log.d("TAG", "onBindViewHolder: datacan"+bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_cancel_charge()+"\n"+bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_refundable_amount());
        holder.txtPaymentBy.setText(bookinglist.get(position).getBooking_company_name()+","+"Booking Charges: SAR "
                + bookinglist.get(position).getBooking_total_price()+
                " C Charges: SAR " + bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_cancel_charge()+
        ","+"R Charges: SAR " + bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_refundable_amount());

        Glide.with(context).load(bookinglist.get(position).getBooking_supllier_log()).into(holder.imgLogo);
        Glide.with(context).load(bookinglist.get(position).getBooking_car_image()).into(holder.car_image);

        

//        btnCancelationCharge.setTag(position);
//        btnBookingCharge.setText("Booking Charges: SAR " + bookinglist.get(position).getBooking_total_price());
//        if (bookinglist.get(position).getBooking_status().equals("2")) {
//            btnCancelationCharge.setVisibility(View.VISIBLE);
//            btnCancelationCharge.setText("Cancellation Charges : SAR " + bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_cancel_charge());
//
//        } else {
//            btnCancelationCharge.setVisibility(View.GONE);
//        }
//
//        /*holder.txtPricedetails.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setBookingPop(position);
//            }
//        });*/
//        if (cancledetailBeans != null && cancledetailBeans.size() > 0) {
//
//            for (int i = 0; i < bookinglist.size(); i++) {
//                String bookingid = bookinglist.get(i).getBooking_id();
//               /* String can = cancledetailBeans.get(i).getResponse().getCancel_detail()
//                        .getBooking_cancel_booking_id();
//                btnCancelationCharge.setText( bookingid + "  "+ can);*/
//            }
//
////            btnCancelationCharge.setText("Cancellation  charges: SAR "+cancledetailBeans.get(position)
////                    .getResponse().getCancel_detail().getBooking_cancel_cancel_charge());
//        }
//        btnCancelationCharge.setTag(position);
//        btnCancelationCharge.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setCancelationPop(position);
////                setCancelationPop((Integer) btnCancelationCharge.getTag());
//            }
//        });

     /*   holder.btnCancelationCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });*/
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
List<BookingHistory>  bookinglist;
    @Override
    public int getItemCount() {
        return bookinglist.size();
    }
}