package com.skyjaj.hors.admin.wigwet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skyjaj.hors.R;

/**
 * 长按医生管理中的item弹出对话框类
 * Created by Administrator on 2016/3/13.
 */
public class DoctorManagerForOnItemLongClickDialog implements View.OnClickListener{


    private Context mContext;

    private Dialog dialog;

    private OnItemClickListener mListener;


    public DoctorManagerForOnItemLongClickDialog(OnItemClickListener listener,Context mContext) {
        this.mContext = mContext;
        mListener = listener;
        init();
    }

    private void init() {


        LayoutInflater inflater = LayoutInflater.from(mContext);
        //获取布局
        View v = inflater.inflate(R.layout.manager_doctor_dialog, null);
        //加载最外层view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.manager_doctor_dialog_view);

        TextView deleteTv = (TextView) v.findViewById(R.id.manager_doctor_delete);
        TextView updateTv = (TextView) v.findViewById(R.id.manager_doctor_update);
        TextView stopTv = (TextView) v.findViewById(R.id.manager_doctor_stop);
        deleteTv.setOnClickListener(this);
        updateTv.setOnClickListener(this);
        stopTv.setOnClickListener(this);


        dialog =  new android.support.v7.app.AlertDialog.Builder(mContext)
                .setView(layout)
                .show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mListener.onDismissListener(dialog);
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mListener.onCancelListener(dialog);
            }
        });


    }

    @Override
    public void onClick(View v) {
        if (dialog != null) {
            dialog.dismiss();
        }
        mListener.onItemClick(v.getId());
    }

    public interface OnItemClickListener {

        void onItemClick(int resId);

        void onDismissListener(DialogInterface dialog);

        void onCancelListener(DialogInterface dialog);
    }



}
