package com.example.yysun.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yysun on 2017/12/11.
 */
public class Suggesstion {
    public class Comfort{
        @SerializedName("txt")
        public String info;
    }
    public class CarWash{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("txt")
        public String info;
    }

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;
}
