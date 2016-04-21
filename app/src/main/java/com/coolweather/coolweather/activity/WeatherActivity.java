package com.coolweather.coolweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.coolweather.R;
import com.coolweather.coolweather.util.HttpCallbackListener;
import com.coolweather.coolweather.util.HttpUtil;
import com.coolweather.coolweather.util.Utility;

/**
 * Created by ZongJie on 2016/4/21.
 */
public class WeatherActivity extends Activity {
    private LinearLayout weatherInfo;
    private TextView cityName;
    private TextView publishTime;
    private TextView currentDate;
    private TextView weatherDesp;
    private TextView temp1;
    private TextView temp2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        weatherInfo=(LinearLayout)findViewById(R.id.weather_info);
        cityName= (TextView) findViewById(R.id.city_name);
        publishTime= (TextView) findViewById(R.id.publish_time);
        currentDate= (TextView) findViewById(R.id.current_date);
        weatherDesp= (TextView) findViewById(R.id.weather_desp);
        temp1= (TextView) findViewById(R.id.temp1);
        temp2= (TextView) findViewById(R.id.temp2);
        String countryCode=getIntent().getStringExtra("country_code");
        if(!TextUtils.isEmpty(countryCode)){
            publishTime.setText("同步中...");
            weatherInfo.setVisibility(View.VISIBLE);
            cityName.setVisibility(View.VISIBLE);
            queryWeatherCode(countryCode);
        }else{
            showWeather();
        }
    }
    //查询县级代号对应的天气代号
    private void queryWeatherCode(String countryCode){
        String address="http://www.weather.com.cn/data/list3/city"+countryCode+".xml";
        queryFromServer(address, "countryCode");
    }
    //查询天气代号对应的天气
    private void queryWeatherInfo(String weatherCode){
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        queryFromServer(address,"weatherCode");
    }
    //根据传入的地址和类型去向服务器查询天气代号或天气信息
    private void queryFromServer(final String address,final String type){
        HttpUtil.sendHttpRequestWithHttpURLConnection(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if (("countryCode").equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        //从服务器返回的数据中解析出天气代号
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if (("weatherCode").equals(type)) {
                    //处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                              showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       publishTime.setText("同步失败");
                   }
               });
            }
        });
    }
    //从SharedPreferences文件中读取储存的天气信息，并加载到界面上。
    private void showWeather(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        cityName.setText(preferences.getString("city_name",""));
        temp1.setText(preferences.getString("temp1",""));
        temp2.setText(preferences.getString("temp2",""));
        weatherDesp.setText(preferences.getString("weather_desp",""));
        publishTime.setText("今天"+preferences.getString("publish_time","")+"发布");
        currentDate.setText(preferences.getString("current_date",""));
        weatherInfo.setVisibility(View.VISIBLE);
        cityName.setVisibility(View.VISIBLE);
    }
}
