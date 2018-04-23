package com.carshiring.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.carshiring.R;
import com.carshiring.activities.home.PayActivity;
import com.carshiring.activities.home.ThankYou;
import com.carshiring.fragments.SearchCarFragment;
import com.carshiring.models.BookingHistory;
import com.carshiring.models.CancledetailBean;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carshiring.splash.SplashActivity.TAG;
import static com.google.android.gms.internal.zzahg.runOnUiThread;

/**
 * Created by Rakhi
 *
 */

public class MyBookingAdapter extends RecyclerView.Adapter<MyBookingAdapter.MyViewHolder> {
    List<BookingHistory> bookinglist;
    List<CancledetailBean> cancledetailBeans;
    Context context;

    String type;

    public MyBookingAdapter(List<BookingHistory> bookinglist,    List<CancledetailBean> cancledetailBeans,
                            Context context, String type) {
        this.bookinglist = bookinglist;
        this.cancledetailBeans = cancledetailBeans;
        this.context = context;
        this.type = type;
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
        holder.refnumb.setText(bookinglist.get(position).getBooking_id());
        holder.date_time.setText(Utility.convertdate(bookinglist.get(position).getBooking_from_date()));

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
        holder.txtPaymentBy.setText(bookinglist.get(position).getBooking_company_name());


        Glide.with(context).load(bookinglist.get(position).getBooking_supllier_log()).into(holder.imgLogo);
        Glide.with(context).load(bookinglist.get(position).getBooking_car_image()).into(holder.car_image);

        btnCancelationCharge.setTag(position);
        btnBookingCharge.setText("Booking Charges: SAR "+bookinglist.get(position).getBooking_total_price());
        if (bookinglist.get(position).getBooking_status().equals("2")){
            btnCancelationCharge.setVisibility(View.VISIBLE);
            btnCancelationCharge.setText("Cancellation Charges : SAR "+ bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_cancel_charge());

        } else {
            btnCancelationCharge.setVisibility(View.GONE);
        }
        holder.txtPricedetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBookingPop(position);

            }
        });
        if (cancledetailBeans!=null&&cancledetailBeans.size()>0){

            for (int i=0;i<bookinglist.size();i++){
                String bookingid = bookinglist.get(i).getBooking_id();
               /* String can = cancledetailBeans.get(i).getResponse().getCancel_detail()
                        .getBooking_cancel_booking_id();
                btnCancelationCharge.setText( bookingid + "  "+ can);*/

            }

//            btnCancelationCharge.setText("Cancellation  charges: SAR "+cancledetailBeans.get(position)
//                    .getResponse().getCancel_detail().getBooking_cancel_cancel_charge());

        }
        btnCancelationCharge.setTag(position);
        btnCancelationCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCancelationPop(position);
//                setCancelationPop((Integer) btnCancelationCharge.getTag());
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
    TextView txtCoupon, txtCreditValue, txtWallet,txtPriceTitle, txtPoint,txtBookingDate,rate,txtFullProt;
    Button btnCancel;
    Dialog dialog;
    @SuppressLint("SetTextI18n")
    private void setBookingPop(int position){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.price_details_view);
        txtCoupon = dialog.findViewById(R.id.txtCuoponValue);
        txtCreditValue = dialog.findViewById(R.id.txtCreditValueAmt);
        txtWallet = dialog.findViewById(R.id.txtWalletValueAmt);
        txtPoint = dialog.findViewById(R.id.txtPointValueAmt);
        txtFullProt = (TextView) dialog.findViewById(R.id.txtFullProtection);
        btnCancel = dialog.findViewById(R.id.txtCancel);
        txtBookingDate = dialog.findViewById(R.id.txt_bookingdate);
        txtPriceTitle = dialog.findViewById(R.id.price_detail_title);
        txtPriceTitle.setText("Booking Charges ");
        rate = (TextView) dialog.findViewById(R.id.txt_rate);
        if (bookinglist.get(position).getBooking_payfort_value()!=null&&
                !bookinglist.get(position).getBooking_payfort_value().equals("0.00")) {
            txtCreditValue.setVisibility(View.VISIBLE);
            txtCreditValue.setText("Payfort : SAR " + bookinglist.get(position).getBooking_payfort_value());
        }
        txtBookingDate.setText(context.getResources().getString(R.string.txtTransactionDate)+" : "
                + Utility.convertSimpleDate(bookinglist.get(position).getBokking_date()));
        rate.setText(context.getResources().getString(R.string.txtCarPrice)+" : " + bookinglist.get(position).getBooking_currency()
                + " " + bookinglist.get(position).getBooking_actual_price());

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if (bookinglist.get(position).getBooking_coupon_value()!=null&&!bookinglist.get(position).getBooking_coupon_value().equals("0.00")) {
            txtCoupon.setVisibility(View.VISIBLE);
            txtCoupon.setText(context.getResources().getString(R.string.txtDiscount)+" : SAR " + bookinglist.get(position).getBooking_coupon_value());
        }
        if (bookinglist.get(position).getBooking_wallet_value()!=null&&!bookinglist.get(position).getBooking_wallet_value().equals("0.00")) {
            txtWallet.setVisibility(View.VISIBLE);
            txtWallet.setText(context.getResources().getString(R.string.txtWallet)+" : SAR " + bookinglist.get(position).getBooking_wallet_value());
        }
        if (bookinglist.get(position).getBooking_pont_value()!=null&&!bookinglist.get(position).getBooking_pont_value().equals("0.00")) {
            txtPoint.setVisibility(View.VISIBLE);
            txtPoint.setText(context.getResources().getString(R.string.points) + " : SAR " + bookinglist.get(position).getBooking_pont_value());
        }
        if (bookinglist.get(position).getBooking_fullprotection_value() != null
                && !bookinglist.get(position).getBooking_fullprotection_value().equalsIgnoreCase("0.00")) {
            txtFullProt.setVisibility(View.VISIBLE);
            txtFullProt.setText(context.getResources().getString(R.string.full_protection) +" : SAR "+ bookinglist.get(position).getBooking_fullprotection_value());
        }
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void setCancelationPop(int position){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.price_details_view);
        txtCoupon = dialog.findViewById(R.id.txtCuoponValue);
        txtCreditValue = dialog.findViewById(R.id.txtCreditValueAmt);
        txtWallet = dialog.findViewById(R.id.txtWalletValueAmt);
        txtPoint = dialog.findViewById(R.id.txtPointValueAmt);
        txtFullProt = (TextView) dialog.findViewById(R.id.txtFullProtection);
        btnCancel = dialog.findViewById(R.id.txtCancel);
        txtBookingDate = dialog.findViewById(R.id.txt_bookingdate);
        rate = (TextView) dialog.findViewById(R.id.txt_rate);
        txtPriceTitle = dialog.findViewById(R.id.price_detail_title);
        txtPriceTitle.setText("Booking Canceled Charges ");
        txtBookingDate.setVisibility(View.GONE);
        if (bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_booking_id()
                .equalsIgnoreCase(bookinglist.get(position).getBooking_id())){
            if (bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_booking_amount()!=null&&
                    !bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_booking_amount().equals("0.00")) {
                rate.setVisibility(View.VISIBLE);
                rate.setText("Booking Amount : SAR " +
                        bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_booking_amount());
            }

            txtCoupon.setText("Cancellation Charges : SAR "+bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_cancel_charge());
            txtCreditValue.setVisibility(View.VISIBLE);
            txtCreditValue.setText("Refundable Amount : SAR "+bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_refundable_amount());
            txtCoupon.setVisibility(View.VISIBLE);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            if (bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_cancel_charge()
                    !=null) {
                txtWallet.setVisibility(View.VISIBLE);
                txtWallet.setText("Wallet : SAR " +bookinglist.get(position)
                        .getBooking_canceldetail().getBooking_cancel_wallet_amount());
            }
            txtPoint.setVisibility(View.VISIBLE);
            txtPoint.setText("Refundable Points Value : SAR " +String.valueOf(bookinglist.get(position)
                    .getBooking_canceldetail().getBooking_cancel_point_amount()));


        }

        dialog.show();
    }



    @Override
    public int getItemCount() {
        return bookinglist.size();
    }
    Button btnBookingCharge, btnCancelationCharge;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView refnumb, date_time, rate, txtjourneyToDate, txtStatus, txtPickUp, txtBookingDate,
                txtDropUp, txtCancel, txtCarName,txtPricedetails, txtFullProt,txtPaymentBy, txtCreditValue, txtWallet, txtPoint, txtCoupon;
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
            txtPricedetails = itemView.findViewById(R.id.previous_booking_txtPricedetails);
            imgLogo = (ImageView) itemView.findViewById(R.id.previous_booking_imgCarLogo);
            car_image = (ImageView) itemView.findViewById(R.id.my_booking_image);
            txtCancel = itemView.findViewById(R.id.previous_booking_txtcancel);
            txtjourneyToDate = itemView.findViewById(R.id.txt_date_totime);
            txtCoupon = itemView.findViewById(R.id.txtCuoponValue);
            txtCreditValue = itemView.findViewById(R.id.txtCreditValueAmt);
            txtWallet = itemView.findViewById(R.id.txtWalletValueAmt);
            txtPoint = itemView.findViewById(R.id.txtPointValueAmt);
            btnBookingCharge = itemView.findViewById(R.id.btnBookingCharge);
            btnCancelationCharge = itemView.findViewById(R.id.btn_cancelCharge);
          /*  txtCancel.setOnClickListener(new View.OnClickListener() {
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
