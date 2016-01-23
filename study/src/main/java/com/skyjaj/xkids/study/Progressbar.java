package com.skyjaj.xkids.study;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;

/**
 * Created by Administrator on 2015/11/19.
 */
public class Progressbar extends Activity{

    private ProgressBar mProgressbar;
    private  myAsyncTask mtask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progressbar);

        mProgressbar = (ProgressBar) findViewById(R.id.progressbar);
        mtask = new myAsyncTask();
        mtask.execute();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(mtask!=null &&
                mtask.getStatus()== AsyncTask.Status.RUNNING){
            mtask.cancel(true);

        }
    }

    class  myAynscTask extends AsyncTask<Void,Integer,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            for (int i=0; i<100; i++){
                if(isCancelled()){
                    break;
                }
                publishProgress(i);
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(isCancelled()){
                return;
            }

            mProgressbar.setProgress(values[0]);

        }
    }
}
