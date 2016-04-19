package com.skyjaj.hors.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.RoleConstant;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;

import java.sql.Timestamp;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


/**
 * Created by Administrator on 2016/1/16.
 */
public class RegistereActivity extends BaseActivity {

    private static String APPKEY = "116fb12b1951f";
    private static String APPSECRET = "2f86e61f4c737a55d4387c2f6ccae290";

    private NetworkTask mNetworkTask;
    private String mResult;
    private Patient mPatientResult;

    //true 为男
    private Boolean sex=true;

    private Toolbar mToolbar=null;

    private TextView passwordFirst;
    private TextView passwordSecond;
    private TextView mMobileView;
    private static Button getCodeBtn;
    private EditText code;

    private static Thread th;
    private static boolean canSend;
    private static final int UPDATECODE = 0, UPDATEUI = 1, MESSAGE = 2,SEND_SMS = 3;
    private static Context ctx;
    private boolean ready;
    private Dialog dialog;


    private static  final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.i("handle :", "event :" + event + " result :" + result + " data :" + data);
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    boolean smart = (Boolean) data;
                    Log.i("skyjaj", "data :" + data);
                    if (smart) {
                        Log.i("skyjaj", "data :" + "通过智能验证");
                        //通过智能验证
                        Toast.makeText(ctx, "通过智能验证", Toast.LENGTH_SHORT).show();
                    } else {
                        //依然走短信验证
                        Log.i("skyjaj", "data :" + "短信验证已发送");
                        Toast.makeText(ctx, "短信验证已发送", Toast.LENGTH_SHORT).show();
                    }
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    //返回支持发送验证码的国家列表
                }
            } else if (msg.what == UPDATECODE) {
                getCodeBtn.setText("获取验证码");
                getCodeBtn.setBackgroundResource(R.drawable.selector_button);
                canSend = true;
            } else if (msg.what == UPDATEUI) {
                getCodeBtn.setBackgroundResource(R.drawable.edittext_focus);
                getCodeBtn.setText(data + "");
            } else if (msg.what == MESSAGE) {
                Toast.makeText(ctx,(String)data, Toast.LENGTH_SHORT).show();
            }else if (msg.what == SEND_SMS) {
                SMSSDK.getVerificationCode("86", msg.obj+"");
                sendedMSM();
            } else {
                ((Throwable) data).printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initToolbar();
        initView();
        initSDK();
        ctx = getApplication();
        if (th != null && th.isAlive()) {
            canSend = false;
            if (getCodeBtn != null) {
                getCodeBtn.setBackgroundResource(R.drawable.edittext_focus);
            }
        } else {
            canSend = true;
        }
    }

    private void initView() {
        passwordFirst = (TextView) findViewById(R.id.register_psw_one);
        passwordSecond = (TextView) findViewById(R.id.register_psw_two);
        mMobileView = (TextView) findViewById(R.id.register_mobile);

        code = (EditText) findViewById(R.id.register_code);
        getCodeBtn = (Button) findViewById(R.id.register_getCode);

    }


    private void initSDK() {
        // 初始化短信SDK
        SMSSDK.initSDK(this, APPKEY, APPSECRET, true);
        EventHandler eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        // 注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
        ready = true;
        Log.i("handle :", "init ");

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
        String codevalue = code.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password
        if (TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)) {
            passwordSecond.setError("密码不能为空");
            focusView = passwordSecond;
            cancel = true;
        }else if (!isPasswordValid(password1,password2)) {
            passwordSecond.setError("两次密码不一样");
            focusView = passwordSecond;
            cancel = true;
        }else if (password1.length() < 6 || password1.length() > 18 || password2.length() < 6 || password2.length() > 18 ) {
            passwordSecond.setError("密码长度为6~18");
            focusView = passwordSecond;
            cancel = true;
        }


        // Check for a valid mobile
        if (TextUtils.isEmpty(mobile)) {
            mMobileView.setError(getString(R.string.error_field_required));
            focusView = mMobileView;
            cancel = true;
        } else if (!isMobileValid(mobile)) {
            mMobileView.setError("请输入11位手机号码");
            focusView = mMobileView;
            cancel = true;
        }

        if (TextUtils.isEmpty(codevalue)) {
            code.setError("验证码不能为空");
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
            patient.setRemark(codevalue);
            if (dialog == null) {
                dialog = DialogStylel.createLoadingDialog(this, "正在注册..");
                dialog.show();
            } else if(!dialog.isShowing()){
                dialog.show();
            }
            mNetworkTask = new NetworkTask(patient);
            mNetworkTask.execute((Void) null);
        }

    }


    //验证密码输入是否有效
    private boolean isPasswordValid(String password1,String password2) {
        if (!password1.equals(password2)) {
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

    public void tryToSendSms() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                String phone = mMobileView.getText().toString();
                try {
                    result = OkHttpManager.post(ServerAddress.PATIENT_HAS_MOBILE_URL, phone);
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "网络异常，发送失败";
                }

                if (!TextUtils.isEmpty(result)) {
                    result = result.replaceAll("'\"", "");
                }
                Message msg = new Message();
                if ("no".equals(result)) {
                    msg.what = SEND_SMS;
                    msg.obj = phone;
                }else if ("yes".equals(result)) {
                    msg.obj = "该用户已存在";
                    msg.what = MESSAGE;
                }else {
                    msg.what = MESSAGE;
                    msg.obj = result;
                }
                handler.sendMessage(msg);
            }
        }).start();
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.register_getCode:
                if (canSend&&attempToGetCode()) {
				    tryToSendSms();
                }
                break;
        }
    }

    private synchronized static void sendedMSM() {
        canSend = false;
        getCodeBtn.setBackgroundResource(R.drawable.edittext_focus);
        th =new Thread(new Runnable() {
            @Override
            public void run() {
                int i=60;
                while (i>0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                    Message msg = new Message();
                    msg.what = UPDATEUI;
                    msg.obj = i+"";
                    handler.sendMessage(msg);
                }
                Message msg = new Message();
                msg.what = UPDATECODE;
                handler.sendMessage(msg);
            }
        });

        th.start();

    }

    private boolean attempToGetCode() {

        String mobile = mMobileView.getText().toString();

        TextView errorView = null;


        if (TextUtils.isEmpty(mobile) || mobile.length() != 11) {
            errorView = mMobileView;
            errorView.setError("请输入11位手机号码");
        }

        if (errorView != null) {

            errorView.requestFocus();
            return false;
        }
        return true;
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
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
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
            } else {
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
