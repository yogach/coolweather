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
    @SerializedName("prov")
    public String provinceName;
    @SerializedName("cnty")
    public String countryName;

    public Update update;

    public class Update
    {
        @SerializedName("loc")
        public String updateTime;
    }
}
