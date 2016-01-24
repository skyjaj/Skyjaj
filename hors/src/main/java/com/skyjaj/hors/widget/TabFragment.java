package com.skyjaj.hors.widget;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.skyjaj.hors.R;
import com.skyjaj.hors.index.widget.IndexView;


public class TabFragment extends Fragment
{
	private String mTitle = "Default";

	public static final String TITLE = "title";

	public View fragmentView;

	public ListView mListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (getArguments() != null) {
			mTitle = getArguments().getString(TITLE);
		}


		if (fragmentView != null) {
			return fragmentView;
		}

//		Log.i("skyjaj", "on create view ???");

		try {

			if(mTitle.equals("index_service")){
				this.fragmentView=IndexView.getIndexServiceView(inflater, container, getContext(), mTitle);
				mListView = (ListView) fragmentView.findViewById(R.id.index_service_one);
//				Log.i("skyjaj","ListView of "+mTitle +" "+mListView);
				return fragmentView;
			}else if (mTitle.equals("index_found")) {
				this.fragmentView = IndexView.getIndexFoundView(inflater, container, getContext(), mTitle);
				mListView = (ListView) fragmentView.findViewById(R.id.index_service_one);
//				Log.i("skyjaj","ListView of "+mTitle +" "+mListView);
				return fragmentView;
			}else if (mTitle.equals("index_me")) {
				this.fragmentView = IndexView.getIndexMeView(inflater, container, getContext(), mTitle);
				mListView = (ListView) fragmentView.findViewById(R.id.index_service_one);
//				Log.i("skyjaj","ListView of "+mTitle +" "+mListView);
				return fragmentView;
			}else {
				TextView tv = new TextView(getContext());
				tv.setGravity(Gravity.CENTER);
				tv.setText("无任何信息...");
				return tv;
			}

		} catch (Exception e) {

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
