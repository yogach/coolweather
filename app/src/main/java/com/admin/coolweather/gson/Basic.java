package com.admin.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 2017/8/16.
 */

public class Basic
{
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String WeatherId;

    public Update update;

    public class Update
    {
        @SerializedName("loc")
        public String updateTime;
    }
}
