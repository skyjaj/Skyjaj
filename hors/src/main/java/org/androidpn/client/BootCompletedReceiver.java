package org.androidpn.client;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.skyjaj.hors.R;


public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		Toast.makeText(context, "Boot completed", Toast.LENGTH_LONG).show();
		SharedPreferences pref = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		if(pref.getBoolean(Constants.SETTINGS_AUTO_START, true)){
			ServiceManager serviceManager = new ServiceManager(context);
			serviceManager.setNotificationIcon(R.drawable.men_scan_icon);
			serviceManager.startService();
		}
	}

}
