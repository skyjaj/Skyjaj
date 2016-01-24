package com.skyjaj.hors.activities;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.BaseMenuPresenter;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.TintManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.skyjaj.hors.R;
import com.skyjaj.hors.index.widget.IndexView;
import com.skyjaj.hors.widget.ChangeColorIconWithText;
import com.skyjaj.hors.widget.IndexContentFragment;
import com.skyjaj.hors.widget.IndexLeftMenuFragment;
import com.skyjaj.hors.widget.TabFragment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class IndexCommonActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,View.OnClickListener{

/*    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private DrawerLayout mDrawerLayout;


    private IndexLeftMenuFragment mLeftMenuFragment;
    private IndexContentFragment mCurrentFragment;
    private FragmentManager fm=null;*/

//    private String mTitle;
//    private static final String TAG = "com.skyjaj.hors";
//    private static final String KEY_TITLLE = "key_title";



    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private String[] mTitles = new String[]
            { "index_service", "index_found", "index_me"};

    private FragmentPagerAdapter mAdapter;
    //指示器
    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();

    Toolbar mToolbar = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_common);

        initToolbar();
        initContentViews();
        initDatas();
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(this);
        //initViews();
        //初始化默认标题
        //restoreTitle(savedInstanceState);
        //初始化fragments
       // initFragments();

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
//        ChangeColorIconWithText four = (ChangeColorIconWithText) findViewById(R.id.id_indicator_four);
//        mTabIndicators.add(four);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
//        four.setOnClickListener(this);
        one.setIconAlpha(1.0f);
    }


/*    private void initFragments() {
        fm = getSupportFragmentManager();
        //查找当前显示的Fragment
        mCurrentFragment = (IndexContentFragment) fm.findFragmentByTag(mTitle);

        if (mCurrentFragment == null) {
            mCurrentFragment = IndexContentFragment.newInstance(mTitle);
            fm.beginTransaction().add(R.id.index_content_container, mCurrentFragment, mTitle).commit();
        }

        mLeftMenuFragment = (IndexLeftMenuFragment) fm.findFragmentById(R.id.index_left_menu_container);
        if (mLeftMenuFragment == null) {
            mLeftMenuFragment = new IndexLeftMenuFragment();
            fm.beginTransaction().add(R.id.index_left_menu_container, mLeftMenuFragment).commit();
        }

        //隐藏别的Fragment，如果存在的话
        List<Fragment> fragments = fm.getFragments();
        if (fragments != null)
            for (Fragment fragment : fragments) {
                if (fragment == mCurrentFragment || fragment == mLeftMenuFragment) continue;
                fm.beginTransaction().hide(fragment).commit();
            }

        //设置MenuItem的选择回调
        mLeftMenuFragment.setOnMenuItemSelectedListener(new IndexLeftMenuFragment.OnMenuItemSelectedListener() {
            @Override
            public void menuItemSelected(String title) {

//                FragmentManager fm = getSupportFragmentManager();
                IndexContentFragment fragment = (IndexContentFragment) getSupportFragmentManager().findFragmentByTag(title);
                if (fragment == mCurrentFragment) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    return;
                }

                FragmentTransaction transaction = fm.beginTransaction();
                transaction.hide(mCurrentFragment);

                if (fragment == null) {
                    fragment = IndexContentFragment.newInstance(title);
                    transaction.add(R.id.index_content_container, fragment, title);
                } else {
                    transaction.show(fragment);
                }
                transaction.commit();

                mCurrentFragment = fragment;
                mTitle = title;
                mToolbar.setTitle(mTitle);
                mDrawerLayout.closeDrawer(Gravity.LEFT);

            }
        });
    }*/


    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.index_toolbar);
        mToolbar.setBackgroundColor(Color.DKGRAY);
        mToolbar.setTitle("hors");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.actionbar_add_icon));
//        mToolbar.setNavigationContentDescription("init toolbar..");
        setSupportActionBar(mToolbar);
//        mToolbar.setNavigationIcon(R.drawable.tab_settings_normal);
//        Log.i("skyjaj", "init bar id " + mToolbar.getNavigationContentDescription());
//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(IndexCommonActivity.this, "action addfriend ", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

  /*  private void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.index_drawerlayout);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }*/

  /*  @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TITLLE, mTitle);
    }*/

/*    private void restoreTitle(Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mTitle = savedInstanceState.getString(KEY_TITLLE);

        if (TextUtils.isEmpty(mTitle)) {
            mTitle = getResources().getStringArray(
                    R.array.index_array_left_menu)[0];
        }

        mToolbar.setTitle(mTitle);
    }*/


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
}
