package com.skyjaj.hors.admin.wigwet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.skyjaj.hors.R;
import com.skyjaj.hors.doctor.widget.MenuViewUtil;
import com.skyjaj.hors.index.widget.IndexView;

/**
 * Created by Administrator on 2016/1/10.
 */
public class IndexContentFragment extends Fragment{

    public static final String KEY_TITLE = "key_title";
    private String mTitle;

    public static IndexContentFragment newInstance(String title) {
        IndexContentFragment fragment = new IndexContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }




    public View fragmentView;

    public ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mTitle = (String) getArguments().get(KEY_TITLE);

        if (fragmentView != null) {
            return fragmentView;
        }

        try {

            if(mTitle.equals( getResources().getStringArray(
                    R.array.index_array_left_menu)[0])){
                this.fragmentView= AdminMenuViewUtil.getUserManagerView(inflater, container, getContext(), mTitle);
                mListView = (ListView) fragmentView.findViewById(R.id.index_service_one);
//				Log.i("skyjaj","ListView of "+mTitle +" "+mListView);
                return fragmentView;
            }else if (mTitle.equals( getResources().getStringArray(
                    R.array.index_array_left_menu)[1])) {
                this.fragmentView = AdminMenuViewUtil.getDepartmentManagerView(inflater, container, getContext(), mTitle);
                mListView = (ListView) fragmentView.findViewById(R.id.index_service_one);
                return fragmentView;
            }else if (mTitle.equals( getResources().getStringArray(
                    R.array.index_array_left_menu)[2])) {
                this.fragmentView = AdminMenuViewUtil.getReservationManagerView(inflater, container, getContext(), mTitle);
                mListView = (ListView) fragmentView.findViewById(R.id.index_service_one);
                return fragmentView;
            }else if (mTitle.equals( getResources().getStringArray(
                    R.array.index_array_left_menu)[3])) {
                this.fragmentView = AdminMenuViewUtil.getMessageManagerView(inflater, container, getContext(), mTitle);
                return fragmentView;
            } else if (mTitle.equals( getResources().getStringArray(
                    R.array.index_array_left_menu)[4])) {
                this.fragmentView = AdminMenuViewUtil.getIndexMeView(inflater, container, getContext(), mTitle);
                return fragmentView;
            }else {
                TextView tv = new TextView(getContext());
                tv.setGravity(Gravity.CENTER);
                tv.setText("无任何信息...");
                return tv;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView tv = new TextView(getContext());
        tv.setGravity(Gravity.CENTER);
        tv.setText("无任何信息...");
        return tv;


    }



    public ListView getmListView() {
        return mListView;
    }

    public void setmListView(ListView mListView) {
        this.mListView = mListView;
    }

    public View getFragmentView() {
        return fragmentView;
    }

    public void setFragmentView(View fragmentView) {
        this.fragmentView = fragmentView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		Log.i("skyjaj","fragment onCreate "+mTitle);

    }

    @Override
    public void onStop() {
        super.onStop();
//		Log.i("skyjaj", "fragment onStop.."+mTitle);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
//		Log.i("skyjaj", "onDestroyView " + mTitle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//		Log.i("skyjaj", "onDestroy " + mTitle);
    }
}
