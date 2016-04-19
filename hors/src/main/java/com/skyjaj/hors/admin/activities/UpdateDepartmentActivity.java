package com.skyjaj.hors.admin.activities;

import android.app.Dialog;
import android.content.DialogInterface;
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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.BaseActivity;
import com.skyjaj.hors.adapter.CalendarTypeAdapter;
import com.skyjaj.hors.admin.wigwet.ChooseParentDepartmentDialog;
import com.skyjaj.hors.bean.BaseMessage;
import com.skyjaj.hors.bean.Department;
import com.skyjaj.hors.bean.ScheduleOfMonth;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.PinYinUtil;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;
import com.skyjaj.hors.widget.EditTextDialog;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UpdateDepartmentActivity extends BaseActivity {

    public Toolbar mToolbar;
    private boolean isEdit;
    private TextView mCnNameTv,mEnNameTv,mMobileTv,
            mIsParentTv,mAddressTv;
    private MenuItem menuItem;
    private Dialog mDialog;
    private Department mDepartment;
    private Department mParent;

    private final int UPDATE = 1 , INSERT = 2 , MESSAGE = 3 , PARENT_DEPARTMENT = 4;

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
                case PARENT_DEPARTMENT:
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                    openParentDepartmentChooseDialog((List) msg.obj);
                    break;
                case MESSAGE:
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
        mParent = null;
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

        mDialog = DialogStylel.createLoadingDialog(this, "加载中..");
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
                getParentDepartment();
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


    /**
     * 弹出父级科室对话框的选择
     * @param departments
     */
    private void openParentDepartmentChooseDialog(List departments) {
        ChooseParentDepartmentDialog.OnItemClickListener listener = new
                ChooseParentDepartmentDialog.OnItemClickListener() {
                    @Override
                    public void onItemClick(Department department) {
                        if (department != null) {
                            mIsParentTv.setText(department.getNameCn());
                            if (department.getItemType() != BaseMessage.Type.OUTCOMING) {
                                mParent = department;
                            }
                        }
                    }

                    @Override
                    public void onDismissListener(DialogInterface dialog) {

                    }

                    @Override
                    public void onCancelListener(DialogInterface dialog) {

                    }
                };
        ChooseParentDepartmentDialog chooseDialog = new ChooseParentDepartmentDialog(listener, this, departments);

    }

    /**
     * 获取父级科室信息
     */
    private void getParentDepartment() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                List<Department> departments = null;
                Message msg = new Message();
                try {
                    result = OkHttpManager.post(ServerAddress.ADMIN_FIND_PARENT_DEPARTMENT_URL);
                    Log.i("skyjaj", "result :" + result);
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "网络异常，请稍后重试";
                    msg.what = MESSAGE;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                    return;
                }
                try {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new CalendarTypeAdapter()).create();
                    departments = gson.fromJson(result,new TypeToken<List<Department>>() {}.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "解释数据失败，请重试";
                    msg.what = MESSAGE;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                    return;
                }

                if (departments == null || departments.size() == 0) {
                    msg.what = MESSAGE;
                    departments = new ArrayList<Department>();
                    Department d = new Department();
                    d.setNameCn("无");
                    d.setItemType(BaseMessage.Type.OUTCOMING);
                    msg.obj = departments;
                } else {
                    msg.what = PARENT_DEPARTMENT;
                    Department d = new Department();
                    d.setNameCn("无");
                    d.setItemType(BaseMessage.Type.OUTCOMING);
                    departments.add(0,d);
                    msg.obj = departments;
                }
                mHandler.sendMessage(msg);

            }
        }).start();
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
                        depart.setParentId(mParent!=null?mParent.getId():"0");
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