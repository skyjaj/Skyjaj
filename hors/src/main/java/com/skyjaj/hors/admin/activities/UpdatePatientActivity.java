package com.skyjaj.hors.admin.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.BaseActivity;
import com.skyjaj.hors.bean.Doctor;
import com.skyjaj.hors.bean.Patient;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;
import com.skyjaj.hors.widget.ChooseSexDialog;
import com.skyjaj.hors.widget.EditTextDialog;
import com.skyjaj.hors.widget.SettingDialog;

public class UpdatePatientActivity extends BaseActivity {

    public Toolbar mToolbar;
    private boolean isEdit;
    private ImageView mHeadIV;
    private TextView mAccountTv,mSexTv,mSignitureTv,
            mRegionTv,mNicknameTv,mPasswordTv;
    private MenuItem menuItem;
    private Dialog mDialog;
    private Patient mPatient;
    private final int UPDATE = 1 , INSERT = 2, MESSAGE = 3;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case UPDATE:
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    Toast.makeText(UpdatePatientActivity.this, "已更新", Toast.LENGTH_SHORT).show();
                break;

                case INSERT:
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    setUIEmpty();
                    Toast.makeText(UpdatePatientActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    break;

                case MESSAGE:
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    Toast.makeText(UpdatePatientActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_patient);
        mPatient = (Patient) getIntent().getSerializableExtra("patient");

        if (mPatient == null) {
            mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "添加患者");
        } else {
            mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "修改患者信息");
        }
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        initView();
    }


    private void initView() {
        mHeadIV = (ImageView) findViewById(R.id.update_patient_head);
        mAccountTv = (TextView) findViewById(R.id.update_patient_account);
        mNicknameTv = (TextView) findViewById(R.id.update_patient_nickname);
        mSexTv= (TextView) findViewById(R.id.update_patient_sex);
        mSignitureTv = (TextView) findViewById(R.id.update_patient_signtrue);
        mRegionTv = (TextView) findViewById(R.id.update_patient_region);
        mPasswordTv = (TextView) findViewById(R.id.update_patient_password);

        if (mPatient != null) {
            mAccountTv.setText(mPatient.getMobile());
            mNicknameTv.setText(mPatient.getUsername());
            mSexTv.setText(mPatient.getSex() == 1 ? "男" : "女");
            mSignitureTv.setText(mPatient.getSignature());
            mRegionTv.setText(mPatient.getRegion());
            mPasswordTv.setText(mPatient.getPassword());
        } else {
            mAccountTv.setText("");
            mNicknameTv.setText("");
            mSexTv.setText("男");
            mSignitureTv.setText("");
            mRegionTv.setText("");
            mPasswordTv.setText("");
        }
    }

    public void setUIEmpty() {
        try {
            mAccountTv.setText("");
            mNicknameTv.setText("");
            mSexTv.setText("男");
            mSignitureTv.setText("");
            mRegionTv.setText("");
            mPasswordTv.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.update_patient_account_view:

                if (mPatient != null) {
                    return;
                }

                EditTextDialog.OnEditListener accountListener = new EditTextDialog.OnEditListener() {
                    @Override
                    public void onDialogItemClick(View view,EditText content) {
                        if (view.getId() == R.id.dialog_edit_ok) {
                            mAccountTv.setText(content.getText());
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
                EditTextDialog dialog = new EditTextDialog(UpdatePatientActivity.this, accountListener);
                dialog.setTitle("输入手机号");
                dialog.setContent(mAccountTv.getText().toString());

                break;

            case R.id.update_patient_sex_view:
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
                ChooseSexDialog mSexDialog = new ChooseSexDialog(this, sexListener);
                mSexDialog.setChoose("男".equals(mSexTv.getText().toString()) ? 1 : 0);
                break;
            case R.id.update_patient_nickname_view:

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
                EditTextDialog accountDialog = new EditTextDialog(UpdatePatientActivity.this, listener);
                accountDialog.setTitle("修改昵称");
                accountDialog.setContent(mNicknameTv.getText().toString());
                break;

            case R.id.update_patient_signtrue_view:
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
                EditTextDialog singitrueDialog= new EditTextDialog(UpdatePatientActivity.this, singitrueListener);
                singitrueDialog.setTitle("修改签名");
                singitrueDialog.setContent(mSignitureTv.getText().toString());
//                Toast.makeText(this, "user_informaction_signtrue_view ",Toast.LENGTH_SHORT).show();
                break;
            case R.id.update_patient_region_view:

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
                EditTextDialog regionDialog= new EditTextDialog(UpdatePatientActivity.this, regionListener);
                regionDialog.setTitle("修改地区");
                regionDialog.setContent(mRegionTv.getText().toString());

                break;

            case R.id.update_patient_password_view:
                SettingDialog.SettingDialogListener pswListener = new SettingDialog.SettingDialogListener() {
                    @Override
                    public void onClick(View view,String oldPassword,String newPassword) {
                        switch (view.getId()) {
                            case R.id.dialog_update_password_ok:
                                mPasswordTv.setText(newPassword);
                                isEdit= true;
                                if (menuItem!=null&&!menuItem.isVisible()) {
                                    menuItem.setVisible(true);
                                }
                                break;
                        }
                    }
                };
                SettingDialog settingDialog = new SettingDialog(this, pswListener);
                settingDialog.setTitleContent("修改密码");
                settingDialog.setDialogType(SettingDialog.DialogType.ADMIN);
                settingDialog.setViewVisibility(R.id.dialog_update_origin_password,View.GONE);
                break;
        }
    }


    private void updateInformation() {
        synchronized (UpdatePatientActivity.class) {
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
                        Patient patient = new Patient();
                        patient.setUsername(mNicknameTv.getText().toString());
                        patient.setSex("男".equals(mSexTv.getText().toString()) == true ? 1 : 0);
                        patient.setRegion(mRegionTv.getText().toString());
                        patient.setMobile(mPatient.getMobile());
                        patient.setSignature(mSignitureTv.getText().toString());
                        patient.setPassword(mPasswordTv.getText().toString());
                        updateResult = OkHttpManager.post(ServerAddress.ADMIN_UPDATE_PATIENT_URL, gson.toJson(patient));
                    } catch (Exception e) {
                        e.printStackTrace();
                        updateResult = "网络异常，修改失败";
                    }

                    if (!TextUtils.isEmpty(updateResult)) {
                        updateResult = updateResult.replaceAll("\"", "");
                    }
                    Message msg = new Message();
                    if ("success".equals(updateResult)) {
                        msg.what = UPDATE;
                        msg.obj = updateResult;
                        mHandler.sendMessage(msg);
                    } else {
                        msg.what = MESSAGE;
                        msg.obj = updateResult;
                        mHandler.sendMessage(msg);
                    }

                }
            }).start();
        }
    }

    private void insertPatient() {
        synchronized (UpdatePatientActivity.class) {
            if (menuItem != null && menuItem.isVisible()) {
                menuItem.setVisible(false);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i("skyjaj", "insertPatient thread run");
                    String result = "";
                    Gson gson = new Gson();
                    try {
                        Patient patient = new Patient();
                        patient.setUsername(mNicknameTv.getText().toString());
                        patient.setSex("男".equals(mSexTv.getText().toString()) == true ? 1 : 0);
                        patient.setRegion(mRegionTv.getText().toString());
                        patient.setMobile(mAccountTv.getText().toString());
                        patient.setSignature(mSignitureTv.getText().toString());
                        patient.setPassword(mPasswordTv.getText().toString());
                        result = OkHttpManager.post(ServerAddress.ADMIN_INSERT_PATIENT_URL, gson.toJson(patient));
                    } catch (Exception e) {
                        e.printStackTrace();
                        result = "网络异常，修改失败";
                    }

                    if (!TextUtils.isEmpty(result)) {
                        result = result.replaceAll("\"", "");
                    }
                    Message msg = new Message();
                    if ("success".equals(result)) {
                        msg.what = INSERT;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    } else {
                        msg.what = MESSAGE;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }

                }
            }).start();
        }
    }

    //菜单选择，并打开加载对话框
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (R.id.action_bar_save == item.getItemId()) {
            if (mDialog == null) {
                mDialog = DialogStylel.createLoadingDialog(this, "正在保存..");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
            } else if (!mDialog.isShowing()){
                mDialog.show();
            }
            Log.i("skyjaj", " sex :" + mSexTv.getText().toString());

            if (mPatient == null) {
                insertPatient();
            } else {
                updateInformation();
            }
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

}
