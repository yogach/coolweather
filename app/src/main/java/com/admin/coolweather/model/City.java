package com.admin.coolweather.model;

import org.litepal.crud.DataSupport;

/**
 * Created by admin on 2017/9/20.
 */


public class City extends DataSupport
{
    private long id;
    private String cityName;
    private String weatherId;
    private String weather;


    public void setId(int id)
    {
        this.id = id;
    }

    public long getId()
    {
        return this.id;
    }

    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }

    public String getCityName()
    {
        return this.cityName;
    }

    public String getWeatherId()
    {
        return weatherId;
    }

    public void setWeatherId(String weatherId)
    {
        this.weatherId = weatherId;
    }

    public String getWeather()
    {
        return weather;
    }

    public void setWeather(String weather)
    {
        this.weather = weather;
    }


}
