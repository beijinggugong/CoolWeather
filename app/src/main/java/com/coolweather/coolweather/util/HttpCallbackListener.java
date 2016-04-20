package com.coolweather.coolweather.util;

/**
 * Created by ZongJie on 2016/4/20.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
