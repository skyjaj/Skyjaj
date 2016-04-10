package com.skyjaj.hors.activities;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/12.
 */
public class MyActivityManager extends Application {

    private static MyActivityManager instance;

    private List<Activity> activities;

    public MyActivityManager() {
    }

    public static MyActivityManager getInstance() {
        synchronized (MyActivityManager.class) {
            if (instance == null) {
                return (instance = new MyActivityManager());
            }
            return instance;
        }
    }


    public synchronized void addActivity(Activity activity) {
        Log.i(this.getClass().getName(), "add " + activity.getClass().getName());
        if (activities == null) {
            activities = new ArrayList<Activity>();
        }
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
        Log.i("skyjaj", "activitiy size  :" + activities.size());
    }


    public void remove(Activity activity) {
        Log.i(this.getClass().getName(), "remove " + activity.getClass().getName());
        synchronized (MyActivityManager.class) {
            try {
                if (activity != null && activities != null && activities.size() != 0) {
                    if (activities.contains(activity)) {
                        activities.remove(activity);
                    }
                }
                Log.i("skyjaj", "after remove activitiy size  :" + activities.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void finishButThis(String activityName) {
        try {
            if (activities != null) {
                for (Activity activity : activities) {
                    if (activity != null && !activity.isFinishing() &&
                            !activity.getClass().getName().equals(activityName)) {
                        activity.finish();
                    } else {
                        Log.i("skyjaj", "activity :" + activity);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //System.exit(0);
        }
    }


    public void exit() {
        try {
            if (activities != null) {
                for (Activity activity : activities) {
                    if (activity != null && !activity.isFinishing()) {
                        activity.finish();
                    } else {
                        Log.i("skyjaj", "activity :" + activity);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //System.exit(0);
        }
    }



    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
