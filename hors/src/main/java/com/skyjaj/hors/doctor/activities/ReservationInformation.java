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
import com.skyjaj.hors.widget.CustomProgressDialog;

import org.litepal.crud.DataSupport;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReservationInformation extends AppCompatActivity {


    private Toolbar mToolbar;
    private ListView mListView;
    private CommonAdapter mAdapter;
    private List<Reservation> mDatas;
    private Map<BaseMessage.Type,Integer> mViews;
    private NetWorkTask mTask;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_information);
        MyActivityManager.getInstance().addActivity(this);
        mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "已预约病人");
        initView();
        initDatas();

        mTask = new NetWorkTask();
        mTask.execute();


    }



    private void initDatas() {
        mViews = new HashMap<BaseMessage.Type, Integer>();
        mViews.put(BaseMessage.Type.INCOMING, R.layout.show_reservation_item);
        mViews.put(BaseMessage.Type.OUTCOMING, R.layout.index_service_item_text);

        mDatas = new ArrayList<Reservation>();

//        Reservation reservation = new Reservation();
//        reservation.setName("小明");
//        reservation.setAppointmentTime("2016-03-10 14:20");
//        reservation.setPatientId("13631158354");
//        mDatas.add(reservation);
//
//        reservation = new Reservation();
//        reservation.setName("小红");
//        reservation.setAppointmentTime("2016-03-10 14:20");
//        reservation.setPatientId("13631158354");
//        mDatas.add(reservation);
//
//        reservation = new Reservation();
//        reservation.setRemark("2016年03月");
//        reservation.setItemType(BaseMessage.Type.OUTCOMING);
//        mDatas.add(reservation);
//
//        reservation = new Reservation();
//        reservation.setName("小白");
//        reservation.setAppointmentTime("2016-03-10 14:20");
//        reservation.setPatientId("13631158354");
//        mDatas.add(reservation);

        mAdapter = new CommonAdapter<Reservation>(this,mDatas,mViews) {
            @Override
            public void convert(ViewHolder viewHolder, Reservation reservation) {
                if (reservation.getItemType() == BaseMessage.Type.INCOMING) {
                    viewHolder.setText(R.id.show_reservation_name, reservation.getName())
                            .setText(R.id.show_reservation_mobile, reservation.getPatientId())
                            .setText(R.id.show_reservation_time, ""+DateUtil.string2TimeFormatThree(reservation.getAppointmentTime()));
                }else if (reservation.getItemType() == BaseMessage.Type.OUTCOMING) {
                    viewHolder.setText(R.id.index_service_item_tv, reservation.getAppointmentTime());
                }
            }
        };


        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ReservationInformation.this, "position :" + position, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void initView() {

        mListView = (ListView) findViewById(R.id.reservation_information_listview);
        dialog = DialogStylel.createLoadingDialog(this,"正在加载..");
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
        super.onDestroy();
    }

    //network Task
    public class NetWorkTask extends AsyncTask<Void, Void, Boolean> {

        private String result;
        private String id;
        private boolean isEmpty;

        @Override
        protected Boolean doInBackground(Void... params) {

            List<LoginInformation> lif = DataSupport.where("state = ? and role = ?", "1","doctor").find(LoginInformation.class);
            Reservation reservation = new Reservation();
            isEmpty = false;
            if (lif != null && lif.size()!=0) {
                reservation.setDoctorId(lif.get(0).getUid());
                Log.i("skyjaj", "id :"+lif.get(0).getUid());
                reservation.setRemark(lif.get(0).getToken());
            } else {
                result = "您不能操作该功能，您登陆的不是医生帐号！";
                return false;
            }

            try {
                result = OkHttpManager.post(ServerAddress.DOCTOR_RESERVATION_URL, new Gson().toJson(reservation));
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
                    dialog.dismiss();
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
                    Toast.makeText(ReservationInformation.this, result, Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            super.onPostExecute(success);
        }
    }

}
