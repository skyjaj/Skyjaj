package  com.skyjaj.hors.baidu;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import  com.skyjaj.hors.R;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class BNMainActivity extends Activity {


	public static List<Activity> activityList = new LinkedList<Activity>();
	private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";

	Button btnmap = null;
	Button btntobus = null;
	Button btnzhuan = null;
	Button goplace1 = null;
	Button goplace2 = null;

	private String mSDCardPath = null;

	public static final String ROUTE_PLAN_NODE = "routePlanNode";
	public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
	public static final String RESET_END_NODE = "resetEndNode";
	public static final String VOID_MODE = "voidMode";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		activityList.add(this);

		setContentView(R.layout.activity_map_main);


		btnmap = (Button) findViewById(R.id.openmymap);
		btntobus = (Button) findViewById(R.id.tobus);
		btnzhuan= (Button) findViewById(R.id.tozhuan);
		goplace1= (Button) findViewById(R.id.goplace1);
		goplace2= (Button) findViewById(R.id.goplace2);
		BNOuterLogUtil.setLogSwitcher(true);
		initListener();

		if (initDirs()) {
			initNavi();
		}


//		 BNOuterLogUtil.setLogSwitcher(true);
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initListener() {
		goplace1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (BaiduNaviManager.isNaviInited()) {
					routeplanToNavi(CoordinateType.BD09LL,"113.331058","23.004836");
				}
			}
		});
		goplace2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (BaiduNaviManager.isNaviInited()) {
					routeplanToNavi(CoordinateType.BD09LL,"113.354389","23.142174");
				}
			}
		});
		btnmap.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				startActivity(new Intent(BNMainActivity.this,Mylocation.class));
			}
		});
		btntobus.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				startActivity(new Intent(BNMainActivity.this,BusLine.class));
			}
		});
		btnzhuan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(BNMainActivity.this, Zhuanhua.class));
			}
		});
	}

	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	private boolean initDirs() {
		mSDCardPath = getSdcardDir();
		if (mSDCardPath == null) {
			return false;
		}
		File f = new File(mSDCardPath, APP_FOLDER_NAME);
		if (!f.exists()) {
			try {
				f.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	String authinfo = null;

	/**
	 * 内部TTS播报状态回传handler
	 */
	private Handler ttsHandler = new Handler() {
		public void handleMessage(Message msg) {
			int type = msg.what;
			switch (type) {
				case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
//	                 showToastMsg("Handler : TTS play start");
					break;
				}
				case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
//	                 showToastMsg("Handler : TTS play end");
					break;
				}
				default :
					break;
			}
		}
	};

	/**
	 * 内部TTS播报状态回调接口
	 */
	private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

		@Override
		public void playEnd() {
//            showToastMsg("TTSPlayStateListener : TTS play end");
		}

		@Override
		public void playStart() {
//            showToastMsg("TTSPlayStateListener : TTS play start");
		}
	};

	public void showToastMsg(final String msg) {
		BNMainActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(BNMainActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void initNavi() {

//		BNOuterTTSPlayerCallback ttsCallback = null;

		BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME, new NaviInitListener() {
			@Override
			public void onAuthResult(int status, String msg) {
				if (0 == status) {
					authinfo = "key校验成功!";
				} else {
					authinfo = "key校验失败, " + msg;
				}
				BNMainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
//						Toast.makeText(BNMainActivity.this, authinfo, Toast.LENGTH_LONG).show();
					}
				});
			}

			public void initSuccess() {
//				Toast.makeText(BNMainActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
				initSetting();
			}

			public void initStart() {
//				Toast.makeText(BNMainActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
			}

			public void initFailed() {
//				Toast.makeText(BNMainActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
				Toast.makeText(BNMainActivity.this, "服务异常,请重试", Toast.LENGTH_SHORT).show();
			}
		}, null, ttsHandler, null);

	}



	private void routeplanToNavi(CoordinateType coType,String end1,String end2) {
		BNRoutePlanNode sNode = null;
		BNRoutePlanNode eNode = null;
		switch (coType) {
			case BD09LL: {//百度经纬度算法
				SharedPreferences sp = getSharedPreferences("location", MODE_PRIVATE);
				sNode = new BNRoutePlanNode(Double.valueOf(sp.getString("Longitude", "").toString()),Double.valueOf(sp.getString("Latitude", "").toString()), "开始地点名称", null, coType);
				eNode = new BNRoutePlanNode(Double.valueOf(end1),Double.valueOf(end2), "结束地点名称", null, coType);
				break;
			}
		}
		if (sNode != null && eNode != null) {
			List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
			list.add(sNode);
			list.add(eNode);
			BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
		}
	}

	public class DemoRoutePlanListener implements RoutePlanListener {

		private BNRoutePlanNode mBNRoutePlanNode = null;

		public DemoRoutePlanListener(BNRoutePlanNode node) {
			mBNRoutePlanNode = node;
		}

		@Override
		public void onJumpToNavigator() {
			/*
			 * 设置途径点以及resetEndNode会回调该接口
			 */

			for (Activity ac : activityList) {

				if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {

					return;
				}
			}
			Intent intent = new Intent(BNMainActivity.this, BNDemoGuideActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
			intent.putExtras(bundle);
			startActivity(intent);

		}

		@Override
		public void onRoutePlanFailed() {
			// TODO Auto-generated method stub
			Toast.makeText(BNMainActivity.this, "计算路程失败", Toast.LENGTH_SHORT).show();
		}
	}

	private void initSetting(){
		BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
		BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
		BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
		BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
		BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
	}

	private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

		@Override
		public void stopTTS() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "stopTTS");
		}

		@Override
		public void resumeTTS() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "resumeTTS");
		}

		@Override
		public void releaseTTSPlayer() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "releaseTTSPlayer");
		}

		@Override
		public int playTTSText(String speech, int bPreempt) {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);

			return 1;
		}

		@Override
		public void phoneHangUp() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "phoneHangUp");
		}

		@Override
		public void phoneCalling() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "phoneCalling");
		}

		@Override
		public void pauseTTS() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "pauseTTS");
		}

		@Override
		public void initTTSPlayer() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "initTTSPlayer");
		}

		@Override
		public int getTTSState() {
			// TODO Auto-generated method stub
			Log.e("test_TTS", "getTTSState");
			return 1;
		}
	};

}
