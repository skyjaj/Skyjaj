package com.skyjaj.hors.loadtest;


import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.skyjaj.hors.R;
import com.skyjaj.hors.activities.BaseActivity;
import com.skyjaj.hors.utils.DialogStylel;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

//请注意：测试短信条数限制发送数量：20条/天，APP开发完成后请到mob.com后台提交审核，获得不限制条数的免费短信权限。
public class MainActivity extends BaseActivity {

	private static String APPKEY = "116fb12b1951f";
	private static String APPSECRET = "2f86e61f4c737a55d4387c2f6ccae290";


	private boolean ready;

	private static Thread th;
	private EditText phone,code,psw1,psw2;
	private LinearLayout codeView;
	private static Button getCodeBtn;
	private Toolbar mToolbar;
	private static boolean canSend;
	private ForgetPSWTask mTask;
	private Dialog mDialog;
	private static Context ctx;

	private static final int UPDATECODE = 0, UPDATEUI = 1;

	private static  final Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			Log.i("handle :", "event :" + event + " result :" + result + " data :" + data);
			if (result == SMSSDK.RESULT_COMPLETE) {
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
					//提交验证码成功
				}else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
					//获取验证码成功
					boolean smart = (Boolean) data;
					Log.i("skyjaj", "data :" + data);
					if (smart) {
						Log.i("skyjaj", "data :" + "通过智能验证");
						//通过智能验证
						Toast.makeText(ctx, "通过智能验证", Toast.LENGTH_SHORT).show();
					} else {
						//依然走短信验证
						Log.i("skyjaj", "data :" + "短信验证已发送");
						Toast.makeText(ctx, "短信验证已发送", Toast.LENGTH_SHORT).show();
					}
				}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
					//返回支持发送验证码的国家列表
				}
			} else if (msg.what == UPDATECODE) {
				getCodeBtn.setText("获取验证码");
				getCodeBtn.setBackgroundResource(R.drawable.selector_button);
				canSend = true;
			} else if (msg.what == UPDATEUI) {
				getCodeBtn.setBackgroundResource(R.drawable.edittext_focus);
				getCodeBtn.setText(data + "");
			} else {
				((Throwable) data).printStackTrace();
			}
		}
	};


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);

		initView();
		initSDK();
		ctx = getApplication();
		if (th != null && th.isAlive()) {
			canSend = false;
			if (getCodeBtn != null) {
				getCodeBtn.setBackgroundResource(R.drawable.edittext_focus);
			}
		} else {
			canSend = true;
		}
		Log.i("skyjaj", " th :" + (th == null ? th + "" : th.isAlive()));

	}

	private void initView() {
		phone = (EditText) findViewById(R.id.forget_password_phone);
		code = (EditText) findViewById(R.id.forget_password_code);
		psw1 = (EditText) findViewById(R.id.forget_password_psw1);
		psw2 = (EditText) findViewById(R.id.forget_password_psw2);
		codeView = (LinearLayout) findViewById(R.id.forget_password_code_view);
		getCodeBtn = (Button) findViewById(R.id.forget_password_getCode);
		mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "重置密码");

		mDialog = DialogStylel.createLoadingDialog(this, "提交中..");
	}


	private void initSDK() {
		// 初始化短信SDK
		SMSSDK.initSDK(this, APPKEY, APPSECRET, true);
		EventHandler eventHandler = new EventHandler() {
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);
		ready = true;
		Log.i("handle :", "init ");

	}

//	private void loadSharePrefrence() {
//		SharedPreferences p = getSharedPreferences("SMSSDK_SAMPLE", Context.MODE_PRIVATE);
//		APPKEY = p.getString("APPKEY", APPKEY);
//		APPSECRET = p.getString("APPSECRET", APPSECRET);
//	}

//	private void setSharePrefrence() {
//		SharedPreferences p = getSharedPreferences("SMSSDK_SAMPLE", Context.MODE_PRIVATE);
//		Editor edit = p.edit();
//		edit.putString("APPKEY", APPKEY);
//		edit.putString("APPSECRET", APPSECRET);
//		edit.commit();
//	}

	protected void onDestroy() {
		if (ready) {
			// 销毁回调监听接口
			SMSSDK.unregisterAllEventHandler();
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}




	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.forget_password_getCode:
//			SMSSDK.getSupportedCountries();
			//提交短信验证码，在监听中返回
//			SMSSDK.submitVerificationCode(String country, String phone, String code)
			// 请求获取短信验证码，在监听中返回
			if (canSend && attempToGetCode()) {
				SMSSDK.getVerificationCode("86", phone.getText().toString());
				sendedMSM();
			}

			break;
		case R.id.forget_password_submit:
			attempToSubmit();
			break;
		}
	}

	private synchronized void sendedMSM() {
		canSend = false;
		getCodeBtn.setBackgroundResource(R.drawable.edittext_focus);
		th =new Thread(new Runnable() {
			@Override
			public void run() {
				int i=60;
				while (i>0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					i--;
					Message msg = new Message();
					msg.what = UPDATEUI;
					msg.obj = i+"";
					handler.sendMessage(msg);
				}
				Message msg = new Message();
				msg.what = UPDATECODE;
				handler.sendMessage(msg);
			}
		});

		th.start();

	}

	private boolean attempToGetCode() {

		String mobile = phone.getText().toString();

		EditText errorView = null;


		if (TextUtils.isEmpty(mobile) || mobile.length() != 11) {
			errorView = phone;
			errorView.setError("请输入11位手机号码");
		}

		if (errorView != null) {

			errorView.requestFocus();
			return false;
		}
		return true;
	}

	private void attempToSubmit() {

		String pswOne = psw1.getText().toString();
		String pswTwo = psw2.getText().toString();
		String codeValue = code.getText().toString();
		String mobile = phone.getText().toString();

		EditText errorView = null;

		if (TextUtils.isEmpty(pswOne) || TextUtils.isEmpty(pswTwo) || !pswOne.equals(pswTwo)) {
			errorView = psw2;
			errorView.setError("两次密码不一致");
		}

		if (TextUtils.isEmpty(codeValue)) {
			errorView = code;
			errorView.setError("验证码不能为空");
		}

		if (TextUtils.isEmpty(mobile) || mobile.length() != 11) {
			errorView = phone;
			errorView.setError("请输入11位手机号码");
		}


		if (errorView != null) {
			errorView.requestFocus();
		} else {
			//submit
			SMSBean smsBean = new SMSBean();
			smsBean.setPhone(mobile);
			smsBean.setPassword(pswOne);
			smsBean.setVerificationCode(codeValue);
			smsBean.setCountry("86");
			mTask = new ForgetPSWTask(smsBean);
			if (mDialog != null && !mDialog.isShowing()) {
				mDialog.show();
			}
			mTask.execute();
		}
	}


	private class ForgetPSWTask extends AsyncTask<Void, Void, Boolean> {

		private SMSBean smsBean;
		private String result;

		public ForgetPSWTask(SMSBean smsBean) {
			this.smsBean = smsBean;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				result = OkHttpManager.post(ServerAddress.PATIENT_FORGET_PASSWORD_URL, new Gson().toJson(smsBean));
			} catch (Exception e) {
				e.printStackTrace();
				result = "网络异常，稍后重试";
			}

			Log.i("skyjaj", "result :" + result);

			if (!TextUtils.isEmpty(result) && "success".equals(result)) {
				return true;
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);

			if (mDialog != null && mDialog.isShowing()) {
				mDialog.dismiss();
			}
			if (success) {
				findViewById(R.id.forget_password_tips_view).setVisibility(View.VISIBLE);
				findViewById(R.id.forget_password_all_view).setVisibility(View.GONE);
			} else {
				Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
			}

		}
	}


	public class SMSBean {


		private String phone;
		private String country;
		private String password;
		private String verificationCode;

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getVerificationCode() {
			return verificationCode;
		}

		public void setVerificationCode(String verificationCode) {
			this.verificationCode = verificationCode;
		}

	}


}