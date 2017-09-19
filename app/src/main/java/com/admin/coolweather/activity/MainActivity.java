package com.admin.coolweather.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.admin.coolweather.Bean.HourlyForecaseItemBean;
import com.admin.coolweather.R;
import com.admin.coolweather.adapter.BindingAdapter;
import com.admin.coolweather.adapter.BindingAdapterItem;
import com.admin.coolweather.databinding.ActivityWeatherBinding;
import com.admin.coolweather.databinding.ForecastItemBinding;
import com.admin.coolweather.gson.Forecast;
import com.admin.coolweather.gson.HourlyForecast;
import com.admin.coolweather.gson.SearchCity;
import com.admin.coolweather.gson.Weather;
import com.admin.coolweather.util.HttpUtil;
import com.admin.coolweather.util.Utility;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity  extends AppCompatActivity
{
    private String  weatherId;

    private static final String  TAG = "WeatherActivity";

    private ActivityWeatherBinding binding;

    private BindingAdapter adapter;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private static final int LOCATION_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;


    public void setWeatherId(String weatherId)
    {
        this.weatherId = weatherId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_weather);

        //实现透明状态栏
//        if(Build.VERSION.SDK_INT>=21)
//        {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_FULLSCREEN |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }

        initLocation();
        checkLocationPermission();


        //初始化RecyclerView适配器
        adapter = new BindingAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);

        binding.recylerview.setAdapter(adapter);
        binding.recylerview.setLayoutManager(manager);

        //得到存储的信息的对象 并从里面得到存储下来的weather和bing_pic
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String weatherString = prefs.getString("weather",null);
        String bingPic = prefs.getString("bing_pic",null);

        if(bingPic !=null)
        {
            Glide.with(this).load(bingPic).into(binding.bingPicImg); //将图片显示到bingPicImg控件上
        }
        else
        {
            loadBingPic();
        }

        if(weatherString!=null)
        {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);

            weatherId = weather.basic.WeatherId;

            showWeatherInfo(weather);

        }
        else
        {
            //无缓存时去服务器查询天气
            weatherId =getIntent().getStringExtra("weather_id");
            binding.weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);

        }

        //选择城市按键监听器
        binding.title.selectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                binding.drawerLayout.openDrawer(GravityCompat.START);

                Intent intent = new Intent(MainActivity.this, SearchCityActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        //设置按键监听器
        binding.title.settingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent intent =new Intent(MainActivity.this , SettingActivity.class);
                MainActivity.this.startActivity(intent);
               // binding.drawerLayout.openDrawer(GravityCompat.END);
            }
        });


        //设置下拉刷新的控件颜色
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        //下拉刷新监听器
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                startLocation();
                requestWeather(weatherId);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1)
        {
            //closedrawerLayout(); //关闭显示的fragment
//            Log.d(TAG,"返回天气信息");
            setswipeRefresh(true);
            setWeatherId(data.getStringExtra("weatherId"));
            requestWeather(data.getStringExtra("weatherId"));
            notShowLocationIcon();

        }
    }


    //高德定位初始化
    private void initLocation()
    {
        //初始化定位
        mLocationClient = new AMapLocationClient(MainActivity.this);
        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener()
        {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation)
            {
                if (aMapLocation != null)
                {
                    if (aMapLocation.getErrorCode() == 0)
                    {
                        //可在其中解析amapLocation获取相应内容。
                        String  district = aMapLocation.getDistrict();//城区信息
                        String  province = aMapLocation.getProvince();//获取省信息
                        SearchDistrict(district,province);

                    }
                    else
                    {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError","location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        });
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

    }



    private void checkLocationPermission()
    {
        // 检查是否有定位权限
        // 检查权限的方法: ContextCompat.checkSelfPermission()两个参数分别是Context和权限名.
        // 返回PERMISSION_GRANTED是有权限，PERMISSION_DENIED没有权限
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            //没有权限，向系统申请该权限。
//            Log.i("MY","没有权限");
//            requestPermission(LOCATION_PERMISSION_CODE);

            //申请定位权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_CODE);//自定义的code
        }
        else
        {

        }
    }

    /**
     * 开始定位
     */
    private void startLocation()
    {
        // 启动定位
        mLocationClient.startLocation();
    }
    /**
     * 停止定位
     */
    private void stopLocation()
    {
        // 停止定位
        mLocationClient.stopLocation();
    }


    public void SearchDistrict(String city , final String province)
    {
        final String searchUrl = "https://api.heweather.com/v5/search?city="+city+"&key=bc0418b57b2d4918819d3974ac1285d9";


        HttpUtil.sendOkHttpRequest(searchUrl, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {

                final String responseText = response.body().string();
                final ArrayList<SearchCity> searchCityList = Utility.handleBasicResponse(responseText);

                String  mpro = province.replace("省","");

                if(searchCityList!=null )
                {
                    for(SearchCity searchCity:searchCityList)
                    {
                       if(searchCity.basic.provinceName.equals(mpro)) //如果查询到相同的省名的话
                       {
                           //记录查询到天气id
                           weatherId = searchCity.basic.WeatherId;
//                           binding.title.local.setVisibility(View.VISIBLE);
                           showLocationIcon();
                           break;
                       }
                    }
                }
            }
        });
    }

    public void requestWeather(final String weatherId)
    {
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId +"&key=bc0418b57b2d4918819d3974ac1285d9";

//        //开启自动更新
//        Intent intent = new Intent(this, AutoUpdateService.class);
//        startService(intent);

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(MainActivity.this,"更新请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
                binding.swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final  String responseText =response.body().string();
                final  Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(weather !=null && "ok".equals(weather.status))
                        {
                            SharedPreferences.Editor editor =PreferenceManager.
                                    getDefaultSharedPreferences(MainActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply(); //提交数据

                            showWeatherInfo(weather);

                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }

                        binding.swipeRefresh.setRefreshing(false);
                    }
                });

            }
        });
        loadBingPic();
    }

    /*
    * 加载必应每日一图
    * */
    private void loadBingPic()
    {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String bingPic =response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Glide.with(MainActivity.this).load(bingPic).into(binding.bingPicImg);
                    }
                });
            }
        });

    }

    private void  loadWeatherIcon(final String code,final ForecastItemBinding itemBinding)
    {
        final String requestBingPic = "https://cdn.heweather.com/cond_icon/"+code+".png";

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Glide.with(MainActivity.this).load(requestBingPic).into(itemBinding.weatherIcon);
            }
        });

    }


    private void showWeatherInfo(Weather weather)
    {
        String cirtName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1]+"更新"; //将字符串根据空格分成两段 取后面的时间
        String degree = weather.now.temperature +"°";
        String weatherInfo = weather.now.more.info;
        binding.title.titleCtiy.setText(cirtName);
        binding.now.titleUpdateTime.setText(updateTime);
        binding.now.degreeText.setText(degree);
        binding.now.weatherInfoText.setText(weatherInfo);
        binding.forcast.forcastLayout.removeAllViews();

        for (Forecast forecast:weather.forecastList)
        {
            final ForecastItemBinding itemBinding =  DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.forecast_item,binding.forcast.forcastLayout,false);


            itemBinding.dateText.setText(forecast.date);
            itemBinding.infoText.setText(forecast.more.txt_d);
            itemBinding.maxText.setText(forecast.temperature.max+"°");
            itemBinding.minText.setText(forecast.temperature.min+"°");
            loadWeatherIcon(forecast.more.code_d,itemBinding);


            binding.forcast.forcastLayout.addView(itemBinding.getRoot());
        }


        ArrayList<BindingAdapterItem> items = new ArrayList<>();

        for(HourlyForecast hourlyForecast:weather.hourlyForecastList)
        {
            items.add(new HourlyForecaseItemBean(hourlyForecast.weatherInfo.txt,hourlyForecast.weatherInfo.code,hourlyForecast.date,hourlyForecast.temperature));

        }

        adapter.setItems(items);
        adapter.notifyDataSetChanged();

        if(weather.aqi !=null)
        {
            binding.aqi.aqiText.setText(weather.aqi.city.aqi);
            binding.aqi.pm25Text.setText(weather.aqi.city.pm25);
            binding.aqi.coText.setText(weather.aqi.city.co);
            binding.aqi.so2Text.setText(weather.aqi.city.so2);
            binding.aqi.no2Text.setText(weather.aqi.city.no2);
            binding.aqi.o3Text.setText(weather.aqi.city.o3);
            binding.aqi.airQualityText.setText(weather.aqi.city.airQuality);
            binding.aqi.pm10Text.setText(weather.aqi.city.pm10);
        }
        else
        {
            binding.aqi.aqiText.setText("");
            binding.aqi.pm25Text.setText("");
            binding.aqi.coText.setText("");
            binding.aqi.so2Text.setText("");
            binding.aqi.no2Text.setText("");
            binding.aqi.o3Text.setText("");
            binding.aqi.airQualityText.setText("");
            binding.aqi.pm10Text.setText("");
        }


        String comfort = "舒适度:"+weather.suggestion.comfort.brief+"\n    "+weather.suggestion.comfort.text;
        String carWash = "洗车指数:"+weather.suggestion.carwash.brief+"\n    "+weather.suggestion.carwash.text;
        String sport = "运动建议:"+weather.suggestion.sport.brief+"\n    "+weather.suggestion.sport.text;
        String dressSuggest = "穿衣指数:" +weather.suggestion.dressSuggest.brief +"\n    "+weather.suggestion.dressSuggest.text;
        String fluIndex ="感冒指数:"+weather.suggestion.fluIndex.brief+"\n    "+weather.suggestion.fluIndex.text;
        String travelIndex = "旅游指数:"+weather.suggestion.travelIndex.brief+"\n    "+weather.suggestion.travelIndex.text;
        String uv = "紫外线指数:"+weather.suggestion.uv.brief+"\n    "+weather.suggestion.uv.text;

        binding.suggestion.comfortText.setText(comfort);
        binding.suggestion.carWashText.setText(carWash);
        binding.suggestion.sportText.setText(sport);
        binding.suggestion.drsgText.setText(dressSuggest);
        binding.suggestion.fluText.setText(fluIndex);
        binding.suggestion.travText.setText(travelIndex);
        binding.suggestion.uvText.setText(uv);

        binding.weatherLayout.setVisibility(View.VISIBLE);

    }

    //
    public void closedrawerLayout()
    {
        binding.drawerLayout.closeDrawers();
    }

    public void setswipeRefresh(boolean state)
    {
        binding.swipeRefresh.setRefreshing(state);

    }

    public void showLocationIcon()
    {
        binding.title.local.setVisibility(View.VISIBLE);
    }
    //不显示定位标志
    public void notShowLocationIcon()
    {
        binding.title.local.setVisibility(View.INVISIBLE);
    }

    // 当用户选择定位权限 允许或拒绝后，会回调onRequestPermissionsResult方法, 该方法类似于onActivityResult方法。
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startLocation();

    }


}
