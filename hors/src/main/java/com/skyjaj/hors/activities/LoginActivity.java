package com.skyjaj.hors.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.skyjaj.hors.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class LoginActivity extends AppCompatActivity{

    //临时模拟
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    //自动登录
    private SharedPreferences sharedPreferences = null;
    private CheckBox autoLoginCheckBox;
    private UserLoginTask mAuthTask = null;

    //UI　引用
    private EditText mMobileView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    Toolbar mToolbar=null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        mToolbar.setBackgroundColor(Color.DKGRAY);
        //标题
        mToolbar.setTitle("skyjaj");
        mToolbar.setTitleTextColor(Color.WHITE);
        //导航
        mToolbar.setNavigationIcon(R.drawable.profile);
        mToolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.actionbar_add_icon));
        //二级标题
       /* toolbar.setSubtitle("hors");*/
        /*toolbar.setLogo(R.drawable.icon_msg_search);*/
        Log.i("skyjaj", "theme :" + mToolbar.getPopupTheme());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /*导航的监听*/
                Toast.makeText(LoginActivity.this, "navigation", Toast.LENGTH_SHORT).show();
            }
        });


        sharedPreferences = this.getSharedPreferences("horsUserInfo", 1);

        autoLoginCheckBox = (CheckBox) findViewById(R.id.checkBox_auto_login);
        autoLoginCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Toast.makeText(LoginActivity.this,isChecked+"",Toast.LENGTH_SHORT).show();
                if (isChecked) {
                    sharedPreferences.edit().putBoolean("AUTO_LOGIN_ISCHECK", true).commit();
                } else {
                    sharedPreferences.edit().putBoolean("AUTO_LOGIN_ISCHECK", false).commit();
                }
            }
        });
        mMobileView = (EditText) findViewById(R.id.login_username);
        mPasswordView = (EditText) findViewById(R.id.login_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == R.id.login || EditorInfo.IME_NULL == actionId){
                    attemptLogin();
                    Toast.makeText(LoginActivity.this, "mPasswordView edit .", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
//        if (!sharedPreferences.getBoolean("AUTO_LOGIN_ISCHECK", false)) {
//            Log.i("skyjaj", sharedPreferences.getBoolean("AUTO_LOGIN_ISCHECK", false) + " if");
////            mMobileView.setText(sharedPreferences.getString("HORS_USERNAME", ""));
////            mPasswordView.setText(sharedPreferences.getString("HORS_PASSWORD", ""));
//        }else if(sharedPreferences.getBoolean("AUTO_LOGIN_ISCHECK", true)) {
//            Log.i("skyjaj", sharedPreferences.getBoolean("AUTO_LOGIN_ISCHECK", true) + " else");
//            mMobileView.setText(sharedPreferences.getString("HORS_USERNAME", ""));
//            mPasswordView.setText(sharedPreferences.getString("HORS_PASSWORD", ""));
//            //尝试登录
//            showProgress(true);
//            mAuthTask = new UserLoginTask(sharedPreferences.getString("HORS_USERNAME", "username"), sharedPreferences.getString("HORS_PASSWORD", "password"));
//            mAuthTask.execute((Void) null);
//        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
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
        return true;
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
    public boolean onMenuOpened(int featureId, Menu menu) {

        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        switch (id) {

            //注册
            case R.id.action_register:
                Toast.makeText(this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                Intent registerIntent = new Intent(LoginActivity.this, RegistereActivity.class);
                startActivity(registerIntent);
                return  true;
            //角色选择
            case R.id.action_role_common:
                Toast.makeText(this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                return  true;
            case R.id.action_role_doctor:
                Toast.makeText(this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                return  true;
            case R.id.action_role_manager:
                Toast.makeText(this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                return  true;

            //忘记密码
            case R.id.action_forget_password:
                Toast.makeText(this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                return  true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * 登录回调接口
     * @param v
     */
    public void loginIn(View v) {
        //attemptLogin();
        Intent intent = new Intent(LoginActivity.this, IndexCommonActivity.class);
        startActivity(intent);
        //设置Activity进入、退出动画
        //overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_from_left);
        Toast.makeText(this, "login... ", Toast.LENGTH_SHORT).show();
    }

    //登录验证
    private void attemptLogin() {

        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mMobileView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String mobile = mMobileView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mobile)) {
            mMobileView.setError(getString(R.string.error_field_required));
            focusView = mMobileView;
            cancel = true;
        } else if (!isMobileValid(mobile)) {
            mMobileView.setError(getString(R.string.error_invalid_mobile));
            focusView = mMobileView;
            cancel = true;
        }

        if (cancel) {
            //登录失败
            focusView.requestFocus();
        } else {
            // 显示进度，后台异步登录
            showProgress(true);
            mAuthTask = new UserLoginTask(mobile, password);
            mAuthTask.execute((Void) null);
        }

    }

    //验证密码输入是否有效
    private boolean isPasswordValid(String password) {
        return password.length()>6;
    }

    //手机号码验证
    private boolean isMobileValid(String mobile) {
        return mobile.length()==11;
    }


    //显示进度，隐藏登录信息 targetapi指出应用的版本，
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        //对比应用的版本与平台的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            //获得android定义的短片动画时间
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            //设置该VIEW是否显示
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            //设置动画显示时间以及渐变效果
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //异步登录任务
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mMobile;
        private final String mPassword;

        UserLoginTask(String mobile, String password) {
            mMobile = mobile;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: 通过网络进行身份验证


            Log.d("xys","doInBackground");
            try {
                // 模拟网络访问
                Thread.sleep(800);
            } catch (InterruptedException e) {
                return false;
            }
            //DUMMY_CREDENTIALS为模拟数据
            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                Log.i("skyjaj", pieces[0] + " password :" + pieces[1]);
                if (pieces[0].equals(mMobile)) {
                    Log.d("xys",true+"");
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: 匹配失败，可以提示注册新帐号
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            Log.d("xys", "onPostExecute :"+success);
            if (success) {
                if (autoLoginCheckBox.isChecked()) {
                    // 记住用户名、密码、
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("AUTO_LOGIN_ISCHECK", true);
                    editor.putString("HORS_USERNAME", mMobile);

                    editor.commit();
                }else{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("AUTO_LOGIN_ISCHECK", false);
                    editor.commit();
                }
                Intent intent = new Intent(LoginActivity.this, IndexCommonActivity.class);
                startActivity(intent);
                //设置Activity进入、退出动画
                overridePendingTransition(R.anim.activity_in_from_right,R.anim.activity_out_from_left);
                LoginActivity.this.finish();

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("HORS_PASSWORD", "");
                editor.commit();
            }
        }

        @Override
        protected void onCancelled() {
            Log.d("xys", "onCancelled");
            mAuthTask = null;
            showProgress(false);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("login activity ", " : onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("login activity ", " : onPause");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("login activity ", " : onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("login activity ", " : onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("login activity ", " : onStop");
    }
}