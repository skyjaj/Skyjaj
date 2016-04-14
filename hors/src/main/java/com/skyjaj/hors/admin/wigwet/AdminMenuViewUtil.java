package com.skyjaj.hors.admin.wigwet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.LoginActivity;
import com.skyjaj.hors.activities.MyActivityManager;
import com.skyjaj.hors.activities.SettingActivity;
import com.skyjaj.hors.activities.UserInformactionAcitvity;
import com.skyjaj.hors.adapter.CommonAdapter;
import com.skyjaj.hors.admin.activities.DepartmentManagerActivity;
import com.skyjaj.hors.admin.activities.DoctorManagerActivity;
import com.skyjaj.hors.admin.activities.MessageManagerActivity;
import com.skyjaj.hors.admin.activities.ReservationManagerActivity;
import com.skyjaj.hors.admin.activities.UserManagerActivity;
import com.skyjaj.hors.bean.BaseMessage;
import com.skyjaj.hors.bean.IndexServiceMenu;
import com.skyjaj.hors.doctor.activities.DoctorWorkRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员菜单
 * Created by Administrator on 2016/3/13.
 */
public class AdminMenuViewUtil {

    public static AlertDialog ad;


    //预约管理视图
    public static View getReservationManagerView(final LayoutInflater inflater, ViewGroup container, final Context ctx,String mTitle) {
        List<IndexServiceMenu> mDatas = new ArrayList<IndexServiceMenu>();

        IndexServiceMenu menu = new IndexServiceMenu(R.drawable.me, "预约情况", 0);
        menu.setItemType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);

        menu = new IndexServiceMenu(0, "", 0);
        menu.setItemType(BaseMessage.Type.OUTCOMING);
        mDatas.add(menu);

        Map<BaseMessage.Type, Integer> itemViews = new HashMap<BaseMessage.Type, Integer>();
        itemViews.put(BaseMessage.Type.INCOMING, R.layout.index_service_item);
        itemViews.put(BaseMessage.Type.OUTCOMING, R.layout.index_service_item_bg);

        CommonAdapter<IndexServiceMenu> madapter  = new CommonAdapter<IndexServiceMenu>(ctx,mDatas,itemViews) {
            @Override
            public void convert(com.skyjaj.hors.adapter.ViewHolder viewHolder, IndexServiceMenu indexServiceMenu) {
                if(indexServiceMenu.getItemType() == BaseMessage.Type.INCOMING){
                    viewHolder.setImageResource(R.id.index_service_item_icon, indexServiceMenu.getResId())
                            .setText(R.id.index_service_item_text, indexServiceMenu.getText());
                }
            }
        };

        View view = inflater.inflate(R.layout.index_service, container, false);
        ListView lv = (ListView) view.findViewById(R.id.index_service_one);
        lv.setAdapter(madapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Activity activity = (Activity) ctx;
                switch (position) {
                    case 0:
                        Intent intent = new Intent(ctx, ReservationManagerActivity.class);
                        ctx.startActivity(intent);
                        break;
                }

            }
        });
        return view;
    }


    //消息管理视图
    public static View getMessageManagerView(final LayoutInflater inflater, ViewGroup container, final Context ctx,String mTitle) {
        List<IndexServiceMenu> mDatas = new ArrayList<IndexServiceMenu>();

        IndexServiceMenu menu = new IndexServiceMenu(R.drawable.menu_feedback_icon, "发送消息", 0);
        menu.setItemType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);

        menu = new IndexServiceMenu(R.drawable.menu_feedback_icon, "查看消息", 0);
        menu.setItemType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);

        menu = new IndexServiceMenu(0, "", 0);
        menu.setItemType(BaseMessage.Type.OUTCOMING);
        mDatas.add(menu);



        Map<BaseMessage.Type, Integer> itemViews = new HashMap<BaseMessage.Type, Integer>();
        itemViews.put(BaseMessage.Type.INCOMING, R.layout.index_service_item);
        itemViews.put(BaseMessage.Type.OUTCOMING, R.layout.index_service_item_bg);

        CommonAdapter<IndexServiceMenu> madapter  = new CommonAdapter<IndexServiceMenu>(ctx,mDatas,itemViews) {
            @Override
            public void convert(com.skyjaj.hors.adapter.ViewHolder viewHolder, IndexServiceMenu indexServiceMenu) {
                if(indexServiceMenu.getItemType() == BaseMessage.Type.INCOMING){
                    viewHolder.setImageResource(R.id.index_service_item_icon, indexServiceMenu.getResId())
                            .setText(R.id.index_service_item_text, indexServiceMenu.getText());
                }
            }
        };

        View view = inflater.inflate(R.layout.index_service, container, false);
        ListView lv = (ListView) view.findViewById(R.id.index_service_one);
        lv.setAdapter(madapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Activity activity = (Activity) ctx;
                switch (position) {
                    case 0:
                        Intent intent = new Intent(ctx, MessageManagerActivity.class);
                        ctx.startActivity(intent);
                        break;
                }

            }
        });
        return view;
    }

    //用户管理
    public static View getUserManagerView(final LayoutInflater inflater, ViewGroup container, final Context ctx,String mTitle) {
        List<IndexServiceMenu> mDatas = new ArrayList<IndexServiceMenu>();



        //0
        IndexServiceMenu menu = new IndexServiceMenu(R.drawable.tab_settings_normal,"患者管理", 0);
        menu.setItemType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);

        menu = new IndexServiceMenu(0, "", 0);
        menu.setItemType(BaseMessage.Type.OUTCOMING);
        mDatas.add(menu);


        Map<BaseMessage.Type, Integer> itemViews = new HashMap<BaseMessage.Type, Integer>();
        itemViews.put(BaseMessage.Type.INCOMING, R.layout.index_service_item);
        itemViews.put(BaseMessage.Type.OUTCOMING, R.layout.index_service_item_bg);

        CommonAdapter<IndexServiceMenu> madapter  = new CommonAdapter<IndexServiceMenu>(ctx,mDatas,itemViews) {
            @Override
            public void convert(com.skyjaj.hors.adapter.ViewHolder viewHolder, IndexServiceMenu indexServiceMenu) {
                if(indexServiceMenu.getItemType() == BaseMessage.Type.INCOMING){
                    viewHolder.setImageResource(R.id.index_service_item_icon, indexServiceMenu.getResId())
                            .setText(R.id.index_service_item_text, indexServiceMenu.getText());
                }
            }
        };

        View view = inflater.inflate(R.layout.index_service, container, false);
        ListView lv = (ListView) view.findViewById(R.id.index_service_one);
        lv.setAdapter(madapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Activity activity = (Activity) ctx;
                switch (position) {
                    case 0:
                        Intent intent = new Intent(ctx, UserManagerActivity.class);
                        ctx.startActivity(intent);
                        break;
                }

            }
        });
        return view;
    }




    //部门管理视图
    public static View getDepartmentManagerView(final LayoutInflater inflater, ViewGroup container, final Context ctx,String mTitle) {
        List<IndexServiceMenu> mDatas = new ArrayList<IndexServiceMenu>();

        IndexServiceMenu menu = new IndexServiceMenu(R.drawable.me, "科室管理", 0);
        menu.setItemType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);



        menu = new IndexServiceMenu(0, "", 0);
        menu.setItemType(BaseMessage.Type.OUTCOMING);
        mDatas.add(menu);



        Map<BaseMessage.Type, Integer> itemViews = new HashMap<BaseMessage.Type, Integer>();
        itemViews.put(BaseMessage.Type.INCOMING, R.layout.index_service_item);
        itemViews.put(BaseMessage.Type.OUTCOMING, R.layout.index_service_item_bg);

        CommonAdapter<IndexServiceMenu> madapter  = new CommonAdapter<IndexServiceMenu>(ctx,mDatas,itemViews) {
            @Override
            public void convert(com.skyjaj.hors.adapter.ViewHolder viewHolder, IndexServiceMenu indexServiceMenu) {
                if(indexServiceMenu.getItemType() == BaseMessage.Type.INCOMING){
                    viewHolder.setImageResource(R.id.index_service_item_icon, indexServiceMenu.getResId())
                            .setText(R.id.index_service_item_text, indexServiceMenu.getText());
                }
            }
        };

        View view = inflater.inflate(R.layout.index_service, container, false);
        ListView lv = (ListView) view.findViewById(R.id.index_service_one);
        lv.setAdapter(madapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Activity activity = (Activity) ctx;
                switch (position) {
                    case 0:
                        Intent intent = new Intent(ctx, DepartmentManagerActivity.class);
                        ctx.startActivity(intent);
                        break;
                }

            }
        });
        return view;
    }



    /**
     * “我”的管理
     * @param inflater
     * @param container
     * @param ctx
     * @param mTitle
     * @return
     */
    public static View getIndexMeView(final LayoutInflater inflater, ViewGroup container, final Context ctx,String mTitle) {
        List<IndexServiceMenu> mDatas = new ArrayList<IndexServiceMenu>();

        IndexServiceMenu menu = new IndexServiceMenu(R.drawable.me, ctx.getString(R.string.index_me_person), 0);
        menu.setItemType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);



        menu = new IndexServiceMenu(R.drawable.tab_settings_normal,ctx.getString(R.string.index_me_setting), 0);
        menu.setItemType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);

        menu = new IndexServiceMenu(0, "", 0);
        menu.setItemType(BaseMessage.Type.OUTCOMING);
        mDatas.add(menu);



        Map<BaseMessage.Type, Integer> itemViews = new HashMap<BaseMessage.Type, Integer>();
        itemViews.put(BaseMessage.Type.INCOMING, R.layout.index_service_item);
        itemViews.put(BaseMessage.Type.OUTCOMING, R.layout.index_service_item_bg);

        CommonAdapter<IndexServiceMenu> madapter  = new CommonAdapter<IndexServiceMenu>(ctx,mDatas,itemViews) {
            @Override
            public void convert(com.skyjaj.hors.adapter.ViewHolder viewHolder, IndexServiceMenu indexServiceMenu) {
                if(indexServiceMenu.getItemType() == BaseMessage.Type.INCOMING){
                    viewHolder.setImageResource(R.id.index_service_item_icon, indexServiceMenu.getResId())
                            .setText(R.id.index_service_item_text, indexServiceMenu.getText());
                }
            }
        };

        View view = inflater.inflate(R.layout.index_service, container, false);
        ListView lv = (ListView) view.findViewById(R.id.index_service_one);
        lv.setAdapter(madapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Activity activity = (Activity) ctx;
                switch (position) {
                    case 0:
                        Intent intent = new Intent(ctx, UserInformactionAcitvity.class);
                        ctx.startActivity(intent);
                        break;
                    case 1:
                        Intent settingIntent = new Intent(ctx, SettingActivity.class);
                        ctx.startActivity(settingIntent);
                        break;
                }

            }
        });
        return view;
    }
}
