package com.admin.coolweather.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

/**
 * Created by admin on 2017/8/25.
 */

public class TextWatcherUtil implements TextWatcher
{
    private Context context;

    public TextWatcherUtil(Context context){
        this.context = context;

    }

    /**
     * 编辑框的内容发生改变之前的回调方法
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int before, int count)
    {

    }

    /**
     * 编辑框的内容改变以后,用户没有继续输入时 的回调方法
     */
    @Override
    public void afterTextChanged(Editable editable)
    {
        Toast.makeText(context,"设置时间成功",Toast.LENGTH_SHORT).show();
    }

    /**
     * 编辑框的内容正在发生改变时的回调方法 >>用户正在输入
     * 我们可以在这里实时地 通过搜索匹配用户的输入
     */
    @Override
    public void onTextChanged(CharSequence charSequence,int start, int before, int count)
    {


    }
}
