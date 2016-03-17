package com.skyjaj.hors.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.skyjaj.hors.R;

/**
 * 性别选择对话框
 * Created by Administrator on 2016/3/16.
 */
public class ChooseSexDialog implements View.OnClickListener{

    private Context mContext;
    private OnItemClickListener mListener;
    private Dialog mDialog;
    private RadioButton f,m;


    public ChooseSexDialog(Context mContext, OnItemClickListener listener) {
        this.mContext = mContext;
        this.mListener = listener;
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //获取布局
        View v = inflater.inflate(R.layout.dialog_choose_sex, null);
        //加载最外层view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.choose_sex_view);

        f = (RadioButton) v.findViewById(R.id.choose_sex_f);
        m = (RadioButton) v.findViewById(R.id.choose_sex_m);
        f.setOnClickListener(this);
        m.setOnClickListener(this);


        mDialog =  new android.support.v7.app.AlertDialog.Builder(mContext)
                .setView(layout)
                .show();

    }

    @Override
    public void onClick(View v) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mListener.onDialogItemClick(v);
    }

    //回调接口
    public interface OnItemClickListener {
        void onDialogItemClick(View view);

    }


    //1为男
    public void setChoose(int sex) {
        Log.i("skyjaj", "sex: " + sex);
        if (sex == 1) {
            m.setChecked(false);
            f.setChecked(true);
        } else {
            f.setChecked(false);
            m.setChecked(true);
        }
    }



}
