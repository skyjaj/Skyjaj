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
import android.widget.RadioButton;
import android.widget.TextView;

import com.skyjaj.hors.R;

/**
 * 编辑对话框
 * Created by Administrator on 2016/3/16.
 */
public class EditTextDialog implements View.OnClickListener{

    private Context mContext;
    private OnEditListener mListener;
    private Dialog mDialog;
    private TextView mTitle;
    private EditText mContent;
    private Button ok,cancel;


    public EditTextDialog(Context mContext, OnEditListener listener) {
        this.mContext = mContext;
        this.mListener = listener;
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //获取布局
        View v = inflater.inflate(R.layout.dialog_edittext, null);
        //加载最外层view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dailog_edittext_view);

        mTitle = (TextView) v.findViewById(R.id.dialog_edit_title);
        mContent = (EditText) v.findViewById(R.id.dialog_edit_content);
        ok = (Button) v.findViewById(R.id.dialog_edit_ok);
        cancel = (Button) v.findViewById(R.id.dialog_edit_cancel);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mListener.textChange(s + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


//        mDialog =  new android.support.v7.app.AlertDialog.Builder(mContext)
//                .setView(layout)
//                .show();


        mDialog = new Dialog(mContext, R.style.loading_dialog);// 创建自定义样式dialog

        mDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();

    }

    @Override
    public void onClick(View v) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mListener.onDialogItemClick(v, mContent);
    }

    public interface OnEditListener {

        void onDialogItemClick(View view,EditText editText);

        void textChange(String s);

    }

    /**
     * 设置edittext默认内容
     * @param content
     */
    public void setContent(String content) {
        mContent.setText(content);
    }

    /**
     * 设置默认标题
     * @param title
     */
    public void setTitle(String title) {

        mTitle.setText(title);
    }



    public String getContent() {
       return mContent.getText().toString();
    }



}
