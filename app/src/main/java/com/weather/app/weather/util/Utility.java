package com.weather.app.weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.weather.app.weather.db.WeatherDB;
import com.weather.app.weather.model.City;
import com.weather.app.weather.model.County;
import com.weather.app.weather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zhangqinning on 15/8/8.
 */
public class Utility {
    public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB,String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces=response.split(",");
            if(allProvinces!=null&&allProvinces.length>0){
                for(String p:allProvinces){
                    String[] array=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    weatherDB.saveProvince(province);
                }
                return true;
            }
        }return false;
    }

    public synchronized static boolean handleCitiesResponse(WeatherDB weatherDB,String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities=response.split(",");
            if(allCities!=null&&allCities.length>0){
                for(String c:allCities){
                    String[] array=c.split("\\|");
                    City city=new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    weatherDB.saveCity(city);
                }
                return true;
            }
        }return false;
    }

    public synchronized static boolean handleCountiesResponse(WeatherDB weatherDB,String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties=response.split(",");
            if(allCounties!=null&&allCounties.length>0){
                for(String c:allCounties){
                    String[] array=c.split("\\|");
                    County county=new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    weatherDB.saveCounty(county);
                }
                return true;
            }
        }return false;
    }

    /**
     * 解析服务器返回的JSON数据，存储的本地
     */
    public static void handleWeatherResponse(Context context,String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            String cityname=weatherInfo.getString("city");
            String weatherCode=weatherInfo.getString("cityid");
            String temp1=weatherInfo.getString("temp1");
            String temp2=weatherInfo.getString("temp2");
            String weatherDsp=weatherInfo.getString("weather");
            String publishTime=weatherInfo.getString("ptime");
            Log.d("Utility",cityname+" "+weatherCode+" "+weatherDsp);
            saveWeatherInfo(context,cityname,weatherCode,temp1,temp2,weatherDsp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 天气信息存储到sharePreferences文件
     */
    public static void saveWeatherInfo(Context context,String cityname,String weatherCode,String temp1,String temp2,String weatherDsp,String publishTime){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name", cityname);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDsp);
        editor.putString("publish_item", publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }
}
