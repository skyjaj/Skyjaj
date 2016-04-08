package com.skyjaj.hors.admin.activities;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.skyjaj.hors.R;
import com.skyjaj.hors.bean.Doctor;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.RoleConstant;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;
import com.skyjaj.hors.widget.ChooseSexDialog;
import com.skyjaj.hors.widget.EditTextDialog;
import com.skyjaj.hors.widget.SettingDialog;

public class UpdateDoctorActivity extends AppCompatActivity {

    public Toolbar mToolbar;
    private boolean isEdit;
    private ImageView mHeadIV;
    private TextView mAccountTv,mSexTv,mSignitureTv,
            mRegionTv,mNicknameTv,mTitleTv,mPasswordTv,
            mIntroductionTv;
    private MenuItem menuItem;
    private Dialog mDialog;
    private Doctor mDoctor;
    private final int UPDATE = 1 , INSERT = 2;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case UPDATE:
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    Toast.makeText(UpdateDoctorActivity.this, (String)msg.obj.toString().replaceAll("\"",""), Toast.LENGTH_SHORT).show();
                break;

                case INSERT:
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    Toast.makeText(UpdateDoctorActivity.this, (String)msg.obj.toString().replaceAll("\"",""), Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_doctor);
        mDoctor = (Doctor) getIntent().getSerializableExtra("doctor");

        if (mDoctor == null) {
            mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "添加医生");
        } else {
            mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "修改医生信息");
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
        mHeadIV = (ImageView) findViewById(R.id.update_doctor_head);
        mAccountTv = (TextView) findViewById(R.id.update_doctor_account);
        mNicknameTv = (TextView) findViewById(R.id.update_doctor_nickname);
        mSexTv= (TextView) findViewById(R.id.update_doctor_sex);
        mSignitureTv = (TextView) findViewById(R.id.update_doctor_signtrue);
        mRegionTv = (TextView) findViewById(R.id.update_doctor_region);
        mTitleTv = (TextView) findViewById(R.id.update_doctor_title);
        mPasswordTv = (TextView) findViewById(R.id.update_doctor_password);
        mIntroductionTv = (TextView) findViewById(R.id.update_doctor_introduction);

        if (mDoctor != null) {
            mAccountTv.setText(mDoctor.getMobile());
            mNicknameTv.setText(mDoctor.getName());
            mSexTv.setText(mDoctor.getSex() == 1 ? "男" : "女");
            mSignitureTv.setText(mDoctor.getSignature());
            mRegionTv.setText(mDoctor.getAddress());
            mIntroductionTv.setText(mDoctor.getIntroduction());
            mTitleTv.setText(mDoctor.getTitle());
        } else {
            mAccountTv.setText("");
            mNicknameTv.setText("");
            mSexTv.setText("男");
            mSignitureTv.setText("");
            mRegionTv.setText("");
            mIntroductionTv.setText("");
            mPasswordTv.setText("");
            mTitleTv.setText("");
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.update_doctor_account_view:

                if (mDoctor != null) {
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
                EditTextDialog dialog = new EditTextDialog(UpdateDoctorActivity.this, accountListener);
                dialog.setTitle("输入手机号");
                dialog.setContent(mAccountTv.getText().toString());

                break;

            case R.id.update_doctor_sex_view:
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
            case R.id.update_doctor_nickname_view:

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
                EditTextDialog accountDialog = new EditTextDialog(UpdateDoctorActivity.this, listener);
                accountDialog.setTitle("修改昵称");
                accountDialog.setContent(mNicknameTv.getText().toString());
                break;

            case R.id.update_doctor_signtrue_view:
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
                EditTextDialog singitrueDialog= new EditTextDialog(UpdateDoctorActivity.this, singitrueListener);
                singitrueDialog.setTitle("修改签名");
                singitrueDialog.setContent(mSignitureTv.getText().toString());
//                Toast.makeText(this, "user_informaction_signtrue_view ",Toast.LENGTH_SHORT).show();
                break;
            case R.id.update_doctor_region_view:

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
                EditTextDialog regionDialog= new EditTextDialog(UpdateDoctorActivity.this, regionListener);
                regionDialog.setTitle("修改地区");
                regionDialog.setContent(mRegionTv.getText().toString());

                break;
            case R.id.update_doctor_title_view:
                EditTextDialog.OnEditListener titleListener = new EditTextDialog.OnEditListener() {
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
                EditTextDialog titleDialog= new EditTextDialog(UpdateDoctorActivity.this, titleListener);
                titleDialog.setTitle("修改职称");
                titleDialog.setContent(mTitleTv.getText().toString());
                break;

            case R.id.update_doctor_introduction_view:
                EditTextDialog.OnEditListener introductionListener = new EditTextDialog.OnEditListener() {
                    @Override
                    public void onDialogItemClick(View view,EditText content) {
                        if (view.getId() == R.id.dialog_edit_ok) {
                            mIntroductionTv.setText(content.getText());
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
                EditTextDialog introductionDialog= new EditTextDialog(UpdateDoctorActivity.this, introductionListener);
                introductionDialog.setTitle("修改简介");
                introductionDialog.setContent(mTitleTv.getText().toString());
                break;
            case R.id.update_doctor_password_view:
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
                settingDialog.setViewVisibility(R.id.dialog_update_origin_password,View.GONE);
                break;
        }
    }


    private void updateInformation() {
        synchronized (UpdateDoctorActivity.class) {
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
                        Doctor doctor = new Doctor();
                        doctor.setUsername(mNicknameTv.getText().toString());
                        doctor.setSex("男".equals(mSexTv.getText().toString()) == true ? 1 : 0);
                        doctor.setAddress(mRegionTv.getText().toString());
                        doctor.setMobile(mDoctor==null?"":mDoctor.getMobile());
                        doctor.setSignature(mSignitureTv.getText().toString());
                        doctor.setTitle(mTitleTv.getText().toString());
                        doctor.setPassword(mPasswordTv.getText().toString());
                        doctor.setIntroduction(mIntroductionTv.getText().toString());
                        doctor.setItemType(null);
                        updateResult = OkHttpManager.post(null, gson.toJson(doctor));
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

    private void insertDoctor() {
        synchronized (UpdateDoctorActivity.class) {
            if (menuItem != null && menuItem.isVisible()) {
                menuItem.setVisible(false);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i("skyjaj", "insertDoctor thread run");
                    String updateResult = "";
                    Gson gson = new Gson();
                    try {
                        Doctor doctor = new Doctor();
                        doctor.setUsername(mNicknameTv.getText().toString());
                        doctor.setSex("男".equals(mSexTv.getText().toString()) == true ? 1 : 0);
                        doctor.setAddress(mRegionTv.getText().toString());
                        doctor.setMobile(mDoctor==null?"":mDoctor.getMobile());
                        doctor.setSignature(mSignitureTv.getText().toString());
                        doctor.setTitle(mTitleTv.getText().toString());
                        doctor.setPassword(mPasswordTv.getText().toString());
                        doctor.setIntroduction(mIntroductionTv.getText().toString());
                        doctor.setItemType(null);
                        updateResult = OkHttpManager.post(null, gson.toJson(doctor));
                    } catch (Exception e) {
                        e.printStackTrace();
                        updateResult = "网络异常，修改失败";
                    }
                    Message msg = new Message();
                    msg.what = INSERT;
                    msg.obj = updateResult;
                    mHandler.sendMessage(msg);
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
                mDialog.show();
            } else if (!mDialog.isShowing()){
                mDialog.show();
            }
            Log.i("skyjaj", " sex :" + mSexTv.getText().toString());

            if (mDoctor == null) {
                insertDoctor();
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
