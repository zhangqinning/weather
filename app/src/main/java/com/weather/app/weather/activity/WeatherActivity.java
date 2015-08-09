package com.weather.app.weather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weather.app.weather.R;
import com.weather.app.weather.util.HttpCallbackListener;
import com.weather.app.weather.util.HttpUtil;
import com.weather.app.weather.util.Utility;

/**
 * Created by zhangqinning on 15/8/8.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {

    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

//        refreshWeather=(Button)findViewById(R.id.refresh_weather);
//        switchCity=(Button)findViewById(R.id.switch_city);
        currentDateText = (TextView) findViewById(R.id.current_date);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        publishText = (TextView) findViewById(R.id.publish_test);
        cityNameText = (TextView) findViewById(R.id.city_name);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);

        String countryCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countryCode)) {
            publishText.setText("努力更新中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.VISIBLE);
            queryWeatherCode(countryCode);
        } else {
            showWeather();
        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.swtich_city:
//                Intent intent=new Intent(this,ChooseAreaActivity.class);
//                intent.putExtra("from_weather_activity", true);
//                startActivity(intent);
//                finish();
//                break;
//            case R.id.refresh_weather:
//                publishText.setText("努力更新中...");
//                SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
//                String weatherCode=prefs.getString("weather_code","");
//                if(!TextUtils.isEmpty(weatherCode)){
//                    queryWeatherInfo(weatherCode);
//                }
//                break;
//            default:
//                break;
//        }
//    }

    /**
     *  查询县级代码对应到天气代码
     */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countryCode");
    }

    /**
     * 查询天气带回对应的天气
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_dsp",""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

    }
}
