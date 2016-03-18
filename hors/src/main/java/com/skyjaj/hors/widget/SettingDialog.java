package com.skyjaj.hors.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skyjaj.hors.R;

/**
 * Created by Administrator on 2016/3/18.
 */
public class SettingDialog implements View.OnClickListener {

    private Dialog mDialog;
    private Context mContext;
    private SettingDialogListener listener;

    private TextView title;

    private EditText riginTv,password1,password2;

    private Button ok,cancel;


    public SettingDialog(Context mContext, SettingDialogListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        init();
    }

    private void init() {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_update_password, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dailog_update_password_view);// 加载布局

        ok = (Button) v.findViewById(R.id.dialog_update_password_ok);
        cancel = (Button) v.findViewById(R.id.dialog_update_password_cancel);

        title = (TextView) v.findViewById(R.id.dialog_update_password_title);

        riginTv = (EditText) v.findViewById(R.id.dialog_update_origin_password);
        password1 = (EditText) v.findViewById(R.id.dialog_update_password1);
        password2 = (EditText) v.findViewById(R.id.dialog_update_password2);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);


        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //invalidate2Psw(s + "", password1.getText() + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDialog = new Dialog(mContext, R.style.loading_dialog);// 创建自定义样式dialog

        mDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));// 设置布局
        mDialog.setCanceledOnTouchOutside(true);

        mDialog.show();


    }


//    public void invalidate2Psw(String s1, String s2) {
//        if (TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2)) {
//            Toast.makeText(mContext, "两次密码不一样，不能为空", Toast.LENGTH_SHORT).show();
//            ok.setEnabled(false);
//            return;
//        }
//        if (!s1.equals(s2)) {
//            Toast.makeText(mContext, "两次密码不一样", Toast.LENGTH_SHORT).show();
//            ok.setEnabled(false);
//            return;
//        }
//
//        ok.setEnabled(true);
//
//    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.dialog_update_password_ok:

                if (TextUtils.isEmpty(riginTv.getText()+"") || TextUtils.isEmpty(password1.getText()+"") ||
                        !(password1.getText()+"").equals(password2.getText()+"")) {
//                    Log.i("skyjaj", "psw :" + riginTv.getText());
//                    Log.i("skyjaj", "psw :" + password1.getText());
//                    Log.i("skyjaj", "psw :" + password2.getText());
//                    Log.i("skyjaj", "psw :" + (password1.getText()+"").equals(password2.getText()+""));
                    Toast.makeText(mContext, "两次密码不一样,不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                listener.onClick(v,riginTv.getText()+"",password2.getText()+"");
                break;
            case  R.id.dialog_update_password_cancel:
                break;
        }

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

    }


    public void setTitleContent(String titleContent) {
        if (title != null) {
            title.setText(titleContent);
        }
    }


    public interface  SettingDialogListener{
        void onClick(View view,String originPassword,String newPassword);
    }


}
