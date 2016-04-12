package com.skyjaj.hors.admin.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.BaseActivity;
import com.skyjaj.hors.bean.BaseMessage;
import com.skyjaj.hors.bean.LoginInformation;
import com.skyjaj.hors.bean.Patient;
import com.skyjaj.hors.bean.Reservation;
import com.skyjaj.hors.bean.SystemUser;
import com.skyjaj.hors.utils.CommonAdapter;
import com.skyjaj.hors.utils.DateUtil;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;
import com.skyjaj.hors.utils.ViewHolder;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class UserManagerActivity extends BaseActivity {

    private Toolbar mToolbar;

    private ListView mListView;
    private CommonAdapter mAdapter;
    private List<Patient> mDatas;
    private UserManagerTask mTask;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);

        mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "用户列表");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initView();
        mDialog = DialogStylel.createLoadingDialog(this, "等待中...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        mTask = new UserManagerTask();
        mTask.execute();

    }

    private void initView() {

        mListView = (ListView) findViewById(R.id.user_manager_listview);

        mDatas = new ArrayList<Patient>();

        mAdapter   = new CommonAdapter(this,mDatas,R.layout.manager_user_item) {
            @Override
            public void convert(ViewHolder holder, int position) {

                Patient patient = mDatas.get(position);

                holder.setText(R.id.manager_user_item_item_name, patient.getUsername())
                        .setText(R.id.manager_user_item_phone, patient.getMobile())
                        .setText(R.id.manager_user_item_create_time, DateUtil.Timestamp2StringFormat2(patient.getCreateTime()));
                Button btn = holder.getView(R.id.manager_user_item_status);
                if (btn != null && patient.getState() == 1) {
                    btn.setText("正常");
                }else if (btn != null && patient.getState() == 0) {
                    btn.setBackgroundResource(R.drawable.textview_bg_pressed);
                    btn.setTextColor(Color.RED);
                    btn.setText("已停用");
                }else if (btn != null && patient.getState() == -1) {
                    btn.setBackgroundResource(R.drawable.textview_bg_pressed);
                    btn.setTextColor(Color.RED);
                    btn.setText("已删除");
                }


            }
        };

        mListView.setAdapter(mAdapter);

        //添加下拉加载
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    Log.i("skyjaj", "onscrooll");
                    if (mDatas == null || mDatas.size() == 0) {
                        return;
                    }
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        Log.i("skyjaj", "onscrooll end");
                        if (mDialog != null && !mDialog.isShowing()) {
                            mDialog.show();
                        }

                        mTask = new UserManagerTask();
                        mTask.setTime(mDatas.get(mDatas.size() - 1).getRemark());
                        mTask.execute();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }



    public class UserManagerTask extends AsyncTask<Void, Void, Boolean> {


        private String result;
        private boolean isEmpty;
        //通过时间线拉取数据
        private String time;

        public void setTime(String time) {
            this.time = time;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            List<LoginInformation> lif = DataSupport.where("state = ? and role = ?", "1","admin").find(LoginInformation.class);
            SystemUser systemUser = new SystemUser();
            List<Patient> patients=null;
            isEmpty = false;
            if (lif != null) {
                systemUser.setId(lif.get(0).getUid());
                systemUser.setToken(systemUser.getToken());
            } else {
                result = "找不到登录信息";
                return false;
            }

            try {
                systemUser.setRemark(time);
                result = OkHttpManager.post(ServerAddress.ADMIN_FIND_PATIENT_URL,OkHttpManager.gson.toJson(systemUser));
                Log.i("skyjaj", result);
            } catch (Exception e) {
                e.printStackTrace();
                result = "网络请求失败";
                return false;
            }

            try {
                patients = OkHttpManager.gson.fromJson(result, new TypeToken<List<Patient>>() {
                }.getType());
            } catch (Exception e) {
                result = "获取信息失败";
                return false;
            }

            if (patients != null && patients.size() == 0) {
                isEmpty = true;
                result = "没有可加载的数据了";
                return false;
            }

            if (patients != null && patients.size() != 0) {

                for (Patient p : patients) {
                    mDatas.add(p);
                }

                return true;
            } else {
                result = "获取信息失败";
                return false;
            }

        }


        @Override
        protected void onPostExecute(final Boolean success) {

            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            if (success) {
                if (mAdapter != null) {
                    mAdapter.setmData(mDatas);
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                if (isEmpty&&mDatas.size()==0) {
                    Toast.makeText(UserManagerActivity.this, "暂无用户数据", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(UserManagerActivity.this, result, Toast.LENGTH_SHORT).show();
                    //finish();
                }
            }
        }

    }




}
