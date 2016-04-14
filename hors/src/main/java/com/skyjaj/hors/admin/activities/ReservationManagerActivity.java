package com.skyjaj.hors.admin.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.BaseActivity;
import com.skyjaj.hors.activities.HistoryDetails;
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

public class ReservationManagerActivity extends BaseActivity implements View.OnClickListener{

    private Toolbar mToolbar;
    private ListView mListView;
    private LinearLayout mMenuView;
    private Button mCancelBtn,mOkBtn,mChooseBtn;

    private CommonAdapter mAdapter;
    private List<Reservation> mDatas;
    private Map<BaseMessage.Type,Integer> mViews;
    private NetWorkTask mTask;
    private Dialog dialog;
    private boolean showCheckBox;
    private boolean isAllChoose;
    private List<Reservation> mCheckReservations;

    private final int DELETE = 0, MESSAGE = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELETE:
                    try {
//                        if (mCheckReservations != null && mCheckReservations.size() != 0 && mDatas.size() > 0) {
//                            for (Reservation r : mCheckReservations) {
//                                mDatas.remove(r);
//                            }
//                            showCheckBox = false;
//                            mCheckReservations.clear();
//                            mMenuView.setVisibility(View.GONE);
//                            mAdapter.notifyDataSetChanged();
//                        }
                        showCheckBox = false;
                        mCheckReservations.clear();
                        mMenuView.setVisibility(View.GONE);
                        mDatas.clear();
                        mTask = new NetWorkTask();
                        mTask.execute();
                        if (dialog != null && !dialog.isShowing()) {
                            dialog.show();
                        }
                        Toast.makeText(ReservationManagerActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MESSAGE:
                    String tips = (String) msg.obj;
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if (!TextUtils.isEmpty(tips)) {
                        Toast.makeText(ReservationManagerActivity.this, tips, Toast.LENGTH_SHORT).show();
                    }
                    showCheckBox = false;
                    mCheckReservations.clear();
                    mMenuView.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_manager);
        mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "已预约病人");

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
        mCheckReservations = new ArrayList<Reservation>();
        showCheckBox = false;
        isAllChoose = false;

        mAdapter = new CommonAdapter<Reservation>(this,mDatas,mViews) {
            @Override
            public void convert(ViewHolder viewHolder, final Reservation reservation) {
                if (reservation.getItemType() == BaseMessage.Type.INCOMING) {
                    viewHolder.setText(R.id.show_reservation_name, reservation.getName())
                            .setText(R.id.show_reservation_mobile, reservation.getName())
                            .setText(R.id.show_reservation_time, "" + DateUtil.string2TimeFormatThree(reservation.getAppointmentTime()));

                    Button btn = viewHolder.getView(R.id.show_reservation_remark_btn);
                    if (reservation.getCancel()) {
                        btn.setBackgroundResource(R.drawable.selector_button);
                        btn.setTextColor(Color.WHITE);
                        btn.setText("预约成功");

                    } else {
                        btn.setBackgroundResource(R.drawable.textview_bg_pressed);
                        btn.setTextColor(Color.RED);
                        btn.setText("预约失败");
                    }
                    //checkbox的控制
                    CheckBox checkBox = viewHolder.getView(R.id.show_reservation_checkbox);
                    if (showCheckBox) {
                        checkBox.setVisibility(View.VISIBLE);
                    } else {
//                        checkBox.setChecked(false);
                        checkBox.setVisibility(View.GONE);
                    }

                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked && mCheckReservations != null && !mCheckReservations.contains(reservation)) {
                                mCheckReservations.add(reservation);
                                Log.i("skyjaj", "add reservation :" + reservation.getId());
                            } else if (!isChecked && mCheckReservations != null && mCheckReservations.contains(reservation)){
                                mCheckReservations.remove(reservation);
                                Log.i("skyjaj", "remove reservation :" + reservation.getId());
                            }
                        }
                    });

                    if (mCheckReservations != null && mCheckReservations.contains(reservation)) {
                        checkBox.setChecked(true);
                    } else {
                        checkBox.setChecked(false);
                    }


                }else if (reservation.getItemType() == BaseMessage.Type.OUTCOMING) {
                    viewHolder.setText(R.id.index_service_item_tv, reservation.getAppointmentTime());
                }
            }
        };


        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Reservation reservation = mDatas.get(position);
                if (reservation == null || reservation.getItemType() == BaseMessage.Type.OUTCOMING) {
                    return;
                }
                Intent intent = new Intent(ReservationManagerActivity.this, HistoryDetails.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("reservation", reservation);
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
        mMenuView = (LinearLayout) findViewById(R.id.reservation_manager_menu_view);
        mCancelBtn = (Button) findViewById(R.id.reservation_manager_cancel);
        mOkBtn = (Button) findViewById(R.id.reservation_manager_ok);
        mChooseBtn = (Button) findViewById(R.id.reservation_manager_choose);

        mCancelBtn.setOnClickListener(this);
        mOkBtn.setOnClickListener(this);
        mChooseBtn.setOnClickListener(this);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reservation_manager_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_reservation_delete:
                showCheckBox = true;
                mCheckReservations.clear();
                mMenuView.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteReservation(final String url, final int what) {

        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result="";
                if (mCheckReservations == null || mCheckReservations.size() == 0) {
                    return;
                }

                List<Reservation> rs = new ArrayList<Reservation>();
                Log.i("skyjaj", "rs size :" + mCheckReservations.size());
                for (Reservation r : mCheckReservations) {
                    Reservation rd = new Reservation();
                    rd.setItemType(null);
                    rd.setId(r.getId());
                    rs.add(rd);
                }
                try {
                    result = OkHttpManager.post(url, OkHttpManager.gson.toJson(rs));
                } catch (Exception e) {
                    result = "网络异常，请稍后重试";
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(result)) {
                    result = result.replaceAll("\"", "");
                }
                Message msg = new Message();
                if ("success".equals(result)) {
                    msg.what = what;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reservation_manager_cancel:
                if (!showCheckBox) {
                    return ;
                }
                mMenuView.setVisibility(View.GONE);
                showCheckBox = false;
                mCheckReservations.clear();
                mAdapter.notifyDataSetChanged();
                break;

            case R.id.reservation_manager_ok:
                if (showCheckBox)
                    deleteReservation(ServerAddress.ADMIN_DELETE_RESERVATION_URL, DELETE);
                break;

            case R.id.reservation_manager_choose:
                if (!showCheckBox) {
                    return ;
                }
                if (!isAllChoose) {
                    mCheckReservations.clear();
                    for (Reservation r : mDatas) {
                        if (r.getItemType() == BaseMessage.Type.INCOMING) {
                            mCheckReservations.add(r);
                        }
                    }
                    mChooseBtn.setText("清 空");
                    isAllChoose = true;
                    mAdapter.notifyDataSetChanged();
                } else {
                    mCheckReservations.clear();
                    isAllChoose = false;
                    mChooseBtn.setText("全 选");
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
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
