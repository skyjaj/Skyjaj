package com.skyjaj.hors.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
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
import com.skyjaj.hors.adapter.CommonAdapter;
import com.skyjaj.hors.adapter.TimestampTypeAdapter;
import com.skyjaj.hors.adapter.ViewHolder;
import com.skyjaj.hors.admin.wigwet.DoctorManagerForOnItemLongClickDialog;
import com.skyjaj.hors.bean.BaseMessage;
import com.skyjaj.hors.bean.LoginInformation;
import com.skyjaj.hors.bean.Patient;
import com.skyjaj.hors.bean.Reservation;
import com.skyjaj.hors.utils.DateUtil;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;
import com.skyjaj.hors.widget.ShowHistoryLongClicklistener;

import org.litepal.crud.DataSupport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowHistoryActivity extends AppCompatActivity {

    private static Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).create();

    //UI
    private ListView mListView;
    private Toolbar mToolbar;

    private List<Reservation> mDatas;
    private CommonAdapter mAdapter;
    private Map<BaseMessage.Type,Integer> mItemViews;

    private NetworkTask mNetworkTask;
    private boolean isLongClick;
    private Dialog mDialog;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //delete
                case 0:
                    mDatas.remove(msg.obj);
                    mAdapter.setmDatas(mDatas);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(ShowHistoryActivity.this,"已删除", Toast.LENGTH_SHORT).show();
                    break;
                //cancel reservation
                case 1:
                    Reservation reservation = (Reservation) msg.obj;
                    reservation.setCancel(true);
                    mAdapter.setmDatas(mDatas);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(ShowHistoryActivity.this,"已取消", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(ShowHistoryActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
            }
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_history);
        MyActivityManager.getInstance().addActivity(this);
        mToolbar = ToolbarStyle.initToolbar(this, R.id.history_toolbar, "预约记录");
        initDatas();
        initView();
        mDialog = DialogStylel.createLoadingDialog(this, "等待中...");
        mDialog.show();
        mNetworkTask = new NetworkTask();
        mNetworkTask.execute();
    }

    private void initDatas() {
        mDatas = new ArrayList<Reservation>();
    }


    private void initView() {
        mListView = (ListView) findViewById(R.id.history_listview);
        mItemViews = new HashMap<BaseMessage.Type, Integer>();
        mItemViews.put(BaseMessage.Type.INCOMING, R.layout.history_item);
        mItemViews.put(BaseMessage.Type.OUTCOMING, R.layout.index_service_item_text);

        mAdapter = new CommonAdapter<Reservation>(this, mDatas, mItemViews) {
            @Override
            public void convert(ViewHolder viewHolder, Reservation reservation) {
                if (reservation.getItemType() == BaseMessage.Type.INCOMING) {
                    viewHolder.setText(R.id.history_doctor_name, reservation.getDoctorName())
                            .setText(R.id.history_hospital, reservation.getHospitalName()==null?"广东药学院附属医院":reservation.getHospitalName())
                            .setText(R.id.history_time, ""+DateUtil.string2TimeFormatThree(reservation.getAppointmentTime()))
                            .setText(R.id.history_state, reservation.getCancel() == true ? "预约已取消" : "预约成功");
                    if (reservation.getCancel()) {
                        viewHolder.setTextColor(R.id.history_state, Color.parseColor("#ff0000"));
                    } else {
                        viewHolder.setTextColor(R.id.history_state, Color.parseColor("#FF3CBD44"));
                    }
                } else if (reservation.getItemType() == BaseMessage.Type.OUTCOMING) {
                    viewHolder.setText(R.id.index_service_item_tv, reservation.getAppointmentTime());
                }
            }
        };

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isLongClick) {
                    return;
                }
                Toast.makeText(ShowHistoryActivity.this, "position :" + position, Toast.LENGTH_SHORT).show();
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Reservation reservation = mDatas.get(position);
                if (reservation.getItemType() == BaseMessage.Type.OUTCOMING) {
                    return false;
                }
                ShowHistoryLongClicklistener.OnClickListener listener = new ShowHistoryLongClicklistener.OnClickListener() {
                    @Override
                    public void onItemClick(View v) {
                        isLongClick = false;
                        switch (v.getId()) {
                            case R.id.show_history_cancel:
                                if (reservation.getCancel()) {
                                    Toast.makeText(ShowHistoryActivity.this, "当前已取消", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                cancel(ServerAddress.PATIENT_CANCEL_RESERVATION_URL, reservation);
                                break;
                            case R.id.show_history_comment:
                                Toast.makeText(ShowHistoryActivity.this, "resid :" + "show_history_comment ", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.show_history_delete:
                                delete(ServerAddress.PATIENT_DELETE_ERSERVATION_URL, reservation);
                                break;
                        }
                    }

                    @Override
                    public void onDismissListener(DialogInterface dialog) {
                        isLongClick = false;
                    }

                    @Override
                    public void onCancelListener(DialogInterface dialog) {

                    }
                };

                ShowHistoryLongClicklistener clickDialog = new ShowHistoryLongClicklistener(listener, ShowHistoryActivity.this);
                if (reservation.getCancel()) {
                    clickDialog.setViewGone(R.id.show_history_cancel);
                }
                if (reservation.getIsComment() == 1) {
                    clickDialog.setViewGone(R.id.show_history_comment);
                }
                isLongClick = true;
                return false;
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
                    if (view.getLastVisiblePosition() == view.getCount()-1) {
                        Log.i("skyjaj", "onscrooll end");
                        if (mDialog != null && !mDialog.isShowing()) {
                            mDialog.show();
                        }

                        mNetworkTask = new NetworkTask();
                        mNetworkTask.setTime(mDatas.get(mDatas.size() - 1).getRemark());
                        mNetworkTask.execute();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }


    /**删除记预约录
     * 网络操作
     * @param url
     * @param reservation
     */
    private void delete(final String url,final Reservation reservation) {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Reservation reservation1 = new Reservation();
                reservation1.setId(reservation.getId());
                reservation1.setItemType(null);
                String result;
                try {
                    result = OkHttpManager.post(url, new Gson().toJson(reservation1));
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "无法请求服务器,请稍后重试";
                }

                Message msg = new Message();
                if ("success".equals(result)) {
                    msg.what = 0;
                    msg.obj = reservation;
                    mHandler.sendMessage(msg);
                } else {
                    msg.what = 2;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    /**
     * 取消预约
     * @param url
     * @param reservation
     */
    private void cancel(final String url,final Reservation reservation) {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Reservation reservation1 = new Reservation();
                reservation1.setId(reservation.getId());
                reservation1.setItemType(null);
                String result;
                try {
                    result = OkHttpManager.post(url, new Gson().toJson(reservation1));
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "无法请求服务器,请稍后重试";
                }
                Message msg = new Message();
                if ("success".equals(result)) {
                    msg.what = 1;
                    msg.obj = reservation;
                    mHandler.sendMessage(msg);
                } else {
                    msg.what =2 ;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        MyActivityManager.getInstance().remove(this);
        if (mDialog != null) {
            mDialog = null;
        }
        super.onDestroy();
    }

    //网络任务
    public class NetworkTask extends AsyncTask<Void, Void, Boolean> {

        private String result;
        private boolean isEmpty;
        //通过时间线拉取数据
        private String time;

        public void setTime(String time) {
            this.time = time;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            List<LoginInformation> lif = DataSupport.where("state = ?", "1").find(LoginInformation.class);
            Patient patient = new Patient();
            List<Reservation> reservationsTemp=null;
            isEmpty = false;
            if (lif != null) {
                patient.setId(lif.get(0).getUid());
                patient.setToken(patient.getToken());
            } else {
                result = "找不到登录信息";
                return false;
            }

            try {
                patient.setRemark(time);
                result = OkHttpManager.post(ServerAddress.PATIENT_RESERVATION_HISTORY_URL, new Gson().toJson(patient));
                Log.i("skyjaj", result);
            } catch (Exception e) {
                e.printStackTrace();
                result = "网络请求失败";
                return false;
            }

            try {
                reservationsTemp = gson.fromJson(result, new TypeToken<List<Reservation>>() {}.getType());
            } catch (Exception e) {
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
        protected void onPostExecute(final Boolean success) {

            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
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
                    text.setAppointmentTime("暂无信息");
                    mDatas.add(text);
                    mAdapter.setmDatas(mDatas);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ShowHistoryActivity.this, result, Toast.LENGTH_SHORT).show();
                    //finish();
                }
            }
        }
    }
}
