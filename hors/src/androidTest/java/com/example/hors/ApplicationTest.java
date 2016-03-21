package com.example.hors;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.skyjaj.hors.utils.OkHttpManager;
import com.skyjaj.hors.utils.ServerAddress;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        Log.i("skyjaj", "testsss");

    }


}