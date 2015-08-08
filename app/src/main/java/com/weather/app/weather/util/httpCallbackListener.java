package com.weather.app.weather.util;

/**
 * Created by zhangqinning on 15/8/8.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
