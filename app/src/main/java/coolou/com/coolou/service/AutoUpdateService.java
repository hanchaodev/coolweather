package coolou.com.coolou.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import java.io.IOException;

import coolou.com.coolou.gson.Weather;
import coolou.com.coolou.util.HttpUtil;
import coolou.com.coolou.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
             updateBingPic();
             updateweather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60* 60* 1000;
        long triggleAtime =  SystemClock.elapsedRealtime()+anHour;
        Intent intent1 = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,intent,0);
        manager.cancel(pi);
          manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggleAtime,pi);


        return super.onStartCommand(intent, flags, startId);
    }
    /**
     * 更新天气信息
     */
    private void  updateweather(){
       SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
       String weatherString =   sharedPreferences.getString("weather",null);
       if (weatherString != null) {
         final Weather weather=  Utility.handleWeatherResponse(weatherString);
         String weatherId =  weather.basic.weatherId;
         String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=858baa4d76664f6bb4beb78e8bca6763";
           HttpUtil.sendOkRequest(weatherUrl, new Callback() {
               @Override
               public void onFailure(Call call, IOException e) {

               }

               @Override
               public void onResponse(Call call, Response response) throws IOException {
                 String responseText =  response.body().string();
                 if (weather != null && "ok".equals(weather.status)){
                     SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                     editor.putString("weather",responseText);
                     editor.apply();
                 }
               }
           });
       }
    }
    /**
     * 更新背景图
     */
    private void updateBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
             String bingPic =  response.body().string();
              SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",null);
                editor.apply();
            }
        });
    }

}
