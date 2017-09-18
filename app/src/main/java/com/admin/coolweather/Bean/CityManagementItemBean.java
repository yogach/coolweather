package com.admin.coolweather.Bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.admin.coolweather.R;
import com.admin.coolweather.adapter.BindingAdapterItem;

/**
 * Created by admin on 2017/9/12.
 */

public class CityManagementItemBean extends BaseObservable implements BindingAdapterItem
{
    private String CityName;
    private String temperature;
    private String weatherInfo;

    @Override
    public int getViewType()
    {
        return R.layout.city_item;
    }

    public  CityManagementItemBean(String CityName, String temperature, String weatherInfo)
    {
        this.CityName = CityName;
        this.temperature = temperature;
        this.weatherInfo = weatherInfo;
    }

    @Bindable
    public String getCityName()
    {
        return CityName;
    }
    public void setCityName(String cityName)
    {
        CityName = cityName;
    }

    @Bindable
    public String getTemperature()
    {
        return temperature;
    }
    public void setTemperature(String temperature)
    {
        this.temperature = temperature;
    }


    @Bindable
    public String getWeatherInfo()
    {
        return weatherInfo;
    }
    public void setWeatherInfo(String weatherInfo)
    {
        this.weatherInfo = weatherInfo;
    }
}
