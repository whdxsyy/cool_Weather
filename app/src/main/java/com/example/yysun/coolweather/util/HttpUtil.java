package com.example.yysun.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by yysun on 2017/12/8.
 */
public class HttpUtil {
    public void sendOkHttpRequest(String address,okhttp3.Callback callback)
    {
        OkHttpClient client=new OkHttpClient();
        Request req=new Request.Builder().url(address).build();
        client.newCall(req).enqueue(callback);
    }
}
