package com.skyjaj.hors.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.adapter.CalendarTypeAdapter;
import com.skyjaj.hors.bean.Department;
import com.skyjaj.hors.bean.Doctor;
import com.skyjaj.hors.bean.LoginInformation;
import com.skyjaj.hors.bean.ScheduleOfMonth;
import com.skyjaj.hors.utils.DateUtil;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.widget.CalendarCard;
import com.skyjaj.hors.widget.CustomProgressDialog;
import com.skyjaj.hors.widget.PointOfTimeView;

import org.litepal.crud.DataSupport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/3/1.
 */
public class PointOfTimeActivity extends Activity implements PointOfTimeView.OnItemClikListener {

    private final String TAG = "skyjaj";


    //UI
    private LinearLayout output;
    private TextView chooseTime;
    private TextView title;
    private ImageButton buttonBack;
    //时间点的数目
    private int total;
    private List<String> results;
    private String workday;
    private Task mTask;
    private Department mDepartment;
    private Doctor mDoctor;
    private String mTime;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_of_time);
        MyActivityManager.getInstance().addActivity(this);
        mDoctor = (Doctor) getIntent().getSerializableExtra("doctor");
        mDepartment = (Department) getIntent().getSerializableExtra("department");
        workday = getIntent().getStringExtra("workday");
        if (workday == null) {
            Toast.makeText(PointOfTimeActivity.this, "请选择有效的日期！", Toast.LENGTH_SHORT).show();
            finish();
        }
        initView();
        dialog = DialogStylel.createLoadingDialog(this, "加载中...");
        dialog.show();
        mTask = new Task();
        mTask.execute();
    }


    private void initView() {
        output = (LinearLayout) findViewById(R.id.point_of_time_point);
        title = (TextView) findViewById(R.id.point_of_time_title);
        chooseTime = (TextView) findViewById(R.id.point_of_time_choose_time);
        buttonBack = (ImageButton) findViewById(R.id.point_of_time_btn_left);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.point_of_time_ensure:
                if (workday == null || mTime == null) {
                    Toast.makeText(PointOfTimeActivity.this, "请选择时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<LoginInformation> lif = DataSupport.where("state = ? and role = ?", "1", "patient").find(LoginInformation.class);
                Log.i(TAG, "lif :" + lif);
                if (lif == null || lif.size()==0) {
                    //未登录
//                    Intent intent = new Intent(this, LoginActivity.class);
//                    startActivity(intent);
                    Toast.makeText(PointOfTimeActivity.this, "请用普通用户号登陆!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(this, IndexReservationConfirmActivity.class); //确认预约信息
                intent.putExtra("doctor_id", mDoctor.getId());
                intent.putExtra("doctor_name", mDoctor.getName());
                intent.putExtra("department_id", mDepartment.getId());
                intent.putExtra("department_name", mDepartment.getNameCn());
                intent.putExtra("appointment_time", workday + " " + mTime);
                intent.putExtra("patient_name", lif.get(0).getUsername());
                intent.putExtra("patient_id", lif.get(0).getUsername());
                intent.putExtra("token", lif.get(0).getToken());
                intent.putExtra("register_fee", mDoctor.getRegisteredFee() + "");
                intent.putExtra("inspecting_fee", "0");
                intent.putExtra("checking_fee", "0");
                startActivity(intent);
                break;
            case R.id.point_of_time_btn_left:
                finish();
                break;
        }

    }

    @Override
    public void itemClick(String item) {
//        Toast.makeText(PointOfTimeActivity.this, "itemClick...."+item, Toast.LENGTH_SHORT).show();
        if (item != null && !"".equals(item)) {
            chooseTime.setText("已选择时间: " + item);
            mTime = item;
        }

    }


    @Override
    protected void onDestroy() {
        MyActivityManager.getInstance().remove(this);
        super.onDestroy();
        if (dialog != null) {
            dialog = null;
        }
        results =null;
        if (mTask != null) {
            mTask.cancel(true);
            mTask =null;
        }

    }

    //异步任务
    public class Task extends AsyncTask<Void, Void, Boolean> {

        private String result;

        Task() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {


            try {
                Doctor doctor = new Doctor();
                doctor.setId(mDoctor != null ? mDoctor.getId() : null);
                doctor.setRemark(workday);
                doctor.setItemType(null);
                result = OkHttpManager.post(ServerAddress.DOCTOR_SCHEDULE_TIME, new Gson().toJson(doctor));
            } catch (Exception e1) {
                e1.printStackTrace();
                result = "暂时无法请求数据";
                return false;
            }
            Log.i("skyjaj", "pointoftimeactivity mresult :" + result);
            try {
                Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new CalendarTypeAdapter()).create();
                results = gson.fromJson(result, new TypeToken<List<String>>() {
                }.getType());
            } catch (Exception e) {
                return false;
            }

            if (results != null) {
                return true;
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            dialog.dismiss();

            if (success) {
                output.addView(new PointOfTimeView(PointOfTimeActivity.this, PointOfTimeActivity.this, results, results != null ? results.size() : 0));
            } else {
                Toast.makeText(PointOfTimeActivity.this, result, Toast.LENGTH_SHORT).show();
                if (mTask != null) {
                    mTask.cancel(true);
                    mTask =null;
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
