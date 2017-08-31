package com.admin.coolweather.Fragment;


import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.coolweather.R;
import com.admin.coolweather.activity.MainActivity;
import com.admin.coolweather.activity.WeatherActivity;
import com.admin.coolweather.databinding.AlertDialogBinding;
import com.admin.coolweather.databinding.FragmentSettingBinding;
import com.admin.coolweather.service.AutoUpdateService;


public class SettingFragment extends Fragment
{
    private static final String ACTIVITY_TAG="SettingFragment";

//    private SwitchCompat autoupdateSwitch;
//
//    private TextView update_text;
//
//    private TableRow updateInputItem;
//
    private boolean isshowdialog;

    private FragmentSettingBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
//         View view = inflater.inflate(R.layout.fragment_setting,container,false);

//         binding = DataBindingUtil.setContentView(getActivity(),R.layout.fragment_setting);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);

        View rootView = binding.getRoot();
//        autoupdateSwitch = (SwitchCompat)view.findViewById(R.id.autoupdateSwitch);
//        update_text =(TextView)view.findViewById(R.id.update_text);
//
//        updateInputItem = (TableRow)view.findViewById(R.id.updateInputItem);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

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


         return rootView;
    }




//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

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
                    Intent intent = new Intent(getActivity(), AutoUpdateService.class);
                    getActivity().stopService(intent); //如果关闭则停止服务

                    isshowdialog = false;
                    binding.updateText.setTextColor(getResources().getColor(R.color.colorGray));
                }

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putBoolean("isshowdialog",isshowdialog);
                editor.apply();
            }
        });


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




    }

//    public void onUpdateItemClick(View view)
//    {
//        showAlertDialog();
//
//
//    }

    public void showAlertDialog()
    {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();

//        final AlertDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout. alert_dialog, null, false);

        dialog.setView(LayoutInflater.from(getActivity()).inflate(R.layout.alert_dialog, null));
        dialog.show();


        dialog.getWindow().setContentView(R.layout.alert_dialog);

//        dialog.setContentView(binding.getRoot());
//        dialog.getWindow().setContentView();

//        alertDialog.setView(binding.getRoot());
//        alertDialog.show();
//        alertDialog.getWindow().setContentView(R.layout.alert_dialog); // 设置自定义样式


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
