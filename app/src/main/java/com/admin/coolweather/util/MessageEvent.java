package com.admin.coolweather.util;

/**
 * Created by admin on 2017/9/22.
 */

public class MessageEvent
{
    private String message;

    private String extraString;


    public MessageEvent(String message,String extraString)
    {
        this.message = message;
        this.extraString = extraString;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getExtraString()
    {
        return extraString;
    }

    public void setExtraString(String extraString)
    {
        this.extraString = extraString;
    }
}
