package com.skyjaj.hors.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.skyjaj.hors.R;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.widget.CustomProgressDialog;

/**
 * 动作请求Activity结果回调
 */
public class ActionForActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actioin_for_result);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
//        DialogStylel.createLoadingDialog(this,"加载中..").show();
    }



}
