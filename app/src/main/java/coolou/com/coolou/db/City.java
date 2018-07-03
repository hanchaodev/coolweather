package coolou.com.coolou.db;

import org.litepal.crud.DataSupport;

/**
 * 城市
 * Created by Administrator on 2018/6/27 0027.
 */

public class City extends DataSupport {

    private int id;
    private String cityName;
    private int CityCode;

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    private int provinceId;

    public int getCityCode() {
        return CityCode;
    }

    public void setCityCode(int cityCode) {
        CityCode = cityCode;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }


}
