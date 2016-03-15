package com.skyjaj.hors.admin.wigwet;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.skyjaj.hors.R;

/**
 * Created by Administrator on 2016/1/10.
 */
public class IndexMenuFragment extends ListFragment {


    private static final int SIZE_MENU_ITEM = 5;

    private IndexMenuItem[] mItems = new IndexMenuItem[SIZE_MENU_ITEM];

    private IndexMenuAdapter mAdapter;

    private LayoutInflater mInflater;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInflater = LayoutInflater.from(getActivity());

        IndexMenuItem menuItem = null;
        for (int i = 0; i < SIZE_MENU_ITEM; i++) {
            switch (i) {
                case 0:
                    //预约管理
                    menuItem = new IndexMenuItem(getResources().getStringArray(R.array.index_array_left_menu)[i], false, R.drawable.menu_add_icon, R.drawable.menu_add_icon);
                    break;
                case 1:
                    //消息管理
                    menuItem = new IndexMenuItem(getResources().getStringArray(R.array.index_array_left_menu)[i], false, R.drawable.menu_add_icon, R.drawable.menu_add_icon);
                    break;
                case 2:
                    //用户管理
                    menuItem = new IndexMenuItem(getResources().getStringArray(R.array.index_array_left_menu)[i], false, R.drawable.menu_feedback_icon, R.drawable.menu_feedback_icon);
                    break;
                case 3:
                    //科室管理
                    menuItem = new IndexMenuItem(getResources().getStringArray(R.array.index_array_left_menu)[i], false, R.drawable.ic_menu_start_conversation, R.drawable.ic_menu_start_conversation);
                    break;
                case 4:
                    //我
                    menuItem = new IndexMenuItem(getResources().getStringArray(R.array.index_array_left_menu)[i], false, R.drawable.me, R.drawable.me);
                    break;

            }
            mItems[i] = menuItem;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setBackgroundColor(0xffffffff);
        setListAdapter(mAdapter = new IndexMenuAdapter(getActivity(), mItems));

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (mMenuItemSelectedListener != null) {
            mMenuItemSelectedListener.menuItemSelected(((IndexMenuItem) getListAdapter().getItem(position)).text);
        }

        mAdapter.setSelected(position);

    }

    //回调的接口
    public interface OnMenuItemSelectedListener {
        void menuItemSelected(String title);
    }

    private OnMenuItemSelectedListener mMenuItemSelectedListener;

    public void setOnMenuItemSelectedListener(OnMenuItemSelectedListener menuItemSelectedListener) {
        this.mMenuItemSelectedListener = menuItemSelectedListener;
    }


}
