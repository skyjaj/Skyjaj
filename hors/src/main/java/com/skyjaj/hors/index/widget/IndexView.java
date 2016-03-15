package com.skyjaj.hors.index.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.IndexCommonActivity;
import com.skyjaj.hors.activities.IndexServiceAppointmentActivity;
import com.skyjaj.hors.activities.LoginActivity;
import com.skyjaj.hors.activities.MyActivityManager;
import com.skyjaj.hors.activities.ShowHistoryActivity;
import com.skyjaj.hors.activities.UserInformactionAcitvity;
import com.skyjaj.hors.adapter.CommonAdapter;
import com.skyjaj.hors.bean.BaseMessage;
import com.skyjaj.hors.bean.IndexServiceMenu;

import org.androidpn.client.NotificationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/16.
 */
public class IndexView {

    public static AlertDialog ad;
    public static View getIndexServiceView(LayoutInflater inflater, ViewGroup container, final Context ctx,String mTitle) {
        List<IndexServiceMenu> mDatas = new ArrayList<IndexServiceMenu>();


        IndexServiceMenu menu = new IndexServiceMenu(R.drawable.menu_feedback_icon,ctx.getString(R.string.index_service_appointment), 0);
        menu.setItemType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);

        menu = new IndexServiceMenu(R.drawable.men_scan_icon,ctx.getString(R.string.index_service_queue_waiting), 0);
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
//        TextView tv = new TextView(ctx);
//        tv.setTextSize(20);
//        tv.setBackgroundColor(Color.parseColor("#ffffffff"));
//        tv.setText(mTitle);
//        tv.setGravity(Gravity.CENTER);
//        lv.addHeaderView(tv);
//        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    //0为appointment，1为queue
                    case 0 :
                        Intent intent = new Intent(ctx, IndexServiceAppointmentActivity.class);
                        intent.putExtra("index_menu", R.string.index_service_appointment);
                        ctx.startActivity(intent);
                        break;
                    case 1 :
                        Intent queueIntent = new Intent(ctx, IndexServiceAppointmentActivity.class);
                        queueIntent.putExtra("index_menu", R.string.index_service_queue_waiting);
                        ctx.startActivity(queueIntent);
                        break;
                }
            }
        });

        return view;
    }


    public static View getIndexFoundView(LayoutInflater inflater, ViewGroup container, final Context ctx,String mTitle) {
        List<IndexServiceMenu> mDatas = new ArrayList<IndexServiceMenu>();


        IndexServiceMenu menu = new IndexServiceMenu(R.drawable.menu_add_icon, ctx.getString(R.string.index_found_map), 0);
        menu.setItemType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);

        menu = new IndexServiceMenu(R.drawable.tab_weixin_normal,ctx.getString(R.string.index_found_notification_appointment), 0);
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
//        lv.setDivider(null);
        lv.setAdapter(madapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ctx, "position : " + position, Toast.LENGTH_SHORT).show();
//				view.setBackgroundColor(Color.GRAY);
            }
        });
        return view;
    }



    public static View getIndexMeView(final LayoutInflater inflater, ViewGroup container, final Context ctx,String mTitle) {
        List<IndexServiceMenu> mDatas = new ArrayList<IndexServiceMenu>();

        IndexServiceMenu menu = new IndexServiceMenu(R.drawable.me, ctx.getString(R.string.index_me_person), 0);
        menu.setItemType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);


        menu = new IndexServiceMenu(R.drawable.tab_find_frd_normal,ctx.getString(R.string.index_me_history), 0);
        menu.setItemType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);


        menu = new IndexServiceMenu(R.drawable.tab_settings_normal,ctx.getString(R.string.index_me_setting), 0);
        menu.setItemType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);




//        menu = new IndexServiceMenu(0, "", 0);
//        menu.setType(BaseMessage.Type.OUTCOMING);
//        mDatas.add(menu);

        menu = new IndexServiceMenu(R.drawable.tab_settings_normal,ctx.getString(R.string.index_me_exit), 0);
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

//        lv.setDivider(null);
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
                        Intent intent1 = new Intent(ctx, ShowHistoryActivity.class);
                        ctx.startActivity(intent1);
                        break;
                    case 2:
                        break;
                    case 3:
                            LinearLayout exitDialogLayout = (LinearLayout) inflater.inflate(R.layout.dialog_exit, null);
                            TextView switchTv = (TextView) exitDialogLayout.findViewById(R.id.switchOtherAccount);
                            TextView exitTv = (TextView) exitDialogLayout.findViewById(R.id.close_application);
                            //切换帐号
                            switchTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences.Editor editor = ctx.getSharedPreferences("horsUserInfo", 1).edit();
                                    editor.putBoolean("AUTO_LOGIN_ISCHECK", false);
                                    editor.putString("HORS_USERNAME", null);
                                    editor.putString("HORS_PASSWORD", null);
                                    editor.commit();
                                    if (ad != null) {
                                        ad.dismiss();
                                        ad = null;
                                    }
                                    Intent intent = new Intent(ctx, LoginActivity.class);
                                    if (activity != null) {
//                                        Intent intent2 = NotificationService.getIntent(activity);
//                                        activity.stopService(intent2);
                                        activity.finish();
                                    }
//                                    MyActivityManager.getInstance().exit();
                                    ctx.startActivity(intent);
                                }
                            });
                            //直接退出
                            exitTv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //发送关闭程序
                                    if (ad != null) {
                                        ad.dismiss();
                                        ad = null;
                                    }
                                    MyActivityManager.getInstance().exit();
                                    System.exit(0);

                                }
                            });
                            ad = new AlertDialog.Builder(ctx)
                                    .setView(exitDialogLayout)
                                    .show();
                        break;
                }

            }
        });
        return view;
    }
}
