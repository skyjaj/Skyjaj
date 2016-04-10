package com.skyjaj.hors.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.skyjaj.hors.utils.CommonAdapter;
import com.skyjaj.hors.utils.DateUtil;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;
import com.skyjaj.hors.utils.ViewHolder;

import org.litepal.crud.DataSupport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class NowDayPointTime extends BaseActivity {


    private Toolbar mToolbar;
    private TextView mTitle;
    private ListView mListView;

    private Doctor mDoctor;
    private Department mDepartment;
    private List<String> mDatas;
    private CommonAdapter mAdapter;
    private NowDayPointTime.NetworkTask mNetworkTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_day_point_time);

        mDoctor = (Doctor) getIntent().getSerializableExtra("doctor");
        mDepartment = (Department) getIntent().getSerializableExtra("department");

        mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "选择时间");
        initView();
        initDatas();

        mNetworkTask = new NetworkTask();
        mNetworkTask.execute();



    }

    private void initDatas() {
        mDatas = new ArrayList<String>();

        mAdapter = new CommonAdapter(this,mDatas,R.layout.common_item) {
            @Override
            public void convert(ViewHolder holder, int position) {

                holder.setText(R.id.common_item_text, mDatas.get(position));

            }
        };

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                List<LoginInformation> lif = DataSupport.where("state = ? and role = ?", "1", "patient").find(LoginInformation.class);
                if (lif == null || lif.size()==0) {
                    //未登录
//                    Intent intent = new Intent(NowDayPointTime.this, LoginActivity.class);
//                    startActivity(intent);
                    Toast.makeText(NowDayPointTime.this, "请用普通用户号登陆!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(NowDayPointTime.this, IndexReservationConfirmActivity.class); //确认预约信息
                intent.putExtra("doctor_id", mDoctor.getId());
                intent.putExtra("doctor_name", mDoctor.getName());
                intent.putExtra("department_id", mDepartment.getId());
                intent.putExtra("department_name", mDepartment.getNameCn());
                intent.putExtra("appointment_time", mDatas.get(position));
                intent.putExtra("patient_name", lif.get(0).getUsername());
                intent.putExtra("patient_id", lif.get(0).getUsername());
                intent.putExtra("token", lif.get(0).getToken());
                intent.putExtra("register_fee", mDoctor.getRegisteredFee() + "");
                intent.putExtra("inspecting_fee", "0");
                intent.putExtra("checking_fee", "0");
                startActivity(intent);
                Toast.makeText(NowDayPointTime.this, "position :" + position, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initView() {
        mTitle = (TextView) findViewById(R.id.nowday_point_time_title);
        mListView = (ListView) findViewById(R.id.nowday_point_time_listview);
    }


    @Override
    protected void onDestroy() {
        if (mNetworkTask != null) {
            mNetworkTask.cancel(true);
            mNetworkTask = null;
        }
        super.onDestroy();
    }

    //网络任务+
    public class NetworkTask extends AsyncTask<Void, Void, Boolean> {

        private String result;



        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Doctor doctor = new Doctor();
                doctor.setId(mDoctor != null ? mDoctor.getId() : null);
                //token
                doctor.setItemType(null);
                result = OkHttpManager.post(ServerAddress.DOCTOR_NOWDAY_SCHEDULE_TIME, new Gson().toJson(doctor));
            } catch (Exception e1) {
                e1.printStackTrace();
                result = "暂时无法请求数据";
                return false;
            }
            Log.i("skyjaj", "pointoftimeactivity mresult :" + result);
            try {
                Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new CalendarTypeAdapter()).create();
                mDatas = gson.fromJson(result, new TypeToken<List<String>>() {
                }.getType());
            } catch (Exception e) {
                return false;
            }

            if (mDatas != null) {
                return true;
            }

            return false;
        }


        @Override
        protected void onPostExecute(Boolean success) {


            if (success) {
                if (mDatas == null || mDatas.size() == 0) {

                    mTitle.setText("该医生今天已经没有可预约的时间了");
                } else {
                    mTitle.setText(DateUtil.string2TimeFormatfour(mDatas.get(0)));
                    mAdapter.setmData(mDatas);
                    mAdapter.notifyDataSetChanged();
                }

            } else {
                mTitle.setText("该医生今天已经没有可预约的时间了");
            }

            super.onPostExecute(success);
        }

    }



}
