package com.skyjaj.hors.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.adapter.CommonAdapter;
import com.skyjaj.hors.adapter.TimestampTypeAdapter;
import com.skyjaj.hors.adapter.ViewHolder;
import com.skyjaj.hors.bean.BaseMessage;
import com.skyjaj.hors.bean.Department;
import com.skyjaj.hors.bean.Doctor;
import com.skyjaj.hors.db.DBDepartment;
import com.skyjaj.hors.utils.DBUtil;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.PinYinUtil;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.widget.ClearEditTextView;

import org.litepal.crud.DataSupport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchMoreActivity extends BaseActivity {

    private ListView mListView;
    private ImageView mBack;
    private HashMap<BaseMessage.Type, Integer> views;
    private TextView searchTv;

    private ClearEditTextView mEditContent;
    private CommonAdapter<BaseMessage> mAdapter;
    private List<BaseMessage> mDatas;
    private LinearLayout mTipsView;
    private TextView mTipsTv;

    private String input;
    private View view;
    ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_more);

        initView();

    }



    private void initView() {
        mListView = (ListView) findViewById(R.id.activity_search_listview);
        mBack = (ImageView) findViewById(R.id.activity_search_back);
        mEditContent = (ClearEditTextView) findViewById(R.id.activity_search_edittext);
        mTipsView = (LinearLayout) findViewById(R.id.search_more_tips_view);
        mTipsTv = (TextView) findViewById(R.id.search_more_tips_text);


        view = getLayoutInflater().inflate(R.layout.index_service_item, null);
        view.setBackgroundResource(R.drawable.selector_textview);
        imageView = (ImageView) view.findViewById(R.id.index_service_item_icon);
        imageView.setImageResource(R.drawable.actionbar_search_icon);
        searchTv = (TextView) view.findViewById(R.id.index_service_item_text);
        searchTv.setText("请输入科室、医生姓名的关键字");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialog == null) {
                    dialog = DialogStylel.createLoadingDialog(SearchMoreActivity.this, "搜索中..");
                }else if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                }
                task = new NetworkTask(mEditContent.getText().toString());
                task.execute();
//                Toast.makeText(SearchMoreActivity.this, "searchTv view ", Toast.LENGTH_SHORT).show();

            }
        });

        mTipsTv.setBackgroundResource(R.drawable.selector_textview);

//        mTipsTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                task = new NetworkTask(mEditContent.getText().toString());
//                task.execute();
//                Toast.makeText(SearchMoreActivity.this, "text view ", Toast.LENGTH_SHORT).show();
//            }
//        });

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
//                Log.i("skyjaj", "input " + str);
                String str = s + "";
                input = str;
                reflashData(str);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        mDatas = new ArrayList<BaseMessage>();


        views = new HashMap<BaseMessage.Type, Integer>();
        views.put(BaseMessage.Type.INCOMING, R.layout.index_service_item);
        views.put(BaseMessage.Type.OUTCOMING, R.layout.index_service_item);


        mAdapter = new CommonAdapter<BaseMessage>(this, mDatas, views) {

            @Override
            public void convert(ViewHolder viewHolder, BaseMessage baseMessage) {

                if (baseMessage.getItemType() == BaseMessage.Type.INCOMING) {
                    Department department = (Department) baseMessage;
                    viewHolder.setText(R.id.index_service_item_text, department.getNameCn());
                    if (!TextUtils.isEmpty(input)) {
                        int start = department.getNameCn().indexOf(input);
//                        Log.i("skyjaj", "start1 : " + start);
                        int end = -1;
                        String index = PinYinUtil.getPinYinHeadChar(department.getNameCn());
                        if (start == -1) {
//                            Log.i("skyjaj", "index :" + index);
                            if (!TextUtils.isEmpty(index) && index.length() >= input.length()) {
                                start = index.indexOf(input);
                            }
//                            Log.i("skyjaj", "start3 : " + start);
                        }

                        int arr[] = null;
                        if (start == -1) {
                            arr = isConstants(input.toLowerCase(), department.getNameCn());
                            if (arr != null) {
                                start = arr[0];
//                                Log.i("skyjaj", " contents :" + start + " end " + end);
                            }
                        }

                        if (start != -1) {
                            end = start + input.length();
                            if (arr != null) {
                                end = arr[1];
                            }
//                            Log.i("skyjaj", "start :" + start + " end :" + end);
                            viewHolder.setSubTextColor(R.id.index_service_item_text, Color.GREEN, start, end);
                        }
                    }
                } else {
                    Doctor doctor = (Doctor) baseMessage;
                    viewHolder.setText(R.id.index_service_item_text, doctor.getName())
                                .setImageResource(R.id.index_service_item_icon,R.drawable.me);

                }

            }
        };
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseMessage baseMessage = mDatas.get(position);
                if (baseMessage.getItemType() == BaseMessage.Type.INCOMING) {
                    Intent intent = new Intent(SearchMoreActivity.this, IndexDepartmentDoctorActivity.class);Department department = (Department) baseMessage;
                    department.setId(department.getId());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("department", department);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else if (baseMessage.getItemType() == BaseMessage.Type.OUTCOMING) {
                    Intent intent = new Intent(SearchMoreActivity.this, CalenderActivity.class);
                    Bundle bundle = new Bundle();
                    Doctor doctor = (Doctor) baseMessage;
                    bundle.putSerializable("doctor", doctor);
                    Department department = new Department();
                    department.setId(doctor.getDepartmentId());
                    bundle.putSerializable("department", department);
//                    intent.putExtra("department_name", department.getNameCn());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }



                Toast.makeText(SearchMoreActivity.this, "postion :" + position, Toast.LENGTH_SHORT).show();
            }
        });

        mListView.addFooterView(view);

        imageView.setVisibility(View.GONE);


//        mTipsView.setVisibility(View.VISIBLE);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatas = null;
    }

    /**
     * 刷新数据
     *
     * @param input
     */
    public void reflashData(String input) {
        mDatas.clear();

        if (TextUtils.isEmpty(input)) {
//            mTipsView.setVisibility(View.VISIBLE);
            searchTv.setText("请输入科室、医生姓名的关键字");
            imageView.setVisibility(View.GONE);
            mAdapter.setmDatas(mDatas);
            mAdapter.notifyDataSetChanged();
            return;
        } else {
//            String name = "眼科";
//            if(name.indexOf(input.toString()) != -1 || PinYinUtil.getPingYin(name).startsWith(input.toString())){
//                department.setNameCn(name);
//            }
//            if (!isFindDB) {
//                mDbDatas = DataSupport.findAll(DBDepartment.class);
//            }

            List<DBDepartment> ds = (List<DBDepartment>) DataSupport.where("nameIndex like ? or nameCn like ? or nameEn like ?", "%" + input + "%", "%" + input + "%","%"+input + "%")
                    .find(DBDepartment.class);
//            List<DBDepartment> ds = new ArrayList<DBDepartment>();
//            for (DBDepartment d : mDbDatas) {
//                if ((!TextUtils.isEmpty(d.getNameCn()) && d.getNameCn().indexOf(input) != -1) ||
//                        !TextUtils.isEmpty(d.getNameEn()) && d.getNameEn().indexOf(input) != -1) {
//                    ds.add(d);
//                }
//            }

//            if (ds != null && ds.size() == 0) {
//                mTipsView.setVisibility(View.VISIBLE);
//                mTipsTv.setText("没找到相关数据");
//            }
            for (DBDepartment db : ds) {
                if (mDatas != null) {
                    mDatas.add(DBUtil.turn2Department(db));
                }
            }
            mAdapter.setmDatas(mDatas);
            mAdapter.notifyDataSetChanged();
            view.setVisibility(View.VISIBLE);
            searchTv.setText("搜一搜: " + input);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 查找字串 对应位置 ，返回start end
     * @param input
     * @param contents
     * @return
     */
    public int[] isConstants(@NonNull String input,@NonNull String contents) {
        String temp = input;
        String pinyin = PinYinUtil.getPinYinHeadChar(contents);
        System.out.println(PinYinUtil.getPingYin(contents));
        char chars[] = contents.toCharArray();
        int index = PinYinUtil.getPingYin(contents).indexOf(input);
        if (index == -1) {
            return null;
        }
        index = pinyin.indexOf(input.charAt(0));
        if (index == -1) {
            return null;
        }
        int start = -1, end = -1;
        for (int i = index; i < contents.length(); i++) {
            String pin = PinYinUtil.getPingYin(chars[i] + "");
            if (!TextUtils.isEmpty(input) && input.contains(pin) && input.startsWith(pin)) {
                if (start == -1) {
                    start = i;
                    end = i + 1;
                } else {
                    end = i + 1;
                }
                input = input.replaceAll(pin, "");
                if (TextUtils.isEmpty(input)) {
                    break;
                }
            } else if (!TextUtils.isEmpty(input) && pin.contains(input) && pin.startsWith(input)) {
                if (start == -1) {
                    start = i;
                    end = i + 1;
                } else {
                    end = i + 1;
                }
                break;
            } else {
                //两者不包含，且未匹配到
                if (start == -1 && !TextUtils.isEmpty(input))
                {
                    continue;
                }
                //可能前面子串被包含，重新调整  例子：霸霸白 输入：bai
                else if (start != -1 &&temp.contains(PinYinUtil.getPingYin(chars[i-1] + "")) && !TextUtils.isEmpty(input))
                {
                    input = temp;
                    start = -1;
                    end = -1;
                    i--;
                    continue;
                }
                start = -1;
                break;
            }
        }
        if (start != -1) {
            return new int[]{start, end};
        }
        return null;
    }


    private NetworkTask task;
    private Dialog dialog;
    private List<Doctor> doctors;


    public class NetworkTask extends AsyncTask<Void, Void, Boolean> {


        private String url;//链接
        private String content;//查找内容
        private String result;
        private String input;

        public NetworkTask(String input) {
            this.input = input;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Log.i("skyjaj", result + " input :" + input);
            try {
                Doctor doctor = new Doctor();
                doctor.setItemType(null);
                doctor.setName(input);
                result = OkHttpManager.post(ServerAddress.FIND_DOCTORS_BY_NAME, new Gson().toJson(doctor));
                Log.i("skyjaj", "result " + result);
            } catch (Exception e) {
                result = "无法访问网络，请稍候重试";
                e.printStackTrace();
                return false;
            }

            try {
                Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).create();
                doctors = gson.fromJson(result, new TypeToken<List<Doctor>>() {
                }.getType());

            } catch (Exception e) {

                e.printStackTrace();
                return false;
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean success) {


            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            if (success&&doctors!=null&&doctors.size()>0) {

                Log.i("skyjaj", "success " + doctors);
                if (mDatas != null) {
                    mDatas.clear();
                }
                for (Doctor d : doctors) {
                    if (mDatas != null) {
                        d.setItemType(BaseMessage.Type.OUTCOMING);
                        mDatas.add(d);
                    }
                }

                mAdapter.setmDatas(mDatas);
                mAdapter.notifyDataSetChanged();
                view.setVisibility(View.GONE);

            } else {

                Log.i("skyjaj", "false" + result);
                if (task != null) {
                    task.cancel(true);
                    task = null;
                }
                mDatas.clear();
                mAdapter.setmDatas(mDatas);
                mAdapter.notifyDataSetChanged();
                imageView.setVisibility(View.GONE);
                searchTv.setText("没找到相关信息!");

            }

            super.onPostExecute(success);

        }
    }


}
