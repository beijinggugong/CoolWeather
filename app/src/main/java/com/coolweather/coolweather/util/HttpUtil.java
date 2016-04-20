package com.coolweather.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ZongJie on 2016/4/20.
 */
public class HttpUtil {
    public static void sendHttpRequestWithHttpURLConnection(final String address,
                                                             final HttpCallbackListener listener){
        //开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try{
                    URL url=new URL(address);
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in=connection.getInputStream();
                    //读取获取到的输入流
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line;
                    while((line=bufferedReader.readLine())!=null){
                        response.append(line);//将制定数据作为参数添加到已有数据结尾处
                    }
                    if(listener!=null){
                        listener.onFinish(response.toString());
                    }
                }catch(Exception e){
                    listener.onError(e);
                }finally{
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

}
