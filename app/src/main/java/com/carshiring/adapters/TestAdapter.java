package com.carshiring.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.carshiring.R;
import com.carshiring.models.BookingHistory;
import com.carshiring.models.CancledetailBean;
import com.carshiring.models.TestClass;
import com.carshiring.utilities.Utility;

import java.util.List;

/**
 * Created by Rakhi
 *
 */

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.MyViewHolder> {
    TestClass bookinglist;
    Context context;

    String type;

    public TestAdapter(TestClass bookinglist,
                       Context context, String type) {
        this.bookinglist = bookinglist;
        this.context = context;
        this.type = type;
        this.dialog = new Dialog(context);
    }

    ClickItem item;

    public interface ClickItem {
        public void click(View view, int Position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.previous_booking_row, parent, false);
        return new MyViewHolder(row);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        //BookingData bookingModel=bookinglist.get(position);
        holder.refnumb.setText(bookinglist.getBookingHistories().get(position).getBooking_id());
        holder.date_time.setText(Utility.convertdate(bookinglist.getBookingHistories().get(position).getBooking_from_date()));

        holder.txtjourneyToDate.setText(Utility.convertdate(bookinglist.getBookingHistories().get(position).getBooking_to_date()));
        holder.rate.setText("Car Price : " + bookinglist.getBookingHistories().get(position).getBooking_currency()
                + " " + bookinglist.getBookingHistories().get(position).getBooking_actual_price());

        if (bookinglist.getBookingHistories().get(position).getBooking_status().equals("0")) {
            holder.txtStatus.setText(context.getResources().getString(R.string.failed));
        } else if (bookinglist.getBookingHistories().get(position).getBooking_status().equals("1")) {
            holder.txtStatus.setText(context.getResources().getString(R.string.completed));
        } else if (bookinglist.getBookingHistories().get(position).getBooking_status().equals("2")) {
            holder.txtStatus.setText(context.getResources().getString(R.string.canceled));
        } else {
            holder.txtStatus.setText(context.getResources().getString(R.string.failed));
        }
        holder.txtDropUp.setText(bookinglist.getBookingHistories().get(position).getBooking_to_location());
        holder.txtPickUp.setText(bookinglist.getBookingHistories().get(position).getBooking_from_location());
        holder.txtCarName.setText(bookinglist.getBookingHistories().get(position).getBooking_car_model());
        holder.txtPaymentBy.setText(bookinglist.getBookingHistories().get(position).getBooking_company_name());

        holder.txtBookingDate.setText(context.getResources().getString(R.string.txtTransactionDate)+" : "
                + Utility.convertSimpleDate(bookinglist.getBookingHistories().get(position).getBokking_date()));
        Glide.with(context).load(bookinglist.getBookingHistories().get(position).getBooking_supllier_log()).into(holder.imgLogo);
        Glide.with(context).load(bookinglist.getBookingHistories().get(position).getBooking_car_image()).into(holder.car_image);

//        btnCancelationCharge.setTag(position);
        btnBookingCharge.setText("Booking Charges: SAR " +
                bookinglist.getBookingHistories().get(position).getBooking_total_price());
        if (type.equalsIgnoreCase("p")){
            btnCancelationCharge.setVisibility(View.VISIBLE);
            btnCancelationCharge.setText("Cancellation Charges: SAR "+
                    bookinglist.getCancledetailBeans().get(position).getResponse().getCancel_detail().
                            getBooking_cancel_cancel_charge()+

            "\n"+bookinglist.getCancledetailBeans().get(position).getResponse().getCancel_detail().getBooking_cancel_booking_id());
        } else {
            btnCancelationCharge.setVisibility(View.GONE);
        }
        btnBookingCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBookingPop(position);
            }
        });
        btnCancelationCharge.setTag(position);
        btnCancelationCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCancelationPop(position);
            }
        });

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


    public void submit(ClickItem item) {
        this.item = item;
    }
    TextView txtCoupon, txtCreditValue, txtWallet,txtPoint,txtFullProt;
    Button btnCancel;
    Dialog dialog;
    @SuppressLint("SetTextI18n")
    private void setBookingPop(int position){
        dialog.setContentView(R.layout.price_details_view);
        txtCoupon = dialog.findViewById(R.id.txtCuoponValue);
        txtCreditValue = dialog.findViewById(R.id.txtCreditValueAmt);
        txtWallet = dialog.findViewById(R.id.txtWalletValueAmt);
        txtPoint = dialog.findViewById(R.id.txtPointValueAmt);
        txtFullProt = (TextView) dialog.findViewById(R.id.txtFullProtection);
        btnCancel = dialog.findViewById(R.id.txtCancel);
        if (bookinglist.getBookingHistories().get(position).getBooking_payfort_value()!=null&&
                !bookinglist.getBookingHistories().get(position).getBooking_payfort_value().equals("0.00")) {
            txtCreditValue.setVisibility(View.VISIBLE);
            txtCreditValue.setText("Payfort : SAR " + bookinglist.getBookingHistories().get(position).getBooking_payfort_value());
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if (bookinglist.getBookingHistories().get(position).getBooking_coupon_value()!=null&&!bookinglist.getBookingHistories().get(position).getBooking_coupon_value().equals("0.00")) {
            txtCoupon.setVisibility(View.VISIBLE);
            txtCoupon.setText("Discount : SAR " + bookinglist.getBookingHistories().get(position).getBooking_coupon_value());
        }
        if (bookinglist.getBookingHistories().get(position).getBooking_wallet_value()!=null&&!bookinglist.getBookingHistories().get(position).getBooking_wallet_value().equals("0.00")) {
            txtWallet.setVisibility(View.VISIBLE);
            txtWallet.setText("Wallet : SAR " + bookinglist.getBookingHistories().get(position).getBooking_wallet_value());
        }
        if (bookinglist.getBookingHistories().get(position).getBooking_pont_value()!=null&&!bookinglist.getBookingHistories().get(position).getBooking_pont_value().equals("0.00")) {
            txtPoint.setVisibility(View.VISIBLE);
            txtPoint.setText(context.getResources().getString(R.string.points) + " : SAR " + bookinglist.getBookingHistories().get(position).getBooking_pont_value());
        }
        if (bookinglist.getBookingHistories().get(position).getBooking_fullprotection_value() != null
                && !bookinglist.getBookingHistories().get(position).getBooking_fullprotection_value().equalsIgnoreCase("0.00")) {
            txtFullProt.setVisibility(View.VISIBLE);
            txtFullProt.setText(context.getResources().getString(R.string.full_protection) +" : SAR "+ bookinglist.getBookingHistories().get(position).getBooking_fullprotection_value());
        }
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void setCancelationPop(int position){
        dialog.setContentView(R.layout.price_details_view);
        txtCoupon = dialog.findViewById(R.id.txtCuoponValue);
        txtCreditValue = dialog.findViewById(R.id.txtCreditValueAmt);
        txtWallet = dialog.findViewById(R.id.txtWalletValueAmt);
        txtPoint = dialog.findViewById(R.id.txtPointValueAmt);
        txtFullProt = (TextView) dialog.findViewById(R.id.txtFullProtection);
        btnCancel = dialog.findViewById(R.id.txtCancel);
        if (bookinglist.getCancledetailBeans().get(position).getResponse().getCancel_detail().getBooking_cancel_booking_amount()!=null&&
                !bookinglist.getCancledetailBeans().get(position).getResponse().getCancel_detail().getBooking_cancel_booking_amount().equals("0.00")) {
            txtCreditValue.setVisibility(View.VISIBLE);
            txtCreditValue.setText("Booking Amount : SAR " +bookinglist.getCancledetailBeans().get(position).getResponse()
                    .getCancel_detail().getBooking_cancel_booking_amount());
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if (bookinglist.getCancledetailBeans().get(position).getResponse().getCancel_detail().getBooking_cancel_cancel_charge()
                !=null) {
            txtWallet.setVisibility(View.VISIBLE);
            txtWallet.setText("Cancellation Charges : SAR " + bookinglist.getCancledetailBeans().get(position).getResponse().getCancel_detail().getBooking_cancel_cancel_charge());
        }
        txtPoint.setVisibility(View.VISIBLE);
        txtPoint.setText("Refundable Amount : SAR " +String.valueOf(bookinglist.getCancledetailBeans().get(position).getResponse().getCancel_detail().getBooking_cancel_cancel_charge()));

        dialog.show();
    }



    @Override
    public int getItemCount() {
        return bookinglist.getBookingHistories().size();
    }
    Button btnBookingCharge, btnCancelationCharge;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView refnumb, date_time, rate, txtjourneyToDate, txtStatus, txtPickUp, txtBookingDate,
                txtDropUp, txtCancel, txtCarName, txtFullProt,txtPaymentBy, txtCreditValue, txtWallet, txtPoint, txtCoupon;
        ImageView imgLogo, car_image;

        public MyViewHolder(final View itemView) {
            super(itemView);

            refnumb = (TextView) itemView.findViewById(R.id.txt_bookingInvoicenumber);
            date_time = (TextView) itemView.findViewById(R.id.txt_date_time);
            rate = (TextView) itemView.findViewById(R.id.txt_rate);
            txtStatus = (TextView) itemView.findViewById(R.id.previous_booking_txtStatus);
            txtPickUp = (TextView) itemView.findViewById(R.id.previous_booking_pickUp);
            txtDropUp = (TextView) itemView.findViewById(R.id.previous_booking_dropUp);
            txtPaymentBy = (TextView) itemView.findViewById(R.id.previous_booking_txtCarName);
            txtFullProt = (TextView) itemView.findViewById(R.id.txtFullProtection);
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
            btnBookingCharge = itemView.findViewById(R.id.btnBookingCharge);
            btnCancelationCharge = itemView.findViewById(R.id.btn_cancelCharge);
          /*  txtCancel.setOnClickListener(new View.OnClickListener() {`
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                }
            });*/

            if (type.equals("p")) {
                txtCancel.setVisibility(View.GONE);
            }
            txtCancel.setOnClickListener(this);/*
            btnCancelationCharge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnCancelationCharge.findViewWithTag(getAdapterPosition()))

                    cancelDetail(bookinglist.get(position).getBooking_id());

                }
            });*/

        }

        @Override
        public void onClick(View v) {
            if (item != null) {
                item.click(v, getPosition());

            }
        }
    }


}
