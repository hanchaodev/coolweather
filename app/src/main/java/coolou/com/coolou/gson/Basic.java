package coolou.com.coolou.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/6/29 0029.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;
    public class Update{
        @SerializedName("loc")
       public String updateTime;
    }
}
