package com.skyjaj.hors.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.skyjaj.hors.adapter.CalendarTypeAdapter;
import com.skyjaj.hors.bean.Department;
import com.skyjaj.hors.bean.Doctor;
import com.skyjaj.hors.bean.ScheduleOfMonth;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Administrator on 2016/2/23.
 *
 */

public class IndexDoctorAppointmentActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    //医生姓名
    private TextView name;
    private TextView department;
    //职称
    private TextView title;
    //医生简介
    private TextView introducation;
    //头像url
    private ImageView headUrl;
    private Doctor doctor;
    private Department mDepartment;
    private NetworkTask mNetwork;
    private List<ScheduleOfMonth> mScheduleOfMonthResult;
    private String mResult;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_index_doctor_appointment);

        MyActivityManager.getInstance().addActivity(this);

        doctor = (Doctor) getIntent().getSerializableExtra("doctor");
        mDepartment = (Department) getIntent().getSerializableExtra("department");
        mToolbar = ToolbarStyle.initToolbar(this, R.id.index_doctor_appointment_toolbar, "预约大夫");

        initView();
        initData();

    }

    private void initData() {

        if (doctor == null) {
            return;
        }
        introducation.setText(doctor.getIntroduction());
        name.setText("姓名: "+doctor.getName());
        title.setText("职称: "+doctor.getTitle());
        department.setText("科室: " + mDepartment.getNameCn());
    }

    private void initView() {

        name = (TextView) findViewById(R.id.index_doctor_appointment_doctor_name);
        department = (TextView) findViewById(R.id.index_doctor_appointment_department_name);
        title = (TextView) findViewById(R.id.index_doctor_appointment_title);
        introducation = (TextView) findViewById(R.id.index_doctor_appointment_introducation);
        headUrl = (ImageView) findViewById(R.id.index_doctor_appointment_headurl);
    }



    public void appointmentIn(View view) {

        Intent intent = new Intent(this,CalenderActivity.class); //确认预约信息
        Bundle bundle = new Bundle();
        bundle.putSerializable("doctor", doctor);
        bundle.putSerializable("department", mDepartment);
        intent.putExtras(bundle);
        startActivity(intent);

    }



    //异步任务
    public class NetworkTask extends AsyncTask<Void, Void, Boolean> {

        private String doctorId;

        NetworkTask(String doctorId) {
            this.doctorId = doctorId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d("xys", " IndexDoctorAppointmentActivity doInBackground");
            try {
                Doctor mDoctor = new Doctor();
                mDoctor.setItemType(null);
                mDoctor.setCreateTime(null);
                mDoctor.setId(doctorId);
                mDoctor.setMobile(doctor.getMobile());
                mResult = OkHttpManager.post(ServerAddress.DOCTOR_SCHEDULE_URL, new Gson().toJson(mDoctor));
                Log.i("skyjaj", "mresult :" + mResult);
                try {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new CalendarTypeAdapter()).create();
                    mScheduleOfMonthResult = gson.fromJson(mResult, new TypeToken<List<ScheduleOfMonth>>() {}.getType());
                } catch (Exception e) {
                    return false;
                }

                if (mScheduleOfMonthResult != null) {
                    return true;
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
                Toast.makeText(IndexDoctorAppointmentActivity.this, mScheduleOfMonthResult != null ? "已获取日期 " + mScheduleOfMonthResult.size() : mResult, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(IndexDoctorAppointmentActivity.this, CalenderActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("scheduleOfMonth", mScheduleOfMonthResult);
                //CalenderActivity.scheduleOfMonths = mScheduleOfMonthResult;
                startActivity(intent);
            }else {
                Toast.makeText(IndexDoctorAppointmentActivity.this, mResult, Toast.LENGTH_SHORT).show();
                Log.i("skyjaj", "请求失败");
                if (mNetwork != null) {
                    mNetwork.cancel(true);
                    mNetwork = null;
                }
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            Log.d("xys", "onCancelled");

        }
    }

}
