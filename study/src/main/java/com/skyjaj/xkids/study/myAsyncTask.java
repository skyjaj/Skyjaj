package com.skyjaj.xkids.study;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Administrator on 2015/11/19.
 */
public class myAsyncTask extends AsyncTask<Void,Void,Void> {


    @Override
    protected Void doInBackground(Void... params) {
        Log.d("xys","doInbackground");
        publishProgress();
        return null;
    }

    @Override
    protected void onPreExecute() {
        Log.d("xys","onPreExecute");
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d("xys","onPostExecute");
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        Log.d("xys","onProgressUpdate");
        super.onProgressUpdate(values);
    }
}
