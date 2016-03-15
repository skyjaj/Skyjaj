package com.skyjaj.hors.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skyjaj.hors.R;

/**
 * Created by Administrator on 2016/3/14.
 */
public class ShowHistoryLongClicklistener implements View.OnClickListener{


    private Context mContext;
    private Dialog mDialog;
    private ShowHistoryLongClicklistener.OnClickListener mListener;
    private TextView cancel,delete,comment;

    public ShowHistoryLongClicklistener(OnClickListener listener,Context mContext) {
        this.mContext = mContext;
        this.mListener = listener;
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //获取布局
        View v = inflater.inflate(R.layout.show_history_dialog, null);
        //加载最外层view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.show_history_dialog_view);

        cancel = (TextView) v.findViewById(R.id.show_history_cancel);
        delete = (TextView) v.findViewById(R.id.show_history_delete);
        comment = (TextView) v.findViewById(R.id.show_history_comment);
        cancel.setOnClickListener(this);
        delete.setOnClickListener(this);
        comment.setOnClickListener(this);


        mDialog =  new android.support.v7.app.AlertDialog.Builder(mContext)
                .setView(layout)
                .show();
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mListener.onDismissListener(dialog);
            }
        });

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mListener.onCancelListener(dialog);
            }
        });


    }

    @Override
    public void onClick(View v) {
        mListener.onItemClick(v);
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    /**
     * 隐藏view
     * @param resId
     */
    public void setViewGone(int resId) {
        switch (resId) {
            case R.id.show_history_delete:
                delete.setVisibility(View.GONE);
                break;
            case R.id.show_history_cancel:
                cancel.setVisibility(View.GONE);
                break;
            case R.id.show_history_comment:
                comment.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 设置view的可见
     * @param resId
     */
    public void setViewVisible(int resId) {
        switch (resId) {
            case R.id.show_history_delete:
                delete.setVisibility(View.VISIBLE);
                break;
            case R.id.show_history_cancel:
                cancel.setVisibility(View.VISIBLE);
                break;
            case R.id.show_history_comment:
                comment.setVisibility(View.VISIBLE);
                break;
        }
    }


    public interface OnClickListener {

        void onItemClick(View v);

        void onDismissListener(DialogInterface dialog);

        void onCancelListener(DialogInterface dialog);

    }



}
