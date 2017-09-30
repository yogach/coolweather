package com.admin.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;

import com.admin.coolweather.gson.Weather;
import com.admin.coolweather.model.City;
import com.admin.coolweather.util.HttpUtil;
import com.admin.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service
{
    private int time;
    private boolean isautoupdate = false;
    private List<City> cityList = new ArrayList<City>();

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
        time = Integer.parseInt(intent.getStringExtra("updateTime"));
        isautoupdate = true;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        isautoupdate = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (isautoupdate)
        {
            loadCityWeather();
//            updateWeather();
            updateBingPIc();
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

            int anhour = time * 60 * 60 * 1000; //小时对应的毫秒数
            long trigerAtTime = SystemClock.elapsedRealtime() + anhour;

            Intent i = new Intent(this, AutoUpdateService.class);

            PendingIntent pi = PendingIntent.getService(this, 0, i, 0); //当前动作是启动一个服务
            manager.cancel(pi);

            //该方法用于设置一次性闹钟，第一个参数表示闹钟类型，第二个参数表示闹钟执行时间，第三个参数表示闹钟响应动作。
            //AlarmManager.ELAPSED_REALTIME_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟也使用相对时间
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigerAtTime, pi);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void loadCityWeather()
    {
        cityList.clear();
        cityList = DataSupport.findAll(City.class);

        if (cityList != null & cityList.size() > 0)
        {
            for (int i = 0; i < cityList.size(); i++)
            {
                requestWeather(cityList.get(i).getWeatherId());
            }

        }
        else
        {

        }

    }

    /*
    * 更新天气信息
    * */
    private void requestWeather(String weatherId)
    {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String responseText = response.body().string();
                Weather weather = Utility.handleWeatherResponse(responseText);
                if (weather != null && "ok".equals(weather.status))
                {
//                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
//                        editor.putString("weather",responText);
//                        editor.apply();

                    Utility.updataCityInfo(weather.basic.cityName, weather.basic.WeatherId, responseText);

                }

            }
        });

    }

    /*
    * 更新每日一图
    * */
    private void updateBingPIc()
    {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();

            }
        });


    }
}
