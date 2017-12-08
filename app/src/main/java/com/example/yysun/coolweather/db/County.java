package com.example.yysun.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by yysun on 2017/12/8.
 */
public class County extends DataSupport {
    private int id;
    private String countryName;
    private int cityCode;
    private int weatherCode;

    public void setId(int id) {
        this.id = id;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }


    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public void setWeatherCode(int weatherCode) {
        this.weatherCode = weatherCode;
    }

    public int getId() {

        return id;
    }

    public String getCountryName() {
        return countryName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public int getWeatherCode() {
        return weatherCode;
    }
}
