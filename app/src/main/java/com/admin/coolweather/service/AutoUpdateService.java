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
import com.admin.coolweather.util.HttpUtil;
import com.admin.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service
{


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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) //定时时间到后会重新执行此函数 当前定时时间为8小时
    {
        updateWeather();
        updateBingPIc();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anhour = 8*60*60*1000; //8小时对应的毫秒数
        long  trigerAtTime = SystemClock.elapsedRealtime()  + anhour;
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,trigerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /*
    * 更新天气信息
    * */
    private   void updateWeather()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString != null)
        {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.WeatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=bc0418b57b2d4918819d3974ac1285d9";
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
                   String responText = response.body().string();
                    Weather mweather = Utility.handleWeatherResponse(responText);
                    if(mweather != null && "ok".equals(mweather.status))
                    {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather",responText);
                        editor.apply();

                    }

                }
            });

        }


    }

    /*
    * 更新每日一图
    * */
    private void  updateBingPIc()
    {
        String  requestBingPic = "http://guolin.tech/api/bing_pic";
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
                String bingPic =response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();

            }
        });


    }
}
