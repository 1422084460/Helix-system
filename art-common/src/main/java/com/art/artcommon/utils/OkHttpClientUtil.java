package com.art.artcommon.utils;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpClientUtil {

    //连接超时时间
    private static final long connectTimeout = 30;
    //请求超时时间
    private static final long readTimeout = 60;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout,TimeUnit.SECONDS)
            .build();

    public static String postString(String url,String json) throws IOException {
        RequestBody body = RequestBody.create(JSON,json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()){
            return response.body().string();
        }
    }
}
