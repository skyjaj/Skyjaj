package com.skyjaj.hors.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.adapter.TimestampTypeAdapter;
import com.skyjaj.hors.allipay.PayEnterActivity;
import com.skyjaj.hors.bean.Reservation;
import com.skyjaj.hors.utils.DateUtil;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;

import java.sql.Timestamp;

public class IndexReservationConfirmActivity extends AppCompatActivity {

    private Toolbar mToobar;
    private Reservation mReservationResult;
    private Reservation mReservation;
    private String mResult;
    private NetworkTask mNetworkTask;

    //ui
    private TextView departmentTv;
    private TextView doctorTv;
    private TextView timeTv;
    private TextView patientTv;
    private TextView registerFeeTv;
    private TextView inspectingFeeTv;
    private TextView checkingFeeTv;
    private TextView departmentAddressTv;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_reservation_confirm);
        MyActivityManager.getInstance().addActivity(this);
        //初始化所有预约信息
        initView();
        initDatas();

        mToobar = ToolbarStyle.initToolbar(this, R.id.index_reservation_confirm_toolbar, "预约信息确认");


    }

    //UI
    private void initView() {
        departmentTv = (TextView) findViewById(R.id.index_reservation_confirm_department);
        doctorTv = (TextView) findViewById(R.id.index_reservation_confirm_doctor);
        timeTv = (TextView) findViewById(R.id.index_reservation_confirm_time);
        patientTv = (TextView) findViewById(R.id.index_reservation_confirm_patient);
        registerFeeTv = (TextView) findViewById(R.id.index_reservation_confirm_register_fee);
        inspectingFeeTv = (TextView) findViewById(R.id.index_reservation_confirm_inspecting_fee);
        checkingFeeTv = (TextView) findViewById(R.id.index_reservation_confirm_checking_fee);
        departmentAddressTv = (TextView) findViewById(R.id.index_reservation_confirm_department_address);
    }

    //初始化数据
    private void initDatas() {
        Intent intent = getIntent();
        if (intent != null) {
            mReservation = new Reservation();
            mReservation.setDoctorId(intent.getStringExtra("doctor_id"));
            mReservation.setDoctorName(intent.getStringExtra("doctor_name"));
            mReservation.setDepartmentId(intent.getStringExtra("department_id"));
            mReservation.setDepartmentName(intent.getStringExtra("department_name"));
            mReservation.setAppointmentTime(intent.getStringExtra("appointment_time"));
            mReservation.setName(intent.getStringExtra("patient_name"));
            mReservation.setPatientId(intent.getStringExtra("patient_id"));
            mReservation.setRemark(intent.getStringExtra("token"));
            mReservation.setRegisteredFee(Double.parseDouble(intent.getStringExtra("register_fee")));
            //UI datas
            registerFeeTv.setText(intent.getStringExtra("register_fee"));
            inspectingFeeTv.setText(intent.getStringExtra("inspecting_fee"));
            checkingFeeTv.setText(intent.getStringExtra("checking_fee"));
            doctorTv.setText(intent.getStringExtra("doctor_name"));
            departmentTv.setText(intent.getStringExtra("department_name"));
            timeTv.setText(DateUtil.string2TimeFormatOne(intent.getStringExtra("appointment_time")));
            patientTv.setText(intent.getStringExtra("patient_name"));
        }
    }




    //确认按钮
    public void appointmentConfirm(View v) {
        if (v.getId() == R.id.index_reservation_confirm_btn) {
            if (mReservation == null) {
                //重新登录
            }
//            dialog = DialogStylel.createLoadingDialog(this, "提交中...");
//            dialog.show();
//            mNetworkTask = new NetworkTask(mReservation);
//            mNetworkTask.execute();
            Intent intent = new Intent(this, PayEnterActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("reservation", mReservation);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }


    @Override
    protected void onDestroy() {
        MyActivityManager.getInstance().remove(this);
        super.onDestroy();
    }

    //异步任务
    public class NetworkTask extends AsyncTask<Void, Void, Boolean> {

        private Reservation reservation;
        NetworkTask(Reservation reservation) {
            this.reservation = reservation;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d("xys", " IndexDoctorAppointmentActivity doInBackground");
            try {
                mResult = OkHttpManager.post(ServerAddress.PATIENT_RESERVATION_URL, new Gson().toJson(reservation));
                Log.i("skyjaj", "mresult :"+mResult);
                try {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).create();
                    mReservationResult = gson.fromJson(mResult, new TypeToken<Reservation>() {}.getType());
                } catch (Exception e) {
                    return false;
                }

                if (mReservationResult != null && "success".equals(mReservationResult.getRemark())) {
                    return true;
                }

            } catch (Exception e) {
                mResult = "网络异常,暂时无法连接服务器";
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog != null) {
                dialog.dismiss();
            }
            if (success) {
                Log.i("skyjaj", "请求成功");
                Toast.makeText(IndexReservationConfirmActivity.this, mReservationResult != null ? "预约成功" : mResult, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(IndexReservationConfirmActivity.this, ShowResultActivity.class); //确认预约信息
                intent.putExtra("doctor_id", reservation.getDoctorId());
                intent.putExtra("doctor_name", reservation.getDoctorName());
                intent.putExtra("department_id", reservation.getDepartmentId());
                intent.putExtra("department_name", reservation.getDepartmentName());
                intent.putExtra("appointment_time", reservation.getAppointmentTime());
                intent.putExtra("patient_name", reservation.getPatientId());
                intent.putExtra("patient_id", reservation.getPatientId());
                intent.putExtra("token", reservation.getName());
                intent.putExtra("register_fee", reservation.getRegisteredFee() + "");
                intent.putExtra("inspecting_fee", "0");
                intent.putExtra("checking_fee", "0");
                startActivity(intent);
            } else {
                Toast.makeText(IndexReservationConfirmActivity.this, mResult, Toast.LENGTH_SHORT).show();
                Log.i("skyjaj", "请求失败，请稍后再试");
            }
        }

        @Override
        protected void onCancelled() {
            Log.d("xys", "onCancelled");

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mNetworkTask != null) {
            mNetworkTask.cancel(true);
            mNetworkTask = null;
        }
    }
}
