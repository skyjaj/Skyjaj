package com.skyjaj.hors.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.skyjaj.hors.R;
import com.skyjaj.hors.bean.Reservation;
import com.skyjaj.hors.utils.DateUtil;

public class HistoryDetails extends BaseActivity {

    //UI
    private TextView title;
    private TextView departmentTv;
    private TextView doctorTv;
    private TextView timeTv;
    private TextView patientTv;
    private TextView registerFeeTv;
    private TextView inspectingFeeTv;
    private TextView checkingFeeTv;
    private TextView statusTv;
    private TextView createdTimeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyActivityManager.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);

        initView();
        initDatas();

    }

    private void initDatas() {
        Reservation reservation = (Reservation) getIntent().getSerializableExtra("reservation");
        if (reservation != null) {
            //UI datas
            registerFeeTv.setText(reservation.getRegisteredFee()+"");
            inspectingFeeTv.setText("0");
            checkingFeeTv.setText("0");
            doctorTv.setText(reservation.getDoctorName());
            departmentTv.setText(reservation.getDepartmentName());
            timeTv.setText(DateUtil.string2TimeFormatOne(reservation.getAppointmentTime()));
            patientTv.setText(reservation.getName());
            statusTv.setText(reservation.getCancel() == true ? "预约已取消" : "预约成功");
            if (!reservation.getCancel()) {
                statusTv.setTextColor(Color.GREEN);
            } else {
                statusTv.setTextColor(Color.RED);
            }
            if (reservation.getCreateTime() != null) {
                createdTimeTv.setText(DateUtil.Timestamp2StringFormat2(reservation.getCreateTime()));
            } else {
                createdTimeTv.setText("找不到时间！");
            }
        }
        title.setText("预约详情");
    }

    private void initView() {
        //UI
        title = (TextView) findViewById(R.id.history_details_title);
        departmentTv = (TextView) findViewById(R.id.history_details_department);
        doctorTv = (TextView) findViewById(R.id.history_details_doctor);
        timeTv = (TextView) findViewById(R.id.history_details_time);
        patientTv = (TextView) findViewById(R.id.history_details_patient);
        registerFeeTv = (TextView) findViewById(R.id.history_details_register_fee);
        inspectingFeeTv = (TextView) findViewById(R.id.history_details_inspecting_fee);
        checkingFeeTv = (TextView) findViewById(R.id.history_details_checking_fee);
        statusTv = (TextView) findViewById(R.id.history_details_status);
        createdTimeTv = (TextView) findViewById(R.id.history_details_create_time);
    }


    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.history_details_exit:
                finish();
                break;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivityManager.getInstance().remove(this);

    }
}
