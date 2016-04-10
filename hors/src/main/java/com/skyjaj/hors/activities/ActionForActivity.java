package com.skyjaj.hors.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.skyjaj.hors.R;
import com.skyjaj.hors.loadtest.LoadMoreListView;
import com.skyjaj.hors.loadtest.MyAdapter;
import com.skyjaj.hors.loadtest.RefreshAndLoadMoreView;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.widget.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * 动作请求Activity结果回调
 */
public class ActionForActivity extends Activity {


    private Context mContext;
    private int pageIndex = 0;
    private MyAdapter adapter;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private List<String> datas = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        mContext = this;
        mLoadMoreListView = (LoadMoreListView) findViewById(R.id.load_more_list);
        mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) findViewById(R.id.refresh_and_load_more);
        adapter = new MyAdapter(mContext, datas, R.layout.item_action_layout);
        mLoadMoreListView.setAdapter(adapter);
        initData();
    }

    private void initData() {
        //程序开始就加载第一页数据
        loadData(1);
        mRefreshAndLoadMoreView.setLoadMoreListView(mLoadMoreListView);
        mLoadMoreListView.setRefreshAndLoadMoreView(mRefreshAndLoadMoreView);
        //设置下拉刷新监听
        mRefreshAndLoadMoreView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("skyjaj", "onrefresh");
                //loadData(1);
            }
        });
        //设置加载监听
        mLoadMoreListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadData(pageIndex + 1);
            }
        });
        mLoadMoreListView.setOnItemClickListener(new ItemClickListener());

    }


    /**
     * 加载数据
     */
    private void loadData(final int tempPageIndex) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (tempPageIndex == 1) {
                    datas.clear();
                }
                getDatas(tempPageIndex);
                //在这里我设置当加载到第三页时设置已经加载完成
                if (tempPageIndex == 3) {
                    mLoadMoreListView.setHaveMoreData(false);
                } else {
                    mLoadMoreListView.setHaveMoreData(true);
                }
                pageIndex = tempPageIndex;
                adapter.notifyDataSetChanged();
                //当加载完成之后设置此时不在刷新状态
                mRefreshAndLoadMoreView.setRefreshing(false);
                mLoadMoreListView.onLoadComplete();
            }
        }, 1000);
    }

    /**
     * 模拟一些数据源
     *
     * @return
     */
    private List<String> getDatas(final int tempPageIndex) {
        switch (tempPageIndex) {
            case 1:
                for (int i = 0; i < 10; i++) {
                    datas.add("这是第" + (i + 1) + "个Item");
                }
                break;
            case 2:
                for (int i = 0; i < 10; i++) {
                    datas.add("这是第" + (i + 11) + "个Item");
                }
                break;
            case 3:
                for (int i = 0; i < 10; i++) {
                    datas.add("这是第" + (i + 21) + "个Item");
                }
                break;
            default:
                break;
        }

        return datas;
    }

    /**
     * 为ListView每个Item添加点击事件
     */
    public class ItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), datas.get(position), Toast.LENGTH_LONG).show();
        }
    }

}
