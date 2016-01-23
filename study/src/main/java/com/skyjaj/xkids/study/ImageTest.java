package com.skyjaj.xkids.study;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2015/11/19.
 */
public class ImageTest extends Activity {


    private ImageView imageView;
    private ProgressBar mProgressbar;
    private static  String  url = "http://7xlgbe.com2.z0.glb.qiniucdn.com/taopengyou.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iamge);
        imageView = (ImageView) findViewById(R.id.image);
        mProgressbar = (ProgressBar) findViewById(R.id.progressbar);
        new myAsyncTask().execute(url);
    }




    class myAsyncTask extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mProgressbar.setVisibility(View.GONE);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = null;
            InputStream is;
            URLConnection connection;
            try {
                Thread.sleep(3*1000);
                connection = new URL(url).openConnection();
                is = connection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bitmap = BitmapFactory.decodeStream(bis);
                is.close();
                bis.close();

            } catch (Exception e) {
                Log.d("xys","exception");
                e.printStackTrace();
            }

            return bitmap;
        }
    }

}
