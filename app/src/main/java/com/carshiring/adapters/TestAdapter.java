package com.carshiring.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.carshiring.R;
import com.carshiring.fragments.SearchCarFragment;
import com.carshiring.models.BookingHistory;
import com.carshiring.models.Category;
import com.carshiring.models.SearchData;
import com.carshiring.utilities.Utility;
import com.carshiring.webservices.ApiResponse;
import com.carshiring.webservices.RetroFitApis;
import com.carshiring.webservices.RetrofitApiBuilder;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carshiring.splash.SplashActivity.TAG;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class TestAdapter extends RecyclerView.Adapter<TestAdapter.MyViewHolder> {


    private Dialog dialog;
    String type, payfortValue,pointvalue, walltevalue, bookingid;
    public TestAdapter(Activity context, List<BookingHistory> bookinglist, String type) {
        this.context = context;
        this.bookinglist = bookinglist;
        this.type = type;
    }
    ClickItem item;

    public interface ClickItem {
         void click(View view, int Position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView refnumb, date_time, rate, txtjourneyToDate, txtStatus, txtOneway, txtPickUp, txtBookingDate,
                txtDropUp, txtCancel, txtCarName, txtPricedetails, txtFullProt, txtPaymentBy, txtCreditValue, txtWallet, txtPoint, txtCoupon;
        ImageView imgLogo, car_image;
        Button btnBookingCharge, btnCancelationCharge;

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
            btnBookingCharge = itemView.findViewById(R.id.btnBookingCharge);
            btnCancelationCharge = itemView.findViewById(R.id.btn_cancelCharge);
            txtPricedetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setBookingPop(getAdapterPosition());
                }
            });

            btnCancelationCharge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCancelationPop(getAdapterPosition());
                }
            });
            if (type.equals("p")) {
                txtCancel.setVisibility(View.GONE);
            }
            txtCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCancelation(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View view) {
/*
            switch (view.getId()){
                case R.id.previous_booking_txtcancel:
                    if (item != null) {
                        item.click(view, getAdapterPosition());
                    }
                    break;
            }
*/

        }
    }
    private TinyDB tinyDB;

    public void submit(ClickItem item) {
        this.item = item;
    }
    String accountType="1",language;// 1=wallet, 2 account
    private void setCancelation(int position){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.account_popup_view);
        tinyDB = new TinyDB(context);
        language = tinyDB.getString("language_code");
        bookingid=bookinglist.get(position).getBooking_id();
        TextView txtSelect = (TextView) dialog.findViewById(R.id.tctSelect);
        if (bookinglist.get(position).getBooking_payfort_value()!=null&&
                bookinglist.get(position).getBooking_payfort_value().equals("0.00")) {
            payfortValue= bookinglist.get(position).getBooking_payfort_value();
        } else {
            payfortValue= bookinglist.get(position).getBooking_payfort_value();
        }
        if (bookinglist.get(position).getBooking_wallet_value()!=null&&
                bookinglist.get(position).getBooking_wallet_value().equals("0.00")) {
            walltevalue= bookinglist.get(position).getBooking_wallet_value();
        } else {
            walltevalue= bookinglist.get(position).getBooking_wallet_value();
        }
        if (bookinglist.get(position).getBooking_pont_value()!=null&&
                bookinglist.get(position).getBooking_pont_value().equals("0.00")) {
            pointvalue= bookinglist.get(position).getBooking_pont_value();
        } else {
            pointvalue= bookinglist.get(position).getBooking_pont_value();

        }
        final RadioButton walletRadioButton= dialog.findViewById(R.id.radio_wallet);
        final RadioButton accountRadioButton= dialog.findViewById(R.id.radio_account);
        final TextView txtSubmit= dialog.findViewById(R.id.account_btnsubit);
        TextView txtCancel = dialog.findViewById(R.id.account_btnCancel);
        dialog.show();
        if (payfortValue!=null&&walltevalue!=null&&pointvalue!=null){
            if (payfortValue.equals("0.00")&&walltevalue.equals("0.00")&&pointvalue.equals("0.00")) {
                accountRadioButton.setVisibility(View.GONE);
                walletRadioButton.setVisibility(View.GONE);
                txtSelect.setText(context.getResources().getString(R.string.txtCancelBooking));
            } else {
                if (payfortValue.equals("0.00")){
                    accountRadioButton.setVisibility(View.GONE);
                }
            }
        }

        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (accountType!=null&&accountType.length()>0){
                    cancelBooking(accountType, bookingid);
                    dialog.dismiss();
                }
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        accountRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((RadioButton) view).isChecked();
                if (checked){
                    accountType = "2";
                }
            }
        });

        walletRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((RadioButton) view).isChecked();

                if (checked){
                    accountType="1";
                }
            }
        });
    }
    private Gson gson = new Gson();
    private void cancelBooking(String type,String bookingid){
        Utility.showloadingPopup(context);
        RetroFitApis fitApis= RetrofitApiBuilder.getCargHiresapis();
        final Call<ApiResponse> bookingCancel = fitApis.cancelBooking(language,type,bookingid);

        bookingCancel.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                dialog.dismiss();
                Utility.hidepopup();
                Log.d(TAG, "onResponse: cancel"+gson.toJson(response.body()));
                if (response!=null){
                    if (response.body().status){
                        Toast.makeText(context, ""+response.body().msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, ""+context.getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Utility.hidepopup();
                dialog.dismiss();
                Log.d(TAG, "onFailure: "+t.getMessage());
                Toast.makeText(context, ""+context.getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });

    }


    TextView txtCoupon, txtCreditValue, txtWallet, txtPriceTitle, txtPoint, txtBookingDate, rate, txtFullProt;
    Button btnCancel;
    private void setBookingPop(int position) {
        Log.d("data:", bookinglist.get(position).getBooking_actual_price());
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
        txtPriceTitle.setText(context.getResources().getString(R.string.txtBookingCharge));
        rate = (TextView) dialog.findViewById(R.id.txt_rate);
        if (bookinglist.get(position).getBooking_payfort_value() != null &&
                !bookinglist.get(position).getBooking_payfort_value().equals("0.00")) {
            txtCreditValue.setVisibility(View.VISIBLE);
            txtCreditValue.setText(context.getResources().getString(R.string.txtPaid)+" : SAR "
                    + bookinglist.get(position).getBooking_payfort_value());
        }
        txtBookingDate.setText(context.getResources().getString(R.string.txtTransactionDate) + " : "
                + Utility.convertSimpleDate(bookinglist.get(position).getBokking_date()));
        rate.setText(context.getResources().getString(R.string.txtCarPrice) + " : "
                + bookinglist.get(position).getBooking_currency()
                + " " + bookinglist.get(position).getBooking_actual_price());

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if (bookinglist.get(position).getBooking_coupon_value() != null && !bookinglist.get(position).getBooking_coupon_value().equals("0.00")) {
            txtCoupon.setVisibility(View.VISIBLE);
            txtCoupon.setText(context.getResources().getString(R.string.txtDiscount) + " : SAR " + bookinglist.get(position).getBooking_coupon_value());
        }
        if (bookinglist.get(position).getBooking_wallet_value() != null && !bookinglist.get(position).getBooking_wallet_value().equals("0.00")) {
            txtWallet.setVisibility(View.VISIBLE);
            txtWallet.setText(context.getResources().getString(R.string.txtWal) + " : SAR " + bookinglist.get(position).getBooking_wallet_value());
        }
        if (bookinglist.get(position).getBooking_pont_value() != null && !bookinglist.get(position).getBooking_pont_value().equals("0.00")) {
            txtPoint.setVisibility(View.VISIBLE);
            txtPoint.setText(context.getResources().getString(R.string.points) + " : SAR " + bookinglist.get(position).getBooking_pont_value());
        }
        if (bookinglist.get(position).getBooking_fullprotection_value() != null
                && !bookinglist.get(position).getBooking_fullprotection_value().equalsIgnoreCase("0.00")) {
            txtFullProt.setVisibility(View.VISIBLE);
            txtFullProt.setText(context.getResources().getString(R.string.txtFullPro) + " : SAR " + bookinglist.get(position).getBooking_fullprotection_value());
        }
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void setCancelationPop(int position) {
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
        txtPriceTitle.setText(context.getResources().getString(R.string.txtBookingCancel));
        txtBookingDate.setVisibility(View.GONE);
        if (bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_booking_id()
                .equalsIgnoreCase(bookinglist.get(position).getBooking_id())) {
            if (bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_booking_amount() != null &&
                    !bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_booking_amount().equals("0.00")) {
                rate.setVisibility(View.VISIBLE);
                rate.setText(context.getResources().getString(R.string.txtBookingAmt)+" : SAR " +
                        bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_booking_amount());
            }

            txtFullProt.setText(context.getResources().getString(R.string.txtRefun)+" : SAR " + bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_cancel_charge());
            txtCoupon.setVisibility(View.VISIBLE);
            txtCoupon.setText(context.getResources().getString(R.string.txtWal)+" : SAR " + bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_refundable_amount());
            txtFullProt.setVisibility(View.VISIBLE);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            if (bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_wallet_amount()
                    != null && !bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_wallet_amount().equalsIgnoreCase("0.00")) {
                txtWallet.setVisibility(View.VISIBLE);
                txtWallet.setText(context.getResources().getString(R.string.txtWal)+" : SAR " + bookinglist.get(position)
                        .getBooking_canceldetail().getBooking_cancel_wallet_amount());
            } else {
                txtWallet.setVisibility(View.GONE);
            }
            if (bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_credit_amount() != null &&
                    !bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_credit_amount().equalsIgnoreCase("0.00")) {
                txtCreditValue.setVisibility(View.VISIBLE);
                txtCreditValue.setText(context.getResources().getString(R.string.txtRefundBank)
                        +" : SAR " + bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_credit_amount());
            }
            if (bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_point_amount() != null
                    && !bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_point_amount().equalsIgnoreCase("0.00")) {
                txtPoint.setVisibility(View.VISIBLE);
                txtPoint.setText(context.getResources().getString(R.string.txtRefundPoint)+" : SAR " + String.valueOf(bookinglist.get(position)
                        .getBooking_canceldetail().getBooking_cancel_point_amount()));
            } else {
                txtPoint.setVisibility(View.GONE);
            }
        }
        dialog.show();
    }

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
        holder.txtPaymentBy.setText(bookinglist.get(position).getBooking_company_name());

        Glide.with(context).load(bookinglist.get(position).getBooking_supllier_log()).into(holder.imgLogo);
        Glide.with(context).load(bookinglist.get(position).getBooking_car_image()).into(holder.car_image);

        holder.btnBookingCharge.setText("Booking Charges: SAR " + bookinglist.get(position).getBooking_total_price());
        if (bookinglist.get(position).getBooking_status().equals("2")) {
            holder.btnCancelationCharge.setVisibility(View.VISIBLE);
            holder.btnCancelationCharge.setText("Cancellation Charges : SAR " + bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_cancel_charge());

        } else {
            holder.btnCancelationCharge.setVisibility(View.GONE);
        }
        holder.btnBookingCharge.setText("Booking Charges: SAR " + bookinglist.get(position).getBooking_total_price());
        if (bookinglist.get(position).getBooking_status().equals("2")) {
            holder.btnCancelationCharge.setVisibility(View.VISIBLE);
            holder.btnCancelationCharge.setText("Cancellation Charges : SAR " + bookinglist.get(position).getBooking_canceldetail().getBooking_cancel_cancel_charge());

        } else {
            holder.btnCancelationCharge.setVisibility(View.GONE);
        }
    }

    List<BookingHistory>  bookinglist;
    @Override
    public int getItemCount() {
        return bookinglist.size();
    }
}