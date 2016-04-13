package com.skyjaj.hors.admin.wigwet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skyjaj.hors.R;

/**
 * 科室管理listView长按弹框类
 * Created by Administrator on 2016/3/13.
 */
public class UserManagerForOnItemLongClick implements View.OnClickListener{

    private Context mContext;
    private Dialog mDialog;

    TextView deleteTv, disable,enableTv;


    private UserManagerForOnItemLongClick.OnItemClickListener mListener;


    public UserManagerForOnItemLongClick(OnItemClickListener mListener, Context mContext) {
        this.mListener = mListener;
        this.mContext = mContext;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.manager_user_dialog, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.manager_user_dialog_view);

        deleteTv = (TextView) v.findViewById(R.id.manager_user_dialog_delete);
        disable = (TextView) v.findViewById(R.id.manager_user_dialog_disable);
        enableTv = (TextView) v.findViewById(R.id.manager_user_dialog_enable);
        deleteTv.setOnClickListener(this);
        disable.setOnClickListener(this);
        enableTv.setOnClickListener(this);

//        mDialog = new Dialog(mContext, R.style.loading_dialog);
//
//        // 设置自定义布局
//        mDialog.setContentView(layout, new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));

        //默认布局
        mDialog =  new android.support.v7.app.AlertDialog.Builder(mContext)
                .setView(layout)
                .show();

        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mListener.onCancelListener(dialog);
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mListener.onDismissListener(dialog);
            }
        });

//        mDialog.show();

    }

    @Override
    public void onClick(View v) {
        mListener.onItemClick(v.getId());
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }



    public void setViewVisible(int resId,int visibale) {

        switch (resId) {
            case R.id.manager_user_dialog_delete:
                if (deleteTv != null) {
                    deleteTv.setVisibility(visibale);
                }
                break;
            case R.id.manager_user_dialog_disable:
                if (disable != null) {
                    disable.setVisibility(visibale);
                }
                break;
            case R.id.manager_user_dialog_enable:
                if (enableTv != null) {
                    enableTv.setVisibility(visibale);
                }
                break;
        }


    }

    //回调接口
    public interface OnItemClickListener {
        void onItemClick(int resId);

        void onDismissListener(DialogInterface dialog);

        void onCancelListener(DialogInterface dialog);
    }

}
