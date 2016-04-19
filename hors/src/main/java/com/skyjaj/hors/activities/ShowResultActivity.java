package com.skyjaj.hors.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.skyjaj.hors.R;
import com.skyjaj.hors.bean.Reservation;
import com.skyjaj.hors.utils.DateUtil;
import com.skyjaj.hors.weixin.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.openapi.IWXAPI;


public class ShowResultActivity extends Activity {

    //UI
    private TextView title;
    private TextView departmentTv;
    private TextView doctorTv;
    private TextView timeTv;
    private TextView patientTv;
    private TextView registerFeeTv;
    private TextView inspectingFeeTv;
    private TextView checkingFeeTv;
    private TextView departmentAddressTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        MyActivityManager.getInstance().addActivity(this);

        setContentView(R.layout.activity_show_result);
        initView();
        initDatas();

    }



    //UI
    private void initView() {
        title = (TextView) findViewById(R.id.result_title);
        departmentTv = (TextView) findViewById(R.id.result_department);
        doctorTv = (TextView) findViewById(R.id.result_doctor);
        timeTv = (TextView) findViewById(R.id.result_time);
        patientTv = (TextView) findViewById(R.id.result_patient);
        registerFeeTv = (TextView) findViewById(R.id.result_register_fee);
        inspectingFeeTv = (TextView) findViewById(R.id.result_inspecting_fee);
        checkingFeeTv = (TextView) findViewById(R.id.result_checking_fee);
        departmentAddressTv = (TextView) findViewById(R.id.result_department_address);
    }

    //初始化数据
    private void initDatas() {
        Intent intent = getIntent();
        if (intent != null) {
            //UI datas
            registerFeeTv.setText(intent.getStringExtra("register_fee"));
            inspectingFeeTv.setText(intent.getStringExtra("inspecting_fee"));
            checkingFeeTv.setText(intent.getStringExtra("checking_fee"));
            doctorTv.setText(intent.getStringExtra("doctor_name"));
            String departmentName = intent.getStringExtra("department_name");
            if (TextUtils.isEmpty(departmentName)) {
                departmentTv.setVisibility(View.GONE);
            }
            departmentTv.setText(departmentName);
            String time = intent.getStringExtra("appointment_time");
            if (!TextUtils.isEmpty(time)) {
                time = time.replaceAll("-", "");
            }
            timeTv.setText(DateUtil.string2TimeFormatOne(time));
            patientTv.setText(intent.getStringExtra("patient_name"));
        }
        title.setText("预约结果");
    }


    public void onClick(View view) {


        if (view.getId() == R.id.result_complete) {
            //Intent intent = new Intent(this, IndexCommonActivity.class);
            MyActivityManager.getInstance().finishButThis(IndexCommonActivity.class.getName());
            //startActivity(intent);
            finish();
        }
//        else if (view.getId() == R.id.weixin_shared) {
//
//            Intent intent = new Intent(this, WXEntryActivity.class);
//            startActivity(intent);
//        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivityManager.getInstance().remove(this);
    }
}
