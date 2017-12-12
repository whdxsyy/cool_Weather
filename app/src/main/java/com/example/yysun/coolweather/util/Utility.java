package com.example.yysun.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.yysun.coolweather.db.City;
import com.example.yysun.coolweather.db.County;
import com.example.yysun.coolweather.db.Province;
import com.example.yysun.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yysun on 2017/12/8.
 */
public class Utility {
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray provinceArray = new JSONArray(response);
                for (int i = 0; i < provinceArray.length(); i++) {
                    JSONObject province = provinceArray.getJSONObject(i);
                    Province p = new Province();
                    p.setProvinceName(province.getString("name"));
                    p.setProvinceCode(province.getInt("id"));
                    p.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response, int provinceID) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray cityArray = new JSONArray(response);
                for (int i = 0; i < cityArray.length(); i++) {
                    JSONObject city = cityArray.getJSONObject(i);
                    City c = new City();
                    c.setCityName(city.getString("name"));
                    c.setCityCode(city.getInt("id"));
                    c.setProvinceCode(provinceID);
                    c.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountryResponse(String response, int cityID) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray countryArray = new JSONArray(response);
                for (int i = 0; i < countryArray.length(); i++) {
                    JSONObject country = countryArray.getJSONObject(i);
                    County c = new County();
                    c.setCountryName(country.getString("name"));
                    c.setCityCode(cityID);
                    c.setWeatherCode(country.getString("weather_id"));
                    c.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
                String weatherContent=jsonArray.getJSONObject(0).toString();
                return  new Gson().fromJson(weatherContent, Weather.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
