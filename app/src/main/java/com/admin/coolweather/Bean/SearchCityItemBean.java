package com.admin.coolweather.Bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.admin.coolweather.R;
import com.admin.coolweather.adapter.BindingAdapterItem;

/**
 * Created by admin on 2017/9/13.
 */

public class SearchCityItemBean extends BaseObservable implements BindingAdapterItem
{

    private String CityInfo;
//    private String weatherId;

    @Override
    public int getViewType()
    {
        return R.layout.search_city_item;
    }

    public SearchCityItemBean (String CityInfo)
    {
        this.CityInfo = CityInfo;
      //  this.weatherId = weatherId;
    }

    @Bindable
    public String getCityInfo()
    {
        return CityInfo;
    }

    public void setCityInfo(String cityInfo)
    {
        CityInfo = cityInfo;
    }
}
