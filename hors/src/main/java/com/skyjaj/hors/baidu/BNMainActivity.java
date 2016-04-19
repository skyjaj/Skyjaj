package  com.skyjaj.hors.baidu;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import  com.skyjaj.hors.R;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.baidu.navisdk.adapter.BaiduNaviManager.NaviInitListener;
import com.baidu.navisdk.adapter.BaiduNaviManager.RoutePlanListener;
import com.skyjaj.hors.activities.BaseActivity;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.ToolbarStyle;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class BNMainActivity extends BaseActivity {


	public static List<Activity> activityList = new LinkedList<Activity>();
	private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";

	private String locationX = "23.062120724432368",locationY="113.40877992129568";

	private static boolean isInitSuccess = false;

	private String mSDCardPath = null;

	public static final String ROUTE_PLAN_NODE = "routePlanNode";
	public static final String SHOW_CUSTOM_ITEM = "showCustomItem";
	public static final String RESET_END_NODE = "resetEndNode";
	public static final String VOID_MODE = "voidMode";

	private Toolbar mToolbar;

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private Dialog dialog;
	private boolean myLocation;
	private Double latitude, lontitude;//纬度、经度

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		activityList.add(this);


		setContentView(R.layout.activity_map_main);
		mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "导航");

		BNOuterLogUtil.setLogSwitcher(true);
//		initListener();

		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
		mLocationClient.registerLocationListener(myListener);    //注册监听函数
		initLocation();
		dialog = DialogStylel.createLoadingDialog(this, "正在初始化..");
		dialog.show();
		myLocation = false;
		mLocationClient.start();

		if (initDirs()) {
			initNavi();
		}


//		 BNOuterLogUtil.setLogSwitcher(true);
	}


	private void initLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
		);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
		int span=1000;
		option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);//可选，默认false,设置是否使用gps
		option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());// 单位：公里每小时
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\nheight : ");
				sb.append(location.getAltitude());// 单位：米
				sb.append("\ndirection : ");
				sb.append(location.getDirection());// 单位度
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\ndescribe : ");
				sb.append("gps定位成功");

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				//运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
				sb.append("\ndescribe : ");
				sb.append("网络定位成功");
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				sb.append("\ndescribe : ");
				sb.append("离线定位成功，离线定位结果也是有效的");
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				sb.append("\ndescribe : ");
				sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
			}
			sb.append("\nlocationdescribe : ");
			sb.append(location.getLocationDescribe());// 位置语义化信息
			List<Poi> list = location.getPoiList();// POI数据
			if (list != null) {
				sb.append("\npoilist size = : ");
				sb.append(list.size());
				for (Poi p : list) {
					sb.append("\npoi= : ");
					sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
				}
			}

			if (161 == location.getLocType()) {
				latitude = location.getLatitude();
				lontitude = location.getLongitude();
				myLocation = true;
			} else {
				Toast.makeText(BNMainActivity.this, "定位失败请返回", Toast.LENGTH_SHORT).show();
			}
			mLocationClient.stop();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			Log.i("BaiduLocationApiDem", sb.toString());
		}
	}


	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.map_ok:
				Log.i("skyjaj", "navSoloadSuccess ?" + BaiduNaviManager.isNaviSoLoadSuccess());
				if (myLocation && BaiduNaviManager.isNaviInited() && isInitSuccess && BaiduNaviManager.isNaviSoLoadSuccess()) {
					try {

						routeplanToNavi(CoordinateType.BD09LL, locationY, locationX);
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(this, "初始化地图失败", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(this, "百度地图初始化失败，请返回", Toast.LENGTH_SHORT).show();
				}
				break;
		}
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
//	private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {
//
//		@Override
//		public void playEnd() {
////            showToastMsg("TTSPlayStateListener : TTS play end");
//		}
//
//		@Override
//		public void playStart() {
////            showToastMsg("TTSPlayStateListener : TTS play start");
//		}
//	};

//	public void showToastMsg(final String msg) {
//		BNMainActivity.this.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				Toast.makeText(BNMainActivity.this, msg, Toast.LENGTH_SHORT).show();
//			}
//		});
//	}

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
				isInitSuccess = true;
//				Toast.makeText(BNMainActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
				initSetting();
			}

			public void initStart() {
//				Toast.makeText(BNMainActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
			}

			public void initFailed() {
//				Toast.makeText(BNMainActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
				isInitSuccess = false;
				Toast.makeText(BNMainActivity.this, "百度导航引擎初始化失败,请返回", Toast.LENGTH_SHORT).show();
			}
		}, null, ttsHandler, null);

	}



	private void routeplanToNavi(CoordinateType coType,String end1,String end2) {
		BNRoutePlanNode sNode = null;
		BNRoutePlanNode eNode = null;
		switch (coType) {
			case BD09LL: {//百度经纬度算法
				sNode = new BNRoutePlanNode(lontitude,latitude, "开始地点名称", null, coType);
				eNode = new BNRoutePlanNode(Double.valueOf(end1),Double.valueOf(end2), "终点地点名称", null, coType);
				break;
			}
		}
		if (sNode != null && eNode != null) {
			List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
			list.add(sNode);
			list.add(eNode);
			BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new DemoRoutePlanListener(sNode));
		} else {
			Toast.makeText(this, "导航初始化失败，请返回", Toast.LENGTH_SHORT).show();
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

//	private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {
//
//		@Override
//		public void stopTTS() {
//			// TODO Auto-generated method stub
//			Log.e("test_TTS", "stopTTS");
//		}
//
//		@Override
//		public void resumeTTS() {
//			// TODO Auto-generated method stub
//			Log.e("test_TTS", "resumeTTS");
//		}
//
//		@Override
//		public void releaseTTSPlayer() {
//			// TODO Auto-generated method stub
//			Log.e("test_TTS", "releaseTTSPlayer");
//		}
//
//		@Override
//		public int playTTSText(String speech, int bPreempt) {
//			// TODO Auto-generated method stub
//			Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);
//
//			return 1;
//		}
//
//		@Override
//		public void phoneHangUp() {
//			// TODO Auto-generated method stub
//			Log.e("test_TTS", "phoneHangUp");
//		}
//
//		@Override
//		public void phoneCalling() {
//			// TODO Auto-generated method stub
//			Log.e("test_TTS", "phoneCalling");
//		}
//
//		@Override
//		public void pauseTTS() {
//			// TODO Auto-generated method stub
//			Log.e("test_TTS", "pauseTTS");
//		}
//
//		@Override
//		public void initTTSPlayer() {
//			// TODO Auto-generated method stub
//			Log.e("test_TTS", "initTTSPlayer");
//		}
//
//		@Override
//		public int getTTSState() {
//			// TODO Auto-generated method stub
//			Log.e("test_TTS", "getTTSState");
//			return 1;
//		}
//	};

}
