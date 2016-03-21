package com.skyjaj.hors.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.adapter.TimestampTypeAdapter;
import com.skyjaj.hors.bean.Patient;
import com.skyjaj.hors.bean.Reservation;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.RoleConstant;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;

import java.sql.Timestamp;


/**
 * Created by Administrator on 2016/1/16.
 */
public class RegistereActivity extends AppCompatActivity {


    private NetworkTask mNetworkTask;
    private String mResult;
    private Patient mPatientResult;

    //true 为男
    private Boolean sex=true;

    private Toolbar mToolbar=null;

    private TextView passwordFirst;
    private TextView passwordSecond;
    private TextView mMobileView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        MyActivityManager.getInstance().addActivity(this);
        initToolbar();
        initView();
    }

    private void initView() {
        passwordFirst = (TextView) findViewById(R.id.register_psw_one);
        passwordSecond = (TextView) findViewById(R.id.register_psw_two);
        mMobileView = (TextView) findViewById(R.id.register_mobile);
    }

    private void initToolbar() {
        mToolbar = ToolbarStyle.initToolbar(this, R.id.register_toolbar, R.string.register_title);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "on destroy ", Toast.LENGTH_SHORT).show();
    }

    //注册等按钮监听
    public void onRegister(View v) {
//        Toast.makeText(this, "register view ", Toast.LENGTH_SHORT).show();
        attemptRegister();
    }


    private void attemptRegister() {

        if (mNetworkTask != null) {
            return;
        }

        // Reset errors.
        mMobileView.setError(null);
        passwordSecond.setError(null);
        // Store values at the time of the login attempt.
        String mobile = mMobileView.getText().toString();
        String password1 = passwordFirst.getText().toString();
        String password2 = passwordSecond.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password
        if (!TextUtils.isEmpty(password1) && !isPasswordValid(password1,password2)) {
            passwordSecond.setError(getString(R.string.error_invalid_password));
            focusView = passwordSecond;
            cancel = true;
        }

        // Check for a valid mobile
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
            Patient patient = new Patient();
            patient.setMobile(mobile);
            patient.setPassword(password1);
            patient.setSex(sex == true ? 1 : 0);
            mNetworkTask = new NetworkTask(patient);
            mNetworkTask.execute((Void) null);
        }

    }

    //验证密码输入是否有效
    private boolean isPasswordValid(String password1,String password2) {
        if (password1==null ||password1.length() > 20 || !password1.equals(password2) || password1.length()<6) {
            return false;
        }
        return true;
    }

    //手机号码验证
    private boolean isMobileValid(String mobile) {
        return mobile.length()==11;
    }



    //radio button监听
    public void onRadioButton(View v) {
        switch (v.getId()) {
            case R.id.register_sex_f:
                sex=true;
                Toast.makeText(this, "register_sex_f ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.register_sex_m:
                sex = false;
                Toast.makeText(this, "register_sex_m ", Toast.LENGTH_SHORT).show();
                break;
        }
    }




    //异步任务
    public class NetworkTask extends AsyncTask<Void, Void, Boolean> {

        private Patient patient;
        NetworkTask(Patient patient) {
            this.patient = patient;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d("xys", " IndexDoctorAppointmentActivity doInBackground");
            try {
                mResult = OkHttpManager.post(ServerAddress.PATIENT_REGISTER_URL, new Gson().toJson(patient));
                Log.i("skyjaj", "mresult :"+mResult);
                try {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).create();
                    mPatientResult = gson.fromJson(mResult, new TypeToken<Patient>() {}.getType());
                } catch (Exception e) {
                    return false;
                }

                if (mPatientResult != null) {
                    return  true;
                }


            } catch (Exception e) {
                mResult = "网络异常,暂时无法连接服务器";
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Log.i("skyjaj", "请求成功");
                Toast.makeText(RegistereActivity.this, "恭喜您，注册成功", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = RegistereActivity.this.getSharedPreferences("horsUserInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("AUTO_LOGIN_ISCHECK", true);
                editor.putString("HORS_USERNAME", mPatientResult.getMobile());
                editor.putString("HORS_PASSWORD", mPatientResult.getPassword());
                editor.putString("role", RoleConstant.PATIENT);
                editor.putBoolean("AUTO_LOGIN_ISCHECK", true);
                editor.commit();
                Intent intent = new Intent(RegistereActivity.this, LoginActivity.class);
                RegistereActivity.this.finish();
            }else {
                if (mNetworkTask != null) {
                    mNetworkTask.cancel(true);
                    mNetworkTask = null;
                }
                Toast.makeText(RegistereActivity.this, mResult, Toast.LENGTH_SHORT).show();
                Log.i("skyjaj", "请求失败");
            }
        }

        @Override
        protected void onCancelled() {
            Log.d("xys", "onCancelled");

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mNetworkTask != null) {
            mNetworkTask.cancel(true);
            mNetworkTask = null;
        }
    }

}
