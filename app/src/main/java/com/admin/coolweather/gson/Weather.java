package com.admin.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.security.PublicKey;
import java.util.List;

/**
 * Created by admin on 2017/8/16.
 */

public class Weather
{
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public  Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

    @SerializedName("hourly_forecast")
    public List<HourlyForecast> hourlyForecastList;

}
