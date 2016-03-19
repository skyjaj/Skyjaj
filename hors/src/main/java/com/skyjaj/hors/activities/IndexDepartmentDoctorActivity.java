package com.skyjaj.hors.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.adapter.TimestampTypeAdapter;
import com.skyjaj.hors.bean.Department;
import com.skyjaj.hors.bean.Doctor;
import com.skyjaj.hors.utils.CommonAdapter;
import com.skyjaj.hors.utils.DateUtil;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;
import com.skyjaj.hors.utils.ViewHolder;
import com.skyjaj.hors.widget.CustomProgressDialog;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 科室医生
 */

public class IndexDepartmentDoctorActivity extends AppCompatActivity{

    private Toolbar mToolbar;
    private NetworkTask mNetworkTask=null;
    private CommonAdapter<Doctor> mAdapter;
    private ListView mListView;
    private List<Doctor> mDatas=new ArrayList<Doctor>();
    private Department department;
    private Dialog dialog;
    private boolean isNowDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_department_doctor);
        MyActivityManager.getInstance().addActivity(this);
        Intent intent = getIntent();
        department = (Department) intent.getSerializableExtra("department");
        isNowDay = intent.getBooleanExtra("isNowDay",false);
        mToolbar = ToolbarStyle.initToolbar(this, R.id.index_service_department_doctor_toolbar, department.getNameCn());

        initView();
        attemptToLoadData();
    }

    private void attemptToLoadData() {
        dialog = DialogStylel.createLoadingDialog(this, "加载中...");
        dialog.show();
        mNetworkTask = new NetworkTask(department);
        mNetworkTask.execute();
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.index_service_department_doctor_listview);
        mAdapter = new CommonAdapter<Doctor>(this,mDatas,R.layout.index_doctor_item) {
            @Override
            public void convert(final ViewHolder holder, final int position) {
                Doctor doctor = mDatas.get(position);
                if (doctor != null) {
                    holder.setText(R.id.index_doctor_item_name, doctor.getName())
                            .setText(R.id.index_doctor_item_title, "("+doctor.getTitle()+")")
                            .setText(R.id.index_doctor_item_registering_fee, "挂号费:" + doctor.getRegisteredFee() + "元")
                            .setText(R.id.index_doctor_item_inspecting_fee, "诊查费:" + doctor.getRegisteredFee() + "元")
                            .setText(R.id.index_doctor_item_address, "诊室地址:" + doctor.getAddress());
                    holder.getView(R.id.index_doctor_item_details).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(IndexDepartmentDoctorActivity.this, IndexDoctorAppointmentActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("doctor", mDatas.get(position));
                            bundle.putSerializable("department", department);
                            intent.putExtras(bundle);
                            IndexDepartmentDoctorActivity.this.startActivity(intent);
                        }
                    });
                }
            }
        };
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = null;
                if (isNowDay) {
                    intent = new Intent(IndexDepartmentDoctorActivity.this, NowDayPointTime.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("doctor", mDatas.get(position));
                    bundle.putSerializable("department", department);
                    intent.putExtra("department_name", department.getNameCn());
                    intent.putExtras(bundle);
                    startActivity(intent);
//                    intent.putExtra("workday", DateUtil.Timestamp2String(scheduleOfmonth.getWorkday()));
                } else {
                    intent = new Intent(IndexDepartmentDoctorActivity.this, CalenderActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("doctor", mDatas.get(position));
                    bundle.putSerializable("department", department);
                    intent.putExtra("department_name", department.getNameCn());
                    intent.putExtras(bundle);
                    IndexDepartmentDoctorActivity.this.startActivity(intent);
                }

            }
        });
    }


    @Override
    protected void onDestroy() {
        dialog = null;
        MyActivityManager.getInstance().remove(this);
        super.onDestroy();
    }

    //异步任务
    public class NetworkTask extends AsyncTask<Void, Void, Boolean> {


        private Department department;

        NetworkTask(Department department) {
            this.department=department;
            department.setCreateTime(null);
            department.setLastUpdateTime(null);
            department.setItemType(null);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d("xys", "doInBackground");
            try {
                String str = OkHttpManager.post(ServerAddress.FIND_DOCTOR_BY_DEPARTMENT_ID_URL,new Gson().toJson(department));
                Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).create();
                mDatas = gson.fromJson(str, new TypeToken<List<Doctor>>() {}.getType());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            dialog.dismiss();
            if (success) {
                Log.i("skyjaj", "请求成功");
                if (mDatas == null && mDatas.size()==0) {
                    Toast.makeText(IndexDepartmentDoctorActivity.this, "此", Toast.LENGTH_SHORT).show();
                    finish();
                }
                mAdapter.setmData(mDatas);
                mAdapter.notifyDataSetChanged();
            }else {
                Toast.makeText(IndexDepartmentDoctorActivity.this, "暂时无法连接服务器", Toast.LENGTH_SHORT).show();
                finish();
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
            mNetworkTask=null;
        }
    }

}
