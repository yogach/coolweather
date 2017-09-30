package com.admin.coolweather.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.admin.coolweather.Base.DepthPageTransformer;
import com.admin.coolweather.Bean.HourlyForecaseItemBean;
import com.admin.coolweather.R;
import com.admin.coolweather.adapter.BindingAdapter;
import com.admin.coolweather.adapter.BindingAdapterItem;
import com.admin.coolweather.adapter.ViewPagerAdapter;
import com.admin.coolweather.databinding.ActivityWeatherBinding;
import com.admin.coolweather.databinding.ForecastItemBinding;
import com.admin.coolweather.databinding.WeatherinfoBinding;
import com.admin.coolweather.gson.Forecast;
import com.admin.coolweather.gson.HourlyForecast;
import com.admin.coolweather.gson.SearchCity;
import com.admin.coolweather.gson.Weather;
import com.admin.coolweather.model.City;
import com.admin.coolweather.util.HttpUtil;
import com.admin.coolweather.util.MessageEvent;
import com.admin.coolweather.util.Utility;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity
{
//    private String weatherId;

    private static final String TAG = "WeatherActivity";

    private ActivityWeatherBinding binding;

//    private WeatherinfoBinding weatherinfoBinding;

    private BindingAdapter adapter;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private List<View> viewList;// 将要分页显示的View装入数组中

    private List<City> cityList = new ArrayList<>();  //从数据库中读取出来的城市列表

    private Button mPreSelectedBt;
    private int mPreSelectedPos;
//    private ViewPager viewPager;


    private static final int LOCATION_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;


//    public void setWeatherId(String weatherId)
//    {
//        this.weatherId = weatherId;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_weather);

//        binding.title.local.setVisibility(View.INVISIBLE);

//        DataSupport.deleteAll(City.class);
        //实现透明状态栏
//        if(Build.VERSION.SDK_INT>=21)
//        {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_FULLSCREEN |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }

//        //初始化RecyclerView适配器
//        adapter = new BindingAdapter();
//        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
//        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        binding.recylerview.setAdapter(adapter);
//        binding.recylerview.setLayoutManager(manager);

//        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());//设置页面切换时候的监听器(可选，用了之后要重写它的回调方法处理页面切换时候的事务)


        initViewpager();
        initLocation();
        checkLocationPermission();
        loadSavebingpic();
        EventBus.getDefault().register(this);//注册事件
//        binding.pager.setCurrentItem(0);


        //选择城市按键监听器
        binding.title.selectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent intent = new Intent(MainActivity.this, CityManagementActivity.class);

                startActivity(intent);
            }
        });

        //设置按键监听器
        binding.title.settingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent intent = new Intent(MainActivity.this, SettingActivity.class);

                startActivity(intent);
                // binding.drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        binding.pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            /**
             * 滑动监听器OnPageChangeListener
             *  OnPageChangeListener这个接口需要实现三个方法：onPageScrollStateChanged，onPageScrolled ，onPageSelected
             *      1、onPageScrollStateChanged(int state) 此方法是在状态改变的时候调用。
             *          其中state这个参数有三种状态（0，1，2）
             *              state ==1的时表示正在滑动，state==2的时表示滑动完毕了，state==0的时表示什么都没做
             *              当页面开始滑动的时候，三种状态的变化顺序为1-->2-->0
             *      2、onPageScrolled(int position,float positionOffset,int positionOffsetPixels) 当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法回一直被调用。
             *          其中三个参数的含义分别为：
             *              position :当前页面，及你点击滑动的页面
             *              positionOffset:当前页面偏移的百分比
             *              positionOffsetPixels:当前页面偏移的像素位置
             *      3、onPageSelected(int position) 此方法是页面跳转完后被调用，arg0是你当前选中的页面的Position（位置编号）
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                if (mPreSelectedBt != null)
                {
                    mPreSelectedBt.setBackgroundResource(R.drawable.icon_dot_normal);
                }

                Button currentBt = (Button) binding.title.llContainer.getChildAt(position);
                currentBt.setBackgroundResource(R.drawable.home_page_dot_select);
                mPreSelectedBt = currentBt;

                binding.title.titleCtiy.setText(cityList.get(position).getCityName());

                mPreSelectedPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

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
                //存在bug weatherinfoBinding得到的是viewpager最后一页的布局 所以刷新改成刷新全局
//                requestWeather(cityList.get(mPreSelectedPos).getWeatherId());

                if (cityList != null & cityList.size() > 0)
                {
                    for (int i = 0; i < cityList.size(); i++)
                    {
//                        WeatherinfoBinding weatherinfoBinding = DataBindingUtil.inflate(getLayoutInflater().from(MainActivity.this), R.layout.weatherinfo, null, false);

                        requestWeather(cityList.get(i).getWeatherId());
                    }
                }
                else
                {
                    startLocation();
                }
            }
        });



    }

    private void initViewpager()
    {

        viewList = new ArrayList<View>();

        cityList.clear();
        binding.title.llContainer.removeAllViews();

        cityList = DataSupport.findAll(City.class);

        if (cityList != null & cityList.size() > 0)
        {
            for (int i = 0; i < cityList.size(); i++)
            {
                final WeatherinfoBinding weatherinfoBinding = DataBindingUtil.inflate(getLayoutInflater().from(this), R.layout.weatherinfo, null, false);

                Weather weather = Utility.handleWeatherResponse(cityList.get(i).getWeather());

                if (weather != null)
                {
                    showWeatherInfo(weatherinfoBinding, weather);
                }
                else
                {
                    requestWeather(cityList.get(i).getWeatherId());
                }

//                if(cityList.get(i).isLocation())
//                {
//                    showLocationIcon();
//                }

                viewList.add(weatherinfoBinding.getRoot());

                weatherinfoBinding.weatherScrollview.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        binding.swipeRefresh.setEnabled(weatherinfoBinding.weatherScrollview.getScrollY() == 0);

                        return false;
                    }
                });


                Button bt = new Button(this);
//            bt.setLayoutParams(new ViewGroup.LayoutParams(bitmap.getWidth(), bitmap.getHeight()));
                bt.setLayoutParams(new ViewGroup.LayoutParams(18, 18)); //设置长宽

                if (i == 0)
                {
                    bt.setBackgroundResource(R.drawable.home_page_dot_select);
                    mPreSelectedBt = bt;
                    binding.title.titleCtiy.setText(cityList.get(i).getCityName());
                }
                else
                {
                    bt.setBackgroundResource(R.drawable.icon_dot_normal);
                }

                binding.title.llContainer.addView(bt);

            }
        }
        else
        {
            binding.title.titleCtiy.setText(null);

        }

        binding.pager.setAdapter(new ViewPagerAdapter(viewList));
        binding.pager.setCurrentItem(0);
        binding.pager.setPageTransformer(true, new DepthPageTransformer()); //添加页面切换动画

    }


    private void loadSavebingpic()
    {

        //得到存储的信息的对象 并从里面得到存储下来的ing_pic
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String bingPic = prefs.getString("bing_pic", null);

        if (bingPic != null)
        {
            Glide.with(this).load(bingPic).into(binding.bingPicImg); //将图片显示到bingPicImg控件上
        }
        else
        {
            loadBingPic();
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
                        String district = aMapLocation.getDistrict();//城区信息
                        String province = aMapLocation.getProvince();//获取省信息
                        SearchDistrict(district, province);
                    }
                    else
                    {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
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

    @Override
    protected void onResume()
    {
        super.onResume();

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(MessageEvent event)
    {
        if (event.getMessage().equals("onItemClick"))
        {
//            Toast.makeText(this,"城市管理点击返回"+event.getExtraString(), LENGTH_SHORT).show();

            for(int i=0 ; i<cityList.size(); i++)
            {
                if(cityList.get(i).getWeatherId().equals(event.getExtraString()))
                {
//                    binding.pager.getAdapter().notifyDataSetChanged();
                    binding.pager.setCurrentItem(i,false);
//                    binding.pager.setCurrentItem(2);
                    binding.title.titleCtiy.setText(cityList.get(i).getCityName());

                    break;
                }
            }
        }
        else if(event.getMessage().equals("Refresh"))
        {
            initViewpager();
        }

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 开始定位
     */
    private void startLocation()
    {
        // 启动定位
        mLocationClient.startLocation();
        setswipeRefresh(true);
    }

    /**
     * 停止定位
     */
    private void stopLocation()
    {
        // 停止定位
        mLocationClient.stopLocation();
    }


    public void SearchDistrict(String city, final String province)
    {
        final String searchUrl = "https://api.heweather.com/v5/search?city=" + city + "&key=bc0418b57b2d4918819d3974ac1285d9";


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

                final String pro = province.replace("省", "");

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (searchCityList != null)
                        {
                            for (SearchCity searchCity : searchCityList)
                            {
                                if (searchCity.basic.provinceName.equals(pro)) //如果查询到相同的省名的话
                                {
                                    //记录查询到天气id
                                    String weatherId = searchCity.basic.WeatherId;
                                    String cityName = searchCity.basic.cityName;

                                    Utility.saveNewCityInfo(cityName, weatherId, null);

                                    initViewpager();
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    public void requestWeather(final String weatherId)
    {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";

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
                        Toast.makeText(MainActivity.this, "更新请求失败", LENGTH_SHORT).show();
                    }
                });
                setswipeRefresh(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (weather != null && "ok".equals(weather.status))
                        {
//                            SharedPreferences.Editor editor =PreferenceManager.
//                                    getDefaultSharedPreferences(MainActivity.this).edit();
//                            editor.putString("weather",responseText);
//                            editor.apply(); //提交数据
                            Utility.updataCityInfo(weather.basic.cityName, weather.basic.WeatherId, responseText);

                            initViewpager();
//                            showWeatherInfo(weatherinfoBinding, weather);

                            Toast.makeText(MainActivity.this, "获取天气信息成功", LENGTH_SHORT).show();

                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "获取天气信息失败", LENGTH_SHORT).show();
                        }

                        setswipeRefresh(false);
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
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("bing_pic", bingPic);
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

    private void loadWeatherIcon(final String code, final ForecastItemBinding itemBinding)
    {
        final String requestBingPic = "https://cdn.heweather.com/cond_icon/" + code + ".png";

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Glide.with(MainActivity.this).load(requestBingPic).into(itemBinding.weatherIcon);
            }
        });

    }


    private void showWeatherInfo(WeatherinfoBinding weatherinfoBinding,Weather weather)
    {
        String cirtName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1] + "更新"; //将字符串根据空格分成两段 取后面的时间
        String degree = weather.now.temperature + "°";
        String weatherInfo = weather.now.more.info;
//        binding.title.titleCtiy.setText(cirtName);
        weatherinfoBinding.now.titleUpdateTime.setText(updateTime);
        weatherinfoBinding.now.degreeText.setText(degree);
        weatherinfoBinding.now.weatherInfoText.setText(weatherInfo);
        weatherinfoBinding.forcast.forcastLayout.removeAllViews();
//
        for (Forecast forecast : weather.forecastList)
        {
            final ForecastItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.forecast_item, weatherinfoBinding.forcast.forcastLayout, false);


            itemBinding.dateText.setText(forecast.date);
            itemBinding.infoText.setText(forecast.more.txt_d);
            itemBinding.maxText.setText(forecast.temperature.max + "°");
            itemBinding.minText.setText(forecast.temperature.min + "°");
            loadWeatherIcon(forecast.more.code_d, itemBinding);


            weatherinfoBinding.forcast.forcastLayout.addView(itemBinding.getRoot());
        }


        ArrayList<BindingAdapterItem> items = new ArrayList<>();

        //初始化RecyclerView适配器
        adapter = new BindingAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        weatherinfoBinding.recylerview.setAdapter(adapter);
        weatherinfoBinding.recylerview.setLayoutManager(manager);


        for (HourlyForecast hourlyForecast : weather.hourlyForecastList)
        {
            items.add(new HourlyForecaseItemBean(hourlyForecast.weatherInfo.txt, hourlyForecast.weatherInfo.code, hourlyForecast.date, hourlyForecast.temperature));
        }

        adapter.setItems(items);
        adapter.notifyDataSetChanged();

        if (weather.aqi != null)
        {
            weatherinfoBinding.aqi.aqiText.setText(weather.aqi.city.aqi);
            weatherinfoBinding.aqi.pm25Text.setText(weather.aqi.city.pm25);
            weatherinfoBinding.aqi.coText.setText(weather.aqi.city.co);
            weatherinfoBinding.aqi.so2Text.setText(weather.aqi.city.so2);
            weatherinfoBinding.aqi.no2Text.setText(weather.aqi.city.no2);
            weatherinfoBinding.aqi.o3Text.setText(weather.aqi.city.o3);
            weatherinfoBinding.aqi.airQualityText.setText(weather.aqi.city.airQuality);
            weatherinfoBinding.aqi.pm10Text.setText(weather.aqi.city.pm10);
        }
        else
        {
            weatherinfoBinding.aqi.aqiText.setText("");
            weatherinfoBinding.aqi.pm25Text.setText("");
            weatherinfoBinding.aqi.coText.setText("");
            weatherinfoBinding.aqi.so2Text.setText("");
            weatherinfoBinding.aqi.no2Text.setText("");
            weatherinfoBinding.aqi.o3Text.setText("");
            weatherinfoBinding.aqi.airQualityText.setText("");
            weatherinfoBinding.aqi.pm10Text.setText("");
        }


        String comfort = "舒适度:" + weather.suggestion.comfort.brief + "\n    " + weather.suggestion.comfort.text;
        String carWash = "洗车指数:" + weather.suggestion.carwash.brief + "\n    " + weather.suggestion.carwash.text;
        String sport = "运动建议:" + weather.suggestion.sport.brief + "\n    " + weather.suggestion.sport.text;
        String dressSuggest = "穿衣指数:" + weather.suggestion.dressSuggest.brief + "\n    " + weather.suggestion.dressSuggest.text;
        String fluIndex = "感冒指数:" + weather.suggestion.fluIndex.brief + "\n    " + weather.suggestion.fluIndex.text;
        String travelIndex = "旅游指数:" + weather.suggestion.travelIndex.brief + "\n    " + weather.suggestion.travelIndex.text;
        String uv = "紫外线指数:" + weather.suggestion.uv.brief + "\n    " + weather.suggestion.uv.text;

        weatherinfoBinding.suggestion.comfortText.setText(comfort);
        weatherinfoBinding.suggestion.carWashText.setText(carWash);
        weatherinfoBinding.suggestion.sportText.setText(sport);
        weatherinfoBinding.suggestion.drsgText.setText(dressSuggest);
        weatherinfoBinding.suggestion.fluText.setText(fluIndex);
        weatherinfoBinding.suggestion.travText.setText(travelIndex);
        weatherinfoBinding.suggestion.uvText.setText(uv);

//        weatherinfoBinding.weatherLayout.setVisibility(View.VISIBLE);

    }


    public void setswipeRefresh(boolean state)
    {
        binding.swipeRefresh.setRefreshing(state);

    }

    public void showLocationIcon()
    {
        binding.title.local.setVisibility(View.VISIBLE);
    }
//
//    //不显示定位标志
//    public void notShowLocationIcon()
//    {
//        binding.title.local.setVisibility(View.INVISIBLE);
//    }

    // 当用户选择定位权限 允许或拒绝后，会回调onRequestPermissionsResult方法, 该方法类似于onActivityResult方法。
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startLocation();

    }


}
