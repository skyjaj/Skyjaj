package com.skyjaj.hors.utils;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by Administrator on 2016/2/21.
 */
public class OkHttpManager {

    private static OkHttpClient client = new OkHttpClient();
    private static final MediaType jSon = MediaType.parse("application/json;charset=utf-8");



    public static String getToServer(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        }else {
            throw new IOException("unexpected code " + response);
        }
    }

    //post 方式发送parms,名称为“json"
    public static String post(String url, String json) throws Exception {
        RequestBody formBody = new FormEncodingBuilder()
                .add("json", json)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("unexpected code " + response);
        }
    }

    public static String post(String url) throws Exception {
        RequestBody formBody = new FormEncodingBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("unexpected code " + response);
        }
    }

    //post方式发送json数据
    public static String postJsonData(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(jSon, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }
}
