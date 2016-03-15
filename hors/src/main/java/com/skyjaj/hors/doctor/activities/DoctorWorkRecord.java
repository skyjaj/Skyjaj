package com.skyjaj.hors.doctor.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.MyActivityManager;
import com.skyjaj.hors.adapter.CommonAdapter;
import com.skyjaj.hors.adapter.ViewHolder;
import com.skyjaj.hors.bean.BaseMessage;
import com.skyjaj.hors.bean.Reservation;
import com.skyjaj.hors.utils.DateUtil;
import com.skyjaj.hors.utils.ToolbarStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorWorkRecord extends AppCompatActivity {

    private Toolbar mToolbar;
    private ListView mListView;
    private Map<BaseMessage.Type,Integer> mViews;
    private List<Reservation> mDatas;
    private CommonAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_work_record);
        mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "我的工作记录");
        MyActivityManager.getInstance().addActivity(this);
        initView();
        initDatas();

    }

    private void initDatas() {

        mDatas = new ArrayList<Reservation>();
        mViews = new HashMap<BaseMessage.Type, Integer>();
        mViews.put(BaseMessage.Type.OUTCOMING, R.layout.index_service_item_text);
        mViews.put(BaseMessage.Type.INCOMING, R.layout.doctor_work_record_item);

        Reservation reservation = new Reservation();
        reservation.setName("小明");
        reservation.setAppointmentTime("2016-03-10 14:20");
        reservation.setCommentContent("哎哟，不错哦");
        reservation.setIsComment(2);
        mDatas.add(reservation);

        reservation = new Reservation();
        reservation.setName("小明");
        reservation.setAppointmentTime("2016-03-10 14:20");
        reservation.setCommentContent("哎哟，不错哦");
        reservation.setIsComment(1);
        mDatas.add(reservation);

        reservation = new Reservation();
        reservation.setAppointmentTime("2016年03月");
        reservation.setItemType(BaseMessage.Type.OUTCOMING);
        mDatas.add(reservation);

        reservation = new Reservation();
        reservation.setName("小明");
        reservation.setAppointmentTime("2016-03-10 14:20");
        reservation.setCommentContent("哎哟，不错哦");
        reservation.setIsComment(0);
        mDatas.add(reservation);


        mAdapter = new CommonAdapter<Reservation>(this,mDatas,mViews) {
            @Override
            public void convert(ViewHolder viewHolder, Reservation reservation) {
                if (reservation.getItemType() == BaseMessage.Type.INCOMING) {
                    viewHolder.setText(R.id.record_name, reservation.getName())
                            .setText(R.id.record_comment_case,reservation.getIsComment()>0?(reservation.getIsComment()==1?"差评":"好评"):"还没评价")
                            .setText(R.id.record_comment_details, reservation.getCommentContent())
                            .setText(R.id.record_time, reservation.getAppointmentTime());
                }else if (reservation.getItemType() == BaseMessage.Type.OUTCOMING) {
                    viewHolder.setText(R.id.index_service_item_tv, reservation.getAppointmentTime());
                }
            }
        };


        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(DoctorWorkRecord.this, "position :" + position, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void initView() {

        mListView = (ListView) findViewById(R.id.doctor_work_record_listview);

    }
}
