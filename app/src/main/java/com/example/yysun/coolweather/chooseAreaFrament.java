package com.example.yysun.coolweather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yysun.coolweather.db.City;
import com.example.yysun.coolweather.db.County;
import com.example.yysun.coolweather.db.Province;
import com.example.yysun.coolweather.gson.Weather;
import com.example.yysun.coolweather.util.HttpUtil;
import com.example.yysun.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yysun on 2017/12/11.
 */
public class chooseAreaFrament extends Fragment {
    TextView title_text;
    Button back;
    ListView area_list;
    public static final int Area_Province = 0;
    public static final int Area_City = 1;
    public static final int Area_County = 2;
    public static final int Weather_Req = 3;
    int now_location = Area_Province;
    static Province now_Province;
    static City now_City;
    static County now_County;
    private List<String> dataList = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private List<Province> provinceList = new ArrayList<Province>();
    private List<City> cityList = new ArrayList<City>();
    private List<County> countyList = new ArrayList<County>();
    ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(sp.getString("weatherResult",null)!=null){
            Intent intent=new Intent(getActivity(),weatherActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onDestroy() {
        if (pd != null)
            pd.dismiss();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_list, container, false);
        title_text = (TextView) view.findViewById(R.id.title_text);
        back = (Button) view.findViewById(R.id.back_button);
        area_list = (ListView) view.findViewById(R.id.area_list);
        adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, dataList);
        area_list.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (now_location == Area_City) {
                    get_ProvinceList();
                } else if (now_location == Area_County) {
                    get_CityList((String) title_text.getText());
                }
            }
        });
        area_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (now_location == Area_Province) {
                    now_Province = provinceList.get(i);
                    get_CityList(dataList.get(i));
                } else if (now_location == Area_City) {
                    now_City = cityList.get(i);
                    get_CountyList(dataList.get(i));
                } else {
                    now_County = countyList.get(i);
                    get_Weather(now_County.getWeatherCode());
                }
            }
        });
        get_ProvinceList();
    }

    public void get_ProvinceList() {
        title_text.setText("中国");
        back.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province p : provinceList) {
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            area_list.setSelection(0);
        } else {
            get_InternetContent("http://guolin.tech/api/china", Area_Province);
        }
        now_location = Area_Province;
    }

    public void get_CityList(String provinceName) {
        title_text.setText(provinceName);
        back.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceCode=?", String.valueOf(now_Province.getProvinceCode())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City c : cityList) {
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            area_list.setSelection(0);
        } else {
            get_InternetContent("http://guolin.tech/api/china/" + now_Province.getProvinceCode(), Area_City);
        }
        now_location = Area_City;
    }

    public void get_CountyList(String CityName) {
        title_text.setText(CityName);
        back.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityCode=?", String.valueOf(now_City.getCityCode())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County c : countyList) {
                dataList.add(c.getCountryName());
            }
            adapter.notifyDataSetChanged();
            area_list.setSelection(0);
        } else {
            get_InternetContent("http://guolin.tech/api/china/" + now_City.getProvinceCode() + "/" + now_City.getCityCode(), Area_County);
        }
        now_location = Area_County;
    }

    public void get_Weather(String weatherCode) {
        get_InternetContent("http://guolin.tech/api/weather?cityid=" + weatherCode + "&key=bc0418b57b2d4918819d3974ac1285d9", Weather_Req);
        Intent intent = new Intent(getActivity(), weatherActivity.class);
        intent.putExtra("weatherCode", weatherCode);
        startActivity(intent);
        getActivity().finish();
    }

    public void  get_InternetContent(String url, final int typeCode) {
        HttpUtil myhttp = new HttpUtil();
        showProgress();
        myhttp.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgress();
                        Toast.makeText(getActivity(), "加载数据失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                closeProgress();
                String content = response.body().string();
                boolean res = false;
                if (typeCode == Area_Province) {
                    res = Utility.handleProvinceResponse(content);
                } else if (typeCode == Area_City) {
                    res = Utility.handleCityResponse(content, now_Province.getProvinceCode());
                } else if (typeCode == Area_County) {
                    res = Utility.handleCountryResponse(content, now_City.getCityCode());
                } else {
                    SharedPreferences.Editor spe=PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                    spe.putString("weatherResult",content);
                    spe.apply();
                }
                if (res) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgress();
                            if (typeCode == Area_Province)
                                get_ProvinceList();
                            else if (typeCode == Area_City)
                                get_CityList(now_Province.getProvinceName());
                            else if (typeCode == Area_County)
                                get_CountyList(now_City.getCityName());
                        }
                    });
                }
            }
        });
    }

    public void showProgress() {
        if (pd == null) {
            pd = new ProgressDialog(getActivity());
            pd.setMessage("查询中");
            pd.setCanceledOnTouchOutside(false);
        }
        pd.show();
    }

    public void closeProgress() {
        if (pd != null) {
            pd.dismiss();
        }
    }
}
