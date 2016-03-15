package com.skyjaj.hors.admin.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.CalenderActivity;
import com.skyjaj.hors.activities.IndexDoctorAppointmentActivity;
import com.skyjaj.hors.activities.LoginActivity;
import com.skyjaj.hors.activities.MyActivityManager;
import com.skyjaj.hors.adapter.TimestampTypeAdapter;
import com.skyjaj.hors.admin.wigwet.DoctorManagerForOnItemLongClickDialog;
import com.skyjaj.hors.bean.Department;
import com.skyjaj.hors.bean.Doctor;
import com.skyjaj.hors.utils.CommonAdapter;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;
import com.skyjaj.hors.utils.ViewHolder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DoctorManagerActivity extends AppCompatActivity{

    private Toolbar mToolbar;
    private NetworkTask mNetworkTask=null;
    private CommonAdapter<Doctor> mAdapter;
    private ListView mListView;
    private List<Doctor> mDatas=new ArrayList<Doctor>();
    private Department department;
    private Dialog dialog;
    private boolean isOnLongClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_manager);
        mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "医生管理");

        MyActivityManager.getInstance().addActivity(this);
        department = new Department();
        department.setId("15");
        department.setItemType(null);
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
        mListView = (ListView) findViewById(R.id.doctor_manager_listview);
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
                            Intent intent = new Intent(DoctorManagerActivity.this, IndexDoctorAppointmentActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("doctor", mDatas.get(position));
                            bundle.putSerializable("department", department);
                            intent.putExtras(bundle);
                            DoctorManagerActivity.this.startActivity(intent);
                        }
                    });
                }
            }
        };
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isOnLongClick) {
                    return;
                }
                Intent intent = new Intent(DoctorManagerActivity.this, CalenderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("doctor", mDatas.get(position));
                bundle.putSerializable("department", department);
                intent.putExtras(bundle);
                intent.putExtra("department_name", department.getNameCn());
                DoctorManagerActivity.this.startActivity(intent);
            }
        });

        //长按监听
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                DoctorManagerForOnItemLongClickDialog.OnItemClickListener listener = new DoctorManagerForOnItemLongClickDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(int resId) {
                        isOnLongClick = false;
                        switch (resId) {
                            case R.id.manager_doctor_delete:
                                Toast.makeText(DoctorManagerActivity.this, "resid :" + "manager_doctor_delete ", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.manager_doctor_update:
                                Toast.makeText(DoctorManagerActivity.this, "resid :" + "manager_doctor_update ", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.manager_doctor_stop:
                                Toast.makeText(DoctorManagerActivity.this, "resid :" + "manager_doctor_stop ", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onDismissListener(DialogInterface dialog) {
                        isOnLongClick = false;
                        Toast.makeText(DoctorManagerActivity.this, "resid :" + "onDismissListener ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelListener(DialogInterface dialog) {
                        Toast.makeText(DoctorManagerActivity.this, "resid :" + "onCancelListener ", Toast.LENGTH_SHORT).show();
                    }
                };

                DoctorManagerForOnItemLongClickDialog clickDialog = new DoctorManagerForOnItemLongClickDialog(listener, DoctorManagerActivity.this);
                isOnLongClick = true;
                return false;
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
                String str = OkHttpManager.post(ServerAddress.FIND_DOCTOR_BY_DEPARTMENT_ID_URL, new Gson().toJson(department));
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
                mAdapter.setmData(mDatas);
                mAdapter.notifyDataSetChanged();
            }else {
                Toast.makeText(DoctorManagerActivity.this, "暂时无法连接服务器", Toast.LENGTH_SHORT).show();
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
