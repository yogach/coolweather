package com.admin.coolweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


import com.admin.coolweather.R;
import com.admin.coolweather.databinding.SettingBinding;
import com.admin.coolweather.service.AutoUpdateService;

/**
 * Created by admin on 2017/9/12.
 */

public class SettingActivity extends AppCompatActivity
{
    private boolean isshowdialog;

    private SettingBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.setting);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        isshowdialog = prefs.getBoolean("isshowdialog",false); //如果获取不到值 则设置默认值为false

        if(isshowdialog)
        {
            binding.autoupdateSwitch.setChecked(true);
            binding.updateText.setTextColor(getResources().getColor(R.color.colorBlack));
        }
        else
        {
            binding.autoupdateSwitch.setChecked(false);

            binding.updateText.setTextColor(getResources().getColor(R.color.colorGray));
        }


        //switch按键监听器
        binding.autoupdateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked)
            {
                if(ischecked)
                {

                    isshowdialog = true;
                    binding.updateText.setTextColor(getResources().getColor(R.color.colorBlack));

                }
                else
                {
                    Intent intent = new Intent(SettingActivity.this, AutoUpdateService.class);
                    SettingActivity.this.stopService(intent); //如果关闭则停止服务

                    isshowdialog = false;
                    binding.updateText.setTextColor(getResources().getColor(R.color.colorGray));
                }

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SettingActivity.this).edit();
                editor.putBoolean("isshowdialog",isshowdialog);
                editor.apply();
            }
        });


        //自动更新项点击监听
        binding.updateInputItem.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(isshowdialog)
                {
                    showAlertDialog();
                }
            }
        });

        //按键监听
        binding.backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    public void showAlertDialog()
    {
        final AlertDialog dialog = new AlertDialog.Builder(SettingActivity.this).create();

        dialog.setView(LayoutInflater.from(SettingActivity.this).inflate(R.layout.alert_dialog, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.alert_dialog);


        Button btnPositive = (Button) dialog.findViewById(R.id.btn_Positive);
        Button btnNegative = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText etContent = (EditText) dialog.findViewById(R.id.updateTimeInput_text);

        btnPositive.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                String str = etContent.getText().toString();
                if (isNullEmptyBlank(str))
                {
                    etContent.setError("输入内如不能为空");
                }
                else
                {
                    dialog.dismiss();

                    Toast.makeText(SettingActivity.this, etContent.getText().toString(), Toast.LENGTH_LONG).show();

                    //开启自动更新
                    Intent intent = new Intent(SettingActivity.this, AutoUpdateService.class);
                    intent.putExtra("updateTime",str);
                    SettingActivity.this.startService(intent);


                }
            }
        });

        btnNegative.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                dialog.dismiss();
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
