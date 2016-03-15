package com.skyjaj.hors.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.skyjaj.hors.R;
import com.skyjaj.hors.utils.DialogStylel;

/**
 * Created by Administrator on 2016/3/12.
 */
public class BaseActivity extends AppCompatActivity {


    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        dialog = DialogStylel.createLoadingDialog(this, "加载中...");
        dialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
