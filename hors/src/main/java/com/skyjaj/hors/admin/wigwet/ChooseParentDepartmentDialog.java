package com.skyjaj.hors.admin.wigwet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.skyjaj.hors.R;
import com.skyjaj.hors.bean.BaseMessage;
import com.skyjaj.hors.bean.Department;
import com.skyjaj.hors.utils.CommonAdapter;
import com.skyjaj.hors.utils.ViewHolder;

import java.util.List;

/**
 * 长按医生管理中的item弹出对话框类
 * Created by Administrator on 2016/3/13.
 */
public class ChooseParentDepartmentDialog{


    private Context mContext;

    private Dialog dialog;
    private ListView listView;
    private List<Department> mDatas;
    private CommonAdapter mAdapter;


    private OnItemClickListener mListener;


    public ChooseParentDepartmentDialog(OnItemClickListener listener, Context mContext,List<Department> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mListener = listener;
        init();
    }

    private void init() {


        LayoutInflater inflater = LayoutInflater.from(mContext);
        //获取布局
        View v = inflater.inflate(R.layout.dialog_parent_department_choose, null);
        //加载最外层view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_parent_department_choose_view);

        listView = (ListView) v.findViewById(R.id.dialog_parent_department_choose_listview);

        mAdapter = new CommonAdapter(mContext,mDatas,R.layout.index_service_item) {
            @Override
            public void convert(ViewHolder holder, int position) {
                ImageView imageView = holder.getView(R.id.index_service_item_icon);
                imageView.setVisibility(View.GONE);
                holder.setText(R.id.index_service_item_text, mDatas.get(position).getNameCn());
                TextView textView = holder.getView(R.id.index_service_item_text);
                Department department = mDatas.get(position);
                if (department.getItemType() == BaseMessage.Type.OUTCOMING) {
                    textView.setTextColor(Color.RED);
                } else {
                    textView.setTextColor(Color.GRAY);
                }
            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                mListener.onItemClick(mDatas.get(position));
            }
        });

        listView.setAdapter(mAdapter);

        dialog =  new android.support.v7.app.AlertDialog.Builder(mContext)
                .setView(layout)
                .show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mListener.onDismissListener(dialog);
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mListener.onCancelListener(dialog);
            }
        });


    }



    public interface OnItemClickListener {

        void onItemClick(Department department);

        void onDismissListener(DialogInterface dialog);

        void onCancelListener(DialogInterface dialog);
    }



}
