package com.admin.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.admin.coolweather.Base.DividerItemDecoration;
import com.admin.coolweather.Base.recycleview.OnItemClickListener;
import com.admin.coolweather.Bean.SearchCityErrorBean;
import com.admin.coolweather.Bean.SearchCityItemBean;
import com.admin.coolweather.R;
import com.admin.coolweather.adapter.BindingAdapter;
import com.admin.coolweather.adapter.BindingAdapterItem;
import com.admin.coolweather.databinding.SearchCityBinding;
import com.admin.coolweather.gson.SearchCity;
import com.admin.coolweather.util.HttpUtil;
import com.admin.coolweather.util.MessageEvent;
import com.admin.coolweather.util.Utility;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by admin on 2017/9/13.
 */

public class SearchCityActivity extends AppCompatActivity
{
    private static final String TAG = "SearchCityActivity";

    private SearchCityBinding binding;

    private BindingAdapter adapter;

    private LinearLayoutManager manager;

    ArrayList<String> cityInfo = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.search_city);

        initView();

        binding.backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

//        binding.searchCityText.addTextChangedListener(watcher);

        binding.searchCityEnter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String str = binding.searchCityText.getText().toString();
                if (isNullEmptyBlank(str))
                {
                    binding.searchCityText.setError("输入内如不能为空");
                }
                else
                {
                    SearchCity(str);
                }
            }
        });
    }

    private void initView()
    {
        //初始化RecyclerView适配器
        adapter = new BindingAdapter();

        adapter.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(int position)
            {
//                Toast.makeText(SearchCityActivity.this, "onClick事件  您点击了第：" + position + "个Item" + "对应weatherid为" + weatherIdList.get(position), LENGTH_SHORT).show();

                //查询数据库 如果点击的城市不存在则存入数据库，反之不存
                if (!Utility.isCityexisted(cityInfo.get(position).split("-")[0]))
                {
                    Utility.saveNewCityInfo(cityInfo.get(position).split("-")[1], cityInfo.get(position).split("-")[0], null);
                }
//                else
//                {
//                    Toast.makeText(SearchCityActivity.this, "城市已存在 请选择其他城市", LENGTH_SHORT).show();
//
//                }
                EventBus.getDefault().post(new MessageEvent("finish"));
//
//                Intent intent=new Intent(SearchCityActivity.this,MainActivity.class);
//                intent.putExtra("weatherId",cityInfo.get(position).split("-")[0]);
//                startActivity(intent);
                SearchCityActivity.this.finish();  //结束activity

            }
        });

        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        binding.searchCityRlvList.setAdapter(adapter);
        binding.searchCityRlvList.setLayoutManager(manager);
        binding.searchCityRlvList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST)); //设置项目分隔线
//        binding.searchCityRlvList.addItemDecoration(new SpaceItemDecoration(10));
    }

    public void SearchCity(String city)
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

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ArrayList<BindingAdapterItem> items = new ArrayList<>();

                        cityInfo.clear();
                        for (SearchCity searchCity : searchCityList)
                        {
                            //如果输入的结果是对的 必定会返回一个或多个 status为ok数组项
                            //当返回结果是unknown city时则不存在其他为ok的数组项
                            if (searchCity.status.equals("ok"))
                            {
                                items.add(new SearchCityItemBean(searchCity.basic.cityName + "---" + searchCity.basic.provinceName + "---" + searchCity.basic.countryName));
                                cityInfo.add(searchCity.basic.WeatherId + "-" + searchCity.basic.cityName);
                            }
                            else if (searchCity.status.equals("unknown city"))
                            {
                                items.add(new SearchCityErrorBean());
                                break;
                            }
                        }

                        adapter.setItems(items);
                        adapter.notifyDataSetChanged();

                    }
                });
            }
        });
    }


    private static boolean isNullEmptyBlank(String str)
    {
        if (str == null || "".equals(str) || "".equals(str.trim()))
            return true;
        return false;
    }
}