package com.admin.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 2017/8/16.
 */

public class AQI
{
    public AQICity city;

    public class AQICity
    {
        public String aqi;

        public String co;

        public String no2;

        public String o3;

        public String pm10;

        @SerializedName("qlty")
        public String  airQuality;

        public String so2;

        public String pm25;
    }
}
