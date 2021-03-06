package com.skyjaj.hors.admin.wigwet;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyjaj.hors.R;


/**
 * Created by Administrator on 2016/1/10.
 */
public class IndexMenuAdapter extends ArrayAdapter<IndexMenuItem> {

    private LayoutInflater mInflater;

    private int mSelected;


    public IndexMenuAdapter(Context context, IndexMenuItem[] objects) {
        super(context, -1, objects);

        mInflater = LayoutInflater.from(context);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.i("skyjaj", "get view adapter");
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.doctor_index_item_menu, parent, false);
        }

        ImageView iv = (ImageView) convertView.findViewById(R.id.id_item_icon);
        TextView title = (TextView) convertView.findViewById(R.id.id_item_title);
        title.setText(getItem(position).text);
        iv.setImageResource(getItem(position).icon);
        convertView.setBackgroundColor(Color.TRANSPARENT);

        if (position == mSelected) {
            iv.setImageResource(getItem(position).iconSelected);
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.state_menu_item_selected));
        }

        return convertView;
    }

    public void setSelected(int position) {
        this.mSelected = position;
        notifyDataSetChanged();
    }



}
