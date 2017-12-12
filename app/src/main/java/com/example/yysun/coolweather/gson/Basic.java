package com.example.yysun.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yysun on 2017/12/11.
 */
public class Basic {
    @SerializedName("city")
    public String cityname;
    @SerializedName("id")
    public String weatherId;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
