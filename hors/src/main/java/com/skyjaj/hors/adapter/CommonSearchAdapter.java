package com.skyjaj.hors.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

import com.skyjaj.hors.bean.BaseMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/15.
 */
public abstract class CommonSearchAdapter <T extends BaseMessage> extends BaseAdapter implements SectionIndexer{


    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    protected Map<BaseMessage.Type,Integer> itemViews;//定义一个ListView的item,如R.layout.item_listview

    public CommonSearchAdapter(Context context,List<T> datas,Map itemViews) {
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
        if(t.getItemType()== BaseMessage.Type.INCOMING) {
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

    public void setmDatas(List<T> datas) {
        this.mDatas=datas;
    }

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
