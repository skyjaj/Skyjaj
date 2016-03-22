package com.skyjaj.hors.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.skyjaj.hors.R;
import com.skyjaj.hors.utils.ToolbarStyle;
import com.skyjaj.hors.weixin.wxapi.WXEntryActivity;
import com.skyjaj.hors.widget.ChangeColorIconWithText;
import com.skyjaj.hors.widget.TabFragment;

import org.androidpn.client.ServiceManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class IndexCommonActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,View.OnClickListener,View.OnTouchListener{





    private ServiceTask mTask;
    private String username;
    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private String[] mTitles = new String[]
            { "index_service", "index_found", "index_me"};

    private FragmentPagerAdapter mAdapter;
    //指示器
    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();

    Toolbar mToolbar = null;


    private SharedPreferences sharedPreferences;
    private ServiceManager serviceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_common);
        MyActivityManager.getInstance().addActivity(this);
        initToolbar();
        initContentViews();
        initDatas();
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
        sharedPreferences = this.getSharedPreferences("horsUserInfo", MODE_PRIVATE);


        mTask = new ServiceTask();
        mTask.execute();
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
        ChangeColorIconWithText three = (ChangeColorIconWithText) findViewById(R.id.id_indicator_three);
        mTabIndicators.add(three);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        one.setIconAlpha(1.0f);

    }





    private void initToolbar() {
        mToolbar = ToolbarStyle.initToolbarWithNoBackKey(this, R.id.index_toolbar, "hors");
        mToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.actionbar_add_icon));
        setSupportActionBar(mToolbar);
    }






    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if(mToolbar.canShowOverflowMenu()){
                mToolbar.showOverflowMenu();
            }else{
                mToolbar.hideOverflowMenu();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.index_menu, menu);
        //通过反射显示菜单的图标
        if (MenuBuilder.class.isInstance(menu)) {
            MenuBuilder builder = (MenuBuilder) menu;
            builder.setShortcutsVisible(true);
            Method method = null;
            try {
                method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                method.setAccessible(true);
                method.invoke(menu, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id= item.getItemId();

        switch (id){

            case R.id.action_addfriend:

                Toast.makeText(this,"action addfriend ",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_feedback:
                Toast.makeText(this,"action feedback ",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_scan:
                Toast.makeText(this,"action scan ",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_gruop_chat:
                Intent intent = new Intent(this, WXEntryActivity.class);
                startActivity(intent);
                Toast.makeText(this,"action action gruop chat ",Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);

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
    public void onClick(View v) {
        clickTab(v);
    }

    private void clickTab(View v)
    {
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
                case R.id.id_indicator_three:
                    mTabIndicators.get(2).setIconAlpha(1.0f);
                    mViewPager.setCurrentItem(2, false);
                    break;
        }
    }

    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicators.size(); i++)
        {
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
             home.addCategory(Intent.CATEGORY_HOME);
             startActivity(home);
            //返回true表示已处理该事件，不再传递
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onStop() {
        Log.i("skyjaj", "act on stop ..");
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        Log.i("skyjaj", "on destroy ");
        if (serviceManager != null) {
            serviceManager.stopService();
        }
        MyActivityManager.getInstance().remove(this);
        super.onDestroy();
    }



    public class ServiceTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            serviceManager = new ServiceManager(IndexCommonActivity.this);
            serviceManager.setNotificationIcon(R.drawable.me);
            serviceManager.startService();


            username = sharedPreferences.getString("HORS_USERNAME","username");
            if (!"username".equals(username)) {
                //角色+手机号
                serviceManager.setAlias("patient" + username);
                List<String> list = new ArrayList<>();
                list.add("sport");
                list.add("music");
                serviceManager.setTags(list);
            }

            return true;
        }
    }



}
