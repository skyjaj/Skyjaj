package com.skyjaj.hors.admin.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.BaseActivity;
import com.skyjaj.hors.activities.IndexDepartmentDoctorActivity;
import com.skyjaj.hors.activities.MyActivityManager;
import com.skyjaj.hors.adapter.CommonAdapter;
import com.skyjaj.hors.adapter.TimestampTypeAdapter;
import com.skyjaj.hors.admin.wigwet.DepartmentManagerForOnItemLongClick;
import com.skyjaj.hors.admin.wigwet.DoctorManagerForOnItemLongClickDialog;
import com.skyjaj.hors.bean.BaseMessage;
import com.skyjaj.hors.bean.Department;
import com.skyjaj.hors.bean.IndexServiceMenu;
import com.skyjaj.hors.bean.Reservation;
import com.skyjaj.hors.bean.SystemUser;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentManagerActivity extends BaseActivity {

    private ListView mListView;

    private Toolbar mToolbar;


    private Dialog dialog;

    private boolean isLongClick;

    private final int DELETE = 0,STOP = 1, FALSE = 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //delete
                case DELETE:
                    departmentList.remove(msg.obj);
                    mAdapter.setmDatas(departmentList);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(DepartmentManagerActivity.this,"已删除", Toast.LENGTH_SHORT).show();
                    break;
                //STOP
                case STOP:
                    Department department = (Department) msg.obj;
                    department.setState(0);
                    mAdapter.setmDatas(departmentList);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(DepartmentManagerActivity.this,"已停诊该科室", Toast.LENGTH_SHORT).show();
                    break;
                case FALSE:
                    Toast.makeText(DepartmentManagerActivity.this,(String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_department_manager);
        mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "科室管理");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initAppointmentView();

    }


    List<Department> departmentList=new ArrayList<Department>();
    CommonAdapter<Department> mAdapter =null;
    NetworkTask mNetworkTask = null;

    private void initAppointmentView() {

        mListView = (ListView) findViewById(R.id.department_manager_listview);
        Map<BaseMessage.Type, Integer> itemViews = new HashMap<BaseMessage.Type, Integer>();
        itemViews.put(BaseMessage.Type.INCOMING, R.layout.index_service_item);
        itemViews.put(BaseMessage.Type.OUTCOMING, R.layout.index_service_item_text);

        mAdapter = new CommonAdapter<Department>(this, departmentList, itemViews) {
            @Override
            public void convert(com.skyjaj.hors.adapter.ViewHolder viewHolder, Department department) {

                if (department.getItemType() == BaseMessage.Type.INCOMING) {
                    viewHolder.setImageResource(R.id.index_service_item_icon, R.drawable.menu_feedback_icon)
                            .setText(R.id.index_service_item_text, department.getNameCn());
                } else {
                    viewHolder.setText(R.id.index_service_item_tv, department.getNameCn());
                }
            }
        };
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isLongClick) {
                    return ;
                }
                Department department = departmentList.get(position);
                if (department.getItemType() == BaseMessage.Type.INCOMING) {
                    Intent intent = new Intent(DepartmentManagerActivity.this, IndexDepartmentDoctorActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("department", department);
                    intent.putExtras(mBundle);
                    DepartmentManagerActivity.this.startActivity(intent);
                }
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                DepartmentManagerForOnItemLongClick.OnItemClickListener listener =
                        new DepartmentManagerForOnItemLongClick.OnItemClickListener() {
                            @Override
                            public void onItemClick(int resId) {
                                isLongClick = false;
                                switch (resId) {
                                    case R.id.manager_department_update:
                                        Intent intent = new Intent(DepartmentManagerActivity.this, UpdateDepartmentActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("department", departmentList.get(position));
                                        intent.putExtras(bundle);
                                        startActivity(intent);
//                                        Toast.makeText(DepartmentManagerActivity.this, "manager_department_update", Toast.LENGTH_SHORT).show();
                                        break;
                                    case R.id.manager_department_delete:
                                        deleteDepartment(ServerAddress.ADMIN_DELETE_DEPARTMENT_URL, departmentList.get(position),DELETE);
//                                        Toast.makeText(DepartmentManagerActivity.this, "manager_department_delete", Toast.LENGTH_SHORT).show();
                                        break;
                                    case R.id.manager_department_stop:
                                        deleteDepartment(ServerAddress.ADMIN_STOP_DEPARTMENT_URL, departmentList.get(position),STOP);
//                                        Toast.makeText(DepartmentManagerActivity.this, "manager_department_stop", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }

                            @Override
                            public void onDismissListener(DialogInterface dialog) {
//                                Toast.makeText(DepartmentManagerActivity.this, "onDismissListener", Toast.LENGTH_SHORT).show();
                                isLongClick = false;
                            }

                            @Override
                            public void onCancelListener(DialogInterface dialog) {
//                                Toast.makeText(DepartmentManagerActivity.this, "onCancelListener", Toast.LENGTH_SHORT).show();
                            }
                        };
                DepartmentManagerForOnItemLongClick longClick = new DepartmentManagerForOnItemLongClick(listener, DepartmentManagerActivity.this);
                isLongClick = true;
                return false;
            }
        });

        dialog = DialogStylel.createLoadingDialog(this, "加载中..");
        dialog.show();
        mNetworkTask = new NetworkTask();
        mNetworkTask.execute();
    }


    public void deleteDepartment(final String url,final Department department,final int what) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemUser systemUser = new SystemUser();
                systemUser.setId(department.getId());
                systemUser.setRemark(department.getId());
                String result="";
                try {
                    result = OkHttpManager.post(url, new Gson().toJson(systemUser));
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "无法请求服务器,请稍后重试";
                }
                Message msg = new Message();
                if (!TextUtils.isEmpty(result)) {
                    result = result.replaceAll("\"", "");
                }
                if ("已停".equals(result) || "已删除".equals(result)) {
                    msg.what = what;
                    msg.obj = department;
                    mHandler.sendMessage(msg);
                } else {
                    msg.what = FALSE;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (R.id.action_add == item.getItemId()) {
            Intent intent = new Intent(this, UpdateDepartmentActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }




    @Override
    protected void onDestroy() {
        dialog = null;
        super.onDestroy();

    }

    //异步任务
    public class NetworkTask extends AsyncTask<Void, Void, Boolean> {

        NetworkTask() {}

        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d("xys", "doInBackground");


            try {
                String str = null;
                str = OkHttpManager.post(ServerAddress.FIND_ALL_DEPARTMENT_URL);
                Log.d("xys","str "+str);
                Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).create();
                List<Department> departments= gson.fromJson(str, new TypeToken<List<Department>>() {}.getType());
                Map<String, Department> map = new HashMap<String,Department>();

                for (Department d:departments) {
                    if ("0".equals(d.getParentId())) {
                        map.put(d.getId(), d);
                    }
                }
                Log.d("xys","map  "+map.size());

                for (Department dd : departments) {
                    if (!"0".equals(dd.getParentId())) {
                        if (map.get(dd.getParentId())!=null&&map.get(dd.getParentId()).getChildren() != null) {
                            map.get(dd.getParentId()).getChildren().add(dd);
                        } else if (map.get(dd.getParentId()) != null) {
                            map.get(dd.getParentId()).setChildren(new ArrayList<Department>());
                            map.get(dd.getParentId()).getChildren().add(dd);
                        }
                    }
                }

                departments.clear();
                departments = new ArrayList<Department>(map.values());
                Collections.sort(departments, new Comparator<Department>() {
                    public int compare(Department arg0, Department arg1) {
                        return arg0.getDepartmentOrder().compareTo(arg1.getDepartmentOrder());
                    }
                });

                for (Department ds : departments) {
                    if (ds.getChildren() == null) {
                        Department department = new Department();
                        department.setItemType(BaseMessage.Type.OUTCOMING);
                        department.setNameCn(ds.getNameCn());
                        departmentList.add(department);
                        departmentList.add(ds);
                    }else{
                        departmentList.add(ds);
                        ds.setItemType(BaseMessage.Type.OUTCOMING);
                        for (Department dd : ds.getChildren()) {
                            departmentList.add(dd);
                        }
                    }
                }
                map = null;
                departments=null;
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
                mAdapter.setmDatas(departmentList);
                mAdapter.notifyDataSetChanged();
            }else {
                Toast.makeText(DepartmentManagerActivity.this, "暂时无法连接服务器", Toast.LENGTH_SHORT).show();
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