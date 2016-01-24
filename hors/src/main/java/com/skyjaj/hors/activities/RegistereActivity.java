package com.skyjaj.hors.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.skyjaj.hors.R;
import com.skyjaj.hors.utils.ToolbarStyle;


/**
 * Created by Administrator on 2016/1/16.
 */
public class RegistereActivity extends AppCompatActivity {


    //true 为男
    private Boolean sex=true;

    private Toolbar mToolbar=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initToolbar();
    }

    private void initToolbar() {
        mToolbar = ToolbarStyle.initToolbar(this, R.id.register_toolbar, R.string.register_title);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "on destroy ", Toast.LENGTH_SHORT).show();
    }

    //注册等按钮监听
    public void onRegister(View v) {
        Toast.makeText(this, "register view ", Toast.LENGTH_SHORT).show();
    }

    //radio button监听
    public void onRadioButton(View v) {
        switch (v.getId()) {
            case R.id.register_sex_f:
                sex=false;
                Toast.makeText(this, "register_sex_f ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.register_sex_m:
                sex = false;
                Toast.makeText(this, "register_sex_m ", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
