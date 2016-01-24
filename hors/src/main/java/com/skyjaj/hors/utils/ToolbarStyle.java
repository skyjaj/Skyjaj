package com.skyjaj.hors.utils;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.skyjaj.hors.R;

/**
 * Created by Administrator on 2016/1/21.
 */
public class ToolbarStyle {

    /**
     * 初始化Toolbar
     * @param activity
     * @param toolbarId 自定义toolbar的id
     * @param titleId   标题
     * @return
     */
    public static Toolbar initToolbar(final Activity activity,int toolbarId,int titleId) {

        Toolbar mToolbar = (Toolbar) activity.findViewById(toolbarId);
        mToolbar.setBackgroundColor(Color.DKGRAY);
        mToolbar.setTitle(activity.getString(titleId));
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
            return mToolbar;
    }
}
