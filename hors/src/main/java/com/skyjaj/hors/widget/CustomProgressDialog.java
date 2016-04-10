package com.skyjaj.hors.widget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.skyjaj.hors.R;

/**
 * 自定义数据加载对话框
 * Created by Administrator on 2016/3/11.
 */
public class CustomProgressDialog extends ProgressDialog {


    private Context mContext;
    private ProgressBar progressBar;
    private String mLoadingTip;
    private TextView mLoadingTv;
    private int count;
    private String oldLoadingTip;
    private int mResid;
    private AnimationDrawable mAnimation;
    private int mTheme;


    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = mContext;
        this.mTheme= theme;

    }

    public CustomProgressDialog(Context context, String text ,int resid) {
        super(context);
        this.mContext = context;
        this.mLoadingTip = text;
        this.mResid = resid;
        setCanceledOnTouchOutside(false);
//        new CustomProgressDialog(context, R.style.loading_dialog);
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }


    private void initData() {

        progressBar.setBackgroundResource(mResid);
        // 通过ImageView对象拿到背景显示的AnimationDrawable
        mAnimation = (AnimationDrawable) progressBar.getBackground();
        // 为了防止在onCreate方法中只显示第一帧的解决方案之一
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                mAnimation.start();

            }
        });
        mLoadingTv.setText(mLoadingTip);

    }

    public void setContent(String str) {
        mLoadingTv.setText(str);
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.custom_progress_dialog, null);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mContext, null);
//        params.gravity = Gravity.CENTER_HORIZONTAL;

        setContentView(layout, new LinearLayout.LayoutParams(
                80,
                80));
        mLoadingTv = (TextView) findViewById(R.id.loadingTv);
        progressBar = (ProgressBar) findViewById(R.id.loadingIv);
    }

}
