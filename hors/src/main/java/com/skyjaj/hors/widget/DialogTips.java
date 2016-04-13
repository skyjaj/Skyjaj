package com.skyjaj.hors.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skyjaj.hors.R;

/**
 * 编辑对话框
 * Created by Administrator on 2016/3/16.
 */
public class DialogTips implements View.OnClickListener{

    private Context mContext;
    private OnDialogItemClickListener mListener;
    private Dialog mDialog;
    private TextView mTitle;
    private Button ok,cancel;


    public DialogTips(Context mContext, OnDialogItemClickListener listener) {
        this.mContext = mContext;
        this.mListener = listener;
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //获取布局
        View v = inflater.inflate(R.layout.dialog_tips, null);
        //加载最外层view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_tips_view);

        mTitle = (TextView) v.findViewById(R.id.dialog_tips_title);
        ok = (Button) v.findViewById(R.id.dialog_tips_ok);
        cancel = (Button) v.findViewById(R.id.dialog_tips_cancel);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);


        mDialog = new Dialog(mContext, R.style.loading_dialog);// 创建自定义样式dialog

        mDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

    }

    @Override
    public void onClick(View v) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mListener.onDialogItemClick(v);
    }

    public interface OnDialogItemClickListener {

        void onDialogItemClick(View view);
    }



    /**
     * 设置默认标题
     * @param title
     */
    public void setTitle(String title) {

        mTitle.setText(title);
    }




}
