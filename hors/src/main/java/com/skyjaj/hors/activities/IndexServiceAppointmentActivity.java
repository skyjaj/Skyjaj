package com.skyjaj.hors.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.skyjaj.hors.R;
import com.skyjaj.hors.adapter.CommonAdapter;
import com.skyjaj.hors.bean.BaseMessage;
import com.skyjaj.hors.bean.IndexServiceMenu;
import com.skyjaj.hors.utils.ToolbarStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/21.
 */
public class IndexServiceAppointmentActivity extends AppCompatActivity {

    private ListView mListView;
    private String test;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        int viewId = intent.getIntExtra("index_menu", 0);


        switch (viewId) {

            case R.string.index_service_appointment:
                setContentView(R.layout.activity_index_service_appointment);
                mToolbar = ToolbarStyle.initToolbar(this, R.id.index_service_appointment_toolbar, R.string.index_service_appointment);
                initAppointmentView();
                break;
            case R.string.index_service_queue_waiting:
                setContentView(R.layout.activity_index_service_queue);
                mToolbar = ToolbarStyle.initToolbar(this, R.id.index_service_queue_toolbar, R.string.index_service_queue_waiting);
                initQueueView();
                break;
        }


    }

    private void initQueueView() {
        mListView = (ListView) findViewById(R.id.index_service_queue_listview);
        List<IndexServiceMenu> mDatas = new ArrayList<IndexServiceMenu>();


        IndexServiceMenu menu = new IndexServiceMenu(R.drawable.menu_feedback_icon, this.getString(R.string.index_service_appointment), 0);
        menu.setType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);

        menu = new IndexServiceMenu(0, "", 0);
        menu.setType(BaseMessage.Type.OUTCOMING);
        mDatas.add(menu);

        menu = new IndexServiceMenu(R.drawable.men_scan_icon, this.getString(R.string.index_service_queue_waiting), 0);
        menu.setType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);


        Map<BaseMessage.Type, Integer> itemViews = new HashMap<BaseMessage.Type, Integer>();
        itemViews.put(BaseMessage.Type.INCOMING, R.layout.index_service_item);
        itemViews.put(BaseMessage.Type.OUTCOMING, R.layout.index_service_item_bg);

        CommonAdapter<IndexServiceMenu> mAdapter = new CommonAdapter<IndexServiceMenu>(this, mDatas, itemViews) {
            @Override
            public void convert(com.skyjaj.hors.adapter.ViewHolder viewHolder, IndexServiceMenu indexServiceMenu) {
                if (indexServiceMenu.getType() == BaseMessage.Type.INCOMING) {
                    viewHolder.setImageResource(R.id.index_service_item_icon, indexServiceMenu.getResId())
                            .setText(R.id.index_service_item_text, indexServiceMenu.getText());
                }
            }
        };

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(IndexServiceAppointmentActivity.this, "position :" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initAppointmentView() {

        mListView = (ListView) findViewById(R.id.index_service_appointment_listview);
        List<IndexServiceMenu> mDatas = new ArrayList<IndexServiceMenu>();
        IndexServiceMenu menu = new IndexServiceMenu(R.drawable.menu_feedback_icon, this.getString(R.string.index_service_appointment), 0);
        menu.setType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);

        menu = new IndexServiceMenu(0, "", 0);
        menu.setType(BaseMessage.Type.OUTCOMING);
        mDatas.add(menu);

        menu = new IndexServiceMenu(R.drawable.men_scan_icon, this.getString(R.string.index_service_queue_waiting), 0);
        menu.setType(BaseMessage.Type.INCOMING);
        mDatas.add(menu);


        Map<BaseMessage.Type, Integer> itemViews = new HashMap<BaseMessage.Type, Integer>();
        itemViews.put(BaseMessage.Type.INCOMING, R.layout.index_service_item);
        itemViews.put(BaseMessage.Type.OUTCOMING, R.layout.index_service_item_bg);

        CommonAdapter<IndexServiceMenu> mAdapter = new CommonAdapter<IndexServiceMenu>(this, mDatas, itemViews) {
            @Override
            public void convert(com.skyjaj.hors.adapter.ViewHolder viewHolder, IndexServiceMenu indexServiceMenu) {
                if (indexServiceMenu.getType() == BaseMessage.Type.INCOMING) {
                    viewHolder.setImageResource(R.id.index_service_item_icon, indexServiceMenu.getResId())
                            .setText(R.id.index_service_item_text, indexServiceMenu.getText());
                }
            }
        };

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(IndexServiceAppointmentActivity.this, "position :" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

