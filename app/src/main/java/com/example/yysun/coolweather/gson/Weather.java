package com.example.yysun.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yysun on 2017/12/11.
 */
public class Weather {
    public String status;
    public Basic basic;
    public Aqi aqi;
    public Now now;
    public Suggesstion suggesstion;
    @SerializedName("daily_forecast")
    public List<Daily_forecast> forecastList;
}
