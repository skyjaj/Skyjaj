package com.skyjaj.hors.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by skyjaj on 2015/11/25.
 */
public class ViewHolder {

    private SparseArray<View> mViews;
    private int mPosition;
    private View convertView;


    public ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        convertView.setTag(this);
    }

    public static ViewHolder get(Context context,View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView==null) {
            return new ViewHolder(context, parent, layoutId, position);
        }else{
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mPosition=position;
            return holder;
        }
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            mViews.put(viewId, view);
        }

        return (T)view;
    }

    public View getConvertView() {
        return convertView;
    }


    /**
     * 为TextView赋值操作
     * @param viewId
     * @param text
     * @return
     */
    public  ViewHolder setText(int viewId,String text){
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 设置文本颜色
     * @param viewId
     * @param color
     * @return
     */
    public  ViewHolder setTextColor(int viewId,int color){
        TextView tv = getView(viewId);
        tv.setTextColor(color);
        return this;
    }


    /**
     * 加载bitmap图片资源
     * @param viewId
     * @param bitmap
     * @return
     */
    public ViewHolder setImageResource(int viewId,Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }


    /**
     * 为ImageResource设置图片
     * @param viewId
     * @param resId
     * @return
     */
    public ViewHolder setImageResource(int viewId,int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    /**
     * 设置view背景颜色
     * @param viewId
     * @param color
     * @return
     */

    public ViewHolder setViewBackground(int viewId,int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }


  /*  public ViewHolder setImageURI(int viewId,String url) {
        ImageView view = getView(viewId);
//        如果是网络加载ImageLoad.getInstance().load(view,uri);
//        view.setImageURI(url);
        return this;
    }
*/

}
