package com.skyjaj.hors.admin.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.MyActivityManager;
import com.skyjaj.hors.admin.wigwet.IndexContentFragment;
import com.skyjaj.hors.admin.wigwet.IndexMenuFragment;
import com.skyjaj.hors.utils.ToolbarStyle;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private IndexMenuFragment mMenuFragment;
    private IndexContentFragment mCurrentFragment;
    private FragmentManager fm;
    private Toolbar mToolbar;
    private String mTitle;
    private static final String KEY_TITLLE = "key_title";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        MyActivityManager.getInstance().addActivity(this);
        initViews();
      //初始化默认标题
        restoreTitle(savedInstanceState);
        //初始化fragments
        initFragments();
    }




    private void initFragments() {
        fm = getSupportFragmentManager();
        mCurrentFragment = (IndexContentFragment) fm.findFragmentByTag(mTitle);

        if (mCurrentFragment == null) {
            mCurrentFragment = IndexContentFragment.newInstance(mTitle);
            fm.beginTransaction().add(R.id.index_content_container, mCurrentFragment, mTitle).commit();
        }

        mMenuFragment = (IndexMenuFragment) fm.findFragmentById(R.id.index_menu_container);
        if (mMenuFragment == null) {
            mMenuFragment = new IndexMenuFragment();
            fm.beginTransaction().add(R.id.index_menu_container, mMenuFragment).commit();
        }

        List<Fragment> fragments = fm.getFragments();
        if (fragments != null)
            for (Fragment fragment : fragments) {
                if (fragment == mCurrentFragment || fragment == mMenuFragment) continue;
                fm.beginTransaction().hide(fragment).commit();
            }

        //设置MenuItem的选择回调
        mMenuFragment.setOnMenuItemSelectedListener(new IndexMenuFragment.OnMenuItemSelectedListener() {
            @Override
            public void menuItemSelected(String title) {

                IndexContentFragment fragment = (IndexContentFragment) getSupportFragmentManager().findFragmentByTag(title);
                Log.i("skyjaj","title :"+ title+" fragment "+fragment);
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
    }



    private void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.index_drawerlayout);
        mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "管理员主页");

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mActionBarDrawerToggle.syncState();
//        mActionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(AdminMainActivity.this, "view onclick ", Toast.LENGTH_SHORT).show();
//            }
//        });
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_TITLLE, mTitle);
    }

   private void restoreTitle(Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mTitle = savedInstanceState.getString(KEY_TITLLE);

        if (TextUtils.isEmpty(mTitle)) {
            mTitle = getResources().getStringArray(
                    R.array.index_array_left_menu)[0];
        }

        mToolbar.setTitle(mTitle);
    }


}
