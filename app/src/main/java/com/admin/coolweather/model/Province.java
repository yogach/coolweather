package com.admin.coolweather.model;

import org.litepal.crud.DataSupport;

/**
 * Created by admin on 2017/8/9.
 */

public class Province extends DataSupport
{
    private int id;
    private String provinceName;
    private int provinceCode;

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return  this.id;
    }

    public void setProvinceName(String proviceName){
        this.provinceName = proviceName;
    }

    public String getProvinceName(){
        return this.provinceName;
    }

    public void setProvinceCode(int proviceCode){
        this.provinceCode = proviceCode;
    }

    public int getProvinceCode(){
        return this.provinceCode;
    }
}
