package com.carshiring.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.carshiring.R;
import com.carshiring.activities.home.QuotedDetailsActivity;
import com.carshiring.adapters.QuotesAdapter;
import com.carshiring.models.QuotesModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rakhi on 13-03-2018.
 */

public class QuotesFragment extends Fragment implements QuotesAdapter.QuoteInterface {
    View view;
    LinearLayout ll_quote;
    RecyclerView recyclerView;
    QuotesAdapter quotesAdapter;
    List<QuotesModel> quotesModelList=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_quotes,container,false);
       /* ll_quote= (LinearLayout) view.findViewById(R.id.qlinear);
        ll_quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getActivity(),QuotedDetailsActivity.class));
            }
        });*/
       recyclerView= (RecyclerView) view.findViewById(R.id.recy_quotes);
        quotesAdapter=new QuotesAdapter(getContext(),quotesModelList);
        quotesAdapter.QuoteAdapterMethod(this);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(quotesAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        preaparedata();
        return view;
    }

    private void preaparedata() {
        for (int index=0;index<5;index++) {
            QuotesModel model = new QuotesModel("654321", "INR 2,500", "New Delhi");
            quotesModelList.add(model);
            model = new QuotesModel("754321", "INR 3,500", "Noida");
            quotesModelList.add(model);
            model = new QuotesModel("876321", "INR 4,500", "Srinagar");
            quotesModelList.add(model);
            quotesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void QuoteInterfaceMethod(View view, int position) {
        Toast.makeText(getContext(),""+position,Toast.LENGTH_LONG).show();
        String ReferenceNumber=((TextView)view.findViewById(R.id.txt_Quote_refnumb)).getText().toString();
        Intent intent=new Intent(getActivity(),QuotedDetailsActivity.class);
        intent.putExtra("ref",ReferenceNumber);
        startActivity(intent);
    }
}
