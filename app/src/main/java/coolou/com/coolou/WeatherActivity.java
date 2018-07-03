package coolou.com.coolou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;

import coolou.com.coolou.gson.Forecast;
import coolou.com.coolou.gson.Weather;
import coolou.com.coolou.service.AutoUpdateService;
import coolou.com.coolou.util.HttpUtil;
import coolou.com.coolou.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherlayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecatLayout;
    private TextView apiText;
    private TextView comfortText;
    private TextView sportText;
    private SharedPreferences preferences;
    private String weatherString;
    private TextView pm25Text;
    private TextView carWashText;
    private ImageView bingpicImg;
    public SwipeRefreshLayout swipeRefresh;
    private String weatherId;
    public DrawerLayout drawerLayout;
    private Button nav_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
           View decorView =   getWindow().getDecorView();
           decorView.setSystemUiVisibility(
                   View.SYSTEM_UI_FLAG_FULLSCREEN
                   |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);
        // 初始控件
        weatherlayout = (ScrollView) findViewById(R.id.weather_layout);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
         swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav_button = (Button) findViewById(R.id.nav_button);

        bingpicImg = (ImageView) findViewById(R.id.bing_pic_img);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecatLayout = (LinearLayout) findViewById(R.id.forecat_layout);
        apiText = (TextView) findViewById(R.id.api_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        weatherString = preferences.getString("weather", null);

         nav_button.setOnClickListener(new  View.OnClickListener(){
             @Override
             public void onClick(View v) {
                 drawerLayout.openDrawer(GravityCompat.START);
             }
         });
        if (weatherString != null) {
            // 有缓存是直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            // 无缓存去服务器检查天气
            weatherId = getIntent().getStringExtra("weather_id");
            weatherlayout.setVisibility(View.VISIBLE);
            requestWeather(weatherId);
         }
         swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

             @Override
             public void onRefresh() {
                 requestWeather(weatherId);
             }
         });
        String bingPic =  preferences.getString("bing_pic",null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingpicImg);
        }else  {
            loadBingPic();
        }
    }

    private void loadBingPic() {
      String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
              final String bingpic =  response.body().string();
               SharedPreferences.Editor edidor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                edidor.putString("bing_pic",bingpic);
                edidor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingpic).into(bingpicImg);
                    }
                });
            }
        });
    }

    public void requestWeather (String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId + "&key=858baa4d76664f6bb4beb78e8bca6763";
        HttpUtil.sendOkRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      Toast.makeText(WeatherActivity.this,"获取天气失败",Toast.LENGTH_SHORT).show();
                      swipeRefresh.setRefreshing(false);
                  }
              });
                loadBingPic();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
               final String responseText =  response.body().string();
               final Weather weather =  Utility.handleWeatherResponse(responseText);
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       if (weather != null && "OK".equals(weather.status)){
                           SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                           editor.putString("weather",responseText);
                           editor.apply();
                           showWeatherInfo(weather);
                       } else  {
                           Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                       }
                       swipeRefresh.setRefreshing(false);
                   }
               });
            }
        });
        loadBingPic();
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + ".C";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecatLayout.removeAllViews();
        if (weather != null && "ok".equals(weather.status)){
            Intent intent = new Intent(this, AutoUpdateService.class);
            startActivity(intent);

        }
        for (Forecast forecast : weather.forecasts) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecatLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecatLayout.addView(view);
        }
        if (weather.aqi != null) {
            apiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度" + weather.suggestion.comfort.info;
        String info = "洗车指数" + weather.suggestion.carWash.info;
        String sport = "运动建议" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(info);
        sportText.setText(sport);
        weatherlayout.setVisibility(View.VISIBLE);
    }
}
