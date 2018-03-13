package com.carshiring.activities.home;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.carshiring.R;
import com.carshiring.adapters.Page_Adapter;
import com.carshiring.fragments.CurrentBookingFragment;
import com.carshiring.fragments.DriversFragment;
import com.carshiring.fragments.PreviousBookingFragment;
import com.carshiring.fragments.QuotesFragment;
import com.carshiring.utilities.AppBaseActivity;

import java.util.ArrayList;
import java.util.List;

public class MyBookingActivity extends AppBaseActivity {
    ViewPager pager;
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);
        actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }

        TabLayout tabLayout= (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Current"));
        tabLayout.addTab(tabLayout.newTab().setText("Previous"));
        tabLayout.addTab(tabLayout.newTab().setText("Quotes"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        pager= (ViewPager) findViewById(R.id.viewpager_view);
        setupViewPager(pager);
        tabLayout.setupWithViewPager(pager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CurrentBookingFragment(), getResources().getString(R.string.txtCurrent));
        adapter.addFragment(new PreviousBookingFragment(),  getResources().getString(R.string.txtPrevious));
        adapter.addFragment(new QuotesFragment(),  getResources().getString(R.string.txtQuotes));
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actionBar.setTitle(R.string.txtmybooking);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
