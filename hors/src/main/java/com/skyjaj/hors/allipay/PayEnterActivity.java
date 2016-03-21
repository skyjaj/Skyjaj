package com.skyjaj.hors.allipay;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.ShowResultActivity;
import com.skyjaj.hors.adapter.TimestampTypeAdapter;
import com.skyjaj.hors.bean.Reservation;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;

import java.sql.Timestamp;

/**
 * 支付进入界面
 * Created by Administrator on 2016/3/21.
 */
public class PayEnterActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    private Reservation mReservation;
    private Dialog dialog;
    private NetworkTask mNetworkTask;
    private Reservation mReservationResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_enter);


        mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "支付单");

        mReservation = (Reservation) getIntent().getSerializableExtra("reservation");
    }


    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.pay_enter_ok:

                if (mReservation == null) {
                    //重新登录
                    Toast.makeText(this, "reservation " + mReservation, Toast.LENGTH_SHORT).show();
                }
                if (dialog == null) {
                    dialog = DialogStylel.createLoadingDialog(this, "提交中...");
                    dialog.show();
                }else if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                }
                mNetworkTask = new NetworkTask(mReservation);
                mNetworkTask.execute();
                break;
        }

    }



    //异步任务
    public class NetworkTask extends AsyncTask<Void, Void, Boolean> {

        private String mResult;
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
                Toast.makeText(PayEnterActivity.this, mReservationResult != null ? "预约成功" : mResult, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PayEnterActivity.this, ShowResultActivity.class); //确认预约信息
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
                Toast.makeText(PayEnterActivity.this, mResult, Toast.LENGTH_SHORT).show();
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
