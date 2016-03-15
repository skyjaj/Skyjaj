package com.skyjaj.hors.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.adapter.TimestampTypeAdapter;
import com.skyjaj.hors.bean.Doctor;
import com.skyjaj.hors.bean.LoginInformation;
import com.skyjaj.hors.bean.Patient;
import com.skyjaj.hors.bean.Reservation;
import com.skyjaj.hors.bean.SystemUser;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.RoleConstant;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;

import org.litepal.crud.DataSupport;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Administrator on 2016/3/5.
 */
public class UserInformactionAcitvity extends AppCompatActivity {

    //UI
    private ImageView mHeadIV;
    private TextView mAccountTV,mNicknameTV,mSexTV,mSignitureTV,mRegionTV;
    private Toolbar mToolbar;

    private Patient mPatient;
    private Doctor mDoctor;
    private SystemUser mAdmin;
    private NetworkTask mNetworkTask;
    private String role;
    private SharedPreferences sharedPreferences;

    private final int PATIENT =1 , DOCTOR =2, ADMIN =3 ;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case PATIENT:
                    setPatientUI();
                    break;
                case DOCTOR:
                    setDoctorUI();
                    break;
                case ADMIN:
                    setAdminUI();
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_informaction);
        MyActivityManager.getInstance().addActivity(this);
        mToolbar = ToolbarStyle.initToolbar(this, R.id.user_informaction_toolbar, "个人信息");
        initView();

        sharedPreferences = this.getSharedPreferences("horsUserInfo", Context.MODE_PRIVATE);
        role = sharedPreferences.getString("role", "");

        mNetworkTask = new NetworkTask();
        mNetworkTask.execute();

    }

    private void initView() {
        mHeadIV = (ImageView) findViewById(R.id.user_informaction_head);
        mAccountTV = (TextView) findViewById(R.id.user_informaction_account);
        mNicknameTV = (TextView) findViewById(R.id.user_informaction_nickname);
        mSexTV = (TextView) findViewById(R.id.user_informaction_sex);
        mSignitureTV = (TextView) findViewById(R.id.user_informaction_signtrue);
        mRegionTV = (TextView) findViewById(R.id.user_informaction_region);
    }

    public void setPatientUI() {
        if (mPatient != null) {
            mAccountTV.setText(mPatient.getMobile());
            mNicknameTV.setText(mPatient.getUsername());
            mSexTV.setText(mPatient.getSex() == 1 ? "男" : "女");
            mRegionTV.setVisibility(View.GONE);

        }
    }

    public void setDoctorUI() {
        if (mDoctor != null) {
            mAccountTV.setText(mDoctor.getMobile());
            mNicknameTV.setText(mDoctor.getUsername());
            mSexTV.setText(mDoctor.getSex() == 1 ? "男" : "女");
            mRegionTV.setText(mDoctor.getAddress()!=null?mDoctor.getAddress():"");
        }
    }

    public void setAdminUI(){
        if (mAdmin != null) {
            mAccountTV.setText(mAdmin.getMobile());
            mNicknameTV.setText(mAdmin.getUsername());
            mSexTV.setText(mAdmin.getSex() == 1 ? "男" : "女");
            mRegionTV.setText(mAdmin.getAddress()!=null?mAdmin.getAddress():"");
        }
    }


    @Override
    protected void onDestroy() {
        MyActivityManager.getInstance().remove(this);
        super.onDestroy();
    }

    //网络任务
    public class NetworkTask extends AsyncTask<Void, Void, Boolean> {

        private String result;

        @Override
        protected Boolean doInBackground(Void... params) {

            List<LoginInformation> lif = DataSupport.where("state = ?", "1").find(LoginInformation.class);
            Log.i("skyjaj", "role :" + role);
            if (lif == null || lif.size()==0) {
                return false;
            }

            try {
                Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).create();
                Message msg = new Message();
                if (RoleConstant.PATIENT.equals(role)) {//PATIENT
                    Patient patient = new Patient();
                    patient.setUsername(lif.get(0).getUsername());
                    patient.setToken(patient.getToken());
                    result = OkHttpManager.post(ServerAddress.PATIENT_INFORMACTION_URL, new Gson().toJson(patient));
                    try {
                        mPatient = gson.fromJson(result, new TypeToken<Patient>() {
                        }.getType());
                        Log.i("skyjaj", "result ;" + result);
                    } catch (Exception e) {
                        result = "获取信息失败";
                        return false;
                    }
                    if (mPatient != null) {
                        Log.i("skyjaj", "mResult ;" + mPatient);
                        msg.what = PATIENT;
                        mHandler.sendMessage(msg);
                        return true;
                    }
                } else if (RoleConstant.DOCTOR.equals(role)) {//DOCTOR
                    Doctor doctor = new Doctor();
                    doctor.setItemType(null);
                    Log.i("skyjaj", "lif.get(0).getUid() " + lif.get(0).getUid());
                    doctor.setId(lif.get(0).getUid());
                    doctor.setToken(doctor.getToken());
                    result = OkHttpManager.post(ServerAddress.DOCTOR_INFORMACTION_URL, new Gson().toJson(doctor));
                    Log.i("skyjaj", "result ;" + result);
                    try {
                        mDoctor = gson.fromJson(result, new TypeToken<Doctor>() {
                        }.getType());
                    } catch (Exception e) {
                        result = "获取信息失败";
                        return false;
                    }
                    if (mDoctor != null) {
                        msg.what = DOCTOR;
                        mHandler.sendMessage(msg);
                        return true;
                    }
                } else if (RoleConstant.ADMIN.equals(role)) {//ADMIN
                    SystemUser systemUser = new SystemUser();
                    systemUser.setId(lif.get(0).getUid());
                    systemUser.setToken(systemUser.getToken());
                    result = OkHttpManager.post(ServerAddress.ADMIN_INFORMACTION_URL, new Gson().toJson(systemUser));
                    Log.i("skyjaj", "result :" + result);
                    try {
                        mAdmin = gson.fromJson(result, new TypeToken<SystemUser>() {}.getType());
                    } catch (Exception e) {
                        result = "获取信息失败";
                        return false;
                    }
                    if (mAdmin != null) {
                        msg.what = ADMIN;
                        mHandler.sendMessage(msg);
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = "网络请求失败";
                return false;
            }

                result = "获取信息失败";
                return false;

        }


        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {

            } else {
                Toast.makeText(UserInformactionAcitvity.this, result, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
