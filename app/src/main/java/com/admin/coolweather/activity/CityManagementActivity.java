package com.admin.coolweather.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.admin.coolweather.Base.DividerItemDecoration;
import com.admin.coolweather.Base.recycleview.OnItemClickListener;
import com.admin.coolweather.Base.recycleview.OnItemLongClickListener;
import com.admin.coolweather.Bean.CityManagementItemBean;
import com.admin.coolweather.R;
import com.admin.coolweather.adapter.BindingAdapter;
import com.admin.coolweather.adapter.BindingAdapterItem;
import com.admin.coolweather.databinding.CityManagementBinding;
import com.admin.coolweather.gson.Weather;
import com.admin.coolweather.model.City;
import com.admin.coolweather.util.MessageEvent;
import com.admin.coolweather.util.Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/9/12.
 */

public class CityManagementActivity extends AppCompatActivity
{

    private CityManagementBinding binding;

    private BindingAdapter adapter;

    private ArrayList<BindingAdapterItem> items = new ArrayList<>();

    private ArrayList<String> weatherIdList = new ArrayList<>();

    private LinearLayoutManager manager;


//    public final static instance = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.city_management);

        initView();
        quayCities();
        EventBus.getDefault().register(this);//注册事件

        binding.backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        //添加城市
        binding.addCity.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(CityManagementActivity.this, SearchCityActivity.class);

                startActivity(intent);
            }
        });

    }


    @Override
    protected void onResume()
    {
        super.onResume();


    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //取消注册事件
        EventBus.getDefault().unregister(this);

    }

    @Subscribe
    public void onMoonEvent(MessageEvent event)
    {
        if (event.getMessage().equals("finish"))
        {
            finish();
        }
    }

    private void quayCities()
    {
        //市列表
        List<City> cityList;

        cityList = DataSupport.findAll(City.class);

        items.clear();

        if(cityList.size()>0)
        {
          for(City city:cityList)
          {
//              if (city.getWeather() != null)
//              {
              Weather weather = Utility.handleWeatherResponse(city.getWeather());
//                  items.add(new CityManagementItemBean(city.getCityName(), weather.now.temperature + "°", weather.now.more.info));
              items.add(new CityManagementItemBean(city.getCityName(), null, null));
              weatherIdList.add(city.getWeatherId());
//              }
          }
          refrshItem();
        }

    }


    private void refrshItem()
    {
        adapter.setItems(items);
        adapter.notifyDataSetChanged();

    }


    private void initView()
    {
        //初始化RecyclerView适配器
        adapter = new BindingAdapter();
        manager = new LinearLayoutManager(CityManagementActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);




        adapter.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(int position)
            {
//                Toast.makeText(CityManagementActivity.this, "onClick事件  您点击了第：" + position + "个Item" + "对应weatherid为" + weatherIdList.get(position), LENGTH_SHORT).show();

                //跳转到mainactivity执行
//                Intent intent = new Intent();
//                intent.putExtra("weatherId",weatherIdList.get(position));
//                startActivity(intent);
//                CityManagementActivity.this.finish();  //结束activity

            }
        });

        adapter.setmOnItemLongClickListener(new OnItemLongClickListener()
        {
            @Override
            public void onItemClick(View view,int position)
            {
//                Toast.makeText(CityManagementActivity.this, "onLongClick事件  您点击了第：" + position + "个Item" + "对应weatherid为" + weatherIdList.get(position), LENGTH_SHORT).show();
                showPopMenu(view,position);
            }
        });


        binding.recylerview.setAdapter(adapter);
        binding.recylerview.setLayoutManager(manager);
        binding.recylerview.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST)); //设置项目分隔线
//        binding.recylerview.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(CityManagementActivity.this));

    }

    public void showPopMenu(View view, final int position)
    {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.delect_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            public boolean onMenuItemClick(MenuItem item)
            {

//                myAdapter.removeItem(pos);
                Utility.delectCityInfo(position+1);

                quayCities();
                return false;
            }
        });
//        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener()
//        {
//            @Override
//            public void onDismiss(PopupMenu menu)
//            {
//                Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
//            }
//        });
        popupMenu.show();
    }
}



