package com.admin.coolweather.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.admin.coolweather.Base.DividerItemDecoration;
import com.admin.coolweather.R;
import com.admin.coolweather.adapter.BindingAdapter;
import com.admin.coolweather.databinding.CityManagementBinding;

/**
 * Created by admin on 2017/9/12.
 */

public class CityManagementActivity extends AppCompatActivity
{

    private CityManagementBinding binding;

    private BindingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.city_management);

        initView();



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

            }
        });

    }

    private void initView()
    {
        //初始化RecyclerView适配器
        adapter = new BindingAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(CityManagementActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);


        binding.recylerview.setAdapter(adapter);
        binding.recylerview.setLayoutManager(manager);
        binding.recylerview.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST)); //设置项目分隔线
    }


}
