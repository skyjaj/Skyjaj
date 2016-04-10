package com.skyjaj.hors.admin.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.BaseActivity;
import com.skyjaj.hors.bean.Department;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.PinYinUtil;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;
import com.skyjaj.hors.widget.EditTextDialog;

public class UpdateDepartmentActivity extends BaseActivity {

    public Toolbar mToolbar;
    private boolean isEdit;
    private TextView mCnNameTv,mEnNameTv,mMobileTv,
            mIsParentTv,mAddressTv;
    private MenuItem menuItem;
    private Dialog mDialog;
    private Department mDepartment;
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

                    String rs = (String) msg.obj.toString().replaceAll("\"", "");
//                    if ("已更新".equals(rs) && mDepartment != null) {
//                        mDepartment.setNameCn(mCnNameTv.getText().toString());
//                        mDepartment.setNameEn(mEnNameTv.getText().toString());
//                        mDepartment.setAddress(mAddressTv.getText().toString());
//                        mDepartment.setMobile(mMobileTv.getText().toString());
//                        Toast.makeText(UpdateDepartmentActivity.this,"eqs", Toast.LENGTH_SHORT).show();
//                    }
                    Toast.makeText(UpdateDepartmentActivity.this, rs, Toast.LENGTH_SHORT).show();
                    break;

                case INSERT:
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    Toast.makeText(UpdateDepartmentActivity.this, (String)msg.obj.toString().replaceAll("\"",""), Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_department);
        mDepartment = (Department) getIntent().getSerializableExtra("department");

        if (mDepartment == null) {
            mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "添加科室");
        } else {
            mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "修改科室信息");
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
        mAddressTv = (TextView) findViewById(R.id.update_department_address);
        mMobileTv = (TextView) findViewById(R.id.update_department_mobile);
        mCnNameTv = (TextView) findViewById(R.id.update_department_cnname);
        mEnNameTv = (TextView) findViewById(R.id.update_department_enname);
        mIsParentTv = (TextView) findViewById(R.id.update_department_isparent);

        if (mDepartment != null) {
            mAddressTv.setText(mDepartment.getAddress());
            mMobileTv.setText(mDepartment.getMobile());
            mCnNameTv.setText(mDepartment.getNameCn());
            mEnNameTv.setText(mDepartment.getNameEn());
            mIsParentTv.setText(mDepartment.getParentId().equals("0")==true?"是":"否");
        } else {
            //insert
            mAddressTv.setText("");
            mMobileTv.setText("");
            mCnNameTv.setText("");
            mEnNameTv.setText("");
            mIsParentTv.setText("");
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.update_department_address_view:

                EditTextDialog.OnEditListener accountListener = new EditTextDialog.OnEditListener() {
                    @Override
                    public void onDialogItemClick(View view,EditText content) {
                        if (view.getId() == R.id.dialog_edit_ok) {
                            mAddressTv.setText(content.getText());
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
                EditTextDialog dialog = new EditTextDialog(UpdateDepartmentActivity.this, accountListener);
                dialog.setTitle("输入诊室地址");
                dialog.setContent(mAddressTv.getText().toString());

                break;

            case R.id.update_department_cnname_view:

                EditTextDialog.OnEditListener cnNmaeListener = new EditTextDialog.OnEditListener() {
                    @Override
                    public void onDialogItemClick(View view,EditText content) {
                        if (view.getId() == R.id.dialog_edit_ok) {
                            mCnNameTv.setText(content.getText());
                            mEnNameTv.setText(PinYinUtil.getPingYin(content.getText().toString()));
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
                EditTextDialog cnNmaeDialog = new EditTextDialog(UpdateDepartmentActivity.this, cnNmaeListener);
                cnNmaeDialog.setTitle("修改中文名称");
                cnNmaeDialog.setContent(mCnNameTv.getText().toString());
                break;

            case R.id.update_department_isparent_view:
                if (mDepartment != null) {
                    return;
                }

                break;
            case R.id.update_department_mobile_view:

                EditTextDialog.OnEditListener regionListener = new EditTextDialog.OnEditListener() {
                    @Override
                    public void onDialogItemClick(View view,EditText content) {
                        if (view.getId() == R.id.dialog_edit_ok) {
                            mMobileTv.setText(content.getText());
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
                EditTextDialog regionDialog= new EditTextDialog(UpdateDepartmentActivity.this, regionListener);
                regionDialog.setTitle("修改联系方式");
                regionDialog.setContent(mMobileTv.getText().toString());

                break;
        }
    }


    private void updateInformation() {
        synchronized (UpdateDepartmentActivity.class) {
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
                        Department depart = new Department();
                        depart.setItemType(null);
                        depart.setId(mDepartment.getId());
                        depart.setNameCn(mCnNameTv.getText().toString());
                        depart.setNameEn(mEnNameTv.getText().toString());
                        depart.setAddress(mAddressTv.getText().toString());
                        depart.setMobile(mMobileTv.getText().toString());
                        updateResult = OkHttpManager.post(ServerAddress.ADMIN_UPDATE_DEPARTMENT_URL, gson.toJson(depart));
                        Log.i("skyjaj", "updateResult "+updateResult);
                        Log.i("skyjaj", "gson.toJson(depart) "+gson.toJson(depart));

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

    private void insertDepartment() {
        synchronized (UpdateDepartmentActivity.class) {
            if (menuItem != null && menuItem.isVisible()) {
                menuItem.setVisible(false);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i("skyjaj", "add thread run");
                    String updateResult = "";
                    Gson gson = new Gson();
                    try {
                        Department depart = new Department();
                        depart.setItemType(null);
                        depart.setNameCn(mCnNameTv.getText().toString());
                        depart.setNameEn(mEnNameTv.getText().toString());
                        depart.setAddress(mAddressTv.getText().toString());
                        depart.setMobile(mMobileTv.getText().toString());
                        depart.setParentId("0");
                        updateResult = OkHttpManager.post(ServerAddress.ADMIN_ADD_DEPARTMENT_URL, gson.toJson(depart));
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

            if (mDepartment == null) {
                insertDepartment();
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