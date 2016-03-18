package com.skyjaj.hors.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.skyjaj.hors.R;
import com.skyjaj.hors.bean.Doctor;
import com.skyjaj.hors.bean.Patient;
import com.skyjaj.hors.bean.SystemUser;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.RoleConstant;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;
import com.skyjaj.hors.widget.SettingDialog;

/**
 * 设置导航页面
 */
public class SettingActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    private TextView mPasswordTv,mExitTv;
    public  AlertDialog ad;
    private String role;
    private String mobile;
    private Dialog loadingDialog;
    private SharedPreferences sharedPreferences;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
            Toast.makeText(SettingActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "设置");


        sharedPreferences = SettingActivity.this.getSharedPreferences("horsUserInfo", MODE_PRIVATE);
        role =sharedPreferences.getString("role", RoleConstant.PATIENT);
        mobile = sharedPreferences.getString("HORS_USERNAME", "");
        initView();
        loadingDialog = DialogStylel.createLoadingDialog(this, "提交中..");
    }

    private void initView() {

        mPasswordTv = (TextView) findViewById(R.id.setting_update_password);
        mExitTv = (TextView) findViewById(R.id.setting_exit);

    }


    public void updatePassword(final String oldPsw, final String newPsw) {

        synchronized (SettingActivity.class) {

            if (loadingDialog != null && !loadingDialog.isShowing()) {
                loadingDialog.show();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {

                    String result = "";
                    try {
                        if (RoleConstant.PATIENT.equals(role)) {

                            Patient patient = new Patient();
                            patient.setMobile(mobile);
                            patient.setPassword(newPsw);
                            patient.setRemark(oldPsw);
                            result = OkHttpManager.post(ServerAddress.PATIENT_INFORMACTION_UPDATE_PASSWORD_URL, new Gson().toJson(patient));

                        } else if (RoleConstant.DOCTOR.equals(role)) {

                            Doctor doctor = new Doctor();
                            doctor.setMobile(mobile);
                            doctor.setPassword(newPsw);
                            doctor.setRemark(oldPsw);
                            result = OkHttpManager.post(ServerAddress.DOCTOR_INFORMACTION_UPDATE_PASSWORD_URL, new Gson().toJson(doctor));

                        } else if (RoleConstant.ADMIN.equals(role)) {

                            SystemUser user = new SystemUser();
                            user.setMobile(mobile);
                            user.setPassword(newPsw);
                            user.setRemark(oldPsw);
                            result = OkHttpManager.post(ServerAddress.ADMIN_INFORMACTION_UPDATE_PASSWORD_URL, new Gson().toJson(user));

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        result = "连接服务器失败，请检查网络";
                    }

                    Message msg = new Message();
                    msg.obj = result;
                    mHandler.sendMessage(msg);

                }
            }).start();

        }

    }




    //响应控件
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.setting_update_password_view:
                SettingDialog.SettingDialogListener listener = new SettingDialog.SettingDialogListener() {
                    @Override
                    public void onClick(View view,String oldPassword,String newPassword) {
                        switch (view.getId()) {
                            case R.id.dialog_update_password_ok:
                                updatePassword(oldPassword,newPassword);
                                break;
                        }
                    }
                };
                SettingDialog settingDialog = new SettingDialog(this, listener);
                settingDialog.setTitleContent("修改密码");
                break;
            case R.id.setting_exit_view:

                LinearLayout exitDialogLayout = (LinearLayout) SettingActivity.this.getLayoutInflater().inflate(R.layout.dialog_exit, null);
                TextView switchTv = (TextView) exitDialogLayout.findViewById(R.id.switchOtherAccount);
                TextView exitTv = (TextView) exitDialogLayout.findViewById(R.id.close_application);
                //切换帐号
                switchTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = SettingActivity.this.getSharedPreferences("horsUserInfo", 1).edit();
                        editor.putBoolean("AUTO_LOGIN_ISCHECK", false);
                        editor.putString("HORS_USERNAME", null);
                        editor.putString("HORS_PASSWORD", null);
                        editor.commit();
                        if (ad != null) {
                            ad.dismiss();
                            ad = null;
                        }
                        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                        SettingActivity.this.finish();
                        SettingActivity.this.startActivity(intent);
                    }
                });
                //直接退出
                exitTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //发送关闭程序
                        if (ad != null) {
                            ad.dismiss();
                            ad = null;
                        }
                        MyActivityManager.getInstance().exit();
                        System.exit(0);

                    }
                });
                ad = new AlertDialog.Builder(SettingActivity.this)
                        .setView(exitDialogLayout)
                        .show();

                break;
        }

    }


}
