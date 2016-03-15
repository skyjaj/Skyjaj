package org.androidpn.demoapp;

import java.util.ArrayList;
import java.util.List;

import org.androidpn.client.Constants;
import org.androidpn.client.NotificationDetailsActivity;
import org.androidpn.client.NotificationHistory;
import org.litepal.crud.DataSupport;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.skyjaj.hors.R;


public class NotificationHistoryActivity extends Activity {

	private ListView listView ;
	private NotificationHistoryAdpter mAdpter;
	private  List<NotificationHistory> mList = new ArrayList<NotificationHistory>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_history);
		
		
		Log.d("TAG", "findAll from the db");
		mList = DataSupport.findAll(NotificationHistory.class);
		Log.d("TAG", "findAll from the db is "+ mList);
		listView = (ListView)findViewById(R.id.list_view);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				NotificationHistory history = mList.get(index);
		           Intent intent = new Intent(NotificationHistoryActivity.this,
		                    NotificationDetailsActivity.class);
		            intent.putExtra(Constants.NOTIFICATION_API_KEY, history.getApiKey());
		            intent.putExtra(Constants.NOTIFICATION_TITLE, history.getTitle());
		            intent.putExtra(Constants.NOTIFICATION_MESSAGE, history.getMessage());
		            intent.putExtra(Constants.NOTIFICATION_URI, history.getUri());
		            intent.putExtra(Constants.NOTIFICATION_IMAGME_URL, history.getImageUrl());				
		            startActivity(intent);
			}
			
		});
		mAdpter = new NotificationHistoryAdpter(this,0, mList);
		listView.setAdapter(mAdpter);
		//注册上下文菜单
		registerForContextMenu(listView);
		
		
	}

	//复写上下文菜单
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, 0, 0, "Romove");
	}
	
	
	//
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if(item.getItemId() == 0){
			AdapterContextMenuInfo contextMenuInfo = (AdapterContextMenuInfo)
					item.getMenuInfo();
			int index = contextMenuInfo.position;
			NotificationHistory history = mList.get(index);
			history.delete();
			mList.remove(index);
			//及时清楚界面
			mAdpter.notifyDataSetChanged();
		}
		return super.onContextItemSelected(item);
	}
	
	
	
	class NotificationHistoryAdpter extends ArrayAdapter<NotificationHistory>{

		public NotificationHistoryAdpter(Context context, int resource,
				List<NotificationHistory> objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NotificationHistory history = getItem(position);
			View view ;
			if(convertView == null){
				view = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, null);
			}else{
				view = convertView;
			}
			
			
			
			if(history != null){
				Log.d("TAG", "history title: "+ history.getTitle());
				Log.d("TAG", "history title: "+ history.getTime());

				TextView textViewTitle = (TextView)view.findViewById(R.id.tv_title);
				TextView textViewTime = (TextView)view.findViewById(R.id.tv_time);
				
				if(textViewTime != null && textViewTitle != null){
					
					textViewTitle.setText(history.getTitle());
					textViewTime.setText(history.getTime());
				}else{
					Log.d("TAG", "textView is null");
				}
			}

			return view;
		}
		
	}
	
}
