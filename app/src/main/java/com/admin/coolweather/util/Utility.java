package com.admin.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.admin.coolweather.gson.Basic;
import com.admin.coolweather.gson.SearchCity;
import com.admin.coolweather.gson.Weather;
//import com.admin.coolweather.model.City;
//import com.admin.coolweather.model.County;
//import com.admin.coolweather.model.Province;
import com.admin.coolweather.model.City;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/8/15.
 */

public class Utility
{
    /*
    * 新建城市数据
    * */
    public static boolean saveNewCityInfo(String cityName, String weatherId,String weather)
    {
        City city = new City();
        city.setCityName(cityName);
        city.setWeatherId(weatherId);
        city.setWeather(weather);


        return city.save();
    }

    /*
    * 更新城市数据
    * 根据weatherId来更新数据，weatherId是唯一的
    * */
    public static void updataCityInfo(String cityName, String weatherId,String weather)
    {
        City city = new City();
        city.setCityName(cityName);
        city.setWeatherId(weatherId);
        city.setWeather(weather);


        city.updateAll("weatherId = ?",weatherId);

    }

    /*
    * 删除城市信息
    * 根据weatherId来删除数据，weatherId是唯一的
    * */
    public static void delectCityInfo(String weatherId)
    {
//        DataSupport.delete(City.class,position);
        DataSupport.deleteAll(City.class,"weatherId = ?",weatherId);
    }

    /*
    * 查询城市是否存在
    * */
    public static boolean isCityexisted(String weatherId)
    {
        List<City> cityList = DataSupport.where("WeatherId = ?",weatherId).find(City.class);

        if(cityList.size()==0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /*
    * 将返回的JSON数据解析成Weather实体类
    * */
    public static Weather handleWeatherResponse(String  response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String  weatherContent =jsonArray.getJSONObject(0).toString();
            return  new Gson().fromJson(weatherContent,Weather.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }


    /*
    * 解析返回的城市搜索结果
    * */
    public static ArrayList<SearchCity> handleBasicResponse(String response)
    {
        try
        {
            ArrayList<SearchCity> searchCity = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(response);
            JSONArray  jsonArray = jsonObject.getJSONArray("HeWeather5");


            for(int i= 0 ;i< jsonArray.length();i++)
            {
                String searchContent = jsonArray.getJSONObject(i).toString();

                searchCity.add(new Gson().fromJson(searchContent,SearchCity.class));
            }

            return  searchCity;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

}
