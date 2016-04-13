package com.skyjaj.hors.admin.activities;

import android.app.Dialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
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
import com.skyjaj.hors.bean.Patient;
import com.skyjaj.hors.bean.Reservation;
import com.skyjaj.hors.bean.SystemUser;
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

public class ReservationManagerActivity extends BaseActivity {

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
        setContentView(R.layout.activity_reservation_manager);
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


        mAdapter = new CommonAdapter<Reservation>(this,mDatas,mViews) {
            @Override
            public void convert(ViewHolder viewHolder, Reservation reservation) {
                if (reservation.getItemType() == BaseMessage.Type.INCOMING) {

                    viewHolder.setText(R.id.show_reservation_name, reservation.getName())
                            .setText(R.id.show_reservation_mobile, reservation.getName())
                            .setText(R.id.show_reservation_time, ""+ DateUtil.string2TimeFormatThree(reservation.getAppointmentTime()));
                }else if (reservation.getItemType() == BaseMessage.Type.OUTCOMING) {
                    viewHolder.setText(R.id.index_service_item_tv, reservation.getAppointmentTime());
                }
            }
        };


        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ReservationManagerActivity.this, "position :" + position, Toast.LENGTH_SHORT).show();
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
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.show();
                        }
                        mTask = new NetWorkTask();
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

    private void initView() {
        mListView = (ListView) findViewById(R.id.reservation_manager_listview);
        dialog = DialogStylel.createLoadingDialog(this, "加载中..");
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
        private boolean isEmpty;

        private String time;

        public void setTime(String time) {
            this.time = time;
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            List<LoginInformation> lif = DataSupport.where("state = ? and role = ?", "1","admin").find(LoginInformation.class);
            SystemUser systemUser = new SystemUser();
            List<Reservation> reservationsTemp=null;
            isEmpty = false;
            if (lif == null || lif.size() == 0) {
                result = "找不到登录信息";
                return false;
            } else {
                systemUser.setId(lif.get(0).getUid());
                systemUser.setId(lif.get(0).getToken());
            }
            try {
                systemUser.setRemark(time);
                result = OkHttpManager.post(ServerAddress.ADMIN_FIND_RESERVATION_URL, new Gson().toJson(systemUser));
                Log.i("skyjaj", result);
            } catch (Exception e) {
                e.printStackTrace();
                result = "网络请求失败";
                return false;
            }


            try {
                Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).create();
                reservationsTemp = gson.fromJson(result, new TypeToken<List<Reservation>>() {}.getType());
            } catch (Exception e) {
                Log.i("skyjaj", "turn false gson");
                result = "获取信息失败";
                return false;
            }

            if (reservationsTemp != null && reservationsTemp.size() == 0) {
                isEmpty = true;
                result = "没有可加载的数据了";
                return false;
            }

            if (reservationsTemp != null && reservationsTemp.size() != 0) {
                //处理服务器返回的信息，即分月排版显示
                List<Reservation> reservations = new ArrayList<Reservation>();
                //上一次拉取数据的预约时间
                String lastTime = "";
                if (mDatas != null && mDatas.size() > 0) {
                    lastTime = DateUtil.string2TimeFormatTwo(mDatas.get(mDatas.size()-1).getAppointmentTime());
                }
                String time = DateUtil.string2TimeFormatTwo(reservationsTemp.get(0).getAppointmentTime());

                //若与上一次时间不在同一月份则显示
                if (!(!TextUtils.isEmpty(lastTime) && lastTime.equals(time))) {
                    Reservation re = new Reservation();
                    //设置提示信息
                    re.setItemType(BaseMessage.Type.OUTCOMING);
                    re.setAppointmentTime(time);
                    reservations.add(re);
                }

                for (Reservation r : reservationsTemp) {
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
                for (Reservation rsvt : reservations) {
                    mDatas.add(rsvt);
                }
                reservations = null;
                return true;
            } else {
                result = "获取信息失败";
                return false;
            }


        }


        @Override
        protected void onPostExecute(Boolean success) {

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (success) {
                if (mAdapter != null) {
                    mAdapter.setmDatas(mDatas);
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                if (isEmpty&&mDatas.size()==0) {
                    Reservation text = new Reservation();
                    text.setItemType(BaseMessage.Type.OUTCOMING);
                    text.setAppointmentTime("暂无预约信息");
                    mDatas.add(text);
                    mAdapter.setmDatas(mDatas);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ReservationManagerActivity.this, result, Toast.LENGTH_SHORT).show();
                    //finish();
                }
            }

        }
    }
}
