package com.example.yysun.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yysun.coolweather.gson.Daily_forecast;
import com.example.yysun.coolweather.gson.Weather;
import com.example.yysun.coolweather.util.HttpUtil;
import com.example.yysun.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by yysun on 2017/12/12.
 */
public class weatherActivity extends AppCompatActivity {
    TextView titleName;
    TextView loc_Time;
    TextView now_Degree;
    TextView now_Info;
    TextView aqi_Text;
    TextView pm25_Text;
    TextView comfort_Text;
    TextView carwash_Text;
    TextView sport_Text;
    ScrollView scroll_V;
    LinearLayout forecast_lay;
    TextView date_Text;
    TextView info_Text;
    TextView tem_Max;
    TextView tem_Min;
    ImageView bing_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        if(Build.VERSION.SDK_INT>=21){
            View decorView =getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        titleName = (TextView) findViewById(R.id.loc_name);
        loc_Time = (TextView) findViewById(R.id.loc_time);
        now_Degree = (TextView) findViewById(R.id.now_degree);
        now_Info = (TextView) findViewById(R.id.now_info_text);
        aqi_Text = (TextView) findViewById(R.id.aqi_text);
        pm25_Text = (TextView) findViewById(R.id.pm25_text);
        comfort_Text = (TextView) findViewById(R.id.comfort_text);
        carwash_Text = (TextView) findViewById(R.id.carwash_text);
        sport_Text = (TextView) findViewById(R.id.sport_text);

        scroll_V = (ScrollView) findViewById(R.id.weather_layout);
        forecast_lay = (LinearLayout) findViewById(R.id.forecast_layout);

        bing_pic = (ImageView) findViewById(R.id.bing_pic);


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherResult = sp.getString("weatherResult", null);
        String bing_res = sp.getString("bing_res", null);
        if (bing_res != null) {
            Glide.with(this).load(bing_res).into(bing_pic);
        } else {
            request_Img();
        }
        if (weatherResult != null)
            Log.d("测试", weatherResult);
        if (weatherResult != null) {
            Weather we = Utility.handleWeatherResponse(weatherResult);
            fill_weatherInfo(Utility.handleWeatherResponse(weatherResult));
        } else {
            Intent intent = getIntent();
            String weatherCode = intent.getStringExtra("weatherCode");
            request_Weather(weatherCode);
        }
    }

    public void request_Img() {
        HttpUtil httu = new HttpUtil();
        httu.sendOkHttpRequest("http://guolin.tech/api/bing_pic", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String pic_url = response.body().string();
                SharedPreferences.Editor she = PreferenceManager.getDefaultSharedPreferences(weatherActivity.this).edit();
                she.putString("bing_res", pic_url);
                she.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(weatherActivity.this).load(pic_url).into(bing_pic);
                    }
                });
            }
        });
    }

    public void fill_weatherInfo(Weather weather) {
        titleName.setText(weather.basic.cityname);
        loc_Time.setText(weather.basic.update.updateTime);
        now_Degree.setText(weather.now.temperature + " °");
        now_Info.setText(weather.now.more.info);
        aqi_Text.setText(weather.aqi.city.aqi);
        pm25_Text.setText(weather.aqi.city.pm25);
        //   comfort_Text.setText(weather.suggesstion.comfort.info);
        //  carwash_Text.setText(weather.suggesstion.carWash.info);
        //  sport_Text.setText(weather.suggesstion.sport.info);
        for (Daily_forecast df : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecast_lay, false);
            date_Text = (TextView) view.findViewById(R.id.date_text);
            info_Text = (TextView) view.findViewById(R.id.info_text);
            tem_Max = (TextView) view.findViewById(R.id.tem_max);
            tem_Min = (TextView) view.findViewById(R.id.tem_min);
            date_Text.setText(df.date);
            info_Text.setText(df.more.info);
            tem_Max.setText(df.temperature.max);
            tem_Min.setText(df.temperature.min);
            forecast_lay.addView(view);
        }
    }

    public void request_Weather(String weatherCode) {
        scroll_V.setVisibility(View.GONE);
        HttpUtil myhttp = new HttpUtil();
        myhttp.sendOkHttpRequest("http://guolin.tech/api/weather?cityid=" + weatherCode + "&key=bc0418b57b2d4918819d3974ac1285d9", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(weatherActivity.this, "加载数据失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String content = response.body().string();
                boolean res = false;
                SharedPreferences.Editor spe = PreferenceManager.getDefaultSharedPreferences(weatherActivity.this).edit();
                spe.putString("weatherResult", content);
                spe.apply();
                if (res) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scroll_V.setVisibility(View.VISIBLE);
                            fill_weatherInfo(Utility.handleWeatherResponse(content));
                        }
                    });
                }
            }
        });
    }
}
