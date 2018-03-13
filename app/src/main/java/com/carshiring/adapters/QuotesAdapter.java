package com.carshiring.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.carshiring.R;
import com.carshiring.models.QuotesModel;

import java.util.List;

/**
 * Created by Rakhi on 13-03-2018.
 */

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.Holder> {
    List<QuotesModel> quoteslist;
    Context context;
    QuoteInterface quoteInterface;
    int lastpositon = -1;

    public QuotesAdapter(Context context, List<QuotesModel> quoteslist) {

        this.context = context;
        this.quoteslist = quoteslist;
    }

    public interface QuoteInterface {
        void QuoteInterfaceMethod(View view, int position);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quotes_row, parent, false);
        return (new Holder(v));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        QuotesModel model = quoteslist.get(position);
        holder.Reference.setText(model.getBookingrefNumber());
        holder.Rate.setText(model.getRate());
        holder.Address.setText(model.getAddress());
        // Animation animation= AnimationUtils.loadAnimation(context,(position>lastposition? R.anim.up_from_bottom:R.anim.bottom_from_up));

        /*Animation animation = AnimationUtils.loadAnimation(context, (position > lastposition) ? R.anim.up_from_bottom : R.anim.bottom_from_up);
        holder.itemView.setAnimation(animation);
        lastposition = position;*/
       /* if (position>lastpositon) {
            AnimatioonUtils.animate(holder,true);
        }
        else
        {
            AnimatioonUtils.animate(holder,false);
        }
        lastpositon=position;*/

    }

    @Override
    public int getItemCount() {
        return quoteslist.size();
    }

    public void QuoteAdapterMethod(QuoteInterface quoteInterface) {
        this.quoteInterface = quoteInterface;
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Reference, Rate, Address;

        public Holder(View view) {
            super(view);
            Reference = (TextView) view.findViewById(R.id.txt_Quote_refnumb);
            Rate = (TextView) view.findViewById(R.id.txt_quoterate);
            Address = (TextView) view.findViewById(R.id.txt_quoteaddress);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (quoteInterface != null) {
                quoteInterface.QuoteInterfaceMethod(v, getPosition());
            }
          /*  String name=Reference.getText().toString();
           Intent intent=new Intent(context,QuotedDetailsActivity.class);
            intent.putExtra("ref",name);
            context.startActivity(intent);*/

            // context.startActivity(new Intent(context,QuotedDetailsActivity.class));

            // Toast.makeText(context,""+name,Toast.LENGTH_LONG).show();
        }

    }
}

