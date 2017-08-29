package com.admin.coolweather.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ViewStubCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.coolweather.Fragment.SettingFragment;
import com.admin.coolweather.R;
import com.admin.coolweather.gson.Forecast;
import com.admin.coolweather.gson.Weather;
import com.admin.coolweather.service.AutoUpdateService;
import com.admin.coolweather.util.HttpUtil;
import com.admin.coolweather.util.Utility;
import com.bumptech.glide.Glide;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity
{
    public SwipeRefreshLayout swipeRefresh;

    public DrawerLayout drawerLayout;

    private Button navButton;

    private ScrollView weatherLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecaseLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView airQualityText;

    private TextView pm10Text;

    private TextView no2Text;

    private TextView o3Text;

    private TextView so2Text;

    private TextView coText;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private TextView uvText;

    private TextView dressSuggestText;

    private TextView fluIndexText;

    private TextView travelIndexText;

    private ImageView bingPicImg;

    private String  weatherId;

    private Button settingButton;




    public String getWeatherId()
    {
        return weatherId;
    }

    public void setWeatherId(String weatherId)
    {
        this.weatherId = weatherId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
//        if(Build.VERSION.SDK_INT>=21)
//        {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_FULLSCREEN |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }

        //初始化控件
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);//下拉刷新控件
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navButton = (Button)findViewById(R.id.nav_button);
        settingButton = (Button)findViewById(R.id.setting_button);

        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_ctiy);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText =(TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);

        forecaseLayout = (LinearLayout)findViewById(R.id.forcast_layout); //得到LinearLayout

        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        airQualityText =(TextView)findViewById(R.id.air_quality_text);
        pm10Text = (TextView)findViewById(R.id.pm10_text);
        no2Text =(TextView)findViewById(R.id.no2_text);
        o3Text  =(TextView)findViewById(R.id.o3_text);
        so2Text = (TextView)findViewById(R.id.so2_text);
        coText = (TextView)findViewById(R.id.co_text);



        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        uvText = (TextView)findViewById(R.id.uv_text);
        dressSuggestText = (TextView)findViewById(R.id.drsg_text);
        fluIndexText  = (TextView)findViewById(R.id.flu_text);
        travelIndexText = (TextView)findViewById(R.id.trav_text);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String weatherString = prefs.getString("weather",null);

        String bingPic = prefs.getString("bing_pic",null);

        if(bingPic !=null)
        {
            Glide.with(this).load(bingPic).into(bingPicImg); //将图片显示到bingPicImg控件上
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
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);

        }

        navButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                drawerLayout.openDrawer(GravityCompat.END);
            }
        });



        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                requestWeather(weatherId);
            }
        });

    }


    public void requestWeather(final String weatherId)
    {
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId +"&key=d95f0f53814741b99b42f17351766c1a";

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
                        Toast.makeText(WeatherActivity.this,"更新请求失败", Toast.LENGTH_SHORT).show();
                    }
                });
                swipeRefresh.setRefreshing(false);
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
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply(); //提交数据
                            showWeatherInfo(weather);

                        }
                        else
                        {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }

                        swipeRefresh.setRefreshing(false);
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
//                Toast.makeText(WeatherActivity.this,"网址错误", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String bingPic =response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });

    }

    private void showWeatherInfo(Weather weather)
    {
        String cirtName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1]+"更新"; //取后面的时间
        String degree = weather.now.temperature +"°";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cirtName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecaseLayout.removeAllViews();

        for (Forecast forecast:weather.forecastList)
        {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecaseLayout,false);
            TextView dateText =(TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText =(TextView)view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max+"°");
            minText.setText(forecast.temperature.min+"°");
            forecaseLayout.addView(view);
        }

        if(weather.aqi !=null)
        {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
            coText.setText(weather.aqi.city.co);
            so2Text.setText(weather.aqi.city.so2);
            no2Text.setText(weather.aqi.city.no2);
            o3Text.setText(weather.aqi.city.o3);
            airQualityText.setText(weather.aqi.city.airQuality);
            pm10Text.setText(weather.aqi.city.pm10);
        }

        String comfort = "舒适度:"+weather.suggestion.comfort.brief+"\n    "+weather.suggestion.comfort.text;
        String carWash = "洗车指数:"+weather.suggestion.carwash.brief+"\n    "+weather.suggestion.carwash.text;
        String sport = "运动建议:"+weather.suggestion.sport.brief+"\n    "+weather.suggestion.sport.text;
        String dressSuggest = "穿衣指数:" +weather.suggestion.dressSuggest.brief +"\n    "+weather.suggestion.dressSuggest.text;
        String fluIndex ="感冒指数:"+weather.suggestion.fluIndex.brief+"\n    "+weather.suggestion.fluIndex.text;
        String travelIndex = "旅游指数:"+weather.suggestion.travelIndex.brief+"\n    "+weather.suggestion.travelIndex.text;
        String uv = "紫外线指数:"+weather.suggestion.uv.brief+"\n    "+weather.suggestion.uv.text;

        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        dressSuggestText.setText(dressSuggest);
        fluIndexText.setText(fluIndex);
        travelIndexText.setText(travelIndex);
        uvText.setText(uv);

        weatherLayout.setVisibility(View.VISIBLE);

    }


}
