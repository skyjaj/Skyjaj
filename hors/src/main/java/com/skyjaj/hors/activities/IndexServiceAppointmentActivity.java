package com.skyjaj.hors.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.skyjaj.hors.adapter.CommonAdapter;
import com.skyjaj.hors.adapter.CommonSearchAdapter;
import com.skyjaj.hors.adapter.SearchAdapter;
import com.skyjaj.hors.adapter.TimestampTypeAdapter;
import com.skyjaj.hors.bean.BaseMessage;
import com.skyjaj.hors.bean.Department;
import com.skyjaj.hors.bean.IndexServiceMenu;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.PinYinUtil;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;
import com.skyjaj.hors.widget.CustomProgressDialog;
import com.skyjaj.hors.widget.PinyinBarView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 显示科室信息
 * Created by Administrator on 2016/1/21.
 */
public class IndexServiceAppointmentActivity extends AppCompatActivity implements PinyinBarView.OnTouchingLetterChangedListener{

    private ListView mListView;

    private Toolbar mToolbar;
    private PinyinBarView mPinyinBarView;
    private TextView mPinyinTips;


    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        MyActivityManager.getInstance().addActivity(this);
        int viewId = intent.getIntExtra("index_menu", 0);


        switch (viewId) {

            case R.string.index_service_appointment:
                setContentView(R.layout.activity_index_service_appointment);
                mToolbar = ToolbarStyle.initToolbar(this, R.id.index_service_appointment_toolbar, R.string.index_service_appointment);
                initAppointmentView();
                break;
            case R.string.index_service_queue_waiting:
                setContentView(R.layout.activity_index_service_queue);
                mToolbar = ToolbarStyle.initToolbar(this, R.id.index_service_queue_toolbar, R.string.index_service_queue_waiting);
                initQueueView();
                break;
        }


    }

    private void initQueueView() {
        mListView = (ListView) findViewById(R.id.index_service_queue_listview);
        List<IndexServiceMenu> mDatas = new ArrayList<IndexServiceMenu>();


        IndexServiceMenu menu = new IndexServiceMenu(R.drawable.menu_feedback_icon, this.getString(R.string.index_service_appointment), 0);
        menu.setItemType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);

        menu = new IndexServiceMenu(0, "", 0);
        menu.setItemType(BaseMessage.Type.OUTCOMING);
        mDatas.add(menu);

        menu = new IndexServiceMenu(R.drawable.men_scan_icon, this.getString(R.string.index_service_queue_waiting), 0);
        menu.setItemType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);


        Map<BaseMessage.Type, Integer> itemViews = new HashMap<BaseMessage.Type, Integer>();
        itemViews.put(BaseMessage.Type.INCOMING, R.layout.index_service_item);
        itemViews.put(BaseMessage.Type.OUTCOMING, R.layout.index_service_item_bg);

        CommonAdapter<IndexServiceMenu> mAdapter = new CommonAdapter<IndexServiceMenu>(this, mDatas, itemViews) {
            @Override
            public void convert(com.skyjaj.hors.adapter.ViewHolder viewHolder, IndexServiceMenu indexServiceMenu) {
                if (indexServiceMenu.getItemType() == BaseMessage.Type.INCOMING) {
                    viewHolder.setImageResource(R.id.index_service_item_icon, indexServiceMenu.getResId())
                            .setText(R.id.index_service_item_text, indexServiceMenu.getText());
                }
            }
        };

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(IndexServiceAppointmentActivity.this, "position :" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    List<Department> departmentList=new ArrayList<Department>();
    CommonSearchAdapter<Department> mAdapter =null;
    NetworkTask mNetworkTask = null;

    private void initAppointmentView() {

        mListView = (ListView) findViewById(R.id.index_service_appointment_listview);
        mPinyinBarView = (PinyinBarView) findViewById(R.id.index_service_appointment_pinyinbar);
        mPinyinTips = (TextView) findViewById(R.id.index_service_appointment_tips);
        mPinyinBarView.setTextView(mPinyinTips);
        mPinyinBarView.setOnTouchingLetterChangedListener(this);
        Map<BaseMessage.Type, Integer> itemViews = new HashMap<BaseMessage.Type, Integer>();
        itemViews.put(BaseMessage.Type.INCOMING, R.layout.index_service_item);
        itemViews.put(BaseMessage.Type.OUTCOMING, R.layout.index_service_item_text);

        mAdapter = new CommonSearchAdapter<Department>(this, departmentList, itemViews) {
            @Override
            public Object[] getSections() {
                return new Object[0];
            }

            @Override
            public int getPositionForSection(int section) {
                for (int i = 0; i < getCount(); i++) {
                    String sortStr = mDatas.get(i).getIndex();
                    if (TextUtils.isEmpty(sortStr)) {
                        continue;
                    }
                    char firstChar = sortStr.toUpperCase().charAt(0);
                    if (firstChar == section) {
                        return i;
                    }
                }
                return -1;
            }

            @Override
            public int getSectionForPosition(int position) {
                String str = mDatas.get(position).getIndex();
                return TextUtils.isEmpty(str)?-1:str.charAt(0);
            }

            @Override
            public void convert(com.skyjaj.hors.adapter.ViewHolder viewHolder, Department department) {
                if (department.getItemType() == BaseMessage.Type.INCOMING) {
                    viewHolder.setImageResource(R.id.index_service_item_icon, R.drawable.menu_feedback_icon)
                            .setText(R.id.index_service_item_text, department.getNameCn());
                } else {
                    viewHolder.setText(R.id.index_service_item_tv, department.getNameCn())
                            .setTextColor(R.id.index_service_item_tv, Color.WHITE)
                            .setViewBackground(R.id.index_service_text_view, Color.parseColor("#FFB4B0B0"));
                }
            }
        };
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(IndexServiceAppointmentActivity.this, "position :" + position, Toast.LENGTH_SHORT).show();
                Department department = departmentList.get(position);
                if (department.getItemType() == BaseMessage.Type.INCOMING) {
                    Intent intent = new Intent(IndexServiceAppointmentActivity.this, IndexDepartmentDoctorActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("department", department);
                    intent.putExtras(mBundle);
                    IndexServiceAppointmentActivity.this.startActivity(intent);
                }
            }
        });
//        dialog = new CustomProgressDialog(this, "加载中...", R.anim.loading_frame);
//        dialog.show();
        dialog = DialogStylel.createLoadingDialog(this, "加载中..");
        dialog.show();
        mNetworkTask = new NetworkTask();
        mNetworkTask.execute();
    }


    @Override
    protected void onDestroy() {
        dialog = null;
        MyActivityManager.getInstance().remove(this);
        super.onDestroy();

    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = mAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            mListView.setSelection(position);
        }
    }

    //异步任务
    public class NetworkTask extends AsyncTask<Void, Void, Boolean> {

        NetworkTask() {

        }

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
                        //可以通过order来排列
                        //return arg0.getDepartmentOrder().compareTo(arg1.getDepartmentOrder());
                        //也可以通过名称来排列
                        return PinYinUtil.getPingYin(arg0.getNameCn()).compareTo(PinYinUtil.getPingYin(arg1.getNameCn()));
                    }
                });



                for (Department ds : departments) {

                    if (ds.getChildren() == null) {
                        Department department = new Department();
                        department.setItemType(BaseMessage.Type.OUTCOMING);
                        department.setNameCn(ds.getNameCn());
                        //汉字转换成拼音
                        String pinyin = PinYinUtil.getPingYin(department.getNameCn());
                        String sortString = TextUtils.isEmpty(pinyin) ? "" :pinyin.substring(0, 1).toUpperCase();
                        // 正则表达式，判断首字母是否是英文字母
                        if(!TextUtils.isEmpty(sortString)&&sortString.matches("[A-Z]")){
                            department.setIndex(sortString.toUpperCase());
                        }else{
                            department.setIndex("#");
                        }
                        departmentList.add(department);
                        departmentList.add(ds);
                    }else{
                        departmentList.add(ds);
                        ds.setItemType(BaseMessage.Type.OUTCOMING);
                        //汉字转换成拼音
                        String pinyin = PinYinUtil.getPingYin(ds.getNameCn());
                        String sortString = TextUtils.isEmpty(pinyin) ? "" :pinyin.substring(0, 1).toUpperCase();
                        // 正则表达式，判断首字母是否是英文字母
                        if(!TextUtils.isEmpty(sortString)&&sortString.matches("[A-Z]")){
                            ds.setIndex(sortString.toUpperCase());
                        }else{
                            ds.setIndex("#");
                        }
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
                Toast.makeText(IndexServiceAppointmentActivity.this, "暂时无法连接服务器", Toast.LENGTH_SHORT).show();
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

