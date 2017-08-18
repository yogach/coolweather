package com.admin.coolweather.gson;

/**
 * Created by admin on 2017/8/16.
 */

public class AQI
{
    public AQICity city;

    public class AQICity
    {
        public String aqi;

        public String pm25;
    }
}
