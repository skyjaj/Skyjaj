package com.skyjaj.hors.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.adapter.CalendarTypeAdapter;
import com.skyjaj.hors.bean.CustomDate;
import com.skyjaj.hors.bean.Department;
import com.skyjaj.hors.bean.Doctor;
import com.skyjaj.hors.bean.ScheduleOfMonth;
import com.skyjaj.hors.utils.DateUtil;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.widget.CalendarCard;
import com.skyjaj.hors.widget.CustomProgressDialog;

import java.security.PublicKey;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalenderActivity extends Activity implements  CalendarCard.OnCellClickListener {


    //UI
    private LinearLayout calendarView;
    private LinearLayout titleView,tipsView;
    private TextView titleTv;

    private  List<ScheduleOfMonth>  scheduleOfMonths;
    private  List<ScheduleOfMonth>  scheduleOfMonthsOne =  new ArrayList<ScheduleOfMonth>();
    private  List<ScheduleOfMonth>  scheduleOfMonthsTwo;
    private CustomDate customDate;
    private Task mTask;
    private Doctor doctor;
    private Department mDepartment;
    private Dialog dialog;

    private boolean canClickEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calender);
        MyActivityManager.getInstance().addActivity(this);
        doctor = (Doctor) getIntent().getSerializableExtra("doctor");
        String mobile = getIntent().getStringExtra("doctor_mobile");
        String doctorId = getIntent().getStringExtra("doctor_id");
        mDepartment = (Department) getIntent().getSerializableExtra("department");

        calendarView = (LinearLayout) findViewById(R.id.calender_view);
        tipsView = (LinearLayout) findViewById(R.id.calender_tips_view);
        titleView = (LinearLayout) findViewById(R.id.calender_title_view);
        titleTv = (TextView) findViewById(R.id.calender_title);


        dialog = DialogStylel.createLoadingDialog(this, "加载中...");
        dialog.show();
        canClickEnter = true;
        if (!TextUtils.isEmpty(mobile) && doctor == null && !TextUtils.isEmpty(doctorId)) {
            doctor = new Doctor();
            doctor.setId(doctorId);
            doctor.setMobile(mobile);
            canClickEnter = false;
            titleTv.setText("排班详情");
        }
        mTask = new Task();
        mTask.execute();
    }


    //UI
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.calender_back:
                finish();
                break;
        }
    }


    @Override
    public void ensureDate(CustomDate date,ScheduleOfMonth scheduleOfmonth) {

        if (canClickEnter) {
            if (scheduleOfmonth != null && scheduleOfmonth.getPeriodOfTime() != 3) {
                Intent intent = new Intent(this, PointOfTimeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("doctor", doctor);
                bundle.putSerializable("department", mDepartment);
                intent.putExtra("workday", DateUtil.Timestamp2String(scheduleOfmonth.getWorkday()));
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                Toast.makeText(CalenderActivity.this, "该时间不可预约", Toast.LENGTH_SHORT).show();
            }
        } else {

        }

    }


    private void setUI() {
        try {
            calendarView.addView(new CalendarCard(CalenderActivity.this, CalenderActivity.this, scheduleOfMonthsOne, scheduleOfMonthsTwo, customDate));
        } catch (Exception e) {
            tipsView.setVisibility(View.VISIBLE);
            titleView.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivityManager.getInstance().remove(this);
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
        scheduleOfMonths = null;
        dialog = null;
    }

    //异步任务
    public class Task extends AsyncTask<Void, Void, Boolean> {

        private String result;

        Task() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Doctor mDoctor = new Doctor();
                mDoctor.setItemType(null);
                mDoctor.setCreateTime(null);
                mDoctor.setId(doctor != null ? doctor.getId() : null);
                mDoctor.setMobile(doctor != null ? doctor.getMobile() : null);
                result = OkHttpManager.post(ServerAddress.DOCTOR_SCHEDULE_URL, new Gson().toJson(mDoctor));
                Log.i("skyjaj", "mresult :" + result);
                try {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new CalendarTypeAdapter()).create();
                    scheduleOfMonths = gson.fromJson(result, new TypeToken<List<ScheduleOfMonth>>() {}.getType());
                } catch (Exception e) {
                    return false;
                }

            } catch (Exception e) {
                result = "网络异常,暂时无法连接服务器";
                e.printStackTrace();
                return false;
            }

            if (scheduleOfMonths != null) {
                Date date =DateUtil.getLastDate(scheduleOfMonths.get(0).getWorkday());
                if (date != null) {
                    customDate = new CustomDate(DateUtil.getYear(date),DateUtil.getMonth(date),DateUtil.getDay(date));
                }
            }

            if (scheduleOfMonths != null) {
                for (ScheduleOfMonth s : scheduleOfMonths) {
                    Date date = DateUtil.Timestamp2Date(s.getWorkday());
                    if (customDate != null && customDate.month == DateUtil.getMonth(date)) {
                        if (scheduleOfMonthsOne == null) {
                            scheduleOfMonthsOne = new ArrayList<ScheduleOfMonth>();
                        }
                        scheduleOfMonthsOne.add(s);
                    } else {
                        if (scheduleOfMonthsTwo == null) {
                            scheduleOfMonthsTwo = new ArrayList<ScheduleOfMonth>();
                        }
                        scheduleOfMonthsTwo.add(s);
                    }
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog != null) {
                dialog.dismiss();
            }

            if (scheduleOfMonths == null || scheduleOfMonths.size() == 0) {
                Toast.makeText(CalenderActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            if (success) {
                setUI();
                Toast.makeText(CalenderActivity.this, "已获取数据", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CalenderActivity.this, "暂时无法加载数据", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            Log.d("xys", "onCancelled");

        }
    }




} 