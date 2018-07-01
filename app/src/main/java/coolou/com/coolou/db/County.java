package coolou.com.coolou.db;

import org.litepal.crud.DataSupport;

/**县
 * Created by Administrator on 2018/6/27 0027.
 */

public class County extends DataSupport {

    private int id;
    private String countryName;
    private String weatherId;

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    private int countryId;





    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }


}
