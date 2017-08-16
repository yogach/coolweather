package com.admin.coolweather.model;

import org.litepal.crud.DataSupport;

/**
 * Created by admin on 2017/8/9.
 */

public class County extends DataSupport
{
    private int id;
    private String countyName;
    private String weatherId;
    private int cityid;

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return  this.id;
    }

    public void setCountyName(String countyName){
        this.countyName = countyName;
    }

    public String getCountyName(){
        return this.countyName;
    }

    public void setWeatherId(String weatherId){
        this.weatherId = weatherId;
    }

    public String getWeatherId()
    {
        return this.weatherId;
    }

    public void setCityid(int cityid)
    {
        this.cityid = cityid;
    }

    public int getCityid()
    {
        return this.cityid;
    }
}
