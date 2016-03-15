package com.skyjaj.hors.admin.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.R;
import com.skyjaj.hors.adapter.TimestampTypeAdapter;
import com.skyjaj.hors.bean.Department;
import com.skyjaj.hors.bean.Notification;
import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;
import com.skyjaj.hors.utils.ToolbarStyle;

import org.androidpn.client.NotificationIQ;

import java.sql.Timestamp;
import java.util.List;

public class MessageManagerActivity extends AppCompatActivity {

    private static Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).create();
    private Toolbar mToolbar;
    private EditText mUsername,mTitle,mContent;
    private boolean isSendByUser;
    private String username;
    private SendMessageTask mTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_manager);
        mToolbar = ToolbarStyle.initToolbar(this, R.id.mToolbar, "消息发送");
        initView();
    }

    private void initView() {
        mUsername = (EditText) findViewById(R.id.message_username);
        mTitle = (EditText) findViewById(R.id.message_title);
        mContent = (EditText) findViewById(R.id.message_content);
    }


    public void onClick(View view) {
        if (view != null) {
            switch (view.getId()) {
                case R.id.message_send_broadcast:
                    isSendByUser = false;
                    mUsername.setVisibility(View.GONE);
                    break;
                case R.id.message_send_to_user:
                    isSendByUser = true;
                    mUsername.setVisibility(View.VISIBLE);
                    break;
                case R.id.message_send_btn:
                    validateParams();
                    break;
            }
        }
    }

    //发送信息
    private void sendMessage() {
        username = mUsername.getText().toString();
        mTask = new SendMessageTask(mTitle.getText().toString(),mContent.getText().toString());
        mTask.execute();
    }

    private void validateParams() {

        String title = mTitle.getText().toString();
        String content = mContent.getText().toString();
        View errorViewFocus;
        if (TextUtils.isEmpty(title)) {
            mTitle.setError("请填写标题");
            errorViewFocus = mTitle;
            errorViewFocus.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(content)) {
            mContent.setError("请填写内容");
            errorViewFocus = mContent;
            errorViewFocus.requestFocus();
            return;
        }

        Log.i("skyjaj", "title :" + title + " content :" + content);
        if (isSendByUser) {
            String name = mUsername.getText().toString();
            Log.i("skyjaj", "username :" + username);
            if (TextUtils.isEmpty(name)) {
                mUsername.setError("用户名不能为空");
                errorViewFocus = mUsername;
                errorViewFocus.requestFocus();
                return ;
            }
        }
        sendMessage();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    //发送消息任务
    public class SendMessageTask extends AsyncTask<Void, Void, Boolean> {


        private String result;
        private String title;
        private String message;

        public SendMessageTask(String title, String message) {
            this.title = title;
            this.message = message;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (isSendByUser && !TextUtils.isEmpty(username)) {
                try {
                    Notification notification = new Notification();
                    notification.setTitle(title);
                    notification.setMessage(message);
                    result = OkHttpManager.post(ServerAddress.ADMIN_NOTIFICATION_TO_USER_URL, gson.toJson(notification));
                    Log.i("skyjaj", "result :" + result);
                    result = gson.fromJson(result, new TypeToken<String>() {
                    }.getType());
                } catch (Exception e) {
                    result = "网络异常，请稍后重试";
                    e.printStackTrace();
                    return false;
                }
            } else {
                try {
                    Notification notification = new Notification();
                    notification.setTitle(title);
                    notification.setMessage(message);
                    result = OkHttpManager.post(ServerAddress.ADMIN_NOTIFICATION_SEND_BROADCAST_URL,gson.toJson(notification));
                    Log.i("skyjaj", "result :" + result);
                    Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).create();
                    result = gson.fromJson(result, new TypeToken<String>() {
                    }.getType());
                } catch (Exception e) {
                    result = "网络异常，请稍后重试";
                    e.printStackTrace();
                }
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Toast.makeText(MessageManagerActivity.this, result, Toast.LENGTH_SHORT).show();
                mUsername.setText("");
                username = "";
                mTitle.setText("");
                mContent.setText("");
            } else {
                Toast.makeText(MessageManagerActivity.this, result, Toast.LENGTH_SHORT).show();
            }

            if (mTask != null) {
                mTask.cancel(true);
                mTask = null;
            }

        }
    }



}
