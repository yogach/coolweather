package com.admin.coolweather.Fragment;


import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.coolweather.R;
import com.admin.coolweather.activity.MainActivity;
import com.admin.coolweather.activity.WeatherActivity;
import com.admin.coolweather.service.AutoUpdateService;


public class SettingFragment extends Fragment
{
    private static final String ACTIVITY_TAG="SettingFragment";

    private SwitchCompat autoupdateSwitch;

    private TextView update_text;

    private TableRow updateInputItem;

    private boolean isshowdialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_setting,container,false);

//        TextView title_text =(TextView)view.findViewById(R.id.title_text);
//        title_text.setText(R.string.settings);
        autoupdateSwitch = (SwitchCompat)view.findViewById(R.id.checkbox);
        update_text =(TextView)view.findViewById(R.id.update_text);
//         updateTimeInput_text = (EditText)view.findViewById(R.id.updateTimeInput_text);
        updateInputItem = (TableRow)view.findViewById(R.id.tablerow1);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        isshowdialog = prefs.getBoolean("isshowdialog",false); //如果获取不到值 则设置默认值为false

        if(isshowdialog)
        {
            autoupdateSwitch.setChecked(true);
            update_text.setTextColor(getResources().getColor(R.color.colorBlack));
        }
        else
        {
            autoupdateSwitch.setChecked(false);

            update_text.setTextColor(getResources().getColor(R.color.colorGray));
        }


        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        autoupdateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked)
            {
                if(ischecked)
                {

                    isshowdialog = true;
                    update_text.setTextColor(getResources().getColor(R.color.colorBlack));

                }
                else
                {
                    Intent intent = new Intent(getActivity(), AutoUpdateService.class);
                    getActivity().stopService(intent); //如果关闭则停止服务

                    isshowdialog = false;
                    update_text.setTextColor(getResources().getColor(R.color.colorGray));
                }

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putBoolean("isshowdialog",isshowdialog);
                editor.apply();
            }
        });


        updateInputItem.setOnClickListener(new View.OnClickListener()
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



    }

    private void showAlertDialog()
    {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setView(LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.alert_dialog);
        Button btnPositive = (Button) dialog.findViewById(R.id.btn_add);
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

                    Toast.makeText(getActivity(), etContent.getText().toString(), Toast.LENGTH_LONG).show();

                    //开启自动更新
                    Intent intent = new Intent(getActivity(), AutoUpdateService.class);
                    intent.putExtra("updateTime",str);
                    getActivity().startService(intent);


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
