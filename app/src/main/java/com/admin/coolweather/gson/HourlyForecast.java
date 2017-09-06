package com.admin.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 2017/9/5.
 */

public class HourlyForecast
{
    @SerializedName("cond")
    public  WeatherInfo weatherInfo;

    public  class  WeatherInfo
    {
        public String code;
        public String txt;

    }

    public String date;

    @SerializedName("tmp")
    public String  temperature;



}
