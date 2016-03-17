package com.skyjaj.hors.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.skyjaj.hors.R;
import com.skyjaj.hors.bean.Department;
import com.skyjaj.hors.bean.Doctor;
import com.skyjaj.hors.db.DBDepartment;
import com.skyjaj.hors.utils.CommonAdapter;
import com.skyjaj.hors.utils.PinYinUtil;
import com.skyjaj.hors.utils.ViewHolder;
import com.skyjaj.hors.widget.ClearEditTextView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class SearchMoreActivity extends AppCompatActivity {

    private ListView mListView;
    private ListView mDoctorLv;
    private ImageView mBack;
    private ClearEditTextView mEditContent;
    private CommonAdapter<DBDepartment> mAdapter;
    private CommonAdapter<Doctor> mDoctorAdapter;
    private List<DBDepartment> mDatas;
    private LinearLayout mTipsView;
    private TextView mTipsTv;
    private List<Doctor> doctors;

    private List<DBDepartment> mDbDatas = new ArrayList<DBDepartment>();
    private boolean isFindDB = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_more);
        MyActivityManager.getInstance().addActivity(this);

        initView();

    }


    private void initView() {
        mListView = (ListView) findViewById(R.id.activity_search_listview);
        mBack = (ImageView) findViewById(R.id.activity_search_back);
        mEditContent = (ClearEditTextView) findViewById(R.id.activity_search_edittext);
        mTipsView = (LinearLayout) findViewById(R.id.search_more_tips_view);
        mTipsTv = (TextView) findViewById(R.id.search_more_tips_text);
        mDoctorLv = (ListView) findViewById(R.id.activity_search_listview_other);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mEditContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
//                Log.i("skyjaj", "data :" + s);
                reflashData(s + "");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        mDatas = new ArrayList<DBDepartment>();


        doctors = new ArrayList<Doctor>();

        Doctor d = new Doctor();
        d.setName("test ");
        doctors.add(d);

        mAdapter = new CommonAdapter<DBDepartment>(this,mDatas,R.layout.index_service_item) {
            @Override
            public void convert(ViewHolder holder, int position) {
                holder.setText(R.id.index_service_item_text, mDatas.get(position).getNameCn());
            }
        };
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchMoreActivity.this, IndexDepartmentDoctorActivity.class);
                Department department = new Department();
                department.setId(mDatas.get(position).getDepartId());
                Bundle bundle = new Bundle();
                bundle.putSerializable("department", department);
                intent.putExtras(bundle);
                startActivity(intent);
                Toast.makeText(SearchMoreActivity.this, "postion :" + position, Toast.LENGTH_SHORT).show();
            }
        });


        mDoctorAdapter = new CommonAdapter<Doctor>(this,doctors,R.layout.index_service_item) {
            @Override
            public void convert(ViewHolder holder, int position) {
                holder.setText(R.id.index_service_item_text,doctors.get(position).getName());
            }
        };

        mDoctorLv.setAdapter(mDoctorAdapter);

        mDoctorLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SearchMoreActivity.this, "postition :" + position, Toast.LENGTH_SHORT).show();
            }
        });

        mTipsView.setVisibility(View.VISIBLE);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDbDatas != null) {
            mDbDatas.clear();
            mDbDatas = null;
        }
        mDatas = null;
        MyActivityManager.getInstance().remove(this);
    }

    /**
     * 刷新数据
     *
     * @param input
     */
    public void reflashData(String input) {
        mDatas.clear();
        if (TextUtils.isEmpty(input)) {
            mTipsView.setVisibility(View.VISIBLE);
            mTipsTv.setText("请输入搜索科室、医生的关键字");

            return;
        } else {
//            String name = "眼科";
//            if(name.indexOf(input.toString()) != -1 || PinYinUtil.getPingYin(name).startsWith(input.toString())){
//                department.setNameCn(name);
//            }
//            if (!isFindDB) {
//                mDbDatas = DataSupport.findAll(DBDepartment.class);
//            }

            List<DBDepartment> ds = (List<DBDepartment>) DataSupport.where("nameIndex like ? or nameCn like ? or nameEn like ?", "%" + input + "%", "%" + input + "%", "%" + input + "%")
                    .find(DBDepartment.class);
//            List<DBDepartment> ds = new ArrayList<DBDepartment>();
//            for (DBDepartment d : mDbDatas) {
//                if ((!TextUtils.isEmpty(d.getNameCn()) && d.getNameCn().indexOf(input) != -1) ||
//                        !TextUtils.isEmpty(d.getNameEn()) && d.getNameEn().indexOf(input) != -1) {
//                    ds.add(d);
//                }
//            }

            if (ds != null && ds.size() == 0) {
                mTipsView.setVisibility(View.VISIBLE);
                mTipsTv.setText("没找到相关数据");
            } else {
                mListView.removeHeaderView(mTipsView);
                mTipsView.setVisibility(View.GONE);
            }
            mDatas = ds;
            mAdapter.setmData(mDatas);
            mAdapter.notifyDataSetChanged();
        }

    }





    public class NetworkTask extends AsyncTask<Void, Void, Boolean> {


        private String url;//链接
        private String content;//查找内容


        @Override
        protected Boolean doInBackground(Void... params) {



            return null;
        }
    }




}
