package com.skyjaj.xkids.skyjaj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.skyjaj.hors.R;

import org.androidpn.client.ServiceManager;
import org.androidpn.demoapp.NotificationHistoryActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("DemoAppActivity", "onCreate()...");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        // Settings
        Button okButton = (Button) findViewById(R.id.btn_settings);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ServiceManager.viewNotificationSettings(MainActivity.this);
            }
        });


        Button historiesbButton = (Button)findViewById(R.id.btn_histories);
        historiesbButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, NotificationHistoryActivity.class);
                startActivity(intent);
            }
        });


        // Start the service
        ServiceManager serviceManager = new ServiceManager(this);
//        serviceManager.setNotificationIcon(R.drawable.notification);
        serviceManager.startService();
        String alias ="skyjaj";
        serviceManager.setAlias(alias);
        List<String> list = new ArrayList<>();
        list.add("sport");
        list.add("music");
        serviceManager.setTags(list);
    }
}
