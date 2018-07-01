package coolou.com.coolou.util;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import coolou.com.coolou.db.City;
import coolou.com.coolou.db.County;
import coolou.com.coolou.db.Province;
import coolou.com.coolou.gson.Weather;

/**
 * Created by Administrator on 2018/6/28 0028.
 */

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray all = new JSONArray(response);
                for (int i =0;i<all.length();i++) {
                    JSONObject provinceObject = all.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     *解析和处理服务器返回的市数据
     */
    public static  boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCity = new JSONArray(response);
                for (int  i = 0; i < allCity.length();i++) {
                    JSONObject cityObject = allCity.getJSONObject(i);
                     City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityId(cityObject.getInt("id"));
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     *解析和处理服务器返回的县数据
     */
    public static  boolean handleCountryResponse(String response,int countryId){
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCountry = new JSONArray(response);
                for (int  i = 0; i < allCountry.length();i++) {
                    JSONObject countryObject = allCountry.getJSONObject(i);
                    County county = new County();
                    county.setCountryName(countryObject.getString("name"));
                    county.setCountryId(countryObject.getInt("id"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 解析 json 数据返回的实体类 Weather 类
     */
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray =  jsonObject.getJSONArray("HeWeather");
            String weatherContent =    jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
