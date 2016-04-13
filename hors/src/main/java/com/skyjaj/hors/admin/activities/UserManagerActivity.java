package com.skyjaj.hors.admin.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.BaseActivity;
import com.skyjaj.hors.adapter.TimestampTypeAdapter;
import com.skyjaj.hors.admin.wigwet.UserManagerForOnItemLongClick;
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
import com.skyjaj.hors.widget.DialogTips;

import org.litepal.crud.DataSupport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserManagerActivity extends BaseActivity {

    private Toolbar mToolbar;

    private ListView mListView;
    private CommonAdapter mAdapter;
    private List<Patient> mDatas;
    private UserManagerTask mTask;
    private Dialog mDialog;
    private boolean isOnLongClick;
    private static final int DISABLE = 0 , ENABLE = 1, DELETE = 2 , MESSAGE = 3;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Patient patient = (Patient) msg.obj;
            switch (msg.what) {
                case DISABLE:
                    if (patient != null && mAdapter != null) {
                        patient.setState(0);
                        mAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(UserManagerActivity.this, "已禁用", Toast.LENGTH_SHORT).show();
                    break;
                case ENABLE:
                    if (patient != null && mAdapter != null) {
                        patient.setState(1);
                        mAdapter.notifyDataSetChanged();
                    }
                    Toast.makeText(UserManagerActivity.this, "已恢复", Toast.LENGTH_SHORT).show();
                    break;
                case DELETE:
//                    if (patient != null && mAdapter != null && mDatas != null) {
//                        mDatas.remove(patient);
//                        mAdapter.notifyDataSetChanged();
//                    }
                    mTask = new UserManagerTask();
                    if (mDatas != null) {
                        mDatas.clear();
                    }
                    mTask.setTime(null);
                    mTask.execute();
                    if (mDialog != null) {
                        mDialog.show();
                    }
                    Toast.makeText(UserManagerActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE:
                    Toast.makeText(UserManagerActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    };


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
                holder.setText(R.id.manager_user_item_name, patient.getUsername())
                        .setText(R.id.manager_user_item_phone, patient.getMobile())
                        ;

                try {
                    holder.setText(R.id.manager_user_item_create_time,DateUtil.Timestamp2StringFormat2(patient.getCreateTime()));
                } catch (Exception e) {
                    holder.setText(R.id.manager_user_item_create_time, "创建时间未知");
                    e.printStackTrace();
                }

                Button btn = holder.getView(R.id.manager_user_item_status);
                if (btn != null && patient.getState() == 1) {
                    btn.setBackgroundResource(R.drawable.selector_button);
                    btn.setTextColor(Color.WHITE);
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
        isOnLongClick = false;

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isOnLongClick) {
                    return;
                }
                Intent intent = new Intent(UserManagerActivity.this, UpdatePatientActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("patient", mDatas.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


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


        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final Patient p = mDatas.get(position);
                UserManagerForOnItemLongClick.OnItemClickListener listener =
                        new UserManagerForOnItemLongClick.OnItemClickListener() {
                            @Override
                            public void onItemClick(int resId) {
                                isOnLongClick = false;
                                switch (resId) {
                                    case R.id.manager_user_dialog_enable:
//                                        Toast.makeText(UserManagerActivity.this, "enable ", Toast.LENGTH_SHORT).show();
                                        runNetworkTask(ServerAddress.ADMIN_ENABLE_PATIENT_URL, p, ENABLE);
                                        break;
                                    case R.id.manager_user_dialog_disable:
                                        runNetworkTask(ServerAddress.ADMIN_DISABLE_PATIENT_URL,p,DISABLE);
                                        break;
                                    case R.id.manager_user_dialog_delete:
                                        DialogTips.OnDialogItemClickListener deleteListener =
                                                new DialogTips.OnDialogItemClickListener() {
                                                    @Override
                                                    public void onDialogItemClick(View view) {
                                                        switch (view.getId()) {
                                                            case R.id.dialog_tips_cancel:
                                                                break;
                                                            case R.id.dialog_tips_ok:
                                                                runNetworkTask(ServerAddress.ADMIN_DELETE_PATIENT_URL, p, DELETE);
                                                                break;
                                                        }
                                                    }
                                                };

                                        DialogTips tips = new DialogTips(UserManagerActivity.this, deleteListener);
                                        tips.setTitle("您确定要删除" + p.getMobile() + "吗?");
                                        break;
                                }
                            }

                            @Override
                            public void onDismissListener(DialogInterface dialog) {
                                isOnLongClick = false;
                            }

                            @Override
                            public void onCancelListener(DialogInterface dialog) {

                            }
                        };
                UserManagerForOnItemLongClick longClickDialog =
                        new UserManagerForOnItemLongClick(listener, UserManagerActivity.this);

                if (p != null && p.getState() == 1) {
                    longClickDialog.setViewVisible(R.id.manager_user_dialog_enable, View.GONE);
                    longClickDialog.setViewVisible(R.id.manager_user_dialog_disable, View.VISIBLE);
                    longClickDialog.setViewVisible(R.id.manager_user_dialog_delete, View.VISIBLE);
                } else if (p != null && p.getState() == 0) {
                    longClickDialog.setViewVisible(R.id.manager_user_dialog_enable, View.VISIBLE);
                    longClickDialog.setViewVisible(R.id.manager_user_dialog_disable, View.GONE);
                } else {
                    longClickDialog.setViewVisible(R.id.manager_user_dialog_enable, View.GONE);
                    longClickDialog.setViewVisible(R.id.manager_user_dialog_disable, View.GONE);
                    longClickDialog.setViewVisible(R.id.manager_user_dialog_delete, View.GONE);
                }
                isOnLongClick = true;
                return false;
            }
        });


    }

    /**
     * 启用线程
     *
     * @param url
     * @param patient
     * @param what
     */
    public synchronized void runNetworkTask(final String url, final Patient patient, final int what) {

        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (patient == null) {
                    return;
                }
                String result = "";
                try {
                    SystemUser systemUser = new SystemUser();
                    systemUser.setRemark(patient.getId());
                    result = OkHttpManager.post(url, new Gson().toJson(systemUser));
                    Log.i("skyjaj", "result :" + result);
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "无法连接网络";
                }
                if (!TextUtils.isEmpty(result)) {
                    result = result.replaceAll("\"", "");
                }

                Message msg = new Message();

                if ("success".equals(result)) {
                    msg.what = what;
                    msg.obj = patient;
                    mHandler.sendMessage(msg);
                } else {
                    msg.what = MESSAGE;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }


            }
        }).start();
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (R.id.action_add == item.getItemId()) {
            Intent intent = new Intent(this, UpdatePatientActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
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
                Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).create();
                patients = gson.fromJson(result, new TypeToken<List<Patient>>() {
                }.getType());
                Log.i("skyjaj",  "patients "+patients);
            } catch (Exception e) {
                result = "无法解释数据";
                return false;
            }

            if (patients == null || patients.size() == 0) {
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
            Log.i("skyjaj",  "success "+success);
            if (success) {
                Log.i("skyjaj",  "mDatas "+mDatas.size());
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
