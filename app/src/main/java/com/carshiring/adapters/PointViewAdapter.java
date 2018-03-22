package com.carshiring.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.carshiring.R;
import com.carshiring.models.PointHistoryData;
import com.carshiring.models.WalletHistoryData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muhib.
 * Contact Number : +91 9796173066
 */
public class PointViewAdapter extends RecyclerView.Adapter<PointViewAdapter.MyViewHolder> {

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_booking_date, tv_booking_type, tv_booking_amount, tv_booking_id, tv_booking_point;

        public MyViewHolder(View view) {
            super(view);

            tv_booking_id = (TextView) view.findViewById(R.id.layout_wallet_view_booking_id);
            tv_booking_amount = (TextView) view.findViewById(R.id.layout_wallet_view_booking_amount);
            tv_booking_date = (TextView) view.findViewById(R.id.layout_wallet_view_booking_date);
            tv_booking_point = (TextView) view.findViewById(R.id.layout_wallet_view_booking_point);
            tv_booking_type = (TextView) view.findViewById(R.id.layout_wallet_view_booking_type);

        }
    }


    Context context;
    List<PointHistoryData> pointHistoryData = new ArrayList<>();

    public PointViewAdapter(Context context, List<PointHistoryData> pointHistoryData) {
        this.context = context;
        this.pointHistoryData = pointHistoryData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_wallet_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.tv_booking_id.setText(pointHistoryData.get(position).getBooking_id());
        holder.tv_booking_amount.setText(pointHistoryData.get(position).get_$BookingPoint18());
        holder.tv_booking_date.setText(pointHistoryData.get(position).getBokking_date());
        holder.tv_booking_type.setText(pointHistoryData.get(position).get_$BookingPointType184());
        holder.tv_booking_point.setText(pointHistoryData.get(position).get_$BookingAffetctedPoint183());
    }

    @Override
    public int getItemCount() {
        return pointHistoryData.size();
    }
}