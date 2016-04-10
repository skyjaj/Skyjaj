package com.skyjaj.hors.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hp.hpl.sparta.xpath.TrueExpr;
import com.skyjaj.hors.R;
import com.skyjaj.hors.adapter.TimestampTypeAdapter;
import com.skyjaj.hors.bean.Doctor;
import com.skyjaj.hors.bean.LoginInformation;
import com.skyjaj.hors.bean.Patient;
import com.skyjaj.hors.bean.SystemUser;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.RoleConstant;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;
import com.skyjaj.hors.widget.ChooseSexDialog;
import com.skyjaj.hors.widget.EditTextDialog;

import org.litepal.crud.DataSupport;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.List;

/**
 * 用户信息显示界面
 * Created by skyjaj on 2016/3/5.
 */
public class UserInformactionAcitvity extends BaseActivity {

    //UI
    private ImageView mHeadIV;
    private LinearLayout mAccountView, mSexView,mSignitureView, mRegionView , mNicknameView;
    private TextView mAccountTv,mSexTv,mSignitureTv,mRegionTv,mNicknameTv;
    private Toolbar mToolbar;

    private Patient mPatient;
    private Doctor mDoctor;
    private SystemUser mAdmin;
    private NetworkTask mNetworkTask;
    private String role;
    private SharedPreferences sharedPreferences;

    private Patient patient;
    private Doctor doctor;
    private SystemUser systemUser;

    private ChooseSexDialog mSexDialog;
    private boolean isEdit;
    private MenuItem menuItem;

    private final int PATIENT =1 , DOCTOR =2, ADMIN =3 ,UPDATE=4;
    private Dialog mDialog;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case PATIENT:
                    setPatientUI();
                    break;
                case DOCTOR:
                    setDoctorUI();
                    break;
                case ADMIN:
                    setAdminUI();
                    break;
                case UPDATE:
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    Toast.makeText(UserInformactionAcitvity.this, (String)msg.obj.toString().replaceAll("\"",""), Toast.LENGTH_SHORT).show();

            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_informaction);
        mToolbar = ToolbarStyle.initToolbar(this, R.id.user_informaction_toolbar, "个人信息");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();

        sharedPreferences = this.getSharedPreferences("horsUserInfo", Context.MODE_PRIVATE);
        role = sharedPreferences.getString("role", "");

        mNetworkTask = new NetworkTask();
        mNetworkTask.execute();

    }

    private void initView() {
        mHeadIV = (ImageView) findViewById(R.id.user_informaction_head);
        mAccountTv = (TextView) findViewById(R.id.user_informaction_account);
        mNicknameTv = (TextView) findViewById(R.id.user_informaction_nickname);
        mSexTv= (TextView) findViewById(R.id.user_informaction_sex);
        mSignitureTv = (TextView) findViewById(R.id.user_informaction_signtrue);
        mRegionTv = (TextView) findViewById(R.id.user_informaction_region);
    }

    public void setPatientUI() {
        if (mPatient != null) {
            mAccountTv.setText(mPatient.getMobile());
            mNicknameTv.setText(mPatient.getUsername());
            mSexTv.setText(mPatient.getSex() == 1 ? "男" : "女");
            mRegionTv.setText(mPatient.getRegion());
            mSignitureTv.setText(mPatient.getSignature());
        }
    }

    public void setDoctorUI() {
        if (mDoctor != null) {
            mAccountTv.setText(mDoctor.getMobile());
            mNicknameTv.setText(mDoctor.getUsername());
            mSexTv.setText(mDoctor.getSex() == 1 ? "男" : "女");
            mRegionTv.setText(mDoctor.getAddress() != null ? mDoctor.getAddress() : "");
            mSignitureTv.setText(mDoctor.getSignature()+"");
        }
    }

    public void setAdminUI(){
        if (mAdmin != null) {
            mAccountTv.setText(mAdmin.getMobile());
            mNicknameTv.setText(mAdmin.getUsername());
            mSexTv.setText(mAdmin.getSex() == 1 ? "男" : "女");
            mRegionTv.setText(mAdmin.getAddress()!=null?mAdmin.getAddress():"");
            mSignitureTv.setText(mAdmin.getSignature());
        }
    }


    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.user_informaction_sex_view:
                ChooseSexDialog.OnItemClickListener sexListener = new ChooseSexDialog.OnItemClickListener() {
                    @Override
                    public void onDialogItemClick(View v) {
                        switch (v.getId()) {
                            case R.id.choose_sex_f:
                                mSexTv.setText("男");
                                break;
                            case R.id.choose_sex_m:
                                mSexTv.setText("女");
                                break;
                        }
                        if (menuItem!=null&&!menuItem.isVisible()) {
                            menuItem.setVisible(true);
                        }
                    }
                };
                mSexDialog = new ChooseSexDialog(this, sexListener);
                mSexDialog.setChoose("男".equals(mSexTv.getText().toString()) ? 1 : 0);
                break;
            case R.id.user_informaction_nickname_view:

                EditTextDialog.OnEditListener listener = new EditTextDialog.OnEditListener() {
                    @Override
                    public void onDialogItemClick(View view,EditText content) {
                        if (view.getId() == R.id.dialog_edit_ok) {
                            mNicknameTv.setText(content.getText());
                        }
                    }

                    @Override
                    public void textChange(String s) {
                        isEdit= true;
                        if (menuItem!=null&&!menuItem.isVisible()) {
                            menuItem.setVisible(true);
                        }

                    }
                };
                EditTextDialog dialog = new EditTextDialog(UserInformactionAcitvity.this, listener);
                dialog.setTitle("修改昵称");
                dialog.setContent(mNicknameTv.getText().toString());

//                Toast.makeText(this, "user_informaction_nickname_view ",Toast.LENGTH_SHORT).show();
                break;
            case R.id.user_informaction_signtrue_view:
                EditTextDialog.OnEditListener singitrueListener = new EditTextDialog.OnEditListener() {
                    @Override
                    public void onDialogItemClick(View view,EditText content) {
                        if (view.getId() == R.id.dialog_edit_ok) {
                            mSignitureTv.setText(content.getText());
                        }
                    }

                    @Override
                    public void textChange(String s) {
                        isEdit= true;
                        if (menuItem!=null&&!menuItem.isVisible()) {
                            menuItem.setVisible(true);
                        }

                    }
                };
                EditTextDialog singitrueDialog= new EditTextDialog(UserInformactionAcitvity.this, singitrueListener);
                singitrueDialog.setTitle("修改签名");
                singitrueDialog.setContent(mSignitureTv.getText().toString());
//                Toast.makeText(this, "user_informaction_signtrue_view ",Toast.LENGTH_SHORT).show();
                break;
            case R.id.user_informaction_region_view:

                EditTextDialog.OnEditListener regionListener = new EditTextDialog.OnEditListener() {
                    @Override
                    public void onDialogItemClick(View view,EditText content) {
                        if (view.getId() == R.id.dialog_edit_ok) {
                            mRegionTv.setText(content.getText());
                        }
                    }

                    @Override
                    public void textChange(String s) {
                        isEdit= true;
                        if (menuItem!=null&&!menuItem.isVisible()) {
                            menuItem.setVisible(true);
                        }

                    }
                };
                EditTextDialog regionDialog= new EditTextDialog(UserInformactionAcitvity.this, regionListener);
                regionDialog.setTitle("修改地区");
                regionDialog.setContent(mRegionTv.getText().toString());

//                Toast.makeText(this, "user_informaction_region_view ",Toast.LENGTH_SHORT).show();
                break;
            case R.id.user_informaction_head_view:
                Toast.makeText(this, "user_informaction_head_view ",Toast.LENGTH_SHORT).show();
                break;
        }
    }



    //菜单选择，并打开加载对话框
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (R.id.action_bar_save == item.getItemId()) {
            if (mDialog == null) {
                mDialog = DialogStylel.createLoadingDialog(this, "正在保存..");
                mDialog.show();
            } else if (!mDialog.isShowing()){
                mDialog.show();
            }
            Log.i("skyjaj", " sex :" + mSexTv.getText().toString());
            updateInformation(mNicknameTv.getText().toString(), mRegionTv.getText().toString(),
                    mSignitureTv.getText().toString(), "男".equals(mSexTv.getText().toString()) ? true : false);
//            Toast.makeText(this, "save ", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_information, menu);
        menuItem = menu.getItem(0);
        Log.i("skyjaj", "title "+menu.getItem(0).getTitle().toString());
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //异步更新保存信息
    public void updateInformation(final String  nickname,final String region,final String signtrue,final boolean sex) {
        Log.i("skyaj", nickname + "　re :" + region + "　signtrue :" + signtrue + "  sex :" + sex);
        synchronized (UserInformactionAcitvity.class) {
            if (menuItem != null && menuItem.isVisible()) {
                menuItem.setVisible(false);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i("skyjaj", "thread run");
                    String updateResult = "";
                    Gson gson = new Gson();
                    try {
                        if (patient != null && RoleConstant.PATIENT.equals(role)) {
                            patient.setUsername(nickname);
                            patient.setSex(sex == true ? 1 : 0);
                            patient.setMobile(mPatient.getMobile());
                            patient.setSignature(signtrue);
                            patient.setRegion(region);
                            updateResult = OkHttpManager.post(ServerAddress.PATIENT_INFORMACTION_UPDATE_URL, gson.toJson(patient));
                        } else if (doctor != null && RoleConstant.DOCTOR.equals(role)) {
                            doctor.setUsername(nickname);
                            doctor.setSex(sex == true ? 1 : 0);
                            doctor.setAddress(region);
                            doctor.setMobile(mDoctor.getMobile());
                            doctor.setSignature(signtrue);
                            updateResult = OkHttpManager.post(ServerAddress.DOCTOR_INFORMACTION_UPDATE_URL, gson.toJson(doctor));
                        } else if (systemUser != null && RoleConstant.ADMIN.equals(role)) {
                            systemUser.setUsername(nickname);
                            systemUser.setAddress(region);
                            systemUser.setSex(sex == true ? 1 : 0);
                            systemUser.setSignature(signtrue);
                            systemUser.setMobile(mAdmin.getMobile());
                            updateResult = OkHttpManager.post(ServerAddress.ADMIN_INFORMACTION_UPDATE_URL, gson.toJson(systemUser));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        updateResult = "网络异常，修改失败";
                    }
                    Message msg = new Message();
                    msg.what = UPDATE;
                    msg.obj = updateResult;
                    mHandler.sendMessage(msg);
                }
            }).start();
        }
    }


    //网络任务
    public class NetworkTask extends AsyncTask<Void, Void, Boolean> {

        private String result;

        @Override
        protected Boolean doInBackground(Void... params) {

            List<LoginInformation> lif = DataSupport.where("state = ?", "1").find(LoginInformation.class);
            Log.i("skyjaj", "role :" + role);
            if (lif == null || lif.size()==0) {
                return false;
            }

            try {
                Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).create();
                Message msg = new Message();
                if (RoleConstant.PATIENT.equals(role)) {//PATIENT
                    patient = new Patient();
                    patient.setUsername(lif.get(0).getUsername());
                    patient.setToken(lif.get(0).getToken());
                    result = OkHttpManager.post(ServerAddress.PATIENT_INFORMACTION_URL, new Gson().toJson(patient));
                    try {
                        mPatient = gson.fromJson(result, new TypeToken<Patient>() {
                        }.getType());
                        Log.i("skyjaj", "result ;" + result);
                    } catch (Exception e) {
                        result = "获取信息失败";
                        return false;
                    }
                    if (mPatient != null) {
                        Log.i("skyjaj", "mResult ;" + mPatient);
                        msg.what = PATIENT;
                        mHandler.sendMessage(msg);
                        return true;
                    }
                } else if (RoleConstant.DOCTOR.equals(role)) {//DOCTOR
                    doctor = new Doctor();
                    doctor.setItemType(null);
                    Log.i("skyjaj", "lif.get(0).getUid() " + lif.get(0).getUid());
                    doctor.setId(lif.get(0).getUid());
                    doctor.setToken(lif.get(0).getToken());
                    result = OkHttpManager.post(ServerAddress.DOCTOR_INFORMACTION_URL, new Gson().toJson(doctor));
                    Log.i("skyjaj", "result ;" + result);
                    try {
                        mDoctor = gson.fromJson(result, new TypeToken<Doctor>() {
                        }.getType());
                    } catch (Exception e) {
                        result = "获取信息失败";
                        return false;
                    }
                    if (mDoctor != null) {
                        msg.what = DOCTOR;
                        mHandler.sendMessage(msg);
                        return true;
                    }
                } else if (RoleConstant.ADMIN.equals(role)) {//ADMIN
                    systemUser = new SystemUser();
                    systemUser.setId(lif.get(0).getUid());
                    systemUser.setToken(lif.get(0).getToken());
                    result = OkHttpManager.post(ServerAddress.ADMIN_INFORMACTION_URL, new Gson().toJson(systemUser));
                    Log.i("skyjaj", "result :" + result);
                    try {
                        mAdmin = gson.fromJson(result, new TypeToken<SystemUser>() {}.getType());
                    } catch (Exception e) {
                        result = "获取信息失败";
                        return false;
                    }
                    if (mAdmin != null) {
                        msg.what = ADMIN;
                        mHandler.sendMessage(msg);
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = "网络请求失败";
                return false;
            }

                result = "获取信息失败";
                return false;

        }


        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {

            } else {
                Toast.makeText(UserInformactionAcitvity.this, result, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
