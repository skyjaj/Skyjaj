package com.skyjaj.hors.doctor.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.BaseActivity;
import com.skyjaj.hors.activities.MyActivityManager;
import com.skyjaj.hors.utils.ToolbarStyle;
import com.skyjaj.hors.widget.ChangeColorIconWithText;
import com.skyjaj.hors.widget.TabFragment;

import org.androidpn.client.ServiceManager;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends BaseActivity implements ViewPager.OnPageChangeListener,View.OnClickListener {

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private String[] mTitles = new String[]
            { "doctor_index_service", "doctor_index_me"};

    private FragmentPagerAdapter mAdapter;
    //指示器
    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();


    private SharedPreferences sharedPreferences;
    private ServiceManager serviceManager;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mToolbar = ToolbarStyle.initToolbarWithNoBackKey(this, R.id.mToolbar, "医生主页");
        initContentViews();
        initDatas();
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);

    }




    private void initDatas() {
        for (String title : mTitles)
        {
            TabFragment tabFragment = new TabFragment();
            Bundle bundle = new Bundle();
            bundle.putString(TabFragment.TITLE, title);
            tabFragment.setArguments(bundle);
            mTabs.add(tabFragment);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
        {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position)
            {
                return mTabs.get(position);
            }
        };
    }

    private void initContentViews() {

        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        ChangeColorIconWithText one = (ChangeColorIconWithText) findViewById(R.id.id_indicator_one);
        mTabIndicators.add(one);
        ChangeColorIconWithText two = (ChangeColorIconWithText) findViewById(R.id.id_indicator_two);
        mTabIndicators.add(two);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        one.setIconAlpha(1.0f);
    }

    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicators.size(); i++)
        {
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }


    @Override
    public void onClick(View v) {

        resetOtherTabs();

        switch (v.getId())
        {
            case R.id.id_indicator_one:
                mTabIndicators.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_two:
                mTabIndicators.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0)
        {
            ChangeColorIconWithText left = mTabIndicators.get(position);
            ChangeColorIconWithText right = mTabIndicators.get(position + 1);
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    protected void onDestroy() {
        if (serviceManager != null) {
            serviceManager.stopService();
        }
        super.onDestroy();
    }



    //push task
    public class ServiceTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            serviceManager = new ServiceManager(HomePage.this);
            serviceManager.setNotificationIcon(R.drawable.me);
            serviceManager.startService();


            username = sharedPreferences.getString("HORS_USERNAME","username");
            if (!"username".equals(username)) {
                //角色+手机号
                serviceManager.setAlias("doctor" + username);
                List<String> list = new ArrayList<>();
                list.add("sport");
                list.add("music");
                serviceManager.setTags(list);
            }

            return true;
        }
    }

}
