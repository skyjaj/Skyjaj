package com.skyjaj.hors.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.skyjaj.hors.bean.BaseMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/25.
 */
public abstract class CommonAdapter<T extends BaseMessage> extends BaseAdapter {

    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    protected Map<BaseMessage.Type,Integer> itemViews;//定义一个ListView的item,如R.layout.item_listview

    public CommonAdapter(Context context,List<T> datas,Map itemViews) {
        this.mContext=context;
        this.mDatas=datas;
        this.itemViews =itemViews;
        mInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        T t = mDatas.get(position);
        if(t.getType()== BaseMessage.Type.INCOMING){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return itemViews.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int id = getItemViewType(position);
        int layoutId =0;
        if (id==0) {
            layoutId = itemViews.get(BaseMessage.Type.INCOMING);
        }else {
            layoutId = itemViews.get(BaseMessage.Type.OUTCOMING);
        }
        ViewHolder holder = ViewHolder.get(mContext,
                convertView, parent,layoutId, position);
        convert(holder, getItem(position));
        return holder.getConvertView();
    }


    public abstract void convert(ViewHolder viewHolder,T t);


    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
