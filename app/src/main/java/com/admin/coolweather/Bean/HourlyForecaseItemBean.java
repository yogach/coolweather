package com.admin.coolweather.Bean;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.admin.coolweather.R;
import com.admin.coolweather.adapter.BindingAdapterItem;
import com.bumptech.glide.Glide;
import com.admin.coolweather.BR;

/**
 * Created by admin on 2017/9/4.
 */

public class HourlyForecaseItemBean extends BaseObservable implements BindingAdapterItem
{
    private String info;
    private String code;
    private String date;
    private String temperature;

    @Override
    public int getViewType()
    {
        return R.layout.recycler_item;
    }

    public HourlyForecaseItemBean(String info, String code, String date, String temperature)
    {
        this.info = info;
        this.code = code;
        this.date = date;
        this.temperature = temperature;
    }

    //和layout文件中的info绑定
    @Bindable
    public String getInfo()
    {
        return info;
    }
    public void setInfo(String info)
    {
        this.info = info;
        notifyPropertyChanged(BR.info);  //在此处刷新绑定的数据
    }

    @Bindable
    public String getDate()
    {
        return date.split(" ")[1];
    }
    public void setDate(String date)
    {
        this.date = date;
    }

    @Bindable
    public String getTemperature()
    {
        return temperature+"°";
    }
    public void setTemperature(String temperature)
    {
        this.temperature = temperature;
    }


    public String getImageUrl()
    {
        // The URL will usually come from a model (i.e Profile)
        return  "https://cdn.heweather.com/cond_icon/"+code+".png";
    }

    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView view, String imageUrl)
    {

        Glide.with(view.getContext()).load(imageUrl).into(view);

    }

}
