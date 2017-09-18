package com.admin.coolweather.Bean;

import android.databinding.BaseObservable;

import com.admin.coolweather.R;
import com.admin.coolweather.adapter.BindingAdapterItem;

/**
 * Created by admin on 2017/9/13.
 */

public class SearchCityErrorBean extends BaseObservable implements BindingAdapterItem
{

    @Override
    public int getViewType()
    {
        return R.layout.search_city_error_layout;
    }
}
