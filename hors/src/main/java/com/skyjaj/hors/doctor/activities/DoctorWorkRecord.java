package com.skyjaj.hors.doctor.activities;

import android.app.Dialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.BaseActivity;
import com.skyjaj.hors.activities.MyActivityManager;
import com.skyjaj.hors.adapter.CommonAdapter;
import com.skyjaj.hors.adapter.TimestampTypeAdapter;
import com.skyjaj.hors.adapter.ViewHolder;
import com.skyjaj.hors.bean.BaseMessage;
import com.skyjaj.hors.bean.LoginInformation;
import com.skyjaj.hors.bean.Reservation;
import com.skyjaj.hors.utils.DateUtil;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;

import org.litepal.crud.DataSupport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorWorkRecord extends BaseActivity {

    private Toolbar mToolbar;
    private ListView mListView;
    private Map<BaseMessage.Type,Integer> mViews;
    private List<Reservation> mDatas;
    private CommonAdapter mAdapter;

    private NetworkTask mTask;
    private Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_work_record);
        mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "我的工作记录");
        initView();
        initDatas();

        mTask = new NetworkTask();
        dialog = DialogStylel.createLoadingDialog(this, "加载中...");
        mTask.execute();
    }

    private void initDatas() {

        mDatas = new ArrayList<Reservation>();
        mViews = new HashMap<BaseMessage.Type, Integer>();
        mViews.put(BaseMessage.Type.OUTCOMING, R.layout.index_service_item_text);
        mViews.put(BaseMessage.Type.INCOMING, R.layout.doctor_work_record_item);


        mAdapter = new CommonAdapter<Reservation>(this,mDatas,mViews) {
            @Override
            public void convert(ViewHolder viewHolder, Reservation reservation) {
                if (reservation.getItemType() == BaseMessage.Type.INCOMING) {
                    viewHolder.setText(R.id.record_name, reservation.getName())
                            .setText(R.id.record_comment_case,reservation.getIsComment()>0?(reservation.getIsComment()==1?"差评":"好评"):"还没评价")
                            .setText(R.id.record_comment_details, reservation.getCommentContent())
                            .setText(R.id.record_time, reservation.getAppointmentTime());
                }else if (reservation.getItemType() == BaseMessage.Type.OUTCOMING) {
                    viewHolder.setText(R.id.index_service_item_tv, reservation.getAppointmentTime());
                }
            }
        };


        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DoctorWorkRecord.this, "position :" + position, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void initView() {

        mListView = (ListView) findViewById(R.id.doctor_work_record_listview);

    }


    public class NetworkTask extends AsyncTask<Void, Void, Boolean> {

        private String result;
        private String id;
        private boolean isEmpty;

        @Override
        protected Boolean doInBackground(Void... params) {

            List<LoginInformation> lif = DataSupport.where("state = ? and role = ?", "1", "doctor").find(LoginInformation.class);
            Reservation reservation = new Reservation();
            isEmpty = false;
            if (lif != null && lif.size()!=0) {
                reservation.setDoctorId(lif.get(0).getUid());
                Log.i("skyjaj", "id :" + lif.get(0).getUid());
                reservation.setRemark(lif.get(0).getToken());
            } else {
                result = "您不能操作该功能，您登陆的不是医生帐号！";
                return false;
            }

            try {
                result = OkHttpManager.post(ServerAddress.DOCTOR_HISTORY_RESERVATION_URL, new Gson().toJson(reservation));
                Log.i("skyjaj", result);
            } catch (Exception e) {
                e.printStackTrace();
                result = "网络请求失败";
                return false;
            }


            try {
                Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).create();
                mDatas = gson.fromJson(result, new TypeToken<List<Reservation>>() {}.getType());
            } catch (Exception e) {
                Log.i("skyjaj", "turn false gson");
                result = "获取信息失败";
                return false;
            }

            Log.i("skyjaj", "mDatas"+mDatas.size());
            if (mDatas!=null&&mDatas.size()==0) {
                isEmpty = true;
                result = null;
                return false;
            }

            if (mDatas != null && mDatas.size()!=0) {
                //处理服务器返回的信息，即分月排版显示
                List<Reservation> reservations = new ArrayList<Reservation>();
                String time = DateUtil.string2TimeFormatTwo(mDatas.get(0).getAppointmentTime());
                Log.i("skyjaj", "time :" + time);
                Reservation re = new Reservation();
                //设置提示信息
                re.setItemType(BaseMessage.Type.OUTCOMING);
                re.setAppointmentTime(time);
                reservations.add(re);
                for (Reservation r : mDatas) {
                    if (!TextUtils.isEmpty(time) && time.equals(DateUtil.string2TimeFormatTwo((r.getAppointmentTime())))) {
                        reservations.add(r);
                    } else if (!TextUtils.isEmpty(time) && !TextUtils.isEmpty(r.getAppointmentTime())) {
                        //遇到下一个月份信息
                        time = DateUtil.string2TimeFormatTwo(r.getAppointmentTime());
                        Reservation rs = new Reservation();
                        //设置提示信息
                        rs.setItemType(BaseMessage.Type.OUTCOMING);
                        rs.setAppointmentTime(time);
                        reservations.add(rs);
                        reservations.add(r);
                    }
                }
                mDatas = new ArrayList<Reservation>(reservations);
                reservations = null;
                return true;
            } else {
                result = "获取信息失败";
                return false;
            }



        }


        @Override
        protected void onPostExecute(Boolean success) {

            if (success) {
                if (mAdapter != null) {
                    mAdapter.setmDatas(mDatas);
                    mAdapter.notifyDataSetChanged();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            } else {
                if (mTask != null) {
                    mTask.cancel(true);
                    mTask = null;
                }
                //没有信息则提示，异常则退出
                if (isEmpty) {
                    Reservation text = new Reservation();
                    text.setItemType(BaseMessage.Type.OUTCOMING);
                    text.setAppointmentTime("暂无信息");
                    dialog.dismiss();
                    mDatas.add(text);
                    mAdapter.setmDatas(mDatas);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DoctorWorkRecord.this, result, Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            super.onPostExecute(success);
        }
    }


}
